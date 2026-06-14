package com.example.ajedrez4x4;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Random;

public class Main extends Application {

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        mostrarMenuPrincipal();
    }

    // VISTA 1: Menú Principal Estético (Estilo Cyber/Neon)
    private void mostrarMenuPrincipal() {
        VBox root = new VBox(25); // Espaciado un poco más amplio
        root.setPadding(new Insets(50));
        root.setAlignment(Pos.CENTER);

        // Degradado oscuro elegante (Morado profundo a Azul noche)
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f0c29, #302b63, #24243e);");

        Label titulo = new Label("Tablero 10x10 ");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        titulo.setTextFill(Color.web("#00e5ff")); // Cyan Neón
        // Efecto de resplandor (Glow) en el título
        titulo.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 229, 255, 0.6), 15, 0.3, 0, 0);");

        Button btnManual = new Button("Jugar Ajedrez (Manual)");
        Button btnAutomatico = new Button("Jugar Ajedrez (Automático)");
        Button btnGrafo = new Button("Visualizar Grafo del Jugador 1");

        // Botones transparentes con borde de color
        String estiloBotones = "-fx-background-color: rgba(255, 255, 255, 0.05); -fx-text-fill: white; -fx-font-size: 15px; " +
                "-fx-font-weight: bold; -fx-min-width: 300px; -fx-padding: 14px; " +
                "-fx-border-color: #00e5ff; -fx-border-radius: 30; -fx-background-radius: 30; -fx-cursor: hand;";

        // Botones sólidos al pasar el mouse (Hover)
        String estiloHover = "-fx-background-color: linear-gradient(to right, #00e5ff, #0076ff); -fx-text-fill: white; -fx-font-size: 15px; " +
                "-fx-font-weight: bold; -fx-min-width: 300px; -fx-padding: 14px; " +
                "-fx-border-color: transparent; -fx-border-radius: 30; -fx-background-radius: 30; -fx-cursor: hand;";

        btnManual.setStyle(estiloBotones);
        btnAutomatico.setStyle(estiloBotones);
        btnGrafo.setStyle(estiloBotones);

        btnManual.setOnMouseEntered(e -> btnManual.setStyle(estiloHover));
        btnManual.setOnMouseExited(e -> btnManual.setStyle(estiloBotones));
        btnAutomatico.setOnMouseEntered(e -> btnAutomatico.setStyle(estiloHover));
        btnAutomatico.setOnMouseExited(e -> btnAutomatico.setStyle(estiloBotones));
        btnGrafo.setOnMouseEntered(e -> btnGrafo.setStyle(estiloHover));
        btnGrafo.setOnMouseExited(e -> btnGrafo.setStyle(estiloBotones));

        btnManual.setOnAction(e -> mostrarVistaManual());
        btnAutomatico.setOnAction(e -> iniciarJuegoAutomatico());
        btnGrafo.setOnAction(e -> AutomataGrafico.mostrarGrafo("cadenas_ganadoras_jugador1.txt"));

        root.getChildren().addAll(titulo, btnManual, btnAutomatico, btnGrafo);

        Scene scene = new Scene(root, 500, 420);
        primaryStage.setTitle("Menú Principal - Autómatas");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // VISTA 2: Formulario Manual Rediseñado
    private void mostrarVistaManual() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        // Mismo fondo para mantener la consistencia
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f0c29, #302b63, #24243e);");

        Label titulo = new Label("Configuración Manual");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.web("#ff007f")); // Magenta Neón
        titulo.setStyle("-fx-effect: dropshadow(gaussian, rgba(255, 0, 127, 0.5), 10, 0.2, 0, 0);");

        // Panel de instrucciones estilo cristal (Glassmorphism) oscuro
        VBox panelInstrucciones = new VBox(8);
        panelInstrucciones.setPadding(new Insets(15));
        panelInstrucciones.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-background-radius: 12; " +
                "-fx-border-color: #ff007f; -fx-border-width: 1.5; -fx-border-radius: 12;");

        Label lbInstruccionesTitulo = new Label("REGLAS DEL AUTÓMATA:");
        lbInstruccionesTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lbInstruccionesTitulo.setTextFill(Color.web("#ff007f"));

        Label lbRegla1 = new Label("• Ambas cadenas deben tener la misma longitud exacta.");
        Label lbRegla2 = new Label("• Mínimo 9 movimientos (Ej: \"rrrrrrrrr\" es victoria directa).");
        Label lbRegla3 = new Label("• Recomendado: 18 a 35 caracteres para permitir evasiones.");

        String estiloTextoReglas = "-fx-text-fill: #dcdde1; -fx-font-size: 12px;";
        lbRegla1.setStyle(estiloTextoReglas);
        lbRegla2.setStyle(estiloTextoReglas);
        lbRegla3.setStyle(estiloTextoReglas);
        panelInstrucciones.getChildren().addAll(lbInstruccionesTitulo, lbRegla1, lbRegla2, lbRegla3);

        // Estilos para los campos de texto
        String estiloLabel = "-fx-text-fill: #00e5ff; -fx-font-weight: bold; -fx-font-size: 13px;";
        String estiloInput = "-fx-padding: 10; -fx-background-radius: 8; -fx-background-color: rgba(255, 255, 255, 0.9); -fx-font-family: 'Consolas'; -fx-font-size: 14px;";

        Label lbJ1 = new Label("Cadena Jugador 1 (Blanco):");
        lbJ1.setStyle(estiloLabel);
        TextField txtJ1 = new TextField();
        txtJ1.setPromptText("Ej: rbrbrbrbrbrbrb");
        txtJ1.setStyle(estiloInput);

        Label lbJ2 = new Label("Cadena Jugador 2 (Azul):");
        lbJ2.setStyle(estiloLabel);
        TextField txtJ2 = new TextField();
        txtJ2.setPromptText("Ej: brbrbrbrbrbrbr");
        txtJ2.setStyle(estiloInput);

        Button btnIniciar = new Button("Iniciar Simulación");
        Button btnVolver = new Button("Volver");

        // Botón Iniciar (Verde/Teal Brillante)
        btnIniciar.setStyle("-fx-background-color: linear-gradient(to right, #11998e, #38ef7d); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-min-width: 220; -fx-background-radius: 25; -fx-cursor: hand;");
        // Botón Volver (Gris oscuro con borde)
        btnVolver.setStyle("-fx-background-color: transparent; -fx-border-color: #7f8fa6; -fx-text-fill: #7f8fa6; -fx-font-weight: bold; -fx-padding: 10; -fx-min-width: 220; -fx-background-radius: 25; -fx-border-radius: 25; -fx-cursor: hand;");

        btnVolver.setOnMouseEntered(e -> btnVolver.setStyle("-fx-background-color: #7f8fa6; -fx-text-fill: #2f3640; -fx-font-weight: bold; -fx-padding: 10; -fx-min-width: 220; -fx-background-radius: 25; -fx-cursor: hand;"));
        btnVolver.setOnMouseExited(e -> btnVolver.setStyle("-fx-background-color: transparent; -fx-border-color: #7f8fa6; -fx-text-fill: #7f8fa6; -fx-font-weight: bold; -fx-padding: 10; -fx-min-width: 220; -fx-background-radius: 25; -fx-border-radius: 25; -fx-cursor: hand;"));

        btnIniciar.setOnAction(e -> {
            String c1 = txtJ1.getText().trim().toLowerCase();
            String c2 = txtJ2.getText().trim().toLowerCase();

            if (c1.isEmpty() || c2.isEmpty()) {
                mostrarAlerta("Campos Vacíos", "Por favor, defina las cadenas para ambos autómatas.");
                return;
            }
            if (c1.length() != c2.length()) {
                mostrarAlerta("Error de Estructura", "Las cadenas de entrada deben tener longitudes equivalentes.");
                return;
            }
            if (c1.length() < 9) {
                mostrarAlerta("Longitud Insuficiente", "Se requieren mínimo 9 movimientos para conectar los extremos opuestos de la matriz.");
                return;
            }

            ExploradorRutas.explorarRutasJugador1(c1, "cadenas_ganadoras_jugador1.txt");
            ExploradorRutas.explorarRutasJugador2(c2, "cadenas_ganadoras_jugador2.txt");
            desplegarMatrizDeJuego();
        });

        btnVolver.setOnAction(e -> mostrarMenuPrincipal());

        // HBox para alinear los botones lado a lado
        javafx.scene.layout.HBox cajaBotones = new javafx.scene.layout.HBox(15, btnVolver, btnIniciar);
        cajaBotones.setAlignment(Pos.CENTER);
        cajaBotones.setPadding(new Insets(10, 0, 0, 0));

        root.getChildren().addAll(titulo, panelInstrucciones, lbJ1, txtJ1, lbJ2, txtJ2, cajaBotones);

        Scene scene = new Scene(root, 540, 560);
        primaryStage.setScene(scene);
    }

    private void iniciarJuegoAutomatico() {
        Random rand = new Random();
        int longitud = 18 + rand.nextInt(18);

        String cadenaJ1 = generarCadenaAleatoria(longitud, true);
        String cadenaJ2 = generarCadenaAleatoria(longitud, false);

        ExploradorRutas.explorarRutasJugador1(cadenaJ1, "cadenas_ganadoras_jugador1.txt");
        ExploradorRutas.explorarRutasJugador2(cadenaJ2, "cadenas_ganadoras_jugador2.txt");

        desplegarMatrizDeJuego();
    }

    private String generarCadenaAleatoria(int longitud, boolean terminaEnR) {
        Random rand = new Random();
        StringBuilder cadena = new StringBuilder();
        for (int i = 0; i < longitud - 1; i++) {
            cadena.append(rand.nextBoolean() ? "r" : "b");
        }
        cadena.append(terminaEnR ? "r" : "b");
        return cadena.toString();
    }

    private void desplegarMatrizDeJuego() {
        TableroGUI tableroGUI = new TableroGUI();
        try {
            tableroGUI.start(primaryStage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarAlerta(String cabecera, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validación de Parámetros");
        alert.setHeaderText(cabecera);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}