package com.example.ajedrez4x4;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class AutomataGrafico {
    public static void mostrarGrafo(String rutaArchivo) {
        System.setProperty("org.graphstream.ui", "swing");
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        Graph grafo = new SingleGraph("Árbol de Movimientos Unificado");
        grafo.setStrict(false);
        grafo.setAttribute("ui.stylesheet",
                "node { size: 20px; fill-color: #FFA07A; text-size: 14; }");

        // Mapa para nodos únicos: Clave = "posición_valor"
        Map<String, String> nodosUnicos = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;

            // 1. Nodo raíz (posición 0, valor 1)
            String nodoRaiz = "0_1";
            grafo.addNode(nodoRaiz).setAttribute("ui.label", "1");
            nodosUnicos.put(nodoRaiz, nodoRaiz);

            // 2. Procesar cada ruta
            while ((linea = br.readLine()) != null) {
                String[] movimientos = linea.replaceAll("[\\[\\] ]", "").split(",");
                if (movimientos.length < 2 || !movimientos[0].equals("1")) continue;

                String nodoActual = nodoRaiz;

                // 3. Procesar cada movimiento
                for (int pos = 1; pos < movimientos.length; pos++) {
                    String claveNodo = pos + "_" + movimientos[pos].trim();

                    // Crear nodo si no existe
                    if (!nodosUnicos.containsKey(claveNodo)) {
                        grafo.addNode(claveNodo)
                                .setAttribute("ui.label", movimientos[pos].trim());
                        nodosUnicos.put(claveNodo, claveNodo);
                    }

                    // Conectar con nodo anterior (si no existe la arista)
                    String aristaId = nodoActual + "-" + claveNodo;
                    if (grafo.getEdge(aristaId) == null) {
                        grafo.addEdge(aristaId, nodoActual, claveNodo);
                    }

                    nodoActual = claveNodo;
                }
            }

            // 4. Visualización optimizada
            grafo.setAttribute("layout.force");
            Viewer viewer = grafo.display();
            viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}