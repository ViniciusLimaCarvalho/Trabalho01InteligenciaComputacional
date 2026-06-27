package cripto.operadores;

import cripto.Individuo;

import java.util.Random;

/** Mutacao por troca (swap) de 2 posicoes dentre as 10 do vetor. Preserva a permutacao. */
public class Mutacao {

    /** Swap: troca 2 posicoes distintas. */
    public static void mutacaoPermutacao(Individuo ind, Random rnd) {
        int i = rnd.nextInt(10);
        int j = rnd.nextInt(10);
        while (j == i) {
            j = rnd.nextInt(10);
        }
        int t = ind.genes[i];
        ind.genes[i] = ind.genes[j];
        ind.genes[j] = t;
    }
}
