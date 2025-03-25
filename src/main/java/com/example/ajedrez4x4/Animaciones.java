package com.example.ajedrez4x4;

import javafx.animation.TranslateTransition;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Animaciones {

    private GridPane tablero;

    public Animaciones(GridPane tablero) {
        this.tablero = tablero;
    }

    // Función para mover una pieza
    public void moverPieza(Circle pieza, int[] cadena) {
        for (int i = 1; i < cadena.length; i++) {
            int casillaInicial = cadena[i - 1];
            int casillaFinal = cadena[i];

            // Obtener las coordenadas de la casilla final
            int filaFinal = (casillaFinal - 1) / 4;
            int columnaFinal = (casillaFinal - 1) % 4;

            // Obtener las coordenadas de la casilla inicial
            int filaInicial = (casillaInicial - 1) / 4;
            int columnaInicial = (casillaInicial - 1) % 4;

            // Calcular el desplazamiento
            double desplazamientoX = (columnaFinal - columnaInicial) * (tablero.getHgap() + 80);
            double desplazamientoY = (filaFinal - filaInicial) * (tablero.getVgap() + 80);

            // Crear la animación
            TranslateTransition transicion = new TranslateTransition(Duration.seconds(1), pieza);
            transicion.setByX(desplazamientoX);
            transicion.setByY(desplazamientoY);
            transicion.play();
        }
    }
}