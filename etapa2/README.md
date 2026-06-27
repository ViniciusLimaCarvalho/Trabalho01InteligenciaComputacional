# Etapa 2 — Refinamento (parâmetros numéricos)

Partindo da **melhor configuração da Etapa 1** (`TM2-S1-C2-R1`: mutação swap 20%, torneio
tour=3, PMX, reinserção ordenada, população 100, 50 gerações), a Etapa 2 refina o AG
explorando os **eixos numéricos**: tamanho de população, número de gerações, taxa de
mutação, taxa de crossover e tamanho do torneio. Os operadores de seleção (torneio),
crossover (PMX) e mutação (swap) são mantidos fixos; as combinações fortes (V15–V18)
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

## 18 variações (≥10 exigidas)

Eixos isolados (V1–V12), combinações dos ajustes baratos (V13–V14) e combinações fortes
com **reinserção elitista** (V15–V18). Os rótulos são gerados automaticamente a partir
dos valores reais de cada `Config`, mostrando só o que difere do baseline.

## Teto de tempo

Medido no próprio run: roda-se o baseline (com warmup da JVM) e define-se
`cap = 1.5 × tempoBaseline`. Cada variação é marcada como dentro/fora do teto.

## Resultados (1000 execuções por configuração)

Baseline: **64,0%** em **0,4808 ms** → teto (+50%) = **0,7212 ms**.

| Variação                    | Conv.(%) | Tempo(ms) | ≤ teto |
|-----------------------------|---------:|----------:|:------:|
| V0 baseline                 |     64,0 |    0,4808 |  ref   |
| V1 pop=150                  |     74,0 |    0,4654 |  sim   |
| V2 pop=200                  |     82,3 |    0,5193 |  sim   |
| V3 ger=75                   |     64,5 |    0,4677 |  sim   |
| V4 ger=100                  |     60,1 |    0,6168 |  sim   |
| V5 mut=10%                  |     55,9 |    0,3813 |  sim   |
| V6 mut=30%                  |     71,3 |    0,3365 |  sim   |
| V7 mut=40%                  |     72,3 |    0,3379 |  sim   |
| V8 cx=70%                   |     67,2 |    0,3528 |  sim   |
| V9 cx=80%                   |     69,9 |    0,3524 |  sim   |
| V10 cx=90%                  |     72,5 |    0,3417 |  sim   |
| V11 tour=2                  |     72,2 |    0,3401 |  sim   |
| V12 tour=5                  |     55,8 |    0,3913 |  sim   |
| V13 mut=30% cx=80%          |     72,7 |    0,3418 |  sim   |
| V14 mut=30% cx=80% tour=5   |     70,2 |    0,3383 |  sim   |
| V15 mut=40% cx=80% elit     |     79,9 |    0,3310 |  sim   |
| V16 mut=40% cx=80% elit     |     78,2 |    0,3340 |  sim   |
| V17 mut=50% cx=80% elit     |     83,3 |    0,3176 |  sim   |
| **V18 mut=100% cx=30% elit**| **97,9** |    0,2077 |  sim   |

**Melhor variação: V18 — reinserção elitista + mutação 100% (cx=30%)** → **97,9%** de
convergência (ganho de **+33,9 p.p.** sobre o baseline) em **0,2077 ms** — também a
**mais rápida** de todas (~⅓ do tempo do baseline), bem dentro do teto.

### Observações

- **Reinserção com elitismo é o grande enabler.** Garantir a sobrevivência dos melhores
  permite **disparar a mutação ao máximo** (exploração agressiva) sem perder progresso.
  É o que separa V15–V18 (79–98%) dos eixos isolados (~55–82%).
- **Com elitismo, mutação altíssima vence.** V17 (mut=50%) → 83,3% e V18 (mut=100%) →
  97,9%. Sem elitismo, mutação alta seria destrutiva; com elitismo, vira busca paralela
  intensiva que converge **cedo** — por isso V18 é também a mais barata (0,21 ms).
- **População ajuda, mas é cara.** pop=150 → 74,0% e pop=200 → 82,3%: mais diversidade
  reduz a convergência prematura, porém custa tempo. O elitismo (V18) alcança convergência
  bem maior **sem** aumentar a população.
- **Mais gerações não ajuda.** ger=100 cai para 60,1% gastando o dobro do tempo: se o AG
  não converge em ~50 gerações, estagnou em ótimo local; iterar mais só gasta tempo.
- **Pressão seletiva alta prejudica.** tour=5 → 55,8% (converge rápido demais para ótimo
  local); tour=2 → 72,2% (mais diversidade). Mutação baixa (10%) também é ruim (55,9%).

> **Notas:**
> - V15 e V16 são a **mesma** configuração (`mut=40% cx=80% elit`); a diferença (79,9% vs
>   78,2%) é só variância de sementes entre duas amostras de 1000 execuções.
> - O baseline mede 64,0% (vs. 66,7% na Etapa 1) por usar sementes aleatórias diferentes —
>   variação normal de Monte Carlo; não altera as conclusões comparativas.

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
(`Selecao`, `Crossover`, `Mutacao`, `Reinsercao`) são os mesmos da melhor config da Etapa 1.
