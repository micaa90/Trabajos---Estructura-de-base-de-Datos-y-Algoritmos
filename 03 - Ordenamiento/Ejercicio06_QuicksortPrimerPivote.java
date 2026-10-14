/**
 * Ejercicio 06 - Quicksort con el primer elemento como pivote.
 *
 * <p>Ordena un arreglo de enteros con Quicksort eligiendo SIEMPRE el primer elemento del
 * subarreglo como pivote. En cada llamada imprime, con indentacion segun la profundidad de
 * recursion, el subarreglo recibido, el pivote elegido, el subarreglo izquierdo (menores) y
 * el derecho (mayores). Al final muestra el arreglo ordenado.</p>
 *
 * <p><b>Divide y venceras.</b> Quicksort aplica las tres fases del esquema:</p>
 * <ol>
 *   <li><b>Dividir:</b> particionar el subarreglo alrededor del pivote. Aca esta TODO el
 *       trabajo del algoritmo: un recorrido lineal que separa menores de mayores.</li>
 *   <li><b>Vencer:</b> ordenar recursivamente el lado de los menores y el de los mayores,
 *       que son problemas del mismo tipo pero mas chicos.</li>
 *   <li><b>Combinar:</b> no hay nada que hacer. Como la particion opera in situ sobre el
 *       mismo arreglo y deja menores | pivote | mayores en posiciones contiguas, al volver
 *       de las dos recursiones el tramo ya esta ordenado. Esto contrasta con MergeSort,
 *       donde el trabajo real esta justamente en la combinacion (la fusion).</li>
 * </ol>
 *
 * <p><b>Por que el pivote queda en su posicion definitiva.</b> Al terminar la particion se
 * cumple el invariante: todo lo que esta a la izquierda del pivote es menor que el y todo lo
 * que esta a la derecha es mayor o igual. En el arreglo final ordenado, el pivote tiene
 * exactamente tantos elementos menores a su izquierda como los que la particion dejo ahi,
 * asi que su indice ya es el correcto. Por eso las recursiones excluyen esa posicion y el
 * pivote nunca se vuelve a mover.</p>
 *
 * <p><b>Caso base.</b> {@code inicio >= fin}: un subarreglo de 0 o 1 elementos ya esta
 * ordenado por definicion (no existe un par de elementos que pueda estar desordenado), asi
 * que la recursion corta sin hacer nada.</p>
 *
 * <p><b>Definicion de los contadores.</b></p>
 * <ul>
 *   <li><i>comparacion</i>: cada evaluacion de {@code arreglo[j] < pivote} dentro de la particion.</li>
 *   <li><i>intercambio</i>: cada permuta efectiva de dos posiciones, incluida la que coloca
 *       el pivote en su indice final.</li>
 *   <li><i>invocaciones totales</i>: todas las entradas al metodo recursivo, contando la
 *       llamada inicial y las que cortan por caso base.</li>
 *   <li><i>llamadas recursivas</i>: invocaciones realizadas desde dentro del metodo
 *       (invocaciones totales - 1).</li>
 *   <li><i>particiones</i>: llamadas que efectivamente particionaron (las que no cortaron
 *       por caso base).</li>
 * </ul>
 *
 * <p>Complejidad: O(n log n) en el mejor caso y en el promedio (particiones balanceadas, el
 * arbol de recursion tiene altura log n y cada nivel cuesta O(n)); O(n^2) en el peor caso
 * (arreglo ya ordenado o invertido con este pivote, el arbol degenera en una cadena de
 * altura n). Espacial: O(1) de datos auxiliares porque ordena in situ, mas O(log n) de pila
 * de recursion en el caso promedio y O(n) en el peor. No es estable.</p>
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio06_QuicksortPrimerPivote {

    /**
     * Metodo principal. Ordena un arreglo desordenado de 8 elementos con traza completa,
     * repite con el arreglo de la traza documentada y recorre los casos limite.
     *
     * @param args argumentos de la linea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== EJERCICIO 06 - QUICKSORT CON EL PRIMER ELEMENTO COMO PIVOTE ===");

        int[] numeros = {7, 2, 9, 4, 1, 8, 3, 6};
        ejecutarConTraza("Caso nominal (8 elementos)", numeros);

        ejecutarConTraza("Arreglo de la traza del analisis", new int[] {6, 3, 9, 1, 7, 4});

        System.out.println("\n=== CASOS LIMITE ===");
        resumen("Arreglo vacio", new int[] {});
        resumen("Un solo elemento", new int[] {5});
        resumen("Dos elementos desordenados", new int[] {2, 1});
        resumen("Ya ordenado (peor caso de este pivote)", new int[] {1, 2, 3, 4, 5, 6, 7, 8});
        resumen("Ordenado al reves", new int[] {8, 7, 6, 5, 4, 3, 2, 1});
        resumen("Todos iguales", new int[] {4, 4, 4, 4, 4});
        resumen("Con repetidos y negativos", new int[] {3, -1, 3, 0, -1, 2});

        System.out.println("\n--- Arreglo nulo (debe lanzar excepcion) ---");
        try {
            quicksort(null, new MetricasQuicksort());
            System.out.println("ERROR: no se lanzo la excepcion esperada.");
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException capturada: " + e.getMessage());
        }
    }

    /**
     * Ordena una copia del arreglo imprimiendo la traza de cada llamada recursiva.
     *
     * @param titulo rotulo del escenario
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
        MetricasQuicksort m = new MetricasQuicksort();
        m.mostrarTraza = true;
        quicksort(copia, m);
        System.out.println("Arreglo final ordenado: " + aTexto(copia));
        System.out.println("Invocaciones totales: " + m.invocaciones
                + " | Llamadas recursivas: " + Math.max(0, m.invocaciones - 1)
                + " | Particiones: " + m.particiones
                + " | Comparaciones: " + m.comparaciones
                + " | Intercambios: " + m.intercambios
                + " | Profundidad maxima: " + m.profundidadMaxima);
        System.out.println("Verificacion estaOrdenado(): " + estaOrdenado(copia));
    }

    /**
     * Ordena una copia del arreglo sin traza y muestra el resumen.
     *
     * @param titulo rotulo del escenario
     * @param original arreglo de entrada; no se modifica
     * @throws IllegalArgumentException si {@code original} es nulo
     */
    public static void resumen(String titulo, int[] original) {
        if (original == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        int[] copia = copiar(original);
        MetricasQuicksort m = new MetricasQuicksort();
        quicksort(copia, m);
        System.out.println(titulo + ": " + aTexto(original) + " -> " + aTexto(copia)
                + " | invocaciones=" + m.invocaciones
                + " particiones=" + m.particiones
                + " comparaciones=" + m.comparaciones
                + " profundidadMax=" + m.profundidadMaxima
                + " | ordenado=" + estaOrdenado(copia));
    }

    /**
     * Ordena todo el arreglo con Quicksort.
     *
     * @param arreglo arreglo a ordenar in situ
     * @param metricas acumulador de invocaciones, particiones, comparaciones e intercambios
     * @throws IllegalArgumentException si {@code arreglo} o {@code metricas} son nulos
     */
    public static void quicksort(int[] arreglo, MetricasQuicksort metricas) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        if (metricas == null) {
            throw new IllegalArgumentException("El acumulador de metricas no puede ser nulo.");
        }
        quicksortRecursivo(arreglo, 0, arreglo.length - 1, 0, metricas);
    }

    /**
     * Ordena el subarreglo {@code arreglo[inicio..fin]} aplicando divide y venceras.
     *
     * @param arreglo arreglo sobre el que se opera
     * @param inicio indice inicial del subarreglo (inclusive)
     * @param fin indice final del subarreglo (inclusive)
     * @param profundidad nivel de recursion actual, usado solo para la indentacion de la traza
     * @param metricas acumulador de metricas
     */
    private static void quicksortRecursivo(int[] arreglo, int inicio, int fin,
            int profundidad, MetricasQuicksort metricas) {
        metricas.invocaciones++;
        if (profundidad > metricas.profundidadMaxima) {
            metricas.profundidadMaxima = profundidad;
        }

        // CASO BASE: 0 o 1 elementos. Un tramo asi ya esta ordenado, no hay par que comparar.
        if (inicio >= fin) {
            if (metricas.mostrarTraza && inicio == fin) {
                System.out.println(sangria(profundidad) + "CORTE: [" + arreglo[inicio]
                        + "] (un elemento, ya ordenado)");
            }
            return;
        }

        if (metricas.mostrarTraza) {
            System.out.println(sangria(profundidad) + "LLAMADA sobre "
                    + aTextoRango(arreglo, inicio, fin) + "  (indices " + inicio + ".." + fin + ")");
        }

        // FASE DIVIDIR: todo el trabajo del algoritmo esta aca.
        int indicePivote = particionar(arreglo, inicio, fin, profundidad, metricas);

        if (metricas.mostrarTraza) {
            System.out.println(sangria(profundidad) + "  Pivote " + arreglo[indicePivote]
                    + " queda FIJO en el indice " + indicePivote);
            System.out.println(sangria(profundidad) + "  Izquierdo (menores): "
                    + aTextoRango(arreglo, inicio, indicePivote - 1));
            System.out.println(sangria(profundidad) + "  Derecho  (mayores): "
                    + aTextoRango(arreglo, indicePivote + 1, fin));
        }

        // FASE VENCER: las dos mitades se ordenan por separado. El pivote se excluye porque
        // ya esta en su lugar definitivo.
        quicksortRecursivo(arreglo, inicio, indicePivote - 1, profundidad + 1, metricas);
        quicksortRecursivo(arreglo, indicePivote + 1, fin, profundidad + 1, metricas);

        // FASE COMBINAR: vacia. No hace falta ningun trabajo extra, a diferencia de MergeSort.
    }

    /**
     * Particiona {@code arreglo[inicio..fin]} usando como pivote el PRIMER elemento y deja
     * el pivote en su posicion definitiva.
     *
     * @param arreglo arreglo sobre el que se opera
     * @param inicio indice inicial del subarreglo (inclusive)
     * @param fin indice final del subarreglo (inclusive)
     * @param profundidad nivel de recursion actual, usado solo para la traza
     * @param metricas acumulador de metricas
     * @return el indice final del pivote
     */
    private static int particionar(int[] arreglo, int inicio, int fin,
            int profundidad, MetricasQuicksort metricas) {
        // LINEA CRITICA: el pivote es SIEMPRE el primer elemento. Esta eleccion es la que
        // determina si las particiones salen balanceadas o degeneradas.
        int pivote = arreglo[inicio];
        metricas.particiones++;

        if (metricas.mostrarTraza) {
            System.out.println(sangria(profundidad) + "  Pivote elegido (primer elemento): " + pivote);
        }

        // Invariante del recorrido: arreglo[inicio+1..limiteMenores] contiene solo elementos
        // menores que el pivote, y arreglo[limiteMenores+1..j-1] solo mayores o iguales.
        int limiteMenores = inicio;
        for (int j = inicio + 1; j <= fin; j++) {
            metricas.comparaciones++;
            if (arreglo[j] < pivote) {
                limiteMenores++;
                intercambiar(arreglo, limiteMenores, j);
                metricas.intercambios++;
            }
        }

        // El pivote se mueve al borde de la zona de menores: ahi tiene a su izquierda
        // exactamente a todos los menores, que es su posicion en el arreglo ordenado.
        intercambiar(arreglo, inicio, limiteMenores);
        metricas.intercambios++;
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
            sb.append("    ");
        }
        return sb.toString();
    }
}

/**
 * Acumulador de metricas de Quicksort y bandera de traza.
 * No es publica: es soporte interno del ejercicio.
 */
class MetricasQuicksort {
    long comparaciones;
    long intercambios;
    int invocaciones;
    int particiones;
    int profundidadMaxima;
    boolean mostrarTraza;
}


