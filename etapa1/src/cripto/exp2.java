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


public class exp2 {

    // tempo de exec original: 0.4631ms
    // tempo maximo agora: 0.4631ms * 1.5 = 0.69465 ms


    final static int EXECS = 1000;
    static Problema problema = new Problema("SEND", "MORE", "MONEY");
    static final String LINHA =
            "--------------------------------------------------------";

    public static void main(String[] args) {
        // melhores configs da etapa 1: TM2-S1-C2-R1
        // executou em: 0.4321 -> limite = 0.64

        System.out.println(LINHA);
        Exec1();
        System.out.println(LINHA);

        System.out.println(LINHA);
        Exec2();
        System.out.println(LINHA);

        System.out.println(LINHA);
        Exec3();
        System.out.println(LINHA);

        System.out.println(LINHA);
        Exec4();
        System.out.println(LINHA);

        System.out.println(LINHA);
        Exec5();
        System.out.println(LINHA);

        System.out.println(LINHA);
        Exec6();
        System.out.println(LINHA);

        System.out.println(LINHA);
        Exec7();
        System.out.println(LINHA);

        System.out.println(LINHA);
        Exec8();
        System.out.println(LINHA);
    }

    static void Exec1() {
        System.out.println("Mudanças:\n" +
                "-> Seleção mudada para R2\n" +
                "-> Elitismo = 0\n");
        Config cfg = new Config(Config.TaxaMutacao.TM2, Config.Selecao.S1_TORNEIO,
                Config.Crossover.C2_PMX, Config.Reinsercao.R2_ELITISMO);
        Config.ELITISMO = 0;
        TesteAG(cfg);
        Config.ELITISMO = 0.2;
    }

    static void Exec2() {
        System.out.println("Mudanças:\n" +
                "-> Seleção mudada para R2\n" +
                "-> Elitismo = 0\n" +
                "-> Taxa de crossover = 1\n");
        Config cfg = new Config(Config.TaxaMutacao.TM2, Config.Selecao.S1_TORNEIO,
                Config.Crossover.C2_PMX, Config.Reinsercao.R2_ELITISMO);
        Config.ELITISMO = 0;
        Config.taxaCrossover = 1;
        TesteAG(cfg);
        Config.ELITISMO = 0.2;
    }

    static void Exec3() {
        System.out.println("Mudanças:\n" +
                "-> Seleção mudada para R2\n" +
                "-> Elitismo = 0\n" +
                "-> Taxa de crossover = 0\n" +
                "-> Taxa de mutação = 1");
        Config cfg = new Config(Config.TaxaMutacao.TM3, Config.Selecao.S1_TORNEIO,
                Config.Crossover.C2_PMX, Config.Reinsercao.R2_ELITISMO);
        Config.ELITISMO = 0;
        Config.taxaCrossover = 0;
        TesteAG(cfg);
        Config.ELITISMO = 0.2;
    }

    static void Exec4() {
        System.out.println("Mudanças:\n" +
                "-> Seleção mudada para R2\n" +
                "-> Elitismo = 0\n" +
                "-> Taxa de crossover = 0\n" +
                "-> Taxa de mutação = 1\n" +
                "-> Tamanho da população = 250");
        Config cfg = new Config(Config.TaxaMutacao.TM3, Config.Selecao.S1_TORNEIO,
                Config.Crossover.C2_PMX, Config.Reinsercao.R2_ELITISMO);
        Config.POP = 250;
        Config.ELITISMO = 0;
        Config.taxaCrossover = 0;
        TesteAG(cfg);
        Config.POP = 100; // como eh static eh bom voltar pro valor original dps
        Config.ELITISMO = 0.2;
    }

    static void Exec5() {
        System.out.println("Mudanças:\n" +
                "-> Seleção mudada para R2\n" +
                "-> Elitismo = 20%\n" +
                "-> Taxa de crossover = 0\n" +
                "-> Taxa de mutação = 1\n" +
                "-> Tamanho da população = 250\n" );
        Config cfg = new Config(Config.TaxaMutacao.TM3, Config.Selecao.S1_TORNEIO,
                Config.Crossover.C2_PMX, Config.Reinsercao.R2_ELITISMO);
        Config.POP = 250;
        Config.ELITISMO = 0.2;
        Config.taxaCrossover = 0;
        TesteAG(cfg);
        Config.POP = 100; // como eh static eh bom voltar pro valor original dps
        Config.ELITISMO = 0.2;
    }

    static void Exec6() {
        System.out.println("Mudanças:\n" +
                "-> Seleção mudada para R2\n" +
                "-> Elitismo = 0\n" +
                "-> Taxa de crossover = 0\n" +
                "-> Taxa de mutação = 1\n" +
                "-> Tamanho da população = 250\n" +
                "-> TOUR do torneio = 0");
        Config cfg = new Config(Config.TaxaMutacao.TM3, Config.Selecao.S1_TORNEIO,
                Config.Crossover.C2_PMX, Config.Reinsercao.R2_ELITISMO);
        Config.POP = 250;
        Config.ELITISMO = 0;
        Config.TOUR = 0;
        Config.taxaCrossover = 0;
        TesteAG(cfg);
        Config.POP = 100; // como eh static eh bom voltar pro valor original dps
        Config.ELITISMO = 0.2;
        Config.TOUR = 3;
    }

    static void Exec7() {
        System.out.println("Mudanças:\n" +
                "-> Seleção mudada para R2\n" +
                "-> Elitismo = 0.2\n" +
                "-> Taxa de crossover = 0.3\n" +
                "-> Taxa de mutação = 1\n" +
                "-> Tamanho da população = 100\n");
        Config cfg = new Config(Config.TaxaMutacao.TM3, Config.Selecao.S1_TORNEIO,
                Config.Crossover.C2_PMX, Config.Reinsercao.R2_ELITISMO);
        Config.POP = 100;
        Config.ELITISMO = 0.2;
        Config.taxaCrossover = 0.3;
        TesteAG(cfg);
        Config.POP = 100; // como eh static eh bom voltar pro valor original dps
        Config.ELITISMO = 0.2;
        Config.TOUR = 3;
    }

    static void Exec8() {
        System.out.println("Mudanças:\n" +
                "-> Seleção mudada para R2\n" +
                "-> Elitismo = 0.1\n" +
                "-> Taxa de crossover = 0\n" +
                "-> Taxa de mutação = 1\n" +
                "-> Tamanho da população = 150\n");
        Config cfg = new Config(Config.TaxaMutacao.TM3, Config.Selecao.S1_TORNEIO,
                Config.Crossover.C2_PMX, Config.Reinsercao.R2_ELITISMO);
        Config.POP = 150;
        Config.ELITISMO = 0.1;
        Config.taxaCrossover = 0;
        TesteAG(cfg);
        Config.POP = 100; // como eh static eh bom voltar pro valor original dps
        Config.ELITISMO = 0.2;
        Config.TOUR = 3;
    }

    private static void TesteAG(Config cfg) {
        // aquecimento da JVM
        AG.Resultado res;
        for (int i = 0; i < 500; i++) {
            res = AG.executar(problema, cfg, new Random(i));
        }

        double tempoExec = 0;
        int gens = 0;
        int convergencias = 0;

        for(int i = 0; i < EXECS; i++) {
            Random rnd = new Random();
            long t0 = System.nanoTime();
            res = AG.executar(problema, cfg, rnd);
            tempoExec += System.nanoTime() - t0;
            gens += res.geracoes;
            if (res.convergiu) {
                convergencias++;
            }
        }

        double convPct = 100.0 * convergencias / EXECS;
        double tempo = (tempoExec / (double) EXECS) / 1_000_000.0;

        System.out.println("\nTempo de execucao: " + tempo);
        System.out.println("Taxa de convergencia: " + convPct);
        System.out.println("Gen media: " + gens / (double) EXECS );

        System.out.println();
        demonstrarSolucao(problema, cfg);

    }

    private static void demonstrarSolucao(Problema problema, Config cfg) {
        AG.Resultado solucao = null;
        for (int i = 0; i < 2000 && solucao == null; i++) {
            AG.Resultado res = AG.executar(problema, cfg, new Random(i));
            if (res.convergiu) {
                solucao = res;
            }
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
