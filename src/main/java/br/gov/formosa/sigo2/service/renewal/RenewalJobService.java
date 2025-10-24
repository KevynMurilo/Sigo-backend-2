package br.gov.formosa.sigo2.service.renewal;

import br.gov.formosa.sigo2.model.Request;
import br.gov.formosa.sigo2.model.enums.RequestStatus;
import br.gov.formosa.sigo2.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RenewalJobService {

    private final RequestRepository requestRepository;
    private final CreateRenewalRequestUseCase createRenewalRequestUseCase;

    @Scheduled(cron = "0 0 1 1 1 ?")
    public void generateAnnualRenewals() {
        log.info("[JOB INICIADO] Gerando renovações anuais...");

        LocalDate lastDayOfPreviousYear = LocalDate.now()
                .withDayOfYear(1)
                .minusDays(1);

        log.info("Buscando licenças ATIVAS que expiraram em: {}", lastDayOfPreviousYear);

        List<Request> activeLicenses = requestRepository.findByStatusAndExpiresAt(
                RequestStatus.ATIVO,
                lastDayOfPreviousYear
        );

        if (activeLicenses.isEmpty()) {
            log.info("[JOB FINALIZADO] Nenhuma licença ativa encontrada para renovação.");
            return;
        }

        log.info("Encontradas {} licenças para renovar.", activeLicenses.size());
        int successCount = 0;
        int failCount = 0;

        for (Request parentRequest : activeLicenses) {
            try {
                createRenewalRequestUseCase.execute(parentRequest);
                successCount++;
            } catch (Exception e) {
                log.error("Falha ao gerar renovação para a solicitação ID {}: {}", parentRequest.getId(), e.getMessage(), e);
                failCount++;
            }
        }

        log.info(
                "[JOB FINALIZADO] Processamento concluído. Sucessos: {}. Falhas: {}.",
                successCount,
                failCount
        );
    }
}