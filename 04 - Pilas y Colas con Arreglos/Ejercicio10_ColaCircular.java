/*
 * =====================================================================================
 * Práctico 4 — Pilas y Colas con Arreglos. Ejercicio 10: Cola circular
 * =====================================================================================
 * Prompt inicial utilizado:
 * "Analizá el diseño de una COLA CIRCULAR implementada con arreglo, que debe reutilizar los
 *  espacios liberados al inicio del arreglo, resolviendo el defecto demostrado en el ejercicio 9.
 *  El punto central: explicá CÓMO SE USA EL OPERADOR MÓDULO % para mover front y rear, y CÓMO
 *  SE DETECTA si la cola está llena o vacía. Desarrollá:
 *  - la idea conceptual: tratar el arreglo como un anillo, donde la posición siguiente a la
 *    última es la primera;
 *  - por qué (indice + 1) % capacidad logra ese efecto, mostrando con números concretos para
 *    capacidad 5 qué devuelve la expresión cuando el índice es 0, 3 y 4;
 *  - el problema de AMBIGÜEDAD que aparece y que hay que resolver explícitamente: con una cola
 *    circular, la condición front == rear se cumple tanto cuando la cola está VACÍA como cuando
 *    está LLENA, así que esos dos estados se vuelven indistinguibles mirando solo los índices;
 *  - las dos soluciones estándar a esa ambigüedad, con sus ventajas y desventajas:
 *    1) mantener un atributo contador de cantidad de elementos, y definir vacía como contador
 *       igual a 0 y llena como contador igual a capacidad;
 *    2) sacrificar una posición del arreglo, dejando siempre una vacía, y definir llena como
 *       (rear + 1) % capacidad == front.
 *    Elegí una justificando la elección;
 *  - por qué todas las operaciones siguen siendo O(1), a diferencia de la solución de desplazar
 *    elementos;
 *  - cómo recorrer la cola para imprimirla cuando los elementos 'dan la vuelta', es decir cuando
 *    rear quedó en un índice menor que front.
 *  Casos límite: cola vacía, cola llena, el momento exacto en que rear da la vuelta, llenar,
 *  vaciar parcialmente y volver a llenar reutilizando las posiciones del inicio, cola de
 *  capacidad 1.
 *  No escribas la implementación."
 *
 * Ajustes realizados:
 *  1) Se agregó el pedido explícito de resolver la AMBIGÜEDAD front == rear. La primera
 *     respuesta movía los índices con módulo y daba por cerrado el problema, pero definía
 *     isFull() como (rear + 1) % capacidad == front SIN aclarar que esa misma condición es
 *     verdadera en la cola vacía si no se sacrifica una posición. Era un bug latente: la cola
 *     habría reportado LLENA estando vacía. Pedir la ambigüedad por separado obligó a elegir
 *     una de las dos soluciones y a justificarla, que es el punto conceptual del ejercicio.
 *  2) Se agregó "cómo recorrer la cola cuando los elementos dan la vuelta". La primera
 *     respuesta imprimía el arreglo de 0 a capacidad-1, es decir el orden FÍSICO, que una vez
 *     que rear pasa por detrás de front ya no coincide con el orden de la cola. Con capacidad 5
 *     y front = 2, el orden lógico es 30,40,50,60,70 mientras el físico muestra 60,70,30,40,50.
 *     Mostrar los dos órdenes es lo que hace visible que el anillo funciona.
 *  3) Se agregó el requisito de repetir la MISMA secuencia del ejercicio 9. Sin eso, las dos
 *     implementaciones quedaban probadas con datos distintos y la comparación no demostraba
 *     nada. Con la misma secuencia, el enqueue(60) que allá fallaba acá entra en la posición 0,
 *     que es exactamente la posición que el ejercicio 9 reportaba como inutilizable.
 *
 * Estrategia: arreglo de tamaño fijo tratado como anillo; front y rear avanzan con
 * (indice + 1) % capacidad y nunca retroceden, de modo que las posiciones liberadas al inicio
 * se reutilizan sin desplazar ningún elemento. La ambigüedad vacía/llena se resuelve con un
 * atributo contador 'cantidad' (solución 1), que permite usar las capacidad posiciones.
 * Complejidad temporal: O(1) en enqueue, dequeue, front, isEmpty, isFull y size. El contraste
 * es con la alternativa de desplazar los elementos en cada dequeue, que sería O(n).
 * Complejidad espacial: O(capacidad), fija y decidida en el constructor. El costo adicional
 * respecto de la cola simple es un único int.
 * =====================================================================================
 */

/**
 * Demostración de una cola circular sobre arreglo, que resuelve el desperdicio de posiciones
 * expuesto en el ejercicio 9.
 *
 * <p>El programa ejecuta la misma secuencia de operaciones que el ejercicio 9 y muestra que el
 * {@code enqueue} que allá era rechazado acá se acepta, reutilizando la posición que la cola
 * simple había declarado inutilizable.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio10_ColaCircular {

    /** Capacidad usada en la demostración: la misma del ejercicio 9, para poder comparar. */
    private static final int CAPACIDAD_DEMO = 5;

    /**
     * Punto de entrada. Reproduce la secuencia del ejercicio 9 y después fuerza los casos
     * límite: cola genuinamente llena, vaciado completo y recarga.
     *
     * <p>Complejidad: O(capacidad) por el recorrido de las impresiones de estado.</p>
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 10 - Cola circular ===");
        System.out.println("Cola CIRCULAR de capacidad " + CAPACIDAD_DEMO + ".");
        System.out.println("Se ejecuta la MISMA secuencia del ejercicio 9 para comparar.");

        ColaCircular cola = new ColaCircular(CAPACIDAD_DEMO);
        System.out.println("\n--- Paso 0: cola recién creada ---");
        System.out.println("front = 0 y rear = -1: rear apunta 'una posición antes' del primero,");
        System.out.println("así que el primer enqueue lo deja en 0 sin ningún caso especial.");
        cola.imprimirEstado();

        System.out.println("\n--- Paso 1: enqueue(10) ---");
        cola.enqueue(10);
        cola.imprimirEstado();

        System.out.println("\n--- Paso 2: enqueue(20) ---");
        cola.enqueue(20);
        cola.imprimirEstado();

        System.out.println("\n--- Paso 3: enqueue(30) ---");
        cola.enqueue(30);
        cola.imprimirEstado();

        System.out.println("\n--- Paso 4: dequeue() -> " + cola.dequeue() + " ---");
        System.out.println("front avanzó de 0 a 1. La posición 0 queda libre, y a diferencia del");
        System.out.println("ejercicio 9 acá SÍ va a poder reutilizarse.");
        cola.imprimirEstado();

        System.out.println("\n--- Paso 5: dequeue() -> " + cola.dequeue() + " ---");
        System.out.println("front avanzó de 1 a 2. Dos posiciones libres al inicio del arreglo.");
        cola.imprimirEstado();

        System.out.println("\n--- Paso 6: enqueue(40) ---");
        cola.enqueue(40);
        cola.imprimirEstado();

        System.out.println("\n--- Paso 7: enqueue(50) ---");
        System.out.println("rear llega al último índice (" + (CAPACIDAD_DEMO - 1)
                + "). En el ejercicio 9 este era el estado que reportaba LLENA.");
        cola.enqueue(50);
        cola.imprimirEstado();

        // ------------------------------------------------------------------
        // La demostración: el enqueue que el ejercicio 9 rechazaba.
        // ------------------------------------------------------------------
        System.out.println("\n============================================================");
        System.out.println("EL PASO QUE EN EL EJERCICIO 9 FALLABA");
        System.out.println("============================================================");
        System.out.println("isFull() = " + cola.isFull() + " | size() = " + cola.size()
                + " de " + cola.capacidad() + " posiciones");
        System.out.println("La cola simple del ejercicio 9 reportaba LLENA en este mismo estado,");
        System.out.println("con 2 posiciones libres al inicio. Acá isFull() es false porque la");
        System.out.println("condición mira la CANTIDAD de elementos, no la posición de rear.");

        System.out.println("\n--- Paso 8: enqueue(60) ---");
        System.out.println("rear = (" + cola.rearActual() + " + 1) % " + CAPACIDAD_DEMO
                + " = " + ((cola.rearActual() + 1) % CAPACIDAD_DEMO)
                + "  <-- AQUÍ rear DA LA VUELTA");
        cola.enqueue(60);
        cola.imprimirEstado();
        System.out.println(">>> El 60 se escribió en la posición 0, que es exactamente la que el");
        System.out.println(">>> ejercicio 9 clasificaba como LIBRE INUTILIZABLE. El espacio se");
        System.out.println(">>> reutilizó sin desplazar ningún elemento: la operación fue O(1).");

        System.out.println("\n--- Paso 9: enqueue(70), que llena la cola de verdad ---");
        cola.enqueue(70);
        cola.imprimirEstado();
        System.out.println("Ahora sí: cantidad = capacidad = " + cola.capacidad()
                + ", así que isFull() = " + cola.isFull() + ".");
        System.out.println("Observar que front = " + cola.frontActual() + " y rear = "
                + cola.rearActual() + ": front == rear + 1 en sentido circular, que es");
        System.out.println("justamente el estado que sería AMBIGUO sin el contador.");

        System.out.println("\n--- Paso 10: enqueue(80) sobre la cola realmente llena ---");
        intentarEnqueue(cola, 80);

        // ------------------------------------------------------------------
        // Casos límite restantes.
        // ------------------------------------------------------------------
        System.out.println("\n============================================================");
        System.out.println("VACIAR POR COMPLETO Y VOLVER A LLENAR");
        System.out.println("============================================================");
        while (!cola.isEmpty()) {
            System.out.println("dequeue() -> " + cola.dequeue() + " | size = " + cola.size()
                    + " | front = " + cola.frontActual());
        }
        System.out.println("\nCola vacía. front = " + cola.frontActual() + ", rear = "
                + cola.rearActual() + ", cantidad = " + cola.size() + ".");
        System.out.println("Los índices quedaron en posiciones arbitrarias del anillo, y eso NO");
        System.out.println("es un problema: lo que define 'vacía' es cantidad == 0.");
        cola.imprimirEstado();

        System.out.println("\n--- dequeue() y front() sobre la cola vacía ---");
        intentarDequeue(cola);
        intentarFront(cola);

        System.out.println("\n--- Se vuelve a llenar desde ese estado ---");
        for (int valor = 100; valor <= 140; valor += 10) {
            cola.enqueue(valor);
            System.out.println("enqueue(" + valor + ") | front = " + cola.frontActual()
                    + " | rear = " + cola.rearActual() + " | size = " + cola.size()
                    + " | orden lógico: " + cola.ordenLogico());
        }
        cola.imprimirEstado();
        System.out.println("Se usaron las " + cola.capacidad() + " posiciones del arreglo, con los");
        System.out.println("elementos dando la vuelta. El orden lógico es el de llegada.");

        // ------------------------------------------------------------------
        // Cola de capacidad 1: el anillo más chico posible.
        // ------------------------------------------------------------------
        System.out.println("\n============================================================");
        System.out.println("CASO LÍMITE: COLA DE CAPACIDAD 1");
        System.out.println("============================================================");
        ColaCircular minima = new ColaCircular(1);
        System.out.println("Recién creada: isEmpty() = " + minima.isEmpty()
                + " | isFull() = " + minima.isFull());
        minima.enqueue(7);
        System.out.println("Tras enqueue(7): isEmpty() = " + minima.isEmpty()
                + " | isFull() = " + minima.isFull() + " | front = " + minima.frontActual()
                + " | rear = " + minima.rearActual());
        System.out.println("Con capacidad 1, front == rear tanto vacía como llena: es el caso");
        System.out.println("donde la ambigüedad es más evidente y donde la solución de");
        System.out.println("sacrificar una posición habría dejado la cola INUTILIZABLE.");
        intentarEnqueue(minima, 8);
        System.out.println("dequeue() -> " + minima.dequeue() + " | isEmpty() = "
                + minima.isEmpty());

        System.out.println("\n--- Validación del constructor ---");
        intentarCrear(0);
        intentarCrear(-3);
    }

    /**
     * Intenta un enqueue que se espera rechazado y reporta el resultado.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param cola cola sobre la que se opera
     * @param valor valor a encolar
     */
    private static void intentarEnqueue(ColaCircular cola, int valor) {
        try {
            cola.enqueue(valor);
            System.out.println("ERROR: enqueue(" + valor + ") debió lanzar excepción y no lo hizo");
        } catch (IllegalStateException e) {
            System.out.println("enqueue(" + valor + ") lanzó IllegalStateException: "
                    + e.getMessage() + "  -> OK");
        }
    }

    /**
     * Intenta un dequeue sobre una cola vacía y reporta el resultado.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param cola cola sobre la que se opera
     */
    private static void intentarDequeue(ColaCircular cola) {
        try {
            cola.dequeue();
            System.out.println("ERROR: dequeue() debió lanzar excepción y no lo hizo");
        } catch (IllegalStateException e) {
            System.out.println("dequeue() lanzó IllegalStateException: " + e.getMessage()
                    + "  -> OK");
        }
    }

    /**
     * Intenta consultar el frente de una cola vacía y reporta el resultado.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param cola cola sobre la que se opera
     */
    private static void intentarFront(ColaCircular cola) {
        try {
            cola.front();
            System.out.println("ERROR: front() debió lanzar excepción y no lo hizo");
        } catch (IllegalStateException e) {
            System.out.println("front() lanzó IllegalStateException: " + e.getMessage()
                    + "  -> OK");
        }
    }

    /**
     * Intenta construir una cola con capacidad inválida y reporta el resultado.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param capacidad capacidad inválida a probar
     */
    private static void intentarCrear(int capacidad) {
        try {
            new ColaCircular(capacidad);
            System.out.println("ERROR: capacidad " + capacidad + " debió ser rechazada");
        } catch (IllegalArgumentException e) {
            System.out.println("new ColaCircular(" + capacidad
                    + ") lanzó IllegalArgumentException: " + e.getMessage() + "  -> OK");
        }
    }
}

/**
 * Cola de enteros FIFO implementada sobre un arreglo de tamaño fijo tratado como un anillo.
 *
 * <p><b>La idea del anillo.</b> En una cola simple los índices avanzan en línea recta y mueren
 * al llegar al final del arreglo. Acá, en cambio, la posición siguiente a la última es la
 * primera: el arreglo se recorre en círculo. Eso se consigue con una sola operación,
 * {@code (indice + 1) % capacidad}, que devuelve el sucesor dentro del anillo.</p>
 *
 * <p><b>Por qué el módulo logra ese efecto.</b> El resto de dividir por la capacidad siempre
 * cae en el rango 0..capacidad-1, que es exactamente el rango de índices válidos. Con
 * capacidad 5: {@code (0+1)%5 = 1} y {@code (3+1)%5 = 4}, avances normales; pero
 * {@code (4+1)%5 = 0}, es decir que desde el último índice el sucesor es el primero. El módulo
 * no es un truco: es la aritmética del reloj, donde después de las 12 viene la 1.</p>
 *
 * <p><b>La ambigüedad front == rear y cómo se resuelve acá.</b> Si solo se guardan front y rear,
 * una cola circular no puede distinguir vacía de llena: en los dos casos los índices terminan
 * en la misma relación, porque al dar la vuelta rear vuelve a quedar junto a front. Hay dos
 * soluciones estándar:</p>
 * <ul>
 *   <li><b>Contador de elementos</b> (la elegida acá): se agrega el atributo {@code cantidad}.
 *       Vacía es {@code cantidad == 0} y llena es {@code cantidad == capacidad}. Cuesta un
 *       {@code int} y permite usar <b>las capacidad posiciones</b>.</li>
 *   <li><b>Sacrificar una posición</b>: se deja siempre una celda vacía y llena pasa a ser
 *       {@code (rear + 1) % capacidad == front}. No necesita atributo extra, pero desperdicia
 *       una posición de forma permanente y rompe el caso de capacidad 1, que quedaría
 *       inutilizable.</li>
 * </ul>
 * <p>Se eligió el contador porque el costo —un entero por cola— es menor que el de perder una
 * posición de cada arreglo, porque hace que {@code size()} sea O(1) sin ninguna cuenta, y
 * porque deja las condiciones de vacía y llena legibles y sin casos especiales.</p>
 *
 * <p><b>Invariantes.</b> (1) {@code 0 <= cantidad <= capacidad}; (2) los elementos vivos son
 * los que están en las posiciones {@code front, (front+1)%cap, ...} tomadas {@code cantidad}
 * veces; (3) {@code rear == (front + cantidad - 1) % capacidad} cuando la cola no está vacía;
 * (4) ninguna operación desplaza elementos, por eso todas son O(1).</p>
 */
class ColaCircular {

    /** Arreglo interno de tamaño fijo. Su longitud es la capacidad y nunca cambia. */
    private final int[] arreglo;

    /** Índice del primer elemento (el próximo en salir). Avanza con dequeue. */
    private int front;

    /**
     * Índice del último elemento encolado. Avanza con enqueue. Arranca en -1 para que el primer
     * {@code enqueue} lo lleve a 0 con la misma fórmula que todos los demás, sin caso especial.
     */
    private int rear;

    /**
     * Cantidad de elementos vivos. Es el atributo que desambigua vacía de llena, el rol que en
     * una cola simple cumplía la posición de rear.
     */
    private int cantidad;

    /**
     * Crea una cola circular vacía con la capacidad indicada.
     *
     * <p>Complejidad: O(capacidad) por la asignación del arreglo.</p>
     *
     * @param capacidad cantidad de posiciones del arreglo interno; debe ser mayor que 0
     * @throws IllegalArgumentException si {@code capacidad} es menor o igual que 0, porque una
     *         cola de capacidad 0 no podría aceptar ningún elemento y el módulo por 0 provocaría
     *         una división por cero en la primera operación
     */
    ColaCircular(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que 0 y se recibió "
                    + capacidad + ": una cola sin posiciones no admitiría ningún elemento, y el "
                    + "módulo por 0 fallaría en el primer avance de los índices.");
        }
        this.arreglo = new int[capacidad];
        this.front = 0;
        this.rear = -1;
        this.cantidad = 0;
    }

    /**
     * Agrega un elemento al final de la cola.
     *
     * <p>El avance {@code rear = (rear + 1) % arreglo.length} es lo que hace circular a la cola:
     * cuando rear está en la última posición, el módulo lo devuelve a 0 y se reutiliza el inicio
     * del arreglo. No se desplaza ningún elemento, así que el costo es constante.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param valor valor a encolar
     * @throws IllegalStateException si la cola está llena, es decir si ya hay tantos elementos
     *         como posiciones. A diferencia de la cola simple, acá "llena" significa realmente
     *         que no queda ninguna posición libre
     */
    public void enqueue(int valor) {
        if (isFull()) {
            throw new IllegalStateException("No se puede encolar " + valor
                    + ": la cola está llena (" + cantidad + " de " + arreglo.length
                    + " posiciones ocupadas). Acá 'llena' sí significa que no queda espacio.");
        }
        rear = (rear + 1) % arreglo.length;
        arreglo[rear] = valor;
        cantidad++;
    }

    /**
     * Quita y devuelve el elemento del frente de la cola.
     *
     * <p>Igual que en enqueue, {@code front} avanza con módulo y da la vuelta al llegar al final.
     * El valor que queda en la posición liberada es basura residual: no se borra porque nadie
     * puede leerlo mientras no sea reasignado, y borrarlo sería trabajo inútil.</p>
     *
     * <p>Complejidad: O(1). No hay desplazamiento de elementos, que es precisamente lo que
     * distingue esta solución de la alternativa O(n).</p>
     *
     * @return el valor que estaba en el frente
     * @throws IllegalStateException si la cola está vacía
     */
    public int dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("No se puede desencolar: la cola está vacía.");
        }
        int valor = arreglo[front];
        front = (front + 1) % arreglo.length;
        cantidad--;
        return valor;
    }

    /**
     * Devuelve el elemento del frente sin quitarlo.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return el valor que está en el frente
     * @throws IllegalStateException si la cola está vacía
     */
    public int front() {
        if (isEmpty()) {
            throw new IllegalStateException("No se puede consultar el frente: la cola está vacía.");
        }
        return arreglo[front];
    }

    /**
     * Indica si la cola no tiene elementos.
     *
     * <p>La condición es {@code cantidad == 0} y no una comparación entre índices: ahí está la
     * resolución de la ambigüedad.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return {@code true} si la cola está vacía
     */
    public boolean isEmpty() {
        return cantidad == 0;
    }

    /**
     * Indica si la cola no admite más elementos.
     *
     * <p>Contraste con el ejercicio 9: allá la condición era {@code rear == capacidad - 1}, que
     * daba un falso positivo cuando había posiciones libres al inicio. Acá la condición mira la
     * cantidad de elementos, así que solo es verdadera cuando el arreglo está realmente
     * completo.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return {@code true} si hay tantos elementos como posiciones
     */
    public boolean isFull() {
        return cantidad == arreglo.length;
    }

    /**
     * Devuelve la cantidad de elementos vivos.
     *
     * <p>Complejidad: O(1), porque es el atributo contador y no requiere ninguna cuenta.</p>
     *
     * @return cantidad de elementos en la cola
     */
    public int size() {
        return cantidad;
    }

    /**
     * Devuelve la capacidad del arreglo interno.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return cantidad total de posiciones
     */
    public int capacidad() {
        return arreglo.length;
    }

    /**
     * Devuelve el valor actual del índice front, para poder observarlo desde las pruebas.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return índice del primer elemento
     */
    public int frontActual() {
        return front;
    }

    /**
     * Devuelve el valor actual del índice rear, para poder observarlo desde las pruebas.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return índice del último elemento encolado
     */
    public int rearActual() {
        return rear;
    }

    /**
     * Devuelve los elementos en su orden LÓGICO, es decir de frente a final siguiendo el anillo.
     *
     * <p>Este recorrido es el que resuelve el problema de "los elementos dan la vuelta": no se
     * itera de 0 a capacidad-1, sino que se arranca en front y se avanza con módulo tantas veces
     * como elementos haya. Así el orden impreso es el de llegada, aunque físicamente los
     * elementos estén partidos entre el final y el principio del arreglo.</p>
     *
     * <p>Complejidad: O(cantidad).</p>
     *
     * @return representación del orden lógico, de frente a final
     */
    public String ordenLogico() {
        if (isEmpty()) {
            return "(vacía)";
        }
        StringBuilder sb = new StringBuilder("FRENTE -> ");
        for (int i = 0; i < cantidad; i++) {
            sb.append(arreglo[(front + i) % arreglo.length]);
            if (i < cantidad - 1) {
                sb.append(" -> ");
            }
        }
        return sb.append(" <- FINAL").toString();
    }

    /**
     * Imprime el estado interno completo: el arreglo con sus índices, la posición de front y
     * rear, los contadores y el orden lógico.
     *
     * <p>Se muestran los dos órdenes a propósito. El FÍSICO es cómo están guardados los valores
     * en el arreglo; el LÓGICO es el orden de la cola. Mientras rear no haya dado la vuelta los
     * dos coinciden; después dejan de coincidir, y esa diferencia es la prueba de que el anillo
     * está funcionando.</p>
     *
     * <p>Complejidad: O(capacidad).</p>
     */
    public void imprimirEstado() {
        System.out.print("  arreglo:  ");
        for (int i = 0; i < arreglo.length; i++) {
            System.out.printf("[%d]=%3d  ", i, arreglo[i]);
        }
        System.out.println();
        System.out.print("  punteros: ");
        for (int i = 0; i < arreglo.length; i++) {
            String puntero = "         ";
            if (!isEmpty() && i == front && i == rear) {
                puntero = "   F/R   ";
            } else if (!isEmpty() && i == front) {
                puntero = "    F    ";
            } else if (!isEmpty() && i == rear) {
                puntero = "    R    ";
            }
            System.out.print(puntero);
        }
        System.out.println();
        System.out.println("  front = " + front + " | rear = " + rear + " | cantidad = " + cantidad
                + " | isEmpty() = " + isEmpty() + " | isFull() = " + isFull());
        System.out.println("  orden FÍSICO (del arreglo, de 0 a " + (arreglo.length - 1)
                + "): " + ordenFisico());
        System.out.println("  orden LÓGICO (de la cola, desde front): " + ordenLogico());
        System.out.println("  posiciones libres: " + (arreglo.length - cantidad)
                + ", todas reutilizables (no hay posiciones inutilizables)");
    }

    /**
     * Devuelve el contenido del arreglo en su orden físico, marcando qué posiciones están vivas.
     *
     * <p>Complejidad: O(capacidad).</p>
     *
     * @return representación del arreglo en orden de índice, con las posiciones ocupadas marcadas
     */
    private String ordenFisico() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arreglo.length; i++) {
            sb.append(arreglo[i]);
            sb.append(estaViva(i) ? "*" : "·");
            if (i < arreglo.length - 1) {
                sb.append("  ");
            }
        }
        return sb.append("    (* = ocupada, · = libre)").toString();
    }

    /**
     * Indica si una posición del arreglo contiene un elemento vivo de la cola.
     *
     * <p>La comprobación recorre el tramo lógico en lugar de comparar el índice con front y rear,
     * porque esa comparación directa fallaría cuando rear quedó en un índice menor que front,
     * que es justamente el caso que introduce la circularidad.</p>
     *
     * <p>Complejidad: O(cantidad).</p>
     *
     * @param indice índice del arreglo interno
     * @return {@code true} si la posición forma parte de la cola
     */
    private boolean estaViva(int indice) {
        for (int i = 0; i < cantidad; i++) {
            if ((front + i) % arreglo.length == indice) {
                return true;
            }
        }
        return false;
    }
}
