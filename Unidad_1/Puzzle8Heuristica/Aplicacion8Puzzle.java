import java.util.LinkedList;

public class Aplicacion8Puzzle {

    public static void main(String[] args) {
        // 0 representa el hueco vacío
        int[] tableroInicial = {
            1, 2, 3,
            8, 0, 4,
            7, 6, 5
        };

        int[] tableroMeta = {
            1, 2, 8,
            4, 3, 7,
            6, 0, 5
        };

        MotorBusquedas motor = new MotorBusquedas();

        System.out.println("=== COMPARATIVA DE BUSQUEDAS NO INFORMADAS (8-PUZZLE) ===\n");

        ejecutarYMostrar("1. Primero en Anchura (BFS)", motor.buscarEnAnchura(tableroInicial, tableroMeta));
        ejecutarYMostrar("2. Primero en Profundidad (DFS Lim=25)", motor.buscarEnProfundidad(tableroInicial, tableroMeta, 25));
        ejecutarYMostrar("3. Costo Uniforme (UCS)", motor.buscarCostoUniforme(tableroInicial, tableroMeta));
        ejecutarYMostrar("4. Profundidad Iterativa (IDDFS)", motor.buscarProfundidadIterativa(tableroInicial, tableroMeta, 25));
    }

    private static void ejecutarYMostrar(String nombreAlgoritmo, MotorBusquedas.ResultadoBusqueda res) {
        System.out.println("Algoritmo: " + nombreAlgoritmo);
        if (res.solucion != null) {
            System.out.println("  -> Estado: SOLUCION ENCONTRADA");
            System.out.println("  -> Profundidad (Pasos): " + res.solucion.getNivel());
            System.out.println("  -> Costo Total: " + res.solucion.getCostoAcumulado());
            System.out.println("  -> Nodos Expandidos: " + res.nodosExpandidos);
            System.out.println("  -> Tiempo: " + res.tiempoMs + " ms");
            
            // Descomenta la siguiente linea si quieres imprimir el paso a paso del tablero:
            // imprimirCamino(res.solucion);
        } else {
            System.out.println("  -> Estado: NO ENCONTRADA (Nodos expandidos: " + res.nodosExpandidos + ")");
        }
        System.out.println("--------------------------------------------------");
    }

    private static void imprimirCamino(EstadoPuzzle meta) {
        LinkedList<EstadoPuzzle> pasos = new LinkedList<>();
        for (EstadoPuzzle actual = meta; actual != null; actual = actual.getPadre()) {
            pasos.addFirst(actual);
        }
        for (EstadoPuzzle paso : pasos) {
            System.out.println("Movimiento: " + paso.getMovimiento());
            paso.imprimirTablero();
            System.out.println();
        }
    }
}