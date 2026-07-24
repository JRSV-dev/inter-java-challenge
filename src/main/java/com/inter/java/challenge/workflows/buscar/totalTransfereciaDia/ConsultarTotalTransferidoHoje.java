package com.inter.java.challenge.workflows.buscar.totalTransfereciaDia;

import com.inter.java.challenge.data.records.Periodo;
import com.inter.java.challenge.repository.TransferenciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsultarTotalTransferidoHoje {

    private final TransferenciaRepository transferenciaRepository;
    private final Clock clock;

    public BigDecimal consultar(Long usuarioId) {
        Periodo periodo = periodoAtual();
        BigDecimal total = transferenciaRepository.buscarTotalTransferidoNoPeriodo(usuarioId, periodo.inicio(), periodo.fim());
        log.info("Buscando total transferido hoje.");
        return Optional.ofNullable(total).orElse(BigDecimal.ZERO);
    }

    private Periodo periodoAtual() {
        LocalDate hoje = LocalDate.now(clock);
        return new Periodo(hoje.atStartOfDay(), hoje.atTime(LocalTime.MAX));
    }
}