import java.util.*;

public class MotorBusquedas {

    public static class ResultadoBusqueda {
        public final EstadoPuzzle solucion;
        public final int nodosExpandidos;
        public final long tiempoMs;

        public ResultadoBusqueda(EstadoPuzzle solucion, int nodosExpandidos, long tiempoMs) {
            this.solucion = solucion;
            this.nodosExpandidos = nodosExpandidos;
            this.tiempoMs = tiempoMs;
        }
    }

    // 1. Búsqueda Primero en Anchura (BFS)
    public ResultadoBusqueda buscarEnAnchura(int[] inicial, int[] meta) {
        long inicio = System.currentTimeMillis();
        Queue<EstadoPuzzle> frontera = new LinkedList<>();
        Set<String> explorados = new HashSet<>();
        int expandidos = 0;

        EstadoPuzzle raiz = new EstadoPuzzle(inicial, null, "INICIO", 0);
        frontera.offer(raiz);
        explorados.add(raiz.obtenerFirma());

        while (!frontera.isEmpty()) {
            EstadoPuzzle actual = frontera.poll();
            expandidos++;

            if (actual.esMeta(meta)) {
                return new ResultadoBusqueda(actual, expandidos, System.currentTimeMillis() - inicio);
            }

            for (EstadoPuzzle hijo : actual.generarSucesores()) {
                if (explorados.add(hijo.obtenerFirma())) {
                    frontera.offer(hijo);
                }
            }
        }
        return new ResultadoBusqueda(null, expandidos, System.currentTimeMillis() - inicio);
    }

    // 2. Búsqueda Primero en Profundidad (DFS con límite de seguridad para evitar bucles infinitos)
    public ResultadoBusqueda buscarEnProfundidad(int[] inicial, int[] meta, int limiteMax) {
        long inicio = System.currentTimeMillis();
        Deque<EstadoPuzzle> pila = new ArrayDeque<>();
        Set<String> explorados = new HashSet<>();
        int expandidos = 0;

        EstadoPuzzle raiz = new EstadoPuzzle(inicial, null, "INICIO", 0);
        pila.push(raiz);

        while (!pila.isEmpty()) {
            EstadoPuzzle actual = pila.pop();
            
            if (!explorados.add(actual.obtenerFirma())) continue;
            expandidos++;

            if (actual.esMeta(meta)) {
                return new ResultadoBusqueda(actual, expandidos, System.currentTimeMillis() - inicio);
            }

            if (actual.getNivel() < limiteMax) {
                List<EstadoPuzzle> hijos = actual.generarSucesores();
                for (int i = hijos.size() - 1; i >= 0; i--) {
                    if (!explorados.contains(hijos.get(i).obtenerFirma())) {
                        pila.push(hijos.get(i));
                    }
                }
            }
        }
        return new ResultadoBusqueda(null, expandidos, System.currentTimeMillis() - inicio);
    }

    // 3. Búsqueda de Costo Uniforme (UCS real con PriorityQueue)
    public ResultadoBusqueda buscarCostoUniforme(int[] inicial, int[] meta) {
        long inicio = System.currentTimeMillis();
        PriorityQueue<EstadoPuzzle> frontera = new PriorityQueue<>();
        Map<String, Integer> mejorCosto = new HashMap<>();
        int expandidos = 0;

        EstadoPuzzle raiz = new EstadoPuzzle(inicial, null, "INICIO", 0);
        frontera.offer(raiz);
        mejorCosto.put(raiz.obtenerFirma(), 0);

        while (!frontera.isEmpty()) {
            EstadoPuzzle actual = frontera.poll();
            expandidos++;

            if (actual.esMeta(meta)) {
                return new ResultadoBusqueda(actual, expandidos, System.currentTimeMillis() - inicio);
            }

            if (actual.getCostoAcumulado() > mejorCosto.getOrDefault(actual.obtenerFirma(), Integer.MAX_VALUE)) {
                continue;
            }

            for (EstadoPuzzle hijo : actual.generarSucesores()) {
                String firma = hijo.obtenerFirma();
                if (hijo.getCostoAcumulado() < mejorCosto.getOrDefault(firma, Integer.MAX_VALUE)) {
                    mejorCosto.put(firma, hijo.getCostoAcumulado());
                    frontera.offer(hijo);
                }
            }
        }
        return new ResultadoBusqueda(null, expandidos, System.currentTimeMillis() - inicio);
    }

    // 4. Búsqueda en Profundidad Iterativa (IDDFS - combina Profundidad Limitada en ciclo)
    public ResultadoBusqueda buscarProfundidadIterativa(int[] inicial, int[] meta, int profundidadTope) {
        long inicio = System.currentTimeMillis();
        int totalExpandidos = 0;

        for (int limite = 0; limite <= profundidadTope; limite++) {
            Set<String> caminoActual = new HashSet<>();
            int[] contadorIteracion = {0};
            EstadoPuzzle raiz = new EstadoPuzzle(inicial, null, "INICIO", 0);

            EstadoPuzzle resultado = dlsRecursivo(raiz, meta, limite, caminoActual, contadorIteracion);
            totalExpandidos += contadorIteracion[0];

            if (resultado != null) {
                return new ResultadoBusqueda(resultado, totalExpandidos, System.currentTimeMillis() - inicio);
            }
        }
        return new ResultadoBusqueda(null, totalExpandidos, System.currentTimeMillis() - inicio);
    }

    // Búsqueda Limitada en Profundidad (DLS) auxiliar para IDDFS
    private EstadoPuzzle dlsRecursivo(EstadoPuzzle nodo, int[] meta, int limite, Set<String> visitadosRama, int[] contador) {
        contador[0]++;
        if (nodo.esMeta(meta)) return nodo;
        if (limite <= 0) return null;

        visitadosRama.add(nodo.obtenerFirma());
        for (EstadoPuzzle hijo : nodo.generarSucesores()) {
            if (!visitadosRama.contains(hijo.obtenerFirma())) {
                EstadoPuzzle encontrado = dlsRecursivo(hijo, meta, limite - 1, visitadosRama, contador);
                if (encontrado != null) return encontrado;
            }
        }
        visitadosRama.remove(nodo.obtenerFirma());
        return null;
    }
}