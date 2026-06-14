package com.example.ajedrez4x4;

import javafx.animation.TranslateTransition;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Animaciones {

    private GridPane tablero;
    private static final int TAMANO_MATRIZ = 10;
    private static final int TAMANO_CASILLA = 60;

    public Animaciones(GridPane tablero) {
        this.tablero = tablero;
    }

    // Función para mover una pieza adaptada al tablero de 10x10
    public void moverPieza(Circle pieza, int[] cadena) {
        for (int i = 1; i < cadena.length; i++) {
            int casillaInicial = cadena[i - 1];
            int casillaFinal = cadena[i];

            // Coordenadas basadas en divisiones y módulos de 10
            int filaFinal = (casillaFinal - 1) / TAMANO_MATRIZ;
            int columnaFinal = (casillaFinal - 1) % TAMANO_MATRIZ;

            int filaInicial = (casillaInicial - 1) / TAMANO_MATRIZ;
            int columnaInicial = (casillaInicial - 1) % TAMANO_MATRIZ;

            // Cálculo del desplazamiento con la nueva escala de 60px
            double desplazamientoX = (columnaFinal - columnaInicial) * (tablero.getHgap() + TAMANO_CASILLA);
            double desplazamientoY = (filaFinal - filaInicial) * (tablero.getVgap() + TAMANO_CASILLA);

            // Crear la animación
            TranslateTransition transicion = new TranslateTransition(Duration.seconds(0.5), pieza);
            transicion.setByX(desplazamientoX);
            transicion.setByY(desplazamientoY);
            transicion.play();
        }
    }
}