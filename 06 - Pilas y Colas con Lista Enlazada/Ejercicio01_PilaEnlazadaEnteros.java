/*
 * ============================================================================
 * Ejercicio 1 - Pila enlazada de enteros
 * Práctico 6 - Pilas y Colas con Listas Enlazadas
 * ============================================================================
 *
 * Prompt inicial utilizado (transcripción literal del prompt del Agente Analista):
 *
 *   Analizá el diseño de una PILA implementada con lista enlazada simple, que debe
 *   permitir apilar, desapilar, consultar el tope, verificar si está vacía, buscar si
 *   contiene un valor e imprimir su contenido.
 *   El punto central: explicá POR QUÉ EL HEAD REPRESENTA EL TOPE DE LA PILA y POR QUÉ
 *   PUSH Y POP DEBEN RESOLVERSE EN O(1). Desarrollá:
 *   - qué estructura se necesita y por qué corresponde una pila (política LIFO);
 *   - cómo funciona internamente: nodos enlazados donde head es el único punto de acceso;
 *   - por qué head es la elección natural para el tope: insertar y eliminar al INICIO de
 *     una lista enlazada no requiere ningún recorrido, porque head ya apunta ahí.
 *     Desarrollá esto;
 *   - qué pasaría si se eligiera el ÚLTIMO nodo como tope: habría que recorrer toda la
 *     lista en cada push y en cada pop para llegar al final, y además, en una lista
 *     SIMPLEMENTE enlazada, el pop necesitaría el nodo ANTERIOR al último, que tampoco se
 *     puede alcanzar sin recorrer. Las operaciones pasarían de O(1) a O(n). Este contraste
 *     es lo que evalúa el ejercicio, desarrollalo con detalle;
 *   - por qué la política LIFO encaja perfecto con las operaciones baratas de una lista
 *     enlazada;
 *   - el orden exacto de actualización de referencias en push (conectar el nodo nuevo a
 *     head y después mover head) y en pop (guardar el dato, mover head al siguiente), y
 *     qué pasa si se invierte;
 *   - la VENTAJA PRINCIPAL sobre la pila con arreglo: no hay capacidad fija, así que no
 *     existe el estado "llena" ni el método isFull. Explicá qué se gana y qué se paga
 *     (memoria extra por cada referencia, y pérdida de localidad de memoria);
 *   - por qué buscar() es O(n) mientras push, pop y peek son O(1), y por qué eso es
 *     aceptable (buscar no es una operación propia de una pila);
 *   - la complejidad esperada de cada operación.
 *   Casos especiales: pila vacía, pop en pila vacía, peek en pila vacía, pila con un solo
 *   elemento, imprimir pila vacía. Definí si se lanza excepción o se devuelve un centinela
 *   y justificá.
 *   Decidí si conviene mantener un atributo size y justificá.
 *   No escribas la implementación.
 *
 * Ajustes realizados posteriormente al prompt inicial:
 *   1) Se agregó el método tamanio() y el atributo size. El análisis dejaba la decisión
 *      abierta: se resolvió mantener size porque sin él tamanio() sería O(n) (habría que
 *      recorrer la cadena) y porque estaVacia() se vuelve verificable por dos vías
 *      independientes (head == null y size == 0), lo que sirve de invariante de control.
 *   2) Se fijó el formato de imprimir() como "TOPE -> 30 -> 20 -> 10 -> null" para que la
 *      salida muestre visualmente que head es el tope y que el fondo termina en null.
 *   3) Se agregó la prueba de 1000 apilados, que el prompt inicial no pedía, para
 *      demostrar empíricamente que no existe capacidad fija ni estado "llena".
 *   No hubo ajustes sobre el diseño de referencias ni sobre las complejidades: el análisis
 *   ya fijaba head como tope y O(1) para apilar/desapilar/verTope, y eso se implementó tal
 *   cual.
 *
 * Estrategia: pila LIFO sobre lista simplemente enlazada donde head ES el tope, de modo
 * que apilar y desapilar sólo reescriben referencias en el extremo que head ya alcanza.
 *
 * Complejidad esperada de las operaciones principales:
 *   apilar    O(1)        desapilar  O(1)        verTope   O(1)
 *   estaVacia O(1)        tamanio    O(1)        contiene  O(n)        imprimir O(n)
 *   Espacial: O(n) nodos, cada uno con un int y una referencia.
 *
 * DIFERENCIA CON EL PRÁCTICO 4 (pila sobre arreglo): acá NO EXISTE isFull(). Una pila
 * sobre arreglo tiene capacidad fija y necesita preguntar "¿está llena?" antes de cada
 * apilado, porque el arreglo se reserva entero de una vez. En la versión enlazada cada
 * apilado reserva UN nodo nuevo en el Heap en el momento en que se necesita, así que el
 * único límite es la memoria disponible de la JVM: el estado "llena" no es representable
 * y el método no tiene sentido. Lo que se gana: nada de capacidad fija y nada de
 * redimensionado. Lo que se paga: 1 referencia extra (8 bytes en una JVM de 64 bits) y la
 * cabecera de objeto por cada elemento, más la pérdida de localidad de memoria (los nodos
 * quedan dispersos en el Heap, mientras un arreglo es contiguo y aprovecha mejor la caché).
 *
 * @author Micaela Gimenez
 * @version 1.0
 */

/**
 * Nodo de la lista simplemente enlazada que soporta la pila.
 *
 * <p>Guarda un entero y UNA sola referencia al nodo siguiente. Esa única referencia es
 * suficiente para una pila porque la pila opera en un solo extremo: nunca hace falta
 * retroceder.</p>
 */
class Nodo {

    /** Valor almacenado en este nodo. */
    int dato;

    /** Referencia al nodo que sigue hacia el fondo de la pila; null si es el fondo. */
    Nodo siguiente;

    /**
     * Crea un nodo suelto, todavía no enlazado a ninguna pila.
     *
     * @param dato valor entero que guarda el nodo
     */
    Nodo(int dato) {
        this.dato = dato;
        this.siguiente = null;
    }
}

/**
 * Pila de enteros (política LIFO) implementada sobre una lista simplemente enlazada.
 *
 * <p><b>¿Por qué head es el tope?</b> En una lista enlazada, head es el único punto de
 * entrada: es la única referencia que la estructura conoce sin recorrer nada. Insertar y
 * eliminar EN head cuesta un par de asignaciones, porque ya se tiene la referencia al
 * lugar donde hay que operar. Como una pila sólo necesita trabajar en un extremo (el
 * último que entró es el primero que sale), hacer coincidir ese extremo con head es la
 * elección natural y da apilar/desapilar en O(1).</p>
 *
 * <p><b>¿Y si el tope fuera el ÚLTIMO nodo?</b> Para apilar habría que recorrer la cadena
 * entera hasta encontrar el nodo cuyo siguiente es null: O(n). Para desapilar sería peor:
 * hay que poner en null el campo siguiente del nodo ANTERIOR al último, y en una lista
 * SIMPLEMENTE enlazada ese anterior no se puede alcanzar desde el último nodo (no hay
 * referencia hacia atrás), así que también habría que recorrer desde head: O(n). Las dos
 * operaciones centrales de la pila pasarían de O(1) a O(n).</p>
 *
 * <p>No existe isFull(): no hay capacidad fija. Ver la cabecera del archivo.</p>
 */
class PilaEnlazada {

    /** Tope de la pila: primer y único nodo accesible directamente. null si está vacía. */
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
     * Apila un entero, que queda como nuevo tope.
     *
     * <p>Orden de actualización de referencias (NO es intercambiable):
     * primero el nodo nuevo apunta al head actual, y sólo DESPUÉS head pasa a apuntar al
     * nodo nuevo. Si se invirtiera (head = nuevo y después nuevo.siguiente = head), el
     * nodo nuevo terminaría apuntándose a sí mismo y el resto de la pila quedaría
     * inalcanzable: imprimir() entraría en un ciclo infinito.</p>
     *
     * <p>Complejidad: O(1) - dos asignaciones, sin recorrer ningún nodo.</p>
     *
     * @param dato valor entero a apilar
     */
    void apilar(int dato) {
        Nodo nuevo = new Nodo(dato);
        nuevo.siguiente = head; // 1) enganchar el nodo nuevo al tope actual
        head = nuevo;           // 2) recién ahora head puede moverse al nodo nuevo
        size++;
    }

    /**
     * Quita y devuelve el elemento del tope.
     *
     * <p>Orden de actualización: primero se GUARDA el dato en una variable local, y
     * después se mueve head al siguiente. Si se moviera head primero, el dato quedaría
     * inalcanzable y no se podría devolver.</p>
     *
     * <p>Complejidad: O(1) - no se recorre nada, head ya apunta al nodo a eliminar.</p>
     *
     * @return el entero que estaba en el tope
     * @throws IllegalStateException si la pila está vacía
     */
    int desapilar() {
        // Se lanza excepción en vez de devolver un centinela (-1, 0, Integer.MIN_VALUE)
        // porque CUALQUIER int es un dato válido para esta pila: un centinela sería
        // indistinguible de un dato real y ocultaría el error.
        if (estaVacia()) {
            throw new IllegalStateException("No se puede desapilar: la pila está vacía.");
        }
        int dato = head.dato;    // 1) rescatar el dato antes de perder la referencia
        head = head.siguiente;   // 2) el nodo desapilado queda sin referencias -> el GC lo recupera
        size--;
        return dato;
    }

    /**
     * Consulta el elemento del tope sin quitarlo.
     *
     * <p>Complejidad: O(1) - lectura directa de head.dato.</p>
     *
     * @return el entero que está en el tope
     * @throws IllegalStateException si la pila está vacía
     */
    int verTope() {
        if (estaVacia()) {
            throw new IllegalStateException("No se puede consultar el tope: la pila está vacía.");
        }
        return head.dato;
    }

    /**
     * Indica si la pila no tiene elementos.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return true si la pila está vacía, false en caso contrario
     */
    boolean estaVacia() {
        // head == null es la condición autoritativa; size es un espejo que se mantiene
        // sincronizado y permite detectar una inconsistencia interna.
        return head == null;
    }

    /**
     * Informa si un valor está en algún lugar de la pila.
     *
     * <p>Complejidad: O(n) - es la única operación que RECORRE la cadena, porque el valor
     * puede estar en cualquier profundidad y los nodos no están ordenados ni indexados.
     * Esa complejidad es aceptable: buscar no es una operación propia del contrato de una
     * pila (una pila sólo promete acceso al tope); se agrega como utilidad de inspección.</p>
     *
     * @param dato valor a buscar
     * @return true si el valor está apilado, false si no
     */
    boolean contiene(int dato) {
        Nodo actual = head;
        while (actual != null) {
            // Con int se compara con ==, que compara VALORES. En la versión genérica
            // (ejercicio 3) hay que pasar a equals(), porque con objetos == compararía
            // identidad de referencia.
            if (actual.dato == dato) {
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    /**
     * Cantidad de elementos apilados.
     *
     * <p>Complejidad: O(1) gracias al atributo size. Sin ese atributo habría que recorrer
     * la cadena entera y sería O(n).</p>
     *
     * @return número de elementos en la pila
     */
    int tamanio() {
        return size;
    }

    /**
     * Imprime la pila desde el tope hacia el fondo, en formato
     * {@code TOPE -> 30 -> 20 -> 10 -> null}.
     *
     * <p>El recorrido arranca en head justamente porque head es el tope: el orden en que
     * aparecen los nodos es el orden en que van a salir.</p>
     *
     * <p>Complejidad: O(n) - visita los n nodos una vez.</p>
     */
    void imprimir() {
        StringBuilder salida = new StringBuilder("TOPE");
        Nodo actual = head;
        while (actual != null) {
            salida.append(" -> ").append(actual.dato);
            actual = actual.siguiente;
        }
        salida.append(" -> null");
        if (estaVacia()) {
            salida.append("   (pila vacía)");
        }
        System.out.println("  " + salida + "   [tamanio=" + size + "]");
    }

    /**
     * Devuelve a qué dato apunta head, o la palabra null si la pila está vacía.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return representación textual del dato del tope
     */
    String estadoHead() {
        return estaVacia() ? "null" : String.valueOf(head.dato);
    }
}

/**
 * Programa de demostración del ejercicio 1: pila de enteros sobre lista enlazada simple.
 *
 * <p>Recorre todos los casos especiales del análisis (pila vacía, desapilar y consultar
 * tope con la pila vacía, pila de un solo elemento, imprimir pila vacía) capturando las
 * excepciones, y cierra con una prueba de 1000 apilados que demuestra que no hay límite
 * de capacidad.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio01_PilaEnlazadaEnteros {

    /** Cantidad de elementos de la prueba de capacidad ilimitada. */
    private static final int ELEMENTOS_PRUEBA_CAPACIDAD = 1000;

    /**
     * Método principal: ejecuta la demostración completa.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 1 - Pila enlazada de enteros (LIFO) ===");

        PilaEnlazada pila = new PilaEnlazada();

        System.out.println("\n--- Caso: pila recién creada (vacía) ---");
        System.out.println("  estaVacia() = " + pila.estaVacia() + " | tamanio() = " + pila.tamanio());
        System.out.print("  imprimir() de pila vacía:");
        System.out.println();
        pila.imprimir();

        System.out.println("\n--- Caso: desapilar y verTope con la pila VACÍA (errores forzados) ---");
        intentarDesapilar(pila);
        intentarVerTope(pila);

        System.out.println("\n--- Caso: pila con UN solo elemento ---");
        apilarMostrando(pila, 10);
        System.out.println("  verTope() = " + pila.verTope() + " (el único elemento es a la vez tope y fondo)");

        System.out.println("\n--- Flujo nominal: apilar 20 y 30 (traza del análisis) ---");
        apilarMostrando(pila, 20);
        apilarMostrando(pila, 30);
        System.out.println("  verTope() = " + pila.verTope() + " -> el último en entrar es el primero en salir (LIFO)");

        System.out.println("\n--- Búsquedas: contiene() recorre la cadena, es O(n) ---");
        System.out.println("  contiene(10) = " + pila.contiene(10) + " (está en el fondo: se recorren los 3 nodos)");
        System.out.println("  contiene(30) = " + pila.contiene(30) + " (está en el tope: corta en el primer nodo)");
        System.out.println("  contiene(99) = " + pila.contiene(99) + " (ausente: recorre los 3 nodos y devuelve false)");

        System.out.println("\n--- Desapilados sucesivos: el orden de salida es el inverso al de entrada ---");
        desapilarMostrando(pila);
        desapilarMostrando(pila);
        System.out.println("  verTope() = " + pila.verTope() + " (volvió a quedar un solo elemento)");
        desapilarMostrando(pila);

        System.out.println("\n--- Caso: la pila volvió a quedar vacía y se vuelve a apilar ---");
        System.out.println("  estaVacia() = " + pila.estaVacia() + " | head -> " + pila.estadoHead());
        intentarDesapilar(pila);
        apilarMostrando(pila, 77);
        System.out.println("  El elemento nuevo es accesible: verTope() = " + pila.verTope());

        pruebaCapacidadIlimitada();

        System.out.println("\n=== Fin de la demostración ===");
    }

    /**
     * Apila un valor e informa el estado completo de la pila después de la operación.
     *
     * @param pila pila sobre la que se opera
     * @param dato valor a apilar
     */
    private static void apilarMostrando(PilaEnlazada pila, int dato) {
        pila.apilar(dato);
        System.out.println("  apilar(" + dato + ")  ->  head -> " + pila.estadoHead());
        pila.imprimir();
    }

    /**
     * Desapila un valor e informa el estado completo de la pila después de la operación.
     *
     * @param pila pila sobre la que se opera
     */
    private static void desapilarMostrando(PilaEnlazada pila) {
        int dato = pila.desapilar();
        System.out.println("  desapilar() = " + dato + "  ->  head -> " + pila.estadoHead());
        pila.imprimir();
    }

    /**
     * Intenta desapilar capturando la excepción esperada cuando la pila está vacía.
     *
     * @param pila pila sobre la que se opera
     */
    private static void intentarDesapilar(PilaEnlazada pila) {
        try {
            System.out.println("  desapilar() = " + pila.desapilar());
        } catch (IllegalStateException e) {
            System.out.println("  desapilar() lanzó IllegalStateException: " + e.getMessage() + "  -> OK");
        }
    }

    /**
     * Intenta consultar el tope capturando la excepción esperada cuando la pila está vacía.
     *
     * @param pila pila sobre la que se opera
     */
    private static void intentarVerTope(PilaEnlazada pila) {
        try {
            System.out.println("  verTope() = " + pila.verTope());
        } catch (IllegalStateException e) {
            System.out.println("  verTope() lanzó IllegalStateException: " + e.getMessage() + "  -> OK");
        }
    }

    /**
     * Apila 1000 enteros para demostrar que la pila enlazada no tiene capacidad fija.
     *
     * <p>En la versión sobre arreglo del práctico 4 este bucle fallaría (o dispararía un
     * redimensionado) en cuanto se superara la capacidad reservada. Acá cada apilado
     * reserva un nodo nuevo, así que no hay estado "llena" ni método isFull.</p>
     *
     * <p>Complejidad: O(n) apilados de O(1) cada uno.</p>
     */
    private static void pruebaCapacidadIlimitada() {
        System.out.println("\n--- Prueba de capacidad: apilar " + ELEMENTOS_PRUEBA_CAPACIDAD
                + " elementos (no existe isFull) ---");
        PilaEnlazada grande = new PilaEnlazada();
        for (int i = 1; i <= ELEMENTOS_PRUEBA_CAPACIDAD; i++) {
            grande.apilar(i);
        }
        System.out.println("  tamanio() = " + grande.tamanio()
                + " | verTope() = " + grande.verTope() + " (el último apilado)");
        System.out.println("  Se desapilan los 3 primeros: " + grande.desapilar() + ", "
                + grande.desapilar() + ", " + grande.desapilar()
                + " -> salen en orden inverso al de entrada");
        System.out.println("  tamanio() tras los 3 desapilados = " + grande.tamanio());
        boolean esperado = grande.tamanio() == ELEMENTOS_PRUEBA_CAPACIDAD - 3;
        System.out.println("  Ningún apilado falló por capacidad -> " + (esperado ? "OK" : "FALLO"));
    }
}


