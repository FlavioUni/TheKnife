/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.logica;

import java.util.ArrayList;
import java.util.List;

import theknife.ristorante.Ristorante;
import theknife.utente.Ruolo;
import theknife.utente.Utente;

public class UtenteService {
    private List<Utente> utenti;

    public UtenteService(List<Utente> utenti) {
        this.utenti = utenti;
    }

    public List<Utente> getUtenti() {
        return utenti;
    }

    public Utente trovaUtente(String username) {
        for (Utente u : utenti)
            if (u.getUsername().equalsIgnoreCase(username))
                return u;
        return null;
    }

    private boolean controlloPassword(String password) {
        return password != null && password.length() >= 6 && password.length() <= 12;
    }

    public boolean registrazione(Utente nuovo) {
        if (trovaUtente(nuovo.getUsername()) != null) {
            System.out.println("Username non disponibile.");
            return false;
        }
        if (!controlloPassword(nuovo.getPassword())) {
            System.out.println("La password deve contenere tra i 6 e i 12 caratteri.");
            return false;
        }
        Utente hashato = new Utente(
            nuovo.getNome(), nuovo.getCognome(), nuovo.getUsername(),
            Util.hashPassword(nuovo.getPassword()),
            nuovo.getDomicilio(), nuovo.getData(), nuovo.getRuolo()
        );
        utenti.add(hashato);
        System.out.println("Registrazione avvenuta con successo.");
        return true;
    }

    public Utente login(String username, String password) {
        Utente u = trovaUtente(username);
        if (u != null && u.getPassword().equals(Util.hashPassword(password))) {
            System.out.println("Login avvenuto con successo.");
            return u;
        }
        System.out.println("Credenziali errate.");
        return null;
    }

    public boolean aggiungiPreferito(String username, Ristorante ristorante) {
        Utente u = trovaUtente(username);
        if (u != null)
            return u.aggiungiPreferito(ristorante);
        return false;
    }

    public boolean rimuoviPreferito(String username, Ristorante ristorante) {
        Utente u = trovaUtente(username);
        if (u != null)
            return u.rimuoviPreferito(ristorante);
        return false;
    }

    public void visualizzaPreferiti(String username) {
        Utente u = trovaUtente(username);
        if (u != null)
            u.visualizzaPreferiti();
        else
            System.out.println("Utente non trovato nella sezione clienti.");
    }

    public boolean aggiungiRistoranteGestito(String username, Ristorante ristorante) {
        Utente u = trovaUtente(username);
        if (u != null && u.getRuolo() == Ruolo.RISTORATORE) {
            return u.aggiungiRistoranteGestito(ristorante);
        }
        return false;
    }

    public boolean rimuoviRistoranteGestito(String username, Ristorante ristorante) {
        Utente u = trovaUtente(username);
        if (u != null && u.getRuolo() == Ruolo.RISTORATORE) {
            return u.rimuoviRistoranteGestito(ristorante);
        }
        return false;
    }

    public void visualizzaRistorantiGestiti(String username) {
        Utente u = trovaUtente(username);
        if (u != null && u.getRuolo() == Ruolo.RISTORATORE) {
            u.visualizzaRistorantiGestiti();
        } else {
            System.out.println("Utente non trovato nella sezione ristoratori.");
        }
    }
}