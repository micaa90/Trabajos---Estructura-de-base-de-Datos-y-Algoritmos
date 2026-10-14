/*
 * =====================================================================================
 * Práctico 2 — Recursividad. Ejercicio 1: Factorial
 * =====================================================================================
 *
 * Prompt inicial utilizado:
 *
 * "Analizá este problema para resolverlo recursivamente: calcular el factorial de un
 *  número n.
 *  Tu análisis debe responder:
 *  - por qué el factorial puede resolverse de forma recursiva, mostrando que n! se define
 *    en términos de (n-1)!;
 *  - cuál es el caso base y por qué;
 *  - qué ocurre cuando n vale 0 o 1, y por qué 0! = 1 no es una convención arbitraria sino
 *    lo que hace consistente la definición;
 *  - cómo se reduce el problema en cada llamada;
 *  - qué devuelve cada nivel de la recursión;
 *  - qué valores de prueba deberían usarse, incluyendo 0, 1, un valor chico verificable a
 *    mano, y un valor donde el int desborde (indicá a partir de qué n pasa eso);
 *  - qué sucede si n es negativo y cómo debería manejarse.
 *  Indicá complejidad temporal y espacial, considerando que la pila de llamadas consume
 *  memoria.
 *  No escribas la implementación."
 *
 * Ajustes realizados luego de la primera respuesta:
 *
 *  1) Se agregó al prompt la exigencia explícita de usar "long" y de indicar a partir de
 *     qué n desborda el tipo. La primera respuesta usaba int y devolvía silenciosamente
 *     valores negativos desde n = 13 (13! = 6.227.020.800 > Integer.MAX_VALUE), lo que es
 *     un error peor que una excepción porque no se nota.
 *  2) Se agregó el pedido de tratar el n negativo. La primera respuesta no lo contemplaba
 *     y la recursión nunca alcanzaba el caso base n == 0, terminando en StackOverflowError.
 *  3) Se pidió además una versión iterativa en un método aparte, para poder comparar
 *     consumo de pila (O(n) recursiva contra O(1) iterativa) sin tocar la solución
 *     principal, que la consigna exige recursiva.
 *
 * Estrategia: definición inductiva n! = n * (n-1)! con caso base 0! = 1! = 1, reduciendo n
 * en una unidad por llamada hasta tocar el caso base y multiplicando al desapilar.
 *
 * Complejidad temporal: O(n) — una llamada y una multiplicación por cada valor de n a 1.
 * Complejidad espacial: O(n) — la pila de llamadas guarda n + 1 marcos simultáneos
 * (la versión iterativa, en cambio, es O(1) espacial).
 * =====================================================================================
 */

/**
 * Cálculo del factorial de un número entero no negativo mediante recursión.
 *
 * <p>Se ofrece la solución recursiva exigida por la consigna y, con fines comparativos,
 * una versión iterativa equivalente. Ambas devuelven {@code long} y rechazan los valores
 * que desbordarían el tipo.</p>
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio01_Factorial {

    /** Mayor n cuyo factorial todavía cabe en un long: 20! = 2432902008176640000. */
    private static final int MAX_N_SIN_DESBORDE = 20;

    /** Mayor n cuyo factorial cabe en un int: 12! = 479001600 (13! ya desborda int). */
    private static final int MAX_N_SIN_DESBORDE_INT = 12;

    /**
     * Calcula n! de forma recursiva.
     *
     * <p>Definición inductiva aplicada: {@code 0! = 1} y {@code n! = n * (n-1)!}.</p>
     *
     * @param n número entero no negativo cuyo factorial se desea calcular
     * @return el factorial de {@code n}
     * @throws IllegalArgumentException si {@code n} es negativo, porque el factorial no está
     *         definido para enteros negativos y la recursión nunca alcanzaría el caso base
     * @throws ArithmeticException si {@code n > 20}, porque el resultado desbordaría el long
     *         <p>Complejidad: O(n) temporal, O(n) espacial por la pila de llamadas.</p>
     */
    public static long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("El factorial no está definido para n negativo: " + n);
        }
        if (n > MAX_N_SIN_DESBORDE) {
            throw new ArithmeticException("Desborde: " + n + "! no cabe en un long (máximo n = "
                    + MAX_N_SIN_DESBORDE + ").");
        }

        // CASO BASE: 0! y 1! valen 1. Devolver 1 (y no 0) es lo que hace consistente la
        // definición, porque n! = n * (n-1)! exige que el neutro de la multiplicación cierre
        // la cadena. Al llegar acá la pila deja de crecer y empieza a desapilarse.
        if (n <= 1) {
            return 1L;
        }

        // CASO RECURSIVO: el problema se reduce en una unidad (n -> n-1). Se apila un marco
        // nuevo con n fijo y se suspende esta multiplicación hasta que la llamada interna
        // devuelva su valor; la multiplicación real recién ocurre al DESAPILAR.
        return n * factorial(n - 1);
    }

    /**
     * Calcula n! de forma iterativa. Existe solo para comparar el consumo de memoria con la
     * versión recursiva: no usa pila de llamadas, por lo que su espacio es constante.
     *
     * @param n número entero no negativo cuyo factorial se desea calcular
     * @return el factorial de {@code n}
     * @throws IllegalArgumentException si {@code n} es negativo
     * @throws ArithmeticException si {@code n > 20}
     *         <p>Complejidad: O(n) temporal, O(1) espacial.</p>
     */
    public static long factorialIterativo(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("El factorial no está definido para n negativo: " + n);
        }
        if (n > MAX_N_SIN_DESBORDE) {
            throw new ArithmeticException("Desborde: " + n + "! no cabe en un long (máximo n = "
                    + MAX_N_SIN_DESBORDE + ").");
        }

        long acumulado = 1L;
        for (int i = 2; i <= n; i++) {
            acumulado *= i;
        }
        return acumulado;
    }

    /**
     * Imprime el resultado recursivo e iterativo de un caso válido y lo compara con el
     * valor esperado calculado por fuera del algoritmo.
     *
     * @param n valor de entrada a probar
     * @param esperado factorial esperado de {@code n}
     */
    private static void probar(int n, long esperado) {
        try {
            long recursivo = factorial(n);
            long iterativo = factorialIterativo(n);
            System.out.printf("n = %-3d | recursivo = %-20d | iterativo = %-20d | esperado = %-20d | %s%n",
                    n, recursivo, iterativo, esperado,
                    (recursivo == esperado && iterativo == esperado) ? "OK" : "FALLA");
        } catch (IllegalArgumentException | ArithmeticException e) {
            System.out.printf("n = %-3d | excepción inesperada: %s%n", n, e.getMessage());
        }
    }

    /**
     * Ejecuta un caso que debe terminar en excepción y muestra que se capturó sin cortar el
     * programa.
     *
     * @param n valor de entrada a probar
     * @param excepcionEsperada nombre simple de la excepción que se espera
     */
    private static void probarError(int n, String excepcionEsperada) {
        try {
            long resultado = factorial(n);
            System.out.printf("n = %-3d | se esperaba %s pero devolvió %d | FALLA%n",
                    n, excepcionEsperada, resultado);
        } catch (IllegalArgumentException | ArithmeticException e) {
            System.out.printf("n = %-3d | esperado = %s | obtenido = %s: %s | %s%n",
                    n, excepcionEsperada, e.getClass().getSimpleName(), e.getMessage(),
                    e.getClass().getSimpleName().equals(excepcionEsperada) ? "OK" : "FALLA");
        }
    }

    /**
     * Punto de entrada. Ejecuta los valores de prueba definidos en el análisis.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 1: Factorial recursivo ===");
        System.out.println("Casos válidos (obtenido vs esperado):");
        probar(0, 1L);                      // caso base
        probar(1, 1L);                      // caso base
        probar(5, 120L);                    // verificable a mano: 5*4*3*2*1
        probar(MAX_N_SIN_DESBORDE_INT + 1, 6227020800L);   // 13!: desbordaría int, no long
        probar(MAX_N_SIN_DESBORDE, 2432902008176640000L);  // 20!: máximo que cabe en long

        System.out.println();
        System.out.println("Casos de error (se capturan con try/catch):");
        probarError(MAX_N_SIN_DESBORDE + 1, "ArithmeticException");  // 21! desborda long
        probarError(-3, "IllegalArgumentException");                 // n negativo

        System.out.println();
        System.out.println("Nota: 13! ya no cabe en int (máximo seguro en int: "
                + MAX_N_SIN_DESBORDE_INT + "!), por eso el retorno es long.");
    }
}


