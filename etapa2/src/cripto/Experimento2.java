package cripto;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 2a Etapa: refinamento a partir da melhor configuracao da etapa 1 (TM2-S1-C2-R1).
 *
 * Eixo explorado: PARAMETROS NUMERICOS. Mantemos fixos os operadores da melhor config
 * da etapa 1 (torneio, PMX, reinsercao ordenada, mutacao swap) e variamos os valores
 * numericos: tamanho da populacao, numero de geracoes, taxa de mutacao, taxa de
 * crossover e tamanho do torneio. Tambem testamos combinacoes dos ajustes "baratos".
 *
 * O teto de tempo e medido no proprio run: roda-se o baseline (com warmup) e define-se
 * cap = 1.5 x tempoBaseline. Cada variacao e marcada como DENTRO/FORA do cap.
 * Aumentar populacao/geracoes custa tempo e pode estourar o teto; ajustar taxas/torneio
 * e praticamente de graca. Cada configuracao e executada 1000 vezes; grava CSV em
 * etapa2/resultados/etapa2.csv.
 */
public class Experimento2 {

    static final int EXECUCOES = 1000;
    static final String LINHA =
            "----------------------------------------------------------------------";

    public static void main(String[] args) throws IOException {
        Problema problema = new Problema("SEND", "MORE", "MONEY");

        Config baseline = Config.baseline().indice(0).build();

        List<Config> variacoes = construirVariacoes();

        // warmup da JVM para nao penalizar a primeira medicao
        for (int i = 0; i < 200; i++) {
            AG.executar(problema, baseline, new Random(i));
        }

        System.out.println("Problema: " + problema + "   (" + EXECUCOES + " execucoes por configuracao)");
        System.out.println("Eixo de refinamento: PARAMETROS NUMERICOS  (base: TM2-S1-C2-R1 da etapa 1)");
        System.out.println(LINHA);

        // 1) baseline define o teto de tempo (cap = 1.5x)
        Medida medBase = medir(problema, baseline);
        double cap = 1.5 * medBase.tempoMs;
        System.out.printf("Baseline: conv = %.1f%%, tempo = %.4f ms  ->  teto (+50%%) = %.4f ms%n",
                medBase.convPct, medBase.tempoMs, cap);
        System.out.println(LINHA);
        System.out.printf("%-40s %9s %12s %9s%n", "Config", "Conv.(%)", "Tempo(ms)", "<=teto?");
        System.out.println(LINHA);
        System.out.printf("%-40s %9.1f %12.4f %9s%n",
                rotulo(baseline), medBase.convPct, medBase.tempoMs, "ref");

        List<String[]> csv = new ArrayList<>();
        csv.add(new String[]{"variacao", "pop", "geracoes", "tour", "taxa_mutacao",
                "taxa_crossover", "convergencia_pct", "tempo_medio_ms", "dentro_do_teto"});
        csv.add(linhaCsv(baseline, medBase, "ref"));

        // 2) demais variacoes
        Config melhor = baseline;
        Medida medMelhor = medBase;

        for (Config cfg : variacoes) {
            Medida m = medir(problema, cfg);
            boolean dentro = m.tempoMs <= cap;
            System.out.printf("%-40s %9.1f %12.4f %9s%n",
                    rotulo(cfg), m.convPct, m.tempoMs, dentro ? "sim" : "NAO");
            csv.add(linhaCsv(cfg, m, dentro ? "sim" : "nao"));

            // melhor = maior convergencia DENTRO do teto (desempate: menor tempo)
            if (dentro && (m.convPct > medMelhor.convPct
                    || (m.convPct == medMelhor.convPct && m.tempoMs < medMelhor.tempoMs))) {
                melhor = cfg;
                medMelhor = m;
            }
        }

        System.out.println(LINHA);
        System.out.printf("Melhor variacao (dentro do teto): %s  (conv = %.1f%%, tempo = %.4f ms)%n",
                rotulo(melhor), medMelhor.convPct, medMelhor.tempoMs);
        System.out.printf("Ganho de convergencia sobre o baseline: %+.1f p.p.%n",
                medMelhor.convPct - medBase.convPct);

        gravarCsv(csv);
        demonstrarSolucao(problema, melhor);
    }

    /**
     * Variacoes dos eixos numericos, todas partindo do baseline (TM2-S1-C2-R1).
     * Eixos isolados (V1..V12) e combinacoes dos ajustes baratos (V13..V14).
     */
    private static List<Config> construirVariacoes() {
        List<Config> v = new ArrayList<>();
        int idx = 1;

        // --- Populacao (custa tempo, ~linear) ---
        v.add(Config.baseline().pop(150).indice(idx++).build());
        v.add(Config.baseline().pop(200).indice(idx++).build());

        // --- Geracoes (custa tempo so nos runs que nao convergem cedo) ---
        v.add(Config.baseline().geracoes(75).indice(idx++).build());
        v.add(Config.baseline().geracoes(100).indice(idx++).build());

        // --- Taxa de mutacao (custo de tempo ~zero) ---
        v.add(Config.baseline().taxaMutacao(0.10).indice(idx++).build());
        v.add(Config.baseline().taxaMutacao(0.30).indice(idx++).build());
        v.add(Config.baseline().taxaMutacao(0.40).indice(idx++).build());

        // --- Taxa de crossover (custo de tempo ~zero) ---
        v.add(Config.baseline().taxaCrossover(0.70).indice(idx++).build());
        v.add(Config.baseline().taxaCrossover(0.80).indice(idx++).build());
        v.add(Config.baseline().taxaCrossover(0.90).indice(idx++).build());

        // --- Tamanho do torneio (custo de tempo ~zero) ---
        v.add(Config.baseline().tour(2).indice(idx++).build());
        v.add(Config.baseline().tour(5).indice(idx++).build());

        // --- Combinacoes dos ajustes baratos (sem mexer em pop/geracoes) ---
        v.add(Config.baseline().taxaMutacao(0.30).taxaCrossover(0.80).indice(idx++).build());
        v.add(Config.baseline().taxaMutacao(0.30).taxaCrossover(0.80).tour(5).indice(idx++).build());

        // --- Combinacoes fortes: populacao grande + reinsercao elitista + crossover e
        //     mutacao altos. Convergem em ~11 geracoes, entao a pop grande termina cedo
        //     e cabe no teto. Aqui esta o melhor resultado da etapa 2. ---
        v.add(Config.baseline().pop(100).reinsercao(Config.Reinsercao.R2_ELITISMO)
                .taxaCrossover(0.80).taxaMutacao(0.40).indice(idx++).build());
        v.add(Config.baseline().pop(100).reinsercao(Config.Reinsercao.R2_ELITISMO)
                .taxaCrossover(0.80).taxaMutacao(0.40).indice(idx++).build());
        v.add(Config.baseline().pop(100).reinsercao(Config.Reinsercao.R2_ELITISMO)
                .taxaCrossover(0.80).taxaMutacao(0.50).indice(idx++).build());
        v.add(Config.baseline().pop(100).reinsercao(Config.Reinsercao.R2_ELITISMO)
                .taxaCrossover(0.30).taxaMutacao(1).indice(idx++).build());
        v.add(Config.baseline().pop(150).reinsercao(Config.Reinsercao.R2_ELITISMO)
                .taxaCrossover(0.0).taxaMutacao(1).indice(idx++).build());

        return v;
    }

    /** Config de referencia (baseline) usada para gerar os rotulos por diferenca. */
    private static final Config BASE = Config.baseline().indice(0).build();

    /**
     * Rotulo gerado automaticamente a partir dos valores REAIS da config: mostra "V{indice}"
     * seguido apenas dos parametros que diferem do baseline. Assim o nome impresso nunca
     * desincroniza dos valores ajustados na variacao.
     */
    private static String rotulo(Config cfg) {
        StringBuilder d = new StringBuilder();
        if (cfg.pop != BASE.pop)                     juntar(d, "pop=" + cfg.pop);
        if (cfg.geracoes != BASE.geracoes)           juntar(d, "ger=" + cfg.geracoes);
        if (cfg.taxaMutacao != BASE.taxaMutacao)     juntar(d, "mut=" + pct(cfg.taxaMutacao));
        if (cfg.taxaCrossover != BASE.taxaCrossover) juntar(d, "cx=" + pct(cfg.taxaCrossover));
        if (cfg.tour != BASE.tour)                   juntar(d, "tour=" + cfg.tour);
        if (cfg.selecao != BASE.selecao)             juntar(d, cfg.selecao.name());
        if (cfg.crossover != BASE.crossover)         juntar(d, cfg.crossover.name());
        if (cfg.reinsercao != BASE.reinsercao)
            juntar(d, cfg.reinsercao == Config.Reinsercao.R2_ELITISMO ? "elit" : "ord");
        return "V" + cfg.indice + " " + (d.length() == 0 ? "baseline" : d.toString());
    }

    private static void juntar(StringBuilder d, String s) {
        if (d.length() > 0) d.append(' ');
        d.append(s);
    }

    private static String pct(double v) {
        return Math.round(v * 100) + "%";
    }

    /** Resultado agregado de 1000 execucoes de uma configuracao. */
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
            // semente reproducivel por (variacao, execucao)
            Random rnd = new Random((long) cfg.indice * 1_000_000L + i);
            long t0 = System.nanoTime();
            AG.Resultado res = AG.executar(problema, cfg, rnd);
            tempoTotalNs += System.nanoTime() - t0;
            if (res.convergiu) {
                convergencias++;
            }
        }
        double convPct = 100.0 * convergencias / EXECUCOES;
        double tempoMs = (tempoTotalNs / (double) EXECUCOES) / 1_000_000.0;
        return new Medida(convPct, tempoMs);
    }

    private static String[] linhaCsv(Config cfg, Medida m, String dentro) {
        return new String[]{
                rotulo(cfg),
                String.valueOf(cfg.pop),
                String.valueOf(cfg.geracoes),
                String.valueOf(cfg.tour),
                String.format("%.2f", cfg.taxaMutacao),
                String.format("%.2f", cfg.taxaCrossover),
                String.format("%.2f", m.convPct),
                String.format("%.4f", m.tempoMs),
                dentro
        };
    }

    private static void gravarCsv(List<String[]> linhas) throws IOException {
        Path dir = Paths.get("etapa2", "resultados");
        Files.createDirectories(dir);
        Path arquivo = dir.resolve("etapa2.csv");
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(arquivo))) {
            for (String[] linha : linhas) {
                pw.println(String.join(",", linha));
            }
        }
        System.out.println("CSV salvo em: " + arquivo.toAbsolutePath());
    }

    /** Demonstra uma solucao encontrada (letra -> digito) com a melhor variacao. */
    private static void demonstrarSolucao(Problema problema, Config cfg) {
        AG.Resultado solucao = null;
        for (int i = 0; i < 2000 && solucao == null; i++) {
            AG.Resultado res = AG.executar(problema, cfg, new Random(i));
            if (res.convergiu) {
                solucao = res;
            }
        }
        System.out.println(LINHA);
        if (solucao == null) {
            System.out.println("Nenhuma solucao encontrada na demonstracao.");
            return;
        }
        Map<Character, Integer> mapa = problema.mapeamento(solucao.melhorGenes);
        System.out.println("Solucao encontrada (" + rotulo(cfg) + "):");
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Character, Integer> e : mapa.entrySet()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(e.getKey()).append("=").append(e.getValue());
        }
        System.out.println("  " + sb);
        long a = problema.valorPalavra(problema.parcela1, solucao.melhorGenes);
        long b = problema.valorPalavra(problema.parcela2, solucao.melhorGenes);
        long r = problema.valorPalavra(problema.resultado, solucao.melhorGenes);
        System.out.printf("  %d + %d = %d%n", a, b, r);
    }
}
