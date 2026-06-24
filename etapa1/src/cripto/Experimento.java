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
 * 1a Etapa: executa as 16 combinacoes (TM x S x C x R) no problema SEND + MORE = MONEY,
 * cada uma 1000 vezes, medindo taxa de convergencia e tempo medio de execucao.
 * Imprime a tabela no console e grava o CSV em etapa1/resultados/etapa1.csv.
 */
public class Experimento {

    static final int EXECUCOES = 1000;
    static final String LINHA =
            "--------------------------------------------------------";

    public static void main(String[] args) throws IOException {
        Problema problema = new Problema("SEND", "MORE", "MONEY");

        List<String[]> csv = new ArrayList<>();
        csv.add(new String[]{"config", "TM", "S", "C", "R", "convergencia_pct", "tempo_medio_ms"});

        System.out.println("Problema: " + problema + "   (" + EXECUCOES + " execucoes por configuracao)");
        System.out.println(LINHA);
        System.out.printf("%-18s %12s %15s%n", "Config", "Conv.(%)", "Tempo(ms)");
        System.out.println(LINHA);

        Config melhorConfig = null;
        double melhorConv = -1.0;
        double melhorTempo = Double.MAX_VALUE;

        for (Config.TaxaMutacao tm : Config.TaxaMutacao.values()) {
            for (Config.Selecao s : Config.Selecao.values()) {
                for (Config.Crossover c : Config.Crossover.values()) {
                    for (Config.Reinsercao r : Config.Reinsercao.values()) {
                        Config cfg = new Config(tm, s, c, r);

                        int convergencias = 0;
                        long tempoTotalNs = 0;
                        for (int i = 0; i < EXECUCOES; i++) {
                            // semente reproducivel por (configuracao, execucao)
                            Random rnd = new Random((long) cfg.indice() * 1_000_000L + i);
                            long t0 = System.nanoTime();
                            AG.Resultado res = AG.executar(problema, cfg, rnd);
                            tempoTotalNs += System.nanoTime() - t0;
                            if (res.convergiu) {
                                convergencias++;
                            }
                        }

                        double convPct = 100.0 * convergencias / EXECUCOES;
                        double tempoMedioMs = (tempoTotalNs / (double) EXECUCOES) / 1_000_000.0;

                        System.out.printf("%-18s %12.1f %15.4f%n", cfg.codigo(), convPct, tempoMedioMs);
                        csv.add(new String[]{
                                cfg.codigo(), tm.name(), s.name(), c.name(), r.name(),
                                String.format("%.2f", convPct), String.format("%.4f", tempoMedioMs)
                        });

                        if (convPct > melhorConv
                                || (convPct == melhorConv && tempoMedioMs < melhorTempo)) {
                            melhorConv = convPct;
                            melhorTempo = tempoMedioMs;
                            melhorConfig = cfg;
                        }
                    }
                }
            }
        }

        System.out.println(LINHA);
        System.out.printf("Melhor configuracao: %s  (conv = %.1f%%, tempo = %.4f ms)%n",
                melhorConfig.codigo(), melhorConv, melhorTempo);

        gravarCsv(csv);
        demonstrarSolucao(problema, melhorConfig);
    }

    private static void gravarCsv(List<String[]> linhas) throws IOException {
        Path dir = Paths.get("etapa1", "resultados");
        Files.createDirectories(dir);
        Path arquivo = dir.resolve("etapa1.csv");
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(arquivo))) {
            for (String[] linha : linhas) {
                pw.println(String.join(",", linha));
            }
        }
        System.out.println("CSV salvo em: " + arquivo.toAbsolutePath());
    }

    /** Demonstra uma solucao encontrada (letra -> digito) com a melhor configuracao. */
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
        System.out.println("Solucao encontrada (" + cfg.codigo() + "):");
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
