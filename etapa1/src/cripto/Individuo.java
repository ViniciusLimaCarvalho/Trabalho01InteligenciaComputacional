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

    /** (Re)avalia o individuo no problema, atualizando o fitness em cache. */
    public void avaliar(Problema problema) {
        this.fitness = problema.fitness(genes);
    }

    /** Cria um individuo aleatorio: permutacao de 0..9 sem repeticao (Fisher-Yates). */
    public static Individuo aleatorio(Random rnd) {
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

    public Individuo copia() {
        Individuo c = new Individuo(genes.clone());
        c.fitness = this.fitness;
        return c;
    }

    @Override
    public int compareTo(Individuo o) {
        return Long.compare(this.fitness, o.fitness); // menor fitness = melhor
    }
}
