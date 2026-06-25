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

    public static void main(String[] args) {
        double tempoExec = 0;
        int convergencias = 0;
        Problema problema = new Problema("SEND", "MORE", "MONEY");

        // 0.733
        // TM1-S1-C1-R1
        Config cfg = new Config(Config.TaxaMutacao.TM1,
                Config.Selecao.S1_TORNEIO,
                Config.Crossover.C1_CX,
                Config.Reinsercao.R1_ORDENADA); // melhores configs da etapa 1
        final int EXECS = 1000;
        System.out.println("Primeira ideia: dobrar gerações e tamanho da população");
//        Config.POP = 100;
//        Config.GERACOES = 50;
        int gens = 0;
        for(int i = 0; i < EXECS; i++) {
            // semente reproducivel por (configuracao, execucao)
            Random rnd = new Random((long) cfg.indice() * 1_000_000L + i);
            long t0 = System.nanoTime();
            AG.Resultado res = AG.executar(problema, cfg, rnd);
            tempoExec += System.nanoTime() - t0;
            gens += res.geracoes;
            if (res.convergiu) {
                convergencias++;
            }
        }

        double convPct = 100.0 * convergencias / EXECS;
        double tempo = (tempoExec / (double) EXECS) / 1_000_000.0;

        System.out.println("Taxa de convergencia: " + convPct);
        System.out.println("Tempo de execucao: " + tempo);
        System.out.println("Gen media: " + gens / (double) EXECS );
//        Experimento.demonstrarSolucao(problema, cfg);
    }

}
