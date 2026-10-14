/*
=====================================================================================
Práctico 5 - Listas Enlazadas Simples. Ejercicio 4: Insertar un nodo en una posición específica
=====================================================================================

Prompt inicial utilizado:

  "Analizá el diseño del método void insertarEnPosicion(int dato, int posicion) sobre una lista
  enlazada simple, que debe permitir insertar al inicio, en el medio y al final, y validar
  posiciones inválidas.
  El punto central: explicá CON CLARIDAD EL ORDEN CORRECTO DE ACTUALIZACIÓN DE REFERENCIAS:
    nuevo.setSiguiente(actual.getSiguiente());
    actual.setSiguiente(nuevo);
  y explicá QUÉ PODRÍA PASAR SI SE ACTUALIZAN LAS REFERENCIAS EN EL ORDEN INCORRECTO.
  Desarrollá:
  - por qué el orden correcto es primero conectar el nodo nuevo hacia adelante y después
    reconectar el anterior hacia el nodo nuevo;
  - qué pasa exactamente si se invierte el orden: al hacer primero actual.setSiguiente(nuevo)
    se SOBRESCRIBE la única referencia que existía al resto de la lista, así que
    actual.getSiguiente() ya no devuelve el nodo que seguía sino el nodo nuevo. El resultado es
    que se pierde todo el resto de la lista (queda inaccesible y el Garbage Collector puede
    recogerlo) y además nuevo.setSiguiente(nuevo) crearía un ciclo que haría que imprimir()
    no termine nunca. Desarrollá esto con todo detalle, es el corazón del ejercicio;
  - los TRES casos de inserción y por qué hay que tratarlos distinto: en posición 0 no existe
    un nodo anterior, así que hay que actualizar head (es un caso especial); en el medio y al
    final el procedimiento es el mismo y por qué;
  - por qué para insertar en la posición p hay que llegar al nodo de la posición p-1 y no al p;
  - la validación de posiciones: por qué acá la posición válida máxima es size (insertar al
    final) y no size - 1, a diferencia del método obtener. Este detalle es sutil, explicalo;
  - cómo se actualiza size;
  - complejidad temporal según la posición y complejidad espacial.
  Casos límite: insertar en lista vacía, insertar en posición 0, en posición size, en el medio,
  posición negativa, posición mayor a size.
  No escribas la implementación."

Ajustes realizados luego de la primera respuesta de OpenCode:

1) La primera respuesta escribía las dos asignaciones en el orden
    anterior.setSiguiente(nuevo) y después nuevo.setSiguiente(anterior.getSiguiente()).
    Eso sobrescribe la única referencia al resto de la lista: se pierde la cola y el nodo
    nuevo termina apuntándose a sí mismo, con lo cual imprimir() entra en un ciclo
    infinito. Se corrigió el orden y se dejó el escenario documentado en el código.
 2) La primera versión validaba con posicion >= size, copiando la validación de obtener.
    Se corrigió a posicion > size: insertar en la posición size significa insertar al
    final, que es una operación perfectamente válida.
 3) El caso de la posición 0 se trataba dentro del ciclo general, lo que fallaba porque no
    existe nodo anterior al primero. Se separó delegando en insertarAlInicio(), que es el
    único método que puede mover head.

Estrategia: validar el rango 0..size, tratar la posición 0 moviendo head y, en el resto de los casos, llegar al nodo posicion-1 y reconectar en el orden nuevo-hacia-adelante y después anterior-hacia-nuevo.

Complejidad temporal de insertarEnPosicion: O(1) si la posición es 0 (sólo se mueve head),
O(posicion) en el caso general y por lo tanto O(n) en el peor caso (insertar al final).
La inserción en sí es O(1): lo que cuesta es LOCALIZAR el nodo anterior.
Complejidad espacial: O(1) — una referencia auxiliar, un contador y el nodo nuevo.
=====================================================================================
*/

/**
 * Ejercicio 4 del Práctico 5 (Listas Enlazadas Simples): inserción de un nodo en una posición
 * específica, al inicio, en el medio o al final.
 *
 * <p>El eje del ejercicio es el ORDEN de actualización de referencias: primero se conecta el
 * nodo nuevo hacia adelante y recién después se reconecta el nodo anterior hacia el nuevo. El
 * orden inverso pierde el resto de la lista y crea un ciclo infinito.</p>
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio04_InsertarEnPosicion {

    /**
     * Método principal: ejecuta las pruebas manuales del ejercicio.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Práctico 5 - Ejercicio 4: insertar un nodo en una posición específica ===");

        System.out.println();
        System.out.println("--- 1) Caso límite: insertar en la posición 0 de una lista vacía ---");
        ListaEnlazada lista = new ListaEnlazada();
        mostrarEstado("Antes: ", lista);
        lista.insertarEnPosicion(10, 0); // size == 0, así que 0 es la única posición válida
        mostrarEstado("Después de insertarEnPosicion(10, 0):", lista);

        System.out.println();
        System.out.println("--- 2) Se completa la lista 10 -> 20 -> 30 -> 40 -> null ---");
        lista.insertarAlFinal(20);
        lista.insertarAlFinal(30);
        lista.insertarAlFinal(40);
        mostrarEstado("Estado:", lista);

        System.out.println();
        System.out.println("--- 3) Inserción EN EL MEDIO: insertarEnPosicion(25, 2) ---");
        mostrarEstado("Antes: ", lista);
        System.out.println("    Se recorre hasta el nodo de la posición 1 (el 20), que es el anterior al destino.");
        System.out.println("    1) nuevo.setSiguiente(anterior.getSiguiente()) -> el 25 apunta al 30");
        System.out.println("    2) anterior.setSiguiente(nuevo)                -> el 20 apunta al 25");
        lista.insertarEnPosicion(25, 2);
        mostrarEstado("Después:", lista);

        System.out.println();
        System.out.println("--- 4) Inserción AL INICIO: insertarEnPosicion(5, 0), caso especial de head ---");
        mostrarEstado("Antes: ", lista);
        lista.insertarEnPosicion(5, 0); // no hay nodo anterior: lo que cambia es head
        mostrarEstado("Después:", lista);

        System.out.println();
        System.out.println("--- 5) Inserción AL FINAL: posición size, el máximo válido ---");
        mostrarEstado("Antes: ", lista);
        System.out.println("    size = " + lista.getSize() + ", así que insertarEnPosicion(99, "
                + lista.getSize() + ") es válido.");
        lista.insertarEnPosicion(99, lista.getSize());
        mostrarEstado("Después:", lista);

        System.out.println();
        System.out.println("--- 6) Posición inválida: negativa ---");
        probarInsercionConError(lista, 7, -1);

        System.out.println();
        System.out.println("--- 7) Posición inválida: mayor que size ---");
        probarInsercionConError(lista, 7, lista.getSize() + 1);

        System.out.println();
        System.out.println("--- 8) Verificación final ---");
        mostrarEstado("Estado final:", lista);
        System.out.println("No hubo ciclos: contarNodosEnlazados() terminó, lo que prueba que la");
        System.out.println("cadena sigue acabando en null, y su valor coincide con size.");

        /*
         * Comparación de rangos válidos (no es código ejecutable, es la conclusión del
         * ejercicio): acá la posición máxima válida es size porque sólo se necesita un LUGAR
         * donde engancharse, y el lugar size es el hueco posterior al último nodo. En
         * obtener(posicion) el máximo es size - 1 porque ahí el nodo tiene que EXISTIR.
         */
    }

    /**
     * Intenta insertar en una posición inválida y muestra el mensaje de la excepción.
     *
     * Complejidad: O(1), porque la validación corta antes de recorrer.
     *
     * @param lista    lista a modificar
     * @param dato     entero que se intenta insertar
     * @param posicion índice en base 0 que se espera inválido
     */
    private static void probarInsercionConError(ListaEnlazada lista, int dato, int posicion) {
        int sizeAntes = lista.getSize();
        try {
            lista.insertarEnPosicion(dato, posicion);
            System.out.println("insertarEnPosicion(" + dato + ", " + posicion
                    + ") no lanzó excepción (no se esperaba éxito)");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("insertarEnPosicion(" + dato + ", " + posicion
                    + ") lanzó IndexOutOfBoundsException: " + e.getMessage());
        }
        System.out.println("    La lista quedó intacta: size antes = " + sizeAntes
                + ", size después = " + lista.getSize() + " | " + lista.aCadena());
    }

    /**
     * Imprime la lista con una etiqueta, el {@code size} declarado y el recuento real de
     * nodos enlazados, marcando si ambos coinciden.
     *
     * Complejidad: O(n) por el recorrido de verificación.
     *
     * @param etiqueta texto que precede al estado
     * @param lista    lista a mostrar
     */
    private static void mostrarEstado(String etiqueta, ListaEnlazada lista) {
        int reales = lista.contarNodosEnlazados();
        System.out.println(etiqueta + " " + lista.aCadena()
                + "   | size = " + lista.getSize()
                + " | nodos enlazados = " + reales
                + (reales == lista.getSize() ? "  [consistente]" : "  [INCONSISTENTE]"));
    }
}

/**
 * Nodo de una lista enlazada simple de enteros: la unidad mínima de la estructura.
 *
 * <p>Un nodo tiene exactamente dos atributos, con roles completamente distintos:</p>
 * <ul>
 *   <li>{@code dato}: el CONTENIDO, el entero que la lista almacena.</li>
 *   <li>{@code siguiente}: la ESTRUCTURA, una referencia al nodo que viene después. Es lo
 *       único que mantiene la lista unida. Cada nodo conoce solamente al que le sigue, y la
 *       dirección de un nodo sólo puede obtenerse preguntándosela al nodo anterior: de ahí
 *       que el recorrido sea obligatoriamente secuencial.</li>
 * </ul>
 *
 * <p>Que {@code siguiente} valga {@code null} significa que el nodo es el ÚLTIMO: no hay
 * nadie más después. Por eso {@code null} funciona como marca de fin de recorrido.</p>
 *
 * <p>Se implementa como clase aparte (de paquete, no pública) y no como clase interna de
 * {@code ListaEnlazada} para que cada clase tenga una sola responsabilidad: el nodo guarda
 * un dato y un enlace, la lista coordina los enlaces. Va en este mismo archivo para que el
 * ejercicio sea compilable y entregable por sí solo.</p>
 */
class Nodo {

    /** Contenido del nodo. */
    private int dato;

    /** Referencia al nodo siguiente; {@code null} si este nodo es el último. */
    private Nodo siguiente;

    /**
     * Crea un nodo suelto, todavía sin enlazar a ninguna lista.
     *
     * Complejidad: O(1).
     *
     * @param dato entero a almacenar
     */
    Nodo(int dato) {
        this.dato = dato;
        this.siguiente = null; // un nodo nuevo nace sin sucesor
    }

    /**
     * Devuelve el contenido del nodo.
     *
     * Complejidad: O(1).
     *
     * @return el entero almacenado
     */
    int getDato() {
        return dato;
    }

    /**
     * Cambia el contenido del nodo. Operación NO estructural: no altera ningún enlace.
     *
     * Complejidad: O(1).
     *
     * @param dato nuevo entero a almacenar
     */
    void setDato(int dato) {
        this.dato = dato;
    }

    /**
     * Devuelve la referencia al nodo siguiente.
     *
     * Complejidad: O(1).
     *
     * @return el nodo siguiente, o {@code null} si este es el último
     */
    Nodo getSiguiente() {
        return siguiente;
    }

    /**
     * Cambia la referencia al nodo siguiente. Operación ESTRUCTURAL: reescribe un enlace,
     * así que el orden en que se la invoca respecto de otras asignaciones es crítico.
     *
     * Complejidad: O(1).
     *
     * @param siguiente nodo que pasa a ser el sucesor, o {@code null} para marcar el final
     */
    void setSiguiente(Nodo siguiente) {
        this.siguiente = siguiente;
    }
}

/**
 * Lista enlazada simple de números enteros, implementada desde cero: sin {@code ArrayList},
 * sin {@code LinkedList}, sin arreglos y sin ninguna estructura de la librería de Java.
 *
 * <p>La estructura es una cadena de objetos {@code Nodo} dispersos en el Heap y unidos por
 * referencias. {@code head} es el único punto de entrada y {@code null} marca el final.
 * A diferencia de un arreglo, no hay memoria contigua ni acceso directo por índice: se gana
 * inserción y eliminación en O(1) una vez localizado el punto de la operación, y se pierde
 * el acceso O(1) por posición.</p>
 *
 * <p><b>Invariantes de la clase:</b></p>
 * <ul>
 *   <li>{@code head == null} si y sólo si la lista está vacía (y entonces {@code size == 0});</li>
 *   <li>{@code size} es exactamente la cantidad de nodos alcanzables desde {@code head};</li>
 *   <li>la cadena termina siempre en {@code null}: no hay ciclos;</li>
 *   <li>cada nodo es alcanzable desde {@code head} por un único camino.</li>
 * </ul>
 *
 * <p>Métodos disponibles en este ejercicio: insertarAlInicio, insertarAlFinal, imprimir, estaVacia, getSize, buscar, obtener e insertarEnPosicion.</p>
 */
class ListaEnlazada {

    /**
     * Único punto de entrada a la estructura: referencia al PRIMER nodo.
     *
     * <p>Sin {@code head} la lista entera se vuelve inaccesible, porque los enlaces apuntan
     * en un solo sentido y no existe ninguna otra forma de alcanzar al primer nodo (y, a
     * través de él, a todos los demás). Que {@code head} sea {@code null} significa que la
     * lista está vacía.</p>
     */
    private Nodo head;

    /**
     * Cantidad de nodos enlazados.
     *
     * <p>Se mantiene como atributo y se actualiza en TODA operación que cambia la longitud.
     * Así {@code getSize()} cuesta O(1); recontar los nodos en cada consulta costaría O(n).
     * El precio es la disciplina: si una operación se olvida de actualizarlo, las
     * validaciones de posición empiezan a mentir.</p>
     */
    private int size;

    /**
     * Crea una lista vacía: todavía no hay primer nodo y la longitud es cero.
     *
     * Complejidad: O(1).
     */
    ListaEnlazada() {
        this.head = null;
        this.size = 0;
    }

    /**
     * Nodos visitados por la última operación de recorrido ({@code buscar},
     * {@code obtener} o {@code contarOcurrencias}).
     *
     * <p>No forma parte de la estructura de datos: es instrumentación para poder OBSERVAR
     * el costo real del acceso secuencial desde el {@code main}.</p>
     */
    private int nodosVisitados;

    /**
     * Inserta un dato como PRIMER elemento de la lista.
     *
     * <p>Orden obligatorio de las dos asignaciones de referencias:</p>
     * <ol>
     *   <li>{@code nuevo.setSiguiente(head)}: el nodo nuevo queda conectado hacia adelante
     *       con la lista que ya existía.</li>
     *   <li>{@code head = nuevo}: recién ahora se mueve el punto de entrada.</li>
     * </ol>
     *
     * <p><b>Qué pasaría con el orden invertido:</b> si primero se hiciera
     * {@code head = nuevo}, se sobrescribiría la ÚNICA referencia que existía al viejo
     * primer nodo sin haberla guardado en ninguna parte. Todo el resto de la lista quedaría
     * inalcanzable (candidato para el Garbage Collector) y la asignación posterior
     * {@code nuevo.setSiguiente(head)} haría que el nodo nuevo se apunte a sí mismo: un
     * ciclo que dejaría a {@code imprimir()} dando vueltas para siempre.</p>
     *
     * Complejidad: O(1) temporal — no hay recorrido, sólo dos asignaciones. O(1) espacial.
     *
     * @param dato entero a almacenar en el nuevo primer nodo
     */
    void insertarAlInicio(int dato) {
        Nodo nuevo = new Nodo(dato);
        nuevo.setSiguiente(head); // 1) primero conectar el nodo nuevo hacia adelante
        head = nuevo;             // 2) después mover el punto de entrada
        size++;                   // la longitud cambió: hay que reflejarlo
    }

    /**
     * Inserta un dato como ÚLTIMO elemento de la lista.
     *
     * <p>Caso especial, la lista vacía: no hay ningún nodo al que engancharse, así que el
     * nodo nuevo pasa a ser también el primero y lo que hay que actualizar es {@code head}.
     * En cualquier otro caso hay que RECORRER hasta el último nodo (el único que tiene
     * {@code siguiente == null}), porque una lista simplemente enlazada no guarda ninguna
     * referencia al final.</p>
     *
     * <p>Mantener un atributo {@code tail} volvería esta operación O(1), pero obligaría a
     * actualizarlo en cada inserción y cada eliminación; no se usa acá porque la consigna
     * pide la estructura mínima y el costo de mantenerlo consistente no se justifica.</p>
     *
     * Complejidad: O(n) temporal por el recorrido (O(1) sólo si la lista está vacía).
     * O(1) espacial: una única referencia auxiliar.
     *
     * @param dato entero a almacenar en el nuevo último nodo
     */
    void insertarAlFinal(int dato) {
        Nodo nuevo = new Nodo(dato);
        if (head == null) { // caso especial: lista vacía, el nuevo nodo es también el primero
            head = nuevo;
        } else {
            // El recorrido usa SIEMPRE una variable auxiliar: mover head perdería la lista.
            Nodo actual = head;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevo); // el viejo último ahora apunta al nodo nuevo
        }
        size++;
    }

    /**
     * Valida una posición de INSERCIÓN, cuyo rango válido es {@code 0 .. size}.
     *
     * <p><b>Por qué acá el máximo es {@code size} y no {@code size - 1}:</b> insertar no
     * requiere que ya exista un nodo en esa posición, sino que exista un LUGAR donde
     * engancharse. El lugar número {@code size} es el hueco que queda después del último
     * nodo, es decir "insertar al final", que es una operación perfectamente válida. En
     * {@code obtener}, {@code modificar} y {@code eliminarEnPosicion} el nodo tiene que
     * existir de antemano, y por eso ahí el máximo es {@code size - 1}.</p>
     *
     * Complejidad: O(1).
     *
     * @param posicion índice en base 0 donde se quiere insertar
     * @throws IndexOutOfBoundsException si {@code posicion < 0} o {@code posicion > size}
     */
    private void validarPosicionDeInsercion(int posicion) {
        if (posicion < 0) {
            throw new IndexOutOfBoundsException("Posición inválida: " + posicion
                    + ". Las posiciones son base 0 y nunca negativas.");
        }
        if (posicion > size) {
            throw new IndexOutOfBoundsException("Posición inválida: " + posicion
                    + ". Para insertar, la posición máxima válida es size = " + size
                    + " (que significa insertar al final).");
        }
    }

    /**
     * Inserta un dato en una posición determinada, desplazando hacia atrás al nodo que
     * estaba ahí. Admite insertar al inicio, en el medio y al final.
     *
     * <p><b>Por qué hay que llegar al nodo {@code posicion - 1} y no al {@code posicion}:</b>
     * en una lista simplemente enlazada el nodo nuevo entra en la cadena porque ALGUIEN lo
     * apunta, y ese alguien es el nodo anterior. El nodo que está en la posición destino no
     * tiene ninguna manera de "hacerse lugar" a sí mismo.</p>
     *
     * <p><b>Caso especial, posición 0:</b> no existe un nodo anterior al primero, así que lo
     * que hay que modificar es {@code head}. Se delega en {@code insertarAlInicio}.</p>
     *
     * <p>Orden correcto de actualización de referencias (y el corazón de este ejercicio):</p>
     * <ol>
     *   <li>{@code nuevo.setSiguiente(anterior.getSiguiente())} — conectar el nodo nuevo
     *       hacia adelante, con el nodo que hasta ahora seguía a {@code anterior}.</li>
     *   <li>{@code anterior.setSiguiente(nuevo)} — reconectar el nodo anterior hacia el
     *       nodo nuevo.</li>
     * </ol>
     *
     * Complejidad: O(1) para la posición 0; O(posicion) en el caso general, es decir O(n) en
     * el peor caso (insertar al final). O(1) espacial.
     *
     * @param dato     entero a insertar
     * @param posicion índice en base 0 donde quedará el dato, entre 0 y {@code size}
     * @throws IndexOutOfBoundsException si {@code posicion < 0} o {@code posicion > size}
     */
    void insertarEnPosicion(int dato, int posicion) {
        validarPosicionDeInsercion(posicion); // validar ANTES de recorrer y de crear enlaces

        if (posicion == 0) {
            // Caso especial: no hay nodo anterior; el punto de entrada es lo que cambia.
            insertarAlInicio(dato);
            return;
        }

        // Recorrido hasta el nodo anterior al destino (posicion - 1), con auxiliar.
        Nodo anterior = head;
        int indice = 0;
        while (indice < posicion - 1) {
            anterior = anterior.getSiguiente();
            indice++;
        }

        Nodo nuevo = new Nodo(dato);

        /*
         * ORDEN DE LAS DOS ASIGNACIONES. Qué pasaría si se invirtieran:
         *
         *     anterior.setSiguiente(nuevo);                 // (mal) PRIMERO
         *     nuevo.setSiguiente(anterior.getSiguiente());  // (mal) DESPUÉS
         *
         * La primera línea sobrescribe la única referencia que existía hacia el resto de la
         * lista, así que anterior.getSiguiente() ya no devuelve el nodo que seguía: devuelve
         * el nodo nuevo. Consecuencias:
         *   1) el resto de la lista queda inalcanzable desde head (se pierde, y el Garbage
         *      Collector puede recogerlo);
         *   2) la segunda línea equivale a nuevo.setSiguiente(nuevo), es decir un nodo que
         *      se apunta a sí mismo: un CICLO. A partir de ahí imprimir(), buscar() o
         *      contarNodosEnlazados() no terminan nunca, porque nunca alcanzan null.
         *
         * El estado INTERMEDIO del orden correcto, en cambio, es seguro: después de la
         * asignación 1) el nodo nuevo ya apunta al resto de la lista pero todavía nadie lo
         * apunta a él; la lista sigue siendo exactamente la de antes y nada se perdió.
         */
        nuevo.setSiguiente(anterior.getSiguiente()); // 1) conectar hacia adelante
        anterior.setSiguiente(nuevo);                // 2) reconectar hacia atrás

        size++; // la longitud cambió
    }

    /**
     * Indica si un dato está presente en la lista, recorriéndola desde {@code head}.
     *
     * <p><b>Por qué no se puede acceder directamente por índice:</b> en un arreglo los
     * elementos ocupan memoria CONTIGUA, así que la dirección del elemento {@code i} se
     * calcula con una cuenta ({@code base + i * tamaño}) y el acceso es O(1). En una lista
     * enlazada los nodos están dispersos en el Heap y no hay ninguna fórmula que permita
     * saltar: la dirección de un nodo la conoce únicamente el nodo anterior. Para llegar al
     * nodo {@code i} hay que pasar obligatoriamente por los {@code i} anteriores, y eso es
     * lo que hace que la búsqueda sea secuencial.</p>
     *
     * <p><b>Corolario:</b> por el mismo motivo NO se puede aplicar búsqueda binaria sobre
     * una lista enlazada aunque esté ordenada. La búsqueda binaria necesita saltar al
     * elemento del medio en O(1); acá llegar al medio ya cuesta O(n/2), con lo cual el
     * supuesto ahorro desaparece.</p>
     *
     * <p>El recorrido avanza con una variable auxiliar de tipo {@code Nodo} y nunca con
     * {@code head}: si se usara {@code head} como variable de recorrido, al terminar
     * apuntaría a {@code null} y la lista entera quedaría inalcanzable.</p>
     *
     * <p>Hay dos condiciones de corte: encontrar el dato (éxito) o llegar a {@code null}
     * (fin de la cadena, el dato no está).</p>
     *
     * Complejidad: O(1) en el mejor caso (dato en el primer nodo), O(n) en el peor (dato en
     * el último o inexistente), O(n) en promedio ((n+1)/2 nodos visitados si está presente).
     * O(1) espacial.
     *
     * @param dato entero buscado
     * @return {@code true} si algún nodo contiene el dato, {@code false} si no
     */
    boolean buscar(int dato) {
        nodosVisitados = 0;
        Nodo actual = head; // auxiliar de recorrido: head no se mueve nunca
        while (actual != null) { // corte 1: se llegó al final de la cadena
            nodosVisitados++;
            if (actual.getDato() == dato) {
                return true; // corte 2: la existencia ya quedó demostrada, no hace falta más
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    /**
     * Valida una posición que debe corresponder a un nodo EXISTENTE, es decir del rango
     * {@code 0 .. size - 1} (indexación base 0). La usan {@code obtener},
     * {@code modificar} y {@code eliminarEnPosicion}.
     *
     * <p><b>Por qué se lanza una excepción y no se devuelve un valor centinela:</b>
     * {@code obtener} devuelve {@code int}, y cualquier número que se eligiera como
     * centinela ({@code -1}, {@code Integer.MIN_VALUE}) es también un dato perfectamente
     * válido para guardar en la lista. El llamador no podría distinguir "ahí no hay nodo" de
     * "ahí hay un -1". La excepción, en cambio, viaja por un canal separado del valor de
     * retorno y no puede confundirse con un dato.</p>
     *
     * <p>La validación se hace ANTES de recorrer: evita trabajo inútil y garantiza que la
     * estructura nunca quede a medio modificar.</p>
     *
     * Complejidad: O(1) — sólo compara contra {@code size}, que se mantiene actualizado.
     *
     * @param posicion índice en base 0 que se quiere usar
     * @throws IndexOutOfBoundsException si {@code posicion < 0} o {@code posicion >= size}
     */
    private void validarPosicionExistente(int posicion) {
        if (posicion < 0) {
            // Una posición negativa no podría encontrarse nunca: no existe sintácticamente.
            throw new IndexOutOfBoundsException("Posición inválida: " + posicion
                    + ". Las posiciones son base 0 y nunca negativas.");
        }
        if (posicion >= size) {
            // Acá la posición sí es sintácticamente válida, pero no hay ningún nodo ahí.
            String detalle = (size == 0)
                    ? " (la lista está vacía: ninguna posición es válida)."
                    : ", así que la última posición válida es " + (size - 1) + ".";
            throw new IndexOutOfBoundsException("Posición inválida: " + posicion
                    + ". La lista tiene size = " + size + detalle);
        }
    }

    /**
     * Devuelve el dato almacenado en una posición determinada (indexación base 0).
     *
     * <p><b>La trampa conceptual del método:</b> la firma se parece a la de un arreglo
     * ({@code vector[i]}) y eso puede hacer creer que el costo es el mismo. No lo es:
     * adentro hay un RECORRIDO nodo por nodo. El arreglo resuelve el acceso en O(1) con una
     * cuenta de direcciones; la lista enlazada necesita O(n) porque la única forma de
     * conocer la dirección de un nodo es preguntándosela al anterior. Recorrer una lista
     * entera con {@code obtener(i)} dentro de un {@code for} cuesta O(n²), mientras que el
     * mismo bucle sobre un arreglo cuesta O(n).</p>
     *
     * Complejidad: O(1) en el mejor caso ({@code posicion == 0}), O(n) en el peor
     * ({@code posicion == size - 1}), O(n) en promedio. O(1) espacial.
     *
     * @param posicion índice en base 0 del nodo cuyo dato se quiere leer
     * @return el entero almacenado en esa posición
     * @throws IndexOutOfBoundsException si {@code posicion < 0} o {@code posicion >= size}
     */
    int obtener(int posicion) {
        validarPosicionExistente(posicion); // validar ANTES de recorrer

        Nodo actual = head;
        int indice = 0;
        nodosVisitados = 1; // ya estamos parados sobre el nodo de la posición 0
        while (indice < posicion) { // avanzar auxiliar y contador en paralelo
            actual = actual.getSiguiente();
            indice++;
            nodosVisitados++;
        }
        return actual.getDato();
    }

    /**
     * Indica si la lista no tiene ningún nodo.
     *
     * <p>Se consulta {@code head == null} porque es la condición estructural; por el
     * invariante de la clase es equivalente a {@code size == 0}.</p>
     *
     * Complejidad: O(1).
     *
     * @return {@code true} si la lista está vacía
     */
    boolean estaVacia() {
        return head == null;
    }

    /**
     * Devuelve la cantidad de elementos de la lista.
     *
     * Complejidad: O(1), porque {@code size} se mantiene actualizado en cada operación.
     *
     * @return la longitud de la lista
     */
    int getSize() {
        return size;
    }

    /**
     * Imprime la lista por consola en el formato {@code 10 -> 20 -> 30 -> null}, de modo que
     * se vean los enlaces y el {@code null} final que marca el fin de la cadena. Una lista
     * vacía se imprime como {@code null}.
     *
     * Complejidad: O(n) temporal — un paso por nodo. O(n) espacial por el texto construido.
     */
    void imprimir() {
        System.out.println(aCadena());
    }

    /**
     * Devuelve la lista como texto en el formato {@code 10 -> 20 -> 30 -> null}.
     *
     * Complejidad: O(n) temporal.
     *
     * @return representación textual de la cadena de nodos
     */
    String aCadena() {
        return cadenaDesde(head);
    }

    /**
     * Devuelve como texto la cadena de nodos que arranca en un nodo dado. Se usa para
     * mostrar porciones de la lista (por ejemplo, la parte ya invertida).
     *
     * <p>Se concatenan Strings en lugar de usar un StringBuilder o cualquier colección,
     * para no introducir estructuras de la librería en un práctico que las prohíbe.</p>
     *
     * Complejidad: O(n) pasos de recorrido.
     *
     * @param inicio nodo donde empieza la cadena, puede ser {@code null}
     * @return texto con los datos separados por flechas y terminado en {@code null}
     */
    private String cadenaDesde(Nodo inicio) {
        String texto = "";
        Nodo actual = inicio;
        while (actual != null) {
            texto = texto + actual.getDato() + " -> ";
            actual = actual.getSiguiente();
        }
        return texto + "null";
    }

    /**
     * Cuenta los nodos REALMENTE enlazados recorriendo la cadena desde {@code head}.
     *
     * <p>No reemplaza a {@code getSize()}: existe para VERIFICAR que el atributo
     * {@code size} siga coincidiendo con la estructura después de cada operación. Si los dos
     * números difieren, hay una operación que se olvidó de actualizar {@code size} o un
     * enlace mal puesto.</p>
     *
     * Complejidad: O(n) temporal, O(1) espacial.
     *
     * @return cantidad de nodos alcanzables desde {@code head}
     */
    int contarNodosEnlazados() {
        int cuenta = 0;
        Nodo actual = head;
        while (actual != null) {
            cuenta++;
            actual = actual.getSiguiente();
        }
        return cuenta;
    }

    /**
     * Devuelve cuántos nodos visitó la última operación de recorrido.
     *
     * Complejidad: O(1).
     *
     * @return nodos visitados por la última llamada a {@code buscar}, {@code obtener} o
     *         {@code contarOcurrencias}
     */
    int getNodosVisitados() {
        return nodosVisitados;
    }
}


