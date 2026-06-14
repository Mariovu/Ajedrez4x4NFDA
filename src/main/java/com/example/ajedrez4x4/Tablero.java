package com.example.ajedrez4x4;

import java.util.HashMap;
import java.util.Map;


public class Tablero {
    public static char obtenerColor(int casilla) {
        // En un tablero 10x10, las casillas van del 1 al 100
        int indice = casilla - 1;
        int fila = indice / 10;
        int columna = indice % 10;

        // Patrón de tablero de ajedrez alternado
        return (fila + columna) % 2 == 0 ? 'r' : 'b';
    }
}