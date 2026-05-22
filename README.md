📦 Brots — Sistema de Estoque, Validade e Produção

O Brots nasceu de uma experiência real. Durante um período em que tive um café chamado “Brots”, enfrentei dificuldades operacionais que apareciam todos os dias:

* controle de estoque,
* validade após manipulação,
* organização de compras,
* etiquetas,
* rastreabilidade,
* principalmente precificação dos produtos.

Grande parte desses processos era feita manualmente ou de forma improvisada, o que gerava perda de insumos, inconsistência nas compras e dificuldade para entender o custo real da operação. Esse projeto surgiu justamente da vontade de transformar essas dores em um sistema funcional.

**Além de estudar Java, a ideia também foi pensar o software como produto: entender problemas reais, organizar fluxos e construir soluções que fizessem sentido para uma operação de verdade.**

## 🚀 Tecnologias e Estrutura --

* Java (CLI) — aplicação feita via terminal
* Programação Orientada a Objetos (POO) — modelagem de entidades como produtos, lotes, movimentações e fichas técnicas
* Persistência em arquivos (.csv e .txt) — armazenamento local sem depender de banco de dados externo
* Estrutura modular focada em separar responsabilidades conforme o projeto evolui

## ⚙️ O que o sistema faz --

*📦 Controle de estoque e lotes*

- Cadastro de produtos e insumos
- Controle de entrada e saída
- Geração automática de IDs internos por lote
- Rastreabilidade de movimentações
- Controle de validade tradicional + validade após abertura

** A funcionalidade de validade após abertura surgiu porque muitos produtos continuam “válidos” pela data do fabricante, mas perdem qualidade, segurança ou estabilidade poucos dias após serem manipulados ou abertos. Além de impactar diretamente o controle interno da operação, esse também é um ponto importante dentro das normas de manipulação de alimentos e das verificações realizadas pela vigilância sanitária.

*🍳 Fichas técnicas e precificação*

O sistema permite montar receitas utilizando os itens cadastrados no estoque, facilitando o controle de custos da operação e o acompanhamento do valor real de produção de cada produto. Isso ajuda na precificação, na organização da cozinha e na tomada de decisão do dia a dia.

Com isso, o sistema calcula automaticamente:

- custo total da produção
- custo fracionado dos ingredientes
- custo por unidade/rendimento

A lógica desse módulo foi pensada para dar mais flexibilidade na operação e facilitar testes e tomadas de decisão no dia a dia. Com os custos organizados, fica mais fácil analisar mudanças de preço dos insumos, testar novas combinações e receitas, entender o impacto de cada alteração no custo final do produto e até identificar oportunidades de otimização dentro da produção.

*🛒 Gestão de compras*

- Geração de listas de compra
- Controle de estoque mínimo
- Check de recebimento
- Entrada automática dos itens no estoque após conferência

O módulo de compras foi pensado para deixar o processo mais organizado e prático dentro da operação. A ideia é facilitar a visualização do que realmente precisa ser comprado, acompanhar o que já chegou dos fornecedores e identificar itens pendentes ou em falta através do sistema de check no recebimento.

Além do controle interno, também existe a exportação automática das listas para WhatsApp. Essa funcionalidade surgiu porque, na prática, grande parte das compras em pequenos negócios ainda acontece diretamente pelo WhatsApp com fornecedores. Em vez de montar mensagens manualmente, o sistema já gera tudo formatado e organizado, agilizando tanto o envio dos pedidos quanto o trabalho da pessoa responsável pelo orçamento e separação dos produtos.

*🏷️ Etiquetas e manipulação*

O sistema gera etiquetas de manipulação para produtos fracionados, calculando automaticamente:

- nova validade: calculada automaticamente com base nas regras definidas no cadastro original do produto, seguindo os padrões de manipulação e conservação previamente estabelecidos pela operação, nutricionista ou responsável técnico.
- responsável
- histórico da manipulação
  
*🧠 Visão de Produto*

Uma das partes mais interessantes desse projeto foi pensar menos em “fazer código” e mais em:

* como a operação funciona,
* onde existem gargalos e quais informações realmente ajudam na tomada de decisão.

Boa parte das funcionalidades nasceu observando problemas reais:

* perda de insumos,
* dificuldade em calcular custo real,
* falta de rastreabilidade,
* compras desorganizadas,
* precificação inconsistente.

## 🏗️ Aprendizados e próximos passos --

Como esse é meu primeiro projeto "grande" em Java, algumas partes ainda estão em evolução. No início, a prioridade foi validar as regras de negócio e fazer o sistema funcionar de ponta a ponta. Agora o foco está em melhorar arquitetura e organização do código.

Os próximos passos são:

* separar melhor responsabilidades da Main.java
* aplicar conceitos próximos de MVC
* criar classes utilitárias para validação e input
* melhorar desacoplamento entre módulos
* futuramente migrar persistência de arquivos para banco de dados relacional
  
## 💡 Sobre o desenvolvimento --

Durante o projeto, usei IA como apoio para:

* entender conceitos de Java,
* estudar arquitetura,
* validar ideias,
* explorar possibilidades de implementação.

**Mas toda a modelagem do sistema, regras de negócio, estrutura das funcionalidades e evolução do projeto foram desenvolvidas como parte do meu processo de aprendizado.**

## 🛠️ Como executar --

1. Clone o repositório git clone https://github.com/jaoqzs/Brots-Estoque.git
2. Abra o projeto na IDE de sua preferência: IntelliJ IDEA, VS Code, Eclipse...
3. Execute o arquivo Main.java
** Os arquivos .csv e .txt serão criados automaticamente conforme os primeiros dados forem cadastrados ou você pode usar os de exemplos.
