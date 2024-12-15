package com.rocksti.miniautorizador.exception;

import com.google.gson.Gson;
import com.rocksti.miniautorizador.dto.CartaoResponseDTO;

public class CartaoExistenteException extends RuntimeException {
    public CartaoExistenteException(CartaoResponseDTO cartaoResponseDTO) {
        super(convertToJson(cartaoResponseDTO));
    }

    private static String convertToJson(CartaoResponseDTO cartaoResponseDTO) {
        return new Gson().toJson(cartaoResponseDTO);
    }
}
