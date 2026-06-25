package cripto;

/**
 * Parametros de uma variacao do AG na 2a etapa.
 *
 * Diferente da etapa 1 (onde populacao, geracoes, torneio e elitismo eram constantes
 * estaticas), aqui todos os parametros sao campos de INSTANCIA. Isso permite que cada
 * variacao da etapa 2 ajuste livremente qualquer parametro. Use o {@link Builder}
 * (via {@link #baseline()}) para construir variacoes sobrescrevendo apenas o que muda.
 *
 * A etapa 2 parte da melhor configuracao da etapa 1 (TM2-S1-C2-R1) e explora o eixo
 * de MUTACAO CRIATIVA: novos operadores de mutacao (alem do swap) e diferentes taxas.
 */
public class Config {

    public enum Selecao { S1_TORNEIO, S2_ROLETA }

    public enum Crossover { C1_CX, C2_PMX }

    public enum Reinsercao { R1_ORDENADA, R2_ELITISMO }

    /** Operadores de mutacao. SWAP e o da etapa 1; os demais sao as variacoes criativas. */
    public enum TipoMutacao {
        SWAP,        // troca 2 posicoes (baseline da etapa 1)
        INVERSAO,    // inverte um segmento [a,b]
        SCRAMBLE,    // embaralha um segmento [a,b]
        MULTI_SWAP,  // aplica 2 trocas independentes
        INSERCAO,    // remove um gene e o reinsere em outra posicao (deslocamento)
        HIBRIDA      // a cada mutacao, escolhe aleatoriamente entre swap e inversao
    }

    public final String nome;
    public final int indice;

    public final int pop;
    public final int geracoes;
    public final int tour;
    public final double elitismo;

    public final Selecao selecao;
    public final Crossover crossover;
    public final Reinsercao reinsercao;

    public final TipoMutacao tipoMutacao;
    public final double taxaMutacao;
    public final double taxaCrossover;

    private Config(Builder b) {
        this.nome = b.nome;
        this.indice = b.indice;
        this.pop = b.pop;
        this.geracoes = b.geracoes;
        this.tour = b.tour;
        this.elitismo = b.elitismo;
        this.selecao = b.selecao;
        this.crossover = b.crossover;
        this.reinsercao = b.reinsercao;
        this.tipoMutacao = b.tipoMutacao;
        this.taxaMutacao = b.taxaMutacao;
        this.taxaCrossover = b.taxaCrossover;
    }

    public double taxaCrossover() { return taxaCrossover; }
    public double taxaMutacao()   { return taxaMutacao; }

    /**
     * Builder pre-carregado com a melhor configuracao da etapa 1 (TM2-S1-C2-R1):
     * pop=100, geracoes=50, torneio (tour=3), PMX, reinsercao ordenada,
     * mutacao swap a 20%, crossover 60%. Cada variacao da etapa 2 sobrescreve so o que muda.
     */
    public static Builder baseline() {
        return new Builder()
                .pop(100)
                .geracoes(50)
                .tour(3)
                .elitismo(0.20)
                .selecao(Selecao.S1_TORNEIO)
                .crossover(Crossover.C2_PMX)
                .reinsercao(Reinsercao.R1_ORDENADA)
                .tipoMutacao(TipoMutacao.SWAP)
                .taxaMutacao(0.20)
                .taxaCrossover(0.60);
    }

    public static class Builder {
        private String nome = "";
        private int indice = 0;
        private int pop;
        private int geracoes;
        private int tour;
        private double elitismo;
        private Selecao selecao;
        private Crossover crossover;
        private Reinsercao reinsercao;
        private TipoMutacao tipoMutacao;
        private double taxaMutacao;
        private double taxaCrossover;

        public Builder nome(String v)            { this.nome = v; return this; }
        public Builder indice(int v)             { this.indice = v; return this; }
        public Builder pop(int v)                { this.pop = v; return this; }
        public Builder geracoes(int v)           { this.geracoes = v; return this; }
        public Builder tour(int v)               { this.tour = v; return this; }
        public Builder elitismo(double v)        { this.elitismo = v; return this; }
        public Builder selecao(Selecao v)        { this.selecao = v; return this; }
        public Builder crossover(Crossover v)    { this.crossover = v; return this; }
        public Builder reinsercao(Reinsercao v)  { this.reinsercao = v; return this; }
        public Builder tipoMutacao(TipoMutacao v){ this.tipoMutacao = v; return this; }
        public Builder taxaMutacao(double v)     { this.taxaMutacao = v; return this; }
        public Builder taxaCrossover(double v)   { this.taxaCrossover = v; return this; }

        public Config build() { return new Config(this); }
    }
}
