/*
 * ============================================================================
 * Ejercicio 2 - Simulador de torre de platos
 * Práctico 4: Pilas y Colas con Arreglos en Java
 * ============================================================================
 *
 * Prompt inicial utilizado:
 * """
 * Analizá este problema: simular una torre de platos, donde cada plato se representa con un
 * número o un nombre. El programa debe permitir agregar un plato, retirar el plato superior y
 * consultar cuál es el plato que está arriba.
 * El punto central del análisis: explicá por qué este problema se resuelve con una PILA y NO
 * con una cola. Desarrollá:
 * - qué restricción física tiene una torre de platos: solo se puede sacar el de arriba, porque
 *   sacar uno del medio o del fondo haría caer la torre;
 * - cómo esa restricción se corresponde exactamente con la política LIFO;
 * - qué pasaría si se modelara con una cola: mostrá concretamente que retirar el primer plato
 *   agregado significaría sacar el de abajo, lo cual es imposible en una torre real;
 * - qué operación de la pila corresponde a cada acción del enunciado (agregar plato = push,
 *   retirar el de arriba = pop, consultar el de arriba = peek);
 * - por qué el plato agregado último es el primero en salir.
 * Además definí: la estructura interna (arreglo de String o de objetos Plato), el manejo del
 * arreglo de tamaño fijo, y los casos límite: retirar de una torre vacía, agregar en una torre
 * llena (qué altura máxima tiene la torre), consultar en una torre vacía.
 * Indicá la complejidad de cada operación.
 * No escribas la implementación.
 * """
 *
 * Ajustes realizados:
 * Sí hubo que ajustar el prompt. El análisis devuelto dejaba abierta la estructura interna
 * ("arreglo de String o de objetos Plato"), lo que no alcanza para implementar: se agregó al
 * prompt del desarrollador que la torre guarde objetos Plato con nombre y número de orden de
 * apilado, porque con un simple String no se puede mostrar en qué momento entró cada plato,
 * que es justamente lo que hace evidente el orden LIFO al retirarlos.
 * También hubo que pedir explícitamente que imprimirTorre() dibuje la torre de ARRIBA hacia
 * abajo (el primer prompt no lo decía y la impresión en orden de arreglo daba la torre al
 * revés, invirtiendo visualmente el concepto que el ejercicio quiere mostrar), y se agregó un
 * imprimirEstado() aparte, porque el dibujo de la torre oculta los índices del arreglo.
 *
 * ¿Por qué una PILA y no una cola? Una torre de platos tiene una restricción FÍSICA: solo se
 * puede manipular el plato de arriba. Sacar uno del medio o del fondo haría caer la torre. Esa
 * restricción es exactamente la política LIFO (Last In, First Out): el último plato apoyado es
 * el único accesible y, por lo tanto, el primero que sale. Si se modelara con una cola (FIFO),
 * "retirar" devolvería el PRIMER plato apilado, es decir el del FONDO de la torre: una
 * operación imposible en el mundo real sin desarmar o tirar la torre. La correspondencia es
 * directa: agregar plato = push, retirar el de arriba = pop, consultar el de arriba = peek.
 *
 * Estrategia: pila LIFO de objetos Plato sobre un arreglo de tamaño fijo con un único índice
 * top que apunta al plato superior (-1 cuando la torre está vacía).
 *
 * Complejidad de las operaciones principales:
 *   agregarPlato, retirarPlato, verPlatoSuperior, estaVacia, estaLlena, cantidadPlatos -> O(1).
 *   imprimirTorre / imprimirEstado -> O(n) (son métodos de diagnóstico).
 *   Espacial: O(n) con n = altura máxima de la torre.
 * ============================================================================
 */

/**
 * Programa de demostración del ejercicio 2: simulador de una torre de platos resuelto con
 * una pila sobre arreglo de tamaño fijo.
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio02_TorreDePlatos {

    /**
     * Método principal: simula una secuencia realista de uso de la torre y fuerza los dos
     * casos límite (retirar de una torre vacía y apilar en una torre llena).
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 2 - Simulador de torre de platos (PILA) ===");
        System.out.println("Altura máxima de la torre: 5 platos\n");

        TorreDePlatos torre = new TorreDePlatos(5);
        torre.imprimirTorre();

        // --- Apilar 4 platos ---
        System.out.println("\n--- Se apilan 4 platos ---");
        String[] nombres = {"Playo grande", "Hondo", "Postre", "Playo chico"};
        for (String nombre : nombres) {
            torre.agregarPlato(nombre);
            System.out.println("agregarPlato(\"" + nombre + "\")");
            torre.imprimirTorre();
        }
        torre.imprimirEstado();

        // --- Consultar el de arriba ---
        System.out.println("\n--- Consultar el plato de arriba (peek) ---");
        System.out.println("verPlatoSuperior() -> " + torre.verPlatoSuperior());
        System.out.println("La torre NO cambia al consultar:");
        torre.imprimirTorre();

        // --- Retirar 2 platos ---
        System.out.println("\n--- Se retiran 2 platos (pop) ---");
        System.out.println("retirarPlato() -> " + torre.retirarPlato());
        torre.imprimirTorre();
        System.out.println("retirarPlato() -> " + torre.retirarPlato());
        torre.imprimirTorre();
        System.out.println("Salieron en orden INVERSO al de apilado: primero el último apoyado.");

        // --- Apilar 1 más: sobreescribe un residuo del arreglo ---
        System.out.println("\n--- Se apila 1 plato más ---");
        torre.agregarPlato("Sopero");
        System.out.println("agregarPlato(\"Sopero\")");
        torre.imprimirTorre();
        torre.imprimirEstado();

        // --- Caso límite: torre llena ---
        System.out.println("\n--- Caso límite: apilar en una torre LLENA ---");
        torre.agregarPlato("Bandeja");
        torre.agregarPlato("Ensaladera");
        System.out.println("Se apilan 2 más hasta alcanzar la altura máxima.");
        torre.imprimirTorre();
        System.out.println("estaLlena() -> " + torre.estaLlena());
        try {
            System.out.println("agregarPlato(\"Fuente\")");
            torre.agregarPlato("Fuente");
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("Excepción capturada: " + e.getMessage());
        }
        torre.imprimirEstado();

        // --- Caso límite: torre vacía ---
        System.out.println("\n--- Caso límite: retirar y consultar en una torre VACÍA ---");
        System.out.println("Se desarma la torre completa:");
        while (!torre.estaVacia()) {
            System.out.println("  retirarPlato() -> " + torre.retirarPlato()
                    + " | quedan " + torre.cantidadPlatos());
        }
        torre.imprimirTorre();
        torre.imprimirEstado();
        try {
            torre.retirarPlato();
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("retirarPlato() -> Excepción capturada: " + e.getMessage());
        }
        try {
            torre.verPlatoSuperior();
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("verPlatoSuperior() -> Excepción capturada: " + e.getMessage());
        }

        System.out.println("\n--- Contraejemplo: qué pasaría con una COLA (FIFO) ---");
        System.out.println("Apilado: Playo grande, Hondo, Postre, Playo chico");
        System.out.println("  Con PILA (LIFO) el primero en salir es 'Playo chico' = el de ARRIBA. Correcto.");
        System.out.println("  Con COLA (FIFO) el primero en salir sería 'Playo grande' = el del FONDO,");
        System.out.println("  es decir sacar el plato de abajo con 3 platos encima: la torre se cae.");

        System.out.println("\n=== Fin de la demostración ===");
    }
}

/**
 * Representa un plato individual de la torre.
 *
 * <p>Guarda el nombre y el número de orden en que fue apoyado, para que al retirarlos se
 * pueda verificar que el orden de salida es el inverso del de entrada.</p>
 */
class Plato {

    /** Nombre descriptivo del plato. */
    private final String nombre;

    /** Número de orden de apilado (1 = primer plato apoyado en la sesión). */
    private final int ordenApilado;

    /**
     * Crea un plato.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param nombre nombre descriptivo del plato
     * @param ordenApilado número de orden en que fue apoyado
     */
    public Plato(String nombre, int ordenApilado) {
        this.nombre = nombre;
        this.ordenApilado = ordenApilado;
    }

    /**
     * Devuelve el nombre del plato.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return nombre descriptivo
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Representación textual del plato.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return cadena con nombre y orden de apilado
     */
    @Override
    public String toString() {
        return nombre + " (apilado #" + ordenApilado + ")";
    }
}

/**
 * Torre de platos modelada como una pila (LIFO) sobre un arreglo de tamaño fijo.
 *
 * <p>Cada método del dominio implementa una operación de pila:
 * {@code agregarPlato} = push, {@code retirarPlato} = pop,
 * {@code verPlatoSuperior} = peek, {@code estaVacia} = isEmpty,
 * {@code estaLlena} = isFull, {@code cantidadPlatos} = size.</p>
 *
 * <p>La restricción LIFO no es una decisión arbitraria del programa: refleja la restricción
 * física real de una torre de platos, donde el único plato manipulable es el de arriba.</p>
 *
 * <p>Complejidad: todas las operaciones del dominio son O(1).</p>
 */
class TorreDePlatos {

    /** Arreglo interno de tamaño fijo: la altura máxima de la torre. */
    private final Plato[] platos;

    /**
     * Índice del plato SUPERIOR de la torre (el único accesible).
     * Vale -1 cuando la torre está vacía, porque no hay plato que señalar y -1 es el índice
     * inmediatamente anterior al primero válido (0).
     */
    private int top;

    /** Contador de platos apoyados en toda la sesión; da el número de orden de apilado. */
    private int apiladosHistoricos;

    /**
     * Crea una torre vacía con una altura máxima dada.
     *
     * <p>Complejidad: O(1) temporal, O(alturaMaxima) espacial.</p>
     *
     * @param alturaMaxima cantidad máxima de platos que soporta la torre
     * @throws IllegalArgumentException si la altura máxima es negativa
     */
    public TorreDePlatos(int alturaMaxima) {
        if (alturaMaxima < 0) {
            throw new IllegalArgumentException(
                    "La altura máxima no puede ser negativa: " + alturaMaxima);
        }
        this.platos = new Plato[alturaMaxima];
        this.top = -1; // torre vacía
        this.apiladosHistoricos = 0;
    }

    /**
     * Apoya un plato nuevo sobre la torre. Implementa la operación de pila <b>push</b>.
     *
     * <p>Complejidad: O(1). Incrementa top y escribe una sola posición del arreglo.</p>
     *
     * @param nombre nombre descriptivo del plato a apoyar
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     * @throws IllegalStateException si la torre alcanzó su altura máxima
     */
    public void agregarPlato(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El plato debe tener un nombre.");
        }
        if (estaLlena()) {
            // Altura máxima alcanzada: apoyar otro plato haría inestable la torre.
            throw new IllegalStateException("La torre está llena: altura máxima "
                    + platos.length + " platos. No se puede apoyar otro más.");
        }
        apiladosHistoricos++;
        top++; // el nuevo plato pasa a ser el superior
        platos[top] = new Plato(nombre, apiladosHistoricos);
    }

    /**
     * Retira el plato superior de la torre. Implementa la operación de pila <b>pop</b>.
     *
     * <p>Es el único retiro físicamente posible: sacar uno del medio tiraría la torre.</p>
     *
     * <p>Complejidad: O(1). Una lectura y un decremento; no desplaza platos.</p>
     *
     * @return el plato que estaba arriba
     * @throws IllegalStateException si la torre está vacía
     */
    public Plato retirarPlato() {
        if (estaVacia()) {
            throw new IllegalStateException("La torre está vacía: no hay plato para retirar.");
        }
        Plato retirado = platos[top];
        platos[top] = null; // con objetos sí conviene anular: libera la referencia para el GC
        top--;
        return retirado;
    }

    /**
     * Consulta cuál es el plato de arriba sin retirarlo. Implementa <b>peek</b>.
     *
     * <p>Se diferencia de {@link #retirarPlato()} en que no modifica top: mirar el plato de
     * arriba no es lo mismo que levantarlo.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return el plato superior
     * @throws IllegalStateException si la torre está vacía
     */
    public Plato verPlatoSuperior() {
        if (estaVacia()) {
            throw new IllegalStateException("La torre está vacía: no hay plato arriba.");
        }
        return platos[top];
    }

    /**
     * Indica si la torre no tiene platos. Implementa <b>isEmpty</b>.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return true si la torre está vacía
     */
    public boolean estaVacia() {
        return top == -1;
    }

    /**
     * Indica si la torre alcanzó su altura máxima. Implementa <b>isFull</b>.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return true si no se puede apoyar otro plato
     */
    public boolean estaLlena() {
        return top == platos.length - 1;
    }

    /**
     * Devuelve la cantidad de platos apilados. Implementa <b>size</b>.
     *
     * <p>Complejidad: O(1). Es un dato derivado de top, no un atributo aparte.</p>
     *
     * @return cantidad de platos en la torre
     */
    public int cantidadPlatos() {
        return top + 1;
    }

    /**
     * Dibuja la torre de ARRIBA hacia abajo, como se la ve físicamente.
     *
     * <p>Complejidad: O(n) temporal, donde n es la cantidad de platos apilados.</p>
     */
    public void imprimirTorre() {
        System.out.println("  Torre (" + cantidadPlatos() + "/" + platos.length + " platos):");
        if (estaVacia()) {
            System.out.println("    (no hay platos)");
        } else {
            for (int i = top; i >= 0; i--) {
                String etiqueta = (i == top) ? "  <-- ARRIBA (accesible)" : "";
                System.out.println("    |" + platos[i] + "|" + etiqueta);
            }
            System.out.println("    =========== mesa");
        }
    }

    /**
     * Imprime el estado INTERNO de la estructura: contenido del arreglo con sus índices y el
     * valor del índice de control top.
     *
     * <p>Complejidad: O(capacidad) temporal. Método de diagnóstico.</p>
     */
    public void imprimirEstado() {
        System.out.print("  [estado interno] arreglo: ");
        if (platos.length == 0) {
            System.out.print("[] (capacidad 0)");
        }
        for (int i = 0; i < platos.length; i++) {
            String contenido = (platos[i] == null) ? "null" : platos[i].getNombre();
            String marca = (i <= top) ? "" : "*";
            System.out.print("[" + i + "]=" + contenido + marca + "  ");
        }
        System.out.println();
        System.out.println("  [estado interno] top = " + top
                + " | cantidadPlatos = " + cantidadPlatos()
                + " | estaVacia = " + estaVacia()
                + " | estaLlena = " + estaLlena()
                + "   (* = posición arriba de top, no pertenece a la torre)");
    }
}

/*
 * ============================================================================
 * MATRIZ DE CASOS DE PRUEBA MANUALES
 * ============================================================================
 * Compilación: javac Ejercicio02_TorreDePlatos.java -d /tmp/v4
 * Ejecución:   java -cp /tmp/v4 Ejercicio02_TorreDePlatos
 * Todos los casos están cableados en main(); el programa no lee entrada por consola.
 *
 * ID    | Objetivo                            | Entrada exacta                                 | Resultado esperado                                                       | Criterio de aprobación
 * ------+-------------------------------------+------------------------------------------------+--------------------------------------------------------------------------+---------------------------------------------------------
 * CP-01 | Flujo nominal: orden LIFO de salida | TorreDePlatos(5); agregarPlato de "Playo       | retirarPlato() devuelve primero "Playo chico (apilado #4)" y luego        | Las dos líneas "retirarPlato() ->" muestran #4 y #3 en
 *       |                                     | grande","Hondo","Postre","Playo chico";         | "Postre (apilado #3)": orden inverso al de apilado                       | ese orden, nunca #1
 *       |                                     | retirarPlato() x2                               |                                                                          |
 * CP-02 | Consulta no destructiva (peek)      | sobre la torre con 4 platos, verPlatoSuperior() | devuelve "Playo chico (apilado #4)" y la torre sigue con 4 platos        | La impresión posterior muestra "Torre (4/5 platos)" y
 *       |                                     |                                                 |                                                                          | top = 3 sin cambios
 * CP-03 | Estructura llena: altura máxima     | tras CP-01, agregarPlato("Sopero"),            | estaLlena() -> true con 5/5 platos y ninguna posición marcada con *      | "estaLlena() -> true" y top = 4
 *       |                                     | "Bandeja", "Ensaladera"                         |                                                                          |
 * CP-04 | Error forzado: torre llena          | sobre la torre de CP-03, agregarPlato("Fuente") | IllegalStateException "La torre está llena: altura máxima 5 platos..."   | Se imprime el mensaje y el estado posterior sigue en
 *       |                                     |                                                 |                                                                          | top = 4, 5/5 platos
 * CP-05 | Error forzado: torre vacía          | desarmar la torre completa y luego               | IllegalStateException "La torre está vacía: no hay plato para retirar."  | Se imprimen los dos mensajes y el programa llega a
 *       |                                     | retirarPlato() y verPlatoSuperior()             | y "La torre está vacía: no hay plato arriba."                            | "=== Fin de la demostración ==="
 * ============================================================================
 */
