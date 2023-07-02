package com.carloshoil.waaljanal.DTO;

public class MenuPersonalizado {

    public String cKey;
    public String cNombre;
    public String cFondo;
    public String cFondoVar;
    public String cFondoCat;
    public String cFondoPlat;
    public String cTextCat;
    public String cTextDescrip;
    public String cTextPlat;
    public String cTextPrice;
    public boolean lOscuro;


    public MenuPersonalizado(String cKey, String cNombre, String cFondo,String cFondoVar, String cFondoCat, String cFondoPlat, String cTextCat, String cTextDescrip, String cTextPlat, String cTextPrice, boolean lOscuro) {
        this.cKey = cKey;
        this.cNombre = cNombre;
        this.cFondo = cFondo;
        this.cFondoVar=cFondoVar;
        this.cFondoCat = cFondoCat;
        this.cFondoPlat = cFondoPlat;
        this.cTextCat = cTextCat;
        this.cTextDescrip = cTextDescrip;
        this.cTextPlat = cTextPlat;
        this.cTextPrice = cTextPrice;
    }

    public MenuPersonalizado()
    {

    }
}
