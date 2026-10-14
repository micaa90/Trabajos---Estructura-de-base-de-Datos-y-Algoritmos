/*
 * ============================================================================
 * Ejercicio 1 - Implementar una pila de enteros
 * Práctico 4: Pilas y Colas con Arreglos en Java
 * ============================================================================
 *
 * Prompt inicial utilizado:
 * """
 * Analizá el diseño de una clase PilaEnteros implementada sobre un arreglo de enteros de
 * TAMAÑO FIJO, con las operaciones push, pop, peek, isEmpty, isFull y size.
 * Tu análisis debe explicar:
 * - qué es una pila y qué significa la política LIFO;
 * - cómo se usa el índice top: qué representa exactamente (si apunta al último elemento
 *   ocupado o a la próxima posición libre), QUÉ VALOR INICIAL debe tener y por qué, y cómo
 *   se modifica en push y en pop. Elegí una de las dos convenciones y justificá la elección;
 * - qué sucede cuando la pila está LLENA y se intenta un push, y qué sucede cuando está
 *   VACÍA y se intenta un pop o un peek: definí si se lanza excepción o se devuelve un valor
 *   centinela, y justificá por qué una de las dos opciones es mejor para int;
 * - por qué pop y peek son distintos aunque los dos miren el tope;
 * - qué relación hay entre top y size, y por qué size no necesita ser un atributo aparte;
 * - los invariantes de la estructura;
 * - la complejidad de cada operación y por qué todas son O(1);
 * - la complejidad espacial y la desventaja de usar tamaño fijo.
 * Lista exhaustiva de casos límite: push en pila llena, pop en pila vacía, peek en pila vacía,
 * pila de capacidad 0, llenar y vaciar completamente, push después de haber vaciado.
 * No escribas la implementación.
 * """
 *
 * Ajustes realizados:
 * El prompt inicial se usó tal cual para el análisis, pero hubo que ajustarlo en dos puntos
 * al pasar a la implementación:
 *   1) El prompt pedía un método "imprimir()" genérico; se lo renombró a imprimirEstado() y
 *      se le exigió mostrar el arreglo interno CON LOS ÍNDICES y el valor de top, porque con
 *      una impresión "bonita" de la pila no se ve el estado interno real (los valores
 *      residuales que quedan arriba de top después de un pop quedarían escondidos).
 *   2) Hubo que agregar explícitamente "prohibido java.util.Stack" y "el main debe capturar
 *      con try/catch los casos que lanzan excepción", porque sin eso el programa se cortaba
 *      en la primera excepción y los casos límite posteriores no se llegaban a ejecutar.
 * No se ajustó la convención de top: la primera respuesta del análisis ya eligió
 * top = índice del último elemento ocupado, que es la que se implementó.
 *
 * Estrategia: pila LIFO sobre int[] de tamaño fijo con un único índice de control top que
 * apunta al último elemento ocupado (-1 cuando está vacía).
 *
 * Complejidad de las operaciones principales:
 *   push, pop, peek, isEmpty, isFull, size -> O(1) temporal, O(1) espacial.
 *   Constructor -> O(1) temporal (reserva), O(n) espacial con n = capacidad.
 * ============================================================================
 */

/**
 * Programa de demostración del ejercicio 1: pila de enteros sobre arreglo de tamaño fijo.
 *
 * <p>Recorre todos los casos límite del análisis: pila vacía, pila llena, pop y peek sobre
 * pila vacía, push sobre pila llena, capacidad 0, vaciado total y reutilización posterior.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio01_PilaEnteros {

    /**
     * Método principal: ejecuta la secuencia de prueba y todos los casos límite.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 1 - Pila de enteros con arreglo de tamaño fijo ===");

        // --- Flujo nominal: la secuencia de la traza del análisis, capacidad 4 ---
        System.out.println("\n--- Caso 1: flujo nominal (capacidad 4) ---");
        PilaEnteros pila = new PilaEnteros(4);
        pila.imprimirEstado();

        System.out.println("\npush(10)");
        pila.push(10);
        pila.imprimirEstado();

        System.out.println("\npush(20)");
        pila.push(20);
        pila.imprimirEstado();

        System.out.println("\npush(30)");
        pila.push(30);
        pila.imprimirEstado();

        System.out.println("\npop() -> " + pila.pop());
        // Observar: el 30 sigue FÍSICAMENTE en la posición 2; solo bajó top.
        pila.imprimirEstado();

        System.out.println("\npeek() -> " + pila.peek() + "   (peek NO modifica top)");
        pila.imprimirEstado();

        System.out.println("\npush(40)");
        pila.push(40);
        pila.imprimirEstado();

        System.out.println("\npush(50)");
        pila.push(50);
        pila.imprimirEstado();

        // --- Caso límite: push sobre pila llena ---
        System.out.println("\n--- Caso 2: push sobre pila LLENA (desbordamiento) ---");
        System.out.println("isFull() -> " + pila.isFull());
        try {
            System.out.println("push(60)");
            pila.push(60);
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("Excepción capturada: " + e.getMessage());
        }
        pila.imprimirEstado();

        // --- Caso límite: vaciar completamente ---
        System.out.println("\n--- Caso 3: vaciar completamente ---");
        while (!pila.isEmpty()) {
            System.out.println("pop() -> " + pila.pop() + " | size ahora = " + pila.size());
        }
        pila.imprimirEstado();
        System.out.println("isEmpty() -> " + pila.isEmpty());

        // --- Caso límite: pop y peek sobre pila vacía ---
        System.out.println("\n--- Caso 4: pop y peek sobre pila VACÍA ---");
        try {
            pila.pop();
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("pop() -> Excepción capturada: " + e.getMessage());
        }
        try {
            pila.peek();
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("peek() -> Excepción capturada: " + e.getMessage());
        }

        // --- Caso límite: push después de haber vaciado (la pila sigue usable) ---
        System.out.println("\n--- Caso 5: push después de haber vaciado ---");
        pila.push(99);
        System.out.println("push(99) | peek() -> " + pila.peek() + " | size = " + pila.size());
        pila.imprimirEstado();

        // --- Caso límite: capacidad 0 y capacidad negativa ---
        System.out.println("\n--- Caso 6: capacidades degeneradas ---");
        PilaEnteros pilaCero = new PilaEnteros(0);
        System.out.println("PilaEnteros(0): isEmpty() -> " + pilaCero.isEmpty()
                + " | isFull() -> " + pilaCero.isFull()
                + "   (vacía y llena a la vez: nunca acepta un push)");
        pilaCero.imprimirEstado();
        try {
            pilaCero.push(1);
        } catch (IllegalStateException e) {
            System.out.println("push(1) -> Excepción capturada: " + e.getMessage());
        }
        try {
            new PilaEnteros(-3);
        } catch (IllegalArgumentException e) {
            System.out.println("new PilaEnteros(-3) -> Excepción capturada: " + e.getMessage());
        }

        System.out.println("\n=== Fin de la demostración ===");
    }
}

/**
 * Pila de enteros (estructura LIFO) implementada sobre un arreglo de tamaño fijo.
 *
 * <p>LIFO (Last In, First Out): el último elemento insertado es el primero en salir.
 * Toda la manipulación ocurre en un solo extremo, el tope.</p>
 *
 * <p><b>Convención del índice top:</b> top es el índice del ÚLTIMO elemento ocupado.
 * Su valor inicial es -1 porque con la pila vacía no hay ningún elemento ocupado y -1 es
 * el índice "inmediatamente anterior" al primero válido (0). Con esta convención
 * size = top + 1 de forma directa y peek es simplemente arreglo[top], sin restarle 1.
 * La convención alternativa (top = próxima posición libre, inicial 0) obliga a escribir
 * arreglo[top - 1] en pop y peek, que es más fácil de equivocar.</p>
 *
 * <p><b>Invariantes:</b> -1 &lt;= top &lt;= capacidad - 1; las posiciones 0..top contienen
 * elementos vivos; las posiciones top+1..capacidad-1 contienen basura (valores residuales
 * de elementos ya retirados o el 0 por defecto de Java) y no deben leerse.</p>
 *
 * <p>Complejidad: todas las operaciones son O(1) porque acceden a una única posición del
 * arreglo mediante aritmética de índices, sin recorrer ni desplazar nada.</p>
 */
class PilaEnteros {

    /** Arreglo interno de tamaño fijo. Se reserva una sola vez en el constructor. */
    private final int[] arreglo;

    /**
     * Índice del último elemento ocupado.
     * Vale -1 cuando la pila está vacía (no hay ningún elemento que señalar).
     */
    private int top;

    /**
     * Crea una pila vacía con la capacidad indicada.
     *
     * <p>Complejidad: O(1) temporal, O(capacidad) espacial.</p>
     *
     * @param capacidad cantidad máxima de elementos que podrá almacenar la pila
     * @throws IllegalArgumentException si la capacidad es negativa
     */
    public PilaEnteros(int capacidad) {
        if (capacidad < 0) {
            throw new IllegalArgumentException("La capacidad no puede ser negativa: " + capacidad);
        }
        this.arreglo = new int[capacidad];
        this.top = -1; // pila vacía: ningún índice ocupado
    }

    /**
     * Inserta un elemento en el tope de la pila.
     *
     * <p>Complejidad: O(1). Incrementa top y escribe una sola posición; no hay recorridos.</p>
     *
     * @param valor entero a insertar
     * @throws IllegalStateException si la pila está llena (desbordamiento por tamaño fijo)
     */
    public void push(int valor) {
        if (isFull()) {
            // El tamaño fijo convierte el desbordamiento en un caso límite real, no teórico.
            throw new IllegalStateException(
                    "Desbordamiento: la pila está llena (capacidad " + arreglo.length + ").");
        }
        top++;                  // top pasa a señalar la primera posición libre...
        arreglo[top] = valor;   // ...que a partir de ahora es el último elemento ocupado
    }

    /**
     * Retira y devuelve el elemento del tope.
     *
     * <p>Se diferencia de {@link #peek()} en que pop MODIFICA la pila: baja top, por lo que
     * el elemento deja de pertenecer a la estructura. peek solo consulta.</p>
     *
     * <p>Complejidad: O(1). Una lectura y un decremento.</p>
     *
     * @return el entero que estaba en el tope
     * @throws IllegalStateException si la pila está vacía (subdesbordamiento)
     */
    public int pop() {
        if (isEmpty()) {
            // Se lanza excepción en lugar de devolver un centinela (-1, 0, Integer.MIN_VALUE)
            // porque cualquier valor centinela es un int legítimo que la pila podría contener:
            // el llamador no podría distinguir "pila vacía" de "el tope valía -1".
            throw new IllegalStateException("Subdesbordamiento: la pila está vacía.");
        }
        int valor = arreglo[top];
        top--; // El valor NO se borra del arreglo: queda como residuo arriba de top.
        return valor;
    }

    /**
     * Devuelve el elemento del tope sin retirarlo.
     *
     * <p>Complejidad: O(1). Un único acceso indexado.</p>
     *
     * @return el entero que está en el tope
     * @throws IllegalStateException si la pila está vacía
     */
    public int peek() {
        if (isEmpty()) {
            throw new IllegalStateException("La pila está vacía: no hay tope para consultar.");
        }
        return arreglo[top]; // top no cambia: la pila queda idéntica
    }

    /**
     * Indica si la pila no contiene elementos.
     *
     * <p>Complejidad: O(1). Una comparación.</p>
     *
     * @return true si la pila está vacía
     */
    public boolean isEmpty() {
        return top == -1; // única forma de estar vacía con esta convención
    }

    /**
     * Indica si la pila alcanzó su capacidad máxima.
     *
     * <p>Complejidad: O(1). Una comparación.</p>
     *
     * @return true si no se puede insertar ningún elemento más
     */
    public boolean isFull() {
        return top == arreglo.length - 1; // top llegó al último índice válido
    }

    /**
     * Devuelve la cantidad de elementos almacenados.
     *
     * <p>No hace falta un atributo aparte: la cantidad es información derivada de top.
     * Mantener dos atributos para el mismo dato habilitaría estados inconsistentes.</p>
     *
     * <p>Complejidad: O(1). Una suma.</p>
     *
     * @return cantidad de elementos vivos en la pila
     */
    public int size() {
        return top + 1; // top es el último índice ocupado, los índices arrancan en 0
    }

    /**
     * Devuelve la capacidad máxima fija de la pila.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return capacidad del arreglo interno
     */
    public int capacidad() {
        return arreglo.length;
    }

    /**
     * Imprime el estado INTERNO de la estructura: el contenido del arreglo con sus índices,
     * el valor de top y las posiciones que contienen valores residuales.
     *
     * <p>Complejidad: O(capacidad) temporal, porque recorre el arreglo completo. Es un método
     * de diagnóstico; no participa de las operaciones de la pila.</p>
     */
    public void imprimirEstado() {
        System.out.print("  arreglo interno: ");
        if (arreglo.length == 0) {
            System.out.print("[] (capacidad 0)");
        }
        for (int i = 0; i < arreglo.length; i++) {
            String marca = (i <= top) ? "" : "*"; // * = valor residual / basura
            System.out.print("[" + i + "]=" + arreglo[i] + marca + "  ");
        }
        System.out.println();
        System.out.println("  top = " + top + " | size = " + size()
                + " | isEmpty = " + isEmpty() + " | isFull = " + isFull()
                + "   (* = posición arriba de top: valor residual, no pertenece a la pila)");
        System.out.print("  vista LIFO (tope -> base): ");
        if (isEmpty()) {
            System.out.println("(vacía)");
        } else {
            for (int i = top; i >= 0; i--) {
                System.out.print(arreglo[i] + (i > 0 ? " -> " : ""));
            }
            System.out.println();
        }
    }
}

/*
 * ============================================================================
 * MATRIZ DE CASOS DE PRUEBA MANUALES
 * ============================================================================
 * Compilación: javac Ejercicio01_PilaEnteros.java -d /tmp/v4
 * Ejecución:   java -cp /tmp/v4 Ejercicio01_PilaEnteros
 * Todos los casos están cableados en main(); el programa no lee entrada por consola.
 *
 * ID    | Objetivo                          | Entrada exacta                              | Resultado esperado                                                      | Criterio de aprobación
 * ------+-----------------------------------+---------------------------------------------+-------------------------------------------------------------------------+-----------------------------------------------------------
 * CP-01 | Flujo nominal LIFO                | new PilaEnteros(4); push(10),push(20),      | pop devuelve 30 (el último insertado), peek devuelve 20 sin modificar    | "pop() -> 30" y "peek() -> 20"; después de peek, top sigue
 *       |                                   | push(30), pop(), peek()                     | la pila; top queda en 1                                                 | valiendo 1 en imprimirEstado()
 * CP-02 | Estructura vacía: consultas        | pila recién construida PilaEnteros(4)       | isEmpty = true, size = 0, top = -1, vista LIFO "(vacía)"                | La primera impresión de estado muestra top = -1 y size = 0
 * CP-03 | Estructura llena: detección        | push(10),push(20),push(30),pop(),push(40),  | isFull = true, size = 4, top = 3, ninguna posición marcada con *        | "isFull() -> true" y top = 3 con capacidad 4
 *       |                                   | push(50)                                    |                                                                         |
 * CP-04 | Error forzado: push en pila llena | sobre la pila de CP-03, push(60)            | IllegalStateException "Desbordamiento: la pila está llena (capacidad 4)."| Se imprime el mensaje de la excepción y el estado queda intacto
 *       |                                   |                                             |                                                                         | (top = 3, size = 4)
 * CP-05 | Error forzado: pop/peek en vacía  | vaciar la pila con pops y luego pop()       | IllegalStateException "Subdesbordamiento: la pila está vacía." y        | Se imprimen los dos mensajes de excepción y el programa
 *       |                                   | y peek()                                    | "La pila está vacía: no hay tope para consultar."                       | continúa hasta "=== Fin de la demostración ==="
 * ============================================================================
 */
