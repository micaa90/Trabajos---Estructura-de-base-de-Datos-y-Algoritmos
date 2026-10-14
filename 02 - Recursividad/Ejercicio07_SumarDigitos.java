/*
 * =====================================================================================
 * Práctico 2 — Recursividad. Ejercicio 7: Sumar dígitos
 * =====================================================================================
 *
 * Prompt inicial utilizado:
 *
 * "Analizá este problema para resolverlo recursivamente: sumar los dígitos de un número
 *  entero positivo.
 *  Tu análisis debe responder:
 *  - cómo obtener el último dígito usando el operador módulo 10;
 *  - cómo reducir el número usando división entera por 10;
 *  - cuál es el caso base y qué devuelve;
 *  - cómo se combinan los resultados de cada llamada, es decir por qué el resultado es
 *    (n % 10) + sumaDigitos(n / 10);
 *  - qué pruebas validar, incluyendo un número de un dígito, uno con ceros intermedios, y un
 *    caso verificable a mano;
 *  - qué diferencia hay entre usar como caso base n == 0 y usar n < 10.
 *  Indicá complejidad temporal y espacial en función de la cantidad de dígitos.
 *  No escribas la implementación."
 *
 * Ajustes realizados luego de la primera respuesta:
 *
 *  1) Se agregó al prompt la pregunta por la diferencia entre los dos casos base posibles.
 *     La primera respuesta usaba n == 0 devolviendo 0, que es correcto pero apila un nivel
 *     extra por cada número (el nivel del 0, que no aporta nada). Con n < 10 devolviendo n
 *     se ahorra ese nivel y el caso base ya entrega el último dígito. Las dos versiones dan
 *     el mismo resultado; la diferencia es de profundidad de pila, no de corrección.
 *  2) Se agregó la validación de negativos. En la primera respuesta -45 entraba al caso base
 *     por ser menor que 10 y devolvía -45, un resultado sin sentido para una suma de dígitos.
 *  3) Se agregó la prohibición de usar String. Sin ella la solución natural recorre los
 *     caracteres del número convertido a texto, lo que no ejercita la recursión aritmética
 *     del práctico y asigna memoria en el heap innecesariamente.
 *
 * Estrategia: módulo 10 extrae el último dígito y división entera por 10 descarta ese mismo
 * dígito; cada nivel suma su dígito al resultado del resto, con caso base n < 10 -> n.
 *
 * Complejidad temporal: O(d) donde d es la cantidad de dígitos, equivalente a O(log10 n)
 * respecto del valor del número.
 * Complejidad espacial: O(d) — la pila alcanza d marcos simultáneos.
 * =====================================================================================
 */

/**
 * Suma recursiva de los dígitos decimales de un número entero no negativo, usando únicamente
 * aritmética entera.
 *
 * <p>Las dos operaciones centrales son complementarias: {@code n % 10} devuelve el último
 * dígito y {@code n / 10} devuelve todo lo demás. Entre las dos parten el número sin perder
 * información.</p>
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio07_SumarDigitos {

    /** Base del sistema decimal: divisor y módulo que separan el último dígito del resto. */
    private static final int BASE_DECIMAL = 10;

    /**
     * Suma recursivamente los dígitos decimales de {@code numero}.
     *
     * <p>Caso base elegido: {@code numero < 10} devuelve {@code numero}. La alternativa
     * {@code numero == 0} devolviendo 0 también es correcta y da el mismo resultado, pero
     * apila un nivel adicional por cada invocación (el nivel del 0, que aporta un sumando
     * nulo).</p>
     *
     * @param numero número entero no negativo cuyos dígitos se desean sumar
     * @return la suma de los dígitos decimales de {@code numero}; 0 cuando {@code numero} es 0
     * @throws IllegalArgumentException si {@code numero} es negativo. El enunciado restringe
     *         el dominio a enteros positivos, y aceptarlo en silencio daría un resultado
     *         absurdo: -45 satisface {@code -45 < 10} y el caso base devolvería -45
     *         <p>Complejidad: O(d) temporal y O(d) espacial, con d = cantidad de dígitos, que
     *         equivale a O(log10 n) respecto del valor del número.</p>
     */
    public static int sumaDigitos(int numero) {
        if (numero < 0) {
            throw new IllegalArgumentException("Solo se admiten enteros no negativos; se recibió: "
                    + numero + ". Un negativo entraría al caso base y devolvería el propio número.");
        }

        // CASO BASE: si queda un solo dígito, la suma de sus dígitos es él mismo. No hace
        // falta descomponer nada más. Acá la pila deja de crecer y empieza el desapilado.
        if (numero < BASE_DECIMAL) {
            return numero;
        }

        // El módulo 10 devuelve el RESTO de dividir por la base, que en notación posicional es
        // exactamente el último dígito: 1002 % 10 = 2.
        int ultimoDigito = numero % BASE_DECIMAL;

        // La división entera por 10 devuelve el COCIENTE, es decir todo el número menos su
        // último dígito: 1002 / 10 = 100. Módulo y división son complementarias: entre las dos
        // parten el número en "último dígito" y "resto", sin perder ni duplicar información.
        int restoDelNumero = numero / BASE_DECIMAL;

        // CASO RECURSIVO: este nivel aporta su dígito y delega el resto del problema. La suma
        // "ultimoDigito + ?" queda pendiente en el marco y se resuelve al DESAPILAR, cuando la
        // llamada interna devuelve la suma de los dígitos restantes.
        return ultimoDigito + sumaDigitos(restoDelNumero);
    }

    /**
     * Ejecuta un caso válido e imprime la suma obtenida frente a la esperada.
     *
     * @param numero número de entrada a probar
     * @param esperado suma esperada, calculada a mano
     */
    private static void probar(int numero, int esperado) {
        try {
            int obtenido = sumaDigitos(numero);
            System.out.printf("numero = %-12d | obtenido = %-4d | esperado = %-4d | %s%n",
                    numero, obtenido, esperado, (obtenido == esperado) ? "OK" : "FALLA");
        } catch (IllegalArgumentException e) {
            System.out.printf("numero = %-12d | excepción inesperada: %s%n", numero, e.getMessage());
        }
    }

    /**
     * Ejecuta un caso que debe terminar en excepción y verifica que se capture.
     *
     * @param numero número de entrada a probar
     */
    private static void probarError(int numero) {
        try {
            int obtenido = sumaDigitos(numero);
            System.out.printf("numero = %-12d | se esperaba IllegalArgumentException pero devolvió %d | FALLA%n",
                    numero, obtenido);
        } catch (IllegalArgumentException e) {
            System.out.printf("numero = %-12d | esperado = IllegalArgumentException | obtenido = %s | OK%n",
                    numero, e.getMessage());
        }
    }

    /**
     * Punto de entrada. Ejecuta los valores de prueba definidos en el análisis.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 7: Suma recursiva de dígitos (sin String) ===");
        System.out.println("Casos del enunciado (suma esperada calculada a mano):");
        probar(7, 7);              // un dígito: 7
        probar(1002, 3);           // 1 + 0 + 0 + 2 = 3, con ceros intermedios
        probar(99999, 45);         // 9*5 = 45, máximo por cantidad de dígitos
        probar(0, 0);              // entrada mínima del dominio

        System.out.println();
        System.out.println("Casos adicionales:");
        probar(9, 9);              // último valor del caso base
        probar(10, 1);             // primer valor que recursa: 1 + 0
        probar(123, 6);            // 1 + 2 + 3 = 6, verificable a mano
        probar(100, 1);            // termina en ceros
        probar(Integer.MAX_VALUE, 46);  // 2147483647: 2+1+4+7+4+8+3+6+4+7 = 46

        System.out.println();
        System.out.println("Caso de error (se captura con try/catch):");
        probarError(-45);  // sin validación devolvería -45
    }
}

/*
 * =====================================================================================
 * AJUSTES REALIZADOS AL PROMPT 
 * =====================================================================================
 * Cambio 1: se agregó "qué diferencia hay entre usar como caso base n == 0 y usar n < 10".
 *   Motivo: la primera respuesta usaba n == 0 devolviendo 0. Es correcta, pero cada número
 *   apila un nivel extra que aporta el sumando 0: sumaDigitos(7) recursaba a sumaDigitos(0).
 *   Con n < 10 devolviendo n, el caso base ya entrega el último dígito y se ahorra un marco
 *   por invocación. La diferencia es de profundidad de pila, no de corrección.
 * Cambio 2: se agregó la validación de negativos.
 *   Motivo: sin ella, -45 satisface la condición del caso base (-45 < 10) y el método
 *   devolvía -45. Una "suma de dígitos" negativa no tiene sentido, y el error era silencioso.
 * Cambio 3: se agregó la prohibición de usar String.
 *   Motivo: la primera respuesta proponía recorrer los caracteres de String.valueOf(n). Eso
 *   resuelve el problema pero esquiva el objetivo del práctico, que es la descomposición
 *   aritmética con módulo y división, y además asigna objetos en el heap sin necesidad.
 * =====================================================================================
 */


