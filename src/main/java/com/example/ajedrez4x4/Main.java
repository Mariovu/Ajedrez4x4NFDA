package com.example.ajedrez4x4;

import javafx.application.Application;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Jugar Ajedrez (Manual)");
            System.out.println("2. Jugar Ajedrez (Automático)");
            System.out.println("3. Visualizar Grafo del Jugador 1");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    iniciarJuegoManual(scanner);
                    break;
                case 2:
                    iniciarJuegoAutomatico();
                    break;
                case 3:
                    AutomataGrafico.mostrarGrafo("todos_los_caminos_jugador1.txt");
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Opción no válida!");
            }
        } while (opcion != 4);

        scanner.close();
    }

    private static void iniciarJuegoManual(Scanner scanner) {
        scanner.nextLine();
        System.out.println("\n--- MODO MANUAL ---");

        System.out.print("Ingrese cadena para Jugador 1 (ej: rbbr): ");
        String cadenaJ1 = scanner.nextLine();

        System.out.print("Ingrese cadena para Jugador 2: ");
        String cadenaJ2 = scanner.nextLine();

        if (cadenaJ1.length() != cadenaJ2.length()) {
            System.out.println("ERROR: Las cadenas deben tener la misma longitud");
            return;
        }

        procesarJuego(cadenaJ1, cadenaJ2);
    }

    private static void iniciarJuegoAutomatico() {
        System.out.println("\n--- MODO AUTOMÁTICO ---");
        Random rand = new Random();
        int longitud = 3 + rand.nextInt(18); // Longitud entre 3 y 15

        String cadenaJ1 = generarCadenaAleatoria(longitud, true);  // Termina en 'r'
        String cadenaJ2 = generarCadenaAleatoria(longitud, false); // Termina en 'b'

        System.out.println("Cadena generada para Jugador 1: " + cadenaJ1);
        System.out.println("Cadena generada para Jugador 2: " + cadenaJ2);

        procesarJuego(cadenaJ1, cadenaJ2);
    }

    private static String generarCadenaAleatoria(int longitud, boolean terminaEnR) {
        Random rand = new Random();
        StringBuilder cadena = new StringBuilder();

        for (int i = 0; i < longitud - 1; i++) {
            cadena.append(rand.nextBoolean() ? "r" : "b");
        }

        cadena.append(terminaEnR ? "r" : "b");
        return cadena.toString();
    }

    private static void procesarJuego(String cadenaJ1, String cadenaJ2) {
        ExploradorRutas.explorarRutasJugador1(cadenaJ1, "todos_los_caminos_jugador1.txt");
        ExploradorRutas.explorarRutasJugador2(cadenaJ2, "todos_los_caminos_jugador2.txt");

        ArchivosSalida.filtrarCadenasGanadoras("todos_los_caminos_jugador1.txt", "cadenas_ganadoras_jugador1.txt", 16);
        ArchivosSalida.filtrarCadenasGanadoras("todos_los_caminos_jugador2.txt", "cadenas_ganadoras_jugador2.txt", 13);

        System.out.println("\nArchivos generados exitosamente!");
        System.out.println("Iniciando interfaz gráfica...");
        Application.launch(TableroGUI.class, new String[0]);
    }
}