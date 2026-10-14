/*
 * =====================================================================================
 * Práctico 2 — Recursividad. Ejercicio 3: Multiplicación mediante sumas
 * =====================================================================================
 *
 * Prompt inicial utilizado:
 *
 * "Analizá este problema para resolverlo recursivamente: multiplicar dos números enteros SIN
 *  usar el operador *.
 *  Tu análisis debe responder:
 *  - cómo representar una multiplicación como sumas repetidas;
 *  - cuál de los dos parámetros se reduce en cada llamada y por qué elegís ese;
 *  - por qué el caso base debería ser cuando uno de los factores vale 0, y qué valor devuelve;
 *  - qué debe devolver la función en el caso recursivo;
 *  - qué pruebas deberían hacerse, incluyendo multiplicar por 0, por 1, y el caso de factores
 *    negativos: analizá qué pasa si el factor que se reduce es negativo y cómo resolverlo;
 *  - por qué conviene reducir el factor de menor valor absoluto.
 *  Indicá complejidad temporal y espacial en función del factor que se reduce.
 *  No escribas la implementación."
 *
 * Ajustes realizados luego de la primera respuesta:
 *
 *  1) Se agregó al prompt el tratamiento explícito de factores negativos. La primera
 *     respuesta reducía el segundo factor sin mirar su signo: con multiplicar(4, -3) el
 *     contador bajaba -3, -4, -5, ... y nunca llegaba al caso base 0, terminando en
 *     StackOverflowError. La solución es trabajar con valores absolutos y aplicar el signo
 *     al final.
 *  2) Se agregó "por qué conviene reducir el factor de menor valor absoluto". La primera
 *     respuesta fijaba el segundo factor como contador, de modo que multiplicar(2, 1000)
 *     costaba 1001 llamadas y multiplicar(1000, 2) solo 3, para el mismo resultado. Elegir
 *     el menor hace que el costo sea O(min(|a|,|b|)) y no O(|b|).
 *  3) Se pidió acotar la cantidad de llamadas y usar long. Motivo: la profundidad de la pila
 *     es igual al factor reducido, así que factores grandes desbordan la pila antes que el
 *     tipo numérico; conviene fallar con un mensaje claro.
 *
 * Estrategia: a * b se reescribe como sumar |mayor| exactamente |menor| veces, con caso base
 * "0 veces -> 0", y el signo del producto se aplica al resultado absoluto mediante negación.
 *
 * Complejidad temporal: O(min(|a|, |b|)) — una llamada y una suma por cada unidad del factor
 * reducido.
 * Complejidad espacial: O(min(|a|, |b|)) — la pila alcanza min(|a|,|b|) + 1 marcos.
 * =====================================================================================
 */

/**
 * Multiplicación de dos enteros implementada como sumas repetidas y recursión, sin usar el
 * operador {@code *} en ningún punto del cálculo.
 *
 * <p>El método público normaliza los signos y elige cuál de los dos factores actúa como
 * contador; el método recursivo privado trabaja siempre con cantidades no negativas, lo que
 * garantiza que el caso base sea alcanzable.</p>
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio03_MultiplicacionPorSumas {

    /**
     * Máxima cantidad de sumas (y por lo tanto de marcos de pila) que se admite encadenar.
     * Es una cota conservadora para fallar con un mensaje claro antes de que la JVM lance
     * StackOverflowError.
     */
    private static final long MAX_SUMAS_ENCADENADAS = 10_000L;

    /**
     * Multiplica dos enteros sin usar el operador de multiplicación.
     *
     * <p>Normaliza el problema a valores absolutos, delega el cálculo en la recursión y
     * aplica el signo al final: el producto es negativo exactamente cuando los factores
     * tienen signos distintos. El signo se aplica con una negación, no con una
     * multiplicación por -1.</p>
     *
     * @param factorA primer factor; puede ser negativo, cero o positivo
     * @param factorB segundo factor; puede ser negativo, cero o positivo
     * @return el producto {@code factorA * factorB} calculado por sumas repetidas
     * @throws IllegalArgumentException si el menor valor absoluto de los dos factores excede
     *         {@value #MAX_SUMAS_ENCADENADAS}, porque la pila de llamadas podría desbordar
     *         <p>Complejidad: O(min(|a|,|b|)) temporal y espacial.</p>
     */
    public static long multiplicar(int factorA, int factorB) {
        // Se promueve a long antes de cualquier operación: Math.abs(Integer.MIN_VALUE) en
        // aritmética de int devuelve un valor negativo por desborde, y eso rompería el
        // contador del caso base.
        long a = factorA;
        long b = factorB;

        // El producto es negativo si y solo si los signos difieren (XOR lógico).
        boolean resultadoNegativo = (a < 0) ^ (b < 0);

        long absA = Math.abs(a);
        long absB = Math.abs(b);

        // Se reduce SIEMPRE el factor de menor valor absoluto: la cantidad de llamadas es
        // igual al factor reducido, así que multiplicar(1000, 2) debe costar lo mismo que
        // multiplicar(2, 1000). El mayor queda como sumando, que no afecta el costo.
        long sumando = Math.max(absA, absB);
        long veces = Math.min(absA, absB);

        if (veces > MAX_SUMAS_ENCADENADAS) {
            throw new IllegalArgumentException("El factor menor en valor absoluto es " + veces
                    + " y excede el máximo de sumas encadenadas (" + MAX_SUMAS_ENCADENADAS
                    + "): la pila de llamadas podría desbordar.");
        }

        long productoAbsoluto = sumarRepetido(sumando, veces);
        return resultadoNegativo ? -productoAbsoluto : productoAbsoluto;
    }

    /**
     * Suma {@code sumando} consigo mismo {@code veces} veces, de forma recursiva.
     *
     * <p>Es el núcleo del algoritmo: multiplicar es, por definición, sumar un número tantas
     * veces como indica el otro. Ambos parámetros son no negativos por construcción, lo que
     * asegura que el contador siempre llegue a 0.</p>
     *
     * @param sumando valor que se repite en la suma; no negativo
     * @param veces cantidad de repeticiones restantes; no negativo
     * @return el resultado de sumar {@code sumando} un total de {@code veces} veces
     *         <p>Complejidad: O(veces) temporal y espacial.</p>
     */
    private static long sumarRepetido(long sumando, long veces) {
        // CASO BASE: cuando el factor que actúa como contador llega a 0, no queda ninguna
        // suma por hacer y el resultado es 0, que es el elemento neutro de la suma. Es el
        // caso base correcto porque cualquier número multiplicado por 0 da 0, y porque 0 es
        // el único valor que no altera las sumas que quedaron pendientes en los niveles
        // superiores. Acá la pila deja de crecer y arranca el desapilado.
        if (veces == 0L) {
            return 0L;
        }

        // CASO RECURSIVO: se descuenta una repetición y se delega el resto del problema.
        // El marco actual guarda su "sumando" y deja la suma "sumando + ?" pendiente; la
        // suma real recién ocurre al DESAPILAR, cuando la llamada interna entrega su valor.
        return sumando + sumarRepetido(sumando, veces - 1L);
    }

    /**
     * Ejecuta un caso válido e imprime el resultado obtenido frente al esperado.
     *
     * @param a primer factor
     * @param b segundo factor
     * @param esperado producto esperado, calculado por fuera del algoritmo
     */
    private static void probar(int a, int b, long esperado) {
        try {
            long obtenido = multiplicar(a, b);
            System.out.printf("%4d x %-4d | obtenido = %-8d | esperado = %-8d | llamadas = %-6d | %s%n",
                    a, b, obtenido, esperado,
                    Math.min(Math.abs((long) a), Math.abs((long) b)) + 1,
                    (obtenido == esperado) ? "OK" : "FALLA");
        } catch (IllegalArgumentException e) {
            System.out.printf("%4d x %-4d | excepción inesperada: %s%n", a, b, e.getMessage());
        }
    }

    /**
     * Ejecuta un caso que debe terminar en excepción y verifica que se capture.
     *
     * @param a primer factor
     * @param b segundo factor
     */
    private static void probarError(int a, int b) {
        try {
            long obtenido = multiplicar(a, b);
            System.out.printf("%4d x %-4d | se esperaba IllegalArgumentException pero devolvió %d | FALLA%n",
                    a, b, obtenido);
        } catch (IllegalArgumentException e) {
            System.out.printf("%4d x %-4d | esperado = IllegalArgumentException | obtenido = %s | OK%n",
                    a, b, e.getMessage());
        }
    }

    /**
     * Punto de entrada. Ejecuta los casos de prueba definidos en el análisis.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 3: Multiplicación recursiva por sumas (sin operador *) ===");
        System.out.println("La columna 'llamadas' es min(|a|,|b|) + 1, la profundidad de la pila:");
        probar(4, 3, 12L);     // flujo nominal
        probar(3, 4, 12L);     // conmutatividad: mismo resultado y mismo costo que 4x3
        probar(7, 0, 0L);      // caso base por el segundo factor
        probar(0, 7, 0L);      // caso base por el primer factor
        probar(1, 9, 9L);      // multiplicar por 1: una sola suma
        probar(-4, 3, -12L);   // primer factor negativo
        probar(4, -3, -12L);   // el factor que se reduciría es negativo
        probar(-4, -3, 12L);   // dos negativos: producto positivo

        System.out.println();
        System.out.println("Comparación de costo según qué factor se reduce (siempre el menor):");
        probar(2, 1000, 2000L);
        probar(1000, 2, 2000L);

        System.out.println();
        System.out.println("Caso de error (se captura con try/catch):");
        probarError(20_000, 30_000);  // min(|a|,|b|) = 20000 > 10000 marcos admitidos
    }
}

/*
 * =====================================================================================
 * AJUSTES REALIZADOS AL PROMPT 
 * =====================================================================================
 * Cambio 1: se agregó al prompt "analizá qué pasa si el factor que se reduce es negativo y
 *   cómo resolverlo".
 *   Motivo: la primera respuesta reducía ciegamente el segundo factor. Con multiplicar(4, -3)
 *   el contador iba -3, -4, -5, ... alejándose del caso base veces == 0, y el programa moría
 *   por StackOverflowError. Se resolvió normalizando a valores absolutos y aplicando el signo
 *   al final con una negación (no con una multiplicación por -1, que está prohibida).
 * Cambio 2: se agregó "por qué conviene reducir el factor de menor valor absoluto".
 *   Motivo: sin esa indicación, multiplicar(2, 1000) apilaba 1001 marcos y multiplicar(1000, 2)
 *   solo 3, para el mismo producto. Reduciendo siempre el menor, el costo pasa a ser
 *   O(min(|a|,|b|)) y la operación es simétrica, igual que la multiplicación real.
 * Cambio 3: se pidió usar long y acotar la cantidad de sumas encadenadas.
 *   Motivo: la profundidad de pila es exactamente el factor reducido; con factores grandes el
 *   límite se alcanza en la pila, no en el tipo. Además Math.abs(Integer.MIN_VALUE) desborda
 *   en aritmética de int y habría dejado el contador en negativo.
 * =====================================================================================
 */


