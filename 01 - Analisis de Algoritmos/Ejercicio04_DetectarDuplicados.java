import java.util.HashSet;
import java.util.Set;

/**
 * Ejercicio 4 del Práctico 1 (Análisis de Algoritmos): detecta si un vector de enteros
 * contiene elementos duplicados mediante DOS estrategias distintas, y las compara.
 *
 * <p><b>Tabla comparativa de complejidad</b></p>
 * <pre>
 * Solución                 | Temporal mejor | Temporal peor | Espacial | Operación dominante
 * -------------------------+----------------+---------------+----------+-------------------------------
 * A) Dos ciclos anidados   | O(1)           | O(n^2)        | O(1)     | comparación vector[i]==vector[j]
 * B) HashSet               | O(1)           | O(n)          | O(n)     | add/contains sobre la tabla hash
 * </pre>
 *
 * <p><b>¿Por qué el HashSet detecta el duplicado en una sola pasada?</b> Porque consultar
 * si un elemento ya fue visto le cuesta O(1) promedio: la función hash transforma el valor
 * en un índice de bucket, así que no hay que recorrer lo ya visto para saber si está.
 * La versión anidada, en cambio, no recuerda nada: para cada elemento vuelve a recorrer la
 * cola del vector, y esas comparaciones se acumulan en n(n-1)/2.</p>
 *
 * <p><b>Qué recurso sacrifica cada una:</b> la anidada sacrifica TIEMPO para no gastar
 * memoria (O(1) extra, sirve con memoria muy limitada o n chico); el HashSet sacrifica
 * MEMORIA (hasta n claves más la sobrecarga de cada nodo y del arreglo de buckets, bastante
 * más que n*4 bytes por el boxing de Integer) para ganar tiempo. Con vectores grandes la
 * anidada se vuelve inviable (n = 100.000 implica ~5.000.000.000 de comparaciones) mientras
 * el HashSet sigue haciendo n operaciones; el límite del HashSet es la RAM disponible.</p>
 *
 * Complejidad: ver tabla; cada método documenta la suya.
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio04_DetectarDuplicados {

    /** Cantidad de elementos del vector grande usado en la comparación de rendimiento. */
    private static final int TAMANIO_GRANDE = 20000;

    /** Comparaciones realizadas por la última corrida de {@link #hayDuplicadosAnidado(int[])}. */
    private static long operacionesAnidado = 0;

    /** Operaciones de tabla hash de la última corrida de {@link #hayDuplicadosHashSet(int[])}. */
    private static long operacionesHashSet = 0;

    /**
     * Método principal: ejecuta ambas soluciones sobre los mismos vectores y compara.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 4 - Detección de duplicados: anidado contra HashSet ===");

        System.out.printf("%n%-42s | %-9s | %-9s | %12s | %12s | %10s | %10s%n",
                "Caso", "Esp.", "Anid.", "Ops anidado", "Ops HashSet", "ns anid.", "ns hash");
        System.out.println("-".repeat(130));

        compararAmbas("Vector vacío", new int[0], false);
        compararAmbas("Un solo elemento", new int[] {5}, false);
        compararAmbas("Sin duplicados", new int[] {3, 7, 2, 9}, false);
        compararAmbas("Con duplicado (traza del análisis)", new int[] {3, 7, 2, 7}, true);
        compararAmbas("Todos iguales (mejor caso anidado)", new int[] {4, 4, 4, 4}, true);
        compararAmbas("Duplicado en las dos últimas posiciones", new int[] {1, 2, 3, 8, 8}, true);
        compararAmbas("Negativos sin duplicados", new int[] {-1, -5, -3}, false);
        compararAmbas("Grande " + TAMANIO_GRANDE + " sin duplicados", generarDistintos(TAMANIO_GRANDE), false);
        compararAmbas("Grande " + TAMANIO_GRANDE + " peor caso anidado",
                generarDuplicadoAlFinal(TAMANIO_GRANDE), true);

        probarError("Vector nulo", null);

        trazarAnidado(new int[] {3, 7, 2, 7});
        trazarHashSet(new int[] {3, 7, 2, 7});
    }

    /**
     * Detecta duplicados comparando todos los pares posibles con dos ciclos anidados.
     *
     * <p>La operación dominante es la comparación vector[i] == vector[j]; se ejecuta
     * n(n-1)/2 veces cuando no hay duplicados. No usa memoria auxiliar.</p>
     *
     * Complejidad: O(1) mejor caso, O(n^2) peor caso y promedio; O(1) espacial.
     *
     * @param vector arreglo a inspeccionar
     * @return true si existe al menos un par de elementos iguales
     * @throws IllegalArgumentException si el vector es nulo
     */
    public static boolean hayDuplicadosAnidado(int[] vector) {
        if (vector == null) {
            throw new IllegalArgumentException("El vector no puede ser nulo.");
        }

        operacionesAnidado = 0;

        for (int i = 0; i < vector.length; i++) {
            // j arranca en i + 1 porque los pares (i, j) con j < i ya fueron comparados:
            // comparar ambos órdenes duplicaría el trabajo sin aportar información.
            for (int j = i + 1; j < vector.length; j++) {
                operacionesAnidado++;
                if (vector[i] == vector[j]) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Detecta duplicados con una sola pasada, usando un HashSet como memoria de lo visto.
     *
     * <p>Memoria adicional que se paga: un HashSet con hasta n claves Integer (cada una es
     * un objeto en el Heap) más el arreglo de buckets, es decir O(n) espacio contra el O(1)
     * de la versión anidada. Se cuenta una operación por intento de inserción, que incluye
     * el cálculo del hash y la consulta del bucket, ambos O(1) promedio.</p>
     *
     * Complejidad: O(1) mejor caso, O(n) peor caso y promedio; O(n) espacial.
     *
     * @param vector arreglo a inspeccionar
     * @return true si existe al menos un par de elementos iguales
     * @throws IllegalArgumentException si el vector es nulo
     */
    public static boolean hayDuplicadosHashSet(int[] vector) {
        if (vector == null) {
            throw new IllegalArgumentException("El vector no puede ser nulo.");
        }

        operacionesHashSet = 0;
        Set<Integer> vistos = new HashSet<>();

        for (int valor : vector) {
            operacionesHashSet++;
            // add devuelve false si la clave ya estaba: una sola operación O(1) promedio
            // reemplaza el recorrido completo de los elementos anteriores.
            if (!vistos.add(valor)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Informa las comparaciones de la última corrida de la versión anidada.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @return cantidad de comparaciones entre pares
     */
    public static long getOperacionesAnidado() {
        return operacionesAnidado;
    }

    /**
     * Informa las operaciones de tabla hash de la última corrida de la versión con HashSet.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @return cantidad de intentos de inserción realizados
     */
    public static long getOperacionesHashSet() {
        return operacionesHashSet;
    }

    /**
     * Genera un vector de n elementos distintos (peor caso de ambas soluciones en recorrido).
     *
     * Complejidad: O(n) temporal, O(n) espacial.
     *
     * @param n cantidad de elementos
     * @return arreglo con los valores 0, 2, 4, ... sin repeticiones
     * @throws IllegalArgumentException si n es negativo
     */
    public static int[] generarDistintos(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("El tamaño no puede ser negativo.");
        }
        int[] vector = new int[n];
        for (int i = 0; i < n; i++) {
            vector[i] = i * 2;
        }
        return vector;
    }

    /**
     * Genera un vector de n elementos cuyo único duplicado está en las dos últimas posiciones.
     *
     * Complejidad: O(n) temporal, O(n) espacial.
     *
     * @param n cantidad de elementos (debe ser al menos 2)
     * @return arreglo con un único par duplicado al final
     * @throws IllegalArgumentException si n es menor que 2
     */
    public static int[] generarDuplicadoAlFinal(int n) {
        if (n < 2) {
            throw new IllegalArgumentException("Se necesitan al menos 2 elementos.");
        }
        int[] vector = generarDistintos(n);
        // Peor caso de la versión anidada: el par duplicado es el último que se compara.
        vector[n - 1] = vector[n - 2];
        return vector;
    }

    /**
     * Ejecuta ambas soluciones sobre el mismo vector e imprime la fila comparativa.
     *
     * Complejidad: O(n^2) temporal (la domina la versión anidada), O(n) espacial.
     *
     * @param descripcion nombre del caso
     * @param vector      entrada del caso
     * @param esperado    resultado que deben devolver ambas soluciones
     */
    public static void compararAmbas(String descripcion, int[] vector, boolean esperado) {
        long inicioAnidado = System.nanoTime();
        boolean resultadoAnidado = hayDuplicadosAnidado(vector);
        long nsAnidado = System.nanoTime() - inicioAnidado;

        long inicioHash = System.nanoTime();
        boolean resultadoHash = hayDuplicadosHashSet(vector);
        long nsHash = System.nanoTime() - inicioHash;

        boolean ok = resultadoAnidado == esperado && resultadoHash == esperado;
        System.out.printf("%-42s | %-9s | %-9s | %12d | %12d | %10d | %10d  %s%n",
                descripcion, esperado, resultadoAnidado, getOperacionesAnidado(),
                getOperacionesHashSet(), nsAnidado, nsHash, ok ? "OK" : "FALLA");
    }

    /**
     * Ejecuta un caso de prueba que debe lanzar excepción en ambas soluciones.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @param descripcion nombre del caso
     * @param vector      entrada inválida
     */
    public static void probarError(String descripcion, int[] vector) {
        System.out.println("\n- " + descripcion);
        try {
            hayDuplicadosAnidado(vector);
            System.out.println("  Anidado  -> FALLA: no lanzó excepción");
        } catch (IllegalArgumentException e) {
            System.out.println("  Anidado  -> OK: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        try {
            hayDuplicadosHashSet(vector);
            System.out.println("  HashSet  -> FALLA: no lanzó excepción");
        } catch (IllegalArgumentException e) {
            System.out.println("  HashSet  -> OK: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    /**
     * Imprime cada par comparado por la versión anidada.
     *
     * Complejidad: O(n^2) temporal, O(1) espacial.
     *
     * @param vector arreglo a trazar
     * @throws IllegalArgumentException si el vector es nulo
     */
    public static void trazarAnidado(int[] vector) {
        if (vector == null) {
            throw new IllegalArgumentException("El vector no puede ser nulo.");
        }
        System.out.println("\n--- Traza versión anidada con " + vectorATexto(vector) + " ---");
        System.out.printf("  %4s | %4s | %-18s | %s%n", "i", "j", "par comparado", "¿iguales?");
        int comparaciones = 0;
        for (int i = 0; i < vector.length; i++) {
            for (int j = i + 1; j < vector.length; j++) {
                comparaciones++;
                boolean iguales = vector[i] == vector[j];
                System.out.printf("  %4d | %4d | %-18s | %s%n", i, j,
                        "(" + vector[i] + ", " + vector[j] + ")", iguales);
                if (iguales) {
                    System.out.println("  Corta acá: duplicado hallado tras " + comparaciones + " comparaciones.");
                    return;
                }
            }
        }
        System.out.println("  Sin duplicados tras " + comparaciones + " comparaciones = n(n-1)/2.");
    }

    /**
     * Imprime el contenido del conjunto paso a paso en la versión con HashSet.
     *
     * Complejidad: O(n) temporal, O(n) espacial.
     *
     * @param vector arreglo a trazar
     * @throws IllegalArgumentException si el vector es nulo
     */
    public static void trazarHashSet(int[] vector) {
        if (vector == null) {
            throw new IllegalArgumentException("El vector no puede ser nulo.");
        }
        System.out.println("\n--- Traza versión HashSet con " + vectorATexto(vector) + " ---");
        System.out.printf("  %4s | %8s | %-12s | %s%n", "i", "valor", "add devuelve", "conjunto después");
        Set<Integer> vistos = new HashSet<>();
        for (int i = 0; i < vector.length; i++) {
            boolean agregado = vistos.add(vector[i]);
            System.out.printf("  %4d | %8d | %-12s | %s%n", i, vector[i], agregado, vistos);
            if (!agregado) {
                System.out.println("  Corta acá: el valor " + vector[i]
                        + " ya estaba en el conjunto, tras " + (i + 1) + " operaciones.");
                return;
            }
        }
        System.out.println("  Sin duplicados tras " + vector.length + " operaciones = n.");
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
}

/*
MATRIZ DE CASOS DE PRUEBA MANUALES
Compilación: javac Ejercicio04_DetectarDuplicados.java -d /tmp/v1
Ejecución:   java -cp /tmp/v1 Ejercicio04_DetectarDuplicados
Todos los casos ya están cableados en main(); no se lee entrada por consola.
Los tiempos en nanosegundos varían entre corridas; solo se evalúa su orden de magnitud relativo.

ID    | Objetivo                                           | Entrada exacta                            | Resultado esperado                                            | Criterio de aprobación
------+----------------------------------------------------+-------------------------------------------+---------------------------------------------------------------+---------------------------------------------------------------
CP-01 | Flujo nominal: ambas soluciones coinciden en true  | {3, 7, 2, 7}                              | Esp. true, Anid. true, 5 ops anidado, 4 ops HashSet           | La fila termina en "OK" y ambos resultados son true
CP-02 | Flujo nominal negativo: sin duplicados             | {3, 7, 2, 9}                              | Esp. false, 6 ops anidado (= n(n-1)/2), 4 ops HashSet         | La fila termina en "OK" y ops anidado = 6
CP-03 | Valor límite: peor caso anidado con n = 20000      | generarDuplicadoAlFinal(20000)            | true en ambas; ~199.990.000 ops anidado contra 20.000 HashSet  | La fila termina en "OK" y ops anidado supera 10^8
CP-04 | Entrada vacía y de un solo elemento                | new int[0] y {5}                          | false en ambas, 0 ops anidado; HashSet 0 y 1 op               | Ambas filas terminan en "OK" sin excepciones
CP-05 | Entrada nula (error de validación)                 | null                                      | IllegalArgumentException "El vector no puede ser nulo." x2    | Se imprimen las dos líneas "-> OK:" con el mensaje
*/
