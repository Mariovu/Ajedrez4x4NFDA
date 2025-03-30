package com.example.ajedrez4x4;

import java.io.*;
import java.util.*;

public class ExploradorRutas {

    public static void explorarRutasJugador1(String cadena, String archivoSalida) {
        procesarRutasNoDeterministas(cadena, archivoSalida, 1);
    }

    public static void explorarRutasJugador2(String cadena, String archivoSalida) {
        procesarRutasNoDeterministas(cadena, archivoSalida, 4);
    }

    private static void procesarRutasNoDeterministas(String cadena, String archivoSalida, int inicio) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoSalida))) {
            Queue<Estado> cola = new LinkedList<>();
            cola.add(new Estado(inicio, 0, null));

            while (!cola.isEmpty()) {
                int nivelSize = cola.size();

                for (int i = 0; i < nivelSize; i++) {
                    Estado actual = cola.poll();

                    if (actual.turno == cadena.length()) {
                        escribirRuta(actual, writer);
                    } else {
                        char color = cadena.charAt(actual.turno);
                        for (int movimiento : Movimientos.movimientosValidos(actual.posicion, color, -1)) {
                            Estado nuevo = new Estado(movimiento, actual.turno + 1, actual);
                            cola.add(nuevo);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void escribirRuta(Estado estado, BufferedWriter writer) throws IOException {
        LinkedList<Integer> ruta = new LinkedList<>();
        while (estado != null) {
            ruta.addFirst(estado.posicion);
            estado = estado.padre;
        }
        writer.write(ruta.toString());
        writer.newLine();
    }
}