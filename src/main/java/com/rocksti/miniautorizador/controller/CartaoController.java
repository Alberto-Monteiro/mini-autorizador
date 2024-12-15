package com.rocksti.miniautorizador.controller;

import com.rocksti.miniautorizador.dto.CartaoRequestDTO;
import com.rocksti.miniautorizador.dto.CartaoResponseDTO;
import com.rocksti.miniautorizador.service.CartaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cartoes")
@RequiredArgsConstructor
public class CartaoController {

    private final CartaoService cartaoService;

    @PostMapping
    public ResponseEntity<CartaoResponseDTO> criarCartao(@RequestBody CartaoRequestDTO cartaoRequestDTO) {
        CartaoResponseDTO response = cartaoService.criarCartao(cartaoRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{numeroCartao}")
    public ResponseEntity<Double> obterSaldo(@PathVariable String numeroCartao) {
        Double saldo = cartaoService.obterSaldo(numeroCartao);

        return ResponseEntity.ok(saldo);
    }
}


