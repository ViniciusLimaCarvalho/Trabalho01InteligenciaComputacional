package cripto;

import java.util.Random;

/**
 * Individuo do AG. O cromossomo e uma permutacao de {0..9} (vetor de inteiros de tamanho 10),
 * conforme a especificacao fixa da 1a etapa. A permutacao garante que nao ha digitos repetidos.
 */
public class Individuo implements Comparable<Individuo> {

    public final int[] genes;
    public long fitness;

    public Individuo(int[] genes) {
        this.genes = genes;
    }

    public void avaliar(Problema problema) {
        this.fitness = problema.fitness(genes);
    }

    public static Individuo aleatorio(Random rnd) {
        /**
         * Esta função gera um indivíduo aleatório. O algoritmo utilizado
         * é o de Fisher-Yates, cujo qual pode ser encontrado no relatório
         * e nas referências do mesmo no trabalho.
         *
         * Ele consiste em permutar aleatoriamente o vetores de valores.
         */
        int[] g = new int[10];
        for (int i = 0; i < 10; i++) {
            g[i] = i;
        }
        for (int i = 9; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int t = g[i];
            g[i] = g[j];
            g[j] = t;
        }
        return new Individuo(g);
    }

    @Override
    public int compareTo(Individuo o) {
        return Long.compare(this.fitness, o.fitness); // menor fitness = melhor
    }
}
