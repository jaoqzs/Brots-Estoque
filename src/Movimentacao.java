public class Movimentacao {
    private String tipo;
    private String classificacao;
    private double quantidade;
    private double precoUnitario;
    private String dataMovimentacao;
    private String dataValidade;
    private String observacoes;

    public Movimentacao(String tipo, String classificacao, double quantidade,
                        double precoUnitario, String dataMovimentacao,
                        String dataValidade, String observacoes) {
        this.tipo = tipo;
        this.classificacao = classificacao;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.dataMovimentacao = dataMovimentacao;
        this.dataValidade = dataValidade;
        this.observacoes = observacoes;
    }

    // Método para o sistema injetar mensagens automáticas nas observações
    public void adicionarObservacaoAutomatica(String textoExtra) {
        if (this.observacoes == null || this.observacoes.trim().isEmpty()) {
            this.observacoes = textoExtra; // Se não tinha observação, coloca só o texto extra
        } else {
            this.observacoes += " | " + textoExtra; // Se já tinha, junta os dois
        }
    }

    @Override
    public String toString() {
        String sinal = tipo.equals("Entrada") ? "+" : (tipo.equals("Saída") ? "-" : "=");

        // Criamos uma mensagem de validade apenas se ela existir
        String infoValidade = (dataValidade != null && !dataValidade.isEmpty())
                ? " | Validade: " + dataValidade
                : "";

        return String.format("[%s] Data: %s | %s | Qtd: %s %.2f %s | Obs: %s",
                tipo.toUpperCase(), dataMovimentacao, classificacao,
                sinal, quantidade, infoValidade, observacoes);
    }

    // Método para o GerenciadorDeArquivo salvar no bloco de notas
    public String paraTexto(String nomeProduto) {
        String dataVal = (dataValidade == null || dataValidade.trim().isEmpty()) ? "N/A" : dataValidade;
        String obs = (observacoes == null || observacoes.trim().isEmpty()) ? "N/A" : observacoes;

        return "MOVIMENTACAO;" + nomeProduto + ";" + tipo + ";" + classificacao + ";" +
                quantidade + ";" + precoUnitario + ";" + dataMovimentacao + ";" + dataVal + ";" + obs;
    }
}