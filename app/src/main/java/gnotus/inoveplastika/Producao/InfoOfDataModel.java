package gnotus.inoveplastika.Producao;

import com.google.gson.annotations.SerializedName;

import java.sql.Time;

public class InfoOfDataModel {


    private String ref;
    @SerializedName("obranome")
    private String numof;
    private Boolean fechada;
    private Boolean iniciada;
    private Boolean finalizada;
    private Boolean posconsumo;
    private Integer armazem;
    @SerializedName("zona1")
    private String zona;
    @SerializedName("identificacao1")
    private String alveolo;
    private Integer ndos;
    private Double qttprevista;
    private Double qttprod;
    private Boolean le_caixa_cb;
    private String primeira_entrada,ultima_entrada;
    private Integer ultima_cx;
    private Integer diasemprod;
    private Integer qttcx;
    private Integer minutes_to_deadline;
    private Integer minutes_to_alert;

    private Boolean complyIntervalBetweenBoxes;
    private Boolean tem_lim_dias;
    private Boolean tem_lim_qtt;
    private Boolean u_noinvfof;

    private Integer secsBetweenBoxes;
    private Integer os_corretivas_open;

    private String production_deadline;
    private String u_cbpeca;
    private String u_cbpadrao;


    public String getRef() {
        return ref.trim();
    }

    public String getNumof() {
        return numof.trim();
    }

    public Boolean getFechada() {
        return fechada;
    }

    public Boolean getIniciada() {
        return iniciada;
    }

    public Boolean getFinalizada() {
        return finalizada;
    }

    public Boolean getPosconsumo() {
        return posconsumo;
    }

    public Integer getArmazem() {
        return armazem;
    }

    public String getZona() {
        return zona.trim();
    }

    public String getAlveolo() {
        return alveolo.trim();
    }

    public Integer getNdos() {
        return ndos;
    }

    public Double getQttprevista() {
        return qttprevista;
    }

    public Double getQttprod() {
        return qttprod;
    }

    public Boolean getLe_caixa_cb() {
        return le_caixa_cb;
    }

    public String getPrimeira_entrada() {
        return primeira_entrada;
    }

    public String getUltima_entrada() {
        return ultima_entrada;
    }

    public Integer getUltima_cx() {
        return ultima_cx;
    }

    public Integer getDiasemprod() {
        return diasemprod;
    }

    public Integer getQttcx() {
        return qttcx;
    }

    public Boolean getU_noinvfof() {
        return u_noinvfof;
    }

    public Integer getMinutes_to_deadline() {
        return minutes_to_deadline;
    }
    public Integer getMinutes_to_alert() {
        return minutes_to_alert;
    }
    public Boolean getComplyIntervalBetweenBoxes() {
        return complyIntervalBetweenBoxes;
    }
    public Integer getSecsBetweenBoxes() {
        return secsBetweenBoxes;
    }
    public Boolean getTem_lim_dias() {
        return tem_lim_dias;
    }
    public Boolean getTem_lim_qtt() {
        return tem_lim_qtt;
    }
    public String getProduction_deadline() {
        return production_deadline;
    }
    public Integer getOs_corretivas_open() {
        return os_corretivas_open;
    }

    public String getU_cbpeca() {
        return u_cbpeca;
    }
    public String getU_cbpadrao() {
        return u_cbpadrao;
    }
}
