package com.example.ajedrez4x4;

import java.io.*;
import java.util.*;

public class ExploradorRutas {
    public static void explorarRutasJugador1(String cadena, String archivoSalida) {
        explorarRutas(cadena, archivoSalida, 1, 16, false);
    }

    public static void explorarRutasJugador2(String cadena, String archivoSalida) {
        explorarRutas(cadena, archivoSalida, 4, 13, true);
    }

    private static void explorarRutas(String cadena, String archivoSalida, int casillaInicial, int casillaFinal, boolean esJugador2) {
        try (FileWriter writer = new FileWriter(archivoSalida)) {
            Queue<List<Integer>> cola = new LinkedList<>();
            cola.add(Arrays.asList(casillaInicial));

            for (int i = 0; i < cadena.length(); i++) {
                char color = cadena.charAt(i);
                int nivelSize = cola.size();

                for (int j = 0; j < nivelSize; j++) {
                    List<Integer> ruta = cola.poll();
                    int ultimaCasilla = ruta.get(ruta.size() - 1);

                    for (int movimiento : Movimientos.movimientosValidos(ultimaCasilla, color, -1)) {
                        List<Integer> nuevaRuta = new ArrayList<>(ruta);
                        nuevaRuta.add(movimiento);
                        cola.add(nuevaRuta);
                    }
                }
            }

            for (List<Integer> ruta : cola) {
                writer.write(ruta.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[] getRutaAlternativa(int movimientoActual, int posicionActual,
                                           String archivoGanadoras, int casillaOcupada) throws IOException {
        List<int[]> rutasValidas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoGanadoras))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                int[] ruta = parsearRuta(linea);

                // Verificación 1: Misma posición en el movimiento actual
                if (ruta.length > movimientoActual && ruta[movimientoActual] == posicionActual) {

                    // Verificación 2: Movimiento válido (1 casilla de distancia)
                    if (ruta.length > movimientoActual + 1) {
                        int casillaSiguiente = ruta[movimientoActual + 1];
                        if (esMovimientoValido(posicionActual, casillaSiguiente) &&
                                casillaSiguiente != casillaOcupada) {
                            rutasValidas.add(ruta);
                        }
                    }
                }
            }
        }
        return rutasValidas.isEmpty() ? null : rutasValidas.get(new Random().nextInt(rutasValidas.size()));
    }

    // Nueva función auxiliar para validar movimientos
    private static boolean esMovimientoValido(int actual, int siguiente) {
        int filaActual = (actual - 1) / 4;
        int colActual = (actual - 1) % 4;
        int filaSiguiente = (siguiente - 1) / 4;
        int colSiguiente = (siguiente - 1) % 4;

        // Solo movimientos a 1 casilla de distancia (horizontal, vertical o diagonal)
        return Math.abs(filaActual - filaSiguiente) <= 1 &&
                Math.abs(colActual - colSiguiente) <= 1;
    }

    private static int[] parsearRuta(String linea) {
        String[] partes = linea.replace("[", "").replace("]", "").split(", ");
        int[] ruta = new int[partes.length];
        for (int i = 0; i < partes.length; i++) {
            ruta[i] = Integer.parseInt(partes[i].trim());
        }
        return ruta;
    }
}