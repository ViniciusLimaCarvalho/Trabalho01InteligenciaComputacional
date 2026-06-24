package cripto.operadores;

import cripto.Config;
import cripto.Individuo;

import java.util.Arrays;
import java.util.Random;

/**
 * Operadores de crossover que preservam a validade da permutacao (sem digitos repetidos):
 * C1 = Cycle Crossover (CX), C2 = PMX (Partially Mapped Crossover).
 * Cada operador retorna 2 filhos.
 */
public class Crossover {

    public static Individuo[] cruzar(Individuo p1, Individuo p2, Config cfg, Random rnd) {
        switch (cfg.crossover) {
            case C1_CX:  return cx(p1.genes, p2.genes);
            case C2_PMX: return pmx(p1.genes, p2.genes, rnd);
            default: throw new IllegalStateException("Crossover desconhecido");
        }
    }

    /**
     * Cycle Crossover (CX): identifica os ciclos entre os pais e os alterna entre os filhos.
     * Ciclos de indice par vao do pai 1 para o filho 1 (e pai 2 para filho 2); ciclos de
     * indice impar invertem.
     */
    public static Individuo[] cx(int[] p1, int[] p2) {
        int n = p1.length;
        int[] c1 = new int[n];
        int[] c2 = new int[n];
        boolean[] visitado = new boolean[n];

        // posicao de cada valor no pai 1 (para seguir o ciclo)
        int[] posEmP1 = new int[10];
        for (int i = 0; i < n; i++) {
            posEmP1[p1[i]] = i;
        }

        int cicloIdx = 0;
        for (int inicio = 0; inicio < n; inicio++) {
            if (visitado[inicio]) {
                continue;
            }
            boolean usarP1 = (cicloIdx % 2 == 0);
            int j = inicio;
            do {
                visitado[j] = true;
                if (usarP1) {
                    c1[j] = p1[j];
                    c2[j] = p2[j];
                } else {
                    c1[j] = p2[j];
                    c2[j] = p1[j];
                }
                j = posEmP1[p2[j]]; // proxima posicao do ciclo
            } while (j != inicio);
            cicloIdx++;
        }
        return new Individuo[]{ new Individuo(c1), new Individuo(c2) };
    }

    /** PMX: dois cortes aleatorios; gera um filho herdando o segmento de cada pai. */
    public static Individuo[] pmx(int[] p1, int[] p2, Random rnd) {
        int n = p1.length;
        int a = rnd.nextInt(n);
        int b = rnd.nextInt(n);
        if (a > b) {
            int t = a; a = b; b = t;
        }
        return new Individuo[]{
                new Individuo(pmxFilho(p1, p2, a, b)),
                new Individuo(pmxFilho(p2, p1, a, b))
        };
    }

    /**
     * Constroi um filho PMX: copia o segmento [a,b] de pA e resolve os elementos de pB
     * desse segmento via mapeamento; o restante e preenchido por pB.
     */
    private static int[] pmxFilho(int[] pA, int[] pB, int a, int b) {
        int n = pA.length;
        int[] filho = new int[n];
        Arrays.fill(filho, -1);

        boolean[] noFilho = new boolean[10];
        int[] posEmPB = new int[10];
        for (int i = 0; i < n; i++) {
            posEmPB[pB[i]] = i;
        }

        // 1) copia o segmento de pA
        for (int i = a; i <= b; i++) {
            filho[i] = pA[i];
            noFilho[pA[i]] = true;
        }

        // 2) elementos de pB no segmento que ainda nao estao no filho
        for (int i = a; i <= b; i++) {
            int v = pB[i];
            if (noFilho[v]) {
                continue;
            }
            int pos = i;
            // segue o mapeamento ate cair fora do segmento
            while (pos >= a && pos <= b) {
                int valNaPos = pA[pos];
                pos = posEmPB[valNaPos];
            }
            filho[pos] = v;
            noFilho[v] = true;
        }

        // 3) restante vem de pB
        for (int i = 0; i < n; i++) {
            if (filho[i] == -1) {
                filho[i] = pB[i];
            }
        }
        return filho;
    }
}
