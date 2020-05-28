package gnotus.inoveplastika.DataModels;

import com.google.gson.annotations.SerializedName;

public class DataModelWsResponse {

    @SerializedName("Codigo")
    private int codigo;
    @SerializedName("Descricao")
    private String descricao;


    public DataModelWsResponse() {


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

}

