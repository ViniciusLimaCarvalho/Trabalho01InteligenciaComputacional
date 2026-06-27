package cripto;

import cripto.operadores.Crossover;
import cripto.operadores.Mutacao;
import cripto.operadores.Reinsercao;
import cripto.operadores.Selecao;

import java.util.*;

/** Algoritmo Genetico parametrizado por uma {@link Config}. */
public class AG {

    /** Resultado de uma execucao do AG. */
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

        List<Individuo> pop = new ArrayList<>(Config.POP);
        Set<String> genesExistentes = new HashSet<>();

        while(pop.size() < Config.POP){
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

        for (int g = 1; g <= Config.GERACOES; g++){

            List<Individuo> filhos = new ArrayList<>(Config.POP);
            List<Individuo> candidatos = new ArrayList<>(pop);

            // alerta de la ele abaixo:
            long quantidadeDePais = Math.round((cfg.taxaCrossover() *  Config.POP));
            if((quantidadeDePais % 2) != 0) quantidadeDePais += 1;
            long quantidadeDeReproducoes = quantidadeDePais/2;

            // caso de beirada (apenas para testes onde cruzamento nao ocorre)
            // (mas ai eu acho que nao eh AG...)
            if(quantidadeDeReproducoes == 0){
                filhos.addAll(pop);
                for (Individuo f : filhos){
                    if(rnd.nextDouble() <= cfg.taxaMutacao()){
                        Mutacao.mutacaoPermutacao(f);
                    }
                }
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
        return new Resultado(false, Config.GERACOES, melhor.fitness, melhor.genes.clone());
    }

//    public static Resultado executar(Problema problema, Config cfg, Random rnd) {
//        // populacao inicial aleatoria
//        List<Individuo> pop = new ArrayList<>(Config.POP);
//        for (int i = 0; i < Config.POP; i++) {
//            Individuo ind = Individuo.aleatorio(rnd);
//            ind.avaliar(problema);
//            pop.add(ind);
//        }
//
//        Individuo melhor = melhorDe(pop);
//        if (melhor.fitness == 0) {
//            return new Resultado(true, 0, 0, melhor.genes.clone());
//        }
//
//        for (int g = 1; g <= Config.GERACOES; g++) {
//            List<Individuo> filhos = new ArrayList<>(Config.POP);
//            while (filhos.size() < Config.POP) {
//                Individuo p1 = Selecao.selecionar(pop, cfg);
//                Individuo p2 = Selecao.selecionar(pop, cfg);
//
//                Individuo f1;
//                Individuo f2;
//                if (rnd.nextDouble() < cfg.taxaCrossover()) {
////                    System.out.println("Cruzando");
//                    Individuo[] f = Crossover.cruzar(p1, p2, cfg);
//                    f1 = f[0];
//                    f2 = f[1];
//                } else {
//                    f1 = p1.copia();
//                    f2 = p2.copia();
//                }
//
//                if (rnd.nextDouble() < cfg.taxaMutacao()) {
//                    Mutacao.mutacaoPermutacao(f1);
//                }
//                if (rnd.nextDouble() < cfg.taxaMutacao()) {
//                    Mutacao.mutacaoPermutacao(f2);
//                }
//
//                f1.avaliar(problema);
//                filhos.add(f1);
////                if (filhos.size() < Config.POP) {
//                    f2.avaliar(problema);
//                    filhos.add(f2);
////                }
//            }
//
//            pop = Reinsercao.reinserir(pop, filhos, cfg);
//
//            Individuo melhorGeracao = melhorDe(pop);
//            if (melhorGeracao.fitness < melhor.fitness) {
//                melhor = melhorGeracao;
//            }
//            if (melhor.fitness == 0) {
//                return new Resultado(true, g, 0, melhor.genes.clone());
//            }
////            System.out.println(melhor.fitness);
//        }
//        return new Resultado(false, Config.GERACOES, melhor.fitness, melhor.genes.clone());
//    }

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
