/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.logica;

import java.time.LocalDate;
import java.util.List;

import theknife.ristorante.Ristorante;
import theknife.utente.Ruolo;
import theknife.utente.Utente;

public class UtenteService {

    private final DataContext ctx;

    public UtenteService(DataContext ctx) {
        this.ctx = ctx;
    }

    public List<Utente> getUtenti() {
        return ctx.getUtenti();
    }

    public Utente trovaUtente(String username) {
        return ctx.findUtente(username);
    }

    private boolean controlloPassword(String password) {
        return password != null && password.length() >= 6 && password.length() <= 12;
    }

    public boolean registrazione(Utente nuovo) {
        if (nuovo == null) {
            System.out.println("Dati utente non validi.");
            return false;
        }
        if (trovaUtente(nuovo.getUsername()) != null) {
            System.out.println("Username non disponibile.");
            return false;
        }
        if (!controlloPassword(nuovo.getPassword())) {
            System.out.println("La password deve contenere tra i 6 e i 12 caratteri.");
            return false;
        }

        Utente hashato = new Utente(
                nuovo.getNome(),
                nuovo.getCognome(),
                nuovo.getUsername(),
                Util.hashPassword(nuovo.getPassword()),
                nuovo.getDomicilio(),
                nuovo.getData(),
                nuovo.getRuolo()
        );

        boolean ok = ctx.addUtente(hashato);  // aggiorna anche gli indici
        if (ok) System.out.println("Registrazione avvenuta con successo.");
        return ok;
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

    public Utente registraGuest(String nome, String cognome, String username, String password,
                                String domicilio, LocalDate data, Ruolo ruolo) {
        Utente nuovo = new Utente(nome, cognome, username, password, domicilio, data, ruolo);
        return registrazione(nuovo) ? trovaUtente(username) : null;
    }

    // --- Preferiti ---

    public boolean aggiungiPreferito(String username, Ristorante ristorante) {
        Utente u = trovaUtente(username);
        return (u != null) && u.aggiungiPreferito(ristorante);
    }

    public boolean rimuoviPreferito(String username, Ristorante ristorante) {
        Utente u = trovaUtente(username);
        return (u != null) && u.rimuoviPreferito(ristorante);
    }

    public void visualizzaPreferiti(String username) {
        Utente u = trovaUtente(username);
        if (u != null) u.visualizzaPreferiti();
        else System.out.println("Utente non trovato nella sezione clienti.");
    }

    // --- Ristoranti gestiti ---

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