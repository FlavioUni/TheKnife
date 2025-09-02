package theknife.logica;

import theknife.utente.Utente;
import theknife.utente.Ruolo;
import theknife.ristorante.Ristorante;

/**
 * La classe UtenteService fornisce i servizi principali per la gestione degli utenti.
 * Si occupa di operazioni critiche come registrazione, login (con hashing della password) e gestione delle liste di preferiti e ristoranti gestiti.
 * 
 * @author Lorenzo Gasparini
 * @see DataContext
 * @see Utente
 * @see Ruolo
 * @see Ristorante
 */

public class UtenteService {
    private final DataContext data;
    
    /**
     * Costruttore per UtenteService.
     * 
     * @param data Un'istanza di {@link DataContext} per accedere e modificare i dati degli utenti e dei ristoranti.
     */
    public UtenteService(DataContext data) { this.data = data; }

    /**
     * Cerca un utente nel sistema per username.
     * Metodo utilizzato principalmente da {@link theknife.menu.MenuHandler}.
     * 
     * @param username Lo username (identificativo unico) dell'utente da cercare.
     * @return L'oggetto {@link Utente} corrispondente, o {@code null} se non trovato.
     */
    public Utente trovaUtente(String username) { return data.findUtente(username); }
    /**
     * Cerca un utente nel sistema per username.
     * 
     * @param username Lo username (identificativo unico) dell'utente da cercare.
     * @return L'oggetto {@link Utente} corrispondente, o {@code null} se non trovato.
     */
    public Utente find(String username)        { return data.findUtente(username); }
    
    /**
     * Registra un nuovo utente nel sistema.
     * Effettua l'hashing della password in chiaro prima di registrare l'utente.
     * La verifica dell'unicità dello username è delegata a {@link DataContext#addUtente(Utente)}
     * 
     * @param nuovo L'oggetto {@link Utente} contenente i dati del nuovo utente.
     * @return {@code true} se la registrazione ha successo (username unico), {@code false} altrimenti.
     */
    public boolean registrazione(Utente nuovo) {
        if (nuovo == null || nuovo.getPassword() == null) return false;
        // Hash UNA sola volta prima di salvare
        nuovo.setPassword(Util.hashPassword(nuovo.getPassword()));
        return data.addUtente(nuovo);
    }
    
    /**
     * Autentica un utente nel sistema (login).
     * Supporta la migrazione da password in chiaro a password hashate.
     * * <ol>
     *   <li>Cerca l'utente per username.</li>
     *   <li>Se la password memorizzata è un hash SHA-256, confronta gli hash.</li>
     *   <li>Se la password memorizzata è in chiaro, confronta in chiaro e, in caso di successo, aggiorna la password con il suo hash.</li>
     *   </ol>
     *   
     * @param username Lo username dell'utente che tenta il login.
     * @param passwordPlain La password in chiaro fornita dall'utente.
     * @return L'oggetto {@link Utente} autenticato se le credenziali sono corrette, {@code null} altrimenti.
     */
    public Utente login(String username, String passwordPlain) {
        Utente u = data.findUtente(username);
        if (u == null) { System.out.println("Credenziali errate."); return null; }

        String salvata   = u.getPassword();
        String inputHash = Util.hashPassword(passwordPlain == null ? "" : passwordPlain);

        // Caso normale: sul CSV c'è già l'hash
        if (isSha256Hex(salvata)) {
            if (!salvata.equals(inputHash)) { System.out.println("Credenziali errate."); return null; }
            return u;
        }

        // Caso migrazione: sul CSV c'era in chiaro
        if (passwordPlain != null && passwordPlain.equals(salvata)) {
            u.setPassword(inputHash); // aggiorna in RAM; saveAll() lo scriverà su CSV
            return u;
        }

        System.out.println("Credenziali errate.");
        return null;
    }
    
    /**
     * Metodo di utilità privato per verificare se una stringa ha il formato di un hash SHA-256 esadecimale.
     * 
     * @param s La stringa da verificare.
     * @return {@code true} se la stringa è lunga 64 caratteri e contiene solo cifre esadecimali (0-9, a-f), {@code false} altrimenti.
     */
    private boolean isSha256Hex(String s) {
        return s != null && s.matches("^[0-9a-f]{64}$");
    }

    // ===== API unificate =====
    
    /**
     * Visualizza la lista dei ristoranti preferiti di un cliente.
     * Mostra l'elenco tramite {@link Utente#visualizzaAssoc()}.
     * 
     * @param username Lo username del cliente di cui visualizzare i preferiti.
     */
    public void visualizzaPreferiti(String username) {
        Utente u = data.findUtente(username);
        if (u == null) return;
        if (u.getRuolo() != Ruolo.CLIENTE) { System.out.println("Utente non nella sezione clienti."); return; }
        u.visualizzaAssoc(); // stampa i preferiti
    }
    
    /**
     * Visualizza la lista dei ristoranti gestiti da un ristoratore.
     * Mostra l'elenco tramite {@link Utente#visualizzaAssoc()}.
     * 
     * @param username Lo username del ristoratore di cui visualizzare i ristoranti gestiti.
     */
    public void visualizzaRistorantiGestiti(String username) {
        Utente u = data.findUtente(username);
        if (u == null) return;
        if (u.getRuolo() != Ruolo.RISTORATORE) { System.out.println("Utente non nella sezione ristoratori."); return; }
        u.visualizzaAssoc(); // stampa i gestiti
    }
    
    /**
     * Aggiunge un ristorante alla lista di quelli gestiti da un ristoratore.
     * L'operazione viene delegata al metodo {@link Utente#aggiungiAssoc(Ristorante)}.
     * 
     * @param username Lo username del ristoratore.
     * @param r Il ristorante da aggiungere alla lista dei gestiti.
     * @return {@code true} se l'aggiunta ha successo, {@code false} in caso di errore (utente non trovato, ruolo errato, ristorante già presente).
     */
    public boolean aggiungiRistoranteGestito(String username, Ristorante r) {
        Utente u = data.findUtente(username);
        if (u == null || r == null || u.getRuolo() != Ruolo.RISTORATORE) return false;

        // Blocco: nessun altro ristoratore deve già gestire questo ristorante
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
     * Rimuove un ristorante dalla lista di quelli gestiti da un ristoratore.
     * L'operazione viene delegata al metodo {@link Utente#rimuoviAssoc(Ristorante)}.
     * 
     * @param username Lo username del ristoratore.
     * @param r Il ristorante da rimuovere dalla lista dei gestiti.
     * @return {@code true} se la rimozione ha successo, {@code false} in caso di errore (utente non trovato, ruolo errato, ristorante non presente).
     */
    public boolean rimuoviRistoranteGestito(String username, Ristorante r) {
        Utente u = data.findUtente(username);
        return u != null && u.getRuolo() == Ruolo.RISTORATORE && u.rimuoviAssoc(r);
    }
}