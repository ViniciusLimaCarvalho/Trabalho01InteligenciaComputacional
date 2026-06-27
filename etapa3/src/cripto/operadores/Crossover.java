package cripto.operadores;

import cripto.Config;
import cripto.Individuo;

import java.util.*;

public class Crossover {

    public static Individuo[] cruzar(Individuo p1, Individuo p2, Config cfg) {
        switch (cfg.crossover) {
            case C1_CX:  return cx(p1.genes, p2.genes);
            case C2_PMX: return pmx(p1.genes, p2.genes);
            default: throw new IllegalStateException("Crossover não configurado");
        }
    }

    public static Individuo[] cx(int[] p1, int[] p2) {
        Random rnd = new Random();
        int n = p1.length;
        int[] f1 = new int[n];
        int[] f2 = new int[n];

        int[] posEmP2 = new int[n];
        for (int i = 0; i < n; i++) {
            posEmP2[p2[i]] = i;
        }

        boolean[] noCiclo = new boolean[n];

        int posInicial = rnd.nextInt(n);
        int posAtual = posInicial;

        do {
            noCiclo[posAtual] = true;
            int elementoP1 = p1[posAtual];
            posAtual = posEmP2[elementoP1];
        } while (posAtual != posInicial);

        for (int i = 0; i < n; i++) {
            if (noCiclo[i]) {
                f1[i] = p1[i];
                f2[i] = p2[i];
            } else {
                f1[i] = p2[i];
                f2[i] = p1[i];
            }
        }

        return new Individuo[]{ new Individuo(f1), new Individuo(f2) };
    }


    public static Individuo[] pmx(int[] p1, int[] p2) {
        Random rnd = new Random();
        int n = p1.length;

        int a = rnd.nextInt(n);
        int b = rnd.nextInt(n);
        if (a > b) {
            int t = a; a = b; b = t;
        }

        int[] f1 = new int[n];
        int[] f2 = new int[n];

        Arrays.fill(f1, -1);
        Arrays.fill(f2, -1);

        for (int i = a; i <= b; i++) {
            f1[i] = p2[i];
            f2[i] = p1[i];
        }

        for (int i = 0; i < n; i++) {
            if (i >= a && i <= b) continue;

            int val1 = p1[i];
            while (contem(f1, a, b, val1)) {
                int posNoFilho = encontrarPosicao(f1, a, b, val1);
                val1 = p1[posNoFilho];
            }
            f1[i] = val1;

            int val2 = p2[i];
            while (contem(f2, a, b, val2)) {
                int posNoFilho = encontrarPosicao(f2, a, b, val2);
                val2 = p2[posNoFilho];
            }
            f2[i] = val2;
        }

        return new Individuo[]{ new Individuo(f1), new Individuo(f2) };
    }

    private static boolean contem(int[] arr, int start, int end, int val) {
        for (int i = start; i <= end; i++) {
            if (arr[i] == val) return true;
        }
        return false;
    }

    private static int encontrarPosicao(int[] arr, int start, int end, int val) {
        for (int i = start; i <= end; i++) {
            if (arr[i] == val) return i;
        }
        return -1;
    }
}
