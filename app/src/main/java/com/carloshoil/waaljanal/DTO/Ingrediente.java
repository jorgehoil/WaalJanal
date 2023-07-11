package com.carloshoil.waaljanal.DTO;

import java.io.Serializable;

public class Ingrediente implements Serializable {
    public String cKey;
    public String cNombre;
    public String cPrecio;
    public boolean lPrecioAdicional;
    public boolean lDisponible;

    public Ingrediente(String cKey, String cNombre, String cPrecio, boolean lPrecioAdicional, boolean lDisponible) {
        this.cKey = cKey;
        this.cNombre = cNombre;
        this.cPrecio = cPrecio;
        this.lPrecioAdicional = lPrecioAdicional;
        this.lDisponible = lDisponible;
    }
    public Ingrediente()
    {

    }
}
