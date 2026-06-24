package cripto;

import cripto.operadores.Crossover;
import cripto.operadores.Mutacao;
import cripto.operadores.Reinsercao;
import cripto.operadores.Selecao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Algoritmo Genetico parametrizado por uma {@link Config}. */
public class AG {

    /** Resultado de uma execucao do AG. */
    public static class Resultado {
        public final boolean convergiu;
        public final int geracoes;
        public final long melhorFitness;
        public final int[] melhorGenes;

        public Resultado(boolean convergiu, int geracoes, long melhorFitness, int[] melhorGenes) {
            this.convergiu = convergiu;
            this.geracoes = geracoes;
            this.melhorFitness = melhorFitness;
            this.melhorGenes = melhorGenes;
        }
    }

    public static Resultado executar(Problema problema, Config cfg, Random rnd) {
        // populacao inicial aleatoria
        List<Individuo> pop = new ArrayList<>(Config.POP);
        for (int i = 0; i < Config.POP; i++) {
            Individuo ind = Individuo.aleatorio(rnd);
            ind.avaliar(problema);
            pop.add(ind);
        }

        Individuo melhor = melhorDe(pop);
        if (melhor.fitness == 0) {
            return new Resultado(true, 0, 0, melhor.genes.clone());
        }

        for (int g = 1; g <= Config.GERACOES; g++) {
            List<Individuo> filhos = new ArrayList<>(Config.POP);
            while (filhos.size() < Config.POP) {
                Individuo p1 = Selecao.selecionar(pop, cfg, rnd);
                Individuo p2 = Selecao.selecionar(pop, cfg, rnd);

                Individuo f1;
                Individuo f2;
                if (rnd.nextDouble() < cfg.taxaCrossover()) {
                    Individuo[] f = Crossover.cruzar(p1, p2, cfg, rnd);
                    f1 = f[0];
                    f2 = f[1];
                } else {
                    f1 = p1.copia();
                    f2 = p2.copia();
                }

                if (rnd.nextDouble() < cfg.taxaMutacao()) {
                    Mutacao.swap(f1, rnd);
                }
                if (rnd.nextDouble() < cfg.taxaMutacao()) {
                    Mutacao.swap(f2, rnd);
                }

                f1.avaliar(problema);
                filhos.add(f1);
                if (filhos.size() < Config.POP) {
                    f2.avaliar(problema);
                    filhos.add(f2);
                }
            }

            pop = Reinsercao.reinserir(pop, filhos, cfg);

            Individuo melhorGeracao = melhorDe(pop);
            if (melhorGeracao.fitness < melhor.fitness) {
                melhor = melhorGeracao;
            }
            if (melhor.fitness == 0) {
                return new Resultado(true, g, 0, melhor.genes.clone());
            }
        }

        return new Resultado(false, Config.GERACOES, melhor.fitness, melhor.genes.clone());
    }

    private static Individuo melhorDe(List<Individuo> pop) {
        Individuo m = pop.get(0);
        for (Individuo ind : pop) {
            if (ind.fitness < m.fitness) {
                m = ind;
            }
        }
        return m;
    }
}
