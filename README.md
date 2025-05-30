# 📄 Documentação Técnica – Sistema de Gestão de Revendas (Mobiauto)

## 📄 Set-Up Projeto
Para testar a aplicação é recomendado utilizar o arquivo docker-compose disponilizado na pasta [docker](\docker), ou é recomendado ter um servidor MySql rodando na maquina local.

### 📄 Usando o docker
Inicie o docker e abra o seu CMD (Command prompt), pelo CMD acesse a pasta que contém o arquivo **docker-compose.yml**.

Após acessar a pasta que contém o docker-compose.yml pelo CMD, execute o comando **docker-compose up**
e aguarde o mysql instanciar.

Caso aconteça algum erro procure por **serviços** na barra de pesquisa, verifique se o serviço **MySql80** está rodando, se estiver pare ele e execute o comando novamente.

- Caso não tenha o docker em sua máquina, [baixe ele aqui](https://www.docker.com/products/docker-desktop/).

## 📄 Primeiro uso da aplicação
Você não deve se preocupar com os dados existentes no banco de dados, a aplicação se responsabilizará por executar os inserts necessários para o seu uso.
Caso deseje verificar quais são os inserts e dados utilizados verifique o arquivo **DataInitializer** presente na [nesta pasta](src/main/java/com/testeTecnico/revenda/config).

## 🧭 Visão Geral

Este projeto foi desenvolvido como parte de um **teste técnico para a empresa Mobiauto**, com o objetivo de demonstrar habilidades em desenvolvimento backend utilizando Java e Spring Boot.

A aplicação simula um **sistema de gestão de oportunidades comerciais entre revendedoras de veículos e seus clientes**, incluindo funcionalidades de autenticação, controle de vendas, e administração de usuários e veículos.

---

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3**
- **Spring Security + JWT**
- **Spring Data JPA**
- **MySql**
- **MapStruct**
- **Lombok** 
- **Stelaceum** 
---

## 📚 Funcionalidades

### 👤 Autenticação e Autorização
- Login com e-mail e senha.
- Emissão de JWT com `usuario_id`, `revenda_id` e `user_role`.
- Controle de acesso por `roles`:
    - `ADMINISTRADOR`
    - `PROPRIETARIO`
    - `GERENTE`
    - `ASSISTENTE`.

### 🧾 Gestão de Revendas
- Criação de revendas com validação de CNPJ.
- Verificação de duplicidade de CNPJ na base de dados.

### 🚗 Gestão de Veículos
- Cadastro de veículos.
- Verificação de disponibilidade antes de associar a uma oportunidade.

### 🧑‍💼 Gestão de Usuários
- Criação e modificação de usuários por revenda.
- Controle de cargos e permissões por cargo.
- Proibição de movimentação de usuários entre revendas para quem não for administrador.

### 📈 Gestão de Oportunidades
- Atribuição automática ao funcionário com menos vendas ou data de última oportunidade mais antiga.
- Modificação e transferência com regras por cargo.
- Conclusão de oportunidades altera a contagem de vendas do funcionário.

---

## 🔐 Segurança

- JWT enviado no header `Authorization: Bearer <token>`.
- Filtro de autenticação injeta `revenda_id`, `usuario_id` e `user_role` no contexto de segurança.
- Permissões são aplicadas por role que são extraídas do JWT.

---

## 📦 Bibliotecas de Suporte

### ✅ MapStruct
Usado para realizar **conversão eficiente entre DTOs e entidades**, reduzindo boilerplate e aumentando a performance em tempo de execução, por ser baseado em geração de código.

### ✅ Lombok
Elimina a necessidade de escrever manualmente:
- Getters/Setters
- Construtores
- Métodos `equals`, `hashCode`, `toString`, etc.

Isso reduz significativamente o tamanho e a complexidade do código.

### ✅ Stelaceum
Utilizado como suporte para validações de cnpj.

---

## 📘 Postman
- Recomendo o uso do postman para o teste pois todas as requisições foram colocadas em ordem para o teste ser mais simples
de ser efetuado.
- Caso deseje testar a aplicação direto pelo postman você pode usar a collection disponilizada [aqui](\postmanCollection)


---
## 📘 Swagger
- Para acessar a documentação swagger a aplicação deve estar rodando.
- A documentação está disponível via Swagger UI:
  [`/swagger-ui.html`](http://localhost:8080/swagger-ui.html)

---

## 👨‍💻 Considerações Finais

Este sistema foi desenvolvido com foco em:

- Clareza de domínio e separação de responsabilidades
- Código enxuto e reutilizável
- Boas práticas de segurança, organização e testes
- Pronto para crescer e integrar múltiplas revendas

---

