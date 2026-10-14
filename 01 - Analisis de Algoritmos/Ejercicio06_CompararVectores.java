/**
 * Ejercicio 6 del Práctico 1 (Análisis de Algoritmos): determina si dos vectores de
 * enteros son iguales, entendiendo igualdad como misma longitud y mismos elementos en
 * las mismas posiciones. No se usa Arrays.equals: la comparación se implementa a mano.
 *
 * <p><b>¿Por qué es correcto el corte anticipado?</b> La igualdad de vectores es una
 * conjunción: a es igual a b si y solo si a[0]==b[0] Y a[1]==b[1] Y ... Y a[n-1]==b[n-1].
 * En una conjunción, un solo término falso vuelve falsa toda la expresión, sin importar el
 * valor de los demás. Por lo tanto, en cuanto se detecta a[i] != b[i] la respuesta ya está
 * decidida y seguir comparando solo gastaría tiempo. Es lo contrario del ejercicio 5, donde
 * la respuesta era una cantidad agregada y exigía ver todo el vector.</p>
 *
 * <p><b>¿Por qué conviene comparar primero las longitudes?</b> Porque es una verificación
 * O(1) que puede descartar el caso en un solo paso, sin recorrer nada, y además garantiza
 * que el ciclo posterior pueda indexar ambos vectores con el mismo índice sin riesgo de
 * ArrayIndexOutOfBoundsException.</p>
 *
 * <p><b>Mejor, peor y promedio:</b> el mejor caso (O(1), 0 comparaciones de elementos) lo
 * producen longitudes distintas; el casi-mejor caso es diferir en la posición 0
 * (1 comparación). El peor caso (O(n), n comparaciones) lo producen vectores iguales o que
 * difieren recién en la última posición: hay que mirar todo. El caso promedio, con
 * diferencias ubicadas al azar, es n/2 comparaciones, que sigue siendo O(n).</p>
 *
 * Complejidad: O(1) mejor caso, O(n) peor caso, O(n) promedio; O(1) espacial.
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio06_CompararVectores {

    /** Comparaciones de elementos realizadas por la última invocación de sonIguales. */
    private static int ultimasComparaciones = 0;

    /**
     * Método principal: ejecuta las pruebas manuales de todos los casos límite.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 6 - Comparar dos vectores con corte anticipado ===");

        probar("Peor caso: iguales, hay que mirar todo", new int[] {1, 2, 3}, new int[] {1, 2, 3}, true, 3);
        probar("Mejor caso real: difieren en la primera posición", new int[] {1, 2, 3}, new int[] {9, 2, 3}, false, 1);
        probar("Peor caso: difieren en la última posición", new int[] {1, 2, 3}, new int[] {1, 2, 9}, false, 3);
        probar("Mejor caso absoluto: longitudes distintas", new int[] {1, 2, 3}, new int[] {1, 2}, false, 0);
        probar("Ambos vacíos: iguales por definición", new int[0], new int[0], true, 0);
        probar("Uno vacío y el otro no", new int[0], new int[] {1}, false, 0);
        probar("Un elemento, iguales", new int[] {-4}, new int[] {-4}, true, 1);
        probar("Mismo objeto comparado consigo mismo", new int[] {5, 6}, new int[] {5, 6}, true, 2);

        probarError("Primer vector nulo", null, new int[] {1});
        probarError("Segundo vector nulo", new int[] {1}, null);
        probarError("Ambos nulos", null, null);

        mostrarTrazaDetallada(new int[] {1, 2, 3}, new int[] {1, 2, 3});
        mostrarTrazaDetallada(new int[] {1, 2, 3}, new int[] {9, 2, 3});
        mostrarTrazaDetallada(new int[] {1, 2, 3}, new int[] {1, 2, 9});
    }

    /**
     * Compara dos vectores posición por posición y corta en la primera diferencia.
     *
     * Complejidad: O(1) mejor caso, O(n) peor caso y promedio; O(1) espacial.
     *
     * @param a primer vector
     * @param b segundo vector
     * @return true si tienen la misma longitud y los mismos elementos en el mismo orden
     * @throws IllegalArgumentException si alguno de los vectores es nulo, porque "nulo"
     *                                  no es un vector y la igualdad no estaría definida
     */
    public static boolean sonIguales(int[] a, int[] b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Ninguno de los vectores puede ser nulo.");
        }

        ultimasComparaciones = 0;

        // Verificación O(1) previa: si las longitudes difieren ya son distintos, y además
        // así el ciclo puede indexar ambos vectores con un único índice sin salirse de rango.
        if (a.length != b.length) {
            return false;
        }

        for (int i = 0; i < a.length; i++) {
            ultimasComparaciones++;
            if (a[i] != b[i]) {
                // Corte anticipado: la igualdad es una conjunción, un término falso
                // ya determina el resultado final.
                return false;
            }
        }

        return true;
    }

    /**
     * Informa cuántas comparaciones de elementos hizo la última comparación de vectores.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @return 0 si se cortó por longitudes distintas, k si la diferencia estaba en k-1,
     *         o n si los vectores resultaron iguales
     */
    public static int getUltimasComparaciones() {
        return ultimasComparaciones;
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
     * Ejecuta un caso de prueba exitoso e imprime resultado y comparaciones realizadas.
     *
     * Complejidad: O(n) temporal, O(n) espacial.
     *
     * @param descripcion            nombre del caso
     * @param a                      primer vector
     * @param b                      segundo vector
     * @param esperado               resultado esperado de la comparación
     * @param comparacionesEsperadas comparaciones de elementos esperadas
     */
    public static void probar(String descripcion, int[] a, int[] b,
                              boolean esperado, int comparacionesEsperadas) {
        boolean obtenido = sonIguales(a, b);
        int comparaciones = getUltimasComparaciones();
        boolean ok = obtenido == esperado && comparaciones == comparacionesEsperadas;
        System.out.println("\n- " + descripcion);
        System.out.println("  a = " + vectorATexto(a) + " | b = " + vectorATexto(b));
        System.out.println("  Esperado: " + esperado + " | Obtenido: " + obtenido);
        System.out.println("  Comparaciones esperadas: " + comparacionesEsperadas
                + " | realizadas: " + comparaciones + " -> " + (ok ? "OK" : "FALLA"));
    }

    /**
     * Ejecuta un caso de prueba que debe lanzar excepción y muestra el mensaje capturado.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @param descripcion nombre del caso
     * @param a           primer vector, posiblemente nulo
     * @param b           segundo vector, posiblemente nulo
     */
    public static void probarError(String descripcion, int[] a, int[] b) {
        System.out.println("\n- " + descripcion);
        System.out.println("  a = " + vectorATexto(a) + " | b = " + vectorATexto(b));
        try {
            boolean obtenido = sonIguales(a, b);
            System.out.println("  Esperado: IllegalArgumentException | Obtenido: " + obtenido + " -> FALLA");
        } catch (IllegalArgumentException e) {
            System.out.println("  Esperado: IllegalArgumentException | Obtenido: "
                    + e.getClass().getSimpleName() + ": " + e.getMessage() + " -> OK");
        }
    }

    /**
     * Imprime la comparación elemento por elemento hasta la decisión.
     *
     * Complejidad: O(n) temporal, O(1) espacial.
     *
     * @param a primer vector
     * @param b segundo vector
     * @throws IllegalArgumentException si alguno de los vectores es nulo
     */
    public static void mostrarTrazaDetallada(int[] a, int[] b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Ninguno de los vectores puede ser nulo.");
        }
        System.out.println("\n--- Traza de " + vectorATexto(a) + " contra " + vectorATexto(b) + " ---");
        if (a.length != b.length) {
            System.out.println("  Longitudes distintas (" + a.length + " != " + b.length
                    + "): decide en 0 comparaciones de elementos.");
            return;
        }
        System.out.printf("  %3s | %6s | %6s | %-11s | %s%n", "i", "a[i]", "b[i]", "¿iguales?", "comparaciones");
        int comparaciones = 0;
        for (int i = 0; i < a.length; i++) {
            comparaciones++;
            boolean iguales = a[i] == b[i];
            System.out.printf("  %3d | %6d | %6d | %-11s | %d%n", i, a[i], b[i], iguales, comparaciones);
            if (!iguales) {
                System.out.println("  Corta acá: devuelve false tras " + comparaciones + " comparaciones.");
                return;
            }
        }
        System.out.println("  Devuelve true tras " + comparaciones
                + " comparaciones (= n): los iguales son siempre el peor caso.");
    }
}

/*
MATRIZ DE CASOS DE PRUEBA MANUALES
Compilación: javac Ejercicio06_CompararVectores.java -d /tmp/v1
Ejecución:   java -cp /tmp/v1 Ejercicio06_CompararVectores
Todos los casos ya están cableados en main(); no se lee entrada por consola.

ID    | Objetivo                                        | Entrada exacta                      | Resultado esperado                                           | Criterio de aprobación
------+-------------------------------------------------+-------------------------------------+--------------------------------------------------------------+-----------------------------------------------------------
CP-01 | Flujo nominal: vectores iguales (peor caso)     | a={1,2,3}, b={1,2,3}                | true con 3 comparaciones                                     | Termina en "-> OK" y comparaciones = 3 (= n)
CP-02 | Corte anticipado en la primera posición         | a={1,2,3}, b={9,2,3}                | false con 1 comparación                                      | Termina en "-> OK" y comparaciones = 1
CP-03 | Valor límite: diferencia en la última posición  | a={1,2,3}, b={1,2,9}                | false con 3 comparaciones                                    | Termina en "-> OK" y comparaciones = 3, igual que CP-01
CP-04 | Entrada vacía y longitudes distintas            | a=new int[0], b=new int[0]; a={1,2,3}, b={1,2} | true con 0 comparaciones; false con 0 comparaciones | Ambas filas terminan en "-> OK" con comparaciones = 0
CP-05 | Entrada nula (error de validación)              | a=null, b={1}                       | IllegalArgumentException "Ninguno de los vectores puede ser nulo." | Se imprime el mensaje de la excepción y "-> OK"
*/
