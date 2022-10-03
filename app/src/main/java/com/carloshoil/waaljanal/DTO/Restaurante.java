package com.carloshoil.waaljanal.DTO;

public class Restaurante {
    public String cLlave;
    public String cNombre;
    public String cIdMenu;

    public Restaurante(String cLlave, String cNombre, String cIdMenu) {
        this.cLlave = cLlave;
        this.cNombre = cNombre;
        this.cIdMenu=cIdMenu;
    }

    public Restaurante() {
    }
}
