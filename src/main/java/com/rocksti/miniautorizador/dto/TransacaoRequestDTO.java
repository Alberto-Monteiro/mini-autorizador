package com.rocksti.miniautorizador.dto;

public record TransacaoRequestDTO(String numeroCartao, String senhaCartao, Double valor) {
}
