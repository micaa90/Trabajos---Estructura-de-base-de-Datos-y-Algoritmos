/*
 * ============================================================================
 * Ejercicio 5 - Implementar una pila genérica
 * Práctico 4: Pilas y Colas con Arreglos en Java
 * ============================================================================
 *
 * Prompt inicial utilizado:
 * """
 * Analizá el diseño de una clase Pila<T> genérica implementada sobre arreglos, que deba poder
 * almacenar Integer, String u objetos simples.
 * El punto central: explicá QUÉ PROBLEMA RESUELVE el uso de genéricos y en qué se diferencia
 * de una pila hecha solamente con int. Desarrollá:
 * - la limitación concreta de PilaEnteros: solo sirve para int, y si se necesita una pila de
 *   String hay que duplicar toda la clase cambiando un solo tipo;
 * - la alternativa mala de usar Object: explicá por qué obligaría a hacer casting en cada pop,
 *   y por qué eso mueve los errores de tipo del tiempo de COMPILACIÓN al tiempo de EJECUCIÓN,
 *   con riesgo de ClassCastException. Este contraste es el corazón del ejercicio;
 * - qué garantiza el genérico: seguridad de tipos verificada por el compilador, sin casting
 *   y sin duplicar código;
 * - la particularidad técnica de Java que tenés que anticipar: no se puede escribir
 *   new T[capacidad] por el borrado de tipos (type erasure). Explicá por qué, y cuál es la
 *   solución habitual (crear un Object[] y castear, con la advertencia del compilador y el
 *   uso de @SuppressWarnings);
 * - por qué la LÓGICA de la pila (el manejo de top, push, pop) no cambia en absoluto respecto
 *   de la versión con int;
 * - qué pasa con los tipos primitivos y el autoboxing (int contra Integer);
 * - la complejidad de cada operación, que no cambia respecto de la versión no genérica.
 * Casos límite: los mismos que la pila de enteros, más el caso de almacenar null y si se
 * debe permitir.
 * No escribas la implementación.
 * """
 *
 * Ajustes realizados:
 * Sí hubo que ajustar el prompt. El prompt inicial dejaba abierto si se debe permitir
 * almacenar null; se ajustó el prompt del desarrollador para RECHAZAR null con excepción, y
 * la razón quedó documentada en el código: con null permitido, un pop que devuelve null sería
 * ambiguo (¿había un null guardado o la pila hace algo raro?), y además el arreglo interno usa
 * null como marca de "posición libre", así que un null almacenado confundiría la impresión del
 * estado interno.
 * Segundo ajuste: hubo que agregar explícitamente al prompt que el arreglo interno se declare
 * Object[] y se castee en el getter, porque el primer intento escribía (T[]) new Object[n] en
 * el constructor, lo que compila pero produce un ClassCastException diferido ("heap pollution")
 * si alguien expone el arreglo. Guardar Object[] y castear SOLO al devolver un elemento es la
 * forma segura, y el @SuppressWarnings queda localizado en los dos métodos que lo necesitan.
 * Tercer ajuste: se pidió también la línea COMENTADA con el push de tipo incorrecto, porque sin
 * ella el beneficio del genérico no queda demostrado en el propio archivo.
 *
 * ¿QUÉ PROBLEMA RESUELVEN LOS GENÉRICOS? Tres situaciones, en orden:
 *   1) PilaEnteros (ejercicio 1) solo sirve para int. Para una pila de String habría que
 *      DUPLICAR la clase completa cambiando un único tipo: PilaStrings, PilaAlumnos, etc.
 *      Código repetido que hay que mantener n veces.
 *   2) Una PilaObject evitaría la duplicación, pero pop() devolvería Object y el usuario
 *      tendría que escribir (String) pila.pop() en CADA extracción. Peor: nada impediría
 *      hacer push de un Integer en una pila "de Strings", y el error aparecería como
 *      ClassCastException en TIEMPO DE EJECUCIÓN, lejos del push culpable.
 *   3) Pila<T> da seguridad de tipos verificada en TIEMPO DE COMPILACIÓN: sin casting en el
 *      código del usuario, sin duplicar la clase y sin riesgo de ClassCastException.
 * La LÓGICA de la pila (top, push, pop, peek) es IDÉNTICA a la del ejercicio 1: el genérico
 * cambia el tipo de los elementos, no el algoritmo.
 *
 * Estrategia: pila LIFO genérica sobre un Object[] de tamaño fijo, con casting localizado a T
 * en los únicos dos métodos que devuelven elementos, y top inicial -1.
 *
 * Complejidad de las operaciones principales:
 *   push, pop, peek, isEmpty, isFull, size -> O(1), exactamente la misma que la versión con
 *   int: el genérico no agrega ningún costo en tiempo de ejecución (se borra al compilar).
 *   imprimirEstado -> O(capacidad) (diagnóstico). Espacial: O(n).
 * ============================================================================
 */

/**
 * Programa de demostración del ejercicio 5: pila genérica {@code Pila<T>} sobre arreglo de
 * tamaño fijo, probada con tres tipos distintos.
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio05_PilaGenerica {

    /**
     * Método principal: demuestra la misma estructura con {@code Integer}, {@code String} y
     * una clase propia {@code Alumno}, y recorre los casos límite.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 5 - Pila genérica Pila<T> sobre arreglo ===");

        // ------------------------------------------------------------------
        // 1) Pila<Integer>: los int se autoboxean a Integer al hacer push
        // ------------------------------------------------------------------
        System.out.println("\n--- 1) Pila<Integer> (capacidad 4) ---");
        Pila<Integer> pilaEnteros = new Pila<>(4);
        pilaEnteros.push(10);   // autoboxing: int 10 -> Integer.valueOf(10)
        pilaEnteros.push(20);
        pilaEnteros.push(30);
        pilaEnteros.imprimirEstado();
        int suma = pilaEnteros.pop() + pilaEnteros.peek(); // auto-unboxing, SIN casting
        System.out.println("  pop() + peek() = " + suma + "   (sin un solo casting)");
        pilaEnteros.imprimirEstado();

        // ------------------------------------------------------------------
        // 2) Pila<String>
        // ------------------------------------------------------------------
        System.out.println("\n--- 2) Pila<String> (capacidad 3) ---");
        Pila<String> pilaTextos = new Pila<>(3);
        pilaTextos.push("primero");
        pilaTextos.push("segundo");
        pilaTextos.push("tercero");
        pilaTextos.imprimirEstado();
        // El tipo de retorno de pop() ya es String: se puede llamar a un método de String.
        System.out.println("  pop().toUpperCase() -> " + pilaTextos.pop().toUpperCase());
        System.out.println("  peek() -> " + pilaTextos.peek());
        pilaTextos.imprimirEstado();

        // --- La línea que demuestra el beneficio del genérico ---
        // pilaTextos.push(42);
        // ^ COMENTADA A PROPÓSITO: el COMPILADOR la rechaza con el error
        //   "incompatible types: int cannot be converted to String".
        //   Con una pila de Object esta línea compilaría sin problema y el error aparecería
        //   recién en ejecución, como ClassCastException, en el momento del pop y no en el
        //   del push: lejos de la línea culpable y difícil de rastrear.
        System.out.println("  (la línea 'pilaTextos.push(42);' está comentada: no compila)");

        // ------------------------------------------------------------------
        // 3) Pila<Alumno>: una clase propia
        // ------------------------------------------------------------------
        System.out.println("\n--- 3) Pila<Alumno> (capacidad 3) ---");
        Pila<Alumno> pilaAlumnos = new Pila<>(3);
        pilaAlumnos.push(new Alumno("Micaela Gómez", 45821));
        pilaAlumnos.push(new Alumno("Juan Pérez", 45822));
        pilaAlumnos.push(new Alumno("Ana Ruiz", 45823));
        pilaAlumnos.imprimirEstado();
        Alumno retirado = pilaAlumnos.pop(); // tipo Alumno directo, sin casting
        System.out.println("  pop() -> " + retirado
                + " | legajo accedido sin casting: " + retirado.getLegajo());
        pilaAlumnos.imprimirEstado();

        // ------------------------------------------------------------------
        // 4) Casos límite sobre la Pila<String>
        // ------------------------------------------------------------------
        System.out.println("\n--- 4) Casos límite (sobre Pila<String>) ---");
        System.out.println("Estado actual: size = " + pilaTextos.size()
                + " | isFull = " + pilaTextos.isFull());

        System.out.println("\nSe llena la pila:");
        pilaTextos.push("relleno");
        pilaTextos.imprimirEstado();
        System.out.println("isFull() -> " + pilaTextos.isFull());
        try {
            System.out.println("push(\"desborde\")");
            pilaTextos.push("desborde");
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("Excepción capturada: " + e.getMessage());
        }

        System.out.println("\nSe vacía la pila por completo:");
        while (!pilaTextos.isEmpty()) {
            System.out.println("  pop() -> " + pilaTextos.pop()
                    + " | size ahora = " + pilaTextos.size());
        }
        pilaTextos.imprimirEstado();

        System.out.println("\npop() y peek() sobre pila VACÍA:");
        try {
            pilaTextos.pop();
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("  pop()  -> Excepción capturada: " + e.getMessage());
        }
        try {
            pilaTextos.peek();
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("  peek() -> Excepción capturada: " + e.getMessage());
        }

        System.out.println("\npush(null): se rechaza a propósito");
        try {
            pilaTextos.push(null);
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalArgumentException e) {
            System.out.println("  Excepción capturada: " + e.getMessage());
        }

        System.out.println("\npush después de haber vaciado (la pila sigue usable):");
        pilaTextos.push("reutilizada");
        System.out.println("  peek() -> " + pilaTextos.peek() + " | size = " + pilaTextos.size());
        pilaTextos.imprimirEstado();

        System.out.println("\nCapacidades degeneradas:");
        Pila<Integer> cero = new Pila<>(0);
        System.out.println("  Pila<Integer>(0): isEmpty = " + cero.isEmpty()
                + " | isFull = " + cero.isFull() + "   (vacía y llena a la vez)");
        try {
            new Pila<String>(-2);
        } catch (IllegalArgumentException e) {
            System.out.println("  new Pila<String>(-2) -> Excepción capturada: " + e.getMessage());
        }

        // ------------------------------------------------------------------
        // 5) Resumen del beneficio
        // ------------------------------------------------------------------
        System.out.println("\n--- 5) Resumen ---");
        System.out.println("Una sola clase Pila<T> sirvió para Integer, String y Alumno.");
        System.out.println("Con PilaEnteros habrían hecho falta 3 clases casi idénticas.");
        System.out.println("Con una PilaObject habría hecho falta un casting en cada pop y los");
        System.out.println("errores de tipo aparecerían en ejecución en lugar de al compilar.");

        System.out.println("\n=== Fin de la demostración ===");
    }
}

/**
 * Alumno: clase propia simple usada para demostrar que la pila genérica funciona con
 * cualquier tipo de referencia definido por el usuario.
 */
class Alumno {

    /** Nombre completo del alumno. */
    private final String nombre;

    /** Número de legajo. */
    private final int legajo;

    /**
     * Crea un alumno.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param nombre nombre completo
     * @param legajo número de legajo
     */
    public Alumno(String nombre, int legajo) {
        this.nombre = nombre;
        this.legajo = legajo;
    }

    /**
     * Devuelve el nombre del alumno.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return nombre completo
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el legajo del alumno.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return número de legajo
     */
    public int getLegajo() {
        return legajo;
    }

    /**
     * Representación textual del alumno.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return nombre y legajo
     */
    @Override
    public String toString() {
        return nombre + " (legajo " + legajo + ")";
    }
}

/**
 * Pila genérica (LIFO) de capacidad fija, parametrizada por el tipo de sus elementos.
 *
 * <p><b>Qué resuelve el genérico.</b> Una única implementación sirve para cualquier tipo de
 * referencia: {@code Pila<Integer>}, {@code Pila<String>}, {@code Pila<Alumno>}. El compilador
 * verifica los tipos en cada {@code push} y {@code pop}, así que el código del usuario no
 * necesita ningún casting y los errores de tipo se detectan al compilar, no en ejecución.</p>
 *
 * <p><b>Por qué el arreglo interno es {@code Object[]} y no {@code T[]}.</b> Java implementa
 * los genéricos por <i>borrado de tipos</i> (type erasure): en tiempo de ejecución no existe
 * ninguna información sobre {@code T}, el bytecode solo conoce {@code Object}. Como
 * {@code new T[capacidad]} necesitaría saber el tipo real para crear el arreglo, el compilador
 * lo prohíbe ("generic array creation"). La solución estándar es declarar el arreglo como
 * {@code Object[]} y castear a {@code T} únicamente al devolver un elemento. Ese casting es
 * seguro por construcción —solo pueden haber entrado elementos de tipo {@code T}, porque
 * {@code push} solo acepta {@code T}— pero el compilador no puede demostrarlo y emite una
 * advertencia de operación no verificada, que se silencia con {@code @SuppressWarnings
 * ("unchecked")} localizado en los dos únicos métodos que castean.</p>
 *
 * <p><b>Primitivos y autoboxing.</b> {@code T} solo puede ser un tipo de referencia:
 * {@code Pila<int>} no existe. Al escribir {@code pila.push(10)} sobre una
 * {@code Pila<Integer>}, Java convierte automáticamente el {@code int} en {@code Integer}
 * (autoboxing) y al leerlo lo convierte de vuelta (auto-unboxing). El costo es la creación de
 * objetos envoltorio, que la versión con {@code int[]} del ejercicio 1 no paga.</p>
 *
 * <p><b>La lógica no cambia.</b> El manejo de {@code top}, las condiciones de vacío y lleno y
 * las complejidades son idénticas a la pila de enteros. El genérico afecta a los tipos, no al
 * algoritmo.</p>
 *
 * <p>Complejidad: push, pop, peek, isEmpty, isFull y size son O(1).</p>
 *
 * @param <T> tipo de los elementos almacenados; debe ser un tipo de referencia
 */
class Pila<T> {

    /**
     * Arreglo interno de tamaño fijo.
     * Declarado Object[] porque el borrado de tipos impide escribir new T[capacidad].
     */
    private final Object[] arreglo;

    /** Índice del último elemento ocupado; -1 cuando la pila está vacía. */
    private int top;

    /**
     * Crea una pila vacía con la capacidad indicada.
     *
     * <p>Complejidad: O(1) temporal, O(capacidad) espacial.</p>
     *
     * @param capacidad cantidad máxima de elementos
     * @throws IllegalArgumentException si la capacidad es negativa
     */
    public Pila(int capacidad) {
        if (capacidad < 0) {
            throw new IllegalArgumentException("La capacidad no puede ser negativa: " + capacidad);
        }
        // Se crea un Object[] real, NO un (T[]): castear el arreglo entero acá compilaría pero
        // produciría un ClassCastException diferido si el arreglo se expusiera ("heap pollution").
        this.arreglo = new Object[capacidad];
        this.top = -1;
    }

    /**
     * Inserta un elemento en el tope.
     *
     * <p>El compilador verifica estáticamente que el argumento sea de tipo {@code T}: un
     * {@code push} de un tipo incorrecto no llega a compilar.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param elemento elemento a insertar; no se admite null
     * @throws IllegalArgumentException si el elemento es null
     * @throws IllegalStateException si la pila está llena
     */
    public void push(T elemento) {
        if (elemento == null) {
            // Se rechaza null por dos motivos: 1) un pop que devuelve null sería ambiguo para
            // el llamador; 2) el arreglo interno usa null como marca de posición libre, así que
            // un null almacenado falsearía la impresión del estado interno.
            throw new IllegalArgumentException("No se admiten elementos null en la pila.");
        }
        if (isFull()) {
            throw new IllegalStateException(
                    "Desbordamiento: la pila está llena (capacidad " + arreglo.length + ").");
        }
        top++;
        arreglo[top] = elemento;
    }

    /**
     * Retira y devuelve el elemento del tope.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return el elemento del tope, ya con el tipo {@code T}: el llamador no castea nada
     * @throws IllegalStateException si la pila está vacía
     */
    @SuppressWarnings("unchecked") // seguro: en arreglo solo entraron elementos de tipo T
    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Subdesbordamiento: la pila está vacía.");
        }
        T elemento = (T) arreglo[top];
        arreglo[top] = null; // libera la referencia para el recolector de basura
        top--;
        return elemento;
    }

    /**
     * Devuelve el elemento del tope sin retirarlo.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return el elemento del tope
     * @throws IllegalStateException si la pila está vacía
     */
    @SuppressWarnings("unchecked") // seguro: en arreglo solo entraron elementos de tipo T
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("La pila está vacía: no hay tope para consultar.");
        }
        return (T) arreglo[top]; // top no cambia
    }

    /**
     * Indica si la pila no contiene elementos.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return true si está vacía
     */
    public boolean isEmpty() {
        return top == -1;
    }

    /**
     * Indica si la pila alcanzó su capacidad fija.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return true si no cabe otro elemento
     */
    public boolean isFull() {
        return top == arreglo.length - 1;
    }

    /**
     * Devuelve la cantidad de elementos almacenados.
     *
     * <p>Complejidad: O(1). Dato derivado de top.</p>
     *
     * @return cantidad de elementos vivos
     */
    public int size() {
        return top + 1;
    }

    /**
     * Imprime el estado interno: contenido del arreglo con sus índices y el valor de top.
     *
     * <p>Se apoya en el {@code toString()} de cada elemento, que es polimórfico: la pila no
     * sabe de qué tipo son sus elementos y no necesita saberlo.</p>
     *
     * <p>Complejidad: O(capacidad) temporal. Método de diagnóstico.</p>
     */
    public void imprimirEstado() {
        System.out.print("  arreglo interno: ");
        if (arreglo.length == 0) {
            System.out.print("[] (capacidad 0)");
        }
        for (int i = 0; i < arreglo.length; i++) {
            String marca = (i <= top) ? "" : "*";
            System.out.print("[" + i + "]=" + arreglo[i] + marca + "  ");
        }
        System.out.println();
        System.out.println("  top = " + top + " | size = " + size()
                + " | isEmpty = " + isEmpty() + " | isFull = " + isFull()
                + "   (* = posición libre o residual)");
    }
}


