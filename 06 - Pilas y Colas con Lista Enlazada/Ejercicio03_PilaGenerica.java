/*
 * ============================================================================
 * Ejercicio 3 - Pila genérica
 * Práctico 6 - Pilas y Colas con Listas Enlazadas
 * ============================================================================
 *
 * Prompt inicial utilizado (transcripción literal del prompt del Agente Analista):
 *
 *   Analizá la transformación de la pila enlazada de enteros en una PILA GENÉRICA Pila<T>,
 *   que debe funcionar con Integer, String y objetos propios.
 *   El punto central: explicá QUÉ SIGNIFICA USAR GENÉRICOS EN JAVA y POR QUÉ LA LÓGICA DE
 *   LA PILA NO DEPENDE DEL TIPO DE DATO ALMACENADO. Desarrollá:
 *   - qué es un parámetro de tipo y qué problema resuelve: evitar duplicar la clase entera
 *     por cada tipo de dato, y conseguir seguridad de tipos verificada por el COMPILADOR;
 *   - la alternativa mala de usar Object: por qué obligaría a castear en cada desapilar, y
 *     por qué eso traslada los errores de tipo del tiempo de compilación al tiempo de
 *     ejecución, con riesgo de ClassCastException. Este contraste es el corazón del
 *     ejercicio;
 *   - por qué la lógica de la pila es INDEPENDIENTE del tipo: apilar crea un nodo y mueve
 *     head, desapilar mueve head al siguiente. Ninguna de esas operaciones INSPECCIONA el
 *     dato, solo manipula referencias. El algoritmo opera sobre la ESTRUCTURA, no sobre el
 *     CONTENIDO. Desarrollá este punto, es lo que evalúa el ejercicio;
 *   - la ÚNICA excepción: el método contiene(), que sí compara datos. Explicá que con int
 *     se usaba == pero con objetos hay que usar equals(), y qué pasa si se usa == con
 *     objetos (compara identidad de referencia, no contenido). Explicá qué necesita una
 *     clase propia para que contiene() funcione bien;
 *   - una observación importante: esta pila enlazada NO sufre el problema de new T[] que
 *     tiene una pila sobre arreglo, porque cada nodo guarda un solo dato de tipo T y nunca
 *     hace falta crear un arreglo de T. Explicá por qué, y mencioná que en la versión con
 *     arreglo hay que recurrir a un Object[] con casting y @SuppressWarnings. Este
 *     contraste es una buena observación, incluila;
 *   - qué pasa con los tipos primitivos y el autoboxing: por qué Pila<Integer> y no
 *     Pila<int>;
 *   - por qué las complejidades son idénticas a la versión con int.
 *   Casos especiales: los mismos de la pila de enteros, más el caso de almacenar null y si
 *   se debe permitir, y el caso de un objeto sin equals() sobrescrito.
 *   No escribas la implementación.
 *
 * Ajustes realizados posteriormente al prompt inicial:
 *   1) El prompt inicial dejaba abierto si se permite almacenar null. Se resolvió
 *      RECHAZARLO con IllegalArgumentException en apilar(), porque desapilar() devuelve T:
 *      un null almacenado sería indistinguible de "no hay dato" y contiene(null) obligaría
 *      a una rama especial con comparación por identidad. Ver el Javadoc de apilar().
 *   2) Se agregó el método tamanio() con atributo size, igual que en el ejercicio 1.
 *   3) Se agregó la prueba explícita de == contra equals() con dos objetos Libro de igual
 *      contenido creados por separado, y la línea COMENTADA de apilado con tipo incorrecto.
 *      No estaban en el prompt inicial del analista: se sumaron porque son la evidencia
 *      concreta de los dos puntos que el ejercicio evalúa (comparación por contenido y
 *      verificación de tipos en compilación).
 *   No hubo ajustes sobre la estructura: la lógica de referencias quedó IDÉNTICA a la del
 *   ejercicio 1, que es exactamente lo que el análisis predecía.
 *
 * Estrategia: la misma pila enlazada del ejercicio 1 parametrizada con un tipo T, porque la
 * lógica sólo manipula referencias entre nodos y nunca inspecciona el dato almacenado.
 *
 * Complejidad esperada de las operaciones principales (IDÉNTICAS a la versión con int,
 * porque los genéricos son una verificación de COMPILACIÓN y no agregan trabajo en
 * ejecución):
 *   apilar    O(1)        desapilar  O(1)        verTope   O(1)
 *   estaVacia O(1)        tamanio    O(1)        contiene  O(n)        imprimir O(n)
 *
 * QUÉ SIGNIFICAN LOS GENÉRICOS ACÁ. T es un parámetro de tipo: un hueco que se completa en
 * el momento de declarar la pila (Pila<Integer>, Pila<String>, Pila<Libro>). Resuelve dos
 * problemas: (1) evita duplicar la clase entera por cada tipo de dato; (2) le da al
 * COMPILADOR la información necesaria para rechazar un apilado de tipo equivocado y para
 * que desapilar() devuelva T sin castear.
 *
 * POR QUÉ NO ALCANZA CON Object. Una PilaDeObject compilaría igual, pero desapilar()
 * devolvería Object y el llamador tendría que escribir (String) pila.desapilar() en cada
 * uso. Eso traslada el control de tipos del tiempo de COMPILACIÓN al tiempo de EJECUCIÓN:
 * nada impediría apilar un Integer en una pila usada como pila de String, y el error
 * aparecería recién al castear, como ClassCastException, en producción y lejos del lugar
 * donde se cometió. Con Pila<String> ese mismo error es un error de compilación.
 *
 * POR QUÉ LA LÓGICA NO DEPENDE DEL TIPO. apilar() crea un nodo y mueve head; desapilar()
 * mueve head al siguiente. Ninguna de las dos INSPECCIONA el dato: sólo manipulan
 * referencias. El algoritmo opera sobre la ESTRUCTURA (quién apunta a quién), no sobre el
 * CONTENIDO. La ÚNICA excepción es contiene(), que sí compara datos y por eso es el único
 * método que tuvo que cambiar: == pasó a equals().
 *
 * ACÁ NO HACE FALTA new T[]. En una pila sobre ARREGLO genérico habría que escribir
 * new T[capacidad], que Java prohíbe por el borrado de tipos (type erasure): en ejecución T
 * no existe, así que la JVM no sabe qué tipo de arreglo crear. La solución habitual es
 * reservar un Object[] y castear al leer, con @SuppressWarnings("unchecked") para silenciar
 * la advertencia. En esta pila ENLAZADA el problema no aparece: cada nodo guarda UN SOLO
 * dato de tipo T como campo (T dato), y un campo de tipo T sí es legal; nunca hay que crear
 * un arreglo de T, porque el "contenedor" de los elementos no es un bloque de memoria
 * reservado de antemano sino una cadena de objetos creados de a uno.
 *
 * AUTOBOXING: Pila<Integer> y no Pila<int>. Los parámetros de tipo sólo admiten tipos por
 * referencia, así que no existe Pila<int>. Se usa la clase envoltorio Integer y Java
 * convierte automáticamente (autoboxing al apilar, unboxing al leer un int). El costo es un
 * objeto Integer extra por elemento, además del Nodo.
 *
 * DIFERENCIA CON EL PRÁCTICO 4 (pila sobre arreglo): acá NO EXISTE isFull(), porque no hay
 * capacidad fija: cada apilado reserva un nodo nuevo en el Heap. Y tampoco existe el
 * problema del new T[] descripto arriba, que es específico de la versión con arreglo.
 *
 * @author Micaela Gimenez
 * @version 1.0
 */

/**
 * Nodo genérico de la lista simplemente enlazada que soporta la pila.
 *
 * <p>Guarda un dato de tipo {@code T} y una referencia al siguiente. Que el campo sea de
 * tipo {@code T} es perfectamente legal en Java; lo que no sería legal es
 * {@code new T[...]} (ver la cabecera del archivo).</p>
 *
 * @param <T> tipo del dato almacenado
 */
class Nodo<T> {

    /** Dato almacenado en este nodo. Nunca es null: apilar() rechaza null. */
    T dato;

    /** Referencia al nodo que sigue hacia el fondo de la pila; null si es el fondo. */
    Nodo<T> siguiente;

    /**
     * Crea un nodo suelto, todavía no enlazado a ninguna pila.
     *
     * @param dato valor a almacenar
     */
    Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }
}

/**
 * Pila genérica (política LIFO) implementada sobre una lista simplemente enlazada, donde
 * {@code head} es el tope.
 *
 * <p>La lógica es idéntica a la de la pila de enteros del ejercicio 1: {@code apilar} crea
 * un nodo y mueve {@code head}; {@code desapilar} mueve {@code head} al siguiente. Ninguna
 * de las dos inspecciona el dato, así que el algoritmo no depende del tipo almacenado. El
 * único método que cambió respecto de la versión con {@code int} es {@code contiene}, que
 * compara datos y por eso pasó de {@code ==} a {@code equals()}.</p>
 *
 * <p>No existe isFull(): no hay capacidad fija.</p>
 *
 * @param <T> tipo de los elementos almacenados
 */
class Pila<T> {

    /** Tope de la pila. null si está vacía. */
    private Nodo<T> head;

    /** Cantidad de elementos apilados, para que tamanio() sea O(1). */
    private int size;

    /**
     * Crea una pila vacía.
     *
     * <p>Complejidad: O(1)</p>
     */
    Pila() {
        this.head = null;
        this.size = 0;
    }

    /**
     * Apila un elemento, que queda como nuevo tope.
     *
     * <p>Orden de actualización de referencias: primero {@code nuevo.siguiente = head} y
     * después {@code head = nuevo}. Invertirlo haría que el nodo nuevo se apunte a sí mismo
     * y el resto de la pila quedaría inalcanzable.</p>
     *
     * <p>Complejidad: O(1) - dos asignaciones, sin recorrer nodos. Exactamente la misma
     * que en la versión con int: los genéricos no agregan trabajo en ejecución.</p>
     *
     * @param dato elemento a apilar; no puede ser null
     * @throws IllegalArgumentException si el dato es null
     */
    void apilar(T dato) {
        // Se RECHAZA null: desapilar() devuelve T, así que un null almacenado sería
        // indistinguible de "no hay dato", y contiene(null) necesitaría una rama especial
        // porque no se le puede pedir equals() a una referencia nula.
        if (dato == null) {
            throw new IllegalArgumentException("No se admiten elementos null en la pila.");
        }
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.siguiente = head;
        head = nuevo;
        size++;
    }

    /**
     * Quita y devuelve el elemento del tope.
     *
     * <p>Devuelve {@code T} directamente: no hace falta ningún casting en el llamador. Eso
     * es justamente lo que se gana frente a una pila de {@code Object}.</p>
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return el elemento que estaba en el tope
     * @throws IllegalStateException si la pila está vacía
     */
    T desapilar() {
        if (estaVacia()) {
            throw new IllegalStateException("No se puede desapilar: la pila está vacía.");
        }
        T dato = head.dato;
        head = head.siguiente; // el nodo desapilado queda inalcanzable -> lo recupera el GC
        size--;
        return dato;
    }

    /**
     * Consulta el elemento del tope sin quitarlo.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return el elemento del tope
     * @throws IllegalStateException si la pila está vacía
     */
    T verTope() {
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
     * @return true si está vacía
     */
    boolean estaVacia() {
        return head == null;
    }

    /**
     * Informa si un elemento está en la pila, comparando por CONTENIDO.
     *
     * <p>Este es el ÚNICO método cuya lógica cambió al hacer la pila genérica, porque es el
     * único que inspecciona el dato en lugar de manipular referencias. Con {@code int} se
     * comparaba con {@code ==}, que sobre primitivos compara valores. Con objetos
     * {@code ==} compararía IDENTIDAD DE REFERENCIA, es decir "¿son el mismo objeto en el
     * Heap?", y daría false para dos objetos distintos de contenido igual. Por eso se usa
     * {@code equals()}.</p>
     *
     * <p>Qué necesita una clase propia para que este método funcione bien: sobrescribir
     * {@code equals()} con una comparación por contenido (y, por contrato de Java,
     * {@code hashCode()} de forma consistente). Si la clase NO sobrescribe
     * {@code equals()}, hereda el de {@code Object}, que compara por identidad, y entonces
     * {@code contiene()} sólo encontraría exactamente el mismo objeto que se apiló.</p>
     *
     * <p>Complejidad: O(n) - recorre la cadena; el dato puede estar a cualquier
     * profundidad. Aceptable porque buscar no pertenece al contrato de una pila.</p>
     *
     * @param dato elemento a buscar; si es null siempre devuelve false
     * @return true si algún elemento de la pila es equals() al dato
     */
    boolean contiene(T dato) {
        if (dato == null) {
            return false; // no se admiten nulls dentro de la pila, así que nunca puede estar
        }
        Nodo<T> actual = head;
        while (actual != null) {
            // equals() y NO ==: comparación por contenido, no por identidad de referencia.
            // Se invoca sobre el parámetro y no sobre actual.dato por simetría de estilo;
            // los datos de la pila nunca son null, así que cualquiera de las dos sirve.
            if (dato.equals(actual.dato)) {
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    /**
     * Cantidad de elementos apilados.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return número de elementos
     */
    int tamanio() {
        return size;
    }

    /**
     * Imprime la pila desde el tope hacia el fondo, en formato
     * {@code TOPE -> 30 -> 20 -> 10 -> null}.
     *
     * <p>Cada elemento se muestra con su {@code toString()}: la pila no necesita saber cómo
     * se representa el dato, sólo le pide su representación textual. Otro ejemplo de que la
     * estructura es indiferente al tipo.</p>
     *
     * <p>Complejidad: O(n)</p>
     */
    void imprimir() {
        StringBuilder salida = new StringBuilder("TOPE");
        Nodo<T> actual = head;
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
     * Devuelve la representación textual del dato del tope, o la palabra null si la pila
     * está vacía.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return estado de la referencia head
     */
    String estadoHead() {
        return estaVacia() ? "null" : String.valueOf(head.dato);
    }
}

/**
 * Clase propia de dominio usada para probar la pila con objetos.
 *
 * <p>Sobrescribe {@code toString()} para que {@code imprimir()} muestre algo legible, y
 * {@code equals()}/{@code hashCode()} para que {@code contiene()} compare por CONTENIDO.
 * Sin ese {@code equals()}, {@code contiene()} heredaría la comparación por identidad de
 * {@code Object} y sólo encontraría el mismo objeto que se apiló.</p>
 */
class Libro {

    /** Título del libro. */
    private final String titulo;

    /** Autor del libro. */
    private final String autor;

    /**
     * Crea un libro.
     *
     * @param titulo título del libro
     * @param autor  autor del libro
     * @throws IllegalArgumentException si alguno de los dos campos es null o está en blanco
     */
    Libro(String titulo, String autor) {
        if (titulo == null || titulo.isBlank() || autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("Título y autor son obligatorios.");
        }
        this.titulo = titulo;
        this.autor = autor;
    }

    /**
     * Representación textual del libro, usada por imprimir().
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return cadena con título y autor
     */
    @Override
    public String toString() {
        return "\"" + titulo + "\" de " + autor;
    }

    /**
     * Compara dos libros por CONTENIDO: son iguales si coinciden título y autor.
     *
     * <p>Complejidad: O(k) sobre la longitud de las cadenas comparadas.</p>
     *
     * @param obj objeto a comparar
     * @return true si obj es un Libro con el mismo título y autor
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // atajo: misma referencia
        }
        if (!(obj instanceof Libro)) {
            return false; // cubre también el caso obj == null
        }
        Libro otro = (Libro) obj;
        return titulo.equals(otro.titulo) && autor.equals(otro.autor);
    }

    /**
     * Código hash consistente con equals(), como exige el contrato de Java.
     *
     * <p>Complejidad: O(k) sobre la longitud de las cadenas.</p>
     *
     * @return hash derivado de título y autor
     */
    @Override
    public int hashCode() {
        return 31 * titulo.hashCode() + autor.hashCode();
    }
}

/**
 * Programa de demostración del ejercicio 3: pila genérica probada con Integer, String y una
 * clase propia (Libro).
 *
 * <p>Incluye la prueba explícita de {@code ==} contra {@code equals()} con dos objetos de
 * igual contenido creados por separado, y una línea comentada que el compilador rechazaría
 * por tipo incorrecto.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio03_PilaGenerica {

    /**
     * Método principal: ejecuta la demostración completa.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 3 - Pila genérica Pila<T> (LIFO) ===");

        probarPilaDeEnteros();
        probarPilaDeCadenas();
        probarPilaDeLibros();
        demostrarIgualdadPorContenido();
        demostrarVerificacionDeTiposEnCompilacion();
        probarCasosLimite();

        System.out.println("\n=== Fin de la demostración ===");
    }

    /**
     * Prueba Pila&lt;Integer&gt;: apilar, desapilar, verTope y contiene.
     *
     * <p>Se usa Integer y no int porque un parámetro de tipo sólo admite tipos por
     * referencia: Pila&lt;int&gt; no existe. Java hace autoboxing al apilar.</p>
     */
    private static void probarPilaDeEnteros() {
        System.out.println("\n--- Pila<Integer> (autoboxing: se apila int y se guarda Integer) ---");
        Pila<Integer> pila = new Pila<>();
        pila.apilar(10); // autoboxing implícito: int 10 -> Integer.valueOf(10)
        pila.imprimir();
        pila.apilar(20);
        pila.imprimir();
        pila.apilar(30);
        pila.imprimir();
        System.out.println("  verTope() = " + pila.verTope() + " | head -> " + pila.estadoHead());
        System.out.println("  contiene(20) = " + pila.contiene(20)
                + " (equals() de Integer compara el valor envuelto)");
        System.out.println("  contiene(99) = " + pila.contiene(99));
        int desapilado = pila.desapilar(); // unboxing implícito al asignar a un int
        System.out.println("  desapilar() = " + desapilado + " (el último que entró)");
        pila.imprimir();
    }

    /**
     * Prueba Pila&lt;String&gt;: apilar, desapilar, verTope y contiene.
     *
     * <p>Exactamente el mismo código de la pila de Integer con otro tipo: eso es lo que se
     * gana al no duplicar la clase por cada tipo de dato.</p>
     */
    private static void probarPilaDeCadenas() {
        System.out.println("\n--- Pila<String> (misma clase, otro tipo: no se duplicó nada) ---");
        Pila<String> pila = new Pila<>();
        pila.apilar("alfa");
        pila.apilar("beta");
        pila.apilar("gamma");
        pila.imprimir();
        System.out.println("  verTope() = " + pila.verTope());
        System.out.println("  contiene(\"alfa\") = " + pila.contiene("alfa") + " (está en el fondo)");
        System.out.println("  contiene(\"delta\") = " + pila.contiene("delta"));
        System.out.println("  desapilar() = " + pila.desapilar());
        pila.imprimir();
        // Sin genéricos (pila de Object) esta línea necesitaría un casting explícito:
        //     String tope = (String) pila.desapilar();
        // y nada impediría que alguien hubiera apilado un Integer, con ClassCastException
        // en ejecución. Con Pila<String> el compilador ya garantizó que sólo hay String.
        String tope = pila.desapilar(); // sin casting: desapilar() devuelve String
        System.out.println("  desapilar() sin casting = " + tope);
    }

    /**
     * Prueba Pila&lt;Libro&gt;: apilar, desapilar, verTope y contiene con objetos propios.
     */
    private static void probarPilaDeLibros() {
        System.out.println("\n--- Pila<Libro> (clase propia con toString() y equals()) ---");
        Pila<Libro> pila = new Pila<>();
        pila.apilar(new Libro("El Aleph", "Borges"));
        pila.apilar(new Libro("Rayuela", "Cortázar"));
        pila.apilar(new Libro("Ficciones", "Borges"));
        pila.imprimir();
        System.out.println("  verTope() = " + pila.verTope());
        // El libro buscado es un objeto DISTINTO con el mismo contenido: sólo lo encuentra
        // porque Libro sobrescribe equals(). Con == daría false.
        System.out.println("  contiene(new Libro(\"Rayuela\", \"Cortázar\")) = "
                + pila.contiene(new Libro("Rayuela", "Cortázar"))
                + " (objeto distinto, mismo contenido -> lo encuentra por equals())");
        System.out.println("  contiene(new Libro(\"Rayuela\", \"Borges\")) = "
                + pila.contiene(new Libro("Rayuela", "Borges"))
                + " (mismo título, otro autor -> no es igual)");
        System.out.println("  desapilar() = " + pila.desapilar());
        pila.imprimir();
    }

    /**
     * Demuestra explícitamente la diferencia entre {@code ==} y {@code equals()} usando dos
     * objetos de igual contenido creados por separado.
     *
     * <p>Es la prueba de por qué {@code contiene()} tuvo que pasar de {@code ==} a
     * {@code equals()} al hacer la pila genérica.</p>
     */
    private static void demostrarIgualdadPorContenido() {
        System.out.println("\n--- Prueba explícita: == contra equals() ---");
        Libro a = new Libro("Rayuela", "Cortázar");
        Libro b = new Libro("Rayuela", "Cortázar"); // objeto DISTINTO, contenido IGUAL
        Libro mismaReferencia = a;

        System.out.println("  Libro a = " + a);
        System.out.println("  Libro b = " + b + "   (creado por separado con new)");
        System.out.println("  a == b            -> " + (a == b)
                + "   (false: son DOS objetos distintos en el Heap; == compara identidad)");
        System.out.println("  a.equals(b)       -> " + a.equals(b)
                + "    (true: equals() de Libro compara título y autor, es decir CONTENIDO)");
        System.out.println("  a == mismaReferencia -> " + (a == mismaReferencia)
                + " (true: es literalmente la misma referencia)");

        Pila<Libro> pila = new Pila<>();
        pila.apilar(a);
        System.out.println("  Se apila 'a'. contiene(b) = " + pila.contiene(b)
                + " -> con equals() SÍ lo encuentra.");
        System.out.println("  Si contiene() usara ==, habría devuelto false y el libro");
        System.out.println("  'estaría' en la pila pero el método diría que no.");

        // Contraejemplo con una clase SIN equals(): dos Object de igual "contenido" no son
        // iguales para nada, porque Object.equals() compara identidad de referencia.
        Pila<Object> pilaSinEquals = new Pila<>();
        Object o1 = new Object();
        Object o2 = new Object();
        pilaSinEquals.apilar(o1);
        System.out.println("  Caso de clase SIN equals() sobrescrito: contiene(otro objeto) = "
                + pilaSinEquals.contiene(o2) + " | contiene(el mismo objeto) = "
                + pilaSinEquals.contiene(o1));
    }

    /**
     * Muestra, mediante una línea comentada, que el compilador rechaza apilar un tipo
     * incorrecto: la verificación de tipos de los genéricos ocurre en COMPILACIÓN.
     */
    private static void demostrarVerificacionDeTiposEnCompilacion() {
        System.out.println("\n--- Verificación de tipos en tiempo de COMPILACIÓN ---");
        Pila<String> pilaDeTextos = new Pila<>();
        pilaDeTextos.apilar("texto válido");

        // ===== LÍNEA COMENTADA A PROPÓSITO: EL COMPILADOR LA RECHAZA =====
        // pilaDeTextos.apilar(42);
        //
        // Si se descomenta, javac falla con:
        //   error: incompatible types: int cannot be converted to String
        // El error ocurre en COMPILACIÓN, antes de que el programa llegue a ejecutarse.
        // Eso es exactamente lo que se gana con los genéricos: con una pila de Object esa
        // línea compilaría sin problema y el error aparecería recién en ejecución, como
        // ClassCastException, al castear el resultado de desapilar() a String, en otro
        // lugar del programa y quizá mucho tiempo después.
        // ==================================================================

        System.out.println("  Pila<String> declarada; la línea pilaDeTextos.apilar(42) está");
        System.out.println("  COMENTADA porque javac la rechaza: \"incompatible types: int");
        System.out.println("  cannot be converted to String\". Es mejor fallar en compilación");
        System.out.println("  que arrastrar un ClassCastException hasta ejecución.");
        System.out.println("  Contenido real de la pila:");
        pilaDeTextos.imprimir();
    }

    /**
     * Recorre los casos límite: pila vacía, desapilar y verTope sin elementos, un solo
     * elemento, vaciar y volver a cargar, y el rechazo de null.
     */
    private static void probarCasosLimite() {
        System.out.println("\n--- Casos límite (los mismos del ejercicio 1, más null) ---");
        Pila<String> pila = new Pila<>();

        System.out.println("  Pila vacía: estaVacia() = " + pila.estaVacia()
                + " | tamanio() = " + pila.tamanio());
        pila.imprimir();

        try {
            pila.desapilar();
        } catch (IllegalStateException e) {
            System.out.println("  desapilar() con pila vacía lanzó: " + e.getMessage() + "  -> OK");
        }
        try {
            pila.verTope();
        } catch (IllegalStateException e) {
            System.out.println("  verTope() con pila vacía lanzó: " + e.getMessage() + "  -> OK");
        }
        try {
            pila.apilar(null);
        } catch (IllegalArgumentException e) {
            System.out.println("  apilar(null) lanzó: " + e.getMessage() + "  -> OK");
        }

        System.out.println("  Un solo elemento:");
        pila.apilar("único");
        pila.imprimir();
        System.out.println("  Se vacía por completo: desapilar() = " + pila.desapilar());
        pila.imprimir();
        System.out.println("  Se vuelve a apilar después de vaciar:");
        pila.apilar("nuevo");
        pila.imprimir();
        System.out.println("  El elemento nuevo es accesible: verTope() = " + pila.verTope()
                + " | contiene(\"nuevo\") = " + pila.contiene("nuevo"));
        System.out.println("  contiene(null) = " + pila.contiene(null)
                + " (no se admiten nulls dentro de la pila)");
    }
}


