package com.tecsup.petclinic.util;

import com.tecsup.petclinic.entities.*;

/** Objetos esperados para la version de pruebas con servicio simulado. */
public final class VetSpecialtyObjectCreator {
    private VetSpecialtyObjectCreator() { }

    public static VetSpecialty assignment(int vetId, int specialtyId) {
        return new VetSpecialty(new VetSpecialtyId(vetId, specialtyId));
    }

    public static Specialty specialty(int id, String name) {
        Specialty specialty = new Specialty();
        specialty.setId(id);
        specialty.setName(name);
        return specialty;
    }

    public static Vet vet(int id, String firstName, String lastName) {
        Vet vet = new Vet();
        vet.setId(id);
        vet.setFirstName(firstName);
        vet.setLastName(lastName);
        return vet;
    }
}
