package com.rocksti.miniautorizador.exception;

import com.rocksti.miniautorizador.enums.ErroTransacao;

public class CartaoInexistenteException extends RuntimeException {
    public CartaoInexistenteException() {
        super(ErroTransacao.CARTAO_INEXISTENTE.name());
    }
}
