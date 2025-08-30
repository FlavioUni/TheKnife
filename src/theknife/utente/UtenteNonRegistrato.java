/* Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.utente;

import java.util.List;
import theknife.ristorante.Ristorante;
import theknife.logica.UtenteService;
import theknife.recensione.Recensione;

public class UtenteNonRegistrato {
	// Visualizza lista di ristoranti
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
    // Visualizza recensioni di un ristorante
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
    // Registrazione (delegata a UtenteService)
    public Utente registra (UtenteService service, Utente nuovo) {
        if (service.registrazione(nuovo)) {
            return service.trovaUtente(nuovo.getUsername());
        }
        return null;
    }
}
