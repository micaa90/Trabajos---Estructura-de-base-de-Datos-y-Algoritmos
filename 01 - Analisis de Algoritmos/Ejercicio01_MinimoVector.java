/**
 * Ejercicio 1 del Práctico 1 (Análisis de Algoritmos): encuentra el valor mínimo
 * de un vector de números enteros mediante un único recorrido secuencial.
 *
 * <p>Estrategia: barrido lineal con un acumulador. Se toma el primer elemento como
 * mínimo provisorio y se lo compara contra cada elemento restante, actualizándolo
 * cuando aparece uno menor.</p>
 *
 * <p><b>¿Por qué NO es necesario ordenar el vector?</b> Ordenar resolvería el problema
 * (el mínimo quedaría en la posición 0), pero es un trabajo mucho mayor que el pedido:
 * ordenar por comparaciones cuesta como mínimo O(n log n), mientras que el mínimo se
 * obtiene en O(n). Ordenar calcula el orden relativo de TODOS los pares de elementos;
 * el problema solo pide un dato puntual. Además, ordenar in situ destruye el orden
 * original del vector y ordenar con copia gasta O(n) de memoria extra. Para n = 1.000.000
 * el barrido hace ~1.000.000 de comparaciones y el ordenamiento ~20.000.000: se pagaría
 * 20 veces más por la misma respuesta.</p>
 *
 * <p><b>¿Por qué alcanza una sola pasada?</b> Porque el invariante del acumulador es
 * "minimo contiene el menor de vector[0..i-1]". Al terminar el ciclo, i = n, por lo que
 * minimo es el menor de todo el vector. Ningún elemento puede quedar sin examinarse: si
 * se salteara uno, podría ser precisamente el mínimo (cota inferior Ω(n) del problema).</p>
 *
 * Complejidad: O(n) temporal (mejor = peor = promedio) y O(1) espacial.
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio01_MinimoVector {

    /** Comparaciones realizadas por la última invocación de {@link #encontrarMinimo(int[])}. */
    private static int ultimasComparaciones = 0;

    /**
     * Método principal: ejecuta las pruebas manuales de todos los casos límite.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 1 - Mínimo de un vector ===");

        probar("Caso nominal / traza del análisis", new int[] {7, 3, 9, 1, 5}, 1);
        probar("Un solo elemento", new int[] {42}, 42);
        probar("Todos los elementos iguales", new int[] {8, 8, 8, 8}, 8);
        probar("Todos negativos", new int[] {-3, -17, -1, -9}, -17);
        probar("Mínimo en la primera posición", new int[] {-5, 0, 3, 7}, -5);
        probar("Mínimo en la última posición", new int[] {4, 6, 2, -8}, -8);
        probar("Valores extremos de int", new int[] {Integer.MAX_VALUE, Integer.MIN_VALUE, 0}, Integer.MIN_VALUE);

        probarError("Vector vacío", new int[0]);
        probarError("Vector nulo", null);

        mostrarTrazaDetallada(new int[] {7, 3, 9, 1, 5});
    }

    /**
     * Devuelve el menor valor contenido en el vector recibido.
     *
     * <p>No ordena ni copia el vector: una sola pasada basta porque el acumulador
     * mantiene el mínimo de la porción ya examinada.</p>
     *
     * Complejidad: O(n) temporal, O(1) espacial.
     *
     * @param vector arreglo de enteros en el que se busca el mínimo
     * @return el valor mínimo del arreglo
     * @throws IllegalArgumentException si el arreglo es nulo o vacío, porque el mínimo
     *                                  de un conjunto sin elementos no está definido
     */
    public static int encontrarMinimo(int[] vector) {
        validar(vector);

        int minimo = vector[0];
        ultimasComparaciones = 0;

        // Arranca en 1: la posición 0 ya es el mínimo provisorio, compararla consigo
        // misma sería una comparación inútil. Se hacen exactamente n - 1 comparaciones.
        for (int i = 1; i < vector.length; i++) {
            ultimasComparaciones++;
            if (vector[i] < minimo) {
                minimo = vector[i];
            }
        }

        return minimo;
    }

    /**
     * Informa cuántas comparaciones hizo la última búsqueda del mínimo.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @return cantidad de comparaciones de la última invocación (n - 1 para n elementos)
     */
    public static int getUltimasComparaciones() {
        return ultimasComparaciones;
    }

    /**
     * Valida la precondición de la búsqueda del mínimo.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @param vector arreglo a validar
     * @throws IllegalArgumentException si el arreglo es nulo o vacío
     */
    private static void validar(int[] vector) {
        if (vector == null || vector.length == 0) {
            throw new IllegalArgumentException("El vector no puede ser nulo ni vacío.");
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
     * Ejecuta un caso de prueba exitoso e imprime esperado contra obtenido.
     *
     * Complejidad: O(n) temporal, O(n) espacial.
     *
     * @param descripcion nombre del caso de prueba
     * @param vector      entrada del caso
     * @param esperado    mínimo que debe devolver el algoritmo
     */
    public static void probar(String descripcion, int[] vector, int esperado) {
        int obtenido = encontrarMinimo(vector);
        System.out.println("\n- " + descripcion);
        System.out.println("  Vector: " + vectorATexto(vector));
        System.out.println("  Esperado: " + esperado + " | Obtenido: " + obtenido
                + " | Comparaciones: " + getUltimasComparaciones()
                + " -> " + (obtenido == esperado ? "OK" : "FALLA"));
    }

    /**
     * Ejecuta un caso de prueba que debe lanzar excepción y muestra el mensaje capturado.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @param descripcion nombre del caso de prueba
     * @param vector      entrada inválida del caso
     */
    public static void probarError(String descripcion, int[] vector) {
        System.out.println("\n- " + descripcion);
        System.out.println("  Vector: " + vectorATexto(vector));
        try {
            int obtenido = encontrarMinimo(vector);
            System.out.println("  Esperado: IllegalArgumentException | Obtenido: " + obtenido + " -> FALLA");
        } catch (IllegalArgumentException e) {
            System.out.println("  Esperado: IllegalArgumentException | Obtenido: "
                    + e.getClass().getSimpleName() + ": " + e.getMessage() + " -> OK");
        }
    }

    /**
     * Imprime la evolución del acumulador de mínimo iteración por iteración.
     *
     * Complejidad: O(n) temporal, O(1) espacial.
     *
     * @param vector arreglo a trazar
     * @throws IllegalArgumentException si el arreglo es nulo o vacío
     */
    public static void mostrarTrazaDetallada(int[] vector) {
        validar(vector);
        System.out.println("\n--- Traza de " + vectorATexto(vector) + " ---");
        System.out.printf("  %3s | %9s | %12s | %s%n", "i", "vector[i]", "minimo antes", "minimo despues");
        int minimo = vector[0];
        System.out.printf("  %3s | %9s | %12s | %d  (inicialización con vector[0])%n", "-", "-", "-", minimo);
        for (int i = 1; i < vector.length; i++) {
            int antes = minimo;
            if (vector[i] < minimo) {
                minimo = vector[i];
            }
            System.out.printf("  %3d | %9d | %12d | %d%n", i, vector[i], antes, minimo);
        }
        System.out.println("  Mínimo final: " + minimo
                + " | comparaciones: " + (vector.length - 1) + " = n - 1 con n = " + vector.length);
    }
}

