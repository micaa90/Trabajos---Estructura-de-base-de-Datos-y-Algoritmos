/**
 * Ejercicio 2 del Práctico 1 (Análisis de Algoritmos): busca un elemento dentro de un
 * vector DESORDENADO de enteros e informa cuántas posiciones se recorrieron hasta
 * encontrarlo o hasta determinar que no existe.
 *
 * <p>Estrategia: búsqueda lineal (secuencial) con corte anticipado en la primera
 * coincidencia. Se devuelve la posición de la PRIMERA aparición, o -1 si no está.</p>
 *
 * <p><b>¿Por qué el desorden obliga al recorrido completo en el peor caso?</b> En un
 * vector ordenado, comparar contra un elemento informa sobre todos los que están a su
 * izquierda y a su derecha, y eso permite descartar media tabla por paso (ejercicio 3).
 * En un vector desordenado esa información no existe: saber que vector[i] != valor no
 * dice absolutamente nada sobre vector[i+1]. Cualquier posición no mirada podría contener
 * el valor buscado, así que para afirmar "no existe" hay que haber mirado las n posiciones.
 * De ahí la cota inferior Ω(n) para el caso ausente.</p>
 *
 * <p><b>Qué representa el contador:</b> la cantidad de posiciones efectivamente
 * inspeccionadas, es decir el número de comparaciones vector[i] == valor ejecutadas.
 * Si el elemento está en el índice k, el contador vale k + 1; si no está, vale n.</p>
 *
 * Complejidad: O(1) en el mejor caso, O(n) en el peor y O(n) en promedio;
 * espacial O(1).
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio02_BusquedaLineal {

    /** Valor devuelto cuando el elemento buscado no está en el vector. */
    public static final int NO_ENCONTRADO = -1;

    /** Posiciones recorridas por la última invocación de {@link #buscar(int[], int)}. */
    private static int ultimasPosicionesRecorridas = 0;

    /**
     * Método principal: ejecuta las pruebas manuales de todos los casos límite.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 2 - Búsqueda lineal en vector desordenado ===");

        int[] base = {4, 8, 15, 16, 23};

        probar("Mejor caso: elemento en la primera posición", base, 4, 0, 1);
        probar("Caso promedio: elemento en el medio (traza del análisis)", base, 15, 2, 3);
        probar("Peor caso presente: elemento en la última posición", base, 23, 4, 5);
        probar("Peor caso ausente: el elemento no existe", base, 7, NO_ENCONTRADO, 5);
        probar("Elemento repetido: devuelve la primera aparición", new int[] {5, 9, 5, 5}, 5, 0, 1);
        probar("Vector vacío: no hay nada que recorrer", new int[0], 1, NO_ENCONTRADO, 0);
        probar("Vector de un elemento que coincide", new int[] {-7}, -7, 0, 1);

        probarError("Vector nulo", null, 1);

        mostrarTrazaDetallada(base, 15);
        mostrarTrazaDetallada(base, 7);
        explicarCasoPromedio(base.length);
    }

    /**
     * Busca la primera aparición de un valor recorriendo el vector de izquierda a derecha.
     *
     * <p>El contador de posiciones recorridas queda disponible mediante
     * {@link #getUltimasPosicionesRecorridas()}: el método no imprime nada para no
     * mezclar la lógica de búsqueda con la presentación.</p>
     *
     * Complejidad: O(1) mejor caso, O(n) peor caso, O(n) promedio; O(1) espacial.
     *
     * @param vector arreglo desordenado donde buscar
     * @param valor  valor a localizar
     * @return índice de la primera aparición, o {@link #NO_ENCONTRADO} si el valor no está
     * @throws IllegalArgumentException si el vector es nulo
     */
    public static int buscar(int[] vector, int valor) {
        if (vector == null) {
            throw new IllegalArgumentException("El vector no puede ser nulo.");
        }

        ultimasPosicionesRecorridas = 0;

        for (int i = 0; i < vector.length; i++) {
            ultimasPosicionesRecorridas++;
            if (vector[i] == valor) {
                // Corte anticipado: el problema pide la PRIMERA aparición, así que
                // seguir recorriendo no podría mejorar la respuesta.
                return i;
            }
        }

        // Solo se llega acá después de inspeccionar las n posiciones: en un vector
        // desordenado es la única forma de garantizar que el valor no está.
        return NO_ENCONTRADO;
    }

    /**
     * Informa cuántas posiciones inspeccionó la última búsqueda.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @return k + 1 si el valor estaba en el índice k, o n si el valor no estaba
     */
    public static int getUltimasPosicionesRecorridas() {
        return ultimasPosicionesRecorridas;
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
     * Ejecuta un caso de prueba exitoso e imprime posición y posiciones recorridas.
     *
     * Complejidad: O(n) temporal, O(n) espacial.
     *
     * @param descripcion         nombre del caso
     * @param vector              entrada del caso
     * @param valor               valor buscado
     * @param posicionEsperada    índice esperado o {@link #NO_ENCONTRADO}
     * @param recorridosEsperados cantidad de posiciones que deberían inspeccionarse
     */
    public static void probar(String descripcion, int[] vector, int valor,
                              int posicionEsperada, int recorridosEsperados) {
        int obtenida = buscar(vector, valor);
        int recorridas = getUltimasPosicionesRecorridas();
        boolean ok = obtenida == posicionEsperada && recorridas == recorridosEsperados;
        System.out.println("\n- " + descripcion);
        System.out.println("  Vector: " + vectorATexto(vector) + " | Buscado: " + valor);
        System.out.println("  Posición esperada: " + posicionEsperada + " | obtenida: " + obtenida);
        System.out.println("  Posiciones recorridas esperadas: " + recorridosEsperados
                + " | recorridas: " + recorridas + " -> " + (ok ? "OK" : "FALLA"));
    }

    /**
     * Ejecuta un caso de prueba que debe lanzar excepción y muestra el mensaje capturado.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @param descripcion nombre del caso
     * @param vector      entrada inválida
     * @param valor       valor buscado
     */
    public static void probarError(String descripcion, int[] vector, int valor) {
        System.out.println("\n- " + descripcion);
        System.out.println("  Vector: " + vectorATexto(vector) + " | Buscado: " + valor);
        try {
            int obtenida = buscar(vector, valor);
            System.out.println("  Esperado: IllegalArgumentException | Obtenido: " + obtenida + " -> FALLA");
        } catch (IllegalArgumentException e) {
            System.out.println("  Esperado: IllegalArgumentException | Obtenido: "
                    + e.getClass().getSimpleName() + ": " + e.getMessage() + " -> OK");
        }
    }

    /**
     * Imprime la tabla de traza del recorrido: índice, comparación y contador.
     *
     * Complejidad: O(n) temporal, O(1) espacial.
     *
     * @param vector arreglo a recorrer
     * @param valor  valor buscado
     * @throws IllegalArgumentException si el vector es nulo
     */
    public static void mostrarTrazaDetallada(int[] vector, int valor) {
        if (vector == null) {
            throw new IllegalArgumentException("El vector no puede ser nulo.");
        }
        System.out.println("\n--- Traza de " + vectorATexto(vector) + " buscando " + valor + " ---");
        System.out.printf("  %3s | %9s | %-16s | %s%n", "i", "vector[i]", "vector[i] == " + valor, "contador");
        int contador = 0;
        for (int i = 0; i < vector.length; i++) {
            contador++;
            boolean coincide = vector[i] == valor;
            System.out.printf("  %3d | %9d | %-16s | %d%n", i, vector[i], coincide, contador);
            if (coincide) {
                System.out.println("  Resultado: posición " + i + " con " + contador + " posiciones recorridas.");
                return;
            }
        }
        System.out.println("  Resultado: " + NO_ENCONTRADO + " con " + contador
                + " posiciones recorridas (= n, el caso ausente siempre recorre todo).");
    }

    /**
     * Desarrolla el cálculo del número esperado de comparaciones del caso promedio,
     * asumiendo que el elemento está presente con igual probabilidad en cada posición.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @param n cantidad de elementos del vector
     * @throws IllegalArgumentException si n no es positivo
     */
    public static void explicarCasoPromedio(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n debe ser positivo para calcular el promedio.");
        }
        // Suma de 1..n dividida por n: cada posición es equiprobable (1/n) y costar i+1.
        long suma = (long) n * (n + 1) / 2;
        System.out.println("\n--- Caso promedio para n = " + n + " ---");
        System.out.println("  Comparaciones si el valor está en i: i + 1 -> 1, 2, ..., " + n);
        System.out.println("  Suma = n(n+1)/2 = " + suma + "; esperanza = suma / n = "
                + (suma / (double) n) + " = (n + 1) / 2");
        System.out.println("  Conclusión: ~" + ((n + 1) / 2.0) + " comparaciones promedio, que sigue siendo O(n).");
    }
}

/*
MATRIZ DE CASOS DE PRUEBA MANUALES
Compilación: javac Ejercicio02_BusquedaLineal.java -d /tmp/v1
Ejecución:   java -cp /tmp/v1 Ejercicio02_BusquedaLineal
Todos los casos ya están cableados en main(); no se lee entrada por consola.

ID    | Objetivo                                     | Entrada exacta                       | Resultado esperado                                              | Criterio de aprobación
------+----------------------------------------------+--------------------------------------+-----------------------------------------------------------------+---------------------------------------------------------
CP-01 | Flujo nominal: elemento presente en el medio | vector {4,8,15,16,23}, valor 15      | posición 2 y 3 posiciones recorridas                            | La línea del caso termina en "-> OK"
CP-02 | Valor límite: mejor caso en la posición 0    | vector {4,8,15,16,23}, valor 4       | posición 0 y 1 posición recorrida                               | Termina en "-> OK": el corte anticipado evita recorrer más
CP-03 | Peor caso ausente: contador igual a n       | vector {4,8,15,16,23}, valor 7       | posición -1 y 5 posiciones recorridas                           | Termina en "-> OK" y recorridas = longitud del vector
CP-04 | Entrada vacía                                | new int[0], valor 1                  | posición -1 y 0 posiciones recorridas                           | Termina en "-> OK" sin lanzar excepción
CP-05 | Entrada nula (error de validación)           | null, valor 1                        | IllegalArgumentException "El vector no puede ser nulo."         | Se imprime el mensaje de la excepción y "-> OK"
*/
