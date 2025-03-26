package com.example.ajedrez4x4;

import java.io.*;
import java.util.*;

public class ExploradorRutas {
    private static final Random random = new Random();

    public static void explorarRutasJugador1(String cadena, String archivoSalida) {
        explorarRutas(cadena, archivoSalida, 1, 16);
    }

    public static void explorarRutasJugador2(String cadena, String archivoSalida) {
        explorarRutas(cadena, archivoSalida, 4, 13);
    }

    private static void explorarRutas(String cadena, String archivoSalida, int casillaInicial, int casillaFinal) {
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

    private static void escribirArchivo(String archivo, List<List<Integer>> rutas) {
        try (FileWriter writer = new FileWriter(archivo)) {
            for (List<Integer> ruta : rutas) {
                writer.write(ruta.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[] getRutaAlternativa(int turnoActual, int posicionActual,
                                           String archivoGanadoras, int posicionOponente) {
        List<int[]> rutasValidas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivoGanadoras))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                int[] ruta = parsearRuta(linea);
                if (validarRuta(turnoActual, posicionActual, posicionOponente, ruta)) {
                    rutasValidas.add(ruta);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rutasValidas.isEmpty() ? null : rutasValidas.get(random.nextInt(rutasValidas.size()));
    }

    private static boolean validarRuta(int turno, int actual, int oponente, int[] ruta) {
        return ruta.length > turno && ruta[turno] == actual && !contieneOponente(ruta, turno, oponente);
    }

    private static boolean contieneOponente(int[] ruta, int desde, int oponente) {
        for (int i = desde; i < ruta.length; i++) {
            if (ruta[i] == oponente) return true;
        }
        return false;
    }

    private static int[] parsearRuta(String linea) {
        return Arrays.stream(linea.replaceAll("[\\[\\]]", "").split(", "))
                .mapToInt(Integer::parseInt)
                .toArray();
    }
}


