package br.com.fiap.sobrasolidaria;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SobraSolidariaApplication {

    public static void main(String[] args) {
        // Datas de validade são "dia do calendário": usamos o fuso do Brasil mesmo rodando em servidor UTC.
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
        SpringApplication.run(SobraSolidariaApplication.class, args);
    }
}
