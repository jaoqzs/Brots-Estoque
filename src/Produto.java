import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Produto {

    private String tipo;
    private String nome;
    private String unidade;
    private double estoqueMinimo;
    private double precoPorUnidade;
    private String categoria;
    private String fornecedor;
    private boolean compoeCmv;
    private String regraPreco;

    // 🌟 MUDANÇA 1: A nossa nova variável para as Etiquetas!
    private int diasValidadeAberto;

    private ArrayList<Lote> lotes = new ArrayList<>();
    private ArrayList<Movimentacao> historico = new ArrayList<>();

    // 🌟 MUDANÇA 2: Adicionámos o "int diasValidadeAberto" no final dos parênteses
    public Produto(String tipo, String nome, String unidade, double estoqueMinimo,
                   double quantidadeInicial, String dataValidadeInicial, String codigoLoteInicial, double precoPorUnidade,
                   String categoria, String fornecedor, boolean compoeCmv, String regraPreco, int diasValidadeAberto) {

        this.tipo = tipo;
        this.nome = nome;
        this.unidade = unidade;
        this.estoqueMinimo = estoqueMinimo;
        this.precoPorUnidade = precoPorUnidade;
        this.categoria = categoria;
        this.fornecedor = fornecedor;
        this.compoeCmv = compoeCmv;
        this.regraPreco = regraPreco;

        // 🌟 MUDANÇA 3: Guardamos a validade após aberto
        this.diasValidadeAberto = diasValidadeAberto;

        if (quantidadeInicial > 0) {
            // 🌟 CORREÇÃO IMPORTANTE: Agora passamos "this.nome" para o Lote poder gerar o ID dele!
            Lote lote1 = new Lote(this.nome, quantidadeInicial, dataValidadeInicial, codigoLoteInicial);
            this.lotes.add(lote1);
        }
    }

    // GETTERS BÁSICOS
    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public String getFornecedor() { return fornecedor; }
    public String getUnidade() { return unidade; }
    public double getEstoqueMinimo() { return estoqueMinimo; }
    public double getPrecoPorUnidade() { return precoPorUnidade; }
    public ArrayList<Movimentacao> getHistorico() { return historico; }
    public ArrayList<Lote> getLotes() { return lotes; }
    public String getRegraPreco() { return regraPreco; }
    public String getTipo() { return tipo; }

    // 🌟 MUDANÇA 4: O Getter da nossa nova variável
    public int getDiasValidadeAberto() { return diasValidadeAberto; }

    public void setNome(String nome) { this.nome = nome; }
    public void setUnidade(String unidade) { this.unidade = unidade; }
    public void setEstoqueMinimo(double estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setFornecedor(String fornecedor) { this.fornecedor = fornecedor; }
    public void setRegraPreco(String regraPreco) { this.regraPreco = regraPreco; }

    // 🌟 MUDANÇA 5: O Setter da nossa nova variável
    public void setDiasValidadeAberto(int diasValidadeAberto) { this.diasValidadeAberto = diasValidadeAberto; }

    public double getQuantidadeAtual() {
        double total = 0;
        for (Lote l : lotes) {
            total += l.getQuantidade();
        }
        return total;
    }

    public double getValorTotalEstoque() {
        return getQuantidadeAtual() * precoPorUnidade;
    }

    public void registrarEntrada(Lote novoLote, double novoPreco, Movimentacao mov) {
        double qtdAntiga = this.getQuantidadeAtual();
        double qtdNova = novoLote.getQuantidade();

        if (this.regraPreco != null && (this.regraPreco.equalsIgnoreCase("Médio") || this.regraPreco.equalsIgnoreCase("Medio"))) {
            if (qtdAntiga > 0) {
                double valorTotalAntigo = qtdAntiga * this.precoPorUnidade;
                double valorTotalNovo = qtdNova * novoPreco;
                this.precoPorUnidade = (valorTotalAntigo + valorTotalNovo) / (qtdAntiga + qtdNova);
            } else {
                this.precoPorUnidade = novoPreco;
            }
        } else {
            this.precoPorUnidade = novoPreco;
        }

        this.lotes.add(novoLote);
        this.historico.add(mov);

        Collections.sort(this.lotes, new Comparator<Lote>() {
            @Override
            public int compare(Lote l1, Lote l2) {
                if (l1.getDataValidade() == null && l2.getDataValidade() == null) return 0;
                if (l1.getDataValidade() == null) return 1;
                if (l2.getDataValidade() == null) return -1;
                return l1.getDataValidade().compareTo(l2.getDataValidade());
            }
        });
    }

    public void registrarSaida(double qtdSaida, Movimentacao mov) {
        if (qtdSaida > getQuantidadeAtual()) {
            System.out.println("❌ Erro: Quantidade insuficiente no estoque! Saldo atual: " + getQuantidadeAtual());
            return;
        }

        double quantidadeRestanteParaTirar = qtdSaida;
        ArrayList<Lote> lotesVazios = new ArrayList<>();
        String detalhesConsumo = "FEFO Automático: ";

        for (Lote lote : lotes) {
            if (quantidadeRestanteParaTirar <= 0) break;

            double qtdTiradaDesteLote = 0;

            if (lote.getQuantidade() <= quantidadeRestanteParaTirar) {
                qtdTiradaDesteLote = lote.getQuantidade();
                quantidadeRestanteParaTirar -= lote.getQuantidade();
                lote.setQuantidade(0);
                lotesVazios.add(lote);
            } else {
                qtdTiradaDesteLote = quantidadeRestanteParaTirar;
                lote.setQuantidade(lote.getQuantidade() - quantidadeRestanteParaTirar);
                quantidadeRestanteParaTirar = 0;
            }

            detalhesConsumo += String.format("[Tirou %.2f do lote val: %s] ",
                    qtdTiradaDesteLote, lote.getDataValidadeFormatada());
        }

        lotes.removeAll(lotesVazios);
        mov.adicionarObservacaoAutomatica(detalhesConsumo);
        this.historico.add(mov);
    }

    public void registrarInventario(double novaQuantidadeReal, String novaValidade, Movimentacao mov) {
        this.lotes.clear();
        if (novaQuantidadeReal > 0) {
            // 🌟 CORREÇÃO: Passando o nome do produto para o Inventário também!
            this.lotes.add(new Lote(this.nome, novaQuantidadeReal, novaValidade, "N/A"));
        }
        this.historico.add(mov);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Categoria: %s | Qtd: %.2f %s | Valor Total: R$ %.2f",
                tipo, nome, categoria, getQuantidadeAtual(), unidade, getValorTotalEstoque());
    }

    public ArrayList<String> exportarDados() {
        ArrayList<String> linhas = new ArrayList<>();

        // 🌟 MUDANÇA 6: Adicionámos o diasValidadeAberto no final para gravar no ficheiro!
        linhas.add("PRODUTO;" + tipo + ";" + nome + ";" + unidade + ";" + estoqueMinimo + ";" +
                precoPorUnidade + ";" + categoria + ";" + fornecedor + ";" + compoeCmv + ";" + regraPreco + ";" + diasValidadeAberto);

        for (Lote l : lotes) {
            String val = l.getDataValidade() == null ? "N/A" : l.getDataValidadeFormatada();
            if(val.contains("N/A")) val = "N/A";

            linhas.add("LOTE;" + nome + ";" + l.getQuantidade() + ";" + val + ";" + l.getCodigoLote());
        }

        for (Movimentacao m : historico) {
            linhas.add(m.paraTexto(nome));
        }

        return linhas;
    }
}