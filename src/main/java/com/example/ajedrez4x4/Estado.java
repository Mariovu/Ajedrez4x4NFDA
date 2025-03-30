package com.example.ajedrez4x4;

public class Estado {
    public final int posicion;   // Casilla actual (1-16(
    public final int turno;    // Turno o posicion de la cadena
    public final Estado padre;   // Estado anterior

    public Estado(int posicion, int turno, Estado padre) {
        this.posicion = posicion;
        this.turno = turno;
        this.padre = padre;
    }
}