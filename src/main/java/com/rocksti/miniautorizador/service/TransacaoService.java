package com.rocksti.miniautorizador.service;

import com.rocksti.miniautorizador.dto.TransacaoRequestDTO;
import com.rocksti.miniautorizador.entity.CartaoEntity;
import com.rocksti.miniautorizador.exception.CartaoInexistenteException;
import com.rocksti.miniautorizador.exception.SaldoInsuficienteException;
import com.rocksti.miniautorizador.exception.SenhaInvalidaException;
import com.rocksti.miniautorizador.repository.CartaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final CartaoRepository cartaoRepository;

    private CartaoEntity validarSenha(CartaoEntity cartao, String senhaCartao) {
        return Optional.of(cartao)
                .filter(c -> c.getSenha().equals(senhaCartao))
                .orElseThrow(SenhaInvalidaException::new);
    }

    private CartaoEntity validarSaldo(CartaoEntity cartao, Double valor) {
        return Optional.of(cartao)
                .filter(c -> c.getSaldo() >= valor)
                .orElseThrow(SaldoInsuficienteException::new);
    }

    private void debitarSaldo(CartaoEntity cartao, Double valor) {
        cartao.setSaldo(cartao.getSaldo() - valor);
    }

    @Transactional
    public void autorizarTransacao(TransacaoRequestDTO transacaoRequestDTO) {
        CartaoEntity cartao = cartaoRepository.findByNumeroCartao(transacaoRequestDTO.numeroCartao())
                .orElseThrow(CartaoInexistenteException::new);

        validarSenha(cartao, transacaoRequestDTO.senhaCartao());

        validarSaldo(cartao, transacaoRequestDTO.valor());

        debitarSaldo(cartao, transacaoRequestDTO.valor());

        cartaoRepository.save(cartao);
    }
}
