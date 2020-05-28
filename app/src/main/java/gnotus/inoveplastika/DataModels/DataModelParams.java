package gnotus.inoveplastika.DataModels;

public class DataModelParams {

    private String descricao;
    private String valor;


    public DataModelParams() {


    }

    public Boolean getValorBoolean ()
    {

        Boolean boolRetVal;
        try {
            boolRetVal = Boolean.parseBoolean(this.valor);
            }
            catch (Exception e) {
        boolRetVal = false;
        // Handle the error/exception
        }
        return  boolRetVal;

    }

    public Double getValorDouble ()
    {
        Double doubleRetVal;
        try {
            doubleRetVal = Double.parseDouble(this.valor);
        }
        catch (Exception e) {
            doubleRetVal = 0.0;
            // Handle the error/exception
        }
        return  doubleRetVal;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getValor() {
        return valor;
    }


}

