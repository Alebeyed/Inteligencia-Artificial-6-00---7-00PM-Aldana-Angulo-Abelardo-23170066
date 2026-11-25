package Unidad_1.Puzzle8BNI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EstadoTablero implements Comparable<EstadoTablero> {
    private final int[] celdas;
    private final int indiceHueco;
    private final EstadoTablero predecesor;
    private final String accion;
    private final int profundidad;
    private final int costoPonderado;

    public EstadoTablero(int[] celdas) {
        this.celdas = Arrays.copyOf(celdas, celdas.length);
        this.predecesor = null;
        this.accion = "INICIO";
        this.profundidad = 0;
        this.costoPonderado = 0;
        this.indiceHueco = buscarHueco();
    }

    private EstadoTablero(int[] celdas, EstadoTablero predecesor, String accion, int fichaMovida) {
        this.celdas = celdas;
        this.predecesor = predecesor;
        this.accion = accion;
        this.profundidad = predecesor.profundidad + 1;
        this.costoPonderado = predecesor.costoPonderado + fichaMovida;
        this.indiceHueco = buscarHueco();
    }

    private int buscarHueco() {
        for (int i = 0; i < celdas.length; i++) {
            if (celdas[i] == 0) return i;
        }
        return -1;
    }

    public List<EstadoTablero> obtenerVecinos() {
        List<EstadoTablero> vecinos = new ArrayList<>();
        int fila = indiceHueco / 3;
        int col = indiceHueco % 3;

        int[][] direcciones = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        String[] etiquetas = {"ARRIBA", "ABAJO", "IZQUIERDA", "DERECHA"};

        for (int i = 0; i < direcciones.length; i++) {
            int nFila = fila + direcciones[i][0];
            int nCol = col + direcciones[i][1];

            if (nFila >= 0 && nFila < 3 && nCol >= 0 && nCol < 3) {
                int destino = nFila * 3 + nCol;
                int fichaDesplazada = celdas[destino];

                int[] copia = Arrays.copyOf(celdas, celdas.length);
                copia[indiceHueco] = fichaDesplazada;
                copia[destino] = 0;

                vecinos.add(new EstadoTablero(copia, this, etiquetas[i], fichaDesplazada));
            }
        }
        return vecinos;
    }

    // Permite construir un nodo encadenado (útil para unir mitades en Búsqueda Bidireccional)
    public EstadoTablero encadenarDesde(EstadoTablero nuevoPadre, String direccionInversa) {
        int fichaMovida = 0;
        for (int i = 0; i < 9; i++) {
            if (this.celdas[i] != nuevoPadre.celdas[i] && this.celdas[i] != 0) {
                fichaMovida = this.celdas[i];
                break;
            }
        }
        return new EstadoTablero(Arrays.copyOf(this.celdas, 9), nuevoPadre, direccionInversa, fichaMovida);
    }

    public boolean esIgualA(int[] objetivo) {
        return Arrays.equals(this.celdas, objetivo);
    }

    public String clave() {
        return Arrays.toString(celdas);
    }

    public int[] getCeldas() { return celdas; }
    public EstadoTablero getPredecesor() { return predecesor; }
    public String getAccion() { return accion; }
    public int getProfundidad() { return profundidad; }
    public int getCostoPonderado() { return costoPonderado; }

    public void mostrarCuadricula() {
        for (int i = 0; i < 9; i++) {
            System.out.print((celdas[i] == 0 ? " " : celdas[i]) + " ");
            if ((i + 1) % 3 == 0) System.out.println();
        }
    }

    @Override
    public int compareTo(EstadoTablero otro) {
        return Integer.compare(this.costoPonderado, otro.costoPonderado);
    }
}