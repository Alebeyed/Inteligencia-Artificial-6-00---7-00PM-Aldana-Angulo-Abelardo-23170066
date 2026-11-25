package Unidad_1.Puzzle8BNI;

import java.util.*;

public class EstrategiasBusqueda {

    public static class Metricas {
        public final EstadoTablero metaAlcanzada;
        public final long nodosExpandidos;
        public final int maxFrontera;
        public final long tiempoMs;

        public Metricas(EstadoTablero metaAlcanzada, long nodosExpandidos, int maxFrontera, long tiempoMs) {
            this.metaAlcanzada = metaAlcanzada;
            this.nodosExpandidos = nodosExpandidos;
            this.maxFrontera = maxFrontera;
            this.tiempoMs = tiempoMs;
        }
    }

    private long expandidos;
    private int picoFrontera;
    private boolean huboCorte;

    private void resetearContadores() {
        expandidos = 0;
        picoFrontera = 0;
        huboCorte = false;
    }

    // 1. BÚSQUEDA EN ANCHURA (BFS - Cola FIFO)
    public Metricas anchura(int[] inicio, int[] objetivo) {
        resetearContadores();
        long t0 = System.currentTimeMillis();

        EstadoTablero raiz = new EstadoTablero(inicio);
        Queue<EstadoTablero> cola = new ArrayDeque<>();
        Set<String> visitados = new HashSet<>();

        cola.add(raiz);
        visitados.add(raiz.clave());

        while (!cola.isEmpty()) {
            picoFrontera = Math.max(picoFrontera, cola.size());
            EstadoTablero actual = cola.poll();

            if (actual.esIgualA(objetivo)) {
                return new Metricas(actual, expandidos, picoFrontera, System.currentTimeMillis() - t0);
            }

            expandidos++;
            for (EstadoTablero vecino : actual.obtenerVecinos()) {
                if (visitados.add(vecino.clave())) {
                    cola.add(vecino);
                }
            }
        }
        return new Metricas(null, expandidos, picoFrontera, System.currentTimeMillis() - t0);
    }

    // 2. BÚSQUEDA EN PROFUNDIDAD (DFS - Pila LIFO)
    public Metricas profundidad(int[] inicio, int[] objetivo) {
        resetearContadores();
        long t0 = System.currentTimeMillis();

        EstadoTablero raiz = new EstadoTablero(inicio);
        Deque<EstadoTablero> pila = new ArrayDeque<>();
        Set<String> visitados = new HashSet<>();

        pila.push(raiz);
        visitados.add(raiz.clave());

        while (!pila.isEmpty()) {
            picoFrontera = Math.max(picoFrontera, pila.size());
            EstadoTablero actual = pila.pop();

            if (actual.esIgualA(objetivo)) {
                return new Metricas(actual, expandidos, picoFrontera, System.currentTimeMillis() - t0);
            }

            expandidos++;
            List<EstadoTablero> vecinos = actual.obtenerVecinos();
            for (int i = vecinos.size() - 1; i >= 0; i--) {
                EstadoTablero v = vecinos.get(i);
                if (visitados.add(v.clave())) {
                    pila.push(v);
                }
            }
        }
        return new Metricas(null, expandidos, picoFrontera, System.currentTimeMillis() - t0);
    }

    // 3. COSTO UNIFORME (UCS - PriorityQueue por costo acumulado g(n))
    public Metricas costoUniforme(int[] inicio, int[] objetivo) {
        resetearContadores();
        long t0 = System.currentTimeMillis();

        EstadoTablero raiz = new EstadoTablero(inicio);
        PriorityQueue<EstadoTablero> frontera = new PriorityQueue<>();
        Map<String, Integer> costoMinimo = new HashMap<>();

        frontera.add(raiz);
        costoMinimo.put(raiz.clave(), 0);

        while (!frontera.isEmpty()) {
            picoFrontera = Math.max(picoFrontera, frontera.size());
            EstadoTablero actual = frontera.poll();

            if (actual.getCostoPonderado() > costoMinimo.getOrDefault(actual.clave(), Integer.MAX_VALUE)) {
                continue;
            }

            if (actual.esIgualA(objetivo)) {
                return new Metricas(actual, expandidos, picoFrontera, System.currentTimeMillis() - t0);
            }

            expandidos++;
            for (EstadoTablero vecino : actual.obtenerVecinos()) {
                String k = vecino.clave();
                if (vecino.getCostoPonderado() < costoMinimo.getOrDefault(k, Integer.MAX_VALUE)) {
                    costoMinimo.put(k, vecino.getCostoPonderado());
                    frontera.add(vecino);
                }
            }
        }
        return new Metricas(null, expandidos, picoFrontera, System.currentTimeMillis() - t0);
    }

    // 4a. PROFUNDIDAD LIMITADA (DLS)
    public Metricas profundidadLimitada(int[] inicio, int[] objetivo, int limite) {
        resetearContadores();
        long t0 = System.currentTimeMillis();
        EstadoTablero res = dls(new EstadoTablero(inicio), objetivo, limite, new HashSet<>());
        return new Metricas(res, expandidos, picoFrontera, System.currentTimeMillis() - t0);
    }

    private EstadoTablero dls(EstadoTablero nodo, int[] objetivo, int limite, Set<String> ramaActiva) {
        if (nodo.esIgualA(objetivo)) return nodo;

        if (nodo.getProfundidad() >= limite) {
            huboCorte = true;
            return null;
        }

        expandidos++;
        ramaActiva.add(nodo.clave());
        picoFrontera = Math.max(picoFrontera, ramaActiva.size());

        for (EstadoTablero vecino : nodo.obtenerVecinos()) {
            if (!ramaActiva.contains(vecino.clave())) {
                EstadoTablero hallado = dls(vecino, objetivo, limite, ramaActiva);
                if (hallado != null) return hallado;
            }
        }

        ramaActiva.remove(nodo.clave());
        return null;
    }

    // 4b. PROFUNDIZACIÓN ITERATIVA (IDDFS - Combina DLS incrementalmente)
    public Metricas profundizacionIterativa(int[] inicio, int[] objetivo, int topeMaximo) {
        resetearContadores();
        long t0 = System.currentTimeMillis();
        EstadoTablero raiz = new EstadoTablero(inicio);

        for (int limite = 0; limite <= topeMaximo; limite++) {
            huboCorte = false;
            EstadoTablero res = dls(raiz, objetivo, limite, new HashSet<>());
            if (res != null) {
                return new Metricas(res, expandidos, picoFrontera, System.currentTimeMillis() - t0);
            }
            if (!huboCorte) break;
        }
        return new Metricas(null, expandidos, picoFrontera, System.currentTimeMillis() - t0);
    }

    // 5. BÚSQUEDA BIDIRECCIONAL (Dos BFS simultáneos: Inicio -> Meta y Meta -> Inicio)
    public Metricas bidireccional(int[] inicio, int[] objetivo) {
        resetearContadores();
        long t0 = System.currentTimeMillis();

        EstadoTablero nodoInicio = new EstadoTablero(inicio);
        EstadoTablero nodoMeta = new EstadoTablero(objetivo);

        if (nodoInicio.esIgualA(objetivo)) {
            return new Metricas(nodoInicio, 0, 1, 0);
        }

        Queue<EstadoTablero> colaAdelante = new ArrayDeque<>();
        Queue<EstadoTablero> colaAtras = new ArrayDeque<>();
        Map<String, EstadoTablero> vistosAdelante = new HashMap<>();
        Map<String, EstadoTablero> vistosAtras = new HashMap<>();

        colaAdelante.add(nodoInicio);
        vistosAdelante.put(nodoInicio.clave(), nodoInicio);

        colaAtras.add(nodoMeta);
        vistosAtras.put(nodoMeta.clave(), nodoMeta);

        while (!colaAdelante.isEmpty() && !colaAtras.isEmpty()) {
            picoFrontera = Math.max(picoFrontera, colaAdelante.size() + colaAtras.size());

            EstadoTablero[] cruce = avanzarNivel(colaAdelante, vistosAdelante, vistosAtras);
            if (cruce != null) {
                return new Metricas(reconstruirPuente(cruce[0], cruce[1]), expandidos, picoFrontera, System.currentTimeMillis() - t0);
            }

            cruce = avanzarNivel(colaAtras, vistosAtras, vistosAdelante);
            if (cruce != null) {
                return new Metricas(reconstruirPuente(cruce[1], cruce[0]), expandidos, picoFrontera, System.currentTimeMillis() - t0);
            }
        }
        return new Metricas(null, expandidos, picoFrontera, System.currentTimeMillis() - t0);
    }

    private EstadoTablero[] avanzarNivel(Queue<EstadoTablero> cola, Map<String, EstadoTablero> propios, Map<String, EstadoTablero> opuestos) {
        int nivelSize = cola.size();
        EstadoTablero[] mejorCruce = null;
        int menorDistancia = Integer.MAX_VALUE;

        for (int i = 0; i < nivelSize; i++) {
            EstadoTablero actual = cola.poll();
            expandidos++;

            for (EstadoTablero vecino : actual.obtenerVecinos()) {
                String k = vecino.clave();
                if (propios.containsKey(k)) continue;

                propios.put(k, vecino);
                cola.add(vecino);

                EstadoTablero contraparte = opuestos.get(k);
                if (contraparte != null) {
                    int distTotal = vecino.getProfundidad() + contraparte.getProfundidad();
                    if (distTotal < menorDistancia) {
                        menorDistancia = distTotal;
                        mejorCruce = new EstadoTablero[]{vecino, contraparte};
                    }
                }
            }
        }
        return mejorCruce;
    }

    private EstadoTablero reconstruirPuente(EstadoTablero desdeInicio, EstadoTablero desdeMeta) {
        EstadoTablero cursor = desdeInicio;
        for (EstadoTablero p = desdeMeta.getPredecesor(); p != null; p = p.getPredecesor()) {
            cursor = p.encadenarDesde(cursor, "REVERSO");
        }
        return cursor;
    }
}
