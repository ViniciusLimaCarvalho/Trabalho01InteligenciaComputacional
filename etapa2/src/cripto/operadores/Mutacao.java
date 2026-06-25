package cripto.operadores;

import cripto.Config;
import cripto.Individuo;

import java.util.Random;

/**
 * Operadores de mutacao. Todos atuam sobre a permutacao de {0..9} e preservam sua
 * validade (nao geram digitos repetidos). Alem do SWAP da etapa 1, a etapa 2 explora
 * operadores criativos: inversao, scramble, multi-swap, insercao e hibrida.
 */
public class Mutacao {

    /** Aplica o operador de mutacao definido em {@code cfg.tipoMutacao}. */
    public static void mutar(Individuo ind, Config cfg, Random rnd) {
        switch (cfg.tipoMutacao) {
            case SWAP:       swap(ind, rnd); break;
            case INVERSAO:   inversao(ind, rnd); break;
            case SCRAMBLE:   scramble(ind, rnd); break;
            case MULTI_SWAP: swap(ind, rnd); swap(ind, rnd); break;
            case INSERCAO:   insercao(ind, rnd); break;
            case HIBRIDA:    if (rnd.nextBoolean()) swap(ind, rnd); else inversao(ind, rnd); break;
            default: throw new IllegalStateException("Mutacao desconhecida");
        }
    }

    /** Swap: troca 2 posicoes distintas. */
    public static void swap(Individuo ind, Random rnd) {
        int i = rnd.nextInt(10);
        int j = rnd.nextInt(10);
        while (j == i) {
            j = rnd.nextInt(10);
        }
        int t = ind.genes[i];
        ind.genes[i] = ind.genes[j];
        ind.genes[j] = t;
    }

    /** Inversao: escolhe um segmento [a,b] e inverte sua ordem. */
    public static void inversao(Individuo ind, Random rnd) {
        int[] g = ind.genes;
        int a = rnd.nextInt(10);
        int b = rnd.nextInt(10);
        if (a > b) { int t = a; a = b; b = t; }
        while (a < b) {
            int t = g[a]; g[a] = g[b]; g[b] = t;
            a++; b--;
        }
    }

    /** Scramble: escolhe um segmento [a,b] e embaralha (Fisher-Yates) seus elementos. */
    public static void scramble(Individuo ind, Random rnd) {
        int[] g = ind.genes;
        int a = rnd.nextInt(10);
        int b = rnd.nextInt(10);
        if (a > b) { int t = a; a = b; b = t; }
        for (int i = b; i > a; i--) {
            int j = a + rnd.nextInt(i - a + 1);
            int t = g[i]; g[i] = g[j]; g[j] = t;
        }
    }

    /** Insercao: remove o gene da posicao i e o reinsere na posicao j, deslocando o restante. */
    public static void insercao(Individuo ind, Random rnd) {
        int[] g = ind.genes;
        int i = rnd.nextInt(10);
        int j = rnd.nextInt(10);
        while (j == i) {
            j = rnd.nextInt(10);
        }
        int v = g[i];
        if (i < j) {
            for (int k = i; k < j; k++) g[k] = g[k + 1];
        } else {
            for (int k = i; k > j; k--) g[k] = g[k - 1];
        }
        g[j] = v;
    }
}
