/**
 * Ejercicio 7 del Práctico 1 (Análisis de Algoritmos): invierte el orden de los elementos
 * de un vector de enteros mediante DOS versiones, y las compara.
 *
 * <p><b>Tabla comparativa</b></p>
 * <pre>
 * Versión                   | Temporal | Espacial | Modifica el original | Ventaja                              | Desventaja
 * --------------------------+----------+----------+----------------------+--------------------------------------+-------------------------------------
 * A) Con vector auxiliar    | O(n)     | O(n)     | No                   | Preserva el original (datos         | Duplica la memoria: con 100.000.000
 *                           |          |          |                      | compartidos, auditoría, deshacer)    | de int pide otros 400 MB
 * B) In situ (dos índices)  | O(n)     | O(1)     | Sí                   | Memoria constante, cabe en          | Pierde el orden original: si se
 *                           |          |          |                      | cualquier tamaño que ya esté en RAM  | necesita hay que copiarlo antes
 * </pre>
 *
 * <p>Ambas hacen n copias o n/2 intercambios, es decir Θ(n) operaciones: la diferencia real
 * está en el espacio. La versión A hace n copias (una por elemento); la B hace
 * floor(n/2) intercambios de 3 asignaciones cada uno.</p>
 *
 * Complejidad: O(n) temporal en las dos versiones; O(n) espacial con auxiliar y O(1) in situ.
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio07_InvertirVector {

    /** Copias realizadas por la última invocación de {@link #invertirConAuxiliar(int[])}. */
    private static int ultimasCopias = 0;

    /** Intercambios realizados por la última invocación de {@link #invertirEnSitio(int[])}. */
    private static int ultimosIntercambios = 0;

    /**
     * Método principal: ejecuta ambas versiones sobre los mismos casos límite.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 7 - Invertir un vector: con auxiliar contra in situ ===");

        probarAmbas("Longitud impar (traza del análisis)", new int[] {10, 20, 30, 40, 50});
        probarAmbas("Longitud par", new int[] {1, 2, 3, 4});
        probarAmbas("Dos elementos", new int[] {7, 9});
        probarAmbas("Un elemento", new int[] {5});
        probarAmbas("Vector vacío", new int[0]);
        probarAmbas("Con negativos", new int[] {-1, 0, 8, -4});

        probarError("Vector nulo");

        mostrarTrazaEnSitio(new int[] {10, 20, 30, 40, 50});
        demostrarDobleInversion(new int[] {10, 20, 30, 40, 50});
    }

    /**
     * Devuelve un NUEVO arreglo con los elementos en orden inverso, sin tocar el original.
     *
     * <p>Se recorre el original de izquierda a derecha y se escribe el auxiliar de derecha
     * a izquierda: el elemento de la posición i del original va a la posición n-1-i.</p>
     *
     * Complejidad: O(n) temporal, O(n) espacial (el arreglo auxiliar).
     *
     * @param vector arreglo a invertir; no se modifica
     * @return nuevo arreglo con los elementos en orden inverso
     * @throws IllegalArgumentException si el vector es nulo
     */
    public static int[] invertirConAuxiliar(int[] vector) {
        if (vector == null) {
            throw new IllegalArgumentException("El vector no puede ser nulo.");
        }

        ultimasCopias = 0;
        int longitud = vector.length;
        int[] auxiliar = new int[longitud];

        for (int i = 0; i < longitud; i++) {
            ultimasCopias++;
            auxiliar[longitud - 1 - i] = vector[i];
        }

        return auxiliar;
    }

    /**
     * Invierte el arreglo recibido modificándolo, sin memoria adicional significativa.
     *
     * <p>Técnica de dos índices que avanzan desde los extremos hacia el centro. Solo se
     * usa una variable temporal para el intercambio, de ahí el O(1) espacial.</p>
     *
     * Complejidad: O(n) temporal (floor(n/2) intercambios), O(1) espacial.
     *
     * @param vector arreglo que se invierte en el lugar; queda modificado al retornar
     * @throws IllegalArgumentException si el vector es nulo
     */
    public static void invertirEnSitio(int[] vector) {
        if (vector == null) {
            throw new IllegalArgumentException("El vector no puede ser nulo.");
        }

        ultimosIntercambios = 0;
        int izquierda = 0;
        int derecha = vector.length - 1;

        // El ciclo se detiene cuando los índices se cruzan, o sea en la mitad del vector.
        // Si siguiera hasta el final desharía su propio trabajo: cada par se intercambiaría
        // dos veces y el vector volvería al orden original.
        // Longitud impar: el elemento central queda en su lugar porque cuando
        // izquierda == derecha la condición ya es falsa; y su posición final ES el centro,
        // así que no necesita moverse.
        while (izquierda < derecha) {
            int temporal = vector[izquierda];
            vector[izquierda] = vector[derecha];
            vector[derecha] = temporal;
            ultimosIntercambios++;
            izquierda++;
            derecha--;
        }
    }

    /**
     * Informa cuántas copias hizo la última inversión con auxiliar.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @return cantidad de copias, igual a n
     */
    public static int getUltimasCopias() {
        return ultimasCopias;
    }

    /**
     * Informa cuántos intercambios hizo la última inversión in situ.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @return cantidad de intercambios, igual a floor(n / 2)
     */
    public static int getUltimosIntercambios() {
        return ultimosIntercambios;
    }

    /**
     * Devuelve una copia independiente del arreglo, para poder probar la versión in situ
     * sin destruir la entrada original de la prueba.
     *
     * Complejidad: O(n) temporal, O(n) espacial.
     *
     * @param vector arreglo a copiar
     * @return copia con los mismos elementos y el mismo orden
     * @throws IllegalArgumentException si el vector es nulo
     */
    public static int[] copiar(int[] vector) {
        if (vector == null) {
            throw new IllegalArgumentException("El vector no puede ser nulo.");
        }
        int[] copia = new int[vector.length];
        for (int i = 0; i < vector.length; i++) {
            copia[i] = vector[i];
        }
        return copia;
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
     * Construye el resultado esperado de la inversión, para contrastarlo con lo obtenido.
     *
     * Complejidad: O(n) temporal, O(n) espacial.
     *
     * @param vector arreglo de partida
     * @return arreglo invertido, calculado de forma independiente al algoritmo bajo prueba
     * @throws IllegalArgumentException si el vector es nulo
     */
    public static int[] invertidoEsperado(int[] vector) {
        if (vector == null) {
            throw new IllegalArgumentException("El vector no puede ser nulo.");
        }
        int[] esperado = new int[vector.length];
        for (int i = vector.length - 1, k = 0; i >= 0; i--, k++) {
            esperado[k] = vector[i];
        }
        return esperado;
    }

    /**
     * Compara dos arreglos elemento por elemento sin usar Arrays.equals.
     *
     * Complejidad: O(n) temporal, O(1) espacial.
     *
     * @param a primer arreglo
     * @param b segundo arreglo
     * @return true si tienen la misma longitud y los mismos elementos en el mismo orden
     */
    public static boolean coinciden(int[] a, int[] b) {
        if (a == null || b == null || a.length != b.length) {
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
     * Ejecuta las dos versiones sobre el mismo caso y demuestra el efecto sobre el original.
     *
     * Complejidad: O(n) temporal, O(n) espacial.
     *
     * @param descripcion nombre del caso
     * @param original    entrada del caso
     */
    public static void probarAmbas(String descripcion, int[] original) {
        int[] esperado = invertidoEsperado(original);

        System.out.println("\n- " + descripcion);
        System.out.println("  Esperado invertido: " + vectorATexto(esperado));

        int[] paraAuxiliar = copiar(original);
        int[] resultadoAuxiliar = invertirConAuxiliar(paraAuxiliar);
        boolean okAuxiliar = coinciden(resultadoAuxiliar, esperado) && coinciden(paraAuxiliar, original);
        System.out.println("  [A] con auxiliar  -> antes: " + vectorATexto(original)
                + " | devuelto: " + vectorATexto(resultadoAuxiliar)
                + " | original después: " + vectorATexto(paraAuxiliar)
                + " | copias: " + getUltimasCopias()
                + " -> " + (okAuxiliar ? "OK (original intacto)" : "FALLA"));

        int[] paraEnSitio = copiar(original);
        invertirEnSitio(paraEnSitio);
        boolean okEnSitio = coinciden(paraEnSitio, esperado);
        System.out.println("  [B] in situ       -> antes: " + vectorATexto(original)
                + " | después: " + vectorATexto(paraEnSitio)
                + " | intercambios: " + getUltimosIntercambios()
                + " (= n/2 con n = " + original.length + ")"
                + " -> " + (okEnSitio ? "OK (invertido en el lugar)" : "FALLA"));
    }

    /**
     * Ejecuta el caso nulo en ambas versiones y muestra los mensajes capturados.
     *
     * Complejidad: O(1) temporal, O(1) espacial.
     *
     * @param descripcion nombre del caso
     */
    public static void probarError(String descripcion) {
        System.out.println("\n- " + descripcion);
        try {
            invertirConAuxiliar(null);
            System.out.println("  [A] con auxiliar -> FALLA: no lanzó excepción");
        } catch (IllegalArgumentException e) {
            System.out.println("  [A] con auxiliar -> OK: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        try {
            invertirEnSitio(null);
            System.out.println("  [B] in situ      -> FALLA: no lanzó excepción");
        } catch (IllegalArgumentException e) {
            System.out.println("  [B] in situ      -> OK: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    /**
     * Imprime los índices, los valores intercambiados y el estado del vector en cada paso.
     *
     * Complejidad: O(n^2) temporal por la impresión del vector completo en cada paso, O(n) espacial.
     *
     * @param vector arreglo a trazar; queda invertido al finalizar
     * @throws IllegalArgumentException si el vector es nulo
     */
    public static void mostrarTrazaEnSitio(int[] vector) {
        if (vector == null) {
            throw new IllegalArgumentException("El vector no puede ser nulo.");
        }
        System.out.println("\n--- Traza de invertirEnSitio con " + vectorATexto(vector) + " ---");
        System.out.printf("  %4s | %10s | %8s | %-18s | %s%n",
                "iter", "izquierda", "derecha", "valores a cambiar", "vector después");
        int izquierda = 0;
        int derecha = vector.length - 1;
        int iteracion = 0;
        while (izquierda < derecha) {
            iteracion++;
            String par = "(" + vector[izquierda] + ", " + vector[derecha] + ")";
            int temporal = vector[izquierda];
            vector[izquierda] = vector[derecha];
            vector[derecha] = temporal;
            System.out.printf("  %4d | %10d | %8d | %-18s | %s%n",
                    iteracion, izquierda, derecha, par, vectorATexto(vector));
            izquierda++;
            derecha--;
        }
        System.out.println("  Corta con izquierda = " + izquierda + " y derecha = " + derecha
                + ": los índices se cruzaron en el centro.");
        if (vector.length % 2 == 1) {
            System.out.println("  Longitud impar: el elemento central vector[" + (vector.length / 2)
                    + "] = " + vector[vector.length / 2] + " nunca se intercambió.");
        }
    }

    /**
     * Demuestra que invertir dos veces devuelve el vector original.
     *
     * Complejidad: O(n) temporal, O(n) espacial.
     *
     * @param original arreglo de partida; no se modifica
     * @throws IllegalArgumentException si el vector es nulo
     */
    public static void demostrarDobleInversion(int[] original) {
        int[] trabajo = copiar(original);
        System.out.println("\n--- Doble inversión ---");
        System.out.println("  Original:            " + vectorATexto(trabajo));
        invertirEnSitio(trabajo);
        System.out.println("  Después de invertir: " + vectorATexto(trabajo));
        invertirEnSitio(trabajo);
        System.out.println("  Invertido dos veces: " + vectorATexto(trabajo)
                + " -> " + (coinciden(trabajo, original) ? "OK, volvió al original" : "FALLA"));
    }
}

/*
MATRIZ DE CASOS DE PRUEBA MANUALES
Compilación: javac Ejercicio07_InvertirVector.java -d /tmp/v1
Ejecución:   java -cp /tmp/v1 Ejercicio07_InvertirVector
Todos los casos ya están cableados en main(); no se lee entrada por consola.

ID    | Objetivo                                                | Entrada exacta            | Resultado esperado                                                        | Criterio de aprobación
------+---------------------------------------------------------+---------------------------+---------------------------------------------------------------------------+------------------------------------------------------------
CP-01 | Flujo nominal impar: ambas versiones invierten bien     | {10, 20, 30, 40, 50}      | [50, 40, 30, 20, 10]; A con 5 copias, B con 2 intercambios                | [A] dice "OK (original intacto)" y [B] "OK (invertido en el lugar)"
CP-02 | Flujo nominal par: cantidad de intercambios = n/2       | {1, 2, 3, 4}              | [4, 3, 2, 1]; A con 4 copias, B con 2 intercambios                        | Ambas líneas terminan en "OK" e intercambios = 2
CP-03 | Valor límite: un solo elemento (el ciclo no entra)      | {5}                       | [5]; A con 1 copia, B con 0 intercambios                                  | Ambas terminan en "OK" e intercambios = 0
CP-04 | Entrada vacía                                           | new int[0]                | [] en ambas; 0 copias y 0 intercambios, sin excepción                     | Ambas terminan en "OK" y no hay stack trace
CP-05 | Entrada nula (error de validación)                      | null                      | IllegalArgumentException "El vector no puede ser nulo." en ambas versiones | Se imprimen las dos líneas "-> OK:" con el mensaje
*/
