import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EstadoPuzzle implements Comparable<EstadoPuzzle> {
    private final int[] fichas; // 0 representa el espacio vacío
    private final int posCero;
    private final EstadoPuzzle padre;
    private final String movimiento;
    private final int nivel;
    private final int costoAcumulado;

    public EstadoPuzzle(int[] fichas, EstadoPuzzle padre, String movimiento, int costoPaso) {
        this.fichas = Arrays.copyOf(fichas, fichas.length);
        this.padre = padre;
        this.movimiento = movimiento;
        this.nivel = (padre == null) ? 0 : padre.nivel + 1;
        this.costoAcumulado = (padre == null) ? 0 : padre.costoAcumulado + costoPaso;
        this.posCero = localizarCero();
    }

    private int localizarCero() {
        for (int i = 0; i < fichas.length; i++) {
            if (fichas[i] == 0) return i;
        }
        return -1;
    }

    public List<EstadoPuzzle> generarSucesores() {
        List<EstadoPuzzle> sucesores = new ArrayList<>();
        int fila = posCero / 3;
        int col = posCero % 3;

        // Movimientos: Arriba, Abajo, Izquierda, Derecha
        int[][] desplazamientos = {
            {-1, 0, 1}, // Arriba (costo 1)
            {1, 0, 1},  // Abajo (costo 1)
            {0, -1, 1}, // Izquierda (costo 1)
            {0, 1, 1}   // Derecha (costo 1)
        };
        String[] nombres = {"ARRIBA", "ABAJO", "IZQUIERDA", "DERECHA"};

        for (int i = 0; i < desplazamientos.length; i++) {
            int nuevaFila = fila + desplazamientos[i][0];
            int nuevaCol = col + desplazamientos[i][1];

            if (nuevaFila >= 0 && nuevaFila < 3 && nuevaCol >= 0 && nuevaCol < 3) {
                int nuevaPos = nuevaFila * 3 + nuevaCol;
                int[] nuevoTablero = Arrays.copyOf(fichas, fichas.length);
                // Intercambiar el 0 con la ficha destino
                nuevoTablero[posCero] = nuevoTablero[nuevaPos];
                nuevoTablero[nuevaPos] = 0;

                sucesores.add(new EstadoPuzzle(nuevoTablero, this, nombres[i], desplazamientos[i][2]));
            }
        }
        return sucesores;
    }

    public boolean esMeta(int[] objetivo) {
        return Arrays.equals(this.fichas, objetivo);
    }

    public String obtenerFirma() {
        return Arrays.toString(fichas);
    }

    public EstadoPuzzle getPadre() { return padre; }
    public String getMovimiento() { return movimiento; }
    public int getNivel() { return nivel; }
    public int getCostoAcumulado() { return costoAcumulado; }

    public void imprimirTablero() {
        for (int i = 0; i < 9; i++) {
            System.out.print((fichas[i] == 0 ? "_" : fichas[i]) + " ");
            if ((i + 1) % 3 == 0) System.out.println();
        }
    }

    @Override
    public int compareTo(EstadoPuzzle otro) {
        return Integer.compare(this.costoAcumulado, otro.costoAcumulado);
    }
}