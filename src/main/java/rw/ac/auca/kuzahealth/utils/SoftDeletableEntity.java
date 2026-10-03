package rw.ac.auca.kuzahealth.utils;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

/**
 * Base for clinical records, which are kept when "deleted". Entities extending this
 * must also declare {@code @SQLRestriction(SoftDeletableEntity.NOT_DELETED)} so that
 * removed rows disappear from every query.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class SoftDeletableEntity extends BaseEntity {

    public static final String NOT_DELETED = "deleted_at IS NULL";

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deleted_at")
    private Date deletedAt;
}
