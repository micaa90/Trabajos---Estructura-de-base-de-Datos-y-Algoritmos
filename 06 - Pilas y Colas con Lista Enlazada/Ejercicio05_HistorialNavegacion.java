/*
 * ============================================================================
 * Ejercicio 5 - Historial de navegación con pila
 * Práctico 6 - Pilas y Colas con Listas Enlazadas
 * ============================================================================
 *
 * Prompt inicial utilizado (transcripción literal del prompt del Agente Analista):
 *
 *   Analizá este problema: un sistema simple de historial de navegación usando una PILA
 *   ENLAZADA, que debe permitir visitar una página, volver a la página anterior, consultar
 *   la página actual e imprimir el historial.
 *   El punto central: explicá POR QUÉ UNA PILA REPRESENTA CORRECTAMENTE EL COMPORTAMIENTO
 *   DE "VOLVER ATRÁS". Desarrollá:
 *   - qué espera el usuario al apretar "atrás": volver a la página INMEDIATAMENTE anterior,
 *     es decir la última que visitó antes de la actual;
 *   - que "la última en entrar es la primera en salir" es literalmente la definición de
 *     LIFO, así que la correspondencia es exacta y no forzada;
 *   - qué pasaría con una cola: volver atrás llevaría a la PRIMERA página de la sesión, lo
 *     cual es claramente incorrecto. Dá el ejemplo concreto con tres páginas visitadas;
 *   - una sutileza que tenés que resolver explícitamente: la página ACTUAL, ¿está dentro de
 *     la pila o fuera? Analizá las dos convenciones posibles (tope = página actual, o tope =
 *     página anterior) y elegí una justificando, porque de eso depende qué hace exactamente
 *     la operación "volver atrás" y qué devuelve "página actual";
 *   - qué operación de pila corresponde a cada acción del enunciado;
 *   - una observación valiosa: por qué una pila SOLA no alcanza para implementar el botón
 *     "adelante" de un navegador real, y qué haría falta (una segunda pila). Explicá por qué
 *     al visitar una página nueva después de haber vuelto atrás se pierde el camino hacia
 *     adelante, y por qué eso coincide con el comportamiento de un navegador real;
 *   - por qué corresponde una pila ENLAZADA y no sobre arreglo acá: el historial no tiene un
 *     límite natural de tamaño, así que la capacidad fija sería una restricción artificial;
 *   - la complejidad esperada de cada operación.
 *   Casos especiales: volver atrás sin historial, volver atrás con una sola página visitada,
 *   consultar la página actual con el historial vacío, visitar la misma página dos veces
 *   seguidas (decidí si se apila igual y justificá).
 *   No escribas la implementación.
 *
 * Ajustes realizados posteriormente al prompt inicial:
 *   1) Se agregó el método cantidadPaginas() (y el atributo size en la pila) para que la
 *      cantidad de páginas del historial sea O(1) y para poder distinguir en volverAtras()
 *      el caso "historial vacío" del caso "una sola página visitada", que el prompt pedía
 *      tratar por separado.
 *   2) Se agregó hayPaginaActual() como consulta no destructiva, para que el main pueda
 *      informar el estado sin provocar la excepción de paginaActual() sobre historial vacío.
 *   3) Se fijó el formato de imprimirHistorial() como "TOPE -> actual -> anterior -> ... ->
 *      null", con la marca "(página actual)" sobre el tope. El prompt pedía imprimir el
 *      historial; marcar el tope es lo que vuelve visible la convención elegida, que es el
 *      punto ambiguo del ejercicio.
 *   4) Se decidió que volverAtras() devuelva la NUEVA página actual (la que queda en el tope
 *      después del desapilado) en lugar de la página abandonada, porque es lo que el usuario
 *      ve en pantalla después de apretar "atrás". La página abandonada se informa igual en la
 *      traza impresa.
 *   No hubo ajustes sobre el diseño: la convención (tope = página actual), el uso de head
 *   como tope y las complejidades O(1) salieron tal cual del análisis.
 *
 * Estrategia: pila enlazada de String donde head es el tope y EL TOPE ES LA PÁGINA ACTUAL;
 * visitar es apilar, volver atrás es desapilar y la nueva página actual es el nuevo tope.
 *
 * Complejidad esperada de las operaciones principales:
 *   visitar          O(1)      volverAtras      O(1)      paginaActual     O(1)
 *   cantidadPaginas  O(1)      hayPaginaActual  O(1)      imprimirHistorial O(n)
 *   Espacial: O(n) nodos, uno por página visitada que todavía no se abandonó.
 *
 * POR QUÉ LIFO MODELA EL "VOLVER ATRÁS". Al apretar "atrás" el usuario espera volver a la
 * página INMEDIATAMENTE anterior, es decir a la ÚLTIMA que visitó antes de la actual. "La
 * última en entrar es la primera en salir" es, literalmente, la definición de LIFO: la
 * correspondencia es exacta y no forzada. Con una COLA el comportamiento sería incorrecto:
 * visitando inicio.com, noticias.com y deportes.com, apretar "atrás" debería llevar a
 * noticias.com (la anterior), pero una cola entregaría inicio.com (la PRIMERA de la sesión),
 * que no es la página anterior sino la más vieja de todas.
 *
 * CONVENCIÓN ELEGIDA PARA LA PÁGINA ACTUAL: EL TOPE DE LA PILA ES LA PÁGINA ACTUAL, es decir
 * la página actual está DENTRO de la pila. Las dos convenciones posibles eran:
 *   (a) tope = página actual (elegida): paginaActual() es verTope(), y volverAtras() desapila
 *       la actual para que el nuevo tope pase a ser la actual. Una sola estructura, un solo
 *       invariante, y la pila impresa es exactamente el historial de la sesión.
 *   (b) tope = página anterior: la actual viviría en un atributo aparte, fuera de la pila.
 *       volverAtras() desapilaría y asignaría ese valor al atributo. Es la convención que
 *       usan los navegadores reales (porque necesitan la actual separada para el botón
 *       "adelante"), pero duplica el estado: hay que mantener sincronizados el atributo y la
 *       pila, y aparecen estados inconsistentes posibles (actual != null con pila vacía).
 * Se eligió (a) porque el enunciado no pide el botón "adelante" y, sin él, el atributo extra
 * de (b) sólo agrega una fuente de errores. CONSECUENCIA DIRECTA DE LA ELECCIÓN: volver atrás
 * requiere AL MENOS DOS páginas en el historial. Con una sola, desapilar dejaría la pila
 * vacía y la sesión sin ninguna página actual, que es un estado que no existe en un navegador
 * (una pestaña abierta siempre muestra algo). Por eso volverAtras() con una sola página lanza
 * excepción en lugar de vaciar el historial.
 *
 * POR QUÉ UNA SOLA PILA NO ALCANZA PARA EL BOTÓN "ADELANTE". Al desapilar, la página
 * abandonada queda inalcanzable: nadie la referencia y el Garbage Collector la recupera. Para
 * poder volver "adelante" habría que GUARDARLA en algún lado, y el orden en que habría que
 * devolverla también es LIFO (la última que se abandonó es la primera a la que se vuelve
 * adelante), así que hace falta una SEGUNDA PILA. El mecanismo sería un trasvase: "atrás"
 * desapila de la pila de atrás y apila en la de adelante; "adelante" hace lo inverso. Y hay
 * una regla más: al VISITAR una página nueva después de haber vuelto atrás, la pila de
 * adelante se VACÍA, porque el camino hacia adelante que había dejado de ser alcanzable: se
 * abrió una rama nueva. Eso coincide exactamente con el comportamiento de un navegador real,
 * donde el botón "siguiente" se apaga en cuanto uno vuelve atrás y hace clic en otro enlace.
 *
 * POR QUÉ PILA ENLAZADA Y NO SOBRE ARREGLO: el historial de una sesión no tiene un límite
 * natural de tamaño, así que cualquier capacidad fija sería una restricción artificial (o se
 * dimensiona de más y se desperdicia memoria, o se dimensiona de menos y hay que descartar
 * páginas visitadas).
 *
 * DIFERENCIA CON EL PRÁCTICO 4 (historial sobre arreglo): acá NO EXISTE isFull(), porque no
 * hay capacidad fija: cada página visitada crea su nodo en el momento y no hay un bloque de
 * memoria reservado de antemano que pueda agotarse. En la versión con arreglo del práctico 4
 * había que preguntar isFull() antes de cada visita y decidir qué hacer con el desborde
 * (rechazar la navegación o descartar la página más vieja del fondo, que en un arreglo con
 * tope en el final es justamente la más cara de descartar). Acá el único límite es la memoria
 * de la JVM. El precio es la memoria extra de una referencia por nodo y la pérdida de
 * localidad: los nodos quedan dispersos en el Heap en lugar de contiguos.
 *
 * @author Micaela Gimenez
 * @version 1.0
 */

/**
 * Nodo de la lista simplemente enlazada que soporta la pila del historial.
 *
 * <p>Guarda la URL de una página visitada y UNA sola referencia, hacia la página que se
 * visitó ANTES que ella. Esa referencia es, literalmente, el enlace "hacia atrás" del
 * historial: siguiendo la cadena desde el tope se recorre la sesión en orden inverso al de
 * visita.</p>
 */
class Nodo {

    /** URL de la página visitada. Nunca es null: visitar() rechaza null y cadenas vacías. */
    String url;

    /** Página visitada inmediatamente antes que esta; null si es la primera de la sesión. */
    Nodo siguiente;

    /**
     * Crea un nodo suelto, todavía no enlazado a ninguna pila.
     *
     * @param url URL de la página visitada
     */
    Nodo(String url) {
        this.url = url;
        this.siguiente = null;
    }
}

/**
 * Pila de cadenas (política LIFO) implementada sobre una lista simplemente enlazada, donde
 * {@code head} es el TOPE.
 *
 * <p><b>¿Por qué head es el tope?</b> Insertar y eliminar al INICIO de una lista enlazada no
 * requiere ningún recorrido, porque {@code head} ya apunta ahí: apilar y desapilar son O(1).
 * Si el tope fuera el último nodo habría que recorrer la cadena entera en cada operación y,
 * además, desapilar necesitaría el nodo ANTERIOR al último, inalcanzable sin recorrer desde
 * {@code head} en una lista simplemente enlazada: O(n).</p>
 *
 * <p>No existe isFull(): no hay capacidad fija. Ver la cabecera del archivo.</p>
 */
class PilaEnlazada {

    /** Tope de la pila: el último elemento apilado. null si la pila está vacía. */
    private Nodo head;

    /** Cantidad de elementos apilados. Se mantiene para que tamanio() sea O(1). */
    private int size;

    /**
     * Crea una pila vacía.
     *
     * <p>Complejidad: O(1)</p>
     */
    PilaEnlazada() {
        this.head = null;
        this.size = 0;
    }

    /**
     * Apila una cadena, que pasa a ser el nuevo tope.
     *
     * <p>Complejidad: O(1) - head ya está en el punto de inserción, no hay recorrido.</p>
     *
     * @param url cadena a apilar; no puede ser null
     * @throws IllegalArgumentException si la cadena es null
     */
    void apilar(String url) {
        if (url == null) {
            throw new IllegalArgumentException("No se admiten elementos null en la pila.");
        }
        Nodo nuevo = new Nodo(url);
        // ORDEN DE LAS DOS ASIGNACIONES: primero el nodo nuevo apunta al tope actual y
        // DESPUÉS head se mueve al nodo nuevo. Si se invirtiera (head = nuevo primero), se
        // perdería la única referencia al resto de la pila y nuevo.siguiente terminaría
        // apuntándose a sí mismo: la pila quedaría con un solo nodo y un ciclo.
        nuevo.siguiente = head;
        head = nuevo;
        size++;
    }

    /**
     * Quita y devuelve el tope: el último elemento apilado.
     *
     * <p>Complejidad: O(1) - head ya apunta al nodo a eliminar.</p>
     *
     * @return la cadena que estaba en el tope
     * @throws IllegalStateException si la pila está vacía
     */
    String desapilar() {
        if (estaVacia()) {
            throw new IllegalStateException("No se puede desapilar: la pila está vacía.");
        }
        String url = head.url;   // 1) rescatar el dato antes de perder la referencia
        head = head.siguiente;   // 2) el nodo desapilado queda inalcanzable -> lo recupera el GC
        size--;
        return url;
    }

    /**
     * Consulta el tope sin quitarlo.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return la cadena que está en el tope
     * @throws IllegalStateException si la pila está vacía
     */
    String verTope() {
        if (estaVacia()) {
            throw new IllegalStateException("No se puede consultar el tope: la pila está vacía.");
        }
        return head.url;
    }

    /**
     * Indica si la pila no tiene elementos.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return true si la pila está vacía
     */
    boolean estaVacia() {
        return head == null;
    }

    /**
     * Cantidad de elementos apilados.
     *
     * <p>Complejidad: O(1) gracias al atributo size; sin él habría que recorrer: O(n).</p>
     *
     * @return número de elementos
     */
    int tamanio() {
        return size;
    }

    /**
     * Informa si una cadena está en algún lugar de la pila, comparando por CONTENIDO.
     *
     * <p>Se usa {@code equals()} y no {@code ==}: con objetos {@code ==} compararía
     * identidad de referencia y daría false para dos String de igual contenido creados por
     * separado.</p>
     *
     * <p>Complejidad: O(n) - recorre la cadena. Buscar no pertenece al contrato de una pila;
     * se agrega como utilidad de inspección.</p>
     *
     * @param url cadena a buscar; si es null devuelve false
     * @return true si la cadena está en la pila
     */
    boolean contiene(String url) {
        if (url == null) {
            return false; // la pila no admite nulls, así que nunca puede estar
        }
        Nodo actual = head;
        while (actual != null) {
            if (url.equals(actual.url)) {
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    /**
     * Imprime la pila desde el tope hacia el fondo, en formato
     * {@code TOPE -> 30 -> 20 -> 10 -> null}.
     *
     * <p>El orden impreso es el orden de SALIDA, es decir el inverso al de entrada: eso es
     * LIFO.</p>
     *
     * <p>Complejidad: O(n)</p>
     */
    void imprimir() {
        StringBuilder salida = new StringBuilder("TOPE");
        Nodo actual = head;
        while (actual != null) {
            salida.append(" -> ").append(actual.url);
            actual = actual.siguiente;
        }
        salida.append(" -> null");
        if (estaVacia()) {
            salida.append("   (pila vacía)");
        }
        System.out.println("  " + salida + "   [tamanio=" + size + "]");
    }

    /**
     * Devuelve a qué dato apunta head, o la palabra null si no apunta a ningún nodo.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return representación textual del dato del tope
     */
    String estadoHead() {
        return head == null ? "null" : head.url;
    }
}

/**
 * Historial de navegación de una sesión, construido sobre la pila enlazada.
 *
 * <p><b>Convención (decidida en el análisis y clave para leer este código):</b> EL TOPE DE LA
 * PILA ES LA PÁGINA ACTUAL. La página que el usuario está viendo está DENTRO de la pila, en
 * el tope. Sin esta aclaración el código sería ambiguo: no se sabría si {@code volverAtras()}
 * tiene que desapilar una vez (si el tope es la actual) o ninguna (si el tope ya fuera la
 * anterior).</p>
 *
 * <p><b>Correspondencia entre cada acción del enunciado y su operación de pila:</b></p>
 * <table border="1">
 *   <caption>Acciones del dominio y operaciones de pila</caption>
 *   <tr><th>Acción del enunciado</th><th>Operación de pila</th><th>Complejidad</th></tr>
 *   <tr><td>visitar una página</td><td>{@code apilar(url)}</td><td>O(1)</td></tr>
 *   <tr><td>volver a la página anterior</td><td>{@code desapilar()} y el nuevo tope pasa a
 *       ser la actual</td><td>O(1)</td></tr>
 *   <tr><td>consultar la página actual</td><td>{@code verTope()}</td><td>O(1)</td></tr>
 *   <tr><td>imprimir el historial</td><td>recorrido completo desde el tope</td><td>O(n)</td></tr>
 *   <tr><td>cantidad de páginas</td><td>{@code tamanio()}</td><td>O(1)</td></tr>
 * </table>
 *
 * <p><b>Visitar la misma página dos veces seguidas:</b> se apila igual. La pila registra la
 * SECUENCIA de navegación, no el conjunto de páginas distintas visitadas; si se recarga o se
 * vuelve a entrar al mismo sitio, ese paso ocurrió y "atrás" tiene que deshacerlo. Filtrar
 * los duplicados consecutivos rompería la correspondencia entre la cantidad de "atrás"
 * disponibles y la cantidad de pasos que el usuario dio.</p>
 */
class HistorialNavegacion {

    /** Pila de URLs visitadas. El tope es, por convención, la página actual. */
    private final PilaEnlazada pila;

    /**
     * Crea un historial vacío: una sesión sin ninguna página visitada todavía.
     *
     * <p>Complejidad: O(1)</p>
     */
    HistorialNavegacion() {
        this.pila = new PilaEnlazada();
    }

    /**
     * Visita una página: la apila y pasa a ser la página actual.
     *
     * <p>Se apila incluso si es igual a la página actual (ver la nota de la clase).</p>
     *
     * <p>Complejidad: O(1)</p>
     *
     * @param url dirección de la página visitada; no puede ser null ni estar en blanco
     * @throws IllegalArgumentException si la URL es null o está en blanco
     */
    void visitar(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("La URL es obligatoria y no puede estar en blanco.");
        }
        // Acá, y en un navegador real, correspondería además VACIAR la pila de "adelante":
        // visitar una página nueva abre una rama y descarta el camino hacia adelante. Como
        // esta implementación no tiene segunda pila, no hay nada que vaciar.
        pila.apilar(url);
    }

    /**
     * Vuelve a la página anterior: abandona la página actual y devuelve la que queda.
     *
     * <p>Por la convención elegida (tope = página actual) esto es UN desapilado: se saca la
     * actual y el nuevo tope pasa a ser la actual. La página abandonada queda inalcanzable,
     * y por eso el botón "adelante" necesitaría una segunda pila para conservarla.</p>
     *
     * <p>Requiere AL MENOS DOS páginas en el historial: con una sola no hay página anterior a
     * la cual volver, y desapilarla dejaría la sesión sin ninguna página actual, un estado que
     * en un navegador no existe.</p>
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return la URL de la nueva página actual (la anterior a la que se abandonó)
     * @throws IllegalStateException si el historial está vacío o tiene una sola página
     */
    String volverAtras() {
        // CASO ESPECIAL A: historial vacío. No hay ni página actual ni anterior.
        if (pila.estaVacia()) {
            throw new IllegalStateException(
                    "No se puede volver atrás: el historial está vacío (ninguna página visitada).");
        }
        // CASO ESPECIAL B: una sola página visitada. Hay página actual pero NO hay anterior.
        // Es consecuencia directa de la convención: la actual está dentro de la pila, así que
        // desapilarla dejaría la sesión sin página actual.
        if (pila.tamanio() == 1) {
            throw new IllegalStateException("No se puede volver atrás: '" + pila.verTope()
                    + "' es la primera página de la sesión y no tiene anterior.");
        }
        pila.desapilar();     // se abandona la página actual (queda inalcanzable: GC)
        return pila.verTope(); // el nuevo tope es, por convención, la nueva página actual
    }

    /**
     * Consulta la página actual sin modificar el historial.
     *
     * <p>Es {@code verTope()} directo, por la convención elegida.</p>
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return la URL de la página actual
     * @throws IllegalStateException si el historial está vacío
     */
    String paginaActual() {
        if (pila.estaVacia()) {
            throw new IllegalStateException(
                    "No hay página actual: el historial está vacío.");
        }
        return pila.verTope();
    }

    /**
     * Indica si hay alguna página actual, sin lanzar excepción.
     *
     * <p>Consulta no destructiva pensada para que el llamador pueda decidir si tiene sentido
     * invocar {@code paginaActual()}.</p>
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return true si hay al menos una página visitada
     */
    boolean hayPaginaActual() {
        return !pila.estaVacia();
    }

    /**
     * Imprime el historial completo desde la página actual hacia la más antigua.
     *
     * <p>El orden impreso es el orden en que el botón "atrás" iría recorriendo las páginas:
     * primero la actual (tope), después la anterior, y así hasta la primera de la sesión.</p>
     *
     * <p>Complejidad: O(n)</p>
     */
    void imprimirHistorial() {
        pila.imprimir();
        System.out.println("      head (TOPE) -> " + pila.estadoHead()
                + "   <- por convención, esta es la PÁGINA ACTUAL");
    }

    /**
     * Cantidad de páginas que quedan en el historial (incluida la actual).
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return número de páginas registradas
     */
    int cantidadPaginas() {
        return pila.tamanio();
    }

    /**
     * Informa si una URL figura en el historial.
     *
     * <p>Complejidad: O(n)</p>
     *
     * @param url URL a buscar
     * @return true si la URL está en el historial
     */
    boolean fueVisitada(String url) {
        return pila.contiene(url);
    }

    /**
     * Indica si hay una página anterior a la actual, es decir si "atrás" está habilitado.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return true si el historial tiene dos o más páginas
     */
    boolean puedeVolverAtras() {
        return pila.tamanio() >= 2;
    }
}

/**
 * Programa de demostración del ejercicio 5: historial de navegación sobre pila enlazada.
 *
 * <p>Simula una sesión completa: visita cuatro páginas, consulta la actual, vuelve atrás dos
 * veces, visita una página nueva (el momento en que un navegador real perdería el botón
 * "siguiente"), imprime el historial y después vuelve atrás hasta agotarlo para mostrar el
 * caso límite. Después de cada operación muestra el historial completo y la página actual.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio05_HistorialNavegacion {

    /**
     * Método principal: ejecuta la simulación completa de la sesión.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 5 - Historial de navegación con pila (LIFO) ===");
        System.out.println("CONVENCIÓN: el TOPE de la pila ES la página actual (está dentro de la pila).");

        HistorialNavegacion historial = new HistorialNavegacion();

        System.out.println("\n--- Caso límite: historial recién creado (vacío) ---");
        System.out.println("  hayPaginaActual() = " + historial.hayPaginaActual()
                + " | cantidadPaginas() = " + historial.cantidadPaginas());
        historial.imprimirHistorial();
        intentarPaginaActual(historial);
        intentarVolverAtras(historial);

        System.out.println("\n--- Sesión: se visitan 4 páginas ---");
        visitarMostrando(historial, "inicio.com");
        System.out.println("  *** Con UNA sola página: hay actual pero NO hay anterior ***");
        intentarVolverAtras(historial);
        visitarMostrando(historial, "noticias.com");
        visitarMostrando(historial, "deportes.com");
        visitarMostrando(historial, "clima.com");

        System.out.println("\n--- Consultar la página actual (no modifica el historial) ---");
        System.out.println("  paginaActual() = " + historial.paginaActual()
                + "  (la ÚLTIMA visitada: eso es LIFO)");
        System.out.println("  cantidadPaginas() = " + historial.cantidadPaginas()
                + " | puedeVolverAtras() = " + historial.puedeVolverAtras());
        System.out.println("  Con una COLA, 'atrás' habría llevado a inicio.com (la PRIMERA de");
        System.out.println("  la sesión) en lugar de a deportes.com (la anterior). Por eso pila.");

        System.out.println("\n--- Volver atrás dos veces ---");
        volverAtrasMostrando(historial);
        volverAtrasMostrando(historial);

        System.out.println("\n*** MOMENTO CLAVE: se visita una página NUEVA después de volver atrás ***");
        System.out.println("  Antes de visitar: paginaActual() = " + historial.paginaActual());
        System.out.println("  Las páginas abandonadas (clima.com y deportes.com) ya se desapilaron:");
        System.out.println("  fueVisitada(\"clima.com\")    = " + historial.fueVisitada("clima.com")
                + "  -> quedó inalcanzable, la recuperó el GC");
        System.out.println("  fueVisitada(\"deportes.com\") = " + historial.fueVisitada("deportes.com")
                + "  -> idem");
        visitarMostrando(historial, "mapas.com");
        System.out.println("  Se abrió una RAMA NUEVA: el camino hacia adelante se perdió para");
        System.out.println("  siempre. En un navegador real el botón 'siguiente' se apaga acá.");
        System.out.println("  Para conservarlo haría falta una SEGUNDA pila (la de 'adelante'),");
        System.out.println("  y visitar una página nueva tendría que VACIARLA.");

        System.out.println("\n--- Historial completo en este punto ---");
        historial.imprimirHistorial();
        System.out.println("  paginaActual() = " + historial.paginaActual()
                + " | cantidadPaginas() = " + historial.cantidadPaginas());

        System.out.println("\n--- Caso límite: volver atrás HASTA AGOTAR el historial ---");
        while (historial.puedeVolverAtras()) {
            volverAtrasMostrando(historial);
        }
        System.out.println("  Queda UNA sola página y 'atrás' ya no está habilitado:");
        System.out.println("  puedeVolverAtras() = " + historial.puedeVolverAtras()
                + " | paginaActual() = " + historial.paginaActual());
        intentarVolverAtras(historial);
        System.out.println("  La sesión conserva siempre una página actual: no se puede dejar");
        System.out.println("  la pestaña sin nada que mostrar. Eso es consecuencia de la convención.");

        System.out.println("\n--- Caso especial: visitar la MISMA página dos veces seguidas ---");
        visitarMostrando(historial, "inicio.com");
        visitarMostrando(historial, "inicio.com");
        System.out.println("  Se apilaron las dos veces: la pila registra la SECUENCIA de pasos,");
        System.out.println("  no el conjunto de páginas distintas. Hay dos 'atrás' disponibles");
        System.out.println("  porque el usuario dio dos pasos.");
        System.out.println("  volverAtras() = " + historial.volverAtras() + " (misma URL, un paso menos)");
        historial.imprimirHistorial();

        System.out.println("\n--- Caso límite: URL inválida (error forzado) ---");
        intentarVisitar(historial, null);
        intentarVisitar(historial, "   ");
        System.out.println("  El historial quedó intacto: cantidadPaginas() = "
                + historial.cantidadPaginas());

        System.out.println("\n--- Sin capacidad fija: no existe isFull() ---");
        HistorialNavegacion largo = new HistorialNavegacion();
        for (int i = 1; i <= 1000; i++) {
            largo.visitar("pagina" + i + ".com");
        }
        System.out.println("  1000 páginas visitadas sin desborde. cantidadPaginas() = "
                + largo.cantidadPaginas() + " | paginaActual() = " + largo.paginaActual());
        System.out.println("  En la versión sobre arreglo del práctico 4 habría que haber");
        System.out.println("  consultado isFull() antes de cada visita.");

        System.out.println("\n=== Fin de la demostración ===");
    }

    /**
     * Visita una página e informa el estado completo del historial.
     *
     * @param historial historial sobre el que se opera
     * @param url       página a visitar
     */
    private static void visitarMostrando(HistorialNavegacion historial, String url) {
        historial.visitar(url);
        System.out.println("  visitar(\"" + url + "\")   [apilar]");
        historial.imprimirHistorial();
        System.out.println("      paginaActual() = " + historial.paginaActual()
                + " | puedeVolverAtras() = " + historial.puedeVolverAtras());
    }

    /**
     * Vuelve atrás e informa el estado completo del historial.
     *
     * @param historial historial sobre el que se opera
     */
    private static void volverAtrasMostrando(HistorialNavegacion historial) {
        String abandonada = historial.paginaActual();
        String nueva = historial.volverAtras();
        System.out.println("  volverAtras()   [desapilar] se abandona \"" + abandonada
                + "\" y la nueva actual es \"" + nueva + "\"");
        historial.imprimirHistorial();
        System.out.println("      cantidadPaginas() = " + historial.cantidadPaginas()
                + " | puedeVolverAtras() = " + historial.puedeVolverAtras());
    }

    /**
     * Intenta consultar la página actual capturando la excepción esperada con historial vacío.
     *
     * @param historial historial sobre el que se opera
     */
    private static void intentarPaginaActual(HistorialNavegacion historial) {
        try {
            System.out.println("  paginaActual() = " + historial.paginaActual());
        } catch (IllegalStateException e) {
            System.out.println("  paginaActual() lanzó IllegalStateException: " + e.getMessage()
                    + "  -> OK");
        }
    }

    /**
     * Intenta volver atrás capturando la excepción esperada cuando no hay página anterior.
     *
     * @param historial historial sobre el que se opera
     */
    private static void intentarVolverAtras(HistorialNavegacion historial) {
        try {
            System.out.println("  volverAtras() = " + historial.volverAtras());
        } catch (IllegalStateException e) {
            System.out.println("  volverAtras() lanzó IllegalStateException: " + e.getMessage()
                    + "  -> OK");
        }
    }

    /**
     * Intenta visitar una URL inválida capturando la excepción esperada.
     *
     * @param historial historial sobre el que se opera
     * @param url       URL inválida a probar
     */
    private static void intentarVisitar(HistorialNavegacion historial, String url) {
        try {
            historial.visitar(url);
            System.out.println("  visitar(" + url + ") no lanzó excepción -> FALLO");
        } catch (IllegalArgumentException e) {
            System.out.println("  visitar(" + (url == null ? "null" : "\"" + url + "\"")
                    + ") lanzó IllegalArgumentException: " + e.getMessage() + "  -> OK");
        }
    }
}


