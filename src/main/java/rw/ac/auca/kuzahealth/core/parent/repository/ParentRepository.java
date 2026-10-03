package rw.ac.auca.kuzahealth.core.parent.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import rw.ac.auca.kuzahealth.core.parent.entity.Parent;

public interface ParentRepository extends JpaRepository<Parent, UUID>, JpaSpecificationExecutor<Parent> {

    long countByAssignedHealthWorker_Id(UUID healthWorkerId);

    long countByAssignedHealthWorker_IdAndIsHighRiskTrue(UUID healthWorkerId);
}
