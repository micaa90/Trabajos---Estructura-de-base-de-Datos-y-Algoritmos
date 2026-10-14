/*
 * ============================================================================
 * Ejercicio 8 - Cola de impresión
 * Práctico 4: Pilas y Colas con Arreglos en Java
 * ============================================================================
 *
 * Prompt inicial utilizado:
 * """
 * Analizá este problema: un sistema de cola de impresión, donde cada documento tiene un nombre
 * y una cantidad de páginas, y los documentos se imprimen en el mismo orden en que fueron
 * agregados.
 * El punto central: explicá cómo una COLA representa correctamente el orden de impresión.
 * Desarrollá:
 * - por qué el orden de impresión debe ser el orden de llegada, y qué expectativa del usuario
 *   se rompe si no lo es;
 * - cómo la política FIFO garantiza exactamente eso, sin que el programa tenga que hacer
 *   ninguna ordenación;
 * - por qué la cantidad de páginas de un documento NO cambia su orden en la cola: un documento
 *   de 100 páginas enviado antes se imprime antes que uno de 1 página enviado después. Hacé
 *   notar explícitamente que esto es una consecuencia de FIFO y que podría considerarse una
 *   desventaja (un documento largo bloquea a los cortos), y mencioná que una cola de prioridad
 *   resolvería eso pero rompería el orden de llegada;
 * - qué operación de cola corresponde a agregar un trabajo y a imprimir el siguiente.
 * Definí además: la estructura para representar un documento, el manejo del arreglo de tamaño
 * fijo, y los casos límite: imprimir con la cola vacía, cola de impresión llena, documento de
 * 0 páginas, dos documentos con el mismo nombre.
 * Indicá la complejidad de cada operación y qué información útil puede calcular el sistema
 * (total de páginas pendientes).
 * No escribas la implementación.
 * """
 *
 * Ajustes realizados:
 * Sí hubo que ajustar el prompt. El análisis admitía el documento de 0 páginas sin decir qué
 * hacer con él; se ajustó el prompt del desarrollador para ACEPTARLO (es un archivo válido,
 * solo que no produce hojas) pero RECHAZAR una cantidad negativa de páginas con excepción, y
 * la distinción quedó documentada en el código: 0 es un dato posible del dominio, -3 es un
 * dato imposible.
 * Segundo ajuste: hubo que aclarar que dos documentos con el MISMO nombre se aceptan sin
 * problema. La cola no es un conjunto ni un diccionario: no indexa por nombre, así que no hay
 * ninguna colisión. Lo que los distingue es su posición en la cola, no el nombre.
 * Tercer ajuste: se pidió explícitamente que el main cargue el documento LARGO primero y el
 * CORTO después, porque con cualquier otro orden el efecto de bloqueo no se ve, que es lo que
 * el ejercicio quiere mostrar.
 * Cuarto ajuste: el análisis proponía mantener un acumulador de páginas pendientes para que
 * totalPaginasPendientes() fuera O(1); se decidió calcularlo recorriendo la cola (O(n)) para
 * tener una sola fuente de verdad, y la justificación quedó en el Javadoc del método.
 *
 * ¿CÓMO REPRESENTA UNA COLA EL ORDEN DE IMPRESIÓN? El usuario que manda un trabajo a imprimir
 * espera que salga después de los que ya estaban y antes de los que todavía no se enviaron. Esa
 * expectativa es exactamente FIFO (First In, First Out): agregar un trabajo = enqueue, imprimir
 * el siguiente = dequeue, consultar qué sale ahora = front. El orden correcto no se calcula ni
 * se ordena: lo garantiza la estructura. Si el orden no se respetara, el usuario no podría
 * predecir cuándo sale su trabajo ni retirar sus hojas con confianza (podría llevarse las de
 * otro).
 *
 * LA CONSECUENCIA INCÓMODA: la cantidad de páginas NO influye en el orden. Un documento de 120
 * páginas enviado primero se imprime ANTES que uno de 1 página enviado después, y el dueño del
 * documento corto espera las 120 hojas. Eso no es un error: es lo que FIFO significa, y es
 * visible en la ejecución de este programa. Una COLA DE PRIORIDAD (atender primero los
 * trabajos más cortos) reduciría la espera promedio, pero rompería el orden de llegada y
 * habilitaría la inanición: un documento largo podría quedar postergado indefinidamente
 * mientras sigan llegando documentos cortos. FIFO elige la previsibilidad y la equidad por
 * sobre el tiempo de espera promedio.
 *
 * Estrategia: cola FIFO de objetos Documento sobre un arreglo de tamaño fijo, con front =
 * índice del próximo a imprimir y rear = índice del último enviado, sin reordenamientos.
 *
 * Complejidad de las operaciones principales:
 *   agregarDocumento, imprimirSiguiente, consultarSiguiente, cantidadPendientes, colaVacia,
 *   colaLlena -> O(1) temporal, O(1) espacial.
 *   totalPaginasPendientes -> O(n) temporal (recorre los pendientes).
 *   imprimirCola -> O(n); imprimirEstado -> O(capacidad) (diagnóstico).
 *   Espacial: O(n) con n = capacidad de la cola.
 * ============================================================================
 */

/**
 * Programa de demostración del ejercicio 8: cola de impresión FIFO sobre arreglo de tamaño
 * fijo.
 *
 * <p>Carga cuatro documentos con cantidades de páginas distintas —el más largo primero y uno
 * de una sola página después— para que se vea el bloqueo, y después recorre los casos límite:
 * cola llena, documento de 0 páginas, dos documentos con el mismo nombre, páginas negativas e
 * impresión con la cola vacía.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio08_ColaDeImpresion {

    /**
     * Método principal: simula una sesión de impresión completa y fuerza los casos límite.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 8 - Cola de impresión (COLA FIFO) ===");
        System.out.println("Capacidad de la cola de impresión: 6 trabajos");

        ColaDeImpresion impresora = new ColaDeImpresion(6);
        impresora.imprimirCola();

        // --- Se envían 4 documentos: el LARGO primero, el CORTO después ---
        System.out.println("\n--- 1) Se envían 4 documentos (agregar trabajo = enqueue) ---");
        System.out.println("Se envía: " + impresora.agregarDocumento("Tesis_Final.pdf", 120));
        impresora.imprimirCola();
        System.out.println("Se envía: " + impresora.agregarDocumento("Recibo_Sueldo.pdf", 1));
        impresora.imprimirCola();
        System.out.println("Se envía: " + impresora.agregarDocumento("Apunte_Pilas.pdf", 15));
        impresora.imprimirCola();
        System.out.println("Se envía: " + impresora.agregarDocumento("Planilla_Notas.xlsx", 3));
        impresora.imprimirCola();
        impresora.imprimirEstado();

        System.out.println("\n  OBSERVAR: Recibo_Sueldo.pdf tiene 1 sola página y está SEGUNDO,");
        System.out.println("  detrás de una tesis de 120 páginas, porque se envió después.");
        System.out.println("  Las páginas NO reordenan la cola: eso es lo que significa FIFO.");

        // --- Consultar el siguiente sin imprimirlo ---
        System.out.println("\n--- 2) Consultar el siguiente (consultar = front) ---");
        System.out.println("consultarSiguiente() -> " + impresora.consultarSiguiente());
        System.out.println("totalPaginasPendientes() -> " + impresora.totalPaginasPendientes()
                + " páginas");
        System.out.println("La cola NO cambió: consultar no imprime.");
        impresora.imprimirCola();

        // --- Casos límite de carga: 0 páginas y nombre repetido ---
        System.out.println("\n--- 3) Casos límite de carga ---");
        System.out.println("Documento de 0 páginas (archivo válido que no produce hojas):");
        System.out.println("  Se envía: " + impresora.agregarDocumento("Portada_Vacia.pdf", 0));
        System.out.println("Documento con un nombre YA USADO (la cola no indexa por nombre:");
        System.out.println("no hay colisión posible, los distingue su posición):");
        System.out.println("  Se envía: " + impresora.agregarDocumento("Recibo_Sueldo.pdf", 2));
        impresora.imprimirCola();
        impresora.imprimirEstado();

        try {
            impresora.agregarDocumento("Factura_Rota.pdf", -3);
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalArgumentException e) {
            System.out.println("agregarDocumento(..., -3) -> Excepción capturada: "
                    + e.getMessage());
        }
        try {
            impresora.agregarDocumento(null, 5);
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalArgumentException e) {
            System.out.println("agregarDocumento(null, 5) -> Excepción capturada: "
                    + e.getMessage());
        }

        // --- Caso límite: cola de impresión llena ---
        System.out.println("\n--- 4) Caso límite: cola de impresión LLENA ---");
        System.out.println("colaLlena() -> " + impresora.colaLlena()
                + " | pendientes = " + impresora.cantidadPendientes()
                + "/" + impresora.capacidad());
        try {
            impresora.agregarDocumento("Informe_Tarde.docx", 8);
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("Excepción capturada: " + e.getMessage());
        }

        // --- Se procesan todos los trabajos en orden ---
        System.out.println("\n--- 5) Se procesan TODOS los trabajos en orden de llegada ---");
        int hojasImpresas = 0;
        while (!impresora.colaVacia()) {
            Documento impreso = impresora.imprimirSiguiente();
            hojasImpresas += impreso.getPaginas();
            System.out.println("  Hojas acumuladas: " + hojasImpresas
                    + " | páginas pendientes: " + impresora.totalPaginasPendientes());
            impresora.imprimirCola();
        }
        impresora.imprimirEstado();
        System.out.println("Total impreso en la sesión: " + hojasImpresas + " páginas.");
        System.out.println("El orden de salida fue EXACTAMENTE el orden de envío.");

        // --- Caso límite: imprimir y consultar con la cola vacía ---
        System.out.println("\n--- 6) Caso límite: cola VACÍA ---");
        System.out.println("totalPaginasPendientes() -> " + impresora.totalPaginasPendientes()
                + "   (0 páginas, sin recorrer nada)");
        try {
            impresora.imprimirSiguiente();
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("imprimirSiguiente()  -> Excepción capturada: " + e.getMessage());
        }
        try {
            impresora.consultarSiguiente();
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("consultarSiguiente() -> Excepción capturada: " + e.getMessage());
        }

        // --- Caso límite: capacidad degenerada ---
        System.out.println("\n--- 7) Caso límite: capacidades degeneradas ---");
        ColaDeImpresion impresoraCero = new ColaDeImpresion(0);
        System.out.println("ColaDeImpresion(0): colaVacia = " + impresoraCero.colaVacia()
                + " | colaLlena = " + impresoraCero.colaLlena()
                + "   (no acepta ningún trabajo)");
        try {
            new ColaDeImpresion(-2);
        } catch (IllegalArgumentException e) {
            System.out.println("new ColaDeImpresion(-2) -> Excepción capturada: "
                    + e.getMessage());
        }

        System.out.println("\n=== Fin de la demostración ===");
    }
}

/**
 * Trabajo de impresión: un documento con su nombre y su cantidad de páginas.
 *
 * <p><b>Por qué la cantidad de páginas es solo un dato y no una clave de orden.</b> Las
 * páginas sirven para informar al usuario y para calcular el total pendiente, pero la cola
 * nunca las consulta para decidir a quién le toca: el orden lo determina únicamente el momento
 * de llegada. Si las páginas influyeran en el orden, la estructura ya no sería una cola sino
 * una cola de prioridad.</p>
 *
 * <p><b>Nombres repetidos.</b> Dos documentos pueden tener el mismo nombre sin que haya
 * conflicto, porque la cola no indexa por nombre ni lo usa como identidad: los distingue su
 * posición. Por eso la clase no sobreescribe {@code equals} ni {@code hashCode}.</p>
 */
class Documento {

    /** Nombre del archivo a imprimir. */
    private final String nombre;

    /** Cantidad de páginas; 0 es válido (archivo sin contenido imprimible). */
    private final int paginas;

    /**
     * Crea un documento.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param nombre nombre del archivo; no puede ser null ni estar en blanco
     * @param paginas cantidad de páginas; debe ser mayor o igual que 0
     * @throws IllegalArgumentException si el nombre es inválido o las páginas son negativas
     */
    public Documento(String nombre, int paginas) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del documento no puede estar vacío.");
        }
        if (paginas < 0) {
            // 0 páginas se ACEPTA: es un archivo válido que no produce hojas. Una cantidad
            // negativa es un dato imposible en el dominio, así que se rechaza.
            throw new IllegalArgumentException(
                    "La cantidad de páginas no puede ser negativa: " + paginas);
        }
        this.nombre = nombre.trim();
        this.paginas = paginas;
    }

    /**
     * Devuelve el nombre del documento.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return nombre del archivo
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve la cantidad de páginas del documento.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return cantidad de páginas
     */
    public int getPaginas() {
        return paginas;
    }

    /**
     * Representación textual del documento.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return cadena con el nombre y la cantidad de páginas
     */
    @Override
    public String toString() {
        return nombre + " (" + paginas + " pág.)";
    }
}

/**
 * Cola de impresión: una cola FIFO de {@link Documento} sobre un arreglo de tamaño fijo.
 *
 * <p><b>Traducción del dominio a operaciones de cola.</b> {@code agregarDocumento} =
 * <b>enqueue</b>; {@code imprimirSiguiente} = <b>dequeue</b>; {@code consultarSiguiente} =
 * <b>front</b>; {@code cantidadPendientes} = <b>size</b>; {@code colaVacia} = <b>isEmpty</b>;
 * {@code colaLlena} = <b>isFull</b>.</p>
 *
 * <p><b>Por qué una cola y no una pila.</b> Con una pila (LIFO) se imprimiría primero el
 * ÚLTIMO trabajo enviado, de modo que el que mandó su documento primero vería salir las hojas
 * de todos los demás antes de las suyas, y mientras siguieran llegando trabajos nuevos podría
 * no imprimirse nunca (inanición). El orden de una impresora compartida tiene que ser
 * predecible, y FIFO es justamente "se imprime en el orden en que se envió".</p>
 *
 * <p><b>El bloqueo por diseño.</b> Como el orden depende solo de la llegada, un documento de
 * muchas páginas retrasa a todos los que están detrás, aunque sean de una sola hoja. Es una
 * consecuencia directa de FIFO, no un defecto de la implementación. Priorizar los cortos
 * exigiría una cola de prioridad, que mejora la espera promedio a cambio de perder el orden de
 * llegada y habilitar la inanición de los trabajos largos.</p>
 *
 * <p><b>Índices.</b> {@code front} es el índice del próximo documento a imprimir (inicial 0) y
 * {@code rear} el índice del último enviado (inicial -1). Los dos solo avanzan, así que las
 * posiciones de los trabajos ya impresos quedan libres pero inutilizables: el defecto que
 * demuestra el ejercicio 9.</p>
 *
 * <p>Complejidad: todas las operaciones de la cola son O(1), salvo
 * {@link #totalPaginasPendientes()} que es O(n) y los métodos de impresión, que son
 * diagnóstico.</p>
 */
class ColaDeImpresion {

    /** Arreglo interno de tamaño fijo con los trabajos en orden de llegada. */
    private final Documento[] trabajos;

    /** Índice del próximo documento a imprimir. Avanza SOLO al imprimir. */
    private int front;

    /** Índice del último documento enviado. Avanza SOLO al agregar. */
    private int rear;

    /**
     * Crea una cola de impresión vacía con la capacidad indicada.
     *
     * <p>Complejidad: O(1) temporal, O(capacidad) espacial.</p>
     *
     * @param capacidad cantidad máxima de trabajos en espera
     * @throws IllegalArgumentException si la capacidad es negativa
     */
    public ColaDeImpresion(int capacidad) {
        if (capacidad < 0) {
            throw new IllegalArgumentException("La capacidad no puede ser negativa: " + capacidad);
        }
        this.trabajos = new Documento[capacidad];
        this.front = 0;
        this.rear = -1;
    }

    /**
     * Agrega un documento al final de la cola de impresión.
     *
     * <p>Equivale a un <b>enqueue</b>: rear avanza una posición y ahí queda el nuevo trabajo.
     * El documento se encola <b>sin mirar sus páginas</b>: por eso el orden de impresión es
     * siempre el orden de envío.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param nombre nombre del archivo
     * @param paginas cantidad de páginas (0 o más)
     * @return el documento encolado
     * @throws IllegalArgumentException si el nombre es inválido o las páginas son negativas
     * @throws IllegalStateException si la cola de impresión está llena
     */
    public Documento agregarDocumento(String nombre, int paginas) {
        Documento documento = new Documento(nombre, paginas); // valida nombre y páginas
        if (colaLlena()) {
            throw new IllegalStateException("La cola de impresión está llena (capacidad "
                    + trabajos.length + "): " + documento.getNombre() + " se rechaza.");
        }
        rear++;
        trabajos[rear] = documento;
        return documento;
    }

    /**
     * Imprime el siguiente documento: lo muestra y lo retira de la cola.
     *
     * <p>Equivale a un <b>dequeue</b>: se lee {@code trabajos[front]} y front avanza una
     * posición. No se desplaza nada, así que la operación es O(1).</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return el documento que se acaba de imprimir
     * @throws IllegalStateException si no hay trabajos pendientes
     */
    public Documento imprimirSiguiente() {
        if (colaVacia()) {
            throw new IllegalStateException(
                    "No hay trabajos pendientes: la cola de impresión está vacía.");
        }
        Documento documento = trabajos[front];
        trabajos[front] = null; // libera la referencia: el trabajo ya salió del sistema
        front++;                // le toca al siguiente
        System.out.println("  IMPRIMIENDO: " + documento.getNombre()
                + " | " + documento.getPaginas() + " página(s)");
        return documento;
    }

    /**
     * Consulta qué documento se va a imprimir a continuación, sin imprimirlo.
     *
     * <p>Equivale a un <b>front</b>: no modifica ningún índice.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return el próximo documento de la cola
     * @throws IllegalStateException si no hay trabajos pendientes
     */
    public Documento consultarSiguiente() {
        if (colaVacia()) {
            throw new IllegalStateException(
                    "No hay trabajos pendientes: la cola de impresión está vacía.");
        }
        return trabajos[front];
    }

    /**
     * Devuelve la suma de páginas de todos los trabajos pendientes.
     *
     * <p>Es la información útil que el sistema puede calcular: sirve para estimar cuánto papel
     * y cuánto tiempo falta. Se calcula recorriendo los pendientes en lugar de mantener un
     * acumulador: el acumulador haría la operación O(1), pero duplicaría el dato (la suma
     * viviría en un atributo <i>y</i> en los documentos) y cualquier olvido al agregar o al
     * imprimir dejaría el total mentiroso. Con una sola fuente de verdad el total nunca puede
     * desincronizarse, y el costo O(n) es irrelevante para una cola de capacidad fija chica.</p>
     *
     * <p>Complejidad: O(n) temporal con n = trabajos pendientes; O(1) espacial.</p>
     *
     * @return total de páginas pendientes; 0 si la cola está vacía
     */
    public int totalPaginasPendientes() {
        int total = 0;
        if (colaVacia()) {
            return total;
        }
        for (int i = front; i <= rear; i++) { // solo el rango vivo: front..rear
            total += trabajos[i].getPaginas();
        }
        return total;
    }

    /**
     * Devuelve la cantidad de trabajos pendientes.
     *
     * <p>Equivale a <b>size</b>: dato derivado de los índices.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return cantidad de documentos en espera
     */
    public int cantidadPendientes() {
        if (colaVacia()) {
            return 0;
        }
        return rear - front + 1;
    }

    /**
     * Indica si no hay trabajos pendientes.
     *
     * <p>Equivale a <b>isEmpty</b>: front "pasó de largo" a rear.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return true si la cola está vacía
     */
    public boolean colaVacia() {
        return rear < front;
    }

    /**
     * Indica si la cola de impresión no admite más trabajos.
     *
     * <p>Equivale a <b>isFull</b>: rear llegó al último índice del arreglo. Igual que en el
     * ejercicio 6, esto no significa "no queda ninguna posición libre".</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return true si no entra ningún trabajo más
     */
    public boolean colaLlena() {
        return rear == trabajos.length - 1;
    }

    /**
     * Devuelve la capacidad fija de la cola.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return capacidad del arreglo interno
     */
    public int capacidad() {
        return trabajos.length;
    }

    /**
     * Imprime la cola pendiente en el orden en que se va a imprimir (vista del usuario).
     *
     * <p>Complejidad: O(n) con n = trabajos pendientes. Método de diagnóstico.</p>
     */
    public void imprimirCola() {
        System.out.print("  COLA PENDIENTE (próximo -> último enviado): ");
        if (colaVacia()) {
            System.out.println("(sin trabajos pendientes) | 0 páginas pendientes");
            return;
        }
        for (int i = front; i <= rear; i++) {
            System.out.print("[" + trabajos[i] + "]" + (i < rear ? " <- " : ""));
        }
        System.out.println();
        System.out.println("  pendientes: " + cantidadPendientes() + "/" + capacidad()
                + " | páginas pendientes: " + totalPaginasPendientes());
    }

    /**
     * Imprime el estado INTERNO: el arreglo con sus índices y los valores de front y rear.
     *
     * <p>Complejidad: O(capacidad) temporal. Método de diagnóstico.</p>
     */
    public void imprimirEstado() {
        System.out.print("  arreglo interno: ");
        if (trabajos.length == 0) {
            System.out.print("[] (capacidad 0)");
        }
        for (int i = 0; i < trabajos.length; i++) {
            String contenido = (trabajos[i] == null)
                    ? "null"
                    : (trabajos[i].getNombre() + "/" + trabajos[i].getPaginas() + "p");
            System.out.print("[" + i + "]=" + contenido + marcaDe(i) + "  ");
        }
        System.out.println();
        System.out.println("  front = " + front + " | rear = " + rear + " | pendientes = "
                + cantidadPendientes() + " | colaVacia = " + colaVacia()
                + " | colaLlena = " + colaLlena());
        System.out.println("  marcas: F = front, R = rear, <F = libre INUTILIZABLE (ya"
                + " impreso), * = libre disponible");
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
        if (!colaVacia() && i == front && i == rear) {
            return "(FR)";
        }
        if (!colaVacia() && i == front) {
            return "(F)";
        }
        if (!colaVacia() && i == rear) {
            return "(R)";
        }
        if (!colaVacia() && i > front && i < rear) {
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
 * Compilación: javac Ejercicio08_ColaDeImpresion.java -d /tmp/v4
 * Ejecución:   java -cp /tmp/v4 Ejercicio08_ColaDeImpresion
 * Todos los casos están cableados en main(); el programa no lee entrada por consola.
 *
 * ID    | Objetivo                          | Entrada exacta                              | Resultado esperado                                                      | Criterio de aprobación
 * ------+-----------------------------------+---------------------------------------------+-------------------------------------------------------------------------+-----------------------------------------------------------
 * CP-01 | Flujo nominal: el orden de        | new ColaDeImpresion(6); agregarDocumento    | consultarSiguiente() devuelve "Tesis_Final.pdf (120 pág.)" y el total   | "consultarSiguiente() -> Tesis_Final.pdf (120 pág.)" y
 *       | impresión es el orden de envío,   | ("Tesis_Final.pdf",120), ("Recibo_Sueldo    | pendiente es 139 páginas; la tesis de 120 páginas sale ANTES del        | "totalPaginasPendientes() -> 139 páginas"; al procesar,
 *       | sin importar las páginas          | .pdf",1), ("Apunte_Pilas.pdf",15),          | recibo de 1 página porque se envió primero                              | la primera línea IMPRIMIENDO es Tesis_Final.pdf
 *       |                                   | ("Planilla_Notas.xlsx",3)                   |                                                                         |
 * CP-02 | Estructura vacía y valores en el  | cola recién construida; documento de 0      | "(sin trabajos pendientes) | 0 páginas pendientes" al inicio;           | La primera imprimirCola() muestra "(sin trabajos
 *       | límite (0 páginas, nombre         | páginas; segundo documento llamado          | Portada_Vacia.pdf (0 pág.) y el segundo Recibo_Sueldo.pdf (2 pág.) se  | pendientes)" y los dos documentos límite aparecen
 *       | repetido)                         | "Recibo_Sueldo.pdf" con 2 páginas           | aceptan sin error y conviven en la cola                                 | encolados en las posiciones 4 y 5
 * CP-03 | Estructura llena                  | con los 6 trabajos cargados,                | colaLlena = true con pendientes = 6/6 y rear = 5                        | "colaLlena() -> true | pendientes = 6/6"
 *       |                                   | agregarDocumento("Informe_Tarde.docx",8)    |                                                                         |
 * CP-04 | Error forzado: cola llena y       | agregarDocumento("Informe_Tarde.docx",8)    | IllegalStateException "La cola de impresión está llena (capacidad 6):   | Se imprimen los tres mensajes de excepción y la cola
 *       | datos inválidos                   | con la cola llena; (..., -3); (null, 5)     | Informe_Tarde.docx se rechaza." e IllegalArgumentException "La cantidad | sigue con los mismos 6 trabajos
 *       |                                   |                                             | de páginas no puede ser negativa: -3" y "El nombre del documento no     |
 *       |                                   |                                             | puede estar vacío."                                                      |
 * CP-05 | Error forzado: imprimir y         | procesar los 6 trabajos y luego             | IllegalStateException "No hay trabajos pendientes: la cola de impresión | Las 6 líneas IMPRIMIENDO salen en el orden de envío, el
 *       | consultar con la cola vacía       | imprimirSiguiente() y consultarSiguiente()  | está vacía." en las dos llamadas; total impreso = 141 páginas           | total impreso es 141 y se imprimen los dos mensajes de
 *       |                                   |                                             |                                                                         | excepción antes de "=== Fin de la demostración ==="
 * ============================================================================
 */
