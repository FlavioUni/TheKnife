/* Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.utente;

import java.util.List;
import theknife.ristorante.Ristorante;
import theknife.logica.UtenteService;
import theknife.recensione.Recensione;

/**
 * La classe UtenteNonRegistrato rappresenta un utente anonimo che visita l'applicazione.
 * <p>
 * Fornisce le funzionalità base consentite senza effettuare l'accesso:
 * <ul>
 *  <li>Visualizzare i dettagli dei ristoranti (luogo, fascia di prezzo, recensioni...).</li>
 *  <li>Registrarsi all'applicazione come cliente o ristoratore.</li>
 *  </ul>
 *  </p>
 *  
 * @author Lorenzo Gasparini
 * @see Utente
 * @see UtenteService
 */

public class UtenteNonRegistrato {
	/**
	 * Mostra l'elenco dei ristoranti disponibili nel sistema.
	 * Per ogni ristorante, visualizza il nome e la location.
	 * 
	 * @param ristoranti La lista di {@link Ristorante} da visualizzare.
	 */
    public void visualizzaRistoranti (List<Ristorante> ristoranti) {
        if (ristoranti.isEmpty()) {
            System.out.println("Nessun ristorante disponibile.");
            return;
        }
        System.out.println("Lista ristoranti disponibili:");
        for (Ristorante r : ristoranti) {
            System.out.println("- " + r.getNome() + " (" + r.getLocation() + ")");
        }
    }
    /**
     * Mostra tutte le recensioni associate ad un ristorante specifico.
     * Per ogni recensione, visualizza il voto in stelle e il contenuto.
     * 
     * @param ristorante Il {@link Ristorante} di cui visualizzare le recensioni.
     */
    public void visualizzaRecensioni (Ristorante ristorante) {
        List<Recensione> recensioni = ristorante.getRecensioni();
        if (recensioni.isEmpty()) {
            System.out.println("Nessuna recensione per questo ristorante.");
            return;
        }
        System.out.println("Recensioni per " + ristorante.getNome() + ":");
        for (Recensione rec : recensioni) {
            System.out.println("- " + rec.getStelle() + "/5: " + rec.getDescrizione());
        }
    }
    /**
     * Delega alla classe {@link UtenteService} il compito di registrare un nuovo utente.
     * 
     * @param service Il servizio di gestione utenti a cui delegare l'operazione.
     * @param nuovo L'oggetto {@link Utente} contenente i dati per la registrazione.
     * @return L'oggetto {@link Utente} registrato se l'operazione ha avuto successo; {@code null} se la registrazione fallisce.
     */
    public Utente registra (UtenteService service, Utente nuovo) {
        if (service.registrazione(nuovo)) {
            return service.trovaUtente(nuovo.getUsername());
        }
        return null;
    }
}
