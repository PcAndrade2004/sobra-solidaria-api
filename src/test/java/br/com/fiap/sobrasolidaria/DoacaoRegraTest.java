package br.com.fiap.sobrasolidaria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import br.com.fiap.sobrasolidaria.exception.RegraNegocioException;
import br.com.fiap.sobrasolidaria.model.Doacao;
import br.com.fiap.sobrasolidaria.model.StatusDoacao;

/** Testa a regra de negócio direto no model, sem Spring nem banco. */
class DoacaoRegraTest {

    private Doacao doacao(String quantidade, LocalDate validade) {
        Doacao d = new Doacao();
        d.setQuantidadeDisponivel(new BigDecimal(quantidade));
        d.setUnidade("kg");
        d.setDataValidade(validade);
        d.setStatus(StatusDoacao.DISPONIVEL);
        return d;
    }

    @Test
    void doacaoVencidaNaoPodeSerRetirada() {
        Doacao d = doacao("10", LocalDate.now().minusDays(1));
        assertThrows(RegraNegocioException.class, () -> d.registrarRetirada(BigDecimal.ONE));
        assertEquals(0, new BigDecimal("10").compareTo(d.getQuantidadeDisponivel()));
    }

    @Test
    void doacaoQueVenceHojeAindaPodeSerRetirada() {
        Doacao d = doacao("10", LocalDate.now());
        d.registrarRetirada(new BigDecimal("3"));
        assertEquals(0, new BigDecimal("7").compareTo(d.getQuantidadeDisponivel()));
    }

    @Test
    void retiradaTotalEsgotaADoacao() {
        Doacao d = doacao("2.50", LocalDate.now().plusDays(1));
        d.registrarRetirada(new BigDecimal("2.50"));
        assertEquals(StatusDoacao.ESGOTADA, d.getStatus());
    }

    @Test
    void naoRetiraMaisQueODisponivel() {
        Doacao d = doacao("2", LocalDate.now().plusDays(1));
        assertThrows(RegraNegocioException.class, () -> d.registrarRetirada(new BigDecimal("2.01")));
    }
}
