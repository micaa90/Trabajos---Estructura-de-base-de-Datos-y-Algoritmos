/**
 * Ejercicio 09 - Comparacion de los tres algoritmos simples.
 *
 * <p>Implementa Bubble Sort, Selection Sort e Insertion Sort y ordena con los tres el MISMO
 * arreglo (cada uno sobre su propia COPIA). Para cada escenario imprime el arreglo original,
 * el arreglo ordenado por cada metodo y una tabla con comparaciones, intercambios o
 * desplazamientos y tiempo en nanosegundos. Verifica con {@code estaOrdenado(int[])} y
 * confirma que los tres resultados son identicos entre si.</p>
 *
 * <p><b>Estrategia de cada uno, en una frase que lo distingue de los otros dos:</b></p>
 * <ul>
 *   <li><b>Bubble Sort:</b> compara e intercambia VECINOS, de modo que el mayor pendiente
 *       burbujea hasta el final en cada pasada.</li>
 *   <li><b>Selection Sort:</b> BUSCA el minimo del tramo no ordenado recordando solo su
 *       indice y lo trae al frente con un unico intercambio por pasada.</li>
 *   <li><b>Insertion Sort:</b> mantiene un PREFIJO ordenado e inserta cada elemento nuevo
 *       dentro de ese prefijo corriendo a la derecha los mayores que el.</li>
 * </ul>
 *
 * <p><b>Metricas y como hacerlas comparables.</b> Burbuja y Seleccion mueven datos con
 * INTERCAMBIOS (una permuta de dos posiciones = 3 asignaciones); Insertion mueve datos con
 * DESPLAZAMIENTOS (una copia de un elemento un lugar a la derecha = 1 asignacion). Para que
 * la tabla sea leible se informan ambos en la misma columna, aclarando el tipo, y se agrega
 * la columna "escrituras" que normaliza todo a ASIGNACIONES de elementos: 3 por intercambio
 * y 1 por desplazamiento (mas 1 por la escritura final de la clave en Insertion). Esa
 * columna es la que permite comparar de verdad el costo de mover datos.</p>
 *
 * <p><b>Por que los tres son O(n^2) pero se comportan distinto.</b> Con un arreglo YA
 * ORDENADO ganan Burbuja (corta en la primera pasada: n-1 comparaciones, 0 movimientos) e
 * Insertion (n-1 comparaciones, 0 desplazamientos), mientras Seleccion sigue haciendo
 * n(n-1)/2 comparaciones porque nunca detecta el orden previo. Con un arreglo INVERTIDO
 * pierden Burbuja e Insertion, que hacen n(n-1)/2 movimientos; Seleccion mantiene sus
 * comparaciones pero sigue haciendo a lo sumo n-1 intercambios, asi que es la mejor opcion
 * cuando mover un elemento es caro.</p>
 *
 * <p><b>Estabilidad.</b> Burbuja (solo intercambia si {@code >} estricto) e Insertion (el
 * while corta ante un igual) son ESTABLES: no alteran el orden relativo de elementos de
 * igual valor. Seleccion NO es estable: el intercambio con el minimo manda el elemento
 * desplazado a una posicion arbitraria lejana. Importa cuando se ordena por una clave y se
 * quiere preservar un orden previo por otra (por ejemplo, ordenar por puntaje manteniendo el
 * orden de llegada).</p>
 *
 * <p>Tabla de referencia:</p>
 * <pre>
 * Algoritmo       Mejor     Promedio   Peor      Espacial  Estable  Metrica de movimiento
 * Bubble Sort     O(n)      O(n^2)     O(n^2)    O(1)      si       intercambios
 * Selection Sort  O(n^2)    O(n^2)     O(n^2)    O(1)      no       intercambios
 * Insertion Sort  O(n)      O(n^2)     O(n^2)    O(1)      si       desplazamientos
 * </pre>
 *
 * <p>Complejidad: O(n^2) temporal en el caso promedio para los tres; O(1) espacial adicional
 * (los tres ordenan in situ).</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio09_ComparacionAlgoritmosSimples {

    /**
     * Metodo principal. Ejecuta la comparacion sobre cuatro escenarios y luego los casos limite.
     *
     * @param args argumentos de la linea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== EJERCICIO 09 - COMPARACION DE ALGORITMOS SIMPLES ===");

        compararEscenario("Desordenado", new int[] {5, 2, 9, 1, 7, 6});
        compararEscenario("Ya ordenado", new int[] {1, 2, 5, 6, 7, 9});
        compararEscenario("Ordenado al reves", new int[] {9, 7, 6, 5, 2, 1});
        compararEscenario("Arreglo de la traza del analisis", new int[] {4, 3, 2, 1});

        System.out.println("\n=== CASOS LIMITE ===");
        compararEscenario("Arreglo vacio", new int[] {});
        compararEscenario("Un solo elemento", new int[] {8});
        compararEscenario("Con repetidos", new int[] {2, 1, 2, 1});

        System.out.println("\n--- Arreglos nulos (deben lanzar excepcion) ---");
        verificarNulo("bubbleSort");
        verificarNulo("selectionSort");
        verificarNulo("insertionSort");

        System.out.println("\n=== CONTRASTE CON LA TEORIA ===");
        int n = 6;
        System.out.println("Para n = " + n + ": n(n-1)/2 = " + (n * (n - 1) / 2)
                + " (comparaciones de seleccion en todos los casos, y cota del peor caso"
                + " de burbuja e insercion) | n-1 = " + (n - 1)
                + " (comparaciones de burbuja e insercion con entrada ya ordenada, y cota de"
                + " intercambios de seleccion).");
    }

    /**
     * Ordena copias del mismo arreglo con los tres algoritmos e imprime la tabla comparativa.
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

        // Los tres algoritmos deben partir del MISMO estado inicial: si compartieran el
        // arreglo, el segundo y el tercero recibirian datos ya ordenados.
        int[] paraBurbuja = copiar(original);
        int[] paraSeleccion = copiar(original);
        int[] paraInsercion = copiar(original);

        MetricasSimples mBurbuja = new MetricasSimples("Bubble Sort", "intercambios");
        MetricasSimples mSeleccion = new MetricasSimples("Selection Sort", "intercambios");
        MetricasSimples mInsercion = new MetricasSimples("Insertion Sort", "desplazamientos");

        long t0 = System.nanoTime();
        bubbleSort(paraBurbuja, mBurbuja);
        mBurbuja.nanosegundos = System.nanoTime() - t0;

        t0 = System.nanoTime();
        selectionSort(paraSeleccion, mSeleccion);
        mSeleccion.nanosegundos = System.nanoTime() - t0;

        t0 = System.nanoTime();
        insertionSort(paraInsercion, mInsercion);
        mInsercion.nanosegundos = System.nanoTime() - t0;

        System.out.println("Bubble Sort ordenado:    " + aTexto(paraBurbuja)
                + " | estaOrdenado=" + estaOrdenado(paraBurbuja));
        System.out.println("Selection Sort ordenado: " + aTexto(paraSeleccion)
                + " | estaOrdenado=" + estaOrdenado(paraSeleccion));
        System.out.println("Insertion Sort ordenado: " + aTexto(paraInsercion)
                + " | estaOrdenado=" + estaOrdenado(paraInsercion));
        boolean coinciden = sonIguales(paraBurbuja, paraSeleccion) && sonIguales(paraSeleccion, paraInsercion);
        System.out.println("Los tres resultados son identicos: " + coinciden);

        System.out.println("+----------------+---------------+------------------+-----------------+------------+-------------+");
        System.out.println("| Algoritmo      | Comparaciones | Movimientos      | Tipo movimiento | Escrituras | Nanosegundos|");
        System.out.println("+----------------+---------------+------------------+-----------------+------------+-------------+");
        imprimirFila(mBurbuja);
        imprimirFila(mSeleccion);
        imprimirFila(mInsercion);
        System.out.println("+----------------+---------------+------------------+-----------------+------------+-------------+");
    }

    /**
     * Imprime una fila de la tabla comparativa.
     *
     * @param m metricas del algoritmo a mostrar
     * @throws IllegalArgumentException si {@code m} es nulo
     */
    public static void imprimirFila(MetricasSimples m) {
        if (m == null) {
            throw new IllegalArgumentException("Las metricas no pueden ser nulas.");
        }
        System.out.printf("| %-14s | %13d | %16d | %-15s | %10d | %11d |%n",
                m.nombre, m.comparaciones, m.movimientos, m.tipoMovimiento,
                m.escrituras, m.nanosegundos);
    }

    /**
     * Ordena el arreglo comparando e intercambiando elementos vecinos (con corte temprano).
     *
     * @param arreglo arreglo a ordenar in situ
     * @param metricas acumulador de metricas
     * @throws IllegalArgumentException si {@code arreglo} o {@code metricas} son nulos
     */
    public static void bubbleSort(int[] arreglo, MetricasSimples metricas) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        if (metricas == null) {
            throw new IllegalArgumentException("El acumulador de metricas no puede ser nulo.");
        }
        int n = arreglo.length;
        for (int pasada = 1; pasada < n; pasada++) {
            boolean huboIntercambio = false;
            for (int j = 0; j < n - pasada; j++) {
                metricas.comparaciones++;
                if (arreglo[j] > arreglo[j + 1]) {
                    int temporal = arreglo[j];
                    arreglo[j] = arreglo[j + 1];
                    arreglo[j + 1] = temporal;
                    metricas.movimientos++;
                    metricas.escrituras += 3; // un intercambio cuesta 3 asignaciones
                    huboIntercambio = true;
                }
            }
            if (!huboIntercambio) {
                break;
            }
        }
    }

    /**
     * Ordena el arreglo seleccionando el minimo del tramo no ordenado en cada pasada.
     *
     * @param arreglo arreglo a ordenar in situ
     * @param metricas acumulador de metricas
     * @throws IllegalArgumentException si {@code arreglo} o {@code metricas} son nulos
     */
    public static void selectionSort(int[] arreglo, MetricasSimples metricas) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        if (metricas == null) {
            throw new IllegalArgumentException("El acumulador de metricas no puede ser nulo.");
        }
        int n = arreglo.length;
        for (int i = 0; i < n - 1; i++) {
            int indiceMinimo = i;
            for (int j = i + 1; j < n; j++) {
                metricas.comparaciones++;
                if (arreglo[j] < arreglo[indiceMinimo]) {
                    indiceMinimo = j;
                }
            }
            if (indiceMinimo != i) {
                int temporal = arreglo[i];
                arreglo[i] = arreglo[indiceMinimo];
                arreglo[indiceMinimo] = temporal;
                metricas.movimientos++;
                metricas.escrituras += 3;
            }
        }
    }

    /**
     * Ordena el arreglo insertando cada elemento en el prefijo ya ordenado.
     *
     * @param arreglo arreglo a ordenar in situ
     * @param metricas acumulador de metricas
     * @throws IllegalArgumentException si {@code arreglo} o {@code metricas} son nulos
     */
    public static void insertionSort(int[] arreglo, MetricasSimples metricas) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        if (metricas == null) {
            throw new IllegalArgumentException("El acumulador de metricas no puede ser nulo.");
        }
        for (int i = 1; i < arreglo.length; i++) {
            int clave = arreglo[i];
            int j = i - 1;
            while (j >= 0) {
                metricas.comparaciones++;
                if (arreglo[j] > clave) {
                    arreglo[j + 1] = arreglo[j];
                    metricas.movimientos++;
                    metricas.escrituras += 1; // un desplazamiento cuesta 1 asignacion
                    j--;
                } else {
                    break;
                }
            }
            arreglo[j + 1] = clave;
            metricas.escrituras += 1; // escritura de la clave en su hueco
        }
    }

    /**
     * Comprueba que el metodo indicado rechace un arreglo nulo.
     *
     * @param nombreMetodo nombre del metodo a probar: "bubbleSort", "selectionSort" o "insertionSort"
     */
    public static void verificarNulo(String nombreMetodo) {
        try {
            if ("bubbleSort".equals(nombreMetodo)) {
                bubbleSort(null, new MetricasSimples("x", "y"));
            } else if ("selectionSort".equals(nombreMetodo)) {
                selectionSort(null, new MetricasSimples("x", "y"));
            } else {
                insertionSort(null, new MetricasSimples("x", "y"));
            }
            System.out.println(nombreMetodo + ": ERROR, no se lanzo la excepcion esperada.");
        } catch (IllegalArgumentException e) {
            System.out.println(nombreMetodo + " -> IllegalArgumentException: " + e.getMessage());
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
     * Indica si dos arreglos son identicos posicion por posicion.
     *
     * @param a primer arreglo
     * @param b segundo arreglo
     * @return {@code true} si tienen la misma longitud y el mismo contenido
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
 * Acumulador de metricas por algoritmo, incluido el tiempo medido.
 * No es publica: es soporte interno del ejercicio.
 */
class MetricasSimples {
    final String nombre;
    final String tipoMovimiento;
    long comparaciones;
    long movimientos;
    long escrituras;
    long nanosegundos;

    MetricasSimples(String nombre, String tipoMovimiento) {
        this.nombre = nombre;
        this.tipoMovimiento = tipoMovimiento;
    }
}

/*
 * ===========================================================================
 * MATRIZ DE CASOS DE PRUEBA MANUALES
 * ===========================================================================
 * Compilacion: javac Ejercicio09_ComparacionAlgoritmosSimples.java -d /tmp/v3
 * Ejecucion:   java -cp /tmp/v3 Ejercicio09_ComparacionAlgoritmosSimples
 *
 * ID: CP-09-01  (flujo nominal)
 *   Objetivo: ordenar el mismo arreglo desordenado con los tres algoritmos y comparar metricas.
 *   Entrada exacta: escenario "Desordenado" con {5, 2, 9, 1, 7, 6}.
 *   Resultado esperado: los tres dan [1, 2, 5, 6, 7, 9] y "Los tres resultados son identicos:
 *                       true"; Bubble 14 comparaciones / 7 intercambios; Selection 15
 *                       comparaciones / 3 intercambios; Insertion 10 comparaciones /
 *                       7 desplazamientos.
 *   Criterio de aprobacion: estaOrdenado=true en los tres y los tres arreglos coinciden.
 *
 * ID: CP-09-02  (mejor caso)
 *   Objetivo: ver que algoritmo gana con un arreglo ya ordenado.
 *   Entrada exacta: escenario "Ya ordenado" con {1, 2, 5, 6, 7, 9}.
 *   Resultado esperado: Bubble 5 comparaciones / 0 intercambios; Insertion 5 comparaciones /
 *                       0 desplazamientos; Selection 15 comparaciones / 0 intercambios.
 *   Criterio de aprobacion: Bubble e Insertion hacen n-1 = 5 comparaciones y Selection
 *                           hace n(n-1)/2 = 15, confirmando que solo los dos primeros
 *                           tienen mejor caso O(n).
 *
 * ID: CP-09-03  (peor caso)
 *   Objetivo: ver que algoritmo pierde con el arreglo invertido.
 *   Entrada exacta: escenario "Ordenado al reves" con {9, 7, 6, 5, 2, 1}.
 *   Resultado esperado: Bubble 15 comparaciones / 15 intercambios (45 escrituras);
 *                       Insertion 15 comparaciones / 15 desplazamientos (20 escrituras);
 *                       Selection 15 comparaciones / 3 intercambios (9 escrituras).
 *   Criterio de aprobacion: los movimientos de Bubble e Insertion llegan a n(n-1)/2 = 15 y
 *                           Selection se mantiene en <= n-1 = 5 intercambios.
 *
 * ID: CP-09-04  (entrada vacia)
 *   Objetivo: comprobar que los tres toleran un arreglo sin elementos.
 *   Entrada exacta: escenario "Arreglo vacio" con new int[] {}.
 *   Resultado esperado: los tres imprimen [], estaOrdenado=true, resultados identicos=true y
 *                       la tabla queda en 0 comparaciones y 0 movimientos.
 *   Criterio de aprobacion: no hay excepcion y todos los contadores quedan en cero.
 *
 * ID: CP-09-05  (un elemento)
 *   Objetivo: comprobar el caso minimo no vacio.
 *   Entrada exacta: escenario "Un solo elemento" con new int[] {8}.
 *   Resultado esperado: los tres imprimen [8], estaOrdenado=true y 0 comparaciones y
 *                       0 movimientos en la tabla.
 *   Criterio de aprobacion: ningun ciclo externo se ejecuta y los arreglos quedan intactos.
 * ===========================================================================
 */
