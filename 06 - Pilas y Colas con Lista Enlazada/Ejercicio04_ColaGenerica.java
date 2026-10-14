/*
 * ============================================================================
 * Ejercicio 4 - Cola genérica
 * Práctico 6 - Pilas y Colas con Listas Enlazadas
 * ============================================================================
 *
 * Prompt inicial utilizado (transcripción literal del prompt del Agente Analista):
 *
 *   Analizá la transformación de la cola enlazada de enteros en una COLA GENÉRICA Cola<T>,
 *   que debe probarse con una cola de nombres de personas y con una cola de objetos Cliente.
 *   El punto central: explicá CÓMO SE MANTIENE EL COMPORTAMIENTO FIFO INDEPENDIENTEMENTE
 *   DEL TIPO DE DATO. Desarrollá:
 *   - qué garantiza el orden FIFO: la POSICIÓN del nodo en la cadena de enlaces y las
 *     referencias head y tail. Nada de eso depende de qué contiene el nodo. Arrancá por acá;
 *   - por eso encolar siempre engancha el nodo nuevo después de tail, y desencolar siempre
 *     toma el nodo de head, sin importar si el dato es un String o un Cliente. El orden
 *     está codificado en la ESTRUCTURA, no en los datos. Desarrollá este punto, es lo que
 *     evalúa el ejercicio;
 *   - hacé una distinción importante: FIFO ordena por ORDEN DE LLEGADA, no por ninguna
 *     propiedad del dato. Explicá explícitamente que una cola NO compara ni ordena sus
 *     elementos, y que por eso es indiferente al tipo. Contrastá con una cola de PRIORIDAD,
 *     que sí necesita comparar los datos entre sí y por lo tanto sí depende del tipo
 *     (exigiría Comparable o un Comparator). Esta observación es la más valiosa del
 *     ejercicio, incluila;
 *   - qué cambia al hacerla genérica y qué no cambia, con una lista concreta;
 *   - la excepción del método contiene(), que compara datos y necesita equals() en lugar
 *     de ==;
 *   - por qué los dos casos especiales de la cola (encolar en cola vacía, desencolar el
 *     último elemento con tail a null) siguen siendo exactamente los mismos y no se ven
 *     afectados por los genéricos;
 *   - qué pasa con el autoboxing;
 *   - por qué las complejidades son idénticas a la versión con int.
 *   Casos especiales: los mismos de la cola de enteros, más almacenar null y objetos sin
 *   equals().
 *   No escribas la implementación.
 *
 * Ajustes realizados posteriormente al prompt inicial:
 *   1) Igual que en el ejercicio 3, el prompt dejaba abierto si se permite almacenar null.
 *      Se resolvió RECHAZARLO en encolar() con IllegalArgumentException, por las mismas
 *      razones: desencolar() devuelve T y un null guardado sería indistinguible de "no hay
 *      dato", y contiene(null) obligaría a una rama especial.
 *   2) Se agregó tamanio() con atributo size y el método verificarInvariante(), igual que
 *      en el ejercicio 2.
 *   3) Se agregó una verificación automática que compara el orden de entrada con el de
 *      salida construyendo las dos secuencias como texto, para que el criterio de
 *      aceptación "el orden de salida coincide EXACTAMENTE con el de entrada" quede
 *      demostrado en la salida y no sólo afirmado.
 *   No hubo ajustes sobre la estructura: head, tail y los dos casos especiales quedaron
 *   IDÉNTICOS a los del ejercicio 2, que es justamente lo que el análisis predecía (los
 *   genéricos no afectan el manejo de las referencias).
 *
 * Estrategia: la misma cola enlazada del ejercicio 2 parametrizada con un tipo T, porque el
 * orden FIFO está determinado por la posición del nodo en la cadena y no por el dato.
 *
 * Complejidad esperada de las operaciones principales (IDÉNTICAS a la versión con int):
 *   encolar   O(1)        desencolar O(1)       verFrente O(1)
 *   estaVacia O(1)        tamanio    O(1)       contiene  O(n)       imprimir O(n)
 *
 * CÓMO SE MANTIENE FIFO INDEPENDIENTEMENTE DEL TIPO DE DATO. Lo que garantiza el orden es
 * la POSICIÓN del nodo en la cadena de enlaces, más las referencias head y tail. Nada de
 * eso depende de qué contiene el nodo: encolar SIEMPRE engancha el nodo nuevo después de
 * tail y desencolar SIEMPRE toma el nodo de head, sea el dato un String o un Cliente. El
 * orden está codificado en la ESTRUCTURA, no en los datos.
 *
 * DISTINCIÓN CLAVE: UNA COLA NO COMPARA NI ORDENA SUS ELEMENTOS. FIFO ordena por ORDEN DE
 * LLEGADA, que es una propiedad del momento en que el elemento entró y no una propiedad del
 * elemento. Por eso la cola puede ser indiferente al tipo: nunca necesita preguntar cuál de
 * dos datos "va antes". Contrastá con una COLA DE PRIORIDAD: esa sí necesita comparar los
 * datos entre sí para decidir a quién atender, así que sí depende del tipo y exigiría que T
 * implemente Comparable<T> (o recibir un Comparator<T>). Una Cola<T> sin restricciones sobre
 * T sólo es posible porque FIFO no compara nada.
 *
 * QUÉ CAMBIA Y QUÉ NO AL HACERLA GENÉRICA:
 *   NO cambia: encolar, desencolar, verFrente, estaVacia, tamanio, imprimir, el invariante
 *              head/tail, los DOS casos especiales (encolar en cola vacía y desencolar el
 *              último elemento con tail a null) y todas las complejidades.
 *   SÍ cambia: la declaración de las clases (Nodo<T>, Cola<T>), el tipo de retorno de
 *              desencolar() y verFrente() (T en lugar de int, sin casting en el llamador),
 *              y el método contiene(), que pasa de == a equals() porque es el único que
 *              INSPECCIONA el dato en lugar de manipular referencias.
 *
 * AUTOBOXING: Cola<Integer> y no Cola<int>, porque un parámetro de tipo sólo admite tipos
 * por referencia. Java convierte automáticamente al encolar y al leer.
 *
 * DIFERENCIA CON EL PRÁCTICO 4 (cola sobre arreglo): acá NO EXISTE isFull() y tampoco
 * existe el desperdicio de espacio que obliga a la aritmética circular (frente = (frente+1)
 * % capacidad). Los nodos se crean al encolar y quedan inalcanzables al desencolar, así que
 * no hay posiciones que queden inutilizables. Además, una cola genérica sobre arreglo
 * necesitaría el truco del Object[] con @SuppressWarnings, porque new T[] es ilegal; acá no,
 * porque cada nodo guarda un solo campo de tipo T.
 *
 * @author Micaela Gimenez
 * @version 1.0
 */

/**
 * Nodo genérico de la lista simplemente enlazada que soporta la cola.
 *
 * <p>Una sola referencia, en el sentido frente -&gt; final. El nodo no sabe nada sobre el
 * dato que guarda más allá de su tipo: eso es lo que permite que la cola sea indiferente al
 * contenido.</p>
 *
 * @param <T> tipo del dato almacenado
 */
class Nodo<T> {

    /** Dato almacenado. Nunca es null: encolar() rechaza null. */
    T dato;

    /** Referencia al nodo que está detrás en la fila; null si es el último. */
    Nodo<T> siguiente;

    /**
     * Crea un nodo suelto, todavía no enlazado a ninguna cola.
     *
     * @param dato valor a almacenar
     */
    Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }
}

/**
 * Cola genérica (política FIFO) implementada sobre una lista simplemente enlazada con
 * {@code head} (frente, por donde se sale) y {@code tail} (final, por donde se entra).
 *
 * <p>El orden de atención lo determina la <b>posición</b> del nodo en la cadena, no ninguna
 * propiedad del dato. Por eso la clase no impone ninguna restricción sobre {@code T}: no
 * necesita comparar elementos. Una cola de PRIORIDAD sí la necesitaría
 * ({@code T extends Comparable<T>} o un {@code Comparator<T>}).</p>
 *
 * <p>No existe isFull(): no hay capacidad fija.</p>
 *
 * @param <T> tipo de los elementos almacenados
 */
class Cola<T> {

    /** Frente de la cola: por acá SALEN los elementos. null si está vacía. */
    private Nodo<T> head;

    /** Final de la cola: por acá ENTRAN los elementos. null si está vacía. */
    private Nodo<T> tail;

    /** Cantidad de elementos, para que tamanio() sea O(1). */
    private int size;

    /**
     * Crea una cola vacía.
     *
     * <p>Complejidad: O(1)</p>
     */
    Cola() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Encola un elemento, que pasa a ser el último de la fila.
     *
     * <p>Observar que el método no inspecciona el dato ni lo compara con nada: sólo lo
     * envuelve en un nodo y engancha ese nodo detrás de {@code tail}. Ahí está la razón de
     * que FIFO funcione igual para cualquier tipo.</p>
     *
     * <p>Complejidad: O(1) gracias a tail. Sin tail habría que recorrer la cadena para
     * llegar al último nodo: O(n).</p>
     *
     * @param dato elemento a encolar; no puede ser null
     * @throws IllegalArgumentException si el dato es null
     */
    void encolar(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException("No se admiten elementos null en la cola.");
        }
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            // *** CASO ESPECIAL 1: ENCOLAR EN COLA VACÍA ***
            // Idéntico al del ejercicio 2: los genéricos no lo afectan en nada, porque es
            // un problema de REFERENCIAS y no de datos. No hay último nodo al que
            // engancharse, así que head y tail deben apuntar LOS DOS al nodo nuevo.
            head = nuevo;
            tail = nuevo;
        } else {
            tail.siguiente = nuevo; // 1) enganchar detrás del último actual
            tail = nuevo;           // 2) después mover tail
        }
        size++;
    }

    /**
     * Quita y devuelve el elemento del frente: el que llegó primero.
     *
     * <p>Devuelve {@code T} directamente, sin casting en el llamador. Con una cola de
     * {@code Object} habría que escribir {@code (Cliente) cola.desencolar()} en cada uso y
     * el error de tipo aparecería en ejecución como {@code ClassCastException}.</p>
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return el elemento que estaba en el frente
     * @throws IllegalStateException si la cola está vacía
     */
    T desencolar() {
        if (estaVacia()) {
            throw new IllegalStateException("No se puede desencolar: la cola está vacía.");
        }
        T dato = head.dato;
        head = head.siguiente; // el nodo desencolado queda inalcanzable -> lo recupera el GC
        if (head == null) {
            // *** CASO ESPECIAL 2: SE DESENCOLÓ EL ÚLTIMO ELEMENTO ***
            // tail seguiría apuntando a un nodo que ya no forma parte de la cola. Hay que
            // ponerlo en null explícitamente.
            // BUG QUE PRODUCE OLVIDARSE: el próximo encolar engancharía el elemento nuevo
            // DETRÁS DE UN NODO INALCANZABLE (head seguiría en null), la cola seguiría
            // pareciendo vacía y ese elemento se perdería. También impediría que el GC
            // liberara el nodo retenido.
            // Este caso es EXACTAMENTE el mismo que en la versión con int: es un problema
            // de referencias, así que los genéricos no lo cambian ni lo agravan.
            tail = null;
        }
        size--;
        return dato;
    }

    /**
     * Consulta el elemento del frente sin quitarlo: informa a quién le toca.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return el elemento del frente
     * @throws IllegalStateException si la cola está vacía
     */
    T verFrente() {
        if (estaVacia()) {
            throw new IllegalStateException("No se puede consultar el frente: la cola está vacía.");
        }
        return head.dato;
    }

    /**
     * Indica si la cola no tiene elementos.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return true si está vacía
     */
    boolean estaVacia() {
        return head == null;
    }

    /**
     * Informa si un elemento está en la cola, comparando por CONTENIDO.
     *
     * <p>ÚNICO método cuya lógica cambió respecto de la versión con {@code int}, porque es
     * el único que INSPECCIONA el dato en lugar de manipular referencias. Con {@code int}
     * se usaba {@code ==} (compara valores); con objetos {@code ==} compararía IDENTIDAD DE
     * REFERENCIA y daría false para dos objetos distintos de contenido igual. Por eso
     * {@code equals()}, y por eso la clase {@code Cliente} lo sobrescribe.</p>
     *
     * <p>Complejidad: O(n)</p>
     *
     * @param dato elemento a buscar; si es null siempre devuelve false
     * @return true si algún elemento de la cola es equals() al dato
     */
    boolean contiene(T dato) {
        if (dato == null) {
            return false; // la cola no admite nulls, así que nunca puede estar
        }
        Nodo<T> actual = head;
        while (actual != null) {
            // equals() y NO ==: comparación por contenido.
            if (dato.equals(actual.dato)) {
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    /**
     * Cantidad de elementos en la cola.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return número de elementos
     */
    int tamanio() {
        return size;
    }

    /**
     * Imprime la cola desde el frente hacia el final, en formato
     * {@code FRENTE -> 10 -> 20 -> 30 -> null}, e informa a qué dato apuntan head y tail.
     *
     * <p>El orden impreso es el orden de SALIDA y coincide con el de entrada: eso es FIFO.
     * Cada elemento se muestra por su {@code toString()}: la cola no sabe cómo se
     * representa el dato, se lo pregunta al objeto.</p>
     *
     * <p>Complejidad: O(n)</p>
     */
    void imprimir() {
        StringBuilder salida = new StringBuilder("FRENTE");
        Nodo<T> actual = head;
        while (actual != null) {
            salida.append(" -> ").append(actual.dato);
            actual = actual.siguiente;
        }
        salida.append(" -> null");
        if (estaVacia()) {
            salida.append("   (cola vacía)");
        }
        System.out.println("  " + salida);
        System.out.println("      head -> " + estadoHead() + " | tail -> " + estadoTail()
                + " | tamanio = " + size);
    }

    /**
     * Representación textual del dato del frente, o la palabra null si head no apunta a
     * ningún nodo.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return estado de la referencia head
     */
    String estadoHead() {
        return head == null ? "null" : String.valueOf(head.dato);
    }

    /**
     * Representación textual del dato del final, o la palabra null si tail no apunta a
     * ningún nodo.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return estado de la referencia tail
     */
    String estadoTail() {
        return tail == null ? "null" : String.valueOf(tail.dato);
    }

    /**
     * Indica si head y tail apuntan al MISMO nodo (cola de un solo elemento).
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return true si head y tail son la misma referencia y no son null
     */
    boolean headYTailCoinciden() {
        // == a propósito: acá SÍ interesa la identidad de referencia ("¿es el mismo nodo?")
        // y no el contenido.
        return head != null && head == tail;
    }

    /**
     * Verifica el invariante recorriendo la cadena real: cantidad de nodos igual a size, y
     * tail igual al último nodo alcanzable desde head con siguiente en null.
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
        Nodo<T> actual = head;
        Nodo<T> ultimo = null;
        while (actual != null) {
            contados++;
            ultimo = actual;
            actual = actual.siguiente;
            if (contados > size) {
                return false; // corte de seguridad ante un posible ciclo
            }
        }
        return contados == size && ultimo == tail && tail.siguiente == null;
    }
}

/**
 * Cliente de prueba para la cola de objetos propios.
 *
 * <p>Sobrescribe {@code toString()} para que {@code imprimir()} muestre algo legible y
 * {@code equals()}/{@code hashCode()} para que {@code contiene()} compare por CONTENIDO.
 * Nótese que <b>NO</b> implementa {@code Comparable}: una cola FIFO no lo necesita porque
 * nunca compara dos clientes entre sí. Para usarlo en una cola de PRIORIDAD habría que
 * agregarle ese criterio de orden.</p>
 */
class Cliente {

    /** Nombre del cliente. */
    private final String nombre;

    /** Número de cuenta del cliente. */
    private final String numeroCuenta;

    /**
     * Crea un cliente.
     *
     * @param nombre       nombre del cliente
     * @param numeroCuenta número de cuenta
     * @throws IllegalArgumentException si alguno de los campos es null o está en blanco
     */
    Cliente(String nombre, String numeroCuenta) {
        if (nombre == null || nombre.isBlank() || numeroCuenta == null || numeroCuenta.isBlank()) {
            throw new IllegalArgumentException("Nombre y número de cuenta son obligatorios.");
        }
        this.nombre = nombre;
        this.numeroCuenta = numeroCuenta;
    }

    /**
     * Representación textual del cliente.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return cadena con nombre y número de cuenta
     */
    @Override
    public String toString() {
        return nombre + "(cta " + numeroCuenta + ")";
    }

    /**
     * Compara dos clientes por CONTENIDO: iguales si coinciden nombre y número de cuenta.
     *
     * <p>Complejidad: O(k) sobre la longitud de las cadenas.</p>
     *
     * @param obj objeto a comparar
     * @return true si obj es un Cliente con el mismo nombre y número de cuenta
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
        return nombre.equals(otro.nombre) && numeroCuenta.equals(otro.numeroCuenta);
    }

    /**
     * Código hash consistente con equals().
     *
     * <p>Complejidad: O(k)</p>
     *
     * @return hash derivado de nombre y número de cuenta
     */
    @Override
    public int hashCode() {
        return 31 * nombre.hashCode() + numeroCuenta.hashCode();
    }
}

/**
 * Programa de demostración del ejercicio 4: cola genérica probada con una Cola&lt;String&gt;
 * de nombres de personas y una Cola&lt;Cliente&gt; de objetos propios.
 *
 * <p>En cada cola se verifica automáticamente que el orden de salida coincide EXACTAMENTE
 * con el orden de entrada, y se cubren los dos casos especiales de head y tail.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio04_ColaGenerica {

    /**
     * Método principal: ejecuta la demostración completa.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 4 - Cola genérica Cola<T> (FIFO) ===");

        boolean ordenNombres = probarColaDeNombres();
        boolean ordenClientes = probarColaDeClientes();

        System.out.println("\n--- Comparación de los dos patrones de orden ---");
        System.out.println("  Cola<String>  conservó el orden de entrada: " + ordenNombres);
        System.out.println("  Cola<Cliente> conservó el orden de entrada: " + ordenClientes);
        System.out.println("  El patrón es IDÉNTICO en las dos: el orden lo da la posición en");
        System.out.println("  la cadena de enlaces, no el tipo ni el contenido del dato.");
        System.out.println("  -> " + (ordenNombres && ordenClientes ? "OK" : "FALLO"));

        demostrarIgualdadPorContenido();
        probarCasosEspecialesDeReferencias();
        probarCasosLimite();
        explicarColaDePrioridad();

        System.out.println("\n=== Fin de la demostración ===");
    }

    /**
     * Prueba una Cola&lt;String&gt; con nombres de personas, verificando que el orden de
     * salida coincida con el de entrada.
     *
     * @return true si el orden de salida fue idéntico al de entrada
     */
    private static boolean probarColaDeNombres() {
        System.out.println("\n--- Cola<String>: nombres de personas ---");
        Cola<String> cola = new Cola<>();

        String[] ordenDeEntradaTexto = null; // no se usan arreglos para almacenar: sólo texto
        String entrada = "";
        String[] nombres = null;

        // Se encolan de a uno, acumulando el orden de entrada como TEXTO (no se usa ninguna
        // colección ni arreglo para guardar los datos: la única estructura es la cola).
        entrada = encolarMostrando(cola, "Ana", entrada);
        entrada = encolarMostrando(cola, "Bruno", entrada);
        entrada = encolarMostrando(cola, "Carla", entrada);
        entrada = encolarMostrando(cola, "Diego", entrada);

        System.out.println("  verFrente() = " + cola.verFrente()
                + " -> la primera que llegó, no la última (FIFO)");
        System.out.println("  contiene(\"Carla\") = " + cola.contiene("Carla")
                + " (equals() de String compara contenido)");
        System.out.println("  contiene(\"Elena\") = " + cola.contiene("Elena"));

        System.out.println("  Orden de ENTRADA: " + entrada);
        String salida = "";
        while (!cola.estaVacia()) {
            salida = desencolarMostrando(cola, salida);
        }
        System.out.println("  Orden de SALIDA:  " + salida);
        boolean coincide = entrada.equals(salida);
        System.out.println("  ¿Coinciden EXACTAMENTE? " + coincide + "  -> " + (coincide ? "OK" : "FALLO"));
        return coincide;
    }

    /**
     * Prueba una Cola&lt;Cliente&gt; con objetos propios, verificando que el orden de salida
     * coincida con el de entrada.
     *
     * @return true si el orden de salida fue idéntico al de entrada
     */
    private static boolean probarColaDeClientes() {
        System.out.println("\n--- Cola<Cliente>: objetos propios (misma clase Cola<T>) ---");
        Cola<Cliente> cola = new Cola<>();

        String entrada = "";
        entrada = encolarClienteMostrando(cola, new Cliente("Ana", "1001"), entrada);
        entrada = encolarClienteMostrando(cola, new Cliente("Bruno", "1002"), entrada);
        entrada = encolarClienteMostrando(cola, new Cliente("Carla", "1003"), entrada);

        System.out.println("  verFrente() = " + cola.verFrente());
        // Cliente DISTINTO con el mismo contenido: lo encuentra sólo por equals().
        System.out.println("  contiene(new Cliente(\"Bruno\", \"1002\")) = "
                + cola.contiene(new Cliente("Bruno", "1002"))
                + " (objeto distinto, mismo contenido -> equals())");
        System.out.println("  contiene(new Cliente(\"Bruno\", \"9999\")) = "
                + cola.contiene(new Cliente("Bruno", "9999")) + " (otra cuenta -> no es igual)");

        System.out.println("  Orden de ENTRADA: " + entrada);
        String salida = "";
        while (!cola.estaVacia()) {
            salida = desencolarClienteMostrando(cola, salida);
        }
        System.out.println("  Orden de SALIDA:  " + salida);
        boolean coincide = entrada.equals(salida);
        System.out.println("  ¿Coinciden EXACTAMENTE? " + coincide + "  -> " + (coincide ? "OK" : "FALLO"));
        return coincide;
    }

    /**
     * Encola un nombre, muestra el estado y acumula el orden de entrada como texto.
     *
     * @param cola       cola sobre la que se opera
     * @param nombre     nombre a encolar
     * @param acumulado  secuencia de entrada acumulada hasta el momento
     * @return la secuencia de entrada con el nombre agregado
     */
    private static String encolarMostrando(Cola<String> cola, String nombre, String acumulado) {
        cola.encolar(nombre);
        System.out.println("  encolar(\"" + nombre + "\")");
        cola.imprimir();
        return acumulado.isEmpty() ? nombre : acumulado + ", " + nombre;
    }

    /**
     * Desencola un nombre, muestra el estado y acumula el orden de salida como texto.
     *
     * @param cola      cola sobre la que se opera
     * @param acumulado secuencia de salida acumulada hasta el momento
     * @return la secuencia de salida con el nombre agregado
     */
    private static String desencolarMostrando(Cola<String> cola, String acumulado) {
        String nombre = cola.desencolar();
        System.out.println("  desencolar() = \"" + nombre + "\"  | invariante: "
                + cola.verificarInvariante() + " | head -> " + cola.estadoHead()
                + " | tail -> " + cola.estadoTail());
        return acumulado.isEmpty() ? nombre : acumulado + ", " + nombre;
    }

    /**
     * Encola un cliente, muestra el estado y acumula el orden de entrada como texto.
     *
     * @param cola      cola sobre la que se opera
     * @param cliente   cliente a encolar
     * @param acumulado secuencia de entrada acumulada
     * @return la secuencia de entrada con el cliente agregado
     */
    private static String encolarClienteMostrando(Cola<Cliente> cola, Cliente cliente, String acumulado) {
        cola.encolar(cliente);
        System.out.println("  encolar(" + cliente + ")");
        cola.imprimir();
        return acumulado.isEmpty() ? cliente.toString() : acumulado + ", " + cliente;
    }

    /**
     * Desencola un cliente, muestra el estado y acumula el orden de salida como texto.
     *
     * @param cola      cola sobre la que se opera
     * @param acumulado secuencia de salida acumulada
     * @return la secuencia de salida con el cliente agregado
     */
    private static String desencolarClienteMostrando(Cola<Cliente> cola, String acumulado) {
        Cliente cliente = cola.desencolar(); // sin casting: desencolar() devuelve Cliente
        System.out.println("  desencolar() = " + cliente + "  | invariante: "
                + cola.verificarInvariante() + " | head -> " + cola.estadoHead()
                + " | tail -> " + cola.estadoTail());
        return acumulado.isEmpty() ? cliente.toString() : acumulado + ", " + cliente;
    }

    /**
     * Demuestra la diferencia entre {@code ==} y {@code equals()} con dos clientes de igual
     * contenido creados por separado.
     */
    private static void demostrarIgualdadPorContenido() {
        System.out.println("\n--- Prueba explícita: == contra equals() ---");
        Cliente a = new Cliente("Ana", "1001");
        Cliente b = new Cliente("Ana", "1001"); // objeto DISTINTO, contenido IGUAL
        Cliente mismaReferencia = a;

        System.out.println("  Cliente a = " + a);
        System.out.println("  Cliente b = " + b + "   (creado por separado con new)");
        System.out.println("  a == b               -> " + (a == b)
                + "  (false: son DOS objetos distintos; == compara identidad de referencia)");
        System.out.println("  a.equals(b)          -> " + a.equals(b)
                + "   (true: equals() compara nombre y cuenta, es decir CONTENIDO)");
        System.out.println("  a == mismaReferencia -> " + (a == mismaReferencia)
                + "   (true: misma referencia)");

        Cola<Cliente> cola = new Cola<>();
        cola.encolar(a);
        System.out.println("  Se encola 'a'. contiene(b) = " + cola.contiene(b)
                + " -> con equals() SÍ lo encuentra. Con == habría dado false.");

        // Clase SIN equals() sobrescrito: Object.equals() compara identidad.
        Cola<Object> colaSinEquals = new Cola<>();
        Object o1 = new Object();
        Object o2 = new Object();
        colaSinEquals.encolar(o1);
        System.out.println("  Clase SIN equals() sobrescrito: contiene(otro) = "
                + colaSinEquals.contiene(o2) + " | contiene(el mismo) = "
                + colaSinEquals.contiene(o1));
    }

    /**
     * Prueba los dos casos especiales de head y tail, que son exactamente los mismos que en
     * la versión con int: los genéricos no los afectan.
     */
    private static void probarCasosEspecialesDeReferencias() {
        System.out.println("\n--- Los DOS casos especiales de head y tail (iguales que con int) ---");
        Cola<Cliente> cola = new Cola<>();

        System.out.println("  *** CASO ESPECIAL 1: encolar en cola VACÍA ***");
        cola.encolar(new Cliente("Único", "2001"));
        cola.imprimir();
        System.out.println("  ¿head y tail apuntan al MISMO nodo? " + cola.headYTailCoinciden()
                + "  -> " + (cola.headYTailCoinciden() ? "OK" : "FALLO"));

        System.out.println("  *** CASO ESPECIAL 2: desencolar el ÚLTIMO elemento ***");
        System.out.println("  desencolar() = " + cola.desencolar());
        cola.imprimir();
        boolean tailEnNull = "null".equals(cola.estadoTail());
        System.out.println("  ¿tail volvió a null? " + tailEnNull + "  -> " + (tailEnNull ? "OK" : "FALLO"));

        System.out.println("  Cola vaciada por completo; se vuelve a encolar:");
        cola.encolar(new Cliente("Nuevo", "2002"));
        cola.imprimir();
        System.out.println("  El elemento nuevo es ACCESIBLE: verFrente() = " + cola.verFrente()
                + " | contiene(new Cliente(\"Nuevo\", \"2002\")) = "
                + cola.contiene(new Cliente("Nuevo", "2002")));
        System.out.println("  (Si tail hubiera quedado colgado, este elemento se habría perdido.)");
    }

    /**
     * Recorre los casos límite: cola vacía, desencolar y verFrente sin elementos, rechazo de
     * null y Cola&lt;Integer&gt; con autoboxing.
     */
    private static void probarCasosLimite() {
        System.out.println("\n--- Casos límite ---");
        Cola<String> cola = new Cola<>();
        System.out.println("  Cola vacía: estaVacia() = " + cola.estaVacia()
                + " | tamanio() = " + cola.tamanio());
        cola.imprimir();

        try {
            cola.desencolar();
        } catch (IllegalStateException e) {
            System.out.println("  desencolar() con cola vacía lanzó: " + e.getMessage() + "  -> OK");
        }
        try {
            cola.verFrente();
        } catch (IllegalStateException e) {
            System.out.println("  verFrente() con cola vacía lanzó: " + e.getMessage() + "  -> OK");
        }
        try {
            cola.encolar(null);
        } catch (IllegalArgumentException e) {
            System.out.println("  encolar(null) lanzó: " + e.getMessage() + "  -> OK");
        }
        System.out.println("  contiene(null) = " + cola.contiene(null) + " (sin recorrer)");

        System.out.println("  Cola<Integer> con autoboxing (no existe Cola<int>):");
        Cola<Integer> numeros = new Cola<>();
        numeros.encolar(1);   // autoboxing: int -> Integer
        numeros.encolar(2);
        numeros.encolar(3);
        numeros.imprimir();
        int primero = numeros.desencolar(); // unboxing al asignar a int
        System.out.println("  desencolar() = " + primero + " (el primero que entró)");
    }

    /**
     * Explica, sin implementarla, por qué una cola de prioridad sí dependería del tipo.
     *
     * <p>Es la observación central del ejercicio: FIFO no compara elementos, y por eso
     * {@code Cola<T>} no impone ninguna restricción sobre {@code T}.</p>
     */
    private static void explicarColaDePrioridad() {
        System.out.println("\n--- Por qué una cola FIFO es indiferente al tipo y una de prioridad no ---");
        System.out.println("  Esta Cola<T> nunca pregunta cuál de dos datos 'va antes': el orden lo");
        System.out.println("  determina la POSICIÓN del nodo en la cadena, que depende sólo del momento");
        System.out.println("  en que el elemento entró. Por eso T no tiene ninguna restricción.");
        System.out.println("  Una COLA DE PRIORIDAD sí tendría que comparar los datos entre sí, así que");
        System.out.println("  la declaración pasaría a ser algo como Cola<T extends Comparable<T>> o");
        System.out.println("  recibiría un Comparator<T> en el constructor. A Cliente habría que");
        System.out.println("  agregarle entonces un criterio de orden (por ejemplo implementar");
        System.out.println("  Comparable<Cliente> comparando una categoría de prioridad), y encolar");
        System.out.println("  dejaría de ser O(1) porque habría que buscar la posición de inserción.");
    }
}


