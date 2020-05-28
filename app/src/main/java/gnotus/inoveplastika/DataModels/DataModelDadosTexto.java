package gnotus.inoveplastika.DataModels;

public class DataModelDadosTexto {

    private String titulo;
    private String valor;


    public DataModelDadosTexto(String titulo,String valor) {

        this.titulo = titulo;
        this.valor = valor;


    }

    //Get methods

    public String getTitulo() { return this.titulo; }
    public String getValor  () { return this.valor; }

}

