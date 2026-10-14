/*
 * ============================================================================
 * Ejercicio 2 - Cola enlazada de enteros
 * Práctico 6 - Pilas y Colas con Listas Enlazadas
 * ============================================================================
 *
 * Prompt inicial utilizado (transcripción literal del prompt del Agente Analista):
 *
 *   Analizá el diseño de una COLA implementada con lista enlazada simple, que debe
 *   permitir encolar, desencolar, consultar el frente, verificar si está vacía, buscar si
 *   contiene un valor e imprimir su contenido.
 *   El punto central: explicá POR QUÉ LA COLA NECESITA HEAD Y TAIL, y QUÉ PROBLEMA
 *   APARECERÍA SI SOLAMENTE SE USARA HEAD. Desarrollá:
 *   - qué estructura se necesita y por qué corresponde una cola (política FIFO);
 *   - que FIFO exige operar en los DOS EXTREMOS: se saca por un lado y se agrega por el
 *     otro. Esta es la diferencia esencial con la pila, que opera en un solo extremo.
 *     Arrancá por acá;
 *   - por qué el frente debe ser head: desencolar tiene que ser O(1), y en una lista
 *     simplemente enlazada eliminar el ÚLTIMO nodo es caro (hace falta el nodo anterior,
 *     que no se puede alcanzar sin recorrer). Entonces el extremo "de salida" tiene que
 *     ser head;
 *   - por qué, en consecuencia, el extremo "de entrada" es el final de la lista, y por qué
 *     sin una referencia tail cada encolar tendría que RECORRER toda la lista para llegar
 *     al último nodo, pasando de O(1) a O(n). Desarrollá este punto con detalle, es lo que
 *     evalúa el ejercicio;
 *   - qué hace exactamente tail: mantiene una referencia directa al último nodo,
 *     convirtiendo encolar en O(1) a cambio de un solo atributo extra. Explicá que es un
 *     intercambio de memoria constante por tiempo;
 *   - los DOS CASOS ESPECIALES que surgen de tener dos referencias, y que son la fuente de
 *     errores de este ejercicio:
 *     1) encolar en una cola VACÍA: no hay último nodo, así que head y tail deben apuntar
 *        los dos al nodo nuevo;
 *     2) desencolar el ÚLTIMO elemento: head queda en null, pero tail quedaría apuntando a
 *        un nodo que ya no forma parte de la cola. Hay que ponerlo en null explícitamente.
 *        Explicá qué bug produce olvidarse de esto (el próximo encolar se engancharía a un
 *        nodo inalcanzable y el elemento se perdería);
 *   - el invariante que relaciona head, tail y la cadena de nodos;
 *   - la complejidad esperada de cada operación;
 *   - la ventaja sobre la cola con arreglo: no hay capacidad fija y, sobre todo, NO EXISTE
 *     el problema del desperdicio de espacio que obliga a la aritmética circular. Explicá
 *     por qué: los nodos se crean y se liberan según se necesiten, así que no hay
 *     posiciones que queden inutilizables.
 *   Casos especiales: cola vacía, desencolar en cola vacía, consultar frente en cola vacía,
 *   cola con un solo elemento, encolar después de haber vaciado la cola completamente.
 *   No escribas la implementación.
 *
 * Ajustes realizados posteriormente al prompt inicial:
 *   1) Se agregó el método tamanio() con un atributo size, para que la cantidad en espera
 *      sea O(1) y para poder validar el invariante contra el recorrido real.
 *   2) Se fijó el formato de imprimir() como "FRENTE -> 10 -> 20 -> 30 -> null" y se le
 *      sumó una línea que informa a qué DATO apuntan head y tail. El prompt inicial pedía
 *      imprimir el contenido; mostrar las dos referencias es lo que vuelve visible el
 *      manejo correcto de tail, que es el punto evaluado del ejercicio.
 *   3) Se agregó el método verificarInvariante(), que no estaba en el prompt inicial: se
 *      incorporó para demostrar en la salida que tail siempre es el último nodo alcanzable
 *      desde head y que size coincide con el recorrido real.
 *   No hubo ajustes sobre el diseño: head como frente, tail como final, los dos casos
 *   especiales y las complejidades O(1) salieron tal cual del análisis.
 *
 * Estrategia: cola FIFO sobre lista simplemente enlazada con DOS referencias, head (frente,
 * por donde se sale) y tail (final, por donde se entra), para que ambos extremos sean O(1).
 *
 * Complejidad esperada de las operaciones principales:
 *   encolar   O(1)        desencolar O(1)       verFrente O(1)
 *   estaVacia O(1)        tamanio    O(1)       contiene  O(n)       imprimir O(n)
 *   Espacial: O(n) nodos + 2 referencias fijas (head y tail).
 *
 * DIFERENCIA CON EL PRÁCTICO 4 (cola sobre arreglo): acá NO EXISTE isFull() y, sobre todo,
 * NO EXISTE el problema del desperdicio de espacio que obliga a la aritmética circular. En
 * una cola sobre arreglo, desencolar avanza el índice del frente y deja atrás posiciones
 * que ya no se pueden reutilizar salvo que los índices "den la vuelta" con el operador
 * módulo (frente = (frente + 1) % capacidad); sin esa aritmética circular la cola se
 * declararía llena teniendo el arreglo medio vacío. En la versión enlazada no hay
 * posiciones: cada nodo se crea al encolar y queda inalcanzable (basura para el Garbage
 * Collector) al desencolar, así que no sobra ni falta espacio y no hay nada que "dar la
 * vuelta". El precio es la memoria extra por cada referencia y la pérdida de localidad.
 *
 * @author Micaela Gimenez
 * @version 1.0
 */

/**
 * Nodo de la lista simplemente enlazada que soporta la cola.
 *
 * <p>Guarda un entero y UNA sola referencia hacia el nodo siguiente, en el sentido
 * frente -> final. No hace falta una referencia hacia atrás: la cola saca por head y
 * agrega por tail, y en los dos casos avanza en el mismo sentido que los enlaces.</p>
 */
class Nodo {

    /** Valor almacenado en este nodo. */
    int dato;

    /** Referencia al nodo que está detrás en la fila; null si es el último. */
    Nodo siguiente;

    /**
     * Crea un nodo suelto, todavía no enlazado a ninguna cola.
     *
     * @param dato valor entero que guarda el nodo
     */
    Nodo(int dato) {
        this.dato = dato;
        this.siguiente = null;
    }
}

/**
 * Cola de enteros (política FIFO) implementada sobre una lista simplemente enlazada con
 * referencias {@code head} (frente) y {@code tail} (final).
 *
 * <p><b>¿Por qué la cola necesita head Y tail?</b> FIFO obliga a operar en los DOS
 * extremos: se saca por uno y se agrega por el otro. Esa es la diferencia esencial con la
 * pila, que opera en un solo extremo y por eso se arregla con una sola referencia.</p>
 *
 * <p><b>¿Por qué el frente es head y no tail?</b> Porque desencolar tiene que ser O(1), y
 * en una lista SIMPLEMENTE enlazada eliminar el ÚLTIMO nodo es caro: hay que poner en null
 * el campo siguiente del nodo ANTERIOR al último, y ese anterior no se puede alcanzar
 * desde el último (no hay referencia hacia atrás), así que habría que recorrer desde head.
 * Eliminar el PRIMER nodo, en cambio, es sólo mover head. Por eso el extremo de salida
 * tiene que ser head y, en consecuencia, el de entrada es el final de la lista.</p>
 *
 * <p><b>¿Qué problema aparecería con SOLO head?</b> Encolar tendría que recorrer la cadena
 * entera en cada llamada para encontrar el nodo con siguiente == null, pasando de O(1) a
 * O(n): una cola de 10.000 elementos haría 10.000 saltos de puntero para agregar uno.
 * La referencia tail elimina ese recorrido manteniendo un acceso directo al último nodo:
 * es un intercambio de memoria CONSTANTE (un atributo, no uno por nodo) por tiempo.</p>
 *
 * <p><b>Invariante:</b> o bien {@code head == null && tail == null && size == 0} (cola
 * vacía), o bien {@code head != null && tail != null}, {@code tail.siguiente == null},
 * {@code tail} es el último nodo alcanzable desde {@code head} y la cadena tiene
 * exactamente {@code size} nodos. Con un solo elemento, {@code head == tail}.</p>
 *
 * <p>No existe isFull(): no hay capacidad fija. Ver la cabecera del archivo.</p>
 */
class ColaEnlazada {

    /** Frente de la cola: por acá SALEN los elementos. null si la cola está vacía. */
    private Nodo head;

    /** Final de la cola: por acá ENTRAN los elementos. null si la cola está vacía. */
    private Nodo tail;

    /** Cantidad de elementos en la cola. Se mantiene para que tamanio() sea O(1). */
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
     * Encola un entero, que pasa a ser el último de la fila.
     *
     * <p>Complejidad: O(1) - gracias a tail no hay que recorrer nada para llegar al final.
     * Sin tail esta operación sería O(n).</p>
     *
     * @param dato valor entero a encolar
     */
    void encolar(int dato) {
        Nodo nuevo = new Nodo(dato);
        if (estaVacia()) {
            // *** CASO ESPECIAL 1: ENCOLAR EN COLA VACÍA ***
            // No hay último nodo al que engancharse, así que el nodo nuevo es
            // simultáneamente el primero y el último: head y tail deben apuntar LOS DOS
            // al mismo nodo. Si sólo se actualizara tail, head quedaría en null y la cola
            // seguiría pareciendo vacía con un elemento adentro.
            head = nuevo;
            tail = nuevo;
        } else {
            tail.siguiente = nuevo; // 1) enganchar el nodo nuevo detrás del último actual
            tail = nuevo;           // 2) recién ahora tail avanza al nodo nuevo
        }
        size++;
    }

    /**
     * Quita y devuelve el elemento del frente: el que llegó primero.
     *
     * <p>Complejidad: O(1) - head ya apunta al nodo a eliminar, no hay búsqueda.</p>
     *
     * @return el entero que estaba en el frente
     * @throws IllegalStateException si la cola está vacía
     */
    int desencolar() {
        // Excepción en lugar de centinela: cualquier int es un dato válido, así que un
        // valor especial sería indistinguible de un dato real.
        if (estaVacia()) {
            throw new IllegalStateException("No se puede desencolar: la cola está vacía.");
        }
        int dato = head.dato;   // 1) rescatar el dato antes de perder la referencia
        head = head.siguiente;  // 2) el nodo desencolado queda inalcanzable -> lo recupera el GC
        if (head == null) {
            // *** CASO ESPECIAL 2: SE DESENCOLÓ EL ÚLTIMO ELEMENTO ***
            // head quedó en null, pero tail seguiría apuntando al nodo que acabamos de
            // sacar: un nodo que YA NO forma parte de la cola y que nadie puede alcanzar
            // desde head. Hay que ponerlo en null explícitamente.
            //
            // BUG QUE PRODUCE OLVIDARSE DE ESTA LÍNEA: el próximo encolar vería
            // estaVacia() == true (porque mira head) y entraría por la rama correcta...
            // pero si la rama de cola vacía se escribiera mirando tail != null, el encolar
            // ejecutaría tail.siguiente = nuevo, es decir engancharía el elemento nuevo
            // DETRÁS DE UN NODO INALCANZABLE: head seguiría en null, la cola seguiría
            // pareciendo vacía y el elemento recién encolado se perdería para siempre.
            // Dejar tail colgado también impide que el Garbage Collector libere ese nodo.
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
     * @return el entero que está en el frente
     * @throws IllegalStateException si la cola está vacía
     */
    int verFrente() {
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
     * @return true si la cola está vacía, false en caso contrario
     */
    boolean estaVacia() {
        // Se decide por head, no por tail: head es el extremo por el que se sale y es la
        // referencia que determina si queda algo alcanzable.
        return head == null;
    }

    /**
     * Informa si un valor está en algún lugar de la cola.
     *
     * <p>Complejidad: O(n) - único método que recorre la cadena. Es aceptable porque
     * buscar no pertenece al contrato de una cola (una cola sólo promete acceso al frente);
     * se agrega como utilidad de inspección.</p>
     *
     * @param dato valor a buscar
     * @return true si el valor está en la cola, false si no
     */
    boolean contiene(int dato) {
        Nodo actual = head;
        while (actual != null) {
            // Con int se usa == porque compara VALORES. En la versión genérica
            // (ejercicio 4) hay que usar equals(): con objetos == compararía identidad.
            if (actual.dato == dato) {
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    /**
     * Cantidad de elementos en la cola.
     *
     * <p>Complejidad: O(1) gracias al atributo size; sin él habría que recorrer: O(n).</p>
     *
     * @return número de elementos en la cola
     */
    int tamanio() {
        return size;
    }

    /**
     * Imprime la cola desde el frente hacia el final, en formato
     * {@code FRENTE -> 10 -> 20 -> 30 -> null}, e informa a qué dato apuntan head y tail.
     *
     * <p>El orden impreso es el orden de SALIDA, y coincide con el orden de entrada:
     * eso es FIFO.</p>
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
            salida.append("   (cola vacía)");
        }
        System.out.println("  " + salida);
        System.out.println("      head -> " + estadoHead() + " | tail -> " + estadoTail()
                + " | tamanio = " + size);
    }

    /**
     * Devuelve a qué dato apunta head, o la palabra null si no apunta a ningún nodo.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return representación textual del dato del frente
     */
    String estadoHead() {
        return head == null ? "null" : String.valueOf(head.dato);
    }

    /**
     * Devuelve a qué dato apunta tail, o la palabra null si no apunta a ningún nodo.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return representación textual del dato del final de la cola
     */
    String estadoTail() {
        return tail == null ? "null" : String.valueOf(tail.dato);
    }

    /**
     * Indica si head y tail apuntan al MISMO nodo, lo que ocurre exactamente cuando la
     * cola tiene un solo elemento.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return true si head y tail son la misma referencia y no son null
     */
    boolean headYTailCoinciden() {
        // Acá se usa == a propósito: lo que se compara es IDENTIDAD de referencia, no
        // contenido. La pregunta es "¿es el mismo nodo?", no "¿tienen el mismo dato?".
        return head != null && head == tail;
    }

    /**
     * Verifica el invariante de la estructura recorriendo la cadena real de nodos.
     *
     * <p>Comprueba que: la cantidad de nodos alcanzables desde head coincide con size, que
     * el último nodo alcanzable es exactamente el que apunta tail, que tail.siguiente es
     * null, y que con la cola vacía las dos referencias son null.</p>
     *
     * <p>Complejidad: O(n) - se usa sólo como control de la demostración.</p>
     *
     * @return true si la estructura es consistente, false si alguna referencia quedó mal
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
}

/**
 * Programa de demostración del ejercicio 2: cola de enteros sobre lista enlazada simple.
 *
 * <p>Cubre obligatoriamente los dos casos especiales que surgen de tener dos referencias:
 * encolar en cola vacía (head y tail apuntan al mismo nodo) y vaciar la cola por completo
 * para volver a encolar (si tail no se hubiera puesto en null, el elemento nuevo se
 * perdería). Después de cada operación informa el estado de head y de tail.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio02_ColaEnlazadaEnteros {

    /**
     * Método principal: ejecuta la demostración completa.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 2 - Cola enlazada de enteros (FIFO) ===");

        ColaEnlazada cola = new ColaEnlazada();

        System.out.println("\n--- Caso: cola recién creada (vacía) ---");
        System.out.println("  estaVacia() = " + cola.estaVacia() + " | tamanio() = " + cola.tamanio());
        cola.imprimir();

        System.out.println("\n--- Caso: desencolar y verFrente con la cola VACÍA (errores forzados) ---");
        intentarDesencolar(cola);
        intentarVerFrente(cola);

        System.out.println("\n*** CASO ESPECIAL 1: encolar en cola VACÍA ***");
        encolarMostrando(cola, 10);
        System.out.println("  ¿head y tail apuntan al MISMO nodo? " + cola.headYTailCoinciden()
                + "  -> " + (cola.headYTailCoinciden() ? "OK (es el único nodo: primero y último a la vez)" : "FALLO"));
        System.out.println("  verFrente() = " + cola.verFrente());

        System.out.println("\n--- Flujo nominal: encolar 20 y 30 (traza del análisis) ---");
        encolarMostrando(cola, 20);
        encolarMostrando(cola, 30);
        System.out.println("  verFrente() = " + cola.verFrente()
                + " -> sigue siendo el PRIMERO que llegó, no el último (FIFO)");
        System.out.println("  ¿head y tail coinciden ahora? " + cola.headYTailCoinciden()
                + " (con 3 elementos no deben coincidir)");

        System.out.println("\n--- Búsquedas: contiene() recorre la cadena, es O(n) ---");
        System.out.println("  contiene(10) = " + cola.contiene(10) + " (está en el frente)");
        System.out.println("  contiene(30) = " + cola.contiene(30) + " (está en el final: recorre los 3 nodos)");
        System.out.println("  contiene(99) = " + cola.contiene(99) + " (ausente)");

        System.out.println("\n--- Verificación: el orden de SALIDA es idéntico al de ENTRADA ---");
        System.out.println("  Orden de entrada fue: 10, 20, 30");
        int primero = desencolarMostrando(cola);
        int segundo = desencolarMostrando(cola);

        System.out.println("\n*** CASO ESPECIAL 2: desencolar el ÚLTIMO elemento (tail vuelve a null) ***");
        System.out.println("  Antes del desencolado: head -> " + cola.estadoHead()
                + " | tail -> " + cola.estadoTail() + " (un solo elemento: coinciden = "
                + cola.headYTailCoinciden() + ")");
        int tercero = desencolarMostrando(cola);
        System.out.println("  Después: head -> " + cola.estadoHead() + " | tail -> " + cola.estadoTail());
        boolean tailEnNull = "null".equals(cola.estadoTail());
        System.out.println("  ¿tail volvió a null? " + tailEnNull + "  -> " + (tailEnNull ? "OK" : "FALLO"));
        System.out.println("  Si tail hubiera quedado colgado, el próximo encolar engancharía el");
        System.out.println("  elemento detrás de un nodo inalcanzable y ese elemento se perdería.");

        boolean ordenOk = primero == 10 && segundo == 20 && tercero == 30;
        System.out.println("\n  Orden de salida observado: " + primero + ", " + segundo + ", " + tercero);
        System.out.println("  ¿Coincide EXACTAMENTE con el orden de entrada (10, 20, 30)? "
                + ordenOk + "  -> " + (ordenOk ? "OK" : "FALLO"));

        System.out.println("\n--- Caso: cola vaciada por completo y se vuelve a encolar ---");
        intentarDesencolar(cola);
        encolarMostrando(cola, 44);
        System.out.println("  ¿head y tail apuntan al mismo nodo otra vez? " + cola.headYTailCoinciden());
        System.out.println("  El elemento nuevo es ACCESIBLE: verFrente() = " + cola.verFrente()
                + " | contiene(44) = " + cola.contiene(44));
        encolarMostrando(cola, 55);
        System.out.println("  Se desencolan los dos para confirmar el orden: "
                + cola.desencolar() + ", " + cola.desencolar());
        cola.imprimir();

        System.out.println("\n=== Fin de la demostración ===");
    }

    /**
     * Encola un valor e informa el estado completo de la cola y del invariante.
     *
     * @param cola cola sobre la que se opera
     * @param dato valor a encolar
     */
    private static void encolarMostrando(ColaEnlazada cola, int dato) {
        cola.encolar(dato);
        System.out.println("  encolar(" + dato + ")");
        cola.imprimir();
        System.out.println("      invariante consistente: " + cola.verificarInvariante());
    }

    /**
     * Desencola un valor e informa el estado completo de la cola y del invariante.
     *
     * @param cola cola sobre la que se opera
     * @return el valor desencolado
     */
    private static int desencolarMostrando(ColaEnlazada cola) {
        int dato = cola.desencolar();
        System.out.println("  desencolar() = " + dato);
        cola.imprimir();
        System.out.println("      invariante consistente: " + cola.verificarInvariante());
        return dato;
    }

    /**
     * Intenta desencolar capturando la excepción esperada cuando la cola está vacía.
     *
     * @param cola cola sobre la que se opera
     */
    private static void intentarDesencolar(ColaEnlazada cola) {
        try {
            System.out.println("  desencolar() = " + cola.desencolar());
        } catch (IllegalStateException e) {
            System.out.println("  desencolar() lanzó IllegalStateException: " + e.getMessage() + "  -> OK");
        }
    }

    /**
     * Intenta consultar el frente capturando la excepción esperada cuando la cola está vacía.
     *
     * @param cola cola sobre la que se opera
     */
    private static void intentarVerFrente(ColaEnlazada cola) {
        try {
            System.out.println("  verFrente() = " + cola.verFrente());
        } catch (IllegalStateException e) {
            System.out.println("  verFrente() lanzó IllegalStateException: " + e.getMessage() + "  -> OK");
        }
    }
}


