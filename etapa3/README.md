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
| V0 base (global)          | 98,2 | 30,7 |   2,2 | 17,3 |    9,4 |  **31,6** |    0,6731 |  ref   |
| V1 posicional             | 82,7 | 17,0 |   6,7 | 46,2 |   22,1 |  **34,9** |    0,7272 |  sim   |
| **V2 zero-esq**           | 95,5 | 98,1 |   3,5 | 25,6 |    9,7 |  **46,5** |    0,6693 |  sim   |
| V3 posicional+zero-esq    | 23,0 | 33,3 |   7,7 | 49,7 |   16,2 |  **26,0** |    0,8365 |  sim   |
| V4 pop=200                |100,0 | 48,6 |   2,3 | 30,1 |   16,2 |  **39,4** |    1,3033 |  NÃO   |
| V5 ger=100                | 99,4 | 32,2 |   2,1 | 26,3 |   15,6 |  **35,1** |    1,2429 |  NÃO   |
| V6 posicional+pop=150     | 91,3 | 20,7 |   9,7 | 55,7 |   32,9 |  **42,1** |    1,0469 |  NÃO   |

Teto de tempo = 1,5 × tempo médio do baseline = **1,0097 ms**.

**Melhor variação dentro do teto: V2 — restrição de zero à esquerda → 46,5% média
(+15,1 p.p.)**, e ainda entrega as soluções *válidas* de verdade.

## Observações

- **A campeã da Etapa 2 NÃO generaliza.** Otimizada para SEND+MORE (98,2%), desaba nos
  demais (CROSS 2,2%, DONALD 9,4%), com média de apenas **31,6%**. Os parâmetros
  agressivos (mutação 100%) foram **superajustados** a um único problema de 8 letras.
- **Zero à esquerda guia a busca (V2).** Além de corrigir a contagem (SEND cai de 98,2%
  para 95,5% — a convergência *real* à solução única válida), **EAT salta de 30,7% para
  98,1%**: forçar a primeira letra ≠ 0 canaliza o "vai-um" e elimina becos sem saída.
- **Fitness posicional (V1) ajuda os problemas difíceis e atrapalha os fáceis.** COCA
  17,3→46,2 e DONALD 9,4→22,1 (gradiente suave guia quem está "quase lá" nos números
  grandes), mas SEND 98,2→82,7 e EAT 30,7→17,0 pioram. É um trade-off, não um ganho geral.
- **Posicional + zero-à-esquerda combinam mal (V3).** A penalidade (10⁸) ofusca o erro
  posicional (máx. ~54), achatando o gradiente — SEND desaba para 23,0%. Escalas
  incompatíveis.
- **Mais recursos ajudam os difíceis, mas estouram o teto (V4–V6).** pop/gerações maiores
  elevam CROSS/DONALD/COCA, porém custam ~2× o tempo: ficam **fora** do limite de +50%.
- **CROSS (9 letras) e DONALD (10 letras) continuam baixos em todas as variações** — o
  espaço de busca (até 10! permutações) é grande demais para pop=100/50 gerações; exigiriam
  orçamento de tempo bem maior.

> **Reprodutibilidade:** após integrar o crossover da branch `mpgp`, os operadores CX/PMX
> usam `new Random()` interno (não o gerador semeado), então cada execução produz números
> ligeiramente diferentes. As tabelas acima são um **snapshot representativo**; as
> conclusões comparativas se mantêm.

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
