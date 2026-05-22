import java.util.ArrayList;

public class ListaCompra {
    private String nome;
    private String previsaoRecebimento;
    private String dataFinalizada;
    private String status; // "Aguardando Recebimento" ou "Finalizada"
    private String criadoPor;
    private ArrayList<ItemCompra> itens;

    public ListaCompra(String nome, String previsaoRecebimento, String criadoPor) {
        this.nome = nome;
        this.previsaoRecebimento = previsaoRecebimento;
        this.criadoPor = criadoPor;
        this.status = "Aguardando Recebimento";
        this.dataFinalizada = "";
        this.itens = new ArrayList<>();
    }

    public void adicionarItem(ItemCompra item) {
        this.itens.add(item);
    }

    // Verifica se todos os itens da lista já receberam o "Check"
    public boolean todosItensChecados() {
        for (ItemCompra item : itens) {
            if (!item.isChecado()) {
                return false;
            }
        }
        return true;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public String getPrevisaoRecebimento() { return previsaoRecebimento; }
    public String getStatus() { return status; }
    public String getCriadoPor() { return criadoPor; }
    public String getDataFinalizada() { return dataFinalizada; }
    public ArrayList<ItemCompra> getItens() { return itens; }

    public void setStatus(String status) { this.status = status; }
    public void setDataFinalizada(String dataFinalizada) { this.dataFinalizada = dataFinalizada; }

    // =================================================================
    // 🌟 NOVO: PREPARA OS DADOS PARA O BANCO DE DADOS EM ARQUIVO
    // =================================================================
    public ArrayList<String> exportarDados() {
        ArrayList<String> linhas = new ArrayList<>();

        String dataFim = (dataFinalizada == null || dataFinalizada.isEmpty()) ? "N/A" : dataFinalizada;

        // 1. Exporta o "Cabeçalho" da lista
        linhas.add("LISTA;" + nome + ";" + previsaoRecebimento + ";" + criadoPor + ";" + status + ";" + dataFim);

        // 2. Exporta cada produto que está no carrinho
        for (ItemCompra item : itens) {
            String obs = (item.getObservacao() == null || item.getObservacao().isEmpty()) ? "N/A" : item.getObservacao();
            linhas.add("ITEM;" + nome + ";" + item.getProduto().getNome() + ";" +
                    item.getQuantidadeSolicitada() + ";" + item.getQuantidadeRecebida() + ";" +
                    item.isChecado() + ";" + obs);
        }

        return linhas;
    }
}