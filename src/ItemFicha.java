public class ItemFicha {
    private Produto produto;
    private double quantidadeUso;

    public ItemFicha(Produto produto, double quantidadeUso) {
        this.produto = produto;
        this.quantidadeUso = quantidadeUso;
    }

    public Produto getProduto() {
        return produto;
    }

    public double getQuantidadeUso() {
        return quantidadeUso;
    }

    // 🌟 A MÁGICA DO CUSTO FRACIONADO AQUI:
    // Ele pega o Custo Médio do seu estoque e multiplica pela fração usada na
    // receita
    public double getCustoCalculado() {
        return this.quantidadeUso * produto.getPrecoPorUnidade();
    }
}