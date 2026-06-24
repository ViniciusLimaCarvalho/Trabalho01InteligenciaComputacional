package cripto.operadores;

import cripto.Config;
import cripto.Individuo;

import java.util.List;
import java.util.Random;

/** Operadores de selecao de pais. */
public class Selecao {

    public static Individuo selecionar(List<Individuo> pop, Config cfg, Random rnd) {
        switch (cfg.selecao) {
            case S1_TORNEIO: return torneio(pop, rnd);
            case S2_ROLETA:  return roleta(pop, rnd);
            default: throw new IllegalStateException("Selecao desconhecida");
        }
    }

    /** S1: torneio com tour=3, vencedor e o de menor fitness. */
    public static Individuo torneio(List<Individuo> pop, Random rnd) {
        Individuo melhor = pop.get(rnd.nextInt(pop.size()));
        for (int k = 1; k < Config.TOUR; k++) {
            Individuo concorrente = pop.get(rnd.nextInt(pop.size()));
            if (concorrente.fitness < melhor.fitness) {
                melhor = concorrente;
            }
        }
        return melhor;
    }

    /**
     * S2: roleta. Como o problema e de MINIMIZACAO, a aptidao precisa ser invertida:
     * usamos o peso 1/(1+fitness), de modo que individuos com menor erro (melhores)
     * recebam maior probabilidade de selecao.
     */
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
        return pop.get(pop.size() - 1); // fallback por erro de ponto flutuante
    }
}
