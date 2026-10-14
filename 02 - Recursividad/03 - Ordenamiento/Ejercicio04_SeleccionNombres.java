/**
 * Ejercicio 04 - Ordenamiento de nombres con Selection Sort.
 *
 * <p>Ordena alfabeticamente {@code String[] nombres = {"Lucia", "Ana", "Pedro", "Juan"}}
 * con Selection Sort, mostrando el arreglo original, que nombre se selecciono como minimo
 * en cada pasada y el arreglo final. Un segundo arreglo con mayusculas y minusculas
 * mezcladas y un acento muestra la diferencia entre {@code compareTo} y
 * {@code compareToIgnoreCase}.</p>
 *
 * <p><b>Que cambia al ordenar textos en lugar de numeros.</b> En Java los operadores
 * {@code <} y {@code >} solo funcionan con tipos primitivos numericos; con {@code String}
 * no compilan porque un String es una referencia a un objeto, no un numero. Y {@code ==}
 * compila pero compara REFERENCIAS (si dos variables apuntan al mismo objeto en el heap),
 * no contenido: por el pool de literales puede dar {@code true} por casualidad y
 * {@code false} con cadenas construidas en tiempo de ejecucion. La comparacion correcta es
 * {@code a.compareTo(b)}, que devuelve un entero:</p>
 * <ul>
 *   <li>negativo: {@code a} va ANTES que {@code b} alfabeticamente;</li>
 *   <li>cero: son equivalentes caracter por caracter;</li>
 *   <li>positivo: {@code a} va DESPUES que {@code b}.</li>
 * </ul>
 *
 * <p><b>Orden lexicografico.</b> {@code compareTo} recorre las cadenas en paralelo y, en el
 * primer caracter distinto, devuelve la diferencia de sus codigos UTF-16; si una es prefijo
 * de la otra, devuelve la diferencia de longitudes. Como 'A'=65..'Z'=90 estan antes que
 * 'a'=97..'z'=122, "Zara" queda ANTES que "ana", resultado que no coincide con el orden
 * alfabetico que espera una persona. Los acentos quedan todavia mas atras ('A' con tilde
 * vale 193). {@code compareToIgnoreCase} corrige el problema de las mayusculas, pero no el
 * de los acentos.</p>
 *
 * <p><b>Que NO cambia.</b> La logica de Selection Sort es identica a la version con enteros:
 * recorrer el tramo no ordenado recordando el indice del minimo y hacer un unico intercambio
 * por pasada. La UNICA linea que cambia es la condicion de comparacion, que pasa de
 * {@code arreglo[j] < arreglo[indiceMinimo]} a
 * {@code arreglo[j].compareTo(arreglo[indiceMinimo]) < 0}.</p>
 *
 * <p><b>Definicion de los contadores.</b> Una <i>comparacion</i> es cada llamada a
 * {@code compareTo} o {@code compareToIgnoreCase} entre dos elementos del arreglo; un
 * <i>intercambio</i> es cada permuta efectiva de dos posiciones (se evita si el minimo ya
 * esta en su lugar).</p>
 *
 * <p>Complejidad: O(n^2) comparaciones en todos los casos y O(1) espacial adicional. El
 * costo de comparar dos String es O(L) con L la longitud del prefijo comun, por lo que el
 * costo real es O(n^2 * L); con nombres cortos L es una constante chica y no cambia el orden.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio04_SeleccionNombres {

    /**
     * Metodo principal. Ordena el arreglo del enunciado, luego el arreglo con mayusculas
     * mezcladas con las dos estrategias de comparacion, y finalmente los casos limite.
     *
     * @param args argumentos de la linea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== EJERCICIO 04 - SELECTION SORT SOBRE NOMBRES ===");

        String[] nombres = {"Lucia", "Ana", "Pedro", "Juan"};
        ordenarConTraza("Caso del enunciado (compareTo)", nombres, false);

        String[] mezclados = {"Zara", "ana", "Angel", "bruno", "Ana", "Ángel"};
        System.out.println("\n### Efecto de compareTo vs compareToIgnoreCase ###");
        String[] conCompareTo = ordenarConTraza("compareTo (sensible a mayusculas)", mezclados, false);
        String[] sinDistinguir = ordenarConTraza("compareToIgnoreCase", mezclados, true);
        System.out.println("Resultado con compareTo:           " + aTexto(conCompareTo));
        System.out.println("Resultado con compareToIgnoreCase: " + aTexto(sinDistinguir));
        System.out.println("Los dos resultados coinciden: " + sonIguales(conCompareTo, sinDistinguir));
        System.out.println("Motivo: compareTo usa codigos UTF-16, y 'Z'(90) < 'a'(97) < 'Á'(193).");

        System.out.println("\n=== CASOS LIMITE ===");
        resumen("Arreglo vacio", new String[] {});
        resumen("Un solo nombre", new String[] {"Ana"});
        resumen("Ya ordenado (mejor caso)", new String[] {"Ana", "Juan", "Lucia", "Pedro"});
        resumen("Ordenado al reves (peor caso)", new String[] {"Pedro", "Lucia", "Juan", "Ana"});
        resumen("Con nombres repetidos", new String[] {"Ana", "Pedro", "Ana", "Juan"});
        resumen("Con cadena vacia", new String[] {"Ana", "", "Pedro"});

        System.out.println("\n--- Entradas invalidas (deben lanzar excepcion) ---");
        try {
            selectionSortNombres(null, new MetricasNombres(), false, false);
            System.out.println("ERROR: no se lanzo la excepcion esperada.");
        } catch (IllegalArgumentException e) {
            System.out.println("Arreglo nulo -> IllegalArgumentException: " + e.getMessage());
        }
        try {
            selectionSortNombres(new String[] {"Ana", null}, new MetricasNombres(), false, false);
            System.out.println("ERROR: no se lanzo la excepcion esperada.");
        } catch (IllegalArgumentException e) {
            System.out.println("Elemento nulo -> IllegalArgumentException: " + e.getMessage());
        }

        System.out.println("\nComparaciones teoricas para n = 4: n(n-1)/2 = " + (4 * 3 / 2));
    }

    /**
     * Ordena una copia del arreglo de nombres imprimiendo la traza de cada pasada.
     *
     * @param titulo rotulo del escenario
     * @param original arreglo de entrada; no se modifica
     * @param ignorarMayusculas si es {@code true} usa {@code compareToIgnoreCase}
     * @return el arreglo ordenado (copia nueva)
     * @throws IllegalArgumentException si {@code original} es nulo o contiene nulos
     */
    public static String[] ordenarConTraza(String titulo, String[] original, boolean ignorarMayusculas) {
        if (original == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        System.out.println("\n--- " + titulo + " ---");
        String[] copia = copiar(original);
        System.out.println("Arreglo original: " + aTexto(copia));
        MetricasNombres m = new MetricasNombres();
        selectionSortNombres(copia, m, true, ignorarMayusculas);
        System.out.println("Arreglo final:    " + aTexto(copia));
        System.out.println("Comparaciones: " + m.comparaciones + " | Intercambios: " + m.intercambios);
        if (ignorarMayusculas) {
            System.out.println("Verificacion estaOrdenadoSinDistinguirMayusculas(): "
                    + estaOrdenadoSinDistinguirMayusculas(copia));
            System.out.println("Verificacion estaOrdenado() con compareTo: " + estaOrdenado(copia)
                    + "  (puede ser false: el criterio usado fue otro)");
        } else {
            System.out.println("Verificacion estaOrdenado(): " + estaOrdenado(copia));
        }
        return copia;
    }

    /**
     * Ordena una copia del arreglo con {@code compareTo} y muestra solo el resumen.
     *
     * @param titulo rotulo del escenario
     * @param original arreglo de entrada; no se modifica
     * @throws IllegalArgumentException si {@code original} es nulo o contiene nulos
     */
    public static void resumen(String titulo, String[] original) {
        if (original == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        String[] copia = copiar(original);
        MetricasNombres m = new MetricasNombres();
        selectionSortNombres(copia, m, false, false);
        System.out.println(titulo + ": " + aTexto(original) + " -> " + aTexto(copia)
                + " | comparaciones=" + m.comparaciones
                + " intercambios=" + m.intercambios
                + " | ordenado=" + estaOrdenado(copia));
    }

    /**
     * Ordena alfabeticamente un arreglo de nombres con Selection Sort.
     *
     * @param arreglo arreglo a ordenar in situ
     * @param metricas acumulador de comparaciones e intercambios
     * @param mostrarTraza si es {@code true} informa el minimo elegido en cada pasada
     * @param ignorarMayusculas si es {@code true} compara con {@code compareToIgnoreCase}
     * @throws IllegalArgumentException si {@code arreglo} o {@code metricas} son nulos,
     *         o si algun elemento del arreglo es nulo
     */
    public static void selectionSortNombres(String[] arreglo, MetricasNombres metricas,
            boolean mostrarTraza, boolean ignorarMayusculas) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        if (metricas == null) {
            throw new IllegalArgumentException("El acumulador de metricas no puede ser nulo.");
        }
        // Se valida antes de comparar porque compareTo sobre null lanzaria
        // NullPointerException en medio del ordenamiento, dejando el arreglo a medio ordenar.
        for (int i = 0; i < arreglo.length; i++) {
            if (arreglo[i] == null) {
                throw new IllegalArgumentException("El arreglo no puede contener nombres nulos (posicion " + i + ").");
            }
        }

        int n = arreglo.length;
        for (int i = 0; i < n - 1; i++) {
            int indiceMinimo = i;

            for (int j = i + 1; j < n; j++) {
                metricas.comparaciones++;
                // UNICA linea que cambia respecto de la version con enteros: en vez de
                // arreglo[j] < arreglo[indiceMinimo] se consulta el signo de compareTo.
                int resultado = ignorarMayusculas
                        ? arreglo[j].compareToIgnoreCase(arreglo[indiceMinimo])
                        : arreglo[j].compareTo(arreglo[indiceMinimo]);
                if (resultado < 0) {
                    // Negativo significa "arreglo[j] va antes alfabeticamente", o sea que
                    // es un candidato mejor a minimo.
                    indiceMinimo = j;
                }
            }

            if (indiceMinimo != i) {
                String temporal = arreglo[i];
                arreglo[i] = arreglo[indiceMinimo];
                arreglo[indiceMinimo] = temporal;
                metricas.intercambios++;
            }

            if (mostrarTraza) {
                System.out.println("  Pasada " + (i + 1) + ": minimo seleccionado = \""
                        + arreglo[i] + "\" -> " + aTexto(arreglo));
            }
        }
    }

    /**
     * Verifica alfabeticamente el resultado usando {@code compareTo}, de forma independiente
     * del algoritmo de ordenamiento.
     *
     * @param arreglo arreglo a verificar
     * @return {@code true} si cada nombre es menor o igual al siguiente segun {@code compareTo}
     * @throws IllegalArgumentException si {@code arreglo} es nulo o contiene nulos
     */
    public static boolean estaOrdenado(String[] arreglo) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        for (int i = 1; i < arreglo.length; i++) {
            if (arreglo[i - 1] == null || arreglo[i] == null) {
                throw new IllegalArgumentException("El arreglo no puede contener nombres nulos.");
            }
            if (arreglo[i - 1].compareTo(arreglo[i]) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica el resultado ignorando mayusculas y minusculas.
     *
     * @param arreglo arreglo a verificar
     * @return {@code true} si cada nombre es menor o igual al siguiente segun
     *         {@code compareToIgnoreCase}
     * @throws IllegalArgumentException si {@code arreglo} es nulo o contiene nulos
     */
    public static boolean estaOrdenadoSinDistinguirMayusculas(String[] arreglo) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        for (int i = 1; i < arreglo.length; i++) {
            if (arreglo[i - 1] == null || arreglo[i] == null) {
                throw new IllegalArgumentException("El arreglo no puede contener nombres nulos.");
            }
            if (arreglo[i - 1].compareToIgnoreCase(arreglo[i]) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Indica si dos arreglos de nombres son identicos posicion por posicion.
     *
     * @param a primer arreglo
     * @param b segundo arreglo
     * @return {@code true} si tienen la misma longitud y el mismo contenido en el mismo orden
     * @throws IllegalArgumentException si alguno es nulo
     */
    public static boolean sonIguales(String[] a, String[] b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Los arreglos no pueden ser nulos.");
        }
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            // equals compara CONTENIDO; == compararia referencias y no sirve para String.
            if (!a[i].equals(b[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Devuelve una copia independiente del arreglo recibido.
     *
     * @param arreglo arreglo origen
     * @return nuevo arreglo con las mismas referencias
     * @throws IllegalArgumentException si {@code arreglo} es nulo
     */
    public static String[] copiar(String[] arreglo) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        String[] copia = new String[arreglo.length];
        for (int i = 0; i < arreglo.length; i++) {
            copia[i] = arreglo[i];
        }
        return copia;
    }

    /**
     * Representa el arreglo de nombres como texto entre corchetes.
     *
     * @param arreglo arreglo a formatear
     * @return cadena con los nombres entre comillas separados por coma
     * @throws IllegalArgumentException si {@code arreglo} es nulo
     */
    public static String aTexto(String[] arreglo) {
        if (arreglo == null) {
            throw new IllegalArgumentException("El arreglo no puede ser nulo.");
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arreglo.length; i++) {
            sb.append('"').append(arreglo[i]).append('"');
            if (i < arreglo.length - 1) {
                sb.append(", ");
            }
        }
        return sb.append("]").toString();
    }
}

/**
 * Acumulador de comparaciones e intercambios del ordenamiento de nombres.
 * No es publica: es soporte interno del ejercicio.
 */
class MetricasNombres {
    long comparaciones;
    long intercambios;
}

/*
 * ===========================================================================
 * MATRIZ DE CASOS DE PRUEBA MANUALES
 * ===========================================================================
 * Compilacion: javac -encoding UTF-8 Ejercicio04_SeleccionNombres.java -d /tmp/v3
 * Ejecucion:   java -cp /tmp/v3 Ejercicio04_SeleccionNombres
 *
 * ID: CP-04-01  (flujo nominal)
 *   Objetivo: ordenar alfabeticamente el arreglo del enunciado.
 *   Entrada exacta: String[] nombres = {"Lucia", "Ana", "Pedro", "Juan"} (ya en el main).
 *   Resultado esperado: ["Ana", "Juan", "Lucia", "Pedro"]; comparaciones=6 (n(n-1)/2);
 *                       intercambios=3; estaOrdenado()=true.
 *   Criterio de aprobacion: el arreglo final esta alfabeticamente ordenado y
 *                           estaOrdenado() devuelve true.
 *
 * ID: CP-04-02  (mejor caso)
 *   Objetivo: comprobar que seleccion compara igual aunque la entrada ya este ordenada.
 *   Entrada exacta: linea "Ya ordenado (mejor caso)" con {"Ana","Juan","Lucia","Pedro"}.
 *   Resultado esperado: mismo arreglo de salida, comparaciones=6, intercambios=0, ordenado=true.
 *   Criterio de aprobacion: intercambios=0 y comparaciones=6, mostrando que seleccion no
 *                           tiene mejor caso en comparaciones.
 *
 * ID: CP-04-03  (peor caso)
 *   Objetivo: ordenar la entrada invertida y confirmar la cota de intercambios n-1.
 *   Entrada exacta: linea "Ordenado al reves (peor caso)" con {"Pedro","Lucia","Juan","Ana"}.
 *   Resultado esperado: ["Ana","Juan","Lucia","Pedro"], comparaciones=6, intercambios=2,
 *                       ordenado=true.
 *   Criterio de aprobacion: intercambios <= n-1 = 3 y resultado alfabetico.
 *
 * ID: CP-04-04  (entrada vacia)
 *   Objetivo: comprobar que un arreglo sin nombres no rompe el programa.
 *   Entrada exacta: linea "Arreglo vacio" con new String[] {}.
 *   Resultado esperado: "[] -> [] | comparaciones=0 intercambios=0 | ordenado=true".
 *   Criterio de aprobacion: no se lanza excepcion y estaOrdenado() devuelve true.
 *
 * ID: CP-04-05  (un elemento)
 *   Objetivo: comprobar el caso minimo no vacio.
 *   Entrada exacta: linea "Un solo nombre" con new String[] {"Ana"}.
 *   Resultado esperado: ["Ana"] -> ["Ana"], comparaciones=0, intercambios=0, ordenado=true.
 *   Criterio de aprobacion: el ciclo externo no se ejecuta y el arreglo queda intacto.
 * ===========================================================================
 */
