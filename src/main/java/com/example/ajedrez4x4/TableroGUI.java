package com.example.ajedrez4x4;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TableroGUI extends Application {
    private Circle piezaJugador1, piezaJugador2;
    private GridPane tablero;
    private int[] cadenaJugador1, cadenaJugador2;

    private int posicionRealJ1 = 1;  // Inicialmente en casilla 1
    private int posicionRealJ2 = 4;

    @Override
    public void start(Stage primaryStage) {
        tablero = new GridPane();
        tablero.setHgap(2);
        tablero.setVgap(2);
        int tamañoCasilla = 80;

        for (int fila = 0; fila < 4; fila++) {
            for (int columna = 0; columna < 4; columna++) {
                Rectangle casilla = new Rectangle(tamañoCasilla, tamañoCasilla);
                casilla.setFill((fila + columna) % 2 == 0 ? Color.RED : Color.BLACK);
                Label numeroCasilla = new Label(String.valueOf(fila * 4 + columna + 1));
                numeroCasilla.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                numeroCasilla.setTextFill(Color.WHITE);
                tablero.add(casilla, columna, fila);
                tablero.add(numeroCasilla, columna, fila);
            }
        }

        piezaJugador1 = new Circle(30, Color.WHITE);
        piezaJugador2 = new Circle(30, Color.BLUE);
        tablero.add(piezaJugador1, 0, 0);
        tablero.add(piezaJugador2, 3, 0);

        cadenaJugador1 = leerCadenaAleatoriaDesdeArchivo("cadenas_ganadoras_jugador1.txt");
        cadenaJugador2 = leerCadenaAleatoriaDesdeArchivo("cadenas_ganadoras_jugador2.txt");

        Scene scene = new Scene(tablero, tamañoCasilla * 4, tamañoCasilla * 4);
        primaryStage.setTitle("Ajedrez 4x4");
        primaryStage.setScene(scene);
        primaryStage.show();

        moverPiezas();
    }

    private int[] leerCadenaAleatoriaDesdeArchivo(String archivo) {
        List<int[]> cadenas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.replace("[", "").replace("]", "").split(", ");
                int[] cadena = new int[partes.length];
                for (int i = 0; i < partes.length; i++) {
                    cadena[i] = Integer.parseInt(partes[i].trim());
                }
                cadenas.add(cadena);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cadenas.isEmpty() ? new int[0] : cadenas.get(new Random().nextInt(cadenas.size()));
    }

    private List<Integer> obtenerCasillasAdyacentes(int casillaActual) {
        List<Integer> adyacentes = new ArrayList<>();
        int fila = (casillaActual - 1) / 4;
        int columna = (casillaActual - 1) % 4;

        for (int df = -1; df <= 1; df++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (df == 0 && dc == 0) continue;

                int nuevaFila = fila + df;
                int nuevaColumna = columna + dc;

                if (nuevaFila >= 0 && nuevaFila < 4 && nuevaColumna >= 0 && nuevaColumna < 4) {
                    adyacentes.add(nuevaFila * 4 + nuevaColumna + 1);
                }
            }
        }
        return adyacentes;
    }

    private boolean esMovimientoValido(int actual, int siguiente) {
        return obtenerCasillasAdyacentes(actual).contains(siguiente);
    }

    private TranslateTransition crearTransicion(Circle pieza, int casillaInicial, int casillaFinal) {
        int filaInicial = (casillaInicial - 1) / 4;
        int columnaInicial = (casillaInicial - 1) % 4;
        int filaFinal = (casillaFinal - 1) / 4;
        int columnaFinal = (casillaFinal - 1) % 4;

        TranslateTransition transicion = new TranslateTransition(Duration.seconds(0.5), pieza);
        transicion.setByX((columnaFinal - columnaInicial) * (tablero.getHgap() + 80));
        transicion.setByY((filaFinal - filaInicial) * (tablero.getVgap() + 80));
        return transicion;
    }

    private void moverPiezas() {
        SequentialTransition secuencia = new SequentialTransition();
        int maxMovimientos = Math.max(cadenaJugador1.length, cadenaJugador2.length);

        final boolean[] empate = {false};
        final int[][] rutasActuales = {cadenaJugador1.clone(), cadenaJugador2.clone()};

        for (int i = 0; i < maxMovimientos && !empate[0]; i++) {
            System.out.printf("\nTurno %d: J1=%d (meta: %d) | J2=%d (meta: %d)",
                    i, posicionRealJ1, rutasActuales[0][i],
                    posicionRealJ2, rutasActuales[1][i]);

            if (i < rutasActuales[0].length) {
                procesarMovimiento(i, true, rutasActuales, secuencia);
            }

            if (i < rutasActuales[1].length) {
                procesarMovimiento(i, false, rutasActuales, secuencia);
            }

            if (i > 0 && estaBloqueado(posicionRealJ1, rutasActuales[0], i, posicionRealJ2) &&
                    estaBloqueado(posicionRealJ2, rutasActuales[1], i, posicionRealJ1)) {
                empate[0] = true;
                System.out.println("\n¡Empate detectado!");
            }
        }

        secuencia.setOnFinished(e -> mostrarResultadoFinal(posicionRealJ1, posicionRealJ2, empate[0]));
        secuencia.play();
    }

    private void procesarMovimiento(int turno, boolean esJugador1,
                                    int[][] rutasActuales, SequentialTransition secuencia) {
        int jugadorIdx = esJugador1 ? 0 : 1;
        int posicionActual = esJugador1 ? posicionRealJ1 : posicionRealJ2;
        int posicionOponente = esJugador1 ? posicionRealJ2 : posicionRealJ1;
        int siguienteCasilla = rutasActuales[jugadorIdx][turno];
        Circle pieza = esJugador1 ? piezaJugador1 : piezaJugador2;

        if (siguienteCasilla < 1 || siguienteCasilla > 16) {
            System.out.printf("\n%s intenta moverse a casilla inválida %d", esJugador1 ? "J1" : "J2", siguienteCasilla);
            buscarRutaAlternativa(turno, esJugador1, rutasActuales, secuencia);
            return;
        }

        if (esMovimientoValido(posicionActual, siguienteCasilla) &&
                siguienteCasilla != posicionOponente) {
            secuencia.getChildren().addAll(
                    crearTransicion(pieza, posicionActual, siguienteCasilla),
                    new PauseTransition(Duration.seconds(0.5))
            );
            if (esJugador1) {
                posicionRealJ1 = siguienteCasilla;
            } else {
                posicionRealJ2 = siguienteCasilla;
            }
            System.out.printf("\n%s se mueve a %d", esJugador1 ? "J1" : "J2", siguienteCasilla);
        } else {
            buscarRutaAlternativa(turno, esJugador1, rutasActuales, secuencia);
        }
    }

    private void buscarRutaAlternativa(int turno, boolean esJugador1,
                                       int[][] rutasActuales, SequentialTransition secuencia) {
        try {
            String archivo = esJugador1 ? "cadenas_ganadoras_jugador1.txt" : "cadenas_ganadoras_jugador2.txt";
            int jugadorIdx = esJugador1 ? 0 : 1;
            int posicionActual = esJugador1 ? posicionRealJ1 : posicionRealJ2;
            int posicionOponente = esJugador1 ? posicionRealJ2 : posicionRealJ1;

            List<int[]> rutasValidas = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    int[] ruta = parsearRuta(linea);
                    if (ruta.length > turno && ruta[turno] == posicionActual) {
                        if (ruta.length > turno + 1) {
                            int siguienteCasilla = ruta[turno + 1];
                            if (siguienteCasilla >= 1 && siguienteCasilla <= 16 &&
                                    esMovimientoValido(posicionActual, siguienteCasilla) &&
                                    siguienteCasilla != posicionOponente) {
                                rutasValidas.add(ruta);
                            }
                        }
                    }
                }
            }

            if (!rutasValidas.isEmpty()) {
                int[] nuevaRuta = rutasValidas.get(new Random().nextInt(rutasValidas.size()));
                System.arraycopy(nuevaRuta, 0, rutasActuales[jugadorIdx], 0,
                        Math.min(nuevaRuta.length, rutasActuales[jugadorIdx].length));

                int siguienteCasilla = nuevaRuta[turno + 1];
                Circle pieza = esJugador1 ? piezaJugador1 : piezaJugador2;

                secuencia.getChildren().addAll(
                        crearTransicion(pieza, posicionActual, siguienteCasilla),
                        new PauseTransition(Duration.seconds(0.5))
                );

                if (esJugador1) {
                    posicionRealJ1 = siguienteCasilla;
                } else {
                    posicionRealJ2 = siguienteCasilla;
                }
                System.out.printf("\n%s usa ruta alternativa: %s", esJugador1 ? "J1" : "J2",
                        Arrays.toString(nuevaRuta));
            } else {
                System.out.printf("\n%s no tiene movimientos válidos desde %d",
                        esJugador1 ? "J1" : "J2", posicionActual);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[] parsearRuta(String linea) {
        String[] partes = linea.replace("[", "").replace("]", "").split(", ");
        int[] ruta = new int[partes.length];
        for (int i = 0; i < partes.length; i++) {
            ruta[i] = Integer.parseInt(partes[i].trim());
        }
        return ruta;
    }

    private boolean estaBloqueado(int posicion, int[] ruta, int turno, int posOponente) {
        if (turno >= ruta.length) return true;

        boolean movimientoInvalido = !esMovimientoValido(posicion, ruta[turno]) ||
                ruta[turno] == posOponente;

        boolean tieneMovimientosPosibles = obtenerCasillasAdyacentes(posicion).stream()
                .anyMatch(casilla -> casilla != posOponente &&
                        Tablero.obtenerColor(casilla) == obtenerColorSiguiente(ruta, turno));

        return movimientoInvalido && !tieneMovimientosPosibles;
    }

    private char obtenerColorSiguiente(int[] ruta, int turno) {
        return (turno < ruta.length - 1) ? Tablero.obtenerColor(ruta[turno + 1]) : 'r';
    }

    private void mostrarResultadoFinal(int posJ1, int posJ2, boolean empate) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fin del juego");
            alert.setHeaderText(null);

            if (empate) {
                alert.setContentText("¡Empate! Ambos jugadores están bloqueados");
            } else if (posJ1 == 16) {
                alert.setContentText("¡Ganó Jugador 1!");
            } else if (posJ2 == 13) {
                alert.setContentText("¡Ganó Jugador 2!");
            } else {
                alert.setContentText("¡Juego terminado sin ganador!");
            }

            alert.initModality(Modality.APPLICATION_MODAL);
            alert.show();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}