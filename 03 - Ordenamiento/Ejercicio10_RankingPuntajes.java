/**
 * Ejercicio 10 - Sistema de ranking de puntajes.
 *
 * <p>Recibe un arreglo de jugadores con nombre y puntaje y arma el ranking de MAYOR A MENOR
 * puntaje. Muestra el arreglo sin ordenar y despues el ranking numerado con posicion, nombre
 * y puntaje, y verifica el resultado con un metodo independiente del algoritmo.</p>
 *
 * <h2>JUSTIFICACION DE LA ELECCION DEL ALGORITMO: Insertion Sort</h2>
 * <ul>
 *   <li><b>Tamano de los datos.</b> El ranking de una partida o de una tabla de posiciones
 *       maneja decenas o a lo sumo unos pocos cientos de jugadores. En ese rango la
 *       diferencia entre O(n^2) y O(n log n) es irrelevante en tiempo real, y pesa mas la
 *       simplicidad y la ausencia de memoria extra. Si el ranking fuera de millones de
 *       jugadores la decision cambiaria a MergeSort o a un Quicksort con buen pivote.</li>
 *   <li><b>Datos casi ordenados.</b> Un ranking no se arma de cero cada vez: normalmente ya
 *       esta ordenado y solo se le agregan o actualizan unos pocos puntajes. Insertion Sort
 *       es el unico de los algoritmos simples que aprovecha eso: su mejor caso es O(n)
 *       porque el while interno corta en la primera comparacion cuando el elemento ya esta
 *       en su lugar. Reordenar un ranking casi ordenado le cuesta practicamente un solo
 *       recorrido.</li>
 *   <li><b>Estabilidad y criterio de desempate.</b> CRITERIO DECLARADO: a igual puntaje gana
 *       el nombre alfabeticamente menor (orden ascendente por nombre). Se elige asi porque
 *       es un orden TOTAL y determinista: el ranking publicado no depende del orden en que
 *       se cargaron los jugadores, y dos ejecuciones con los mismos datos en distinto orden
 *       de entrada producen la misma tabla, lo cual es deseable para un resultado publico.
 *       Al estar el desempate dentro de la propia comparacion, la estabilidad del algoritmo
 *       deja de ser un requisito. Si en cambio el criterio hubiese sido "orden de llegada",
 *       la estabilidad pasaria a ser obligatoria y quedarian descartados Selection Sort y
 *       Quicksort, que no son estables; Insertion Sort seguiria sirviendo porque SI es
 *       estable.</li>
 *   <li><b>Costo de mover objetos.</b> En Java un arreglo de {@code Jugador} guarda
 *       REFERENCIAS, no los objetos: mover un jugador cuesta copiar una referencia (4 u 8
 *       bytes), igual que mover un entero, y no se copia ni se clona el objeto. Por eso el
 *       mayor volumen de desplazamientos de Insertion Sort frente a los pocos intercambios
 *       de Selection Sort no es un problema en esta escala. Si los elementos fueran
 *       estructuras grandes copiadas por valor, Selection Sort seria preferible por hacer a
 *       lo sumo n-1 movimientos.</li>
 *   <li><b>Peor caso.</b> El peor caso de Insertion Sort es O(n^2) y se da con el arreglo
 *       exactamente invertido, es decir un ranking cargado de menor a mayor puntaje. Es un
 *       escenario posible, pero con n de decenas significa unos pocos miles de operaciones:
 *       inofensivo.</li>
 *   <li><b>ALGORITMO DESCARTADO: Quicksort con el primer elemento como pivote.</b> Su peor
 *       caso O(n^2) se dispara justamente con entradas YA ORDENADAS, que es el caso mas
 *       frecuente de un ranking que se actualiza. Ademas consume O(n) de pila en ese
 *       escenario. Tambien se descarta MergeSort: garantiza O(n log n) pero necesita O(n) de
 *       memoria auxiliar y no aporta nada visible con decenas de elementos.</li>
 * </ul>
 *
 * <p><b>Como se invirtio el criterio para ordenar de mayor a menor.</b> La version ascendente
 * de Insertion Sort desplaza mientras {@code arreglo[j] > clave}. Aqui se desplaza mientras
 * {@code vaAntes(clave, arreglo[j])}, es decir mientras la clave DEBA ir antes que el
 * elemento ya ubicado; y {@code vaAntes} define "antes" como puntaje MAYOR. Esa unica
 * inversion del sentido de la comparacion convierte el ordenamiento ascendente en
 * descendente, sin tocar la estructura del algoritmo.</p>
 *
 * <p><b>Definicion de los contadores.</b> <i>comparacion</i>: cada llamada a
 * {@code vaAntes(...)} entre dos jugadores (incluida la que corta el while).
 * <i>desplazamiento</i>: cada copia de una referencia a {@code Jugador} un lugar a la
 * derecha. La escritura final de la clave en su hueco no se cuenta como desplazamiento.</p>
 *
 * <p>Complejidad: O(n) en el mejor caso (ranking ya ordenado), O(n^2) en el peor (ranking
 * invertido) y O(n^2) en promedio; O(1) espacial adicional, ordena in situ sobre el arreglo
 * de referencias. Comparar dos jugadores es O(1) por el puntaje y O(L) solo cuando hay
 * empate y hay que mirar el nombre.</p>
 *
 * @author Estudiante
 * @version 1.0
 */
public class Ejercicio10_RankingPuntajes {

    /**
     * Metodo principal. Arma el ranking de 6 jugadores (con dos pares empatados), muestra la
     * verificacion y recorre los casos limite.
     *
     * @param args argumentos de la linea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        System.out.println("=== EJERCICIO 10 - SISTEMA DE RANKING DE PUNTAJES ===");
        System.out.println("Algoritmo elegido: Insertion Sort (descendente por puntaje).");
        System.out.println("Criterio de desempate declarado: a igual puntaje, nombre alfabetico ascendente.");

        Jugador[] jugadores = {
            new Jugador("Ana", 1200),
            new Jugador("Pedro", 900),
            new Jugador("Lucia", 1500),
            new Jugador("Sofia", 1200),   // empate con Ana
            new Jugador("Bruno", 1100),
            new Jugador("Diego", 900)     // empate con Pedro
        };

        armarRanking("Caso nominal (6 jugadores, dos pares empatados)", jugadores);

        System.out.println("\n=== CASOS LIMITE ===");
        resumen("Arreglo vacio", new Jugador[] {});
        resumen("Un solo jugador", new Jugador[] {new Jugador("Ana", 1200)});
        resumen("Ya ordenado (mejor caso)", new Jugador[] {
            new Jugador("Lucia", 1500), new Jugador("Ana", 1200),
            new Jugador("Bruno", 1100), new Jugador("Diego", 900)
        });
        resumen("Invertido (peor caso)", new Jugador[] {
            new Jugador("Diego", 900), new Jugador("Bruno", 1100),
            new Jugador("Ana", 1200), new Jugador("Lucia", 1500)
        });
        resumen("Todos con el mismo puntaje", new Jugador[] {
            new Jugador("Pedro", 1000), new Jugador("Ana", 1000),
            new Jugador("Diego", 1000), new Jugador("Bruno", 1000)
        });
        resumen("Puntajes cero y negativos", new Jugador[] {
            new Jugador("Ana", 0), new Jugador("Pedro", -50), new Jugador("Lucia", 10)
        });

        System.out.println("\n--- Entradas invalidas (deben lanzar excepcion) ---");
        try {
            ordenarRanking(null, new MetricasRanking());
            System.out.println("ERROR: no se lanzo la excepcion esperada.");
        } catch (IllegalArgumentException e) {
            System.out.println("Arreglo nulo -> IllegalArgumentException: " + e.getMessage());
        }
        try {
            ordenarRanking(new Jugador[] {new Jugador("Ana", 10), null}, new MetricasRanking());
            System.out.println("ERROR: no se lanzo la excepcion esperada.");
        } catch (IllegalArgumentException e) {
            System.out.println("Jugador nulo -> IllegalArgumentException: " + e.getMessage());
        }
        try {
            Jugador invalido = new Jugador("", 100);
            System.out.println("ERROR: no se lanzo la excepcion esperada para " + invalido);
        } catch (IllegalArgumentException e) {
            System.out.println("Nombre vacio -> IllegalArgumentException: " + e.getMessage());
        }

        System.out.println("\n=== CONTRASTE CON LA TEORIA ===");
        System.out.println("Mejor caso (ya ordenado) con n = 4: comparaciones = n-1 = 3 y"
                + " desplazamientos = 0.");
        System.out.println("Peor caso (invertido) con n = 4: comparaciones = desplazamientos ="
                + " n(n-1)/2 = " + (4 * 3 / 2) + ".");
    }

    /**
     * Ordena una copia del arreglo de jugadores e imprime el ranking numerado y su verificacion.
     *
     * @param titulo rotulo del escenario
     * @param original arreglo de jugadores sin ordenar; no se modifica
     * @throws IllegalArgumentException si {@code original} es nulo o contiene nulos
     */
    public static void armarRanking(String titulo, Jugador[] original) {
        if (original == null) {
            throw new IllegalArgumentException("El arreglo de jugadores no puede ser nulo.");
        }
        System.out.println("\n--- " + titulo + " ---");
        System.out.println("Jugadores sin ordenar:");
        for (int i = 0; i < original.length; i++) {
            System.out.println("  " + original[i]);
        }

        Jugador[] copia = copiar(original);
        MetricasRanking m = new MetricasRanking();
        m.mostrarTraza = true;
        ordenarRanking(copia, m);

        System.out.println("\nRANKING FINAL (mayor a menor puntaje):");
        System.out.println("+----------+----------------+----------+");
        System.out.println("| Posicion | Jugador        | Puntaje  |");
        System.out.println("+----------+----------------+----------+");
        for (int i = 0; i < copia.length; i++) {
            System.out.printf("| %8d | %-14s | %8d |%n",
                    i + 1, copia[i].getNombre(), copia[i].getPuntaje());
        }
        System.out.println("+----------+----------------+----------+");
        System.out.println("Comparaciones: " + m.comparaciones
                + " | Desplazamientos de referencias: " + m.desplazamientos);
        System.out.println("Verificacion estaOrdenado(): " + estaOrdenado(copia));
        System.out.println("(verifica puntaje[i] >= puntaje[i+1] y, ante empate, nombre[i] <= nombre[i+1])");
    }

    /**
     * Ordena una copia del arreglo y muestra solo el resumen en una linea.
     *
     * @param titulo rotulo del escenario
     * @param original arreglo de jugadores; no se modifica
     * @throws IllegalArgumentException si {@code original} es nulo o contiene nulos
     */
    public static void resumen(String titulo, Jugador[] original) {
        if (original == null) {
            throw new IllegalArgumentException("El arreglo de jugadores no puede ser nulo.");
        }
        Jugador[] copia = copiar(original);
        MetricasRanking m = new MetricasRanking();
        ordenarRanking(copia, m);
        System.out.println(titulo + ": " + aTexto(original) + " -> " + aTexto(copia)
                + " | comparaciones=" + m.comparaciones
                + " desplazamientos=" + m.desplazamientos
                + " | ordenado=" + estaOrdenado(copia));
    }

    /**
     * Ordena el arreglo de jugadores de mayor a menor puntaje con Insertion Sort, aplicando
     * el desempate alfabetico por nombre.
     *
     * @param ranking arreglo de jugadores a ordenar in situ
     * @param metricas acumulador de comparaciones y desplazamientos
     * @throws IllegalArgumentException si {@code ranking} o {@code metricas} son nulos, o si
     *         algun elemento del arreglo es nulo
     */
    public static void ordenarRanking(Jugador[] ranking, MetricasRanking metricas) {
        if (ranking == null) {
            throw new IllegalArgumentException("El arreglo de jugadores no puede ser nulo.");
        }
        if (metricas == null) {
            throw new IllegalArgumentException("El acumulador de metricas no puede ser nulo.");
        }
        // Se valida antes de comparar: un elemento nulo haria fallar getPuntaje() en medio
        // del ordenamiento y dejaria el ranking a medio armar.
        for (int i = 0; i < ranking.length; i++) {
            if (ranking[i] == null) {
                throw new IllegalArgumentException("El arreglo no puede contener jugadores nulos (posicion " + i + ").");
            }
        }

        // Invariante: ranking[0..i-1] ya esta ordenado de mayor a menor puntaje.
        for (int i = 1; i < ranking.length; i++) {
            Jugador clave = ranking[i];
            int j = i - 1;
            long desplazamientosDeEstaClave = 0;

            while (j >= 0) {
                metricas.comparaciones++;
                // Sentido INVERTIDO respecto del Insertion Sort ascendente: se desplaza
                // mientras la clave deba ir ANTES (puntaje mayor) que el ya ubicado.
                if (vaAntes(clave, ranking[j])) {
                    ranking[j + 1] = ranking[j]; // se copia una referencia, no el objeto
                    metricas.desplazamientos++;
                    desplazamientosDeEstaClave++;
                    j--;
                } else {
                    // Corta apenas encuentra un jugador que debe quedar por delante de la
                    // clave: con un ranking casi ordenado esto pasa en la 1ra comparacion.
                    break;
                }
            }
            ranking[j + 1] = clave;

            if (metricas.mostrarTraza) {
                System.out.println("  Inserto " + clave + " (desplazamientos: "
                        + desplazamientosDeEstaClave + ") -> " + aTexto(ranking));
            }
        }
    }

    /**
     * Decide si {@code a} debe ubicarse antes que {@code b} en el ranking.
     *
     * <p>Criterio: primero el puntaje MAYOR; ante igualdad de puntaje, el nombre
     * alfabeticamente menor segun {@code compareTo}.</p>
     *
     * @param a primer jugador
     * @param b segundo jugador
     * @return {@code true} si {@code a} va estrictamente antes que {@code b}
     * @throws IllegalArgumentException si alguno de los jugadores es nulo
     */
    public static boolean vaAntes(Jugador a, Jugador b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("No se puede comparar un jugador nulo.");
        }
        if (a.getPuntaje() != b.getPuntaje()) {
            return a.getPuntaje() > b.getPuntaje(); // mayor a menor
        }
        // DESEMPATE: mismo puntaje -> nombre alfabetico ascendente.
        return a.getNombre().compareTo(b.getNombre()) < 0;
    }

    /**
     * Verifica, de forma independiente del algoritmo de ordenamiento, que el ranking cumpla
     * el criterio declarado: puntaje descendente y, ante empate, nombre ascendente.
     *
     * @param ranking arreglo de jugadores a verificar
     * @return {@code true} si cada jugador tiene puntaje mayor o igual al siguiente y los
     *         empates respetan el orden alfabetico
     * @throws IllegalArgumentException si {@code ranking} es nulo o contiene nulos
     */
    public static boolean estaOrdenado(Jugador[] ranking) {
        if (ranking == null) {
            throw new IllegalArgumentException("El arreglo de jugadores no puede ser nulo.");
        }
        for (int i = 1; i < ranking.length; i++) {
            if (ranking[i - 1] == null || ranking[i] == null) {
                throw new IllegalArgumentException("El arreglo no puede contener jugadores nulos.");
            }
            Jugador anterior = ranking[i - 1];
            Jugador actual = ranking[i];
            if (anterior.getPuntaje() < actual.getPuntaje()) {
                return false;
            }
            if (anterior.getPuntaje() == actual.getPuntaje()
                    && anterior.getNombre().compareTo(actual.getNombre()) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Devuelve una copia independiente del arreglo de jugadores (copia superficial: comparte
     * los objetos {@code Jugador}, que son inmutables).
     *
     * @param ranking arreglo origen
     * @return nuevo arreglo con las mismas referencias
     * @throws IllegalArgumentException si {@code ranking} es nulo
     */
    public static Jugador[] copiar(Jugador[] ranking) {
        if (ranking == null) {
            throw new IllegalArgumentException("El arreglo de jugadores no puede ser nulo.");
        }
        Jugador[] copia = new Jugador[ranking.length];
        for (int i = 0; i < ranking.length; i++) {
            copia[i] = ranking[i];
        }
        return copia;
    }

    /**
     * Representa el arreglo de jugadores en una sola linea.
     *
     * @param ranking arreglo a formatear
     * @return cadena con los jugadores separados por coma
     * @throws IllegalArgumentException si {@code ranking} es nulo
     */
    public static String aTexto(Jugador[] ranking) {
        if (ranking == null) {
            throw new IllegalArgumentException("El arreglo de jugadores no puede ser nulo.");
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < ranking.length; i++) {
            sb.append(ranking[i]);
            if (i < ranking.length - 1) {
                sb.append(", ");
            }
        }
        return sb.append("]").toString();
    }
}

/**
 * Jugador del ranking: nombre y puntaje. Es inmutable, por lo que compartir referencias
 * entre arreglos es seguro. No es publica: es soporte interno del ejercicio.
 */
class Jugador {

    private final String nombre;
    private final int puntaje;

    /**
     * Crea un jugador.
     *
     * @param nombre nombre no nulo ni vacio
     * @param puntaje puntaje obtenido (puede ser cero o negativo)
     * @throws IllegalArgumentException si {@code nombre} es nulo o esta en blanco
     */
    Jugador(String nombre, int puntaje) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del jugador no puede ser nulo ni vacio.");
        }
        this.nombre = nombre;
        this.puntaje = puntaje;
    }

    /**
     * Devuelve el nombre del jugador.
     *
     * @return nombre del jugador
     */
    String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el puntaje del jugador.
     *
     * @return puntaje del jugador
     */
    int getPuntaje() {
        return puntaje;
    }

    /**
     * Representacion textual del jugador.
     *
     * @return cadena con la forma {@code Nombre(puntaje)}
     */
    @Override
    public String toString() {
        return nombre + "(" + puntaje + ")";
    }
}

/**
 * Acumulador de comparaciones y desplazamientos del armado del ranking.
 * No es publica: es soporte interno del ejercicio.
 */
class MetricasRanking {
    long comparaciones;
    long desplazamientos;
    boolean mostrarTraza;
}

/*
 * ===========================================================================
 * MATRIZ DE CASOS DE PRUEBA MANUALES
 * ===========================================================================
 * Compilacion: javac Ejercicio10_RankingPuntajes.java -d /tmp/v3
 * Ejecucion:   java -cp /tmp/v3 Ejercicio10_RankingPuntajes
 *
 * ID: CP-10-01  (flujo nominal, incluye empates)
 *   Objetivo: armar el ranking de 6 jugadores con dos pares empatados y ver el desempate.
 *   Entrada exacta: Ana(1200), Pedro(900), Lucia(1500), Sofia(1200), Bruno(1100), Diego(900)
 *                   (ya cargados en el main).
 *   Resultado esperado: 1 Lucia 1500, 2 Ana 1200, 3 Sofia 1200, 4 Bruno 1100, 5 Diego 900,
 *                       6 Pedro 900; 9 comparaciones; 5 desplazamientos;
 *                       estaOrdenado()=true.
 *   Criterio de aprobacion: los empates quedan en orden alfabetico (Ana antes de Sofia,
 *                           Diego antes de Pedro), tal como declara el criterio, y
 *                           estaOrdenado() devuelve true.
 *
 * ID: CP-10-02  (mejor caso)
 *   Objetivo: comprobar el mejor caso O(n) con un ranking ya ordenado.
 *   Entrada exacta: linea "Ya ordenado (mejor caso)" con Lucia(1500), Ana(1200),
 *                   Bruno(1100), Diego(900).
 *   Resultado esperado: mismo orden de salida, comparaciones=3 (n-1), desplazamientos=0,
 *                       ordenado=true.
 *   Criterio de aprobacion: desplazamientos=0 y comparaciones=n-1=3, que es la situacion
 *                           habitual de un ranking que se actualiza.
 *
 * ID: CP-10-03  (peor caso)
 *   Objetivo: validar la cota n(n-1)/2 con el ranking cargado de menor a mayor.
 *   Entrada exacta: linea "Invertido (peor caso)" con Diego(900), Bruno(1100), Ana(1200),
 *                   Lucia(1500).
 *   Resultado esperado: salida [Lucia(1500), Ana(1200), Bruno(1100), Diego(900)];
 *                       comparaciones=6 y desplazamientos=6, iguales a 4*3/2 = 6; ordenado=true.
 *   Criterio de aprobacion: desplazamientos observados == n(n-1)/2 == 6.
 *
 * ID: CP-10-04  (entrada vacia)
 *   Objetivo: comprobar que un arreglo sin jugadores no rompe el programa.
 *   Entrada exacta: linea "Arreglo vacio" con new Jugador[] {}.
 *   Resultado esperado: "[] -> [] | comparaciones=0 desplazamientos=0 | ordenado=true".
 *   Criterio de aprobacion: no se lanza excepcion y estaOrdenado() devuelve true.
 *
 * ID: CP-10-05  (un elemento)
 *   Objetivo: comprobar el caso minimo no vacio.
 *   Entrada exacta: linea "Un solo jugador" con new Jugador[] {new Jugador("Ana", 1200)}.
 *   Resultado esperado: "[Ana(1200)] -> [Ana(1200)] | comparaciones=0 desplazamientos=0
 *                       | ordenado=true".
 *   Criterio de aprobacion: el ciclo externo no se ejecuta y el arreglo queda intacto.
 * ===========================================================================
 */
