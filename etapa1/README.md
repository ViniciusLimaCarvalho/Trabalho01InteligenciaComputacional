# Etapa 1 — AG para SEND + MORE = MONEY

Algoritmo Genético que avalia as **16 combinações** dos eixos de variação
(TM × S × C × R), cada uma executada **1000 vezes**, medindo taxa de convergência
e tempo médio de execução.

## Eixos de variação

| Código | Parâmetro   | Opção 1                          | Opção 2                                   |
|--------|-------------|----------------------------------|-------------------------------------------|
| TM     | Mutação     | TM1: 10%                         | TM2: 20%                                  |
| S      | Seleção     | S1: Torneio (tour=3)             | S2: Roleta                                |
| C      | Crossover   | C1: Cíclico (CX)                 | C2: PMX                                   |
| R      | Reinserção  | R1: Ordenada (pais+filhos)       | R2: Pura c/ elitismo 20% (crossover 80%)  |

Parâmetros fixos: população 100, 50 gerações, mutação por troca de 2 posições, crossover 60% (80% em R2).

## Compilar e executar

A partir da pasta `Trabalho 1 Inteligencia Computacional` (raiz do projeto):

```bash
javac -d etapa1/out etapa1/src/cripto/*.java etapa1/src/cripto/operadores/*.java
java -cp etapa1/out cripto.Experimento
```

A tabela é impressa no console e o CSV é gravado em `etapa1/resultados/etapa1.csv`.

## Estrutura

- `cripto.Problema` — parse do problema e função de avaliação `|(SEND+MORE) − MONEY|` (genérico para etapas 2 e 3).
- `cripto.Individuo` — cromossomo como permutação de {0..9}.
- `cripto.Config` — parâmetros de cada variação.
- `cripto.AG` — laço evolutivo parametrizado.
- `cripto.operadores.*` — Seleção (torneio/roleta), Crossover (CX/PMX), Mutação (swap), Reinserção (ordenada/elitismo).
- `cripto.Experimento` — runner das 16 configurações.
