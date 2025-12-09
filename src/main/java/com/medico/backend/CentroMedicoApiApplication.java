package com.medico.backend;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class CentroMedicoApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CentroMedicoApiApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // Esto fuerza a la máquina virtual de Java a usar hora Perú
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
        System.out.println("Hora configurada en Backend: " + new java.util.Date());
    }
}