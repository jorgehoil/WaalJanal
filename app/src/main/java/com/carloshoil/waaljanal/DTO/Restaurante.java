package com.carloshoil.waaljanal.DTO;

public class Restaurante {
    public String cLlave;
    public String cNombre;
    public String cIdMenu;
    public boolean lDisponible;

    public Restaurante(String cLlave, String cNombre, String cIdMenu, boolean lDisponible) {
        this.cLlave = cLlave;
        this.cNombre = cNombre;
        this.cIdMenu=cIdMenu;
        this.lDisponible=lDisponible;
    }

    public Restaurante() {
    }
}
