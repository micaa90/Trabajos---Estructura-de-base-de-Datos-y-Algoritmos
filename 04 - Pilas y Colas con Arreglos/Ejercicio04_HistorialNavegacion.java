/*
 * ============================================================================
 * Ejercicio 4 - Historial de navegación
 * Práctico 4: Pilas y Colas con Arreglos en Java
 * ============================================================================
 *
 * Prompt inicial utilizado:
 * """
 * Analizá este problema: un simulador simple de historial de navegación. Cada vez que el
 * usuario visita una página se apila la URL, y el programa debe permitir volver a la página
 * anterior usando pop.
 * El punto central: explicá por qué el historial funciona bajo el principio LIFO. Desarrollá:
 * - qué espera el usuario cuando aprieta "atrás": volver a la página INMEDIATAMENTE anterior,
 *   que es la última que visitó antes de la actual;
 * - por qué eso es exactamente el comportamiento LIFO, y qué pasaría con una cola (volvería a
 *   la primera página que visitó en la sesión, lo cual es claramente incorrecto);
 * - una sutileza importante que tenés que resolver explícitamente: la página ACTUAL, ¿está
 *   dentro de la pila o fuera? Analizá las dos opciones (tope = página actual, o tope = página
 *   anterior) y elegí una justificando, porque de eso depende qué devuelve la operación "atrás";
 * - qué operación corresponde a visitar una página, a volver atrás y a consultar la página actual.
 * Definí además: la estructura interna sobre arreglo de String de tamaño fijo, qué pasa cuando
 * el historial se llena (si se descarta la entrada más vieja o se rechaza, y las implicancias
 * de cada opción), y los casos límite: volver atrás sin historial, volver atrás con una sola
 * página visitada, historial lleno.
 * Indicá la complejidad de cada operación.
 * No escribas la implementación.
 * """
 *
 * Ajustes realizados:
 * Sí hubo que ajustar el prompt, y el ajuste fue conceptual. El prompt inicial decía "volver a
 * la página anterior usando pop", lo que sugiere que un "atrás" es un solo pop. Al analizar la
 * sutileza de la página actual quedó claro que eso es ambiguo: si el TOPE es la página actual
 * (convención elegida), un "atrás" requiere hacer pop de la actual y DESPUÉS peek de la nueva
 * cima, es decir un pop seguido de una consulta, no un pop que devuelve el destino. Se ajustó
 * el prompt del desarrollador para que volverAtras() devuelva la página a la que se LLEGA y no
 * la que se descarta, porque eso es lo que el usuario percibe al apretar el botón.
 * Segundo ajuste: hubo que decidir explícitamente qué pasa con el historial lleno. El prompt
 * dejaba las dos opciones abiertas; se eligió RECHAZAR con excepción (y se documenta por qué)
 * en lugar de descartar la entrada más vieja, porque descartar exige desplazar todo el arreglo
 * y convierte visitar() en O(n).
 * Tercer ajuste: se agregó un imprimirEstado() además de imprimirHistorial(), porque el
 * historial "lindo" no muestra los índices del arreglo ni los valores residuales.
 *
 * ¿Por qué LIFO? Cuando el usuario aprieta "atrás" espera volver a la página INMEDIATAMENTE
 * anterior, o sea la última que visitó antes de la actual. "La última en entrar es la primera
 * en salir" es exactamente LIFO. Con una cola (FIFO) el botón "atrás" lo mandaría a la PRIMERA
 * página de la sesión: desde la cuarta página de un sitio, "atrás" lo devolvería al buscador
 * inicial en lugar de a la página previa. Claramente incorrecto.
 *
 * CONVENCIÓN ELEGIDA (sin esto el código es ambiguo): el TOPE de la pila ES la página actual.
 * El historial nunca está "vacío" mientras haya una página cargada; visitar = push,
 * consultar la página actual = peek, volver atrás = pop de la actual + peek de la nueva cima.
 * Consecuencia directa: con una sola página visitada NO se puede volver atrás (no existe
 * destino), y eso coincide con el botón "atrás" deshabilitado de un navegador real.
 * La alternativa (tope = página anterior y la actual guardada fuera de la pila) también
 * funciona, pero obliga a mantener dos estados sincronizados: la pila y la variable "actual".
 *
 * Estrategia: pila LIFO de URLs sobre un arreglo de String de tamaño fijo, donde el tope es la
 * página actualmente cargada.
 *
 * Complejidad de las operaciones principales:
 *   visitar, volverAtras, paginaActual, puedeVolverAtras, cantidadPaginas -> O(1).
 *   imprimirHistorial / imprimirEstado -> O(n) (diagnóstico).
 *   Espacial: O(n) con n = profundidad máxima del historial.
 * ============================================================================
 */

/**
 * Programa de demostración del ejercicio 4: simulador de historial de navegación resuelto con
 * una pila sobre arreglo de String de tamaño fijo.
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio04_HistorialNavegacion {

    /**
     * Método principal: simula una sesión de navegación completa y fuerza los casos límite.
     *
     * @param args argumentos de la línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 4 - Historial de navegación (PILA / LIFO) ===");
        System.out.println("Profundidad máxima del historial: 5 páginas");
        System.out.println("Convención: el TOPE de la pila es la página ACTUAL.\n");

        HistorialNavegacion historial = new HistorialNavegacion(5);
        historial.imprimirHistorial();

        // --- Visitar 4 páginas ---
        System.out.println("\n--- El usuario visita 4 páginas ---");
        String[] urls = {
            "buscador.com",
            "buscador.com/resultados?q=pilas",
            "wiki.org/Pila_informatica",
            "wiki.org/LIFO"
        };
        for (String url : urls) {
            historial.visitar(url);
            System.out.println("visitar(\"" + url + "\")");
            historial.imprimirHistorial();
        }
        historial.imprimirEstado();

        // --- Consultar la página actual ---
        System.out.println("\n--- Consultar la página actual (peek) ---");
        System.out.println("paginaActual() -> " + historial.paginaActual());
        System.out.println("El historial NO cambia al consultar: " + historial.cantidadPaginas()
                + " páginas.");

        // --- Volver atrás dos veces ---
        System.out.println("\n--- El usuario aprieta ATRÁS dos veces ---");
        System.out.println("volverAtras() -> llega a: " + historial.volverAtras());
        historial.imprimirHistorial();
        System.out.println("volverAtras() -> llega a: " + historial.volverAtras());
        historial.imprimirHistorial();
        historial.imprimirEstado();

        // --- Visitar una página nueva después de haber vuelto atrás ---
        System.out.println("\n--- El usuario visita una página NUEVA desde acá ---");
        System.out.println("(Observar: las dos páginas que quedaban 'adelante' se pierden)");
        historial.visitar("docs.oracle.com/javase/tutorial");
        System.out.println("visitar(\"docs.oracle.com/javase/tutorial\")");
        historial.imprimirHistorial();
        historial.imprimirEstado();
        System.out.println("  Las URLs 'wiki.org/Pila_informatica' y 'wiki.org/LIFO' ya NO son");
        System.out.println("  alcanzables: la posición 2 fue sobreescrita por la URL nueva y la");
        System.out.println("  posición 3 ya había quedado en null al volver atrás.");
        System.out.println("  Por eso un navegador real pierde el botón 'siguiente' en este punto.");

        // --- Caso límite: historial lleno ---
        System.out.println("\n--- Caso límite: historial LLENO ---");
        historial.visitar("stackoverflow.com/questions/pila");
        historial.visitar("github.com/ejemplos-java");
        System.out.println("Se visitan 2 páginas más hasta agotar la profundidad máxima.");
        historial.imprimirHistorial();
        System.out.println("estaLleno() -> " + historial.estaLleno());
        try {
            System.out.println("visitar(\"otra-pagina.com\")");
            historial.visitar("otra-pagina.com");
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("Excepción capturada: " + e.getMessage());
        }
        historial.imprimirEstado();

        // --- Caso límite: volver atrás hasta agotar el historial ---
        System.out.println("\n--- Caso límite: ATRÁS hasta agotar el historial ---");
        while (historial.puedeVolverAtras()) {
            System.out.println("volverAtras() -> llega a: " + historial.volverAtras()
                    + " | quedan " + historial.cantidadPaginas() + " página(s)");
        }
        historial.imprimirHistorial();
        System.out.println("puedeVolverAtras() -> " + historial.puedeVolverAtras()
                + "   (queda 1 sola página: no hay destino anterior)");
        try {
            historial.volverAtras();
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("volverAtras() -> Excepción capturada: " + e.getMessage());
        }

        // --- Caso límite: historial recién creado, sin ninguna página ---
        System.out.println("\n--- Caso límite: historial SIN ninguna página visitada ---");
        HistorialNavegacion nuevo = new HistorialNavegacion(3);
        nuevo.imprimirEstado();
        try {
            nuevo.paginaActual();
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("paginaActual() -> Excepción capturada: " + e.getMessage());
        }
        try {
            nuevo.volverAtras();
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalStateException e) {
            System.out.println("volverAtras() -> Excepción capturada: " + e.getMessage());
        }
        try {
            nuevo.visitar("   ");
            System.out.println("ERROR: debió lanzar excepción");
        } catch (IllegalArgumentException e) {
            System.out.println("visitar(\"   \") -> Excepción capturada: " + e.getMessage());
        }

        System.out.println("\n--- Contraejemplo: qué pasaría con una COLA (FIFO) ---");
        System.out.println("Sesión: buscador.com -> resultados -> Pila_informatica -> LIFO");
        System.out.println("  Con PILA (LIFO) 'atrás' desde LIFO lleva a 'Pila_informatica'. Correcto.");
        System.out.println("  Con COLA (FIFO) 'atrás' llevaría a 'buscador.com', la PRIMERA de la");
        System.out.println("  sesión, salteando las dos intermedias. El botón dejaría de ser 'atrás'.");

        System.out.println("\n=== Fin de la demostración ===");
    }
}

/**
 * Historial de navegación modelado como una pila (LIFO) sobre un arreglo de String de tamaño
 * fijo.
 *
 * <p><b>Convención de la página actual:</b> el TOPE de la pila es la página actualmente
 * cargada. Esta convención se elige frente a la alternativa (tope = página anterior, con la
 * actual en una variable aparte) porque mantiene un único estado y evita tener que sincronizar
 * la pila con un atributo externo. Su consecuencia observable es que con una sola página
 * visitada no se puede volver atrás, igual que el botón deshabilitado de un navegador real.</p>
 *
 * <p>Correspondencia con las operaciones de pila:
 * {@code visitar} = push, {@code paginaActual} = peek,
 * {@code volverAtras} = pop (de la actual) + peek (de la nueva cima),
 * {@code cantidadPaginas} = size, {@code estaLleno} = isFull.</p>
 *
 * <p><b>Política ante historial lleno:</b> se RECHAZA la nueva visita con excepción. La
 * alternativa sería descartar la entrada más vieja (comportamiento de los navegadores reales,
 * que mantienen un historial acotado), pero eso exige desplazar todo el arreglo un lugar hacia
 * la izquierda en cada visita, degradando {@code visitar} de O(1) a O(n). Rechazar deja la
 * limitación del tamaño fijo a la vista, que es justamente lo que el práctico pide observar.</p>
 *
 * <p>Complejidad: todas las operaciones del dominio son O(1).</p>
 */
class HistorialNavegacion {

    /** Arreglo interno de tamaño fijo: la profundidad máxima del historial. */
    private final String[] paginas;

    /**
     * Índice de la página ACTUAL (el tope de la pila).
     * Vale -1 cuando todavía no se visitó ninguna página.
     */
    private int top;

    /**
     * Crea un historial vacío.
     *
     * <p>Complejidad: O(1) temporal, O(profundidadMaxima) espacial.</p>
     *
     * @param profundidadMaxima cantidad máxima de páginas que recuerda el historial
     * @throws IllegalArgumentException si la profundidad máxima es negativa
     */
    public HistorialNavegacion(int profundidadMaxima) {
        if (profundidadMaxima < 0) {
            throw new IllegalArgumentException(
                    "La profundidad máxima no puede ser negativa: " + profundidadMaxima);
        }
        this.paginas = new String[profundidadMaxima];
        this.top = -1; // ninguna página visitada todavía
    }

    /**
     * Registra la visita a una página nueva, que pasa a ser la página actual.
     * Implementa la operación de pila <b>push</b>.
     *
     * <p>Complejidad: O(1). Incrementa top y escribe una sola posición.</p>
     *
     * @param url dirección de la página visitada
     * @throws IllegalArgumentException si la URL es nula o está en blanco
     * @throws IllegalStateException si el historial alcanzó su profundidad máxima
     */
    public void visitar(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("La URL no puede ser nula ni estar en blanco.");
        }
        if (estaLleno()) {
            throw new IllegalStateException("El historial está lleno: profundidad máxima "
                    + paginas.length + " páginas. No se puede registrar la visita.");
        }
        top++;                 // la página nueva pasa a ser la actual
        paginas[top] = url;    // si había un residuo de una rama abandonada, se sobreescribe
    }

    /**
     * Vuelve a la página inmediatamente anterior y devuelve la página a la que se LLEGA.
     *
     * <p>Son dos operaciones de pila: un <b>pop</b> que descarta la página actual y un
     * <b>peek</b> que devuelve la nueva cima. Se devuelve el destino y no la página
     * descartada porque es lo que el usuario percibe al apretar el botón "atrás".</p>
     *
     * <p>Complejidad: O(1). Un decremento y una lectura indexada.</p>
     *
     * @return la URL de la página a la que se vuelve
     * @throws IllegalStateException si no hay página anterior (0 o 1 páginas en el historial)
     */
    public String volverAtras() {
        if (!puedeVolverAtras()) {
            throw new IllegalStateException("No hay página anterior: el historial tiene "
                    + cantidadPaginas() + " página(s). El botón 'atrás' estaría deshabilitado.");
        }
        paginas[top] = null; // se abandona la página actual; se libera la referencia
        top--;               // el tope pasa a ser la página anterior: ahora es la actual
        return paginas[top]; // peek de la nueva cima
    }

    /**
     * Devuelve la página actualmente cargada sin modificar el historial.
     * Implementa <b>peek</b>.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return la URL de la página actual
     * @throws IllegalStateException si no se visitó ninguna página
     */
    public String paginaActual() {
        if (estaVacio()) {
            throw new IllegalStateException(
                    "No hay ninguna página cargada: el historial está vacío.");
        }
        return paginas[top];
    }

    /**
     * Indica si existe una página anterior a la actual.
     *
     * <p>Hacen falta al menos DOS páginas: una para abandonar y una de destino. Con la
     * convención "tope = página actual", tener una sola página significa que no hay destino.</p>
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return true si el botón "atrás" estaría habilitado
     */
    public boolean puedeVolverAtras() {
        return top >= 1;
    }

    /**
     * Indica si no se visitó ninguna página. Implementa <b>isEmpty</b>.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return true si el historial está vacío
     */
    public boolean estaVacio() {
        return top == -1;
    }

    /**
     * Indica si el historial alcanzó su profundidad máxima. Implementa <b>isFull</b>.
     *
     * <p>Complejidad: O(1).</p>
     *
     * @return true si no se puede registrar otra visita
     */
    public boolean estaLleno() {
        return top == paginas.length - 1;
    }

    /**
     * Devuelve la cantidad de páginas recordadas, incluida la actual. Implementa <b>size</b>.
     *
     * <p>Complejidad: O(1). Dato derivado de top.</p>
     *
     * @return cantidad de páginas en el historial
     */
    public int cantidadPaginas() {
        return top + 1;
    }

    /**
     * Imprime el historial del más reciente al más antiguo, marcando la página actual.
     *
     * <p>Complejidad: O(n) temporal, con n = cantidad de páginas recordadas.</p>
     */
    public void imprimirHistorial() {
        System.out.println("  Historial (" + cantidadPaginas() + "/" + paginas.length + "):");
        if (estaVacio()) {
            System.out.println("    (sin páginas visitadas)");
            return;
        }
        for (int i = top; i >= 0; i--) {
            String etiqueta;
            if (i == top) {
                etiqueta = "  <-- ACTUAL";
            } else if (i == top - 1) {
                etiqueta = "  <-- destino de 'atrás'";
            } else {
                etiqueta = "";
            }
            System.out.println("    " + paginas[i] + etiqueta);
        }
    }

    /**
     * Imprime el estado INTERNO: contenido del arreglo con sus índices y el valor de top.
     *
     * <p>Complejidad: O(capacidad) temporal. Método de diagnóstico.</p>
     */
    public void imprimirEstado() {
        System.out.println("  [estado interno] arreglo:");
        if (paginas.length == 0) {
            System.out.println("    (capacidad 0)");
        }
        for (int i = 0; i < paginas.length; i++) {
            String marca = (i <= top) ? "" : "   * (fuera del historial)";
            System.out.println("    [" + i + "] = " + paginas[i] + marca);
        }
        System.out.println("  [estado interno] top = " + top
                + " | cantidadPaginas = " + cantidadPaginas()
                + " | estaVacio = " + estaVacio()
                + " | estaLleno = " + estaLleno()
                + " | puedeVolverAtras = " + puedeVolverAtras());
    }
}

/*
 * ============================================================================
 * MATRIZ DE CASOS DE PRUEBA MANUALES
 * ============================================================================
 * Compilación: javac Ejercicio04_HistorialNavegacion.java -d /tmp/v4
 * Ejecución:   java -cp /tmp/v4 Ejercicio04_HistorialNavegacion
 * Toda la sesión está cableada en main(); el programa no lee entrada por consola.
 *
 * ID    | Objetivo                              | Entrada exacta                               | Resultado esperado                                                      | Criterio de aprobación
 * ------+---------------------------------------+----------------------------------------------+-------------------------------------------------------------------------+--------------------------------------------------------
 * CP-01 | Flujo nominal: 'atrás' va a la página | HistorialNavegacion(5); visitar de            | volverAtras() devuelve "wiki.org/Pila_informatica" y luego              | Las dos líneas "volverAtras() -> llega a:" muestran
 *       | inmediatamente anterior               | buscador.com, .../resultados?q=pilas,         | "buscador.com/resultados?q=pilas", nunca "buscador.com"                 | esas dos URLs en ese orden
 *       |                                       | wiki.org/Pila_informatica, wiki.org/LIFO;     |                                                                         |
 *       |                                       | volverAtras() x2                              |                                                                         |
 * CP-02 | Estructura vacía: sin páginas          | HistorialNavegacion(3) recién creado;          | IllegalStateException "No hay ninguna página cargada: el historial     | Se imprimen los dos mensajes y top = -1 en el estado
 *       |                                       | paginaActual() y volverAtras()                | está vacío." y "No hay página anterior: el historial tiene 0 página(s)."| interno
 * CP-03 | Rama abandonada: se pierde el         | tras CP-01, visitar("docs.oracle.com/         | top = 2, la posición [2] pasa a tener la URL nueva y la [3] queda      | El estado interno muestra [2] = docs.oracle.com/... y
 *       | 'siguiente'                           | javase/tutorial")                             | en null marcada como "* (fuera del historial)"                          | [3] = null con la marca * (fuera del historial)
 * CP-04 | Error forzado: historial lleno        | visitar hasta 5/5 y luego                     | IllegalStateException "El historial está lleno: profundidad máxima 5   | Se imprime el mensaje y el estado posterior sigue en
 *       |                                       | visitar("otra-pagina.com")                    | páginas. No se puede registrar la visita."                              | top = 4 con estaLleno = true
 * CP-05 | Error forzado: 'atrás' con una sola   | volver atrás hasta que quede 1 página y       | IllegalStateException "No hay página anterior: el historial tiene 1    | Se imprime "puedeVolverAtras() -> false" y el mensaje
 *       | página, y URL en blanco               | luego volverAtras(); además visitar("   ")    | página(s)..." e IllegalArgumentException "La URL no puede ser nula ni   | de las dos excepciones; el programa llega al final
 *       |                                       |                                               | estar en blanco."                                                       |
 * ============================================================================
 */
