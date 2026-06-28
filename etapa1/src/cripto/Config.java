package cripto;

/**
 * Parametros de uma variacao do AG na 1a etapa. Os eixos de variacao sao:
 *   TM (taxa de mutacao), S (selecao), C (crossover), R (reinsercao),
 * gerando as 16 combinacoes possiveis. Os demais parametros sao fixos.
 */
public class Config {

    public enum TaxaMutacao {
        TM1(0.10), TM2(0.20), TM3(1);
        public final double valor;
        TaxaMutacao(double valor) { this.valor = valor; }
    }

    public enum Selecao { S1_TORNEIO, S2_ROLETA }

    public enum Crossover { C1_CX, C2_PMX }

    public enum Reinsercao { R1_ORDENADA, R2_ELITISMO }

    // Parametros fixos da 1a etapa
    public static int POP = 100;
    public static int GERACOES = 50;
    public static int TOUR = 3;
    public static double ELITISMO = 0.20;
    public static double taxaCrossover;

    public final TaxaMutacao tm;
    public final Selecao selecao;
    public final Crossover crossover;
    public final Reinsercao reinsercao;

    public Config(TaxaMutacao tm, Selecao selecao, Crossover crossover, Reinsercao reinsercao) {
        this.tm = tm;
        this.selecao = selecao;
        this.crossover = crossover;
        this.reinsercao = reinsercao;
        if(reinsercao == Reinsercao.R2_ELITISMO){
            taxaCrossover = .8;
        } else {
            taxaCrossover = 0.6;
        }
    }

    public double taxaCrossover() {
        return taxaCrossover;
    }

    public double taxaMutacao() {
        return tm.valor;
    }

    public int indice() {
        return ((tm.ordinal() * 2 + selecao.ordinal()) * 2 + crossover.ordinal()) * 2
                + reinsercao.ordinal();
    }

    public String codigo() {
        return tm.name()
                + "-" + selecao.name().substring(0, 2)
                + "-" + crossover.name().substring(0, 2)
                + "-" + reinsercao.name().substring(0, 2);
    }
}
