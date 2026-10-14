/**
 * Ejercicio 02 - Comparacion entre Bubble Sort y Selection Sort.
 *
 * <p>Implementa {@code bubbleSort(int[])} y {@code selectionSort(int[])}. Ambos ordenan
 * una COPIA del mismo arreglo original para que la medicion sea justa, y cada uno cuenta
 * sus comparaciones e intercambios. La salida termina en una tabla comparativa.</p>
 *
 * <p><b>Diferencia conceptual.</b> Bubble Sort compara VECINOS: cada vez que encuentra un
 * par desordenado intercambia en el acto, por lo que puede mover el mismo elemento muchas
 * veces. Selection Sort primero BUSCA el minimo de todo el tramo no ordenado recordando
 * solo su indice, y recien al terminar la busqueda hace un unico intercambio por pasada.
 * Consecuencia: ambos comparan del orden de n^2/2 veces, pero Burbuja puede hacer hasta
 * n(n-1)/2 intercambios mientras Seleccion hace a lo sumo n-1. Eso importa cuando mover
 * un elemento es caro (objetos grandes, registros en disco).</p>
 *
 * <p><b>Definicion de los contadores</b> (identica para los dos algoritmos, para que la
 * tabla sea comparable):</p>
 * <ul>
 *   <li><i>comparacion</i>: cada evaluacion de una condicion que confronta dos elementos
 *       del arreglo (arreglo[j] &gt; arreglo[j+1] en burbuja, arreglo[j] &lt; arreglo[indiceMinimo]
 *       en seleccion). La comparacion de indices no cuenta.</li>
 *   <li><i>intercambio</i>: cada permuta efectiva de dos posiciones. En seleccion no se
 *       cuenta el intercambio cuando el minimo ya esta en su lugar (se evita).</li>
 * </ul>
 *
 * <p>Tabla de referencia:</p>
 * <pre>
 * Algoritmo       Mejor     Promedio   Peor      Espacial  Estable  Intercambios (peor)
 * Bubble Sort     O(n)      O(n^2)     O(n^2)    O(1)      si       n(n-1)/2
 * Selection Sort  O(n^2)    O(n^2)     O(n^2)    O(1)      no       n-1
 * </pre>
 *
 * <p>Complejidad: O(n^2) temporal en el caso promedio para ambos; O(1) espacial adicional.</p>
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio02_BurbujaVsSeleccion {

    /** Acumulador de la ultima corrida de bubbleSort. */
    private static final MetricasComparativa METRICAS_BURBUJA = new MetricasComparativa("Bubble Sort");

    /** Acumulador de la ultima corrida de selectionSort. */
    private static final MetricasComparativa METRICAS_SELECCION = new MetricasComparativa("Selection Sort");

    /**
     * Metodo principal. Corre los dos algoritmos sobre tres escenarios (desordenado,
     * ya ordenado e invertido) y sobre los casos limite.
     *
     * @param args argumentos de la linea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== EJERCICIO 02 - BURBUJA VS SELECCION ===");

        compararEscenario("Desordenado", new int[] {5, 2, 9, 1, 7});
        compararEscenario("Ya ordenado", new int[] {1, 2, 3, 4, 5});
        compararEscenario("Ordenado al reves", new int[] {5, 4, 3, 2, 1});

        System.out.println("\n=== CASOS LIMITE ===");
        compararEscenario("Arreglo vacio", new int[] {});
        compararEscenario("Un solo elemento", new int[] {7});
        compararEscenario("Con repetidos", new int[] {3, 1, 3, 1, 2});

        System.out.println("\n--- Arreglo nulo (debe lanzar excepcion) ---");
        try {
            bubbleSort(null);
            System.out.println("ERROR: no se lanzo la excepcion esperada.");
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException capturada en bubbleSort: " + e.getMessage());
        }
        try {
            selectionSort(null);
            System.out.println("ERROR: no se lanzo la excepcion esperada.");
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException capturada en selectionSort: " + e.getMessage());
        }

        System.out.println("\n=== CONTRASTE CON LA TEORIA (n = 5) ===");
        int n = 5;
        System.out.println("n(n-1)/2 = " + (n * (n - 1) / 2)
                + "  -> cota de comparaciones de seleccion y de intercambios de burbuja en el peor caso.");
        System.out.println("n-1 = " + (n - 1) + "  -> cota de intercambios de seleccion en cualquier caso.");
    }

    /**
     * Ejecuta los dos algoritmos sobre copias del mismo arreglo e imprime la tabla comparativa.
     *
     * @param titulo rotulo del escenario
     * @param original arreglo de entrada; no se modifica
     * @throws IllegalArgumentException si {@code original} es nulo
     */
    public static void compararEscenario(String titulo, int[] original) {
        if (original == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        System.out.println("\n--- Escenario: " + titulo + " ---");
        System.out.println("Arreglo original: " + aTexto(original));

        // Cada algoritmo recibe su propia copia: si compartieran el arreglo, el segundo
        // recibiria datos ya ordenados y la medicion no seria comparable.
        int[] paraBurbuja = copiar(original);
        int[] paraSeleccion = copiar(original);

        bubbleSort(paraBurbuja);
        selectionSort(paraSeleccion);

        System.out.println("Ordenado por Bubble Sort:    " + aTexto(paraBurbuja)
                + " | estaOrdenado=" + estaOrdenado(paraBurbuja));
        System.out.println("Ordenado por Selection Sort: " + aTexto(paraSeleccion)
                + " | estaOrdenado=" + estaOrdenado(paraSeleccion));
        System.out.println("Ambos resultados coinciden: " + sonIguales(paraBurbuja, paraSeleccion));

        System.out.println("+----------------+---------------+---------------+");
        System.out.println("| Algoritmo      | Comparaciones | Intercambios  |");
        System.out.println("+----------------+---------------+---------------+");
        System.out.printf("| %-14s | %13d | %13d |%n",
                METRICAS_BURBUJA.nombre, METRICAS_BURBUJA.comparaciones, METRICAS_BURBUJA.intercambios);
        System.out.printf("| %-14s | %13d | %13d |%n",
                METRICAS_SELECCION.nombre, METRICAS_SELECCION.comparaciones, METRICAS_SELECCION.intercambios);
        System.out.println("+----------------+---------------+---------------+");
    }

    /**
     * Ordena el arreglo en forma ascendente comparando e intercambiando elementos vecinos.
     *
     * @param array arreglo a ordenar in situ
     * @throws IllegalArgumentException si {@code array} es nulo
     */
    public static void bubbleSort(int[] array) {
        if (array == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        METRICAS_BURBUJA.reiniciar();

        int n = array.length;
        for (int pasada = 1; pasada < n; pasada++) {
            boolean huboIntercambio = false;
            for (int j = 0; j < n - pasada; j++) {
                METRICAS_BURBUJA.comparaciones++;
                if (array[j] > array[j + 1]) {
                    // ESTA es la linea del intercambio en burbuja: ocurre DENTRO del ciclo
                    // interno, por eso puede repetirse muchas veces en una sola pasada.
                    intercambiar(array, j, j + 1);
                    METRICAS_BURBUJA.intercambios++;
                    huboIntercambio = true;
                }
            }
            METRICAS_BURBUJA.pasadas++;
            if (!huboIntercambio) {
                // Con entrada ya ordenada burbuja corta en la primera pasada: por eso su
                // mejor caso es O(n) y le gana a seleccion en ese escenario.
                break;
            }
        }
    }

    /**
     * Ordena el arreglo en forma ascendente seleccionando en cada pasada el minimo del
     * tramo no ordenado y llevandolo al frente con un unico intercambio.
     *
     * @param array arreglo a ordenar in situ
     * @throws IllegalArgumentException si {@code array} es nulo
     */
    public static void selectionSort(int[] array) {
        if (array == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        METRICAS_SELECCION.reiniciar();

        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            int indiceMinimo = i;

            // La busqueda solo RECUERDA el indice del menor; no mueve datos. Ese es el
            // motivo por el que seleccion hace tan pocos intercambios.
            for (int j = i + 1; j < n; j++) {
                METRICAS_SELECCION.comparaciones++;
                if (array[j] < array[indiceMinimo]) {
                    indiceMinimo = j;
                }
            }

            // ESTA es la unica linea de intercambio de seleccion, y esta FUERA del ciclo
            // interno: a lo sumo un intercambio por pasada, es decir n-1 en total.
            if (indiceMinimo != i) {
                intercambiar(array, i, indiceMinimo);
                METRICAS_SELECCION.intercambios++;
            }
            METRICAS_SELECCION.pasadas++;
        }
    }

    /**
     * Verifica de forma independiente del algoritmo que el arreglo quedo ascendente.
     *
     * @param arreglo arreglo a verificar
     * @return {@code true} si cada elemento es menor o igual al siguiente
     * @throws IllegalArgumentException si {@code arreglo} es nulo
     */
    public static boolean estaOrdenado(int[] arreglo) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        for (int i = 1; i < arreglo.length; i++) {
            if (arreglo[i - 1] > arreglo[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Indica si dos arreglos tienen la misma longitud y los mismos elementos en el mismo orden.
     *
     * @param a primer arreglo
     * @param b segundo arreglo
     * @return {@code true} si son identicos posicion por posicion
     * @throws IllegalArgumentException si alguno es nulo
     */
    public static boolean sonIguales(int[] a, int[] b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Los arreglos no pueden ser nulos.");
        }
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Intercambia dos posiciones del arreglo.
     *
     * @param arreglo arreglo sobre el que se opera
     * @param i primera posicion
     * @param j segunda posicion
     * @throws IndexOutOfBoundsException si alguna posicion esta fuera de rango
     */
    public static void intercambiar(int[] arreglo, int i, int j) {
        int temporal = arreglo[i];
        arreglo[i] = arreglo[j];
        arreglo[j] = temporal;
    }

    /**
     * Devuelve una copia independiente del arreglo recibido.
     *
     * @param arreglo arreglo origen
     * @return nuevo arreglo con los mismos elementos
     * @throws IllegalArgumentException si {@code arreglo} es nulo
     */
    public static int[] copiar(int[] arreglo) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        int[] copia = new int[arreglo.length];
        for (int i = 0; i < arreglo.length; i++) {
            copia[i] = arreglo[i];
        }
        return copia;
    }

    /**
     * Representa el arreglo como texto entre corchetes.
     *
     * @param arreglo arreglo a formatear
     * @return cadena con los elementos separados por coma
     * @throws IllegalArgumentException si {@code arreglo} es nulo
     */
    public static String aTexto(int[] arreglo) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arreglo.length; i++) {
            sb.append(arreglo[i]);
            if (i < arreglo.length - 1) {
                sb.append(", ");
            }
        }
        return sb.append("]").toString();
    }
}

/**
 * Acumulador de metricas por algoritmo. No es publica: es soporte interno del ejercicio.
 */
class MetricasComparativa {
    final String nombre;
    long comparaciones;
    long intercambios;
    int pasadas;

    MetricasComparativa(String nombre) {
        this.nombre = nombre;
    }

    void reiniciar() {
        comparaciones = 0;
        intercambios = 0;
        pasadas = 0;
    }
}


