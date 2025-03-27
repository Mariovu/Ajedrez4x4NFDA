package com.example.ajedrez4x4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {
    static {
        System.setProperty("org.graphstream.ui", "swing"); // Configuración inicial
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Jugar Ajedrez");
            System.out.println("2. Visualizar Grafo del Jugador 1");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    iniciarJuego(scanner);
                    break;
                case 2:
                    AutomataGrafico.mostrarGrafo("todos_los_caminos_jugador1.txt");
                    break;
                case 3:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción no válida!");
            }
        } while (opcion != 3);

        scanner.close();
    }

    private static void iniciarJuego(Scanner scanner) {
        scanner.nextLine(); // Limpiar buffer

        System.out.print("\nIngrese cadena para Jugador 1 (ej: rbbr): ");
        String cadenaJ1 = scanner.nextLine();

        System.out.print("Ingrese cadena para Jugador 2: ");
        String cadenaJ2 = scanner.nextLine();

        if (cadenaJ1.length() != cadenaJ2.length()) {
            System.out.println("ERROR: Las cadenas deben tener la misma longitud");
            return;
        }

        // Procesamiento de cadenas
        ExploradorRutas.explorarRutasJugador1(cadenaJ1, "todos_los_caminos_jugador1.txt");
        ExploradorRutas.explorarRutasJugador2(cadenaJ2, "todos_los_caminos_jugador2.txt");

        ArchivosSalida.filtrarCadenasGanadoras("todos_los_caminos_jugador1.txt", "cadenas_ganadoras_jugador1.txt", 16);
        ArchivosSalida.filtrarCadenasGanadoras("todos_los_caminos_jugador2.txt", "cadenas_ganadoras_jugador2.txt", 13);

        System.out.println("\nArchivos generados exitosamente!");
        System.out.println("Iniciando interfaz gráfica...");
        TableroGUI.launch(TableroGUI.class, new String[0]);
    }

}