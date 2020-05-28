package gnotus.inoveplastika.DataModels;
import com.google.gson.annotations.SerializedName;

public class DataModelCargaPicking {


    private String bistamp;
    private String referencia;
    private String lote;
    private String descricao;
    private String unidade;
    private String unidad2;

    private double qtt = 0,qtt2 = 0,uni2qtt = 0, fconversao = 0;

    private int armazem,ar2mazem;
    private String zona1,zona2,alveolo1,alveolo2,clientepicking,obranome;


    public DataModelCargaPicking(String bistamp, String referencia, String lote, String descricao, double qtt, double uni2qtt, double qtt2, String unidade,
                       String unidad2, int armazem, int ar2mazem,String zona1, String alveolo1, String zona2, String alveolo2,
                                 Double fconversao, String clientepicking, String obranome) {

        this.bistamp = bistamp;
        this.referencia = referencia;
        this.lote = lote;
        this.descricao = descricao;
        this.qtt = qtt;
        this.qtt2 = qtt2;
        this.unidade = unidade;
        this.unidad2 = unidad2  ;
        this.armazem = armazem;
        this.ar2mazem = ar2mazem;
        this.zona1= zona1;
        this.alveolo1 = alveolo1;
        this.zona2= zona2;
        this.alveolo2 = alveolo2;
        this.fconversao = fconversao;
        this.clientepicking = clientepicking;
        this.obranome = obranome;

    }

    //Get methods

    public String getBistamp() { return this.bistamp; }

    public String getReferencia() { return this.referencia.trim();}

    public String getLote() {return this.lote.trim();}

    public String getUnidade() { return this.unidade.trim(); }

    public String getUnidad2() { return this.unidad2.trim(); }

    public String getDescricao() {
        return this.descricao.trim();
    }

    public double getQtt() {
        return this.qtt;
    }
    public double getUni2qtt() { return this.uni2qtt; }

    public double getQtt2() { return this.qtt2; }

    public int getArmazem() {return this.armazem;}
    public int getAr2mazem() {return this.ar2mazem;}

    public String getZona1() {return this.zona1.trim();}
    public String getAlveolo1() {return this.alveolo1.trim();}
    public String getZona2() {return this.zona2.trim();}
    public String getAlveolo2() {return this.alveolo2.trim();}
    public Double getFconversao() {return this.fconversao;}
    public String getClientepicking() {return this.clientepicking.trim();}
    public String getObranome() {return this.obranome.trim();}


}
