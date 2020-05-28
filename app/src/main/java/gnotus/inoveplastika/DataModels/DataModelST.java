package gnotus.inoveplastika.DataModels;

import com.google.gson.annotations.SerializedName;

public class DataModelST {


    private String ref;
    private String design;
    private String unidade;
    private String uni2;
    private String familia;
    private String u_cbpeca;

    private Boolean usalote;

    private  double fconversao;



    public String getRef() {
        return ref.trim();
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getDesign() {
        return design.trim();
    }

    public void setDesign(String design) {
        this.design = design;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getUni2() {
        return uni2;
    }

    public void setUni2(String uni2) {
        this.uni2 = uni2;
    }

    public String getFamilia() {
        return familia;
    }

    public void setFamilia(String familia) {
        this.familia = familia;
    }

    public String getU_cbpeca() {
        return u_cbpeca;
    }

    public void setU_cbpeca(String u_cbpeca) {
        this.u_cbpeca = u_cbpeca;
    }

    public Boolean getUsalote() {
        return usalote;
    }

    public void setUsalote(Boolean usalote) {
        this.usalote = usalote;
    }

    public double getFconversao() {
        return fconversao;
    }

    public void setFconversao(double fconversao) {
        this.fconversao = fconversao;
    }






}
