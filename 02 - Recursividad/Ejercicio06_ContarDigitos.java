/*
 * =====================================================================================
 * Práctico 2 — Recursividad. Ejercicio 6: Contar dígitos
 * =====================================================================================
 *
 * Prompt inicial utilizado:
 *
 * "Analizá este problema para resolverlo recursivamente: contar los dígitos de un número
 *  entero positivo.
 *  Tu análisis debe responder:
 *  - cómo se puede reducir el número usando división entera por 10, y qué significa esa
 *    operación (quitar el último dígito);
 *  - cuál es el caso base;
 *  - por qué un número menor que 10 tiene un solo dígito;
 *  - qué debe devolver cada llamada;
 *  - qué valores de prueba usar, incluyendo 0, un número de un dígito, uno de varios dígitos
 *    y uno con ceros intermedios como 1002;
 *  - qué pasa con el 0 como entrada y por qué hay que decidir explícitamente si tiene 0 o 1
 *    dígito;
 *  - qué pasa con números negativos.
 *  Indicá complejidad temporal en función de la cantidad de dígitos, y explicá por qué eso
 *  equivale a O(log n) respecto del valor del número.
 *  No escribas la implementación."
 *
 * Ajustes realizados luego de la primera respuesta:
 *
 *  1) Se agregó al prompt la pregunta explícita sobre el 0. La primera respuesta usaba como
 *     caso base n == 0 devolviendo 0, con lo cual contarDigitos(0) daba 0 dígitos: un
 *     resultado defendible en teoría pero contrario a la escritura decimal, donde el 0 se
 *     escribe con un carácter. Se cambió el caso base a n < 10 devolviendo 1, que resuelve
 *     el 0 y todos los números de un dígito con la misma condición.
 *  2) Se agregó el tratamiento de los negativos. La primera respuesta no lo contemplaba y
 *     -1002 entraba directamente al caso base (porque -1002 < 10), devolviendo 1 dígito en
 *     lugar de 4: un error silencioso.
 *  3) Se agregó la prohibición de convertir el número a String. Sin esa restricción la
 *     solución natural es String.valueOf(n).length(), que resuelve el problema pero no
 *     ejercita la recursión aritmética que pide el práctico.
 *
 * Estrategia: división entera por 10 para quitar el último dígito en cada llamada, con caso
 * base n < 10 (un solo dígito) y suma de 1 por nivel al desapilar.
 *
 * Complejidad temporal: O(d) donde d es la cantidad de dígitos, equivalente a
 * O(log10 n) respecto del valor del número.
 * Complejidad espacial: O(d) — la pila alcanza d marcos simultáneos.
 * =====================================================================================
 */

/**
 * Conteo recursivo de la cantidad de dígitos decimales de un número entero no negativo,
 * operando únicamente con aritmética entera.
 *
 * <p>No se convierte el número a texto: la reducción se hace con división entera por 10, que
 * es la operación que elimina el último dígito en base 10.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio06_ContarDigitos {

    /** Base del sistema de numeración decimal: el divisor que quita un dígito por llamada. */
    private static final int BASE_DECIMAL = 10;

    /**
     * Cuenta recursivamente los dígitos decimales de {@code numero}.
     *
     * <p>Decisión de diseño documentada: {@code contarDigitos(0)} devuelve <b>1</b>, no 0.
     * El 0 se escribe con un único carácter en notación decimal, y la alternativa (0 dígitos)
     * dejaría al 0 como el único número sin representación, lo que rompe la idea de "cantidad
     * de caracteres necesarios para escribirlo".</p>
     *
     * @param numero número entero no negativo cuyos dígitos se desean contar
     * @return la cantidad de dígitos decimales de {@code numero}; 1 cuando {@code numero} es 0
     * @throws IllegalArgumentException si {@code numero} es negativo. El enunciado restringe
     *         el dominio a enteros positivos, y aceptar negativos en silencio sería peor que
     *         rechazarlos: -1002 cumple la condición del caso base ({@code -1002 < 10}) y
     *         devolvería 1 dígito en lugar de 4
     *         <p>Complejidad: O(d) temporal y O(d) espacial, con d = cantidad de dígitos, que
     *         equivale a O(log10 n) respecto del valor del número.</p>
     */
    public static int contarDigitos(int numero) {
        if (numero < 0) {
            throw new IllegalArgumentException("Solo se admiten enteros no negativos; se recibió: "
                    + numero + ". Un negativo entraría al caso base y devolvería 1 dígito.");
        }

        // CASO BASE: todo número menor que 10 se escribe con un solo dígito, porque el rango
        // 0..9 es exactamente el conjunto de los símbolos de la base decimal. Esta única
        // condición cubre también el 0, que por decisión explícita cuenta como 1 dígito.
        // Acá la pila deja de crecer y empieza el desapilado.
        if (numero < BASE_DECIMAL) {
            return 1;
        }

        // CASO RECURSIVO: la división entera por 10 descarta el último dígito (1002 / 10 =
        // 100, se perdió el 2). El problema se reduce a contar los dígitos de ese número más
        // chico, y este nivel aporta 1 por el dígito que acaba de quitar. La suma del "1 +"
        // queda pendiente en el marco y se resuelve al DESAPILAR.
        return 1 + contarDigitos(numero / BASE_DECIMAL);
    }

    /**
     * Ejecuta un caso válido e imprime la cantidad obtenida frente a la esperada.
     *
     * @param numero número de entrada a probar
     * @param esperado cantidad de dígitos esperada, contada a mano
     */
    private static void probar(int numero, int esperado) {
        try {
            int obtenido = contarDigitos(numero);
            System.out.printf("numero = %-12d | obtenido = %-3d | esperado = %-3d | %s%n",
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
            int obtenido = contarDigitos(numero);
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
        System.out.println("=== Ejercicio 6: Contar dígitos recursivamente (sin String) ===");
        System.out.println("Decisión documentada: el 0 cuenta como 1 dígito.");
        System.out.println();
        probar(0, 1);              // decisión explícita: 1 dígito
        probar(7, 1);              // un dígito
        probar(9, 1);              // último valor del caso base
        probar(10, 2);             // primer valor que recursa
        probar(1002, 4);           // ceros intermedios
        probar(4072, 4);           // ceros intermedios, caso de la traza
        probar(987654321, 9);      // varios dígitos
        probar(Integer.MAX_VALUE, 10);  // 2147483647: máximo int, 10 dígitos

        System.out.println();
        System.out.println("Efecto de multiplicar el número por 10 (agrega una sola llamada):");
        probar(5, 1);
        probar(50, 2);
        probar(500, 3);

        System.out.println();
        System.out.println("Caso de error (se captura con try/catch):");
        probarError(-1002);  // sin validación devolvería 1 en lugar de 4
    }
}

/*
 * =====================================================================================
 * AJUSTES REALIZADOS AL PROMPT (requerido por la consigna general del práctico)
 * =====================================================================================
 * Cambio 1: se agregó "qué pasa con el 0 como entrada y por qué hay que decidir explícitamente
 *   si tiene 0 o 1 dígito".
 *   Motivo: la primera respuesta usaba el caso base n == 0 devolviendo 0, con lo cual
 *   contarDigitos(0) daba 0. Es coherente con la recursión pero contradice la escritura
 *   decimal: el 0 se escribe con un carácter. Al mover el caso base a n < 10 devolviendo 1,
 *   una sola condición resuelve el 0 y todos los números de un dígito.
 * Cambio 2: se agregó "qué pasa con números negativos".
 *   Motivo: sin validación, -1002 satisface la condición del caso base (-1002 < 10) y el
 *   método devolvía 1 dígito en lugar de 4, sin lanzar ningún error. Como el enunciado
 *   restringe el dominio a enteros positivos, corresponde rechazarlos explícitamente.
 * Cambio 3: se agregó la prohibición de convertir el número a String.
 *   Motivo: la primera respuesta resolvía el problema con String.valueOf(n).length(). Es
 *   correcto y más corto, pero no ejercita la recursión aritmética que es el objetivo del
 *   práctico, y además asigna un objeto en el heap para un cálculo que no lo necesita.
 * =====================================================================================
 */

/*
 * =====================================================================================
 * MATRIZ DE CASOS DE PRUEBA MANUALES
 * Compilar: javac Ejercicio06_ContarDigitos.java -d /tmp/v2
 * Ejecutar: java -cp /tmp/v2 Ejercicio06_ContarDigitos
 * =====================================================================================
 * ID    | CP-06-01
 * Obj.  | Verificar el flujo nominal con un número de varios dígitos y ceros intermedios.
 * Entr. | contarDigitos(1002) y contarDigitos(4072) — ya incluidos en el main.
 * Esper.| Ambas líneas: obtenido = 4, esperado = 4, OK.
 * Crit. | Aprueba si los ceros intermedios se cuentan como dígitos y el total es 4.
 * -------------------------------------------------------------------------------------
 * ID    | CP-06-02
 * Obj.  | Verificar el caso base: cualquier número menor que 10 tiene un solo dígito.
 * Entr. | contarDigitos(7) y contarDigitos(9) — ya incluidos en el main.
 * Esper.| Ambas líneas: obtenido = 1, esperado = 1, OK.
 * Crit. | Aprueba si devuelven 1 sin ejecutar ninguna llamada recursiva.
 * -------------------------------------------------------------------------------------
 * ID    | CP-06-03
 * Obj.  | Verificar los límites: el salto del caso base al primer recursivo, y el máximo int.
 * Entr. | contarDigitos(9), contarDigitos(10) y contarDigitos(2147483647).
 * Esper.| 1, 2 y 10 respectivamente, cada uno igual a su esperado, todos OK.
 * Crit. | Aprueba si 9 da 1, 10 da 2 (frontera del caso base) y Integer.MAX_VALUE da 10.
 * -------------------------------------------------------------------------------------
 * ID    | CP-06-04
 * Obj.  | Cubrir la entrada mínima del dominio y la decisión explícita sobre el 0. El
 *       | parámetro es int primitivo, por lo que no existe entrada nula; el análogo del caso
 *       | vacío es el 0, donde no queda ningún dígito por quitar.
 * Entr. | contarDigitos(0) — ya incluido en el main.
 * Esper.| Línea "numero = 0 | obtenido = 1 | esperado = 1 | OK".
 * Crit. | Aprueba si devuelve 1 (decisión documentada) y no 0.
 * -------------------------------------------------------------------------------------
 * ID    | CP-06-05
 * Obj.  | Forzar el error de rango por entrada negativa, que sin validación daría un
 *       | resultado incorrecto silencioso.
 * Entr. | contarDigitos(-1002) — ya incluido en el main vía probarError.
 * Esper.| Línea "obtenido = Solo se admiten enteros no negativos; se recibió: -1002 ..." y OK.
 * Crit. | Aprueba si lanza IllegalArgumentException y NO devuelve 1, que es lo que devolvería
 *       | el caso base si el negativo no se rechazara.
 * =====================================================================================
 */
