package gnotus.inoveplastika.DataModels;

public class DataModelDocDescarga {

    private String bostamp;
    private String dataobra;

    public DataModelDocDescarga(String bostamp,String dataobra) {

        this.bostamp = bostamp;
        this.dataobra = dataobra;

    }

    public String getBostamp() { return this.bostamp; }

    public String getDataobra() { return this.dataobra; }

}
