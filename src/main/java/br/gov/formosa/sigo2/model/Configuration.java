package br.gov.formosa.sigo2.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "configuracao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Configuration implements Serializable {

    @Id
    @Column(name = "chave")
    @EqualsAndHashCode.Include
    private String key;

    @Column(name = "valor", columnDefinition = "TEXT")
    private String value;
}