package com.carloshoil.waaljanal.DTO;

import java.io.Serializable;

public class Variedad implements Serializable {
    public String cKey;
    public String cNombre;
    public String cPrecio;
    public boolean lDisponible;

    public Variedad(String cKey, String cNombre, String cPrecio, boolean lDisponible) {
        this.cKey = cKey;
        this.cNombre = cNombre;
        this.cPrecio = cPrecio;
        this.lDisponible = lDisponible;
    }
    public Variedad()
    {

    }
}
