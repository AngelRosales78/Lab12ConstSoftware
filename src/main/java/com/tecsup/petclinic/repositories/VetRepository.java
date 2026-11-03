package com.tecsup.petclinic.repositories;

import com.tecsup.petclinic.entities.Vet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface VetRepository extends JpaRepository<Vet, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from vets v where v.id = :id")
    Optional<Vet> findByIdForUpdate(@Param("id") Integer id);
}
