package gnotus.inoveplastika.Producao;

import java.util.List;

public class DmRPnok {


    private String Referencia;
    private String Lote;
    private Integer Tiporegisto;
    private Integer Armazem;
    private String Zona;
    private String Alveolo;
    private Integer Operador;
    private List<DmPnokDef> DefList;


    public DmRPnok(String referencia, String lote, Integer tiporegisto, Integer armazem, String zona, String alveolo, Integer operador, List<DmPnokDef> defList) {
        this.Referencia = referencia;
        this.Lote = lote;
        this.Tiporegisto = tiporegisto;
        this.Armazem = armazem;
        this.Zona = zona;
        this.Alveolo = alveolo;
        this.Operador = operador;
        this.DefList = defList;
    }


    public String getReferencia() {
        return Referencia;
    }

    public void setReferencia(String referencia) {
        Referencia = referencia;
    }

    public String getLote() {
        return Lote;
    }

    public void setLote(String lote) {
        Lote = lote;
    }

    public Integer getTiporegisto() {
        return Tiporegisto;
    }

    public void setTiporegisto(Integer tiporegisto) {
        Tiporegisto = tiporegisto;
    }

    public Integer getArmazem() {
        return Armazem;
    }

    public void setArmazem(Integer armazem) {
        Armazem = armazem;
    }

    public String getZona() {
        return Zona;
    }

    public void setZona(String zona) {
        Zona = zona;
    }

    public String getAlveolo() {
        return Alveolo;
    }

    public void setAlveolo(String alveolo) {
        Alveolo = alveolo;
    }

    public Integer getOperador() {
        return Operador;
    }

    public void setOperador(Integer operador) {
        Operador = operador;
    }

    public List<DmPnokDef> getDefList() {
        return DefList;
    }

    public void setDefList(List<DmPnokDef> defList) {
        DefList = defList;
    }

    public DmRPnok() {

    }

}
