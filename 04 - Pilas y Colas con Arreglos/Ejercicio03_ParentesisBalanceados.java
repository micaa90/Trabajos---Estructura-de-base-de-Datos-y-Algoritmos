/*
 * ============================================================================
 * Ejercicio 3 - Validar paréntesis balanceados
 * Práctico 4: Pilas y Colas con Arreglos en Java
 * ============================================================================
 *
 * Prompt inicial utilizado:
 * """
 * Analizá este problema: recibir una expresión matemática como String y determinar si los
 * paréntesis están correctamente balanceados.
 * Casos: "(5 + 3) * (2 + 1)" es válido; "(5 + 3)) * (2 + 1" es inválido.
 * El punto central: explicá cómo se usa una PILA para guardar los paréntesis abiertos y
 * CUÁNDO se realiza un pop. Desarrollá:
 * - por qué el problema requiere memoria del tipo "último abierto, primero cerrado", y por qué
 *   eso es exactamente LIFO;
 * - el algoritmo paso a paso: al encontrar un paréntesis de apertura se hace push, al encontrar
 *   uno de cierre se hace pop;
 * - las TRES condiciones de invalidez, que hay que distinguir con claridad:
 *   1) aparece un cierre y la pila está vacía (cierre sin apertura previa),
 *   2) se termina de recorrer la expresión y la pila NO quedó vacía (aperturas sin cerrar),
 *   3) si se consideran varios tipos de delimitadores, el tipo del cierre no coincide con el
 *      tipo del tope;
 * - por qué NO alcanza con contar aperturas y cierres: dá el contraejemplo ")(" que tiene la
 *   misma cantidad de cada uno y sin embargo es inválido. Este punto es importante, desarrollalo;
 * - qué se hace con los caracteres que no son paréntesis;
 * - qué altura máxima puede alcanzar la pila y cómo dimensionar el arreglo de tamaño fijo;
 * - la complejidad temporal y espacial del algoritmo.
 * Casos límite: cadena vacía, cadena sin ningún paréntesis, solo aperturas, solo cierres,
 * anidamiento profundo, ")(".
 * No escribas la implementación.
 * """
 *
 * Ajustes realizados:
 * Sí hubo que ajustar el prompt. El prompt inicial mencionaba la tercera condición de
 * invalidez de forma condicional ("si se consideran varios tipos de delimitadores"), y con
 * solo paréntesis redondos esa condición NUNCA puede dispararse, así que el programa no podía
 * demostrarla. Se ajustó el prompt del desarrollador para soportar tres tipos de
 * delimitadores -- (), [] y {} -- de modo que las tres condiciones sean alcanzables y
 * verificables con datos de ejemplo.
 * El segundo ajuste fue exigir que el resultado no sea un boolean a secas: hubo que pedir
 * explícitamente que el validador informe CUÁL de las tres condiciones falló y EN QUÉ
 * POSICIÓN de la cadena, porque un true/false no permite distinguir ")(" de "((" y el
 * objetivo del ejercicio es justamente diferenciar los tipos de error.
 * Tercer ajuste: la pila no guarda solo el carácter sino también su posición original, porque
 * para el error 2 (apertura sin cerrar) hay que informar dónde estaba la apertura huérfana, y
 * esa posición ya no está disponible al terminar el recorrido.
 *
 * ¿Por qué NO alcanza con contar aperturas y cierres? El contraejemplo es ")(" : tiene
 * exactamente un '(' y un ')', así que cualquier algoritmo basado en contadores lo declararía
 * válido. Pero es inválido, porque el cierre aparece ANTES de su apertura. Contar pierde la
 * información del ORDEN, y el balanceo es un problema de orden, no de cantidad. La pila
 * conserva ese orden: el cierre de la posición 0 encuentra la pila vacía y falla de inmediato.
 *
 * Estrategia: un solo recorrido de la cadena con una pila propia sobre arreglo de tamaño fijo;
 * cada apertura hace push de (símbolo, posición) y cada cierre hace pop verificando que el
 * tope sea el delimitador complementario.
 *
 * Complejidad de las operaciones principales:
 *   validar(expresion) -> O(n) temporal con n = longitud de la expresión (un solo recorrido,
 *   trabajo O(1) por carácter), O(n) espacial en el peor caso (todas aperturas).
 *   push, pop, peek, isEmpty, isFull, size de la pila -> O(1).
 * ============================================================================
 */

/**
 * Programa de demostración del ejercicio 3: validación de delimitadores balanceados con una
 * pila propia sobre arreglo de tamaño fijo.
 *
 * <p>El validador no devuelve un simple true/false: informa cuál de las tres condiciones de
 * invalidez se cumplió y en qué posición de la cadena.</p>
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio03_ParentesisBalanceados {

    /**
     * Método principal: valida el conjunto de expresiones de prueba, incluidos todos los
     * casos límite del análisis.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 3 - Validación de paréntesis balanceados ===");

        String[] expresiones = {
            "(5 + 3) * (2 + 1)",   // válido, caso del enunciado
            "(5 + 3)) * (2 + 1",   // inválido, caso del enunciado
            ")(",                   // inválido aunque la cantidad de cada símbolo coincida
            "",                     // cadena vacía
            "sin parentesis",       // sin ningún delimitador
            "((((1))))",            // anidamiento profundo
            "(((",                  // solo aperturas
            "[(1 + 2])"             // tipos cruzados: dispara la condición 3
        };

        for (String expresion : expresiones) {
            System.out.println("\n============================================================");
            System.out.println("Expresión: \"" + expresion + "\"");
            ResultadoValidacion resultado = ValidadorDelimitadores.validar(expresion, true);
            System.out.println("  Resultado: " + (resultado.esValido() ? "VÁLIDO" : "INVÁLIDO"));
            System.out.println("  Motivo: " + resultado.getMotivo());
            if (!resultado.esValido()) {
                System.out.println("  Condición de invalidez N° " + resultado.getCondicion()
                        + " | posición del problema: " + resultado.getPosicion()
                        + " (índice base 0)");
            }
            System.out.println("  Altura máxima alcanzada por la pila: "
                    + resultado.getAlturaMaxima());
        }

        // --- Demostración explícita de por qué contar no alcanza ---
        System.out.println("\n============================================================");
        System.out.println("--- Por qué contar aperturas y cierres NO alcanza ---");
        String contraejemplo = ")(";
        int aperturas = 0;
        int cierres = 0;
        for (int i = 0; i < contraejemplo.length(); i++) {
            if (contraejemplo.charAt(i) == '(') {
                aperturas++;
            } else if (contraejemplo.charAt(i) == ')') {
                cierres++;
            }
        }
        System.out.println("Expresión \")(\": aperturas = " + aperturas
                + ", cierres = " + cierres + " -> un contador diría BALANCEADO (incorrecto).");
        ResultadoValidacion r = ValidadorDelimitadores.validar(contraejemplo, false);
        System.out.println("La pila dice: " + (r.esValido() ? "VÁLIDO" : "INVÁLIDO")
                + " -> " + r.getMotivo());
        System.out.println("Conclusión: el balanceo es un problema de ORDEN, no de CANTIDAD.");
        System.out.println("La pila conserva el orden; un contador lo pierde.");

        // --- Caso límite: desbordamiento de la pila con capacidad insuficiente ---
        System.out.println("\n============================================================");
        System.out.println("--- Caso límite: pila de capacidad insuficiente (tamaño fijo) ---");
        PilaDelimitadores pilaChica = new PilaDelimitadores(2);
        pilaChica.push('(', 0);
        pilaChica.push('(', 1);
        pilaChica.imprimirEstado();
        try {
            System.out.println("push('(', 2) sobre una pila de capacidad 2:");
            pilaChica.push('(', 2);
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("Excepción capturada: " + e.getMessage());
        }
        System.out.println("Por eso el validador dimensiona la pila con la longitud de la");
        System.out.println("expresión: en el peor caso (todas aperturas) la altura máxima es n.");

        // --- Caso límite: pop en pila vacía ---
        System.out.println("\n--- Caso límite: pop y peek sobre pila VACÍA ---");
        PilaDelimitadores vacia = new PilaDelimitadores(3);
        vacia.imprimirEstado();
        try {
            vacia.pop();
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("pop() -> Excepción capturada: " + e.getMessage());
        }
        try {
            vacia.peek();
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("peek() -> Excepción capturada: " + e.getMessage());
        }

        System.out.println("\n=== Fin de la demostración ===");
    }
}

/**
 * Validador de delimitadores balanceados: paréntesis {@code ()}, corchetes {@code []} y
 * llaves <code>{}</code>.
 *
 * <p>El problema exige memoria de tipo "el último abierto es el primero que debe cerrarse",
 * que es literalmente la política LIFO: por eso la estructura adecuada es una pila.</p>
 */
class ValidadorDelimitadores {

    /** Caracteres considerados aperturas, en el mismo orden que sus cierres. */
    private static final String APERTURAS = "([{";

    /** Caracteres considerados cierres, en el mismo orden que sus aperturas. */
    private static final String CIERRES = ")]}";

    /** Constructor privado: la clase es un contenedor de utilidades estáticas. */
    private ValidadorDelimitadores() {
    }

    /**
     * Determina si los delimitadores de la expresión están correctamente balanceados.
     *
     * <p>Algoritmo: un único recorrido de izquierda a derecha.
     * Al encontrar una APERTURA se hace <b>push</b> del símbolo junto con su posición.
     * Al encontrar un CIERRE se hace <b>pop</b> y se verifica que el tope retirado sea el
     * complemento del cierre. Los caracteres que no son delimitadores se IGNORAN: no aportan
     * información al balanceo (números, operadores, espacios, letras).</p>
     *
     * <p>Complejidad: O(n) temporal, con n = longitud de la expresión, porque cada carácter
     * se procesa una sola vez y el trabajo por carácter es O(1). O(n) espacial en el peor
     * caso, que es una expresión formada únicamente por aperturas.</p>
     *
     * @param expresion expresión a validar; no debe ser nula
     * @param trazar si es true imprime el estado de la pila tras cada carácter relevante
     * @return el resultado con validez, condición de invalidez, posición y altura máxima
     * @throws IllegalArgumentException si la expresión es nula
     */
    public static ResultadoValidacion validar(String expresion, boolean trazar) {
        if (expresion == null) {
            throw new IllegalArgumentException("La expresión no puede ser nula.");
        }

        // Dimensionado del arreglo de tamaño fijo: la altura máxima que puede alcanzar la
        // pila es la cantidad de aperturas, acotada por la longitud total de la expresión.
        // Dimensionar con expresion.length() garantiza que nunca haya desbordamiento.
        PilaDelimitadores pila = new PilaDelimitadores(expresion.length());
        int alturaMaxima = 0;

        for (int i = 0; i < expresion.length(); i++) {
            char c = expresion.charAt(i);

            if (esApertura(c)) {
                // ---- PUSH: se registra una apertura pendiente de cierre ----
                pila.push(c, i);
                if (pila.size() > alturaMaxima) {
                    alturaMaxima = pila.size();
                }
                if (trazar) {
                    System.out.println("  pos " + i + " '" + c + "' -> PUSH");
                    pila.imprimirEstado();
                }

            } else if (esCierre(c)) {
                // ---- CONDICIÓN 1: cierre sin apertura previa ----
                // La pila vacía significa que no hay ninguna apertura pendiente que este
                // cierre pueda emparejar. Es el caso que atrapa ")(".
                if (pila.isEmpty()) {
                    if (trazar) {
                        System.out.println("  pos " + i + " '" + c
                                + "' -> POP imposible: la pila está VACÍA");
                    }
                    return new ResultadoValidacion(false, 1,
                            "Condición 1: cierre '" + c + "' en la posición " + i
                            + " sin ninguna apertura previa pendiente.", i, alturaMaxima);
                }

                // ---- POP: se consume la apertura más reciente ----
                Delimitador tope = pila.pop();

                // ---- CONDICIÓN 3: el tipo del cierre no coincide con el tipo del tope ----
                if (complementoDe(tope.getSimbolo()) != c) {
                    if (trazar) {
                        System.out.println("  pos " + i + " '" + c + "' -> POP de '"
                                + tope.getSimbolo() + "' : TIPO NO COINCIDE");
                    }
                    return new ResultadoValidacion(false, 3,
                            "Condición 3: el cierre '" + c + "' de la posición " + i
                            + " no corresponde a la apertura '" + tope.getSimbolo()
                            + "' abierta en la posición " + tope.getPosicion() + ".",
                            i, alturaMaxima);
                }

                if (trazar) {
                    System.out.println("  pos " + i + " '" + c + "' -> POP: cierra la '"
                            + tope.getSimbolo() + "' de la posición " + tope.getPosicion());
                    pila.imprimirEstado();
                }

            } else if (trazar && esRelevanteParaTraza(c)) {
                // Carácter no delimitador: se ignora, no altera la pila.
                System.out.println("  pos " + i + " '" + c + "' -> IGNORADO (no delimitador)");
            }
        }

        // ---- CONDICIÓN 2: terminó el recorrido y quedaron aperturas sin cerrar ----
        // El invariante de la pila dice que contiene exactamente las aperturas todavía no
        // cerradas; si no quedó vacía, esas aperturas son huérfanas.
        if (!pila.isEmpty()) {
            Delimitador huerfana = pila.peek();
            return new ResultadoValidacion(false, 2,
                    "Condición 2: quedaron " + pila.size()
                    + " apertura(s) sin cerrar; la más reciente es '" + huerfana.getSimbolo()
                    + "' en la posición " + huerfana.getPosicion() + ".",
                    huerfana.getPosicion(), alturaMaxima);
        }

        return new ResultadoValidacion(true, 0,
                "Todos los delimitadores están correctamente balanceados y anidados.",
                -1, alturaMaxima);
    }

    /**
     * Indica si el carácter es un delimitador de apertura.
     *
     * <p>Complejidad: O(1) (la búsqueda es sobre una cadena de 3 caracteres).</p>
     *
     * @param c carácter a clasificar
     * @return true si es '(', '[' o '{'
     */
    private static boolean esApertura(char c) {
        return APERTURAS.indexOf(c) >= 0;
    }

    /**
     * Indica si el carácter es un delimitador de cierre.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param c carácter a clasificar
     * @return true si es ')', ']' o '}'
     */
    private static boolean esCierre(char c) {
        return CIERRES.indexOf(c) >= 0;
    }

    /**
     * Devuelve el cierre que corresponde a una apertura dada.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param apertura carácter de apertura
     * @return el carácter de cierre complementario
     */
    private static char complementoDe(char apertura) {
        return CIERRES.charAt(APERTURAS.indexOf(apertura));
    }

    /**
     * Filtra los caracteres irrelevantes para no inundar la traza con espacios.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param c carácter a evaluar
     * @return true si conviene mostrarlo en la traza
     */
    private static boolean esRelevanteParaTraza(char c) {
        return c != ' ';
    }
}

/**
 * Resultado inmutable de una validación.
 *
 * <p>Transporta mucho más que un boolean: la condición de invalidez que se cumplió (1, 2 o 3),
 * un mensaje explicativo, la posición del problema en la cadena y la altura máxima que
 * alcanzó la pila durante el recorrido.</p>
 */
class ResultadoValidacion {

    private final boolean valido;
    private final int condicion;
    private final String motivo;
    private final int posicion;
    private final int alturaMaxima;

    /**
     * Crea un resultado de validación.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param valido true si la expresión está balanceada
     * @param condicion número de condición de invalidez (1, 2 o 3; 0 si es válida)
     * @param motivo explicación legible del resultado
     * @param posicion índice del carácter problemático, o -1 si la expresión es válida
     * @param alturaMaxima máxima cantidad de aperturas simultáneamente pendientes
     */
    public ResultadoValidacion(boolean valido, int condicion, String motivo,
                               int posicion, int alturaMaxima) {
        this.valido = valido;
        this.condicion = condicion;
        this.motivo = motivo;
        this.posicion = posicion;
        this.alturaMaxima = alturaMaxima;
    }

    /**
     * Indica si la expresión está balanceada.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return true si es válida
     */
    public boolean esValido() {
        return valido;
    }

    /**
     * Devuelve el número de condición de invalidez.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return 1 (cierre sin apertura), 2 (apertura sin cierre), 3 (tipos cruzados) o 0
     */
    public int getCondicion() {
        return condicion;
    }

    /**
     * Devuelve la explicación del resultado.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return mensaje legible
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * Devuelve la posición del carácter problemático.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return índice base 0, o -1 si la expresión es válida
     */
    public int getPosicion() {
        return posicion;
    }

    /**
     * Devuelve la altura máxima alcanzada por la pila.
     *
     * <p>Equivale al nivel de anidamiento más profundo de la expresión.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return cantidad máxima de aperturas pendientes en simultáneo
     */
    public int getAlturaMaxima() {
        return alturaMaxima;
    }
}

/**
 * Delimitador de apertura pendiente de cierre: el símbolo y la posición donde apareció.
 *
 * <p>Guardar la posición es necesario para informar dónde quedó la apertura huérfana en la
 * condición de invalidez 2, cuando el recorrido ya terminó.</p>
 */
class Delimitador {

    private final char simbolo;
    private final int posicion;

    /**
     * Crea un delimitador pendiente.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param simbolo carácter de apertura
     * @param posicion índice en la expresión original
     */
    public Delimitador(char simbolo, int posicion) {
        this.simbolo = simbolo;
        this.posicion = posicion;
    }

    /**
     * Devuelve el símbolo de apertura.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return carácter de apertura
     */
    public char getSimbolo() {
        return simbolo;
    }

    /**
     * Devuelve la posición original del símbolo.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return índice base 0 en la expresión
     */
    public int getPosicion() {
        return posicion;
    }

    /**
     * Representación textual.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return símbolo con su posición
     */
    @Override
    public String toString() {
        return "'" + simbolo + "'@" + posicion;
    }
}

/**
 * Pila de delimitadores pendientes (LIFO) sobre un arreglo de tamaño fijo.
 *
 * <p><b>Invariante central del algoritmo:</b> en todo momento la pila contiene exactamente
 * las aperturas que ya se leyeron y todavía no se cerraron, en el orden en que aparecieron.
 * De ese invariante se deducen las tres condiciones de invalidez.</p>
 *
 * <p>Convención de top: índice del último elemento ocupado, inicial -1.</p>
 *
 * <p>Complejidad: push, pop, peek, isEmpty, isFull y size son O(1).</p>
 */
class PilaDelimitadores {

    /** Arreglo interno de tamaño fijo. */
    private final Delimitador[] arreglo;

    /** Índice del último elemento ocupado; -1 cuando la pila está vacía. */
    private int top;

    /**
     * Crea una pila vacía.
     *
     * <p>Complejidad: O(1) temporal, O(capacidad) espacial.</p>
     *
     * @param capacidad cantidad máxima de delimitadores pendientes
     * @throws IllegalArgumentException si la capacidad es negativa
     */
    public PilaDelimitadores(int capacidad) {
        if (capacidad < 0) {
            throw new IllegalArgumentException("La capacidad no puede ser negativa: " + capacidad);
        }
        this.arreglo = new Delimitador[capacidad];
        this.top = -1;
    }

    /**
     * Apila un delimitador de apertura pendiente.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param simbolo carácter de apertura
     * @param posicion índice en la expresión original
     * @throws IllegalStateException si la pila está llena
     */
    public void push(char simbolo, int posicion) {
        if (isFull()) {
            throw new IllegalStateException("Desbordamiento: la pila de delimitadores está "
                    + "llena (capacidad " + arreglo.length + ").");
        }
        top++;
        arreglo[top] = new Delimitador(simbolo, posicion);
    }

    /**
     * Retira el delimitador más reciente. Es el <b>pop</b> que se ejecuta al encontrar un
     * cierre: consume la apertura que ese cierre debe emparejar.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return el delimitador retirado
     * @throws IllegalStateException si la pila está vacía
     */
    public Delimitador pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Subdesbordamiento: la pila está vacía.");
        }
        Delimitador retirado = arreglo[top];
        arreglo[top] = null; // libera la referencia
        top--;
        return retirado;
    }

    /**
     * Consulta el delimitador más reciente sin retirarlo.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return el delimitador del tope
     * @throws IllegalStateException si la pila está vacía
     */
    public Delimitador peek() {
        if (isEmpty()) {
            throw new IllegalStateException("La pila está vacía: no hay tope para consultar.");
        }
        return arreglo[top];
    }

    /**
     * Indica si no hay aperturas pendientes.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return true si la pila está vacía
     */
    public boolean isEmpty() {
        return top == -1;
    }

    /**
     * Indica si la pila alcanzó su capacidad fija.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return true si no cabe otro delimitador
     */
    public boolean isFull() {
        return top == arreglo.length - 1;
    }

    /**
     * Devuelve la cantidad de aperturas pendientes.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return cantidad de elementos vivos
     */
    public int size() {
        return top + 1;
    }

    /**
     * Imprime el estado interno: contenido del arreglo con sus índices y el valor de top.
     *
     * <p>Complejidad: O(capacidad) temporal. Método de diagnóstico.</p>
     */
    public void imprimirEstado() {
        System.out.print("     [pila] ");
        if (arreglo.length == 0) {
            System.out.print("arreglo de capacidad 0  ");
        }
        for (int i = 0; i < arreglo.length; i++) {
            String contenido = (arreglo[i] == null) ? "-" : arreglo[i].toString();
            String marca = (i <= top) ? "" : "*";
            System.out.print("[" + i + "]=" + contenido + marca + " ");
        }
        System.out.println("| top = " + top + " | size = " + size());
    }
}

