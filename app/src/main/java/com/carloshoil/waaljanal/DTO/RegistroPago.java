package com.carloshoil.waaljanal.DTO;

public class RegistroPago {
    public String cFolio;
    public String cFechaAprobacion;
    public String cPaquete;
    public long dateRegistro;
    public String cReferencia;
    public int iEstatus;


    public RegistroPago(String cFolio, String cFechaAprobacion, String cPaquete, long dateRegistro, String cReferencia, int iEstatus) {
        this.cFolio = cFolio;
        this.cFechaAprobacion = cFechaAprobacion;
        this.cPaquete = cPaquete;
        this.dateRegistro = dateRegistro;
        this.cReferencia = cReferencia;
        this.iEstatus=iEstatus;

    }
    public RegistroPago()
    {

    }
}
