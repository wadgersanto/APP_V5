package gnotus.inoveplastika.DataModels;


public class DataModelDescarga {

    private String referencia;
    private String lote;
    private String descricao;
    private double qt;
    private double qtAlt;

    private String unidade;
    private String unidadeAlt;

    private String armazem_o;
    private String zona_o;
    private String alveolo_o;

    private String armazem_d;
    private String zona_d;
    private String alveolo_d;

    private boolean usaQtAlt = false;


    public DataModelDescarga(String referencia, String lote, String descricao, double qt, double qtAlt, String unidade, String unidadeAlt, String armazem_o, String zona_o, String alveolo_o, String armazem_d, String zona_d, String alveolo_d, boolean usaQtAlt) {

        this.referencia = referencia;
        this.lote = lote;
        this.descricao = descricao;
        this.qt = qt;
        this.qtAlt = qtAlt;
        this.unidade = unidade;
        this.unidadeAlt = unidadeAlt;
        this.armazem_o = armazem_o;
        this.zona_o = zona_o;
        this.alveolo_o = alveolo_o;
        this.armazem_d = armazem_d;
        this.zona_d = zona_d;
        this.alveolo_d = alveolo_d;
        this.usaQtAlt = usaQtAlt;

    }

    public String getReferencia() {
        return this.referencia;
    }

    public String getLote() {
        return this.lote;
    }

    public String getDescricao() { return  this.descricao; }

    public double getQt() {
        return this.qt;
    }

    public double getQtAlt() { return this.qtAlt; }

    public String getUnidade() { return this.unidade; }

    public String getUnidadeAlt() { return this.unidadeAlt; }

    public String getArmazem_o() {
        return this.armazem_o;
    }

    public String getZona_o() {
        return this.zona_o;
    }

    public String getAlveolo_o() {
        return this.alveolo_o;
    }

    public String getArmazem_d() {
        return this.armazem_d;
    }

    public String getZona_d() {
        return this.zona_d;
    }

    public String getAlveolo_d() {
        return this.alveolo_d;
    }

    public boolean isUsaQtAlt() { return this.usaQtAlt; }


    public void setQt(double qt) {

        this.qt = qt + qt;
    }

    public void setQtAlt() { this.qtAlt = qtAlt++; }

}
