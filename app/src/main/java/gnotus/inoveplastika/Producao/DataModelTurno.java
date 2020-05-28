package gnotus.inoveplastika.Producao;



import java.util.Date;

public class DataModelTurno {

    private String bostamp;
    private String dataobra;
    private String operador;
    // Data de criação do registo
    private String ousrdata;
    private String datahoraini;
    private String datafinal;
    private String datahorafim;
    private Double horasextra;
    private Double tempoadicional;
    private int codigo;
    private String descricao;
    private int minleftrp;
    private int minleftfimturno;
    private String agora;
    public DataModelTurno() {


    }

    public String getBostamp() { return this.bostamp; }
    public String getDataobra() { return this.dataobra; }

    public void setBostamp(String bostamp) {
        this.bostamp = bostamp;
    }

    public void setDataobra(String dataobra) {
        this.dataobra = dataobra;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public Double getHorasextra() {
        return horasextra;
    }

    public void setHorasextra(Double horasextra) {
        this.horasextra = horasextra;
    }

    public Double getTempoadicional() {
        return tempoadicional;
    }

    public void setTempoadicional(Double tempoadicional) {
        this.tempoadicional = tempoadicional;
    }

    public String getOusrdata() {
        return ousrdata;
    }

    public void setOusrdata(String ousrdata) {
        this.ousrdata = ousrdata;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDatahoraini() {
        return datahoraini;
    }

    public void setDatahoraini(String datahoraini) {
        this.datahoraini = datahoraini;
    }

    public String getDatahorafim() {
        return datahorafim;
    }

    public void setDatahorafim(String datahorafim) {
        this.datahorafim = datahorafim;
    }

    public String getAgora() {
        return agora;
    }

    public int getMinleftrp() {
        return minleftrp;
    }

    public int getMinleftfimturno() {
        return minleftfimturno;
    }

    public String getDatafinal() {
        return datafinal;
    }
}
