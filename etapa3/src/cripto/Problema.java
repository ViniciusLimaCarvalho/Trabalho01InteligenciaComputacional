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
 * Na ETAPA 3 a classe ganha dois eixos configuraveis (alem de ser usada nos 5 problemas):
 *
 *  - {@link Fitness}: GLOBAL (| (p1+p2) - resultado |, da etapa 1/2) ou POSICIONAL
 *    (soma dos erros digito a digito entre (p1+p2) e resultado). A posicional oferece um
 *    gradiente mais suave: acertar cada digito reduz o erro, em vez de depender so da
 *    diferenca absoluta global.
 *
 *  - zeroEsquerda: se {@code true}, penaliza individuos cujo digito da PRIMEIRA letra de
 *    qualquer palavra seja 0 (numeros nao tem zero a esquerda). Sem essa restricao, a
 *    aritmetica pode ser satisfeita por atribuicoes invalidas (ex.: SEND+MORE=MONEY tem
 *    25 atribuicoes que zeram a diferenca, mas so 1 e valida). Ambas as funcoes valem 0
 *    se e somente se a soma bate E (quando exigido) nao ha zero a esquerda.
 */
public class Problema {

    public enum Fitness { GLOBAL, POSICIONAL }

    /** Penalidade somada por cada palavra com zero a esquerda; grande o bastante para
     *  nunca zerar o fitness de uma atribuicao invalida. */
    private static final long PENALIDADE_ZERO_ESQ = 100_000_000L;

    public final String parcela1;
    public final String parcela2;
    public final String resultado;

    public final Fitness tipoFitness;
    public final boolean zeroEsquerda;

    private final Map<Character, Integer> indiceLetra = new LinkedHashMap<>();

    /** Construtor padrao: fitness GLOBAL, sem restricao de zero a esquerda (igual etapa 1/2). */
    public Problema(String parcela1, String parcela2, String resultado) {
        this(parcela1, parcela2, resultado, Fitness.GLOBAL, false);
    }

    public Problema(String parcela1, String parcela2, String resultado,
                    Fitness tipoFitness, boolean zeroEsquerda) {
        this.parcela1 = parcela1.toUpperCase();
        this.parcela2 = parcela2.toUpperCase();
        this.resultado = resultado.toUpperCase();
        this.tipoFitness = tipoFitness;
        this.zeroEsquerda = zeroEsquerda;

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
     * Funcao de avaliacao (minimizacao): 0 indica solucao valida (convergencia).
     * Despacha entre erro GLOBAL e POSICIONAL e aplica (se ativada) a penalidade de
     * zero a esquerda.
     */
    public long fitness(int[] genes) {
        long a = valorPalavra(parcela1, genes);
        long b = valorPalavra(parcela2, genes);
        long r = valorPalavra(resultado, genes);

        long erro = (tipoFitness == Fitness.POSICIONAL)
                ? erroPosicional(a + b, r)
                : Math.abs((a + b) - r);

        if (zeroEsquerda) {
            erro += PENALIDADE_ZERO_ESQ * (long) palavrasComZeroEsquerda(genes);
        }
        return erro;
    }

    /**
     * Erro posicional: soma de |digito_k(soma) - digito_k(resultado)| em cada casa decimal
     * (unidades, dezenas, ...). E 0 se e somente se soma == resultado. Casas faltantes em
     * um dos numeros contam como digito 0, de modo que comprimentos diferentes geram erro.
     */
    static long erroPosicional(long soma, long resultado) {
        long erro = 0;
        long s = soma;
        long r = resultado;
        while (s > 0 || r > 0) {
            erro += Math.abs((s % 10) - (r % 10));
            s /= 10;
            r /= 10;
        }
        return erro;
    }

    /** Conta quantas das 3 palavras teriam zero a esquerda (primeira letra mapeada para 0). */
    private int palavrasComZeroEsquerda(int[] genes) {
        int n = 0;
        for (String palavra : new String[]{parcela1, parcela2, resultado}) {
            if (genes[indiceLetra.get(palavra.charAt(0))] == 0) {
                n++;
            }
        }
        return n;
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
