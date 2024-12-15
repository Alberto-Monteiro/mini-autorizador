package com.rocksti.miniautorizador.service;

import com.rocksti.miniautorizador.dto.CartaoRequestDTO;
import com.rocksti.miniautorizador.dto.CartaoResponseDTO;
import com.rocksti.miniautorizador.entity.CartaoEntity;
import com.rocksti.miniautorizador.exception.CartaoExistenteException;
import com.rocksti.miniautorizador.exception.NotFoundException;
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

class CartaoServiceTest {

    private static final String VALID_CARD_NUMBER = "6549873025634501";
    private static final String VALID_PASSWORD = "1234";
    private static final String INVALID_CARD_NUMBER = "0000000000000000";
    private static final double SALDO_INICIAL = 500.0;

    @Mock
    private CartaoRepository cartaoRepository;

    @InjectMocks
    private CartaoService cartaoService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void criarCartao_DeveCriarComSucesso() {
        CartaoRequestDTO request = new CartaoRequestDTO(VALID_CARD_NUMBER, VALID_PASSWORD);
        CartaoEntity novoCartao = new CartaoEntity()
                .setNumeroCartao(VALID_CARD_NUMBER)
                .setSenha(VALID_PASSWORD)
                .setSaldo(SALDO_INICIAL);

        when(cartaoRepository.findByNumeroCartao(request.numeroCartao())).thenReturn(Optional.empty());
        when(cartaoRepository.save(any(CartaoEntity.class))).thenReturn(novoCartao);

        CartaoResponseDTO resultado = cartaoService.criarCartao(request);

        assertThat(resultado.numeroCartao()).isEqualTo(VALID_CARD_NUMBER);
        assertThat(resultado.senha()).isEqualTo(VALID_PASSWORD);
        verify(cartaoRepository, times(1)).save(any(CartaoEntity.class));
    }

    @Test
    void criarCartao_DeveLancarExcecao_QuandoCartaoJaExiste() {
        CartaoRequestDTO request = new CartaoRequestDTO(VALID_CARD_NUMBER, VALID_PASSWORD);
        CartaoEntity cartaoExistente = new CartaoEntity()
                .setNumeroCartao(VALID_CARD_NUMBER)
                .setSenha(VALID_PASSWORD)
                .setSaldo(SALDO_INICIAL);

        when(cartaoRepository.findByNumeroCartao(request.numeroCartao())).thenReturn(Optional.of(cartaoExistente));

        assertThatThrownBy(() -> cartaoService.criarCartao(request))
                .isInstanceOf(CartaoExistenteException.class)
                .hasMessageContaining("{\"numeroCartao\":\"6549873025634501\",\"senha\":\"1234\"}");

        verify(cartaoRepository, never()).save(any(CartaoEntity.class));
    }

    @Test
    void consultarSaldo_DeveRetornarSaldoComSucesso() {
        CartaoEntity cartao = new CartaoEntity()
                .setNumeroCartao(VALID_CARD_NUMBER)
                .setSenha(VALID_PASSWORD)
                .setSaldo(SALDO_INICIAL);

        when(cartaoRepository.findByNumeroCartao(VALID_CARD_NUMBER)).thenReturn(Optional.of(cartao));

        Double saldo = cartaoService.obterSaldo(VALID_CARD_NUMBER);

        assertThat(saldo).isEqualTo(500.0);
        verify(cartaoRepository, times(1)).findByNumeroCartao(VALID_CARD_NUMBER);
    }

    @Test
    void consultarSaldo_DeveLancarExcecao_QuandoCartaoNaoExiste() {
        when(cartaoRepository.findByNumeroCartao(INVALID_CARD_NUMBER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartaoService.obterSaldo(INVALID_CARD_NUMBER))
                .isInstanceOf(NotFoundException.class);

        verify(cartaoRepository, times(1)).findByNumeroCartao(INVALID_CARD_NUMBER);
    }
}
