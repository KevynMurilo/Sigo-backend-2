package br.gov.formosa.sigo2.config;

import br.gov.formosa.sigo2.model.*;
import br.gov.formosa.sigo2.model.enums.*;
import br.gov.formosa.sigo2.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeederConfig implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ConfigurationRepository configurationRepository;
    private final ChecklistTemplateRepository checklistTemplateRepository;
    private final RequestRepository requestRepository;
    private final InspectionRepository inspectionRepository;
    private final PaymentRepository paymentRepository;
    private final ReportRepository reportRepository;

    private Role roleSolicitante;
    private Role roleCidadao;
    private Role roleSecretario;
    private Role roleFiscal;
    private Role roleVigilante;
    private Role roleAdministrativo;
    private Role roleAdminMaster;

    private User userSolicitante1;
    private User userCidadao1;
    private User userSecretario1;
    private User userFiscal1;
    private User userVigilante1;
    private User userAdministrativo1;
    private User userAdminMaster1;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Iniciando Data Seeder...");

        loadOrCreateRoles();
        loadOrCreateUsers();
        seedConfigurations();
        seedChecklistTemplates();
        seedRequestsInspectionsPayments();
        seedReports();

        log.info("Data Seeder concluído.");
    }

    private void loadOrCreateRoles() {
        if (roleRepository.count() == 0) {
            log.info("Criando Roles padrão...");
            roleSolicitante = createRole("SOLICITANTE");
            roleCidadao = createRole("CIDADAO_DENUNCIANTE");
            roleSecretario = createRole("SECRETARIO");
            roleFiscal = createRole("FISCAL");
            roleVigilante = createRole("VIGILANTE_SANITARIO");
            roleAdministrativo = createRole("ADMINISTRATIVO");
            roleAdminMaster = createRole("ADMIN_MASTER");

            roleRepository.saveAll(Arrays.asList(
                    roleSolicitante, roleCidadao, roleSecretario, roleFiscal,
                    roleVigilante, roleAdministrativo, roleAdminMaster
            ));
            log.info("Roles padrão criadas.");
        } else {
            roleSolicitante = roleRepository.findByName("SOLICITANTE").orElseGet(() -> roleRepository.save(createRole("SOLICITANTE")));
            roleCidadao = roleRepository.findByName("CIDADAO_DENUNCIANTE").orElseGet(() -> roleRepository.save(createRole("CIDADAO_DENUNCIANTE")));
            roleSecretario = roleRepository.findByName("SECRETARIO").orElseGet(() -> roleRepository.save(createRole("SECRETARIO")));
            roleFiscal = roleRepository.findByName("FISCAL").orElseGet(() -> roleRepository.save(createRole("FISCAL")));
            roleVigilante = roleRepository.findByName("VIGILANTE_SANITARIO").orElseGet(() -> roleRepository.save(createRole("VIGILANTE_SANITARIO")));
            roleAdministrativo = roleRepository.findByName("ADMINISTRATIVO").orElseGet(() -> roleRepository.save(createRole("ADMINISTRATIVO")));
            roleAdminMaster = roleRepository.findByName("ADMIN_MASTER").orElseGet(() -> roleRepository.save(createRole("ADMIN_MASTER")));
            log.info("Roles existentes carregadas.");
        }
    }

    private void loadOrCreateUsers() {
        if (userRepository.count() == 0) {
            log.info("Criando Usuários padrão...");

            userSolicitante1 = createUser("Solicitante Um", "solicitante1@email.com", "11111111111", roleSolicitante, true);
            userCidadao1 = createUser("Cidadão Denunciante Um", "cidadao1@email.com", "22222222222", roleCidadao, true);
            userSecretario1 = createUser("Secretário Um", "secretario1@email.com", "33333333333", roleSecretario, true);
            userFiscal1 = createUser("Fiscal Um", "fiscal1@email.com", "44444444444", roleFiscal, true);
            userVigilante1 = createUser("Vigilante Um", "vigilante1@email.com", "55555555555", roleVigilante, true);
            userAdministrativo1 = createUser("Administrativo Um", "admin1@email.com", "66666666666", roleAdministrativo, true);
            userAdminMaster1 = createUser("Admin Master", "master@email.com", "77777777777", roleAdminMaster, true);

            userRepository.saveAll(Arrays.asList(
                    userSolicitante1, userCidadao1, userSecretario1, userFiscal1,
                    userVigilante1, userAdministrativo1, userAdminMaster1
            ));
            log.info("Usuários padrão criados.");
        } else {
            userSolicitante1 = userRepository.findByEmail("solicitante1@email.com").orElseGet(() -> userRepository.save(createUser("Solicitante Um", "solicitante1@email.com", "11111111111", roleSolicitante, true)));
            userCidadao1 = userRepository.findByEmail("cidadao1@email.com").orElseGet(() -> userRepository.save(createUser("Cidadão Denunciante Um", "cidadao1@email.com", "22222222222", roleCidadao, true)));
            userSecretario1 = userRepository.findByEmail("secretario1@email.com").orElseGet(() -> userRepository.save(createUser("Secretário Um", "secretario1@email.com", "33333333333", roleSecretario, true)));
            userFiscal1 = userRepository.findByEmail("fiscal1@email.com").orElseGet(() -> userRepository.save(createUser("Fiscal Um", "fiscal1@email.com", "44444444444", roleFiscal, true)));
            userVigilante1 = userRepository.findByEmail("vigilante1@email.com").orElseGet(() -> userRepository.save(createUser("Vigilante Um", "vigilante1@email.com", "55555555555", roleVigilante, true)));
            userAdministrativo1 = userRepository.findByEmail("admin1@email.com").orElseGet(() -> userRepository.save(createUser("Administrativo Um", "admin1@email.com", "66666666666", roleAdministrativo, true)));
            userAdminMaster1 = userRepository.findByEmail("master@email.com").orElseGet(() -> userRepository.save(createUser("Admin Master", "master@email.com", "77777777777", roleAdminMaster, true)));
            log.info("Usuários existentes carregados.");
        }
    }


    private void seedConfigurations() {
        if (configurationRepository.count() == 0) {
            log.info("Criando Configurações padrão...");
            List<Configuration> configs = Arrays.asList(
                    createConfig("SANITARY_COMMERCE_TYPES", "[\"Alimentação\", \"Alimentação - Lanches\", \"Saúde\"]"),
                    createConfig("TAXA_OCUPACAO_ANUAL_BASE", "150.00"),
                    createConfig("TAXA_VISTORIA_SANITARIA_ANUAL", "80.00"),
                    createConfig("RENEWAL_PAYMENT_DAYS", "30"),
                    createConfig("LICENSE_INACTIVITY_DAYS", "45")
            );
            configurationRepository.saveAll(configs);
            log.info("Configurações padrão criadas.");
        } else {
            log.info("Configurações já existem.");
        }
    }

    private void seedChecklistTemplates() {
        if (checklistTemplateRepository.count() == 0) {
            log.info("Criando Templates de Checklist padrão...");

            ChecklistTemplate fiscalTemplate = createTemplate("Vistoria Fiscal Padrão", InspectionType.FISCAL, true);
            checklistTemplateRepository.save(fiscalTemplate);
            List<ChecklistTemplateItem> fiscalItems = new ArrayList<>(Arrays.asList(
                    createItem(fiscalTemplate, "Localização confere com o solicitado?", ChecklistItemType.BOOLEAN, 1, true, null),
                    createItem(fiscalTemplate, "Metragem confere com o solicitado? (LxC)", ChecklistItemType.TEXT, 2, true, null),
                    createItem(fiscalTemplate, "Área calculada (m²)", ChecklistItemType.NUMBER, 3, true, null),
                    createItem(fiscalTemplate, "Tipo de estrutura?", ChecklistItemType.SELECT, 4, true, "[\"Barraca\",\"Quiosque\",\"Food Truck\",\"Carrinho\"]"),
                    createItem(fiscalTemplate, "Foto da estrutura", ChecklistItemType.PHOTO, 5, false, null)
            ));
            fiscalTemplate.setItems(fiscalItems);
            checklistTemplateRepository.save(fiscalTemplate);

            ChecklistTemplate sanitaryTemplate = createTemplate("Vistoria Sanitária - Alimentos", InspectionType.SANITARIA, true);
            checklistTemplateRepository.save(sanitaryTemplate);
            List<ChecklistTemplateItem> sanitaryItems = new ArrayList<>(Arrays.asList(
                    createItem(sanitaryTemplate, "Possui fonte de água potável?", ChecklistItemType.BOOLEAN, 1, true, null),
                    createItem(sanitaryTemplate, "Armazenamento de alimentos adequado?", ChecklistItemType.BOOLEAN, 2, true, null),
                    createItem(sanitaryTemplate, "Higiene do manipulador?", ChecklistItemType.SELECT, 3, true, "[\"Bom\",\"Regular\",\"Ruim\"]"),
                    createItem(sanitaryTemplate, "Validade dos produtos verificada?", ChecklistItemType.BOOLEAN, 4, true, null),
                    createItem(sanitaryTemplate, "Observações gerais", ChecklistItemType.TEXT_AREA, 5, false, null),
                    createItem(sanitaryTemplate, "Foto do local de armazenamento", ChecklistItemType.PHOTO, 6, false, null)
            ));
            sanitaryTemplate.setItems(sanitaryItems);
            checklistTemplateRepository.save(sanitaryTemplate);

            log.info("Templates de Checklist padrão criados.");
        } else {
            log.info("Templates de Checklist já existem.");
        }
    }

    private void seedRequestsInspectionsPayments() {
        if (requestRepository.count() == 0) {
            log.info("Criando Solicitações, Vistorias e Pagamentos de exemplo...");

            Request reqNova = createRequest(userSolicitante1, "Quiosque Praça", "Alimentação - Lanches", BigDecimal.valueOf(10.5), RequestStatus.NOVA, null);
            requestRepository.save(reqNova);

            Request reqEmVistoriaFiscal = createRequest(userSolicitante1, "Artesanato Local", "Artesanato", BigDecimal.valueOf(6.0), RequestStatus.EM_VISTORIA, null);
            requestRepository.save(reqEmVistoriaFiscal);
            Inspection inspFiscalPendente = createInspection(reqEmVistoriaFiscal, userFiscal1, InspectionType.FISCAL, InspectionStatus.PENDENTE);
            inspectionRepository.save(inspFiscalPendente);
            reqEmVistoriaFiscal.setInspections(new ArrayList<>(Arrays.asList(inspFiscalPendente)));
            requestRepository.save(reqEmVistoriaFiscal);


            Request reqEmVistoriaAmbas = createRequest(userSolicitante1, "Food Truck Gourmet", "Alimentação", BigDecimal.valueOf(15.0), RequestStatus.EM_VISTORIA_FISCAL_E_SANITARIA, null);
            requestRepository.save(reqEmVistoriaAmbas);
            Inspection inspFiscalPendente2 = createInspection(reqEmVistoriaAmbas, userFiscal1, InspectionType.FISCAL, InspectionStatus.PENDENTE);
            Inspection inspSanitariaPendente = createInspection(reqEmVistoriaAmbas, userVigilante1, InspectionType.SANITARIA, InspectionStatus.PENDENTE);
            inspectionRepository.saveAll(List.of(inspFiscalPendente2, inspSanitariaPendente));
            reqEmVistoriaAmbas.setInspections(new ArrayList<>(Arrays.asList(inspFiscalPendente2, inspSanitariaPendente)));
            requestRepository.save(reqEmVistoriaAmbas);


            Request reqAguardandoBoleto = createRequest(userSolicitante1, "Bazar Comunitário", "Vestuário", BigDecimal.valueOf(9.0), RequestStatus.AGUARDANDO_EMISSAO_BOLETO, null);
            requestRepository.save(reqAguardandoBoleto);
            Inspection inspFiscalAprovada = createInspection(reqAguardandoBoleto, userFiscal1, InspectionType.FISCAL, InspectionStatus.APROVADA);
            inspFiscalAprovada.setCalculatedFee(BigDecimal.valueOf(120.50));
            inspFiscalAprovada.setInspectionDate(LocalDateTime.now().minusDays(1));
            inspectionRepository.save(inspFiscalAprovada);
            reqAguardandoBoleto.setInspections(new ArrayList<>(Arrays.asList(inspFiscalAprovada)));
            requestRepository.save(reqAguardandoBoleto);


            Request reqAguardandoPgto = createRequest(userSolicitante1, "Pipoca do Parque", "Alimentação - Lanches", BigDecimal.valueOf(4.0), RequestStatus.AGUARDANDO_PAGAMENTO, null);
            requestRepository.save(reqAguardandoPgto);
            Inspection inspFiscalAprovada2 = createInspection(reqAguardandoPgto, userFiscal1, InspectionType.FISCAL, InspectionStatus.APROVADA);
            inspFiscalAprovada2.setCalculatedFee(BigDecimal.valueOf(50.00));
            inspFiscalAprovada2.setInspectionDate(LocalDateTime.now().minusDays(2));
            Inspection inspSanitariaAprovada = createInspection(reqAguardandoPgto, userVigilante1, InspectionType.SANITARIA, InspectionStatus.APROVADA);
            inspSanitariaAprovada.setCalculatedFee(BigDecimal.valueOf(80.00));
            inspSanitariaAprovada.setInspectionDate(LocalDateTime.now().minusDays(1));
            inspectionRepository.saveAll(List.of(inspFiscalAprovada2, inspSanitariaAprovada));
            Payment payPendente = createPayment(reqAguardandoPgto, BigDecimal.valueOf(130.00), LocalDate.now().plusDays(10), PaymentStatus.PENDENTE, "http://boleto.example/pendente");
            paymentRepository.save(payPendente);
            reqAguardandoPgto.setInspections(new ArrayList<>(Arrays.asList(inspFiscalAprovada2, inspSanitariaAprovada)));
            reqAguardandoPgto.setPayments(new ArrayList<>(Arrays.asList(payPendente)));
            requestRepository.save(reqAguardandoPgto);


            LocalDate lastYearEnd = LocalDate.now().withDayOfYear(1).minusDays(1);
            Request reqAtivoAnoPassado = createRequest(userSolicitante1, "Flores da Estação", "Floricultura", BigDecimal.valueOf(8.0), RequestStatus.ATIVO, null);
            reqAtivoAnoPassado.setExpiresAt(lastYearEnd);
            requestRepository.save(reqAtivoAnoPassado);
            Payment payPago = createPayment(reqAtivoAnoPassado, BigDecimal.valueOf(100.00), lastYearEnd.minusMonths(1), PaymentStatus.PAGO, "http://boleto.example/pago");
            paymentRepository.save(payPago);
            reqAtivoAnoPassado.setPayments(new ArrayList<>(Arrays.asList(payPago)));
            requestRepository.save(reqAtivoAnoPassado);


            Request reqAguardandoAceite = createRequest(userSolicitante1, "Café Expresso", "Alimentação", BigDecimal.valueOf(5.0), RequestStatus.AGUARDANDO_ACEITE_RENOVACAO, reqAtivoAnoPassado);
            requestRepository.save(reqAguardandoAceite);
            Payment payRenovacao = createPayment(reqAguardandoAceite, BigDecimal.valueOf(230.00), LocalDate.now().plusDays(30), PaymentStatus.PENDENTE, null);
            paymentRepository.save(payRenovacao);
            reqAguardandoAceite.setPayments(new ArrayList<>(Arrays.asList(payRenovacao)));
            requestRepository.save(reqAguardandoAceite);

            log.info("Dados de exemplo para Request/Inspection/Payment criados.");
        } else {
            log.info("Solicitações/Vistorias/Pagamentos já existem.");
        }
    }

    private void seedReports() {
        if (reportRepository.count() == 0) {
            log.info("Criando Denúncias de exemplo...");

            Report repRecebidaAnon = createReport(null, true, "Som alto após as 22h no food truck da esquina.", ReportStatus.RECEBIDA, null);
            reportRepository.save(repRecebidaAnon);
            ReportEvidence ev1 = createEvidence(repRecebidaAnon, "http://example.com/audio.mp3");
            repRecebidaAnon.setEvidence(new ArrayList<>(Arrays.asList(ev1)));
            reportRepository.save(repRecebidaAnon);

            Report repRecebidaIdent = createReport(userCidadao1, false, "Venda de produtos vencidos na barraca de frutas.", ReportStatus.RECEBIDA, null);
            reportRepository.save(repRecebidaIdent);
            ReportEvidence ev2 = createEvidence(repRecebidaIdent, "http://example.com/foto_vencido.jpg");
            repRecebidaIdent.setEvidence(new ArrayList<>(Arrays.asList(ev2)));
            reportRepository.save(repRecebidaIdent);

            Report repEncaminhada = createReport(userCidadao1, false, "Quiosque ocupando a calçada indevidamente.", ReportStatus.ENCAMINHADA, userFiscal1);
            reportRepository.save(repEncaminhada);

            Report repResolvida = createReport(null, true, "Falta de higiene no carrinho de cachorro-quente.\n\n--- RESOLUÇÃO DA EQUIPE ---\nVerificado no local. Orientado sobre boas práticas. Situação regularizada.", ReportStatus.RESOLVIDA, userVigilante1);
            reportRepository.save(repResolvida);

            log.info("Denúncias de exemplo criadas.");
        } else {
            log.info("Denúncias já existem.");
        }
    }

    private Role createRole(String name) {
        Role role = new Role();
        role.setName(name);
        return role;
    }

    private User createUser(String fullName, String email, String cpf, Role role, boolean onboardingCompleted) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setCpf(cpf);
        user.setRole(role);
        user.setOnboardingCompleted(onboardingCompleted);
        user.setEnabled(true);
        if (onboardingCompleted && role.getName().equals("SOLICITANTE")) {
            user.setIdentityDocumentFrontUrl("http://example.com/id_front.jpg");
            user.setIdentityDocumentBackUrl("http://example.com/id_back.jpg");
            user.setProofOfResidenceUrl("http://example.com/residence.pdf");
        }
        return user;
    }

    private Configuration createConfig(String key, String value) {
        Configuration config = new Configuration();
        config.setKey(key);
        config.setValue(value);
        return config;
    }

    private ChecklistTemplate createTemplate(String name, InspectionType type, boolean active) {
        ChecklistTemplate template = new ChecklistTemplate();
        template.setName(name);
        template.setType(type);
        template.setActive(active);
        template.setItems(new ArrayList<>());
        return template;
    }

    private ChecklistTemplateItem createItem(ChecklistTemplate template, String text, ChecklistItemType type, int order, boolean required, String optionsJson) {
        ChecklistTemplateItem item = new ChecklistTemplateItem();
        item.setTemplate(template);
        item.setQuestionText(text);
        item.setQuestionType(type);
        item.setItemOrder(order);
        item.setRequired(required);
        item.setOptionsJson(optionsJson);
        return item;
    }

    private Request createRequest(User applicant, String tradeName, String commerceType, BigDecimal area, RequestStatus status, Request parent) {
        Request request = new Request();
        request.setApplicant(applicant);
        request.setTradeName(tradeName);
        request.setCommerceType(commerceType);
        request.setAreaSqM(area);
        request.setStatus(status);
        request.setCreatedAt(LocalDateTime.now().minusDays(status.ordinal()));
        request.setParentRequest(parent);
        request.setProtocol("SIGO-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());

        request.setOwnerType(OwnerType.CPF);
        request.setOwnerDocument(applicant.getCpf());

        Location loc = new Location();
        loc.setLatitude(BigDecimal.valueOf(-15.8 + Math.random() * 0.1));
        loc.setLongitude(BigDecimal.valueOf(-47.9 + Math.random() * 0.1));
        loc.setRequest(request);
        request.setLocation(loc);

        request.setPhotoFrontUrl("http://example.com/front.jpg");
        request.setPhotoLeftUrl("http://example.com/left.jpg");
        request.setPhotoRightUrl("http://example.com/right.jpg");
        request.setPhotoBackUrl("http://example.com/back.jpg");

        request.setInspections(new ArrayList<>());
        request.setPayments(new ArrayList<>());
        request.setStatusHistory(new ArrayList<>());

        return request;
    }

    private Inspection createInspection(Request request, User inspector, InspectionType type, InspectionStatus status) {
        Inspection inspection = new Inspection();
        inspection.setRequest(request);
        inspection.setInspector(inspector);
        inspection.setType(type);
        inspection.setStatus(status);
        inspection.setEvidence(new ArrayList<>());
        inspection.setInspectionItems(new ArrayList<>());
        return inspection;
    }

    private Payment createPayment(Request request, BigDecimal amount, LocalDate dueDate, PaymentStatus status, String billUrl) {
        Payment payment = new Payment();
        payment.setRequest(request);
        payment.setTotalAmount(amount);
        payment.setDueDate(dueDate);
        payment.setStatus(status);
        payment.setBillUrl(billUrl);
        payment.setPaymentItemsJson("[{\"description\":\"TAXA_OCUPACAO\",\"amount\":" + amount.toString() + "}]");
        return payment;
    }

    private Report createReport(User reporter, boolean anonymous, String description, ReportStatus status, User assignedTo) {
        Report report = new Report();
        report.setReporter(reporter);
        report.setAnonymous(anonymous);
        report.setDescription(description);
        report.setStatus(status);
        report.setAssignedTo(assignedTo);
        report.setProtocol("DEN-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        report.setLatitude(BigDecimal.valueOf(-15.85 + Math.random() * 0.1));
        report.setLongitude(BigDecimal.valueOf(-47.95 + Math.random() * 0.1));
        report.setEvidence(new ArrayList<>());
        return report;
    }

    private ReportEvidence createEvidence(Report report, String url) {
        ReportEvidence evidence = new ReportEvidence();
        evidence.setReport(report);
        evidence.setFileUrl(url);
        return evidence;
    }
}