package cripto;

import cripto.operadores.Crossover;
import cripto.operadores.Mutacao;
import cripto.operadores.Reinsercao;
import cripto.operadores.Selecao;

import java.util.*;

public class AG {

    public static class Resultado {
        public final boolean convergiu;
        public final int geracoes;
        public final long melhorFitness;
        public final int[] melhorGenes;

        public Resultado(boolean convergiu, int geracoes, long melhorFitness, int[] melhorGenes) {
            this.convergiu = convergiu;
            this.geracoes = geracoes;
            this.melhorFitness = melhorFitness;
            this.melhorGenes = melhorGenes;
        }
    }

    public static Resultado executar(Problema problema, Config cfg) {
        Random rnd = new Random();
        if(cfg.pop < 2){
            throw new IllegalArgumentException(
                    "Deve existir no mínimo dois indivíduos na população");
        }
        if((cfg.pop % 2) != 0){
            throw new IllegalArgumentException(
                    "A população deve ser um número par");
        }

        List<Individuo> pop = new ArrayList<>(cfg.pop);
        Set<String> genesExistentes = new HashSet<>();

        while(pop.size() < cfg.pop){
            Individuo ind = Individuo.aleatorio(rnd);

            String assinatura = Arrays.toString(ind.genes);

            if(!genesExistentes.contains(assinatura)){
                genesExistentes.add(assinatura);
                pop.add(ind);
            }
        }

        for (Individuo ind : pop) {
            ind.avaliar(problema);
        }

        Individuo melhor = melhorDe(pop);

        for (int g = 1; g <= cfg.geracoes; g++){

            List<Individuo> filhos = new ArrayList<>(cfg.pop);
            List<Individuo> candidatos = new ArrayList<>(pop);

            long quantidadeDePais = Math.round((cfg.taxaCrossover() *  cfg.pop));
            if((quantidadeDePais % 2) != 0) quantidadeDePais -= 1;
            long quantidadeDeReproducoes = quantidadeDePais/2;

            if(quantidadeDeReproducoes < 1){
                throw new IllegalArgumentException(
                        "A quantidade de reproduções eh nula, sendo inválido\n" +
                                "Recomenda-se ajustar o parâmentro de população e/ou taxa de crossover");
            }

            for(int i = 0; i < quantidadeDeReproducoes; ++i) {
                Individuo pai1 = Selecao.selecionar(candidatos, cfg);
                candidatos.remove(pai1);
                Individuo pai2 = Selecao.selecionar(candidatos, cfg);
                candidatos.remove(pai2);

                Individuo[] fis = Crossover.cruzar(pai1, pai2, cfg);

                if(rnd.nextDouble() <= cfg.taxaMutacao()){
                    Mutacao.mutacaoPermutacao(fis[0]);
                }

                if(rnd.nextDouble() <= cfg.taxaMutacao()) {
                    Mutacao.mutacaoPermutacao(fis[1]);
                }

                fis[0].avaliar(problema);
                fis[1].avaliar(problema);
                filhos.add(fis[0]);
                filhos.add(fis[1]);

            }

            pop = Reinsercao.reinserir(pop, filhos, cfg);
            Individuo melhorGeracao = melhorDe(pop);
            if (melhorGeracao.fitness < melhor.fitness) {
                melhor = melhorGeracao;
            }

            if (melhor.fitness == 0) {
                return new Resultado(true, g, 0, melhor.genes.clone());
            }

        }
        return new Resultado(false, cfg.geracoes, melhor.fitness, melhor.genes.clone());
    }

    private static Individuo melhorDe(List<Individuo> pop) {
        Individuo m = pop.getFirst();
        for (Individuo ind : pop) {
            if (ind.fitness < m.fitness) {
                m = ind;
            }
        }
        return m;
    }
}
