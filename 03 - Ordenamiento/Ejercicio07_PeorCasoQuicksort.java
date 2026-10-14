/**
 * Ejercicio 07 - Peor caso de Quicksort.
 *
 * <p>Aplica Quicksort con el PRIMER elemento como pivote sobre el arreglo YA ORDENADO
 * {@code int[] numeros = {1, 2, 3, 4, 5, 6, 7}}, contando las llamadas recursivas y las
 * comparaciones, e imprimiendo cada llamada con indentacion por profundidad para que se vea
 * la cadena lineal. Luego repite el ordenamiento del mismo arreglo eligiendo el pivote del
 * MEDIO y muestra la tabla comparativa.</p>
 *
 * <p><b>Por que un arreglo ya ordenado es el peor caso de esta variante.</b> Si el arreglo
 * esta ordenado, el primer elemento del subarreglo es siempre el MINIMO. Al particionar, no
 * hay ningun elemento menor que el pivote: el lado izquierdo (menores) queda VACIO y el
 * derecho (mayores) se queda con los n-1 elementos restantes. La recursion no se bifurca en
 * dos mitades parecidas, sino que se convierte en una CADENA: cada nivel reduce el problema
 * en un solo elemento. El arbol de recursion pasa de altura log n a altura n.</p>
 *
 * <p><b>Cuenta de comparaciones.</b> El nivel 1 compara n-1 elementos contra el pivote, el
 * nivel 2 compara n-2, y asi hasta 1:
 * (n-1) + (n-2) + ... + 2 + 1 = n(n-1)/2. Para n = 7 eso da 7*6/2 = 21 comparaciones,
 * O(n^2), en lugar de las ~n log n del caso promedio.</p>
 *
 * <p><b>Prediccion verificable para n = 7:</b> 6 llamadas que particionan (una por cada
 * subarreglo de tamano 7, 6, 5, 4, 3 y 2), cada una generando 2 llamadas hijas, mas la
 * llamada inicial: 13 invocaciones totales, es decir 12 llamadas recursivas, de las cuales
 * 7 cortan por caso base. Profundidad maxima 6.</p>
 *
 * <p><b>Consumo de pila.</b> Cada nivel de recursion deja un marco (frame) vivo en el Stack
 * con sus parametros y variables locales. Con altura n, la pila crece O(n) en lugar de
 * O(log n), y con arreglos grandes ordenados eso produce {@code StackOverflowError}. El
 * programa incluye una demostracion controlada de ese limite.</p>
 *
 * <p><b>Estrategias que evitan el problema:</b> pivote del MEDIO (acierta exactamente en
 * entradas ya ordenadas, que son las mas frecuentes en la practica), pivote ALEATORIO
 * (ninguna entrada fija puede ser sistematicamente el peor caso, el peor caso pasa a ser
 * improbable) y MEDIANA DE TRES (toma primero, medio y ultimo y usa la mediana, lo que
 * aproxima mejor el valor central y es barato de calcular).</p>
 *
 * <p><b>Definicion de los contadores:</b> igual que en el ejercicio 06. <i>comparacion</i>
 * es cada evaluacion de {@code arreglo[j] < pivote}; <i>invocaciones</i> son todas las
 * entradas al metodo recursivo incluida la inicial; <i>llamadas recursivas</i> son
 * invocaciones - 1; <i>particiones</i> son las llamadas que no cortaron por caso base.</p>
 *
 * <p>Complejidad: O(n^2) temporal en este escenario con pivote inicial; O(n) de pila de
 * recursion. Con pivote del medio sobre la misma entrada: O(n log n) temporal y O(log n) de pila.</p>
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio07_PeorCasoQuicksort {

    /** Estrategia de pivote: siempre el primer elemento del subarreglo. */
    private static final int PIVOTE_PRIMERO = 0;

    /** Estrategia de pivote: el elemento del medio del subarreglo. */
    private static final int PIVOTE_MEDIO = 1;

    /**
     * Metodo principal. Ejecuta el peor caso con pivote inicial, la misma entrada con pivote
     * del medio, la tabla comparativa, los casos limite y la demostracion de StackOverflowError.
     *
     * @param args argumentos de la linea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== EJERCICIO 07 - PEOR CASO DE QUICKSORT ===");

        int[] numeros = {1, 2, 3, 4, 5, 6, 7};
        int n = numeros.length;

        MetricasPeorCaso conPrimero = ejecutarConTraza(
                "Pivote = primer elemento sobre arreglo YA ORDENADO (peor caso)",
                numeros, PIVOTE_PRIMERO);

        MetricasPeorCaso conMedio = ejecutarConTraza(
                "Pivote = elemento del medio sobre el MISMO arreglo",
                numeros, PIVOTE_MEDIO);

        System.out.println("\n=== TABLA COMPARATIVA (entrada {1,2,3,4,5,6,7}) ===");
        System.out.println("+---------------------+--------------+-------------------+---------------+--------------+");
        System.out.println("| Estrategia pivote   | Invocaciones | Llamadas recurs.  | Comparaciones | Profundidad  |");
        System.out.println("+---------------------+--------------+-------------------+---------------+--------------+");
        System.out.printf("| %-19s | %12d | %17d | %13d | %12d |%n", "Primer elemento",
                conPrimero.invocaciones, conPrimero.invocaciones - 1,
                conPrimero.comparaciones, conPrimero.profundidadMaxima);
        System.out.printf("| %-19s | %12d | %17d | %13d | %12d |%n", "Elemento del medio",
                conMedio.invocaciones, conMedio.invocaciones - 1,
                conMedio.comparaciones, conMedio.profundidadMaxima);
        System.out.println("+---------------------+--------------+-------------------+---------------+--------------+");

        System.out.println("\nContraste con la prediccion teorica (n = " + n + "):");
        System.out.println("  Comparaciones esperadas con pivote inicial: n(n-1)/2 = " + (n * (n - 1) / 2)
                + " | observadas: " + conPrimero.comparaciones);
        System.out.println("  Particiones esperadas con pivote inicial: n-1 = " + (n - 1)
                + " | observadas: " + conPrimero.particiones);
        System.out.println("  Invocaciones esperadas con pivote inicial: 2*(n-1)+1 = " + (2 * (n - 1) + 1)
                + " | observadas: " + conPrimero.invocaciones);
        System.out.println("  Profundidad esperada con pivote inicial: n-1 = " + (n - 1)
                + " | observada: " + conPrimero.profundidadMaxima);

        System.out.println("\n=== CASOS LIMITE ===");
        resumen("Arreglo vacio", new int[] {}, PIVOTE_PRIMERO);
        resumen("Un solo elemento", new int[] {5}, PIVOTE_PRIMERO);
        resumen("Dos elementos ya ordenados", new int[] {1, 2}, PIVOTE_PRIMERO);
        resumen("Ordenado al reves", new int[] {7, 6, 5, 4, 3, 2, 1}, PIVOTE_PRIMERO);
        resumen("Con repetidos", new int[] {2, 2, 2, 2}, PIVOTE_PRIMERO);
        resumen("Desordenado (caso promedio)", new int[] {4, 1, 7, 3, 6, 2, 5}, PIVOTE_PRIMERO);

        System.out.println("\n--- Arreglo nulo (debe lanzar excepcion) ---");
        try {
            quicksort(null, new MetricasPeorCaso(), PIVOTE_PRIMERO);
            System.out.println("ERROR: no se lanzo la excepcion esperada.");
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException capturada: " + e.getMessage());
        }

        demostrarDesbordeDePila(60000);
    }

    /**
     * Ordena una copia del arreglo con la estrategia de pivote indicada, imprimiendo la
     * traza indentada por profundidad.
     *
     * @param titulo rotulo del escenario
     * @param original arreglo de entrada; no se modifica
     * @param estrategiaPivote {@code 0} para primer elemento, {@code 1} para elemento del medio
     * @return metricas acumuladas de la corrida
     * @throws IllegalArgumentException si {@code original} es nulo
     */
    public static MetricasPeorCaso ejecutarConTraza(String titulo, int[] original, int estrategiaPivote) {
        if (original == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        System.out.println("\n--- " + titulo + " ---");
        int[] copia = copiar(original);
        System.out.println("Arreglo original: " + aTexto(copia));
        MetricasPeorCaso m = new MetricasPeorCaso();
        m.mostrarTraza = true;
        quicksort(copia, m, estrategiaPivote);
        System.out.println("Arreglo final:    " + aTexto(copia));
        System.out.println("Invocaciones totales: " + m.invocaciones
                + " | Llamadas recursivas: " + (m.invocaciones - 1)
                + " | Cortes por caso base: " + (m.invocaciones - m.particiones)
                + " | Particiones: " + m.particiones
                + " | Comparaciones: " + m.comparaciones
                + " | Profundidad maxima: " + m.profundidadMaxima);
        System.out.println("Verificacion estaOrdenado(): " + estaOrdenado(copia));
        return m;
    }

    /**
     * Ordena una copia del arreglo sin traza y muestra el resumen.
     *
     * @param titulo rotulo del escenario
     * @param original arreglo de entrada; no se modifica
     * @param estrategiaPivote estrategia de eleccion del pivote
     * @throws IllegalArgumentException si {@code original} es nulo
     */
    public static void resumen(String titulo, int[] original, int estrategiaPivote) {
        if (original == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        int[] copia = copiar(original);
        MetricasPeorCaso m = new MetricasPeorCaso();
        quicksort(copia, m, estrategiaPivote);
        System.out.println(titulo + ": " + aTexto(original) + " -> " + aTexto(copia)
                + " | invocaciones=" + m.invocaciones
                + " particiones=" + m.particiones
                + " comparaciones=" + m.comparaciones
                + " profundidadMax=" + m.profundidadMaxima
                + " | ordenado=" + estaOrdenado(copia));
    }

    /**
     * Demuestra de forma controlada el limite de pila del peor caso: ordena un arreglo
     * grande YA ORDENADO con pivote inicial y captura el {@code StackOverflowError} si ocurre.
     *
     * @param tamano cantidad de elementos del arreglo ordenado a generar
     * @throws IllegalArgumentException si {@code tamano} es negativo
     */
    public static void demostrarDesbordeDePila(int tamano) {
        if (tamano < 0) {
            throw new IllegalArgumentException("El tamano no puede ser negativo.");
        }
        System.out.println("\n=== DEMOSTRACION DEL CONSUMO DE PILA O(n) ===");
        System.out.println("Se ordena un arreglo YA ORDENADO de " + tamano
                + " elementos con pivote inicial.");
        int[] grande = new int[tamano];
        for (int i = 0; i < tamano; i++) {
            grande[i] = i;
        }
        MetricasPeorCaso m = new MetricasPeorCaso();
        try {
            quicksort(grande, m, PIVOTE_PRIMERO);
            System.out.println("Termino sin desbordar: profundidad maxima alcanzada = "
                    + m.profundidadMaxima + " | ordenado=" + estaOrdenado(grande));
        } catch (StackOverflowError e) {
            // Se captura el Error solo con fines demostrativos: en produccion la solucion es
            // cambiar la eleccion del pivote o recursar primero sobre el lado mas chico.
            System.out.println("StackOverflowError capturado. Profundidad alcanzada antes de"
                    + " desbordar: " + m.profundidadMaxima);
            System.out.println("Esto confirma que la pila crece O(n) y no O(log n) en el peor caso.");
        }
    }

    /**
     * Ordena todo el arreglo con Quicksort.
     *
     * @param arreglo arreglo a ordenar in situ
     * @param metricas acumulador de metricas
     * @param estrategiaPivote {@code 0} para primer elemento, {@code 1} para elemento del medio
     * @throws IllegalArgumentException si {@code arreglo} o {@code metricas} son nulos
     */
    public static void quicksort(int[] arreglo, MetricasPeorCaso metricas, int estrategiaPivote) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        if (metricas == null) {
            throw new IllegalArgumentException("El acumulador de metricas no puede ser nulo.");
        }
        quicksortRecursivo(arreglo, 0, arreglo.length - 1, 0, metricas, estrategiaPivote);
    }

    /**
     * Ordena recursivamente {@code arreglo[inicio..fin]}.
     *
     * @param arreglo arreglo sobre el que se opera
     * @param inicio indice inicial (inclusive)
     * @param fin indice final (inclusive)
     * @param profundidad nivel de recursion actual
     * @param metricas acumulador de metricas
     * @param estrategiaPivote estrategia de eleccion del pivote
     */
    private static void quicksortRecursivo(int[] arreglo, int inicio, int fin, int profundidad,
            MetricasPeorCaso metricas, int estrategiaPivote) {
        metricas.invocaciones++;
        if (profundidad > metricas.profundidadMaxima) {
            metricas.profundidadMaxima = profundidad;
        }

        // Caso base: 0 o 1 elementos. Aca se corta la cadena de llamadas.
        if (inicio >= fin) {
            return;
        }

        int indicePivote = particionar(arreglo, inicio, fin, profundidad, metricas, estrategiaPivote);

        if (metricas.mostrarTraza) {
            System.out.println(sangria(profundidad) + "profundidad " + profundidad + ": "
                    + aTextoRango(arreglo, inicio, fin)
                    + " | pivote = " + arreglo[indicePivote]
                    + " (indice final " + indicePivote + ")");
            System.out.println(sangria(profundidad) + "   izquierdo: "
                    + aTextoRango(arreglo, inicio, indicePivote - 1)
                    + " | derecho: " + aTextoRango(arreglo, indicePivote + 1, fin));
        }

        quicksortRecursivo(arreglo, inicio, indicePivote - 1, profundidad + 1, metricas, estrategiaPivote);
        quicksortRecursivo(arreglo, indicePivote + 1, fin, profundidad + 1, metricas, estrategiaPivote);
    }

    /**
     * Particiona {@code arreglo[inicio..fin]} dejando el pivote en su posicion definitiva.
     *
     * @param arreglo arreglo sobre el que se opera
     * @param inicio indice inicial (inclusive)
     * @param fin indice final (inclusive)
     * @param profundidad nivel de recursion actual (no se usa para el calculo, solo contexto)
     * @param metricas acumulador de metricas
     * @param estrategiaPivote estrategia de eleccion del pivote
     * @return indice final del pivote
     */
    private static int particionar(int[] arreglo, int inicio, int fin, int profundidad,
            MetricasPeorCaso metricas, int estrategiaPivote) {
        metricas.particiones++;

        // LINEA CRITICA DEL RENDIMIENTO: aqui se decide el pivote. Con PIVOTE_PRIMERO sobre
        // una entrada ordenada el pivote es siempre el minimo y la particion sale degenerada;
        // con PIVOTE_MEDIO sobre la misma entrada el pivote es la mediana exacta.
        if (estrategiaPivote == PIVOTE_MEDIO) {
            int medio = inicio + (fin - inicio) / 2; // evita el desborde de (inicio + fin)
            intercambiar(arreglo, inicio, medio);
        }

        int pivote = arreglo[inicio];
        int limiteMenores = inicio;
        for (int j = inicio + 1; j <= fin; j++) {
            metricas.comparaciones++;
            if (arreglo[j] < pivote) {
                limiteMenores++;
                intercambiar(arreglo, limiteMenores, j);
            }
        }
        // Si no hubo ningun menor, limiteMenores sigue valiendo inicio: el pivote se queda
        // donde esta, el lado izquierdo queda vacio y el derecho conserva n-1 elementos.
        intercambiar(arreglo, inicio, limiteMenores);
        return limiteMenores;
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
     * Representa el arreglo completo como texto entre corchetes.
     *
     * @param arreglo arreglo a formatear
     * @return cadena con los elementos separados por coma
     * @throws IllegalArgumentException si {@code arreglo} es nulo
     */
    public static String aTexto(int[] arreglo) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        return aTextoRango(arreglo, 0, arreglo.length - 1);
    }

    /**
     * Representa el tramo {@code arreglo[desde..hasta]} como texto entre corchetes.
     *
     * @param arreglo arreglo a formatear
     * @param desde indice inicial (inclusive)
     * @param hasta indice final (inclusive)
     * @return cadena con los elementos del tramo, o "[] (vacio)" si el tramo esta vacio
     * @throws IllegalArgumentException si {@code arreglo} es nulo
     */
    public static String aTextoRango(int[] arreglo, int desde, int hasta) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        if (desde > hasta) {
            return "[] (vacio)";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = desde; i <= hasta; i++) {
            sb.append(arreglo[i]);
            if (i < hasta) {
                sb.append(", ");
            }
        }
        return sb.append("]").toString();
    }

    /**
     * Devuelve la sangria correspondiente a un nivel de recursion.
     *
     * @param profundidad nivel de recursion (0 para la llamada inicial)
     * @return cadena de espacios proporcional a la profundidad
     */
    public static String sangria(int profundidad) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < profundidad; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }
}

/**
 * Acumulador de metricas del peor caso de Quicksort.
 * No es publica: es soporte interno del ejercicio.
 */
class MetricasPeorCaso {
    long comparaciones;
    int invocaciones;
    int particiones;
    int profundidadMaxima;
    boolean mostrarTraza;
}


