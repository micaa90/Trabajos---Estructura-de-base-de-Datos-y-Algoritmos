/*
 * ============================================================================
 * Ejercicio 9 - Problema de desperdicio de espacio en la cola simple
 * Práctico 4: Pilas y Colas con Arreglos en Java
 * ============================================================================
 *
 * Prompt inicial utilizado:
 * """
 * Analizá este problema: DEMOSTRAR el problema del desperdicio de posiciones en una cola
 * simple implementada con arreglo.
 * El objetivo del ejercicio no es que la cola funcione bien, sino EXPONER SU DEFECTO. Tu
 * análisis debe explicar:
 * - por qué el defecto aparece: como front solo avanza y nunca retrocede, las posiciones
 *   anteriores a front quedan libres pero INALCANZABLES, porque rear tampoco puede volver
 *   atrás;
 * - la situación paradójica concreta que hay que demostrar: la cola reporta estar LLENA
 *   (rear llegó al final del arreglo) cuando en realidad hay posiciones vacías al principio.
 *   Este es el resultado que el programa tiene que hacer visible;
 * - qué secuencia exacta de operaciones enqueue y dequeue produce esa situación con el menor
 *   número de pasos posible, para un arreglo de capacidad 5. Especificá la secuencia;
 * - por qué la condición isFull basada en rear es la que genera el falso positivo;
 * - cuáles son las dos soluciones posibles y por qué una es mala: desplazar todos los
 *   elementos en cada dequeue (resuelve el desperdicio pero convierte dequeue en O(n)), o usar
 *   una cola circular con aritmética modular (mantiene O(1)). Esto conecta con el ejercicio 10;
 * - cuánto espacio se desperdicia en el peor caso.
 * Lo que el programa debe MOSTRAR: el contenido del arreglo interno con los valores de front
 * y rear después de cada operación, marcando qué posiciones están libres pero inutilizables.
 * No escribas la implementación.
 * """
 *
 * Ajustes realizados:
 * Sí hubo que ajustar el prompt, en el punto de la secuencia. El análisis respondió con la
 * secuencia de MENOR cantidad de pasos en sentido estricto: enqueue(10), dequeue(),
 * enqueue(20), enqueue(30), enqueue(40), enqueue(50) —6 operaciones, porque hacen falta 5
 * enqueue para que rear llegue al índice 4 y al menos 1 dequeue para mover front—. Pero esa
 * secuencia desperdicia UNA sola posición, y con una sola el fenómeno se lee como un detalle.
 * Se ajustó el prompt para pedir la secuencia mínima que desperdicie DOS posiciones:
 * enqueue(10), enqueue(20), enqueue(30), dequeue(), dequeue(), enqueue(40), enqueue(50)
 * —7 operaciones—, que además es la misma traza del ejercicio 6 con un enqueue más, de modo
 * que la comparación entre los dos ejercicios es directa.
 * Segundo ajuste: hubo que pedir explícitamente las TRES clasificaciones por posición (LIBRE
 * INUTILIZABLE, OCUPADA, LIBRE DISPONIBLE). El primer intento marcaba solo "ocupada" y
 * "libre", y con esa única distinción el defecto no se ve: lo que hay que mostrar es que hay
 * DOS clases de posición libre y que solo una sirve.
 * Tercer ajuste: se pidió que el mensaje final CALCULE N en lugar de escribir "2" a mano,
 * para que la demostración siga siendo válida si se cambia la secuencia.
 *
 * EL DEFECTO QUE ESTE PROGRAMA DEMUESTRA. En una cola simple sobre arreglo, front avanza con
 * cada dequeue y rear avanza con cada enqueue, y NINGUNO de los dos puede retroceder. Por eso
 * las posiciones 0..front-1, que quedaron libres al retirar elementos, son inalcanzables: el
 * único que podría escribir en ellas es rear, y rear ya pasó de largo. La consecuencia es una
 * situación paradójica: la cola se declara LLENA porque rear llegó al último índice, teniendo
 * posiciones vacías al principio del arreglo.
 *
 * POR QUÉ NO ES UN BUG. El código es correcto respecto de su propia definición de "lleno":
 * isFull() pregunta "¿tiene rear lugar hacia adelante?" y la respuesta es honesta. El problema
 * está en la DEFINICIÓN, no en la implementación: "lleno" debería significar "no queda ninguna
 * posición libre", y con índices que solo avanzan esas dos cosas no coinciden.
 *
 * CUÁNTO SE DESPERDICIA. Se desperdician exactamente tantas posiciones como dequeue se hayan
 * hecho, es decir front posiciones. En el PEOR CASO se desperdicia el arreglo COMPLETO: si se
 * llenan las 5 posiciones y después se vacía la cola, queda front = 5 y rear = 4, o sea una
 * cola simultáneamente VACÍA y "LLENA", con capacidad 5 y 0 elementos, inservible para
 * siempre. Con capacidad n el desperdicio máximo es n.
 *
 * LAS DOS SOLUCIONES POSIBLES.
 *   1) MALA: desplazar todos los elementos un lugar a la izquierda en cada dequeue, para que
 *      front vuelva siempre a 0. Elimina el desperdicio, pero cada dequeue copia hasta n-1
 *      elementos, así que la operación pasa de O(1) a O(n). Se cambia espacio por tiempo, y el
 *      tiempo se paga en TODAS las extracciones, no solo cuando la cola se llena.
 *   2) BUENA: COLA CIRCULAR con aritmética modular. Al avanzar los índices con
 *      (indice + 1) % capacidad, rear vuelve a 0 después del último índice y reutiliza las
 *      posiciones liberadas al inicio. Mantiene todas las operaciones en O(1) y usa el arreglo
 *      completo. Es exactamente lo que implementa el ejercicio 10, que ejecuta esta misma
 *      secuencia y logra el enqueue que acá falla.
 *
 * Estrategia: cola simple (NO circular) de capacidad 5 instrumentada con un imprimirEstado()
 * que clasifica cada posición del arreglo, para que la SALIDA del programa sea la demostración.
 *
 * Complejidad de las operaciones principales:
 *   enqueue, dequeue, front, isEmpty, isFull, size -> O(1) temporal, O(1) espacial.
 *   imprimirEstado -> O(capacidad) temporal (diagnóstico; es el corazón de la demostración).
 *   Espacial: O(n) con n = capacidad.
 * ============================================================================
 */

/**
 * Programa de demostración del ejercicio 9: hace visible el desperdicio de posiciones de una
 * cola simple sobre arreglo.
 *
 * <p>Ejecuta la secuencia mínima que provoca el falso positivo de {@code isFull()} con
 * capacidad 5 —tres enqueue, dos dequeue y dos enqueue— imprimiendo el estado interno
 * clasificado después de cada operación, y termina intentando el enqueue que la cola rechaza
 * teniendo posiciones libres.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio09_DesperdicioEspacioCola {

    /** Capacidad del arreglo usada en la demostración, fijada por el análisis. */
    private static final int CAPACIDAD_DEMO = 5;

    /**
     * Método principal: ejecuta la secuencia que expone el defecto y lo informa con números.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 9 - Desperdicio de espacio en la cola simple ===");
        System.out.println("Cola SIMPLE (no circular) de capacidad " + CAPACIDAD_DEMO + ".");
        System.out.println("El objetivo no es que la cola funcione bien: es EXPONER SU DEFECTO.");

        ColaSimpleInstrumentada cola = new ColaSimpleInstrumentada(CAPACIDAD_DEMO);
        System.out.println("\n--- Paso 0: cola recién creada ---");
        cola.imprimirEstado();

        // ------------------------------------------------------------------
        // Secuencia exacta indicada por el análisis (7 operaciones).
        // Es la traza del ejercicio 6 con un enqueue más, para que la
        // comparación entre los dos ejercicios sea directa.
        // ------------------------------------------------------------------
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
        System.out.println("front avanzó de 0 a 1. La posición 0 quedó LIBRE pero ya nadie");
        System.out.println("puede escribir en ella: rear solo avanza hacia adelante.");
        cola.imprimirEstado();

        System.out.println("\n--- Paso 5: dequeue() -> " + cola.dequeue() + " ---");
        System.out.println("front avanzó de 1 a 2. Ahora son DOS las posiciones perdidas.");
        cola.imprimirEstado();

        System.out.println("\n--- Paso 6: enqueue(40) ---");
        System.out.println("El 40 NO va a la posición 0 (que está libre): va después de rear.");
        cola.enqueue(40);
        cola.imprimirEstado();

        System.out.println("\n--- Paso 7: enqueue(50) ---");
        System.out.println("rear llega al ÚLTIMO índice del arreglo (" + (CAPACIDAD_DEMO - 1)
                + "). Observar qué dice isFull().");
        cola.enqueue(50);
        cola.imprimirEstado();

        // ------------------------------------------------------------------
        // La demostración: isFull() es true teniendo posiciones libres.
        // ------------------------------------------------------------------
        System.out.println("\n============================================================");
        System.out.println("LA SITUACIÓN PARADÓJICA");
        System.out.println("============================================================");
        int desperdiciadas = cola.posicionesDesperdiciadas();
        int libresTotales = cola.capacidad() - cola.size();
        System.out.println("isFull()  = " + cola.isFull());
        System.out.println("size()    = " + cola.size() + " elementos");
        System.out.println("capacidad = " + cola.capacidad() + " posiciones");
        System.out.println("libres    = " + libresTotales + " posiciones ("
                + desperdiciadas + " antes de front + "
                + (libresTotales - desperdiciadas) + " después de rear)");
        System.out.println();
        System.out.println(">>> La cola reporta LLENA pero hay " + desperdiciadas
                + " posiciones libres al inicio");
        System.out.println(">>> que no se pueden reutilizar (índices 0 a "
                + (desperdiciadas - 1) + ").");
        System.out.println(">>> Se está usando el " + (100 * cola.size() / cola.capacidad())
                + "% del arreglo y la cola ya no acepta nada más.");

        System.out.println("\n--- Paso 8: enqueue(60) sobre esa cola 'llena' ---");
        try {
            cola.enqueue(60);
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("Excepción capturada: " + e.getMessage());
        }
        System.out.println("El 60 cabría perfectamente en la posición 0 o en la 1, que están");
        System.out.println("vacías. La cola simple no tiene ninguna forma de llegar ahí.");
        cola.imprimirEstado();

        // ------------------------------------------------------------------
        // El peor caso: vaciar la cola la deja inservible por completo.
        // ------------------------------------------------------------------
        System.out.println("\n--- Paso 9: el PEOR CASO (vaciar la cola por completo) ---");
        while (!cola.isEmpty()) {
            System.out.println("dequeue() -> " + cola.dequeue());
        }
        cola.imprimirEstado();
        System.out.println("isEmpty() = " + cola.isEmpty() + " e isFull() = " + cola.isFull()
                + " AL MISMO TIEMPO.");
        System.out.println("Las " + cola.capacidad() + " posiciones están libres y las "
                + cola.capacidad() + " son inalcanzables:");
        System.out.println("el desperdicio del peor caso es el arreglo COMPLETO (n de n).");
        try {
            cola.enqueue(70);
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("enqueue(70) -> Excepción capturada: " + e.getMessage());
        }

        // ------------------------------------------------------------------
        // Cierre: por qué no es un bug y cuáles son las dos salidas.
        // ------------------------------------------------------------------
        System.out.println("\n============================================================");
        System.out.println("POR QUÉ ESTO NO ES UN BUG, Y CÓMO SE ARREGLA");
        System.out.println("============================================================");
        System.out.println("isFull() pregunta '¿le queda lugar a rear hacia adelante?' y");
        System.out.println("responde la verdad. El problema es la DEFINICIÓN de 'lleno', no el");
        System.out.println("código: debería significar 'no queda ninguna posición libre'.");
        System.out.println();
        System.out.println("Solución 1 (mala): desplazar todos los elementos a la izquierda en");
        System.out.println("cada dequeue para que front vuelva a 0. Elimina el desperdicio pero");
        System.out.println("copia hasta n-1 elementos por extracción: dequeue pasa de O(1) a O(n).");
        System.out.println();
        System.out.println("Solución 2 (buena): COLA CIRCULAR con (indice + 1) % capacidad, que");
        System.out.println("hace que rear vuelva a 0 después del último índice y reutilice las");
        System.out.println("posiciones del inicio, manteniendo TODO en O(1). Es el ejercicio 10,");
        System.out.println("que ejecuta esta misma secuencia y SÍ acepta el enqueue(60).");

        System.out.println("\n=== Fin de la demostración ===");
    }
}

/**
 * Cola simple de enteros (NO circular) instrumentada para exponer el desperdicio de
 * posiciones.
 *
 * <p>La lógica es idéntica a la {@code ColaEnteros} del ejercicio 6: {@code front} es el
 * índice del primer elemento vivo (inicial 0), {@code rear} el índice del último elemento
 * ocupado (inicial -1), y los dos <b>solo avanzan</b>. Lo único que se agrega es
 * instrumentación de diagnóstico: {@link #imprimirEstado()} clasifica cada posición del
 * arreglo en tres estados y {@link #posicionesDesperdiciadas()} cuantifica la pérdida.</p>
 *
 * <p><b>Los tres estados de una posición.</b> Esta distinción es el centro del ejercicio,
 * porque muestra que "libre" no es una sola cosa:</p>
 * <ul>
 *   <li><b>LIBRE INUTILIZABLE</b> (índice &lt; front): quedó libre al retirar un elemento.
 *       Está vacía, pero ningún enqueue podrá volver a usarla, porque el único índice que
 *       escribe es rear y rear ya pasó.</li>
 *   <li><b>OCUPADA</b> (front &lt;= índice &lt;= rear): contiene un elemento vivo de la
 *       cola.</li>
 *   <li><b>LIBRE DISPONIBLE</b> (índice &gt; rear): todavía no se usó nunca; es la única
 *       clase de posición libre en la que el próximo enqueue puede escribir.</li>
 * </ul>
 *
 * <p><b>Invariantes:</b> (I1) {@code 0 <= front <= capacidad}; (I2)
 * {@code -1 <= rear <= capacidad - 1}; (I3) los elementos vivos son los de
 * {@code front..rear}; (I4) la cantidad de posiciones desperdiciadas es exactamente
 * {@code front}, o sea una por cada dequeue realizado.</p>
 *
 * <p>Complejidad: todas las operaciones de la cola son O(1);
 * {@link #imprimirEstado()} es O(capacidad).</p>
 */
class ColaSimpleInstrumentada {

    /** Arreglo interno de tamaño fijo. */
    private final int[] arreglo;

    /** Índice del primer elemento vivo. Avanza con dequeue; NUNCA retrocede. */
    private int front;

    /** Índice del último elemento ocupado. Avanza con enqueue; NUNCA retrocede. */
    private int rear;

    /**
     * Crea una cola simple vacía con la capacidad indicada.
     *
     * <p>Complejidad: O(1) temporal, O(capacidad) espacial.</p>
     *
     * @param capacidad cantidad de posiciones del arreglo interno
     * @throws IllegalArgumentException si la capacidad es negativa
     */
    public ColaSimpleInstrumentada(int capacidad) {
        if (capacidad < 0) {
            throw new IllegalArgumentException("La capacidad no puede ser negativa: " + capacidad);
        }
        this.arreglo = new int[capacidad];
        this.front = 0;
        this.rear = -1;
    }

    /**
     * Inserta un elemento al final de la cola.
     *
     * <p>Escribe en {@code rear + 1}. Nunca busca una posición libre al inicio: una cola
     * simple no tiene forma de expresar "volver al principio".</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param valor entero a insertar
     * @throws IllegalStateException si {@link #isFull()} es true, incluso habiendo posiciones
     *         libres al inicio del arreglo
     */
    public void enqueue(int valor) {
        if (isFull()) {
            throw new IllegalStateException("La cola está LLENA según rear (rear = " + rear
                    + ", último índice " + (arreglo.length - 1) + "), aunque hay "
                    + posicionesDesperdiciadas() + " posición(es) libre(s) al inicio.");
        }
        rear++;
        arreglo[rear] = valor;
    }

    /**
     * Retira y devuelve el primer elemento de la cola.
     *
     * <p>Avanza front en lugar de desplazar los elementos, que es lo que mantiene la
     * operación en O(1) <b>y a la vez</b> lo que produce el desperdicio: la posición que
     * acaba de liberar queda del lado inalcanzable de front.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return el entero que estaba al frente
     * @throws IllegalStateException si la cola está vacía
     */
    public int dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Subdesbordamiento: la cola está vacía.");
        }
        int valor = arreglo[front];
        front++; // acá nace el desperdicio: la posición front-1 queda libre e inalcanzable
        return valor;
    }

    /**
     * Devuelve el primer elemento sin retirarlo.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return el entero que está al frente
     * @throws IllegalStateException si la cola está vacía
     */
    public int front() {
        if (isEmpty()) {
            throw new IllegalStateException("La cola está vacía: no hay frente para consultar.");
        }
        return arreglo[front];
    }

    /**
     * Indica si la cola no contiene elementos.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return true si front pasó de largo a rear
     */
    public boolean isEmpty() {
        return rear < front;
    }

    /**
     * Indica si la cola no admite más inserciones.
     *
     * <p><b>ESTA ES LA LÍNEA QUE GENERA EL FALSO POSITIVO.</b> La condición mira únicamente a
     * rear: "¿llegó al último índice?". Nunca pregunta cuántas posiciones libres hay en el
     * arreglo. Como front pudo haber avanzado, la respuesta "llena" puede ser verdadera
     * teniendo hasta {@code capacidad - 1} posiciones vacías al principio. La condición es
     * correcta respecto de lo que mide; lo que está mal es creer que mide "no queda lugar".</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return true si rear llegó al último índice del arreglo
     */
    public boolean isFull() {
        return rear == arreglo.length - 1;
    }

    /**
     * Devuelve la cantidad de elementos vivos.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return cantidad de elementos entre front y rear; 0 si está vacía
     */
    public int size() {
        if (isEmpty()) {
            return 0;
        }
        return rear - front + 1;
    }

    /**
     * Devuelve la capacidad fija del arreglo interno.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return capacidad del arreglo
     */
    public int capacidad() {
        return arreglo.length;
    }

    /**
     * Devuelve cuántas posiciones quedaron LIBRES pero INUTILIZABLES.
     *
     * <p>Son las posiciones {@code 0..front-1}: exactamente {@code front} posiciones, una por
     * cada dequeue. Este número es la medida del defecto, y es el N del mensaje final del
     * programa.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return cantidad de posiciones desperdiciadas al inicio del arreglo
     */
    public int posicionesDesperdiciadas() {
        return Math.min(front, arreglo.length);
    }

    /**
     * Imprime el estado interno completo con la clasificación de cada posición.
     *
     * <p>Es el método que materializa la demostración: muestra el arreglo con sus índices,
     * los valores de front y rear, el resultado de {@code isFull()} y, posición por posición,
     * si está LIBRE INUTILIZABLE, OCUPADA o LIBRE DISPONIBLE.</p>
     *
     * <p>Complejidad: O(capacidad) temporal, O(1) espacial. Método de diagnóstico.</p>
     */
    public void imprimirEstado() {
        System.out.print("  arreglo:  ");
        for (int i = 0; i < arreglo.length; i++) {
            // El valor se alinea a 2 caracteres para que la fila de punteros de abajo quede
            // exactamente debajo de su posición.
            System.out.printf("[%d]=%2d  ", i, arreglo[i]);
        }
        System.out.println();
        System.out.print("  punteros: ");
        for (int i = 0; i < arreglo.length; i++) {
            // Cada celda ocupa 8 caracteres, igual que "[i]=vv  ", y la marca se ubica
            // debajo del valor.
            String puntero = "        ";
            if (!isEmpty() && i == front && i == rear) {
                puntero = "   F/R  ";
            } else if (!isEmpty() && i == front) {
                puntero = "    F   ";
            } else if (!isEmpty() && i == rear) {
                puntero = "    R   ";
            }
            System.out.print(puntero);
        }
        System.out.println();
        System.out.println("  front = " + front + " | rear = " + rear + " | size = " + size()
                + " | isEmpty() = " + isEmpty() + " | isFull() = " + isFull());
        System.out.println("  clasificación de cada posición:");
        for (int i = 0; i < arreglo.length; i++) {
            System.out.println("    [" + i + "] = " + arreglo[i] + "   -> " + clasificar(i));
        }
        System.out.println("  posiciones LIBRE INUTILIZABLE: " + posicionesDesperdiciadas()
                + " | elementos vivos: " + size() + " | capacidad: " + arreglo.length);
    }

    /**
     * Clasifica una posición del arreglo en uno de los tres estados posibles.
     *
     * <p>La clasificación se deduce solo de comparar el índice con front y con rear, que es
     * justamente lo que hace evidente el defecto: la frontera entre "inutilizable" y
     * "disponible" la fijan los índices, no el contenido.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param i índice del arreglo interno
     * @return "LIBRE INUTILIZABLE", "OCUPADA" o "LIBRE DISPONIBLE", con una aclaración
     */
    private String clasificar(int i) {
        if (i < front) {
            return "LIBRE INUTILIZABLE (antes de front: ningún enqueue puede volver acá)";
        }
        if (!isEmpty() && i <= rear) {
            return "OCUPADA (elemento vivo, entre front y rear)";
        }
        return "LIBRE DISPONIBLE (después de rear: el próximo enqueue puede usarla)";
    }
}

/*
 * ============================================================================
 * MATRIZ DE CASOS DE PRUEBA MANUALES
 * ============================================================================
 * Compilación: javac Ejercicio09_DesperdicioEspacioCola.java -d /tmp/v4
 * Ejecución:   java -cp /tmp/v4 Ejercicio09_DesperdicioEspacioCola
 * Todos los casos están cableados en main(); el programa no lee entrada por consola.
 * La SALIDA del programa es la demostración: cada caso se verifica leyendo la consola.
 *
 * ID    | Objetivo                          | Entrada exacta                              | Resultado esperado                                                      | Criterio de aprobación
 * ------+-----------------------------------+---------------------------------------------+-------------------------------------------------------------------------+-----------------------------------------------------------
 * CP-01 | Flujo nominal de la secuencia que | new ColaSimpleInstrumentada(5); enqueue(10),| los dequeue devuelven 10 y 20; después del paso 7 queda front = 2,      | "dequeue() -> 10" y "dequeue() -> 20"; el estado del paso
 *       | expone el defecto                 | enqueue(20), enqueue(30), dequeue(),        | rear = 4, size = 3 y las posiciones 0 y 1 clasificadas LIBRE            | 7 muestra front = 2, rear = 4, size = 3
 *       |                                   | dequeue(), enqueue(40), enqueue(50)         | INUTILIZABLE                                                            |
 * CP-02 | Estructura vacía: clasificación   | cola recién creada (paso 0)                 | front = 0, rear = -1, size = 0, isEmpty = true, isFull = false y las 5  | El paso 0 clasifica las 5 posiciones como LIBRE
 *       | inicial                           |                                             | posiciones clasificadas LIBRE DISPONIBLE                                | DISPONIBLE y muestra rear = -1
 * CP-03 | El falso positivo de isFull:      | el estado alcanzado tras el paso 7          | isFull() = true con size = 3 sobre capacidad 5, y el mensaje            | Aparece ">>> La cola reporta LLENA pero hay 2 posiciones
 *       | LLENA con posiciones libres       |                                             | ">>> La cola reporta LLENA pero hay 2 posiciones libres al inicio"      | libres al inicio" junto con "isFull()  = true" y
 *       |                                   |                                             | y "Se está usando el 60% del arreglo"                                   | "size()    = 3 elementos"
 * CP-04 | Error forzado: enqueue sobre la   | enqueue(60) sobre el estado del paso 7      | IllegalStateException "La cola está LLENA según rear (rear = 4, último  | Se imprime el mensaje de la excepción y el estado posterior
 *       | cola "llena" con huecos libres    |                                             | índice 4), aunque hay 2 posición(es) libre(s) al inicio."               | sigue mostrando front = 2, rear = 4, size = 3
 * CP-05 | Peor caso: el arreglo entero      | vaciar la cola con dequeue sucesivos y      | las 5 posiciones quedan LIBRE INUTILIZABLE con isEmpty = true e        | "isEmpty() = true e isFull() = true AL MISMO TIEMPO." y la
 *       | queda inutilizable                | luego enqueue(70)                           | isFull = true a la vez; el enqueue(70) lanza IllegalStateException con  | excepción de enqueue(70) informando 5 posiciones libres
 *       |                                   |                                             | 5 posiciones libres al inicio                                           | al inicio; el programa llega al "=== Fin de la demostración ==="
 * ============================================================================
 */
