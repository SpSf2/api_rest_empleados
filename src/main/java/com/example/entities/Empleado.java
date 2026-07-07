package com.example.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.example.models.Genero;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "empleado")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class Empleado implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "El empleado debe tener un nombre")
    @NotBlank(message = "El nombre del empleado no puede estar vacío o tener solo espacios")
    @Size(min = 2, max = 50, message = "El nombre del empleado debe tener entre 2 y 50 caracteres")
    private String nombre;

    @NotNull(message = "El empleado debe tener un primer apellido")
    @NotBlank(message = "El primer apellido no puede estar vacío o tener solo espacios")
    @Size(min = 2, max = 50, message = "El primer apellido debe tener entre 2 y 50 caracteres")
    private String primerApellido;

    @Size(min = 2, max = 50, message = "El segundo apellido, si se agrega,debe tener entre 2 y 50 caracteres")
    private String segundoApellido;

    @NotNull(message = "La fecha de alta es obligatoria")
    private LocalDate fechaAlta;

    @NotNull(message = "El salario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El salario debe ser mayor que cero")
    private BigDecimal salario;

    @NotNull(message = "El género es obligatorio")
    @Enumerated(EnumType.STRING)
    private Genero genero;

    @NotNull(message = "El departamento del empleado es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "empleados"})
    private Departamento departamento;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "empleado")
    @JsonIgnoreProperties({"empleado"})
    private List<Telefono> telefonos;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "empleado")
    @JsonIgnoreProperties({"empleado"})
    private List<Correo> correos;

    private String imagen; // Campo para almacenar la ruta de la foto del empleado
}
