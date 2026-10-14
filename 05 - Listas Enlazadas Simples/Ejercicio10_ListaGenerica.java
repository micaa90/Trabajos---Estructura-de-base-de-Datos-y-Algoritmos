/*
=====================================================================================
Práctico 5 - Listas Enlazadas Simples. Ejercicio 10: Convertir la lista de enteros en una lista genérica
=====================================================================================

Prompt inicial utilizado:

  "Analizá la transformación de una lista enlazada simple de int en una versión GENÉRICA:
  ListaEnlazada<T> y Nodo<T>, que debe funcionar con ListaEnlazada<Integer>,
  ListaEnlazada<String> y ListaEnlazada<Alumno>.
  El punto central: explicá QUÉ PARTES DEL CÓDIGO CAMBIAN, CUÁLES SE MANTIENEN IGUALES, y POR
  QUÉ EL ALGORITMO DE RECORRIDO, INSERCIÓN Y ELIMINACIÓN NO DEPENDE DEL TIPO DE DATO ALMACENADO.
  Desarrollá:
  - qué cambia exactamente: la declaración de las clases, el tipo del atributo dato, los tipos
    de los parámetros y de los valores de retorno. Hacé una lista concreta;
  - qué NO cambia: toda la lógica de manejo de referencias. El recorrido avanza con
    actual = actual.getSiguiente() sin importar qué contiene el nodo; la inserción reconecta
    referencias sin mirar el dato; la eliminación hace el mismo doble salto; invertir() da
    vuelta los mismos enlaces. Explicá POR QUÉ: porque esos algoritmos operan sobre la
    ESTRUCTURA (los enlaces), no sobre el CONTENIDO. Este es el concepto central del ejercicio,
    desarrollalo bien y relacionalo con la distinción dato/referencia del ejercicio 7;
  - la ÚNICA excepción importante: los métodos que COMPARAN datos (buscar y eliminar por valor,
    contarOcurrencias) sí dependen del tipo. Explicá que con int se usaba ==, pero con objetos
    hay que usar equals(), y qué pasa si se usa == con objetos (compara identidad de referencia,
    no contenido, así que dos Strings iguales creados por separado darían false). Desarrollá
    este punto, es el error típico del ejercicio;
  - qué implica eso para la clase Alumno: necesita sobrescribir equals() (y por coherencia
    hashCode()) para que buscar funcione como se espera;
  - el problema técnico de los genéricos en Java que ya apareció en el práctico de pilas: por
    qué acá NO aparece. Explicá que una lista enlazada no necesita new T[], porque cada nodo
    guarda un solo dato de tipo T, así que el type erasure no molesta. Este contraste con la
    pila sobre arreglo es una buena observación, incluila;
  - qué pasa con los tipos primitivos y el autoboxing: por qué hay que usar ListaEnlazada<Integer>
    y no ListaEnlazada<int>;
  - por qué las complejidades de todas las operaciones son idénticas a la versión con int.
  Casos límite: almacenar null, comparar objetos sin equals sobrescrito, lista vacía de
  cualquier tipo.
  No escribas la implementación."

Ajustes realizados luego de la primera respuesta de OpenCode:

1) La primera respuesta dejó las comparaciones con == al cambiar int por T. Eso compila
    pero es incorrecto: con objetos el == compara identidad de referencia, así que
    buscar(new String("ana")) devolvía false aunque la lista tuviera "ana". Se pasó todo a
    equals(), concentrado en un método privado sonIguales().
 2) Ese sonIguales() se agregó porque la primera versión invocaba equals() directamente
    sobre el dato del nodo y lanzaba NullPointerException al almacenar null, que es un
    valor perfectamente válido para un dato de tipo T.
 3) La clase Alumno de la primera respuesta no sobrescribía equals(), con lo cual heredaba
    el de Object (que es ==) y la búsqueda por contenido no funcionaba. Se agregaron
    equals(), hashCode() y toString(), y se incorporó al main la prueba explícita de == contra
    equals() con dos new String("ana").

Estrategia: parametrizar las clases con <T> dejando intacta toda la lógica de enlaces, y reemplazar las únicas comparaciones de datos por equals() a través de un método sonIguales() que tolera null.

Complejidad: idéntica a la versión con int, porque los genéricos de Java se implementan
por BORRADO DE TIPO (type erasure) y no agregan ninguna indirección en tiempo de ejecución.
insertarAlInicio O(1); insertarAlFinal, insertarEnPosicion, obtener, modificar, buscar,
eliminar, eliminarEnPosicion O(n) en el peor caso; contarOcurrencias e invertir O(n)
siempre; estaVacia y getSize O(1). Espacial O(n) por los nodos y O(1) adicional por
operación. La única diferencia práctica es el costo del equals() del tipo concreto, que
para String o Alumno es O(longitud del nombre) en lugar de una comparación de enteros.
=====================================================================================
*/

/**
 * Ejercicio 10 del Práctico 5 (Listas Enlazadas Simples): conversión de la lista de enteros
 * en una lista GENÉRICA {@code ListaEnlazada<T>} con nodos {@code Nodo<T>}.
 *
 * <p><b>Qué cambió respecto de la versión con int:</b></p>
 * <ul>
 *   <li>la declaración de las clases: {@code class Nodo<T>} y {@code class ListaEnlazada<T>};</li>
 *   <li>el tipo del atributo {@code dato}, que pasa de {@code int} a {@code T};</li>
 *   <li>los tipos de los parámetros ({@code insertarAlInicio(T dato)}, {@code buscar(T dato)},
 *       {@code modificar(int posicion, T nuevoDato)}, ...);</li>
 *   <li>el tipo de retorno de {@code obtener}, que pasa de {@code int} a {@code T};</li>
 *   <li>las COMPARACIONES: donde había {@code ==} ahora hay {@code equals()}.</li>
 * </ul>
 *
 * <p><b>Qué NO cambió:</b> absolutamente nada de la lógica de manejo de referencias. El
 * recorrido sigue avanzando con {@code actual = actual.getSiguiente()}; la inserción sigue
 * conectando primero el nodo nuevo hacia adelante y después reconectando el anterior; la
 * eliminación sigue haciendo el mismo doble salto; {@code invertir()} sigue dando vuelta los
 * mismos enlaces con las mismas tres referencias. El motivo es que esos algoritmos operan
 * sobre la ESTRUCTURA (los enlaces), no sobre el CONTENIDO de los nodos: es la misma
 * distinción dato/referencia del ejercicio 7. Por eso las complejidades son idénticas a las
 * de la versión con {@code int}.</p>
 *
 * <p><b>La única excepción son los métodos que COMPARAN datos</b> ({@code buscar},
 * {@code eliminar} y {@code contarOcurrencias}): esos sí dependen del tipo y por eso usan
 * {@code equals()}.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio10_ListaGenerica {

    /**
     * Método principal: prueba la lista genérica con Integer, String y Alumno.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Práctico 5 - Ejercicio 10: lista enlazada genérica ===");

        probarConEnteros();
        probarConCadenas();
        probarConAlumnos();
        demostrarIgualdadDeReferenciaContraContenido();
        probarCasosLimite();

        System.out.println();
        System.out.println("--- 6) Por qué acá no aparece el problema de new T[] ---");
        System.out.println("En una pila sobre ARREGLO hace falta reservar el almacenamiento de golpe,");
        System.out.println("y new T[capacidad] no compila: por el type erasure el tipo T no existe en");
        System.out.println("tiempo de ejecución, así que la JVM no sabe qué arreglo crear. Hay que");
        System.out.println("recurrir a (T[]) new Object[capacidad] y convivir con un unchecked warning.");
        System.out.println("Una lista enlazada NUNCA necesita eso: cada Nodo<T> guarda UN solo dato de");
        System.out.println("tipo T en un campo, y crear nodos (new Nodo<>(dato)) es perfectamente legal");
        System.out.println("porque no requiere construir ningún arreglo de T.");
    }

    /**
     * Prueba la lista con {@code Integer}, el caso que reemplaza a la versión con {@code int}.
     *
     * <p>Hay que escribir {@code ListaEnlazada<Integer>} y no {@code ListaEnlazada<int>}:
     * los parámetros de tipo sólo admiten tipos por referencia, y los primitivos no lo son.
     * El autoboxing convierte automáticamente cada literal {@code int} en un {@code Integer}
     * al insertarlo, y el unboxing hace el camino inverso al leerlo.</p>
     *
     * Complejidad: O(n) por operación en el peor caso, igual que la versión con int.
     */
    private static void probarConEnteros() {
        System.out.println();
        System.out.println("--- 1) ListaEnlazada<Integer> ---");
        ListaEnlazada<Integer> numeros = new ListaEnlazada<>();
        numeros.insertarAlFinal(10);   // autoboxing: int 10 -> Integer.valueOf(10)
        numeros.insertarAlFinal(20);
        numeros.insertarAlFinal(10);
        numeros.insertarAlFinal(30);
        numeros.insertarAlInicio(5);
        mostrarEstado("Estado:", numeros);

        System.out.println("buscar(20) = " + numeros.buscar(20) + " (usa equals, no ==)");
        System.out.println("buscar(99) = " + numeros.buscar(99));
        System.out.println("obtener(0) = " + numeros.obtener(0)
                + " | obtener(" + (numeros.getSize() - 1) + ") = "
                + numeros.obtener(numeros.getSize() - 1));
        System.out.println("contarOcurrencias(10) = " + numeros.contarOcurrencias(10));

        numeros.insertarEnPosicion(15, 2);
        mostrarEstado("insertarEnPosicion(15, 2):", numeros);
        numeros.modificar(0, -5);
        mostrarEstado("modificar(0, -5):", numeros);
        System.out.println("eliminar(10) = " + numeros.eliminar(10) + " (primera aparición)");
        mostrarEstado("después de eliminar:", numeros);
        numeros.eliminarEnPosicion(0);
        mostrarEstado("eliminarEnPosicion(0):", numeros);
        numeros.invertir();
        mostrarEstado("invertir():", numeros);
        System.out.println("    invertir() no mira el dato ni una vez: su lógica es idéntica a la");
        System.out.println("    de la versión con int, porque sólo toca referencias.");

        System.out.println("Caso de error: obtener(" + numeros.getSize() + ")");
        try {
            numeros.obtener(numeros.getSize());
        } catch (IndexOutOfBoundsException e) {
            System.out.println("    IndexOutOfBoundsException: " + e.getMessage());
        }
    }

    /**
     * Prueba la lista con {@code String}, donde la diferencia entre {@code ==} y
     * {@code equals()} se vuelve observable.
     *
     * Complejidad: O(n) por operación en el peor caso.
     */
    private static void probarConCadenas() {
        System.out.println();
        System.out.println("--- 2) ListaEnlazada<String> ---");
        ListaEnlazada<String> palabras = new ListaEnlazada<>();
        palabras.insertarAlFinal("ana");
        palabras.insertarAlFinal("beto");
        palabras.insertarAlFinal("ana");
        palabras.insertarAlFinal("carla");
        mostrarEstado("Estado:", palabras);

        System.out.println("buscar(\"beto\") = " + palabras.buscar("beto"));
        System.out.println("contarOcurrencias(\"ana\") = " + palabras.contarOcurrencias("ana"));
        System.out.println("obtener(3) = " + palabras.obtener(3));
        System.out.println("eliminar(\"ana\") = " + palabras.eliminar("ana"));
        mostrarEstado("después de eliminar:", palabras);
        palabras.invertir();
        mostrarEstado("invertir():", palabras);
    }

    /**
     * Prueba la lista con objetos de dominio ({@code Alumno}), que sobrescriben
     * {@code equals()} para que la búsqueda por contenido funcione.
     *
     * Complejidad: O(n) por operación en el peor caso.
     */
    private static void probarConAlumnos() {
        System.out.println();
        System.out.println("--- 3) ListaEnlazada<Alumno> ---");
        ListaEnlazada<Alumno> curso = new ListaEnlazada<>();
        curso.insertarAlFinal(new Alumno("Ana", 101));
        curso.insertarAlFinal(new Alumno("Beto", 102));
        curso.insertarAlFinal(new Alumno("Carla", 103));
        mostrarEstado("Estado:", curso);

        // Objeto DISTINTO pero con el mismo contenido: sólo equals() puede reconocerlo.
        Alumno buscado = new Alumno("Beto", 102);
        System.out.println("buscar(Alumno(Beto, 102)) = " + curso.buscar(buscado));
        System.out.println("    Es otro objeto, con otra dirección en el Heap: si buscar() usara ==");
        System.out.println("    el resultado sería false. Funciona porque Alumno sobrescribe equals().");

        System.out.println("contarOcurrencias(Alumno(Ana, 101)) = "
                + curso.contarOcurrencias(new Alumno("Ana", 101)));
        System.out.println("eliminar(Alumno(Ana, 101)) = " + curso.eliminar(new Alumno("Ana", 101)));
        mostrarEstado("después de eliminar:", curso);
        curso.invertir();
        mostrarEstado("invertir():", curso);
    }

    /**
     * Demuestra con dos {@code new String("ana")} la diferencia entre {@code ==} y
     * {@code equals()}, que es el error típico al genericar la lista.
     *
     * Complejidad: O(n) por las búsquedas que ejecuta.
     */
    private static void demostrarIgualdadDeReferenciaContraContenido() {
        System.out.println();
        System.out.println("--- 4) == contra equals(): el error típico de la versión genérica ---");

        // new String fuerza DOS objetos distintos en el Heap con el mismo contenido.
        String primera = new String("ana");
        String segunda = new String("ana");

        System.out.println("primera = " + primera + " | segunda = " + segunda);
        System.out.println("primera == segunda      -> " + (primera == segunda)
                + "   (compara IDENTIDAD: dos direcciones distintas del Heap)");
        System.out.println("primera.equals(segunda) -> " + primera.equals(segunda)
                + "    (compara CONTENIDO: los mismos caracteres)");

        ListaEnlazada<String> lista = new ListaEnlazada<>();
        lista.insertarAlFinal(primera);
        mostrarEstado("Lista con la primera cadena:", lista);
        System.out.println("buscar(segunda) con equals() = " + lista.buscar(segunda)
                + "  <- el resultado correcto");
        System.out.println("    Si buscar() estuviera escrito con == devolvería false, porque el nodo");
        System.out.println("    guarda la referencia de 'primera' y se la compara contra otra dirección.");
        System.out.println("    Con int eso no pasaba: == sobre primitivos compara valores, no direcciones.");
        System.out.println("    Por eso Alumno DEBE sobrescribir equals() (y por coherencia hashCode()):");
        System.out.println("    sin eso heredaría el equals() de Object, que es exactamente ==.");
    }

    /**
     * Casos límite propios de la versión genérica: lista vacía de cualquier tipo y
     * almacenamiento de {@code null}.
     *
     * Complejidad: O(n) por operación.
     */
    private static void probarCasosLimite() {
        System.out.println();
        System.out.println("--- 5) Casos límite ---");

        ListaEnlazada<Alumno> vacia = new ListaEnlazada<>();
        mostrarEstado("Lista vacía de Alumno:", vacia);
        System.out.println("estaVacia() = " + vacia.estaVacia()
                + " | buscar(null) = " + vacia.buscar(null)
                + " | contarOcurrencias(null) = " + vacia.contarOcurrencias(null));
        try {
            vacia.obtener(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("obtener(0) lanzó IndexOutOfBoundsException: " + e.getMessage());
        }
        vacia.invertir();
        mostrarEstado("invertir() sobre la lista vacía:", vacia);

        System.out.println();
        System.out.println("Almacenar null: un dato de tipo T puede ser null, así que las");
        System.out.println("comparaciones no pueden invocar equals() a ciegas.");
        ListaEnlazada<String> conNulos = new ListaEnlazada<>();
        conNulos.insertarAlFinal("x");
        conNulos.insertarAlFinal(null);
        conNulos.insertarAlFinal("y");
        conNulos.insertarAlFinal(null);
        mostrarEstado("Estado:", conNulos);
        System.out.println("buscar(null) = " + conNulos.buscar(null)
                + " | contarOcurrencias(null) = " + conNulos.contarOcurrencias(null)
                + " | buscar(\"y\") = " + conNulos.buscar("y"));
        System.out.println("    Ninguna de esas llamadas lanza NullPointerException porque la");
        System.out.println("    comparación pasa por el método sonIguales(), que trata el null aparte.");
    }

    /**
     * Imprime la lista con una etiqueta, el {@code size} declarado y el recuento real de
     * nodos enlazados, marcando si ambos coinciden.
     *
     * Complejidad: O(n) por el recorrido de verificación.
     *
     * @param etiqueta texto que precede al estado
     * @param lista    lista a mostrar, de cualquier tipo de dato
     */
    private static void mostrarEstado(String etiqueta, ListaEnlazada<?> lista) {
        int reales = lista.contarNodosEnlazados();
        System.out.println(etiqueta + " " + lista.aCadena()
                + "   | size = " + lista.getSize()
                + " | nodos enlazados = " + reales
                + (reales == lista.getSize() ? "  [consistente]" : "  [INCONSISTENTE]"));
    }
}

/**
 * Nodo de una lista enlazada simple genérica.
 *
 * <p>Respecto del {@code Nodo} con {@code int}, lo único que cambió es el TIPO del atributo
 * {@code dato} y las firmas que lo mencionan. El atributo {@code siguiente} sigue siendo una
 * referencia a otro nodo del mismo tipo, y su significado es idéntico: {@code null} marca el
 * final de la cadena.</p>
 *
 * @param <T> tipo de dato almacenado en el nodo
 */
class Nodo<T> {

    /** Contenido del nodo; puede ser {@code null}. */
    private T dato;

    /** Referencia al nodo siguiente; {@code null} si este nodo es el último. */
    private Nodo<T> siguiente;

    /**
     * Crea un nodo suelto, todavía sin enlazar.
     *
     * <p>No hace falta ningún {@code new T[]} acá: el nodo guarda UN dato en un campo, y eso
     * el type erasure lo permite sin problemas.</p>
     *
     * Complejidad: O(1).
     *
     * @param dato dato a almacenar
     */
    Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    /**
     * Devuelve el contenido del nodo.
     *
     * Complejidad: O(1).
     *
     * @return el dato almacenado
     */
    T getDato() {
        return dato;
    }

    /**
     * Cambia el contenido del nodo. Operación NO estructural.
     *
     * Complejidad: O(1).
     *
     * @param dato nuevo dato a almacenar
     */
    void setDato(T dato) {
        this.dato = dato;
    }

    /**
     * Devuelve la referencia al nodo siguiente.
     *
     * Complejidad: O(1).
     *
     * @return el nodo siguiente, o {@code null} si este es el último
     */
    Nodo<T> getSiguiente() {
        return siguiente;
    }

    /**
     * Cambia la referencia al nodo siguiente. Operación ESTRUCTURAL.
     *
     * Complejidad: O(1).
     *
     * @param siguiente nodo que pasa a ser el sucesor, o {@code null} para marcar el final
     */
    void setSiguiente(Nodo<T> siguiente) {
        this.siguiente = siguiente;
    }
}

/**
 * Lista enlazada simple genérica, con todos los métodos de los ejercicios 1 a 9, implementada
 * sin {@code ArrayList}, {@code LinkedList}, arreglos ni ninguna estructura de la librería.
 *
 * <p><b>Invariantes:</b> {@code head == null} si y sólo si la lista está vacía;
 * {@code size} es la cantidad de nodos alcanzables desde {@code head}; la cadena termina
 * siempre en {@code null}.</p>
 *
 * <p>Los métodos sensibles al tipo de dato son exactamente tres: {@code buscar},
 * {@code eliminar} y {@code contarOcurrencias}, los únicos que COMPARAN contenidos. Todos
 * delegan en {@code sonIguales()}, que usa {@code equals()} y nunca {@code ==}.</p>
 *
 * @param <T> tipo de dato almacenado en la lista
 */
class ListaEnlazada<T> {

    /** Único punto de entrada a la estructura: referencia al primer nodo. */
    private Nodo<T> head;

    /** Cantidad de nodos enlazados; se actualiza en toda operación que cambia la longitud. */
    private int size;

    /** Nodos visitados por la última operación de recorrido (instrumentación). */
    private int nodosVisitados;

    /**
     * Crea una lista vacía.
     *
     * Complejidad: O(1).
     */
    ListaEnlazada() {
        this.head = null;
        this.size = 0;
    }

    /**
     * Compara dos datos por CONTENIDO, tolerando {@code null}.
     *
     * <p>Acá está la única diferencia algorítmica con la versión de {@code int}. Con
     * primitivos, {@code ==} comparaba VALORES y era correcto. Con objetos, {@code ==}
     * compara IDENTIDAD de referencia: dos objetos distintos con el mismo contenido (dos
     * {@code new String("ana")}, dos {@code new Alumno("Ana", 101)}) darían {@code false}, y
     * {@code buscar} fallaría aunque el dato estuviera en la lista. Por eso se usa
     * {@code equals()}.</p>
     *
     * <p>El {@code null} se trata aparte porque invocar {@code equals()} sobre una referencia
     * nula lanzaría {@code NullPointerException}, y un dato de tipo {@code T} puede ser
     * perfectamente {@code null}.</p>
     *
     * Complejidad: O(1) más el costo del {@code equals()} del tipo concreto.
     *
     * @param a primer dato, puede ser {@code null}
     * @param b segundo dato, puede ser {@code null}
     * @return {@code true} si ambos son {@code null} o si {@code a.equals(b)}
     */
    private boolean sonIguales(T a, T b) {
        if (a == null) {
            return b == null; // acá el == es correcto: se compara contra la constante null
        }
        return a.equals(b);
    }

    /**
     * Inserta un dato como primer elemento.
     *
     * <p>Orden de las dos asignaciones: 1) conectar el nodo nuevo hacia adelante,
     * 2) mover {@code head}. Invertirlo perdería el resto de la lista y crearía un ciclo.
     * La lógica es idéntica a la versión con {@code int}: no mira el dato en ningún momento.</p>
     *
     * Complejidad: O(1) temporal y espacial.
     *
     * @param dato dato a almacenar
     */
    void insertarAlInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(head); // 1) conectar hacia adelante
        head = nuevo;             // 2) mover el punto de entrada
        size++;
    }

    /**
     * Inserta un dato como último elemento, recorriendo hasta el final de la cadena.
     *
     * Complejidad: O(n) temporal (O(1) si la lista está vacía), O(1) espacial.
     *
     * @param dato dato a almacenar
     */
    void insertarAlFinal(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (head == null) { // caso especial: lista vacía
            head = nuevo;
        } else {
            Nodo<T> actual = head; // auxiliar de recorrido: head no se mueve
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevo);
        }
        size++;
    }

    /**
     * Valida una posición que debe corresponder a un nodo existente (rango 0..size-1).
     *
     * <p>Se lanza excepción en lugar de devolver un centinela: en la versión genérica
     * {@code obtener} devuelve {@code T}, y el único centinela posible sería {@code null},
     * que es un dato perfectamente almacenable en la lista. Sería indistinguible.</p>
     *
     * Complejidad: O(1).
     *
     * @param posicion índice en base 0
     * @throws IndexOutOfBoundsException si {@code posicion < 0} o {@code posicion >= size}
     */
    private void validarPosicionExistente(int posicion) {
        if (posicion < 0) {
            throw new IndexOutOfBoundsException("Posición inválida: " + posicion
                    + ". Las posiciones son base 0 y nunca negativas.");
        }
        if (posicion >= size) {
            String detalle = (size == 0)
                    ? " (la lista está vacía: ninguna posición es válida)."
                    : ", así que la última posición válida es " + (size - 1) + ".";
            throw new IndexOutOfBoundsException("Posición inválida: " + posicion
                    + ". La lista tiene size = " + size + detalle);
        }
    }

    /**
     * Valida una posición de inserción (rango 0..size, donde {@code size} significa insertar
     * al final).
     *
     * Complejidad: O(1).
     *
     * @param posicion índice en base 0
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
     * Inserta un dato en una posición determinada.
     *
     * <p>Lógica idéntica a la versión con {@code int}: se llega al nodo {@code posicion - 1}
     * y se reconecta primero hacia adelante y después hacia atrás. El dato no se compara ni
     * se inspecciona nunca.</p>
     *
     * Complejidad: O(1) en la posición 0, O(n) en el peor caso. O(1) espacial.
     *
     * @param dato     dato a insertar
     * @param posicion índice en base 0 entre 0 y {@code size}
     * @throws IndexOutOfBoundsException si la posición está fuera de 0..size
     */
    void insertarEnPosicion(T dato, int posicion) {
        validarPosicionDeInsercion(posicion);

        if (posicion == 0) { // caso especial: no hay nodo anterior, cambia head
            insertarAlInicio(dato);
            return;
        }

        Nodo<T> anterior = head;
        int indice = 0;
        while (indice < posicion - 1) {
            anterior = anterior.getSiguiente();
            indice++;
        }

        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(anterior.getSiguiente()); // 1) conectar hacia adelante
        anterior.setSiguiente(nuevo);                // 2) reconectar hacia atrás
        size++;
    }

    /**
     * Indica si un dato está presente en la lista.
     *
     * <p>Único cambio respecto de la versión con {@code int}: la comparación pasa de
     * {@code actual.getDato() == dato} a {@code sonIguales(actual.getDato(), dato)}, es decir
     * de {@code ==} a {@code equals()}. El recorrido es exactamente el mismo.</p>
     *
     * Complejidad: O(1) mejor caso, O(n) peor y promedio. O(1) espacial.
     *
     * @param dato dato buscado, puede ser {@code null}
     * @return {@code true} si algún nodo contiene un dato igual por contenido
     */
    boolean buscar(T dato) {
        nodosVisitados = 0;
        Nodo<T> actual = head;
        while (actual != null) {
            nodosVisitados++;
            if (sonIguales(actual.getDato(), dato)) { // equals(), nunca ==
                return true; // problema de EXISTENCIA: se puede cortar
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    /**
     * Devuelve el dato almacenado en una posición determinada (base 0).
     *
     * <p>Recibir una posición no implica acceso directo: adentro hay un recorrido O(n),
     * mientras que un arreglo resolvería lo mismo en O(1).</p>
     *
     * Complejidad: O(1) mejor caso, O(n) peor y promedio. O(1) espacial.
     *
     * @param posicion índice en base 0
     * @return el dato almacenado en esa posición
     * @throws IndexOutOfBoundsException si la posición está fuera de 0..size-1
     */
    T obtener(int posicion) {
        validarPosicionExistente(posicion); // validar ANTES de recorrer

        Nodo<T> actual = head;
        int indice = 0;
        nodosVisitados = 1;
        while (indice < posicion) {
            actual = actual.getSiguiente();
            indice++;
            nodosVisitados++;
        }
        return actual.getDato();
    }

    /**
     * Cambia el dato almacenado en una posición, sin tocar ningún enlace ni el {@code size}.
     *
     * <p>Operación de CONTENIDO, no estructural: la longitud, el orden y los enlaces quedan
     * exactamente igual.</p>
     *
     * Complejidad: O(1) mejor caso, O(n) peor y promedio. O(1) espacial.
     *
     * @param posicion  índice en base 0
     * @param nuevoDato dato que reemplaza al anterior
     * @throws IndexOutOfBoundsException si la posición está fuera de 0..size-1
     */
    void modificar(int posicion, T nuevoDato) {
        validarPosicionExistente(posicion);

        Nodo<T> actual = head;
        int indice = 0;
        while (indice < posicion) {
            actual = actual.getSiguiente();
            indice++;
        }
        actual.setDato(nuevoDato); // sólo contenido
    }

    /**
     * Cuenta cuántas veces aparece un dato, recorriendo la lista completa.
     *
     * <p>Sensible al tipo: compara con {@code equals()}. No hay corte anticipado porque es un
     * problema de AGREGACIÓN.</p>
     *
     * Complejidad: O(n) siempre. O(1) espacial.
     *
     * @param dato dato cuyas apariciones se cuentan, puede ser {@code null}
     * @return cantidad de nodos con un dato igual por contenido
     */
    int contarOcurrencias(T dato) {
        nodosVisitados = 0;
        int ocurrencias = 0;
        Nodo<T> actual = head;
        while (actual != null) {
            nodosVisitados++;
            if (sonIguales(actual.getDato(), dato)) { // equals(), nunca ==
                ocurrencias++; // sin return: puede haber más apariciones
            }
            actual = actual.getSiguiente();
        }
        return ocurrencias;
    }

    /**
     * Elimina la primera aparición de un dato.
     *
     * <p>Sensible al tipo: compara con {@code equals()}. El resto es idéntico a la versión
     * con {@code int}: se reconecta el nodo anterior y el nodo eliminado queda inalcanzable
     * desde {@code head}, a merced del Garbage Collector.</p>
     *
     * Complejidad: O(1) si está en el primer nodo, O(n) en el peor caso. O(1) espacial.
     *
     * @param dato dato cuya primera aparición se elimina, puede ser {@code null}
     * @return {@code true} si se eliminó un nodo
     */
    boolean eliminar(T dato) {
        if (head == null) { // CASO lista vacía
            return false;
        }

        if (sonIguales(head.getDato(), dato)) { // CASO primer nodo: hay que mover head
            Nodo<T> eliminado = head;
            head = head.getSiguiente();
            eliminado.setSiguiente(null);
            size--;
            return true;
        }

        Nodo<T> anterior = head;
        Nodo<T> actual = head.getSiguiente();
        while (actual != null) {
            if (sonIguales(actual.getDato(), dato)) { // CASO medio o último
                anterior.setSiguiente(actual.getSiguiente());
                actual.setSiguiente(null);
                size--;
                return true;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }

        return false; // CASO dato inexistente
    }

    /**
     * Elimina el nodo que está en una posición determinada mediante el doble salto de
     * referencias del nodo anterior.
     *
     * Complejidad: O(1) en la posición 0, O(n) en el peor caso. O(1) espacial.
     *
     * @param posicion índice en base 0
     * @throws IndexOutOfBoundsException si la posición está fuera de 0..size-1
     */
    void eliminarEnPosicion(int posicion) {
        validarPosicionExistente(posicion);

        if (posicion == 0) { // caso especial: no hay anterior
            Nodo<T> eliminado = head;
            head = head.getSiguiente();
            eliminado.setSiguiente(null);
            size--;
            return;
        }

        Nodo<T> anterior = head;
        int indice = 0;
        while (indice < posicion - 1) {
            anterior = anterior.getSiguiente();
            indice++;
        }

        Nodo<T> eliminado = anterior.getSiguiente();
        anterior.setSiguiente(eliminado.getSiguiente()); // doble salto
        eliminado.setSiguiente(null);
        size--;
    }

    /**
     * Invierte el orden de la lista en el lugar.
     *
     * <p>Este método es la mejor prueba de que los algoritmos sobre enlaces no dependen del
     * tipo: comparado con la versión de {@code int}, lo ÚNICO que cambió son las
     * declaraciones de tipo de las tres referencias auxiliares. Ni una línea de lógica, ni
     * una comparación de datos, ni un {@code equals()}. Da vuelta el sentido de cada enlace
     * sin mirar jamás lo que el nodo contiene.</p>
     *
     * Complejidad: O(n) temporal, O(1) espacial (tres referencias, sin arreglo auxiliar).
     */
    void invertir() {
        Nodo<T> anterior = null; // la porción ya invertida; null será el final de la lista
        Nodo<T> actual = head;   // el nodo cuyo enlace se da vuelta en este paso

        while (actual != null) {
            Nodo<T> siguiente = actual.getSiguiente(); // 1) RESCATE del resto de la lista
            actual.setSiguiente(anterior);             // 2) dar vuelta el enlace
            anterior = actual;                         // 3) avanzar anterior
            actual = siguiente;                        // 4) avanzar actual
        }

        head = anterior; // actual quedó en null; anterior es el nuevo primer nodo
    }

    /**
     * Indica si la lista no tiene ningún nodo.
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
     * Complejidad: O(1).
     *
     * @return la longitud de la lista
     */
    int getSize() {
        return size;
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

    /**
     * Imprime la lista en el formato {@code ana -> beto -> null}.
     *
     * Complejidad: O(n).
     */
    void imprimir() {
        System.out.println(aCadena());
    }

    /**
     * Devuelve la lista como texto en el formato {@code ana -> beto -> null}.
     *
     * <p>Usa {@code String.valueOf()} para que un dato {@code null} se imprima como la
     * palabra {@code null} en lugar de lanzar excepción, y para invocar el
     * {@code toString()} del tipo concreto (por ejemplo el de {@code Alumno}).</p>
     *
     * Complejidad: O(n).
     *
     * @return representación textual de la cadena de nodos
     */
    String aCadena() {
        String texto = "";
        Nodo<T> actual = head;
        while (actual != null) {
            texto = texto + String.valueOf(actual.getDato()) + " -> ";
            actual = actual.getSiguiente();
        }
        return texto + "null";
    }

    /**
     * Cuenta los nodos realmente enlazados recorriendo la cadena desde {@code head}, para
     * verificar que {@code size} siga coincidiendo con la estructura.
     *
     * Complejidad: O(n) temporal, O(1) espacial.
     *
     * @return cantidad de nodos alcanzables desde {@code head}
     */
    int contarNodosEnlazados() {
        int cuenta = 0;
        Nodo<T> actual = head;
        while (actual != null) {
            cuenta++;
            actual = actual.getSiguiente();
        }
        return cuenta;
    }
}

/**
 * Alumno con nombre y legajo, para probar la lista genérica con un tipo de dominio.
 *
 * <p>Sobrescribe {@code equals()} porque de lo contrario heredaría el de {@code Object}, que
 * compara identidad de referencia (es decir, {@code ==}): dos alumnos con el mismo nombre y
 * el mismo legajo creados por separado se considerarían distintos y {@code buscar} fallaría.
 * Se sobrescribe también {@code hashCode()} por coherencia, que es el contrato que Java
 * espera cuando se redefine {@code equals()}.</p>
 */
class Alumno {

    /** Nombre del alumno. */
    private final String nombre;

    /** Número de legajo, que identifica al alumno. */
    private final int legajo;

    /**
     * Crea un alumno.
     *
     * Complejidad: O(1).
     *
     * @param nombre nombre del alumno
     * @param legajo número de legajo
     */
    Alumno(String nombre, int legajo) {
        this.nombre = nombre;
        this.legajo = legajo;
    }

    /**
     * Devuelve el nombre del alumno.
     *
     * Complejidad: O(1).
     *
     * @return el nombre
     */
    String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el legajo del alumno.
     *
     * Complejidad: O(1).
     *
     * @return el legajo
     */
    int getLegajo() {
        return legajo;
    }

    /**
     * Representación textual usada por {@code aCadena()} e {@code imprimir()}.
     *
     * Complejidad: O(1).
     *
     * @return el alumno en el formato {@code Alumno(nombre, legajo)}
     */
    @Override
    public String toString() {
        return "Alumno(" + nombre + ", " + legajo + ")";
    }

    /**
     * Compara dos alumnos por CONTENIDO: mismo nombre y mismo legajo.
     *
     * Complejidad: O(k), con k la longitud del nombre, por la comparación de Strings.
     *
     * @param otro objeto a comparar
     * @return {@code true} si el otro objeto es un Alumno con igual nombre y legajo
     */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true; // misma referencia: trivialmente iguales
        }
        if (!(otro instanceof Alumno)) {
            return false; // incluye el caso null
        }
        Alumno comparado = (Alumno) otro;
        if (legajo != comparado.legajo) {
            return false;
        }
        return (nombre == null) ? comparado.nombre == null : nombre.equals(comparado.nombre);
    }

    /**
     * Código hash coherente con {@code equals()}: dos alumnos iguales devuelven el mismo
     * valor.
     *
     * Complejidad: O(k), con k la longitud del nombre.
     *
     * @return el código hash del alumno
     */
    @Override
    public int hashCode() {
        int resultado = legajo;
        resultado = 31 * resultado + ((nombre == null) ? 0 : nombre.hashCode());
        return resultado;
    }
}

/*
MATRIZ DE CASOS DE PRUEBA MANUALES
Compilación: javac Ejercicio10_ListaGenerica.java -d /tmp/v5/ej10
Ejecución:   java -cp /tmp/v5/ej10 Ejercicio10_ListaGenerica
Todos los casos están cableados en main(): el programa no lee entrada por consola,
así que cada caso se verifica leyendo el bloque correspondiente de la salida.

ID    | Objetivo                                         | Entrada exacta                                  | Resultado esperado                                   | Criterio de aprobación
------+--------------------------------------------------+-------------------------------------------------+------------------------------------------------------+------------------------------------------------
CP-01 | Flujo nominal con tres tipos de dato            | main(): listas de Integer, String y Alumno      | Las tres ejecutan insertar, buscar, eliminar,        | Bloques 1, 2 y 3 terminan con sus listas
      |                                                  |                                                 | contarOcurrencias e invertir sin excepciones         | invertidas y todas las líneas "[consistente]"
CP-02 | Lista vacía de cualquier tipo                   | ListaEnlazada<Alumno> sin inserciones           | estaVacia() = true, buscar(null) = false,            | Bloque 5 imprime esos valores y el mensaje de
      |                                                  |                                                 | obtener(0) lanza IndexOutOfBoundsException           | la excepción, sin cortar el programa
CP-03 | Primer nodo: eliminar por valor mueve head      | lista de Alumno; eliminar(Alumno("Ana", 101))   | true y la cadena arranca en Alumno(Beto, 102)        | Bloque 3: el estado posterior no contiene a Ana
CP-04 | Último nodo: buscar el dato del final           | ListaEnlazada<String>; obtener(3) sobre         | Devuelve "carla" sin lanzar excepción                | Bloque 2 imprime "obtener(3) = carla"
      |                                                  | ana,beto,ana,carla                              |                                                      |
CP-05 | Identidad contra contenido y posición inválida  | new String("ana") dos veces; obtener(size)      | == da false, equals() da true, buscar(segunda) da    | Bloque 4 imprime false/true/true y el bloque 1
      |                                                  | sobre la lista de Integer                       | true; obtener(size) lanza la excepción de rango      | muestra el mensaje de IndexOutOfBoundsException
*/
