package com.carloshoil.waaljanal.DTO;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Producto implements Serializable {
    public String cLlave;
    public String cNombre;
    public String cDescripcion;
    public String cPrecio;
    public String cIdCategoria;
    public String cUrlImagen;
    public String cUrlImagenMin;
    public boolean lDisponible;
    public List<Variedad> lstVariedad;
    public List<Ingrediente> lstIngrediente;
    public HashMap<String, Integer> dataIngrediente;


    public Producto(String cLlave, String cNombre, String cDescripcion, String cPrecio, String cIdCategoria, String cUrlImagen, List<Variedad> lstVariedad, List<Ingrediente> lstIngrediente, HashMap<String, Integer> dataIngrediente) {
        this.cNombre = cNombre;
        this.cDescripcion = cDescripcion;
        this.cPrecio = cPrecio;
        this.cIdCategoria = cIdCategoria;
        this.cUrlImagen = cUrlImagen;
        this.cLlave=cLlave;
        this.lstVariedad=lstVariedad;
        this.lstIngrediente=lstIngrediente;
        this.dataIngrediente=dataIngrediente;
    }
    public Producto()
    {

    }
}
