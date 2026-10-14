/*
=====================================================================================
Práctico 5 - Listas Enlazadas Simples. Ejercicio 8: Contar ocurrencias de un valor
=====================================================================================

Prompt inicial utilizado:

  "Analizá el diseño del método int contarOcurrencias(int dato) sobre una lista enlazada simple,
  que debe recorrer TODA la lista y contar cuántas veces aparece un valor.
  Ejemplo: en 10 -> 20 -> 10 -> 30 -> 10 -> null, contarOcurrencias(10) debe devolver 3.
  El punto central: explicá POR QUÉ NO SE DEBE CORTAR EL RECORRIDO AL ENCONTRAR LA PRIMERA
  COINCIDENCIA. Desarrollá:
  - el contraste con el método buscar(int) del ejercicio 2: buscar responde "existe o no", y
    una sola coincidencia ya determina la respuesta definitiva, así que cortar es correcto y
    además conveniente;
  - en cambio contarOcurrencias responde "cuántas veces", y una coincidencia no determina nada
    sobre el resto de la lista: podría haber más adelante. Cortar ahí daría siempre 1 como
    resultado máximo, que es incorrecto. Desarrollá este contraste, es lo que evalúa el ejercicio;
  - la regla general detrás: los problemas de EXISTENCIA admiten corte anticipado, los
    problemas de AGREGACIÓN (contar, sumar, promediar, encontrar el máximo) exigen recorrido
    completo. Enunciá esta regla y relacionala con el ejercicio 5 del práctico de análisis;
  - por qué, en consecuencia, el mejor caso y el peor caso de este método coinciden en O(n);
  - qué invariante cumple el contador durante el recorrido;
  - cómo se recorre con la variable auxiliar y la condición de corte única (llegar a null).
  Casos límite: lista vacía, el dato no aparece, aparece una vez, aparece en todos los nodos,
  aparece en el primero y en el último.
  Indicá la complejidad temporal y espacial.
  No escribas la implementación."

Ajustes realizados luego de la primera respuesta de OpenCode:

1) La primera respuesta reutilizaba el esqueleto de buscar() y hacía return en cuanto
    encontraba una coincidencia, así que devolvía 1 como máximo. Se quitó el return
    anticipado: contar es un problema de AGREGACIÓN y exige recorrido completo.
 2) Se agregó el contador de nodos visitados también a este método, para poder contrastar
    en el main que buscar(10) visita 1 nodo y contarOcurrencias(10) visita los 5.
 3) Se pidió documentar por qué acá el mejor caso y el peor caso coinciden en O(n), cosa
    que la primera respuesta no explicaba: no existe ninguna entrada afortunada que
    permita terminar antes.

Estrategia: recorrido completo con una variable auxiliar y un contador que se incrementa en cada coincidencia, sin corte anticipado.

Complejidad temporal de contarOcurrencias: O(n) siempre. Mejor caso = peor caso =
promedio, porque el recorrido no puede interrumpirse: hasta no llegar a null no se sabe si
queda otra aparición.
Complejidad espacial: O(1) — una referencia auxiliar y dos contadores enteros.
=====================================================================================
*/

/**
 * Ejercicio 8 del Práctico 5 (Listas Enlazadas Simples): conteo de las apariciones de un
 * valor en la lista.
 *
 * <p>El eje del ejercicio es por qué NO se corta el recorrido en la primera coincidencia: los
 * problemas de existencia ({@code buscar}) admiten corte anticipado, los de agregación
 * ({@code contarOcurrencias}) exigen recorrer todo.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio08_ContarOcurrencias {

    /**
     * Método principal: ejecuta las pruebas manuales del ejercicio.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Práctico 5 - Ejercicio 8: contar ocurrencias de un valor ===");

        System.out.println();
        System.out.println("--- 1) Lista del enunciado: 10 -> 20 -> 10 -> 30 -> 10 -> null ---");
        ListaEnlazada lista = new ListaEnlazada();
        lista.insertarAlFinal(10);
        lista.insertarAlFinal(20);
        lista.insertarAlFinal(10);
        lista.insertarAlFinal(30);
        lista.insertarAlFinal(10);
        mostrarEstado("Estado:", lista);

        System.out.println();
        System.out.println("--- 2) Verificación del enunciado: contarOcurrencias(10) debe ser 3 ---");
        int ocurrencias = lista.contarOcurrencias(10);
        System.out.println("contarOcurrencias(10) = " + ocurrencias
                + " | nodos visitados = " + lista.getNodosVisitados()
                + " de " + lista.getSize());
        System.out.println("Resultado esperado 3, obtenido " + ocurrencias + ": "
                + (ocurrencias == 3 ? "OK" : "FALLA"));

        System.out.println();
        System.out.println("--- 3) Contraste con buscar(10): existencia contra agregación ---");
        boolean existe = lista.buscar(10);
        int visitadosBuscar = lista.getNodosVisitados();
        int cuenta = lista.contarOcurrencias(10);
        int visitadosContar = lista.getNodosVisitados();
        System.out.println("buscar(10) = " + existe + " visitando " + visitadosBuscar + " nodo(s):");
        System.out.println("    la primera coincidencia ya determina la respuesta definitiva.");
        System.out.println("contarOcurrencias(10) = " + cuenta + " visitando " + visitadosContar
                + " nodo(s):");
        System.out.println("    una coincidencia no dice nada sobre el resto, así que hay que seguir.");
        System.out.println("La diferencia (" + visitadosBuscar + " contra " + visitadosContar
                + ") no es una ineficiencia: es el costo mínimo de la pregunta.");

        System.out.println();
        System.out.println("--- 4) El dato aparece UNA sola vez ---");
        probarConteo(lista, 20);

        System.out.println();
        System.out.println("--- 5) El dato NO aparece ---");
        probarConteo(lista, 99);

        System.out.println();
        System.out.println("--- 6) Caso límite: lista vacía ---");
        ListaEnlazada vacia = new ListaEnlazada();
        mostrarEstado("Estado:", vacia);
        probarConteo(vacia, 10);

        System.out.println();
        System.out.println("--- 7) El dato aparece en TODOS los nodos ---");
        ListaEnlazada todos = new ListaEnlazada();
        todos.insertarAlFinal(8);
        todos.insertarAlFinal(8);
        todos.insertarAlFinal(8);
        mostrarEstado("Estado:", todos);
        probarConteo(todos, 8);

        System.out.println();
        System.out.println("--- 8) El dato aparece en el primero y en el último ---");
        ListaEnlazada extremos = new ListaEnlazada();
        extremos.insertarAlFinal(10);
        extremos.insertarAlFinal(20);
        extremos.insertarAlFinal(30);
        extremos.insertarAlFinal(10);
        mostrarEstado("Estado:", extremos);
        probarConteo(extremos, 10);
        System.out.println("    Si se cortara en la primera coincidencia, la del último nodo se perdería.");

        System.out.println();
        System.out.println("--- 9) Verificación de consistencia ---");
        System.out.println("contarOcurrencias() es de sólo lectura: no toca enlaces ni size.");
        mostrarEstado("Lista del enunciado intacta:", lista);
    }

    /**
     * Cuenta las apariciones de un dato e informa el resultado y los nodos visitados.
     *
     * Complejidad: O(n).
     *
     * @param lista lista a recorrer
     * @param dato  entero cuyas apariciones se cuentan
     */
    private static void probarConteo(ListaEnlazada lista, int dato) {
        int ocurrencias = lista.contarOcurrencias(dato);
        System.out.println("contarOcurrencias(" + dato + ") = " + ocurrencias
                + " | nodos visitados = " + lista.getNodosVisitados()
                + " de " + lista.getSize()
                + " (siempre se visitan todos)");
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
 * <p>Métodos disponibles en este ejercicio: insertarAlInicio, insertarAlFinal, imprimir, estaVacia, getSize, buscar, obtener, insertarEnPosicion, eliminar, eliminarEnPosicion, modificar e contarOcurrencias.</p>
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
     * Cuenta cuántas veces aparece un dato en la lista, recorriéndola COMPLETA.
     *
     * <p><b>Por qué no se corta el recorrido en la primera coincidencia:</b> hay que
     * distinguir dos clases de problema. {@code buscar} responde "¿existe?", y una sola
     * coincidencia ya determina la respuesta definitiva, así que cortar es correcto y además
     * conveniente. {@code contarOcurrencias} responde "¿cuántas veces?", y una coincidencia
     * no determina nada sobre el resto de la lista: podría haber más adelante. Cortar en la
     * primera daría como máximo 1, que es un resultado incorrecto.</p>
     *
     * <p>La regla general: los problemas de EXISTENCIA admiten corte anticipado; los
     * problemas de AGREGACIÓN (contar, sumar, promediar, buscar el máximo) exigen recorrido
     * completo. De ahí que acá el mejor caso y el peor caso coincidan en O(n), y que la única
     * condición de corte sea llegar a {@code null}.</p>
     *
     * <p>Invariante del contador: al terminar de procesar el nodo {@code k}, el contador vale
     * exactamente la cantidad de apariciones del dato entre los nodos {@code 0..k}.</p>
     *
     * Complejidad: O(n) siempre, mejor caso = peor caso = promedio. O(1) espacial.
     *
     * @param dato entero cuyas apariciones se cuentan
     * @return cantidad de nodos que contienen ese dato; 0 si no aparece o la lista está vacía
     */
    int contarOcurrencias(int dato) {
        nodosVisitados = 0;
        int ocurrencias = 0;
        Nodo actual = head;
        while (actual != null) { // única condición de corte: el final de la cadena
            nodosVisitados++;
            if (actual.getDato() == dato) {
                ocurrencias++; // se sigue recorriendo: puede haber más apariciones
            }
            actual = actual.getSiguiente();
        }
        return ocurrencias;
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

/*
MATRIZ DE CASOS DE PRUEBA MANUALES
Compilación: javac Ejercicio08_ContarOcurrencias.java -d /tmp/v5/ej08
Ejecución:   java -cp /tmp/v5/ej08 Ejercicio08_ContarOcurrencias
Todos los casos están cableados en main(): el programa no lee entrada por consola,
así que cada caso se verifica leyendo el bloque correspondiente de la salida.

ID    | Objetivo                                         | Entrada exacta                                  | Resultado esperado                                   | Criterio de aprobación
------+--------------------------------------------------+-------------------------------------------------+------------------------------------------------------+------------------------------------------------
CP-01 | Flujo nominal: el caso del enunciado            | lista 10,20,10,30,10; contarOcurrencias(10)     | 3 con 5 nodos visitados                              | Bloque 2 imprime 3 y la línea termina en "OK"
CP-02 | Lista vacía: el ciclo no se ejecuta             | lista sin inserciones; contarOcurrencias(10)    | 0 con 0 nodos visitados                              | Bloque 6 imprime 0 y 0 sin lanzar excepción
CP-03 | Primer nodo: contraste contra buscar()          | lista 10,20,10,30,10; buscar(10)                | true visitando 1 nodo, contra 5 del conteo           | Bloque 3 imprime 1 contra 5
CP-04 | Último nodo: la coincidencia final no se pierde | lista 10,20,30,10; contarOcurrencias(10)        | 2 con 4 nodos visitados                              | Bloque 8 imprime 2 (no 1)
CP-05 | Dato inexistente (este método no valida         | lista 10,20,10,30,10; contarOcurrencias(99)     | 0 con 5 nodos visitados                              | Bloque 5: 0 ocurrencias y visitados = size
      | posiciones, así que no hay caso de rango)       |                                                 |                                                      |
*/
