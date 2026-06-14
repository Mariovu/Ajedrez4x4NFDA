package com.example.ajedrez4x4;

import java.io.*;
import java.util.*;

public class ExploradorRutas {

    // LÍMITE: Cortar la exploración tras encontrar esta cantidad de rutas ganadoras
    private static final int LIMITE_VICTORIAS = 5000;

    // Contadores para cada jugador
    private static int victoriasJ1 = 0;
    private static int victoriasJ2 = 0;

    public static void explorarRutasJugador1(String cadena, String archivoSalida) {
        victoriasJ1 = 0; // Reiniciamos el contador antes de empezar
        procesarRutasNoDeterministas(cadena, archivoSalida, 1, 100, true);
    }

    public static void explorarRutasJugador2(String cadena, String archivoSalida) {
        victoriasJ2 = 0; // Reiniciamos el contador antes de empezar
        procesarRutasNoDeterministas(cadena, archivoSalida, 10, 91, false);
    }

    private static void procesarRutasNoDeterministas(String cadena, String archivoSalida, int inicio, int meta, boolean esJ1) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoSalida))) {
            LinkedList<Integer> rutaActual = new LinkedList<>();
            dfs(inicio, 0, cadena, meta, rutaActual, writer, esJ1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void dfs(int posicion, int turno, String cadena, int meta, LinkedList<Integer> rutaActual, BufferedWriter writer, boolean esJ1) throws IOException {

        // OPTIMIZACIÓN DE TIEMPO: Si ya encontramos suficientes victorias, detenemos la recursividad (Poda del árbol)
        if (esJ1 && victoriasJ1 >= LIMITE_VICTORIAS) return;
        if (!esJ1 && victoriasJ2 >= LIMITE_VICTORIAS) return;

        // 1. Registrar paso actual en la ruta
        rutaActual.add(posicion);

        // 2. CASO BASE: Comprobar si se procesó toda la cadena
        if (turno == cadena.length()) {
            // Solo se escribe en el archivo si la ruta es ganadora
            if (posicion == meta) {
                writer.write(rutaActual.toString());
                writer.newLine();

                // Aumentamos el contador correspondiente
                if (esJ1) victoriasJ1++;
                else victoriasJ2++;
            }
        } else {
            // 3. PASO RECURSIVO
            char color = cadena.charAt(turno);

            // Obtenemos los movimientos y los pasamos a una lista modificable
            List<Integer> movimientosPosibles = new ArrayList<>(Movimientos.movimientosValidos(posicion, color, -1));

            // LA SOLUCIÓN: Desordenamos aleatoriamente la lista de hijos antes de explorarlos
            Collections.shuffle(movimientosPosibles);

            for (int movimiento : movimientosPosibles) {
                dfs(movimiento, turno + 1, cadena, meta, rutaActual, writer, esJ1);
            }
        }

        // 4. BACKTRACKING
        rutaActual.removeLast();
    }
}