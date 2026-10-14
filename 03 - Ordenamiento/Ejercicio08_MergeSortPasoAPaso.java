/**
 * Ejercicio 08 - MergeSort paso a paso.
 *
 * <p>Ordena un arreglo de enteros con MergeSort mostrando, con indentacion segun la
 * profundidad de recursion: la fase DIVIDO (subarreglo y las dos mitades resultantes), la
 * condicion de CORTE (subarreglo de un elemento) y la fase FUSIONO (los dos subarreglos de
 * entrada y el resultado ordenado). Cuenta las comparaciones hechas en las fusiones y los
 * niveles de recursion.</p>
 *
 * <p><b>Diferencia entre dividir y fusionar.</b> DIVIDIR es trivial y barato: se calcula el
 * punto medio y se parte el rango en dos, SIN mirar el contenido de los datos. No ordena
 * nada; solo genera subproblemas mas chicos. FUSIONAR es donde ocurre el trabajo real de
 * ordenamiento: recorre las dos mitades con dos indices, compara el elemento mas chico
 * pendiente de cada una y lo copia al resultado. La fusion es la que decide el orden
 * final.</p>
 *
 * <p><b>Por que la fusion necesita que las mitades ya esten ordenadas.</b> La fusion solo
 * mira el PRIMER elemento pendiente de cada mitad. Eso alcanza para elegir el minimo global
 * unicamente si cada mitad esta ordenada, porque en ese caso su primer pendiente es su
 * minimo. Si una mitad estuviera desordenada, el minimo podria estar mas atras y la fusion
 * lo pasaria por alto. La recursion garantiza la precondicion: cuando una llamada fusiona,
 * sus dos hijas ya terminaron de ordenar sus mitades.</p>
 *
 * <p><b>Condicion de corte.</b> {@code inicio >= fin}: un subarreglo de un elemento (o
 * vacio) ya esta ordenado por definicion, porque no existe un par de elementos que pueda
 * estar en orden incorrecto. Ese es el punto donde la recursion deja de dividir y empieza a
 * fusionar hacia arriba.</p>
 *
 * <p><b>Por que es O(n log n) en TODOS los casos.</b> El log n sale de las divisiones: cada
 * nivel parte los rangos a la mitad, asi que hacen falta log2(n) niveles para llegar a
 * tramos de un elemento. El n sale de las fusiones: en cada nivel del arbol, las fusiones en
 * conjunto tocan exactamente los n elementos una vez. Producto: n * log n. A diferencia de
 * Quicksort, la cantidad de niveles NO depende de los datos (el corte siempre es por el
 * medio), de modo que el mejor y el peor caso coinciden.</p>
 *
 * <p><b>Por que la complejidad espacial es O(n).</b> La fusion no puede hacerse in situ sin
 * degradar el tiempo: para no sobrescribir elementos que todavia no se copiaron, necesita un
 * arreglo AUXILIAR donde armar el tramo fusionado. Aqui se reserva un unico auxiliar del
 * tamano del arreglo y se reutiliza en todos los niveles, de modo que el extra es O(n) (mas
 * O(log n) de pila). Quicksort, en cambio, particiona in situ y usa O(1) de datos
 * auxiliares: es el intercambio de memoria por garantia de peor caso.</p>
 *
 * <p><b>Estabilidad.</b> La fusion usa {@code <=} al elegir entre el pendiente de la mitad
 * izquierda y el de la derecha, de modo que ante un empate gana el de la izquierda, que es
 * el que venia primero en el arreglo original. Por eso MergeSort es estable: los elementos
 * de igual valor conservan su orden relativo.</p>
 *
 * <p><b>Definicion de los contadores.</b> <i>comparacion</i>: cada evaluacion de
 * {@code auxiliar[i] <= auxiliar[j]} dentro de la fusion (las copias de cola, cuando una
 * mitad se agoto, no comparan y no se cuentan). <i>copias</i>: cada escritura de un elemento
 * al arreglo destino durante la fusion. <i>divisiones</i>: llamadas que partieron el rango.
 * <i>fusiones</i>: llamadas al metodo de fusion. <i>niveles</i>: profundidad maxima + 1.</p>
 *
 * <p>Complejidad: O(n log n) temporal en el mejor, el peor y el caso promedio;
 * O(n) espacial adicional por el arreglo auxiliar mas O(log n) de pila. Es estable y NO
 * ordena in situ.</p>
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio08_MergeSortPasoAPaso {

    /**
     * Metodo principal. Ordena un arreglo de 8 elementos con traza completa, repite con el
     * arreglo de la traza documentada y recorre los casos limite.
     *
     * @param args argumentos de la linea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== EJERCICIO 08 - MERGESORT PASO A PASO ===");

        int[] numeros = {38, 27, 43, 3, 9, 82, 10, 1};
        ejecutarConTraza("Caso nominal (8 elementos)", numeros);

        ejecutarConTraza("Arreglo de la traza del analisis (7 elementos)",
                new int[] {38, 27, 43, 3, 9, 82, 10});

        System.out.println("\n=== CASOS LIMITE ===");
        resumen("Arreglo vacio", new int[] {});
        resumen("Un solo elemento", new int[] {5});
        resumen("Dos elementos desordenados", new int[] {2, 1});
        resumen("Ya ordenado (mejor caso)", new int[] {1, 2, 3, 4, 5, 6, 7, 8});
        resumen("Ordenado al reves (peor caso)", new int[] {8, 7, 6, 5, 4, 3, 2, 1});
        resumen("Con repetidos", new int[] {3, 1, 3, 1, 2, 2});
        resumen("Negativos", new int[] {-5, 3, -1, 0});

        System.out.println("\n--- Arreglo nulo (debe lanzar excepcion) ---");
        try {
            mergeSort(null, new MetricasMerge(), false);
            System.out.println("ERROR: no se lanzo la excepcion esperada.");
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException capturada: " + e.getMessage());
        }

        System.out.println("\n=== CONTRASTE CON LA TEORIA ===");
        int n = 8;
        System.out.println("Para n = " + n + ": niveles esperados = log2(n) + 1 = 4;"
                + " comparaciones acotadas por n*log2(n) = " + (n * 3) + ".");
        System.out.println("El mejor y el peor caso dan la MISMA cantidad de niveles porque el"
                + " corte siempre es por el medio y no depende de los datos.");
    }

    /**
     * Ordena una copia del arreglo imprimiendo la traza completa de divisiones y fusiones.
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
        MetricasMerge m = new MetricasMerge();
        mergeSort(copia, m, true);
        System.out.println("Arreglo final:    " + aTexto(copia));
        System.out.println("Divisiones: " + m.divisiones
                + " | Cortes: " + m.cortes
                + " | Fusiones: " + m.fusiones
                + " | Comparaciones en fusiones: " + m.comparaciones
                + " | Copias: " + m.copias
                + " | Niveles de recursion: " + (m.profundidadMaxima + 1));
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
        MetricasMerge m = new MetricasMerge();
        mergeSort(copia, m, false);
        System.out.println(titulo + ": " + aTexto(original) + " -> " + aTexto(copia)
                + " | divisiones=" + m.divisiones
                + " fusiones=" + m.fusiones
                + " comparaciones=" + m.comparaciones
                + " niveles=" + (copia.length == 0 ? 0 : m.profundidadMaxima + 1)
                + " | ordenado=" + estaOrdenado(copia));
    }

    /**
     * Ordena todo el arreglo con MergeSort.
     *
     * @param arreglo arreglo a ordenar in situ (el resultado queda en este mismo arreglo)
     * @param metricas acumulador de divisiones, fusiones, comparaciones y copias
     * @param mostrarTraza si es {@code true} imprime DIVIDO, CORTE y FUSIONO
     * @throws IllegalArgumentException si {@code arreglo} o {@code metricas} son nulos
     */
    public static void mergeSort(int[] arreglo, MetricasMerge metricas, boolean mostrarTraza) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        if (metricas == null) {
            throw new IllegalArgumentException("El acumulador de metricas no puede ser nulo.");
        }
        if (arreglo.length == 0) {
            return;
        }
        // Un unico auxiliar reutilizado en todos los niveles: si cada fusion creara el suyo,
        // el costo de asignacion de memoria dominaria el tiempo real.
        int[] auxiliar = new int[arreglo.length];
        metricas.mostrarTraza = mostrarTraza;
        mergeSortRecursivo(arreglo, auxiliar, 0, arreglo.length - 1, 0, metricas);
    }

    /**
     * Ordena recursivamente {@code arreglo[inicio..fin]} dividiendo por el medio y fusionando.
     *
     * @param arreglo arreglo sobre el que se opera
     * @param auxiliar arreglo auxiliar compartido del mismo tamano que {@code arreglo}
     * @param inicio indice inicial (inclusive)
     * @param fin indice final (inclusive)
     * @param profundidad nivel de recursion actual, usado para la indentacion
     * @param metricas acumulador de metricas
     */
    private static void mergeSortRecursivo(int[] arreglo, int[] auxiliar, int inicio, int fin,
            int profundidad, MetricasMerge metricas) {
        if (profundidad > metricas.profundidadMaxima) {
            metricas.profundidadMaxima = profundidad;
        }

        // CONDICION DE CORTE: un tramo de un solo elemento ya esta ordenado. Aqui la
        // recursion deja de DIVIDIR y empieza a FUSIONAR de vuelta hacia arriba.
        if (inicio >= fin) {
            metricas.cortes++;
            if (metricas.mostrarTraza) {
                System.out.println(sangria(profundidad) + "CORTE: " + aTextoRango(arreglo, inicio, fin)
                        + " (un elemento, ya ordenado)");
            }
            return;
        }

        // ---------- FASE DE DIVISION ----------
        // Barata: solo aritmetica de indices, no mira los datos ni ordena nada.
        int medio = inicio + (fin - inicio) / 2; // forma segura de promediar, evita desborde
        metricas.divisiones++;
        if (metricas.mostrarTraza) {
            System.out.println(sangria(profundidad) + "DIVIDO " + aTextoRango(arreglo, inicio, fin)
                    + " en " + aTextoRango(arreglo, inicio, medio)
                    + " y " + aTextoRango(arreglo, medio + 1, fin));
        }

        mergeSortRecursivo(arreglo, auxiliar, inicio, medio, profundidad + 1, metricas);
        mergeSortRecursivo(arreglo, auxiliar, medio + 1, fin, profundidad + 1, metricas);

        // ---------- FASE DE FUSION ----------
        // Caro: aqui ocurre el ordenamiento real, comparando los pendientes de cada mitad.
        fusionar(arreglo, auxiliar, inicio, medio, fin, profundidad, metricas);
    }

    /**
     * Fusiona los tramos ordenados {@code arreglo[inicio..medio]} y
     * {@code arreglo[medio+1..fin]} dejando el resultado ordenado en {@code arreglo[inicio..fin]}.
     *
     * @param arreglo arreglo destino, que contiene las dos mitades ya ordenadas
     * @param auxiliar arreglo auxiliar donde se respalda el tramo antes de reescribirlo
     * @param inicio indice inicial del tramo (inclusive)
     * @param medio indice final de la mitad izquierda (inclusive)
     * @param fin indice final de la mitad derecha (inclusive)
     * @param profundidad nivel de recursion actual, usado para la indentacion
     * @param metricas acumulador de metricas
     */
    private static void fusionar(int[] arreglo, int[] auxiliar, int inicio, int medio, int fin,
            int profundidad, MetricasMerge metricas) {
        metricas.fusiones++;

        String izquierdoAntes = aTextoRango(arreglo, inicio, medio);
        String derechoAntes = aTextoRango(arreglo, medio + 1, fin);

        // Se respalda el tramo porque la fusion escribe sobre arreglo[inicio..fin] y
        // pisaria elementos todavia no consumidos. De aqui sale la complejidad espacial O(n).
        for (int k = inicio; k <= fin; k++) {
            auxiliar[k] = arreglo[k];
        }

        int i = inicio;      // pendiente de la mitad izquierda
        int j = medio + 1;   // pendiente de la mitad derecha
        int destino = inicio;

        while (i <= medio && j <= fin) {
            metricas.comparaciones++;
            // El <= hace que, ante un empate, gane el elemento de la mitad IZQUIERDA:
            // eso es exactamente lo que vuelve estable a MergeSort.
            if (auxiliar[i] <= auxiliar[j]) {
                arreglo[destino++] = auxiliar[i++];
            } else {
                arreglo[destino++] = auxiliar[j++];
            }
            metricas.copias++;
        }

        // Cola: si una mitad se agoto, lo que queda de la otra ya esta ordenado y es mayor
        // que todo lo copiado, asi que se traslada sin comparar.
        while (i <= medio) {
            arreglo[destino++] = auxiliar[i++];
            metricas.copias++;
        }
        while (j <= fin) {
            arreglo[destino++] = auxiliar[j++];
            metricas.copias++;
        }

        if (metricas.mostrarTraza) {
            System.out.println(sangria(profundidad) + "FUSIONO " + izquierdoAntes
                    + " + " + derechoAntes + " -> " + aTextoRango(arreglo, inicio, fin));
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
 * Acumulador de metricas de MergeSort y bandera de traza.
 * No es publica: es soporte interno del ejercicio.
 */
class MetricasMerge {
    long comparaciones;
    long copias;
    int divisiones;
    int cortes;
    int fusiones;
    int profundidadMaxima;
    boolean mostrarTraza;
}


