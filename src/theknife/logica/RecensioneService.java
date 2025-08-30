/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.logica;

import theknife.recensione.Recensione;
import theknife.ristorante.Ristorante;
import theknife.utente.Utente;

public class RecensioneService {
	
    private final DataContext dataContext;

    public RecensioneService(DataContext dataContext) {
        this.dataContext = dataContext;
    }
    
    
    //aggiunge una recensione a un ristorante da parte di un utente
    public Recensione aggiungiRecensione(Utente autore, Ristorante ristorante, int stelle, String testo) {
        // Validazioni
        if (autore == null) throw new IllegalArgumentException("Utente non valido");
        if (ristorante == null) throw new IllegalArgumentException("Ristorante non valido");
        if (stelle < 1 || stelle > 5) throw new IllegalArgumentException("Stelle devono essere 1–5");

        // Crea recensione usando i campi richiesti dal costruttore
        Recensione r = new Recensione(
                autore.getUsername(),
                ristorante.getNome(),
                ristorante.getLocation(), // se non hai il campo location in Ristorante, passa "" qui
                stelle,
                testo
        );

        // Aggiorna RAM
        ristorante.getRecensioni().add(r);
        dataContext.getGestoreRecensioni().getAll().add(r);

        return r;
    }

    
    //il ristoratore risponde a una recensione del proprio ristorante
    public void rispondiRecensione(Utente ristoratore, Ristorante ristorante, Recensione recensione, String risposta) {
        if (recensione == null) throw new IllegalArgumentException("Recensione nulla");
        if (ristoratore == null) throw new IllegalArgumentException("Ristoratore non valido");

        // check: il ristoratore deve gestire quel ristorante
        if (!ristoratore.gestisce(ristorante)) {
            throw new SecurityException("Non puoi rispondere a recensioni di ristoranti che non gestisci.");
        }

        recensione.setRisposta(risposta);
    }
    
    
    //l’autore modifica la propria recensione
    public void modificaRecensione(Utente autore, Recensione recensione, int nuoveStelle, String nuovoTesto) {
        if (recensione == null) throw new IllegalArgumentException("Recensione nulla");
        if (autore == null) throw new IllegalArgumentException("Utente non valido");

        // controllo: l’autore deve coincidere
        if (!recensione.getAutore().equalsIgnoreCase(autore.getUsername())) {
            throw new SecurityException("Puoi modificare solo le tue recensioni.");
        }

        recensione.modificaRecensione(nuoveStelle, nuovoTesto);
    }
    
    
    //l’autore o il ristoratore possono eliminare la recensione
    public void eliminaRecensione(Utente utente, Ristorante ristorante, Recensione recensione) {
        if (recensione == null) throw new IllegalArgumentException("Recensione nulla");
        if (utente == null) throw new IllegalArgumentException("Utente non valido");

        boolean isAutore = recensione.getAutore().equalsIgnoreCase(utente.getUsername());
        boolean isGestore = utente.gestisce(ristorante);

        if (!isAutore && !isGestore) {
            throw new SecurityException("Non puoi eliminare questa recensione.");
        }

        // rimuovi da RAM
        ristorante.getRecensioni().remove(recensione);
        dataContext.getGestoreRecensioni().getAll().remove(recensione);
    }


    
}
