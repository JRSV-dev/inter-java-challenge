package com.inter.java.challenge.workflows.transacao;

import com.inter.java.challenge.data.records.TransferirDinheiro;

public interface Transacao {
    void executar(TransferirDinheiro model);
}
