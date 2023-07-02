package com.carloshoil.waaljanal.DTO;

public class Pedido {
    public String cKey;
    public String cNombreCliente;
    public String cMesa;
    public String cDireccion;
    public String cDetallePedido;
    public String cTelefono;
    public long lFechaRegistro;
    public String cPrecio;

    public Pedido(String cKey,String cNombreCliente, String cMesa, String cDireccion, String cDetallePedido, String cTelefono, long lFechaRegistro, String cPrecio) {
        this.cKey = cKey;
        this.cNombreCliente = cNombreCliente;
        this.cMesa = cMesa;
        this.cDireccion = cDireccion;
        this.cDetallePedido = cDetallePedido;
        this.cTelefono = cTelefono;
        this.lFechaRegistro = lFechaRegistro;
        this.cPrecio=cPrecio;
    }
    public Pedido()
    {

    }
}
