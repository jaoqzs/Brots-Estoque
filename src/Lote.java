import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Lote {
    private double quantidade;
    private LocalDate dataValidade;
    private String codigoLote;
    private String idInterno;

    // 🌟 MUDANÇA AQUI: Adicionamos o "String nomeProduto" no começo para gerar a sigla
    public Lote(String nomeProduto, double quantidade, String dataValidadeStr, String codigoLote) {
        this.quantidade = quantidade;
        this.codigoLote = (codigoLote == null || codigoLote.trim().isEmpty()) ? "N/A" : codigoLote;

        if (dataValidadeStr == null || dataValidadeStr.trim().isEmpty()) {
            this.dataValidade = null;
        } else {
            try {
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                this.dataValidade = LocalDate.parse(dataValidadeStr, formato);
            } catch (DateTimeParseException e) {
                System.out.println("⚠️ Formato de data inválido. Lote salvo sem validade.");
                this.dataValidade = null;
            }
        }

        // 🌟 NOVO: Agora ele gera o ID na hora que nasce!
        this.idInterno = gerarIdInterno(nomeProduto);
    }

    // 🌟 O "MOTOR" QUE GERA O ID (Encaixado no seu código)
    private String gerarIdInterno(String nome) {
        String prefixo = "PROD";
        if (nome != null && nome.length() >= 3) {
            prefixo = nome.substring(0, 3).toUpperCase();
        } else if (nome != null && !nome.isEmpty()) {
            prefixo = nome.toUpperCase();
        }

        LocalDate hoje = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("ddMM");
        String dataStr = hoje.format(fmt);

        int random = (int) (Math.random() * 900) + 100;
        return prefixo + "-" + dataStr + "-" + random;
    }

    // 🌟 NOVO: Adicionado o Getter do ID para o sistema conseguir ler
    public String getIdInterno() { return idInterno; }

    // --- SEUS MÉTODOS ORIGINAIS INTACTOS ABAIXO ---

    public double getQuantidade() { return quantidade; }

    public void setQuantidade(double quantidade) { this.quantidade = quantidade; }

    public LocalDate getDataValidade() { return dataValidade;}

    public String getDataValidadeFormatada() {
        if (dataValidade == null) return "N/A (Sem validade)";
        return dataValidade.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getCodigoLote() { return codigoLote; }
}