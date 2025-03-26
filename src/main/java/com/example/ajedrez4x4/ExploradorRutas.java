package com.example.ajedrez4x4;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ExploradorRutas {
    private static final Random random = new Random();

    public static void explorarRutasJugador1(String cadena, String archivoSalida) {
        explorarRutas(cadena, archivoSalida, 1, 16, false);
    }

    public static void explorarRutasJugador2(String cadena, String archivoSalida) {
        explorarRutas(cadena, archivoSalida, 4, 13, true);
    }

    private static void explorarRutas(String cadena, String archivoSalida,
                                      int inicio, int meta, boolean jugador2) {
        try (FileWriter writer = new FileWriter(archivoSalida)) {
            Queue<List<Integer>> cola = new LinkedList<>();
            cola.add(Collections.singletonList(inicio));

            for (int i = 0; i < cadena.length(); i++) {
                char color = cadena.charAt(i);
                int nivelSize = cola.size();

                for (int j = 0; j < nivelSize; j++) {
                    List<Integer> ruta = cola.poll();
                    int actual = ruta.get(ruta.size() - 1);

                    for (int movimiento : Movimientos.movimientosValidos(actual, color, jugador2 ? 13 : 16)) {
                        if (!ruta.contains(movimiento)) {
                            List<Integer> nuevaRuta = new ArrayList<>(ruta);
                            nuevaRuta.add(movimiento);
                            cola.add(nuevaRuta);
                        }
                    }
                }
            }

            for (List<Integer> ruta : cola) {
                if (ruta.get(ruta.size() - 1) == meta) {
                    writer.write(ruta.toString() + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[] encontrarRutaAlternativaCompleta(int turnoActual, int posicionActual,
                                                         int posicionOponente, String archivoGanadoras) {
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

        return !rutasValidas.isEmpty() ? seleccionarRutaOptima(rutasValidas, posicionOponente) : null;
    }

    private static boolean validarRuta(int turno, int actual, int oponente, int[] ruta) {
        return ruta.length > turno &&
                ruta[turno] == actual &&
                !contieneOponente(ruta, turno, oponente) &&
                movimientosValidos(ruta, turno);
    }

    private static boolean contieneOponente(int[] ruta, int desde, int oponente) {
        for (int i = desde; i < ruta.length; i++) {
            if (ruta[i] == oponente) return true;
        }
        return false;
    }

    private static boolean movimientosValidos(int[] ruta, int desde) {
        for (int i = desde + 1; i < ruta.length; i++) {
            if (!esMovimientoValido(ruta[i - 1], ruta[i])) return false;
        }
        return true;
    }

    private static int[] seleccionarRutaOptima(List<int[]> rutas, int oponente) {
        return rutas.stream()
                .sorted(Comparator.comparingInt(r -> calcularPrioridad(r, oponente)))
                .findFirst()
                .orElse(null);
    }

    private static int calcularPrioridad(int[] ruta, int oponente) {
        int prioridad = 0;
        for (int i = 0; i < ruta.length; i++) {
            prioridad += (ruta.length - i) * 2; // Prioriza rutas más cortas
            if (esAdyacente(ruta[i], oponente)) prioridad += 5;
            if (i > 0 && ruta[i] == oponente) prioridad += 100;
        }
        return prioridad;
    }

    public static int[] getRutaAlternativa(int turnoActual, int posicionActual,
                                           String archivoGanadoras, int posicionOponente) {
        try {
            return encontrarRutaAlternativaCompleta(turnoActual, posicionActual,
                    posicionOponente, archivoGanadoras);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean esAdyacente(int c1, int c2) {
        int f1 = (c1 - 1) / 4, co1 = (c1 - 1) % 4;
        int f2 = (c2 - 1) / 4, co2 = (c2 - 1) % 4;
        return Math.abs(f1 - f2) <= 1 && Math.abs(co1 - co2) <= 1;
    }

    private static boolean esMovimientoValidos(int actual, int siguiente) {
        int f1 = (actual - 1) / 4, c1 = (actual - 1) % 4;
        int f2 = (siguiente - 1) / 4, c2 = (siguiente - 1) % 4;
        return Math.abs(f1 - f2) <= 1 && Math.abs(c1 - c2) <= 1;
    }

    private static int[] parsearRuta(String linea) {
        return Arrays.stream(linea.replaceAll("[\\[\\]]", "").split(", "))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    public static void imprimirAnalisisRuta(int[] ruta, int oponente) {
        System.out.println("Análisis de ruta: " + Arrays.toString(ruta));
        System.out.println("Puntaje: " + calcularPrioridad(ruta, oponente));
        System.out.println("Longitud: " + ruta.length);
        System.out.println("Proximidad al oponente: " +
                Arrays.stream(ruta).filter(c -> esAdyacente(c, oponente)).count());
    }
}