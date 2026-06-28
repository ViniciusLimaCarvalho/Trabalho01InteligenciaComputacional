package cripto.operadores;

import cripto.Config;
import cripto.Individuo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Reinsercao {

    public static List<Individuo> reinserir(List<Individuo> pais, List<Individuo> filhos, Config cfg) {
        switch (cfg.reinsercao) {
            case R1_ORDENADA: return ordenada(pais, filhos, cfg);
            case R2_ELITISMO: return elitismo(pais, filhos, cfg);
            default: throw new IllegalStateException("Reinsercao desconhecida");
        }
    }


    private static List<Individuo> ordenada(List<Individuo> pais, List<Individuo> filhos, Config cfg) {
        List<Individuo> todos = new ArrayList<>(pais.size() + filhos.size());
        todos.addAll(pais);
        todos.addAll(filhos);
        Collections.sort(todos);
        return new ArrayList<>(todos.subList(0, cfg.pop));
    }


    private static List<Individuo> elitismo(List<Individuo> pais, List<Individuo> filhos, Config cfg) {
        int nElite = (int) Math.round(cfg.pop * cfg.elitismo);

        List<Individuo> paisOrdenados = new ArrayList<>(pais);
        Collections.sort(paisOrdenados);

        List<Individuo> nova = new ArrayList<>(cfg.pop);
        for (int i = 0; i < nElite; i++) {
            nova.add(paisOrdenados.get(i));
        }
        for (int i = 0; i < filhos.size() && nova.size() < cfg.pop; i++) {
            nova.add(filhos.get(i));
        }
        for (int i = nElite; nova.size() < cfg.pop; i++) {
            nova.add(paisOrdenados.get(i));
        }
        return nova;
    }
}
