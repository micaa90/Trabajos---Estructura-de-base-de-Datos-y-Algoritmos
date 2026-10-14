/*
=====================================================================================
Práctico 5 - Listas Enlazadas Simples. Ejercicio 7: Modificar un elemento de la lista
=====================================================================================

Prompt inicial utilizado:

  "Analizá el diseño del método void modificar(int posicion, int nuevoDato) sobre una lista
  enlazada simple, que debe cambiar el dato almacenado en una posición determinada y validar
  la posición ANTES de recorrer.
  El punto central: explicá LA DIFERENCIA ENTRE MODIFICAR EL DATO DE UN NODO Y MODIFICAR LA
  REFERENCIA AL SIGUIENTE NODO. Desarrollá:
  - que un nodo tiene dos atributos con roles completamente distintos: el dato es el CONTENIDO
    (la información que la lista almacena) y la referencia siguiente es la ESTRUCTURA (lo que
    mantiene la lista unida);
  - que modificar el dato es una operación LOCAL e inofensiva: no altera los enlaces, no cambia
    la longitud, no cambia el orden, y size queda igual;
  - que modificar la referencia siguiente es una operación ESTRUCTURAL: puede reordenar la
    lista, dejar nodos inalcanzables, perder el resto de la lista o crear un ciclo infinito.
    Desarrollá este contraste con ejemplos concretos de qué pasaría, es lo que evalúa el ejercicio;
  - por eso este método, a diferencia de insertar y eliminar, no toca ninguna referencia y no
    modifica size;
  - por qué conviene VALIDAR LA POSICIÓN ANTES de recorrer: para no hacer trabajo inútil y para
    no dejar la estructura a medio modificar;
  - cómo se recorre hasta la posición y por qué el recorrido es idéntico al de obtener();
  - si conviene que el método devuelva el dato anterior y por qué podría ser útil;
  - complejidad temporal y espacial, y por qué la complejidad espacial es O(1).
  Casos límite: lista vacía, posición 0, última posición, posición inválida (negativa y >= size),
  modificar por el mismo valor que ya tenía.
  No escribas la implementación."

Ajustes realizados luego de la primera respuesta de OpenCode:

1) La primera respuesta resolvía la modificación eliminando el nodo de esa posición e
    insertando uno nuevo con el dato nuevo. Se descartó: eso toca referencias, altera
    size dos veces y puede dejar la estructura a medio modificar si algo falla en el
    medio. Modificar es una operación de CONTENIDO y se resuelve con setDato().
 2) Se agregó la validación previa al recorrido, igual que en obtener, para no avanzar
    nodos antes de saber si la posición existe.
 3) Se pidió que el main imprima la lista y el size antes y después de cada modificación,
    y se dejó en el código un comentario (no ejecutable) con la línea que modificaría la
    referencia siguiente en lugar del dato, explicando el efecto destructivo.

Estrategia: validar la posición, recorrer con un auxiliar hasta el nodo pedido y reemplazar únicamente su dato, sin tocar ningún enlace ni el size.

Complejidad temporal de modificar: O(1) si la posición es 0, O(n) en el peor caso, O(n)
en promedio. El recorrido es idéntico al de obtener; lo único distinto es que al llegar se
escribe en lugar de leer.
Complejidad espacial: O(1) — no se crean nodos ni estructuras auxiliares, se reutiliza el
objeto Nodo que ya estaba en el Heap.
=====================================================================================
*/

/**
 * Ejercicio 7 del Práctico 5 (Listas Enlazadas Simples): modificación del dato almacenado en
 * una posición determinada.
 *
 * <p>El eje del ejercicio es la diferencia entre modificar el DATO de un nodo (operación de
 * contenido, local e inofensiva) y modificar su referencia {@code siguiente} (operación
 * estructural, capaz de romper la lista o crear un ciclo).</p>
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio07_ModificarElemento {

    /**
     * Método principal: ejecuta las pruebas manuales del ejercicio.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Práctico 5 - Ejercicio 7: modificar un elemento de la lista ===");

        System.out.println();
        System.out.println("--- 1) Se construye la lista 10 -> 20 -> 30 -> 40 -> null ---");
        ListaEnlazada lista = new ListaEnlazada();
        lista.insertarAlFinal(10);
        lista.insertarAlFinal(20);
        lista.insertarAlFinal(30);
        lista.insertarAlFinal(40);
        mostrarEstado("Estado:", lista);

        System.out.println();
        System.out.println("--- 2) Modificar una posición del medio: modificar(2, 99) ---");
        probarModificar(lista, 2, 99);
        System.out.println("    Mismo largo, mismo orden, mismos enlaces: sólo cambió un contenido.");

        System.out.println();
        System.out.println("--- 3) Modificar la posición 0 (ni siquiera head cambia) ---");
        probarModificar(lista, 0, -5);

        System.out.println();
        System.out.println("--- 4) Modificar la última posición (size - 1) ---");
        probarModificar(lista, lista.getSize() - 1, 77);

        System.out.println();
        System.out.println("--- 5) Modificar por el MISMO valor que ya tenía ---");
        probarModificar(lista, 1, 20);
        System.out.println("    Operación legítima y sin efecto observable: no es un caso de error.");

        System.out.println();
        System.out.println("--- 6) Posición inválida: negativa ---");
        probarModificarConError(lista, -1, 0);

        System.out.println();
        System.out.println("--- 7) Posición inválida: size ---");
        probarModificarConError(lista, lista.getSize(), 0);

        System.out.println();
        System.out.println("--- 8) Caso límite: modificar en una lista vacía ---");
        ListaEnlazada vacia = new ListaEnlazada();
        mostrarEstado("Estado:", vacia);
        probarModificarConError(vacia, 0, 1);

        System.out.println();
        System.out.println("--- 9) Conclusión: la estructura nunca cambió ---");
        mostrarEstado("Estado final:", lista);
        System.out.println("size se mantuvo en 4 durante TODO el ejercicio y la cadena conserva");
        System.out.println("las mismas cuatro posiciones: modificar() no es una operación estructural.");

        /*
         * QUÉ PASARÍA SI SE MODIFICARA LA REFERENCIA EN LUGAR DEL DATO.
         * Las dos líneas siguientes NO son código ejecutable, están comentadas a propósito.
         *
         *     Nodo nodoCero = head;                        // nodo del dato -5
         *     nodoCero.setSiguiente(null);                 // (1) truncar la lista
         *     nodoCero.setSiguiente(nodoCero);             // (2) crear un ciclo
         *
         * Efecto de (1): sobre -5 -> 20 -> 99 -> 77 -> null quedaría "-5 -> null". Los nodos
         * 20, 99 y 77 seguirían existiendo en el Heap pero ya no serían alcanzables desde
         * head, así que se perderían (candidatos del Garbage Collector) y size seguiría
         * diciendo 4 mientras la cadena real tendría 1 nodo: el invariante roto.
         * Efecto de (2): el nodo se apuntaría a sí mismo y imprimir() nunca alcanzaría null,
         * quedando en un bucle infinito.
         * Con setDato(99) ninguna de las dos cosas puede pasar: el campo siguiente ni se toca.
         */
    }

    /**
     * Modifica una posición válida mostrando el estado antes y después.
     *
     * Complejidad: O(n).
     *
     * @param lista     lista a modificar
     * @param posicion  índice en base 0
     * @param nuevoDato entero que reemplaza al contenido anterior
     */
    private static void probarModificar(ListaEnlazada lista, int posicion, int nuevoDato) {
        mostrarEstado("Antes: ", lista);
        int anterior = lista.obtener(posicion); // sólo para mostrar qué dato se reemplaza
        lista.modificar(posicion, nuevoDato);
        System.out.println("modificar(" + posicion + ", " + nuevoDato + "): el dato " + anterior
                + " pasó a ser " + nuevoDato + ".");
        mostrarEstado("Después:", lista);
    }

    /**
     * Intenta modificar una posición inválida y muestra el mensaje de la excepción.
     *
     * Complejidad: O(1), porque la validación corta antes de recorrer.
     *
     * @param lista     lista a modificar
     * @param posicion  índice en base 0 que se espera inválido
     * @param nuevoDato entero que se intentaba escribir
     */
    private static void probarModificarConError(ListaEnlazada lista, int posicion, int nuevoDato) {
        int sizeAntes = lista.getSize();
        try {
            lista.modificar(posicion, nuevoDato);
            System.out.println("modificar(" + posicion + ", " + nuevoDato
                    + ") no lanzó excepción (no se esperaba éxito)");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("modificar(" + posicion + ", " + nuevoDato
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
 * <p>Métodos disponibles en este ejercicio: insertarAlInicio, insertarAlFinal, imprimir, estaVacia, getSize, buscar, obtener, insertarEnPosicion, eliminar, eliminarEnPosicion e modificar.</p>
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
     * Cambia el dato almacenado en una posición determinada, sin tocar ningún enlace.
     *
     * <p><b>Diferencia entre modificar el dato y modificar la referencia:</b> el dato es el
     * CONTENIDO del nodo y la referencia {@code siguiente} es la ESTRUCTURA. Cambiar el dato
     * es una operación LOCAL e inofensiva: la longitud, el orden de los nodos, los enlaces y
     * {@code size} quedan exactamente igual, y por eso ningún invariante de la estructura
     * puede romperse. Cambiar la referencia {@code siguiente}, en cambio, es una operación
     * ESTRUCTURAL: puede reordenar la lista, dejar nodos inalcanzables, perder toda la cola
     * de la cadena o crear un ciclo infinito.</p>
     *
     * <p>Justamente por eso este método NO toca ninguna referencia y NO modifica
     * {@code size}: ninguna de las dos cosas cambia cuando sólo se reemplaza contenido.</p>
     *
     * <p>El recorrido es idéntico al de {@code obtener}: la única diferencia es que al
     * llegar se escribe en lugar de leer.</p>
     *
     * Complejidad: O(1) en el mejor caso, O(n) en el peor, O(n) en promedio. O(1) espacial.
     *
     * @param posicion  índice en base 0 del nodo a modificar
     * @param nuevoDato entero que reemplaza al contenido anterior
     * @throws IndexOutOfBoundsException si {@code posicion < 0} o {@code posicion >= size}
     */
    void modificar(int posicion, int nuevoDato) {
        validarPosicionExistente(posicion); // validar ANTES de recorrer

        Nodo actual = head;
        int indice = 0;
        while (indice < posicion) {
            actual = actual.getSiguiente();
            indice++;
        }

        // Única escritura del método: sólo CONTENIDO. No se toca siguiente ni size.
        actual.setDato(nuevoDato);
    }

    /**
     * Elimina la PRIMERA aparición de un dato en la lista.
     *
     * <p><b>Qué significa "eliminar" en Java:</b> no se libera memoria a mano ni se destruye
     * el objeto. Lo único que se hace es REDIRIGIR la referencia del nodo anterior para que
     * apunte al siguiente del eliminado, salteándolo. A partir de ese momento el nodo
     * eliminado ya no es alcanzable desde {@code head}, y un objeto inalcanzable es
     * candidato a ser recogido por el GARBAGE COLLECTOR, en algún momento que el programa no
     * controla ni puede observar. En C habría que llamar explícitamente a {@code free()} y
     * olvidarse produciría una fuga de memoria; acá el riesgo opuesto es dejar sin querer
     * una referencia viva, que impediría la recolección.</p>
     *
     * <p>Por eso igualmente se pone en {@code null} la referencia {@code siguiente} del nodo
     * eliminado: si alguien todavía conservara una referencia a ese nodo, a través de él
     * mantendría vivo todo el resto de la cadena.</p>
     *
     * <p><b>Por qué hace falta conocer el nodo ANTERIOR:</b> un nodo no puede desconectarse a
     * sí mismo, porque en una lista simplemente enlazada no tiene forma de llegar a su
     * predecesor. De ahí el recorrido con dos referencias ({@code anterior} y {@code actual}).
     * En una lista DOBLEMENTE enlazada esto no sería necesario: cada nodo conoce a su
     * anterior.</p>
     *
     * <p>Casos tratados: lista vacía, primer nodo (caso especial, hay que mover
     * {@code head}), nodo del medio, último nodo (el nuevo último hereda el {@code null}) y
     * dato inexistente. Si el dato está repetido sólo se elimina la primera aparición.</p>
     *
     * Complejidad: O(1) si el dato está en el primer nodo, O(n) en el peor caso (último nodo
     * o dato inexistente). O(1) espacial.
     *
     * @param dato entero cuya primera aparición se quiere eliminar
     * @return {@code true} si se eliminó un nodo, {@code false} si el dato no estaba
     */
    boolean eliminar(int dato) {
        if (head == null) { // CASO lista vacía: no hay nada que eliminar
            return false;
        }

        if (head.getDato() == dato) { // CASO primer nodo: el caso especial, cambia head
            Nodo eliminado = head;
            head = head.getSiguiente(); // el punto de entrada pasa al segundo nodo
            eliminado.setSiguiente(null); // el nodo desconectado no debe retener la cadena
            size--;
            return true;
        }

        // Recorrido con dos referencias: hay que llegar al ANTERIOR del nodo buscado.
        Nodo anterior = head;
        Nodo actual = head.getSiguiente();
        while (actual != null) {
            if (actual.getDato() == dato) { // CASO nodo del medio o CASO último nodo
                // Si actual era el último, getSiguiente() es null y el nuevo último lo hereda.
                anterior.setSiguiente(actual.getSiguiente());
                actual.setSiguiente(null); // queda inalcanzable: trabajo del Garbage Collector
                size--;
                return true;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }

        return false; // CASO dato inexistente: se recorrió todo sin encontrarlo
    }

    /**
     * Elimina el nodo que está en una posición determinada (indexación base 0).
     *
     * <p><b>Por qué se localiza el nodo {@code posicion - 1} y no el {@code posicion}:</b>
     * eliminar un nodo consiste en modificar la referencia del nodo ANTERIOR, y un nodo no
     * puede desconectarse a sí mismo en una lista simplemente enlazada. La asignación que
     * realiza la eliminación es el DOBLE SALTO
     * {@code anterior.setSiguiente(anterior.getSiguiente().getSiguiente())}: el anterior deja
     * de apuntar a su sucesor inmediato y pasa a apuntar al sucesor de su sucesor, salteando
     * un nodo de la cadena.</p>
     *
     * <p>Antes de reconectar se guarda una referencia al nodo a eliminar, porque después del
     * doble salto ya no habría manera de alcanzarlo para ponerle {@code siguiente} en
     * {@code null}.</p>
     *
     * <p><b>Caso especial, posición 0:</b> no existe un nodo anterior, así que lo que hay que
     * modificar es {@code head}.</p>
     *
     * <p><b>Rango válido:</b> acá la posición máxima es {@code size - 1}, porque el nodo debe
     * EXISTIR. En {@code insertarEnPosicion} el máximo era {@code size}, porque ahí sólo se
     * necesita un lugar donde engancharse. Si {@code size} no se actualizara en esta
     * operación, {@code obtener} e {@code insertarEnPosicion} empezarían a validar contra una
     * longitud que ya no es la real.</p>
     *
     * Complejidad: O(1) para la posición 0; O(posicion) en el caso general, O(n) en el peor.
     * O(1) espacial.
     *
     * @param posicion índice en base 0 del nodo a eliminar
     * @throws IndexOutOfBoundsException si {@code posicion < 0} o {@code posicion >= size}
     *         (incluye el caso de la lista vacía, donde ninguna posición es válida)
     */
    void eliminarEnPosicion(int posicion) {
        validarPosicionExistente(posicion); // cubre lista vacía, negativos y posicion >= size

        if (posicion == 0) { // caso especial: no hay anterior, se mueve el punto de entrada
            Nodo eliminado = head;
            head = head.getSiguiente();
            eliminado.setSiguiente(null);
            size--;
            return;
        }

        // Recorrido hasta el nodo anterior al que se quiere eliminar.
        Nodo anterior = head;
        int indice = 0;
        while (indice < posicion - 1) {
            anterior = anterior.getSiguiente();
            indice++;
        }

        Nodo eliminado = anterior.getSiguiente(); // guardar antes de reconectar
        anterior.setSiguiente(eliminado.getSiguiente()); // DOBLE SALTO: saltea el eliminado
        eliminado.setSiguiente(null); // ya es inalcanzable desde head
        size--; // una sola vez, exactamente una eliminación
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


