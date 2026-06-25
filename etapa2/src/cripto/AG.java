package cripto;

import cripto.operadores.Crossover;
import cripto.operadores.Mutacao;
import cripto.operadores.Reinsercao;
import cripto.operadores.Selecao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Algoritmo Genetico parametrizado por uma {@link Config} (parametros por instancia). */
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
        List<Individuo> pop = new ArrayList<>(cfg.pop);
        for (int i = 0; i < cfg.pop; i++) {
            Individuo ind = Individuo.aleatorio(rnd);
            ind.avaliar(problema);
            pop.add(ind);
        }

        Individuo melhor = melhorDe(pop);
        if (melhor.fitness == 0) {
            return new Resultado(true, 0, 0, melhor.genes.clone());
        }

        for (int g = 1; g <= cfg.geracoes; g++) {
            List<Individuo> filhos = new ArrayList<>(cfg.pop);
            while (filhos.size() < cfg.pop) {
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
                    Mutacao.mutar(f1, cfg, rnd);
                }
                if (rnd.nextDouble() < cfg.taxaMutacao()) {
                    Mutacao.mutar(f2, cfg, rnd);
                }

                f1.avaliar(problema);
                filhos.add(f1);
                if (filhos.size() < cfg.pop) {
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

        return new Resultado(false, cfg.geracoes, melhor.fitness, melhor.genes.clone());
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
