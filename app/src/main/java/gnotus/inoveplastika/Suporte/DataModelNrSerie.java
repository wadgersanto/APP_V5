package gnotus.inoveplastika.Suporte;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class DataModelNrSerie {

    @SerializedName("tipoEquip")
    private String tipoEquip;
    @SerializedName("nrSerie")
    private String nrSerie;

    private Integer ctInjecoes;
    private Integer ctHoras;



    @SerializedName("injActuais")
    private Integer injActuais;


    public DataModelNrSerie(JSONObject jsonObject) {

        try {

            if (jsonObject.has("tipoequip"))  this.tipoEquip = Objects.toString(jsonObject.getString("tipoequip")) ;
            if (jsonObject.has("nserie"))  this.nrSerie = Objects.toString(jsonObject.getString("nserie"));
            if (jsonObject.has("injActuais"))  this.injActuais = jsonObject.getInt("injActuais");

        }

        catch (final JSONException e) {
            Log.e("", "Erro Conversão: " + e.getMessage());
        }

    }

    public String getTipoEquip() {
        return tipoEquip;
    }

    public String getNrSerie() {
        return nrSerie;
    }

    public Integer getCtInjecoes() {
        return ctInjecoes;
    }

    public Integer getCtHoras() {
        return ctHoras;
    }

    public Integer getInjActuais() {
        return injActuais;
    }
}
