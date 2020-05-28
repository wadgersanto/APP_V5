package gnotus.inoveplastika.DataModels;

public class DataModelValidate
{
    private String descricao;
    private String tipo;
    private int    decimais;
    private String obrigatorio;
    private String listaValores;
    private String logicYes;
    private String logicNo;
    private int    limInf;
    private int    limSup;
    private String choosed = "";
    private String observacao = "";
    private boolean allChecked = false;


    public DataModelValidate() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getDecimais() {
        return decimais;
    }

    public void setDecimais(int decimais) {
        this.decimais = decimais;
    }

    public String getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(String obrigatorio) {
        this.obrigatorio = obrigatorio;
    }

    public String getListaValores() {
        return listaValores;
    }

    public void setListaValores(String listaValores) {
        this.listaValores = listaValores;
    }

    public String getLogicYes() {
        return logicYes;
    }

    public void setLogicYes(String logicYes) {
        this.logicYes = logicYes;
    }

    public String getLogicNo() {
        return logicNo;
    }

    public void setLogicNo(String logicNo) {
        this.logicNo = logicNo;
    }

    public int getLimInf() {
        return limInf;
    }

    public void setLimInf(int limInf) {
        this.limInf = limInf;
    }

    public int getLimSup() {
        return limSup;
    }

    public void setLimSup(int limSup) {
        this.limSup = limSup;
    }

    public String getChoosed() {
        return choosed;
    }

    public void setChoosed(String choosed) {
        this.choosed = choosed;
    }

    public boolean isAllChecked() {
        return allChecked;
    }

    public void setAllChecked(boolean allChecked) {
        this.allChecked = allChecked;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
