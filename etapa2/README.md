# Etapa 2 — Refinamento (mutação criativa)

Partindo da **melhor configuração da Etapa 1** (`TM2-S1-C2-R1`: mutação 20%, torneio
tour=3, PMX, reinserção ordenada, população 100, 50 gerações), a Etapa 2 refina o AG
explorando o eixo de **mutação criativa**: novos operadores de mutação e diferentes
taxas, mantendo todos os demais parâmetros fixos.

## Por que mutação criativa

Mutação é uma operação **O(1)** sobre o cromossomo — trocar o operador (swap → inversão,
scramble, multi-swap, inserção, híbrida) **não altera o custo de tempo** da execução.
Logo, todas as variações respeitam o teto de **+50%** de tempo por construção, e o ganho
buscado é puramente em **taxa de convergência**.

## Operadores de mutação testados

Todos preservam a validade da permutação de {0..9} (sem dígitos repetidos):

| Operador     | Descrição                                                   |
|--------------|-------------------------------------------------------------|
| `SWAP`       | Troca 2 posições (operador da Etapa 1, baseline)            |
| `INVERSAO`   | Inverte a ordem de um segmento `[a,b]`                      |
| `SCRAMBLE`   | Embaralha (Fisher-Yates) os elementos de um segmento `[a,b]`|
| `MULTI_SWAP` | Aplica 2 trocas independentes                               |
| `INSERCAO`   | Remove um gene e o reinsere em outra posição (deslocamento) |
| `HIBRIDA`    | A cada mutação, escolhe aleatoriamente entre swap e inversão|

## 11 variações (≥10 exigidas)

Operadores @20% (mesma taxa do baseline) e os promissores @30%/@40%:

V1 Inversão@20, V2 Scramble@20, V3 Multi-swap@20, V4 Inserção@20, V5 Híbrida@20,
V6 Inversão@30, V7 Scramble@30, V8 Multi-swap@30, V9 Inserção@30, V10 Híbrida@30,
V11 Inversão@40.

## Teto de tempo

Medido no próprio run: roda-se o baseline (com warmup da JVM) e define-se
`cap = 1.5 × tempoBaseline`. Cada variação é marcada como dentro/fora do teto.

## Resultados (1000 execuções por configuração)

| Variação                | Conv.(%) | Tempo(ms) | ≤ teto |
|-------------------------|---------:|----------:|:------:|
| V0 Baseline (swap @20%) |     64,0 |    0,4536 |  ref   |
| V1 Inversão @20%        |     33,9 |    0,5289 |  sim   |
| V2 Scramble @20%        |     41,2 |    0,5175 |  sim   |
| **V3 Multi-swap @20%**  | **74,6** |    0,4113 |  sim   |
| V4 Inserção @20%        |     35,1 |    0,5347 |  sim   |
| V5 Híbrida @20%         |     62,0 |    0,3881 |  sim   |
| V6 Inversão @30%        |     37,1 |    0,5236 |  sim   |
| V7 Scramble @30%        |     41,6 |    0,5297 |  sim   |
| **V8 Multi-swap @30%**  | **83,7** |    0,3710 |  sim   |
| V9 Inserção @30%        |     34,9 |    0,5539 |  sim   |
| V10 Híbrida @30%        |     67,1 |    0,3798 |  sim   |
| V11 Inversão @40%       |     41,5 |    0,5364 |  sim   |

**Melhor variação: V8 — Multi-swap @30%** → **83,7%** de convergência (ganho de
**+19,7 p.p.** sobre o baseline) em **0,3710 ms** (até *mais rápido* que o baseline,
pois converge em menos gerações).

### Observações

- **Multi-swap é o grande vencedor.** Duas trocas por mutação dão um salto maior no
  espaço de busca sem destruir a estrutura herdada pelo PMX, escapando de ótimos locais.
- **Operadores de segmento (inversão, scramble, inserção) prejudicam.** São disruptivos
  demais: reorganizam blocos inteiros da permutação, quebrando os bons sub-padrões que o
  PMX preserva — a convergência cai para ~33–41%.
- **Taxa maior ajuda o operador certo.** Multi-swap melhora de 74,6% (@20%) para 83,7%
  (@30%); a híbrida também sobe (62→67%).
- Todas as 11 variações respeitam o teto de +50% (mutação é O(1)).

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
estáticas, permitindo que cada variação ajuste qualquer parâmetro. `Mutacao.mutar`
despacha para o operador escolhido em `cfg.tipoMutacao`.
