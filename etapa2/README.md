# Etapa 2 — Refinamento (parâmetros numéricos)

Partindo da **melhor configuração da Etapa 1** (`TM2-S1-C2-R1`: mutação swap 20%, torneio
tour=3, PMX, reinserção ordenada, população 100, 50 gerações), a Etapa 2 refina o AG
explorando os **eixos numéricos**: tamanho de população, número de gerações, taxa de
mutação, taxa de crossover e tamanho do torneio. Os operadores de seleção (torneio),
crossover (PMX) e mutação (swap) são mantidos fixos; as combinações fortes (V15–V19)
também trocam a **reinserção** para a elitista (R2), pois é ela que viabiliza as taxas de
mutação altas.

## Eixos e custo de tempo

| Eixo                | Variações testadas | Custo de tempo                                  |
|---------------------|--------------------|-------------------------------------------------|
| População           | 150, 200           | **Alto** (~linear no nº de indivíduos)          |
| Gerações            | 75, 100            | **Médio** (só pesa nos runs que não convergem)  |
| Taxa de mutação     | 10%, 30%, 40%      | ~zero (mutação é O(1))                           |
| Taxa de crossover   | 70%, 80%, 90%      | ~zero                                            |
| Tamanho do torneio  | 2, 5               | ~zero                                            |
| Combinações baratas | mut+cx, mut+cx+tour| ~zero                                            |
| Combinações fortes  | elitismo+mut alta  | ~zero (convergem cedo)                           |

## 19 variações (≥10 exigidas)

Eixos isolados (V1–V12), combinações dos ajustes baratos (V13–V14) e combinações fortes
com **reinserção elitista** (V15–V19). Os rótulos são gerados automaticamente a partir
dos valores reais de cada `Config`, mostrando só o que difere do baseline.

## Teto de tempo

Medido no próprio run: roda-se o baseline (com warmup da JVM) e define-se
`cap = 1.5 × tempoBaseline`. Cada variação é marcada como dentro/fora do teto.

## Resultados (1000 execuções por configuração)

Baseline: **64,6%** em **0,4370 ms** → teto (+50%) = **0,6555 ms**.

| Variação                          | Conv.(%) | Tempo(ms) | ≤ teto |
|-----------------------------------|---------:|----------:|:------:|
| V0 baseline                       |     64,6 |    0,4370 |  ref   |
| V1 pop=150                        |     72,7 |    0,4790 |  sim   |
| V2 pop=200                        |     82,0 |    0,5098 |  sim   |
| V3 ger=75                         |     65,4 |    0,4440 |  sim   |
| V4 ger=100                        |     63,8 |    0,5596 |  sim   |
| V5 mut=10%                        |     55,9 |    0,3711 |  sim   |
| V6 mut=30%                        |     66,2 |    0,3471 |  sim   |
| V7 mut=40%                        |     73,8 |    0,3207 |  sim   |
| V8 cx=70%                         |     69,1 |    0,3372 |  sim   |
| V9 cx=80%                         |     69,3 |    0,3401 |  sim   |
| V10 cx=90%                        |     71,3 |    0,3359 |  sim   |
| V11 tour=2                        |     68,5 |    0,3499 |  sim   |
| V12 tour=5                        |     59,9 |    0,3647 |  sim   |
| V13 mut=30% cx=80%                |     73,8 |    0,3277 |  sim   |
| V14 mut=30% cx=80% tour=5         |     65,9 |    0,3651 |  sim   |
| V15 mut=40% cx=80% elit           |     81,9 |    0,3289 |  sim   |
| V16 mut=40% cx=80% elit           |     79,2 |    0,3143 |  sim   |
| V17 mut=50% cx=80% elit           |     87,1 |    0,2838 |  sim   |
| V18 mut=100% cx=30% elit          |     98,1 |    0,1982 |  sim   |
| **V19 pop=150 mut=100% cx=0% elit**| **99,8** |   0,1904 |  sim   |

**Melhor variação: V19 — população 150 + reinserção elitista + mutação 100% + crossover 0%**
→ **99,8%** de convergência (ganho de **+35,2 p.p.** sobre o baseline) em **0,1904 ms** —
também a **mais rápida** de todas (~⅖ do tempo do baseline), bem dentro do teto.

### Observações

- **Reinserção com elitismo é o grande enabler.** Garantir a sobrevivência dos melhores
  permite **disparar a mutação ao máximo** (exploração agressiva) sem perder progresso.
  É o que separa V15–V19 (79–100%) dos eixos isolados (~56–82%).
- **Com elitismo, mutação altíssima vence.** V17 (mut=50%) → 87,1% e V18 (mut=100%) →
  98,1%. Sem elitismo, mutação alta seria destrutiva; com elitismo, vira busca paralela
  intensiva que converge **cedo** — por isso essas variações são também as mais baratas.
- **A melhor config dispensa o crossover.** V19 zera o crossover (cx=0%) e fica só com
  mutação 100% + elitismo + pop=150: vira uma busca tipo *hill-climbing* paralela com
  elite. Chega a **99,8%** e é a mais rápida — para este problema, recombinar permutações
  ajuda menos que perturbar e preservar os melhores.
- **População ajuda, mas é cara isoladamente.** pop=150 → 72,7% e pop=200 → 82,0%: mais
  diversidade reduz a convergência prematura, porém custa tempo. Combinada com elitismo +
  mutação alta (V19), a pop=150 rende quase 100% **sem** estourar o teto.
- **Mais gerações não ajuda.** ger=100 → 63,8% gastando ~1,3× o tempo: se o AG não converge
  em ~50 gerações, estagnou em ótimo local; iterar mais só gasta tempo.
- **Pressão seletiva alta prejudica.** tour=5 → 59,9% (converge rápido demais para ótimo
  local); tour=2 → 68,5% (mais diversidade). Mutação baixa (10%) também é ruim (55,9%).

> **Notas:**
> - V15 e V16 são a **mesma** configuração (`mut=40% cx=80% elit`); a diferença (81,9% vs
>   79,2%) é só variância de execução entre duas amostras de 1000 rodadas.
> - **Reprodutibilidade:** após integrar o crossover da branch `mpgp`, os operadores CX/PMX
>   usam `new Random()` interno (não o gerador semeado), então cada execução do experimento
>   produz números ligeiramente diferentes. As tabelas acima são um **snapshot
>   representativo**; as conclusões comparativas se mantêm.

## Compilar e executar

A partir da raiz do projeto:

```bash
javac -d etapa2/out etapa2/src/cripto/*.java etapa2/src/cripto/operadores/*.java
java -cp etapa2/out cripto.Experimento2
```

A tabela é impressa no console e o CSV é gravado em `etapa2/resultados/etapa2.csv`.

## Estrutura

Código auto-contido (cópia refatorada da Etapa 1). Principal diferença: `Config` agora
usa **campos de instância** (via `Config.baseline()` + builder) em vez de constantes
estáticas, permitindo que cada variação ajuste qualquer parâmetro numérico. Os operadores
(`Selecao`, `Crossover`, `Mutacao`, `Reinsercao`) vêm da Etapa 1 (com o Crossover/AG
atualizados a partir da branch `mpgp`).
