/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.csv;

import java.util.List;

/**
 * Classe astratta che definisce la gestione della persistenza
 * di una lista di oggetti da e verso file in formato CSV.
 * 
 * @author Gasparini Lorenzo
 * @author Ciani Flavio Angelo
 * @author Scolaro Gabriele
 * 
 * @param <T> Tipo degli elementi gestiti (es. Utente, Ristorante, Recensione)
 * 
 */
public abstract class GestoreCSV<T> {
    
    /**
     * Lista degli elementi caricati dal CSV o da salvare su CSV.
     * Accessibile alle sottoclassi dello stesso package (theknife.csv).
     */
    protected List<T> elementi;
    
    // GETTER
    public List<T> getElementi() {return elementi;}
    
    /**
     * Carica gli elementi da un file CSV.
     * Ogni sottoclasse deve implementare la logica di parsing (lettura dati grezzi) specifica.
     * 
     * @param filePath Percorso del file CSV da cui leggere i dati
     */
    public abstract void caricaDaCSV(String filePath);
    
    /**
     * Salva gli elementi correnti in un file CSV.
     * Ogni sottoclasse deve implementare la logica di scrittura specifica.
     * 
     * @param filePath Percorso del file CSV su cui scrivere i dati
     */
    public abstract void salvaSuCSV(String filePath);
}