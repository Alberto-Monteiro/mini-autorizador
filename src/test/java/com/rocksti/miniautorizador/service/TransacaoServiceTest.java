package com.rocksti.miniautorizador.service;

import com.rocksti.miniautorizador.dto.TransacaoRequestDTO;
import com.rocksti.miniautorizador.entity.CartaoEntity;
import com.rocksti.miniautorizador.enums.ErroTransacao;
import com.rocksti.miniautorizador.exception.CartaoInexistenteException;
import com.rocksti.miniautorizador.exception.SaldoInsuficienteException;
import com.rocksti.miniautorizador.exception.SenhaInvalidaException;
import com.rocksti.miniautorizador.repository.CartaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransacaoServiceTest {

    private static final String VALID_CARD_NUMBER = "6549873025634501";
    private static final String VALID_PASSWORD = "1234";
    private static final String INVALID_CARD_NUMBER = "0000000000000000";
    private static final String INVALID_PASSWORD = "wrongPassword";
    private static final double SALDO_INICIAL = 500.0;

    @Mock
    private CartaoRepository cartaoRepository;

    @InjectMocks
    private TransacaoService transacaoService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void realizarTransacao_DeveRealizarComSucesso() {
        CartaoEntity cartao = new CartaoEntity()
                .setNumeroCartao(VALID_CARD_NUMBER)
                .setSenha(VALID_PASSWORD)
                .setSaldo(SALDO_INICIAL);
        TransacaoRequestDTO request = new TransacaoRequestDTO(VALID_CARD_NUMBER, VALID_PASSWORD, 10.0);

        when(cartaoRepository.findByNumeroCartao(VALID_CARD_NUMBER)).thenReturn(Optional.of(cartao));

        transacaoService.autorizarTransacao(request);

        assertThat(cartao.getSaldo()).isEqualTo(490.0);
        verify(cartaoRepository, times(1)).save(cartao);
    }

    @Test
    void realizarTransacao_DeveLancarExcecao_QuandoSaldoInsuficiente() {
        CartaoEntity cartao = new CartaoEntity()
                .setNumeroCartao(VALID_CARD_NUMBER)
                .setSenha(VALID_PASSWORD)
                .setSaldo(5.0);
        TransacaoRequestDTO request = new TransacaoRequestDTO(VALID_CARD_NUMBER, VALID_PASSWORD, 10.0);

        when(cartaoRepository.findByNumeroCartao(VALID_CARD_NUMBER)).thenReturn(Optional.of(cartao));

        assertThatThrownBy(() -> transacaoService.autorizarTransacao(request))
                .isInstanceOf(SaldoInsuficienteException.class)
                .hasMessageContaining(ErroTransacao.SALDO_INSUFICIENTE.name());

        verify(cartaoRepository, never()).save(cartao);
    }

    @Test
    void realizarTransacao_DeveLancarExcecao_QuandoSenhaInvalida() {
        CartaoEntity cartao = new CartaoEntity()
                .setNumeroCartao(VALID_CARD_NUMBER)
                .setSenha(VALID_PASSWORD)
                .setSaldo(SALDO_INICIAL);
        TransacaoRequestDTO request = new TransacaoRequestDTO(VALID_CARD_NUMBER, INVALID_PASSWORD, 10.0);

        when(cartaoRepository.findByNumeroCartao(VALID_CARD_NUMBER)).thenReturn(Optional.of(cartao));

        assertThatThrownBy(() -> transacaoService.autorizarTransacao(request))
                .isInstanceOf(SenhaInvalidaException.class)
                .hasMessageContaining(ErroTransacao.SENHA_INVALIDA.name());

        verify(cartaoRepository, never()).save(cartao);
    }

    @Test
    void realizarTransacao_DeveLancarExcecao_QuandoCartaoInexistente() {
        TransacaoRequestDTO request = new TransacaoRequestDTO(INVALID_CARD_NUMBER, VALID_PASSWORD, 10.0);

        when(cartaoRepository.findByNumeroCartao(INVALID_CARD_NUMBER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transacaoService.autorizarTransacao(request))
                .isInstanceOf(CartaoInexistenteException.class)
                .hasMessageContaining(ErroTransacao.CARTAO_INEXISTENTE.name());

        verify(cartaoRepository, never()).save(any(CartaoEntity.class));
    }
}
