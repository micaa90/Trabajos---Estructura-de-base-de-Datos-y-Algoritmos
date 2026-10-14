/*
 * ============================================================================
 * Ejercicio 6 - Implementar una cola simple de enteros
 * Práctico 4: Pilas y Colas con Arreglos en Java
 * ============================================================================
 *
 * Prompt inicial utilizado:
 * """
 * Analizá el diseño de una clase ColaEnteros implementada sobre un arreglo de enteros de
 * TAMAÑO FIJO, con las operaciones enqueue, dequeue, front, isEmpty, isFull y size.
 * Tu análisis debe explicar:
 * - qué es una cola y qué significa la política FIFO, contrastándola con LIFO;
 * - PARA QUÉ SIRVEN los índices front y rear: qué representa cada uno y por qué una cola
 *   necesita DOS índices mientras la pila necesitaba uno solo. Desarrollá este punto, es el
 *   centro del ejercicio;
 * - CÓMO CAMBIAN front y rear cuando se agregan y cuando se eliminan elementos: mostrá que
 *   rear avanza con enqueue y front avanza con dequeue, y que por eso ambos se desplazan
 *   siempre hacia adelante y nunca retroceden;
 * - qué valores iniciales deben tener front y rear y por qué;
 * - cómo se detecta que la cola está vacía y cómo que está llena, y por qué esa detección es
 *   más delicada que en una pila;
 * - qué sucede en dequeue sobre cola vacía y en enqueue sobre cola llena;
 * - la consecuencia de que front solo avance: anticipá que las posiciones liberadas al inicio
 *   del arreglo quedan inutilizables. Mencionalo acá porque es el tema del ejercicio 9;
 * - los invariantes de la estructura;
 * - la complejidad de cada operación y por qué todas son O(1) (y por qué NO conviene
 *   implementar dequeue desplazando todos los elementos, que sería O(n)).
 * Casos límite: dequeue en cola vacía, front en cola vacía, enqueue en cola llena, llenar y
 * vaciar completamente, cola de capacidad 1.
 * No escribas la implementación.
 * """
 *
 * Ajustes realizados:
 * Sí hubo que ajustar el prompt. Tres ajustes al pasar a la implementación:
 *   1) Hubo que exigir explícitamente "dequeue debe ser O(1), sin desplazar elementos",
 *      porque la primera implementación resolvía el dequeue copiando todo el arreglo un lugar
 *      a la izquierda para mantener front siempre en 0. Eso evita el desperdicio de espacio,
 *      pero degrada dequeue de O(1) a O(n) y además borra el fenómeno que el ejercicio 9
 *      tiene que demostrar.
 *   2) Al método de impresión se le pidió mostrar el arreglo interno CON LOS ÍNDICES y los
 *      valores de front y rear señalados, y además distinguir las posiciones libres que
 *      quedaron ANTES de front de las que todavía están DESPUÉS de rear: son las dos clases
 *      de "posición libre" y solo una de las dos es reutilizable.
 *   3) Hubo que agregar "prohibido java.util.Queue y LinkedList" y "el main captura con
 *      try/catch", igual que en el ejercicio 1, para que el programa recorra todos los casos
 *      límite en una sola corrida.
 * No se ajustó la convención de los índices: el análisis ya eligió front = índice del primer
 * elemento vivo y rear = índice del último elemento ocupado, que es la que se implementó.
 *
 * ¿POR QUÉ DOS ÍNDICES Y NO UNO? La pila inserta y retira por el MISMO extremo, así que un
 * único índice (top) describe todo su estado. La cola es FIFO (First In, First Out): inserta
 * por un extremo y retira por el OTRO. Son dos puntos de trabajo que se mueven de forma
 * independiente, y ninguno se puede deducir del otro: hacen falta dos índices.
 *   - front: índice del primer elemento vivo, el próximo en salir. Avanza SOLO con dequeue.
 *   - rear:  índice del último elemento ocupado, el último que entró. Avanza SOLO con enqueue.
 * Los dos se desplazan siempre hacia adelante y NUNCA retroceden. Esa es la causa del defecto
 * que el ejercicio 9 demuestra: las posiciones anteriores a front quedan libres pero
 * inalcanzables, porque rear tampoco puede volver atrás a reutilizarlas.
 *
 * Estrategia: cola FIFO sobre int[] de tamaño fijo con dos índices que solo avanzan
 * (front = 0 y rear = -1 al construirla), sin desplazamiento de elementos en el dequeue.
 *
 * Complejidad de las operaciones principales:
 *   enqueue, dequeue, front, isEmpty, isFull, size -> O(1) temporal, O(1) espacial.
 *   imprimirEstado -> O(capacidad) temporal (diagnóstico).
 *   Constructor -> O(1) temporal, O(n) espacial con n = capacidad.
 * ============================================================================
 */

/**
 * Programa de demostración del ejercicio 6: cola simple de enteros sobre arreglo de tamaño
 * fijo.
 *
 * <p>Ejecuta la secuencia de la traza del análisis y después todos los casos límite: cola
 * vacía, cola llena, dequeue y front sobre cola vacía, vaciado total, capacidad 1 y
 * capacidad negativa.</p>
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio06_ColaEnteros {

    /**
     * Método principal: ejecuta la secuencia de prueba y todos los casos límite.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 6 - Cola simple de enteros con arreglo de tamaño fijo ===");

        // --- Flujo nominal: la secuencia de la traza del análisis, capacidad 5 ---
        System.out.println("\n--- Caso 1: flujo nominal (capacidad 5) ---");
        ColaEnteros cola = new ColaEnteros(5);
        cola.imprimirEstado();

        System.out.println("\nenqueue(10)");
        cola.enqueue(10);
        cola.imprimirEstado();

        System.out.println("\nenqueue(20)");
        cola.enqueue(20);
        cola.imprimirEstado();

        System.out.println("\nenqueue(30)");
        cola.enqueue(30);
        cola.imprimirEstado();

        System.out.println("\ndequeue() -> " + cola.dequeue() + "   (sale el PRIMERO que entró)");
        // Observar: el 10 sigue FÍSICAMENTE en la posición 0; solo avanzó front.
        cola.imprimirEstado();

        System.out.println("\ndequeue() -> " + cola.dequeue());
        cola.imprimirEstado();
        System.out.println("  ATENCIÓN: las posiciones 0 y 1 están libres pero INALCANZABLES.");
        System.out.println("  front ya pasó por ellas y rear nunca retrocede: es el tema del ejercicio 9.");

        System.out.println("\nenqueue(40)   (entra DESPUÉS de rear, no en los huecos del inicio)");
        cola.enqueue(40);
        cola.imprimirEstado();

        System.out.println("\nfront() -> " + cola.front() + "   (front NO modifica los índices)");
        cola.imprimirEstado();

        // --- Caso límite: cola llena ---
        System.out.println("\n--- Caso 2: enqueue sobre cola LLENA (desbordamiento) ---");
        System.out.println("enqueue(50)");
        cola.enqueue(50);
        cola.imprimirEstado();
        System.out.println("isFull() -> " + cola.isFull()
                + "   (rear llegó al último índice: la cola se declara llena)");
        try {
            System.out.println("enqueue(60)");
            cola.enqueue(60);
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("Excepción capturada: " + e.getMessage());
        }
        System.out.println("  Y sin embargo size() = " + cola.size() + " sobre capacidad "
                + cola.capacidad() + ": sobran " + cola.posicionesDesperdiciadas()
                + " posiciones libres al inicio que no se pueden usar.");
        cola.imprimirEstado();

        // --- Caso límite: vaciar completamente ---
        System.out.println("\n--- Caso 3: vaciar completamente ---");
        while (!cola.isEmpty()) {
            System.out.println("dequeue() -> " + cola.dequeue() + " | size ahora = " + cola.size());
        }
        cola.imprimirEstado();
        System.out.println("isEmpty() -> " + cola.isEmpty()
                + " | isFull() -> " + cola.isFull()
                + "   (vacía Y 'llena' a la vez: rear sigue en el último índice)");

        // --- Caso límite: dequeue y front sobre cola vacía ---
        System.out.println("\n--- Caso 4: dequeue y front sobre cola VACÍA ---");
        try {
            cola.dequeue();
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("dequeue() -> Excepción capturada: " + e.getMessage());
        }
        try {
            cola.front();
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("front()   -> Excepción capturada: " + e.getMessage());
        }

        // --- Caso límite: cola de capacidad 1 ---
        System.out.println("\n--- Caso 5: cola de capacidad 1 ---");
        ColaEnteros colaUno = new ColaEnteros(1);
        colaUno.imprimirEstado();
        System.out.println("enqueue(7)");
        colaUno.enqueue(7);
        colaUno.imprimirEstado();
        System.out.println("isFull() -> " + colaUno.isFull() + " | front() -> " + colaUno.front());
        System.out.println("dequeue() -> " + colaUno.dequeue());
        colaUno.imprimirEstado();
        System.out.println("Con capacidad 1 el defecto es máximo: después de UN enqueue y UN");
        System.out.println("dequeue la cola queda vacía y a la vez inservible para siempre.");
        try {
            colaUno.enqueue(8);
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("enqueue(8) -> Excepción capturada: " + e.getMessage());
        }

        // --- Caso límite: capacidades degeneradas ---
        System.out.println("\n--- Caso 6: capacidades degeneradas ---");
        ColaEnteros colaCero = new ColaEnteros(0);
        System.out.println("ColaEnteros(0): isEmpty() -> " + colaCero.isEmpty()
                + " | isFull() -> " + colaCero.isFull()
                + "   (nunca acepta un enqueue)");
        colaCero.imprimirEstado();
        try {
            new ColaEnteros(-4);
        } catch (IllegalArgumentException e) {
            System.out.println("new ColaEnteros(-4) -> Excepción capturada: " + e.getMessage());
        }

        System.out.println("\n=== Fin de la demostración ===");
    }
}

/**
 * Cola simple de enteros (estructura FIFO) implementada sobre un arreglo de tamaño fijo.
 *
 * <p>FIFO (First In, First Out): el primer elemento insertado es el primero en salir. A
 * diferencia de la pila, que trabaja en un solo extremo (LIFO), la cola inserta por el final
 * y retira por el principio.</p>
 *
 * <p><b>Por qué hacen falta DOS índices.</b> La pila del ejercicio 1 alcanzaba con uno
 * (<i>top</i>) porque push y pop tocan el mismo extremo. Acá los puntos de trabajo son dos y
 * se mueven de manera independiente, así que ninguno se deduce del otro:</p>
 * <ul>
 *   <li><b>front</b>: índice del primer elemento vivo, el próximo que va a salir. Lo mueve
 *       únicamente {@code dequeue}.</li>
 *   <li><b>rear</b>: índice del último elemento ocupado, el último que entró. Lo mueve
 *       únicamente {@code enqueue}.</li>
 * </ul>
 *
 * <p><b>Valores iniciales.</b> {@code front = 0} y {@code rear = -1}. front arranca en 0
 * porque, en cuanto entre el primer elemento, va a estar en la posición 0: front señala
 * "de dónde se saca" y ese lugar ya se conoce de antemano. rear arranca en -1 porque no hay
 * ningún elemento ocupado todavía, y -1 es el índice inmediatamente anterior al primero
 * válido, de modo que el primer {@code rear++} lo deja justo en 0. La consecuencia buscada es
 * que con la cola vacía se cumple {@code rear < front} (-1 &lt; 0), que es exactamente el
 * criterio de vacío.</p>
 *
 * <p><b>Detección de vacío y lleno, y por qué es más delicada que en una pila.</b> En la pila
 * los dos estados se leen de un solo número: {@code top == -1} es vacía y
 * {@code top == capacidad - 1} es llena, y son mutuamente excluyentes salvo capacidad 0. Acá
 * hay que combinar dos índices que avanzan por separado: la cola está vacía cuando
 * {@code rear < front} (front "pasó de largo" a rear) y se declara llena cuando
 * {@code rear == capacidad - 1} (rear no tiene más lugar hacia adelante). Y los dos estados
 * <b>pueden ser verdaderos al mismo tiempo</b>: si se llena la cola y después se la vacía,
 * queda vacía y "llena" a la vez. Eso no es un error de programación sino la consecuencia
 * directa de que los índices solo avancen, y es el defecto que el ejercicio 9 demuestra y el
 * ejercicio 10 corrige con aritmética modular.</p>
 *
 * <p><b>Invariantes:</b> (I1) {@code 0 <= front <= capacidad}; (I2)
 * {@code -1 <= rear <= capacidad - 1}; (I3) {@code front <= rear + 1}; (I4) los elementos
 * vivos son exactamente los de las posiciones {@code front..rear}; (I5) las posiciones
 * {@code 0..front-1} contienen residuos de elementos ya retirados y las posiciones
 * {@code rear+1..} contienen el 0 por defecto de Java: en ninguno de los dos casos deben
 * leerse.</p>
 *
 * <p>Complejidad: enqueue, dequeue, front, isEmpty, isFull y size son O(1), porque cada una
 * toca una sola posición del arreglo mediante aritmética de índices.</p>
 */
class ColaEnteros {

    /** Arreglo interno de tamaño fijo. Se reserva una sola vez en el constructor. */
    private final int[] arreglo;

    /**
     * Índice del primer elemento vivo: el próximo en salir.
     * Arranca en 0 y SOLO avanza (nunca retrocede), una posición por cada dequeue.
     * Nota: el campo se llama igual que el método {@link #front()}; en Java no colisionan
     * porque campos y métodos viven en espacios de nombres distintos.
     */
    private int front;

    /**
     * Índice del último elemento ocupado: el último que entró.
     * Arranca en -1 (cola vacía) y SOLO avanza, una posición por cada enqueue.
     */
    private int rear;

    /**
     * Crea una cola vacía con la capacidad indicada.
     *
     * <p>Complejidad: O(1) temporal, O(capacidad) espacial.</p>
     *
     * @param capacidad cantidad máxima de elementos que podrá almacenar la cola
     * @throws IllegalArgumentException si la capacidad es negativa
     */
    public ColaEnteros(int capacidad) {
        if (capacidad < 0) {
            throw new IllegalArgumentException("La capacidad no puede ser negativa: " + capacidad);
        }
        this.arreglo = new int[capacidad];
        this.front = 0;  // de dónde se va a sacar: la posición 0 en cuanto haya algo
        this.rear = -1;  // hasta dónde hay elementos: todavía ninguno
    }

    /**
     * Inserta un elemento al final de la cola.
     *
     * <p>rear avanza una posición y ahí se escribe el valor. front no se toca: insertar no
     * cambia quién es el próximo en salir.</p>
     *
     * <p>Complejidad: O(1). Un incremento y una escritura indexada.</p>
     *
     * @param valor entero a insertar
     * @throws IllegalStateException si la cola está llena (rear llegó al final del arreglo)
     */
    public void enqueue(int valor) {
        if (isFull()) {
            // Con tamaño fijo el desbordamiento es un caso límite real. Y acá puede ocurrir
            // teniendo posiciones libres al inicio: ver isFull() y el ejercicio 9.
            throw new IllegalStateException(
                    "Desbordamiento: la cola está llena (rear = " + rear
                            + ", capacidad " + arreglo.length + ").");
        }
        rear++;                 // el final de la cola se corre una posición hacia adelante...
        arreglo[rear] = valor;  // ...y ahí queda el nuevo último elemento
    }

    /**
     * Retira y devuelve el primer elemento de la cola.
     *
     * <p><b>Por qué NO se desplazan los elementos.</b> La alternativa "natural" sería sacar
     * siempre el de la posición 0 y correr todo el arreglo un lugar a la izquierda para que
     * front vuelva a quedar en 0. Eso evitaría el desperdicio de posiciones, pero costaría
     * O(n) por cada dequeue: con n elementos habría n-1 copias. Acá se avanza front en lugar
     * de mover datos, y el dequeue queda en O(1): se paga con espacio desperdiciado en vez de
     * con tiempo. El ejercicio 10 consigue las dos cosas con aritmética modular.</p>
     *
     * <p>Complejidad: O(1). Una lectura y un incremento.</p>
     *
     * @return el entero que estaba al frente de la cola
     * @throws IllegalStateException si la cola está vacía (subdesbordamiento)
     */
    public int dequeue() {
        if (isEmpty()) {
            // Excepción y no centinela: cualquier valor centinela (-1, 0, Integer.MIN_VALUE)
            // es un int legítimo que la cola podría contener, así que el llamador no podría
            // distinguir "cola vacía" de "el primer elemento valía -1".
            throw new IllegalStateException("Subdesbordamiento: la cola está vacía.");
        }
        int valor = arreglo[front];
        front++; // El valor NO se borra: queda como residuo en una posición ya inalcanzable.
        return valor;
    }

    /**
     * Devuelve el primer elemento de la cola sin retirarlo.
     *
     * <p>Se diferencia de {@link #dequeue()} igual que peek de pop: front consulta y dequeue
     * modifica. Después de un {@code front()} los dos índices quedan idénticos.</p>
     *
     * <p>Complejidad: O(1). Un único acceso indexado.</p>
     *
     * @return el entero que está al frente de la cola
     * @throws IllegalStateException si la cola está vacía
     */
    public int front() {
        if (isEmpty()) {
            throw new IllegalStateException("La cola está vacía: no hay frente para consultar.");
        }
        return arreglo[front]; // ni front ni rear cambian
    }

    /**
     * Indica si la cola no contiene elementos.
     *
     * <p>Está vacía cuando front "pasó de largo" a rear, es decir cuando se retiraron tantos
     * elementos como se habían insertado. Con la cola recién creada eso ya se cumple:
     * {@code -1 < 0}.</p>
     *
     * <p>Complejidad: O(1). Una comparación.</p>
     *
     * @return true si la cola está vacía
     */
    public boolean isEmpty() {
        return rear < front;
    }

    /**
     * Indica si la cola no admite más inserciones.
     *
     * <p><b>Esta es la línea que produce el falso positivo del ejercicio 9.</b> "Llena" acá
     * significa literalmente "rear no tiene más lugar hacia adelante", no "no queda ninguna
     * posición libre en el arreglo". Si front avanzó, las posiciones {@code 0..front-1} están
     * libres y la cola igual se declara llena, porque rear no puede retroceder para
     * reutilizarlas. El problema es la DEFINICIÓN de lleno, no un error de código.</p>
     *
     * <p>Complejidad: O(1). Una comparación.</p>
     *
     * @return true si no se puede insertar ningún elemento más
     */
    public boolean isFull() {
        return rear == arreglo.length - 1;
    }

    /**
     * Devuelve la cantidad de elementos vivos.
     *
     * <p>No hace falta un atributo aparte: la cantidad es información derivada de los dos
     * índices. Los elementos vivos ocupan las posiciones {@code front..rear}, y esa cantidad
     * de posiciones es {@code rear - front + 1}.</p>
     *
     * <p>Complejidad: O(1). Una resta.</p>
     *
     * @return cantidad de elementos en la cola; 0 si está vacía
     */
    public int size() {
        if (isEmpty()) {
            return 0; // con la cola vacía rear - front + 1 daría 0 o negativo: se normaliza
        }
        return rear - front + 1;
    }

    /**
     * Devuelve la capacidad máxima fija de la cola.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return capacidad del arreglo interno
     */
    public int capacidad() {
        return arreglo.length;
    }

    /**
     * Devuelve cuántas posiciones quedaron libres pero inalcanzables al inicio del arreglo.
     *
     * <p>Son exactamente las posiciones {@code 0..front-1}, o sea {@code front} posiciones:
     * una por cada dequeue realizado. Existen solo para hacer medible el defecto del
     * ejercicio 9; no forman parte del contrato de una cola.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return cantidad de posiciones desperdiciadas al inicio
     */
    public int posicionesDesperdiciadas() {
        return Math.min(front, arreglo.length);
    }

    /**
     * Imprime el estado INTERNO de la estructura: el contenido del arreglo con sus índices,
     * los valores de front y rear, y la clasificación de cada posición.
     *
     * <p>Las posiciones se marcan con {@code <F}, {@code F}, {@code R}, {@code FR} y
     * {@code *} para que se vea la diferencia entre las dos clases de posición libre: las
     * anteriores a front, que ya no se pueden reutilizar, y las posteriores a rear, que
     * todavía están disponibles.</p>
     *
     * <p>Complejidad: O(capacidad) temporal, porque recorre el arreglo completo. Es un método
     * de diagnóstico; no participa de las operaciones de la cola.</p>
     */
    public void imprimirEstado() {
        System.out.print("  arreglo interno: ");
        if (arreglo.length == 0) {
            System.out.print("[] (capacidad 0)");
        }
        for (int i = 0; i < arreglo.length; i++) {
            System.out.print("[" + i + "]=" + arreglo[i] + marcaDe(i) + "  ");
        }
        System.out.println();
        System.out.println("  front = " + front + " | rear = " + rear + " | size = " + size()
                + " | isEmpty = " + isEmpty() + " | isFull = " + isFull());
        System.out.println("  marcas: F = front, R = rear, <F = libre INUTILIZABLE (antes de"
                + " front), * = libre disponible (después de rear)");
        System.out.print("  vista FIFO (front -> rear): ");
        if (isEmpty()) {
            System.out.println("(vacía)");
        } else {
            for (int i = front; i <= rear; i++) {
                System.out.print(arreglo[i] + (i < rear ? " -> " : ""));
            }
            System.out.println();
        }
    }

    /**
     * Calcula la marca de diagnóstico de una posición del arreglo.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param i índice del arreglo interno
     * @return cadena con la marca correspondiente a esa posición
     */
    private String marcaDe(int i) {
        if (!isEmpty() && i == front && i == rear) {
            return "(FR)"; // único elemento vivo: es el frente y el final a la vez
        }
        if (!isEmpty() && i == front) {
            return "(F)";
        }
        if (!isEmpty() && i == rear) {
            return "(R)";
        }
        if (!isEmpty() && i > front && i < rear) {
            return "";     // elemento vivo intermedio: ni frente ni final
        }
        if (i < front) {
            return "<F";   // residuo de un dequeue: libre pero inalcanzable
        }
        return "*";        // todavía no se escribió nunca: libre y disponible
    }
}

