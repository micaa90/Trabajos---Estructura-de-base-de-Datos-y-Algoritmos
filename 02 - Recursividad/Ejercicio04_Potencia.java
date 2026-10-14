/*
 * =====================================================================================
 * Práctico 2 — Recursividad. Ejercicio 4: Potencia
 * =====================================================================================
 *
 * Prompt inicial utilizado:
 *
 * "Analizá este problema para resolverlo recursivamente: calcular base elevado a exponente
 *  SIN usar Math.pow.
 *  Tu análisis debe responder:
 *  - por qué una potencia puede verse como multiplicaciones sucesivas, mostrando que
 *    base^e = base * base^(e-1);
 *  - por qué el caso base es exponente 0 y por qué devuelve 1;
 *  - cómo se reduce el exponente en cada llamada;
 *  - qué debe devolver la función en cada caso;
 *  - qué casos de prueba usar, incluyendo exponente 0, exponente 1, base 0, base 1, y un caso
 *    de desborde;
 *  - qué pasa con exponentes negativos y cómo debería tratarse.
 *  Mencioná también, como alternativa, la potencia rápida por división del exponente a la
 *  mitad, indicando su complejidad O(log e), y compará ambas.
 *  Indicá complejidad temporal y espacial de la versión lineal.
 *  No escribas la implementación."
 *
 * Ajustes realizados luego de la primera respuesta:
 *
 *  1) Se agregó al prompt el pedido de contar las llamadas recursivas de cada versión. La
 *     primera respuesta afirmaba que la potencia rápida era O(log e) pero no lo demostraba;
 *     exhibir 101 llamadas contra 8 para el exponente 100 convierte la afirmación teórica en
 *     una medición observable en la salida.
 *  2) Se agregó la detección real de desborde con Math.multiplyExact en lugar de dejar que
 *     el long se envolviera en silencio. Con la versión original, 2^63 devolvía un número
 *     negativo sin ninguna señal; ahora lanza ArithmeticException.
 *  3) Se agregó el tratamiento del exponente negativo. La primera respuesta no lo
 *     contemplaba y la recursión decreciente nunca alcanzaba el caso base e == 0; además
 *     base^(-e) no es un entero, así que corresponde rechazarlo y no "resolverlo".
 *
 * Estrategia: versión lineal base^e = base * base^(e-1) con caso base e = 0 -> 1, y versión
 * rápida base^e = (base^(e/2))^2 (por un factor base extra si e es impar), que divide el
 * exponente a la mitad en cada llamada.
 *
 * Complejidad temporal: O(e) la versión lineal; O(log e) la rápida.
 * Complejidad espacial: O(e) la versión lineal y O(log e) la rápida, en los dos casos por la
 * profundidad de la pila de llamadas.
 * =====================================================================================
 */

/**
 * Cálculo de potencias de exponente entero no negativo sin usar {@code Math.pow}.
 *
 * <p>Se implementan dos recursiones sobre el mismo problema: la lineal, que descuenta una
 * unidad del exponente por llamada, y la rápida (exponenciación binaria), que lo divide a la
 * mitad. Ambas cuentan sus llamadas recursivas para que la diferencia entre O(e) y O(log e)
 * sea visible en la salida, y ambas detectan el desborde del {@code long} en lugar de
 * envolver el resultado en silencio.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio04_Potencia {

    /** Contador de llamadas de la última ejecución de la versión lineal. */
    private static int llamadasLineal;

    /** Contador de llamadas de la última ejecución de la versión rápida. */
    private static int llamadasRapida;

    /**
     * Calcula {@code base} elevado a {@code exponente} con la recursión lineal.
     *
     * <p>Definición aplicada: {@code base^0 = 1} y {@code base^e = base * base^(e-1)}.
     * Reinicia el contador de llamadas antes de empezar.</p>
     *
     * @param base base de la potencia; puede ser negativa, cero o positiva
     * @param exponente exponente entero no negativo
     * @return el valor de {@code base^exponente}
     * @throws IllegalArgumentException si {@code exponente} es negativo, porque el resultado
     *         no sería entero y la recursión nunca alcanzaría el caso base
     * @throws ArithmeticException si el resultado o un producto intermedio desborda el long
     *         <p>Complejidad: O(e) temporal, O(e) espacial por la pila de llamadas.</p>
     */
    public static long potencia(long base, int exponente) {
        validarExponente(exponente);
        llamadasLineal = 0;
        return potenciaLineal(base, exponente);
    }

    /**
     * Calcula {@code base} elevado a {@code exponente} con exponenciación binaria.
     *
     * <p>Definición aplicada: {@code base^e = (base^(e/2))^2} si {@code e} es par, y
     * {@code base^e = base * (base^(e/2))^2} si es impar. Reinicia el contador de
     * llamadas antes de empezar.</p>
     *
     * @param base base de la potencia; puede ser negativa, cero o positiva
     * @param exponente exponente entero no negativo
     * @return el valor de {@code base^exponente}
     * @throws IllegalArgumentException si {@code exponente} es negativo
     * @throws ArithmeticException si el resultado o un producto intermedio desborda el long
     *         <p>Complejidad: O(log e) temporal, O(log e) espacial por la pila.</p>
     */
    public static long potenciaRapida(long base, int exponente) {
        validarExponente(exponente);
        llamadasRapida = 0;
        return potenciaBinaria(base, exponente);
    }

    /**
     * Devuelve la cantidad de llamadas recursivas de la última ejecución lineal.
     *
     * @return cantidad de invocaciones contabilizadas
     */
    public static int getLlamadasLineal() {
        return llamadasLineal;
    }

    /**
     * Devuelve la cantidad de llamadas recursivas de la última ejecución rápida.
     *
     * @return cantidad de invocaciones contabilizadas
     */
    public static int getLlamadasRapida() {
        return llamadasRapida;
    }

    /**
     * Valida que el exponente pertenezca al dominio soportado.
     *
     * @param exponente exponente a validar
     * @throws IllegalArgumentException si es negativo
     */
    private static void validarExponente(int exponente) {
        if (exponente < 0) {
            throw new IllegalArgumentException("No se admiten exponentes negativos (" + exponente
                    + "): base^(-e) = 1/base^e no es un entero.");
        }
    }

    /**
     * Núcleo recursivo de la versión lineal.
     *
     * @param base base de la potencia
     * @param exponente exponente restante, no negativo
     * @return {@code base^exponente}
     * @throws ArithmeticException si algún producto desborda el long
     */
    private static long potenciaLineal(long base, int exponente) {
        llamadasLineal++;

        // CASO BASE: exponente 0 devuelve 1. No es una convención arbitraria: la definición
        // base^e = base * base^(e-1) exige que la cadena se cierre con el elemento neutro de
        // la multiplicación, porque base^1 = base * base^0 solo da base si base^0 vale 1.
        // Acá la pila deja de crecer y empieza el desapilado.
        if (exponente == 0) {
            return 1L;
        }

        // CASO RECURSIVO: el exponente se reduce en UNA unidad por llamada. El marco actual
        // queda apilado con la multiplicación "base * ?" pendiente; se resuelve al DESAPILAR.
        // multiplyExact lanza ArithmeticException si el producto no cabe en un long, en
        // lugar de envolverse en silencio como haría el operador *.
        return Math.multiplyExact(base, potenciaLineal(base, exponente - 1));
    }

    /**
     * Núcleo recursivo de la exponenciación binaria.
     *
     * <p>Se distingue exponente par de impar porque {@code e/2} es división entera: cuando
     * {@code e} es impar se pierde "medio exponente" al dividir, y ese factor {@code base}
     * faltante hay que reponerlo. Con {@code e = 5}, {@code 5/2 = 2}, y
     * {@code (base^2)^2 = base^4}, así que falta un {@code base} para llegar a
     * {@code base^5}.</p>
     *
     * @param base base de la potencia
     * @param exponente exponente restante, no negativo
     * @return {@code base^exponente}
     * @throws ArithmeticException si algún producto desborda el long
     */
    private static long potenciaBinaria(long base, int exponente) {
        llamadasRapida++;

        // CASO BASE: idéntico al de la versión lineal, exponente 0 devuelve 1.
        if (exponente == 0) {
            return 1L;
        }

        // CASO RECURSIVO: el exponente se reduce a la MITAD, no en una unidad. Por eso la
        // profundidad de la pila es log2(e) y no e: cada nivel resuelve el doble de trabajo
        // que el anterior con una sola multiplicación extra.
        long mitad = potenciaBinaria(base, exponente / 2);

        // Al desapilar, elevar al cuadrado el resultado de la mitad duplica el exponente.
        long cuadrado = Math.multiplyExact(mitad, mitad);

        if (exponente % 2 == 0) {
            return cuadrado;
        }
        // Exponente impar: la división entera descartó un factor base, se repone acá.
        return Math.multiplyExact(base, cuadrado);
    }

    /**
     * Ejecuta un caso válido con las dos versiones e imprime resultados y cantidad de
     * llamadas de cada una.
     *
     * @param base base de la potencia
     * @param exponente exponente de la potencia
     * @param esperado resultado esperado, calculado por fuera del algoritmo
     */
    private static void probar(long base, int exponente, long esperado) {
        try {
            long lineal = potencia(base, exponente);
            int callsLineal = getLlamadasLineal();
            long rapida = potenciaRapida(base, exponente);
            int callsRapida = getLlamadasRapida();
            System.out.printf("%3d^%-4d | lineal = %-20d (%4d llamadas) | rapida = %-20d (%2d llamadas) | esperado = %-20d | %s%n",
                    base, exponente, lineal, callsLineal, rapida, callsRapida, esperado,
                    (lineal == esperado && rapida == esperado) ? "OK" : "FALLA");
        } catch (IllegalArgumentException | ArithmeticException e) {
            System.out.printf("%3d^%-4d | excepción inesperada: %s%n", base, exponente, e.getMessage());
        }
    }

    /**
     * Ejecuta un caso que debe terminar en excepción con las dos versiones.
     *
     * @param base base de la potencia
     * @param exponente exponente de la potencia
     * @param excepcionEsperada nombre simple de la excepción esperada
     */
    private static void probarError(long base, int exponente, String excepcionEsperada) {
        String resultadoLineal = ejecutarEsperandoError(base, exponente, true);
        String resultadoRapida = ejecutarEsperandoError(base, exponente, false);
        System.out.printf("%3d^%-4d | esperado = %s%n", base, exponente, excepcionEsperada);
        System.out.printf("         | lineal -> %s%n", resultadoLineal);
        System.out.printf("         | rapida -> %s%n", resultadoRapida);
    }

    /**
     * Invoca una de las dos versiones esperando que falle y describe el resultado observado.
     *
     * @param base base de la potencia
     * @param exponente exponente de la potencia
     * @param usarLineal {@code true} para la versión lineal, {@code false} para la rápida
     * @return descripción de la excepción capturada o del valor devuelto si no falló
     */
    private static String ejecutarEsperandoError(long base, int exponente, boolean usarLineal) {
        try {
            long valor = usarLineal ? potencia(base, exponente) : potenciaRapida(base, exponente);
            return "FALLA: devolvió " + valor + " en lugar de lanzar la excepción";
        } catch (IllegalArgumentException | ArithmeticException e) {
            return "OK: " + e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }

    /**
     * Punto de entrada. Ejecuta los casos de prueba definidos en el análisis.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 4: Potencia recursiva (sin Math.pow) ===");
        System.out.println("Casos válidos, versión lineal O(e) contra versión rápida O(log e):");
        probar(2, 0, 1L);                      // caso base: exponente 0
        probar(2, 1, 2L);                      // exponente 1
        probar(0, 5, 0L);                      // base 0
        probar(0, 0, 1L);                      // 0^0 = 1 por la misma convención que 0! = 1
        probar(1, 100, 1L);                    // base 1
        probar(2, 10, 1024L);                  // verificable a mano
        probar(-3, 3, -27L);                   // base negativa, exponente impar
        probar(-3, 4, 81L);                    // base negativa, exponente par
        probar(2, 62, 4611686018427387904L);   // máxima potencia de 2 que cabe en long

        System.out.println();
        System.out.println("Cantidad de llamadas para exponentes 5, 16 y 100 (base 1 para no desbordar):");
        probar(1, 5, 1L);
        probar(1, 16, 1L);
        probar(1, 100, 1L);

        System.out.println();
        System.out.println("Casos de error (se capturan con try/catch):");
        probarError(2, 63, "ArithmeticException (2^63 desborda el long)");
        probarError(2, -4, "IllegalArgumentException (exponente negativo)");
    }
}

/*
 * =====================================================================================
 * AJUSTES REALIZADOS AL PROMPT (requerido por la consigna general del práctico)
 * =====================================================================================
 * Cambio 1: se agregó "ambas versiones deben contar la cantidad de llamadas recursivas".
 *   Motivo: la primera respuesta decía que la potencia rápida es O(log e) sin mostrarlo.
 *   Con los contadores, la salida exhibe 101 llamadas contra 8 para el exponente 100: la
 *   complejidad deja de ser una afirmación y pasa a ser un dato observable.
 * Cambio 2: se pidió detectar el desborde en lugar de ignorarlo.
 *   Motivo: con el operador * común, potencia(2, 63) devolvía -9223372036854775808 sin aviso,
 *   porque el long se envuelve en complemento a dos. Math.multiplyExact lanza
 *   ArithmeticException y convierte un resultado incorrecto silencioso en un error explícito.
 * Cambio 3: se pidió definir el tratamiento del exponente negativo.
 *   Motivo: la primera respuesta no lo contemplaba; con e = -4 el exponente se alejaba del
 *   caso base e == 0 y producía StackOverflowError. Como base^(-e) = 1/base^e no es entero,
 *   la decisión correcta es rechazarlo con IllegalArgumentException, no truncarlo a 0.
 * Nota de decisión: 0^0 devuelve 1. Es la misma convención que hace 0! = 1, y es lo que exige
 *   la consistencia de la definición base^e = base * base^(e-1).
 * =====================================================================================
 */

/*
 * =====================================================================================
 * MATRIZ DE CASOS DE PRUEBA MANUALES
 * Compilar: javac Ejercicio04_Potencia.java -d /tmp/v2
 * Ejecutar: java -cp /tmp/v2 Ejercicio04_Potencia
 * =====================================================================================
 * ID    | CP-04-01
 * Obj.  | Verificar el flujo nominal en las dos versiones con un valor verificable a mano.
 * Entr. | potencia(2, 10) y potenciaRapida(2, 10) — ya incluidos en el main.
 * Esper.| Línea "2^10 | lineal = 1024 (11 llamadas) | rapida = 1024 (5 llamadas) |
 *       | esperado = 1024 | OK".
 * Crit. | Aprueba si ambas devuelven 1024 y la rápida usa estrictamente menos llamadas.
 * -------------------------------------------------------------------------------------
 * ID    | CP-04-02
 * Obj.  | Verificar el caso base (exponente 0) en las dos versiones, incluido 0^0.
 * Entr. | potencia(2, 0) y potencia(0, 0) — ya incluidos en el main.
 * Esper.| Las dos líneas informan 1 en lineal, rápida y esperado, con 1 llamada cada una, OK.
 * Crit. | Aprueba si ambas devuelven 1 (y no 0) sin recursar más de una vez.
 * -------------------------------------------------------------------------------------
 * ID    | CP-04-03
 * Obj.  | Verificar el límite superior del tipo: la mayor potencia de 2 que cabe en long.
 * Entr. | potencia(2, 62) — ya incluido en el main.
 * Esper.| Línea con lineal = rapida = esperado = 4611686018427387904, OK.
 * Crit. | Aprueba si el valor coincide exactamente y no aparece un número negativo.
 * -------------------------------------------------------------------------------------
 * ID    | CP-04-04
 * Obj.  | Cubrir los bordes del dominio: base 0, base 1 y base negativa con exponente par e
 *       | impar. Los parámetros son primitivos, por lo que no existe entrada nula; el
 *       | análogo del caso vacío es el exponente 0 (ninguna multiplicación a realizar).
 * Entr. | potencia(0, 5), potencia(1, 100), potencia(-3, 3), potencia(-3, 4).
 * Esper.| 0, 1, -27 y 81 respectivamente, cada uno igual a su esperado, todos OK.
 * Crit. | Aprueba si el signo del resultado sigue la paridad del exponente y 0^5 da 0.
 * -------------------------------------------------------------------------------------
 * ID    | CP-04-05
 * Obj.  | Forzar los dos errores: desborde del long y exponente fuera de rango.
 * Entr. | potencia(2, 63) y potencia(2, -4) — ya incluidos en el main vía probarError.
 * Esper.| Para 2^63, ambas versiones informan "OK: ArithmeticException: long overflow".
 *       | Para 2^-4, ambas informan "OK: IllegalArgumentException: No se admiten exponentes
 *       | negativos (-4) ...".
 * Crit. | Aprueba si las cuatro invocaciones lanzan la excepción esperada, se capturan y el
 *       | programa termina normalmente sin StackOverflowError.
 * =====================================================================================
 */
