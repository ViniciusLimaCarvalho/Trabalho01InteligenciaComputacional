package cripto;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 3a Etapa: generalizacao. Avalia a MELHOR configuracao da etapa 2 (reinsercao elitista,
 * mutacao swap 100%, crossover 30%, pop=100, ger=50, torneio tour=3, PMX) nos 5 problemas
 * de criptoaritmetica, 1000 execucoes por problema, e reporta a convergencia e o tempo
 * MEDIOS entre os 5.
 *
 * Em seguida testa 6 variacoes (>=5 exigidas), incluindo OBRIGATORIAMENTE a funcao de
 * fitness por erro POSICIONAL (digito a digito). Tambem testa a restricao de zero a
 * esquerda, que revela a convergencia "real": sem ela, SEND+MORE=MONEY admite 25
 * atribuicoes que zeram a diferenca (so 1 e valida); os outros 4 problemas tem solucao
 * unica de qualquer forma.
 *
 * Teto de tempo: medido no proprio run (cap = 1.5 x tempo medio do baseline).
 */
public class Experimento3 {

    static final int EXECUCOES = 1000;
    static final String LINHA =
            "--------------------------------------------------------------------------------";

    /** Os 5 problemas da etapa 3 (com rotulo curto para a tabela). */
    static final String[][] PROBLEMAS = {
            {"SEND",   "MORE",   "MONEY",  "SEND"},
            {"EAT",    "THAT",   "APPLE",  "EAT"},
            {"CROSS",  "ROADS",  "DANGER", "CROSS"},
            {"COCA",   "COLA",   "OASIS",  "COCA"},
            {"DONALD", "GERALD", "ROBERT", "DONALD"},
    };

    public static void main(String[] args) throws IOException {
        List<Variacao> variacoes = construirVariacoes();
        Variacao base = variacoes.get(0);

        // aquecer JVM
        Problema warm = problemaDe(0, base);
        for (int i = 0; i < 200; i++) {
            AG.executar(warm, base.cfg);
        }

        System.out.println("ETAPA 3 - Generalizacao em 5 problemas  (" + EXECUCOES + " execucoes cada)");
        System.out.println("Base: melhor config da etapa 2 (elitismo + swap 100% + cx 30%)");
        System.out.println(LINHA);
        System.out.printf("%-26s %6s %6s %6s %6s %7s | %8s %9s %6s%n",
                "Variacao", "SEND", "EAT", "CROSS", "COCA", "DONALD",
                "MEDIA%", "tempo_ms", "teto");
        System.out.println(LINHA);

        List<String[]> csv = new ArrayList<>();
        csv.add(new String[]{"variacao", "fitness", "zero_esquerda", "problema", "letras",
                "convergencia_pct", "tempo_medio_ms"});

        double cap = -1;
        Variacao melhor = null;
        double melhorConvMedia = -1;
        double melhorTempoMedio = Double.MAX_VALUE;

        for (Variacao v : variacoes) {
            double somaConv = 0, somaTempo = 0;
            double[] convPorProblema = new double[PROBLEMAS.length];

            for (int p = 0; p < PROBLEMAS.length; p++) {
                Problema problema = problemaDe(p, v);
                Medida m = medir(problema, v.cfg, p);
                convPorProblema[p] = m.convPct;
                somaConv += m.convPct;
                somaTempo += m.tempoMs;
                csv.add(new String[]{
                        v.nome, v.fitness.name(), String.valueOf(v.zeroEsq),
                        PROBLEMAS[p][2], String.valueOf(problema.numLetras()),
                        String.format(Locale.US, "%.2f", m.convPct),
                        String.format(Locale.US, "%.4f", m.tempoMs)
                });
            }

            double convMedia = somaConv / PROBLEMAS.length;
            double tempoMedio = somaTempo / PROBLEMAS.length;

            String tetoStr;
            if (v == base) {
                cap = 1.5 * tempoMedio;
                tetoStr = "ref";
            } else {
                boolean dentro = tempoMedio <= cap;
                tetoStr = dentro ? "sim" : "NAO";
                if (dentro && (convMedia > melhorConvMedia
                        || (convMedia == melhorConvMedia && tempoMedio < melhorTempoMedio))) {
                    melhor = v;
                    melhorConvMedia = convMedia;
                    melhorTempoMedio = tempoMedio;
                }
            }

            System.out.printf("%-26s %6.1f %6.1f %6.1f %6.1f %7.1f | %8.1f %9.4f %6s%n",
                    v.nome, convPorProblema[0], convPorProblema[1], convPorProblema[2],
                    convPorProblema[3], convPorProblema[4], convMedia, tempoMedio, tetoStr);
        }

        System.out.println(LINHA);
        double convBase = mediaConv(base);
        System.out.printf("Baseline (config etapa 2) generaliza com convergencia media de %.1f%%%n",
                convBase);
        if (melhor != null) {
            System.out.printf("Melhor variacao (dentro do teto): %s  ->  %.1f%% media (%+.1f p.p.)%n",
                    melhor.nome, melhorConvMedia, melhorConvMedia - convBase);
        }

        gravarCsv(csv);
        demonstrarSolucoes(base);
    }

    /** Uma variacao = configuracao do AG + modo de fitness + restricao de zero a esquerda. */
    private static class Variacao {
        final String nome;
        final Config cfg;
        final Problema.Fitness fitness;
        final boolean zeroEsq;
        Variacao(String nome, Config cfg, Problema.Fitness fitness, boolean zeroEsq) {
            this.nome = nome;
            this.cfg = cfg;
            this.fitness = fitness;
            this.zeroEsq = zeroEsq;
        }
    }

    /** Builder da melhor config da etapa 2 (V18), para as variacoes derivarem dela. */
    private static Config.Builder baseBuilder() {
        return Config.baseline()
                .reinsercao(Config.Reinsercao.R2_ELITISMO)
                .taxaCrossover(0.30)
                .taxaMutacao(1.0);
    }

    private static List<Variacao> construirVariacoes() {
        List<Variacao> v = new ArrayList<>();
        // V0: baseline = melhor config da etapa 2, fitness global, sem restricao (igual etapa 2)
        v.add(new Variacao("V0 base (global)",
                baseBuilder().indice(0).build(), Problema.Fitness.GLOBAL, false));
        // V1: fitness POSICIONAL (obrigatoria) - gradiente digito a digito
        v.add(new Variacao("V1 posicional",
                baseBuilder().indice(1).build(), Problema.Fitness.POSICIONAL, false));
        // V2: restricao de zero a esquerda (convergencia "real", so solucoes validas)
        v.add(new Variacao("V2 zero-esq",
                baseBuilder().indice(2).build(), Problema.Fitness.GLOBAL, true));
        // V3: posicional + zero a esquerda
        v.add(new Variacao("V3 posicional+zero-esq",
                baseBuilder().indice(3).build(), Problema.Fitness.POSICIONAL, true));
        // V4: populacao maior (ajuda problemas com mais letras: CROSS=9, DONALD=10)
        v.add(new Variacao("V4 pop=200",
                baseBuilder().pop(200).indice(4).build(), Problema.Fitness.GLOBAL, false));
        // V5: mais geracoes
        v.add(new Variacao("V5 ger=100",
                baseBuilder().geracoes(100).indice(5).build(), Problema.Fitness.GLOBAL, false));
        // V6: posicional + populacao 150 (combina gradiente suave com mais diversidade)
        v.add(new Variacao("V6 posicional+pop=150",
                baseBuilder().pop(150).indice(6).build(), Problema.Fitness.POSICIONAL, false));
        return v;
    }

    private static Problema problemaDe(int p, Variacao v) {
        return new Problema(PROBLEMAS[p][0], PROBLEMAS[p][1], PROBLEMAS[p][2],
                v.fitness, v.zeroEsq);
    }

    private static class Medida {
        final double convPct;
        final double tempoMs;
        Medida(double convPct, double tempoMs) {
            this.convPct = convPct;
            this.tempoMs = tempoMs;
        }
    }

    private static Medida medir(Problema problema, Config cfg, int probIdx) {
        int convergencias = 0;
        long tempoTotalNs = 0;
        for (int i = 0; i < EXECUCOES; i++) {
            long t0 = System.nanoTime();
            AG.Resultado res = AG.executar(problema, cfg);
            tempoTotalNs += System.nanoTime() - t0;
            if (res.convergiu) {
                convergencias++;
            }
        }
        double convPct = 100.0 * convergencias / EXECUCOES;
        double tempoMs = (tempoTotalNs / (double) EXECUCOES) / 1_000_000.0;
        return new Medida(convPct, tempoMs);
    }

    private static double mediaConv(Variacao v) {
        double soma = 0;
        for (int p = 0; p < PROBLEMAS.length; p++) {
            soma += medir(problemaDe(p, v), v.cfg, p).convPct;
        }
        return soma / PROBLEMAS.length;
    }

    private static void gravarCsv(List<String[]> linhas) throws IOException {
        Path dir = Paths.get("etapa3", "resultados");
        Files.createDirectories(dir);
        Path arquivo = dir.resolve("etapa3.csv");
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(arquivo))) {
            for (String[] linha : linhas) {
                pw.println(String.join(",", linha));
            }
        }
        System.out.println("CSV salvo em: " + arquivo.toAbsolutePath());
    }

    /** Demonstra uma solucao encontrada para cada um dos 5 problemas com a config base. */
    private static void demonstrarSolucoes(Variacao v) {
        System.out.println(LINHA);
        System.out.println("Solucoes encontradas (config base):");
        for (int p = 0; p < PROBLEMAS.length; p++) {
            Problema problema = problemaDe(p, v);
            AG.Resultado sol = null;
            for (int i = 0; i < 5000 && sol == null; i++) {
                AG.Resultado res = AG.executar(problema, v.cfg);
                if (res.convergiu) {
                    sol = res;
                }
            }
            if (sol == null) {
                System.out.printf("  %-22s : (nao encontrada em 5000 tentativas)%n", problema);
                continue;
            }
            long a = problema.valorPalavra(problema.parcela1, sol.melhorGenes);
            long b = problema.valorPalavra(problema.parcela2, sol.melhorGenes);
            long r = problema.valorPalavra(problema.resultado, sol.melhorGenes);
            System.out.printf("  %-22s : %d + %d = %d%n", problema, a, b, r);
        }
    }
}
