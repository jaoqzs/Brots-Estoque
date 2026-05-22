import java.util.ArrayList;
import java.util.Scanner;

public class GestorFichasTecnicas {

    static ArrayList<FichaTecnica> fichasCadastradas = new ArrayList<>();
    static ArrayList<String> categoriasFicha = new ArrayList<>();

    public static void abrirMenu(Scanner teclado, ArrayList<Produto> estoque) {
        boolean noMenu = true;

        while (noMenu) {
            System.out.println("\n📋 ========================================");
            System.out.println("            FICHAS TÉCNICAS");
            System.out.println("========================================");
            System.out.println("1. [ + Criar Ficha Técnica ]");
            System.out.println("2. Listar e Visualizar Fichas");
            System.out.println("3. [ + Gerenciar Categorias de Receitas ]");
            System.out.println("0. Voltar ao Menu Principal");

            System.out.print("\nEscolha uma opção: ");
            String input = teclado.nextLine();
            int opcao = -1;
            try {
                opcao = Integer.parseInt(input);
            } catch (Exception e) {
            }

            if (opcao == 1) {
                criarFicha(teclado, estoque);
            } else if (opcao == 2) {
                listarFichas(teclado);
            } else if (opcao == 3) {
                gerenciarCategorias(teclado);
            } else if (opcao == 0) {
                noMenu = false;
            } else {
                System.out.println("❌ Opção inválida!");
            }
        }
    }

    private static void criarFicha(Scanner teclado, ArrayList<Produto> estoque) {
        if (categoriasFicha.isEmpty()) {
            System.out.println("⚠️ Você precisa criar pelo menos uma Categoria (Opção 3) antes de criar uma Ficha!");
            return;
        }

        System.out.println("\n🍳 --- NOVA FICHA TÉCNICA ---");
        System.out.print("Nome da Ficha (Ex: Pão com Tucumã): ");
        String nome = teclado.nextLine();

        System.out.println("Categorias disponíveis:");
        for (int i = 0; i < categoriasFicha.size(); i++) {
            System.out.println((i + 1) + ". " + categoriasFicha.get(i));
        }
        System.out.print("Escolha o NÚMERO da Categoria: ");
        int catIndex = Integer.parseInt(teclado.nextLine()) - 1;
        String categoria = categoriasFicha.get(catIndex);

        System.out.print("Unidade de Rendimento (ex: unidade, porção, fatias): ");
        String unidade = teclado.nextLine();

        System.out.print("Quantidade de Rendimento (ex: 1): ");
        double rendimento = Double.parseDouble(teclado.nextLine().replace(",", "."));

        FichaTecnica novaFicha = new FichaTecnica(nome, categoria, unidade, rendimento);

        boolean adicionando = true;
        while (adicionando) {
            System.out.println("\n--- INGREDIENTES DISPONÍVEIS ---");
            for (int i = 0; i < estoque.size(); i++) {
                Produto p = estoque.get(i);
                System.out.printf("%d. %s (Base: %s | Custo Médio: R$ %.2f)\n", (i + 1), p.getNome(), p.getUnidade(),
                        p.getPrecoPorUnidade());
            }
            System.out.println("0. Finalizar e Salvar Ficha");
            System.out.print("Digite o NÚMERO do produto para adicionar à receita: ");

            int escolha = Integer.parseInt(teclado.nextLine());

            if (escolha == 0) {
                adicionando = false;
            } else if (escolha > 0 && escolha <= estoque.size()) {
                Produto prodEscolhido = estoque.get(escolha - 1);
                System.out.print("Quantidade usada de " + prodEscolhido.getNome() + " (Em " + prodEscolhido.getUnidade()
                        + "): ");
                double qtdUsada = Double.parseDouble(teclado.nextLine().replace(",", "."));

                novaFicha.adicionarItem(new ItemFicha(prodEscolhido, qtdUsada));
                System.out.println("✅ Ingrediente adicionado!");
            }
        }

        fichasCadastradas.add(novaFicha);
        
        // Salva no arquivo imediatamente para não perder
        GerenciadorDeArquivo gerenciador = new GerenciadorDeArquivo();
        gerenciador.salvarFichasTecnicas(fichasCadastradas);

        System.out.println("🎉 Ficha Técnica '" + nome + "' salva com sucesso! Custo de produção: R$ "
                + String.format("%.2f", novaFicha.getCustoTotal()));
    }

    private static void listarFichas(Scanner teclado) {
        if (fichasCadastradas.isEmpty()) {
            System.out.println("\n📭 Nenhuma ficha cadastrada.");
            return;
        }

        System.out.println("\n📑 --- FICHAS CADASTRADAS ---");
        for (int i = 0; i < fichasCadastradas.size(); i++) {
            FichaTecnica f = fichasCadastradas.get(i);
            System.out.printf("%d. %s [%s] - Custo Total: R$ %.2f\n", (i + 1), f.getNome(), f.getCategoria(),
                    f.getCustoTotal());
        }
        System.out.println("0. Voltar");
        System.out.print("\nDigite o NÚMERO da ficha para ver a COMPOSIÇÃO: ");

        int escolha = Integer.parseInt(teclado.nextLine());
        if (escolha > 0 && escolha <= fichasCadastradas.size()) {
            FichaTecnica f = fichasCadastradas.get(escolha - 1);

            System.out.println("\n👁️ ========================================");
            System.out.println(f.getNome().toUpperCase());
            System.out.println("Composição da ficha técnica");
            System.out.println("==========================================");

            for (ItemFicha item : f.getItens()) {
                System.out.printf("🔹 %s\n", item.getProduto().getNome());
                System.out.printf("   %.6f %s \t\t R$ %.2f\n", item.getQuantidadeUso(), item.getProduto().getUnidade(),
                        item.getCustoCalculado());
                System.out.printf("   (Custo do Estoque: R$ %.2f/%s)\n", item.getProduto().getPrecoPorUnidade(),
                        item.getProduto().getUnidade());
                System.out.println("   ---------------------------------------");
            }

            System.out.printf("\n💰 CUSTO TOTAL: R$ %.2f\n", f.getCustoTotal());
            System.out.printf("   Custo por %s: R$ %.2f\n", f.getUnidadeRendimento(),
                    (f.getCustoTotal() / f.getQuantidadeRendimento()));
            System.out.println("==========================================\n");
        }
    }

    private static void gerenciarCategorias(Scanner teclado) {
        boolean gerindo = true;
        while (gerindo) {
            System.out.println("\n🏷️ --- CATEGORIAS DE RECEITAS ---");
            if (categoriasFicha.isEmpty()) {
                System.out.println("Nenhuma categoria cadastrada.");
            } else {
                for (int i = 0; i < categoriasFicha.size(); i++) {
                    System.out.println((i + 1) + ". " + categoriasFicha.get(i));
                }
            }

            System.out.println("\n1. Adicionar");
            System.out.println("2. Editar");
            System.out.println("3. Excluir");
            System.out.println("0. Voltar");

            System.out.print("O que deseja fazer? ");
            String input = teclado.nextLine();
            int acao = -1;
            try {
                acao = Integer.parseInt(input);
            } catch (Exception e) {}

            if (acao == 1) {
                System.out.print("Digite o nome da nova categoria: ");
                String nova = teclado.nextLine();
                if (!nova.trim().isEmpty()) {
                    categoriasFicha.add(nova);
                    
                    GerenciadorDeArquivo gerenciador = new GerenciadorDeArquivo();
                    gerenciador.salvarListaSimples(categoriasFicha, "categorias_ficha.txt");
                    
                    System.out.println("✅ Categoria '" + nova + "' adicionada!");
                }
            } else if (acao == 2) {
                if (categoriasFicha.isEmpty()) {
                    System.out.println("A lista está vazia!");
                    continue;
                }
                System.out.print("Digite o NÚMERO para editar: ");
                try {
                    int idx = Integer.parseInt(teclado.nextLine()) - 1;
                    if (idx >= 0 && idx < categoriasFicha.size()) {
                        System.out.print("Novo nome para [" + categoriasFicha.get(idx) + "]: ");
                        String novoNome = teclado.nextLine();
                        if (!novoNome.trim().isEmpty()) {
                            String catAntiga = categoriasFicha.get(idx);
                            for (FichaTecnica f : fichasCadastradas) {
                                if (f.getCategoria().equalsIgnoreCase(catAntiga)) {
                                    f.setCategoria(novoNome);
                                }
                            }
                            
                            categoriasFicha.set(idx, novoNome);
                            
                            GerenciadorDeArquivo gerenciador = new GerenciadorDeArquivo();
                            gerenciador.salvarListaSimples(categoriasFicha, "categorias_ficha.txt");
                            gerenciador.salvarFichasTecnicas(fichasCadastradas);
                            
                            System.out.println("✅ Editado com sucesso!");
                        }
                    } else {
                        System.out.println("❌ Número inválido!");
                    }
                } catch (Exception e) {
                    System.out.println("❌ Número inválido!");
                }
            } else if (acao == 3) {
                if (categoriasFicha.isEmpty()) {
                    System.out.println("A lista está vazia!");
                    continue;
                }
                System.out.print("Digite o NÚMERO para excluir: ");
                try {
                    int idx = Integer.parseInt(teclado.nextLine()) - 1;
                    if (idx >= 0 && idx < categoriasFicha.size()) {
                        String catExcluir = categoriasFicha.get(idx);
                        boolean emUso = false;
                        for (FichaTecnica f : fichasCadastradas) {
                            if (f.getCategoria().equalsIgnoreCase(catExcluir)) {
                                emUso = true;
                                break;
                            }
                        }
                        
                        if (emUso) {
                            System.out.println("❌ ERRO: Não é possível excluir a categoria '" + catExcluir + "' porque existem fichas usando ela!");
                        } else {
                            categoriasFicha.remove(idx);
                            GerenciadorDeArquivo gerenciador = new GerenciadorDeArquivo();
                            gerenciador.salvarListaSimples(categoriasFicha, "categorias_ficha.txt");
                            System.out.println("🗑️ Categoria '" + catExcluir + "' excluída!");
                        }
                    } else {
                        System.out.println("❌ Número inválido!");
                    }
                } catch (Exception e) {
                    System.out.println("❌ Número inválido!");
                }
            } else if (acao == 0) {
                gerindo = false;
            } else {
                System.out.println("❌ Opção inválida!");
            }
        }
    }
}