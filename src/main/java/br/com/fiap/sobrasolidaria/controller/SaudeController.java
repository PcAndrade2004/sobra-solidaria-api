package br.com.fiap.sobrasolidaria.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Rota simples para conferir se a API está no ar (útil no Render). Não acessa o banco. */
@RestController
public class SaudeController {

    @GetMapping("/")
    public Map<String, String> raiz() {
        return Map.of("api", "Sobra Solidária", "status", "no ar");
    }
}
