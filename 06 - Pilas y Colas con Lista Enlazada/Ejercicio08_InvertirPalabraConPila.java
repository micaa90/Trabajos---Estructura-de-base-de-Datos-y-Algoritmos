/*
 * =====================================================================================
 * Práctico 6 — Pilas y Colas con Listas Enlazadas. Ejercicio 8: Invertir una palabra con pila
 * =====================================================================================
 * Prompt inicial utilizado:
 * "Analizá este problema: invertir una palabra usando una PILA ENLAZADA.
 *  Ejemplo: 'algoritmo' debe dar 'omtirogla'.
 *  El punto central: explicá POR QUÉ LA POLÍTICA LIFO PERMITE INVERTIR EL ORDEN DE LOS
 *  CARACTERES. Desarrollá:
 *  - el mecanismo en dos fases: primero se apilan TODOS los caracteres de izquierda a derecha, y
 *    después se desapilan todos. Explicá que la inversión no la hace ningún algoritmo explícito:
 *    es un EFECTO de la política LIFO;
 *  - por qué el primer carácter apilado queda en el fondo y por lo tanto sale último, y por qué
 *    el último apilado queda en el tope y sale primero;
 *  - hacé esta observación valiosa: la pila funciona acá como un 'inversor de orden' genérico, y
 *    ese mismo mecanismo es el que explica por qué una recursión produce resultados en orden
 *    inverso (la pila de llamadas es, literalmente, una pila);
 *  - qué pasaría con una cola: los caracteres saldrían en el mismo orden en que entraron y la
 *    palabra quedaría igual. Explicá que una cola PRESERVA el orden y una pila lo INVIERTE, y
 *    que esa es la diferencia operativa fundamental entre las dos estructuras;
 *  - por qué conviene una pila enlazada: la longitud de la palabra no se conoce de antemano;
 *  - la complejidad temporal (dos recorridos completos) y espacial (la pila guarda n caracteres),
 *    y comparalo con la solución de dos índices sobre un arreglo de chars, que sería O(1) en
 *    espacio. Aclará que acá el objetivo es didáctico: mostrar la propiedad de la pila.
 *  Casos especiales: cadena vacía, un solo carácter, palabra palíndroma (el resultado es igual
 *  a la entrada), palabra con espacios, palabra con caracteres repetidos.
 *  No escribas la implementación."
 *
 * Ajustes realizados:
 *  1) Se agregó la comparación explícita contra la cola. La primera respuesta explicaba LIFO
 *     correctamente pero sin contrastarlo, y así "la pila invierte" quedaba como un dato a
 *     memorizar en lugar de una consecuencia. Procesando la misma palabra con las dos
 *     estructuras se ve que la cola devuelve la palabra intacta: la diferencia no está en el
 *     recorrido, que es idéntico, sino en la política de salida. Por eso el programa incluye
 *     una cola enlazada además de la pila, solo para la demostración.
 *  2) Se agregó la relación con la pila de llamadas de una función recursiva. La primera
 *     respuesta trataba este ejercicio como un truco aislado. En realidad es el mismo mecanismo
 *     que hace que el ejercicio 8 del práctico de recursividad reconstruya la cadena al
 *     desapilar: acá la pila es explícita, allá es la de la JVM.
 *  3) Se pidió mostrar las dos fases por separado con el estado de la pila. La primera
 *     respuesta imprimía solo la palabra invertida, con lo que el mecanismo quedaba invisible:
 *     el resultado era correcto pero no demostraba nada sobre la estructura.
 *
 * Estrategia: dos fases sobre una pila enlazada de caracteres. Fase 1, se apila cada carácter
 * de izquierda a derecha, por lo que el primero queda en el fondo. Fase 2, se desapila hasta
 * vaciarla y se concatena en ese orden, que es el inverso del de entrada. La inversión es un
 * efecto de la política LIFO, no de un algoritmo de inversión.
 * Complejidad temporal: O(n), dos recorridos completos de n operaciones O(1) cada una.
 * Complejidad espacial: O(n), porque la pila mantiene vivos los n caracteres a la vez. La
 * alternativa de dos índices sobre un char[] sería O(1) en espacio; acá el objetivo es
 * didáctico, mostrar la propiedad de la estructura.
 * =====================================================================================
 */

/**
 * Inversión de una palabra usando una pila enlazada de caracteres, con las dos fases visibles.
 *
 * <p>No hay ningún algoritmo de inversión en este programa. Los caracteres se guardan en el
 * orden natural y se recuperan en el único orden que una pila permite: el inverso. La inversión
 * la produce la estructura.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio08_InvertirPalabraConPila {

    /**
     * Punto de entrada. Invierte las palabras de prueba y demuestra el contraste con una cola.
     *
     * <p>Complejidad: O(n) por cada palabra procesada.</p>
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 8 - Invertir una palabra con pila enlazada ===");

        System.out.println("\n--- Caso del enunciado, con las dos fases visibles ---");
        String resultado = invertirMostrandoFases("algoritmo");
        comprobar("algoritmo", resultado, "omtirogla");

        System.out.println("\n--- Casos especiales ---");
        comprobar("", invertir(""), "");
        comprobar("a", invertir("a"), "a");
        comprobar("ana", invertir("ana"), "ana");
        comprobar("oso", invertir("oso"), "oso");
        comprobar("aabb", invertir("aabb"), "bbaa");
        comprobar("hola mundo", invertir("hola mundo"), "odnum aloh");

        System.out.println("\n--- Doble inversión: debe volver a la palabra original ---");
        String original = "estructuras";
        String unaVez = invertir(original);
        String dosVeces = invertir(unaVez);
        System.out.println("original      = \"" + original + "\"");
        System.out.println("invertida     = \"" + unaVez + "\"");
        System.out.println("doble inversa = \"" + dosVeces + "\"");
        System.out.println(original.equals(dosVeces)
                ? "Coincide con la original -> OK"
                : "ERROR: no coincide con la original");

        demostrarContrasteConCola("algoritmo");
        explicarRelacionConRecursividad();

        System.out.println("\n--- Casos de error ---");
        intentarInvertirNull();
        intentarDesapilarVacia();
    }

    /**
     * Invierte una cadena apilando todos sus caracteres y desapilándolos.
     *
     * <p>Fase 1: se recorre la cadena de izquierda a derecha apilando cada carácter. El primero
     * queda en el fondo. Fase 2: se desapila hasta vaciar la pila, concatenando en ese orden.
     * Como el tope es siempre el último carácter apilado, el primero en salir es el último de la
     * palabra original.</p>
     *
     * <p>Complejidad: O(n) temporal —n apilados y n desapilados, todos O(1)— y O(n) espacial,
     * porque los n caracteres coexisten en la pila.</p>
     *
     * @param palabra cadena a invertir; puede ser vacía, pero no {@code null}
     * @return una cadena nueva con los caracteres de {@code palabra} en orden inverso
     * @throws IllegalArgumentException si {@code palabra} es {@code null}, que no es una cadena
     *         vacía y haría fallar la lectura de su longitud
     */
    public static String invertir(String palabra) {
        if (palabra == null) {
            throw new IllegalArgumentException("No se admite null: null no es una cadena vacía.");
        }

        PilaCaracteres pila = new PilaCaracteres();

        // FASE 1 - APILADO. Orden natural de lectura. El carácter de la posición 0 entra
        // primero, así que queda en el FONDO de la pila.
        for (int i = 0; i < palabra.length(); i++) {
            pila.apilar(palabra.charAt(i));
        }

        // FASE 2 - DESAPILADO. La pila solo permite sacar por el tope, y el tope es el último
        // que entró. No hay ninguna decisión de orden acá: se saca lo único que se puede sacar.
        StringBuilder invertida = new StringBuilder();
        while (!pila.estaVacia()) {
            invertida.append(pila.desapilar());
        }
        return invertida.toString();
    }

    /**
     * Igual que {@link #invertir(String)} pero imprimiendo el estado de la pila en cada paso de
     * las dos fases, para que el mecanismo quede visible.
     *
     * <p>Complejidad: O(n^2) por las impresiones del estado completo en cada paso; el algoritmo
     * en sí sigue siendo O(n).</p>
     *
     * @param palabra cadena a invertir; no {@code null}
     * @return la cadena invertida
     * @throws IllegalArgumentException si {@code palabra} es {@code null}
     */
    public static String invertirMostrandoFases(String palabra) {
        if (palabra == null) {
            throw new IllegalArgumentException("No se admite null: null no es una cadena vacía.");
        }

        PilaCaracteres pila = new PilaCaracteres();

        System.out.println("FASE 1 - APILADO (se lee de izquierda a derecha):");
        for (int i = 0; i < palabra.length(); i++) {
            char c = palabra.charAt(i);
            pila.apilar(c);
            System.out.printf("  apilar('%c')  | pila: %s%n", c, pila.contenido());
        }
        System.out.println("  El primer carácter leído ('" + palabra.charAt(0)
                + "') quedó en el FONDO; el último ('"
                + palabra.charAt(palabra.length() - 1) + "') quedó en el TOPE.");

        System.out.println("FASE 2 - DESAPILADO (solo se puede sacar por el tope):");
        StringBuilder invertida = new StringBuilder();
        while (!pila.estaVacia()) {
            char c = pila.desapilar();
            invertida.append(c);
            System.out.printf("  desapilar() -> '%c' | cadena parcial: \"%s\" | pila: %s%n",
                    c, invertida, pila.contenido());
        }
        return invertida.toString();
    }

    /**
     * Procesa la misma palabra con una pila y con una cola, para mostrar que la inversión
     * depende de la política de salida y no del recorrido.
     *
     * <p>Complejidad: O(n).</p>
     *
     * @param palabra palabra a procesar con las dos estructuras; no {@code null}
     */
    private static void demostrarContrasteConCola(String palabra) {
        System.out.println("\n============================================================");
        System.out.println("POR QUÉ LIFO INVIERTE Y FIFO NO");
        System.out.println("============================================================");
        System.out.println("Se recorre la MISMA palabra de la MISMA forma, cambiando solo la");
        System.out.println("estructura donde se guardan los caracteres.");

        PilaCaracteres pila = new PilaCaracteres();
        ColaCaracteres cola = new ColaCaracteres();
        for (int i = 0; i < palabra.length(); i++) {
            pila.apilar(palabra.charAt(i));
            cola.encolar(palabra.charAt(i));
        }

        StringBuilder conPila = new StringBuilder();
        while (!pila.estaVacia()) {
            conPila.append(pila.desapilar());
        }
        StringBuilder conCola = new StringBuilder();
        while (!cola.estaVacia()) {
            conCola.append(cola.desencolar());
        }

        System.out.println("\n  entrada:          \"" + palabra + "\"");
        System.out.println("  con PILA (LIFO):  \"" + conPila + "\"   <- invertida");
        System.out.println("  con COLA (FIFO):  \"" + conCola + "\"   <- intacta");
        System.out.println("\n  La pila devuelve el último que entró, así que recorre la palabra");
        System.out.println("  hacia atrás. La cola devuelve el primero que entró, así que la");
        System.out.println("  recorre hacia adelante y la reconstruye igual. Esa es la diferencia");
        System.out.println("  operativa fundamental entre las dos estructuras: una PRESERVA el");
        System.out.println("  orden de llegada y la otra lo INVIERTE.");
    }

    /**
     * Explica la relación entre este mecanismo y el orden de retorno de una función recursiva.
     *
     * <p>Complejidad: O(1).</p>
     */
    private static void explicarRelacionConRecursividad() {
        System.out.println("\n============================================================");
        System.out.println("LA MISMA IDEA EN UNA FUNCIÓN RECURSIVA");
        System.out.println("============================================================");
        System.out.println("  La pila de llamadas de la JVM es, literalmente, una pila. Cuando");
        System.out.println("  una función recursiva deja una operación pendiente antes de la");
        System.out.println("  llamada, esa operación se resuelve al DESAPILAR el marco, es decir");
        System.out.println("  en orden inverso al de las llamadas.");
        System.out.println("  Por eso invertir(resto) + primerCaracter invierte una cadena sin");
        System.out.println("  ninguna pila explícita: el primer carácter se concatena en el marco");
        System.out.println("  más externo, que es el último en resolverse, y termina al final.");
        System.out.println("  Acá la pila está a la vista; en la versión recursiva es la de la JVM.");
    }

    /**
     * Compara el resultado obtenido con el esperado e informa el veredicto.
     *
     * <p>Complejidad: O(n).</p>
     *
     * @param entrada palabra original, para el informe
     * @param obtenido resultado producido por el programa
     * @param esperado resultado correcto, escrito a mano
     */
    private static void comprobar(String entrada, String obtenido, String esperado) {
        System.out.printf("entrada = %-13s | obtenido = %-13s | esperado = %-13s | %s%n",
                "\"" + entrada + "\"", "\"" + obtenido + "\"", "\"" + esperado + "\"",
                obtenido.equals(esperado) ? "OK" : "ERROR");
    }

    /**
     * Verifica que invertir {@code null} se rechace con una excepción explicativa.
     *
     * <p>Complejidad: O(1).</p>
     */
    private static void intentarInvertirNull() {
        try {
            invertir(null);
            System.out.println("ERROR: invertir(null) debió lanzar excepción");
        } catch (IllegalArgumentException e) {
            System.out.println("invertir(null) lanzó IllegalArgumentException: "
                    + e.getMessage() + "  -> OK");
        }
    }

    /**
     * Verifica que desapilar sobre una pila vacía se rechace con una excepción explicativa.
     *
     * <p>Complejidad: O(1).</p>
     */
    private static void intentarDesapilarVacia() {
        try {
            new PilaCaracteres().desapilar();
            System.out.println("ERROR: desapilar() sobre pila vacía debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("desapilar() sobre pila vacía lanzó IllegalStateException: "
                    + e.getMessage() + "  -> OK");
        }
    }
}

/**
 * Nodo de la pila y de la cola de caracteres: un dato y la referencia al siguiente.
 *
 * <p>El último nodo de la cadena tiene {@code siguiente} en {@code null}, que es la marca de
 * fin: no existe ningún contador de longitud dentro de la cadena misma.</p>
 */
class NodoCaracter {

    /** Carácter almacenado en este nodo. */
    private final char dato;

    /** Referencia al nodo siguiente de la cadena, o {@code null} si es el último. */
    private NodoCaracter siguiente;

    /**
     * Crea un nodo suelto, todavía sin enlazar.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param dato carácter a almacenar
     */
    NodoCaracter(char dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    /**
     * Devuelve el carácter almacenado.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return el carácter de este nodo
     */
    public char getDato() {
        return dato;
    }

    /**
     * Devuelve el nodo siguiente.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return el nodo siguiente, o {@code null} si este es el último
     */
    public NodoCaracter getSiguiente() {
        return siguiente;
    }

    /**
     * Enlaza este nodo con el siguiente.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param siguiente nodo que pasa a ser el siguiente, o {@code null}
     */
    public void setSiguiente(NodoCaracter siguiente) {
        this.siguiente = siguiente;
    }
}

/**
 * Pila de caracteres sobre lista enlazada simple, con el tope en {@code head}.
 *
 * <p>No tiene capacidad fija, así que no existe el método {@code estaLlena}: es la diferencia
 * frente a las pilas sobre arreglo del práctico 4, donde la capacidad era un caso límite real.
 * Acá el único límite es la memoria disponible.</p>
 */
class PilaCaracteres {

    /** Primer nodo de la cadena, que es el TOPE de la pila. */
    private NodoCaracter head;

    /** Cantidad de elementos, mantenida para que {@code tamanio()} sea O(1). */
    private int cantidad;

    /**
     * Apila un carácter.
     *
     * <p>El nodo nuevo se enlaza con el head actual y recién después head pasa a apuntarlo. Ese
     * orden importa: si head se moviera primero, se perdería la referencia al resto de la pila.
     * Como se inserta al inicio, no hay ningún recorrido y la operación es O(1).</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @param dato carácter a apilar
     */
    public void apilar(char dato) {
        NodoCaracter nuevo = new NodoCaracter(dato);
        nuevo.setSiguiente(head);
        head = nuevo;
        cantidad++;
    }

    /**
     * Quita y devuelve el carácter del tope.
     *
     * <p>Complejidad: O(1). El nodo desapilado queda sin referencias que lo alcancen desde head,
     * así que pasa a ser candidato para el recolector de basura.</p>
     *
     * @return el carácter que estaba en el tope
     * @throws IllegalStateException si la pila está vacía
     */
    public char desapilar() {
        if (estaVacia()) {
            throw new IllegalStateException("No se puede desapilar: la pila está vacía.");
        }
        char dato = head.getDato();
        head = head.getSiguiente();
        cantidad--;
        return dato;
    }

    /**
     * Indica si la pila no tiene elementos.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return {@code true} si {@code head} es {@code null}
     */
    public boolean estaVacia() {
        return head == null;
    }

    /**
     * Devuelve la cantidad de elementos apilados.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return cantidad de caracteres en la pila
     */
    public int tamanio() {
        return cantidad;
    }

    /**
     * Devuelve el contenido de la pila desde el tope hacia el fondo.
     *
     * <p>Complejidad: O(n).</p>
     *
     * @return representación del contenido, o {@code (vacía)} si no hay elementos
     */
    public String contenido() {
        if (estaVacia()) {
            return "(vacía)";
        }
        StringBuilder sb = new StringBuilder("TOPE -> ");
        NodoCaracter actual = head;
        while (actual != null) {
            sb.append('\'').append(actual.getDato()).append('\'');
            actual = actual.getSiguiente();
            if (actual != null) {
                sb.append(" -> ");
            }
        }
        return sb.append(" -> null").toString();
    }
}

/**
 * Cola de caracteres sobre lista enlazada simple, incluida únicamente para demostrar por
 * contraste que FIFO preserva el orden de llegada.
 *
 * <p>Necesita dos referencias: se desencola por {@code head} y se encola por {@code tail}. Sin
 * {@code tail} cada encolado tendría que recorrer la cadena completa para llegar al último nodo,
 * y la operación pasaría de O(1) a O(n).</p>
 */
class ColaCaracteres {

    /** Primer nodo: el FRENTE, por donde se desencola. */
    private NodoCaracter head;

    /** Último nodo: el FINAL, por donde se encola. */
    private NodoCaracter tail;

    /**
     * Encola un carácter al final.
     *
     * <p>Caso especial: si la cola estaba vacía no hay último nodo al que enganchar, así que
     * head y tail pasan a apuntar los dos al nodo nuevo.</p>
     *
     * <p>Complejidad: O(1) gracias a la referencia {@code tail}.</p>
     *
     * @param dato carácter a encolar
     */
    public void encolar(char dato) {
        NodoCaracter nuevo = new NodoCaracter(dato);
        if (estaVacia()) {
            head = nuevo;
            tail = nuevo;
            return;
        }
        tail.setSiguiente(nuevo);
        tail = nuevo;
    }

    /**
     * Quita y devuelve el carácter del frente.
     *
     * <p>Caso especial: si al desencolar la cola queda vacía, {@code tail} debe volver a
     * {@code null}. Si no, quedaría apuntando a un nodo que ya no forma parte de la cola y el
     * próximo encolado se engancharía a un nodo inalcanzable, perdiendo el elemento.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return el carácter que estaba en el frente
     * @throws IllegalStateException si la cola está vacía
     */
    public char desencolar() {
        if (estaVacia()) {
            throw new IllegalStateException("No se puede desencolar: la cola está vacía.");
        }
        char dato = head.getDato();
        head = head.getSiguiente();
        if (head == null) {
            tail = null;
        }
        return dato;
    }

    /**
     * Indica si la cola no tiene elementos.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return {@code true} si {@code head} es {@code null}
     */
    public boolean estaVacia() {
        return head == null;
    }
}
