package gnotus.inoveplastika.Producao;

public class DmPnokDef {

    private String Codigo;
    private String Descricao;
    // Guarda a descrição original para quando mudamos a descrição o tipo de defeito outos
    private String DescOriginal;
    // Guarda o input para o motivo "outros"
    private String DetalheOutros;
    private Double Qtt;
    // Se é o código de defeito outros
    private Boolean CDefOutros;

    public String getCodigo() {
        return Codigo;
    }

    public String getDescricao() {
        return Descricao;
    }

    public Double getQtt() {
        return Qtt;
    }

    public void setCodigo(String codigo) {
        this.Codigo = codigo;
    }

    public void setDescricao(String descricao) {
        this.Descricao = descricao;
    }

    public void setQtt(Double qtt) {
        this.Qtt = qtt;
    }


    public String getDescOriginal() {
        return DescOriginal;
    }

    public void setDescOriginal(String descOriginal) {
        DescOriginal = descOriginal;
    }

    public Boolean getCDefOutros() {
        return CDefOutros;
    }

    public void setCDefOutros(Boolean CDefOutros) {
        this.CDefOutros = CDefOutros;
    }

    public String getDetalheOutros() {
        return DetalheOutros;
    }

    public void setDetalheOutros(String detalheOutros) {
        DetalheOutros = detalheOutros;
    }
}
