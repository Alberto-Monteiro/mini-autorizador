# Mini Autorizador - VR Benefícios

Este projeto é um **Mini Autorizador de Transações VR**, desenvolvido em **Spring Boot 3.4.0** com **Java 23**. O
sistema permite a criação de cartões, consulta de saldo e autorização de transações respeitando as regras de segurança e
consistência de dados.

---

## **Tecnologias Utilizadas**

- **Java 23**
- **Spring Boot 3.4.0**
- **Spring Data JPA**
- **Spring Security** (Autenticação Basic)
- **Lombok** (Para redução de boilerplate)
- **Banco de Dados**:
    - MySQL (Produção)
    - H2 (Testes Integração)
- **Docker e Docker Compose** (Gerenciamento de ambiente)
- **JUnit 5** e **Mockito** (Testes Unitários)
- **Testes de Integração** (com `TestRestTemplate`)
- **JaCoCo** (Geração de cobertura de testes)

---

## **Requisitos**

Antes de iniciar o projeto, garanta que os seguintes componentes estejam instalados na sua máquina:

- **Java 23** (JDK)
- **Docker e Docker Compose**

---

## **Configuração e Execução**

### **1. Clonar o Projeto**

```bash
git clone git@github.com:Alberto-Monteiro/mini-autorizador.git
cd mini-autorizador
```

---

### **2. Configuração do Banco de Dados e da Aplicação**

O projeto utiliza **MySQL 5.7** como banco de dados e a aplicação Spring Boot é iniciada no mesmo **docker-compose**.

#### **Passos para Iniciar o Banco de Dados e a Aplicação**:

1. Certifique-se de que o **Docker** e o **Docker Compose** estão instalados e rodando.

2. Navegue até o diretório onde o arquivo **docker-compose.yml** está localizado:

```bash
cd src/docker
```

3. Execute o seguinte comando para iniciar **tanto o banco de dados quanto a aplicação**:

```bash
docker-compose up -d
```

4. Verifique se os serviços estão em execução:

```bash
docker ps
```

#### **Serviços Iniciados**:

- **Banco de Dados**:
    - **Hostname**: `mysql`
    - **Porta**: `3306`
    - **Banco de Dados**: `miniautorizador`
    - **Usuário**: `root`
    - **Senha**: vazia

- **Aplicação Spring Boot**:
    - **Porta**: `8080`
    - **Datasource**: `jdbc:mysql://mysql:3306/miniautorizador`

---

### **3. Acesso à Aplicação**

Com o comando **`docker-compose up -d`**, a aplicação e o banco de dados serão inicializados. A aplicação estará
disponível em:

```
http://localhost:8080
```

#### **Verificando a Inicialização**:

1. **Logs da Aplicação**:  
   Para verificar os logs e garantir que a aplicação iniciou corretamente:

```bash
docker logs app
```

2. **Logs do Banco de Dados**:  
   Para verificar os logs do MySQL:

```bash
docker logs mysql
```

---

### **4. Testes**

#### **Executar Testes Unitários e de Integração**

```bash
mvn clean test
```

- **Testes Unitários**: Utilizam **Mockito** para mockar dependências.
- **Testes de Integração**: Utilizam **TestRestTemplate** para simular chamadas HTTP.

#### **Relatório de Cobertura de Testes**

O relatório de cobertura é gerado pelo **JaCoCo**. Após rodar os testes, acesse:

```
target/site/jacoco/index.html
```

---

## **Endpoints da API**

### **1. Criar Novo Cartão**

- **Método**: `POST`
- **URL**: `http://localhost:8080/cartoes`
- **Autenticação**: Basic Auth (username/password)

#### **Body da Requisição** (JSON):

```json
{
  "numeroCartao": "6549873025634501",
  "senha": "1234"
}
```

#### **Respostas Possíveis**:

1. **Criação com Sucesso**:
    - **Status Code**: `201 Created`
    - **Body**:
      ```json
      {
          "numeroCartao": "6549873025634501",
          "senha": "1234"
      }
      ```

2. **Caso o Cartão Já Exista**:
    - **Status Code**: `422 Unprocessable Entity`
    - **Body**:
      ```json
      {
          "numeroCartao": "6549873025634501",
          "senha": "1234"
      }
      ```

3. **Erro de Autenticação**:
    - **Status Code**: `401 Unauthorized`

---

### **2. Obter Saldo do Cartão**

- **Método**: `GET`
- **URL**: `http://localhost:8080/cartoes/{numeroCartao}`
    - **{numeroCartao}**: Número do cartão a ser consultado.
- **Autenticação**: Basic Auth (username/password)

#### **Respostas Possíveis**:

1. **Obtenção com Sucesso**:
    - **Status Code**: `200 OK`
    - **Body**:
      ```
      495.15
      ```

2. **Caso o Cartão Não Exista**:
    - **Status Code**: `404 Not Found`
    - **Body**: *vazio*

3. **Erro de Autenticação**:
    - **Status Code**: `401 Unauthorized`

---

### **3. Realizar uma Transação**

- **Método**: `POST`
- **URL**: `http://localhost:8080/transacoes`
- **Autenticação**: Basic Auth (username/password)

#### **Body da Requisição** (JSON):

```json
{
  "numeroCartao": "6549873025634501",
  "senhaCartao": "1234",
  "valor": 10.00
}
```

#### **Respostas Possíveis**:

1. **Transação Realizada com Sucesso**:
    - **Status Code**: `201 Created`
    - **Body**:
      ```
      OK
      ```

2. **Caso Alguma Regra de Autorização Não Seja Atendida**:
    - **Status Code**: `422 Unprocessable Entity`
    - **Body**:
      ```
      SALDO_INSUFICIENTE
      ```
      ```
      SENHA_INVALIDA
      ```
      ```
      CARTAO_INEXISTENTE
      ```
      (dependendo da regra que impediu a autorização)

3. **Erro de Autenticação**:
    - **Status Code**: `401 Unauthorized`

---

### **Observações Importantes**

1. **Autenticação**:  
   Todos os endpoints exigem **autenticação Basic**. Utilize o seguinte login e senha padrão:
    - **Username**: `username`
    - **Password**: `password`

2. **Respostas 422**:  
   Quando uma operação falha devido a validações (como saldo insuficiente, senha incorreta ou cartão inexistente), o
   código de status **422** será retornado com a descrição apropriada.

3. **Exemplo de Erro de Autenticação**:
   ```http
   HTTP/1.1 401 Unauthorized
   WWW-Authenticate: Basic realm="Realm"
   ```

---

## **Regras de Negócio**

1. **Criação do Cartão**:
    - Todo cartão é criado com um **saldo inicial de R$ 500,00**.

2. **Autorização da Transação**:  
   Uma transação será autorizada apenas se:
    - O cartão existir.
    - A senha estiver correta.
    - O saldo for suficiente.

3. **Concorrência**:  
   O sistema utiliza **`@Version`** para garantir o controle de concorrência (Optimistic Locking) e evitar condições de
   corrida.

---

### **Controle de Concorrência com @Version**

O projeto utiliza o **`@Version`** do JPA para garantir o **controle de concorrência otimista** (Optimistic Locking).
Esse mecanismo impede a ocorrência de **condições de corrida** ao atualizar dados simultaneamente.

O campo anotado com **`@Version`** é utilizado para rastrear alterações na entidade. A cada atualização bem-sucedida, o
valor da versão é incrementado automaticamente. Caso duas transações tentem atualizar a mesma entidade ao mesmo tempo,
apenas a primeira será aplicada, enquanto a segunda falhará com um erro de concorrência.

**Exemplo de implementação**:

```java

@Version
@Column(name = "version", nullable = false)
private Long version;
```

Essa abordagem garante a **integridade dos dados** sem bloquear registros no banco, oferecendo melhor desempenho em
cenários com múltiplas transações.

---

## **Segurança**

- O projeto utiliza **Spring Security** com **Basic Authentication**.
- A autenticação é configurada no **`application.yml`**.

Exemplo de usuário e senha padrão:

```yaml
spring:
  security:
    user:
      name: username
      password: password
```

---

## **Estrutura do Projeto**

```bash
src
├── docker
│   ├── docker-compose.yml          # Configuração do Docker Compose
├── main
│   ├── java
│   │   └── com.rocksti.miniautorizador
│   │       ├── configuration       # Configurações da Aplicação inclusive a segurança
│   │       ├── controller          # Controllers REST
│   │       ├── dto                 # DTOs
│   │       ├── entity              # Entidades JPA
│   │       ├── enums               # Enums
│   │       ├── exception           # Exceções personalizadas
│   │       ├── handler             # Handlers de Exceções
│   │       ├── repository          # Interfaces de Repositórios
│   │       └── service             # Lógica de Negócio
│   └── resources
│       └── application.properties  # Configurações da aplicação
└── test
    ├── java
    │   └── com.rocksti.miniautorizador
    │       ├── service             # Testes Unitários
    │       └── integration         # Testes de Integração
    └── resources
        └── application.properties  # Configurações de Teste
```

