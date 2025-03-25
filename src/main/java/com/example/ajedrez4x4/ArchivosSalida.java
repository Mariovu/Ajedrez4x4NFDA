package com.example.ajedrez4x4;

import java.io.*;

public class ArchivosSalida {
    public static void filtrarCadenasGanadoras(String archivoEntrada, String archivoSalida, int casillaFinal) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoEntrada));
             FileWriter writer = new FileWriter(archivoSalida)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                // Obtener la última casilla de la ruta
                int ultimaCasilla = Integer.parseInt(linea.substring(linea.lastIndexOf(" ") + 1, linea.length() - 1));

                // Verificar si la última casilla coincide con la casilla final
                if (ultimaCasilla == casillaFinal) {
                    writer.write(linea + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}