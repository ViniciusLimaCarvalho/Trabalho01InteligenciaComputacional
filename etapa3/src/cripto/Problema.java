package cripto;

import java.util.LinkedHashMap;
import java.util.Map;


public class Problema {


    /** Penalidade somada por cada palavra com zero a esquerda; grande o bastante para
     *  nunca zerar o fitness de uma atribuicao invalida. */
    private static final long PENALIDADE_ZERO_ESQ = 100_000_0L;

    public final String parcela1;
    public final String parcela2;
    public final String resultado;

    public final Config.Fitness tipoFitness;
    public final boolean zeroEsquerda;

    private final Map<Character, Integer> indiceLetra = new LinkedHashMap<>();

    /** Construtor padrao: fitness GLOBAL, sem restricao de zero a esquerda (igual etapa 1/2). */
    public Problema(String parcela1, String parcela2, String resultado) {
        this(parcela1, parcela2, resultado, Config.Fitness.GLOBAL, false);
    }

    public Problema(String parcela1, String parcela2, String resultado,
                    Config.Fitness tipoFitness, boolean zeroEsquerda) {
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

        long erro = (tipoFitness == Config.Fitness.POSICIONAL)
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
        long index = 1;
        while (s > 0 || r > 0) {
            erro += Math.abs((s % 10) - (r % 10)) * index;
            index *= 10;
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
