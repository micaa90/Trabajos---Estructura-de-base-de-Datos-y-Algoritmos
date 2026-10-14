/*
 * =====================================================================================
 * Práctico 2 — Recursividad. Ejercicio 9: Palíndromo
 * =====================================================================================
 *
 * Prompt inicial utilizado:
 *
 * "Analizá este problema para resolverlo recursivamente: determinar si una palabra es
 *  palíndromo.
 *  Tu análisis debe responder:
 *  - por qué hay que comparar el primer y el último carácter;
 *  - qué pasa si son distintos: explicá por qué ahí ya se puede decidir que NO es palíndromo
 *    sin seguir comparando;
 *  - cómo se reduce el problema eliminando los extremos;
 *  - cuál es el caso base, considerando tanto la cadena vacía como la de un solo carácter, y
 *    por qué ambos son palíndromos;
 *  - qué pruebas deberían hacerse: palíndromo de longitud par, de longitud impar, no palíndromo
 *    que difiere en los extremos, no palíndromo que difiere en el medio, cadena vacía,
 *    un solo carácter;
 *  - si se deben ignorar mayúsculas y espacios, y cómo tratarlo.
 *  Indicá complejidad temporal y espacial, y mencioná la alternativa con dos índices que evita
 *  crear substrings.
 *  No escribas la implementación."
 *
 * Ajustes realizados luego de la primera respuesta:
 *
 *  1) Se agregó el pedido de la alternativa con dos índices. La primera respuesta resolvía todo
 *     con substring(1, length() - 1), que es correcto pero asigna una cadena nueva por nivel:
 *     O(n^2) caracteres copiados para una respuesta que es un simple booleano. La versión con
 *     índices recorre la misma cadena sin copiar nada y deja el costo en heap en O(1).
 *  2) Se agregó la pregunta por mayúsculas y espacios. La primera respuesta comparaba los
 *     caracteres tal cual, con lo que "Anita lava la tina" daba false (la 'A' inicial no es
 *     igual a la 'a' final, y los espacios tampoco se alinean). Se decidió NO normalizar dentro
 *     de la recursión —el algoritmo debe comparar lo que recibe— sino ofrecer un método de
 *     normalización aparte que el llamador aplica cuando quiere la semántica de frase.
 *  3) Se agregó la distinción entre "no palíndromo que difiere en los extremos" y "no palíndromo
 *     que difiere en el medio". La primera respuesta probaba un solo caso negativo y no dejaba
 *     ver el corte anticipado: "java" corta en el primer nivel, mientras "abcda" necesita bajar
 *     un nivel antes de detectar la diferencia. Son dos comportamientos distintos del algoritmo.
 *
 * Estrategia: comparar los extremos y, si coinciden, repetir el problema sobre el interior de la
 * cadena; caso base cuando quedan 0 o 1 caracteres (true) y corte inmediato con false al primer
 * par de extremos distintos.
 *
 * Complejidad temporal: O(n) comparaciones en la versión con índices; O(n^2) en la versión con
 * substring, porque cada nivel copia una cadena de hasta n caracteres.
 * Complejidad espacial: O(n) marcos de pila en ambas (n/2 niveles como máximo); la versión con
 * índices usa O(1) de heap, mientras la de substring asigna O(n^2) caracteres.
 * =====================================================================================
 */

/**
 * Verificación recursiva de palíndromos, en dos versiones comparables: una que reduce el
 * problema creando subcadenas y otra que lo reduce moviendo dos índices sobre la misma cadena.
 *
 * <p>Un palíndromo se lee igual en los dos sentidos. Eso equivale a decir que su primer carácter
 * es igual al último, y que lo que queda entre ellos también es palíndromo: una definición
 * autosimilar, y por eso el problema es recursivo.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio09_Palindromo {

    /**
     * Longitud máxima admitida. Se acota para fallar con un mensaje claro antes de que la JVM
     * lance {@code StackOverflowError}; el límite es muy superior a cualquier palabra o frase.
     */
    private static final int MAX_LONGITUD_RECURSION_SEGURA = 10_000;

    /**
     * Indica si {@code palabra} es palíndromo, comparando los caracteres tal como los recibe.
     *
     * <p>Es la versión recomendada: delega en la sobrecarga con índices, que no crea subcadenas.
     * Existe para que el llamador no tenga que conocer ni calcular los índices iniciales.</p>
     *
     * @param palabra cadena a verificar; puede ser vacía, pero no {@code null}
     * @return {@code true} si la cadena se lee igual en los dos sentidos; {@code false} en caso
     *         contrario. La cadena vacía y la de un solo carácter devuelven {@code true}
     * @throws IllegalArgumentException si {@code palabra} es {@code null} o si su longitud excede
     *         {@value #MAX_LONGITUD_RECURSION_SEGURA}
     *         <p>Complejidad: O(n) temporal, O(n) espacial en pila y O(1) en heap.</p>
     */
    public static boolean esPalindromo(String palabra) {
        validar(palabra);
        // El llamador solo aporta la cadena; los índices iniciales son un detalle del algoritmo.
        // Para la cadena vacía, fin vale -1, y el caso base inicio >= fin ya lo resuelve.
        return esPalindromo(palabra, 0, palabra.length() - 1);
    }

    /**
     * Sobrecarga con dos índices: verifica si el fragmento {@code palabra[inicio..fin]} es
     * palíndromo <b>sin crear ninguna subcadena</b>.
     *
     * <p>Diferencia con {@link #esPalindromoConSubstring(String)}: ahí "eliminar los extremos"
     * significa construir una cadena nueva con el interior; acá significa simplemente mover dos
     * números. La cadena original nunca se copia, así que el heap no crece con la profundidad.</p>
     *
     * @param palabra cadena sobre la que se trabaja; no {@code null}
     * @param inicio índice del primer carácter del fragmento, entre 0 y {@code palabra.length()}
     * @param fin índice del último carácter del fragmento, entre -1 y
     *        {@code palabra.length() - 1}
     * @return {@code true} si el fragmento delimitado por los índices es palíndromo
     * @throws IllegalArgumentException si {@code palabra} es {@code null} o los índices caen
     *         fuera del rango admitido
     *         <p>Complejidad: O(n) temporal, O(n) espacial en pila (n/2 marcos), O(1) en heap.</p>
     */
    public static boolean esPalindromo(String palabra, int inicio, int fin) {
        if (palabra == null) {
            throw new IllegalArgumentException("No se admite null: null no es una cadena vacía.");
        }
        if (inicio < 0 || fin >= palabra.length() || inicio > palabra.length() || fin < -1) {
            throw new IllegalArgumentException("Índices fuera de rango para una cadena de longitud "
                    + palabra.length() + ": inicio = " + inicio + ", fin = " + fin + ".");
        }

        // CASO BASE: cuando los índices se cruzan o se encuentran no queda nada por comparar.
        // inicio > fin es el fragmento vacío (todas las comparaciones anteriores salieron bien);
        // inicio == fin es el carácter central de una longitud impar, que se compara consigo
        // mismo. Los dos son palíndromos por definición, así que se devuelve true y la pila
        // deja de crecer.
        if (inicio >= fin) {
            return true;
        }

        // CORTE ANTICIPADO (el otro caso base): si los extremos difieren, la cadena NO se lee
        // igual en los dos sentidos y ya está decidido. Seguir comparando el interior no podría
        // cambiar la respuesta, así que se devuelve false sin apilar un nivel más.
        if (palabra.charAt(inicio) != palabra.charAt(fin)) {
            return false;
        }

        // CASO RECURSIVO: los extremos coinciden, así que el problema se reduce al interior.
        // "Eliminar los extremos" se implementa avanzando inicio y retrocediendo fin: la
        // distancia fin - inicio baja en 2 por llamada, lo que garantiza alcanzar el caso base.
        // Al desapilar, cada nivel devuelve tal cual el booleano del nivel interno: no hay nada
        // que combinar, porque la respuesta final es la del nivel más profundo que decidió.
        return esPalindromo(palabra, inicio + 1, fin - 1);
    }

    /**
     * Versión alternativa que reduce el problema creando una subcadena con el interior.
     *
     * <p>Se incluye para comparar costos, no porque sea preferible: es más fácil de leer, pero
     * cada nivel asigna un {@code String} nuevo de dos caracteres menos que el anterior. Para
     * una cadena de n caracteres se crean del orden de n/2 objetos y se copian del orden de
     * n^2/4 caracteres, cuando la respuesta es un único booleano.</p>
     *
     * @param palabra cadena a verificar; puede ser vacía, pero no {@code null}
     * @return {@code true} si la cadena se lee igual en los dos sentidos
     * @throws IllegalArgumentException si {@code palabra} es {@code null} o si su longitud excede
     *         {@value #MAX_LONGITUD_RECURSION_SEGURA}
     *         <p>Complejidad: O(n^2) temporal y O(n^2) caracteres asignados en heap, con O(n)
     *         marcos de pila.</p>
     */
    public static boolean esPalindromoConSubstring(String palabra) {
        validar(palabra);

        // CASO BASE: 0 o 1 caracteres. Es el mismo criterio que inicio >= fin en la otra
        // versión, expresado sobre la longitud del fragmento que recibió este nivel.
        if (palabra.length() <= 1) {
            return true;
        }

        // CORTE ANTICIPADO (segundo caso base): extremos distintos, respuesta inmediata.
        if (palabra.charAt(0) != palabra.charAt(palabra.length() - 1)) {
            return false;
        }

        // CASO RECURSIVO: acá "eliminar los extremos" se hace literalmente, construyendo una
        // cadena nueva con el interior. substring copia los caracteres, así que este objeto es
        // el costo que la versión con índices evita.
        String interior = palabra.substring(1, palabra.length() - 1);
        return esPalindromoConSubstring(interior);
    }

    /**
     * Normaliza un texto para la semántica de frase: pasa todo a minúsculas y descarta lo que no
     * sea letra ni dígito (espacios, signos de puntuación).
     *
     * <p>Decisión de diseño: la normalización vive <b>fuera</b> de la recursión. Si estuviera
     * dentro, cada nivel recursivo volvería a normalizar un fragmento ya normalizado, trabajo
     * redundante que además mezcla dos responsabilidades distintas: decidir qué cuenta como
     * carácter significativo y comparar extremos. No es un método recursivo, por eso puede
     * resolverse con una sola operación sobre la cadena completa.</p>
     *
     * @param texto texto a normalizar; no {@code null}
     * @return el texto en minúsculas y sin caracteres no alfanuméricos
     * @throws IllegalArgumentException si {@code texto} es {@code null}
     *         <p>Complejidad: O(n) temporal y O(n) espacial (una cadena nueva).</p>
     */
    public static String normalizar(String texto) {
        if (texto == null) {
            throw new IllegalArgumentException("No se admite null para normalizar.");
        }
        return texto.toLowerCase().replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]", "");
    }

    /**
     * Valida las precondiciones comunes a las dos versiones públicas de una sola cadena.
     *
     * @param palabra cadena a validar
     * @throws IllegalArgumentException si es {@code null} o excede la profundidad segura
     */
    private static void validar(String palabra) {
        if (palabra == null) {
            throw new IllegalArgumentException("No se admite null: null no es una cadena vacía, "
                    + "y llamar a length() sobre él fallaría dentro de la recursión.");
        }
        if (palabra.length() > MAX_LONGITUD_RECURSION_SEGURA) {
            throw new IllegalArgumentException("La cadena tiene " + palabra.length()
                    + " caracteres y excede el máximo de recursión segura ("
                    + MAX_LONGITUD_RECURSION_SEGURA + "): la pila de llamadas podría desbordar.");
        }
    }

    /**
     * Ejecuta un caso válido con las dos versiones e imprime los resultados contra el esperado.
     *
     * @param palabra cadena de entrada a probar
     * @param esperado resultado esperado, determinado a mano
     */
    private static void probar(String palabra, boolean esperado) {
        try {
            boolean conIndices = esPalindromo(palabra);
            boolean conSubstring = esPalindromoConSubstring(palabra);
            boolean ok = conIndices == esperado && conSubstring == esperado;
            System.out.printf("palabra = %-21s | indices = %-5b | substring = %-5b | esperado = %-5b | %s%n",
                    "\"" + palabra + "\"", conIndices, conSubstring, esperado, ok ? "OK" : "FALLA");
        } catch (IllegalArgumentException e) {
            System.out.printf("palabra = %-21s | excepción inesperada: %s%n",
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
            boolean obtenido = esPalindromo(palabra);
            System.out.printf("%-34s | se esperaba IllegalArgumentException pero devolvió %b | FALLA%n",
                    descripcion, obtenido);
        } catch (IllegalArgumentException e) {
            System.out.printf("%-34s | esperado = IllegalArgumentException | obtenido = %s | OK%n",
                    descripcion, e.getMessage());
        }
    }

    /**
     * Punto de entrada. Ejecuta los valores de prueba definidos en el análisis.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 9: Verificación recursiva de palíndromo ===");
        System.out.println("Casos del enunciado (resultado esperado determinado a mano):");
        probar("ana", true);          // palíndromo de longitud impar (3)
        probar("reconocer", true);    // palíndromo de longitud impar (9)
        probar("java", false);        // no palíndromo: difiere en los EXTREMOS, corta en nivel 1
        probar("abcda", false);       // no palíndromo: difiere en el MEDIO, corta en nivel 2
        probar("", true);             // caso base: cadena vacía
        probar("a", true);            // caso base: un solo carácter

        System.out.println();
        System.out.println("Casos adicionales:");
        probar("abba", true);         // palíndromo de longitud par
        probar("aa", true);           // longitud par mínima que compara extremos
        probar("ab", false);          // longitud par mínima que falla
        probar("oso", true);          // impar con carácter central

        System.out.println();
        System.out.println("Frase con mayúsculas y espacios (normalizada antes de comparar):");
        String frase = "Anita lava la tina";
        String normalizada = normalizar(frase);
        System.out.printf("frase original  = \"%s\"%n", frase);
        System.out.printf("normalizada     = \"%s\"%n", normalizada);
        probar(normalizada, true);
        System.out.println("Sin normalizar, la misma frase da false ('A' != 'a' y los espacios");
        System.out.printf("no se alinean): obtenido = %b | esperado = false | %s%n",
                esPalindromo(frase), esPalindromo(frase) ? "FALLA" : "OK");

        System.out.println();
        System.out.println("Casos de error (se capturan con try/catch):");
        probarError(null, "esPalindromo(null)");
        probarError("x".repeat(MAX_LONGITUD_RECURSION_SEGURA + 1),
                "cadena de 10001 caracteres");
        try {
            boolean obtenido = esPalindromo("ana", 0, 99);
            System.out.printf("%-34s | se esperaba IllegalArgumentException pero devolvió %b | FALLA%n",
                    "esPalindromo(\"ana\", 0, 99)", obtenido);
        } catch (IllegalArgumentException e) {
            System.out.printf("%-34s | esperado = IllegalArgumentException | obtenido = %s | OK%n",
                    "esPalindromo(\"ana\", 0, 99)", e.getMessage());
        }
    }
}

/*
 * =====================================================================================
 * AJUSTES REALIZADOS AL PROMPT (requerido por la consigna general del práctico)
 * =====================================================================================
 * Cambio 1: se agregó "mencioná la alternativa con dos índices que evita crear substrings".
 *   Motivo: la primera respuesta solo contemplaba substring(1, length() - 1). Es correcta y
 *   más legible, pero asigna una cadena por nivel y copia del orden de n^2/4 caracteres para
 *   devolver un booleano. Con dos índices la cadena no se copia nunca: el costo en heap baja de
 *   O(n^2) a O(1) y el temporal de O(n^2) a O(n). Se implementaron las dos para poder comparar.
 * Cambio 2: se agregó la pregunta por mayúsculas y espacios.
 *   Motivo: sin ella, "Anita lava la tina" devolvía false y parecía un error del algoritmo
 *   cuando en realidad era una ambigüedad del requisito. La decisión adoptada fue dejar el
 *   método recursivo comparando exactamente lo que recibe y agregar normalizar() como paso
 *   previo y separado, para que la responsabilidad de "qué cuenta como carácter significativo"
 *   no quede escondida dentro de la recursión y no se repita en cada nivel.
 * Cambio 3: se pidieron dos casos negativos distintos, uno que difiere en los extremos y otro
 *   que difiere en el medio.
 *   Motivo: la primera respuesta probaba un único no palíndromo, con lo que no se veía el corte
 *   anticipado. "java" se resuelve con una sola comparación (nivel 1) y "abcda" necesita dos
 *   (nivel 2): la cantidad de trabajo del caso negativo depende de DÓNDE está la diferencia, y
 *   eso es justamente lo que hace valioso el corte.
 * =====================================================================================
 */

/*
 * =====================================================================================
 * MATRIZ DE CASOS DE PRUEBA MANUALES
 * Compilar: javac Ejercicio09_Palindromo.java -d /tmp/r2
 * Ejecutar: java -cp /tmp/r2 Ejercicio09_Palindromo
 * =====================================================================================
 * ID    | CP-09-01
 * Obj.  | Verificar el flujo nominal positivo en longitud impar y par, con las dos versiones.
 * Entr. | esPalindromo("reconocer") y esPalindromo("abba") — ya incluidos en el main.
 * Esper.| "palabra = "reconocer" | indices = true | substring = true | esperado = true | OK" y
 *       | la línea equivalente para "abba".
 * Crit. | Aprueba si ambas versiones devuelven true y coinciden entre sí en los dos casos.
 * -------------------------------------------------------------------------------------
 * ID    | CP-09-02
 * Obj.  | Verificar el corte anticipado en los dos lugares posibles: diferencia en los extremos
 *       | y diferencia en el medio.
 * Entr. | esPalindromo("java") y esPalindromo("abcda") — ya incluidos en el main.
 * Esper.| Las dos líneas informan indices = false, substring = false, esperado = false, OK.
 * Crit. | Aprueba si ambas devuelven false; "java" decide con una sola comparación de extremos
 *       | ('j' != 'a') y "abcda" con dos ('a' == 'a', después 'b' != 'd').
 * -------------------------------------------------------------------------------------
 * ID    | CP-09-03
 * Obj.  | Verificar las longitudes mínimas que sí comparan extremos, una positiva y una
 *       | negativa, que son la frontera del caso base.
 * Entr. | esPalindromo("aa") y esPalindromo("ab") — ya incluidos en el main.
 * Esper.| "aa" informa true y "ab" informa false, cada uno igual a su esperado, OK.
 * Crit. | Aprueba si "aa" devuelve true tras una comparación y una llamada al caso base
 *       | (inicio = 1 > fin = 0) y "ab" devuelve false sin llamada recursiva.
 * -------------------------------------------------------------------------------------
 * ID    | CP-09-04
 * Obj.  | Cubrir la entrada vacía y la de un solo carácter, que son los dos casos base, y la
 *       | frase con mayúsculas y espacios normalizada antes de comparar.
 * Entr. | esPalindromo(""), esPalindromo("a") y esPalindromo(normalizar("Anita lava la tina")).
 * Esper.| Los tres informan true. La salida muestra además normalizada = "anitalavalatina" y
 *       | que la frase SIN normalizar da false.
 * Crit. | Aprueba si "" y "a" devuelven true sin comparar nada, la frase normalizada da true y
 *       | la frase cruda da false (comportamiento correcto: el algoritmo compara lo que recibe).
 * -------------------------------------------------------------------------------------
 * ID    | CP-09-05
 * Obj.  | Forzar los tres errores validados: entrada nula, cadena que excede la profundidad de
 *       | recursión segura e índices fuera de rango en la sobrecarga.
 * Entr. | esPalindromo(null), esPalindromo("x".repeat(10001)) y esPalindromo("ana", 0, 99).
 * Esper.| Las tres líneas informan esperado = IllegalArgumentException, el mensaje recibido y OK.
 * Crit. | Aprueba si las tres lanzan IllegalArgumentException, se capturan y el programa termina
 *       | normalmente, sin NullPointerException, StringIndexOutOfBoundsException ni
 *       | StackOverflowError.
 * =====================================================================================
 */
