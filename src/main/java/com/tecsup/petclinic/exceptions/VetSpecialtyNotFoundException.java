package com.tecsup.petclinic.exceptions;

import com.tecsup.petclinic.entities.VetSpecialtyId;

public class VetSpecialtyNotFoundException extends RuntimeException {
    public VetSpecialtyNotFoundException(VetSpecialtyId id) {
        super("Especialidad no asignada al veterinario: " + id);
    }
}
