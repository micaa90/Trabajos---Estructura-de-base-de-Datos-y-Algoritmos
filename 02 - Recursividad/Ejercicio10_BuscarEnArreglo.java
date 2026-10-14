import java.util.Arrays;

/*
 * =====================================================================================
 * Práctico 2 — Recursividad. Ejercicio 10: Buscar un elemento en un arreglo (sin ciclos)
 * =====================================================================================
 *
 * Prompt inicial utilizado:
 *
 * "Analizá este problema para resolverlo recursivamente: buscar un número dentro de un arreglo
 *  de enteros SIN usar ciclos.
 *  Tu análisis debe responder:
 *  - desde qué posición empieza la búsqueda y por qué hace falta un parámetro de índice que la
 *    versión iterativa no necesita;
 *  - cómo avanzar en el arreglo sin usar for ni while, es decir qué parámetro se incrementa
 *    en la llamada recursiva;
 *  - cuál es el caso base cuando encuentra el valor;
 *  - cuál es el caso base cuando llega al final del arreglo;
 *  - qué debe devolver la función en cada uno de esos casos;
 *  - por qué este problema tiene DOS casos base y qué pasaría si falta alguno;
 *  - cómo ofrecer al usuario un método que no le obligue a pasar el índice inicial
 *    (método público que llama al recursivo privado);
 *  - qué pruebas hacer: elemento en la primera posición, en la última, inexistente, arreglo
 *    vacío.
 *  Indicá complejidad temporal y espacial, aclarando que acá la pila crece hasta n.
 *  No escribas la implementación."
 *
 * Ajustes realizados luego de la primera respuesta:
 *
 *  1) Se agregó la pregunta por el ORDEN de evaluación de los dos casos base. La primera
 *     respuesta los enumeraba sin decir cuál va primero, y la implementación natural de esa
 *     lista comparaba v[indice] == dato antes de verificar indice == v.length: con el arreglo
 *     vacío, o al pasarse del último elemento, eso lee una posición inexistente y lanza
 *     ArrayIndexOutOfBoundsException. El límite se comprueba SIEMPRE primero.
 *  2) Se agregó el pedido explícito del método público sin índice. La primera respuesta exponía
 *     el método recursivo de tres parámetros, obligando al llamador a pasar un 0 que es un
 *     detalle interno del algoritmo y que, si se pasa mal, produce resultados incorrectos o
 *     excepciones.
 *  3) Se agregó el tratamiento del arreglo null y el pedido de aclarar qué posición se devuelve
 *     cuando el dato está repetido. La primera respuesta no distinguía null de arreglo vacío
 *     —son casos distintos: uno es un error de contrato y el otro una búsqueda legítima que da
 *     -1— y no documentaba que el recorrido de izquierda a derecha devuelve la PRIMERA
 *     aparición.
 *
 * Estrategia: un parámetro de índice reemplaza al contador del for; cada llamada examina su
 * posición y delega el resto del arreglo con indice + 1, con dos casos base (fin del arreglo
 * -> -1, coincidencia -> el índice).
 *
 * Complejidad temporal: O(n) en el peor caso (dato ausente o en la última posición); O(1) en el
 * mejor caso (dato en la primera posición).
 * Complejidad espacial: O(n) — la pila crece hasta n marcos, uno por posición examinada, porque
 * la recursión es de cola pero la JVM no la optimiza.
 * =====================================================================================
 */

/**
 * Búsqueda lineal recursiva de un entero dentro de un arreglo, sin {@code for} ni {@code while}.
 *
 * <p>La búsqueda lineal es recursiva por naturaleza: "buscar el dato en el arreglo desde la
 * posición i" se resuelve mirando la posición i y, si no coincide, buscando el mismo dato desde
 * la posición i+1, que es el mismo problema sobre un tramo más corto.</p>
 *
 * <p>Nota sobre la restricción: el algoritmo no usa ningún ciclo. La única llamada a
 * {@code java.util.Arrays.toString} está en los métodos de impresión, es utilitaria para mostrar
 * el arreglo en la consola y no forma parte de la búsqueda.</p>
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio10_BuscarEnArreglo {

    /** Valor devuelto cuando el dato no está en el arreglo. */
    private static final int NO_ENCONTRADO = -1;

    /**
     * Longitud máxima admitida. La profundidad de la pila es igual a la cantidad de posiciones
     * examinadas, así que se acota para fallar con un mensaje claro antes de que la JVM lance
     * {@code StackOverflowError}.
     */
    private static final int MAX_LONGITUD_RECURSION_SEGURA = 10_000;

    /**
     * Busca {@code dato} en {@code v} y devuelve la posición de su primera aparición.
     *
     * <p>Este método existe además del recursivo por una razón de diseño de API: el índice
     * inicial es un detalle de implementación del algoritmo, no información que el llamador deba
     * conocer. Quien busca un dato quiere escribir {@code buscar(v, 15)}, no
     * {@code buscar(v, 15, 0)}; y si tuviera que pasar el 0 podría pasar otro número por error y
     * obtener un resultado incorrecto sin ninguna señal. Acá se concentran también las
     * validaciones, que se ejecutan una sola vez en lugar de repetirse en cada nivel.</p>
     *
     * @param v arreglo donde buscar; puede estar vacío, pero no ser {@code null}
     * @param dato valor a localizar
     * @return el índice de la primera aparición de {@code dato}, o {@code -1} si no está. Se
     *         devuelve la primera aparición porque el recorrido avanza de izquierda a derecha
     * @throws IllegalArgumentException si {@code v} es {@code null} —un arreglo nulo no es un
     *         arreglo vacío: el vacío es una búsqueda legítima que devuelve -1— o si su longitud
     *         excede {@value #MAX_LONGITUD_RECURSION_SEGURA}
     *         <p>Complejidad: O(n) temporal en el peor caso, O(n) espacial por la pila.</p>
     */
    public static int buscar(int[] v, int dato) {
        if (v == null) {
            throw new IllegalArgumentException("No se admite un arreglo null: null no es un arreglo "
                    + "vacío, y leer v.length sobre él fallaría dentro de la recursión.");
        }
        if (v.length > MAX_LONGITUD_RECURSION_SEGURA) {
            throw new IllegalArgumentException("El arreglo tiene " + v.length
                    + " elementos y excede el máximo de recursión segura ("
                    + MAX_LONGITUD_RECURSION_SEGURA + "): la pila de llamadas podría desbordar.");
        }

        // La búsqueda empieza en la posición 0, el primer índice válido de todo arreglo en Java.
        return buscarDesde(v, dato, 0);
    }

    /**
     * Busca {@code dato} en {@code v} examinando las posiciones desde {@code indice} hasta el
     * final.
     *
     * <p>El parámetro {@code indice} es lo que reemplaza al contador del {@code for}: en la
     * versión iterativa el contador es una variable que se modifica, acá es un argumento nuevo
     * en cada llamada. No se incrementa nada: cada nivel recibe su propio valor y le pasa al
     * siguiente {@code indice + 1}.</p>
     *
     * @param v arreglo donde buscar; no {@code null}
     * @param dato valor a localizar
     * @param indice primera posición a examinar en este nivel
     * @return el índice de la primera aparición de {@code dato} en el tramo
     *         {@code [indice, v.length)}, o {@code -1} si no aparece
     *         <p>Complejidad: O(n - indice) temporal y espacial.</p>
     */
    private static int buscarDesde(int[] v, int dato, int indice) {
        // CASO BASE 1 — FIN DEL ARREGLO: se examinaron todas las posiciones sin coincidencia, o
        // el arreglo estaba vacío y nunca hubo nada que examinar. Devuelve -1, un valor que no
        // puede confundirse con una posición válida (los índices son 0..length-1).
        // ESTE CASO BASE SE EVALÚA PRIMERO, Y EL ORDEN NO ES ARBITRARIO.
        if (indice == v.length) {
            return NO_ENCONTRADO;
        }

        // CASO BASE 2 — ENCONTRÓ EL VALOR: la posición actual contiene el dato buscado. Devuelve
        // su índice y corta: no tiene sentido seguir recorriendo, y además así se garantiza que
        // la posición devuelta sea la PRIMERA aparición cuando el dato está repetido.
        //
        // QUÉ PASARÍA SI LOS DOS CASOS BASE SE EVALUARAN EN ORDEN INVERSO: esta comparación lee
        // v[indice]. Si se hiciera antes de comprobar indice == v.length, entonces con el
        // arreglo vacío (indice = 0, v.length = 0) o al pasarse del último elemento
        // (indice = v.length) se intentaría leer una posición que no existe y la JVM lanzaría
        // ArrayIndexOutOfBoundsException en lugar de devolver -1. El caso base que comprueba el
        // LÍMITE tiene que ir primero porque protege el acceso del que lo sigue: es el mismo
        // orden que exige la evaluación en cortocircuito de "indice < v.length && v[indice] == dato".
        if (v[indice] == dato) {
            return indice;
        }

        // CASO RECURSIVO: esta posición no era. Se delega la búsqueda del MISMO dato en el
        // tramo que empieza una posición más adelante. La medida que decrece es la cantidad de
        // posiciones pendientes (v.length - indice), acotada por abajo por 0, así que el caso
        // base 1 siempre se alcanza.
        //
        // PILA DE LLAMADAS: el resultado del nivel interno se devuelve TAL CUAL, sin combinarlo
        // con nada. Por eso el desapilado es pura propagación: el valor que decide el nivel más
        // profundo (un índice o -1) atraviesa todos los marcos abiertos hasta el main sin
        // modificarse. Es una recursión de cola; la JVM de todos modos no la convierte en un
        // salto, así que los n marcos se crean realmente y la pila crece hasta n.
        return buscarDesde(v, dato, indice + 1);
    }

    /**
     * Ejecuta un caso válido e imprime la posición obtenida frente a la esperada.
     *
     * @param v arreglo donde buscar
     * @param dato valor a localizar
     * @param esperado posición esperada, determinada a mano
     */
    private static void probar(int[] v, int dato, int esperado) {
        try {
            int obtenido = buscar(v, dato);
            System.out.printf("arreglo = %-18s | dato = %-4d | obtenido = %-3d | esperado = %-3d | %s%n",
                    Arrays.toString(v), dato, obtenido, esperado,
                    (obtenido == esperado) ? "OK" : "FALLA");
        } catch (IllegalArgumentException e) {
            System.out.printf("arreglo = %-18s | dato = %-4d | excepción inesperada: %s%n",
                    Arrays.toString(v), dato, e.getMessage());
        }
    }

    /**
     * Ejecuta un caso que debe terminar en excepción y verifica que se capture.
     *
     * @param v arreglo donde buscar
     * @param dato valor a localizar
     * @param descripcion descripción del caso para la salida por consola
     */
    private static void probarError(int[] v, int dato, String descripcion) {
        try {
            int obtenido = buscar(v, dato);
            System.out.printf("%-30s | se esperaba IllegalArgumentException pero devolvió %d | FALLA%n",
                    descripcion, obtenido);
        } catch (IllegalArgumentException e) {
            System.out.printf("%-30s | esperado = IllegalArgumentException | obtenido = %s | OK%n",
                    descripcion, e.getMessage());
        }
    }

    /**
     * Punto de entrada. Ejecuta los valores de prueba definidos en el análisis.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 10: Búsqueda recursiva en un arreglo (sin for ni while) ===");

        int[] v = {4, 8, 15, 16};
        System.out.println("Casos del enunciado sobre " + Arrays.toString(v) + ":");
        probar(v, 4, 0);    // primera posición: el mejor caso, un solo nivel de pila
        probar(v, 16, 3);   // última posición: el peor caso con éxito, 4 niveles
        probar(v, 15, 2);   // caso trazado en el análisis
        probar(v, 7, -1);   // inexistente: recorre todo y cierra con el caso base del final

        System.out.println();
        System.out.println("Arreglo vacío (búsqueda legítima, no un error):");
        probar(new int[0], 5, -1);

        System.out.println();
        System.out.println("Casos adicionales:");
        probar(new int[] {42}, 42, 0);              // un único elemento, presente
        probar(new int[] {42}, 7, -1);              // un único elemento, ausente
        probar(new int[] {9, 3, 9, 3}, 9, 0);       // repetido: devuelve la PRIMERA aparición
        probar(new int[] {9, 3, 9, 3}, 3, 1);       // repetido: devuelve la PRIMERA aparición
        probar(new int[] {0, -7, 25}, -7, 1);       // valores negativos y el 0 como dato válido
        probar(new int[] {0, -7, 25}, 0, 0);        // el 0 se busca como cualquier otro valor

        System.out.println();
        System.out.println("Casos de error (se capturan con try/catch):");
        probarError(null, 5, "buscar(null, 5)");
        probarError(new int[MAX_LONGITUD_RECURSION_SEGURA + 1], 1, "arreglo de 10001 elementos");
    }
}

/*
 * =====================================================================================
 * AJUSTES REALIZADOS AL PROMPT 
 * =====================================================================================
 * Cambio 1: se agregó la pregunta por el orden en que se evalúan los dos casos base.
 *   Motivo: la primera respuesta los listaba como equivalentes ("encontró" y "llegó al final"),
 *   y la implementación que salía de esa lista comparaba v[indice] == dato primero. Con el
 *   arreglo vacío eso lee v[0] de un arreglo de longitud 0: ArrayIndexOutOfBoundsException en
 *   lugar de -1. El caso base del límite protege el acceso del otro, así que va primero; quedó
 *   documentado en el código porque es el error clásico de este ejercicio.
 * Cambio 2: se agregó "cómo ofrecer al usuario un método que no le obligue a pasar el índice
 *   inicial".
 *   Motivo: la primera respuesta exponía directamente el método de tres parámetros. El índice
 *   es estado interno del algoritmo: filtrarlo a la API pública obliga al llamador a conocerlo
 *   y habilita llamadas como buscar(v, 15, 2), que devuelven un resultado válido para un
 *   problema distinto del que se quiso resolver. Se separó en público buscar(int[], int) y
 *   privado recursivo buscarDesde(int[], int, int), con las validaciones en el público para no
 *   repetirlas en los n niveles.
 * Cambio 3: se agregó el tratamiento de null y la aclaración sobre datos repetidos.
 *   Motivo: la primera respuesta mezclaba null con el arreglo vacío. Son dos cosas distintas:
 *   el vacío es una entrada válida cuya respuesta correcta es -1, mientras que null es una
 *   violación del contrato que debe rechazarse antes de recursar. Además no decía qué posición
 *   se devuelve con el dato repetido; el recorrido de izquierda a derecha garantiza la primera
 *   aparición, y eso es parte del contrato del método, no un detalle accidental.
 * =====================================================================================
 */


