/*
=====================================================================================
Práctico 5 - Listas Enlazadas Simples. Ejercicio 3: Obtener un elemento por posición
=====================================================================================

Prompt inicial utilizado:

  "Analizá el diseño del método int obtener(int posicion) sobre una lista enlazada simple, que
  debe devolver el dato almacenado en una posición determinada y validar las posiciones
  inválidas: posicion < 0 y posicion >= size.
  El punto central: explicá que AUNQUE EL MÉTODO RECIBA UNA POSICIÓN, LA LISTA ENLAZADA NO
  TIENE ACCESO DIRECTO COMO UN ARREGLO, y por lo tanto debe recorrerse nodo por nodo.
  Desarrollá:
  - la trampa conceptual del ejercicio: la firma del método se parece a la de un arreglo
    (recibe un índice) y eso puede hacer creer que el costo es el mismo, pero internamente
    hay un recorrido. Un arreglo resuelve esto en O(1) y la lista en O(n). Este contraste es
    lo que evalúa el ejercicio, desarrollalo;
  - cómo se recorre contando: avanzar una variable auxiliar y un contador hasta que el contador
    iguale la posición buscada;
  - por qué conviene VALIDAR LA POSICIÓN ANTES de empezar a recorrer, y no mientras se recorre;
  - por qué las dos validaciones son distintas: posicion < 0 nunca podría encontrarse, y
    posicion >= size significa que la posición existe sintácticamente pero no hay nodo ahí;
  - cómo informar el error: analizá devolver un valor centinela contra lanzar
    IndexOutOfBoundsException, y justificá cuál es mejor para un método que devuelve int
    (un centinela sería indistinguible de un dato válido);
  - qué convención de indexación se usa (base 0) y por qué hay que declararla explícitamente;
  - mejor caso, peor caso y promedio.
  Casos límite: lista vacía, posición 0, última posición (size - 1), posición size,
  posición negativa, lista de un elemento.
  No escribas la implementación."

Ajustes realizados luego de la primera respuesta de OpenCode:

1) La primera respuesta devolvía -1 cuando la posición era inválida. Se cambió por
    IndexOutOfBoundsException: como el método devuelve int, cualquier centinela (-1,
    Integer.MIN_VALUE) es indistinguible de un dato válido guardado en la lista.
 2) La primera versión validaba la posición DENTRO del ciclo de recorrido (chequeaba que
    el auxiliar no fuera null). Se movió la validación ANTES del recorrido: así no se hace
    trabajo inútil y el mensaje de error puede informar el rango válido completo.
 3) Se agregó el contador de nodos recorridos y se dejó documentado el contraste con el
    acceso por índice de un arreglo, incluyendo que recorrer la lista con obtener(i) en un
    for costaría O(n al cuadrado).

Estrategia: validar la posición contra size y después recorrer con un auxiliar y un contador hasta que el contador iguale la posición pedida.

Complejidad temporal de obtener: O(1) si la posición es 0, O(n) si es size - 1, O(n) en
promedio. La validación es O(1) porque size se mantiene actualizado.
Complejidad espacial: O(1) — una referencia auxiliar y un contador entero.
Contraste: un arreglo resuelve el mismo acceso en O(1) con aritmética de direcciones.
=====================================================================================
*/

/**
 * Ejercicio 3 del Práctico 5 (Listas Enlazadas Simples): lectura del dato almacenado en una
 * posición determinada de la lista, con validación previa de la posición.
 *
 * <p>El punto del ejercicio es que recibir una posición NO implica acceso directo: aunque la
 * firma se parezca a {@code vector[i]}, adentro hay un recorrido nodo por nodo.</p>
 *
 * @author Micaela Gimenez  
 * @version 1.0
 */
public class Ejercicio03_ObtenerPorPosicion {

    /**
     * Método principal: ejecuta las pruebas manuales del ejercicio.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Práctico 5 - Ejercicio 3: obtener un elemento por posición ===");

        System.out.println();
        System.out.println("--- 1) Caso límite: obtener sobre una lista vacía ---");
        ListaEnlazada vacia = new ListaEnlazada();
        mostrarEstado("Estado:", vacia);
        probarObtenerConError(vacia, 0);

        System.out.println();
        System.out.println("--- 2) Se construye la lista 10 -> 20 -> 30 -> 40 -> null ---");
        ListaEnlazada lista = new ListaEnlazada();
        lista.insertarAlFinal(10);
        lista.insertarAlFinal(20);
        lista.insertarAlFinal(30);
        lista.insertarAlFinal(40);
        mostrarEstado("Estado:", lista);
        System.out.println("Indexación base 0: las posiciones válidas son 0, 1, 2 y 3.");

        System.out.println();
        System.out.println("--- 3) Posición 0: mejor caso, un solo nodo visitado ---");
        probarObtener(lista, 0);

        System.out.println();
        System.out.println("--- 4) Posición del medio ---");
        probarObtener(lista, 2);

        System.out.println();
        System.out.println("--- 5) Última posición (size - 1): peor caso ---");
        probarObtener(lista, lista.getSize() - 1);

        System.out.println();
        System.out.println("--- 6) Posición size: existe sintácticamente, pero no hay nodo ahí ---");
        probarObtenerConError(lista, lista.getSize());

        System.out.println();
        System.out.println("--- 7) Posición negativa: nunca podría encontrarse ---");
        probarObtenerConError(lista, -1);

        System.out.println();
        System.out.println("--- 8) Lista de un solo elemento ---");
        ListaEnlazada unitaria = new ListaEnlazada();
        unitaria.insertarAlInicio(77);
        mostrarEstado("Estado:", unitaria);
        probarObtener(unitaria, 0);
        probarObtenerConError(unitaria, 1);

        System.out.println();
        System.out.println("--- 9) Costo del acceso secuencial ---");
        System.out.println("obtener(0) visita 1 nodo y obtener(" + (lista.getSize() - 1)
                + ") visita " + lista.getSize() + ": el costo depende de la posición.");
        System.out.println("Un arreglo no tendría esa diferencia: vector[0] y vector[n-1] cuestan lo mismo.");
        mostrarEstado("Lista intacta (obtener es de sólo lectura):", lista);
    }

    /**
     * Lee una posición válida e informa el dato y los nodos recorridos.
     *
     * Complejidad: O(n).
     *
     * @param lista    lista a consultar
     * @param posicion índice en base 0
     */
    private static void probarObtener(ListaEnlazada lista, int posicion) {
        int dato = lista.obtener(posicion);
        System.out.println("obtener(" + posicion + ") = " + dato
                + " | nodos recorridos = " + lista.getNodosVisitados());
    }

    /**
     * Intenta leer una posición inválida y muestra el mensaje de la excepción.
     *
     * Complejidad: O(1), porque la validación corta antes de recorrer.
     *
     * @param lista    lista a consultar
     * @param posicion índice en base 0 que se espera inválido
     */
    private static void probarObtenerConError(ListaEnlazada lista, int posicion) {
        try {
            int dato = lista.obtener(posicion);
            System.out.println("obtener(" + posicion + ") = " + dato + " (no se esperaba éxito)");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("obtener(" + posicion + ") lanzó IndexOutOfBoundsException: "
                    + e.getMessage());
        }
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
 * <p>Métodos disponibles en este ejercicio: insertarAlInicio, insertarAlFinal, imprimir, estaVacia, getSize, buscar e obtener.</p>
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


