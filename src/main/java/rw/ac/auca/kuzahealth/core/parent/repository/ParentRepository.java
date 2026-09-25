package rw.ac.auca.kuzahealth.core.parent.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import rw.ac.auca.kuzahealth.core.parent.entity.Parent;

public interface ParentRepository extends JpaRepository<Parent,UUID>{

}
