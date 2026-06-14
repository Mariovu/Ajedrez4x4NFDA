package com.example.ajedrez4x4;

import java.util.List;
import java.util.ArrayList;

public class Movimientos {
    private static final int[] FILAS = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final int[] COLUMNAS = {-1, 0, 1, -1, 1, -1, 0, 1};

    public static List<Integer> movimientosValidos(int casillaActual, char colorSiguiente, int casillaOcupada) {
        List<Integer> movimientos = new ArrayList<>();
        // CAMBIO: Ahora dividimos y sacamos módulo entre 10
        int fila = (casillaActual - 1) / 10;
        int columna = (casillaActual - 1) % 10;

        for (int i = 0; i < 8; i++) {
            int nuevaFila = fila + FILAS[i];
            int nuevaColumna = columna + COLUMNAS[i];

            // CAMBIO: El límite superior ahora es 10 en lugar de 4
            if (nuevaFila >= 0 && nuevaFila < 10 && nuevaColumna >= 0 && nuevaColumna < 10) {
                int nuevaCasilla = nuevaFila * 10 + nuevaColumna + 1;
                if (nuevaCasilla != casillaOcupada && Tablero.obtenerColor(nuevaCasilla) == colorSiguiente) {
                    movimientos.add(nuevaCasilla);
                }
            }
        }
        return movimientos.isEmpty() ? List.of(casillaActual) : movimientos;
    }
}