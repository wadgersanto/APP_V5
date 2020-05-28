package gnotus.inoveplastika.Producao;

public class DataModelCxof {

    private String ref;
    private String numof;
    private String numcx;
    private String maquina;
    private int operador;
    private double qtt;
    private Boolean isSelected = false;


    public void setNumcx(String numcx) {
        this.numcx = numcx;
    }

    public String getRef() {
        return ref;
    }

    public String getNumof() {
        return numof;
    }

    public String getNumcx() {
        return numcx;
    }

    public String getMaquina() {
        return maquina;
    }

    public int getOperador() {
        return operador;
    }

    public double getQtt() {
        return qtt;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
