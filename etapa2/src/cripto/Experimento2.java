package cripto;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Experimento2 {

    static final int EXECUCOES = 1000;
    static final String LINHA =
            "----------------------------------------------------------------------";

    private static final Config BASE = new Config(0, 100, 50, 3, 0.2, Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
            Config.Reinsercao.R2_ELITISMO, 0.2, 0.6);

    public static void main(String[] args) throws IOException {
        Problema problema = new Problema("SEND", "MORE", "MONEY");

        List<Config> variacoes = construirVariacoes();

        for (int i = 0; i < 200; i++) {
            AG.executar(problema, BASE);
        }

        System.out.println("Problema: " + problema + "   (" + EXECUCOES + " execucoes por configuracao)");
        System.out.println("Eixo de refinamento: PARAMETROS NUMERICOS  (base: TM2-S1-C2-R2 da etapa 1)");
        System.out.println(LINHA);

        Medida medBase = medir(problema, BASE);
        double cap = 1.5 * medBase.tempoMs;
        System.out.printf("Baseline: conv = %.1f%%, tempo = %.4f ms  ->  teto (+50%%) = %.4f ms%n",
                medBase.convPct, medBase.tempoMs, cap);
        System.out.println(LINHA);
        System.out.printf("%-40s %9s %12s %9s%n", "Config", "Conv.(%)", "Tempo(ms)", "<=teto?");
        System.out.println(LINHA);
        System.out.printf("%-40s %9.1f %12.4f %9s%n",
                rotulo(BASE), medBase.convPct, medBase.tempoMs, "ref");

        List<String[]> csv = new ArrayList<>();
        csv.add(new String[]{"variacao", "pop", "geracoes", "tour", "taxa_mutacao",
                "taxa_crossover", "convergencia_pct", "tempo_medio_ms", "dentro_do_teto"});
        csv.add(linhaCsv(BASE, medBase, "ref"));

        Config melhor = BASE;
        Medida medMelhor = medBase;

        for (Config cfg : variacoes) {
            Medida m = medir(problema, cfg);
            boolean dentro = m.tempoMs <= cap;
            System.out.printf("%-40s %9.1f %12.4f %9s%n",
                    rotulo(cfg), m.convPct, m.tempoMs, dentro ? "sim" : "NAO");
            csv.add(linhaCsv(cfg, m, dentro ? "sim" : "nao"));

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

    private static List<Config> construirVariacoes() {
        List<Config> v = new ArrayList<>();
        int idx = 1;

        v.add(new Config(idx++, 150, 50, 3, 0.2, Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.2, 0.6));
        v.add(new Config(idx++, 200, 50, 3, 0.2, Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.2, 0.6));


        v.add(new Config(idx++, 100, 75, 3, 0.2, Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.2, 0.6));
        v.add(new Config(idx++, 100, 100, 3, 0.2, Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.2, 0.6));


        v.add(new Config(idx++, 100, 50, 3, 0.2, Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.1, 0.6));
        v.add(new Config(idx++, 100, 50, 3, 0.2, Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.3, 0.6));
        v.add(new Config(idx++, 100, 50, 3, 0.2, Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.4, 0.6));



        v.add(new Config( idx++, 100, 50, 3, 0.2, Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.2, 0.7));
        v.add(new Config( idx++, 100, 50, 3, 0.2, Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.2, 0.8));
        v.add(new Config( idx++, 100, 50, 3, 0.2, Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.2, 0.9));

        v.add(new Config(idx++, 100, 50, 2, 0.2, Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.2, 0.6));
        v.add(new Config(idx++, 100, 50, 5, 0.2, Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.2, 0.6));

        v.add(new Config(idx++, 100, 50, 3, 0.2, Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.3, 0.8));
        v.add(new Config(idx++, 100, 50, 5, 0.2, Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.3, 0.8));


        v.add(new Config(idx++, 100, 50, 3, 0.2, Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.4, 0.8));


        v.add(new Config(idx++, 100, 50, 3, 0.2, Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 0.5, 0.8));


        v.add(new Config(idx++, 100, 50, 3, 0.2, Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 1, 0.3));


        v.add(new Config(idx++, 150, 50, 3, 0.2, Config.Selecao.S1_TORNEIO, Config.Crossover.C2_PMX,
                Config.Reinsercao.R2_ELITISMO, 1, 0.1));


        return v;
    }

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

    private static void demonstrarSolucao(Problema problema, Config cfg) {
        AG.Resultado solucao = null;
        for (int i = 0; i < 2000 && solucao == null; i++) {
            AG.Resultado res = AG.executar(problema, cfg);
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
