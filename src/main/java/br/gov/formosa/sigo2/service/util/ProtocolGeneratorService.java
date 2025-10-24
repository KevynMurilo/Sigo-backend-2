package br.gov.formosa.sigo2.service.util;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class ProtocolGeneratorService {
    public String generate() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}