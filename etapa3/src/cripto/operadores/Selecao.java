package cripto.operadores;

import cripto.Config;
import cripto.Individuo;

import java.util.List;
import java.util.Random;


public class Selecao {

    public static Individuo selecionar(List<Individuo> pop, Config cfg) {
        Random rnd = new Random();
        switch (cfg.selecao) {
            case S1_TORNEIO: return torneio(pop, cfg, rnd);
            case S2_ROLETA:  return roleta(pop, rnd);
            default: throw new IllegalStateException("Selecao desconhecida");
        }
    }

    public static Individuo torneio(List<Individuo> pop, Config cfg, Random rnd) {
        Individuo melhor = pop.get(rnd.nextInt(pop.size()));
        for (int k = 1; k < cfg.tour; k++) {
            Individuo concorrente = pop.get(rnd.nextInt(pop.size()));
            if (concorrente.fitness < melhor.fitness) {
                melhor = concorrente;
            }
        }
        return melhor;
    }

    public static Individuo roleta(List<Individuo> pop, Random rnd) {
        double total = 0.0;

        for (Individuo ind : pop) {
            total += 1.0 / (1.0 + ind.fitness);
        }

        double alvo = rnd.nextDouble() * total;
        double acumulado = 0.0;
        for (Individuo ind : pop) {
            acumulado += 1.0 / (1.0 + ind.fitness);
            if (acumulado >= alvo) {
                return ind;
            }
        }
        return pop.getLast();
    }
}
