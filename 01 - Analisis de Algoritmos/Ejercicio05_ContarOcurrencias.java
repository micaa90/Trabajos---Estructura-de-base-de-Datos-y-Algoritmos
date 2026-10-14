/**
 * Ejercicio 5 del Práctico 1 (Análisis de Algoritmos): cuenta cuántas veces aparece un
 * valor determinado dentro de un vector de enteros.
 *
 * <p><b>¿Por qué el recorrido NO se interrumpe?</b> Porque la respuesta depende de TODAS
 * las posiciones. Encontrar una coincidencia en el índice i no autoriza a cortar: podría
 * haber más coincidencias en i+1..n-1 y el resultado quedaría incompleto. Es la diferencia
 * esencial con "buscar si existe" (ejercicio 2): ahí la respuesta es un booleano y la
 * primera coincidencia ya la decide de forma definitiva, así que el corte anticipado es
 * correcto. Acá la respuesta es una cantidad agregada y solo está determinada cuando se
 * inspeccionó el vector completo. Por eso el método no usa return anticipado.</p>
 *
 * <p><b>¿Por qué mejor caso y peor caso coinciden?</b> Porque el número de iteraciones no
 * depende de los datos: siempre son exactamente n, sin importar si el valor aparece 0, 1 o
 * n veces. El ciclo no tiene ninguna salida temprana, así que el costo es Θ(n) exacto:
 * mejor caso = peor caso = caso promedio = O(n).</p>
 *
 * Complejidad: O(n) temporal (mejor = peor = promedio) y O(1) espacial.
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio05_ContarOcurrencias {

    /** Comparaciones realizadas por la última invocación de {@link #contarOcurrencias(int[], int)}. */
    private static int ultimasComparaciones = 0;

    /**
     * Método principal: ejecuta las pruebas manuales de todos los casos límite.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 5 - Contar ocurrencias de un valor ===");

        probar("Caso nominal (traza del análisis)", new int[] {10, 20, 10, 30, 10}, 10, 3);
        probar("El valor no aparece", new int[] {1, 2, 3}, 9, 0);
        probar("El valor aparece una sola vez", new int[] {1, 2, 3}, 2, 1);
        probar("El valor aparece en todas las posiciones", new int[] {7, 7, 7, 7}, 7, 4);
        probar("Valores negativos", new int[] {-5, 3, -5, -5, 0}, -5, 3);
        probar("Vector vacío", new int[0], 1, 0);
        probar("Un solo elemento que coincide", new int[] {4}, 4, 1);
        probar("Contar el valor cero", new int[] {0, 1, 0}, 0, 2);

        probarError("Vector nulo", null, 1);

        mostrarTrazaDetallada(new int[] {10, 20, 10, 30, 10}, 10);
    }

    /**
     * Cuenta las apariciones de un valor recorriendo el vector completo, sin cortar.
     *
     * Complejidad: O(n) temporal (mejor = peor = promedio), O(1) espacial.
     *
     * @param vector arreglo donde contar
     * @param valor  valor cuyas apariciones se cuentan
     * @return cantidad de posiciones cuyo contenido es igual a valor (0 si no aparece)
     * @throws IllegalArgumentException si el vector es nulo
     */
    public static int contarOcurrencias(int[] vector, int valor) {
        if (vector == null) {
            throw new IllegalArgumentException("El vector no puede ser nulo.");
        }

        ultimasComparaciones = 0;
        int ocurrencias = 0;

        // Invariante: al terminar la iteración i, "ocurrencias" contiene la cantidad de
        // apariciones del valor en vector[0..i]. Sin return anticipado: una coincidencia
        // no decide el total, el total solo se conoce al agotar el vector.
        for (int i = 0; i < vector.length; i++) {
            ultimasComparaciones++;
            if (vector[i] == valor) {
                ocurrencias++;
            }
        }

        return ocurrencias;
    }

    /**
     * Informa cuántas comparaciones hizo el último conteo (siempre n).
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @return cantidad de comparaciones, igual a la longitud del vector recorrido
     */
    public static int getUltimasComparaciones() {
        return ultimasComparaciones;
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
     * @param descripcion nombre del caso
     * @param vector      entrada del caso
     * @param valor       valor a contar
     * @param esperado    cantidad esperada de apariciones
     */
    public static void probar(String descripcion, int[] vector, int valor, int esperado) {
        int obtenido = contarOcurrencias(vector, valor);
        System.out.println("\n- " + descripcion);
        System.out.println("  Vector: " + vectorATexto(vector) + " | Valor buscado: " + valor);
        System.out.println("  Esperado: " + esperado + " | Obtenido: " + obtenido
                + " | Comparaciones: " + getUltimasComparaciones() + " (= n)"
                + " -> " + (obtenido == esperado ? "OK" : "FALLA"));
    }

    /**
     * Ejecuta un caso de prueba que debe lanzar excepción y muestra el mensaje capturado.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @param descripcion nombre del caso
     * @param vector      entrada inválida
     * @param valor       valor a contar
     */
    public static void probarError(String descripcion, int[] vector, int valor) {
        System.out.println("\n- " + descripcion);
        System.out.println("  Vector: " + vectorATexto(vector) + " | Valor buscado: " + valor);
        try {
            int obtenido = contarOcurrencias(vector, valor);
            System.out.println("  Esperado: IllegalArgumentException | Obtenido: " + obtenido + " -> FALLA");
        } catch (IllegalArgumentException e) {
            System.out.println("  Esperado: IllegalArgumentException | Obtenido: "
                    + e.getClass().getSimpleName() + ": " + e.getMessage() + " -> OK");
        }
    }

    /**
     * Imprime la evolución del contador iteración por iteración.
     *
     * Complejidad: O(n) temporal, O(1) espacial.
     *
     * @param vector arreglo a trazar
     * @param valor  valor a contar
     * @throws IllegalArgumentException si el vector es nulo
     */
    public static void mostrarTrazaDetallada(int[] vector, int valor) {
        if (vector == null) {
            throw new IllegalArgumentException("El vector no puede ser nulo.");
        }
        System.out.println("\n--- Traza de " + vectorATexto(vector) + " contando el " + valor + " ---");
        System.out.printf("  %3s | %9s | %-11s | %s%n", "i", "vector[i]", "¿coincide?", "ocurrencias");
        int ocurrencias = 0;
        for (int i = 0; i < vector.length; i++) {
            boolean coincide = vector[i] == valor;
            if (coincide) {
                ocurrencias++;
            }
            System.out.printf("  %3d | %9d | %-11s | %d%n", i, vector[i], coincide, ocurrencias);
        }
        System.out.println("  Total: " + ocurrencias + " ocurrencias tras " + vector.length
                + " comparaciones; el ciclo nunca cortó antes de i = n - 1.");
    }
}


