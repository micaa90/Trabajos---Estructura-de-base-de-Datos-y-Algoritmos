/*
 * =====================================================================================
 * Práctico 6 — Pilas y Colas con Listas Enlazadas. Ejercicio 10: Lista doblemente enlazada
 * =====================================================================================
 * Prompt inicial utilizado:
 * "Analizá el diseño de una LISTA DOBLEMENTE ENLAZADA GENÉRICA, que debe permitir insertar al
 *  inicio, insertar al final, eliminar un elemento, imprimir hacia adelante e imprimir hacia atrás.
 *  El punto central: explicá QUÉ REFERENCIAS TIENE CADA NODO, POR QUÉ SE NECESITA ANTERIOR Y
 *  SIGUIENTE, y QUÉ CASOS ESPECIALES HAY QUE CONTEMPLAR AL ELIMINAR NODOS. Desarrollá:
 *  - la estructura del nodo: dato, referencia al siguiente y referencia al ANTERIOR. Explicá que
 *    ahora cada enlace entre dos nodos es BIDIRECCIONAL y que eso duplica la cantidad de
 *    referencias a mantener;
 *  - POR QUÉ se necesita la referencia anterior, con tres motivos concretos:
 *    1) permite recorrer la lista HACIA ATRÁS, lo que en una lista simple es imposible sin
 *       recorrerla entera desde head;
 *    2) permite ELIMINAR un nodo en O(1) si ya se tiene la referencia a ese nodo, porque el nodo
 *       puede alcanzar a su propio anterior. En una lista simplemente enlazada eso obliga a
 *       recorrer desde head para encontrar al anterior;
 *    3) permite insertar antes de un nodo dado sin recorrido previo;
 *  - qué se paga por eso: memoria extra por cada nodo, y el doble de referencias que hay que
 *    actualizar correctamente en cada operación, lo que aumenta la probabilidad de error;
 *  - por qué conviene mantener referencias head Y tail, y qué permite tail;
 *  - el orden de actualización de referencias en insertarAlInicio e insertarAlFinal: enumerá las
 *    CUATRO asignaciones que hace falta en el caso general y explicá por qué ninguna puede
 *    omitirse;
 *  - LOS CASOS ESPECIALES AL ELIMINAR, que es lo que el ejercicio pide explícitamente. Enumeralos
 *    y explicá el tratamiento de cada uno: lista vacía; eliminar el ÚNICO nodo; eliminar el
 *    PRIMER nodo; eliminar el ÚLTIMO nodo; eliminar un nodo del MEDIO, donde hay que reconectar
 *    en los dos sentidos; y elemento inexistente;
 *  - por qué imprimir hacia adelante e imprimir hacia atrás son la MEJOR PRUEBA de que la
 *    estructura quedó consistente: si las dos salidas no son exactamente inversas una de la otra,
 *    alguna referencia quedó mal;
 *  - la parte genérica: qué cambia y qué no respecto de una versión con int, y por qué eliminar
 *    por valor necesita equals() y no ==;
 *  - la complejidad de cada operación.
 *  No escribas la implementación."
 *
 * Ajustes realizados:
 *  1) Se agregó el pedido de la verificación automática adelante/atrás. La primera respuesta
 *     proponía imprimir en los dos sentidos y revisar a ojo, lo cual no sirve: el bug típico de
 *     esta estructura —reconectar solo un sentido al eliminar del medio— deja la lista
 *     perfectamente legible hacia adelante. Una prueba que solo mire imprimirAdelante() pasa sin
 *     detectar nada. Por eso el programa compara las dos salidas carácter por carácter y además
 *     verifica el invariante nodo.siguiente.anterior == nodo en toda la cadena.
 *  2) Se desglosaron los SEIS casos de eliminación por separado. La primera respuesta trataba
 *     "eliminar el primero" y "eliminar el único" como el mismo caso, y no lo son: al eliminar el
 *     único nodo hay que poner head Y tail en null, mientras que al eliminar el primero de varios
 *     hay que mover head y además dejar el nuevo primero con anterior en null. Juntarlos dejaba
 *     una referencia colgada apuntando a un nodo ya eliminado.
 *  3) Se agregó la exigencia de equals() en lugar de ==. La primera respuesta usaba == para
 *     comparar los datos, que con objetos compara identidad de referencia y no contenido: dos
 *     new String("20") de igual contenido habrían dado false y eliminar() no habría encontrado
 *     nunca el elemento.
 *
 * Estrategia: cada nodo guarda dato, anterior y siguiente, de modo que todo enlace entre dos
 * nodos es bidireccional. La lista mantiene head, tail y size, lo que hace O(1) la inserción en
 * los dos extremos y permite recorrer en ambos sentidos. La eliminación por valor busca el nodo
 * en O(n) y lo desconecta en O(1) reconectando los dos sentidos, con los seis casos especiales
 * tratados por separado.
 * Complejidad temporal: O(1) en insertarAlInicio, insertarAlFinal, estaVacia y getSize; O(n) en
 * eliminar por valor (la búsqueda; la desconexión en sí es O(1)) y en los dos recorridos de
 * impresión.
 * Complejidad espacial: O(n), un nodo por elemento. Cada nodo cuesta una referencia más que en
 * una lista simple, y ese es el precio de poder recorrer hacia atrás y desconectar en O(1).
 * =====================================================================================
 */

/**
 * Lista doblemente enlazada genérica, con verificación automática de consistencia bidireccional
 * después de cada operación.
 *
 * <p>La prueba central de este ejercicio no es que la lista imprima bien hacia adelante, sino que
 * las dos direcciones coincidan: si una referencia {@code anterior} queda mal puesta, el
 * recorrido hacia adelante sigue funcionando y el error pasa desapercibido.</p>
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio10_ListaDoblementeEnlazada {

    /**
     * Punto de entrada. Recorre los seis casos de eliminación con dos tipos de dato distintos,
     * verificando la consistencia de la estructura después de cada operación.
     *
     * <p>Complejidad: O(n) por operación verificada.</p>
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 10 - Lista doblemente enlazada genérica ===");

        probarConEnteros();
        probarConCadenas();
        probarIgualdadPorContenido();
        explicarBugDeUnSoloSentido();
    }

    /**
     * Recorre los seis casos de eliminación sobre una {@code ListaDoble<Integer>}.
     *
     * <p>Complejidad: O(n) por operación.</p>
     */
    private static void probarConEnteros() {
        System.out.println("\n============================================================");
        System.out.println("ListaDoble<Integer> - LOS SEIS CASOS DE ELIMINACIÓN");
        System.out.println("============================================================");

        ListaDoble<Integer> lista = new ListaDoble<>();

        System.out.println("\n--- CASO 1: eliminar en lista VACÍA ---");
        mostrar(lista);
        System.out.println("eliminar(99) -> " + lista.eliminar(99)
                + "   (false: no hay nada que eliminar, y no es un error)");
        verificar(lista);

        System.out.println("\n--- Se arma la lista: insertarAlFinal 20, 30, 40 ---");
        lista.insertarAlFinal(20);
        lista.insertarAlFinal(30);
        lista.insertarAlFinal(40);
        mostrar(lista);
        verificar(lista);

        System.out.println("\n--- insertarAlInicio(10) ---");
        lista.insertarAlInicio(10);
        mostrar(lista);
        verificar(lista);

        System.out.println("\n--- CASO 5: eliminar un nodo del MEDIO -> eliminar(30) ---");
        System.out.println("Hay que reconectar en LOS DOS sentidos:");
        System.out.println("  anterior.siguiente = siguiente   y   siguiente.anterior = anterior");
        System.out.println("eliminar(30) -> " + lista.eliminar(30));
        mostrar(lista);
        verificar(lista);

        System.out.println("\n--- CASO 3: eliminar el PRIMER nodo -> eliminar(10) ---");
        System.out.println("Hay que mover head, y el nuevo primero queda con anterior = null.");
        System.out.println("eliminar(10) -> " + lista.eliminar(10));
        mostrar(lista);
        verificar(lista);

        System.out.println("\n--- CASO 4: eliminar el ÚLTIMO nodo -> eliminar(40) ---");
        System.out.println("Hay que mover tail, y el nuevo último queda con siguiente = null.");
        System.out.println("eliminar(40) -> " + lista.eliminar(40));
        mostrar(lista);
        verificar(lista);

        System.out.println("\n--- CASO 6: elemento INEXISTENTE -> eliminar(77) ---");
        System.out.println("eliminar(77) -> " + lista.eliminar(77)
                + "   (false: la lista queda intacta)");
        mostrar(lista);
        verificar(lista);

        System.out.println("\n--- CASO 2: eliminar el ÚNICO nodo -> eliminar(20) ---");
        System.out.println("head Y tail deben quedar los dos en null.");
        System.out.println("eliminar(20) -> " + lista.eliminar(20));
        mostrar(lista);
        System.out.println("head == null y tail == null: " + lista.extremosEnNull());
        verificar(lista);

        System.out.println("\n--- Se vuelve a usar la lista después de vaciarla ---");
        System.out.println("Si al eliminar el único nodo tail hubiera quedado colgado, este");
        System.out.println("insertarAlFinal se engancharía a un nodo inalcanzable.");
        lista.insertarAlFinal(50);
        lista.insertarAlInicio(45);
        mostrar(lista);
        verificar(lista);
    }

    /**
     * Repite el ciclo de operaciones sobre una {@code ListaDoble<String>}, para mostrar que la
     * lógica de enlaces no depende del tipo de dato almacenado.
     *
     * <p>Complejidad: O(n) por operación.</p>
     */
    private static void probarConCadenas() {
        System.out.println("\n============================================================");
        System.out.println("ListaDoble<String> - LA MISMA LÓGICA, OTRO TIPO");
        System.out.println("============================================================");
        System.out.println("No cambió una sola línea del manejo de referencias: insertar y");
        System.out.println("eliminar operan sobre la ESTRUCTURA, nunca sobre el contenido.");

        ListaDoble<String> lista = new ListaDoble<>();
        lista.insertarAlFinal("ana");
        lista.insertarAlFinal("jorge");
        lista.insertarAlFinal("micaela");
        lista.insertarAlInicio("lucia");
        mostrar(lista);
        verificar(lista);

        System.out.println("\n--- eliminar(\"jorge\") (nodo del medio) ---");
        System.out.println("eliminar -> " + lista.eliminar("jorge"));
        mostrar(lista);
        verificar(lista);

        System.out.println("\n--- eliminar(\"pedro\") (inexistente) ---");
        System.out.println("eliminar -> " + lista.eliminar("pedro"));
        mostrar(lista);
        verificar(lista);
    }

    /**
     * Demuestra por qué la eliminación por valor necesita {@code equals()} y no {@code ==}.
     *
     * <p>Complejidad: O(n).</p>
     */
    private static void probarIgualdadPorContenido() {
        System.out.println("\n============================================================");
        System.out.println("POR QUÉ eliminar() USA equals() Y NO ==");
        System.out.println("============================================================");

        String guardado = new String("20");
        String buscado = new String("20");

        System.out.println("Dos String de igual contenido creados por separado:");
        System.out.println("  guardado == buscado        -> " + (guardado == buscado)
                + "   (compara IDENTIDAD de referencia: son dos objetos distintos)");
        System.out.println("  guardado.equals(buscado)   -> " + guardado.equals(buscado)
                + "   (compara CONTENIDO)");

        ListaDoble<String> lista = new ListaDoble<>();
        lista.insertarAlFinal("10");
        lista.insertarAlFinal(guardado);
        lista.insertarAlFinal("30");
        mostrar(lista);

        System.out.println("\neliminar(buscado) -> " + lista.eliminar(buscado));
        mostrar(lista);
        System.out.println("Encontró el elemento porque compara con equals(). Con == habría");
        System.out.println("devuelto false y el elemento no se habría eliminado nunca.");
        verificar(lista);
    }

    /**
     * Explica el bug típico de esta estructura y por qué una prueba de un solo sentido no lo
     * detecta.
     *
     * <p>Complejidad: O(1).</p>
     */
    private static void explicarBugDeUnSoloSentido() {
        System.out.println("\n============================================================");
        System.out.println("EL BUG QUE LA VERIFICACIÓN BIDIRECCIONAL DETECTA");
        System.out.println("============================================================");
        System.out.println("Al eliminar un nodo del medio hay que hacer DOS reconexiones:");
        System.out.println("  (a) anterior.siguiente = siguiente");
        System.out.println("  (b) siguiente.anterior = anterior");
        System.out.println();
        System.out.println("Si se hace solo (a), sobre 10 <-> 20 <-> 30 al eliminar el 20:");
        System.out.println("  hacia adelante: null <- 10 <-> 30 -> null      <-- se ve CORRECTA");
        System.out.println("  hacia atrás:    30 <-> 20 <-> 10              <-- el 20 REAPARECE");
        System.out.println();
        System.out.println("El 30 seguiría apuntando con su 'anterior' al nodo eliminado. El");
        System.out.println("recorrido hacia adelante nunca pasa por ahí, así que un test que");
        System.out.println("solo llame a imprimirAdelante() pasa sin detectar nada.");
        System.out.println("Por eso la prueba real es que las dos direcciones sean exactamente");
        System.out.println("inversas, más el invariante nodo.siguiente.anterior == nodo.");
    }

    /**
     * Imprime la lista en los dos sentidos junto con su tamaño.
     *
     * <p>Complejidad: O(n).</p>
     *
     * @param <T> tipo de los elementos
     * @param lista lista a mostrar
     */
    private static <T> void mostrar(ListaDoble<T> lista) {
        System.out.println("   adelante: " + lista.imprimirAdelante());
        System.out.println("   atrás:    " + lista.imprimirAtras());
        System.out.println("   size = " + lista.getSize() + " | estaVacia() = "
                + lista.estaVacia());
    }

    /**
     * Verifica y reporta la consistencia de la estructura.
     *
     * <p>Complejidad: O(n).</p>
     *
     * @param <T> tipo de los elementos
     * @param lista lista a verificar
     */
    private static <T> void verificar(ListaDoble<T> lista) {
        System.out.println("   consistencia: " + lista.verificarConsistencia());
    }
}

/**
 * Nodo de una lista doblemente enlazada: un dato y las referencias a sus dos vecinos.
 *
 * <p>La diferencia con el nodo de una lista simple es la referencia {@code anterior}. Con ella,
 * cada enlace entre dos nodos es <b>bidireccional</b>: si A apunta a B con {@code siguiente}, B
 * debe apuntar a A con {@code anterior}. Mantener esa simetría es el invariante central de la
 * estructura, y romperla es el error más común al eliminar nodos.</p>
 *
 * <p>El primer nodo tiene {@code anterior} en {@code null} y el último tiene {@code siguiente} en
 * {@code null}: esos dos {@code null} son las marcas de los extremos.</p>
 *
 * @param <T> tipo del dato almacenado
 */
class NodoDoble<T> {

    /** Dato almacenado. El algoritmo de enlaces nunca lo inspecciona. */
    private final T dato;

    /** Referencia al nodo anterior, o {@code null} si este es el primero. */
    private NodoDoble<T> anterior;

    /** Referencia al nodo siguiente, o {@code null} si este es el último. */
    private NodoDoble<T> siguiente;

    /**
     * Crea un nodo suelto, con los dos enlaces todavía sin conectar.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param dato dato a almacenar; puede ser {@code null}
     */
    NodoDoble(T dato) {
        this.dato = dato;
        this.anterior = null;
        this.siguiente = null;
    }

    /**
     * Devuelve el dato almacenado.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return el dato de este nodo
     */
    public T getDato() {
        return dato;
    }

    /**
     * Devuelve el nodo anterior.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return el nodo anterior, o {@code null} si este es el primero
     */
    public NodoDoble<T> getAnterior() {
        return anterior;
    }

    /**
     * Devuelve el nodo siguiente.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return el nodo siguiente, o {@code null} si este es el último
     */
    public NodoDoble<T> getSiguiente() {
        return siguiente;
    }

    /**
     * Fija el nodo anterior.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param anterior nodo que pasa a ser el anterior, o {@code null}
     */
    public void setAnterior(NodoDoble<T> anterior) {
        this.anterior = anterior;
    }

    /**
     * Fija el nodo siguiente.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param siguiente nodo que pasa a ser el siguiente, o {@code null}
     */
    public void setSiguiente(NodoDoble<T> siguiente) {
        this.siguiente = siguiente;
    }
}

/**
 * Lista doblemente enlazada genérica con referencias a los dos extremos.
 *
 * <p><b>Por qué se necesita la referencia anterior.</b> Tres motivos concretos:</p>
 * <ol>
 *   <li><b>Recorrer hacia atrás.</b> En una lista simple es imposible sin volver a empezar desde
 *       head en cada paso, lo que convertiría el recorrido inverso en O(n^2).</li>
 *   <li><b>Eliminar en O(1) un nodo conocido.</b> Para desconectar un nodo hay que modificar la
 *       referencia de su anterior. Un nodo doble alcanza a su propio anterior directamente; en
 *       una lista simple hay que recorrer desde head para encontrarlo, y eso es O(n).</li>
 *   <li><b>Insertar antes de un nodo dado</b> sin recorrido previo, por la misma razón.</li>
 * </ol>
 *
 * <p><b>Qué se paga.</b> Una referencia más por nodo, y el doble de enlaces que mantener
 * correctos: cada operación estructural tiene que actualizar los dos sentidos, lo que duplica las
 * oportunidades de dejar la lista inconsistente.</p>
 *
 * <p><b>Por qué head y tail.</b> {@code head} da acceso a la estructura y {@code tail} permite
 * insertar al final e iniciar el recorrido inverso en O(1). Sin {@code tail}, {@code
 * insertarAlFinal} tendría que recorrer toda la cadena.</p>
 *
 * <p><b>Qué aportan los genéricos.</b> Cambia el tipo del dato y las firmas; no cambia nada del
 * manejo de referencias, porque insertar y desconectar operan sobre los enlaces y no sobre el
 * contenido. La única excepción es {@link #eliminar(Object)}, que compara datos y por eso usa
 * {@code equals()}: con {@code ==} compararía identidad de referencia y no encontraría dos
 * objetos de igual contenido creados por separado.</p>
 *
 * <p><b>Invariantes.</b> (1) {@code head.anterior == null} y {@code tail.siguiente == null};
 * (2) para todo nodo con siguiente, {@code nodo.siguiente.anterior == nodo}; (3) {@code size} es
 * la cantidad de nodos alcanzables desde head, y también desde tail hacia atrás; (4) la lista
 * está vacía exactamente cuando {@code head == null}, y en ese caso {@code tail} también es
 * {@code null}.</p>
 *
 * @param <T> tipo de los elementos almacenados
 */
class ListaDoble<T> {

    /** Primer nodo de la lista, o {@code null} si está vacía. */
    private NodoDoble<T> head;

    /** Último nodo de la lista, o {@code null} si está vacía. */
    private NodoDoble<T> tail;

    /** Cantidad de elementos, mantenida para que {@code getSize()} sea O(1). */
    private int size;

    /**
     * Inserta un elemento al principio de la lista.
     *
     * <p>En el caso general hacen falta cuatro asignaciones y ninguna puede omitirse:
     * (1) {@code nuevo.siguiente = head} conecta el nodo nuevo hacia adelante;
     * (2) {@code head.anterior = nuevo} completa el enlace en el sentido inverso, y sin ella el
     * recorrido hacia atrás nunca llegaría al nodo nuevo;
     * (3) {@code head = nuevo} mueve el extremo;
     * (4) {@code size++} mantiene el contador. El nodo nuevo queda con {@code anterior} en
     * {@code null} por construcción, que es correcto porque pasa a ser el primero.</p>
     *
     * <p>Caso especial de la lista vacía: no hay un head al que enlazar, así que head y tail
     * pasan a apuntar los dos al nodo nuevo.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param dato elemento a insertar
     */
    public void insertarAlInicio(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>(dato);

        if (estaVacia()) {
            head = nuevo;
            tail = nuevo;
            size++;
            return;
        }

        nuevo.setSiguiente(head);
        head.setAnterior(nuevo);
        head = nuevo;
        size++;
    }

    /**
     * Inserta un elemento al final de la lista.
     *
     * <p>Es el espejo exacto de {@link #insertarAlInicio(Object)}: se enlaza el nodo nuevo con
     * {@code tail} en los dos sentidos y se mueve el extremo. Gracias a la referencia
     * {@code tail} no hace falta ningún recorrido, así que la operación es O(1) y no O(n) como
     * sería en una lista simple sin esa referencia.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param dato elemento a insertar
     */
    public void insertarAlFinal(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>(dato);

        if (estaVacia()) {
            head = nuevo;
            tail = nuevo;
            size++;
            return;
        }

        nuevo.setAnterior(tail);
        tail.setSiguiente(nuevo);
        tail = nuevo;
        size++;
    }

    /**
     * Elimina la primera aparición del elemento indicado.
     *
     * <p>La comparación se hace con {@code equals()} y no con {@code ==}, porque con objetos
     * {@code ==} compara identidad de referencia: dos instancias de igual contenido creadas por
     * separado darían {@code false} y el elemento no se encontraría nunca.</p>
     *
     * <p>Los seis casos están tratados por separado abajo, cada uno comentado.</p>
     *
     * <p>Complejidad: O(n) por la búsqueda; la desconexión en sí es O(1), que es la ventaja de
     * tener la referencia {@code anterior}.</p>
     *
     * @param dato elemento a eliminar; puede ser {@code null}
     * @return {@code true} si se eliminó un nodo, {@code false} si el elemento no estaba
     */
    public boolean eliminar(T dato) {
        // CASO 1 — LISTA VACÍA: no hay nada que recorrer ni eliminar. No es un error.
        if (estaVacia()) {
            return false;
        }

        NodoDoble<T> actual = head;
        while (actual != null && !sonIguales(actual.getDato(), dato)) {
            actual = actual.getSiguiente();
        }

        // CASO 6 — ELEMENTO INEXISTENTE: se recorrió toda la cadena sin coincidencia. La lista
        // queda intacta y se informa con false.
        if (actual == null) {
            return false;
        }

        NodoDoble<T> anterior = actual.getAnterior();
        NodoDoble<T> siguiente = actual.getSiguiente();

        if (anterior == null && siguiente == null) {
            // CASO 2 — ÚNICO NODO: la lista queda vacía, así que LOS DOS extremos van a null.
            // Dejar tail apuntando al nodo eliminado sería un enlace colgado: la próxima
            // inserción al final se engancharía a una cadena inalcanzable desde head.
            head = null;
            tail = null;
        } else if (anterior == null) {
            // CASO 3 — PRIMER NODO de varios: head se mueve al siguiente, y el nuevo primero
            // queda con anterior en null. Sin esa segunda línea, el nuevo head seguiría
            // apuntando hacia atrás a un nodo ya eliminado y el recorrido inverso lo mostraría.
            head = siguiente;
            head.setAnterior(null);
        } else if (siguiente == null) {
            // CASO 4 — ÚLTIMO NODO de varios: tail se mueve al anterior, y el nuevo último queda
            // con siguiente en null, para que el recorrido hacia adelante termine donde debe.
            tail = anterior;
            tail.setSiguiente(null);
        } else {
            // CASO 5 — NODO DEL MEDIO: hay que reconectar en LOS DOS SENTIDOS. Hacer solo la
            // primera línea deja la lista correcta hacia adelante pero rota hacia atrás, porque
            // 'siguiente' seguiría apuntando con su anterior al nodo eliminado. Es un bug que
            // una prueba que solo imprima hacia adelante NO detecta.
            anterior.setSiguiente(siguiente);
            siguiente.setAnterior(anterior);
        }

        // El nodo eliminado se aísla por completo. En Java no se libera memoria a mano: al
        // quedar sin referencias que lo alcancen desde head o tail, pasa a ser candidato para el
        // recolector de basura. Limpiar sus enlaces evita además que mantenga vivo al resto de
        // la cadena si alguien todavía conservara una referencia a él.
        actual.setAnterior(null);
        actual.setSiguiente(null);
        size--;
        return true;
    }

    /**
     * Indica si la lista no tiene elementos.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return {@code true} si {@code head} es {@code null}
     */
    public boolean estaVacia() {
        return head == null;
    }

    /**
     * Devuelve la cantidad de elementos.
     *
     * <p>Complejidad: O(1), por el atributo contador.</p>
     *
     * @return cantidad de elementos de la lista
     */
    public int getSize() {
        return size;
    }

    /**
     * Devuelve el recorrido de la lista desde {@code head} hacia {@code tail}.
     *
     * <p>Complejidad: O(n).</p>
     *
     * @return representación en formato {@code null <- 10 <-> 20 <-> 30 -> null}
     */
    public String imprimirAdelante() {
        if (estaVacia()) {
            return "(vacía)";
        }
        StringBuilder sb = new StringBuilder("null <- ");
        NodoDoble<T> actual = head;
        while (actual != null) {
            sb.append(actual.getDato());
            actual = actual.getSiguiente();
            if (actual != null) {
                sb.append(" <-> ");
            }
        }
        return sb.append(" -> null").toString();
    }

    /**
     * Devuelve el recorrido de la lista desde {@code tail} hacia {@code head}.
     *
     * <p>Este recorrido es el que solo una lista doblemente enlazada puede hacer en O(n): avanza
     * por las referencias {@code anterior} partiendo de {@code tail}. En una lista simple habría
     * que recorrer desde head una vez por elemento.</p>
     *
     * <p>Complejidad: O(n).</p>
     *
     * @return representación en formato {@code null <- 30 <-> 20 <-> 10 -> null}
     */
    public String imprimirAtras() {
        if (estaVacia()) {
            return "(vacía)";
        }
        StringBuilder sb = new StringBuilder("null <- ");
        NodoDoble<T> actual = tail;
        while (actual != null) {
            sb.append(actual.getDato());
            actual = actual.getAnterior();
            if (actual != null) {
                sb.append(" <-> ");
            }
        }
        return sb.append(" -> null").toString();
    }

    /**
     * Verifica que la estructura esté consistente y devuelve el veredicto.
     *
     * <p>Hace cuatro comprobaciones, y las dos últimas son las que detectan el bug típico:</p>
     * <ol>
     *   <li>los extremos tienen sus {@code null} donde corresponde;</li>
     *   <li>la cantidad de nodos alcanzables desde head y desde tail coincide con {@code size};</li>
     *   <li>el recorrido hacia atrás es exactamente el inverso del recorrido hacia adelante;</li>
     *   <li>el invariante {@code nodo.siguiente.anterior == nodo} se cumple en toda la cadena.</li>
     * </ol>
     *
     * <p>Complejidad: O(n).</p>
     *
     * @return {@code "OK (...)"} si la estructura es consistente, o la descripción del primer
     *         problema encontrado
     */
    public String verificarConsistencia() {
        if (estaVacia()) {
            if (tail != null) {
                return "FALLO: la lista está vacía pero tail no es null (enlace colgado)";
            }
            return size == 0
                    ? "OK (lista vacía, head y tail en null, size = 0)"
                    : "FALLO: lista vacía pero size = " + size;
        }

        if (head.getAnterior() != null) {
            return "FALLO: head.anterior no es null";
        }
        if (tail.getSiguiente() != null) {
            return "FALLO: tail.siguiente no es null";
        }

        int haciaAdelante = 0;
        NodoDoble<T> actual = head;
        while (actual != null) {
            haciaAdelante++;
            NodoDoble<T> siguiente = actual.getSiguiente();
            // El invariante de simetría: si A apunta a B hacia adelante, B tiene que apuntar a A
            // hacia atrás. Este es el chequeo que delata una reconexión hecha en un solo sentido.
            if (siguiente != null && siguiente.getAnterior() != actual) {
                return "FALLO: el enlace entre " + actual.getDato() + " y " + siguiente.getDato()
                        + " es asimétrico (siguiente.anterior no apunta de vuelta)";
            }
            actual = siguiente;
        }

        int haciaAtras = 0;
        actual = tail;
        while (actual != null) {
            haciaAtras++;
            actual = actual.getAnterior();
        }

        if (haciaAdelante != size || haciaAtras != size) {
            return "FALLO: size = " + size + " pero se alcanzan " + haciaAdelante
                    + " nodos hacia adelante y " + haciaAtras + " hacia atrás";
        }

        if (!esInversoExacto()) {
            return "FALLO: el recorrido hacia atrás no es el inverso exacto del de adelante";
        }

        return "OK (" + size + " nodos, enlaces simétricos, recorridos inversos entre sí)";
    }

    /**
     * Indica si los extremos quedaron los dos en {@code null}.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return {@code true} si {@code head} y {@code tail} son {@code null}
     */
    public boolean extremosEnNull() {
        return head == null && tail == null;
    }

    /**
     * Comprueba que la secuencia de nodos hacia atrás sea el inverso exacto de la secuencia
     * hacia adelante.
     *
     * <p>La comprobación es por <b>identidad de nodo</b> y no por contenido: el nodo que ocupa la
     * posición k contando desde {@code head} tiene que ser el mismo objeto que el que ocupa la
     * posición {@code size - 1 - k} contando desde {@code tail}. Comparar los datos no serviría,
     * porque una lista con elementos repetidos podría coincidir por casualidad.</p>
     *
     * <p>Complejidad: O(n^2), porque para cada posición se recorren los dos sentidos. Es
     * aceptable: es código de verificación, no del algoritmo.</p>
     *
     * @return {@code true} si las dos secuencias recorren los mismos nodos en orden opuesto
     */
    private boolean esInversoExacto() {
        for (int k = 0; k < size; k++) {
            NodoDoble<T> desdeInicio = head;
            for (int i = 0; i < k; i++) {
                desdeInicio = desdeInicio.getSiguiente();
                if (desdeInicio == null) {
                    return false;
                }
            }
            NodoDoble<T> desdeFin = tail;
            for (int i = 0; i < size - 1 - k; i++) {
                desdeFin = desdeFin.getAnterior();
                if (desdeFin == null) {
                    return false;
                }
            }
            if (desdeInicio != desdeFin) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compara dos datos por contenido, tolerando {@code null}.
     *
     * <p>Se usa {@code equals()} y no {@code ==} porque con objetos {@code ==} compara identidad
     * de referencia. El caso {@code null} se trata aparte porque invocar {@code equals()} sobre
     * {@code null} lanzaría {@code NullPointerException}.</p>
     *
     * <p>Complejidad: O(1) más el costo del {@code equals()} del tipo concreto.</p>
     *
     * @param a primer dato
     * @param b segundo dato
     * @return {@code true} si los dos son {@code null} o si {@code a.equals(b)}
     */
    private boolean sonIguales(T a, T b) {
        return a == null ? b == null : a.equals(b);
    }
}
