import java.util.ArrayList;

public class FichaTecnica {
    private String nome;
    private String categoria;
    private String unidadeRendimento; // ex: unidade, porção, fatias
    private double quantidadeRendimento; // ex: 1, 10
    private ArrayList<ItemFicha> itens;

    public FichaTecnica(String nome, String categoria, String unidadeRendimento, double quantidadeRendimento) {
        this.nome = nome;
        this.categoria = categoria;
        this.unidadeRendimento = unidadeRendimento;
        this.quantidadeRendimento = quantidadeRendimento;
        this.itens = new ArrayList<>();
    }

    public void adicionarItem(ItemFicha item) {
        this.itens.add(item);
    }

    // 🌟 SOMA O CUSTO TOTAL DA RECEITA
    public double getCustoTotal() {
        double total = 0;
        for (ItemFicha item : itens) {
            total += item.getCustoCalculado();
        }
        return total;
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUnidadeRendimento() {
        return unidadeRendimento;
    }

    public double getQuantidadeRendimento() {
        return quantidadeRendimento;
    }

    public ArrayList<ItemFicha> getItens() {
        return itens;
    }
}