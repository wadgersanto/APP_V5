package gnotus.inoveplastika.DataModels;


public class DataModelBi {


    private String  bistamp = "";
    private String  bostamp = "";
    private String  referencia = "";
    private String  ref = "";
    private String  design = ""; // designação do código do artigo em vez da descricao da linha
    private String  lote ="";
    private String  descricao = "";
    private String  unidade = "";
    private String  unidad2 = "";
    private String  dataobra;
    private String  rdata;
    private double  qtt = 0;
    private double  uni2qtt = 0;
    private double  qtt2 = 0;
    private double  fconversao = 0.0;
    private int     armazem =0;
    private int     ar2mazem =0;
    private int     binum1 = 0;
    private String  zona1 = "";
    private String  alveolo1 = "";
    private String  zona2 = "";
    private String  alveolo2 = "";
    private String  identificacao1 = "";
    private String  identificacao2 = "";
    private boolean u_jacontou = false;
    private String  qttpend;
    private String  u_qttconv;
    private String  uni2;
    private String  uni2qttpend;
    private boolean usalote;
    private String  lobs;
    private int     no;

    public String getLobs() {
        return lobs;
    }

    public void setLobs(String lobs) {
        this.lobs = lobs;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getQttpend() {
        return qttpend;
    }

    public void setQttpend(String qttpend) {
        this.qttpend = qttpend;
    }

    public String getU_qttconv() {
        return u_qttconv;
    }

    public void setU_qttconv(String u_qttconv) {
        this.u_qttconv = u_qttconv;
    }

    public String getUni2() {
        return uni2;
    }

    public void setUni2(String uni2) {
        this.uni2 = uni2;
    }

    public String getUni2qttpend() {
        return uni2qttpend;
    }

    public void setUni2qttpend(String uni2qttpend) {
        this.uni2qttpend = uni2qttpend;
    }

    private double num1;

    private String nome = "";

    private Integer operador = 0;

    public DataModelBi(String bistamp, String referencia, String lote, String descricao, double qtt, double uni2qtt, double qtt2, int binum1, String unidade,
                       String unidad2, int armazem, int ar2mazem, String zona1, String alveolo1, String zona2, String alveolo2, Double fconversao,
                       boolean u_jacontou) {

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
        this.zona1 = zona1;
        this.alveolo1 = alveolo1;
        this.zona2 = zona2;
        this.alveolo2 = alveolo2;
        this.fconversao = fconversao;
        this.u_jacontou = u_jacontou;
        this.binum1 = binum1;

    }

    public DataModelBi() {

    }


    //Get methods


    public boolean isUsalote() {
        return usalote;
    }

    public void setUsalote(boolean usalote) {
        this.usalote = usalote;
    }

    public String getBistamp() { return this.bistamp; }

    public String getBostamp() { return this.bostamp; }

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
    public int getBinum1() {return this.binum1;}

    public String getZona1() {return this.zona1.trim();}
    public String getAlveolo1() {return this.alveolo1.trim();}
    public String getZona2() {return this.zona2.trim();}
    public String getAlveolo2() {return this.alveolo2.trim();}
    public Double getFconversao() {return this.fconversao;}
    public Boolean getJacontou() {return this.u_jacontou;}

    public String getDataobra() {
        return dataobra;
    }

    public String getRdata() {return rdata;}

    public String getRef() {
        return ref.trim();
    }

    public String getDesign() {
        return design;
    }
//Set methods


    public void setQt(double qt) {

        this.qtt = qtt + qtt;
    }

    public String getIdentificacao2() {
        return identificacao2;
    }

    public void setIdentificacao2(String identificacao2) {
        this.identificacao2 = identificacao2;
    }

    public String getIdentificacao1() {
        return identificacao1;
    }

    public void setIdentificacao1(String identificacao1) {
        this.identificacao1 = identificacao1;
    }

    public void setBistamp(String bistamp) {
        this.bistamp = bistamp;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public void setUnidad2(String unidad2) {
        this.unidad2 = unidad2;
    }

    public void setDataobra(String dataobra) {
        this.dataobra = dataobra;
    }

    public void setRdata(String rdata) {
        this.rdata = rdata;
    }

    public void setQtt(double qtt) {
        this.qtt = qtt;
    }

    public void setUni2qtt(double uni2qtt) {
        this.uni2qtt = uni2qtt;
    }

    public void setQtt2(double qtt2) {
        this.qtt2 = qtt2;
    }

    public void setFconversao(double fconversao) {
        this.fconversao = fconversao;
    }

    public void setArmazem(int armazem) {
        this.armazem = armazem;
    }

    public void setAr2mazem(int ar2mazem) {
        this.ar2mazem = ar2mazem;
    }

    public void setBinum1(int binum1) {
        this.binum1 = binum1;
    }

    public void setZona1(String zona1) {
        this.zona1 = zona1;
    }

    public void setAlveolo1(String alveolo1) {
        this.alveolo1 = alveolo1;
    }

    public void setZona2(String zona2) {
        this.zona2 = zona2;
    }

    public void setAlveolo2(String alveolo2) {
        this.alveolo2 = alveolo2;
    }

    public void setJacontou(boolean u_jacontou) {
        this.u_jacontou = u_jacontou;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public void setDesign(String design) {
        this.design = design;
    }

    public void setBostamp(String bostamp) {
        this.bostamp = bostamp;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getOperador() {
        return operador;
    }

    public void setOperador(Integer operador) {
        this.operador = operador;
    }

    public double getNum1() {
        return num1;
    }

    public void setNum1(double num1) {
        this.num1 = num1;
    }
}
