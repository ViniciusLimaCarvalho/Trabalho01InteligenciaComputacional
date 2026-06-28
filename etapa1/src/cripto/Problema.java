package cripto;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Representa um problema de criptoaritmetica da forma parcela1 + parcela2 = resultado.
 *
 * As letras são mapeadas em índices de 0 a n-1 (na ordem quem aparecem seguindo a equação
 * acima). Como cada indivíduo possui um cromossomo com permutação de {0...9} o índice de
 * cada letra é obtido por genes[indiceLetra]. Cada letra possui um digito distinto
 *
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
                    "O problema não pode ser resolvido em uma base decimal por possuir mais de 10 algarismos: " + indiceLetra.size());
        }
    }


    public long valorPalavra(String palavra, int[] genes) {
        long valor = 0;

        for (char c : palavra.toCharArray()) {
            valor = valor * 10 + genes[indiceLetra.get(c)];
        }

        return valor;
    }

    public long fitness(int[] genes) {
        long a = valorPalavra(parcela1, genes);
        long b = valorPalavra(parcela2, genes);
        long r = valorPalavra(resultado, genes);
        return Math.abs((a + b) - r);
    }


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
