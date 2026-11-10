# Estúdio Ana Elizabeth

App de um estúdio de treino focado em **corrida**: cada aluna escolhe seu objetivo, recebe treinos
de acordo com ele, registra o que fez e acompanha a própria evolução. Quando fica **3 dias ou mais
sem treinar**, recebe um aviso automático no **WhatsApp**.

> Estado atual: **esqueleto** (estrutura + fluxos principais de ponta a ponta). Falta autenticação,
> telas de cadastro e a periodização real dos treinos. Veja "Próximos passos".

## Estrutura

```
estudio-ana-elizabeth/
├── backend/            # API REST em Java 17 + Spring Boot 3 (+ webhook do WhatsApp)
├── mobile/             # App Android em Java (Retrofit + RecyclerView)
├── docker-compose.yml  # Postgres + API para desenvolvimento
├── .env.example        # variáveis de ambiente (copie para .env)
└── .github/workflows/  # CI (build + testes)
```

## O que já funciona

| Funcionalidade | Onde |
|---|---|
| Cadastro de aluna (nível, objetivo, opt-in de WhatsApp) | `AlunaController` / `AlunaService` |
| Geração dos treinos da semana por nível + objetivo | `PlanoSemanalService` (regra provisória) |
| Registro do treino feito (km + minutos) | `POST /api/treinos/{id}/concluir` |
| Evolução: total de treinos, km, minutos e pace médio | `GET /api/alunas/{id}/evolucao` |
| Aviso de inatividade (≥ 3 dias), todo dia às 09:00 (São Paulo) | `InatividadeScheduler` / `InatividadeService` |
| Webhook do WhatsApp com verificação e assinatura HMAC | `whatsapp/` |
| Aluna responde **TREINEI** no WhatsApp → zera a inatividade | `WhatsAppWebhookService` |
| App Android: lista treinos, conclui treino, mostra evolução | `mobile/` |

## Rodando o backend

Pré-requisitos: Java 17+, Maven e Docker.

```bash
# 1) banco
docker compose up -d db

# 2) API (http://localhost:8080)
cd backend
mvn spring-boot:run

# testes
mvn test
```

Ou tudo em containers: `cp .env.example .env && docker compose up --build`.

### Testando rápido

```bash
# cadastra uma aluna
curl -X POST localhost:8080/api/alunas -H 'Content-Type: application/json' -d '{
  "nome": "Maria Silva", "telefone": "11999999999", "email": "maria@exemplo.com",
  "nivel": "INICIANTE", "objetivo": "PRIMEIROS_5K", "aceitaWhatsapp": true }'

# gera os treinos da semana
curl -X POST localhost:8080/api/alunas/1/treinos/semana

# marca um treino como concluído
curl -X POST localhost:8080/api/treinos/1/concluir -H 'Content-Type: application/json' \
  -d '{"distanciaKm": 3.2, "duracaoMinutos": 24}'

# evolução
curl localhost:8080/api/alunas/1/evolucao
```

Valores aceitos — `nivel`: `INICIANTE`, `INTERMEDIARIO`, `AVANCADO`;
`objetivo`: `CONDICIONAMENTO`, `PRIMEIROS_5K`, `DEZ_K`, `MEIA_MARATONA`, `MARATONA`.

## Integração com o WhatsApp (Cloud API da Meta)

1. Crie um app em <https://developers.facebook.com> e adicione o produto **WhatsApp**.
2. Preencha o `.env` com `WHATSAPP_PHONE_NUMBER_ID`, `WHATSAPP_ACCESS_TOKEN`,
   `WHATSAPP_APP_SECRET` e um `WHATSAPP_VERIFY_TOKEN` inventado por você.
3. Exponha o backend por uma URL **HTTPS pública** (em desenvolvimento: ngrok ou Cloudflare Tunnel).
4. No painel da Meta, configure o webhook com a URL `https://SEU-DOMINIO/webhook/whatsapp`, o mesmo
   verify token do `.env`, e assine o campo **messages**.
5. Crie e envie para aprovação um **template de mensagem** chamado `aluna_inativa` (idioma `pt_BR`)
   com duas variáveis, por exemplo:

   > Oi, {{1}}! Faz {{2}} dias que você não treina por aqui. Bora retomar? Quando concluir seu próximo
   > treino, responda TREINEI para eu registrar. 🏃‍♀️

Pontos de atenção:

- **Mensagens iniciadas pelo estúdio exigem template aprovado** (é o caso do aviso de inatividade).
  Texto livre só é permitido dentro de 24 h após a aluna escrever.
- A Meta **cobra por mensagem/conversa** conforme a categoria do template; confira a tabela de preços atual.
- Só enviamos para alunas com `aceitaWhatsapp = true` (opt-in explícito, exigido pela política do WhatsApp e pela LGPD).
- Confirme em `application.yml` (`whatsapp.api-base-url`) a versão da Graph API vigente.
- Números brasileiros podem chegar com ou sem o 9º dígito; `AlunaService` já tenta as duas formas.

## App Android

1. Abra a pasta **`mobile/`** no Android Studio (ele gera o Gradle Wrapper e baixa as dependências).
2. Com o backend rodando, execute no emulador (`10.0.2.2` aponta para o seu computador).
3. Por enquanto o app usa a aluna de id `1` fixa (`MainActivity.ALUNA_ID_PROVISORIO`).

### Sobre a App Store (iOS)

Um app escrito em **Java só roda no Android**: a App Store não aceita Java. Boa notícia: o **backend
Java/Spring é reaproveitado integralmente** por qualquer app. Para iOS no futuro, as opções são:

- app nativo em Swift (usando a mesma API REST);
- reescrever o cliente em um framework multiplataforma (Flutter, React Native ou Kotlin Multiplatform),
  cobrindo Android + iOS com uma base só;
- um PWA (site instalável), que dispensa as lojas.

Vale decidir isso **antes** de investir muito na interface Android.

### Antes de publicar na Play Store

- Conferir o `targetSdk` mínimo exigido pela Play (sobe todo ano) em `mobile/app/build.gradle`.
- Ícone do app, trocar `API_BASE_URL` de release por um domínio com HTTPS, assinar o app (keystore — não suba para o Git) e gerar `.aab`.
- Política de privacidade (o app trata dados pessoais e de saúde/atividade física).

## Próximos passos sugeridos

1. **Autenticação** (Spring Security + JWT) e perfis (aluna × treinadora). **A API está aberta** — não publique na internet antes disso.
2. Migrations com **Flyway** (hoje `ddl-auto: update`, só para desenvolvimento).
3. Substituir `PlanoSemanalService` pela periodização definida pela treinadora (progressão, descanso, provas-alvo).
4. Painel/telas para a treinadora cadastrar alunas e ajustar treinos.
5. Gráficos de evolução no app (pace e km por semana).
6. ShedLock se houver mais de uma instância do backend (evita aviso duplicado).
7. LGPD: política de privacidade, consentimento registrado, exclusão de dados sob demanda.

## Subindo para o GitHub

```bash
cd estudio-ana-elizabeth
git init
git add .
git commit -m "Esqueleto inicial do app Estúdio Ana Elizabeth"
git branch -M main
git remote add origin https://github.com/SEU_USUARIO/estudio-ana-elizabeth.git
git push -u origin main
```
