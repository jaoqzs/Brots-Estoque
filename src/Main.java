// Arquivo: Main.java
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    static ArrayList<Produto> estoque = new ArrayList<>();
    static ArrayList<String> categoriasValidas = new ArrayList<>();
    static ArrayList<String> fornecedoresValidos = new ArrayList<>();

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        GerenciadorDeArquivo gerenciador = new GerenciadorDeArquivo();

        categoriasValidas = gerenciador.carregarListaSimples("categorias.txt");
        fornecedoresValidos = gerenciador.carregarListaSimples("fornecedores.txt");

        System.out.println("Iniciando o sistema...");
        estoque = gerenciador.carregarEstoque();
        System.out.println("✅ " + estoque.size() + " produtos carregados do banco de dados!");

        GestorDeCompras.listasDeCompra = gerenciador.carregarListasCompra(estoque);

        GestorFichasTecnicas.categoriasFicha = gerenciador.carregarListaSimples("categorias_ficha.txt");
        GestorFichasTecnicas.fichasCadastradas = gerenciador.carregarFichasTecnicas(estoque);

        boolean executando = true;

        while (executando) {
            System.out.println("\n--- BEM-VINDO AO BROTS: MENU PRINCIPAL ---");
            System.out.println("1. Adicionar Produto");
            System.out.println("2. Listar Produtos");
            System.out.println("3. Gerenciar Categorias e Fornecedores");
            System.out.println("4. Central de Etiquetas (Manipulação)");
            // 🌟 A NOVA OPÇÃO AQUI:
            System.out.println("5. Gestão de Compras");
            System.out.println("6. Fichas Técnicas");
            System.out.println("7. Sair");

            int opcao = lerInteiroSeguro(teclado, "Escolha uma opção: ");

            if (opcao == 1) {
                System.out.println("\n-- VAMOS CADASTRAR UM NOVO PRODUTO --");

                System.out.print("Tipo (Individual ou Insumo): ");
                String tipo = teclado.nextLine();

                System.out.print("Nome do Produto: ");
                String nome = teclado.nextLine();

                System.out.print("Unidade (ex: kg, g, L, un): ");
                String unidade = teclado.nextLine();

                double estoqueMinimo = lerDoubleSeguro(teclado, "Estoque Mínimo (use vírgula para decimais): ");
                double quantidadeAtual = lerDoubleSeguro(teclado, "Quantidade Atual (primeiro entrada do produto: ");
                System.out.print("Data de Validade (ex: 30/12/2026 ou Enter para pular): ");
                String validadeInicial = teclado.nextLine();
                System.out.print("Código do Lote do Fabricante (Opcional - dê Enter para pular): ");
                String codigoLote = teclado.nextLine();
                double precoPorUnidade = lerDoubleSeguro(teclado, "Preço por Unidade (R$): ");

                String categoria = obterOpcao(teclado, categoriasValidas, "Categoria");
                String fornecedor = obterOpcao(teclado, fornecedoresValidos, "Fornecedor");

                System.out.print("Compõe CMV? (true para Sim, false para Não): ");
                boolean compoeCmv = lerBooleanSeguro(teclado);

                System.out.print("Regra de Preço (Médio ou Último): ");
                String regraPreco = teclado.nextLine();

                // 🌟 MUDANÇA 1: Pergunta quantos dias o produto dura depois de aberto!
                int diasValidadeAberto = lerInteiroSeguro(teclado, "Validade após aberto (em dias). Digite 0 se não se aplica: ");

                // 🌟 Adicionamos o diasValidadeAberto no final para o Produto ser criado corretamente
                Produto novoProduto = new Produto(tipo, nome, unidade, estoqueMinimo,
                        quantidadeAtual, validadeInicial, codigoLote, precoPorUnidade, categoria,
                        fornecedor, compoeCmv, regraPreco, diasValidadeAberto);

                estoque.add(novoProduto);
                gerenciador.salvarEstoque(estoque);

                gerenciador.salvarListaSimples(categoriasValidas, "categorias.txt");
                gerenciador.salvarListaSimples(fornecedoresValidos, "fornecedores.txt");

                System.out.println("✅ Oba! Seu produto foi cadastrado com sucesso :)");

            } else if (opcao == 2) {
                System.out.println("\n--- LISTA DE PRODUTOS ---");
                if (estoque.isEmpty()) {
                    System.out.println("O estoque está vazio :(");
                } else {
                    for (int i = 0; i < estoque.size(); i++) {
                        Produto item = estoque.get(i);
                        System.out.println((i + 1) + " " + item.getNome() + " (Qtd: " + item.getQuantidadeAtual() + ")");
                    }

                    System.out.println("\n0. Voltar ao Menu Principal");
                    int escolha = lerInteiroSeguro(teclado, "Digite o número do produto para acessar: ");

                    if (escolha > 0 && escolha <= estoque.size()) {
                        Produto produtoEscolhido = estoque.get(escolha - 1);
                        acessarProduto(produtoEscolhido, teclado, estoque, gerenciador);
                    } else if (escolha != 0) {
                        System.out.println("❌ Produto não encontrado!");
                    }
                }

            } else if (opcao == 3) {
                boolean gerindo = true;
                while(gerindo) {
                    System.out.println("\n--- GERENCIAR DADOS AUXILIARES ---");
                    System.out.println("1. Categorias");
                    System.out.println("2. Fornecedores");
                    System.out.println("0. Voltar ao Menu Principal");
                    int escolhaGestao = lerInteiroSeguro(teclado, "Escolha uma opção: ");

                    if (escolhaGestao == 1) {
                        gerenciarLista(teclado, categoriasValidas, "Categoria", estoque);
                        gerenciador.salvarListaSimples(categoriasValidas, "categorias.txt");
                    } else if (escolhaGestao == 2) {
                        gerenciarLista(teclado, fornecedoresValidos, "Fornecedor", estoque);
                        gerenciador.salvarListaSimples(fornecedoresValidos, "fornecedores.txt");
                    } else if (escolhaGestao == 0) {
                        gerindo = false;
                    } else {
                        System.out.println("Opção inválida!");
                    }
                }

                // ... (resto do código da opção 3)

            } else if (opcao == 4) {
                // 🌟 CHAMA A NOSSA NOVA FÁBRICA DE ETIQUETAS
                abrirCentralEtiquetas(teclado, estoque, gerenciador);

                // 🌟 O NOVO CAMINHO PARA AS COMPRAS:
            } else if (opcao == 5) {
                GestorDeCompras.abrirMenu(teclado, estoque);

            } else if (opcao == 6) {
                GestorFichasTecnicas.abrirMenu(teclado, estoque);

            } else if (opcao == 7) {
                System.out.println("Salvando e finalizando o Sistema Brots, até logo!");
                gerenciador.salvarEstoque(estoque);
                gerenciador.salvarListaSimples(categoriasValidas, "categorias.txt");
                gerenciador.salvarListaSimples(fornecedoresValidos, "fornecedores.txt");
                gerenciador.salvarListasCompra(GestorDeCompras.listasDeCompra);
                gerenciador.salvarListaSimples(GestorFichasTecnicas.categoriasFicha, "categorias_ficha.txt");
                gerenciador.salvarFichasTecnicas(GestorFichasTecnicas.fichasCadastradas);
                executando = false;
            } else {
                System.out.println("Opção inválida! Digite de 1 a 7");
            }
        }

        teclado.close();
    }

    private static void acessarProduto(Produto produto, Scanner teclado, ArrayList<Produto> estoque, GerenciadorDeArquivo gerenciador) {
        boolean noPainel = true;

        while (noPainel) {
            System.out.println("\n========================================");
            System.out.println("📦 PAINEL DO PRODUTO: " + produto.getNome().toUpperCase());
            System.out.println("========================================");
            System.out.println("Categoria:     " + produto.getCategoria() + " | Fornecedor: " + produto.getFornecedor());
            System.out.println("Estoque Atual: " + produto.getQuantidadeAtual() + " " + produto.getUnidade());
            System.out.println("Estoque Mín:   " + produto.getEstoqueMinimo());
            System.out.println("Preço Unit.:   R$ " + produto.getPrecoPorUnidade());
            System.out.println("Valor Total:   R$ " + produto.getValorTotalEstoque());
            System.out.println("========================================");

            System.out.println("📦 LOTES EM ESTOQUE (Organizados por Validade):");
            if (produto.getLotes().isEmpty()) {
                System.out.println("   Nenhum lote disponível (Estoque Zerado).");
            } else {
                for (int i = 0; i < produto.getLotes().size(); i++) {
                    Lote lote = produto.getLotes().get(i);
                    // 🌟 MUDANÇA 2: Agora imprime também o ID Interno do Lote (a Identidade de rastreabilidade)!
                    System.out.printf("   Lote %d: %.2f %s | Validade: %s | Cód. Fab: %s | ID Brots: %s\n",
                            (i + 1), lote.getQuantidade(), produto.getUnidade(),
                            lote.getDataValidadeFormatada(), lote.getCodigoLote(), lote.getIdInterno());
                }
            }
            System.out.println("========================================");

            System.out.println("📜 EXTRATO DE MOVIMENTAÇÕES:");
            if (produto.getHistorico().isEmpty()) {
                System.out.println("   Nenhuma movimentação registada nesta sessão.");
            } else {
                for (Movimentacao m : produto.getHistorico()) {
                    System.out.println("   " + m.toString());
                }
            }

            System.out.println("========================================");

            System.out.println("1. Movimentar Produto (Entrada/Saída)");
            System.out.println("2. Editar Produto");
            System.out.println("3. Excluir Produto");
            System.out.println("4. Voltar");

            int acao = lerInteiroSeguro(teclado, "O que deseja fazer? ");

            if (acao == 1) {
                System.out.println("\n--- REGISTRAR MOVIMENTAÇÃO ---");
                System.out.println("1. Entrada");
                System.out.println("2. Saída");
                System.out.println("3. Inventário/Balanço");
                int tipoMov = lerInteiroSeguro(teclado, "Escolha o tipo de movimentação: ");

                System.out.print("Data (ex: 25/10/2023): ");
                String data = teclado.nextLine();

                if (tipoMov == 1) {
                    System.out.print("Classificação (Compra, Produção, Transferência): ");
                    String classif = teclado.nextLine();
                    double qtd = lerDoubleSeguro(teclado, "Quantidade a adicionar (" + produto.getUnidade() + "): ");
                    double preco = lerDoubleSeguro(teclado, "Preço Unitário da Entrada (R$): ");

                    System.out.print("Data de Validade (Opcional - dê Enter para pular): ");
                    String validade = teclado.nextLine();
                    System.out.print("Código do Lote do Fabricante (Opcional - dê Enter para pular): ");
                    String codigoLote = teclado.nextLine();
                    System.out.print("Observações (Opcional): ");
                    String obs = teclado.nextLine();

                    // 🌟 MUDANÇA 3: Enviando o nome do produto para o Lote poder gerar a sigla da etiqueta!
                    Lote novoLote = new Lote(produto.getNome(), qtd, validade, codigoLote);
                    Movimentacao mov = new Movimentacao("Entrada", classif, qtd, preco, data, validade, obs);
                    produto.registrarEntrada(novoLote, preco, mov);

                    System.out.println("✅ Entrada registada com sucesso!");

                } else if (tipoMov == 2) {
                    System.out.print("Classificação (Venda, Consumo Interno, Transferência, Desperdício): ");
                    String classif = teclado.nextLine();
                    double qtd = lerDoubleSeguro(teclado, "Quantidade a retirar (" + produto.getUnidade() + "): ");

                    System.out.print("Observações (Opcional): ");
                    String obs = teclado.nextLine();

                    Movimentacao mov = new Movimentacao("Saída", classif, qtd, 0.0, data, "", obs);
                    produto.registrarSaida(qtd, mov);
                    System.out.println("✅ Saída registada com sucesso!");

                } else if (tipoMov == 3) {
                    double qtd = lerDoubleSeguro(teclado, "Nova Quantidade Real no Balanço (" + produto.getUnidade() + "): ");
                    System.out.print("Nova Data de Validade para este saldo (Opcional - Enter para pular): ");
                    String validade = teclado.nextLine();

                    System.out.print("Observações (Opcional): ");
                    String obs = teclado.nextLine();

                    Movimentacao mov = new Movimentacao("Inventário", "Ajuste de Balanço", qtd, 0.0, data, validade, obs);
                    produto.registrarInventario(qtd, validade, mov);
                    System.out.println("✅ Balanço atualizado com sucesso!");

                } else {
                    System.out.println("❌ Tipo de movimentação inválido.");
                }

                gerenciador.salvarEstoque(estoque);

            } else if (acao == 2) {

                System.out.println("\n✏️  --- MODO DE EDIÇÃO ---");
                System.out.println("Dica: Se não quiser mudar uma informação, apenas pressione ENTER para mantê-la.");

                System.out.print("Novo Nome [" + produto.getNome() + "]: ");
                String novoNome = teclado.nextLine();
                if (!novoNome.trim().isEmpty()) {
                    produto.setNome(novoNome);
                }

                System.out.print("Nova Unidade [" + produto.getUnidade() + "]: ");
                String novaUnidade = teclado.nextLine();
                if (!novaUnidade.trim().isEmpty()) {
                    produto.setUnidade(novaUnidade);
                }

                System.out.print("Novo Estoque Mínimo [" + produto.getEstoqueMinimo() + "]: ");
                String novoEstoqueStr = teclado.nextLine();
                if (!novoEstoqueStr.trim().isEmpty()) {
                    try {
                        double novoEstoque = Double.parseDouble(novoEstoqueStr.replace(",", "."));
                        produto.setEstoqueMinimo(novoEstoque);
                    } catch (Exception e) {
                        System.out.println("⚠️ Valor inválido ignorado. O estoque mínimo não foi alterado.");
                    }
                }

                // 🌟 MUDANÇA EXTRA: Permitir edição da validade após aberto
                System.out.print("Nova Validade após aberto em dias [" + produto.getDiasValidadeAberto() + "]: ");
                String novaValidadeAbertaStr = teclado.nextLine();
                if (!novaValidadeAbertaStr.trim().isEmpty()) {
                    try {
                        int novaValidadeAberta = Integer.parseInt(novaValidadeAbertaStr);
                        produto.setDiasValidadeAberto(novaValidadeAberta);
                    } catch (Exception e) {
                        System.out.println("⚠️ Valor inválido ignorado.");
                    }
                }

                System.out.println("Categoria Atual: [" + produto.getCategoria() + "]. Deixe em branco se quiser manter.");
                String novaCat = obterOpcao(teclado, categoriasValidas, "Nova Categoria");
                if (!novaCat.trim().isEmpty()) {
                    produto.setCategoria(novaCat);
                }

                System.out.println("Fornecedor Atual: [" + produto.getFornecedor() + "]. Deixe em branco se quiser manter.");
                String novoForn = obterOpcao(teclado, fornecedoresValidos, "Novo Fornecedor");
                if (!novoForn.trim().isEmpty()) {
                    produto.setFornecedor(novoForn);
                }

                System.out.print("Nova Regra de Preço (Último/Médio) [" + produto.getRegraPreco() + "]: ");
                String novaRegra = teclado.nextLine();
                if (!novaRegra.trim().isEmpty()) {
                    produto.setRegraPreco(novaRegra);
                }

                System.out.println("✅ Produto atualizado com sucesso!");
                gerenciador.salvarEstoque(estoque);

            } else if (acao == 3) {
                System.out.println("\n⚠️  CUIDADO: Esta ação é permanente!");
                System.out.print("Tem certeza que deseja excluir o produto '" + produto.getNome() + "'? (S/N): ");
                String confirma = teclado.nextLine();
                if (confirma.equalsIgnoreCase("S")) {
                    estoque.remove(produto);
                    System.out.println("🗑️ Produto excluído com sucesso!");
                    gerenciador.salvarEstoque(estoque);
                    return;
                } else {
                    System.out.println("❌ Exclusão cancelada.");
                }

            } else if (acao == 4) {
                noPainel = false;
            } else {
                System.out.println("Opção inválida!");
            }
        }
    }

    private static double lerDoubleSeguro(Scanner teclado, String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                double valor = teclado.nextDouble();
                teclado.nextLine();
                return valor;
            } catch (Exception e) {
                System.out.print("❌ Erro: Formato inválido! Lembre-se de usar VÍRGULA e apenas números. Tente novamente.\n");
                teclado.nextLine();
            }
        }
    }

    private static int lerInteiroSeguro(Scanner teclado, String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                int valor = teclado.nextInt();
                teclado.nextLine();
                return valor;
            } catch (Exception e) {
                System.out.print("❌ Erro: Formato inválido! Digite apenas números inteiros. Tente novamente.\n");
                teclado.nextLine();
            }
        }
    }

    private static boolean lerBooleanSeguro(Scanner teclado) {
        while (true) {
            try {
                boolean valor = teclado.nextBoolean();
                teclado.nextLine();
                return valor;
            } catch (Exception e) {
                System.out.print("❌ Erro: Formato inválido! Digite apenas 'true' (sim) ou 'false' (não). Tente novamente.\n");
                teclado.nextLine();
                System.out.print("Compõe CMV? (true para Sim, false para Não): ");
            }
        }
    }

    public static String obterOpcao(Scanner teclado, ArrayList<String> lista, String nomeDoCampo) {
        System.out.println("\n--- Selecione " + nomeDoCampo + " ---");
        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + ". " + lista.get(i));
        }

        System.out.println((lista.size() + 1) + ". [ + Cadastrar Novo(a) ]");
        System.out.println("0. [ Manter atual / Pular ]");
        System.out.print("Sua escolha: ");

        String input = teclado.nextLine();
        if(input.trim().isEmpty() || input.equals("0")) {
            return "";
        }

        int opcao;
        try {
            opcao = Integer.parseInt(input);
        } catch (Exception e) {
            System.out.println("⚠️ Opção inválida. Cadastrando como texto digitado.");
            lista.add(input);
            return input;
        }

        if (opcao > 0 && opcao <= lista.size()) {
            return lista.get(opcao - 1);
        } else {
            System.out.print("Digite o nome do(a) novo(a) " + nomeDoCampo + ": ");
            String novoItem = teclado.nextLine();
            if(!novoItem.trim().isEmpty()) {
                lista.add(novoItem);
            }
            return novoItem;
        }
    }

    private static void gerenciarLista(Scanner teclado, ArrayList<String> lista, String nomeEntidade, ArrayList<Produto> estoque) {
        boolean gerindoLista = true;
        while (gerindoLista) {
            System.out.println("\n--- GESTÃO DE " + nomeEntidade.toUpperCase() + "S ---");
            if (lista.isEmpty()) {
                System.out.println("Nenhum registro encontrado.");
            } else {
                for (int i = 0; i < lista.size(); i++) {
                    System.out.println((i + 1) + ". " + lista.get(i));
                }
            }

            System.out.println("\n1. Adicionar");
            System.out.println("2. Editar");
            System.out.println("3. Excluir");
            System.out.println("0. Voltar");

            int acao = lerInteiroSeguro(teclado, "O que deseja fazer? ");
            if (acao == 1) {
                System.out.print("Digite o nome da nova " + nomeEntidade + ": ");
                String novo = teclado.nextLine();
                if (!novo.trim().isEmpty()) {
                    lista.add(novo);
                    System.out.println("✅ Adicionado com sucesso!");
                }
            } else if (acao == 2) {
                if (lista.isEmpty()) {
                    System.out.println("A lista está vazia, adicione algo primeiro!");
                    continue;
                }
                int idx = lerInteiroSeguro(teclado, "Digite o NÚMERO para editar: ") - 1;
                if (idx >= 0 && idx < lista.size()) {
                    System.out.print("Novo nome para [" + lista.get(idx) + "]: ");
                    String novoNome = teclado.nextLine();
                    if (!novoNome.trim().isEmpty()) {
                        lista.set(idx, novoNome);
                        System.out.println("✅ Editado com sucesso!");
                    }
                } else {
                    System.out.println("❌ Número inválido!");
                }
            } else if (acao == 3) {
                if (lista.isEmpty()) {
                    System.out.println("A lista está vazia!");
                    continue;
                }
                int idx = lerInteiroSeguro(teclado, "Digite o NÚMERO para excluir: ") - 1;
                if (idx >= 0 && idx < lista.size()) {
                    String itemParaExcluir = lista.get(idx);
                    boolean estaEmUso = false;

                    for (Produto p : estoque) {
                        if (nomeEntidade.equalsIgnoreCase("Categoria") && p.getCategoria().equalsIgnoreCase(itemParaExcluir)) {
                            estaEmUso = true;
                            break;
                        } else if (nomeEntidade.equalsIgnoreCase("Fornecedor") && p.getFornecedor().equalsIgnoreCase(itemParaExcluir)) {
                            estaEmUso = true;
                            break;
                        }
                    }

                    if (estaEmUso) {
                        System.out.println("❌ ERRO DE INTEGRIDADE!");
                        System.out.println("Não é possível excluir '" + itemParaExcluir + "' porque existem produtos vinculados a ele.");
                        System.out.println("Dica: Edite os produtos e troque a " + nomeEntidade + " deles antes de excluir.");
                    } else {
                        String removido = lista.remove(idx);
                        System.out.println("🗑️ '" + removido + "' excluído com sucesso!");
                    }

                } else {
                    System.out.println("❌ Número inválido!");
                }
            } else if (acao == 0) {
                gerindoLista = false;
            } else {
                System.out.println("Opção inválida!");
            }
        }
    }

    // =========================================================================
    // 🌟 NOVA CENTRAL DE ETIQUETAS COM HISTÓRICO
    // =========================================================================
    private static void abrirCentralEtiquetas(Scanner teclado, ArrayList<Produto> estoque, GerenciadorDeArquivo gerenciador) {
        boolean naCentral = true;

        while (naCentral) {
            System.out.println("\n🖨️ --- CENTRAL DE ETIQUETAS ---");
            System.out.println("1. Gerar Nova Etiqueta de Manipulação");
            System.out.println("2. Ver Histórico de Etiquetas Geradas");
            System.out.println("0. Voltar ao Menu Principal");

            int opcaoCentral = lerInteiroSeguro(teclado, "Escolha uma opção: ");

            if (opcaoCentral == 1) {
                System.out.print("\nDigite o nome do Produto (ou parte do nome): ");
                String termoPesquisa = teclado.nextLine().toLowerCase();

                ArrayList<Produto> produtosEncontrados = new ArrayList<>();
                for (Produto p : estoque) {
                    if (p.getNome().toLowerCase().contains(termoPesquisa)) {
                        produtosEncontrados.add(p);
                    }
                }

                if (produtosEncontrados.isEmpty()) {
                    System.out.println("❌ Nenhum produto encontrado.");
                    continue; // Volta para o menu da central
                }

                System.out.println("\nProdutos encontrados:");
                for (int i = 0; i < produtosEncontrados.size(); i++) {
                    System.out.println((i + 1) + ". " + produtosEncontrados.get(i).getNome() + " (" + produtosEncontrados.get(i).getCategoria() + ")");
                }
                int escolhaProd = lerInteiroSeguro(teclado, "Escolha o produto: ") - 1;

                if (escolhaProd < 0 || escolhaProd >= produtosEncontrados.size()) {
                    System.out.println("❌ Escolha inválida.");
                    continue;
                }
                Produto produtoSelecionado = produtosEncontrados.get(escolhaProd);

                if (produtoSelecionado.getLotes().isEmpty()) {
                    System.out.println("❌ Este produto não tem estoque para gerar etiqueta.");
                    continue;
                }

                System.out.println("\nLotes disponíveis para " + produtoSelecionado.getNome() + ":");
                for (int i = 0; i < produtoSelecionado.getLotes().size(); i++) {
                    Lote lote = produtoSelecionado.getLotes().get(i);
                    System.out.printf("%d. Lote: %s | Validade: %s | Saldo: %.2f %s\n",
                            (i + 1), lote.getIdInterno(), lote.getDataValidadeFormatada(), lote.getQuantidade(), produtoSelecionado.getUnidade());
                }
                int escolhaLote = lerInteiroSeguro(teclado, "De qual Lote você está manipulando? ") - 1;

                if (escolhaLote < 0 || escolhaLote >= produtoSelecionado.getLotes().size()) {
                    System.out.println("❌ Lote inválido.");
                    continue;
                }
                Lote loteSelecionado = produtoSelecionado.getLotes().get(escolhaLote);

                System.out.print("\nQuantidade Manipulada (ex: 200): ");
                String qtdManipulada = teclado.nextLine() + produtoSelecionado.getUnidade();

                System.out.print("Nome do Responsável (Ex: João Neto): ");
                String responsavel = teclado.nextLine();

                java.time.LocalDate hoje = java.time.LocalDate.now();
                java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String dataManipulacao = hoje.format(fmt);

                String validadeFinalCalculada = loteSelecionado.getDataValidadeFormatada();

                if (produtoSelecionado.getDiasValidadeAberto() > 0 && loteSelecionado.getDataValidade() != null) {
                    java.time.LocalDate validadeSeAbertoHoje = hoje.plusDays(produtoSelecionado.getDiasValidadeAberto());
                    if (validadeSeAbertoHoje.isBefore(loteSelecionado.getDataValidade())) {
                        validadeFinalCalculada = validadeSeAbertoHoje.format(fmt);
                    }
                } else if (produtoSelecionado.getDiasValidadeAberto() > 0 && loteSelecionado.getDataValidade() == null) {
                    validadeFinalCalculada = hoje.plusDays(produtoSelecionado.getDiasValidadeAberto()).format(fmt);
                }

                System.out.println("\n==================================================");
                System.out.println("🏷️ PRÉ-VISUALIZAÇÃO DA ETIQUETA GERADA");
                System.out.println("==================================================");
                System.out.println("Produto: " + produtoSelecionado.getNome().toUpperCase());
                System.out.println("Categoria: " + produtoSelecionado.getCategoria());
                System.out.println("Qtde Manipulada: " + qtdManipulada);
                System.out.println("--------------------------------------------------");
                System.out.println("Manipulação:    " + dataManipulacao);
                System.out.println("Val. Original:  " + loteSelecionado.getDataValidadeFormatada());
                System.out.println("Val. FINAL:     " + validadeFinalCalculada + " ⚠️");
                System.out.println("--------------------------------------------------");
                System.out.println("Fornecedor: " + produtoSelecionado.getFornecedor());
                System.out.println("Responsável: " + responsavel);
                System.out.println("ID Rastreio: #" + loteSelecionado.getIdInterno());
                System.out.println("==================================================\n");

                System.out.println("✅ Etiqueta gerada com sucesso!");

                // 🌟 MUDANÇA: Onde nós guardamos o histórico no arquivo .txt!
                String linhaHistorico = String.format("[%s] %s | Lote: %s | Qtd: %s | Validade: %s | Resp: %s",
                        dataManipulacao, produtoSelecionado.getNome(), loteSelecionado.getIdInterno(), qtdManipulada, validadeFinalCalculada, responsavel);
                gerenciador.salvarEtiquetaNoHistorico(linhaHistorico);

            } else if (opcaoCentral == 2) {
                System.out.println("\n📜 --- HISTÓRICO DE ETIQUETAS GERADAS ---");
                gerenciador.lerHistoricoEtiquetas();

            } else if (opcaoCentral == 0) {
                naCentral = false;
            } else {
                System.out.println("Opção inválida!");
            }
        }
    }
}