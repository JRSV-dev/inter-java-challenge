package com.inter.java.challenge.workflows.transferencia;

import com.inter.java.challenge.data.model.Transferencia;
import com.inter.java.challenge.data.records.TransferenciaPreparada;
import com.inter.java.challenge.data.records.TransferenciaResultado;
import com.inter.java.challenge.repository.TransferenciaRepository;
import com.inter.java.challenge.workflows.factory.ResultadoFactory;
import com.inter.java.challenge.workflows.factory.TransferenciaFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ConcluirTransferencia {

    private final TransferenciaFactory transferenciaFactory;
    private final TransferenciaRepository transferenciaRepository;
    private final ResultadoFactory resultadoFactory;
    private final Clock clock;

    public TransferenciaResultado concluir(
            TransferenciaPreparada transferenciaPreparada
    ) {
        Transferencia transferencia = transferenciaFactory.criar(
                transferenciaPreparada.comando(),
                transferenciaPreparada.cotacao(),
                transferenciaPreparada.valores(),
                LocalDateTime.now(clock)
        );
        transferenciaRepository.salvarTransferencia(transferencia);
        return resultadoFactory.criar(transferencia);
    }
}
