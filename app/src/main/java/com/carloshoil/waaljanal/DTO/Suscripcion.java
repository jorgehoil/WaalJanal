package com.carloshoil.waaljanal.DTO;

public class Suscripcion {
    public String cKey;
    public String cNombre;
    public String cPrecio;
    public String cUrl;
    public String cAhorro;

    public Suscripcion(String cKey, String cNombre, String cPrecio, String cUrl, String cAhorro) {
        this.cKey = cKey;
        this.cNombre = cNombre;
        this.cPrecio = cPrecio;
        this.cUrl = cUrl;
        this.cAhorro=cAhorro;
    }
    public Suscripcion()
    {

    }
}
