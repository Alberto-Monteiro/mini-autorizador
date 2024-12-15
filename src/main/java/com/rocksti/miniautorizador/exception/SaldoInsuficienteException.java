package com.rocksti.miniautorizador.exception;

import com.rocksti.miniautorizador.enums.ErroTransacao;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException() {
        super(ErroTransacao.SALDO_INSUFICIENTE.name());
    }
}
