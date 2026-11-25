package Unidad_1.ABB;

public class PruebaABB {
    public static void main(String[] args) {
        ArbolBinarioBusqueda abb = new ArbolBinarioBusqueda();

        int[] valores = {50, 30, 70, 20, 40, 60, 80};
        System.out.println("Insertando valores: 50, 30, 70, 20, 40, 60, 80");
        for (int v : valores) {
            abb.insertar(v);
        }

        abb.imprimirArbol();

        // Mostrar recorridos
        abb.recorrerInorden();
        abb.recorrerPreorden();
        abb.recorrerPostorden();

        // Pruebas de búsqueda
        System.out.println("\n¿Existe el 40 en el árbol?: " + abb.buscar(40));
        System.out.println("¿Existe el 95 en el árbol?: " + abb.buscar(95));

        // Prueba de eliminación
        System.out.println("\nEliminando el nodo 30 (nodo con dos hijos)...");
        abb.eliminar(30);
        abb.imprimirArbol();
        abb.recorrerInorden();
    }
}