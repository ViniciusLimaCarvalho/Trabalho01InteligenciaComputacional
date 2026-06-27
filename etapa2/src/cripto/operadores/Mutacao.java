package cripto.operadores;

import cripto.Individuo;

import java.util.Random;

public class Mutacao {

    public static void mutacaoPermutacao(Individuo ind) {
        Random rnd = new Random();
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
