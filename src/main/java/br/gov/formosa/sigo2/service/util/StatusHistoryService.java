package br.gov.formosa.sigo2.service.util;

import br.gov.formosa.sigo2.model.Request;
import br.gov.formosa.sigo2.model.User;
import br.gov.formosa.sigo2.model.enums.RequestStatus;
import org.springframework.stereotype.Service;

@Service
public class StatusHistoryService {
    //
    public void logStatusChange(Request request, RequestStatus from, RequestStatus to, User responsible) {
        // Lógica para criar e salvar um StatusHistory
    }
}