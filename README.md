# Gestão de Documentos 

Aplicação web desenvolvida. O sistema tem como objetivo principal o gerenciamento simplificado de documentos, permitindo upload de arquivos, listagem e um histórico de comentários para cada item.

## Tecnologias e Arquitetura

O projeto foi construído priorizando a simplicidade de execução e a organização do código, utilizando as seguintes tecnologias:

* **Back-end:** Java 21  com Spring Boot 3.
* **Persistência:** Spring Data JPA.
* **Banco de Dados:** H2 Database (configurado para salvar em disco local).
* **Front-end:** HTML5, CSS3 Customizado (estilo limpo/minimalista), JavaScript básico e Bootstrap 5.
* **Motor de Template:** Thymeleaf (Server-Side Rendering).

## Funcionalidades Implementadas

* **Upload Local:** Envio de arquivos (PDF, JPG, PNG) armazenados fisicamente na pasta `/uploads` do projeto.
* **Persistência de Dados:** Salvamento do caminho do arquivo, título, e data de envio no banco de dados.
* **Listagem:** Interface responsiva para visualização de todos os documentos cadastrados.
* **Sistema de Comentários:** Relacionamento One-to-Many permitindo que cada documento possua um histórico independente de observações e notas, com registro de data e hora.

## Como Executar o Projeto Localmente

O projeto foi configurado para rodar da forma mais simples possível na máquina do avaliador. Não é necessário instalar o Maven ou um Banco de Dados externo.

### Pré-requisito
* **Java JDK 17** (ou versão superior) instalado e configurado nas variáveis de ambiente.

### Passo a Passo

1. Clone este repositório:
   ```bash
   git clone [https://github.com/renatoyx/gestao-documentos.git](https://github.com/renatoyx/gestao-documentos.git)
   cd gestao-documentos

2. Inicie a aplicação utilizando o Maven Wrapper (já incluso no projeto):
    No Windows (CMD/PowerShell):
        ./mvnw.cmd spring-boot:run
    No Linux/Mac:
        ./mvnw spring-boot:run

3. Acesse a aplicação no seu navegador:

    URL: http://localhost:8080

