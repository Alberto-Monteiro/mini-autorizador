package com.rocksti.miniautorizador.exception;

import com.rocksti.miniautorizador.enums.ErroTransacao;

public class SenhaInvalidaException extends RuntimeException {
    public SenhaInvalidaException() {
        super(ErroTransacao.SENHA_INVALIDA.name());
    }
}
