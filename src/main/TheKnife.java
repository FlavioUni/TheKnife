/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package main;

import theknife.logica.MenuHandler;

/**
 * Classe principale di avvio dell'applicazione TheKnife.
 *
 * Questa classe contiene il metodo main che:
 * - disattiva i log predefiniti della libreria SLF4J utilizzata da JOpenCage
 * - inizializza e avvia il MenuHandler, che gestisce la logica di navigazione
 * - intercetta eventuali eccezioni non previste e le stampa a schermo
 *
 * @author Lorenzo Gasparini
 * @author Flavio Angelo Ciani
 * @author Gabriele Scolaro
 */
public class TheKnife {

    /**
     * Punto di ingresso dell'applicazione TheKnife.
     *
     * @param args Argomenti da linea di comando (non utilizzati)
     */
    public static void main(String[] args) {
        // Disattiva logging di librerie esterne (es. JOpenCage via SLF4J)
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "off");

        try {
            new MenuHandler().avvia();
        } catch (Exception e) {
            System.err.println("Errore fatale: " + e.getMessage());
            e.printStackTrace();
        }
    }
}