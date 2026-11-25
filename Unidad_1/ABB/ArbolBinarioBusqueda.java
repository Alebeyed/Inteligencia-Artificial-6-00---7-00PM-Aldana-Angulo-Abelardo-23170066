package Unidad_1.ABB;

public class ArbolBinarioBusqueda {
    private NodoABB raiz;

    public ArbolBinarioBusqueda() {
        this.raiz = null;
    }

    // Insertar un valor manteniendo la propiedad del ABB (menores a la izquierda, mayores a la derecha)
    public void insertar(int dato) {
        raiz = insertarRecursivo(raiz, dato);
    }

    private NodoABB insertarRecursivo(NodoABB actual, int dato) {
        if (actual == null) {
            return new NodoABB(dato);
        }
        if (dato < actual.dato) {
            actual.izquierdo = insertarRecursivo(actual.izquierdo, dato);
        } else if (dato > actual.dato) {
            actual.derecho = insertarRecursivo(actual.derecho, dato);
        }
        return actual;
    }

    // Buscar un valor en el árbol
    public boolean buscar(int dato) {
        return buscarRecursivo(raiz, dato);
    }

    private boolean buscarRecursivo(NodoABB actual, int dato) {
        if (actual == null) {
            return false;
        }
        if (dato == actual.dato) {
            return true;
        }
        return dato < actual.dato
                ? buscarRecursivo(actual.izquierdo, dato)
                : buscarRecursivo(actual.derecho, dato);
    }

    // Eliminar un nodo del árbol
    public void eliminar(int dato) {
        raiz = eliminarRecursivo(raiz, dato);
    }

    private NodoABB eliminarRecursivo(NodoABB actual, int dato) {
        if (actual == null) return null;

        if (dato < actual.dato) {
            actual.izquierdo = eliminarRecursivo(actual.izquierdo, dato);
        } else if (dato > actual.dato) {
            actual.derecho = eliminarRecursivo(actual.derecho, dato);
        } else {
            // Caso 1 y 2: Nodo sin hijos o con un solo hijo
            if (actual.izquierdo == null) return actual.derecho;
            if (actual.derecho == null) return actual.izquierdo;

            // Caso 3: Nodo con dos hijos (se reemplaza con el sucesor inorden)
            actual.dato = encontrarMinimo(actual.derecho);
            actual.derecho = eliminarRecursivo(actual.derecho, actual.dato);
        }
        return actual;
    }

    private int encontrarMinimo(NodoABB nodo) {
        int minimo = nodo.dato;
        while (nodo.izquierdo != null) {
            minimo = nodo.izquierdo.dato;
            nodo = nodo.izquierdo;
        }
        return minimo;
    }

    // Recorridos del árbol
    public void recorrerInorden() {
        System.out.print("Inorden (Izq - Raíz - Der):   ");
        inordenRec(raiz);
        System.out.println();
    }

    private void inordenRec(NodoABB nodo) {
        if (nodo != null) {
            inordenRec(nodo.izquierdo);
            System.out.print(nodo.dato + " ");
            inordenRec(nodo.derecho);
        }
    }

    public void recorrerPreorden() {
        System.out.print("Preorden (Raíz - Izq - Der):  ");
        preordenRec(raiz);
        System.out.println();
    }

    private void preordenRec(NodoABB nodo) {
        if (nodo != null) {
            System.out.print(nodo.dato + " ");
            preordenRec(nodo.izquierdo);
            preordenRec(nodo.derecho);
        }
    }

    public void recorrerPostorden() {
        System.out.print("Postorden (Izq - Der - Raíz): ");
        postordenRec(raiz);
        System.out.println();
    }

    private void postordenRec(NodoABB nodo) {
        if (nodo != null) {
            postordenRec(nodo.izquierdo);
            postordenRec(nodo.derecho);
            System.out.print(nodo.dato + " ");
        }
    }

    // Imprimir estructura visual horizontal en consola
    public void imprimirArbol() {
        System.out.println("\nEstructura del Árbol Binario de Búsqueda:");
        imprimirRec(raiz, 0);
        System.out.println();
    }

    private void imprimirRec(NodoABB nodo, int nivel) {
        if (nodo == null) return;
        imprimirRec(nodo.derecho, nivel + 1);
        for (int i = 0; i < nivel; i++) {
            System.out.print("    ");
        }
        System.out.println("└── " + nodo.dato);
        imprimirRec(nodo.izquierdo, nivel + 1);
    }
}