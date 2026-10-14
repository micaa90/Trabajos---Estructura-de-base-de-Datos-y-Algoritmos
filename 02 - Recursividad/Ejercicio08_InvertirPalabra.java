/*
 * =====================================================================================
 * Práctico 2 — Recursividad. Ejercicio 8: Invertir una palabra
 * =====================================================================================
 *
 * Prompt inicial utilizado:
 *
 * "Analizá este problema para resolverlo recursivamente: invertir un String.
 *  Tu análisis debe responder:
 *  - cuál es el caso base para una cadena vacía o de un solo carácter, y por qué ambos casos
 *    devuelven la cadena tal cual;
 *  - cómo separar el primer carácter del resto (usando charAt y substring);
 *  - cómo se reduce la cadena en cada llamada;
 *  - cómo se reconstruye el resultado al volver de la recursión, es decir por qué el resultado
 *    es invertir(resto) + primerCaracter;
 *  - qué palabras usar como prueba: una vacía, una de un carácter, una de longitud par, una
 *    de longitud impar y una ya simétrica;
 *  - por qué esta solución tiene complejidad espacial mayor a O(n), considerando que cada
 *    substring crea un nuevo String y que la pila guarda una cadena por nivel.
 *  Indicá complejidad temporal y espacial, y mencioná por qué una solución con dos índices
 *  sobre un arreglo de chars sería más eficiente.
 *  No escribas la implementación."
 *
 * Ajustes realizados luego de la primera respuesta:
 *
 *  1) Se agregó la pregunta por la complejidad espacial "mayor a O(n)". La primera respuesta
 *     informaba O(n) espacial contando solo los marcos de la pila, como si el algoritmo no
 *     asignara nada. En realidad cada nivel crea un substring nuevo de longitud n-1, n-2, ...,
 *     y cada retorno crea otra cadena por la concatenación: la suma de esas longitudes es
 *     n(n+1)/2, es decir O(n^2) caracteres asignados en el heap. Pedirlo explícitamente obligó
 *     a contar las asignaciones y no solo la profundidad.
 *  2) Se agregó el tratamiento de null como caso distinto de la cadena vacía. La primera
 *     respuesta trataba los dos juntos; son cosas diferentes: "" es una entrada válida del
 *     dominio cuyo inverso es "", mientras que null no es una cadena y haría fallar la primera
 *     línea del método con NullPointerException desde dentro de la recursión, con una traza
 *     poco informativa.
 *  3) Se agregó la prohibición de StringBuilder.reverse() y el pedido de comparar con la
 *     alternativa de dos índices sobre char[]. Sin la prohibición la respuesta se resolvía en
 *     una línea delegando en la biblioteca estándar, que es lo correcto en producción pero no
 *     ejercita la reconstrucción del resultado al desapilar, que es el objetivo del práctico.
 *
 * Estrategia: charAt(0) separa el primer carácter y substring(1) el resto; cada nivel devuelve
 * el inverso del resto concatenado con su propio primer carácter, con caso base longitud <= 1.
 *
 * Complejidad temporal: O(n^2) — n llamadas, y cada una copia una cadena de hasta n caracteres
 * (una vez por el substring y otra por la concatenación).
 * Complejidad espacial: O(n) marcos de pila, pero O(n^2) caracteres asignados en el heap,
 * porque cada nivel mantiene viva su propia copia de la subcadena.
 * =====================================================================================
 */

/**
 * Inversión recursiva de una cadena de caracteres, sin ciclos y sin
 * {@code StringBuilder.reverse()}.
 *
 * <p>La idea central es que invertir una palabra es invertir todo menos su primera letra y
 * después pegar esa primera letra al final. La parte "invertir todo menos la primera letra" es
 * el mismo problema sobre una cadena más corta, y por eso admite solución recursiva.</p>
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio08_InvertirPalabra {

    /**
     * Longitud máxima admitida. La profundidad de la pila es igual a la longitud de la cadena,
     * así que se acota para fallar con un mensaje claro antes de que la JVM lance
     * {@code StackOverflowError}. El límite es conservador y muy superior a cualquier palabra.
     */
    private static final int MAX_LONGITUD_RECURSION_SEGURA = 10_000;

    /**
     * Devuelve una cadena nueva con los caracteres de {@code palabra} en orden inverso.
     *
     * <p>Descomposición utilizada: {@code "casa"} se parte en el primer carácter {@code 'c'}
     * (obtenido con {@code charAt(0)}) y el resto {@code "asa"} (obtenido con
     * {@code substring(1)}). El resultado es el inverso del resto seguido del primer carácter,
     * y la concatenación ocurre al <b>volver</b> de la recursión, no al bajar.</p>
     *
     * <p>Costo en memoria de los substring sucesivos: la llamada de nivel i recibe una cadena
     * de longitud n-i+1 y asigna un objeto {@code String} nuevo de longitud n-i para pasarlo al
     * nivel siguiente, porque {@code substring} copia los caracteres (no comparte el arreglo
     * interno desde Java 7). Al desapilar, cada {@code +} vuelve a asignar otra cadena. Para una
     * palabra de n caracteres se crean del orden de 2n objetos {@code String} y se copian
     * n(n+1)/2 caracteres, es decir O(n^2) de trabajo y de memoria asignada, aunque solo O(n)
     * marcos coexistan en la pila.</p>
     *
     * @param palabra cadena a invertir; puede ser vacía, pero no {@code null}
     * @return una cadena nueva con los caracteres de {@code palabra} en orden inverso; la
     *         cadena vacía cuando {@code palabra} es vacía
     * @throws IllegalArgumentException si {@code palabra} es {@code null} —no es una cadena del
     *         dominio y provocaría un {@code NullPointerException} dentro de la recursión— o si
     *         su longitud excede {@value #MAX_LONGITUD_RECURSION_SEGURA}, porque la pila de
     *         llamadas podría desbordar
     *         <p>Complejidad: O(n^2) temporal; O(n) marcos de pila y O(n^2) caracteres
     *         asignados en el heap, con n = longitud de la cadena.</p>
     */
    public static String invertir(String palabra) {
        if (palabra == null) {
            throw new IllegalArgumentException("No se admite null: null no es una cadena vacía, "
                    + "y llamar a length() sobre él fallaría dentro de la recursión.");
        }
        if (palabra.length() > MAX_LONGITUD_RECURSION_SEGURA) {
            throw new IllegalArgumentException("La cadena tiene " + palabra.length()
                    + " caracteres y excede el máximo de recursión segura ("
                    + MAX_LONGITUD_RECURSION_SEGURA + "): la pila de llamadas podría desbordar.");
        }

        // CASO BASE: una cadena de 0 o 1 caracteres ya es su propio inverso, porque no hay
        // ningún par de caracteres que intercambiar. Las dos condiciones se cubren con una sola
        // comparación (length() <= 1). Acá la pila deja de crecer y empieza el desapilado.
        if (palabra.length() <= 1) {
            return palabra;
        }

        // charAt(0) devuelve el PRIMER carácter: el que debe terminar en la última posición.
        char primerCaracter = palabra.charAt(0);

        // substring(1) devuelve TODO lo que viene después del primer carácter, es decir el
        // mismo problema con un carácter menos. Esta es la reducción: la longitud baja en 1 por
        // llamada y está acotada por abajo por 0, así que el caso base siempre se alcanza.
        // Cada substring es un objeto String NUEVO: ahí está el costo en memoria del enfoque.
        String resto = palabra.substring(1);

        // CASO RECURSIVO: se invierte el resto y se pega el primer carácter AL FINAL. La
        // concatenación queda pendiente en el marco mientras la llamada interna trabaja; se
        // ejecuta al DESAPILAR. Por eso el primer carácter del original termina último: su
        // concatenación es la del nivel más externo, que es el último en resolverse.
        return invertir(resto) + primerCaracter;
    }

    /**
     * Ejecuta un caso válido e imprime la cadena invertida obtenida frente a la esperada.
     *
     * @param palabra cadena de entrada a probar
     * @param esperado inversión esperada, calculada a mano
     */
    private static void probar(String palabra, String esperado) {
        try {
            String obtenido = invertir(palabra);
            System.out.printf("original = %-12s | obtenido = %-12s | esperado = %-12s | %s%n",
                    "\"" + palabra + "\"", "\"" + obtenido + "\"", "\"" + esperado + "\"",
                    obtenido.equals(esperado) ? "OK" : "FALLA");
        } catch (IllegalArgumentException e) {
            System.out.printf("original = %-12s | excepción inesperada: %s%n",
                    "\"" + palabra + "\"", e.getMessage());
        }
    }

    /**
     * Ejecuta un caso que debe terminar en excepción y verifica que se capture.
     *
     * @param palabra cadena de entrada a probar
     * @param descripcion descripción del caso para la salida por consola
     */
    private static void probarError(String palabra, String descripcion) {
        try {
            String obtenido = invertir(palabra);
            System.out.printf("%-28s | se esperaba IllegalArgumentException pero devolvió \"%s\" | FALLA%n",
                    descripcion, obtenido);
        } catch (IllegalArgumentException e) {
            System.out.printf("%-28s | esperado = IllegalArgumentException | obtenido = %s | OK%n",
                    descripcion, e.getMessage());
        }
    }

    /**
     * Punto de entrada. Ejecuta los valores de prueba definidos en el análisis.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 8: Inversión recursiva de un String ===");
        System.out.println("Casos del enunciado (inversión esperada escrita a mano):");
        probar("", "");                        // caso base: cadena vacía
        probar("a", "a");                      // caso base: un solo carácter
        probar("algoritmo", "omtirogla");      // longitud impar (9), caso general
        probar("ana", "ana");                  // ya simétrica: el inverso coincide con el original
        probar("java", "avaj");                // longitud par (4)

        System.out.println();
        System.out.println("Casos adicionales:");
        probar("casa", "asac");                // la palabra trazada en el análisis
        probar("ab", "ba");                    // longitud mínima que recursa
        probar("  a", "a  ");                  // los espacios son caracteres como cualquier otro

        System.out.println();
        System.out.println("Casos de error (se capturan con try/catch):");
        probarError(null, "invertir(null)");
        probarError("x".repeat(MAX_LONGITUD_RECURSION_SEGURA + 1),
                "cadena de 10001 caracteres");
    }
}

/*
 * =====================================================================================
 * AJUSTES REALIZADOS AL PROMPT 
 * =====================================================================================
 * Cambio 1: se agregó "por qué esta solución tiene complejidad espacial mayor a O(n)".
 *   Motivo: la primera respuesta declaraba O(n) espacial contando únicamente los marcos de la
 *   pila. Faltaba lo más caro: cada nivel asigna un substring nuevo (n-1, n-2, ... caracteres)
 *   y cada retorno asigna otra cadena por la concatenación. El total copiado es n(n+1)/2
 *   caracteres, O(n^2). La pregunta explícita forzó a contar asignaciones, no solo profundidad.
 * Cambio 2: se separó null de la cadena vacía.
 *   Motivo: la primera respuesta los mencionaba como "el mismo caso base". No lo son: "" es una
 *   entrada legítima cuyo inverso es "", mientras que null rompía el método en palabra.length()
 *   con un NullPointerException lanzado desde el interior de la recursión. Se decidió validar
 *   null por adelantado con IllegalArgumentException y un mensaje que explique la diferencia.
 * Cambio 3: se agregó la prohibición de StringBuilder.reverse() y el pedido de comparar con la
 *   alternativa de dos índices sobre un arreglo de chars.
 *   Motivo: sin la prohibición, la primera respuesta resolvía todo con
 *   new StringBuilder(s).reverse().toString(). Es la opción correcta en producción —O(n) tiempo,
 *   O(n) memoria, sin recursión— pero no ejercita la reconstrucción del resultado al desapilar.
 *   La comparación quedó documentada para dejar claro que la versión recursiva es pedagógica.
 * =====================================================================================
 */


