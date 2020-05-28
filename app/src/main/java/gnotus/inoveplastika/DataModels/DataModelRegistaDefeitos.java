package gnotus.inoveplastika.DataModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataModelRegistaDefeitos {

    // o campo código não está a ser utilizado
    @SerializedName("codigo")
    @Expose
    private String codigo;
    @SerializedName("descricao")
    @Expose
    private String descricao;
    @SerializedName("qtt")
    @Expose()
    private double qtt;

    public DataModelRegistaDefeitos(String codigo, String descricao, Double qtt) {

        this.codigo = codigo;
        this.descricao = descricao;
        this.qtt = qtt  ;


    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Double getQtt() {
        return qtt;
    }

    public void setQtt(double qtt) {

        this.qtt = qtt;
    }
}
