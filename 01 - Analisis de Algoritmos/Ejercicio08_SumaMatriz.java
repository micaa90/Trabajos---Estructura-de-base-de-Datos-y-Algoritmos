/**
 * Ejercicio 8 del Práctico 1 (Análisis de Algoritmos): calcula la suma de todos los
 * elementos de una matriz de enteros de f filas por c columnas y contabiliza las
 * operaciones realizadas.
 *
 * <p><b>Orden de recorrido:</b> ciclos anidados por filas (row-major): el ciclo externo
 * avanza por filas y el interno por columnas de la fila actual. Se elige ese orden porque
 * en Java una matriz int[][] es un arreglo de referencias a arreglos de int: los elementos
 * de una misma fila están contiguos en memoria, así que recorrerlos seguidos aprovecha la
 * localidad espacial y la línea de caché ya cargada. El recorrido por columnas daría la
 * misma suma y la misma complejidad, pero saltaría entre objetos fila distintos.</p>
 *
 * <p><b>Qué cuenta el contador de operaciones:</b> una operación por elemento visitado,
 * entendida como el par indivisible "acceso matriz[i][j] + suma al acumulador". Con esa
 * definición el contador final vale exactamente f * c, es decir la cantidad de elementos,
 * y por eso el número es interpretable: dice cuántos elementos se procesaron.</p>
 *
 * <p><b>¿Por qué el acumulador es long y no int?</b> Porque sumar muchos int puede exceder
 * Integer.MAX_VALUE (2.147.483.647) y en Java el desbordamiento no lanza excepción: da
 * vuelta el signo en silencio y la suma sale mal. Con long el rango llega a ~9,22e18, de
 * sobra para f * c elementos int. El método {@link #demostrarDesbordamiento()} muestra el
 * error que se evita.</p>
 *
 * Complejidad: O(f * c) temporal (mejor = peor = promedio) y O(1) espacial.
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio08_SumaMatriz {

    /** Operaciones realizadas por la última invocación de {@link #sumarElementos(int[][])}. */
    private static long ultimasOperaciones = 0;

    /**
     * Método principal: ejecuta las pruebas manuales de todos los casos límite.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 8 - Suma de todos los elementos de una matriz ===");

        int[][] matriz3x4 = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12}
        };

        probar("Matriz 3x4 con valores conocidos", matriz3x4, 78L, 12);
        probar("Matriz de una sola fila", new int[][] {{5, -5, 10}}, 10L, 3);
        probar("Matriz de una sola columna", new int[][] {{1}, {2}, {3}}, 6L, 3);
        probar("Matriz vacía (cero filas)", new int[0][0], 0L, 0);
        probar("Matriz con filas de longitud cero", new int[][] {{}, {}}, 0L, 0);
        probar("Matriz con negativos", new int[][] {{-1, -2}, {-3, -4}}, -10L, 4);
        probar("Matriz irregular (dentada)", new int[][] {{1, 2, 3}, {4}, {5, 6}}, 21L, 6);
        probar("Suma que desbordaría un int", new int[][] {{Integer.MAX_VALUE, Integer.MAX_VALUE}},
                4294967294L, 2);

        probarError("Matriz nula", null);
        probarError("Matriz con una fila nula", new int[][] {{1, 2}, null});

        mostrarTrazaDetallada(new int[][] {{1, 2, 3}, {4, 5, 6}});
        demostrarDesbordamiento();
        mostrarCrecimiento();
    }

    /**
     * Suma todos los elementos de la matriz recorriéndola por filas.
     *
     * Complejidad: O(f * c) temporal, O(1) espacial.
     *
     * @param matriz matriz de enteros; admite filas de distinta longitud
     * @return suma de todos los elementos como long, para no desbordar
     * @throws IllegalArgumentException si la matriz es nula o si alguna de sus filas es nula
     */
    public static long sumarElementos(int[][] matriz) {
        validar(matriz);

        ultimasOperaciones = 0;
        long suma = 0;

        // Recorrido row-major: fila por fila, de izquierda a derecha.
        for (int fila = 0; fila < matriz.length; fila++) {
            for (int columna = 0; columna < matriz[fila].length; columna++) {
                ultimasOperaciones++;
                suma += matriz[fila][columna];
            }
        }

        return suma;
    }

    /**
     * Informa cuántas operaciones (elementos procesados) hizo la última suma.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @return cantidad de elementos visitados, igual a f * c en una matriz rectangular
     */
    public static long getUltimasOperaciones() {
        return ultimasOperaciones;
    }

    /**
     * Valida la precondición: la matriz y cada una de sus filas deben existir.
     *
     * Complejidad: O(f) temporal, O(1) espacial.
     *
     * @param matriz matriz a validar
     * @throws IllegalArgumentException si la matriz es nula o contiene una fila nula
     */
    private static void validar(int[][] matriz) {
        if (matriz == null) {
            throw new IllegalArgumentException("La matriz no puede ser nula.");
        }
        // En Java cada fila es un objeto independiente y puede ser null; detectarlo acá
        // evita un NullPointerException en medio del recorrido, con el acumulador a medias.
        for (int fila = 0; fila < matriz.length; fila++) {
            if (matriz[fila] == null) {
                throw new IllegalArgumentException("La fila " + fila + " de la matriz es nula.");
            }
        }
    }

    /**
     * Representa la matriz como texto legible, una fila por línea.
     *
     * Complejidad: O(f * c) temporal, O(f * c) espacial.
     *
     * @param matriz matriz a representar; puede ser nula
     * @return cadena multilínea con las filas, o "null" si la matriz es nula
     */
    public static String matrizATexto(int[][] matriz) {
        if (matriz == null) {
            return "null";
        }
        if (matriz.length == 0) {
            return "(matriz sin filas)";
        }
        StringBuilder texto = new StringBuilder();
        for (int fila = 0; fila < matriz.length; fila++) {
            texto.append("    ");
            if (matriz[fila] == null) {
                texto.append("null");
            } else {
                texto.append("[");
                for (int columna = 0; columna < matriz[fila].length; columna++) {
                    texto.append(matriz[fila][columna]);
                    if (columna < matriz[fila].length - 1) {
                        texto.append(", ");
                    }
                }
                texto.append("]");
            }
            if (fila < matriz.length - 1) {
                texto.append(System.lineSeparator());
            }
        }
        return texto.toString();
    }

    /**
     * Ejecuta un caso de prueba exitoso e imprime matriz, suma y operaciones.
     *
     * Complejidad: O(f * c) temporal, O(f * c) espacial.
     *
     * @param descripcion          nombre del caso
     * @param matriz               entrada del caso
     * @param sumaEsperada         suma que debe devolver el algoritmo
     * @param operacionesEsperadas cantidad de elementos que deben procesarse
     */
    public static void probar(String descripcion, int[][] matriz, long sumaEsperada, long operacionesEsperadas) {
        long obtenida = sumarElementos(matriz);
        long operaciones = getUltimasOperaciones();
        boolean ok = obtenida == sumaEsperada && operaciones == operacionesEsperadas;
        System.out.println("\n- " + descripcion);
        System.out.println("  Matriz:");
        System.out.println(matrizATexto(matriz));
        System.out.println("  Suma esperada: " + sumaEsperada + " | obtenida: " + obtenida);
        System.out.println("  Operaciones esperadas: " + operacionesEsperadas + " | realizadas: " + operaciones
                + " -> " + (ok ? "OK" : "FALLA"));
    }

    /**
     * Ejecuta un caso de prueba que debe lanzar excepción y muestra el mensaje capturado.
     *
     * Complejidad: O(f) temporal, O(1) espacial.
     *
     * @param descripcion nombre del caso
     * @param matriz      entrada inválida
     */
    public static void probarError(String descripcion, int[][] matriz) {
        System.out.println("\n- " + descripcion);
        try {
            long obtenida = sumarElementos(matriz);
            System.out.println("  Esperado: IllegalArgumentException | Obtenido: " + obtenida + " -> FALLA");
        } catch (IllegalArgumentException e) {
            System.out.println("  Esperado: IllegalArgumentException | Obtenido: "
                    + e.getClass().getSimpleName() + ": " + e.getMessage() + " -> OK");
        }
    }

    /**
     * Imprime fila, columna, elemento, acumulador y contador en cada paso del recorrido.
     *
     * Complejidad: O(f * c) temporal, O(1) espacial.
     *
     * @param matriz matriz a trazar
     * @throws IllegalArgumentException si la matriz es nula o contiene una fila nula
     */
    public static void mostrarTrazaDetallada(int[][] matriz) {
        validar(matriz);
        System.out.println("\n--- Traza del recorrido ---");
        System.out.printf("  %5s | %8s | %9s | %11s | %s%n",
                "fila", "columna", "elemento", "acumulador", "operaciones");
        long suma = 0;
        long operaciones = 0;
        for (int fila = 0; fila < matriz.length; fila++) {
            for (int columna = 0; columna < matriz[fila].length; columna++) {
                operaciones++;
                suma += matriz[fila][columna];
                System.out.printf("  %5d | %8d | %9d | %11d | %d%n",
                        fila, columna, matriz[fila][columna], suma, operaciones);
            }
        }
        System.out.println("  Suma final: " + suma + " | operaciones: " + operaciones
                + " = f * c = " + matriz.length + " * "
                + (matriz.length == 0 ? 0 : matriz[0].length));
    }

    /**
     * Muestra el error que se evitaría si el acumulador fuera int en lugar de long.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     */
    public static void demostrarDesbordamiento() {
        int[][] matriz = {{Integer.MAX_VALUE, Integer.MAX_VALUE}};
        int sumaInt = 0;
        for (int fila = 0; fila < matriz.length; fila++) {
            for (int columna = 0; columna < matriz[fila].length; columna++) {
                sumaInt += matriz[fila][columna];
            }
        }
        System.out.println("\n--- Por qué el acumulador es long ---");
        System.out.println("  Matriz: [[2147483647, 2147483647]]");
        System.out.println("  Acumulando en int  -> " + sumaInt + "  <- desbordó en silencio, sin excepción");
        System.out.println("  Acumulando en long -> " + sumarElementos(matriz) + "  <- correcto");
    }

    /**
     * Muestra cómo crece la cantidad de operaciones al agrandar las dos dimensiones.
     *
     * Complejidad: O(k) temporal con k = cantidad de tamaños mostrados, O(1) espacial.
     */
    public static void mostrarCrecimiento() {
        int[] lados = {2, 4, 8, 1000};
        System.out.println("\n--- Crecimiento multiplicativo del contador ---");
        System.out.printf("  %6s | %6s | %s%n", "f", "c", "operaciones = f * c");
        for (int lado : lados) {
            System.out.printf("  %6d | %6d | %d%n", lado, lado, (long) lado * lado);
        }
        System.out.println("  Duplicar f y c no duplica el trabajo: lo cuadruplica.");
    }
}


