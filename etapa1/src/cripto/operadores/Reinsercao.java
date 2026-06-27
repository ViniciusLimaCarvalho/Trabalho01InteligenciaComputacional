package cripto.operadores;

import cripto.Config;
import cripto.Individuo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Reinsercao {

    public static List<Individuo> reinserir(List<Individuo> pais, List<Individuo> filhos, Config cfg) {
        switch (cfg.reinsercao) {
            case R1_ORDENADA: return reinsercaoOrdenada(pais, filhos);
            case R2_ELITISMO: return reinsercaoElitismo(pais, filhos);
            default: throw new IllegalStateException("Tipo de reinserção inválida");
        }
    }

    private static List<Individuo> reinsercaoOrdenada(List<Individuo> pais, List<Individuo> filhos) {

        List<Individuo> todos = new ArrayList<>(pais.size() + filhos.size());

        todos.addAll(pais);
        todos.addAll(filhos);

        Collections.sort(todos);
        return new ArrayList<>(todos.subList(0, Config.POP));

    }

    private static List<Individuo> reinsercaoElitismo(List<Individuo> pais, List<Individuo> filhos) {

        int nElite = (int) Math.round(Config.POP * Config.ELITISMO);
        List<Individuo> paisOrdenados = new ArrayList<>(pais);
        Collections.sort(paisOrdenados);

        List<Individuo> novaPopulacao = new ArrayList<>(Config.POP);
        for (int i = 0; i < nElite; i++) {
            novaPopulacao.add(paisOrdenados.get(i));
        }
        for (int i = 0; i < filhos.size() && novaPopulacao.size() < Config.POP; i++) {
            novaPopulacao.add(filhos.get(i));
        }

        return novaPopulacao;
    }
}
