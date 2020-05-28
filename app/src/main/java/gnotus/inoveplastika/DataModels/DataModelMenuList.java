package gnotus.inoveplastika.DataModels;

public class DataModelMenuList {

    private String menuopcao;
    private String menuid;


    public DataModelMenuList(String menuopcao, String menuid) {

        this.menuopcao = menuopcao;
        this.menuid = menuid    ;


    }

    //Get methods

    public String getMenuopcao() { return this.menuopcao; }
    public String getMenuid() { return this.menuid; }

}

