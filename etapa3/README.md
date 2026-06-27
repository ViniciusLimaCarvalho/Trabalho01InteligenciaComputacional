# Etapa 3 — Generalização (5 problemas)

Avalia a **melhor configuração da Etapa 2** (reinserção elitista, mutação swap 100%,
crossover 30%, população 100, 50 gerações, torneio tour=3, PMX) nos **5 problemas** de
criptoaritmetica, 1000 execuções por problema, reportando convergência e tempo **médios**.
Em seguida testa **6 variações** (≥5 exigidas), incluindo obrigatoriamente a **fitness por
erro posicional**.

## Os 5 problemas

| Problema                  | Letras | Solução única             |
|---------------------------|:------:|---------------------------|
| SEND + MORE = MONEY       |   8    | 9567 + 1085 = 10652       |
| EAT + THAT = APPLE        |   6    | 819 + 9219 = 10038        |
| CROSS + ROADS = DANGER    |   9    | 96233 + 62513 = 158746    |
| COCA + COLA = OASIS       |   6    | 8186 + 8106 = 16292       |
| DONALD + GERALD = ROBERT  |  10    | 526485 + 197485 = 723970  |

Todos têm **exatamente uma** solução válida (verificado por força bruta). Apenas
SEND+MORE=MONEY admite 25 atribuições que zeram a diferença aritmética se for permitido
**zero à esquerda** (só 1 é válida); os outros 4 têm solução única de qualquer forma.

## Funções de fitness

- **GLOBAL** (Etapa 1/2): `| (p1 + p2) − resultado |`.
- **POSICIONAL** (exigida): soma dos erros **dígito a dígito** entre `p1+p2` e `resultado`.
  Gradiente mais suave — acertar cada casa decimal reduz o erro, em vez de depender da
  diferença absoluta global.

Opcionalmente, a restrição de **zero à esquerda** penaliza individuos cuja primeira letra
de qualquer palavra seja 0. Ambas as funções valem 0 ⇔ a soma bate (e, se exigido, sem
zero à esquerda) → a definição de convergência permanece comparável entre as variações.

## Resultados (convergência %, 1000 execuções por problema)

| Variação                  | SEND | EAT  | CROSS | COCA | DONALD | **Média** | Tempo(ms) | ≤ teto |
|---------------------------|-----:|-----:|------:|-----:|-------:|----------:|----------:|:------:|
| V0 base (global)          | 98,6 | 32,1 |   2,3 | 16,7 |    7,8 |  **31,5** |    0,6734 |  ref   |
| V1 posicional             | 80,9 | 16,7 |   7,6 | 48,8 |   20,6 |  **34,9** |    0,7329 |  sim   |
| **V2 zero-esq**           | 92,8 | 97,8 |   4,7 | 24,3 |   10,6 |  **46,0** |    0,6905 |  sim   |
| V3 posicional+zero-esq    | 24,8 | 36,2 |   9,3 | 50,2 |   18,1 |  **27,7** |    0,8291 |  sim   |
| V4 pop=200                |100,0 | 47,1 |   2,7 | 31,1 |   18,6 |  **39,9** |    1,3192 |  NÃO   |
| V5 ger=100                | 99,9 | 32,6 |   1,7 | 27,7 |   15,1 |  **35,4** |    1,2512 |  NÃO   |
| V6 posicional+pop=150     | 90,8 | 20,8 |   8,2 | 56,6 |   31,4 |  **41,6** |    1,0527 |  NÃO   |

Teto de tempo = 1,5 × tempo médio do baseline = **1,0101 ms**.

**Melhor variação dentro do teto: V2 — restrição de zero à esquerda → 46,0% média
(+14,5 p.p.)**, e ainda entrega as soluções *válidas* de verdade.

## Observações

- **A campeã da Etapa 2 NÃO generaliza.** Otimizada para SEND+MORE (98,6%), desaba nos
  demais (CROSS 2,3%, DONALD 7,8%), com média de apenas **31,5%**. Os parâmetros
  agressivos (mutação 100%) foram **superajustados** a um único problema de 8 letras.
- **Zero à esquerda guia a busca (V2).** Além de corrigir a contagem (SEND cai de 98,6%
  para 92,8% — a convergência *real* à solução única válida), **EAT salta de 32,1% para
  97,8%**: forçar a primeira letra ≠ 0 canaliza o "vai-um" e elimina becos sem saída.
- **Fitness posicional (V1) ajuda os problemas difíceis e atrapalha os fáceis.** COCA
  16,7→48,8 e DONALD 7,8→20,6 (gradiente suave guia quem está "quase lá" nos números
  grandes), mas SEND 98,6→80,9 e EAT 32,1→16,7 pioram. É um trade-off, não um ganho geral.
- **Posicional + zero-à-esquerda combinam mal (V3).** A penalidade (10⁸) ofusca o erro
  posicional (máx. ~54), achatando o gradiente — SEND desaba para 24,8%. Escalas
  incompatíveis.
- **Mais recursos ajudam os difíceis, mas estouram o teto (V4–V6).** pop/gerações maiores
  elevam CROSS/DONALD/COCA, porém custam ~2× o tempo: ficam **fora** do limite de +50%.
- **CROSS (9 letras) e DONALD (10 letras) continuam baixos em todas as variações** — o
  espaço de busca (até 10! permutações) é grande demais para pop=100/50 gerações; exigiriam
  orçamento de tempo bem maior.

## Compilar e executar

A partir da raiz do projeto:

```bash
javac -d etapa3/out etapa3/src/cripto/*.java etapa3/src/cripto/operadores/*.java
java -cp etapa3/out cripto.Experimento3
```

A tabela é impressa no console e o CSV é gravado em `etapa3/resultados/etapa3.csv`
(com `Locale.US` para evitar conflito entre vírgula decimal e separador de campos).

## Estrutura

Código auto-contido (cópia refatorada da Etapa 2). Diferença principal: `Problema` ganha
o eixo de **fitness** (`GLOBAL`/`POSICIONAL`) e a restrição opcional de **zero à esquerda**,
mantendo-se genérico para qualquer um dos 5 problemas. `Experimento3` varre os 5 problemas
× 7 variações (baseline + 6) e reporta convergência e tempo médios.
