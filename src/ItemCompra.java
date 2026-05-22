public class ItemCompra {
    private Produto produto;
    private double quantidadeSolicitada;
    private double quantidadeRecebida;
    private String observacao;
    private boolean checado; // O quadradinho de "check" que você desenhou

    public ItemCompra(Produto produto, double quantidadeSolicitada) {
        this.produto = produto;
        this.quantidadeSolicitada = quantidadeSolicitada;
        this.quantidadeRecebida = 0; // Começa zerado até a entrega chegar
        this.observacao = "";
        this.checado = false;
    }

    // Getters e Setters
    public Produto getProduto() { return produto; }
    public double getQuantidadeSolicitada() { return quantidadeSolicitada; }
    public double getQuantidadeRecebida() { return quantidadeRecebida; }
    public String getObservacao() { return observacao; }
    public boolean isChecado() { return checado; }

    public void setQuantidadeRecebida(double quantidadeRecebida) { this.quantidadeRecebida = quantidadeRecebida; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public void setChecado(boolean checado) { this.checado = checado; }
}