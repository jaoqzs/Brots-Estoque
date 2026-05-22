import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class GerenciadorDeArquivo {
    private final String NOME_ARQUIVO = "estoque_brots.csv";

    public void salvarEstoque(ArrayList<Produto> estoque) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(NOME_ARQUIVO))) {
            // Passa por todos os produtos
            for (Produto p : estoque) {
                // Pega o "pacote" com a linha do produto, os lotes e o histórico
                for (String linha : p.exportarDados()) {
                    writer.println(linha);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao salvar o arquivo: " + e.getMessage());
        }
    }

    public ArrayList<Produto> carregarEstoque() {
        ArrayList<Produto> estoque = new ArrayList<>();
        File arquivo = new File(NOME_ARQUIVO);

        if (!arquivo.exists()) return estoque; // Se é a primeira vez, retorna vazio

        try (Scanner leitor = new Scanner(arquivo)) {
            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine();
                if (linha.trim().isEmpty()) continue;

                String[] dados = linha.split(";");
                String etiqueta = dados[0]; // A nossa tag: PRODUTO, LOTE ou MOVIMENTACAO

                try {
                    if (etiqueta.equals("PRODUTO")) {

                        // 🌟 MUDANÇA 1: A leitura da validade pós-abertura com trava de segurança!
                        int diasValidadeAberto = 0;
                        if (dados.length > 10) { // Se for maior que 10, significa que tem a coluna nova!
                            diasValidadeAberto = Integer.parseInt(dados[10]);
                        }

                        // Cria o produto "vazio"
                        Produto p = new Produto(
                                dados[1], dados[2], dados[3],
                                Double.parseDouble(dados[4]),
                                0.0,
                                "", // validadeInicial
                                "", // codigoLoteInicial vazio
                                Double.parseDouble(dados[5]),
                                dados[6], dados[7],
                                Boolean.parseBoolean(dados[8]), dados[9],
                                diasValidadeAberto // 🌟 MUDANÇA 2: Passamos a variável pro construtor!
                        );
                        estoque.add(p);
                    }
                    else if (etiqueta.equals("LOTE")) {
                        Produto p = encontrarProduto(estoque, dados[1]);
                        if (p != null) {
                            String validade = dados[3].equals("N/A") ? null : dados[3];
                            // NOVO: Pega o código do lote (usa N/A se for um arquivo antigo sem essa coluna)
                            String codigoLote = dados.length > 4 ? dados[4] : "N/A";

                            // 🌟 MUDANÇA 3: Adicionamos dados[1] no começo para o Lote saber qual é o nome do Produto e criar o ID Brots
                            p.getLotes().add(new Lote(dados[1], Double.parseDouble(dados[2]), validade, codigoLote));
                        }
                    }
                    else if (etiqueta.equals("MOVIMENTACAO")) {
                        Produto p = encontrarProduto(estoque, dados[1]);
                        if (p != null) {
                            String validade = dados[7].equals("N/A") ? "" : dados[7];
                            String obs = dados[8].equals("N/A") ? "" : dados[8];

                            Movimentacao mov = new Movimentacao(
                                    dados[2], dados[3], Double.parseDouble(dados[4]),
                                    Double.parseDouble(dados[5]), dados[6], validade, obs
                            );
                            // Guarda o recibo silenciosamente no extrato
                            p.getHistorico().add(mov);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ Erro ao tentar ler uma linha antiga. Como o formato mudou, algumas coisas velhas podem ser ignoradas.");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao carregar arquivo de banco de dados.");
        }
        return estoque;
    }

    // Método "Detetive": procura qual é o produto dono do lote/movimentação pelo nome
    private Produto encontrarProduto(ArrayList<Produto> estoque, String nome) {
        for (Produto p : estoque) {
            if (p.getNome().equals(nome)) return p;
        }
        return null;
    }

    public void salvarListaSimples(ArrayList<String> lista, String nomeArquivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
            for (String item : lista) {
                writer.println(item);
            }
        } catch (Exception e) {
            System.out.println("Erro ao salvar lista: " + nomeArquivo);
        }
    }

    public ArrayList<String> carregarListaSimples(String nomeArquivo) {
        ArrayList<String> lista = new ArrayList<>();
        File arquivo = new File(nomeArquivo);
        if (!arquivo.exists()) return lista;

        try (Scanner leitor = new Scanner(arquivo)) {
            while (leitor.hasNextLine()) {
                lista.add(leitor.nextLine());
            }
        } catch (Exception e) { }
        return lista;
    }

    // =======================================================
    // MÉTODOS DO HISTÓRICO DE ETIQUETAS
    // =======================================================

    public void salvarEtiquetaNoHistorico(String detalhesEtiqueta) {
        // Aquele "true" ali no FileWriter liga o Modo "Append" (Adicionar ao final).
        // Isso impede que ele apague as etiquetas velhas ao salvar uma nova!
        try (PrintWriter writer = new PrintWriter(new FileWriter("historico_etiquetas.txt", true))) {
            writer.println(detalhesEtiqueta);
        } catch (Exception e) {
            System.out.println("❌ Erro ao salvar histórico de etiqueta.");
        }
    }

    public void lerHistoricoEtiquetas() {
        File arquivo = new File("historico_etiquetas.txt");
        if (!arquivo.exists()) {
            System.out.println("   📭 Nenhuma etiqueta foi gerada ainda.");
            return;
        }

        try (Scanner leitor = new Scanner(arquivo)) {
            while (leitor.hasNextLine()) {
                System.out.println("   " + leitor.nextLine());
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao ler o histórico.");
        }
    }

    // =======================================================
    // MÉTODOS DE SALVAMENTO DAS LISTAS DE COMPRAS
    // =======================================================
    private final String NOME_ARQUIVO_COMPRAS = "compras_brots.csv";

    public void salvarListasCompra(ArrayList<ListaCompra> listas) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(NOME_ARQUIVO_COMPRAS))) {
            for (ListaCompra lista : listas) {
                for (String linha : lista.exportarDados()) {
                    writer.println(linha);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao salvar listas de compras.");
        }
    }

    public ArrayList<ListaCompra> carregarListasCompra(ArrayList<Produto> estoque) {
        ArrayList<ListaCompra> listas = new ArrayList<>();
        File arquivo = new File(NOME_ARQUIVO_COMPRAS);

        if (!arquivo.exists()) return listas;

        try (Scanner leitor = new Scanner(arquivo)) {
            ListaCompra listaAtual = null;

            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine();
                if (linha.trim().isEmpty()) continue;
                String[] dados = linha.split(";");

                if (dados[0].equals("LISTA")) {
                    listaAtual = new ListaCompra(dados[1], dados[2], dados[3]);
                    listaAtual.setStatus(dados[4]);
                    listaAtual.setDataFinalizada(dados[5].equals("N/A") ? "" : dados[5]);
                    listas.add(listaAtual);

                } else if (dados[0].equals("ITEM") && listaAtual != null) {
                    // O Detetive entra em ação novamente para achar o produto real no estoque!
                    Produto p = encontrarProduto(estoque, dados[2]);
                    if (p != null) {
                        ItemCompra item = new ItemCompra(p, Double.parseDouble(dados[3]));
                        item.setQuantidadeRecebida(Double.parseDouble(dados[4]));
                        item.setChecado(Boolean.parseBoolean(dados[5]));
                        item.setObservacao(dados[6].equals("N/A") ? "" : dados[6]);
                        listaAtual.adicionarItem(item);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao carregar listas de compras.");
        }
        return listas;
    }

    // =======================================================
    // MÉTODOS DE SALVAMENTO DAS FICHAS TÉCNICAS
    // =======================================================
    private final String NOME_ARQUIVO_FICHAS = "fichas_brots.csv";

    public void salvarFichasTecnicas(ArrayList<FichaTecnica> fichas) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(NOME_ARQUIVO_FICHAS))) {
            for (FichaTecnica ficha : fichas) {
                // Salva a Ficha
                writer.println("FICHA;" + ficha.getNome() + ";" + ficha.getCategoria() + ";" + 
                               ficha.getUnidadeRendimento() + ";" + ficha.getQuantidadeRendimento());
                
                // Salva os Itens
                for (ItemFicha item : ficha.getItens()) {
                    writer.println("ITEM;" + item.getProduto().getNome() + ";" + item.getQuantidadeUso());
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao salvar fichas técnicas.");
        }
    }

    public ArrayList<FichaTecnica> carregarFichasTecnicas(ArrayList<Produto> estoque) {
        ArrayList<FichaTecnica> fichas = new ArrayList<>();
        File arquivo = new File(NOME_ARQUIVO_FICHAS);

        if (!arquivo.exists()) return fichas;

        try (Scanner leitor = new Scanner(arquivo)) {
            FichaTecnica fichaAtual = null;

            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine();
                if (linha.trim().isEmpty()) continue;
                String[] dados = linha.split(";");

                if (dados[0].equals("FICHA")) {
                    fichaAtual = new FichaTecnica(dados[1], dados[2], dados[3], Double.parseDouble(dados[4]));
                    fichas.add(fichaAtual);

                } else if (dados[0].equals("ITEM") && fichaAtual != null) {
                    Produto p = encontrarProduto(estoque, dados[1]);
                    if (p != null) {
                        ItemFicha item = new ItemFicha(p, Double.parseDouble(dados[2]));
                        fichaAtual.adicionarItem(item);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao carregar fichas técnicas.");
        }
        return fichas;
    }
}