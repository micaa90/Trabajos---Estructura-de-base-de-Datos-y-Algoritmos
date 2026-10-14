/**
 * Ejercicio 05 - ShellSort explicando los gaps.
 *
 * <p>Ordena un arreglo de enteros con ShellSort mostrando, para cada etapa, el valor del
 * GAP, las comparaciones y desplazamientos hechos en esa etapa y el estado completo del
 * arreglo al terminarla. Al final ordena el mismo arreglo con Insertion Sort puro y compara
 * los desplazamientos totales.</p>
 *
 * <p><b>Razonamiento del algoritmo.</b> ShellSort es una generalizacion de Insertion Sort:
 * en lugar de comparar cada elemento con su vecino inmediato (distancia 1), lo compara con
 * el elemento que esta a distancia GAP. El problema de Insertion Sort es que un valor chico
 * ubicado al final del arreglo necesita tantos desplazamientos como posiciones tenga que
 * recorrer para llegar al principio. Con un gap grande, ese mismo valor salta varias
 * posiciones por desplazamiento, asi que llega a la zona correcta con muy poco trabajo.</p>
 *
 * <p><b>Por que el gap se reduce hasta 1.</b> Un gap grande solo garantiza que el arreglo
 * quede "gap-ordenado" (cada subsecuencia tomada de gap en gap esta ordenada), no ordenado
 * del todo. Recien con gap = 1 se compara con el vecino inmediato, y esa ultima etapa es
 * EXACTAMENTE un Insertion Sort. La clave es que llega a un arreglo ya casi ordenado por
 * las etapas anteriores, y como Insertion Sort es O(n) con datos casi ordenados, esa etapa
 * final resulta barata. Ordenar con gap grande nunca destruye el trabajo previo: una
 * permutacion que mejora el orden a distancia grande no puede empeorar el desorden local
 * de forma neta.</p>
 *
 * <p><b>Secuencia de gaps usada:</b> la original de Shell, n/2, n/4, ..., 1 (division
 * entera). Es la mas simple de explicar, aunque no la mas eficiente: la complejidad de
 * ShellSort depende fuertemente de la secuencia elegida (con esta secuencia el peor caso es
 * O(n^2); con la secuencia de Hibbard baja a O(n^1.5)).</p>
 *
 * <p><b>Definicion de los contadores.</b></p>
 * <ul>
 *   <li><i>comparacion</i>: cada evaluacion de {@code arreglo[j - gap] > clave}.</li>
 *   <li><i>desplazamiento</i>: cada copia {@code arreglo[j] = arreglo[j - gap]}, o sea una
 *       escritura que mueve un elemento gap posiciones a la derecha. Nota: un desplazamiento
 *       de ShellSort con gap &gt; 1 cubre varias posiciones, mientras que uno de Insertion
 *       Sort cubre solo una; ahi esta el ahorro.</li>
 * </ul>
 *
 * <p>Complejidad: con la secuencia n/2 el peor caso es O(n^2) y en la practica se comporta
 * cerca de O(n^1.3); mejor caso O(n log n) (una pasada por etapa sin desplazamientos);
 * O(1) espacial adicional, ordena in situ. No es estable: los saltos pueden adelantar un
 * elemento por encima de otro de igual valor.</p>
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio05_ShellSortGaps {

    /**
     * Metodo principal. Ejecuta ShellSort sobre un arreglo de 10 elementos con un valor
     * chico cerca del final, repite con el arreglo de 8 elementos de la traza documentada,
     * compara contra Insertion Sort puro y recorre los casos limite.
     *
     * @param args argumentos de la linea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== EJERCICIO 05 - SHELLSORT Y LOS GAPS ===");

        int[] numeros = {45, 12, 78, 33, 90, 25, 67, 54, 2, 38};
        compararConInsercion("Caso nominal (10 elementos, el 2 cerca del final)", numeros);

        int[] ocho = {23, 29, 15, 19, 31, 7, 9, 5};
        compararConInsercion("Arreglo de 8 elementos de la traza del analisis", ocho);

        System.out.println("\n=== CASOS LIMITE ===");
        resumen("Arreglo vacio", new int[] {});
        resumen("Un solo elemento", new int[] {5});
        resumen("Ya ordenado (mejor caso)", new int[] {1, 2, 3, 4, 5, 6, 7, 8});
        resumen("Ordenado al reves (peor caso)", new int[] {8, 7, 6, 5, 4, 3, 2, 1});
        resumen("Con repetidos", new int[] {4, 2, 4, 2, 4, 2});

        System.out.println("\n--- Arreglo nulo (debe lanzar excepcion) ---");
        try {
            shellSort(null, new MetricasShell(), false);
            System.out.println("ERROR: no se lanzo la excepcion esperada.");
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException capturada: " + e.getMessage());
        }
    }

    /**
     * Ordena el mismo arreglo con ShellSort (con traza por etapa) y con Insertion Sort puro,
     * e imprime la tabla comparativa de desplazamientos.
     *
     * @param titulo rotulo del escenario
     * @param original arreglo de entrada; no se modifica
     * @throws IllegalArgumentException si {@code original} es nulo
     */
    public static void compararConInsercion(String titulo, int[] original) {
        if (original == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        System.out.println("\n--- " + titulo + " ---");
        System.out.println("Arreglo original: " + aTexto(original));

        // Cada algoritmo trabaja sobre su propia copia para que la comparacion sea justa.
        int[] paraShell = copiar(original);
        int[] paraInsercion = copiar(original);

        MetricasShell mShell = new MetricasShell();
        shellSort(paraShell, mShell, true);
        System.out.println("ShellSort final:  " + aTexto(paraShell)
                + " | estaOrdenado=" + estaOrdenado(paraShell));

        MetricasShell mInsercion = new MetricasShell();
        insertionSortPuro(paraInsercion, mInsercion);
        System.out.println("Insertion final:  " + aTexto(paraInsercion)
                + " | estaOrdenado=" + estaOrdenado(paraInsercion));
        System.out.println("Los dos resultados coinciden: " + sonIguales(paraShell, paraInsercion));

        System.out.println("+---------------------+---------------+-------------------+");
        System.out.println("| Algoritmo           | Comparaciones | Desplazamientos   |");
        System.out.println("+---------------------+---------------+-------------------+");
        System.out.printf("| %-19s | %13d | %17d |%n", "ShellSort (n/2)",
                mShell.comparaciones, mShell.desplazamientos);
        System.out.printf("| %-19s | %13d | %17d |%n", "Insertion Sort puro",
                mInsercion.comparaciones, mInsercion.desplazamientos);
        System.out.println("+---------------------+---------------+-------------------+");
    }

    /**
     * Ordena una copia del arreglo con ShellSort sin traza y muestra el resumen.
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
        MetricasShell m = new MetricasShell();
        shellSort(copia, m, false);
        System.out.println(titulo + ": " + aTexto(original) + " -> " + aTexto(copia)
                + " | etapas=" + m.etapas
                + " comparaciones=" + m.comparaciones
                + " desplazamientos=" + m.desplazamientos
                + " | ordenado=" + estaOrdenado(copia));
    }

    /**
     * Ordena el arreglo en forma ascendente con ShellSort usando la secuencia de gaps
     * n/2, n/4, ..., 1.
     *
     * @param arreglo arreglo a ordenar in situ
     * @param metricas acumulador global de comparaciones, desplazamientos y etapas
     * @param mostrarTraza si es {@code true} imprime gap, metricas y estado por etapa
     * @throws IllegalArgumentException si {@code arreglo} o {@code metricas} son nulos
     */
    public static void shellSort(int[] arreglo, MetricasShell metricas, boolean mostrarTraza) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        if (metricas == null) {
            throw new IllegalArgumentException("El acumulador de metricas no puede ser nulo.");
        }

        int n = arreglo.length;
        for (int gap = n / 2; gap > 0; gap /= 2) {
            long comparacionesEtapa = 0;
            long desplazamientosEtapa = 0;

            // Insertion Sort "a distancia gap": se recorre desde la posicion gap porque
            // antes de ella no hay con quien comparar a esa distancia.
            for (int i = gap; i < n; i++) {
                int clave = arreglo[i];
                int j = i;

                while (j >= gap) {
                    comparacionesEtapa++;
                    // AQUI esta la diferencia con Insertion Sort: se compara con
                    // arreglo[j - gap] y no con arreglo[j - 1].
                    if (arreglo[j - gap] > clave) {
                        arreglo[j] = arreglo[j - gap]; // un solo desplazamiento cubre gap posiciones
                        desplazamientosEtapa++;
                        j -= gap;
                    } else {
                        break;
                    }
                }
                arreglo[j] = clave;
            }

            metricas.comparaciones += comparacionesEtapa;
            metricas.desplazamientos += desplazamientosEtapa;
            metricas.etapas++;

            if (mostrarTraza) {
                // Cuando gap llega a 1 esta etapa ES un Insertion Sort completo, pero sobre
                // un arreglo que las etapas anteriores dejaron casi ordenado.
                String nota = (gap == 1) ? "  (gap=1: esta etapa es Insertion Sort puro)" : "";
                System.out.println("  Etapa " + metricas.etapas + " | gap = " + gap
                        + " | comparaciones = " + comparacionesEtapa
                        + " | desplazamientos = " + desplazamientosEtapa);
                System.out.println("    Estado: " + aTexto(arreglo) + nota);
            }
        }
    }

    /**
     * Ordena el arreglo con Insertion Sort clasico (gap fijo igual a 1), para contrastar
     * la cantidad de desplazamientos contra ShellSort.
     *
     * @param arreglo arreglo a ordenar in situ
     * @param metricas acumulador de comparaciones y desplazamientos
     * @throws IllegalArgumentException si {@code arreglo} o {@code metricas} son nulos
     */
    public static void insertionSortPuro(int[] arreglo, MetricasShell metricas) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        if (metricas == null) {
            throw new IllegalArgumentException("El acumulador de metricas no puede ser nulo.");
        }
        for (int i = 1; i < arreglo.length; i++) {
            int clave = arreglo[i];
            int j = i - 1;
            while (j >= 0) {
                metricas.comparaciones++;
                if (arreglo[j] > clave) {
                    arreglo[j + 1] = arreglo[j];
                    metricas.desplazamientos++;
                    j--;
                } else {
                    break;
                }
            }
            arreglo[j + 1] = clave;
        }
        metricas.etapas = 1;
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
     * Indica si dos arreglos son identicos posicion por posicion.
     *
     * @param a primer arreglo
     * @param b segundo arreglo
     * @return {@code true} si tienen la misma longitud y el mismo contenido
     * @throws IllegalArgumentException si alguno es nulo
     */
    public static boolean sonIguales(int[] a, int[] b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Los arreglos no pueden ser nulos.");
        }
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
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
 * Acumulador de metricas de ShellSort y de Insertion Sort puro.
 * No es publica: es soporte interno del ejercicio.
 */
class MetricasShell {
    long comparaciones;
    long desplazamientos;
    int etapas;
}


