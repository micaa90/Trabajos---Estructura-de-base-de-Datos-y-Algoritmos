/**
 * Ejercicio 03 - Insertion Sort sobre un arreglo casi ordenado.
 *
 * <p>Ordena {@code int[] numeros = {1, 2, 3, 5, 4, 6, 7}} con Insertion Sort, mostrando
 * cuantos DESPLAZAMIENTOS y comparaciones realiza y el estado del arreglo despues de
 * insertar cada elemento. Para contraste ordena tambien {@code {7, 6, 5, 4, 3, 2, 1}}.</p>
 *
 * <p><b>Razonamiento del algoritmo.</b> Insertion Sort mantiene el invariante de que el
 * prefijo arreglo[0..i-1] siempre esta ordenado. En cada iteracion toma la "clave"
 * arreglo[i] y la inserta en el lugar que le corresponde dentro de ese prefijo,
 * corriendo hacia la derecha solo los elementos mayores que ella. Es la forma en que se
 * ordenan cartas en la mano.</p>
 *
 * <p><b>Por que funciona bien con datos casi ordenados.</b> El ciclo interno es un
 * {@code while} que se detiene APENAS encuentra un elemento menor o igual a la clave.
 * En un arreglo casi ordenado, cada clave ya suele ser mayor que su vecino izquierdo, asi
 * que el while evalua una sola comparacion y no ejecuta ningun desplazamiento. Con n-1
 * claves y trabajo constante por clave, el mejor caso es O(n) y no O(n^2).</p>
 *
 * <p><b>Definicion de los contadores.</b></p>
 * <ul>
 *   <li><i>comparacion</i>: cada evaluacion de la condicion {@code arreglo[j] > clave},
 *       incluida la que hace fallar el while y corta el ciclo.</li>
 *   <li><i>desplazamiento</i>: cada copia {@code arreglo[j + 1] = arreglo[j]}, es decir
 *       UNA sola escritura que corre un elemento un lugar a la derecha. Se diferencia del
 *       intercambio de burbuja o seleccion, que permuta dos elementos y cuesta tres
 *       asignaciones. La escritura final de la clave en su hueco no se cuenta como
 *       desplazamiento porque no corre a nadie.</li>
 * </ul>
 *
 * <p><b>Estabilidad.</b> El while usa {@code >} estricto, por lo que una clave nunca pasa
 * por delante de un elemento igual: los elementos con la misma magnitud conservan su
 * orden relativo de entrada. Eso es lo que significa que el algoritmo sea estable.</p>
 *
 * <p>Complejidad: O(n) en el mejor caso (entrada ya ordenada), O(n^2) en el peor
 * (entrada invertida) y O(n^2) en promedio; O(1) espacial adicional (ordena in situ).</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio03_InsercionCasiOrdenado {

    /**
     * Metodo principal. Ejecuta el caso casi ordenado del enunciado, el contraste con el
     * arreglo invertido y los casos limite.
     *
     * @param args argumentos de la linea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== EJERCICIO 03 - INSERTION SORT CON ARREGLO CASI ORDENADO ===");

        int[] numeros = {1, 2, 3, 5, 4, 6, 7};
        MetricasInsercion casiOrdenado = ejecutarConTraza("Casi ordenado (enunciado)", numeros);

        int[] invertido = {7, 6, 5, 4, 3, 2, 1};
        MetricasInsercion peorCaso = ejecutarConTraza("Ordenado al reves (contraste)", invertido);

        System.out.println("\n=== CONTRASTE DE DESPLAZAMIENTOS ===");
        System.out.println("+-------------------------------+---------------+-------------------+");
        System.out.println("| Entrada                       | Comparaciones | Desplazamientos   |");
        System.out.println("+-------------------------------+---------------+-------------------+");
        System.out.printf("| %-29s | %13d | %17d |%n",
                "{1,2,3,5,4,6,7} casi ordenado", casiOrdenado.comparaciones, casiOrdenado.desplazamientos);
        System.out.printf("| %-29s | %13d | %17d |%n",
                "{7,6,5,4,3,2,1} invertido", peorCaso.comparaciones, peorCaso.desplazamientos);
        System.out.println("+-------------------------------+---------------+-------------------+");
        int n = 7;
        System.out.println("Cota teorica del peor caso n(n-1)/2 con n = " + n + ": " + (n * (n - 1) / 2));

        System.out.println("\n=== CASOS LIMITE ===");
        ejecutarResumen("Arreglo vacio", new int[] {});
        ejecutarResumen("Un solo elemento", new int[] {9});
        ejecutarResumen("Ya ordenado (mejor caso)", new int[] {1, 2, 3, 4, 5, 6, 7});
        ejecutarResumen("Con repetidos", new int[] {2, 1, 2, 1, 3});
        ejecutarResumen("Negativos", new int[] {-1, -5, 3, 0});

        System.out.println("\n--- Arreglo nulo (debe lanzar excepcion) ---");
        try {
            insertionSort(null, new MetricasInsercion(), false);
            System.out.println("ERROR: no se lanzo la excepcion esperada.");
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException capturada: " + e.getMessage());
        }
    }

    /**
     * Ordena una copia del arreglo imprimiendo la traza de cada insercion.
     *
     * @param titulo rotulo del escenario
     * @param original arreglo de entrada; no se modifica
     * @return metricas acumuladas de la corrida
     * @throws IllegalArgumentException si {@code original} es nulo
     */
    public static MetricasInsercion ejecutarConTraza(String titulo, int[] original) {
        if (original == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        System.out.println("\n--- " + titulo + " ---");
        int[] copia = copiar(original);
        System.out.println("Arreglo original: " + aTexto(copia));
        MetricasInsercion m = new MetricasInsercion();
        insertionSort(copia, m, true);
        System.out.println("Arreglo final:    " + aTexto(copia));
        System.out.println("Comparaciones: " + m.comparaciones
                + " | Desplazamientos: " + m.desplazamientos);
        System.out.println("Verificacion estaOrdenado(): " + estaOrdenado(copia));
        return m;
    }

    /**
     * Ordena una copia del arreglo y muestra solo el resumen.
     *
     * @param titulo rotulo del escenario
     * @param original arreglo de entrada; no se modifica
     * @throws IllegalArgumentException si {@code original} es nulo
     */
    public static void ejecutarResumen(String titulo, int[] original) {
        if (original == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        int[] copia = copiar(original);
        MetricasInsercion m = new MetricasInsercion();
        insertionSort(copia, m, false);
        System.out.println(titulo + ": " + aTexto(original) + " -> " + aTexto(copia)
                + " | comparaciones=" + m.comparaciones
                + " desplazamientos=" + m.desplazamientos
                + " | ordenado=" + estaOrdenado(copia));
    }

    /**
     * Ordena el arreglo en forma ascendente con Insertion Sort.
     *
     * @param arreglo arreglo a ordenar in situ
     * @param metricas acumulador de comparaciones y desplazamientos
     * @param mostrarTraza si es {@code true} imprime el estado del arreglo tras cada insercion
     * @throws IllegalArgumentException si {@code arreglo} o {@code metricas} son nulos
     */
    public static void insertionSort(int[] arreglo, MetricasInsercion metricas, boolean mostrarTraza) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        if (metricas == null) {
            throw new IllegalArgumentException("El acumulador de metricas no puede ser nulo.");
        }

        // Invariante: al empezar la iteracion i, el prefijo arreglo[0..i-1] ya esta ordenado.
        // El elemento 0 solo cumple el invariante trivialmente, por eso i arranca en 1.
        for (int i = 1; i < arreglo.length; i++) {
            int clave = arreglo[i];
            int j = i - 1;
            long desplazamientosDeEstaClave = 0;

            // El while corta APENAS arreglo[j] <= clave. En un arreglo casi ordenado eso
            // pasa en la primera evaluacion, de modo que el cuerpo no se ejecuta nunca:
            // de ahi sale el mejor caso O(n).
            while (j >= 0) {
                metricas.comparaciones++;
                if (arreglo[j] > clave) {
                    arreglo[j + 1] = arreglo[j]; // desplazamiento: una sola escritura
                    metricas.desplazamientos++;
                    desplazamientosDeEstaClave++;
                    j--;
                } else {
                    break;
                }
            }

            // El hueco quedo en j+1 porque todos los mayores que la clave se corrieron
            // un lugar a la derecha.
            arreglo[j + 1] = clave;

            if (mostrarTraza) {
                System.out.println("  Inserto " + clave
                        + " (desplazamientos de esta clave: " + desplazamientosDeEstaClave
                        + ") -> " + aTexto(arreglo));
            }
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
 * Acumulador de comparaciones y desplazamientos de Insertion Sort.
 * No es publica: es soporte interno del ejercicio.
 */
class MetricasInsercion {
    long comparaciones;
    long desplazamientos;
}

/*
 * ===========================================================================
 * MATRIZ DE CASOS DE PRUEBA MANUALES
 * ===========================================================================
 * Compilacion: javac Ejercicio03_InsercionCasiOrdenado.java -d /tmp/v3
 * Ejecucion:   java -cp /tmp/v3 Ejercicio03_InsercionCasiOrdenado
 *
 * ID: CP-03-01  (flujo nominal)
 *   Objetivo: ordenar el arreglo casi ordenado del enunciado contando desplazamientos.
 *   Entrada exacta: int[] numeros = {1, 2, 3, 5, 4, 6, 7} (ya cargado en el main).
 *   Resultado esperado: final [1, 2, 3, 4, 5, 6, 7]; comparaciones=7; desplazamientos=1;
 *                       solo la insercion del 4 informa un desplazamiento.
 *   Criterio de aprobacion: estaOrdenado()=true y desplazamientos=1, muy por debajo
 *                           de la cota del peor caso (21).
 *
 * ID: CP-03-02  (mejor caso)
 *   Objetivo: comprobar el mejor caso O(n) con entrada ya ordenada.
 *   Entrada exacta: linea "Ya ordenado (mejor caso)" con {1, 2, 3, 4, 5, 6, 7}.
 *   Resultado esperado: comparaciones=6 (n-1) y desplazamientos=0, ordenado=true.
 *   Criterio de aprobacion: desplazamientos=0 y comparaciones exactamente n-1 = 6.
 *
 * ID: CP-03-03  (peor caso)
 *   Objetivo: validar la cota n(n-1)/2 con entrada invertida.
 *   Entrada exacta: escenario "Ordenado al reves (contraste)" con {7, 6, 5, 4, 3, 2, 1}.
 *   Resultado esperado: comparaciones=21 y desplazamientos=21; la linea de cota teorica
 *                       imprime 21.
 *   Criterio de aprobacion: desplazamientos observados == n(n-1)/2 == 21.
 *
 * ID: CP-03-04  (entrada vacia)
 *   Objetivo: comprobar que el arreglo vacio no rompe el programa.
 *   Entrada exacta: linea "Arreglo vacio" con new int[] {}.
 *   Resultado esperado: "[] -> [] | comparaciones=0 desplazamientos=0 | ordenado=true".
 *   Criterio de aprobacion: no se lanza excepcion y estaOrdenado() devuelve true.
 *
 * ID: CP-03-05  (un elemento)
 *   Objetivo: comprobar el caso minimo no vacio.
 *   Entrada exacta: linea "Un solo elemento" con new int[] {9}.
 *   Resultado esperado: "[9] -> [9] | comparaciones=0 desplazamientos=0 | ordenado=true".
 *   Criterio de aprobacion: el ciclo externo no se ejecuta y el arreglo queda intacto.
 * ===========================================================================
 */
