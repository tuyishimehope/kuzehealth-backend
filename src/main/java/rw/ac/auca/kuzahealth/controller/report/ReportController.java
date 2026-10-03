package rw.ac.auca.kuzahealth.controller.report;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.exception.ResourceNotFoundException;
import rw.ac.auca.kuzahealth.core.immunisation.CoverageRow;
import rw.ac.auca.kuzahealth.core.immunisation.ImmunisationService;
import rw.ac.auca.kuzahealth.core.report.ReportService;
import rw.ac.auca.kuzahealth.core.report.ReportService.DistrictCount;
import rw.ac.auca.kuzahealth.core.report.ReportService.HealthWorkerVisits;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private static final String ADMIN_OR_ANALYST = "hasAnyRole('ADMIN', 'DATA_ANALYST')";

    private final ReportService reportService;
    private final ImmunisationService immunisationService;

    /** Headline numbers for a dashboard. */
    @GetMapping("/summary")
    public Map<String, Object> summary() {
        return reportService.summary();
    }

    /** For each scheduled dose: how many infants are old enough to have had it, and how many did. */
    @GetMapping("/vaccination-coverage")
    public List<CoverageRow> vaccinationCoverage(@RequestParam(required = false) String district) {
        return immunisationService.coverage(district);
    }

    @GetMapping("/parents-by-district")
    public List<DistrictCount> parentsByDistrict() {
        return reportService.parentsByDistrict();
    }

    /** Visits per health worker and status. Defaults to the last 30 days. */
    @GetMapping("/visits-by-health-worker")
    public List<HealthWorkerVisits> visitsByHealthWorker(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date to) {
        Date end = to != null ? to : new Date();
        Date start = from != null ? from : new Date(end.getTime() - TimeUnit.DAYS.toMillis(30));
        return reportService.visitsByHealthWorker(start, end);
    }

    /** Full export of one dataset as CSV: parents, infants, visits or vaccinations. Contains personal data. */
    @GetMapping("/export/{dataset}.csv")
    @PreAuthorize(ADMIN_OR_ANALYST)
    public ResponseEntity<byte[]> export(@PathVariable String dataset) {
        Map<String, Supplier<String>> exports = Map.of(
                "parents", reportService::parentsCsv,
                "infants", reportService::infantsCsv,
                "visits", reportService::visitsCsv,
                "vaccinations", reportService::vaccinationsCsv);
        Supplier<String> export = exports.get(dataset);
        if (export == null) {
            throw new ResourceNotFoundException("Unknown dataset: " + dataset);
        }
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dataset + ".csv\"")
                .body(export.get().getBytes(StandardCharsets.UTF_8));
    }
}
