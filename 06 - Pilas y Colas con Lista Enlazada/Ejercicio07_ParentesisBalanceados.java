/*
 * ============================================================================
 * Ejercicio 7 - Verificador de paréntesis balanceados
 * Práctico 6 - Pilas y Colas con Listas Enlazadas
 * ============================================================================
 *
 * Prompt inicial utilizado (transcripción literal del prompt del Agente Analista):
 *
 *   Analizá este problema: recibir una expresión y determinar si los paréntesis están
 *   balanceados, usando una PILA ENLAZADA.
 *   Ejemplos válidos: "(2 + 3) * (5 - 1)" y "((a + b) * c)". Ejemplos inválidos: "(2 + 3" y
 *   "())(".
 *   El punto central: explicá CÓMO SE USA UNA PILA PARA GUARDAR LAS APERTURAS Y VALIDAR LOS
 *   CIERRES. Desarrollá:
 *   - por qué el problema necesita memoria del tipo "el último abierto es el primero que se
 *     debe cerrar", y por qué eso es exactamente LIFO;
 *   - el algoritmo: al encontrar una apertura se apila, al encontrar un cierre se desapila;
 *   - las TRES condiciones de invalidez, bien distinguidas:
 *     1) aparece un cierre y la pila está VACÍA: cierre sin apertura previa. Este es el caso
 *        de "())(", analizalo con detalle porque es el ejemplo del enunciado;
 *     2) se termina de recorrer la expresión y la pila NO quedó vacía: aperturas sin cerrar.
 *        Este es el caso de "(2 + 3";
 *     3) si se consideran varios tipos de delimitadores, el tipo del cierre no coincide con
 *        el del tope;
 *   - por qué NO alcanza con contar aperturas y cierres: analizá "())(", que tiene dos de
 *     cada uno y sin embargo es inválido, porque el ORDEN importa. Desarrollá este
 *     contraejemplo, es lo que distingue una solución correcta de una ingenua;
 *   - qué se hace con los caracteres que no son paréntesis;
 *   - por qué una pila ENLAZADA es preferible a una sobre arreglo acá: la profundidad de
 *     anidamiento de la expresión no se conoce de antemano, así que no hay una capacidad fija
 *     razonable;
 *   - la complejidad temporal y espacial, y cuál es la profundidad máxima que puede alcanzar
 *     la pila en función de la longitud de la expresión.
 *   Casos especiales: cadena vacía, cadena sin paréntesis, solo aperturas, solo cierres,
 *   "())(", anidamiento profundo, expresión con varios tipos de delimitadores si se decide
 *   soportarlos.
 *   No escribas la implementación.
 *
 * Ajustes realizados posteriormente al prompt inicial:
 *   1) Se decidió SOPORTAR los tres tipos de delimitadores — (), [] y {} — que el prompt
 *      dejaba como opcionales. Sin eso, la tercera condición de invalidez sería inalcanzable
 *      y no habría forma de demostrarla en la salida.
 *   2) Cada nodo de la pila guarda, además del carácter, la POSICIÓN en la que apareció. No
 *      estaba en el prompt inicial: se agregó para que el mensaje de la condición 2 pueda
 *      informar en qué posición está la apertura que quedó sin cerrar, y no sólo que sobró
 *      una.
 *   3) El resultado se devolvió en un objeto ResultadoValidacion (booleano + número de
 *      condición + motivo + posición + profundidad máxima) en lugar de un boolean suelto. El
 *      prompt pedía que el resultado fuera explicativo; un boolean no puede serlo.
 *   4) Se agregó la traza carácter por carácter con el estado de la pila, controlada por un
 *      parámetro, para poder mostrarla en los ejemplos del enunciado y silenciarla en la
 *      prueba de anidamiento masivo.
 *   No hubo ajustes sobre el algoritmo: apilar en la apertura, desapilar en el cierre y las
 *   tres condiciones de invalidez salieron tal cual del análisis.
 *
 * Estrategia: un solo recorrido de izquierda a derecha apilando cada apertura y desapilando
 * en cada cierre; la pila contiene en todo momento exactamente las aperturas pendientes.
 *
 * Complejidad esperada de las operaciones principales:
 *   apilar O(1)      desapilar O(1)      verTope O(1)      estaVacia O(1)      tamanio O(1)
 *   verificar(expresión de longitud n): O(n) temporal, O(n) espacial en el peor caso.
 *   Profundidad máxima de la pila: n/2 con n aperturas y cierres balanceados (por ejemplo
 *   "((((...))))"), y n si la expresión son TODAS aperturas (caso inválido por condición 2).
 *
 * POR QUÉ UNA PILA. El problema necesita una memoria con una regla muy precisa: "el último
 * delimitador abierto es el primero que se debe cerrar". Esa frase es, palabra por palabra,
 * la definición de LIFO. Cuando aparece un cierre, el único candidato legítimo a emparejar es
 * la apertura MÁS RECIENTE que todavía no se cerró, y la pila entrega exactamente eso en
 * O(1) sin buscar nada. INVARIANTE DEL ALGORITMO: en todo momento la pila contiene
 * exactamente las aperturas que todavía no fueron cerradas, en orden del más reciente al más
 * antiguo.
 *
 * POR QUÉ NO ALCANZA CON CONTAR APERTURAS Y CIERRES. El contraejemplo del enunciado es "())(".
 * Tiene DOS aperturas y DOS cierres: cualquier solución que se limite a contar diría
 * "balanceado". Y es inválido, porque el ORDEN importa. Recorrido:
 *     posición 0: '('  -> se apila. Pila: [ ( ]. Aperturas vistas 1, cierres 0.
 *     posición 1: ')'  -> se desapila, empareja con la de la posición 0. Pila: vacía.
 *     posición 2: ')'  -> LLEGA UN CIERRE Y LA PILA ESTÁ VACÍA: no hay ninguna apertura
 *                         pendiente que pueda emparejar. INVÁLIDO por CONDICIÓN 1.
 *                         Observar que en este instante se habían visto 1 apertura y 2
 *                         cierres: todavía no se había visto la cuarta posición, así que un
 *                         contador que mire el total FINAL (2 y 2) no puede detectar nada.
 *     posición 3: '('  -> (nunca se llega, el error ya se detectó)
 * La diferencia es conceptual: contar verifica una propiedad GLOBAL (la cantidad total),
 * mientras que el balanceo es una propiedad de PREFIJOS: en ningún prefijo de la expresión
 * la cantidad de cierres puede superar a la de aperturas, y además el tipo tiene que
 * coincidir. La pila verifica eso naturalmente, porque "pila vacía" significa exactamente
 * "no quedan aperturas pendientes".
 *
 * LAS TRES CONDICIONES DE INVALIDEZ, bien distinguidas:
 *   CONDICIÓN 1 - cierre con la pila VACÍA (cierre sin apertura previa). Se detecta DURANTE
 *     el recorrido, en la posición exacta del cierre sobrante. Ejemplo: "())(" en posición 2.
 *   CONDICIÓN 2 - fin de la expresión con la pila NO vacía (aperturas sin cerrar). Se detecta
 *     AL TERMINAR el recorrido, y la posición informada es la de la apertura que quedó en el
 *     tope. Ejemplo: "(2 + 3", apertura pendiente en la posición 0.
 *   CONDICIÓN 3 - el tipo del cierre no coincide con el del tope. Sólo existe porque este
 *     verificador soporta (), [] y {}. Ejemplo: "(a + b]" en la posición 6: el tope es '(' y
 *     el cierre es ']'.
 *
 * ORDEN DE EVALUACIÓN: la condición 1 debe verificarse ANTES de desapilar. Si se desapilara
 * primero y se preguntara después, desapilar() sobre la pila vacía lanzaría
 * IllegalStateException y el programa se caería en lugar de informar un resultado
 * explicativo. "¿Está vacía?" es una PRECONDICIÓN de desapilar, no una comprobación
 * posterior.
 *
 * CARACTERES QUE NO SON DELIMITADORES: se IGNORAN por completo (dígitos, letras, espacios,
 * operadores). El balanceo no depende de ellos, así que ni se apilan ni se cuentan; el
 * recorrido simplemente avanza.
 *
 * POR QUÉ PILA ENLAZADA Y NO SOBRE ARREGLO: la profundidad de anidamiento de una expresión no
 * se conoce de antemano (una expresión generada automáticamente puede anidar cientos de
 * niveles), así que no hay ninguna capacidad fija razonable. Con arreglo habría que elegir
 * entre rechazar expresiones válidas por desborde o reservar memoria de más.
 *
 * DIFERENCIA CON EL PRÁCTICO 4 (verificador con pila sobre arreglo): acá NO EXISTE isFull(),
 * y por lo tanto desaparece un caso de error que en la versión con arreglo había que
 * contemplar: "expresión demasiado anidada para la capacidad de la pila", que no es un error
 * de la expresión sino una limitación de la estructura. La versión enlazada sólo puede
 * responder sobre la expresión. El precio es la memoria extra por referencia y la pérdida de
 * localidad: los nodos quedan dispersos en el Heap en lugar de contiguos.
 *
 * @author Estudiante
 * @version 1.0
 */

/**
 * Nodo de la lista simplemente enlazada que soporta la pila de aperturas pendientes.
 *
 * <p>Guarda el carácter de apertura y la POSICIÓN en la que apareció en la expresión. La
 * posición no es necesaria para decidir si la expresión está balanceada, pero sí para que el
 * mensaje de la condición 2 pueda señalar qué apertura quedó sin cerrar.</p>
 */
class Nodo {

    /** Carácter de apertura almacenado: '(', '[' o '{'. */
    char caracter;

    /** Índice (base 0) de la expresión donde apareció este carácter. */
    int posicion;

    /** Apertura pendiente inmediatamente anterior; null si es la más antigua. */
    Nodo siguiente;

    /**
     * Crea un nodo suelto, todavía no enlazado a ninguna pila.
     *
     * @param caracter carácter de apertura
     * @param posicion índice del carácter en la expresión
     */
    Nodo(char caracter, int posicion) {
        this.caracter = caracter;
        this.posicion = posicion;
        this.siguiente = null;
    }
}

/**
 * Pila de caracteres de apertura (política LIFO) sobre lista simplemente enlazada, donde
 * {@code head} es el TOPE.
 *
 * <p>Insertar y eliminar en {@code head} no requiere recorrido, porque {@code head} ya apunta
 * al punto de trabajo: apilar y desapilar son O(1). Si el tope fuera el último nodo, cada
 * carácter de la expresión costaría un recorrido completo y el verificador pasaría de O(n) a
 * O(n²).</p>
 *
 * <p>Además de la pila, la clase registra la PROFUNDIDAD MÁXIMA alcanzada, que es el dato que
 * describe cuánto anidamiento tuvo la expresión.</p>
 *
 * <p>No existe isFull(): no hay capacidad fija. Ver la cabecera del archivo.</p>
 */
class PilaEnlazada {

    /** Tope de la pila: la apertura pendiente más reciente. null si no hay ninguna. */
    private Nodo head;

    /** Cantidad de aperturas pendientes. Se mantiene para que tamanio() sea O(1). */
    private int size;

    /** Mayor valor que alcanzó size durante la vida de esta pila. */
    private int profundidadMaxima;

    /**
     * Crea una pila vacía.
     *
     * <p>Complejidad: O(1)</p>
     */
    PilaEnlazada() {
        this.head = null;
        this.size = 0;
        this.profundidadMaxima = 0;
    }

    /**
     * Apila un carácter de apertura junto con su posición.
     *
     * <p>Complejidad: O(1) - head ya está en el punto de inserción.</p>
     *
     * @param caracter carácter de apertura
     * @param posicion índice del carácter en la expresión
     */
    void apilar(char caracter, int posicion) {
        Nodo nuevo = new Nodo(caracter, posicion);
        // ORDEN: primero el nodo nuevo apunta al tope actual, DESPUÉS head se mueve. Al
        // revés se perdería la referencia al resto de la pila.
        nuevo.siguiente = head;
        head = nuevo;
        size++;
        if (size > profundidadMaxima) {
            profundidadMaxima = size; // la profundidad máxima sólo puede crecer al apilar
        }
    }

    /**
     * Quita y devuelve el carácter del tope.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return el carácter de apertura que estaba en el tope
     * @throws IllegalStateException si la pila está vacía
     */
    char desapilar() {
        if (estaVacia()) {
            throw new IllegalStateException("No se puede desapilar: la pila está vacía.");
        }
        char caracter = head.caracter; // 1) rescatar el dato
        head = head.siguiente;         // 2) el nodo queda inalcanzable -> lo recupera el GC
        size--;
        return caracter;
    }

    /**
     * Consulta el carácter del tope sin quitarlo.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return el carácter de apertura del tope
     * @throws IllegalStateException si la pila está vacía
     */
    char verTope() {
        if (estaVacia()) {
            throw new IllegalStateException("No se puede consultar el tope: la pila está vacía.");
        }
        return head.caracter;
    }

    /**
     * Consulta la posición del carácter del tope sin quitarlo.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return índice en la expresión de la apertura que está en el tope
     * @throws IllegalStateException si la pila está vacía
     */
    int verPosicionTope() {
        if (estaVacia()) {
            throw new IllegalStateException("No se puede consultar el tope: la pila está vacía.");
        }
        return head.posicion;
    }

    /**
     * Indica si no hay aperturas pendientes.
     *
     * <p>Que la pila esté vacía significa exactamente "todas las aperturas vistas hasta acá
     * ya fueron cerradas". Es la comprobación de la condición 1 y de la condición 2.</p>
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return true si la pila está vacía
     */
    boolean estaVacia() {
        return head == null;
    }

    /**
     * Cantidad de aperturas pendientes: la profundidad de anidamiento actual.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return número de elementos en la pila
     */
    int tamanio() {
        return size;
    }

    /**
     * Mayor profundidad de anidamiento alcanzada durante el recorrido.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return profundidad máxima
     */
    int getProfundidadMaxima() {
        return profundidadMaxima;
    }

    /**
     * Devuelve el contenido de la pila del tope al fondo, en formato
     * {@code TOPE -> ( -> ( -> null}.
     *
     * <p>Complejidad: O(n)</p>
     *
     * @return representación textual de la pila
     */
    String contenido() {
        StringBuilder salida = new StringBuilder("TOPE");
        Nodo actual = head;
        while (actual != null) {
            salida.append(" -> ").append(actual.caracter);
            actual = actual.siguiente;
        }
        salida.append(" -> null");
        if (estaVacia()) {
            salida.append("   (vacía)");
        }
        return salida.toString();
    }
}

/**
 * Resultado explicativo de una verificación: no sólo si la expresión está balanceada, sino
 * CUÁL de las tres condiciones de invalidez se cumplió y en qué posición.
 *
 * <p>Un {@code boolean} suelto no puede ser explicativo, y el enunciado pide que el resultado
 * lo sea. Por eso esta clase.</p>
 */
class ResultadoValidacion {

    /** true si la expresión está balanceada. */
    private final boolean balanceado;

    /** Número de condición de invalidez (1, 2 o 3); 0 si la expresión es válida. */
    private final int condicion;

    /** Explicación textual del resultado. */
    private final String motivo;

    /** Posición (base 0) donde se detectó el problema; -1 si no hubo problema. */
    private final int posicion;

    /** Profundidad máxima que alcanzó la pila durante el recorrido. */
    private final int profundidadMaxima;

    /**
     * Crea un resultado de verificación.
     *
     * @param balanceado        true si la expresión está balanceada
     * @param condicion         número de condición de invalidez (0 si es válida)
     * @param motivo            explicación textual
     * @param posicion          posición del problema, o -1
     * @param profundidadMaxima profundidad máxima alcanzada por la pila
     */
    ResultadoValidacion(boolean balanceado, int condicion, String motivo, int posicion,
                        int profundidadMaxima) {
        this.balanceado = balanceado;
        this.condicion = condicion;
        this.motivo = motivo;
        this.posicion = posicion;
        this.profundidadMaxima = profundidadMaxima;
    }

    /**
     * Indica si la expresión está balanceada.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return true si está balanceada
     */
    boolean esBalanceado() {
        return balanceado;
    }

    /**
     * Número de la condición de invalidez que se cumplió.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return 1, 2 o 3; 0 si la expresión es válida
     */
    int getCondicion() {
        return condicion;
    }

    /**
     * Explicación textual del resultado.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return motivo
     */
    String getMotivo() {
        return motivo;
    }

    /**
     * Posición donde se detectó el problema.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return índice base 0, o -1 si no hubo problema
     */
    int getPosicion() {
        return posicion;
    }

    /**
     * Profundidad máxima alcanzada por la pila.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return profundidad máxima
     */
    int getProfundidadMaxima() {
        return profundidadMaxima;
    }

    /**
     * Representación textual completa del resultado.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @return cadena con resultado, condición, motivo, posición y profundidad
     */
    @Override
    public String toString() {
        StringBuilder salida = new StringBuilder();
        salida.append(balanceado ? "BALANCEADO" : "NO BALANCEADO");
        if (!balanceado) {
            salida.append(" (condición ").append(condicion).append(")");
        }
        salida.append(" | motivo: ").append(motivo);
        if (posicion >= 0) {
            salida.append(" | posición: ").append(posicion);
        }
        salida.append(" | profundidad máxima: ").append(profundidadMaxima);
        return salida.toString();
    }
}

/**
 * Programa de demostración del ejercicio 7: verificador de paréntesis balanceados con pila
 * enlazada.
 *
 * <p>Soporta tres tipos de delimitadores: {@code ()}, {@code []} y <code>{}</code>. Prueba
 * los cuatro ejemplos del enunciado más la cadena vacía, una cadena sin paréntesis,
 * {@code "((((1))))"} y los casos de sólo aperturas, sólo cierres y tipo de cierre
 * incorrecto.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio07_ParentesisBalanceados {

    /**
     * Método principal: ejecuta todas las verificaciones de demostración.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 7 - Verificador de paréntesis balanceados (pila LIFO) ===");
        System.out.println("Delimitadores soportados: ( )  [ ]  { }");
        System.out.println("Condición 1: cierre con la pila VACÍA | Condición 2: la pila NO quedó");
        System.out.println("vacía al final | Condición 3: el tipo del cierre no coincide con el tope.");

        System.out.println("\n########## EJEMPLOS VÁLIDOS DEL ENUNCIADO ##########");
        verificarMostrando("(2 + 3) * (5 - 1)", true);
        verificarMostrando("((a + b) * c)", true);

        System.out.println("\n########## EJEMPLOS INVÁLIDOS DEL ENUNCIADO ##########");
        verificarMostrando("(2 + 3", true);
        System.out.println("  Condición 2: quedó una apertura sin cerrar. La posición informada");
        System.out.println("  es la de esa apertura, no la del final de la cadena.");
        verificarMostrando("())(", true);
        System.out.println("  EL CONTRAEJEMPLO CENTRAL: \"())(\" tiene 2 aperturas y 2 cierres, así");
        System.out.println("  que CONTAR diría 'balanceado'. Es inválido porque el ORDEN importa:");
        System.out.println("  en la posición 2 llega un cierre sin ninguna apertura pendiente.");
        System.out.println("  En ese instante se habían visto 1 apertura y 2 cierres; el cuarto");
        System.out.println("  carácter ni se llega a procesar. Un contador del TOTAL (2 y 2) no");
        System.out.println("  puede detectarlo: el balanceo es una propiedad de los PREFIJOS.");

        System.out.println("\n########## CASOS LÍMITE ##########");
        verificarMostrando("", true);
        System.out.println("  Cadena vacía: no hay ningún cierre sin apertura ni apertura sin");
        System.out.println("  cerrar, así que está balanceada por vacuidad. Profundidad 0.");
        verificarMostrando("a + b * 2", true);
        System.out.println("  Sin delimitadores: todos los caracteres se IGNORAN. La pila nunca");
        System.out.println("  se usa y la profundidad máxima queda en 0.");
        verificarMostrando("((((1))))", true);
        System.out.println("  Anidamiento profundo: la profundidad máxima (4) es la cantidad de");
        System.out.println("  aperturas consecutivas, y se alcanza justo antes del primer cierre.");

        System.out.println("\n########## CASOS LÍMITE ADICIONALES ##########");
        verificarMostrando("(((", true);
        System.out.println("  Sólo aperturas: condición 2. Acá la profundidad máxima iguala la");
        System.out.println("  longitud de la cadena, que es el peor caso espacial: O(n).");
        verificarMostrando(")))", true);
        System.out.println("  Sólo cierres: condición 1 en la posición 0, el primer carácter.");
        verificarMostrando("[(a + b)] * {c}", true);
        System.out.println("  Varios tipos de delimitadores correctamente anidados.");
        verificarMostrando("(a + b]", true);
        System.out.println("  Condición 3: el tope es '(' y el cierre es ']'. Las cantidades");
        System.out.println("  están bien (una apertura, un cierre) pero los TIPOS no coinciden.");
        verificarMostrando("([a + b)]", true);
        System.out.println("  Condición 3 por anidamiento cruzado: se cierra '(' cuando el tope");
        System.out.println("  pendiente es '['. Contar no detectaría nada: hay 2 y 2.");

        System.out.println("\n########## VERIFICACIONES AUTOMÁTICAS ##########");
        comprobar("(2 + 3) * (5 - 1)", true, 0, 1);
        comprobar("((a + b) * c)", true, 0, 2);
        comprobar("(2 + 3", false, 2, 1);
        comprobar("())(", false, 1, 1);
        comprobar("", true, 0, 0);
        comprobar("a + b * 2", true, 0, 0);
        comprobar("((((1))))", true, 0, 4);
        comprobar("(((", false, 2, 3);
        comprobar(")))", false, 1, 0);
        comprobar("[(a + b)] * {c}", true, 0, 2);
        comprobar("(a + b]", false, 3, 1);
        comprobar("([a + b)]", false, 3, 2);

        System.out.println("\n--- Sin capacidad fija: anidamiento masivo (sin traza) ---");
        StringBuilder profunda = new StringBuilder();
        for (int i = 0; i < 5000; i++) {
            profunda.append('(');
        }
        profunda.append('x');
        for (int i = 0; i < 5000; i++) {
            profunda.append(')');
        }
        ResultadoValidacion masiva = verificar(profunda.toString(), false);
        System.out.println("  Expresión de " + profunda.length() + " caracteres con 5000 niveles:");
        System.out.println("  " + masiva);
        System.out.println("  En la versión sobre arreglo del práctico 4 esto habría requerido");
        System.out.println("  conocer la profundidad de antemano o consultar isFull().");

        System.out.println("\n--- Orden de evaluación de las condiciones ---");
        mostrarPorQueImportaElOrden();

        System.out.println("\n=== Fin de la demostración ===");
    }

    /**
     * Verifica si los delimitadores de una expresión están balanceados.
     *
     * <p>Recorre la expresión de izquierda a derecha una sola vez. Al encontrar una APERTURA
     * la apila; al encontrar un CIERRE comprueba las condiciones 1 y 3 y desapila. Al
     * terminar comprueba la condición 2. Los caracteres que no son delimitadores se
     * ignoran.</p>
     *
     * <p>Complejidad: O(n) temporal (un paso por carácter, todas las operaciones de pila son
     * O(1)) y O(n) espacial en el peor caso (una expresión de sólo aperturas apila n nodos).</p>
     *
     * @param expresion expresión a verificar; si es null se trata como cadena vacía
     * @param conTraza  true para imprimir el estado de la pila después de cada carácter
     *                  relevante
     * @return resultado explicativo con la condición de invalidez, la posición y la
     *         profundidad máxima
     */
    static ResultadoValidacion verificar(String expresion, boolean conTraza) {
        String texto = expresion == null ? "" : expresion;
        PilaEnlazada pila = new PilaEnlazada();

        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);

            if (esApertura(c)) {
                // --- AQUÍ SE APILA: se registra una apertura pendiente ---
                pila.apilar(c, i);
                if (conTraza) {
                    trazar(i, c, "APILAR  ", pila);
                }
            } else if (esCierre(c)) {
                // *** CONDICIÓN 1 DE INVALIDEZ: CIERRE CON LA PILA VACÍA ***
                // Se comprueba ANTES de desapilar, porque "la pila no está vacía" es una
                // PRECONDICIÓN de desapilar(): invertir el orden provocaría una
                // IllegalStateException en lugar de un resultado explicativo.
                if (pila.estaVacia()) {
                    if (conTraza) {
                        trazar(i, c, "ERROR C1", pila);
                    }
                    return new ResultadoValidacion(false, 1,
                            "Cierre '" + c + "' sin ninguna apertura pendiente que lo empareje "
                                    + "(la pila estaba vacía).",
                            i, pila.getProfundidadMaxima());
                }
                // *** CONDICIÓN 3 DE INVALIDEZ: EL TIPO DEL CIERRE NO COINCIDE CON EL TOPE ***
                // Existe sólo porque se soportan tres tipos de delimitadores. Se comprueba
                // con verTope() (no destructivo) para poder informar el carácter esperado.
                char tope = pila.verTope();
                if (aperturaDe(c) != tope) {
                    if (conTraza) {
                        trazar(i, c, "ERROR C3", pila);
                    }
                    return new ResultadoValidacion(false, 3,
                            "El cierre '" + c + "' no corresponde al tope '" + tope
                                    + "' (abierto en la posición " + pila.verPosicionTope()
                                    + "): se esperaba '" + cierreDe(tope) + "'.",
                            i, pila.getProfundidadMaxima());
                }
                // --- AQUÍ SE DESAPILA: la apertura del tope quedó emparejada ---
                pila.desapilar();
                if (conTraza) {
                    trazar(i, c, "DESAPIL.", pila);
                }
            } else if (conTraza && !Character.isWhitespace(c)) {
                // Carácter que no es delimitador: se IGNORA. Se traza sólo para que se vea
                // que el algoritmo lo atravesó sin tocar la pila.
                trazar(i, c, "IGNORAR ", pila);
            }
        }

        // *** CONDICIÓN 2 DE INVALIDEZ: SE TERMINÓ LA EXPRESIÓN Y LA PILA NO QUEDÓ VACÍA ***
        // Cada nodo que sobró es una apertura que nunca se cerró. Se informa la del tope,
        // que es la más reciente de las pendientes.
        if (!pila.estaVacia()) {
            return new ResultadoValidacion(false, 2,
                    "Se terminó la expresión con " + pila.tamanio()
                            + " apertura(s) sin cerrar; la más reciente es '" + pila.verTope()
                            + "' abierta en la posición " + pila.verPosicionTope() + ".",
                    pila.verPosicionTope(), pila.getProfundidadMaxima());
        }

        // Pila vacía al terminar y ningún error durante el recorrido: BALANCEADO.
        return new ResultadoValidacion(true, 0,
                "Todas las aperturas se cerraron en el orden correcto y con el tipo correcto.",
                -1, pila.getProfundidadMaxima());
    }

    /**
     * Indica si un carácter es una apertura soportada.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @param c carácter a clasificar
     * @return true si es '(', '[' o '{'
     */
    private static boolean esApertura(char c) {
        return c == '(' || c == '[' || c == '{';
    }

    /**
     * Indica si un carácter es un cierre soportado.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @param c carácter a clasificar
     * @return true si es ')', ']' o '}'
     */
    private static boolean esCierre(char c) {
        return c == ')' || c == ']' || c == '}';
    }

    /**
     * Devuelve la apertura que corresponde a un cierre dado.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @param cierre carácter de cierre
     * @return la apertura correspondiente, o el espacio si el carácter no es un cierre
     */
    private static char aperturaDe(char cierre) {
        if (cierre == ')') {
            return '(';
        }
        if (cierre == ']') {
            return '[';
        }
        if (cierre == '}') {
            return '{';
        }
        return ' ';
    }

    /**
     * Devuelve el cierre que corresponde a una apertura dada.
     *
     * <p>Complejidad: O(1)</p>
     *
     * @param apertura carácter de apertura
     * @return el cierre correspondiente, o el espacio si el carácter no es una apertura
     */
    private static char cierreDe(char apertura) {
        if (apertura == '(') {
            return ')';
        }
        if (apertura == '[') {
            return ']';
        }
        if (apertura == '{') {
            return '}';
        }
        return ' ';
    }

    /**
     * Imprime una línea de traza con el carácter procesado, la acción y el estado de la pila.
     *
     * @param posicion índice del carácter en la expresión
     * @param c        carácter procesado
     * @param accion   etiqueta de la acción tomada
     * @param pila     pila después de la acción
     */
    private static void trazar(int posicion, char c, String accion, PilaEnlazada pila) {
        System.out.println("      pos " + posicion + "  '" + c + "'  " + accion + "  "
                + pila.contenido() + "   [profundidad=" + pila.tamanio() + "]");
    }

    /**
     * Verifica una expresión imprimiendo la traza y el resultado completo.
     *
     * @param expresion expresión a verificar
     * @param conTraza  true para imprimir el estado de la pila carácter por carácter
     */
    private static void verificarMostrando(String expresion, boolean conTraza) {
        System.out.println("\n  Expresión: \"" + expresion + "\"");
        ResultadoValidacion resultado = verificar(expresion, conTraza);
        System.out.println("  Resultado: " + resultado);
    }

    /**
     * Comprueba automáticamente que una expresión produzca el resultado, la condición y la
     * profundidad esperados.
     *
     * @param expresion                 expresión a verificar
     * @param balanceadoEsperado         resultado booleano esperado
     * @param condicionEsperada          número de condición esperado (0 si es válida)
     * @param profundidadMaximaEsperada  profundidad máxima esperada
     */
    private static void comprobar(String expresion, boolean balanceadoEsperado,
                                  int condicionEsperada, int profundidadMaximaEsperada) {
        ResultadoValidacion r = verificar(expresion, false);
        boolean ok = r.esBalanceado() == balanceadoEsperado
                && r.getCondicion() == condicionEsperada
                && r.getProfundidadMaxima() == profundidadMaximaEsperada;
        System.out.println("  \"" + expresion + "\"  ->  balanceado=" + r.esBalanceado()
                + ", condición=" + r.getCondicion()
                + ", profundidad máxima=" + r.getProfundidadMaxima()
                + "   (esperado: " + balanceadoEsperado + ", " + condicionEsperada + ", "
                + profundidadMaximaEsperada + ")  -> " + (ok ? "OK" : "FALLO"));
    }

    /**
     * Demuestra por qué la condición 1 debe comprobarse ANTES de desapilar, provocando a
     * propósito la excepción que se evitaría con el orden correcto.
     */
    private static void mostrarPorQueImportaElOrden() {
        System.out.println("  La condición 1 se comprueba ANTES de desapilar. Si se hiciera al");
        System.out.println("  revés, desapilar() sobre la pila vacía lanzaría una excepción en");
        System.out.println("  lugar de devolver un resultado explicativo. Demostración directa:");
        PilaEnlazada pila = new PilaEnlazada();
        try {
            pila.desapilar();
            System.out.println("  desapilar() sobre pila vacía no lanzó excepción -> FALLO");
        } catch (IllegalStateException e) {
            System.out.println("  desapilar() sobre pila vacía lanzó IllegalStateException: "
                    + e.getMessage() + "  -> OK");
        }
        System.out.println("  Por eso estaVacia() es una PRECONDICIÓN de desapilar y no una");
        System.out.println("  comprobación posterior.");
    }
}

/*
MATRIZ DE CASOS DE PRUEBA MANUALES
Compilación: javac Ejercicio07_ParentesisBalanceados.java -d /tmp/v6/ej07
Ejecución:   java -cp /tmp/v6/ej07 Ejercicio07_ParentesisBalanceados
Todas las expresiones están cableadas en main(); el programa no lee entrada por consola.

ID    | Objetivo                                                     | Entrada exacta                            | Resultado esperado                                                                  | Criterio de aprobación
------+--------------------------------------------------------------+-------------------------------------------+-------------------------------------------------------------------------------------+------------------------------------------------------------------
CP-01 | Flujo nominal: los dos ejemplos válidos del enunciado        | "(2 + 3) * (5 - 1)" y "((a + b) * c)"     | BALANCEADO con profundidad máxima 1 y 2 respectivamente                             | Las dos líneas de comprobación automática informan "-> OK"
CP-02 | Condición 2: aperturas sin cerrar                            | "(2 + 3"                                  | NO BALANCEADO (condición 2), posición 0, profundidad máxima 1                        | El resultado informa condición 2 y señala la apertura de la posición 0
CP-03 | Condición 1 y contraejemplo de contar: el ORDEN importa      | "())("                                    | NO BALANCEADO (condición 1), posición 2, profundidad máxima 1                        | El resultado informa condición 1 y posición 2, pese a haber 2 aperturas y 2 cierres
CP-04 | Valores límite: cadena vacía, sin paréntesis y muy anidada   | "", "a + b * 2" y "((((1))))"             | Las tres BALANCEADO, con profundidad máxima 0, 0 y 4                                 | Las tres comprobaciones automáticas informan "-> OK"
CP-05 | Condición 3 y error forzado de la pila                       | "(a + b]" y desapilar() sobre pila vacía  | NO BALANCEADO (condición 3) en la posición 6, e IllegalStateException "No se puede desapilar: la pila está vacía." | El resultado informa condición 3 y se imprime el mensaje de la excepción seguido de "-> OK"
*/
