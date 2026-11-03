package com.tecsup.petclinic.repositories;

import com.tecsup.petclinic.entities.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface VetSpecialtyRepository extends JpaRepository<VetSpecialty, VetSpecialtyId> {
    List<VetSpecialty> findByIdVetId(Integer vetId);

    @Query("select r.specialty from VetSpecialty r where r.id.vetId = :vetId order by r.id.specialtyId")
    List<Specialty> findSpecialtiesByVet(@Param("vetId") Integer vetId);

    @Query("select r.vet from VetSpecialty r where r.id.specialtyId = :specialtyId order by r.id.vetId")
    List<Vet> findVetsBySpecialty(@Param("specialtyId") Integer specialtyId);

    @Query("select r.vet from VetSpecialty r where r.id.specialtyId = :specialtyId and r.yearsExperience >= :minimum order by r.id.vetId")
    List<Vet> findVetsByMinExperience(@Param("specialtyId") Integer specialtyId, @Param("minimum") int minimum);
}
