package gnotus.inoveplastika.DataModels;


import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class DataModelConsultaStock {

    @SerializedName("ref")
    private String referencia;
    private String lote;
    @SerializedName("design")
    private String descricao;
    private String unidade;
    private String uni2;

    private double stock = 0;
    private double stock2 = 0;
    
    private int armazem;

    private String zona;
    private String alveolo;


    public DataModelConsultaStock(String referencia, String lote, String descricao, double stock, double stock2, String unidade,
                                  String uni2,int armazem, String zona, String alveolo) {

        this.referencia = referencia.trim();
        this.lote = lote.trim();
        this.descricao = descricao;
        this.stock = stock;
        this.stock2 = stock2;
        this.unidade = unidade;
        this.uni2 = uni2  ;
        this.armazem = armazem;
        this.zona= zona;
        this.alveolo = alveolo;


    }

    public DataModelConsultaStock(JSONObject jsonObject) {


        try {

            if (jsonObject.has("ref"))  this.referencia = Objects.toString(jsonObject.getString("ref").trim(),"") ;
            if (jsonObject.has("design"))  this.descricao = Objects.toString(jsonObject.getString("design").trim(),"");
            if (jsonObject.has("lote"))  this.lote = Objects.toString(jsonObject.getString("lote").trim(),"");
            if (jsonObject.has("stock"))  this.stock = jsonObject.getDouble("stock");
            if (jsonObject.has("stock2"))  this.stock2 = jsonObject.getDouble("stock2");
            if (jsonObject.has("unidade"))  this.unidade = Objects.toString(jsonObject.getString("unidade").trim(),"");
            if (jsonObject.has("uni2"))  this.uni2 = Objects.toString(jsonObject.getString("uni2").trim(),"");
            if (jsonObject.has("armazem"))  this.armazem = jsonObject.getInt("armazem");
            if (jsonObject.has("zona"))  this.zona = Objects.toString(jsonObject.getString("zona").trim(),"");
            if (jsonObject.has("alveolo"))  this.alveolo = Objects.toString(jsonObject.getString("alveolo").trim(),"");


        }

        catch (final JSONException e) {
            Log.e("", "Erro Conversão: " + e.getMessage());
        }

    }

    //Get methods


    public String getReferencia() { return this.referencia.trim();}

    public String getLote() {return this.lote.trim();}


    public String getUnidade() { return this.unidade.trim(); }

    public String getUni2() { return this.uni2.trim(); }


    public String getDescricao() {
        return this.descricao.trim();
    }


    public double getStock() {
        return this.stock;
    }

    public double getStock2() { return this.stock2; }

    public int getArmazem() {return this.armazem;}

    public String getZona() {return this.zona.trim();}
    public String getAlveolo() {return this.alveolo.trim();}


    //Set methods


    public void setQt(double qt) {


    }


}
