package com.example.ajedrez4x4;

import java.util.List;
import java.util.ArrayList;

public class Movimientos {
    private static final int[] FILAS = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final int[] COLUMNAS = {-1, 0, 1, -1, 1, -1, 0, 1};

    public static List<Integer> movimientosValidos(int casillaActual, char colorSiguiente, int casillaOcupada) {
        List<Integer> movimientos = new ArrayList<>();
        int fila = (casillaActual - 1) / 4;
        int columna = (casillaActual - 1) % 4;

        for (int i = 0; i < 8; i++) {
            int nuevaFila = fila + FILAS[i];
            int nuevaColumna = columna + COLUMNAS[i];

            if (nuevaFila >= 0 && nuevaFila < 4 && nuevaColumna >= 0 && nuevaColumna < 4) {
                int nuevaCasilla = nuevaFila * 4 + nuevaColumna + 1;
                if (nuevaCasilla != casillaOcupada && Tablero.obtenerColor(nuevaCasilla) == colorSiguiente) {
                    movimientos.add(nuevaCasilla);
                }
            }
        }
        return movimientos.isEmpty() ? List.of(casillaActual) : movimientos;
    }
}