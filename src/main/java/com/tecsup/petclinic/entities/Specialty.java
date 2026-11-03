package com.tecsup.petclinic.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Set;

@Entity(name = "specialties")
@Table(name = "specialties")
@Data
@NoArgsConstructor
public class Specialty {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String office;
    @Column(name = "h_open") private Integer openingHour;
    @Column(name = "h_close") private Integer closingHour;

    @ManyToMany(mappedBy = "specialties", fetch = FetchType.LAZY)
    @JsonIgnore @ToString.Exclude @EqualsAndHashCode.Exclude
    private Set<Vet> vets;
}
