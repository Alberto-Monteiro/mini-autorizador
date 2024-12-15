package com.rocksti.miniautorizador.controller;

import com.rocksti.miniautorizador.dto.TransacaoRequestDTO;
import com.rocksti.miniautorizador.service.TransacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transacoes")
@RequiredArgsConstructor
class TransacaoController {

    private final TransacaoService transacaoService;

    @PostMapping
    public ResponseEntity<String> realizarTransacao(@RequestBody TransacaoRequestDTO transacaoRequestDTO) {
        transacaoService.autorizarTransacao(transacaoRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body("OK");
    }
}
