package com.example.ajedrez4x4;

public class Estado {
    public final int posicion;
    public final int paso;
    public final Estado padre;

    public Estado(int posicion, int paso, Estado padre) {
        this.posicion = posicion;
        this.paso = paso;
        this.padre = padre;
    }
}