package gnotus.inoveplastika.DataModels;

import android.text.TextUtils;

public class DataModelLeituraCB {

    private String tipocb = "",referencia,lote,alveolo,zona;
    private Integer armazem;
    private Integer operador;
    private double quantidade;

    public static final String TIPOCB_REF = "R";
    public static final String TIPOCB_ALVEOLO = "ALV";
    public static final String TIPOCB_OPERADOR = "OP";

    public DataModelLeituraCB(String cb) {


        if (cb.length()>=3 && cb.substring(0,3).equals("(R)")) {

            String tmpRef = "";
            String tmpLote = "";
            Double tmpQuantidade = 0.0;

            this.lote = "";

            this.tipocb = "R";

            String separacao1[],separacao2[],separacao3[];

            // vamos buscar a referencia
            separacao1 = cb.split("\\(R\\)");
            separacao2 = separacao1[1].split("\\(");
            tmpRef = separacao2[0];
            this.referencia = tmpRef;

            // vamos buscar o lote
            if (cb.contains("(S)"))
            {
                separacao1 = cb.split("\\(S\\)");
                separacao2 = separacao1[1].split("\\(");
                tmpLote = separacao2[0];
                this.lote = tmpLote;
            }

            if (cb.contains("(Q")) {
                // vamos buscar a quantidade
                separacao1 = cb.split("\\(Q");
                separacao2 = separacao1[1].split("\\(");
                separacao3 = separacao2[0].split("\\)");
                if (TextUtils.isDigitsOnly(separacao3[0]) && TextUtils.isDigitsOnly(separacao3[1])) {
                    Integer potencia = Integer.parseInt(separacao3[0]);
                    Double divisor = Math.pow(10,potencia);

                    tmpQuantidade = Double.parseDouble(separacao3[1]);
                    tmpQuantidade = tmpQuantidade / divisor;
                    this.quantidade = tmpQuantidade;

                } else this.quantidade = 0.0;

            }


        }

        if (cb.length()>=5 && cb.substring(0,5).equals("(ALV)")) {

            int armazem;
            String zona,alveolo;

            String separacao[] = cb.substring(5, cb.length()).split("#");

            if (TextUtils.isDigitsOnly(separacao[0])) {

                if (separacao[1].length() > 0 && separacao[2].length() > 0) {

                    armazem = Integer.valueOf(separacao[0]);
                    zona = separacao[1];
                    alveolo = separacao[2];

                    this.armazem = armazem;
                    this.zona = zona;
                    this.alveolo = alveolo;

                    this.tipocb = "ALV";

                }

            }

        }

        if (cb.length()> 4 && cb.substring(0,4).equals("(OP)")) {

            String mOp = cb.substring(4);

            if (TextUtils.isDigitsOnly(mOp)) {

                    this.operador = Integer.valueOf(mOp);
                    this.tipocb = "OP";
                }


        }

    }

    public String getTipocb() {
        return tipocb;
    }

    public String getReferencia() {
        return this.referencia;
    }

    public String getLote() {
        return this.lote;
    }

    public String getZona   () {
        return this.zona;
    }

    public String getAlveolo() {
        return this.alveolo;
    }

    public Double getQtt() { return this.quantidade;}

    public int getArmazem() {return this.armazem;}

    public Integer getOperador() {
        return operador;
    }
}
