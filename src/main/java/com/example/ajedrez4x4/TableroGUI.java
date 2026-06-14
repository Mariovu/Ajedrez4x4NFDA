package com.example.ajedrez4x4;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
    private Stage stage;

    private TextArea txtHistorial;

    // Variable global para controlar la animación y poder detenerla desde los botones
    private SequentialTransition secuenciaAnimacion;

    private static final int TAMANO_MATRIZ = 10;
    private static final int TAMANO_CASILLA = 60;

    private int posicionRealJ1 = 1;
    private int posicionRealJ2 = 10;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;

        HBox root = new HBox(20);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #2c3e50;");

        tablero = new GridPane();
        tablero.setHgap(2);
        tablero.setVgap(2);

        for (int fila = 0; fila < TAMANO_MATRIZ; fila++) {
            for (int columna = 0; columna < TAMANO_MATRIZ; columna++) {
                Rectangle casilla = new Rectangle(TAMANO_CASILLA, TAMANO_CASILLA);
                casilla.setFill((fila + columna) % 2 == 0 ? Color.RED : Color.BLACK);

                Label numeroCasilla = new Label(String.valueOf(fila * TAMANO_MATRIZ + columna + 1));
                numeroCasilla.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                numeroCasilla.setTextFill(Color.WHITE);

                tablero.add(casilla, columna, fila);
                tablero.add(numeroCasilla, columna, fila);
            }
        }

        piezaJugador1 = new Circle(22, Color.WHITE);
        piezaJugador2 = new Circle(22, Color.BLUE);
        tablero.add(piezaJugador1, 0, 0);
        tablero.add(piezaJugador2, 9, 0);

        cadenaJugador1 = leerCadenaAleatoriaDesdeArchivo("cadenas_ganadoras_jugador1.txt");
        cadenaJugador2 = leerCadenaAleatoriaDesdeArchivo("cadenas_ganadoras_jugador2.txt");

        // PANEL DERECHO
        VBox panelDerecho = new VBox(10);
        panelDerecho.setPrefWidth(350);

        int movimientosJugar = Math.max(cadenaJugador1.length - 1, 0);
        Label lblTitulo = new Label("Cantidad de movimientos a jugar: " + movimientosJugar);
        lblTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblTitulo.setTextFill(Color.WHITE);

        txtHistorial = new TextArea();
        txtHistorial.setEditable(false);
        txtHistorial.setWrapText(true);
        txtHistorial.setFont(Font.font("Consolas", 13));
        txtHistorial.setStyle("-fx-control-inner-background: #1e272e; -fx-text-fill: #ecf0f1;");
        VBox.setVgrow(txtHistorial, Priority.ALWAYS);

        // NUEVO: Contenedor para los botones inferiores
        HBox panelBotones = new HBox(15);
        panelBotones.setAlignment(Pos.CENTER);

        Button btnVolver = new Button("Volver al Menú");
        Button btnAyuda = new Button("Ayuda");

        String estiloBtn = "-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 4;";
        btnVolver.setStyle(estiloBtn);
        btnAyuda.setStyle(estiloBtn);

        // Hover
        btnVolver.setOnMouseEntered(e -> btnVolver.setStyle(estiloBtn + "-fx-background-color: #e74c3c;"));
        btnVolver.setOnMouseExited(e -> btnVolver.setStyle(estiloBtn));
        btnAyuda.setOnMouseEntered(e -> btnAyuda.setStyle(estiloBtn + "-fx-background-color: #f39c12;"));
        btnAyuda.setOnMouseExited(e -> btnAyuda.setStyle(estiloBtn));

        // ACCIÓN: Volver al menú
        btnVolver.setOnAction(e -> {
            if (secuenciaAnimacion != null) {
                secuenciaAnimacion.stop(); // Detiene el hilo de juego para no dejar procesos fantasma
            }
            try {
                new Main().start(this.stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // ACCIÓN: Ayuda (Ventana Emergente)
        btnAyuda.setOnAction(e -> {
            Alert alertaAyuda = new Alert(Alert.AlertType.INFORMATION);
            alertaAyuda.setTitle("Información del Sistema");
            alertaAyuda.setHeaderText("Funcionamiento del Tablero 10x10");
            alertaAyuda.setContentText("Este tablero muestra la obtención de las rutas ganadoras de cada ficha mediante el algoritmo de Búsqueda en Profundidad (DFS).\n\n" +
                    "Durante la simulación, ambas fichas intentan cruzar la matriz. Si se encuentran y se estorban, el sistema reacciona dinámicamente: la ficha bloqueada recalcula inmediatamente su ruta y se redirige a una nueva casilla libre, logrando evadir a la pieza contraria para continuar hacia su objetivo.");
            alertaAyuda.initModality(Modality.APPLICATION_MODAL);
            alertaAyuda.showAndWait();
        });

        panelBotones.getChildren().addAll(btnVolver, btnAyuda);

        // Agregamos todo al panel derecho
        panelDerecho.getChildren().addAll(lblTitulo, txtHistorial, panelBotones);
        root.getChildren().addAll(tablero, panelDerecho);

        Scene scene = new Scene(root, (TAMANO_CASILLA + 2) * TAMANO_MATRIZ + 400, (TAMANO_CASILLA + 2) * TAMANO_MATRIZ + 30);
        primaryStage.setTitle("Simulación de Rutas - Ajedrez 10x10");
        primaryStage.setScene(scene);
        primaryStage.show();

        txtHistorial.appendText("Inicio de la partida...\n");
        txtHistorial.appendText("J1 posicionado en " + posicionRealJ1 + "\n");
        txtHistorial.appendText("J2 posicionado en " + posicionRealJ2 + "\n");
        txtHistorial.appendText("--------------------------------\n");

        moverPiezas();
    }

    private void registrarLogSecuencial(SequentialTransition secuencia, String mensaje) {
        PauseTransition actualizacionUI = new PauseTransition(Duration.millis(1));
        actualizacionUI.setOnFinished(e -> {
            txtHistorial.appendText(mensaje + "\n");
            txtHistorial.selectPositionCaret(txtHistorial.getLength());
            txtHistorial.deselect();
        });
        secuencia.getChildren().add(actualizacionUI);
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
        int fila = (casillaActual - 1) / TAMANO_MATRIZ;
        int columna = (casillaActual - 1) % TAMANO_MATRIZ;

        for (int df = -1; df <= 1; df++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (df == 0 && dc == 0) continue;

                int nuevaFila = fila + df;
                int nuevaColumna = columna + dc;

                if (nuevaFila >= 0 && nuevaFila < TAMANO_MATRIZ && nuevaColumna >= 0 && nuevaColumna < TAMANO_MATRIZ) {
                    adyacentes.add(nuevaFila * TAMANO_MATRIZ + nuevaColumna + 1);
                }
            }
        }
        return adyacentes;
    }

    private boolean esMovimientoValido(int actual, int siguiente) {
        return obtenerCasillasAdyacentes(actual).contains(siguiente);
    }

    private TranslateTransition crearTransicion(Circle pieza, int casillaInicial, int casillaFinal) {
        int filaInicial = (casillaInicial - 1) / TAMANO_MATRIZ;
        int columnaInicial = (casillaInicial - 1) % TAMANO_MATRIZ;
        int filaFinal = (casillaFinal - 1) / TAMANO_MATRIZ;
        int columnaFinal = (casillaFinal - 1) % TAMANO_MATRIZ;

        TranslateTransition transicion = new TranslateTransition(Duration.seconds(0.5), pieza);
        transicion.setByX((columnaFinal - columnaInicial) * (tablero.getHgap() + TAMANO_CASILLA));
        transicion.setByY((filaFinal - filaInicial) * (tablero.getVgap() + TAMANO_CASILLA));
        return transicion;
    }

    private void moverPiezas() {
        secuenciaAnimacion = new SequentialTransition();
        final boolean[] empate = {false};
        final int[][] rutasActuales = {cadenaJugador1.clone(), cadenaJugador2.clone()};

        int turno = 0;
        int ganador = 0;

        while (ganador == 0 && !empate[0] && turno < 100) {

            registrarLogSecuencial(secuenciaAnimacion, "\n[Turno " + turno + "]");

            if (posicionRealJ1 != 100) {
                procesarMovimiento(turno, true, rutasActuales, secuenciaAnimacion);
                if (posicionRealJ1 == 100) {
                    ganador = 1;
                    break;
                }
            }

            if (posicionRealJ2 != 91) {
                procesarMovimiento(turno, false, rutasActuales, secuenciaAnimacion);
                if (posicionRealJ2 == 91) {
                    ganador = 2;
                    break;
                }
            }

            if (turno > 0 && estaBloqueado(posicionRealJ1, rutasActuales[0], turno, posicionRealJ2) &&
                    estaBloqueado(posicionRealJ2, rutasActuales[1], turno, posicionRealJ1)) {
                empate[0] = true;
                registrarLogSecuencial(secuenciaAnimacion, "⚠ ¡Empate detectado! Ambos jugadores bloqueados.");
            }

            turno++;
        }

        final int ganadorFinal = ganador;
        secuenciaAnimacion.setOnFinished(e -> mostrarResultadoFinal(ganadorFinal, empate[0]));
        secuenciaAnimacion.play();
    }

    private void procesarMovimiento(int turno, boolean esJugador1,
                                    int[][] rutasActuales, SequentialTransition secuencia) {
        int jugadorIdx = esJugador1 ? 0 : 1;
        int posicionActual = esJugador1 ? posicionRealJ1 : posicionRealJ2;
        int posicionOponente = esJugador1 ? posicionRealJ2 : posicionRealJ1;
        Circle pieza = esJugador1 ? piezaJugador1 : piezaJugador2;
        String nombreJugador = esJugador1 ? "J1" : "J2";

        boolean movimientoRealizado = false;

        if (turno + 1 < rutasActuales[jugadorIdx].length) {
            if (rutasActuales[jugadorIdx][turno] == posicionActual) {
                int siguienteCasilla = rutasActuales[jugadorIdx][turno + 1];

                if (siguienteCasilla >= 1 && siguienteCasilla <= (TAMANO_MATRIZ * TAMANO_MATRIZ) &&
                        esMovimientoValido(posicionActual, siguienteCasilla) &&
                        siguienteCasilla != posicionOponente) {

                    registrarLogSecuencial(secuencia, "  ▶ " + nombreJugador + " se mueve a " + siguienteCasilla);

                    secuencia.getChildren().addAll(
                            crearTransicion(pieza, posicionActual, siguienteCasilla),
                            new PauseTransition(Duration.seconds(0.3))
                    );
                    if (esJugador1) posicionRealJ1 = siguienteCasilla;
                    else posicionRealJ2 = siguienteCasilla;

                    movimientoRealizado = true;
                }
            }
        }

        if (!movimientoRealizado) {
            boolean encontroRuta = buscarRutaAlternativa(turno, esJugador1, rutasActuales, secuencia);
            if (!encontroRuta) {
                registrarLogSecuencial(secuencia, "  ⏸ " + nombreJugador + " salta el turno por bloqueo");
                secuencia.getChildren().add(new PauseTransition(Duration.seconds(0.5)));
            }
        }
    }

    private boolean buscarRutaAlternativa(int turno, boolean esJugador1,
                                          int[][] rutasActuales, SequentialTransition secuencia) {
        try {
            String archivo = esJugador1 ? "cadenas_ganadoras_jugador1.txt" : "cadenas_ganadoras_jugador2.txt";
            int jugadorIdx = esJugador1 ? 0 : 1;
            int posicionActual = esJugador1 ? posicionRealJ1 : posicionRealJ2;
            int posicionOponente = esJugador1 ? posicionRealJ2 : posicionRealJ1;
            String nombreJugador = esJugador1 ? "J1" : "J2";

            List<int[]> rutasValidas = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    int[] ruta = parsearRuta(linea);

                    if (ruta.length > turno + 1 && ruta[turno] == posicionActual) {
                        int siguienteCasilla = ruta[turno + 1];
                        if (siguienteCasilla >= 1 && siguienteCasilla <= (TAMANO_MATRIZ * TAMANO_MATRIZ) &&
                                esMovimientoValido(posicionActual, siguienteCasilla) &&
                                siguienteCasilla != posicionOponente) {
                            rutasValidas.add(ruta);
                        }
                    }
                }
            }

            if (!rutasValidas.isEmpty()) {
                int[] nuevaRuta = rutasValidas.get(new Random().nextInt(rutasValidas.size()));
                rutasActuales[jugadorIdx] = nuevaRuta;

                int siguienteCasilla = nuevaRuta[turno + 1];
                Circle pieza = esJugador1 ? piezaJugador1 : piezaJugador2;

                registrarLogSecuencial(secuencia, "  🔄 " + nombreJugador + " cambia de ruta y esquiva hacia " + siguienteCasilla);

                secuencia.getChildren().addAll(
                        crearTransicion(pieza, posicionActual, siguienteCasilla),
                        new PauseTransition(Duration.seconds(0.3))
                );

                if (esJugador1) posicionRealJ1 = siguienteCasilla;
                else posicionRealJ2 = siguienteCasilla;

                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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

    private void mostrarResultadoFinal(int ganador, boolean empate) {
        Platform.runLater(() -> {
            if (empate) {
                txtHistorial.appendText("\n[FIN] Empate. Jugadores bloqueados.");
            } else if (ganador == 1) {
                txtHistorial.appendText("\n[FIN] ¡Victoria para J1!");
            } else if (ganador == 2) {
                txtHistorial.appendText("\n[FIN] ¡Victoria para J2!");
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fin del juego");
            alert.setHeaderText(null);

            if (empate) {
                alert.setContentText("¡Empate! Ambos jugadores están bloqueados.");
            } else if (ganador == 1) {
                alert.setContentText("¡Ganó Jugador 1! Llegó a la casilla 100.");
            } else if (ganador == 2) {
                alert.setContentText("¡Ganó Jugador 2! Llegó a la casilla 91.");
            } else {
                alert.setContentText("¡Juego terminado sin ganador!");
            }

            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();

            try {
                new Main().start(this.stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}