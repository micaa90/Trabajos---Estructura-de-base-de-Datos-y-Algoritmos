/**
 * Ejercicio 3 del Práctico 1 (Análisis de Algoritmos): busca un elemento en un vector
 * ORDENADO de enteros mediante búsqueda binaria iterativa, mostrando paso a paso cómo
 * evolucionan las variables de búsqueda (inicio, fin, medio).
 *
 * <p><b>Precondición indispensable:</b> el vector debe estar ordenado de menor a mayor.
 * La búsqueda binaria no la verifica (verificarla costaría O(n) y arruinaría la ventaja
 * del O(log n)); si se la aplica sobre un vector desordenado el algoritmo no se cuelga
 * pero puede devolver -1 aunque el elemento exista, porque descarta mitades basándose en
 * una suposición falsa. El método {@link #estaOrdenado(int[])} permite comprobarlo aparte.</p>
 *
 * <p><b>¿Por qué es más eficiente que la búsqueda lineal con datos ordenados?</b> El orden
 * agrega información: si vector[medio] &gt; valor, entonces TODOS los elementos a la derecha
 * de medio también son mayores que valor, así que se descartan sin mirarlos. Cada iteración
 * elimina la mitad del espacio de búsqueda, de modo que n se reduce a n/2, n/4, n/8...
 * La cantidad de divisiones por 2 hasta llegar a 1 es log2(n): para n = 1.000.000 son
 * 20 pasos contra 1.000.000 de la búsqueda lineal.</p>
 *
 * <p><b>Invariante del rango:</b> si el valor está en el vector, su índice está siempre
 * dentro de [inicio, fin]. Las tres ramas lo conservan: si vector[medio] == valor se
 * devuelve medio; si vector[medio] &lt; valor, el valor solo puede estar a la derecha y se
 * hace inicio = medio + 1; si es mayor, solo puede estar a la izquierda y se hace
 * fin = medio - 1. El while corta cuando inicio &gt; fin, es decir cuando el rango queda
 * vacío: ahí el invariante garantiza que el valor no existe.</p>
 *
 * Complejidad: O(1) mejor caso, O(log n) peor caso y promedio; O(1) espacial (versión iterativa).
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio03_BusquedaBinaria {

    /** Valor devuelto cuando el elemento buscado no está en el vector. */
    public static final int NO_ENCONTRADO = -1;

    /** Iteraciones consumidas por la última invocación de {@link #buscar(int[], int)}. */
    private static int ultimasIteraciones = 0;

    /** Indica si {@link #buscar(int[], int)} debe imprimir la evolución de las variables. */
    private static boolean mostrarPasos = true;

    /**
     * Método principal: ejecuta las pruebas manuales de todos los casos límite.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 3 - Búsqueda binaria en vector ordenado ===");

        int[] diez = {2, 5, 8, 12, 16, 23, 38, 56, 72, 91};

        probar("Caso nominal: valor presente (traza del análisis)", diez, 23, 5);
        probar("Valor ausente intermedio (traza del análisis)", diez, 4, NO_ENCONTRADO);
        probar("Extremo izquierdo: primer elemento", diez, 2, 0);
        probar("Extremo derecho: último elemento", diez, 91, 9);
        probar("Ausente menor que todos", diez, -1, NO_ENCONTRADO);
        probar("Ausente mayor que todos", diez, 100, NO_ENCONTRADO);
        probar("Cantidad impar de elementos", new int[] {1, 3, 5, 7, 9}, 7, 3);
        probar("Un solo elemento, presente", new int[] {10}, 10, 0);
        probar("Un solo elemento, ausente", new int[] {10}, 99, NO_ENCONTRADO);
        probar("Vector vacío", new int[0], 5, NO_ENCONTRADO);

        probarError("Vector nulo", null, 5);

        mostrarRiesgoDesbordamiento();
        mostrarEfectoDesorden();
        compararContraLineal();
    }

    /**
     * Busca un valor en un vector ordenado descartando la mitad del rango en cada paso.
     *
     * Complejidad: O(log n) temporal (O(1) si acierta en el primer medio), O(1) espacial.
     *
     * @param vector arreglo ordenado de menor a mayor (precondición no verificada)
     * @param valor  valor a localizar
     * @return índice donde se encontró el valor, o {@link #NO_ENCONTRADO} si no está
     * @throws IllegalArgumentException si el vector es nulo
     */
    public static int buscar(int[] vector, int valor) {
        if (vector == null) {
            throw new IllegalArgumentException("El vector no puede ser nulo.");
        }

        ultimasIteraciones = 0;
        int inicio = 0;
        int fin = vector.length - 1;

        // Invariante: si el valor existe, su índice pertenece a [inicio, fin].
        // Corte: inicio > fin significa rango vacío, por lo tanto el valor no existe.
        while (inicio <= fin) {
            ultimasIteraciones++;

            // inicio + (fin - inicio) / 2 en lugar de (inicio + fin) / 2: la suma de dos
            // índices grandes puede exceder Integer.MAX_VALUE y volverse negativa, lo que
            // provocaría un ArrayIndexOutOfBoundsException. Esta forma solo suma un
            // desplazamiento no negativo y nunca desborda.
            int medio = inicio + (fin - inicio) / 2;

            if (vector[medio] == valor) {
                imprimirPaso(inicio, fin, medio, vector[medio], "ENCONTRADO en el índice " + medio);
                return medio;
            }

            if (vector[medio] < valor) {
                imprimirPaso(inicio, fin, medio, vector[medio], "continúa a la DERECHA (inicio = " + (medio + 1) + ")");
                inicio = medio + 1;
            } else {
                imprimirPaso(inicio, fin, medio, vector[medio], "continúa a la IZQUIERDA (fin = " + (medio - 1) + ")");
                fin = medio - 1;
            }
        }

        return NO_ENCONTRADO;
    }

    /**
     * Informa cuántas iteraciones consumió la última búsqueda binaria.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @return cantidad de iteraciones del while de la última invocación
     */
    public static int getUltimasIteraciones() {
        return ultimasIteraciones;
    }

    /**
     * Verifica si un vector está ordenado de menor a mayor (precondición de la búsqueda).
     *
     * Complejidad: O(n) temporal, O(1) espacial.
     *
     * @param vector arreglo a verificar
     * @return true si cada elemento es menor o igual que el siguiente
     * @throws IllegalArgumentException si el vector es nulo
     */
    public static boolean estaOrdenado(int[] vector) {
        if (vector == null) {
            throw new IllegalArgumentException("El vector no puede ser nulo.");
        }
        for (int i = 1; i < vector.length; i++) {
            if (vector[i - 1] > vector[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Imprime una línea de la evolución de las variables de búsqueda.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @param inicio     límite inferior vivo del rango
     * @param fin        límite superior vivo del rango
     * @param medio      índice medio calculado
     * @param valorMedio valor almacenado en la posición media
     * @param decision   hacia dónde continúa la búsqueda
     */
    private static void imprimirPaso(int inicio, int fin, int medio, int valorMedio, String decision) {
        if (mostrarPasos) {
            System.out.printf("    iter %d | inicio=%d fin=%d medio=%d | vector[medio]=%d | %s%n",
                    ultimasIteraciones, inicio, fin, medio, valorMedio, decision);
        }
    }

    /**
     * Representa el contenido del vector como texto legible.
     *
     * Complejidad: O(n) temporal, O(n) espacial.
     *
     * @param vector arreglo a representar; puede ser nulo
     * @return cadena con el formato [a, b, c] o "null" si el arreglo es nulo
     */
    public static String vectorATexto(int[] vector) {
        if (vector == null) {
            return "null";
        }
        StringBuilder texto = new StringBuilder("[");
        for (int i = 0; i < vector.length; i++) {
            texto.append(vector[i]);
            if (i < vector.length - 1) {
                texto.append(", ");
            }
        }
        return texto.append("]").toString();
    }

    /**
     * Ejecuta un caso de prueba exitoso mostrando los pasos y el resultado.
     *
     * Complejidad: O(log n) temporal, O(n) espacial por el texto del vector.
     *
     * @param descripcion      nombre del caso
     * @param vector           entrada ordenada del caso
     * @param valor            valor buscado
     * @param posicionEsperada índice esperado o {@link #NO_ENCONTRADO}
     */
    public static void probar(String descripcion, int[] vector, int valor, int posicionEsperada) {
        System.out.println("\n- " + descripcion);
        System.out.println("  Vector: " + vectorATexto(vector) + " | Buscado: " + valor);
        int obtenida = buscar(vector, valor);
        System.out.println("  Esperado: " + posicionEsperada + " | Obtenido: " + obtenida
                + " | Iteraciones: " + getUltimasIteraciones()
                + " -> " + (obtenida == posicionEsperada ? "OK" : "FALLA"));
    }

    /**
     * Ejecuta un caso de prueba que debe lanzar excepción y muestra el mensaje capturado.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @param descripcion nombre del caso
     * @param vector      entrada inválida
     * @param valor       valor buscado
     */
    public static void probarError(String descripcion, int[] vector, int valor) {
        System.out.println("\n- " + descripcion);
        System.out.println("  Vector: " + vectorATexto(vector) + " | Buscado: " + valor);
        try {
            int obtenida = buscar(vector, valor);
            System.out.println("  Esperado: IllegalArgumentException | Obtenido: " + obtenida + " -> FALLA");
        } catch (IllegalArgumentException e) {
            System.out.println("  Esperado: IllegalArgumentException | Obtenido: "
                    + e.getClass().getSimpleName() + ": " + e.getMessage() + " -> OK");
        }
    }

    /**
     * Demuestra numéricamente por qué (inicio + fin) / 2 puede desbordar y la fórmula usada no.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     */
    public static void mostrarRiesgoDesbordamiento() {
        int inicio = Integer.MAX_VALUE - 2;
        int fin = Integer.MAX_VALUE;
        int medioIngenuo = (inicio + fin) / 2;        // desborda y queda negativo
        int medioSeguro = inicio + (fin - inicio) / 2; // se mantiene en rango
        System.out.println("\n--- Riesgo de desbordamiento en el cálculo del medio ---");
        System.out.println("  inicio = " + inicio + " | fin = " + fin);
        System.out.println("  (inicio + fin) / 2        = " + medioIngenuo + "  <- índice inválido (negativo)");
        System.out.println("  inicio + (fin - inicio)/2 = " + medioSeguro + "  <- correcto");
    }

    /**
     * Muestra el efecto de violar la precondición aplicando la búsqueda a un vector desordenado.
     *
     * Complejidad: O(n) temporal por la verificación de orden, O(1) espacial.
     */
    public static void mostrarEfectoDesorden() {
        int[] desordenado = {9, 1, 7, 3, 5};
        System.out.println("\n--- Precondición violada: vector desordenado ---");
        System.out.println("  Vector: " + vectorATexto(desordenado)
                + " | ¿está ordenado? " + estaOrdenado(desordenado));
        mostrarPasos = false;
        int obtenida = buscar(desordenado, 1);
        mostrarPasos = true;
        System.out.println("  Buscando 1 (existe en el índice 1) devuelve: " + obtenida
                + " -> resultado incorrecto: se descartó la mitad izquierda con información falsa.");
    }

    /**
     * Compara la cantidad de pasos de la búsqueda binaria contra la lineal para varios n.
     *
     * Complejidad: O(k) temporal con k = cantidad de tamaños comparados, O(1) espacial.
     */
    public static void compararContraLineal() {
        int[] tamanios = {10, 1000, 1000000};
        System.out.println("\n--- Pasos en el peor caso: binaria contra lineal ---");
        System.out.printf("  %10s | %18s | %s%n", "n", "binaria (log2 n)", "lineal (n)");
        for (int n : tamanios) {
            int pasos = (int) Math.ceil(Math.log(n + 1.0) / Math.log(2));
            System.out.printf("  %10d | %18d | %d%n", n, pasos, n);
        }
    }
}

/*
MATRIZ DE CASOS DE PRUEBA MANUALES
Compilación: javac Ejercicio03_BusquedaBinaria.java -d /tmp/v1
Ejecución:   java -cp /tmp/v1 Ejercicio03_BusquedaBinaria
Todos los casos ya están cableados en main(); no se lee entrada por consola.

ID    | Objetivo                                      | Entrada exacta                                 | Resultado esperado                                       | Criterio de aprobación
------+-----------------------------------------------+------------------------------------------------+----------------------------------------------------------+--------------------------------------------------------
CP-01 | Flujo nominal: valor presente                 | {2,5,8,12,16,23,38,56,72,91}, valor 23         | índice 5 en 3 iteraciones, con los pasos impresos        | Termina en "-> OK" e Iteraciones = 3 con 3 líneas "iter"
CP-02 | Valor límite: primer y último elemento        | mismo vector, valores 2 y 91                   | índices 0 y 9 respectivamente                            | Ambos casos terminan en "-> OK"
CP-03 | Error de rango: valor fuera de los extremos   | mismo vector, valores -1 y 100                 | -1 en ambos, sin excepción ni índice inválido            | Ambos terminan en "-> OK" y no hay stack trace
CP-04 | Entrada vacía                                 | new int[0], valor 5                            | -1 con 0 iteraciones (el while no entra)                 | Termina en "-> OK" e Iteraciones = 0
CP-05 | Entrada nula (validación)                     | null, valor 5                                  | IllegalArgumentException "El vector no puede ser nulo."  | Se imprime el mensaje de la excepción y "-> OK"
*/
