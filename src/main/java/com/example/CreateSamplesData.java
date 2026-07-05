package com.example;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.entities.Correo;
import com.example.entities.Departamento;
import com.example.entities.Empleado;
import com.example.models.Genero;
import com.example.entities.Telefono;
import com.example.services.CorreoService;
import com.example.services.DepartamentoService;
import com.example.services.EmpleadoService;
import com.example.services.TelefonoService;

@Configuration
public class CreateSamplesData {

    @Bean
    public CommandLineRunner sampleData(
            DepartamentoService departamentoService,
            EmpleadoService empleadoService,
            TelefonoService telefonoService,
            CorreoService correoService) {

        return args -> {

            Departamento informatica = departamentoService.save(
                    Departamento.builder()
                            .nombre("Informática")
                            .build());

            Departamento rrhh = departamentoService.save(
                    Departamento.builder()
                            .nombre("RRHH")
                            .build());

            Departamento contabilidad = departamentoService.save(
                    Departamento.builder()
                            .nombre("Contabilidad")
                            .build());

            Empleado emp1 = empleadoService.save(
                    Empleado.builder()
                            .nombre("Lucia")
                            .primerApellido("Garcia")
                            .segundoApellido("Lopez")
                            .fechaAlta(LocalDate.of(2024, 2, 10))
                            .salario(new BigDecimal("1850.50"))
                            .genero(Genero.MUJER)
                            .departamento(informatica)
                            .build());

            Empleado emp2 = empleadoService.save(
                    Empleado.builder()
                            .nombre("Carlos")
                            .primerApellido("Martinez")
                            .segundoApellido("Perez")
                            .fechaAlta(LocalDate.of(2023, 9, 1))
                            .salario(new BigDecimal("2100.00"))
                            .genero(Genero.HOMBRE)
                            .departamento(rrhh)
                            .build());

            Empleado emp3 = empleadoService.save(
                    Empleado.builder()
                            .nombre("Andrea")
                            .primerApellido("Santos")
                            .segundoApellido(null)
                            .fechaAlta(LocalDate.of(2025, 1, 15))
                            .salario(new BigDecimal("1950.75"))
                            .genero(Genero.MUJER)
                            .departamento(contabilidad)
                            .build());

            telefonoService.save(
                    Telefono.builder()
                            .numero("600123123")
                            .empleado(emp1)
                            .build());

            telefonoService.save(
                    Telefono.builder()
                            .numero("911223344")
                            .empleado(emp1)
                            .build());

            telefonoService.save(
                    Telefono.builder()
                            .numero("612345678")
                            .empleado(emp2)
                            .build());

            telefonoService.save(
                    Telefono.builder()
                            .numero("699888777")
                            .empleado(emp3)
                            .build());

            correoService.save(
                    Correo.builder()
                            .email("lucia.garcia@empresa.com")
                            .empleado(emp1)
                            .build());

            correoService.save(
                    Correo.builder()
                            .email("lucia.lopez@empresa.com")
                            .empleado(emp1)
                            .build());

            correoService.save(
                    Correo.builder()
                            .email("carlos.martinez@empresa.com")
                            .empleado(emp2)
                            .build());

            correoService.save(
                    Correo.builder()
                            .email("andrea.santos@empresa.com")
                            .empleado(emp3)
                            .build());
        };
    }
}
