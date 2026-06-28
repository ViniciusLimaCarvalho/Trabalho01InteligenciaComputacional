package cripto;

/**
 * Parametros de uma variacao do AG na 2a etapa.
 *
 * Diferente da etapa 1 (onde populacao, geracoes, torneio e elitismo eram constantes
 * estaticas), aqui todos os parametros sao campos de INSTANCIA. Isso permite que cada
 * variacao da etapa 2 ajuste livremente qualquer parametro. Use o {@link Builder}
 * (via {@link #baseline()}) para construir variacoes sobrescrevendo apenas o que muda.
 *
 * A etapa 2 parte da melhor configuracao da etapa 1 (TM2-S1-C2-R1) e explora os
 * EIXOS NUMERICOS: tamanho de populacao, numero de geracoes, taxa de mutacao,
 * taxa de crossover e tamanho do torneio. A mutacao continua sendo o swap da etapa 1.
 */
public class Config {

    public enum Selecao { S1_TORNEIO, S2_ROLETA }

    public enum Crossover { C1_CX, C2_PMX }

    public enum Reinsercao { R1_ORDENADA, R2_ELITISMO }

    public final int indice;

    public final int pop;
    public final int geracoes;
    public final int tour;
    public final double elitismo;

    public final Selecao selecao;
    public final Crossover crossover;
    public final Reinsercao reinsercao;

    public final double taxaMutacao;
    public final double taxaCrossover;

    public Config(int indice, int pop, int geracoes, int tour, double elitismo, Selecao selecao, Crossover crossover,
                  Reinsercao reinsercao, double taxaMutacao, double taxaCrossover) {
        this.indice = indice;
        this.pop = pop;
        this.geracoes = geracoes;
        this.tour = tour;
        this.elitismo = elitismo;
        this.selecao = selecao;
        this.crossover = crossover;
        this.reinsercao = reinsercao;
        this.taxaMutacao = taxaMutacao;
        this.taxaCrossover = taxaCrossover;
    }


    public Config(Config cfg) {
        this.indice = cfg.indice;
        this.pop = cfg.pop;
        this.geracoes = cfg.geracoes;
        this.tour = cfg.tour;
        this.elitismo = cfg.elitismo;
        this.selecao = cfg.selecao;
        this.crossover = cfg.crossover;
        this.reinsercao = cfg.reinsercao;
        this.taxaMutacao = cfg.taxaMutacao;
        this.taxaCrossover = cfg.taxaCrossover;
    }



    public double taxaCrossover() { return taxaCrossover; }

    public double taxaMutacao()   { return taxaMutacao; }

    /**
     * Que porra é essa
     */
//    public static Builder baseline() {
//        return new Builder()
//                .pop(100)
//                .geracoes(50)
//                .tour(3)
//                .elitismo(0.2)
//                .selecao(Selecao.S1_TORNEIO)
//                .crossover(Crossover.C2_PMX)
//                .reinsercao(Reinsercao.R1_ORDENADA)
//                .taxaMutacao(0.20)
//                .taxaCrossover(0.60);
//    }

//    public static class Builder {
//        private int indice = 0;
//        private int pop;
//        private int geracoes;
//        private int tour;
//        private double elitismo;
//        private Selecao selecao;
//        private Crossover crossover;
//        private Reinsercao reinsercao;
//        private double taxaMutacao;
//        private double taxaCrossover;
//
//        public Builder indice(int v)             { this.indice = v; return this; }
//        public Builder pop(int v)                { this.pop = v; return this; }
//        public Builder geracoes(int v)           { this.geracoes = v; return this; }
//        public Builder tour(int v)               { this.tour = v; return this; }
//        public Builder elitismo(double v)        { this.elitismo = v; return this; }
//        public Builder selecao(Selecao v)        { this.selecao = v; return this; }
//        public Builder crossover(Crossover v)    { this.crossover = v; return this; }
//        public Builder reinsercao(Reinsercao v)  { this.reinsercao = v; return this; }
//        public Builder taxaMutacao(double v)     { this.taxaMutacao = v; return this; }
//        public Builder taxaCrossover(double v)   { this.taxaCrossover = v; return this; }
//
//        public Config build() { return new Config(, this, , , , , , , , , ); }
//    }
}
