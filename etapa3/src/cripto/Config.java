package cripto;


public class Config {

    public enum Selecao { S1_TORNEIO, S2_ROLETA }

    public enum Crossover { C1_CX, C2_PMX }

    public enum Reinsercao { R1_ORDENADA, R2_ELITISMO }

    public enum Fitness {GLOBAL, POSICIONAL}


    public final int pop;
    public final int geracoes;
    public final int tour;
    public final double elitismo;

    public final Selecao selecao;
    public final Crossover crossover;
    public final Reinsercao reinsercao;

    public final double taxaMutacao;
    public final double taxaCrossover;

    public final boolean penalidade;
    public final Fitness fitness;
    public final String nome;

    public Config(int pop, int geracoes, int tour, double elitismo, Selecao selecao, Crossover crossover,
                  Reinsercao reinsercao, double taxaMutacao, double taxaCrossover, boolean penalidade, Fitness fitness,
                  String nome) {
        this.pop = pop;
        this.geracoes = geracoes;
        this.tour = tour;
        this.elitismo = elitismo;
        this.selecao = selecao;
        this.crossover = crossover;
        this.reinsercao = reinsercao;
        this.taxaMutacao = taxaMutacao;
        this.taxaCrossover = taxaCrossover;
        this.penalidade = penalidade;
        this.fitness = fitness;
        this.nome = nome;
    }

    public Config(int pop, int geracoes, int tour, double elitismo, Selecao selecao, Crossover crossover,
                  Reinsercao reinsercao, double taxaMutacao, double taxaCrossover, boolean penalidade, Fitness fitness) {
        this.pop = pop;
        this.geracoes = geracoes;
        this.tour = tour;
        this.elitismo = elitismo;
        this.selecao = selecao;
        this.crossover = crossover;
        this.reinsercao = reinsercao;
        this.taxaMutacao = taxaMutacao;
        this.taxaCrossover = taxaCrossover;
        this.penalidade = penalidade;
        this.fitness = fitness;
        nome = "";
    }


    public Config(Config cfg) {
        this.pop = cfg.pop;
        this.geracoes = cfg.geracoes;
        this.tour = cfg.tour;
        this.elitismo = cfg.elitismo;
        this.selecao = cfg.selecao;
        this.crossover = cfg.crossover;
        this.reinsercao = cfg.reinsercao;
        this.taxaMutacao = cfg.taxaMutacao;
        this.taxaCrossover = cfg.taxaCrossover;
        this.penalidade = cfg.penalidade;
        this.fitness = cfg.fitness;
        this.nome = cfg.nome;
    }

    @Override
    public String toString() {
        return this.nome;
    }

    public double taxaCrossover() { return taxaCrossover; }

    public double taxaMutacao()   { return taxaMutacao; }
}
