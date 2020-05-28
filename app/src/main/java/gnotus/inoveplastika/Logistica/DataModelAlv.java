package gnotus.inoveplastika.Logistica;

public class DataModelAlv {



    private String alvstamp;
    private String identificacao;
    private String sznome;
    private String szzstamp;
    private String zona;
    private int armazem;

    // campos da tabela szz
    private Boolean u_loccof;
    private Boolean u_multiof;
    private Boolean u_ofcsalv;
    private Boolean u_noinvfof;



    public String getAlvstamp() {
        return alvstamp;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public String getSznome() {
        return sznome;
    }

    public String getSzzstamp() {
        return szzstamp;
    }

    public String getZona() {
        return zona;
    }

    public int getArmazem() {
        return armazem;
    }

    public Boolean getU_loccof() {
        return u_loccof;
    }

    public Boolean getU_multiof() {
        return u_multiof;
    }

    public Boolean getU_ofcsalv() {
        return u_ofcsalv;
    }

    public Boolean getU_noinvfof() {
        return u_noinvfof;
    }

    public String getFormattedLoc() {
        return  "[" + this.armazem + "] [" + this.zona + "] [" + this.identificacao + "]";
    }

    public boolean loadAlvFromLocalization(String localizacao) {

        if (localizacao.length() < 5) {
            return false;
        }

        String prefixo = localizacao.substring(0, 5);

        switch (prefixo) {

            case ("(ALV)"):

                System.out.println("Começa por ALV");

                // vamos contar se existem só duas hastags
                int tagscount = localizacao.length() - localizacao.replace("#", "").length();

                if (tagscount != 2) {
                    return false;
                }

                int pos_tag1, pos_tag2 = 0;
                pos_tag1 = localizacao.indexOf("#");
                pos_tag2 = localizacao.indexOf("#", pos_tag1 + 1);

                if (pos_tag1 >= pos_tag2) {
                    return false;
                }


                // validamos se o armazém é inteiro
                try {
                    Integer val = Integer.valueOf(localizacao.substring(5, pos_tag1));
                    if (val == null) {
                        return false;
                    } else this.armazem = val;
                } catch (NumberFormatException e) {
                    return false;
                }

                this.zona = localizacao.substring(pos_tag1 + 1, pos_tag2);
                this.identificacao = localizacao.substring(pos_tag2 + 1);
                return true;

            default:
                return  false;

        }
    }

}

