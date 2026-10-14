/**
 * Ejercicio 9 del Práctico 1 (Análisis de Algoritmos): encuentra el mayor elemento de una
 * matriz de enteros e informa también su posición (fila y columna).
 *
 * <p><b>¿Por qué es NECESARIO recorrer todos los elementos?</b> Porque no se conoce ninguna
 * propiedad previa de los datos (no están ordenados ni acotados). Supongamos un algoritmo
 * que devuelve una respuesta habiendo mirado solo f*c - 1 elementos: siempre se puede
 * construir una matriz en la que el elemento no mirado valga más que todos los demás, y el
 * algoritmo se equivocaría. Es el argumento del adversario: ningún elemento puede quedar sin
 * examinarse, por lo tanto la cota inferior del problema es Ω(f * c) y el algoritmo trivial
 * de un solo barrido ya es óptimo. Si en cambio la matriz estuviera ordenada por filas y
 * columnas, el máximo estaría en una esquina y se resolvería en O(1): la diferencia la hace
 * la información previa, no el algoritmo.</p>
 *
 * <p><b>¿Por qué el máximo se inicializa con el primer elemento REAL y no con 0 ni con
 * Integer.MIN_VALUE?</b> Inicializar en 0 es un error directo: si todos los elementos son
 * negativos, el 0 nunca es superado y se devolvería 0, que no pertenece a la matriz.
 * Integer.MIN_VALUE funcionaría para el valor, pero deja la posición indefinida hasta la
 * primera comparación exitosa y falla conceptualmente si la matriz contiene exactamente
 * Integer.MIN_VALUE. Usar matriz[0][0] garantiza que el máximo provisorio sea siempre un
 * elemento existente y que la posición reportada sea válida desde el primer instante.</p>
 *
 * Complejidad: O(f * c) temporal (mejor = peor = promedio, no hay corte posible)
 * y O(1) espacial.
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio09_MayorMatriz {

    /** Comparaciones realizadas por la última invocación de {@link #buscarMaximo(int[][])}. */
    private static long ultimasComparaciones = 0;

    /**
     * Método principal: ejecuta las pruebas manuales de todos los casos límite.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 9 - Mayor elemento de una matriz ===");

        probar("Caso nominal (traza del análisis)", new int[][] {{3, -8, 5}, {-2, 12, 0}}, 12, 1, 1);
        probar("Un solo elemento", new int[][] {{7}}, 7, 0, 0);
        probar("Todos iguales: devuelve la primera aparición", new int[][] {{4, 4}, {4, 4}}, 4, 0, 0);
        probar("Todos negativos", new int[][] {{-9, -3}, {-15, -7}}, -3, 0, 1);
        probar("Máximo en la primera posición", new int[][] {{99, 1, 2}, {3, 4, 5}}, 99, 0, 0);
        probar("Máximo en la última posición", new int[][] {{1, 2}, {3, 50}}, 50, 1, 1);
        probar("Matriz de una sola fila", new int[][] {{5, 8, 2}}, 8, 0, 1);
        probar("Matriz irregular (dentada)", new int[][] {{1}, {2, 30}, {4}}, 30, 1, 1);
        probar("Valor extremo mínimo de int", new int[][] {{Integer.MIN_VALUE, Integer.MIN_VALUE}},
                Integer.MIN_VALUE, 0, 0);

        probarError("Matriz nula");
        probarError2("Matriz sin filas", new int[0][0]);
        probarError2("Matriz con filas vacías", new int[][] {{}, {}});
        probarError2("Matriz con una fila nula", new int[][] {{1}, null});

        mostrarTrazaDetallada(new int[][] {{3, -8, 5}, {-2, 12, 0}});
        demostrarInicializacionEnCero(new int[][] {{-9, -3}, {-15, -7}});
    }

    /**
     * Busca el mayor elemento de la matriz y su posición.
     *
     * <p>Devuelve la PRIMERA aparición del máximo en el recorrido por filas, porque la
     * actualización solo ocurre con una desigualdad estricta.</p>
     *
     * Complejidad: O(f * c) temporal (mejor = peor), O(1) espacial.
     *
     * @param matriz matriz de enteros con al menos un elemento
     * @return resultado con el valor máximo y su fila y columna
     * @throws IllegalArgumentException si la matriz es nula, si alguna fila es nula o si
     *                                  no contiene ningún elemento, porque el máximo de un
     *                                  conjunto vacío no está definido
     */
    public static ResultadoMaximo buscarMaximo(int[][] matriz) {
        validar(matriz);

        ultimasComparaciones = 0;

        // El máximo provisorio es un elemento REAL de la matriz: así nunca se devuelve un
        // valor ajeno a los datos (como pasaría inicializando en 0 con datos negativos) y
        // la posición reportada es válida desde el comienzo.
        int primeraFilaNoVacia = indicePrimeraFilaNoVacia(matriz);
        int maximo = matriz[primeraFilaNoVacia][0];
        int filaMaximo = primeraFilaNoVacia;
        int columnaMaximo = 0;

        for (int fila = primeraFilaNoVacia; fila < matriz.length; fila++) {
            for (int columna = 0; columna < matriz[fila].length; columna++) {
                // Se compara también el elemento inicial contra sí mismo en la primera
                // vuelta; es una comparación de más pero mantiene el ciclo uniforme.
                ultimasComparaciones++;
                if (matriz[fila][columna] > maximo) {
                    maximo = matriz[fila][columna];
                    filaMaximo = fila;
                    columnaMaximo = columna;
                }
            }
        }

        return new ResultadoMaximo(maximo, filaMaximo, columnaMaximo);
    }

    /**
     * Informa cuántas comparaciones hizo la última búsqueda del máximo.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @return cantidad de comparaciones, igual a la cantidad de elementos visitados
     */
    public static long getUltimasComparaciones() {
        return ultimasComparaciones;
    }

    /**
     * Valida que la matriz exista, no tenga filas nulas y contenga al menos un elemento.
     *
     * Complejidad: O(f) temporal, O(1) espacial.
     *
     * @param matriz matriz a validar
     * @throws IllegalArgumentException si la matriz es nula, tiene una fila nula o está vacía
     */
    private static void validar(int[][] matriz) {
        if (matriz == null) {
            throw new IllegalArgumentException("La matriz no puede ser nula.");
        }
        for (int fila = 0; fila < matriz.length; fila++) {
            if (matriz[fila] == null) {
                throw new IllegalArgumentException("La fila " + fila + " de la matriz es nula.");
            }
        }
        if (cantidadElementos(matriz) == 0) {
            throw new IllegalArgumentException("La matriz no contiene elementos: el máximo no está definido.");
        }
    }

    /**
     * Cuenta cuántos elementos contiene la matriz, admitiendo filas de distinta longitud.
     *
     * Complejidad: O(f) temporal, O(1) espacial.
     *
     * @param matriz matriz no nula y sin filas nulas
     * @return cantidad total de elementos
     */
    private static int cantidadElementos(int[][] matriz) {
        int total = 0;
        for (int fila = 0; fila < matriz.length; fila++) {
            total += matriz[fila].length;
        }
        return total;
    }

    /**
     * Localiza la primera fila con al menos un elemento, para inicializar el máximo.
     *
     * Complejidad: O(f) temporal, O(1) espacial.
     *
     * @param matriz matriz con al menos un elemento
     * @return índice de la primera fila no vacía
     */
    private static int indicePrimeraFilaNoVacia(int[][] matriz) {
        for (int fila = 0; fila < matriz.length; fila++) {
            if (matriz[fila].length > 0) {
                return fila;
            }
        }
        // Inalcanzable: validar() ya garantizó que existe al menos un elemento.
        throw new IllegalStateException("La matriz debería tener al menos un elemento.");
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
            return "    (matriz sin filas)";
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
     * Ejecuta un caso de prueba exitoso e imprime matriz, máximo, posición y esperado.
     *
     * Complejidad: O(f * c) temporal, O(f * c) espacial.
     *
     * @param descripcion     nombre del caso
     * @param matriz          entrada del caso
     * @param maximoEsperado  valor máximo esperado
     * @param filaEsperada    fila esperada del máximo
     * @param columnaEsperada columna esperada del máximo
     */
    public static void probar(String descripcion, int[][] matriz,
                              int maximoEsperado, int filaEsperada, int columnaEsperada) {
        ResultadoMaximo obtenido = buscarMaximo(matriz);
        boolean ok = obtenido.valor == maximoEsperado
                && obtenido.fila == filaEsperada
                && obtenido.columna == columnaEsperada;
        System.out.println("\n- " + descripcion);
        System.out.println("  Matriz:");
        System.out.println(matrizATexto(matriz));
        System.out.println("  Esperado: valor " + maximoEsperado
                + " en (" + filaEsperada + ", " + columnaEsperada + ")");
        System.out.println("  Obtenido: " + obtenido
                + " | Comparaciones: " + getUltimasComparaciones()
                + " -> " + (ok ? "OK" : "FALLA"));
    }

    /**
     * Ejecuta el caso de matriz nula y muestra el mensaje capturado.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @param descripcion nombre del caso
     */
    public static void probarError(String descripcion) {
        probarError2(descripcion, null);
    }

    /**
     * Ejecuta un caso de prueba que debe lanzar excepción y muestra el mensaje capturado.
     *
     * Complejidad: O(f) temporal, O(f * c) espacial por el texto de la matriz.
     *
     * @param descripcion nombre del caso
     * @param matriz      entrada inválida
     */
    public static void probarError2(String descripcion, int[][] matriz) {
        System.out.println("\n- " + descripcion);
        System.out.println("  Matriz:");
        System.out.println(matrizATexto(matriz));
        try {
            ResultadoMaximo obtenido = buscarMaximo(matriz);
            System.out.println("  Esperado: IllegalArgumentException | Obtenido: " + obtenido + " -> FALLA");
        } catch (IllegalArgumentException e) {
            System.out.println("  Esperado: IllegalArgumentException | Obtenido: "
                    + e.getClass().getSimpleName() + ": " + e.getMessage() + " -> OK");
        }
    }

    /**
     * Imprime el elemento visitado, el máximo actual y su posición en cada paso.
     *
     * Complejidad: O(f * c) temporal, O(1) espacial.
     *
     * @param matriz matriz a trazar
     * @throws IllegalArgumentException si la matriz es inválida según {@link #validar(int[][])}
     */
    public static void mostrarTrazaDetallada(int[][] matriz) {
        validar(matriz);
        System.out.println("\n--- Traza del recorrido ---");
        System.out.printf("  %5s | %8s | %9s | %-12s | %7s | %s%n",
                "fila", "columna", "elemento", "¿supera max?", "maximo", "posición");
        int maximo = matriz[0][0];
        int filaMaximo = 0;
        int columnaMaximo = 0;
        for (int fila = 0; fila < matriz.length; fila++) {
            for (int columna = 0; columna < matriz[fila].length; columna++) {
                boolean supera = matriz[fila][columna] > maximo;
                if (supera) {
                    maximo = matriz[fila][columna];
                    filaMaximo = fila;
                    columnaMaximo = columna;
                }
                System.out.printf("  %5d | %8d | %9d | %-12s | %7d | (%d, %d)%n",
                        fila, columna, matriz[fila][columna], supera, maximo, filaMaximo, columnaMaximo);
            }
        }
        System.out.println("  Máximo final: " + maximo + " en (" + filaMaximo + ", " + columnaMaximo
                + "); se visitaron todos los elementos, mejor caso = peor caso.");
    }

    /**
     * Muestra el resultado incorrecto que daría inicializar el máximo en cero.
     *
     * Complejidad: O(f * c) temporal, O(1) espacial.
     *
     * @param matriz matriz con todos sus valores negativos
     * @throws IllegalArgumentException si la matriz es inválida según {@link #validar(int[][])}
     */
    public static void demostrarInicializacionEnCero(int[][] matriz) {
        validar(matriz);
        int maximoMalInicializado = 0;
        for (int fila = 0; fila < matriz.length; fila++) {
            for (int columna = 0; columna < matriz[fila].length; columna++) {
                if (matriz[fila][columna] > maximoMalInicializado) {
                    maximoMalInicializado = matriz[fila][columna];
                }
            }
        }
        System.out.println("\n--- Por qué no se inicializa el máximo en 0 ---");
        System.out.println("  Matriz (toda negativa):");
        System.out.println(matrizATexto(matriz));
        System.out.println("  Inicializando en 0          -> " + maximoMalInicializado
                + "  <- valor que NO pertenece a la matriz");
        System.out.println("  Inicializando en matriz[0][0] -> " + buscarMaximo(matriz) + "  <- correcto");
    }

    /**
     * Resultado inmutable de la búsqueda: valor máximo y su posición en la matriz.
     */
    static final class ResultadoMaximo {

        /** Valor máximo encontrado. */
        final int valor;

        /** Fila donde se encontró la primera aparición del máximo. */
        final int fila;

        /** Columna donde se encontró la primera aparición del máximo. */
        final int columna;

        /**
         * Crea el resultado de la búsqueda del máximo.
         *
         * @param valor   valor máximo encontrado
         * @param fila    fila del máximo
         * @param columna columna del máximo
         */
        ResultadoMaximo(int valor, int fila, int columna) {
            this.valor = valor;
            this.fila = fila;
            this.columna = columna;
        }

        /**
         * Describe el resultado en una línea.
         *
         * @return texto con el valor y la posición
         */
        @Override
        public String toString() {
            return "valor " + valor + " en (" + fila + ", " + columna + ")";
        }
    }
}


