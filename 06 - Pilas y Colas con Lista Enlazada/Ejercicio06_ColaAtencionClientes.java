/*
 * ============================================================================
 * Ejercicio 6 - Cola de atención de clientes
 * Práctico 6 - Pilas y Colas con Listas Enlazadas
 * ============================================================================
 *
 * Prompt inicial utilizado (transcripción literal del prompt del Agente Analista):
 *
 *   Analizá este problema: una cola de atención para un banco, donde cada cliente tiene
 *   nombre, número de turno y motivo de consulta. El sistema debe permitir agregar un cliente
 *   a la fila, atender al próximo cliente, consultar quién sigue e imprimir la fila actual.
 *   El punto central: explicá POR QUÉ LA ESTRUCTURA ADECUADA ES UNA COLA y CÓMO SE REPRESENTA
 *   EL ORDEN DE LLEGADA. Desarrollá:
 *   - qué principio de equidad rige una fila de banco: el que llegó primero es atendido
 *     primero;
 *   - que la política FIFO codifica ese principio directamente en la estructura, sin que el
 *     programa tenga que ordenar ni comparar nada;
 *   - CÓMO SE REPRESENTA el orden de llegada: no con un campo que se consulte ni con un
 *     ordenamiento, sino con la POSICIÓN del nodo en la cadena de enlaces. El orden es una
 *     propiedad estructural. Desarrollá este punto;
 *   - qué rol cumple el número de turno entonces: es información para el cliente y para la
 *     auditoría, no el mecanismo que determina el orden. Hacé esta distinción explícita, es
 *     una buena observación;
 *   - qué pasaría con una pila: el último en llegar se atendería primero, y un cliente que
 *     llegó temprano podría no ser atendido nunca si siguen llegando personas (inanición).
 *     Dá el ejemplo concreto;
 *   - qué operación de cola corresponde a cada acción del enunciado;
 *   - por qué conviene una cola ENLAZADA sobre una con arreglo acá: la cantidad de gente en
 *     la fila no tiene un tope conocido, y una capacidad fija obligaría a rechazar clientes o
 *     a dimensionar de más;
 *   - la complejidad esperada de cada operación, y por qué importa que agregar y atender sean
 *     O(1) en un sistema que se usa en tiempo real;
 *   - una observación final: por qué un banco real probablemente necesitaría una cola de
 *     PRIORIDAD (clientes preferenciales, jubilados), y qué cambiaría eso en la
 *     implementación.
 *   Casos especiales: atender con la fila vacía, consultar quién sigue con la fila vacía,
 *   atender a todos y que llegue un cliente nuevo después, dos clientes con el mismo nombre.
 *   Definí la estructura de la clase Cliente.
 *   No escribas la implementación.
 *
 * Ajustes realizados posteriormente al prompt inicial:
 *   1) El número de turno se asigna AUTOMÁTICAMENTE desde un contador static de la clase
 *      Cliente, en lugar de recibirse como parámetro. El prompt sólo decía "número de turno";
 *      asignarlo adentro garantiza que sea único y creciente, y además deja ver que el orden
 *      de atención no depende de ese número (el contador podría reiniciarse y la fila seguiría
 *      atendiendo igual).
 *   2) Se agregó cantidadEnEspera() apoyado en un atributo size, para que la consulta sea
 *      O(1) en lugar de O(n).
 *   3) Se fijó el formato de imprimirFila() como "FRENTE -> ... -> null" con una línea extra
 *      que informa a qué cliente apuntan head y tail. El prompt pedía imprimir la fila; mostrar
 *      las dos referencias es lo que vuelve verificable el manejo de tail.
 *   4) Se agregó verificarInvariante() para demostrar en la salida que tail es siempre el
 *      último nodo alcanzable desde head y que size coincide con el recorrido real.
 *   No hubo ajustes sobre el diseño: head como frente, tail como final, los dos casos
 *   especiales y las complejidades O(1) salieron tal cual del análisis.
 *
 * Estrategia: cola FIFO sobre lista simplemente enlazada con head (frente, por donde se
 * atiende) y tail (final, por donde llega la gente), con el Cliente como dato del nodo.
 *
 * Complejidad esperada de las operaciones principales:
 *   agregarCliente   O(1)     atenderProximo   O(1)     consultarProximo  O(1)
 *   cantidadEnEspera O(1)     imprimirFila     O(n)     totalPorMotivo    O(n)
 *   Espacial: O(n) nodos + 2 referencias fijas (head y tail).
 *
 * POR QUÉ UNA COLA (EQUIDAD) Y NO UNA PILA (INANICIÓN). Una fila de banco se rige por un
 * principio de equidad elemental: el que llegó primero es atendido primero. La política FIFO
 * codifica ese principio DIRECTAMENTE EN LA ESTRUCTURA: el programa no ordena ni compara
 * nada, simplemente saca por head y agrega por tail. Con una PILA pasaría lo contrario: el
 * último en llegar se atendería primero. Ejemplo concreto: si Ana llega a las 9:00, Bruno a
 * las 9:05 y Carla a las 9:10, una pila atendería a Carla, después a Bruno y último a Ana; y
 * si entre atención y atención sigue entrando gente, Ana podría NO SER ATENDIDA NUNCA, porque
 * cada recién llegado se le pone delante. Ese fenómeno se llama INANICIÓN (starvation): un
 * elemento que nunca llega a ser procesado porque siempre hay otro con "mejor posición". FIFO
 * lo vuelve imposible: cada cliente que entra queda DETRÁS de todos los que ya estaban, así
 * que su espera es finita y acotada por la cantidad de gente que tenía adelante al llegar.
 *
 * CÓMO SE REPRESENTA EL ORDEN DE LLEGADA. No con un campo que se consulte ni con un
 * ordenamiento: con la POSICIÓN DEL NODO EN LA CADENA DE ENLACES. El orden es una propiedad
 * ESTRUCTURAL, no un dato. El cliente que está más cerca de head llegó antes, y eso lo
 * determina el momento en que se ejecutó encolar(), no ninguna propiedad del objeto Cliente.
 *
 * EL NÚMERO DE TURNO NO DETERMINA EL ORDEN. El número de turno es información para el cliente
 * (sirve para que sepa cuánto le falta) y para la auditoría (permite reconstruir qué pasó),
 * pero NO es el mecanismo que decide a quién se atiende. La prueba está en la salida de este
 * programa: en ningún método se lee ni se compara el campo numeroTurno para decidir el orden;
 * atenderProximo() se limita a tomar el nodo de head. Si el contador de turnos se reiniciara
 * en 1 a mitad de la jornada, o si dos clientes tuvieran el mismo número, la fila seguiría
 * atendiendo exactamente en el mismo orden, porque el orden vive en los enlaces. Esta
 * distinción es la diferencia entre una cola FIFO y una cola ORDENADA POR UN CAMPO.
 *
 * POR QUÉ COLA ENLAZADA Y NO SOBRE ARREGLO: la cantidad de gente que puede entrar a una
 * sucursal no tiene un tope conocido. Una capacidad fija obligaría a rechazar clientes
 * (inaceptable para el negocio) o a dimensionar de más "por si acaso" (memoria reservada y
 * sin usar la mayor parte del día).
 *
 * POR QUÉ IMPORTA QUE AGREGAR Y ATENDER SEAN O(1): es un sistema de tiempo real con un
 * operador humano esperando. Si agregar un cliente fuera O(n), el tiempo de registrar a cada
 * persona crecería con el largo de la fila, es decir el sistema se pondría más lento
 * justamente cuando hay más gente: exactamente el peor momento posible.
 *
 * UN BANCO REAL NECESITARÍA UNA COLA DE PRIORIDAD (clientes preferenciales, jubilados,
 * embarazadas). Eso cambiaría el diseño de fondo: habría que COMPARAR clientes entre sí para
 * decidir quién va primero, así que la estructura dejaría de ser indiferente al dato (haría
 * falta un criterio de orden, un Comparable/Comparator o varias colas FIFO, una por
 * categoría) y agregarCliente() dejaría de ser O(1) porque habría que buscar la posición de
 * inserción: O(n) con lista enlazada, O(log n) con un heap. Y reaparecería el riesgo de
 * inanición: un cliente común podría esperar indefinidamente si no dejan de llegar
 * preferenciales, por lo que los sistemas reales mezclan políticas (por ejemplo, uno
 * preferencial cada N comunes).
 *
 * DIFERENCIA CON EL PRÁCTICO 4 (cola sobre arreglo): acá NO EXISTE isFull() y tampoco existe
 * el desperdicio de espacio que obliga a la aritmética circular. En la cola sobre arreglo,
 * atender a un cliente avanza el índice del frente y deja atrás posiciones que no se pueden
 * reutilizar salvo que los índices "den la vuelta" con el módulo
 * (frente = (frente + 1) % capacidad); sin esa aritmética la fila se declararía llena con el
 * arreglo medio vacío. Acá no hay posiciones: cada nodo se crea cuando llega el cliente y
 * queda inalcanzable (basura para el GC) cuando se lo atiende. El precio es la memoria extra
 * por referencia y la pérdida de localidad.
 *
 * @author Estudiante
 * @version 1.0
 */

/**
 * Cliente del banco que espera ser atendido.
 *
 * <p>El número de turno se asigna automáticamente desde un contador de clase, de forma
 * incremental. Es información para el cliente y para la auditoría: <b>no</b> es el mecanismo
 * que determina el orden de atención, que lo da la posición del nodo en la cadena.</p>
 *
 * <p>No implementa {@code Comparable}: una cola FIFO nunca compara dos clientes entre sí.
 * Para una cola de prioridad habría que agregarle un criterio de orden.</p>
 */
class Cliente {

    /** Contador compartido por todos los clientes: asigna turnos únicos y crecientes. */
    private static int proximoTurno = 1;

    /** Nombre del cliente. */
    private final String nombre;

    /** Número de turno, asignado automáticamente en el constructor. */
    private final int numeroTurno;

    /** Motivo de la consulta. */
    private final String motivo;

    /**
     * Crea un cliente tomando el próximo número de turno disponible.
     *
     * @param nombre nombre del cliente; obligatorio
     * @param motivo motivo de la consulta; obligatorio
     * @throws IllegalArgumentException si el nombre o el motivo son null o están en blanco
     */
    Cliente(String nombre, String motivo) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo de consulta es obligatorio.");
        }
        this.nombre = nombre;
        this.motivo = motivo;
        // El turno se asigna acá y nunca más cambia: es un identificador, no una prioridad.
        this.numeroTurno = proximoTurno;
        proximoTurno++;
    }

    /**
     * Nombre del cliente.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return nombre
     */
    String getNombre() {
        return nombre;
    }

    /**
     * Número de turno asignado al cliente.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return número de turno
     */
    int getNumeroTurno() {
        return numeroTurno;
    }

    /**
     * Motivo de la consulta.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return motivo
     */
    String getMotivo() {
        return motivo;
    }

    /**
     * Representación textual del cliente, con el turno visible.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return cadena con turno, nombre y motivo
     */
    @Override
    public String toString() {
        return "[T" + numeroTurno + " " + nombre + " / " + motivo + "]";
    }

    /**
     * Compara dos clientes por CONTENIDO: iguales si coinciden turno, nombre y motivo.
     *
     * <p>El turno participa de la igualdad justamente para que dos clientes con el MISMO
     * NOMBRE (caso especial del análisis) no se confundan entre sí.</p>
     *
     * <p>Complejidad: O(k) sobre la longitud de las cadenas.</p>
     *
     * @param obj objeto a comparar
     * @return true si obj es un Cliente con el mismo turno, nombre y motivo
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Cliente)) {
            return false; // cubre también obj == null
        }
        Cliente otro = (Cliente) obj;
        return numeroTurno == otro.numeroTurno
                && nombre.equals(otro.nombre)
                && motivo.equals(otro.motivo);
    }

    /**
     * Código hash consistente con equals().
     *
     * <p>Complejidad: O(k)</p>
     *
     * @return hash derivado de turno, nombre y motivo
     */
    @Override
    public int hashCode() {
        return 31 * (31 * numeroTurno + nombre.hashCode()) + motivo.hashCode();
    }
}

/**
 * Nodo de la lista simplemente enlazada que soporta la cola de atención.
 *
 * <p>Guarda un cliente y UNA sola referencia, hacia el cliente que está DETRÁS en la fila.
 * No hace falta una referencia hacia atrás: se atiende por head y se agrega por tail, y las
 * dos operaciones avanzan en el mismo sentido que los enlaces.</p>
 */
class Nodo {

    /** Cliente que ocupa este lugar de la fila. Nunca es null. */
    Cliente dato;

    /** Cliente que está inmediatamente detrás en la fila; null si es el último. */
    Nodo siguiente;

    /**
     * Crea un nodo suelto, todavía no enlazado a ninguna cola.
     *
     * @param dato cliente a almacenar
     */
    Nodo(Cliente dato) {
        this.dato = dato;
        this.siguiente = null;
    }
}

/**
 * Cola de clientes (política FIFO) implementada sobre una lista simplemente enlazada con
 * {@code head} (frente, por donde se atiende) y {@code tail} (final, por donde se llega).
 *
 * <p><b>¿Por qué head Y tail?</b> FIFO obliga a operar en los DOS extremos. El frente tiene
 * que ser head porque eliminar el PRIMER nodo es sólo mover head (O(1)), mientras que
 * eliminar el último exigiría el nodo anterior, inalcanzable sin recorrer en una lista
 * simplemente enlazada. En consecuencia el extremo de entrada es el final, y sin la
 * referencia tail cada llegada obligaría a recorrer la fila completa para encontrar el último
 * nodo: O(n) en lugar de O(1).</p>
 *
 * <p><b>Invariante:</b> o bien {@code head == null && tail == null && size == 0}, o bien
 * {@code head != null && tail != null}, {@code tail.siguiente == null}, {@code tail} es el
 * último nodo alcanzable desde {@code head} y la cadena tiene {@code size} nodos. Con un solo
 * cliente, {@code head == tail}.</p>
 *
 * <p>No existe isFull(): no hay capacidad fija. Ver la cabecera del archivo.</p>
 */
class ColaEnlazada {

    /** Frente de la fila: el cliente al que le toca. null si la fila está vacía. */
    private Nodo head;

    /** Final de la fila: el último que llegó. null si la fila está vacía. */
    private Nodo tail;

    /** Cantidad de clientes en espera. Se mantiene para que cantidadEnEspera() sea O(1). */
    private int size;

    /**
     * Crea una cola vacía.
     *
     * <p>Complejidad: O(1)</p>
     */
    ColaEnlazada() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Encola un cliente, que pasa a ser el último de la fila.
     *
     * <p>Complejidad: O(1) - gracias a tail no hay que recorrer nada. Sin tail sería O(n).</p>
     *
     * @param dato cliente a encolar; no puede ser null
     * @throws IllegalArgumentException si el cliente es null
     */
    void encolar(Cliente dato) {
        if (dato == null) {
            throw new IllegalArgumentException("No se admiten clientes null en la fila.");
        }
        Nodo nuevo = new Nodo(dato);
        if (estaVacia()) {
            // *** CASO ESPECIAL 1: ENCOLAR EN COLA VACÍA ***
            // No hay último nodo al que engancharse, así que el cliente nuevo es
            // simultáneamente el primero y el último de la fila: head y tail deben apuntar
            // LOS DOS al mismo nodo. Si sólo se moviera tail, head quedaría en null y la
            // fila seguiría pareciendo vacía con un cliente adentro.
            head = nuevo;
            tail = nuevo;
        } else {
            tail.siguiente = nuevo; // 1) enganchar al cliente nuevo detrás del último actual
            tail = nuevo;           // 2) recién ahora tail avanza al nodo nuevo
        }
        size++;
    }

    /**
     * Quita y devuelve el cliente del frente: el que llegó primero.
     *
     * <p>Complejidad: O(1) - head ya apunta al nodo a eliminar, no hay búsqueda.</p>
     *
     * @return el cliente que estaba en el frente
     * @throws IllegalStateException si la fila está vacía
     */
    Cliente desencolar() {
        if (estaVacia()) {
            throw new IllegalStateException("No hay clientes en espera: la fila está vacía.");
        }
        Cliente dato = head.dato;  // 1) rescatar el dato antes de perder la referencia
        head = head.siguiente;     // 2) el nodo atendido queda inalcanzable -> lo recupera el GC
        if (head == null) {
            // *** CASO ESPECIAL 2: SE ATENDIÓ AL ÚLTIMO CLIENTE ***
            // head quedó en null, pero tail seguiría apuntando al nodo del cliente que
            // acabamos de atender: un nodo que YA NO forma parte de la fila y que nadie
            // puede alcanzar desde head. Hay que ponerlo en null explícitamente.
            //
            // BUG QUE PRODUCE OLVIDARSE DE ESTA LÍNEA: si la rama de "cola vacía" de
            // encolar() se escribiera mirando tail != null, el próximo cliente que llegara
            // se engancharía con tail.siguiente = nuevo DETRÁS DE UN NODO INALCANZABLE:
            // head seguiría en null, la fila seguiría pareciendo vacía y ese cliente NUNCA
            // sería llamado. Es el bug clásico de este ejercicio: el cliente "desaparece"
            // justo cuando la sucursal se vació y volvió a entrar gente. Además, dejar tail
            // colgado impide que el Garbage Collector libere el nodo del cliente atendido.
            tail = null;
        }
        size--;
        return dato;
    }

    /**
     * Consulta el cliente del frente sin atenderlo.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return el cliente que está en el frente
     * @throws IllegalStateException si la fila está vacía
     */
    Cliente verFrente() {
        if (estaVacia()) {
            throw new IllegalStateException("No hay próximo cliente: la fila está vacía.");
        }
        return head.dato;
    }

    /**
     * Indica si la fila no tiene clientes.
     *
     * <p>Se decide por head, que es el extremo por el que se sale y la referencia que
     * determina si queda alguien alcanzable.</p>
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return true si la fila está vacía
     */
    boolean estaVacia() {
        return head == null;
    }

    /**
     * Cantidad de clientes en la fila.
     *
     * <p>Complejidad: O(1) gracias al atributo size; sin él habría que recorrer: O(n).</p>
     *
     * @return número de clientes en espera
     */
    int tamanio() {
        return size;
    }

    /**
     * Informa si un cliente está en la fila, comparando por CONTENIDO con {@code equals()}.
     *
     * <p>Con objetos {@code ==} compararía identidad de referencia y daría false para dos
     * clientes distintos de contenido igual.</p>
     *
     * <p>Complejidad: O(n) - recorre la cadena. Buscar no pertenece al contrato de una cola;
     * se agrega como utilidad de inspección.</p>
     *
     * @param dato cliente a buscar; si es null devuelve false
     * @return true si algún cliente de la fila es equals() al dato
     */
    boolean contiene(Cliente dato) {
        if (dato == null) {
            return false;
        }
        Nodo actual = head;
        while (actual != null) {
            if (dato.equals(actual.dato)) {
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    /**
     * Imprime la fila desde el frente hacia el final, en formato
     * {@code FRENTE -> [T1 Ana / ...] -> ... -> null}, e informa a qué cliente apuntan head y
     * tail.
     *
     * <p>El orden impreso es el orden de ATENCIÓN, y coincide con el orden de llegada: eso es
     * FIFO.</p>
     *
     * <p>Complejidad: O(n)</p>
     */
    void imprimir() {
        StringBuilder salida = new StringBuilder("FRENTE");
        Nodo actual = head;
        while (actual != null) {
            salida.append(" -> ").append(actual.dato);
            actual = actual.siguiente;
        }
        salida.append(" -> null");
        if (estaVacia()) {
            salida.append("   (fila vacía)");
        }
        System.out.println("  " + salida);
        System.out.println("      head -> " + estadoHead() + " | tail -> " + estadoTail()
                + " | en espera = " + size);
    }

    /**
     * Devuelve a qué cliente apunta head, o la palabra null si no apunta a ningún nodo.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return representación textual del cliente del frente
     */
    String estadoHead() {
        return head == null ? "null" : head.dato.toString();
    }

    /**
     * Devuelve a qué cliente apunta tail, o la palabra null si no apunta a ningún nodo.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return representación textual del cliente del final
     */
    String estadoTail() {
        return tail == null ? "null" : tail.dato.toString();
    }

    /**
     * Indica si head y tail apuntan al MISMO nodo, lo que ocurre exactamente cuando hay un
     * solo cliente en la fila.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return true si head y tail son la misma referencia y no son null
     */
    boolean headYTailCoinciden() {
        // == a propósito: se compara IDENTIDAD de referencia ("¿es el mismo nodo?"), no
        // contenido.
        return head != null && head == tail;
    }

    /**
     * Verifica el invariante recorriendo la cadena real de nodos.
     *
     * <p>Comprueba que la cantidad de nodos alcanzables desde head coincide con size, que el
     * último alcanzable es exactamente el que apunta tail, que {@code tail.siguiente} es null
     * y que con la fila vacía las dos referencias son null.</p>
     *
     * <p>Complejidad: O(n) - control de la demostración, no parte del contrato.</p>
     *
     * @return true si la estructura es consistente
     */
    boolean verificarInvariante() {
        if (head == null) {
            return tail == null && size == 0;
        }
        int contados = 0;
        Nodo actual = head;
        Nodo ultimo = null;
        while (actual != null) {
            contados++;
            ultimo = actual;
            actual = actual.siguiente;
            if (contados > size) {
                return false; // corte de seguridad: más nodos que size indica un ciclo
            }
        }
        return contados == size && ultimo == tail && tail.siguiente == null;
    }

    /**
     * Devuelve la secuencia de turnos de la fila, del frente al final, como texto.
     *
     * <p>Sirve para comparar automáticamente el orden de llegada con el de atención sin usar
     * ninguna colección auxiliar.</p>
     *
     * <p>Complejidad: O(n)</p>
     *
     * @return cadena con los números de turno separados por coma
     */
    String secuenciaDeTurnos() {
        StringBuilder salida = new StringBuilder();
        Nodo actual = head;
        while (actual != null) {
            if (salida.length() > 0) {
                salida.append(", ");
            }
            salida.append("T").append(actual.dato.getNumeroTurno());
            actual = actual.siguiente;
        }
        return salida.toString();
    }
}

/**
 * Fila de atención de una sucursal bancaria, construida sobre la cola enlazada.
 *
 * <p><b>Correspondencia entre cada acción del enunciado y su operación de cola:</b></p>
 * <table border="1">
 *   <caption>Acciones del dominio y operaciones de cola</caption>
 *   <tr><th>Acción del enunciado</th><th>Operación de cola</th><th>Complejidad</th></tr>
 *   <tr><td>agregar cliente a la fila</td><td>{@code encolar()} por tail</td><td>O(1)</td></tr>
 *   <tr><td>atender al próximo cliente</td><td>{@code desencolar()} por head</td><td>O(1)</td></tr>
 *   <tr><td>consultar quién sigue</td><td>{@code verFrente()}</td><td>O(1)</td></tr>
 *   <tr><td>imprimir la fila actual</td><td>recorrido de head a tail</td><td>O(n)</td></tr>
 *   <tr><td>cantidad en espera</td><td>{@code tamanio()}</td><td>O(1)</td></tr>
 * </table>
 */
class FilaDeAtencion {

    /** Cola FIFO de clientes en espera. */
    private final ColaEnlazada cola;

    /** Cantidad de clientes ya atendidos, para informar el resumen de la jornada. */
    private int atendidos;

    /**
     * Crea una fila de atención vacía.
     *
     * <p>Complejidad: O(1)</p>
     */
    FilaDeAtencion() {
        this.cola = new ColaEnlazada();
        this.atendidos = 0;
    }

    /**
     * Agrega un cliente al final de la fila y le asigna su número de turno.
     *
     * <p>El turno se asigna en el constructor de {@code Cliente}; el ORDEN de atención, en
     * cambio, lo determina la posición en la que queda el nodo. Son dos cosas distintas.</p>
     *
     * <p>Complejidad: O(1)</p>
     *
     * @param nombre nombre del cliente; obligatorio
     * @param motivo motivo de la consulta; obligatorio
     * @return el cliente creado, con su número de turno ya asignado
     * @throws IllegalArgumentException si el nombre o el motivo son null o están en blanco
     */
    Cliente agregarCliente(String nombre, String motivo) {
        Cliente cliente = new Cliente(nombre, motivo);
        cola.encolar(cliente); // va al FINAL: detrás de todos los que ya estaban
        return cliente;
    }

    /**
     * Atiende al próximo cliente: lo saca del frente de la fila.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return el cliente atendido
     * @throws IllegalStateException si la fila está vacía
     */
    Cliente atenderProximo() {
        Cliente atendido = cola.desencolar();
        atendidos++;
        return atendido;
    }

    /**
     * Consulta quién sigue sin atenderlo.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return el cliente del frente
     * @throws IllegalStateException si la fila está vacía
     */
    Cliente consultarProximo() {
        return cola.verFrente();
    }

    /**
     * Imprime la fila completa con los números de turno, y el estado de head y tail.
     *
     * <p>Complejidad: O(n)</p>
     */
    void imprimirFila() {
        cola.imprimir();
    }

    /**
     * Cantidad de clientes que esperan ser atendidos.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return número de clientes en espera
     */
    int cantidadEnEspera() {
        return cola.tamanio();
    }

    /**
     * Indica si la fila está vacía.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return true si no hay nadie esperando
     */
    boolean estaVacia() {
        return cola.estaVacia();
    }

    /**
     * Cantidad de clientes atendidos desde la apertura.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return número de clientes atendidos
     */
    int cantidadAtendidos() {
        return atendidos;
    }

    /**
     * Indica si head y tail apuntan al mismo nodo (un solo cliente en la fila).
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return true si hay exactamente un cliente
     */
    boolean headYTailCoinciden() {
        return cola.headYTailCoinciden();
    }

    /**
     * Representación textual de la referencia head.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return cliente del frente o "null"
     */
    String estadoHead() {
        return cola.estadoHead();
    }

    /**
     * Representación textual de la referencia tail.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return cliente del final o "null"
     */
    String estadoTail() {
        return cola.estadoTail();
    }

    /**
     * Verifica el invariante de la cola subyacente.
     *
     * <p>Complejidad: O(n)</p>
     *
     * @return true si la estructura quedó consistente
     */
    boolean verificarInvariante() {
        return cola.verificarInvariante();
    }

    /**
     * Secuencia de turnos en espera, del frente al final.
     *
     * <p>Complejidad: O(n)</p>
     *
     * @return cadena con los turnos separados por coma
     */
    String secuenciaDeTurnos() {
        return cola.secuenciaDeTurnos();
    }

    /**
     * Informa si un cliente determinado todavía está en la fila.
     *
     * <p>Complejidad: O(n)</p>
     *
     * @param cliente cliente a buscar
     * @return true si está en espera
     */
    boolean estaEnEspera(Cliente cliente) {
        return cola.contiene(cliente);
    }
}

/**
 * Programa de demostración del ejercicio 6: cola de atención de un banco.
 *
 * <p>Simula una jornada completa: llegan cuatro clientes con motivos distintos, se consulta
 * quién sigue, se atienden dos, llegan dos más, se imprime la fila, se atiende a todos, se
 * intenta atender con la fila vacía y llega un cliente nuevo después de haberla vaciado (el
 * caso especial de tail). Después de cada operación muestra la fila con los números de turno
 * y el estado de head y tail.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio06_ColaAtencionClientes {

    /**
     * Método principal: ejecuta la simulación de la jornada.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 6 - Cola de atención de clientes (FIFO) ===");
        System.out.println("El orden lo determina la POSICIÓN en la cadena de enlaces, NO el");
        System.out.println("número de turno: ningún método compara turnos para decidir.");

        FilaDeAtencion fila = new FilaDeAtencion();

        System.out.println("\n--- Caso límite: sucursal recién abierta (fila vacía) ---");
        System.out.println("  estaVacia() = " + fila.estaVacia()
                + " | cantidadEnEspera() = " + fila.cantidadEnEspera());
        fila.imprimirFila();
        intentarAtender(fila);
        intentarConsultar(fila);

        System.out.println("\n*** CASO ESPECIAL 1: el primer cliente llega a una fila VACÍA ***");
        agregarMostrando(fila, "Ana", "Apertura de cuenta");
        System.out.println("  ¿head y tail apuntan al MISMO nodo? " + fila.headYTailCoinciden()
                + "  -> " + (fila.headYTailCoinciden()
                ? "OK (es el único nodo: primero y último a la vez)" : "FALLO"));

        System.out.println("\n--- Llegan 3 clientes más (4 en total, motivos distintos) ---");
        agregarMostrando(fila, "Bruno", "Préstamo personal");
        agregarMostrando(fila, "Carla", "Reclamo por tarjeta");
        agregarMostrando(fila, "Diego", "Transferencia al exterior");

        System.out.println("\n--- Consultar quién sigue (no lo saca de la fila) ---");
        System.out.println("  consultarProximo() = " + fila.consultarProximo()
                + "  -> el PRIMERO que llegó, no el último");
        System.out.println("  cantidadEnEspera() sigue en " + fila.cantidadEnEspera()
                + ": consultar no atiende");
        System.out.println("  Con una PILA el próximo habría sido Diego (el último en llegar),");
        System.out.println("  y Ana podría no ser atendida nunca si sigue entrando gente:");
        System.out.println("  eso es INANICIÓN, y FIFO lo vuelve imposible.");

        String ordenDeLlegada = fila.secuenciaDeTurnos();
        System.out.println("  Orden de LLEGADA registrado: " + ordenDeLlegada);

        System.out.println("\n--- Se atienden 2 clientes ---");
        String ordenDeAtencion = "";
        ordenDeAtencion = atenderMostrando(fila, ordenDeAtencion);
        ordenDeAtencion = atenderMostrando(fila, ordenDeAtencion);

        System.out.println("\n--- Llegan 2 clientes más (uno con el mismo NOMBRE que otro) ---");
        agregarMostrando(fila, "Elena", "Plazo fijo");
        agregarMostrando(fila, "Bruno", "Consulta de saldo");
        System.out.println("  Hay DOS clientes llamados Bruno: se distinguen por el número de");
        System.out.println("  turno (T2 ya fue atendido, T6 está esperando). El nombre no es");
        System.out.println("  identificador y tampoco influye en el orden.");

        System.out.println("\n--- Fila actual completa ---");
        fila.imprimirFila();
        System.out.println("  cantidadEnEspera() = " + fila.cantidadEnEspera()
                + " | atendidos hasta ahora = " + fila.cantidadAtendidos());

        System.out.println("\n--- Se atiende a TODOS los que quedan ---");
        while (!fila.estaVacia()) {
            ordenDeAtencion = atenderMostrando(fila, ordenDeAtencion);
        }

        System.out.println("\n--- Verificación: el orden de atención respeta el de llegada ---");
        System.out.println("  Secuencia de atención completa: " + ordenDeAtencion);
        boolean ordenOk = "T1, T2, T3, T4, T5, T6".equals(ordenDeAtencion);
        System.out.println("  ¿Coincide con el orden de llegada (T1..T6, turnos crecientes)? "
                + ordenOk + "  -> " + (ordenOk ? "OK" : "FALLO"));
        System.out.println("  Importante: los turnos salieron ordenados como CONSECUENCIA de");
        System.out.println("  FIFO, no como causa. El programa nunca los comparó.");

        System.out.println("\n*** CASO ESPECIAL 2: se atendió al ÚLTIMO cliente (tail vuelve a null) ***");
        System.out.println("  head -> " + fila.estadoHead() + " | tail -> " + fila.estadoTail());
        boolean tailEnNull = "null".equals(fila.estadoTail());
        System.out.println("  ¿tail volvió a null? " + tailEnNull + "  -> " + (tailEnNull ? "OK" : "FALLO"));
        System.out.println("  Si tail hubiera quedado colgado, el próximo cliente se engancharía");
        System.out.println("  detrás de un nodo inalcanzable y NUNCA sería llamado.");

        System.out.println("\n--- Caso límite: atender y consultar con la fila VACÍA otra vez ---");
        intentarAtender(fila);
        intentarConsultar(fila);

        System.out.println("\n--- Caso especial: llega un cliente DESPUÉS de haber vaciado la fila ---");
        Cliente tardio = agregarMostrando(fila, "Fabián", "Cambio de domicilio");
        System.out.println("  ¿head y tail apuntan al mismo nodo otra vez? " + fila.headYTailCoinciden());
        System.out.println("  El cliente nuevo es ACCESIBLE: consultarProximo() = "
                + fila.consultarProximo());
        System.out.println("  estaEnEspera(Fabián) = " + fila.estaEnEspera(tardio)
                + "  -> " + (fila.estaEnEspera(tardio) ? "OK, no se perdió" : "FALLO"));
        agregarMostrando(fila, "Gabriela", "Apertura de plazo fijo");
        System.out.println("  Se atiende a los dos para confirmar el orden: "
                + fila.atenderProximo() + ", " + fila.atenderProximo());
        fila.imprimirFila();

        System.out.println("\n--- Caso límite: datos obligatorios inválidos (errores forzados) ---");
        intentarAgregar(fila, null, "Consulta");
        intentarAgregar(fila, "Hugo", "  ");
        System.out.println("  La fila quedó intacta: cantidadEnEspera() = " + fila.cantidadEnEspera());

        System.out.println("\n--- Sin capacidad fija: no existe isFull() ---");
        FilaDeAtencion masiva = new FilaDeAtencion();
        for (int i = 1; i <= 1000; i++) {
            masiva.agregarCliente("Cliente" + i, "Consulta general");
        }
        System.out.println("  1000 clientes en espera sin desborde. cantidadEnEspera() = "
                + masiva.cantidadEnEspera() + " | invariante: " + masiva.verificarInvariante());
        System.out.println("  Próximo a atender: " + masiva.consultarProximo());

        System.out.println("\n=== Fin de la demostración ===");
    }

    /**
     * Agrega un cliente e informa el estado completo de la fila.
     *
     * @param fila   fila sobre la que se opera
     * @param nombre nombre del cliente
     * @param motivo motivo de la consulta
     * @return el cliente agregado
     */
    private static Cliente agregarMostrando(FilaDeAtencion fila, String nombre, String motivo) {
        Cliente cliente = fila.agregarCliente(nombre, motivo);
        System.out.println("  agregarCliente(\"" + nombre + "\", \"" + motivo
                + "\")   [encolar por tail] -> turno T" + cliente.getNumeroTurno());
        fila.imprimirFila();
        System.out.println("      invariante consistente: " + fila.verificarInvariante());
        return cliente;
    }

    /**
     * Atiende al próximo cliente e informa el estado completo de la fila.
     *
     * @param fila      fila sobre la que se opera
     * @param acumulado secuencia de turnos atendidos hasta el momento
     * @return la secuencia de atención con el turno agregado
     */
    private static String atenderMostrando(FilaDeAtencion fila, String acumulado) {
        Cliente atendido = fila.atenderProximo();
        System.out.println("  atenderProximo()   [desencolar por head] = " + atendido);
        fila.imprimirFila();
        System.out.println("      invariante consistente: " + fila.verificarInvariante());
        String turno = "T" + atendido.getNumeroTurno();
        return acumulado.isEmpty() ? turno : acumulado + ", " + turno;
    }

    /**
     * Intenta atender capturando la excepción esperada cuando la fila está vacía.
     *
     * @param fila fila sobre la que se opera
     */
    private static void intentarAtender(FilaDeAtencion fila) {
        try {
            System.out.println("  atenderProximo() = " + fila.atenderProximo());
        } catch (IllegalStateException e) {
            System.out.println("  atenderProximo() lanzó IllegalStateException: " + e.getMessage()
                    + "  -> OK");
        }
    }

    /**
     * Intenta consultar al próximo capturando la excepción esperada con la fila vacía.
     *
     * @param fila fila sobre la que se opera
     */
    private static void intentarConsultar(FilaDeAtencion fila) {
        try {
            System.out.println("  consultarProximo() = " + fila.consultarProximo());
        } catch (IllegalStateException e) {
            System.out.println("  consultarProximo() lanzó IllegalStateException: " + e.getMessage()
                    + "  -> OK");
        }
    }

    /**
     * Intenta agregar un cliente con datos inválidos capturando la excepción esperada.
     *
     * @param fila   fila sobre la que se opera
     * @param nombre nombre a probar
     * @param motivo motivo a probar
     */
    private static void intentarAgregar(FilaDeAtencion fila, String nombre, String motivo) {
        try {
            fila.agregarCliente(nombre, motivo);
            System.out.println("  agregarCliente no lanzó excepción -> FALLO");
        } catch (IllegalArgumentException e) {
            System.out.println("  agregarCliente(" + (nombre == null ? "null" : "\"" + nombre + "\"")
                    + ", \"" + motivo + "\") lanzó IllegalArgumentException: " + e.getMessage()
                    + "  -> OK");
        }
    }
}

/*
MATRIZ DE CASOS DE PRUEBA MANUALES
Compilación: javac Ejercicio06_ColaAtencionClientes.java -d /tmp/v6/ej06
Ejecución:   java -cp /tmp/v6/ej06 Ejercicio06_ColaAtencionClientes
Todos los casos están cableados en main(); el programa no lee entrada por consola.

ID    | Objetivo                                                  | Entrada exacta                                                          | Resultado esperado                                                            | Criterio de aprobación
------+-----------------------------------------------------------+-------------------------------------------------------------------------+-------------------------------------------------------------------------------+------------------------------------------------------------------
CP-01 | Flujo nominal FIFO: se atiende en orden de llegada        | llegan Ana, Bruno, Carla, Diego, Elena y Bruno(2); se atiende a todos    | secuencia de atención "T1, T2, T3, T4, T5, T6"                                | La línea "¿Coincide con el orden de llegada...?" informa true y "-> OK"
CP-02 | Estructura vacía: atender y consultar sin clientes        | fila recién creada, atenderProximo() y consultarProximo()               | IllegalStateException "No hay clientes en espera..." y "No hay próximo cliente..." | Se imprimen los dos mensajes seguidos de "-> OK" y el programa continúa
CP-03 | Un solo cliente: head y tail son el MISMO nodo            | agregarCliente("Ana","Apertura de cuenta") sobre la fila vacía          | headYTailCoinciden() = true, head -> [T1 Ana / ...], tail igual                | La línea del caso especial 1 informa true y "-> OK"
CP-04 | Vaciar la fila por completo y que llegue alguien después  | atender a los 6 y luego agregarCliente("Fabián","Cambio de domicilio")  | tail vuelve a null; después consultarProximo() = [T7 Fabián / ...] y estaEnEspera = true | "¿tail volvió a null?" informa true y estaEnEspera(Fabián) informa true
CP-05 | Datos obligatorios inválidos (errores forzados)           | agregarCliente(null,"Consulta") y agregarCliente("Hugo","  ")           | IllegalArgumentException "El nombre del cliente es obligatorio." y "El motivo de consulta es obligatorio." | Se imprimen los dos mensajes seguidos de "-> OK" y cantidadEnEspera() no cambia
*/
