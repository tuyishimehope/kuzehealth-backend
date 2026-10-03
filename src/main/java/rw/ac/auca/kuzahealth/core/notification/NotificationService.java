package rw.ac.auca.kuzahealth.core.notification;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;
import rw.ac.auca.kuzahealth.core.parent.enums.Language;
import rw.ac.auca.kuzahealth.security.CustomUserDetails;
import rw.ac.auca.kuzahealth.sms.model.BulkSmsRecipient;
import rw.ac.auca.kuzahealth.sms.model.SmsResponse;
import rw.ac.auca.kuzahealth.sms.service.PindoSmsService;

/**
 * The single place SMS messages leave the system from. It picks the template in the
 * parent's language, respects their SMS consent, and records every message (or the
 * reason it was not sent) in the SMS log.
 */
@Service
public class NotificationService {

    private static final int MAX_PROVIDER_RESPONSE = 2000;
    private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final PindoSmsService smsService;
    private final SmsLogRepository smsLogRepository;
    private final ResourceBundleMessageSource templates;
    private final String defaultSender;
    private final DateTimeFormatter dateFormat;
    private final DateTimeFormatter timeFormat;

    public NotificationService(PindoSmsService smsService, SmsLogRepository smsLogRepository,
            @Value("${pindo.sender:PindoTest}") String defaultSender,
            @Value("${app.timezone:Africa/Kigali}") String timezone) {
        this.smsService = smsService;
        this.smsLogRepository = smsLogRepository;
        this.defaultSender = defaultSender;

        ZoneId zone = ZoneId.of(timezone);
        this.dateFormat = DATE_PATTERN.withZone(zone);
        this.timeFormat = DateTimeFormatter.ofPattern("HH:mm").withZone(zone);

        this.templates = new ResourceBundleMessageSource();
        this.templates.setBasename("sms/messages");
        this.templates.setDefaultEncoding("UTF-8");
        this.templates.setFallbackToSystemLocale(false);
    }

    /**
     * Sends a templated message to a parent in their preferred language.
     * Nothing is sent when the parent has withdrawn SMS consent or has no phone number;
     * the returned log entry says which.
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public SmsLog notifyParent(Parent parent, SmsPurpose purpose, String templateKey, Object... args) {
        Language language = parent.getPreferredLanguage() != null ? parent.getPreferredLanguage() : Language.EN;
        String text = render(language, templateKey, args);

        if (!parent.isSmsConsent()) {
            return record(parent.getPhone(), text, defaultSender, purpose, SmsStatus.SKIPPED_NO_CONSENT, null,
                    parent.getId());
        }
        if (parent.getPhone() == null || parent.getPhone().isBlank()) {
            return record(null, text, defaultSender, purpose, SmsStatus.SKIPPED_NO_PHONE, null, parent.getId());
        }
        return deliver(parent.getPhone(), text, defaultSender, purpose, text, parent.getId());
    }

    /**
     * Sends free text to one number.
     *
     * @param loggedText what to keep in the log; pass a placeholder for secrets such as login codes
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public SmsLog sendDirect(String to, String text, String sender, SmsPurpose purpose, String loggedText) {
        return deliver(to, text, sender != null && !sender.isBlank() ? sender : defaultSender, purpose, loggedText,
                null);
    }

    /** Sends the same free text to many numbers and logs one entry per recipient. */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public SmsResponse sendBulk(List<BulkSmsRecipient> recipients, String text, String sender, SmsPurpose purpose) {
        String from = sender != null && !sender.isBlank() ? sender : defaultSender;
        SmsResponse response = smsService.sendBulkSms(recipients, text, from);
        SmsStatus status = response.isSuccess() ? SmsStatus.SENT : SmsStatus.FAILED;
        for (BulkSmsRecipient recipient : recipients) {
            record(recipient.getPhonenumber(), text, from, purpose, status, response.getProviderResponse(), null);
        }
        return response;
    }

    @Transactional(readOnly = true)
    public Page<SmsLog> search(SmsStatus status, SmsPurpose purpose, UUID parentId, Pageable pageable) {
        Specification<SmsLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (purpose != null) {
                predicates.add(cb.equal(root.get("purpose"), purpose));
            }
            if (parentId != null) {
                predicates.add(cb.equal(root.get("parentId"), parentId));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return smsLogRepository.findAll(spec, pageable);
    }

    public String render(Language language, String templateKey, Object... args) {
        Locale locale = language != null ? language.getLocale() : Locale.ENGLISH;
        return templates.getMessage(templateKey, args, locale);
    }

    public String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        if (date instanceof java.sql.Date dateOnly) {
            // A calendar date has no time zone; converting it would shift it by a day.
            return DATE_PATTERN.format(dateOnly.toLocalDate());
        }
        return dateFormat.format(Instant.ofEpochMilli(date.getTime()));
    }

    public String formatTime(Date date) {
        return date == null ? "" : timeFormat.format(Instant.ofEpochMilli(date.getTime()));
    }

    private SmsLog deliver(String to, String text, String sender, SmsPurpose purpose, String loggedText,
            UUID parentId) {
        SmsResponse response = smsService.sendSingleSms(to, text, sender);
        String providerResponse = response.getProviderResponse() != null ? response.getProviderResponse()
                : response.getMessage();
        return record(to, loggedText, sender, purpose, response.isSuccess() ? SmsStatus.SENT : SmsStatus.FAILED,
                providerResponse, parentId);
    }

    private SmsLog record(String recipient, String text, String sender, SmsPurpose purpose, SmsStatus status,
            String providerResponse, UUID parentId) {
        SmsLog log = new SmsLog();
        log.setRecipient(recipient);
        log.setMessage(text);
        log.setSender(sender);
        log.setPurpose(purpose);
        log.setStatus(status);
        log.setProviderResponse(truncate(providerResponse));
        log.setParentId(parentId);
        log.setTriggeredBy(currentUser());
        return smsLogRepository.save(log);
    }

    private static String truncate(String value) {
        return value != null && value.length() > MAX_PROVIDER_RESPONSE ? value.substring(0, MAX_PROVIDER_RESPONSE)
                : value;
    }

    private static String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails user) {
            return user.getEmail();
        }
        return "system";
    }
}
