package cripto;

/**
 * Parametros de uma variacao do AG na 1a etapa. Os eixos de variacao sao:
 *   TM (taxa de mutacao), S (selecao), C (crossover), R (reinsercao),
 * gerando as 16 combinacoes possiveis. Os demais parametros sao fixos.
 */
public class Config {

    public enum TaxaMutacao {
        TM1(0.10), TM2(0.20);
        public final double valor;
        TaxaMutacao(double valor) { this.valor = valor; }
    }

    public enum Selecao { S1_TORNEIO, S2_ROLETA }

    public enum Crossover { C1_CX, C2_PMX }

    public enum Reinsercao { R1_ORDENADA, R2_ELITISMO }

    // Parametros fixos da 1a etapa
    public static final int POP = 100;
    public static final int GERACOES = 50;
    public static final int TOUR = 3;
    public static final double ELITISMO = 0.20;

    public final TaxaMutacao tm;
    public final Selecao selecao;
    public final Crossover crossover;
    public final Reinsercao reinsercao;

    public Config(TaxaMutacao tm, Selecao selecao, Crossover crossover, Reinsercao reinsercao) {
        this.tm = tm;
        this.selecao = selecao;
        this.crossover = crossover;
        this.reinsercao = reinsercao;
    }

    /** Taxa de crossover: 60%, exceto na reinsercao R2 (elitismo) onde e 80%. */
    public double taxaCrossover() {
        return reinsercao == Reinsercao.R2_ELITISMO ? 0.80 : 0.60;
    }

    public double taxaMutacao() {
        return tm.valor;
    }

    /** Indice 0..15 estavel para semeadura reproducivel do gerador aleatorio. */
    public int indice() {
        return ((tm.ordinal() * 2 + selecao.ordinal()) * 2 + crossover.ordinal()) * 2
                + reinsercao.ordinal();
    }

    /** Codigo curto da configuracao, ex.: "TM1-S1-C1-R1". */
    public String codigo() {
        return tm.name()
                + "-" + selecao.name().substring(0, 2)
                + "-" + crossover.name().substring(0, 2)
                + "-" + reinsercao.name().substring(0, 2);
    }
}
