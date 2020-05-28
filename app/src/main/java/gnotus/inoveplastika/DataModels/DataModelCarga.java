package gnotus.inoveplastika.DataModels;


public class DataModelCarga {

    private String bistamp;
    private String referencia;
    private String lote;
    private String descricao;
    private String unidade;
    private String unidadeAlt;

    private double qt = 0;
    private double qtAlt = 0;

    private String armazem;
    private String ar2mazem;
    private String zona1;
    private String alveolo1;
    private String zona2;
    private String alveolo2;



    public DataModelCarga(String bistamp, String referencia, String lote, String descricao, double qt, double qtAlt, String unidade, String unidadeAlt,String armazem,
                          String ar2mazem,String zona1, String alveolo1, String zona2, String alveolo2) {

        this.bistamp = bistamp;
        this.referencia = referencia;
        this.lote = lote;
        this.descricao = descricao;
        this.qt = qt;
        this.qtAlt = qtAlt;
        this.unidade = unidade;
        this.unidadeAlt = unidadeAlt;
        this.armazem = armazem;
        this.ar2mazem = ar2mazem;
        this.zona1= zona1;
        this.alveolo1 = alveolo1;
        this.zona2= zona2;
        this.alveolo2 = alveolo2;
    }

    //Get methods

    public String getBistamp() { return this.bistamp; }

    public String getReferencia() {
        return this.referencia;
    }

    public String getLote() {
        return this.lote;
    }

    public String getUnidade() { return this.unidade; }


    public String getUnidadeAlt() { return this.unidadeAlt; }


    public String getDescricao() {
        return this.descricao;
    }


    public double getQt() {
        return this.qt;
    }

    public double getQtAlt() { return this.qtAlt; }

    public String getArmazem() {return this.armazem.trim();}
    public String getAr2mazem() {return this.ar2mazem.trim();}
    public String getZona1() {return this.zona1.trim();}
    public String getAlveolo1() {return this.alveolo1.trim();}
    public String getZona2() {return this.zona2.trim();}
    public String getAlveolo2() {return this.alveolo2.trim();}


    //Set methods


    public void setQt(double qt) {

        this.qt = qt + qt;
    }

    public void setQtAlt() {

        this.qtAlt++;
    }

    public void setQtM(double qtM) {

        this.qt = qtM;

    }

    public void setQtAltM(double qtAltM) {

        this.qtAlt = qtAltM;
    }


    public void setQtD() {

        this.qt = qt--;

    }

     public void setQtAltD(double qtdAltD) {

         this.qtAlt = qtAlt - qtdAltD;
     }

}
