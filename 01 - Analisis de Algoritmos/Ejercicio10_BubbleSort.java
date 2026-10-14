import java.util.Random;

/**
 * Ejercicio 10 del Práctico 1 (Análisis de Algoritmos): implementa Bubble Sort con
 * optimización de corte anticipado, contabilizando comparaciones e intercambios y
 * mostrando el estado del vector después de cada pasada. No se usa Arrays.sort.
 *
 * <p><b>Razonamiento del algoritmo:</b> en cada pasada se comparan elementos VECINOS y se
 * intercambian si están desordenados. El efecto es que el mayor elemento de la porción
 * todavía desordenada "burbujea" hacia la derecha: cada vez que se lo compara con su vecino
 * gana el intercambio y avanza una posición, hasta quedar al final del tramo. Por eso,
 * después de k pasadas los últimos k elementos ya están en su posición definitiva, y el
 * límite del ciclo interno se puede reducir en uno por pasada: volver a mirar esa cola
 * ordenada sería trabajo inútil. Ese es el invariante del algoritmo.</p>
 *
 * <p><b>¿Por qué solo es adecuado para fines didácticos o conjuntos pequeños?</b> Porque es
 * cuadrático: las comparaciones del peor caso son n(n-1)/2 contra las ~n*log2(n) de Merge
 * Sort o Quick Sort.</p>
 * <pre>
 *        n | Bubble Sort n(n-1)/2 | O(n log n) ~ n*log2(n) | Bubble es peor por un factor de
 * ---------+----------------------+------------------------+--------------------------------
 *       10 |                   45 |                     33 | 1,4x
 *     1000 |              499.500 |                  9.966 | 50x
 *   100000 |        4.999.950.000 |              1.660.964 | 3.010x
 * </pre>
 * <p>Con n = 100.000 la diferencia pasa de milisegundos a horas. Se enseña porque su
 * mecánica es visualmente evidente y el invariante se explica en una frase, no porque
 * sirva en producción.</p>
 *
 * <p><b>Definiciones de conteo:</b> una COMPARACIÓN es cada evaluación
 * vector[j] &gt; vector[j+1]; un INTERCAMBIO es cada permuta efectiva de dos posiciones
 * (tres asignaciones con variable temporal).</p>
 *
 * <p><b>Mejor, peor y promedio:</b> el mejor caso es el vector ya ordenado; SIN la bandera
 * de corte anticipado seguiría siendo O(n^2) porque haría todas las pasadas igual, y CON la
 * bandera baja a O(n) (una sola pasada de n-1 comparaciones y 0 intercambios): la
 * optimización cambia el mejor caso, no el peor. El peor caso es el vector ordenado al revés:
 * n(n-1)/2 comparaciones y n(n-1)/2 intercambios. El promedio, con datos al azar, es también
 * O(n^2), con bastantes menos intercambios que el peor caso (en la prueba con
 * semilla 42 y n = 8: 19 intercambios contra los 28 del peor caso).</p>
 *
 * <p><b>Complejidad espacial:</b> O(1). Es un ordenamiento IN SITU: reordena el propio
 * arreglo usando solo una variable temporal, sin asignar otro arreglo proporcional a n.</p>
 *
 * Complejidad: O(n) mejor caso (con corte anticipado), O(n^2) peor caso y promedio; O(1) espacial.
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio10_BubbleSort {

    /** Semilla fija para que el vector "al azar" sea reproducible entre corridas. */
    private static final long SEMILLA = 42L;

    /**
     * Método principal: ordena tres vectores característicos y compara sus métricas.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 10 - Bubble Sort con corte anticipado ===");

        int[] desordenado = generarAlAzar(8, 100);
        int[] yaOrdenado = {1, 2, 3, 4, 5, 6, 7, 8};
        int[] alReves = {8, 7, 6, 5, 4, 3, 2, 1};

        Metricas mDesordenado = probar("Desordenado al azar (semilla " + SEMILLA + ")", desordenado);
        Metricas mOrdenado = probar("Ya ordenado (mejor caso)", yaOrdenado);
        Metricas mReves = probar("Ordenado al revés (peor caso)", alReves);

        probar("Traza del análisis", new int[] {5, 1, 4, 2});
        probar("Vector de un elemento", new int[] {9});
        probar("Vector vacío", new int[0]);
        probar("Con duplicados y negativos", new int[] {3, -1, 3, 0, -1});

        probarError("Vector nulo");

        mostrarTablaComparativa(mDesordenado, mOrdenado, mReves, 8);
        mostrarCostoDidactico();
    }

    /**
     * Ordena el vector de menor a mayor con Bubble Sort y devuelve sus métricas.
     *
     * <p>El arreglo recibido queda modificado (ordenamiento in situ). Se imprime el estado
     * del vector al final de cada pasada para que el proceso sea observable.</p>
     *
     * Complejidad: O(n) mejor caso con corte anticipado, O(n^2) peor caso y promedio; O(1) espacial.
     *
     * @param vector arreglo a ordenar en el lugar
     * @return métricas de comparaciones, intercambios, pasadas y corte anticipado
     * @throws IllegalArgumentException si el vector es nulo
     */
    public static Metricas ordenar(int[] vector) {
        if (vector == null) {
            throw new IllegalArgumentException("El vector no puede ser nulo.");
        }

        long comparaciones = 0;
        long intercambios = 0;
        int pasadasRealizadas = 0;
        int pasadaDeCorte = 0;
        int n = vector.length;

        for (int pasada = 1; pasada <= n - 1; pasada++) {
            pasadasRealizadas = pasada;

            // Bandera de corte anticipado: si una pasada completa no necesitó ningún
            // intercambio, significa que todo par de vecinos ya está en orden, y eso
            // equivale a que el vector entero está ordenado. Seguir sería trabajo inútil.
            boolean huboIntercambio = false;

            // El límite se reduce en "pasada" porque los últimos elementos ya burbujearon
            // a su posición definitiva: revisarlos otra vez no puede cambiar nada.
            for (int j = 0; j < n - pasada; j++) {
                comparaciones++;
                if (vector[j] > vector[j + 1]) {
                    int temporal = vector[j];
                    vector[j] = vector[j + 1];
                    vector[j + 1] = temporal;
                    intercambios++;
                    huboIntercambio = true;
                }
            }

            System.out.println("    pasada " + pasada + ": " + vectorATexto(vector)
                    + " | cola ordenada: últimos " + pasada);

            if (!huboIntercambio) {
                pasadaDeCorte = pasada;
                break;
            }
        }

        return new Metricas(comparaciones, intercambios, pasadasRealizadas, pasadaDeCorte);
    }

    /**
     * Genera un vector pseudoaleatorio reproducible (semilla fija).
     *
     * Complejidad: O(n) temporal, O(n) espacial.
     *
     * @param n     cantidad de elementos
     * @param techo valor máximo exclusivo de cada elemento
     * @return arreglo de n enteros en el rango [0, techo)
     * @throws IllegalArgumentException si n es negativo o techo no es positivo
     */
    public static int[] generarAlAzar(int n, int techo) {
        if (n < 0 || techo <= 0) {
            throw new IllegalArgumentException("n debe ser no negativo y techo debe ser positivo.");
        }
        Random generador = new Random(SEMILLA);
        int[] vector = new int[n];
        for (int i = 0; i < n; i++) {
            vector[i] = generador.nextInt(techo);
        }
        return vector;
    }

    /**
     * Verifica que el vector quede ordenado de menor a mayor, sin usar Arrays.sort.
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
     * Ordena un caso de prueba imprimiendo estado inicial, pasadas, resultado y métricas.
     *
     * Complejidad: O(n^2) temporal, O(n) espacial por los textos impresos.
     *
     * @param descripcion nombre del caso
     * @param vector      entrada del caso; queda ordenada
     * @return métricas obtenidas, para la tabla comparativa
     */
    public static Metricas probar(String descripcion, int[] vector) {
        System.out.println("\n- " + descripcion);
        System.out.println("  Antes:  " + vectorATexto(vector));
        Metricas metricas = ordenar(vector);
        System.out.println("  Después: " + vectorATexto(vector));
        System.out.println("  Comparaciones: " + metricas.comparaciones
                + " | Intercambios: " + metricas.intercambios
                + " | Pasadas: " + metricas.pasadasRealizadas
                + " | " + metricas.describirCorte());
        System.out.println("  ¿Quedó ordenado? " + estaOrdenado(vector)
                + " -> " + (estaOrdenado(vector) ? "OK" : "FALLA"));
        return metricas;
    }

    /**
     * Ejecuta el caso de vector nulo y muestra el mensaje capturado.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @param descripcion nombre del caso
     */
    public static void probarError(String descripcion) {
        System.out.println("\n- " + descripcion);
        try {
            ordenar(null);
            System.out.println("  Esperado: IllegalArgumentException | Obtenido: sin excepción -> FALLA");
        } catch (IllegalArgumentException e) {
            System.out.println("  Esperado: IllegalArgumentException | Obtenido: "
                    + e.getClass().getSimpleName() + ": " + e.getMessage() + " -> OK");
        }
    }

    /**
     * Imprime la tabla comparativa de los tres vectores característicos.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @param desordenado métricas del vector al azar
     * @param ordenado    métricas del vector ya ordenado
     * @param alReves     métricas del vector ordenado al revés
     * @param n           longitud común de los tres vectores
     */
    public static void mostrarTablaComparativa(Metricas desordenado, Metricas ordenado,
                                               Metricas alReves, int n) {
        long maximoTeorico = (long) n * (n - 1) / 2;
        System.out.println("\n--- Tabla comparativa con n = " + n + " ---");
        System.out.printf("  %-26s | %13s | %13s | %8s | %s%n",
                "Vector", "Comparaciones", "Intercambios", "Pasadas", "Corte");
        System.out.printf("  %-26s | %13d | %13d | %8d | %s%n", "Desordenado al azar",
                desordenado.comparaciones, desordenado.intercambios,
                desordenado.pasadasRealizadas, desordenado.describirCorte());
        System.out.printf("  %-26s | %13d | %13d | %8d | %s%n", "Ya ordenado (mejor)",
                ordenado.comparaciones, ordenado.intercambios,
                ordenado.pasadasRealizadas, ordenado.describirCorte());
        System.out.printf("  %-26s | %13d | %13d | %8d | %s%n", "Al revés (peor)",
                alReves.comparaciones, alReves.intercambios,
                alReves.pasadasRealizadas, alReves.describirCorte());
        System.out.println("  Máximo teórico n(n-1)/2 = " + maximoTeorico
                + " -> el peor caso lo alcanza en comparaciones e intercambios: "
                + (alReves.comparaciones == maximoTeorico && alReves.intercambios == maximoTeorico));
        System.out.println("  Mejor caso: n - 1 = " + (n - 1) + " comparaciones y 0 intercambios: "
                + (ordenado.comparaciones == n - 1 && ordenado.intercambios == 0));
    }

    /**
     * Muestra con números por qué el costo cuadrático lo vuelve solo didáctico.
     *
     * Complejidad: O(k) temporal con k = cantidad de tamaños mostrados, O(1) espacial.
     */
    public static void mostrarCostoDidactico() {
        int[] tamanios = {10, 1000, 100000};
        System.out.println("\n--- Bubble Sort contra O(n log n) ---");
        System.out.printf("  %8s | %20s | %20s | %s%n", "n", "n(n-1)/2", "n*log2(n)", "factor");
        for (int n : tamanios) {
            long cuadratico = (long) n * (n - 1) / 2;
            long logaritmico = (long) (n * (Math.log(n) / Math.log(2)));
            System.out.printf("  %8d | %20d | %20d | %.1fx%n",
                    n, cuadratico, logaritmico, cuadratico / (double) logaritmico);
        }
    }

    /**
     * Métricas inmutables de una corrida de Bubble Sort.
     */
    static final class Metricas {

        /** Comparaciones vector[j] > vector[j+1] evaluadas. */
        final long comparaciones;

        /** Intercambios efectivos de dos posiciones. */
        final long intercambios;

        /** Pasadas del ciclo externo que se ejecutaron. */
        final int pasadasRealizadas;

        /** Pasada en la que la bandera cortó el ciclo, o 0 si no hubo corte anticipado. */
        final int pasadaDeCorte;

        /**
         * Crea las métricas de una corrida.
         *
         * @param comparaciones     comparaciones evaluadas
         * @param intercambios      intercambios realizados
         * @param pasadasRealizadas pasadas ejecutadas
         * @param pasadaDeCorte     pasada del corte anticipado, o 0 si no hubo
         */
        Metricas(long comparaciones, long intercambios, int pasadasRealizadas, int pasadaDeCorte) {
            this.comparaciones = comparaciones;
            this.intercambios = intercambios;
            this.pasadasRealizadas = pasadasRealizadas;
            this.pasadaDeCorte = pasadaDeCorte;
        }

        /**
         * Describe en texto si hubo corte anticipado y en qué pasada.
         *
         * @return descripción legible del corte
         */
        String describirCorte() {
            if (pasadaDeCorte > 0) {
                return "cortó en la pasada " + pasadaDeCorte;
            }
            return "sin corte anticipado";
        }
    }
}


