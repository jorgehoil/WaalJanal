package com.carloshoil.waaljanal.DTO;

public class Categoria {
    public String cLlave;
    public String cNombre;
    public boolean lDisponible;

    public Categoria(String cLlave, String cNombre, boolean lDisponible) {
        this.cLlave = cLlave;
        this.cNombre = cNombre;
        this.lDisponible=lDisponible;
    }

    public Categoria() {


    }

}
