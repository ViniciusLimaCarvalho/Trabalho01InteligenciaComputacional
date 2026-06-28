package cripto;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Experimento3 {

    static final int EXECUCOES = 1000; // 1000;
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


    // Primeiro, testar a função de fitness anterior (erro global)
    // Ai so dps testar a nova função de fitness (erro posicional)

    private static Problema problemaDe(int p, Config cfg) {
        return new Problema(PROBLEMAS[p][0], PROBLEMAS[p][1], PROBLEMAS[p][2], cfg.fitness, cfg.penalidade);
    }

    private static final Config BASEGLOBAL = new Config(200, 50, 3, 0.2,
            Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
            Config.Reinsercao.R2_ELITISMO, 0.2, 0.6, false, Config.Fitness.GLOBAL,
            "Melhor 2 Global");

    private static final Config BASEPOSICIONAL = new Config(200, 50, 3, 0.2,
            Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
            Config.Reinsercao.R2_ELITISMO, 0.2, 0.6, false, Config.Fitness.POSICIONAL,
            "Melhor 2 se fosse posicional");

    public static void main(String[] args) throws IOException {


        // RETORNAR
//        Problema warm = problemaDe(0, BASEGLOBAL);
//        for (int i = 0; i < 200; i++) {
//            AG.executar(warm, BASEGLOBAL);
//        }

        if(true == false){
            System.out.println("\nque.\n");
        }

        System.out.println("Base: melhor config da etapa 2 (elitismo + swap 100% + cx 30%)");

        System.out.println("Testando com fitness global");
        TestarVars(construirVariacoesGlobal());

        System.out.println("Testando com fitness posicional");
        TestarVars(construirVariacoesPosicionais());

        System.out.println("Testando com fitness posicional e penalidade");
        TestarVars(construirVariacoesPenalidade());
    }

    public static void TestarVars(List<Config> variacoes) throws IOException {

//        System.out.println("ETAPA 3 - Generalizacao em 5 problemas  (" + EXECUCOES + " execucoes cada)");
        System.out.println(LINHA);
        System.out.printf("%-26s %6s %6s %6s %6s %7s | %8s %9s %6s%n",
                "Variacao", "SEND", "EAT", "CROSS", "COCA", "DONALD",
                "MEDIA%", "tempo_ms", "teto");
        System.out.println(LINHA);
        List<String[]> csv = new ArrayList<>();
        csv.add(new String[]{"variacao", "fitness", "zero_esquerda", "problema", "letras",
                "convergencia_pct", "tempo_medio_ms"});

        double cap = -1;
        Config melhor = null;
        double melhorConvMedia = -1;
        double melhorTempoMedio = Double.MAX_VALUE;

        for (Config cfg : variacoes) {
            double somaConv = 0, somaTempo = 0;
            double[] convPorProblema = new double[PROBLEMAS.length];
            for (int p = 0; p < PROBLEMAS.length; p++) {
                Problema problema = problemaDe(p, cfg);
                Medida m = medir(problema, cfg);
                convPorProblema[p] = m.convPct;
                somaConv += m.convPct;
                somaTempo += m.tempoMs;
                csv.add(new String[]{
                        cfg.toString(), cfg.fitness.name(), String.valueOf(cfg.penalidade),
                        PROBLEMAS[p][2], String.valueOf(problema.numLetras()),
                        String.format(Locale.US, "%.2f", m.convPct),
                        String.format(Locale.US, "%.4f", m.tempoMs)
                });
            }
            double convMedia = somaConv / PROBLEMAS.length;
            double tempoMedio = somaTempo / PROBLEMAS.length;
            String tetoStr;
            if (cfg == variacoes.get(0)) {
                cap = 1.5 * tempoMedio;
                tetoStr = "ref";
            } else {
                boolean dentro = tempoMedio <= cap;
                tetoStr = dentro ? "sim" : "NAO";
                if (dentro && (convMedia > melhorConvMedia
                        || (convMedia == melhorConvMedia && tempoMedio < melhorTempoMedio))) {
                    melhor = cfg;
                    melhorConvMedia = convMedia;
                    melhorTempoMedio = tempoMedio;
                }
            }

            System.out.printf("%-26s %6.1f %6.1f %6.1f %6.1f %7.1f | %8.1f %9.4f %6s%n",
                    cfg, convPorProblema[0], convPorProblema[1], convPorProblema[2],
                    convPorProblema[3], convPorProblema[4], convMedia, tempoMedio, tetoStr);
        }

        System.out.println(LINHA);



        //        double convBase = mediaConv(variacoes.get(0));
//        System.out.printf("Baseline (config etapa 2) generaliza com convergencia media de %.1f%%%n",
//                convBase);
//        if (melhor != null) {
//            System.out.printf("Melhor variacao (dentro do teto): %s  ->  %.1f%% media (%+.1f p.p.)%n",
//                    melhor, melhorConvMedia, melhorConvMedia - convBase);
//        }
//        gravarCsv(csv);



//        demonstrarSolucoes(variacoes.get(0));
    }

    /** Uma variacao = configuracao do AG + modo de fitness + restricao de zero a esquerda. */
//    private static class Variacao {
//        final String nome;
//        final Config cfg;
//        final Problema.Fitness fitness;
//        final boolean zeroEsq;
//        Variacao(String nome, Config cfg, Problema.Fitness fitness, boolean zeroEsq) {
//            this.nome = nome;
//            this.cfg = cfg;
//            this.fitness = fitness;
//            this.zeroEsq = zeroEsq;
//        }
//    }


    private static List<Config> construirVariacoesGlobal() {
        List<Config> v = new ArrayList<>();
        v.add(BASEGLOBAL);

        v.add(new Config(200, 50, 2, 0.1,
                Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.9, 0.6,
                false, Config.Fitness.GLOBAL,  "-elitismo,tour; +pop")); //

        v.add(new Config(100, 50, 2, 0.1,
                Config.Selecao.S2_ROLETA, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.9, 0.6,
                false, Config.Fitness.GLOBAL, "Usar roleta")); //

        v.add(new Config(150, 50, 2, 0.1,
                Config.Selecao.S1_TORNEIO, Config.Crossover.C1_CX,
                Config.Reinsercao.R2_ELITISMO, 0.9, 0.6,
                false, Config.Fitness.GLOBAL, "C1,+pop,-gens")); //?

        v.add(new Config(200, 50, 2, 0.1,
                Config.Selecao.S1_TORNEIO, Config.Crossover.C1_CX,
                Config.Reinsercao.R1_ORDENADA, 0.7, 0.6,
                false, Config.Fitness.GLOBAL, "C1,R1,+pop")); //

        v.add(new Config(150, 100, 2, 0.1,
                Config.Selecao.S1_TORNEIO, Config.Crossover.C1_CX,
                Config.Reinsercao.R1_ORDENADA, 1, 0.6,
                false, Config.Fitness.GLOBAL, "C1,R1,+pop,+mut")); //?

        v.add(new Config(200, 50, 2, 0.1,
                Config.Selecao.S1_TORNEIO, Config.Crossover.C1_CX,
                Config.Reinsercao.R1_ORDENADA, 1, 0.9,
                false, Config.Fitness.GLOBAL, "C1,R1,+pop,+mut,+cross")); //

        v.add(new Config(150, 50, 2, 0.1,
                Config.Selecao.S1_TORNEIO, Config.Crossover.C1_CX,
                Config.Reinsercao.R1_ORDENADA, 1, 1,
                false, Config.Fitness.GLOBAL, "C1,R1,+pop,mut=1,cross=1"));
        return v;
    }

    // a ideia aqui eh literalmente testar as mesmas coisa, mas com funcao de fitness posicional
    private static List<Config> construirVariacoesPosicionais() {
        List<Config> v = new ArrayList<Config>();
        v.add(BASEPOSICIONAL);

        v.add(new Config(200, 50, 2, 0.1,
                Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.9, 0.6,
                false, Config.Fitness.POSICIONAL,  "-elitismo,tour; +pop")); //

        v.add(new Config(100, 50, 2, 0.1,
                Config.Selecao.S2_ROLETA, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.9, 0.6,
                false, Config.Fitness.POSICIONAL, "Usar roleta")); //

        v.add(new Config(150, 50, 2, 0.1,
                Config.Selecao.S1_TORNEIO, Config.Crossover.C1_CX,
                Config.Reinsercao.R2_ELITISMO, 0.9, 0.6,
                false, Config.Fitness.POSICIONAL, "C1,+pop,-gens")); //?

        v.add(new Config(200, 50, 2, 0.1,
                Config.Selecao.S1_TORNEIO, Config.Crossover.C1_CX,
                Config.Reinsercao.R1_ORDENADA, 0.7, 0.6,
                false, Config.Fitness.POSICIONAL, "C1,R1,+pop")); //

        v.add(new Config(150, 100, 2, 0.1,
                Config.Selecao.S1_TORNEIO, Config.Crossover.C1_CX,
                Config.Reinsercao.R1_ORDENADA, 1, 0.6,
                false, Config.Fitness.POSICIONAL, "C1,R1,+pop,+mut")); //?

        v.add(new Config(200, 50, 2, 0.1,
                Config.Selecao.S1_TORNEIO, Config.Crossover.C1_CX,
                Config.Reinsercao.R1_ORDENADA, 1, 0.9,
                false, Config.Fitness.POSICIONAL, "C1,R1,+pop,+mut,+cross")); //

        v.add(new Config(150, 50, 2, 0.1,
                Config.Selecao.S1_TORNEIO, Config.Crossover.C1_CX,
                Config.Reinsercao.R1_ORDENADA, 1, 1,
                false, Config.Fitness.POSICIONAL, "C1,R1,+pop,mut=1,cross=1"));
        return v;
    }

    private static List<Config> construirVariacoesPenalidade() {
        List<Config> v = new ArrayList<Config>();
        v.add(BASEPOSICIONAL);

        v.add(new Config(200, 50, 2, 0.1,
                Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.9, 0.6,
                true, Config.Fitness.POSICIONAL,  "-elitismo,tour; +pop")); //

        v.add(new Config(100, 50, 2, 0.1,
                Config.Selecao.S2_ROLETA, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.9, 0.6,
                true, Config.Fitness.POSICIONAL, "Usar roleta")); //

        v.add(new Config(150, 50, 2, 0.1,
                Config.Selecao.S1_TORNEIO, Config.Crossover.C1_CX,
                Config.Reinsercao.R2_ELITISMO, 0.9, 0.6,
                true, Config.Fitness.POSICIONAL, "C1,+pop,-gens")); //?

        v.add(new Config(200, 50, 2, 0.1,
                Config.Selecao.S1_TORNEIO, Config.Crossover.C1_CX,
                Config.Reinsercao.R1_ORDENADA, 0.7, 0.6,
                true, Config.Fitness.POSICIONAL, "C1,R1,+pop")); //

        v.add(new Config(150, 100, 2, 0.1,
                Config.Selecao.S1_TORNEIO, Config.Crossover.C1_CX,
                Config.Reinsercao.R1_ORDENADA, 1, 0.6,
                true, Config.Fitness.POSICIONAL, "C1,R1,+pop,+mut")); //?

        v.add(new Config(200, 50, 2, 0.1,
                Config.Selecao.S1_TORNEIO, Config.Crossover.C1_CX,
                Config.Reinsercao.R1_ORDENADA, 1, 0.9,
                true, Config.Fitness.POSICIONAL, "C1,R1,+pop,+mut,+cross")); //

        v.add(new Config(150, 50, 2, 0.1,
                Config.Selecao.S1_TORNEIO, Config.Crossover.C1_CX,
                Config.Reinsercao.R1_ORDENADA, 1, 1,
                true, Config.Fitness.POSICIONAL, "C1,R1,+pop,mut=1,cross=1"));
        return v;
    }

    private static class Medida {
        final double convPct;
        final double tempoMs;
        Medida(double convPct, double tempoMs) {
            this.convPct = convPct;
            this.tempoMs = tempoMs;
        }
    }

    private static Medida medir(Problema problema, Config cfg) {
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

    private static double mediaConv(Config cfg) {
        double soma = 0;
        for (int p = 0; p < PROBLEMAS.length; p++) {
            Problema prob = problemaDe(p, cfg);
            soma += medir(prob, cfg).convPct;
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
    private static void demonstrarSolucoes(Config cfg) {
        System.out.println(LINHA);
        System.out.println("Solucoes encontradas (config base):");
        for (int p = 0; p < PROBLEMAS.length; p++) {
            Problema problema = problemaDe(p, cfg);
            AG.Resultado sol = null;
            for (int i = 0; i < 5000 && sol == null; i++) {
                AG.Resultado res = AG.executar(problema, cfg);
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
