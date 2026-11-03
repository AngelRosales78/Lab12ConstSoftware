package com.tecsup.petclinic.exceptions;

import com.tecsup.petclinic.entities.VetSpecialtyId;

public class DuplicateVetSpecialtyException extends RuntimeException {
    public DuplicateVetSpecialtyException(VetSpecialtyId id) {
        super("La especialidad ya esta asignada al veterinario: " + id);
    }
}
