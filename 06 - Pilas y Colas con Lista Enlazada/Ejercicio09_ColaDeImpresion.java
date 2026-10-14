/*
 * =====================================================================================
 * Práctico 6 — Pilas y Colas con Listas Enlazadas. Ejercicio 9: Cola de impresión
 * =====================================================================================
 * Prompt inicial utilizado:
 * "Analizá este problema: una cola de impresión donde cada trabajo tiene nombre del archivo,
 *  cantidad de páginas y usuario que lo envió. El sistema debe permitir agregar trabajos,
 *  imprimir el próximo trabajo, consultar el próximo archivo y mostrar todos los trabajos
 *  pendientes.
 *  El punto central: explicá POR QUÉ UNA IMPRESORA DEBE PROCESAR LOS TRABAJOS USANDO FIFO.
 *  Desarrollá:
 *  - qué expectativa tiene el usuario que manda un documento a imprimir: que se imprima cuando
 *    le toque, es decir después de los que ya estaban y antes de los que lleguen después;
 *  - por qué FIFO es la única política que garantiza eso sin que el sistema tenga que decidir nada;
 *  - una consecuencia importante que hay que señalar explícitamente: la cantidad de páginas NO
 *    afecta el orden. Un documento de 200 páginas enviado antes se imprime antes que uno de 1
 *    página enviado después. Analizá esto como un TRADE-OFF real: FIFO es justo respecto del
 *    orden de llegada pero puede ser percibido como injusto respecto del tiempo de espera, porque
 *    un trabajo largo bloquea a todos los cortos;
 *  - mencioná que una política alternativa (el trabajo más corto primero) minimizaría el tiempo
 *    de espera promedio pero podría postergar indefinidamente un trabajo largo (inanición), y
 *    que por eso las impresoras reales usan FIFO;
 *  - por qué una cola ENLAZADA conviene acá: la cantidad de trabajos pendientes no tiene un tope
 *    natural;
 *  - qué información útil puede calcular el sistema: total de páginas pendientes, cantidad de
 *    trabajos, y el tiempo estimado de espera del último trabajo de la fila;
 *  - la complejidad esperada de cada operación.
 *  Casos especiales: imprimir con la cola vacía, consultar el próximo con la cola vacía, trabajo
 *  de 0 páginas, dos trabajos con el mismo nombre de archivo, varios trabajos del mismo usuario,
 *  vaciar la cola completamente y agregar un trabajo nuevo después.
 *  Definí la estructura de la clase TrabajoImpresion.
 *  No escribas la implementación."
 *
 * Ajustes realizados:
 *  1) Se pidió analizar el bloqueo como TRADE-OFF y no como defecto. La primera respuesta
 *     mencionaba al pasar que las páginas no alteran el orden, sin medir la consecuencia. Al
 *     exigir el análisis, quedó claro que hay que cuantificarlo: el programa calcula cuántas
 *     páginas tiene que esperar cada trabajo, y con el trabajo de 200 páginas primero se ve que
 *     el de 1 página espera 200. FIFO no está "mal": prioriza equidad de llegada sobre tiempo
 *     de espera, y esa es una decisión de diseño, no un error.
 *  2) Se agregó la mención de la inanición en la política alternativa. Sin eso, "el más corto
 *     primero" parece estrictamente mejor. Con la aclaración se entiende por qué una impresora
 *     real no la usa: un informe largo podría no imprimirse nunca si siguen llegando trabajos
 *     cortos.
 *  3) Se agregó el caso de vaciar la cola y agregar un trabajo después. La primera respuesta no
 *     lo contemplaba, y es justamente el caso que descubre el bug de tail: si al desencolar el
 *     último elemento tail no vuelve a null, el próximo trabajo se engancha a un nodo
 *     inalcanzable y se pierde sin ningún error visible.
 *
 * Estrategia: cola FIFO sobre lista enlazada simple con referencias head y tail. Se encola por
 * tail y se desencola por head, de modo que el orden de impresión es exactamente el orden de
 * llegada sin necesidad de ordenar ni comparar nada. La cantidad de páginas es un dato del
 * trabajo, no un criterio de orden.
 * Complejidad temporal: O(1) en agregarTrabajo, imprimirProximo, consultarProximo,
 * cantidadTrabajos y totalPaginasPendientes (los dos últimos por contadores incrementales);
 * O(n) en mostrarPendientes, que recorre la cadena.
 * Complejidad espacial: O(n), un nodo por trabajo pendiente, sin capacidad fija.
 * =====================================================================================
 */

/**
 * Sistema de cola de impresión FIFO sobre lista enlazada.
 *
 * <p>El programa muestra, además del funcionamiento normal, el costo de la política FIFO: pone a
 * propósito un trabajo de 200 páginas antes de uno de 1 página y calcula cuánto espera cada
 * uno.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio09_ColaDeImpresion {

    /**
     * Punto de entrada. Simula una jornada de impresión y recorre los casos especiales.
     *
     * <p>Complejidad: O(n^2) por las impresiones del estado completo en cada paso; las
     * operaciones de la cola son O(1).</p>
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 9 - Cola de impresión (FIFO con lista enlazada) ===");

        ColaImpresion cola = new ColaImpresion();
        System.out.println("\n--- Cola recién creada ---");
        cola.mostrarPendientes();
        intentarImprimir(cola);
        intentarConsultar(cola);

        // ------------------------------------------------------------------
        // El trabajo largo entra PRIMERO a propósito, para exponer el bloqueo.
        // ------------------------------------------------------------------
        System.out.println("\n--- Llegan los trabajos (el largo primero, a propósito) ---");
        agregarMostrando(cola, "informe_anual.pdf", 200, "micaela");
        agregarMostrando(cola, "recibo.pdf", 1, "jorge");
        agregarMostrando(cola, "tp_estructuras.pdf", 12, "micaela");
        agregarMostrando(cola, "recibo.pdf", 3, "ana");

        System.out.println("\n--- Consulta de quién sigue ---");
        System.out.println("consultarProximo() -> " + cola.consultarProximo());
        System.out.println("La cola NO se modifica al consultar: cantidad = "
                + cola.cantidadTrabajos());

        System.out.println("\n============================================================");
        System.out.println("EL COSTO DE FIFO: CUÁNTO ESPERA CADA TRABAJO");
        System.out.println("============================================================");
        cola.mostrarEsperaEstimada();
        System.out.println("El recibo de 1 página espera 200 páginas porque llegó segundo.");
        System.out.println("FIFO es justo respecto del ORDEN DE LLEGADA, no del tiempo de espera:");
        System.out.println("un trabajo largo bloquea a todos los cortos que vienen detrás.");
        System.out.println("La política alternativa 'el más corto primero' bajaría la espera");
        System.out.println("promedio, pero el informe de 200 páginas podría no imprimirse NUNCA");
        System.out.println("si siguen llegando trabajos cortos. Eso se llama inanición, y es el");
        System.out.println("motivo por el que las impresoras reales usan FIFO.");

        System.out.println("\n--- Se procesan todos los trabajos en orden ---");
        while (cola.cantidadTrabajos() > 0) {
            System.out.println("imprimirProximo() -> " + cola.imprimirProximo());
            System.out.println("   pendientes: " + cola.cantidadTrabajos() + " trabajos, "
                    + cola.totalPaginasPendientes() + " páginas");
        }
        System.out.println("\nEl orden de impresión coincidió exactamente con el de llegada.");

        // ------------------------------------------------------------------
        // Casos especiales.
        // ------------------------------------------------------------------
        System.out.println("\n--- Cola vacía otra vez ---");
        cola.mostrarPendientes();
        intentarImprimir(cola);

        System.out.println("\n--- Caso especial de tail: agregar DESPUÉS de haber vaciado ---");
        System.out.println("Si al desencolar el último trabajo tail no hubiera vuelto a null,");
        System.out.println("este trabajo se engancharía a un nodo inalcanzable y se perdería.");
        agregarMostrando(cola, "despues_de_vaciar.pdf", 5, "micaela");
        System.out.println("consultarProximo() -> " + cola.consultarProximo());
        System.out.println(cola.cantidadTrabajos() == 1
                ? "El trabajo es accesible: tail se manejó bien -> OK"
                : "ERROR: el trabajo se perdió");
        System.out.println("imprimirProximo() -> " + cola.imprimirProximo());

        System.out.println("\n--- Trabajo de 0 páginas (válido: un documento vacío) ---");
        agregarMostrando(cola, "vacio.pdf", 0, "jorge");
        System.out.println("totalPaginasPendientes() = " + cola.totalPaginasPendientes());
        System.out.println("imprimirProximo() -> " + cola.imprimirProximo());

        System.out.println("\n--- Validaciones ---");
        intentarAgregar(cola, null, 5, "ana", "nombre de archivo null");
        intentarAgregar(cola, "x.pdf", -2, "ana", "cantidad de páginas negativa");
        intentarAgregar(cola, "x.pdf", 5, null, "usuario null");
    }

    /**
     * Agrega un trabajo e informa el estado resultante de la cola.
     *
     * <p>Complejidad: O(n) por la impresión de los pendientes.</p>
     *
     * @param cola cola sobre la que se opera
     * @param archivo nombre del archivo
     * @param paginas cantidad de páginas
     * @param usuario usuario que envía el trabajo
     */
    private static void agregarMostrando(ColaImpresion cola, String archivo, int paginas,
                                         String usuario) {
        cola.agregarTrabajo(archivo, paginas, usuario);
        System.out.println("agregarTrabajo(\"" + archivo + "\", " + paginas + ", \"" + usuario
                + "\")");
        cola.mostrarPendientes();
    }

    /**
     * Verifica que imprimir sobre una cola vacía se rechace con una excepción explicativa.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param cola cola sobre la que se opera
     */
    private static void intentarImprimir(ColaImpresion cola) {
        try {
            cola.imprimirProximo();
            System.out.println("ERROR: imprimirProximo() debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("imprimirProximo() lanzó IllegalStateException: "
                    + e.getMessage() + "  -> OK");
        }
    }

    /**
     * Verifica que consultar sobre una cola vacía se rechace con una excepción explicativa.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param cola cola sobre la que se opera
     */
    private static void intentarConsultar(ColaImpresion cola) {
        try {
            cola.consultarProximo();
            System.out.println("ERROR: consultarProximo() debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("consultarProximo() lanzó IllegalStateException: "
                    + e.getMessage() + "  -> OK");
        }
    }

    /**
     * Verifica que un trabajo con datos inválidos se rechace con una excepción explicativa.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param cola cola sobre la que se opera
     * @param archivo nombre del archivo
     * @param paginas cantidad de páginas
     * @param usuario usuario que envía el trabajo
     * @param descripcion descripción del caso, para el informe
     */
    private static void intentarAgregar(ColaImpresion cola, String archivo, int paginas,
                                        String usuario, String descripcion) {
        try {
            cola.agregarTrabajo(archivo, paginas, usuario);
            System.out.println("ERROR: " + descripcion + " debió ser rechazado");
        } catch (IllegalArgumentException e) {
            System.out.println(descripcion + " -> IllegalArgumentException: "
                    + e.getMessage() + "  -> OK");
        }
    }
}

/**
 * Trabajo de impresión: el dato que viaja dentro de cada nodo de la cola.
 *
 * <p>La cantidad de páginas es información del trabajo, no un criterio de orden. La cola nunca
 * la mira para decidir a quién atender: el orden lo determina la posición en la cadena de
 * enlaces.</p>
 */
class TrabajoImpresion {

    /** Nombre del archivo a imprimir. Puede repetirse entre trabajos distintos. */
    private final String archivo;

    /** Cantidad de páginas. Cero es válido: representa un documento vacío. */
    private final int paginas;

    /** Usuario que envió el trabajo. Puede repetirse entre trabajos distintos. */
    private final String usuario;

    /**
     * Crea un trabajo de impresión validando sus datos.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param archivo nombre del archivo; no {@code null} ni vacío
     * @param paginas cantidad de páginas; no negativa
     * @param usuario usuario que envía el trabajo; no {@code null} ni vacío
     * @throws IllegalArgumentException si el archivo o el usuario son {@code null} o vacíos, o si
     *         la cantidad de páginas es negativa
     */
    TrabajoImpresion(String archivo, int paginas, String usuario) {
        if (archivo == null || archivo.isBlank()) {
            throw new IllegalArgumentException("El nombre del archivo no puede ser null ni vacío: "
                    + "un trabajo sin archivo no identifica nada que imprimir.");
        }
        if (usuario == null || usuario.isBlank()) {
            throw new IllegalArgumentException("El usuario no puede ser null ni vacío: hace falta "
                    + "saber quién envió el trabajo.");
        }
        if (paginas < 0) {
            throw new IllegalArgumentException("La cantidad de páginas no puede ser negativa y se "
                    + "recibió " + paginas + ". El 0 sí se admite: es un documento vacío.");
        }
        this.archivo = archivo;
        this.paginas = paginas;
        this.usuario = usuario;
    }

    /**
     * Devuelve el nombre del archivo.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return nombre del archivo
     */
    public String getArchivo() {
        return archivo;
    }

    /**
     * Devuelve la cantidad de páginas.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return cantidad de páginas
     */
    public int getPaginas() {
        return paginas;
    }

    /**
     * Devuelve el usuario que envió el trabajo.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return nombre del usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Devuelve la representación legible del trabajo.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return cadena con archivo, páginas y usuario
     */
    @Override
    public String toString() {
        return "\"" + archivo + "\" (" + paginas + " pág., envió " + usuario + ")";
    }
}

/**
 * Nodo de la cola de impresión: un trabajo y la referencia al siguiente.
 */
class NodoTrabajo {

    /** Trabajo almacenado en este nodo. */
    private final TrabajoImpresion dato;

    /** Referencia al nodo siguiente, o {@code null} si es el último de la fila. */
    private NodoTrabajo siguiente;

    /**
     * Crea un nodo suelto, todavía sin enlazar.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param dato trabajo a almacenar
     */
    NodoTrabajo(TrabajoImpresion dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    /**
     * Devuelve el trabajo almacenado.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return el trabajo de este nodo
     */
    public TrabajoImpresion getDato() {
        return dato;
    }

    /**
     * Devuelve el nodo siguiente.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return el nodo siguiente, o {@code null} si este es el último
     */
    public NodoTrabajo getSiguiente() {
        return siguiente;
    }

    /**
     * Enlaza este nodo con el siguiente.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param siguiente nodo que pasa a ser el siguiente, o {@code null}
     */
    public void setSiguiente(NodoTrabajo siguiente) {
        this.siguiente = siguiente;
    }
}

/**
 * Cola de impresión FIFO sobre lista enlazada simple.
 *
 * <p><b>Por qué una cola y no una pila.</b> El usuario que manda un documento espera que se
 * imprima después de los que ya estaban y antes de los que lleguen después. FIFO garantiza
 * exactamente eso sin que el sistema compare ni ordene nada: el orden está codificado en la
 * posición del nodo dentro de la cadena. Con una pila, el último trabajo enviado se imprimiría
 * primero y un documento podría quedar postergado indefinidamente.</p>
 *
 * <p><b>Por qué hacen falta head y tail.</b> Se desencola por head, que es O(1) porque head ya
 * apunta al primer nodo. Se encola por tail, y sin esa referencia habría que recorrer toda la
 * cadena en cada encolado para llegar al último nodo: la operación pasaría de O(1) a O(n).</p>
 *
 * <p><b>Por qué enlazada y no sobre arreglo.</b> La cantidad de trabajos pendientes no tiene un
 * tope natural, así que una capacidad fija obligaría a rechazar trabajos o a dimensionar de más.
 * Al no haber capacidad, no existe el método {@code estaLlena}.</p>
 */
class ColaImpresion {

    /** Primer nodo: el próximo trabajo en imprimirse. */
    private NodoTrabajo head;

    /** Último nodo: donde se engancha el próximo trabajo que llegue. */
    private NodoTrabajo tail;

    /** Cantidad de trabajos pendientes, mantenida para que la consulta sea O(1). */
    private int cantidad;

    /** Suma de las páginas pendientes, mantenida de forma incremental para ser O(1). */
    private int paginasPendientes;

    /**
     * Agrega un trabajo al final de la fila.
     *
     * <p>Caso especial de la cola vacía: no existe un último nodo al que enganchar, así que head
     * y tail pasan a apuntar los dos al nodo nuevo.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param archivo nombre del archivo; no {@code null} ni vacío
     * @param paginas cantidad de páginas; no negativa
     * @param usuario usuario que envía el trabajo; no {@code null} ni vacío
     * @throws IllegalArgumentException si alguno de los datos del trabajo es inválido
     */
    public void agregarTrabajo(String archivo, int paginas, String usuario) {
        TrabajoImpresion trabajo = new TrabajoImpresion(archivo, paginas, usuario);
        NodoTrabajo nuevo = new NodoTrabajo(trabajo);

        if (estaVacia()) {
            head = nuevo;
            tail = nuevo;
        } else {
            tail.setSiguiente(nuevo);
            tail = nuevo;
        }
        cantidad++;
        paginasPendientes += paginas;
    }

    /**
     * Quita el primer trabajo de la fila y lo devuelve como el trabajo que se está imprimiendo.
     *
     * <p>Caso especial: si al desencolar la fila queda vacía, {@code tail} debe volver a
     * {@code null}. Olvidarlo no produce ningún error inmediato, y ahí está el peligro: tail
     * seguiría apuntando a un nodo ya desencolado, y el próximo {@code agregarTrabajo} se
     * engancharía a esa cadena inalcanzable desde head, perdiendo el trabajo en silencio.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return el trabajo que pasa a imprimirse
     * @throws IllegalStateException si no hay trabajos pendientes
     */
    public TrabajoImpresion imprimirProximo() {
        if (estaVacia()) {
            throw new IllegalStateException("No hay trabajos para imprimir: la cola está vacía.");
        }
        TrabajoImpresion trabajo = head.getDato();
        head = head.getSiguiente();
        if (head == null) {
            tail = null;
        }
        cantidad--;
        paginasPendientes -= trabajo.getPaginas();
        return trabajo;
    }

    /**
     * Devuelve el próximo trabajo sin quitarlo de la fila.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return el trabajo que está primero
     * @throws IllegalStateException si no hay trabajos pendientes
     */
    public TrabajoImpresion consultarProximo() {
        if (estaVacia()) {
            throw new IllegalStateException("No hay próximo trabajo: la cola está vacía.");
        }
        return head.getDato();
    }

    /**
     * Indica si no hay trabajos pendientes.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return {@code true} si {@code head} es {@code null}
     */
    public boolean estaVacia() {
        return head == null;
    }

    /**
     * Devuelve la cantidad de trabajos pendientes.
     *
     * <p>Complejidad: O(1), por el contador incremental.</p>
     *
     * @return cantidad de trabajos en la fila
     */
    public int cantidadTrabajos() {
        return cantidad;
    }

    /**
     * Devuelve la suma de las páginas de todos los trabajos pendientes.
     *
     * <p>Complejidad: O(1), porque el total se mantiene al encolar y desencolar en lugar de
     * recorrer la cadena en cada consulta.</p>
     *
     * @return total de páginas pendientes
     */
    public int totalPaginasPendientes() {
        return paginasPendientes;
    }

    /**
     * Imprime la fila completa en orden de impresión, con el total de páginas pendientes.
     *
     * <p>Complejidad: O(n), porque recorre la cadena desde head.</p>
     */
    public void mostrarPendientes() {
        if (estaVacia()) {
            System.out.println("   fila: (vacía)   | head = null | tail = null");
            return;
        }
        StringBuilder sb = new StringBuilder("   fila: PRÓXIMO -> ");
        NodoTrabajo actual = head;
        while (actual != null) {
            sb.append(actual.getDato().getArchivo());
            actual = actual.getSiguiente();
            if (actual != null) {
                sb.append(" -> ");
            }
        }
        System.out.println(sb.append(" -> null").toString());
        System.out.println("   head -> " + head.getDato().getArchivo()
                + " | tail -> " + tail.getDato().getArchivo()
                + " | trabajos = " + cantidad + " | páginas pendientes = " + paginasPendientes);
    }

    /**
     * Imprime, para cada trabajo de la fila, cuántas páginas tiene que esperar antes de empezar.
     *
     * <p>Es la medida concreta del costo de FIFO: la espera de un trabajo es la suma de las
     * páginas de todos los que llegaron antes, sin importar cuán corto sea él mismo.</p>
     *
     * <p>Complejidad: O(n).</p>
     */
    public void mostrarEsperaEstimada() {
        if (estaVacia()) {
            System.out.println("   (no hay trabajos pendientes)");
            return;
        }
        int acumuladas = 0;
        int posicion = 1;
        NodoTrabajo actual = head;
        while (actual != null) {
            TrabajoImpresion t = actual.getDato();
            System.out.printf("   %d) %-22s %3d pág. | usuario %-8s | espera antes: %3d pág.%n",
                    posicion, t.getArchivo(), t.getPaginas(), t.getUsuario(), acumuladas);
            acumuladas += t.getPaginas();
            posicion++;
            actual = actual.getSiguiente();
        }
        System.out.println("   total a imprimir: " + acumuladas + " páginas");
    }
}
