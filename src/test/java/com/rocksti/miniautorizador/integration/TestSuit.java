package com.rocksti.miniautorizador.integration;

import com.rocksti.miniautorizador.dto.CartaoRequestDTO;
import com.rocksti.miniautorizador.dto.TransacaoRequestDTO;
import com.rocksti.miniautorizador.enums.ErroTransacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class TestSuit {

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @Order(1)
    class CartaoControllerIT {

        private static final String BASE_URL = "http://localhost:%d/cartoes";
        private static final String VALID_CARD_NUMBER = "6549873025634501";
        private static final String VALID_PASSWORD = "1234";
        private static final String INVALID_CARD_NUMBER = "6549873025634502";
        private static final String INVALID_USER = "invalidUser";
        private static final String INVALID_PASSWORD = "invalidPassword";

        @LocalServerPort
        private int port;

        @Autowired
        private TestRestTemplate restTemplate;

        private String baseUrl() {
            return String.format(BASE_URL, port);
        }

        @BeforeEach
        public void setup() {
            restTemplate = new TestRestTemplate("username", "password");
        }

        @Test
        @Order(1)
        void criarCartao_DeveRetornarSucesso() {
            CartaoRequestDTO cartaoDTO = new CartaoRequestDTO(VALID_CARD_NUMBER, VALID_PASSWORD);

            ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), cartaoDTO, String.class);

            assertThat(response.getStatusCode())
                    .as("Verifica se o cartão foi criado com sucesso")
                    .isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody())
                    .as("Verifica se o corpo da resposta contém o número do cartão")
                    .contains(VALID_CARD_NUMBER);
        }

        @Test
        @Order(2)
        void criarCartao_DeveRetornarErro_QuandoCartaoJaExiste() {
            CartaoRequestDTO cartaoDTO = new CartaoRequestDTO(VALID_CARD_NUMBER, VALID_PASSWORD);

            ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), cartaoDTO, String.class);

            assertThat(response.getStatusCode())
                    .as("Verifica se a resposta é 422 Unprocessable Entity para cartão já existente")
                    .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        @Test
        @Order(3)
        void criarCartao_DeveRetornarErro_QuandoAutenticacaoInvalida() {
            TestRestTemplate restTemplateInvalid = new TestRestTemplate(INVALID_USER, INVALID_PASSWORD);
            CartaoRequestDTO cartaoDTO = new CartaoRequestDTO(VALID_CARD_NUMBER, VALID_PASSWORD);

            ResponseEntity<String> response = restTemplateInvalid.postForEntity(baseUrl(), cartaoDTO, String.class);

            assertThat(response.getStatusCode())
                    .as("Verifica se a resposta é 401 Unauthorized para autenticação inválida")
                    .isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @Order(4)
        void consultarSaldo_DeveRetornarSaldoInicial() {
            String url = baseUrl() + "/" + VALID_CARD_NUMBER;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            assertThat(response.getStatusCode())
                    .as("Verifica se a resposta é 200 OK para consulta de saldo")
                    .isEqualTo(HttpStatus.OK);
            assertThat(response.getBody())
                    .as("Verifica se o corpo da resposta contém o saldo inicial")
                    .contains("500.0");
        }

        @Test
        @Order(5)
        void consultarSaldo_DeveRetornarErro_ComNumeroQueNaoExiste() {
            String url = baseUrl() + "/" + INVALID_CARD_NUMBER;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            assertThat(response.getStatusCode())
                    .as("Verifica se a resposta é 404 Not Found para número de cartão inexistente")
                    .isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @Order(6)
        void consultarSaldo_DeveRetornarErro_QuandoAutenticacaoInvalida() {
            TestRestTemplate restTemplateInvalid = new TestRestTemplate(INVALID_USER, INVALID_PASSWORD);
            String url = baseUrl() + "/" + VALID_CARD_NUMBER;

            ResponseEntity<String> response = restTemplateInvalid.getForEntity(url, String.class);

            assertThat(response.getStatusCode())
                    .as("Verifica se a resposta é 401 Unauthorized para autenticação inválida")
                    .isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @Order(2)
    class TransacaoControllerIT {

        private static final String BASE_URL = "http://localhost:%d/transacoes";
        private static final String VALID_CARD_NUMBER = "6549873025634501";
        private static final String VALID_PASSWORD = "1234";
        private static final String INVALID_CARD_NUMBER = "0000000000000000";
        private static final String INVALID_PASSWORD = "wrongPassword";

        @LocalServerPort
        private int port;

        @Autowired
        private TestRestTemplate restTemplate;

        private String baseUrl() {
            return String.format(BASE_URL, port);
        }

        @BeforeEach
        void setup() {
            restTemplate = new TestRestTemplate("username", "password");
        }

        @Test
        @Order(1)
        void realizarTransacao_DeveRetornarSucesso() {
            TransacaoRequestDTO transacaoDTO = new TransacaoRequestDTO(VALID_CARD_NUMBER, VALID_PASSWORD, 10.0);

            ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), transacaoDTO, String.class);

            assertThat(response.getStatusCode())
                    .as("Verifica se a transação foi criada com sucesso")
                    .isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody())
                    .as("Verifica se o corpo da resposta contém 'OK'")
                    .contains("OK");
        }

        @Test
        @Order(2)
        void realizarTransacao_DeveRetornarErro_QuandoAutenticacaoInvalida() {
            TestRestTemplate restTemplateInvalid = new TestRestTemplate("invalidUser", "invalidPassword");
            TransacaoRequestDTO transacaoDTO = new TransacaoRequestDTO(VALID_CARD_NUMBER, VALID_PASSWORD, 10.0);

            ResponseEntity<String> response = restTemplateInvalid.postForEntity(baseUrl(), transacaoDTO, String.class);

            assertThat(response.getStatusCode())
                    .as("Verifica se a resposta é 401 Unauthorized para autenticação inválida")
                    .isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @Order(3)
        void realizarTransacao_DeveRetornarErro_SaldoInsuficiente() {
            TransacaoRequestDTO transacaoDTO = new TransacaoRequestDTO(VALID_CARD_NUMBER, VALID_PASSWORD, 1000.0);

            ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), transacaoDTO, String.class);

            assertThat(response.getStatusCode())
                    .as("Verifica se a resposta é 422 Unprocessable Entity para saldo insuficiente")
                    .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            assertThat(response.getBody())
                    .as("Verifica se o corpo da resposta contém 'SALDO_INSUFICIENTE'")
                    .contains(ErroTransacao.SALDO_INSUFICIENTE.name());
        }

        @Test
        @Order(4)
        void realizarTransacao_DeveRetornarErro_SenhaInvalida() {
            TransacaoRequestDTO transacaoDTO = new TransacaoRequestDTO(VALID_CARD_NUMBER, INVALID_PASSWORD, 10.0);

            ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), transacaoDTO, String.class);

            assertThat(response.getStatusCode())
                    .as("Verifica se a resposta é 422 Unprocessable Entity para senha inválida")
                    .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            assertThat(response.getBody())
                    .as("Verifica se o corpo da resposta contém 'SENHA_INVALIDA'")
                    .contains(ErroTransacao.SENHA_INVALIDA.name());
        }

        @Test
        @Order(5)
        void realizarTransacao_DeveRetornarErro_CartaoInexistente() {
            TransacaoRequestDTO transacaoDTO = new TransacaoRequestDTO(INVALID_CARD_NUMBER, VALID_PASSWORD, 10.0);

            ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), transacaoDTO, String.class);

            assertThat(response.getStatusCode())
                    .as("Verifica se a resposta é 422 Unprocessable Entity para cartão inexistente")
                    .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            assertThat(response.getBody())
                    .as("Verifica se o corpo da resposta contém 'CARTAO_INEXISTENTE'")
                    .contains(ErroTransacao.CARTAO_INEXISTENTE.name());
        }

        @Test
        @Order(6)
        void realizarTransacoes_AteRetornarSaldoInsuficiente() {
            TransacaoRequestDTO transacaoDTO = new TransacaoRequestDTO(VALID_CARD_NUMBER, VALID_PASSWORD, 200.0);

            ResponseEntity<String> response1 = restTemplate.postForEntity(baseUrl(), transacaoDTO, String.class);
            assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response1.getBody()).contains("OK");

            ResponseEntity<String> response2 = restTemplate.postForEntity(baseUrl(), transacaoDTO, String.class);
            assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response2.getBody()).contains("OK");

            ResponseEntity<String> response3 = restTemplate.postForEntity(baseUrl(), transacaoDTO, String.class);
            assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            assertThat(response3.getBody()).contains(ErroTransacao.SALDO_INSUFICIENTE.name());

            String saldoUrl = "http://localhost:" + port + "/cartoes/" + VALID_CARD_NUMBER;
            ResponseEntity<String> saldoResponse = restTemplate.getForEntity(saldoUrl, String.class);

            assertThat(saldoResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(saldoResponse.getBody()).isEqualTo("90.0");
        }
    }
}
