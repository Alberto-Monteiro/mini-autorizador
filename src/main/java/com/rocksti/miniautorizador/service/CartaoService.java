package com.rocksti.miniautorizador.service;

import com.rocksti.miniautorizador.dto.CartaoRequestDTO;
import com.rocksti.miniautorizador.dto.CartaoResponseDTO;
import com.rocksti.miniautorizador.entity.CartaoEntity;
import com.rocksti.miniautorizador.exception.CartaoExistenteException;
import com.rocksti.miniautorizador.exception.NotFoundException;
import com.rocksti.miniautorizador.repository.CartaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartaoService {

    private final CartaoRepository cartaoRepository;

    @Transactional
    public CartaoResponseDTO criarCartao(CartaoRequestDTO cartaoRequestDTO) {
        cartaoRepository.findByNumeroCartao(cartaoRequestDTO.numeroCartao())
                .ifPresent(cartao -> {
                    throw new CartaoExistenteException(new CartaoResponseDTO(cartao.getNumeroCartao(), cartao.getSenha()));
                });

        CartaoEntity novoCartao = new CartaoEntity();
        novoCartao.setNumeroCartao(cartaoRequestDTO.numeroCartao());
        novoCartao.setSenha(cartaoRequestDTO.senha());
        novoCartao.setSaldo(500.00);

        cartaoRepository.save(novoCartao);

        return new CartaoResponseDTO(novoCartao.getNumeroCartao(), novoCartao.getSenha());
    }

    @Transactional(readOnly = true)
    public Double obterSaldo(String numeroCartao) {
        return cartaoRepository.findByNumeroCartao(numeroCartao)
                .map(CartaoEntity::getSaldo)
                .orElseThrow(NotFoundException::new);
    }
}
