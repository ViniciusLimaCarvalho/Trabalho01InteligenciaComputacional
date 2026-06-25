package cripto.operadores;

import cripto.Config;
import cripto.Individuo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Estrategias de reinsercao para formar a proxima populacao. */
public class Reinsercao {

    public static List<Individuo> reinserir(List<Individuo> pais, List<Individuo> filhos, Config cfg) {
        switch (cfg.reinsercao) {
            case R1_ORDENADA: return ordenada(pais, filhos);
            case R2_ELITISMO: return elitismo(pais, filhos);
            default: throw new IllegalStateException("Reinsercao desconhecida");
        }
    }

    /** R1: reinsercao ordenada - mantem os POP melhores dentre pais + filhos. */
    private static List<Individuo> ordenada(List<Individuo> pais, List<Individuo> filhos) {
        List<Individuo> todos = new ArrayList<>(pais.size() + filhos.size());
        todos.addAll(pais);
        todos.addAll(filhos);
        Collections.sort(todos);
        return new ArrayList<>(todos.subList(0, Config.POP));
    }

    /**
     * R2: reinsercao pura com elitismo de 20% - preserva os 20% melhores pais (elite) e
     * preenche o restante com os filhos gerados (a taxa de crossover dessa configuracao e 80%).
     */
    private static List<Individuo> elitismo(List<Individuo> pais, List<Individuo> filhos) {
        int nElite = (int) Math.round(Config.POP * Config.ELITISMO); // 20

        List<Individuo> paisOrdenados = new ArrayList<>(pais);
        Collections.sort(paisOrdenados);

        List<Individuo> nova = new ArrayList<>(Config.POP);
        for (int i = 0; i < nElite; i++) {
            nova.add(paisOrdenados.get(i));
        }
        for (int i = 0; i < filhos.size() && nova.size() < Config.POP; i++) {
            nova.add(filhos.get(i));
        }
        // seguranca: completa com pais caso faltem filhos
//        for (int i = nElite; nova.size() < Config.POP; i++) {
//            nova.add(paisOrdenados.get(i));
//        }
        return nova;
    }
}
