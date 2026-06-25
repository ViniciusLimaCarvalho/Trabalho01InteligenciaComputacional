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
 * Eixo explorado: MUTACAO CRIATIVA. Mantemos fixos populacao=100, geracoes=50,
 * torneio (tour=3), PMX e reinsercao ordenada, e variamos o OPERADOR de mutacao
 * (swap, inversao, scramble, multi-swap, insercao, hibrida) e a TAXA de mutacao.
 *
 * Como mutacao e O(1), todas as variacoes custam praticamente o mesmo tempo do
 * baseline -> respeitam o teto de +50% por construcao. O ganho buscado e puramente
 * em taxa de convergencia.
 *
 * O teto de tempo e medido no proprio run: roda-se o baseline (com warmup) e define-se
 * cap = 1.5 x tempoBaseline. Cada variacao e marcada como DENTRO/FORA do cap.
 * Cada configuracao e executada 1000 vezes; grava CSV em etapa2/resultados/etapa2.csv.
 */
public class Experimento2 {

    static final int EXECUCOES = 1000;
    static final String LINHA =
            "----------------------------------------------------------------------";

    public static void main(String[] args) throws IOException {
        Problema problema = new Problema("SEND", "MORE", "MONEY");

        Config baseline = Config.baseline()
                .nome("V0 Baseline (swap @20%)").indice(0).build();

        List<Config> variacoes = construirVariacoes();

        // warmup da JVM para nao penalizar a primeira medicao
        for (int i = 0; i < 200; i++) {
            AG.executar(problema, baseline, new Random(i));
        }

        System.out.println("Problema: " + problema + "   (" + EXECUCOES + " execucoes por configuracao)");
        System.out.println("Eixo de refinamento: MUTACAO CRIATIVA  (base: TM2-S1-C2-R1 da etapa 1)");
        System.out.println(LINHA);

        // 1) baseline define o teto de tempo (cap = 1.5x)
        Medida medBase = medir(problema, baseline);
        double cap = 1.5 * medBase.tempoMs;
        System.out.printf("Baseline: conv = %.1f%%, tempo = %.4f ms  ->  teto (+50%%) = %.4f ms%n",
                medBase.convPct, medBase.tempoMs, cap);
        System.out.println(LINHA);
        System.out.printf("%-30s %9s %12s %9s%n", "Config", "Conv.(%)", "Tempo(ms)", "<=teto?");
        System.out.println(LINHA);
        System.out.printf("%-30s %9.1f %12.4f %9s%n",
                baseline.nome, medBase.convPct, medBase.tempoMs, "ref");

        List<String[]> csv = new ArrayList<>();
        csv.add(new String[]{"variacao", "tipo_mutacao", "taxa_mutacao",
                "convergencia_pct", "tempo_medio_ms", "dentro_do_teto"});
        csv.add(linhaCsv(baseline, medBase, "ref"));

        // 2) demais variacoes
        Config melhor = baseline;
        Medida medMelhor = medBase;

        for (Config cfg : variacoes) {
            Medida m = medir(problema, cfg);
            boolean dentro = m.tempoMs <= cap;
            System.out.printf("%-30s %9.1f %12.4f %9s%n",
                    cfg.nome, m.convPct, m.tempoMs, dentro ? "sim" : "NAO");
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
                melhor.nome, medMelhor.convPct, medMelhor.tempoMs);
        System.out.printf("Ganho de convergencia sobre o baseline: %+.1f p.p.%n",
                medMelhor.convPct - medBase.convPct);

        gravarCsv(csv);
        demonstrarSolucao(problema, melhor);
    }

    /** As 11 variacoes do eixo de mutacao criativa (todas partindo do baseline). */
    private static List<Config> construirVariacoes() {
        List<Config> v = new ArrayList<>();
        int idx = 1;

        // operadores criativos com a mesma taxa do baseline (20%)
        v.add(Config.baseline().tipoMutacao(Config.TipoMutacao.INVERSAO)
                .nome("V" + idx + " Inversao @20%").indice(idx++).build());
        v.add(Config.baseline().tipoMutacao(Config.TipoMutacao.SCRAMBLE)
                .nome("V" + idx + " Scramble @20%").indice(idx++).build());
        v.add(Config.baseline().tipoMutacao(Config.TipoMutacao.MULTI_SWAP)
                .nome("V" + idx + " Multi-swap @20%").indice(idx++).build());
        v.add(Config.baseline().tipoMutacao(Config.TipoMutacao.INSERCAO)
                .nome("V" + idx + " Insercao @20%").indice(idx++).build());
        v.add(Config.baseline().tipoMutacao(Config.TipoMutacao.HIBRIDA)
                .nome("V" + idx + " Hibrida @20%").indice(idx++).build());

        // operadores promissores com taxa elevada (30%)
        v.add(Config.baseline().tipoMutacao(Config.TipoMutacao.INVERSAO).taxaMutacao(0.30)
                .nome("V" + idx + " Inversao @30%").indice(idx++).build());
        v.add(Config.baseline().tipoMutacao(Config.TipoMutacao.SCRAMBLE).taxaMutacao(0.30)
                .nome("V" + idx + " Scramble @30%").indice(idx++).build());
        v.add(Config.baseline().tipoMutacao(Config.TipoMutacao.MULTI_SWAP).taxaMutacao(0.30)
                .nome("V" + idx + " Multi-swap @30%").indice(idx++).build());
        v.add(Config.baseline().tipoMutacao(Config.TipoMutacao.INSERCAO).taxaMutacao(0.30)
                .nome("V" + idx + " Insercao @30%").indice(idx++).build());
        v.add(Config.baseline().tipoMutacao(Config.TipoMutacao.HIBRIDA).taxaMutacao(0.30)
                .nome("V" + idx + " Hibrida @30%").indice(idx++).build());

        // taxa ainda mais alta no melhor candidato esperado
        v.add(Config.baseline().tipoMutacao(Config.TipoMutacao.INVERSAO).taxaMutacao(0.40)
                .nome("V" + idx + " Inversao @40%").indice(idx++).build());

        return v;
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
                cfg.nome,
                cfg.tipoMutacao.name(),
                String.format("%.2f", cfg.taxaMutacao),
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
        System.out.println("Solucao encontrada (" + cfg.nome + "):");
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
