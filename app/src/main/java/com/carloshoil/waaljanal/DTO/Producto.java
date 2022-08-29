package com.carloshoil.waaljanal.DTO;

public class Producto {
    public String cLlave;
    public String cNombre;
    public String cDescripcion;
    public String cPrecio;
    public String cIdCategoria;
    public String cUrlImagen;
    public boolean lDisponible;


    public Producto(String cLlave, String cNombre, String cDescripcion, String cPrecio, String cIdCategoria, String cUrlImagen) {
        this.cNombre = cNombre;
        this.cDescripcion = cDescripcion;
        this.cPrecio = cPrecio;
        this.cIdCategoria = cIdCategoria;
        this.cUrlImagen = cUrlImagen;
        this.cLlave=cLlave;
    }
    public Producto()
    {

    }
}
