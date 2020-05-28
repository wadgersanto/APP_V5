package gnotus.inoveplastika.Suporte;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Iterator;

public class DataModelTipoEquip {


    private String tipoequip;
    private Integer tipoContador;


    public String getTipoequip() {
        return tipoequip;
    }

    public Integer getTipoContador() {
        return tipoContador;
    }

}

