package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.VetSpecialtyDTO;
import com.tecsup.petclinic.entities.*;
import com.tecsup.petclinic.exceptions.*;
import com.tecsup.petclinic.services.VetSpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

/** Grupo 5: A+B de Ángel y C+D de Diego. */
@RestController
@RequiredArgsConstructor
public class VetSpecialtyController {
    private final VetSpecialtyService service;

    public record AssignmentRequest(Integer vetId, Integer specialtyId) { }
    public record ExperienceRequest(Integer yearsExperience) { }
    public record CertificationRequest(LocalDate certificationDate) { }

    @PostMapping("/vet-specialties")
    public ResponseEntity<VetSpecialtyDTO> assign(@RequestBody AssignmentRequest request) {
        VetSpecialty assignment = service.assignSpecialtyToVet(request.vetId(), request.specialtyId());
        return ResponseEntity.status(HttpStatus.CREATED).body(VetSpecialtyDTO.from(assignment));
    }

    @DeleteMapping("/vet-specialties/{vetId}/{specId}")
    public ResponseEntity<Void> remove(@PathVariable Integer vetId, @PathVariable Integer specId) {
        service.removeSpecialtyFromVet(vetId, specId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/vets/{vetId}/specialties")
    public List<Specialty> findSpecialties(@PathVariable Integer vetId) {
        return service.findSpecialtiesByVet(vetId);
    }

    @GetMapping("/specialties/{specId}/vets")
    public List<Vet> findVets(@PathVariable Integer specId,
            @RequestParam(required = false) Integer minExperience) {
        return minExperience == null ? service.findVetsBySpecialty(specId)
                : service.findVetsByMinExperience(specId, minExperience);
    }

    @PutMapping("/vet-specialties/{vetId}/{specId}/primary")
    public VetSpecialtyDTO setPrimary(@PathVariable Integer vetId, @PathVariable Integer specId) {
        return VetSpecialtyDTO.from(service.setPrimarySpecialty(vetId, specId));
    }

    @PutMapping("/vet-specialties/{vetId}/{specId}/experience")
    public VetSpecialtyDTO updateExperience(@PathVariable Integer vetId, @PathVariable Integer specId,
            @RequestBody ExperienceRequest request) {
        if (request == null || request.yearsExperience() == null) {
            throw new IllegalArgumentException("Se requieren los años de experiencia");
        }
        return VetSpecialtyDTO.from(service.updateYearsExperience(vetId, specId, request.yearsExperience()));
    }

    @PutMapping("/vet-specialties/{vetId}/{specId}/certification")
    public VetSpecialtyDTO updateCertification(@PathVariable Integer vetId, @PathVariable Integer specId,
            @RequestBody CertificationRequest request) {
        if (request == null) throw new IllegalArgumentException("Se requiere fecha de certificacion");
        return VetSpecialtyDTO.from(service.updateCertificationDate(vetId, specId, request.certificationDate()));
    }

    @ExceptionHandler(DuplicateVetSpecialtyException.class)
    public ResponseEntity<Map<String, String>> duplicate(DuplicateVetSpecialtyException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(VetSpecialtyNotFoundException.class)
    public ResponseEntity<Map<String, String>> notAssigned(VetSpecialtyNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> invalid(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }
}
