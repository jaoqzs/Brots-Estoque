import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GestorDeCompras {

    static ArrayList<ListaCompra> listasDeCompra = new ArrayList<>();

    public static void abrirMenu(Scanner teclado, ArrayList<Produto> estoque) {
        boolean noMenu = true;

        while (noMenu) {
            int qtdCritico = 0;
            int qtdZerado = 0;

            for (Produto p : estoque) {
                if (p.getQuantidadeAtual() == 0) {
                    qtdZerado++;
                } else if (p.getQuantidadeAtual() < p.getEstoqueMinimo()) {
                    qtdCritico++;
                }
            }

            System.out.println("\n🛒 ========================================");
            System.out.println("            LISTA DE COMPRAS");
            System.out.println("========================================");
            System.out.println("⚠️  Estoque Crítico: " + qtdCritico + " produtos abaixo do mínimo");
            System.out.println("📦  Estoque Zerado:  " + qtdZerado + " produtos sem estoque");
            System.out.println("----------------------------------------");
            System.out.println("1. [ + Nova Lista de Compras ]");
            System.out.println("2. Listas em Andamento");
            System.out.println("3. Listas Finalizadas");
            System.out.println("0. Voltar ao Menu Principal");

            System.out.print("\nEscolha uma opção: ");
            String input = teclado.nextLine();
            int opcao = -1;
            try { opcao = Integer.parseInt(input); } catch (Exception e) {}

            if (opcao == 1) {
                criarNovaLista(teclado, estoque);
            } else if (opcao == 2) {
                listarEmAndamento(teclado, estoque);
            } else if (opcao == 3) {
                listarFinalizadas(teclado);
            } else if (opcao == 0) {
                noMenu = false;
            } else {
                System.out.println("❌ Opção inválida!");
            }
        }
    }

    private static void criarNovaLista(Scanner teclado, ArrayList<Produto> estoque) {
        System.out.println("\n📝 --- NOVA LISTA DE COMPRAS ---");
        System.out.print("Nome da Lista (Ex: Feira Mercadão, Queiroz): ");
        String nome = teclado.nextLine();

        System.out.print("Previsão de Recebimento (dd/mm/aaaa): ");
        String previsao = teclado.nextLine();

        System.out.print("Criado por (Seu Nome): ");
        String autor = teclado.nextLine();

        ListaCompra novaLista = new ListaCompra(nome, previsao, autor);
        boolean adicionando = true;

        while (adicionando) {
            System.out.println("\n--- PRODUTOS DISPONÍVEIS ---");
            for (int i = 0; i < estoque.size(); i++) {
                Produto p = estoque.get(i);
                String alerta = "";
                if (p.getQuantidadeAtual() == 0) alerta = "[ZERADO] ";
                else if (p.getQuantidadeAtual() < p.getEstoqueMinimo()) alerta = "[CRÍTICO] ";

                System.out.printf("%d. %s%s (Estoque: %.2f %s | Mín: %.2f %s)\n",
                        (i + 1), alerta, p.getNome(), p.getQuantidadeAtual(), p.getUnidade(), p.getEstoqueMinimo(), p.getUnidade());
            }

            System.out.println("0. Salvar e Fechar Lista");
            System.out.print("Digite o NÚMERO do produto para adicionar ao carrinho: ");

            int escolha = -1;
            try { escolha = Integer.parseInt(teclado.nextLine()); } catch (Exception e) {}

            if (escolha == 0) {
                adicionando = false;
            } else if (escolha > 0 && escolha <= estoque.size()) {
                Produto prodEscolhido = estoque.get(escolha - 1);
                System.out.print("Quantidade a comprar de " + prodEscolhido.getNome() + ": ");
                try {
                    double qtd = Double.parseDouble(teclado.nextLine().replace(",", "."));
                    novaLista.adicionarItem(new ItemCompra(prodEscolhido, qtd));
                    System.out.println("✅ Adicionado ao carrinho!");
                } catch (Exception e) {
                    System.out.println("❌ Quantidade inválida.");
                }
            } else {
                System.out.println("❌ Produto não encontrado.");
            }
        }

        if (!novaLista.getItens().isEmpty()) {
            listasDeCompra.add(novaLista);
            System.out.println("🎉 Lista '" + novaLista.getNome() + "' salva e enviada para o Andamento!");
        } else {
            System.out.println("⚠️ A lista estava vazia e foi descartada.");
        }
    }

    // =======================================================
    // MÓDULO: LISTAS EM ANDAMENTO
    // =======================================================
    private static void listarEmAndamento(Scanner teclado, ArrayList<Produto> estoque) {
        ArrayList<ListaCompra> andamento = new ArrayList<>();
        for (ListaCompra l : listasDeCompra) {
            if (l.getStatus().equals("Aguardando Recebimento")) {
                andamento.add(l);
            }
        }

        if (andamento.isEmpty()) {
            System.out.println("\n📭 Nenhuma lista em andamento no momento.");
            return;
        }

        System.out.println("\n⏳ --- LISTAS EM ANDAMENTO ---");
        for (int i = 0; i < andamento.size(); i++) {
            System.out.println((i + 1) + ". " + andamento.get(i).getNome() + " (Previsão: " + andamento.get(i).getPrevisaoRecebimento() + ")");
        }
        System.out.println("0. Voltar");

        System.out.print("Selecione a lista para gerenciar: ");
        int escolha = -1;
        try { escolha = Integer.parseInt(teclado.nextLine()); } catch (Exception e) {}

        if (escolha > 0 && escolha <= andamento.size()) {
            painelListaAndamento(andamento.get(escolha - 1), teclado, estoque);
        }
    }

    private static void painelListaAndamento(ListaCompra lista, Scanner teclado, ArrayList<Produto> estoque) {
        boolean noPainel = true;

        while (noPainel) {
            System.out.println("\n📋 ========================================");
            System.out.println("Lista: " + lista.getNome().toUpperCase());
            System.out.println("Status: " + lista.getStatus() + " | Previsão: " + lista.getPrevisaoRecebimento());
            System.out.println("Criado por: " + lista.getCriadoPor());
            System.out.println("==========================================");

            System.out.println("ITENS DA LISTA:");
            for (int i = 0; i < lista.getItens().size(); i++) {
                ItemCompra item = lista.getItens().get(i);
                String check = item.isChecado() ? "[✅]" : "[ ]";
                System.out.printf("%d. %s %s (Estoque Atual: %.2f %s)\n",
                        (i+1), check, item.getProduto().getNome(), item.getProduto().getQuantidadeAtual(), item.getProduto().getUnidade());
                System.out.printf("   Solicitado: %.2f | Recebido: %.2f\n", item.getQuantidadeSolicitada(), item.getQuantidadeRecebida());
                if (!item.getObservacao().isEmpty()) {
                    System.out.println("   Obs: " + item.getObservacao());
                }
            }
            System.out.println("==========================================");

            System.out.println("1. Informar Recebimento de Item (Dar Check)");
            System.out.println("2. Copiar para WhatsApp");
            System.out.println("3. MARCAR COMO RECEBIDO (Finalizar Lista)");
            System.out.println("0. Voltar");

            System.out.print("\nO que deseja fazer? ");
            int acao = -1;
            try { acao = Integer.parseInt(teclado.nextLine()); } catch (Exception e) {}

            if (acao == 1) {
                System.out.print("Qual item você quer dar check (NÚMERO)? ");
                int idxItem = -1;
                try { idxItem = Integer.parseInt(teclado.nextLine()) - 1; } catch (Exception e) {}

                if (idxItem >= 0 && idxItem < lista.getItens().size()) {
                    ItemCompra item = lista.getItens().get(idxItem);
                    System.out.print("Quantidade recebida de " + item.getProduto().getNome() + " (Solicitado: " + item.getQuantidadeSolicitada() + "): ");
                    try {
                        double qtdChegou = Double.parseDouble(teclado.nextLine().replace(",", "."));
                        item.setQuantidadeRecebida(qtdChegou);

                        if (qtdChegou < item.getQuantidadeSolicitada()) {
                            System.out.print("Veio menos do que o pedido. Motivo/Observação: ");
                            item.setObservacao(teclado.nextLine());
                        } else if (qtdChegou > item.getQuantidadeSolicitada()) {
                            System.out.print("Veio MAIS do que o pedido. Motivo/Observação: ");
                            item.setObservacao(teclado.nextLine());
                        }

                        item.setChecado(true);
                        System.out.println("✅ Item checado com sucesso!");
                    } catch (Exception e) {
                        System.out.println("❌ Quantidade inválida.");
                    }
                } else {
                    System.out.println("❌ Item não encontrado.");
                }

            } else if (acao == 2) {
                System.out.println("\n--- TEXTO PRONTO PARA O WHATSAPP ---");
                System.out.println("🛒 *Pedido de Compras - " + lista.getNome() + "*");
                System.out.println("📅 Para entrega: " + lista.getPrevisaoRecebimento() + "\n");
                for (ItemCompra item : lista.getItens()) {
                    System.out.printf("▪️ %.2f %s de %s\n", item.getQuantidadeSolicitada(), item.getProduto().getUnidade(), item.getProduto().getNome());
                }
                System.out.println("\n_(Enviado via Brots)_");
                System.out.println("------------------------------------");
                System.out.println("Dica: Selecione o texto acima, aperte Ctrl+C e cole no WhatsApp!");

            } else if (acao == 3) {
                if (!lista.todosItensChecados()) {
                    System.out.println("❌ ERRO: Você precisa dar o Check em todos os itens antes de finalizar!");
                    System.out.println("Dica: Se não veio nada de um item, coloque 'Recebido: 0' e justifique.");
                } else {
                    System.out.println("\n⚠️ Finalizando lista e injetando produtos no estoque...");
                    String dataHoje = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                    // A Mágica de injeção automática!
                    for (ItemCompra item : lista.getItens()) {
                        if (item.getQuantidadeRecebida() > 0) {
                            Produto p = item.getProduto();
                            Lote novoLote = new Lote(p.getNome(), item.getQuantidadeRecebida(), "", "LISTA: " + lista.getNome());
                            Movimentacao mov = new Movimentacao("Entrada", "Compra de Lista", item.getQuantidadeRecebida(), p.getPrecoPorUnidade(), dataHoje, "", item.getObservacao());
                            p.registrarEntrada(novoLote, p.getPrecoPorUnidade(), mov);
                        }
                    }

                    lista.setStatus("Finalizada");
                    lista.setDataFinalizada(dataHoje);
                    System.out.println("🎉 Lista recebida! O estoque dos produtos foi atualizado automaticamente.");
                    noPainel = false; // Sai do painel porque a lista não está mais "Em andamento"
                }

            } else if (acao == 0) {
                noPainel = false;
            } else {
                System.out.println("❌ Opção inválida!");
            }
        }
    }

    // =======================================================
    // MÓDULO: LISTAS FINALIZADAS (HISTÓRICO)
    // =======================================================
    private static void listarFinalizadas(Scanner teclado) {
        ArrayList<ListaCompra> finalizadas = new ArrayList<>();
        for (ListaCompra l : listasDeCompra) {
            if (l.getStatus().equals("Finalizada")) {
                finalizadas.add(l);
            }
        }

        if (finalizadas.isEmpty()) {
            System.out.println("\n📭 Nenhuma lista finalizada ainda.");
            return;
        }

        System.out.println("\n✅ --- HISTÓRICO DE COMPRAS ---");
        for (int i = 0; i < finalizadas.size(); i++) {
            System.out.println((i + 1) + ". " + finalizadas.get(i).getNome() + " (Recebido em: " + finalizadas.get(i).getDataFinalizada() + ")");
        }
        System.out.println("0. Voltar");

        System.out.print("Selecione a lista para ver os detalhes: ");
        int escolha = -1;
        try { escolha = Integer.parseInt(teclado.nextLine()); } catch (Exception e) {}

        if (escolha > 0 && escolha <= finalizadas.size()) {
            ListaCompra lista = finalizadas.get(escolha - 1);

            System.out.println("\n🗄️ ========================================");
            System.out.println("Lista: " + lista.getNome().toUpperCase());
            System.out.println("Recebido em: " + lista.getDataFinalizada());
            System.out.println("==========================================");

            for (ItemCompra item : lista.getItens()) {
                System.out.printf("✅ %s\n", item.getProduto().getNome());
                System.out.printf("   Solicitou: %.2f | Recebeu: %.2f\n", item.getQuantidadeSolicitada(), item.getQuantidadeRecebida());
                if (!item.getObservacao().isEmpty()) {
                    System.out.println("   Obs: " + item.getObservacao());
                }
                System.out.println("   - - - - - - - - - - - - - - -");
            }
        }
    }
}