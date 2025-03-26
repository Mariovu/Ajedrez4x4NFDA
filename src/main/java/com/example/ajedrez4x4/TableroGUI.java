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

    private int posicionRealJ1 = 1;
    private int posicionRealJ2 = 4;

    // Nuevas constantes para posiciones finales
    private static final int META_JUGADOR1 = 16;
    private static final int META_JUGADOR2 = 13;

    @Override
    public void start(Stage primaryStage) {
        inicializarTablero(primaryStage);
        cargarCadenasIniciales();
        mostrarVentana(primaryStage);
        moverPiezas();
    }

    private void inicializarTablero(Stage stage) {
        tablero = new GridPane();
        tablero.setHgap(2);
        tablero.setVgap(2);
        int tamañoCasilla = 80;

        for (int fila = 0; fila < 4; fila++) {
            for (int columna = 0; columna < 4; columna++) {
                crearCasilla(fila, columna, tamañoCasilla);
            }
        }

        piezaJugador1 = crearPieza(Color.WHITE, 0, 0);
        piezaJugador2 = crearPieza(Color.BLUE, 3, 0);
    }

    private void crearCasilla(int fila, int columna, int tamaño) {
        Rectangle casilla = new Rectangle(tamaño, tamaño);
        casilla.setFill((fila + columna) % 2 == 0 ? Color.RED : Color.BLACK);

        Label numero = new Label(String.valueOf(fila * 4 + columna + 1));
        numero.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        numero.setTextFill(Color.WHITE);

        tablero.add(casilla, columna, fila);
        tablero.add(numero, columna, fila);
    }

    private Circle crearPieza(Color color, int columna, int fila) {
        Circle pieza = new Circle(30, color);
        tablero.add(pieza, columna, fila);
        return pieza;
    }

    private void cargarCadenasIniciales() {
        cadenaJugador1 = leerCadenaAleatoriaDesdeArchivo("cadenas_ganadoras_jugador1.txt");
        cadenaJugador2 = leerCadenaAleatoriaDesdeArchivo("cadenas_ganadoras_jugador2.txt");
    }

    private int[] leerCadenaAleatoriaDesdeArchivo(String archivo) {
        List<int[]> cadenas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                cadenas.add(parsearRuta(linea));
            }
        } catch (IOException e) {
            mostrarError("Error al leer archivo: " + archivo);
        }
        return cadenas.isEmpty() ? new int[0] : cadenas.get(new Random().nextInt(cadenas.size()));
    }

    private void mostrarVentana(Stage stage) {
        Scene scene = new Scene(tablero, 80 * 4, 80 * 4);
        stage.setTitle("Ajedrez 4x4");
        stage.setScene(scene);
        stage.show();
    }

    private void moverPiezas() {
        SequentialTransition secuencia = new SequentialTransition();
        int[][] rutasActuales = {cadenaJugador1.clone(), cadenaJugador2.clone()};
        int maxMovimientos = calcularMaxMovimientos(rutasActuales);

        for (int turno = 0; turno < maxMovimientos; turno++) {
            if (verificarEmpate(turno, rutasActuales)) break;

            procesarTurno(turno, rutasActuales, secuencia);
        }

        secuencia.setOnFinished(e -> mostrarResultadoFinal());
        secuencia.play();
    }

    private int calcularMaxMovimientos(int[][] rutas) {
        return Math.max(rutas[0].length, rutas[1].length);
    }

    private boolean verificarEmpate(int turno, int[][] rutas) {
        if (turno > 0 && estaBloqueado(posicionRealJ1, rutas[0], turno, posicionRealJ2) &&
                estaBloqueado(posicionRealJ2, rutas[1], turno, posicionRealJ1)) {
            mostrarResultadoFinal(true);
            return true;
        }
        return false;
    }

    private void procesarTurno(int turno, int[][] rutas, SequentialTransition secuencia) {
        System.out.printf("\nTurno %d: J1=%d | J2=%d", turno, posicionRealJ1, posicionRealJ2);

        if (turno < rutas[0].length)
            procesarMovimiento(turno, true, rutas, secuencia);

        if (turno < rutas[1].length)
            procesarMovimiento(turno, false, rutas, secuencia);
    }

    private void procesarMovimiento(int turno, boolean esJugador1,
                                    int[][] rutas, SequentialTransition secuencia) {
        int jugadorIdx = esJugador1 ? 0 : 1;
        int posActual = esJugador1 ? posicionRealJ1 : posicionRealJ2;
        int siguiente = rutas[jugadorIdx][turno];

        if (esMovimientoValido(posActual, siguiente, obtenerPosicionOponente(esJugador1))) {
            ejecutarMovimiento(esJugador1, posActual, siguiente, secuencia);
            actualizarPosicion(esJugador1, siguiente);
        } else {
            buscarRutaAlternativa(turno, esJugador1, rutas, secuencia);
        }
    }

    private boolean esMovimientoValido(int actual, int siguiente, int oponente) {
        return obtenerCasillasAdyacentes(actual).contains(siguiente) && siguiente != oponente;
    }

    private List<Integer> obtenerCasillasAdyacentes(int casilla) {
        List<Integer> adyacentes = new ArrayList<>();
        int fila = (casilla - 1) / 4;
        int col = (casilla - 1) % 4;

        for (int df = -1; df <= 1; df++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (df == 0 && dc == 0) continue;
                int nuevaFila = fila + df;
                int nuevaCol = col + dc;
                if (nuevaFila >= 0 && nuevaFila < 4 && nuevaCol >= 0 && nuevaCol < 4) {
                    adyacentes.add(nuevaFila * 4 + nuevaCol + 1);
                }
            }
        }
        return adyacentes;
    }

    private void ejecutarMovimiento(boolean esJugador1, int desde, int hasta,
                                    SequentialTransition secuencia) {
        Circle pieza = esJugador1 ? piezaJugador1 : piezaJugador2;
        secuencia.getChildren().addAll(
                crearTransicion(pieza, desde, hasta),
                new PauseTransition(Duration.seconds(0.5))
        );
        System.out.printf("\n%s: %d → %d", esJugador1 ? "J1" : "J2", desde, hasta);
    }

    private TranslateTransition crearTransicion(Circle pieza, int desde, int hasta) {
        int[] coordenadasDesde = calcularCoordenadas(desde);
        int[] coordenadasHasta = calcularCoordenadas(hasta);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), pieza);
        tt.setByX((coordenadasHasta[1] - coordenadasDesde[1]) * 82);
        tt.setByY((coordenadasHasta[0] - coordenadasDesde[0]) * 82);
        return tt;
    }

    private int[] calcularCoordenadas(int casilla) {
        return new int[]{(casilla - 1) / 4, (casilla - 1) % 4};
    }

    private void actualizarPosicion(boolean esJugador1, int nuevaPosicion) {
        if (esJugador1) {
            posicionRealJ1 = nuevaPosicion;
        } else {
            posicionRealJ2 = nuevaPosicion;
        }
    }

    private void buscarRutaAlternativa(int turno, boolean esJugador1,
                                       int[][] rutas, SequentialTransition secuencia) {
        String archivo = esJugador1 ? "cadenas_ganadoras_jugador1.txt" : "cadenas_ganadoras_jugador2.txt";
        int jugadorIdx = esJugador1 ? 0 : 1;
        int posActual = esJugador1 ? posicionRealJ1 : posicionRealJ2;

        int[] nuevaRuta = ExploradorRutas.encontrarRutaAlternativaCompleta(
                turno, posActual, obtenerPosicionOponente(esJugador1), archivo
        );

        if (nuevaRuta != null && nuevaRuta.length > turno) {
            rutas[jugadorIdx] = nuevaRuta;
            int siguiente = nuevaRuta[turno];

            if (esMovimientoValido(posActual, siguiente, obtenerPosicionOponente(esJugador1))) {
                ejecutarMovimiento(esJugador1, posActual, siguiente, secuencia);
                actualizarPosicion(esJugador1, siguiente);
                System.out.printf("\n%s nueva ruta: %s", esJugador1 ? "J1" : "J2", Arrays.toString(nuevaRuta));
            }
        } else {
            System.out.printf("\n%s sin movimientos válidos", esJugador1 ? "J1" : "J2");
        }
    }

    private int obtenerPosicionOponente(boolean esJugador1) {
        return esJugador1 ? posicionRealJ2 : posicionRealJ1;
    }

    private boolean estaBloqueado(int posicion, int[] ruta, int turno, int oponente) {
        if (turno >= ruta.length) return true;

        boolean movimientoInvalido = !esMovimientoValido(posicion, ruta[turno], oponente);
        boolean sinAlternativas = obtenerCasillasAdyacentes(posicion).stream()
                .noneMatch(c -> c != oponente && Tablero.obtenerColor(c) == obtenerColorSiguiente(ruta, turno));

        return movimientoInvalido && sinAlternativas;
    }

    private char obtenerColorSiguiente(int[] ruta, int turno) {
        return (turno < ruta.length - 1) ? Tablero.obtenerColor(ruta[turno + 1]) : 'r';
    }

    private void mostrarResultadoFinal() {
        mostrarResultadoFinal(false);
    }

    private void mostrarResultadoFinal(boolean empate) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fin del juego");
            alert.setHeaderText(null);

            String mensaje;
            if (empate) {
                mensaje = "¡Empate! Ambos jugadores bloqueados";
            } else if (posicionRealJ1 == META_JUGADOR1) {
                mensaje = "¡Ganó Jugador 1!";
            } else if (posicionRealJ2 == META_JUGADOR2) {
                mensaje = "¡Ganó Jugador 2!";
            } else {
                mensaje = "¡Juego terminado sin ganador!";
            }

            alert.setContentText(mensaje);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.show();
        });
    }

    private void mostrarError(String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }

    private int[] parsearRuta(String linea) {
        String[] partes = linea.replaceAll("[\\[\\]]", "").split(", ");
        int[] ruta = new int[partes.length];
        for (int i = 0; i < partes.length; i++) {
            ruta[i] = Integer.parseInt(partes[i].trim());
        }
        return ruta;
    }

    public static void main(String[] args) {
        launch(args);
    }
}