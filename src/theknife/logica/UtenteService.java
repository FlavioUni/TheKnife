/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.logica;

import theknife.utente.Utente;
import theknife.utente.Ruolo;
import theknife.ristorante.Ristorante;

/**
 * La classe UtenteService fornisce i servizi principali per la gestione degli utenti.
 * Permette la registrazione, il login (con hashing della password), e la gestione delle liste
 * di ristoranti preferiti o gestiti.
 * 
 * @author Gasparini Lorenzo
 * @author Ciani Flavio Angelo
 * @author Scolaro Gabriele
 */
public class UtenteService {
    
    // CAMPI
    private final DataContext data;
    
    /**
     * COSTRUTTORE parametrico della classe UtenteService.
     * 
     * @param data Oggetto che contiene tutti i dati dell'applicazione (utenti, ristoranti, recensioni)
     */
    public UtenteService(DataContext data) {this.data = data;}

    // METODI
    
    /**
     * Cerca un utente per username.
     * 
     * @param username Username dell'utente da cercare
     * @return Utente trovato oppure null se assente
     */
    public Utente trovaUtente(String username) {return data.findUtente(username);}
    
    /**
     * Registra un nuovo utente.
     * La password viene hashata prima del salvataggio (vedi Util).
     * L’unicità dello username è verificata dal DataContext.
     * 
     * @param nuovo Utente da registrare
     * @return true se la registrazione è avvenuta con successo, false altrimenti
     */
    public boolean registrazione(Utente nuovo) {
        if (nuovo == null || nuovo.getPassword() == null) 
        	return false;
        nuovo.setPassword(Util.hashPassword(nuovo.getPassword()));
        return data.addUtente(nuovo);
    }
    
    /**
     * Autentica un utente al login.
     * Supporta la migrazione da password in chiaro a password hashate, nel caso in cui sul CSV
     * ci siano password in chiaro.
     * 
     * @param username Username inserito
     * @param passwordPlain Password in chiaro inserita (es. "password")
     * @return Utente autenticato se le credenziali sono corrette, null altrimenti
     */
    public Utente login(String username, String passwordPlain) {
        Utente u = data.findUtente(username);
        if (u == null) { 
            System.out.println("Credenziali errate."); 
            return null; 
        }

        String salvata   = u.getPassword();
        String inputHash = Util.hashPassword(passwordPlain == null ? "" : passwordPlain);

        // hash salvato nel CSV
        if (isSha256Hex(salvata)) {
            if (!salvata.equals(inputHash)) { 
                System.out.println("Credenziali errate."); 
                return null; 
            }
            return u;
        }

        // password in chiaro nel CSV
        if (passwordPlain != null && passwordPlain.equals(salvata)) {
            u.setPassword(inputHash); 
            return u;
        }

        System.out.println("Credenziali errate.");
        return null;
    }
    
    /**
     * Controlla se la stringa è lunga 64 caratteri e contiene solo numeri da 0 a 9 e lettere da a a f.
     * Serve a capire se la password salvata è già un hash SHA-256.
     * 
     * @param s La stringa da controllare
     * @return true se è nel formato tipico di un hash SHA-256, false altrimenti
     */
    private boolean isSha256Hex(String s) {
        return s != null && s.matches("^[0-9a-f]{64}$");
    }

    /**
     * Stampa la lista dei ristoranti preferiti di un cliente.
     * 
     * @param username Username del cliente
     */
    public void visualizzaPreferiti(String username) {
        Utente u = data.findUtente(username);
        if (u == null) 
        	return;
        if (u.getRuolo() != Ruolo.CLIENTE) { 
            System.out.println("Utente non nella sezione clienti."); 
            return; 
        }
        u.visualizzaAssoc();
    }
    
    /**
     * Prova ad assegnare un ristorante a un ristoratore.
     * Se il ristorante è già gestito da un altro ristoratore, blocca l’operazione.
     * 
     * @param username Username del ristoratore a cui assegnare il ristorante
     * @param r Ristorante da aggiungere alla sua lista
     * @return true se aggiunto correttamente; false se l’utente non esiste, ha ruolo sbagliato,
     *         oppure se il ristorante è già gestito da qualcun altro
     */
    public boolean aggiungiRistoranteGestito(String username, Ristorante r) {
        Utente u = data.findUtente(username);
        if (u == null || r == null || u.getRuolo() != Ruolo.RISTORATORE) 
        	return false;

        for (Utente altro : data.getUtenti()) {
            if (!altro.getUsername().equals(username)
                    && altro.getRuolo() == Ruolo.RISTORATORE
                    && altro.gestisce(r)) {
                System.out.println("❌ Il ristorante è già gestito da: " + altro.getUsername());
                return false;
            }
        }
        return u.aggiungiAssoc(r);
    }
    
    /**
     * Rimuove un ristorante dalla lista dei gestiti di un ristoratore.
     * 
     * @param username Username del ristoratore
     * @param r Ristorante da rimuovere dalla gestione
     * @return true se rimosso, altrimenti false
     */
    public boolean rimuoviRistoranteGestito(String username, Ristorante r) {
        Utente u = data.findUtente(username);
        return u != null && u.getRuolo() == Ruolo.RISTORATORE && u.rimuoviAssoc(r);
    }
}