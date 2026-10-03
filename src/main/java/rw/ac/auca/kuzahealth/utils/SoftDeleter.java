package rw.ac.auca.kuzahealth.utils;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Marks clinical records as deleted together with the records that hang off them.
 * Everything removed by one call shares the same timestamp, so a deletion can be
 * identified and undone in the database if it was a mistake.
 */
@Component
public class SoftDeleter {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void deleteParent(UUID parentId) {
        Date now = new Date();
        run("UPDATE Vaccination v SET v.deletedAt = :now WHERE v.infant.id IN "
                + "(SELECT i.id FROM Infant i WHERE i.mother.id = :id)", now, parentId);
        run("UPDATE GrowthMeasurement g SET g.deletedAt = :now WHERE g.infant.id IN "
                + "(SELECT i.id FROM Infant i WHERE i.mother.id = :id)", now, parentId);
        run("UPDATE Infant i SET i.deletedAt = :now WHERE i.mother.id = :id", now, parentId);
        run("UPDATE VisitNote n SET n.deletedAt = :now WHERE n.visit.id IN "
                + "(SELECT v.id FROM Visit v WHERE v.parent.id = :id)", now, parentId);
        run("UPDATE Visit v SET v.deletedAt = :now WHERE v.parent.id = :id", now, parentId);
        run("UPDATE PregnancyRecord p SET p.deletedAt = :now WHERE p.parent.id = :id", now, parentId);
        run("UPDATE Parent p SET p.deletedAt = :now WHERE p.id = :id", now, parentId);
    }

    @Transactional
    public void deleteInfant(UUID infantId) {
        Date now = new Date();
        run("UPDATE Vaccination v SET v.deletedAt = :now WHERE v.infant.id = :id", now, infantId);
        run("UPDATE GrowthMeasurement g SET g.deletedAt = :now WHERE g.infant.id = :id", now, infantId);
        run("UPDATE Infant i SET i.deletedAt = :now WHERE i.id = :id", now, infantId);
    }

    @Transactional
    public void deleteVisit(UUID visitId) {
        Date now = new Date();
        run("UPDATE VisitNote n SET n.deletedAt = :now WHERE n.visit.id = :id", now, visitId);
        run("UPDATE Visit v SET v.deletedAt = :now WHERE v.id = :id", now, visitId);
    }

    @Transactional
    public void delete(Class<? extends SoftDeletableEntity> type, UUID id) {
        run("UPDATE " + type.getSimpleName() + " e SET e.deletedAt = :now WHERE e.id = :id", new Date(), id);
    }

    private void run(String jpql, Date now, UUID id) {
        entityManager.createQuery(jpql).setParameter("now", now).setParameter("id", id).executeUpdate();
        entityManager.clear();
    }
}
