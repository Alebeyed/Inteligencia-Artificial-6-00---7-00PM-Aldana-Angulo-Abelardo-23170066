package Unidad_1.Puzzle8BNI;

import java.util.LinkedList;

public class EjecutarPuzzle8BNI {

    public static void main(String[] args) {
        int[] inicial = {
            1, 2, 3,
            8, 0, 4,
            7, 6, 5
        };

        int[] meta = {
            1, 2, 8,
            4, 3, 7,
            6, 0, 5
        };

        EstrategiasBusqueda buscador = new EstrategiasBusqueda();

        System.out.println("===================================================================================");
        System.out.printf("%-26s | %-10s | %-10s | %-12s | %-12s | %-8s%n",
                "ALGORITMO", "PASOS", "COSTO g(n)", "EXPANDIDOS", "MAX FRONTERA", "TIEMPO");
        System.out.println("===================================================================================");

        imprimirFila("1. Anchura (BFS)", buscador.anchura(inicial, meta));
        imprimirFila("2. Profundidad (DFS)", buscador.profundidad(inicial, meta));
        imprimirFila("3. Costo Uniforme (UCS)", buscador.costoUniforme(inicial, meta));
        imprimirFila("4a. Prof. Limitada (L=6)", buscador.profundidadLimitada(inicial, meta, 6));
        imprimirFila("4b. Prof. Iterativa (IDDFS)", buscador.profundizacionIterativa(inicial, meta, 31));
        imprimirFila("5. Bidireccional", buscador.bidireccional(inicial, meta));

        System.out.println("===================================================================================");

        // Mostrar ruta detallada del resultado óptimo (ej. Costo Uniforme / Anchura)
        EstrategiasBusqueda.Metricas optima = buscador.anchura(inicial, meta);
        if (optima.metaAlcanzada != null) {
            mostrarSecuencia(optima.metaAlcanzada);
        }
    }

    private static void imprimirFila(String nombre, EstrategiasBusqueda.Metricas m) {
        if (m.metaAlcanzada != null) {
            System.out.printf("%-26s | %-10d | %-10d | %-12d | %-12d | %d ms%n",
                    nombre,
                    m.metaAlcanzada.getProfundidad(),
                    m.metaAlcanzada.getCostoPonderado(),
                    m.nodosExpandidos,
                    m.maxFrontera,
                    m.tiempoMs);
        } else {
            System.out.printf("%-26s | %-10s | %-10s | %-12d | %-12d | %d ms%n",
                    nombre, "SIN SOL.", "-", m.nodosExpandidos, m.maxFrontera, m.tiempoMs);
        }
    }

    private static void mostrarSecuencia(EstadoTablero finalNodo) {
        LinkedList<EstadoTablero> camino = new LinkedList<>();
        for (EstadoTablero aux = finalNodo; aux != null; aux = aux.getPredecesor()) {
            camino.addFirst(aux);
        }

        System.out.println("\n--- SECUENCIA DE MOVIMIENTOS (SOLUCIÓN ÓPTIMA) ---");
        int paso = 0;
        for (EstadoTablero estado : camino) {
            System.out.println("Paso " + paso++ + " [" + estado.getAccion() + "] (Costo acum: " + estado.getCostoPonderado() + "):");
            estado.mostrarCuadricula();
            System.out.println("-------");
        }
    }
}