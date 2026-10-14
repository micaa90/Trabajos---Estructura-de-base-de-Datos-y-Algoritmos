/*
 * ============================================================================
 * Ejercicio 7 - Sistema de turnos para atención
 * Práctico 4: Pilas y Colas con Arreglos en Java
 * ============================================================================
 *
 * Prompt inicial utilizado:
 * """
 * Analizá este problema: un sistema de turnos para una oficina. Cada persona que llega recibe
 * un turno y se agrega a una cola. El sistema debe permitir agregar persona, atender a la
 * siguiente persona y consultar quién está primero.
 * El punto central: explicá por qué este problema utiliza FIFO y NO LIFO. Desarrollá:
 * - qué principio de equidad implica un sistema de turnos: el que llegó primero es atendido
 *   primero;
 * - qué pasaría si se usara una pila: mostrá concretamente que la última persona en llegar
 *   sería atendida primero, y que una persona que llegó temprano podría no ser atendida nunca
 *   si siguen llegando personas nuevas (inanición). Desarrollá este contraejemplo, es lo que
 *   evalúa el ejercicio;
 * - qué operación de cola corresponde a cada acción del enunciado (llegar = enqueue,
 *   atender = dequeue, consultar quién sigue = front);
 * - cómo se asigna el número de turno y por qué ese número es independiente de la posición
 *   en el arreglo interno.
 * Definí además: la estructura de datos para representar una persona (nombre y número de
 * turno), el manejo del arreglo de tamaño fijo, y los casos límite: atender con la cola vacía,
 * consultar con la cola vacía, oficina al límite de capacidad de espera, atender a todos y
 * seguir llegando gente.
 * Indicá la complejidad de cada operación.
 * No escribas la implementación.
 * """
 *
 * Ajustes realizados:
 * Sí hubo que ajustar el prompt. El análisis dejaba abierto DÓNDE vive el contador de turnos;
 * se ajustó el prompt del desarrollador para que el número lo asigne la propia clase Persona
 * con un contador estático, y la consecuencia quedó documentada en el código: el contador es
 * compartido por todas las instancias, así que si hubiera DOS oficinas los turnos se
 * mezclarían. Para una sola ventanilla, que es el alcance del enunciado, es correcto; con
 * varias oficinas el contador tendría que vivir en SistemaDeTurnos.
 * Segundo ajuste: hubo que pedir explícitamente que imprimirFila() muestre el orden de
 * atención (de front hacia rear) y que imprimirEstado() muestre aparte el arreglo interno con
 * los índices, porque con una sola impresión "linda" de la fila no se ve que las posiciones
 * que libera cada atención quedan inutilizables.
 * Tercer ajuste: se agregó al prompt que el nombre nulo o en blanco se rechace con excepción;
 * el análisis no lo había previsto porque el enunciado no habla de validar el nombre.
 *
 * ¿POR QUÉ FIFO Y NO LIFO? Un sistema de turnos existe para garantizar EQUIDAD: el que llegó
 * primero es atendido primero. Esa regla es literalmente la definición de FIFO (First In,
 * First Out), así que la estructura correcta es una cola.
 *   Contraejemplo con una PILA (LIFO): si la fila fuera una pila, atender haría pop y saldría
 * la ÚLTIMA persona que llegó. Con la fila Ana (turno 1), Bruno (2), Carla (3), Diego (4), la
 * cola atiende Ana, Bruno, Carla, Diego y la pila atendería Diego, Carla, Bruno, Ana: el
 * orden exactamente invertido, premiando al que llegó último.
 *   Y el problema es peor que una simple inversión. En una oficina la gente sigue llegando
 * MIENTRAS se atiende. Con una pila, cada persona nueva se apila ARRIBA de Ana y pasa delante
 * de ella. Si el flujo de llegadas no se corta, Ana puede esperar indefinidamente sin ser
 * atendida nunca: eso se llama INANICIÓN (starvation). Con FIFO la inanición es imposible,
 * porque las llegadas nuevas se agregan DETRÁS y la posición de Ana solo puede mejorar: su
 * espera está acotada por la cantidad de personas que había delante cuando llegó.
 *   Correspondencia entre dominio y estructura: llegar = enqueue, atender = dequeue,
 * consultar quién sigue = front.
 *
 * Estrategia: cola FIFO de objetos Persona sobre un arreglo de tamaño fijo, con front = índice
 * del próximo a atender y rear = índice del último que llegó, y el número de turno asignado
 * por un contador incremental independiente de los índices del arreglo.
 *
 * Complejidad de las operaciones principales:
 *   agregarPersona, atenderSiguiente, consultarPrimero, cantidadEnEspera, filaVacia,
 *   salaLlena -> O(1) temporal, O(1) espacial.
 *   imprimirFila / imprimirEstado -> O(n) y O(capacidad) (diagnóstico).
 *   Espacial: O(n) con n = capacidad de la sala de espera.
 * ============================================================================
 */

/**
 * Programa de demostración del ejercicio 7: sistema de turnos de una oficina resuelto con una
 * cola FIFO sobre arreglo de tamaño fijo.
 *
 * <p>Simula una jornada completa: llegan 4 personas, se consulta quién sigue, se atienden 2,
 * llegan 2 más, se imprime la fila, la sala se declara llena, se atiende a todos y se intenta
 * atender con la fila vacía.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio07_SistemaDeTurnos {

    /**
     * Método principal: simula la jornada de atención y fuerza todos los casos límite.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 7 - Sistema de turnos para atención (COLA FIFO) ===");
        System.out.println("Capacidad de la sala de espera: 6 personas");

        SistemaDeTurnos oficina = new SistemaDeTurnos(6);
        oficina.imprimirFila();

        // --- Llegan las primeras 4 personas (4 enqueue) ---
        System.out.println("\n--- 1) Llegan 4 personas (llegar = enqueue) ---");
        System.out.println("Llega Ana Torres        -> " + oficina.agregarPersona("Ana Torres"));
        System.out.println("Llega Bruno Díaz        -> " + oficina.agregarPersona("Bruno Díaz"));
        System.out.println("Llega Carla Núñez       -> " + oficina.agregarPersona("Carla Núñez"));
        System.out.println("Llega Diego Ramírez     -> " + oficina.agregarPersona("Diego Ramírez"));
        oficina.imprimirFila();
        oficina.imprimirEstado();

        // --- Consultar quién sigue (front): no modifica la fila ---
        System.out.println("\n--- 2) Consultar quién está primero (consultar = front) ---");
        System.out.println("consultarPrimero() -> " + oficina.consultarPrimero());
        System.out.println("La fila NO cambió: consultar no es atender.");
        oficina.imprimirFila();

        // --- Se atienden 2 personas (2 dequeue) ---
        System.out.println("\n--- 3) Se atienden 2 personas (atender = dequeue) ---");
        System.out.println("atenderSiguiente() -> ATENDIDO: " + oficina.atenderSiguiente());
        oficina.imprimirFila();
        System.out.println("atenderSiguiente() -> ATENDIDO: " + oficina.atenderSiguiente());
        oficina.imprimirFila();
        oficina.imprimirEstado();
        System.out.println("Orden de atención = orden de llegada: turno 1 y después turno 2.");

        // --- Llegan 2 personas más: se agregan ATRÁS, no adelante ---
        System.out.println("\n--- 4) Llegan 2 personas más mientras se atiende ---");
        System.out.println("Llega Elena Sosa        -> " + oficina.agregarPersona("Elena Sosa"));
        System.out.println("Llega Facundo Pérez     -> " + oficina.agregarPersona("Facundo Pérez"));
        System.out.println("Los que llegan ahora se ponen DETRÁS de Carla (turno 3).");
        System.out.println("Con una pila se pondrían DELANTE y Carla no se atendería nunca:");
        System.out.println("ese es el riesgo de inanición que FIFO elimina.");
        oficina.imprimirFila();
        oficina.imprimirEstado();

        // --- Caso límite: sala de espera al límite de capacidad ---
        System.out.println("\n--- 5) Caso límite: sala de espera LLENA ---");
        System.out.println("salaLlena() -> " + oficina.salaLlena()
                + " | cantidadEnEspera() = " + oficina.cantidadEnEspera()
                + " sobre capacidad " + oficina.capacidad());
        try {
            System.out.println("Llega Gabriela Luna");
            oficina.agregarPersona("Gabriela Luna");
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("Excepción capturada: " + e.getMessage());
        }
        System.out.println("Observar: solo hay " + oficina.cantidadEnEspera()
                + " personas esperando y la sala ya se declara llena, porque las "
                + oficina.posicionesDesperdiciadas() + " posiciones");
        System.out.println("que liberaron las personas ya atendidas quedaron inutilizables.");
        System.out.println("Es el defecto que demuestra el ejercicio 9 y que corrige el 10.");

        // --- Caso límite: nombre inválido ---
        System.out.println("\n--- 6) Caso límite: nombre inválido ---");
        try {
            oficina.agregarPersona("   ");
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalArgumentException e) {
            System.out.println("agregarPersona(\"   \") -> Excepción capturada: " + e.getMessage());
        }
        try {
            oficina.agregarPersona(null);
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalArgumentException e) {
            System.out.println("agregarPersona(null)  -> Excepción capturada: " + e.getMessage());
        }

        // --- Se atiende a todos ---
        System.out.println("\n--- 7) Se atiende a TODOS los que esperan ---");
        while (!oficina.filaVacia()) {
            System.out.println("atenderSiguiente() -> ATENDIDO: " + oficina.atenderSiguiente()
                    + " | quedan en espera: " + oficina.cantidadEnEspera());
        }
        oficina.imprimirFila();
        oficina.imprimirEstado();

        // --- Caso límite: atender y consultar con la fila vacía ---
        System.out.println("\n--- 8) Caso límite: fila VACÍA ---");
        try {
            oficina.atenderSiguiente();
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("atenderSiguiente()  -> Excepción capturada: " + e.getMessage());
        }
        try {
            oficina.consultarPrimero();
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("consultarPrimero()  -> Excepción capturada: " + e.getMessage());
        }

        // --- Caso límite: sigue llegando gente después de atender a todos ---
        System.out.println("\n--- 9) Caso límite: llega gente después de atender a todos ---");
        try {
            oficina.agregarPersona("Hugo Vera");
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("agregarPersona(\"Hugo Vera\") -> Excepción capturada: "
                    + e.getMessage());
        }
        System.out.println("La sala quedó vacía pero inservible: rear está en el último índice");
        System.out.println("y no puede retroceder. Con una cola circular (ejercicio 10) Hugo");
        System.out.println("entraría en la posición 0, que ya está libre.");

        System.out.println("\n--- 10) Resumen de la jornada ---");
        System.out.println("Turnos asignados en total: " + Persona.turnosAsignados());
        System.out.println("El número de turno es un contador propio del sistema: creció 1,2,3...");
        System.out.println("sin ninguna relación con el índice del arreglo donde quedó cada");
        System.out.println("persona. Ana tuvo el turno 1 y ocupó el índice 0, pero Elena tuvo el");
        System.out.println("turno 5 y ocupó el índice 4: el turno identifica a la persona, el");
        System.out.println("índice es un detalle interno del almacenamiento.");

        System.out.println("\n=== Fin de la demostración ===");
    }
}

/**
 * Persona en espera de atención: su nombre y el número de turno que recibió al llegar.
 *
 * <p><b>Cómo se asigna el número de turno.</b> La clase lleva un contador estático
 * {@code ultimoTurnoAsignado} que se incrementa en cada construcción. Es automático: quien
 * crea la persona no elige el número, lo recibe. El contador <b>nunca se reinicia ni
 * decrementa</b>, así que dos personas distintas nunca comparten turno, incluso después de
 * haber vaciado la fila.</p>
 *
 * <p><b>Por qué el turno es independiente del índice del arreglo.</b> El índice es dónde está
 * guardada la persona; el turno es su identidad dentro de la jornada. Son dos cosas
 * distintas: la persona del turno 5 puede estar en el índice 4, y después de varios dequeue
 * el índice 0 queda libre mientras el turno 1 ya fue atendido y no vuelve. Si el turno se
 * dedujera del índice, reutilizar posiciones (como hace la cola circular del ejercicio 10)
 * rompería la numeración.</p>
 *
 * <p><i>Limitación del contador estático:</i> es compartido por TODAS las instancias, así que
 * con dos oficinas simultáneas los turnos se mezclarían. Para una sola ventanilla, que es el
 * alcance del enunciado, es la solución más simple; con varias el contador debería vivir en
 * {@code SistemaDeTurnos}.</p>
 */
class Persona {

    /** Contador compartido: cuántos turnos se entregaron desde que arrancó el programa. */
    private static int ultimoTurnoAsignado = 0;

    /** Nombre de la persona. */
    private final String nombre;

    /** Número de turno recibido al llegar; inmutable una vez asignado. */
    private final int numeroTurno;

    /**
     * Crea una persona y le asigna automáticamente el siguiente número de turno.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param nombre nombre de la persona; no puede ser null ni estar en blanco
     * @throws IllegalArgumentException si el nombre es null o está en blanco
     */
    public Persona(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la persona no puede estar vacío.");
        }
        this.nombre = nombre.trim();
        ultimoTurnoAsignado++;                  // el contador avanza SIEMPRE, nunca retrocede
        this.numeroTurno = ultimoTurnoAsignado; // el turno queda fijo para esta persona
    }

    /**
     * Devuelve el nombre de la persona.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return nombre ya normalizado (sin espacios al principio ni al final)
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el número de turno asignado al llegar.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return número de turno
     */
    public int getNumeroTurno() {
        return numeroTurno;
    }

    /**
     * Devuelve cuántos turnos se asignaron en total desde el inicio del programa.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return valor actual del contador compartido de turnos
     */
    public static int turnosAsignados() {
        return ultimoTurnoAsignado;
    }

    /**
     * Representación textual de la persona.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return cadena con el número de turno y el nombre
     */
    @Override
    public String toString() {
        return "turno " + numeroTurno + " - " + nombre;
    }
}

/**
 * Sistema de turnos de una oficina: una cola FIFO de {@link Persona} sobre un arreglo de
 * tamaño fijo.
 *
 * <p><b>Traducción del dominio a operaciones de cola.</b> Cada método del dominio es una
 * operación de cola con otro nombre:</p>
 * <ul>
 *   <li>{@code agregarPersona} = <b>enqueue</b>: la persona se pone al final de la fila.</li>
 *   <li>{@code atenderSiguiente} = <b>dequeue</b>: sale la primera de la fila.</li>
 *   <li>{@code consultarPrimero} = <b>front</b>: se mira quién sigue sin atenderlo.</li>
 *   <li>{@code cantidadEnEspera} = <b>size</b>; {@code filaVacia} = <b>isEmpty</b>;
 *       {@code salaLlena} = <b>isFull</b>.</li>
 * </ul>
 *
 * <p><b>Por qué FIFO y no LIFO.</b> Ver la cabecera del archivo: FIFO garantiza la equidad
 * ("el que llegó primero es atendido primero") y hace imposible la inanición, porque las
 * llegadas nuevas se encolan detrás y nunca pueden adelantar a alguien que ya esperaba. Con
 * una pila, cada persona que llega se apila arriba y pasa delante de las anteriores, por lo
 * que la primera en llegar podría no ser atendida nunca mientras siga entrando gente.</p>
 *
 * <p><b>Índices.</b> {@code front} es el índice de la próxima persona a atender (inicial 0) y
 * {@code rear} el índice de la última que llegó (inicial -1). Los dos solo avanzan: atender
 * mueve front, llegar mueve rear. Esa es la causa de que las posiciones que libera cada
 * atención queden inutilizables, el defecto que el ejercicio 9 demuestra.</p>
 *
 * <p><b>Invariantes:</b> (I1) las personas en espera son exactamente las de las posiciones
 * {@code front..rear}; (I2) sus números de turno son estrictamente crecientes en ese rango,
 * porque el contador de turnos solo crece y el orden del arreglo es el orden de llegada;
 * (I3) {@code front <= rear + 1}.</p>
 *
 * <p>Complejidad: todas las operaciones del dominio son O(1).</p>
 */
class SistemaDeTurnos {

    /** Sala de espera: arreglo de tamaño fijo con las personas en orden de llegada. */
    private final Persona[] sala;

    /** Índice de la próxima persona a atender. Avanza SOLO al atender. */
    private int front;

    /** Índice de la última persona que llegó. Avanza SOLO al agregar. */
    private int rear;

    /**
     * Crea un sistema de turnos con la capacidad de espera indicada.
     *
     * <p>Complejidad: O(1) temporal, O(capacidad) espacial.</p>
     *
     * @param capacidadSala cantidad máxima de personas que caben esperando
     * @throws IllegalArgumentException si la capacidad es negativa
     */
    public SistemaDeTurnos(int capacidadSala) {
        if (capacidadSala < 0) {
            throw new IllegalArgumentException(
                    "La capacidad de la sala no puede ser negativa: " + capacidadSala);
        }
        this.sala = new Persona[capacidadSala];
        this.front = 0;
        this.rear = -1;
    }

    /**
     * Registra la llegada de una persona: le asigna un turno y la pone al final de la fila.
     *
     * <p>Equivale a un <b>enqueue</b>. rear avanza una posición; front no se toca, porque la
     * llegada de alguien nuevo no cambia a quién le toca.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param nombre nombre de la persona que llega
     * @return la persona creada, ya con su número de turno asignado
     * @throws IllegalArgumentException si el nombre es null o está en blanco
     * @throws IllegalStateException si la sala de espera está llena
     */
    public Persona agregarPersona(String nombre) {
        // Se valida el nombre ANTES de mirar la capacidad para no consumir un número de turno
        // en una llegada inválida: el contador de Persona no se puede deshacer.
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la persona no puede estar vacío.");
        }
        if (salaLlena()) {
            throw new IllegalStateException("La sala de espera está llena (capacidad "
                    + sala.length + "): " + nombre.trim() + " no puede entrar.");
        }
        Persona persona = new Persona(nombre); // acá se asigna el número de turno
        rear++;
        sala[rear] = persona;
        return persona;
    }

    /**
     * Atiende a la primera persona de la fila y la retira del sistema.
     *
     * <p>Equivale a un <b>dequeue</b>: se lee {@code sala[front]} y front avanza. No se
     * desplaza a nadie, así que la operación es O(1); el precio es que la posición liberada
     * queda inutilizable.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return la persona atendida, con su nombre y número de turno
     * @throws IllegalStateException si no hay nadie esperando
     */
    public Persona atenderSiguiente() {
        if (filaVacia()) {
            throw new IllegalStateException("No hay nadie en la fila: no se puede atender.");
        }
        Persona atendida = sala[front];
        sala[front] = null; // libera la referencia: la persona ya no pertenece al sistema
        front++;            // le toca al siguiente
        return atendida;
    }

    /**
     * Consulta quién está primero en la fila sin atenderlo.
     *
     * <p>Equivale a un <b>front</b>. No modifica ningún índice: consultar no es atender.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return la persona que sigue
     * @throws IllegalStateException si no hay nadie esperando
     */
    public Persona consultarPrimero() {
        if (filaVacia()) {
            throw new IllegalStateException("No hay nadie en la fila: no hay a quién consultar.");
        }
        return sala[front];
    }

    /**
     * Devuelve cuántas personas están esperando.
     *
     * <p>Equivale a <b>size</b>. Es un dato derivado de los índices, no un atributo.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return cantidad de personas en espera
     */
    public int cantidadEnEspera() {
        if (filaVacia()) {
            return 0;
        }
        return rear - front + 1;
    }

    /**
     * Indica si no hay nadie esperando.
     *
     * <p>Equivale a <b>isEmpty</b>: front "pasó de largo" a rear.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return true si la fila está vacía
     */
    public boolean filaVacia() {
        return rear < front;
    }

    /**
     * Indica si la sala de espera no admite más gente.
     *
     * <p>Equivale a <b>isFull</b>. Igual que en el ejercicio 6, "llena" significa "rear llegó
     * al último índice", no "no queda ninguna posición libre": las posiciones que liberaron
     * las personas ya atendidas no se pueden reutilizar.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return true si no entra nadie más
     */
    public boolean salaLlena() {
        return rear == sala.length - 1;
    }

    /**
     * Devuelve la capacidad fija de la sala de espera.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return capacidad del arreglo interno
     */
    public int capacidad() {
        return sala.length;
    }

    /**
     * Devuelve cuántas posiciones del arreglo quedaron libres pero inalcanzables.
     *
     * <p>Son las posiciones {@code 0..front-1}: una por cada persona ya atendida.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return cantidad de posiciones desperdiciadas al inicio
     */
    public int posicionesDesperdiciadas() {
        return Math.min(front, sala.length);
    }

    /**
     * Imprime la fila en el orden en que va a ser atendida (vista del usuario).
     *
     * <p>Recorre de front a rear, que es el orden de llegada y por lo tanto el orden de
     * atención.</p>
     *
     * <p>Complejidad: O(n) con n = cantidad de personas en espera. Método de diagnóstico.</p>
     */
    public void imprimirFila() {
        System.out.print("  FILA (próximo a atender -> último en llegar): ");
        if (filaVacia()) {
            System.out.println("(nadie esperando)");
            return;
        }
        for (int i = front; i <= rear; i++) {
            System.out.print("[" + sala[i] + "]" + (i < rear ? " <- " : ""));
        }
        System.out.println();
        System.out.println("  en espera: " + cantidadEnEspera() + "/" + capacidad()
                + " | próximo: " + sala[front].getNombre()
                + " (turno " + sala[front].getNumeroTurno() + ")");
    }

    /**
     * Imprime el estado INTERNO: el arreglo con sus índices y los valores de front y rear.
     *
     * <p>Hace visible lo que la vista de la fila esconde: las posiciones anteriores a front
     * están en null (libres) pero no se pueden reutilizar.</p>
     *
     * <p>Complejidad: O(capacidad) temporal. Método de diagnóstico.</p>
     */
    public void imprimirEstado() {
        System.out.print("  arreglo interno: ");
        if (sala.length == 0) {
            System.out.print("[] (capacidad 0)");
        }
        for (int i = 0; i < sala.length; i++) {
            String contenido = (sala[i] == null) ? "null" : ("T" + sala[i].getNumeroTurno());
            System.out.print("[" + i + "]=" + contenido + marcaDe(i) + "  ");
        }
        System.out.println();
        System.out.println("  front = " + front + " | rear = " + rear + " | enEspera = "
                + cantidadEnEspera() + " | filaVacia = " + filaVacia()
                + " | salaLlena = " + salaLlena());
        System.out.println("  marcas: F = front, R = rear, Tn = turno n, <F = libre"
                + " INUTILIZABLE (ya atendida), * = libre disponible");
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
        if (!filaVacia() && i == front && i == rear) {
            return "(FR)";
        }
        if (!filaVacia() && i == front) {
            return "(F)";
        }
        if (!filaVacia() && i == rear) {
            return "(R)";
        }
        if (!filaVacia() && i > front && i < rear) {
            return "";
        }
        if (i < front) {
            return "<F";
        }
        return "*";
    }
}

/*
 * ============================================================================
 * MATRIZ DE CASOS DE PRUEBA MANUALES
 * ============================================================================
 * Compilación: javac Ejercicio07_SistemaDeTurnos.java -d /tmp/v4
 * Ejecución:   java -cp /tmp/v4 Ejercicio07_SistemaDeTurnos
 * Todos los casos están cableados en main(); el programa no lee entrada por consola.
 *
 * ID    | Objetivo                          | Entrada exacta                              | Resultado esperado                                                      | Criterio de aprobación
 * ------+-----------------------------------+---------------------------------------------+-------------------------------------------------------------------------+-----------------------------------------------------------
 * CP-01 | Flujo nominal: el orden de        | new SistemaDeTurnos(6); agregarPersona de   | consultarPrimero() devuelve "turno 1 - Ana Torres"; las dos atenciones  | Se imprime "ATENDIDO: turno 1 - Ana Torres" y después
 *       | atención es el orden de llegada   | Ana, Bruno, Carla y Diego;                  | devuelven el turno 1 y el turno 2, en ese orden, y la fila no cambia    | "ATENDIDO: turno 2 - Bruno Díaz", en ese orden
 *       |                                   | consultarPrimero(); atenderSiguiente() x2   | al consultar                                                            |
 * CP-02 | Estructura vacía: fila sin gente  | sistema recién construido y, más adelante,  | "(nadie esperando)", front = 0, rear = -1, enEspera = 0,                | La primera imprimirFila() muestra "(nadie esperando)" y
 *       |                                   | después de atender a todos                  | filaVacia = true                                                        | el estado final muestra enEspera = 0
 * CP-03 | Estructura llena: sala al límite  | tras atender 2, agregarPersona de Elena y   | salaLlena = true con solo 4 personas en espera sobre capacidad 6:       | "salaLlena() -> true" junto con "cantidadEnEspera() = 4
 *       | con posiciones desperdiciadas     | Facundo (rear llega al índice 5)            | las 2 posiciones de los ya atendidos quedaron inutilizables             | sobre capacidad 6" y el mensaje de las 2 posiciones
 * CP-04 | Error forzado: llega gente con la | agregarPersona("Gabriela Luna") con la sala | IllegalStateException "La sala de espera está llena (capacidad 6):     | Se imprime el mensaje de la excepción y la fila sigue
 *       | sala llena, y nombre inválido     | llena; agregarPersona("   ");               | Gabriela Luna no puede entrar." e IllegalArgumentException "El nombre   | con las mismas 4 personas; ningún turno se consume
 *       |                                   | agregarPersona(null)                        | de la persona no puede estar vacío." (dos veces)                        | para los nombres inválidos
 * CP-05 | Error forzado: atender y          | atender a todos y luego atenderSiguiente(), | IllegalStateException "No hay nadie en la fila: no se puede atender.",  | Se imprimen los tres mensajes de excepción y el programa
 *       | consultar con la fila vacía       | consultarPrimero() y                        | "No hay nadie en la fila: no hay a quién consultar." y "La sala de      | llega a "=== Fin de la demostración ==="
 *       |                                   | agregarPersona("Hugo Vera")                 | espera está llena (capacidad 6): Hugo Vera no puede entrar."            |
 * ============================================================================
 */
