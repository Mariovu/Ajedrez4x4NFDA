package com.example.ajedrez4x4;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese la cadena para Jugador 1 (ej: rbbr): ");
        String cadenaJugador1 = scanner.nextLine();

        System.out.print("Ingrese la cadena para Jugador 2: ");
        String cadenaJugador2 = scanner.nextLine();

        if (cadenaJugador1.length() != cadenaJugador2.length()) {
            System.out.println("¡Error! Las cadenas deben tener igual longitud.");
            return;
        }

        // Generar rutas
        ExploradorRutas.explorarRutasJugador1(cadenaJugador1, "todos_los_caminos_jugador1.txt");
        ExploradorRutas.explorarRutasJugador2(cadenaJugador2, "todos_los_caminos_jugador2.txt");

        // Filtrar ganadoras
        ArchivosSalida.filtrarCadenasGanadoras("todos_los_caminos_jugador1.txt", "cadenas_ganadoras_jugador1.txt", 16);
        ArchivosSalida.filtrarCadenasGanadoras("todos_los_caminos_jugador2.txt", "cadenas_ganadoras_jugador2.txt", 13);

        System.out.println("Archivos generados. Ejecutando interfaz gráfica...");
        TableroGUI.launch(TableroGUI.class, args);
    }
}