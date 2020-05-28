package gnotus.inoveplastika.DataModels;

public class DataModelBo
{
    private String bostamp;
    private String nmdos;
    private String nome;
    private String nome2;
    private String dataobra;
    private int obrano;
    private int no;
    private int estab;
    private int ndos;
    private int u_operador;
    private String u_vdocumet = "";

    public String getU_vdocumet() {
        return u_vdocumet;
    }

    public void setU_vdocumet(String u_vdocumet) {
        this.u_vdocumet = u_vdocumet;
    }

    public String getVossodoc() {
        return vossodoc;
    }

    public void setVossodoc(String vossodoc) {
        this.vossodoc = vossodoc;
    }

    private String vossodoc;


    public String getBostamp() {
        return bostamp;
    }

    public void setBostamp(String bostamp) {
        this.bostamp = bostamp;
    }

    public String getNmdos() {
        return nmdos;
    }

    public void setNmdos(String nmdos) {
        this.nmdos = nmdos;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome2() {
        return nome2;
    }

    public void setNome2(String nome2) {
        this.nome2 = nome2;
    }

    public String getDataobra() {
        return dataobra;
    }

    public void setDataobra(String dataobra) {
        this.dataobra = dataobra;
    }

    public int getObrano() {
        return obrano;
    }

    public void setObrano(int obrano) {
        this.obrano = obrano;
    }

    public int getNo() {
        return no;
    }
    public void setNo(int no) {
        this.no = no;
    }

    public int getNdos() {
        return ndos;
    }
    public void setNdos(int ndos) {
        this.ndos = ndos;
    }

    public int getU_operador() {
        return u_operador;
    }
    public void setU_operador(int u_operador) {
        this.u_operador = u_operador;
    }

    public int getEstab() {
        return estab;
    }
    public void setEstab(int estab) {
        this.estab = estab;
    }
}
