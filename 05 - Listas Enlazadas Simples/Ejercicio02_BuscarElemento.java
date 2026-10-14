/*
=====================================================================================
Práctico 5 - Listas Enlazadas Simples. Ejercicio 2: Buscar elementos en una lista enlazada
=====================================================================================

Prompt inicial utilizado:

  "Analizá el diseño del método boolean buscar(int dato) sobre una lista enlazada simple de
  enteros, que debe recorrer la lista desde head hasta encontrar el dato o llegar a null.
  El punto central: explicá POR QUÉ NO SE PUEDE ACCEDER DIRECTAMENTE POR ÍNDICE y POR QUÉ LA
  BÚSQUEDA REQUIERE RECORRIDO SECUENCIAL. Desarrollá:
  - qué permite el acceso directo en un arreglo: los elementos están en memoria contigua, así
    que la dirección del elemento i se calcula aritméticamente en un paso, O(1);
  - por qué en una lista enlazada eso es imposible: los nodos están dispersos en memoria y la
    única forma de conocer la dirección de un nodo es preguntándosela al nodo anterior. No hay
    ninguna fórmula que permita saltar. Desarrollá este punto con detalle, es lo que evalúa
    el ejercicio;
  - la consecuencia: para llegar al nodo i hay que pasar obligatoriamente por los i-1 anteriores;
  - por qué, en consecuencia, NO se puede aplicar búsqueda binaria sobre una lista enlazada
    aunque esté ordenada. Este corolario es importante, incluilo;
  - cómo se implementa el recorrido con una variable auxiliar de tipo Nodo que avanza, y por
    qué NO se debe usar head directamente como variable de recorrido (se perdería la lista);
  - la condición de corte del recorrido y por qué hay dos: encontró el dato, o llegó a null;
  - mejor caso, peor caso y caso promedio.
  Casos límite: lista vacía, dato en el primer nodo, en el último, dato inexistente, dato
  repetido (aclarar que devuelve true en la primera aparición).
  No escribas la implementación."

Ajustes realizados luego de la primera respuesta de OpenCode:

1) La primera respuesta resolvía la búsqueda con un bucle que avanzaba head. Se exigió
    una variable auxiliar de tipo Nodo: al terminar, head habría quedado en null y la
    lista entera se habría vuelto inalcanzable después de la primera búsqueda.
 2) Se agregó un contador de nodos visitados, accesible con getNodosVisitados(), para
    poder OBSERVAR el costo del recorrido secuencial en lugar de afirmarlo. Sin ese dato
    el main no demostraba la diferencia entre el mejor y el peor caso.
 3) Se pidió documentar explícitamente el corolario de que la búsqueda binaria no es
    aplicable a una lista enlazada aunque esté ordenada; la primera respuesta sólo
    mencionaba que el acceso no es directo, sin sacar esa conclusión.

Estrategia: recorrido secuencial desde head con una variable auxiliar y corte anticipado en la primera coincidencia (problema de existencia).

Complejidad temporal de buscar: O(1) en el mejor caso (dato en el primer nodo), O(n) en
el peor (último nodo o dato inexistente) y O(n) en promedio, con (n+1)/2 nodos visitados
cuando el dato está presente.
Complejidad espacial: O(1) — una única referencia auxiliar, independientemente de n.
=====================================================================================
*/

/**
 * Ejercicio 2 del Práctico 5 (Listas Enlazadas Simples): búsqueda de un dato en una lista
 * enlazada simple de enteros mediante recorrido secuencial desde {@code head}.
 *
 * <p>El {@code main} busca en una lista vacía, el primer elemento, uno del medio, el último y
 * uno inexistente, mostrando en cada caso el resultado y la cantidad de nodos visitados para
 * hacer visible el costo del acceso secuencial.</p>
 *
 * @author Micaela Gimenez
 * @version 1.0
 */
public class Ejercicio02_BuscarElemento {

    /**
     * Método principal: ejecuta las pruebas manuales del ejercicio.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Práctico 5 - Ejercicio 2: buscar elementos en una lista enlazada ===");

        ListaEnlazada lista = new ListaEnlazada();

        System.out.println();
        System.out.println("--- 1) Caso límite: buscar en una lista vacía ---");
        mostrarEstado("Estado:", lista);
        probarBusqueda(lista, 10);
        System.out.println("    (head ya era null, así que el ciclo no se ejecutó ni una vez)");

        System.out.println();
        System.out.println("--- 2) Se construye la lista 10 -> 20 -> 30 -> 40 -> null ---");
        lista.insertarAlFinal(10);
        lista.insertarAlFinal(20);
        lista.insertarAlFinal(30);
        lista.insertarAlFinal(40);
        mostrarEstado("Estado:", lista);

        System.out.println();
        System.out.println("--- 3) Mejor caso: el dato está en el primer nodo ---");
        probarBusqueda(lista, 10);

        System.out.println();
        System.out.println("--- 4) Caso intermedio: el dato está en el medio ---");
        probarBusqueda(lista, 30);

        System.out.println();
        System.out.println("--- 5) Peor caso con éxito: el dato está en el último nodo ---");
        probarBusqueda(lista, 40);

        System.out.println();
        System.out.println("--- 6) Peor caso sin éxito: el dato no existe ---");
        probarBusqueda(lista, 99);
        System.out.println("    (para afirmar que NO está hay que haber visitado los n nodos)");

        System.out.println();
        System.out.println("--- 7) Dato repetido: buscar devuelve true en la primera aparición ---");
        ListaEnlazada repetidos = new ListaEnlazada();
        repetidos.insertarAlFinal(7);
        repetidos.insertarAlFinal(7);
        repetidos.insertarAlFinal(7);
        mostrarEstado("Estado:", repetidos);
        probarBusqueda(repetidos, 7);

        System.out.println();
        System.out.println("--- 8) Verificación de consistencia ---");
        System.out.println("buscar() es una operación de sólo lectura: no toca enlaces ni size.");
        mostrarEstado("Lista original intacta:", lista);
    }

    /**
     * Ejecuta una búsqueda e informa el resultado junto con los nodos visitados.
     *
     * Complejidad: O(n) por la búsqueda que delega.
     *
     * @param lista lista donde buscar
     * @param dato  entero buscado
     */
    private static void probarBusqueda(ListaEnlazada lista, int dato) {
        boolean encontrado = lista.buscar(dato);
        System.out.println("buscar(" + dato + ") = " + encontrado
                + " | nodos visitados = " + lista.getNodosVisitados()
                + " de " + lista.getSize());
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
 * <p>Métodos disponibles en este ejercicio: insertarAlInicio, insertarAlFinal, imprimir, estaVacia, getSize e buscar.</p>
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


