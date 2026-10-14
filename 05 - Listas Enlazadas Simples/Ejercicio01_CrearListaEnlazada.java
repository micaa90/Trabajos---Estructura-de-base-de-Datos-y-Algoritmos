/*
=====================================================================================
Práctico 5 - Listas Enlazadas Simples. Ejercicio 1: Crear una lista enlazada simple desde cero
=====================================================================================

Prompt inicial utilizado:

  "Analizá el diseño de una lista enlazada simple de números enteros, implementada desde cero,
  SIN usar ArrayList, LinkedList ni ninguna estructura de la librería de Java.
  Debe permitir: insertarAlInicio(int dato), insertarAlFinal(int dato), imprimir(),
  estaVacia() y getSize().
  Tu análisis debe explicar:
  - CÓMO SE REPRESENTA UN NODO: qué atributos tiene (el dato y la referencia al siguiente),
    por qué el nodo necesita conocer al siguiente, y qué significa que el último nodo tenga
    siguiente igual a null. Desarrollá este punto;
  - QUÉ FUNCIÓN CUMPLE head: por qué es el único punto de entrada a la estructura, por qué sin
    head la lista entera se vuelve inaccesible, y qué significa que head sea null;
  - la diferencia conceptual entre un arreglo y una lista enlazada: memoria contigua con acceso
    por índice contra nodos dispersos unidos por referencias; qué gana y qué pierde cada uno;
  - insertarAlInicio: cuál es el orden correcto de las dos asignaciones de referencias y qué
    pasaría si se invierte (se perdería el resto de la lista). Explicá por qué esta operación
    es O(1);
  - insertarAlFinal: por qué requiere recorrer hasta el último nodo, y por qué es O(n) salvo
    que se mantenga una referencia tail. Mencioná esa optimización y si conviene acá;
  - CÓMO SE ACTUALIZA EL TAMAÑO: por qué conviene mantener un atributo size en lugar de contar
    los nodos cada vez que se consulta, y qué complejidad tiene cada opción;
  - los invariantes de la estructura (relación entre head, size y la cadena de nodos);
  - la complejidad de cada operación.
  Casos límite: lista vacía, insertar el primer elemento (tanto al inicio como al final, y por
  qué ese caso es especial en insertarAlFinal), imprimir una lista vacía, un solo elemento.
  Definí si Nodo debe ser clase interna o clase aparte, y justificá.
  No escribas la implementación."

Ajustes realizados luego de la primera respuesta de OpenCode:

1) La primera respuesta imprimía la lista como "10, 20, 30". Se pidió el formato
    10 -> 20 -> 30 -> null para que se vean los ENLACES y el null que marca el fin de la
    cadena, que es justamente lo que distingue a una lista enlazada de un arreglo.
 2) La primera versión usaba head como variable de recorrido dentro de insertarAlFinal.
    Se exigió una variable auxiliar de tipo Nodo: mover head habría dejado la lista
    inalcanzable después de la primera inserción.
 3) Se agregó el método contarNodosEnlazados() y una verificación en el main que compara
    ese recuento con el atributo size después de cada inserción, para demostrar que size
    no se desincroniza de la estructura.

Estrategia: cadena de objetos Nodo unidos por referencias, con head como único punto de entrada y un atributo size mantenido en cada inserción.

Complejidad temporal: insertarAlInicio O(1) (dos asignaciones, sin recorrido);
insertarAlFinal O(n) (hay que llegar al último nodo); imprimir O(n); estaVacia y getSize
O(1).
Complejidad espacial: O(n) por los n nodos del Heap; O(1) adicional por operación (una
sola referencia auxiliar de recorrido).
=====================================================================================
*/

/**
 * Ejercicio 1 del Práctico 5 (Listas Enlazadas Simples): construcción de una lista enlazada
 * simple de enteros desde cero, con inserción al inicio, inserción al final, impresión,
 * consulta de vacío y consulta de tamaño.
 *
 * <p>El {@code main} crea una lista vacía, la imprime, hace tres inserciones al final y dos
 * al inicio, y después de cada una muestra el {@code size} junto con el recuento real de
 * nodos enlazados para verificar que coinciden.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio01_CrearListaEnlazada {

    /**
     * Método principal: ejecuta las pruebas manuales del ejercicio.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Práctico 5 - Ejercicio 1: crear una lista enlazada simple desde cero ===");

        ListaEnlazada lista = new ListaEnlazada();

        System.out.println();
        System.out.println("--- 1) Lista recién creada: caso límite de la lista vacía ---");
        System.out.print("imprimir() sobre lista vacía: ");
        lista.imprimir(); // head == null: no hay ningún dato, se imprime sólo el null final
        System.out.println("estaVacia() = " + lista.estaVacia()
                + " | getSize() = " + lista.getSize());

        System.out.println();
        System.out.println("--- 2) Tres inserciones al final (cada una recorre hasta el último nodo) ---");
        lista.insertarAlFinal(10); // caso especial: lista vacía, el nuevo nodo pasa a ser head
        mostrarEstado("insertarAlFinal(10) ->", lista);
        lista.insertarAlFinal(20);
        mostrarEstado("insertarAlFinal(20) ->", lista);
        lista.insertarAlFinal(30);
        mostrarEstado("insertarAlFinal(30) ->", lista);

        System.out.println();
        System.out.println("--- 3) Dos inserciones al inicio (O(1): sólo se mueve head) ---");
        lista.insertarAlInicio(5);
        mostrarEstado("insertarAlInicio(5) ->", lista);
        lista.insertarAlInicio(1);
        mostrarEstado("insertarAlInicio(1) ->", lista);

        System.out.println();
        System.out.println("--- 4) Verificación final de los invariantes ---");
        System.out.println("estaVacia() = " + lista.estaVacia() + " (false: head ya no es null)");
        System.out.println("getSize() = " + lista.getSize()
                + " | nodos realmente enlazados = " + lista.contarNodosEnlazados());
        System.out.println("size coincide con la cantidad real de nodos: "
                + (lista.getSize() == lista.contarNodosEnlazados()));
        System.out.print("Estado final: ");
        lista.imprimir();

        System.out.println();
        System.out.println("Nota: en este ejercicio ningún método valida posiciones ni lanza");
        System.out.println("excepciones, porque insertar al inicio o al final siempre es válido.");
        System.out.println("Las excepciones de posición aparecen desde el ejercicio 3.");
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
 * <p>Métodos disponibles en este ejercicio: insertarAlInicio, insertarAlFinal, imprimir, estaVacia e getSize.</p>
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
}

/*
MATRIZ DE CASOS DE PRUEBA MANUALES
Compilación: javac Ejercicio01_CrearListaEnlazada.java -d /tmp/v5/ej01
Ejecución:   java -cp /tmp/v5/ej01 Ejercicio01_CrearListaEnlazada
Todos los casos están cableados en main(): el programa no lee entrada por consola,
así que cada caso se verifica leyendo el bloque correspondiente de la salida.

ID    | Objetivo                                         | Entrada exacta                                  | Resultado esperado                                   | Criterio de aprobación
------+--------------------------------------------------+-------------------------------------------------+------------------------------------------------------+------------------------------------------------
CP-01 | Flujo nominal: construir la lista completa       | main(): 10, 20, 30 al final y 5, 1 al inicio    | Estado final "1 -> 5 -> 10 -> 20 -> 30 -> null"      | La última línea del bloque 4 imprime esa cadena
CP-02 | Lista vacía: imprimir y consultar sin romper     | main() bloque 1, sin ninguna inserción          | "null", estaVacia() = true, getSize() = 0            | No se lanza excepción y se imprime exactamente "null"
CP-03 | Primer nodo: insertarAlInicio mueve head        | main() bloque 3, insertarAlInicio(1)            | "1 -> 5 -> 10 -> 20 -> 30 -> null", size = 5         | El 1 aparece al principio y el resto queda intacto
CP-04 | Último nodo: insertarAlFinal con lista vacía    | main() bloque 2, insertarAlFinal(10)            | "10 -> null", size = 1                               | El nodo nuevo es a la vez primero y último
CP-05 | Consistencia de size (reemplaza al caso de      | main() bloque 4, getSize() contra               | Ambos valen 5 y se imprime "true"                    | Toda línea de estado termina en "[consistente]"
      | posición inválida, que todavía no aplica)       | contarNodosEnlazados()                          |                                                      |
*/
