package gnotus.inoveplastika.Suporte;

public class DataModelCm4 {

    private int cm;
    private String cmdesc;
    private String cm4tdesc;
    private boolean u_dblqrpmc;

    private boolean v_tecmoldes; // se o operador em causa é técnico de moldes. Pode rececionar e fechar pedidos de moldes
    private boolean v_tecequip; // se o operador em causa é técnico de outros equipamentos. Pode rececionar e fechar pedidos de outros equipamentos que não moldes

    // private boolean u_openmh; // se o operador pode abrir ordens de serviço
    private boolean u_openosc; // se o operador pode abrir ordens de serviço corretivas em moldes
    private boolean u_vldosprd; // se o operador pode validar ordens de serviço pela produção

    private boolean v_open_os_moldes; // se o operador em causa pode abrir ordens de serviço de moldes
    private boolean v_validate_os_eng; // se o operador em causa pode validar as ordens de serviço pela área de engenharia
    private boolean v_validate_os_qual; // se o operador em causa pode validar as ordens de serviço pela área da qualidade


    public int getCm() {
        return cm;
    }


    public boolean isU_dblqrpmc() {
        return u_dblqrpmc;
    }

    public String getCmdesc() {
        return cmdesc;
    }

    public String getCm4tdesc() {
        return cm4tdesc;
    }

    public boolean isV_tecmoldes() {
        return v_tecmoldes;
    }

    public boolean isV_tecequip() {
        return v_tecequip;
    }

    public boolean isV_validate_os_qual() {
        return v_validate_os_qual;
    }

    public boolean isU_vldosprd() {
        return u_vldosprd;
    }

    public boolean isU_openosc() {
        return u_openosc;
    }
}

