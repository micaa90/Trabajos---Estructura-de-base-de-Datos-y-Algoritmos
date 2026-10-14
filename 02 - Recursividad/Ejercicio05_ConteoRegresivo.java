/*
 * =====================================================================================
 * Práctico 2 — Recursividad. Ejercicio 5: Conteo regresivo
 * =====================================================================================
 *
 * Prompt inicial utilizado:
 *
 * "Analizá este problema para resolverlo recursivamente: imprimir un conteo desde n hasta 0.
 *  Tu análisis debe responder:
 *  - qué significa que la función no necesariamente devuelva un valor, es decir qué es una
 *    función recursiva void y por qué acá el resultado es un efecto (la impresión) y no un
 *    valor de retorno;
 *  - cuál es el caso base;
 *  - cómo se acerca al caso base en cada llamada;
 *  - qué pasaría si en lugar de n - 1 se usara n + 1: explicá concretamente que nunca se
 *    alcanzaría el caso base y qué error produce la JVM (StackOverflowError);
 *  - cómo probar la ejecución;
 *  - qué diferencia hay entre imprimir ANTES de la llamada recursiva (conteo descendente) y
 *    DESPUÉS de la llamada recursiva (conteo ascendente), y por qué.
 *  Indicá complejidad temporal y espacial.
 *  No escribas la implementación."
 *
 * Ajustes realizados luego de la primera respuesta:
 *
 *  1) Se agregó al prompt el pedido de implementar también la variante que imprime DESPUÉS
 *     de la llamada recursiva. La primera respuesta solo mostraba el conteo descendente, y
 *     sin el contraste no quedaba claro que la misma estructura recursiva produce dos
 *     órdenes de salida distintos según dónde esté el System.out.
 *  2) Se agregó la validación de n negativo. En la primera respuesta el caso base era
 *     n == 0 sin validar la entrada, de modo que n = -1 se alejaba del caso base y producía
 *     StackOverflowError: el mismo defecto que el enunciado pide analizar en el caso n + 1.
 *  3) Se pidió explícitamente NO implementar la variante con n + 1 y dejarla solo comentada.
 *     Motivo: ejecutarla aborta el programa con StackOverflowError y haría imposible correr
 *     el resto de las pruebas; documentada cumple el mismo propósito didáctico sin romper
 *     la ejecución.
 *
 * Estrategia: método void recursivo cuyo resultado es un efecto (la impresión), con caso base
 * n == 0 y reducción n -> n - 1; la posición del System.out respecto de la llamada recursiva
 * decide si el conteo sale descendente o ascendente.
 *
 * Complejidad temporal: O(n) — una llamada y una impresión por cada valor de n a 0.
 * Complejidad espacial: O(n) — la pila alcanza n + 1 marcos simultáneos.
 * =====================================================================================
 */

/**
 * Conteo recursivo entre n y 0 mediante métodos {@code void}.
 *
 * <p>Las dos versiones comparten exactamente la misma estructura recursiva y difieren
 * únicamente en la posición de la impresión: antes de la llamada recursiva produce un conteo
 * descendente (n, n-1, ..., 0) y después de la llamada recursiva produce un conteo ascendente
 * (0, 1, ..., n). Ninguna devuelve un valor: su resultado es un efecto sobre la salida
 * estándar.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio05_ConteoRegresivo {

    /**
     * Máxima profundidad de recursión admitida. Cota conservadora para fallar con un mensaje
     * claro antes de que la JVM lance StackOverflowError.
     */
    private static final int MAX_N_RECURSION_SEGURA = 10_000;

    /**
     * Imprime el conteo descendente desde {@code n} hasta 0, separados por espacios en una misma línea.
     *
     * <p>El método es {@code void} porque el problema no pide calcular ningún valor: pide
     * producir un efecto observable (la salida por consola). La recursión sirve igual para
     * generar efectos que para calcular resultados; la diferencia es que acá no hay nada que
     * combinar al desapilar, así que los niveles simplemente se destruyen.</p>
     *
     * @param n valor inicial del conteo; debe ser mayor o igual a cero
     * @throws IllegalArgumentException si {@code n} es negativo (el caso base sería
     *         inalcanzable) o si excede el máximo de recursión segura
     *         <p>Complejidad: O(n) temporal, O(n) espacial por la pila de llamadas.</p>
     */
    public static void contarDescendente(int n) {
        validar(n);

        // La impresión ocurre ANTES de recursar: cada nivel muestra su propio n mientras la
        // pila se está APILANDO, por eso la salida sale de mayor a menor.
        System.out.print(n + " ");

        // CASO BASE: al llegar a 0 no se hace una nueva llamada. Es el valor que detiene la
        // recursión; sin él (o con un n que nunca lo alcance) la pila crecería sin fin.
        if (n == 0) {
            return;
        }

        // CASO RECURSIVO: se acerca al caso base restando 1. Cuando esta llamada vuelva, el
        // marco actual no tiene nada más que hacer y se DESAPILA en silencio.
        contarDescendente(n - 1);
    }

    /**
     * Imprime el conteo ascendente desde 0 hasta {@code n}, separados por espacios en una misma línea.
     *
     * <p>Es el mismo algoritmo que {@link #contarDescendente(int)} con la impresión movida
     * después de la llamada recursiva. El cambio de orden no es un truco: el apilado ocurre
     * en el mismo orden (n, n-1, ..., 0), pero las impresiones se ejecutan durante el
     * DESAPILADO, que recorre los niveles en sentido inverso.</p>
     *
     * @param n valor final del conteo; debe ser mayor o igual a cero
     * @throws IllegalArgumentException si {@code n} es negativo o excede el máximo seguro
     *         <p>Complejidad: O(n) temporal, O(n) espacial por la pila de llamadas.</p>
     */
    public static void contarAscendente(int n) {
        validar(n);

        // CASO BASE: 0 corta la recursión. Se imprime y se vuelve, porque 0 debe ser el
        // primer número de la salida ascendente.
        if (n == 0) {
            System.out.print(0 + " ");
            return;
        }

        // CASO RECURSIVO: primero se baja hasta el caso base sin imprimir nada. Durante el
        // apilado la consola queda muda; todas las impresiones quedan pendientes.
        contarAscendente(n - 1);

        // La impresión ocurre DESPUÉS de la llamada: se ejecuta al DESAPILAR, cuando los
        // niveles vuelven en orden inverso al de creación, por eso la salida va de menor a
        // mayor.
        System.out.print(n + " ");
    }

    /**
     * Valida que el valor inicial del conteo pertenezca al dominio soportado.
     *
     * @param n valor a validar
     * @throws IllegalArgumentException si es negativo o excede el máximo de recursión segura
     */
    private static void validar(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("No se admite n negativo (" + n
                    + "): el caso base n == 0 sería inalcanzable restando 1.");
        }
        if (n > MAX_N_RECURSION_SEGURA) {
            throw new IllegalArgumentException("n = " + n + " excede el máximo de recursión segura ("
                    + MAX_N_RECURSION_SEGURA + "): la pila de llamadas podría desbordar.");
        }
    }

    /*
     * VARIANTE CON n + 1: DELIBERADAMENTE NO IMPLEMENTADA
     * ---------------------------------------------------
     * Si el caso recursivo fuera contarDescendente(n + 1) en lugar de contarDescendente(n - 1),
     * el código compilaría sin problemas pero nunca terminaría:
     *
     *     public static void contarMal(int n) {
     *         System.out.print(n + " ");
     *         if (n == 0) { return; }
     *         contarMal(n + 1);        // se ALEJA del caso base en cada llamada
     *     }
     *
     * Partiendo de n = 5 los niveles serían 5, 6, 7, 8, ... creciendo indefinidamente: la
     * condición n == 0 jamás se evalúa como verdadera porque el argumento solo aumenta. No
     * existe una medida decreciente acotada, que es justamente la condición de terminación de
     * toda recursión. Cada llamada apila un marco nuevo que nunca se desapila, hasta que la
     * pila del hilo se agota y la JVM lanza java.lang.StackOverflowError. Es un Error, no una
     * Exception: señala que la JVM se quedó sin un recurso estructural, y aunque técnicamente
     * se puede capturar, no se debe, porque el defecto está en el algoritmo y no en la entrada.
     * Se reconoce por una traza de pila con cientos de líneas idénticas repetidas.
     * No se ejecuta acá porque abortaría el programa e impediría correr el resto de las pruebas.
     */

    /**
     * Punto de entrada. Ejecuta los casos de prueba definidos en el análisis.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 5: Conteo regresivo recursivo ===");

        System.out.println("Conteo DESCENDENTE con n = 5 (imprime ANTES de recursar):");
        System.out.print("  obtenido: ");
        contarDescendente(5);
        System.out.println();
        System.out.println("  esperado: 5 4 3 2 1 0 ");

        System.out.println();
        System.out.println("Conteo ASCENDENTE con n = 5 (imprime DESPUÉS de recursar):");
        System.out.print("  obtenido: ");
        contarAscendente(5);
        System.out.println();
        System.out.println("  esperado: 0 1 2 3 4 5 ");

        System.out.println();
        System.out.println("Caso base puro, n = 0 (una sola línea en ambas versiones):");
        System.out.print("  descendente obtenido: ");
        contarDescendente(0);
        System.out.println();
        System.out.println("  descendente esperado: 0 ");
        System.out.print("  ascendente  obtenido: ");
        contarAscendente(0);
        System.out.println();
        System.out.println("  ascendente  esperado: 0 ");

        System.out.println();
        System.out.println("Casos de error (se capturan con try/catch):");
        try {
            contarDescendente(-1);
            System.out.println("  n = -1    | FALLA: no lanzó excepción");
        } catch (IllegalArgumentException e) {
            System.out.println("  n = -1    | esperado = IllegalArgumentException | obtenido = " + e.getMessage() + " | OK");
        }
        try {
            contarAscendente(MAX_N_RECURSION_SEGURA + 1);
            System.out.println("  n = 10001 | FALLA: no lanzó excepción");
        } catch (IllegalArgumentException e) {
            System.out.println("  n = 10001 | esperado = IllegalArgumentException | obtenido = " + e.getMessage() + " | OK");
        }

        System.out.println();
        System.out.println("Nota: la variante con n + 1 no se implementa; está documentada en el");
        System.out.println("comentario de bloque del archivo porque provocaría StackOverflowError.");
    }
}

/*
 * =====================================================================================
 * AJUSTES REALIZADOS AL PROMPT (requerido por la consigna general del práctico)
 * =====================================================================================
 * Cambio 1: se agregó "implementá además un segundo método que imprima DESPUÉS de la llamada
 *   recursiva, para mostrar el conteo ascendente".
 *   Motivo: con una sola versión no se podía mostrar el punto central del ejercicio, que es
 *   que la posición del System.out respecto de la llamada recursiva decide si el efecto
 *   ocurre durante el apilado o durante el desapilado. Con las dos versiones la diferencia
 *   se ve directamente en la salida.
 * Cambio 2: se agregó la validación de n negativo.
 *   Motivo: la primera respuesta usaba el caso base n == 0 sin validar la entrada, así que
 *   n = -1 reproducía el mismo defecto que el enunciado pide analizar para n + 1 (el caso
 *   base queda inalcanzable y la JVM lanza StackOverflowError).
 * Cambio 3: se pidió NO implementar la variante con n + 1 y dejarla únicamente comentada.
 *   Motivo: ejecutarla aborta el proceso con StackOverflowError y ningún caso posterior
 *   podría correr. Documentada con su explicación cumple el objetivo didáctico sin sacrificar
 *   la ejecución del resto del programa.
 * =====================================================================================
 */

/*
 * =====================================================================================
 * MATRIZ DE CASOS DE PRUEBA MANUALES
 * Compilar: javac Ejercicio05_ConteoRegresivo.java -d /tmp/v2
 * Ejecutar: java -cp /tmp/v2 Ejercicio05_ConteoRegresivo
 * =====================================================================================
 * ID    | CP-05-01
 * Obj.  | Verificar el flujo nominal del conteo descendente (impresión antes de recursar).
 * Entr. | contarDescendente(5) — ya incluido en el main.
 * Esper.| Línea "  obtenido: 5 4 3 2 1 0 " idéntica a la línea "  esperado: 5 4 3 2 1 0 ".
 * Crit. | Aprueba si los seis números salen en orden estrictamente decreciente y termina en 0.
 * -------------------------------------------------------------------------------------
 * ID    | CP-05-02
 * Obj.  | Verificar que la misma recursión, imprimiendo después, invierte el orden.
 * Entr. | contarAscendente(5) — ya incluido en el main.
 * Esper.| Línea "  obtenido: 0 1 2 3 4 5 " idéntica a la línea "  esperado: 0 1 2 3 4 5 ".
 * Crit. | Aprueba si la salida es exactamente la inversa de CP-05-01, lo que prueba que las
 *       | impresiones ocurrieron durante el desapilado.
 * -------------------------------------------------------------------------------------
 * ID    | CP-05-03
 * Obj.  | Verificar el caso base puro: la recursión no debe apilar ningún nivel adicional.
 * Entr. | contarDescendente(0) y contarAscendente(0) — ya incluidos en el main.
 * Esper.| Las dos imprimen "0 " y nada más, coincidiendo con sus líneas de esperado.
 * Crit. | Aprueba si cada una imprime un único 0, sin números extra ni líneas vacías.
 * -------------------------------------------------------------------------------------
 * ID    | CP-05-04
 * Obj.  | Cubrir la salida "vacía" del dominio. El parámetro es int primitivo, por lo que no
 *       | existe entrada nula; el análogo del caso vacío es n = 0, donde no hay ninguna
 *       | llamada recursiva que realizar y la salida se reduce al propio caso base.
 * Entr. | contarAscendente(0) — ya incluido en el main.
 * Esper.| "  ascendente  obtenido: 0 " igual a "  ascendente  esperado: 0 ".
 * Crit. | Aprueba si la versión ascendente con n = 0 produce la misma salida que la
 *       | descendente con n = 0, ya que con un solo nivel no hay orden que invertir.
 * -------------------------------------------------------------------------------------
 * ID    | CP-05-05
 * Obj.  | Forzar los dos errores de rango: n negativo (caso base inalcanzable) y n por encima
 *       | de la profundidad de pila segura.
 * Entr. | contarDescendente(-1) y contarAscendente(10001) — ya incluidos en el main.
 * Esper.| Dos líneas con "obtenido = No se admite n negativo (-1) ..." y
 *       | "obtenido = n = 10001 excede el máximo de recursión segura (10000) ...", ambas OK.
 * Crit. | Aprueba si las dos lanzan IllegalArgumentException, se capturan, no se imprime
 *       | ningún número del conteo y el programa termina normalmente sin StackOverflowError.
 * =====================================================================================
 */
