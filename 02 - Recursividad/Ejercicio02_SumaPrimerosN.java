/*
 * =====================================================================================
 * Práctico 2 — Recursividad. Ejercicio 2: Suma de los primeros N números
 * =====================================================================================
 *
 * Prompt inicial utilizado:
 *
 * "Analizá este problema para resolverlo recursivamente: sumar los números desde n hasta 1.
 *  Tu análisis debe responder:
 *  - cuál es el caso más simple del problema;
 *  - por qué suma(n) puede pensarse como n + suma(n - 1), mostrando la descomposición;
 *  - qué debe pasar si n es 0 y por qué devolver 0 es el valor correcto para el caso base;
 *  - cómo se reduce el problema;
 *  - cómo validar que la función funciona correctamente: proponé usar la fórmula cerrada
 *    n(n+1)/2 como verificación independiente;
 *  - qué valores de prueba usar, incluyendo n = 0, n = 1 y un n mediano.
 *  Indicá complejidad temporal y espacial, aclarando la profundidad de la pila.
 *  No escribas la implementación."
 *
 * Ajustes realizados luego de la primera respuesta:
 *
 *  1) Se agregó al prompt el pedido de usar la fórmula cerrada n(n+1)/2 como verificación
 *     independiente. En la primera respuesta la "prueba" consistía en imprimir el resultado
 *     y confiar en él; comparar contra una fórmula que no comparte una sola línea de código
 *     con el algoritmo recursivo es una verificación real, no una tautología.
 *  2) Se agregó la validación de n negativo, ausente en la primera respuesta: con n = -1 la
 *     condición n == 0 nunca se cumple al decrecer y el programa muere por StackOverflowError.
 *  3) Se pidió retorno long y se dejó documentado el techo de profundidad de la pila, porque
 *     para n grande (del orden de decenas de miles) el límite no es el tipo numérico sino la
 *     cantidad de marcos que soporta el hilo.
 *
 * Estrategia: descomposición suma(n) = n + suma(n-1) con caso base suma(0) = 0, reduciendo n
 * en una unidad por llamada y acumulando las sumas al desapilar.
 *
 * Complejidad temporal: O(n) — una llamada y una suma por cada valor de n a 1.
 * Complejidad espacial: O(n) — la pila alcanza n + 1 marcos simultáneos (la fórmula cerrada
 * resuelve lo mismo en O(1) temporal y O(1) espacial).
 * =====================================================================================
 */

/**
 * Suma de los primeros n números naturales mediante recursión lineal.
 *
 * <p>La solución principal es recursiva, como exige la consigna. Para validarla se incluye
 * la fórmula cerrada de Gauss {@code n(n+1)/2}, que calcula el mismo valor por un camino
 * algorítmicamente independiente y sirve como oráculo de prueba.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio02_SumaPrimerosN {

    /**
     * Mayor n para el que se admite la recursión sin arriesgar la pila del hilo.
     * Es una cota conservadora: la pila por defecto de la JVM soporta bastante más, pero el
     * valor exacto depende de -Xss y de la plataforma, así que no es un dato confiable.
     */
    private static final int MAX_N_RECURSION_SEGURA = 10_000;

    /**
     * Suma recursivamente los enteros desde n hasta 1.
     *
     * <p>Descomposición aplicada: {@code suma(n) = n + suma(n-1)} y {@code suma(0) = 0}.</p>
     *
     * @param n cota superior de la suma; debe ser mayor o igual a cero
     * @return la suma {@code n + (n-1) + ... + 1}, y 0 cuando {@code n} es 0
     * @throws IllegalArgumentException si {@code n} es negativo (la recursión nunca
     *         alcanzaría el caso base) o si excede el máximo de recursión segura
     *         <p>Complejidad: O(n) temporal, O(n) espacial por la pila de llamadas.</p>
     */
    public static long sumar(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("No se admite n negativo: " + n);
        }
        if (n > MAX_N_RECURSION_SEGURA) {
            throw new IllegalArgumentException("n = " + n + " excede el máximo de recursión segura ("
                    + MAX_N_RECURSION_SEGURA + "): la pila de llamadas podría desbordar.");
        }

        // CASO BASE: el problema más simple es "sumar desde 0 hasta 1", es decir no sumar
        // nada. El resultado es 0 porque 0 es el elemento neutro de la suma: devolver
        // cualquier otro valor contaminaría todos los niveles que están esperando.
        // Al llegar acá la pila deja de crecer y comienza el desapilado.
        if (n == 0) {
            return 0L;
        }

        // CASO RECURSIVO: se separa el término más grande (n) del resto del problema, que es
        // exactamente el mismo problema con n-1. El marco actual queda apilado con su n
        // guardado y la suma "n + ?" pendiente hasta que la llamada interna devuelva.
        return n + sumar(n - 1);
    }

    /**
     * Calcula la misma suma mediante la fórmula cerrada de Gauss.
     *
     * <p>Es una verificación válida e independiente porque no comparte lógica con el método
     * recursivo: no recursa, no itera y no acumula. Si ambas vías coinciden en varios
     * valores, el error tendría que estar simultáneamente en las dos, lo cual es muy poco
     * probable. Se usa aritmética de {@code long} y se divide al final para no perder
     * precisión: {@code n*(n+1)} siempre es par, así que la división entera es exacta.</p>
     *
     * @param n cota superior de la suma; debe ser mayor o igual a cero
     * @return la suma {@code n(n+1)/2}
     * @throws IllegalArgumentException si {@code n} es negativo
     *         <p>Complejidad: O(1) temporal, O(1) espacial.</p>
     */
    public static long sumarConFormula(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("No se admite n negativo: " + n);
        }
        return (long) n * (n + 1) / 2;
    }

    /**
     * Ejecuta un caso válido comparando la versión recursiva contra la fórmula cerrada.
     *
     * @param n valor de entrada a probar
     */
    private static void probar(int n) {
        try {
            long recursivo = sumar(n);
            long formula = sumarConFormula(n);
            System.out.printf("n = %-6d | recursivo = %-12d | formula n(n+1)/2 = %-12d | %s%n",
                    n, recursivo, formula, (recursivo == formula) ? "COINCIDEN (OK)" : "NO COINCIDEN (FALLA)");
        } catch (IllegalArgumentException e) {
            System.out.printf("n = %-6d | excepción inesperada: %s%n", n, e.getMessage());
        }
    }

    /**
     * Ejecuta un caso que debe terminar en excepción y verifica que se capture.
     *
     * @param n valor de entrada a probar
     */
    private static void probarError(int n) {
        try {
            long resultado = sumar(n);
            System.out.printf("n = %-6d | se esperaba IllegalArgumentException pero devolvió %d | FALLA%n",
                    n, resultado);
        } catch (IllegalArgumentException e) {
            System.out.printf("n = %-6d | esperado = IllegalArgumentException | obtenido = %s | OK%n",
                    n, e.getMessage());
        }
    }

    /**
     * Punto de entrada. Ejecuta los valores de prueba definidos en el análisis.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 2: Suma de 1 a n (recursiva) ===");
        System.out.println("Verificación independiente contra la fórmula cerrada n(n+1)/2:");
        probar(0);     // caso base: suma vacía
        probar(1);     // mínimo caso con un sumando
        probar(4);     // verificable a mano: 4+3+2+1 = 10
        probar(10);    // 55
        probar(100);   // n mediano: 5050
        probar(1000);  // 500500, profundidad de pila 1001 marcos

        System.out.println();
        System.out.println("Casos de error (se capturan con try/catch):");
        probarError(-5);                               // dominio inválido
        probarError(MAX_N_RECURSION_SEGURA + 1);       // riesgo de StackOverflowError
    }
}

/*
 * =====================================================================================
 * AJUSTES REALIZADOS AL PROMPT (requerido por la consigna general del práctico)
 * =====================================================================================
 * Cambio 1: se incorporó al prompt "proponé usar la fórmula cerrada n(n+1)/2 como
 *   verificación independiente".
 *   Motivo: la primera respuesta solo imprimía el resultado recursivo, lo que no prueba nada
 *   (si el algoritmo está mal, imprime mal y nadie se enteraría). La fórmula de Gauss es un
 *   oráculo externo: no comparte ni una línea con la recursión.
 * Cambio 2: se pidió explícitamente validar n negativo.
 *   Motivo: con n = -1 el caso base n == 0 queda inalcanzable porque n solo decrece, y el
 *   programa termina en StackOverflowError en lugar de informar una entrada inválida.
 * Cambio 3: se pidió razonar sobre la profundidad de la pila y acotar n.
 *   Motivo: acá el límite práctico no es el tipo numérico (long aguanta n enormes) sino la
 *   cantidad de marcos del hilo. Se agregó MAX_N_RECURSION_SEGURA para fallar con un mensaje
 *   claro antes de que la JVM lance un Error.
 * =====================================================================================
 */

/*
 * =====================================================================================
 * MATRIZ DE CASOS DE PRUEBA MANUALES
 * Compilar: javac Ejercicio02_SumaPrimerosN.java -d /tmp/v2
 * Ejecutar: java -cp /tmp/v2 Ejercicio02_SumaPrimerosN
 * =====================================================================================
 * ID    | CP-02-01
 * Obj.  | Verificar el flujo nominal con un n mediano contra la fórmula cerrada.
 * Entr. | sumar(100) — ya incluido en el main.
 * Esper.| Línea "n = 100 | recursivo = 5050 | formula n(n+1)/2 = 5050 | COINCIDEN (OK)".
 * Crit. | Aprueba si ambos valores son 5050 y la línea dice COINCIDEN.
 * -------------------------------------------------------------------------------------
 * ID    | CP-02-02
 * Obj.  | Verificar el caso base de la recursión.
 * Entr. | sumar(0) — ya incluido en el main.
 * Esper.| Línea con recursivo = 0 y formula = 0, COINCIDEN (OK).
 * Crit. | Aprueba si devuelve 0 (y no 1) sin lanzar excepción.
 * -------------------------------------------------------------------------------------
 * ID    | CP-02-03
 * Obj.  | Verificar el límite inferior no trivial, con un único sumando.
 * Entr. | sumar(1) — ya incluido en el main.
 * Esper.| Línea con recursivo = 1 y formula = 1, COINCIDEN (OK).
 * Crit. | Aprueba si devuelve 1, lo que prueba que el nivel n = 1 suma 1 + sumar(0).
 * -------------------------------------------------------------------------------------
 * ID    | CP-02-04
 * Obj.  | Cubrir la entrada "vacía" del dominio: el rango sin ningún elemento a sumar.
 *       | El parámetro es un int primitivo, por lo que no existe entrada nula; el análogo
 *       | del conjunto vacío es n = 0, y debe devolver el neutro de la suma.
 * Entr. | sumar(0) comparado contra sumarConFormula(0) — ya incluido en el main.
 * Esper.| Ambos devuelven 0 y la línea dice COINCIDEN (OK).
 * Crit. | Aprueba si ninguna de las dos vías devuelve un valor distinto de 0.
 * -------------------------------------------------------------------------------------
 * ID    | CP-02-05
 * Obj.  | Forzar los dos errores de rango: dominio negativo y profundidad de pila excesiva.
 * Entr. | sumar(-5) y sumar(10001) — ya incluidos en el main vía probarError.
 * Esper.| Dos líneas con "obtenido = No se admite n negativo: -5" y
 *       | "obtenido = n = 10001 excede el máximo de recursión segura (10000) ...", ambas OK.
 * Crit. | Aprueba si las dos lanzan IllegalArgumentException, se capturan y el programa
 *       | termina normalmente, sin StackOverflowError.
 * =====================================================================================
 */
