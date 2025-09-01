/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.csv;

import java.util.List;

/**
 * Classe astratta che definisce la gestione della persistenza di una lista di oggetti da e verso file in formato CSV.
 * <p>
 * Questa classe funge da base per tutti i gestori specifici.
 * Utilizza il generico {@code <T>} per essere riutilizzabile per qualsiasi tipo di entità del dominio.
 * </p>
 * <p>
 * I metodi {@link #caricaDaCSV(String)} e {@link #salvaSuCSV(String)} sono astratti e devono essere implementati dalle sottoclassi per definire
 * la logica specifica di parsing e serializzazione per il tipo {@code T}.
 * </p>
 * 
 * @param <T> Il tipo degli elementi gestiti da questo gestore (es. {@link theknife.utente.Utente}).
 * @author Flavio Angelo Ciani
 * @see GestoreUtenti
 * @see GestoreRistoranti
 */
public abstract class GestoreCSV<T> {
	/**
	 * La lista degli elementi di tipo {@code T} caricati dal CSV o da salvare su CSV.
	 * È protetta per essere direttamente accessibile alle sottoclassi.
	 */
	protected List<T> elementi;
	
	/**
	 * Restituisce la lista corrente degli elementi gestiti.
	 * 
	 * @return La lista {@code List<T>} degli elementi utilizzata dal gestore.
	 */
    public List<T> getElementi() {
        return elementi;
    }
    
    /**
     * Metodo astratto per caricare gli elementi da un file CSV.
     * Le sottoclassi devono implementare la logica per leggere il file, parsare ogni riga e costruire gli oggetti di tipo {@code T}
     * da aggiungere alla lista {@link #elementi}.
     * 
     * @param filePath Il percorso del file CSV da cui caricare i dati.
     */
    public abstract void caricaDaCSV(String filePath);
    
    /**
     * Metodo astratto per salvare gli elementi correnti in un file CSV.
     * Le sottoclassi devono implementare la logica per convertire ogni oggetto di tipo {@code T} nella lista {@link #elementi} in una riga di valori
     * nel formato CSV appropriato.
     * 
     * @param filePath Il percorso del file CSV su cui salvare i dati.
     */
    public abstract void salvaSuCSV(String filePath);
}
