# Fluxo de execução

## **Observação:**
Nos pontos onde seria necessário realizar upload de arquivos, foi adotado o uso de URLs como alternativa temporária. Isso se deve ao fato de que o upload direto ainda não foi implementado, pois o desenvolvimento está sendo feito remotamente, o que impossibilita testes locais com o serviço de armazenamento.

Na próxima semana, com o ambiente local disponível, será possível implementar tanto o login via ConectaID quanto o upload de arquivos para o storage.

Para fins de teste, recomenda-se utilizar URLs simuladas e o endpoint de login com ConectaID em modo de simulação (/api/auth/conectaid/simulate).

---

**Legenda:**

* 👤 **Papel:** O papel do usuário que executa a ação.
* 🔑 **Autenticação:** Se a rota exige um token JWT válido.
* ➡️ **Próximo Passo:** Indica para qual etapa o fluxo geralmente segue.

---

**Passo 1: Login e Preparação do Usuário (SOLICITANTE)**

1.  **Obter Token (Login):**
    * 👤 **Papel:** Público / Solicitante
    * 🔑 **Autenticação:** Não
    * **Rota:** `GET /api/auth/conectaid/callback?code={code}` (ou `POST /api/auth/conectaid/simulate` em DEV)
    * **Ação:** O sistema recebe o código do Conectald (ou dados simulados), encontra ou cria o usuário `SOLICITANTE` no banco, gera um token JWT e o retorna.
    * **Verificar:** Obtenha o token JWT (e guarde-o) e verifique os dados do `LoginResponseDTO`. O usuário no banco deve ter o papel `SOLICITANTE`.
    * ➡️ **Próximo Passo:** Onboarding (se for o primeiro acesso).

2.  **Completar Onboarding:**
    * 👤 **Papel:** **SOLICITANTE**
    * 🔑 **Autenticação:** **Sim** (Use o token JWT obtido)
    * **Rota:** `POST /api/users/me/onboarding`
    * **Ação:** Envia as URLs dos documentos pessoais (Identidade F/V, Comp. Residência). Necessário apenas na primeira vez.
    * **Input:** `OnboardingDataDTO`.
    * **Verificar:** O campo `onboardingCompleted` do usuário vira `true`.
    * ➡️ **Próximo Passo:** Criação da Solicitação.

---

**Passo 2: Criação da Solicitação (SOLICITANTE)**

1.  **Criar Nova Solicitação:**
    * 👤 **Papel:** **SOLICITANTE**
    * 🔑 **Autenticação:** **Sim** (e `onboardingCompleted` deve ser `true`).
    * **Rota:** `POST /api/requests`
    * **Ação:** Submete o formulário completo para solicitar o uso do espaço público, indicando se é PF ou PJ.
    * **Input:** `CreateNewRequestDTO`.
    * **Verificar:** Uma nova `Request` é criada no banco com status `NOVA`. O protocolo é gerado. A resposta é `201 Created` com `RequestDetailsDTO`.
    * ➡️ **Próximo Passo:** Aguardar Triagem do Secretário.

---

**Passo 3: Triagem da Solicitação (SECRETARIO)**

1.  **Listar Solicitações Novas:**
    * 👤 **Papel:** **SECRETARIO**
    * 🔑 **Autenticação:** **Sim**
    * **Rota:** `GET /api/requests?status=NOVA`
    * **Ação:** Busca as solicitações que precisam de triagem.
    * **Verificar:** A solicitação criada no Passo 2 deve aparecer na lista (`Page<RequestSummaryDTO>`). Obtenha o `ID` dela.

2.  **Atribuir para Vistoria:**
    * 👤 **Papel:** **SECRETARIO**
    * 🔑 **Autenticação:** **Sim**
    * **Rota:** `POST /api/requests/{id_da_solicitacao}/triage`
    * **Ação:** Encaminha a solicitação para o(s) agente(s) de campo correto(s) com base no "Tipo de Comércio".
    * **Input:** `TriageRequestDTO` (com `fiscalUserId` e, se necessário, `sanitaryUserId`).
    * **Verificar:** O status da `Request` muda para `EM_VISTORIA` ou `EM_VISTORIA_FISCAL_E_SANITARIA`. Uma ou duas `Inspection`s são criadas com status `PENDENTE` e associadas aos respectivos agentes.
    * ➡️ **Próximo Passo:** Execução da Vistoria pelo(s) agente(s) atribuído(s).

---

**Passo 4: Execução da Vistoria (FISCAL / VIGILANTE SANITARIO)**

1.  **Listar Vistorias Pendentes:**
    * 👤 **Papel:** **FISCAL** ou **VIGILANTE SANITARIO**
    * 🔑 **Autenticação:** **Sim**
    * **Rota:** `GET /api/inspections/my-pending`
    * **Ação:** O agente busca sua fila de trabalho.
    * **Verificar:** A(s) vistoria(s) criada(s) no Passo 3 deve(m) aparecer na lista (`Page<InspectionSummaryDTO>`). Obtenha o(s) `ID`(s).

2.  **Ver Detalhes da Vistoria:**
    * 👤 **Papel:** **FISCAL** ou **VIGILANTE SANITARIO**
    * 🔑 **Autenticação:** **Sim**
    * **Rota:** `GET /api/inspections/{id_da_vistoria}`
    * **Ação:** Carrega os detalhes da vistoria, incluindo as perguntas do checklist que o `ADMIN_MASTER` configurou para aquele tipo (`FISCAL` ou `SANITARIA`).
    * **Verificar:** Retorna `InspectionDetailsDTO` com a lista `checklistQuestions`.

3.  **Submeter Resultado (Cenário 1: Aprovação):**
    * 👤 **Papel:** **FISCAL** ou **VIGILANTE SANITARIO**
    * 🔑 **Autenticação:** **Sim**
    * **Rota:** `POST /api/inspections/{id_da_vistoria}/approve`
    * **Ação:** O agente envia as respostas do checklist, fotos e (no caso do Vigilante) a taxa, aprovando a vistoria.
    * **Input:** `SubmitInspectionDTO`. **Importante:** O Fiscal *não* envia `calculatedFee`; o Vigilante *envia*.
    * **Verificar:** A `Inspection` muda para status `APROVADA`. Se *todas* as vistorias da `Request` pai estiverem `APROVADA`, o status da `Request` muda para `AGUARDANDO_EMISSAO_BOLETO`.
    * ➡️ **Próximo Passo (se Aprovado):** Geração de Boleto.

4.  **Submeter Resultado (Cenário 2: Correção - Apenas Vigilante):**
    * 👤 **Papel:** **VIGILANTE SANITARIO**
    * 🔑 **Autenticação:** **Sim**
    * **Rota:** `POST /api/inspections/{id_da_vistoria}/request-correction`
    * **Ação:** O Vigilante identifica problemas e solicita correções ao solicitante.
    * **Input:** `SubmitCorrectionDTO` (observações são obrigatórias).
    * **Verificar:** A `Inspection` muda para status `CORRECAO`. O status da `Request` pai muda para `AGUARDANDO_CORRECAO`.
    * ➡️ **Próximo Passo (se Correção):** Resposta do Solicitante.

---

**Passo 5: Resposta à Correção (SOLICITANTE)**

1.  **Enviar Correção:**
    * 👤 **Papel:** **SOLICITANTE**
    * 🔑 **Autenticação:** **Sim**
    * **Rota:** `POST /api/requests/{id_da_solicitacao}/submit-correction`
    * **Ação:** O solicitante informa que realizou as correções pedidas e, se necessário, envia novas fotos.
    * **Input:** `SubmitCorrectionResponseDTO`.
    * **Verificar:** O status da `Request` volta para `EM_VISTORIA_FISCAL_E_SANITARIA` (ou similar). A `Inspection` sanitária volta para status `PENDENTE`.
    * ➡️ **Próximo Passo:** Retorna ao Passo 4 (Vigilante reavalia a vistoria).

---

**Passo 6: Módulo Financeiro (ADMINISTRATIVO)**

1.  **Gerar Boleto:**
    * 👤 **Papel:** **ADMINISTRATIVO**
    * 🔑 **Autenticação:** **Sim**
    * **Rota:** `POST /api/payments/generate-bill/request/{id_da_solicitacao}`
    * **Ação:** Gera o boleto consolidado para uma `Request` que está `AGUARDANDO_EMISSAO_BOLETO`.
    * **Input:** `GenerateBillRequestDTO` (`dueDate`, `billUrl`).
    * **Verificar:** Um `Payment` é criado com status `PENDENTE`. O status da `Request` muda para `AGUARDANDO_PAGAMENTO`.
    * ➡️ **Próximo Passo:** Aguardar pagamento do Solicitante (e baixa manual).

2.  **Registrar Baixa Manual:**
    * 👤 **Papel:** **ADMINISTRATIVO**
    * 🔑 **Autenticação:** **Sim**
    * **Rota:** `POST /api/payments/{id_do_pagamento}/register-manual-payment`
    * **Ação:** Confirma o recebimento do pagamento.
    * **Verificar:** O status do `Payment` muda para `PAGO`. O status da `Request` muda para `ATIVO`. A data `expiresAt` da `Request` é definida como 31/12 do ano corrente.
    * ➡️ **Próximo Passo:** Licença ativa. Aguardar ciclo de Renovação.

---

**Passo 7: Renovação (Sistema e SOLICITANTE)**

1.  **Execução do Job (Simulado):**
    * 👤 **Papel:** Sistema (Job Agendado `@Scheduled`)
    * 🔑 **Autenticação:** Não aplicável
    * **Ação:** No início do ano, o job busca `Request`s `ATIVAS` do ano anterior e cria `Request`s filhas para renovação.
    * **Verificar:** Novas `Request`s criadas com `parentRequest` preenchido, status `AGUARDANDO_ACEITE_RENOVACAO`, e um `Payment` associado com as taxas anuais cheias.

2.  **Aceitar Renovação:**
    * 👤 **Papel:** **SOLICITANTE**
    * 🔑 **Autenticação:** **Sim**
    * **Rota:** `POST /api/requests/{id_da_renovacao}/renewal/accept`
    * **Ação:** O solicitante concorda em renovar a licença.
    * **Verificar:** O status da `Request` de renovação muda para `AGUARDANDO_PAGAMENTO`.
    * ➡️ **Próximo Passo:** Retorna ao Passo 6 (Pagamento, mas com o boleto/payment já gerado pelo Job).

3.  **Recusar Renovação:**
    * 👤 **Papel:** **SOLICITANTE**
    * 🔑 **Autenticação:** **Sim**
    * **Rota:** `POST /api/requests/{id_da_renovacao}/renewal/reject`
    * **Ação:** O solicitante não deseja renovar.
    * **Verificar:** O status da `Request` de renovação muda para `INATIVO`.
    * ➡️ **Próximo Passo:** Fim do ciclo para esta licença.

---

**Passo 8: Fluxo de Denúncia (Paralelo)**

1.  **Criar Denúncia:**
    * 👤 **Papel:** Público ou **CIDADAO_DENUNCIANTE**
    * 🔑 **Autenticação:** Opcional
    * **Rota:** `POST /api/reports`
    * **Ação:** Submete uma denúncia sobre irregularidade.
    * **Input:** `CreateReportDTO`.
    * **Verificar:** `Report` criado com status `RECEBIDA`.

2.  **Listar Denúncias para Triagem:**
    * 👤 **Papel:** **SECRETARIO**
    * 🔑 **Autenticação:** **Sim**
    * **Rota:** `GET /api/reports/triage`
    * **Ação:** Busca a fila de denúncias aguardando análise.
    * **Verificar:** A denúncia criada deve aparecer na lista (`Page<ReportSummaryDTO>`). Obtenha o `ID`.

3.  **Atribuir Denúncia:**
    * 👤 **Papel:** **SECRETARIO**
    * 🔑 **Autenticação:** **Sim**
    * **Rota:** `POST /api/reports/{id_da_denuncia}/assign`
    * **Ação:** Encaminha a denúncia para um agente de campo.
    * **Input:** `AssignReportDTO` (`assignToUserId`).
    * **Verificar:** Status do `Report` muda para `ENCAMINHADA`.

4.  **Listar Denúncias Pendentes (Agente):**
    * 👤 **Papel:** **FISCAL** ou **VIGILANTE SANITARIO**
    * 🔑 **Autenticação:** **Sim**
    * **Rota:** `GET /api/reports/my-pending`
    * **Ação:** O agente busca sua fila de denúncias a verificar.
    * **Verificar:** A denúncia atribuída deve aparecer. Obtenha o `ID`.

5.  **Resolver Denúncia:**
    * 👤 **Papel:** **FISCAL** ou **VIGILANTE SANITARIO** (atribuído)
    * 🔑 **Autenticação:** **Sim**
    * **Rota:** `POST /api/reports/{id_da_denuncia}/resolve`
    * **Ação:** Registra o resultado da verificação da denúncia.
    * **Input:** `ResolveReportDTO`.
    * **Verificar:** Status do `Report` muda para `RESOLVIDA`.

6.  **Ver Detalhes da Denúncia:**
    * 👤 **Papel:** **SECRETARIO**, **ADMIN_MASTER**, Agente Atribuído, Reportador (se não anônimo).
    * 🔑 **Autenticação:** **Sim**
    * **Rota:** `GET /api/reports/{id_da_denuncia}`
    * **Ação:** Consulta os detalhes de uma denúncia.
    * **Verificar:** Retorna `ReportDetailsDTO`.

---

**Passo 9: Administração (ADMIN_MASTER)**

* 👤 **Papel:** **ADMIN_MASTER**
* 🔑 **Autenticação:** **Sim**
* **Ações:**
    * Testar **TODAS** as rotas CRUD (`POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`) para:
        * `/api/admin/templates` (Gerenciar Checklists).
        * `/api/admin/configurations` (Gerenciar Configurações do Sistema).
        * `/api/roles` (Gerenciar Papéis).
    * Testar as rotas de gerenciamento de usuários em `/api/users`:
        * `POST /api/users` (Criar usuário interno).
        * `GET /api/users` (Listar todos).
        * `GET /api/users/{id}` (Buscar um).
        * `PUT /api/users/{id}/role` (Alterar papel).
        * `DELETE /api/users/{id}` (Desativar usuário).

