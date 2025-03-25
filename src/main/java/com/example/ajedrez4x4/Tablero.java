package com.example.ajedrez4x4;

import java.util.HashMap;
import java.util.Map;

public class Tablero {
    private static final Map<Integer, Character> tablero = new HashMap<>();

    static {
        tablero.put(1, 'r'); tablero.put(2, 'b'); tablero.put(3, 'r'); tablero.put(4, 'b');
        tablero.put(5, 'b'); tablero.put(6, 'r'); tablero.put(7, 'b'); tablero.put(8, 'r');
        tablero.put(9, 'r'); tablero.put(10, 'b'); tablero.put(11, 'r'); tablero.put(12, 'b');
        tablero.put(13, 'b'); tablero.put(14, 'r'); tablero.put(15, 'b'); tablero.put(16, 'r');
    }

    public static char obtenerColor(int casilla) {
        return tablero.get(casilla);
    }
}