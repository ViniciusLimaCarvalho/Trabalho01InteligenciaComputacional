package cripto;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Representa um problema de criptoaritmetica da forma parcela1 + parcela2 = resultado.
 *
 * As letras distintas sao mapeadas para indices 0..n-1 (na ordem em que aparecem).
 * O cromossomo do individuo e uma permutacao de {0..9}; o digito de cada letra e
 * obtido por genes[indiceDaLetra]. Como o cromossomo e uma permutacao, letras
 * distintas recebem digitos distintos por construcao (restricao do problema).
 *
 * A classe ja e generica para reuso nas etapas 2 e 3 (basta instanciar com outras palavras).
 */
public class Problema {

    public final String parcela1;
    public final String parcela2;
    public final String resultado;

    private final Map<Character, Integer> indiceLetra = new LinkedHashMap<>();

    public Problema(String parcela1, String parcela2, String resultado) {
        this.parcela1 = parcela1.toUpperCase();
        this.parcela2 = parcela2.toUpperCase();
        this.resultado = resultado.toUpperCase();

        for (String palavra : new String[]{this.parcela1, this.parcela2, this.resultado}) {
            for (char c : palavra.toCharArray()) {
                indiceLetra.putIfAbsent(c, indiceLetra.size());
            }
        }
        if (indiceLetra.size() > 10) {
            throw new IllegalArgumentException(
                    "Problema possui mais de 10 letras distintas: " + indiceLetra.size());
        }
    }

    /** Numero de letras distintas do problema. */
    public int numLetras() {
        return indiceLetra.size();
    }

    /** Valor numerico de uma palavra dado o cromossomo (permutacao de 0..9). */
    public long valorPalavra(String palavra, int[] genes) {
        long valor = 0;
        for (char c : palavra.toCharArray()) {
            valor = valor * 10 + genes[indiceLetra.get(c)];
        }
        return valor;
    }

    /**
     * Funcao de avaliacao da 1a etapa: | (parcela1 + parcela2) - resultado |.
     * Menor e melhor; 0 indica solucao valida (convergencia).
     *
     * Obs.: o enunciado define o fitness apenas como a diferenca absoluta, sem
     * penalizar zero a esquerda. A solucao unica de SEND+MORE=MONEY (9567+1085=10652)
     * nao possui zero a esquerda, entao nao restringimos isso aqui.
     */
    public long fitness(int[] genes) {
        long a = valorPalavra(parcela1, genes);
        long b = valorPalavra(parcela2, genes);
        long r = valorPalavra(resultado, genes);
        return Math.abs((a + b) - r);
    }

    /** Mapeamento letra -> digito para apresentacao do resultado. */
    public Map<Character, Integer> mapeamento(int[] genes) {
        Map<Character, Integer> m = new LinkedHashMap<>();
        for (Map.Entry<Character, Integer> e : indiceLetra.entrySet()) {
            m.put(e.getKey(), genes[e.getValue()]);
        }
        return m;
    }

    @Override
    public String toString() {
        return parcela1 + " + " + parcela2 + " = " + resultado;
    }
}
