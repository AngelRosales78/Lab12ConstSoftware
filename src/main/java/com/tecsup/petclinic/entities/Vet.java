package com.tecsup.petclinic.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Set;

@Entity(name = "vets")
@Table(name = "vets")
@Data
@NoArgsConstructor
public class Vet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean active = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "vet_specialties", joinColumns = @JoinColumn(name = "vet_id"),
            inverseJoinColumns = @JoinColumn(name = "specialty_id"))
    @JsonIgnore @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<Specialty> specialties;
}
