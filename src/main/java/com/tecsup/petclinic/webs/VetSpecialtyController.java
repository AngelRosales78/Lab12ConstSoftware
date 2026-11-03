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

/** Bloques A y B de Ángel. Los endpoints C y D quedan a cargo de Diego. */
@RestController
@RequiredArgsConstructor
public class VetSpecialtyController {
    private final VetSpecialtyService service;

    public record AssignmentRequest(Integer vetId, Integer specialtyId) { }

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
    public List<Vet> findVets(@PathVariable Integer specId) {
        return service.findVetsBySpecialty(specId);
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
