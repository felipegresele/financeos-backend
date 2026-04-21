# 💰 FinanceOS — Sistema Financeiro Multi-Usuário

Sistema completo de gestão financeira com **Spring Boot** + **React**, JWT, WebSocket em tempo real, gráficos interativos, metas financeiras e exportação PDF.

---

## 🏗️ Arquitetura

```
financeos/
├── backend/          # Spring Boot 3.2 + Java 21
│   ├── src/main/java/com/financeos/
│   │   ├── controller/     # REST endpoints
│   │   ├── service/        # Regras de negócio
│   │   ├── repository/     # JPA + queries JPQL
│   │   ├── entity/         # User, Transaction, Category, Goal
│   │   ├── dto/            # Request/Response DTOs
│   │   ├── security/       # JWT filter + Spring Security
│   │   ├── websocket/      # STOMP + SockJS
│   │   └── exception/      # Global error handler
│   └── Dockerfile
│
├── frontend/         # React 18 + Vite
│   ├── src/
│   │   ├── pages/          # Dashboard, Transações, Metas, Categorias
│   │   ├── components/     # Layout, UI components
│   │   ├── context/        # AuthContext, ThemeContext
│   │   ├── hooks/          # useFetch, useWebSocket
│   │   ├── services/       # api.js + services.js
│   │   └── utils/          # format.js
│   ├── nginx.conf
│   └── Dockerfile
│
└── docker-compose.yml
```

---

## 🚀 Como rodar

### Opção 1 — Docker Compose (recomendado)

```bash
# Clone o projeto
git clone <repo>
cd financeos

# Subir tudo (PostgreSQL + Backend + Frontend)
docker-compose up --build
```

Acesse: **http://localhost:5173**

---

### Opção 2 — Desenvolvimento local

**Pré-requisitos:** Java 21, Maven, Node 20, PostgreSQL 15+

#### Backend
```bash
cd backend

# Configure o banco em src/main/resources/application.properties
# spring.datasource.url=jdbc:postgresql://localhost:5432/financeos
# spring.datasource.username=postgres
# spring.datasource.password=postgres

mvn spring-boot:run
# API disponível em http://localhost:8080
```

#### Frontend
```bash
cd frontend
npm install
npm run dev
# App disponível em http://localhost:5173
```

---

## 🔑 API Endpoints

### Auth
| Método | Endpoint          | Descrição         |
|--------|-------------------|-------------------|
| POST   | /api/auth/register | Registrar usuário |
| POST   | /api/auth/login    | Login + JWT       |
| POST   | /api/auth/refresh  | Refresh token     |
| POST   | /api/auth/logout   | Logout            |

### Transações
| Método | Endpoint               | Descrição              |
|--------|------------------------|------------------------|
| GET    | /api/transactions      | Listar (filtros + page)|
| GET    | /api/transactions/:id  | Buscar por ID          |
| POST   | /api/transactions      | Criar                  |
| PUT    | /api/transactions/:id  | Atualizar              |
| DELETE | /api/transactions/:id  | Excluir                |

### Dashboard
| Método | Endpoint       | Descrição              |
|--------|----------------|------------------------|
| GET    | /api/dashboard | Resumo + gráficos      |

### Metas
| Método | Endpoint              | Descrição         |
|--------|-----------------------|-------------------|
| GET    | /api/goals            | Listar metas      |
| POST   | /api/goals            | Criar meta        |
| PUT    | /api/goals/:id        | Atualizar         |
| POST   | /api/goals/:id/deposit| Depositar valor   |
| DELETE | /api/goals/:id        | Excluir           |

### Categorias
| Método | Endpoint             | Descrição    |
|--------|----------------------|--------------|
| GET    | /api/categories      | Listar       |
| POST   | /api/categories      | Criar        |
| PUT    | /api/categories/:id  | Atualizar    |
| DELETE | /api/categories/:id  | Excluir      |

### Exportação
| Método | Endpoint       | Descrição         |
|--------|----------------|-------------------|
| GET    | /api/export/pdf| Exportar PDF      |

---

## ✨ Funcionalidades

- ✅ **Cadastro / Login** com JWT + Refresh Token automático
- ✅ **Dashboard** com 4 gráficos (Area, Bar, Pie, Recentes)
- ✅ **CRUD completo** de transações com filtros e paginação
- ✅ **Categorias personalizadas** com ícone e cor
- ✅ **Metas financeiras** com barra de progresso e depósitos
- ✅ **Exportação PDF** com sumário e tabela de transações
- ✅ **Dark Mode / Light Mode** com persistência
- ✅ **WebSocket** para atualizações em tempo real
- ✅ **Proteção de rotas** no frontend
- ✅ **Multi-usuário** — dados totalmente isolados por usuário
- ✅ **Docker Compose** para deploy fácil

---

## 🛠️ Stack

| Camada    | Tecnologia                          |
|-----------|-------------------------------------|
| Backend   | Spring Boot 3.2, Java 21            |
| Segurança | Spring Security, JWT (jjwt 0.12)    |
| Banco     | PostgreSQL 16 + Spring Data JPA     |
| WebSocket | STOMP + SockJS                      |
| PDF       | iText 8                             |
| Frontend  | React 18 + Vite                     |
| Gráficos  | Recharts                            |
| HTTP      | Axios (com interceptors e refresh)  |
| Roteamento| React Router v6                     |
| Deploy    | Docker + Nginx                      |
