/**
 * Ejercicio 01 - Ordenamiento Burbuja paso a paso.
 *
 * <p>Ordena un arreglo de enteros con Bubble Sort mostrando el arreglo original,
 * el estado del arreglo despues de cada pasada (numerada) y el arreglo final.</p>
 *
 * <p><b>Razonamiento del algoritmo:</b> Bubble Sort recorre el arreglo comparando
 * cada elemento con su VECINO inmediato de la derecha y los intercambia si estan
 * desordenados. Como el recorrido avanza de izquierda a derecha, el valor mas grande
 * que se encuentra "viaja" arrastrado de intercambio en intercambio hasta el final
 * del tramo recorrido: por eso se dice que el mayor "burbujea" hacia el final.
 * Terminada la pasada k, los ultimos k elementos ya estan en su posicion definitiva,
 * y el ciclo interno puede acortarse en k posiciones.</p>
 *
 * <p><b>Definicion de los contadores</b> (para que los numeros sean comparables con
 * los de otros algoritmos del practico):</p>
 * <ul>
 *   <li><i>comparacion</i>: cada evaluacion de la condicion arreglo[j] &gt; arreglo[j+1].</li>
 *   <li><i>intercambio</i>: cada permuta efectiva de dos posiciones del arreglo
 *       (equivale a tres asignaciones usando una variable temporal).</li>
 *   <li><i>pasada</i>: cada ejecucion completa del ciclo interno.</li>
 * </ul>
 *
 * <p>Complejidad: O(n) en el mejor caso (arreglo ya ordenado, corte temprano),
 * O(n^2) en el peor y en el caso promedio; O(1) espacial adicional.</p>
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio01_BurbujaPasoAPaso {

    /**
     * Metodo principal. Ejecuta el caso nominal con traza completa y luego una
     * bateria de casos limite (vacio, un elemento, ya ordenado, invertido,
     * con repetidos y nulo).
     *
     * @param args argumentos de la linea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== EJERCICIO 01 - BUBBLE SORT PASO A PASO ===");

        int[] numeros = {5, 2, 9, 1, 7, 6};
        ejecutarConTraza("Caso nominal", numeros);

        // Arreglo de la traza documentada en el analisis (.trabajo/analisis/03-ej01.md).
        ejecutarConTraza("Arreglo de la traza del analisis", new int[] {5, 2, 9, 1, 7});

        System.out.println("\n=== CASOS LIMITE ===");
        ejecutarResumen("Arreglo vacio", new int[] {});
        ejecutarResumen("Un solo elemento", new int[] {42});
        ejecutarResumen("Ya ordenado (mejor caso)", new int[] {1, 2, 3, 4, 5, 6});
        ejecutarResumen("Ordenado al reves (peor caso)", new int[] {6, 5, 4, 3, 2, 1});
        ejecutarResumen("Con elementos repetidos", new int[] {4, 1, 4, 2, 1, 4});
        ejecutarResumen("Negativos y cero", new int[] {0, -3, 7, -10, 3});

        System.out.println("\n--- Arreglo nulo (debe lanzar excepcion) ---");
        try {
            burbujaConTraza(null, new MetricasBurbuja(), false);
            System.out.println("ERROR: no se lanzo la excepcion esperada.");
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException capturada: " + e.getMessage());
        }

        System.out.println("\nVerificacion teorica del peor caso con n = 6:");
        MetricasBurbuja peor = new MetricasBurbuja();
        burbujaConTraza(new int[] {6, 5, 4, 3, 2, 1}, peor, false);
        int n = 6;
        System.out.println("  comparaciones observadas = " + peor.comparaciones
                + " | n(n-1)/2 = " + (n * (n - 1) / 2));
        System.out.println("  intercambios observados  = " + peor.intercambios
                + " | n(n-1)/2 = " + (n * (n - 1) / 2));
    }

    /**
     * Ordena una copia del arreglo imprimiendo la traza pasada por pasada y el
     * resultado de la verificacion independiente.
     *
     * @param titulo rotulo del escenario que se esta ejecutando
     * @param original arreglo de entrada; no se modifica
     * @throws IllegalArgumentException si {@code original} es nulo
     */
    public static void ejecutarConTraza(String titulo, int[] original) {
        if (original == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        System.out.println("\n--- " + titulo + " ---");
        int[] copia = copiar(original);
        System.out.println("Arreglo original: " + aTexto(copia));
        MetricasBurbuja m = new MetricasBurbuja();
        burbujaConTraza(copia, m, true);
        System.out.println("Arreglo final:    " + aTexto(copia));
        System.out.println("Pasadas: " + m.pasadas
                + " | comparaciones: " + m.comparaciones
                + " | intercambios: " + m.intercambios);
        System.out.println("Verificacion estaOrdenado(): " + estaOrdenado(copia));
    }

    /**
     * Ordena una copia del arreglo sin imprimir la traza interna y muestra un resumen.
     *
     * @param titulo rotulo del escenario que se esta ejecutando
     * @param original arreglo de entrada; no se modifica
     * @throws IllegalArgumentException si {@code original} es nulo
     */
    public static void ejecutarResumen(String titulo, int[] original) {
        if (original == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        int[] copia = copiar(original);
        MetricasBurbuja m = new MetricasBurbuja();
        burbujaConTraza(copia, m, false);
        System.out.println(titulo + ": " + aTexto(original) + " -> " + aTexto(copia)
                + " | pasadas=" + m.pasadas
                + " comparaciones=" + m.comparaciones
                + " intercambios=" + m.intercambios
                + " | ordenado=" + estaOrdenado(copia));
    }

    /**
     * Ordena el arreglo en forma ascendente usando Bubble Sort con corte temprano.
     *
     * @param arreglo arreglo a ordenar in situ
     * @param metricas acumulador de comparaciones, intercambios y pasadas
     * @param mostrarTraza si es {@code true} imprime el estado del arreglo despues de cada pasada
     * @throws IllegalArgumentException si {@code arreglo} o {@code metricas} son nulos
     */
    public static void burbujaConTraza(int[] arreglo, MetricasBurbuja metricas, boolean mostrarTraza) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        if (metricas == null) {
            throw new IllegalArgumentException("El acumulador de metricas no puede ser nulo.");
        }

        int n = arreglo.length;
        for (int pasada = 1; pasada < n; pasada++) {
            boolean huboIntercambio = false;

            // El limite es n - pasada porque las ultimas (pasada-1) posiciones ya
            // contienen los mayores en su lugar definitivo: volver a compararlas
            // seria trabajo inutil.
            for (int j = 0; j < n - pasada; j++) {
                metricas.comparaciones++;
                if (arreglo[j] > arreglo[j + 1]) {
                    intercambiar(arreglo, j, j + 1);
                    metricas.intercambios++;
                    // El mayor de los dos queda en j+1, asi que la siguiente iteracion
                    // lo vuelve a comparar: ese arrastre es lo que lo "burbujea" al final.
                    huboIntercambio = true;
                }
            }

            metricas.pasadas++;
            if (mostrarTraza) {
                System.out.println("  Pasada " + metricas.pasadas + ": " + aTexto(arreglo)
                        + "  (ultimas " + metricas.pasadas + " posiciones ya fijas)");
            }

            // Si una pasada completa no intercambio nada, no quedan pares desordenados:
            // este corte es lo que da el mejor caso O(n) con entrada ya ordenada.
            if (!huboIntercambio) {
                if (mostrarTraza) {
                    System.out.println("  Corte temprano: la pasada no hizo intercambios.");
                }
                break;
            }
        }
    }

    /**
     * Verifica que el arreglo este ordenado de forma ascendente. Es una comprobacion
     * independiente del algoritmo de ordenamiento: solo recorre el resultado.
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
 * Acumulador mutable de metricas de ejecucion. No es publica porque solo sirve
 * de soporte interno del ejercicio.
 */
class MetricasBurbuja {
    long comparaciones;
    long intercambios;
    int pasadas;
}


