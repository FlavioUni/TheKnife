package theknife.logica;

import theknife.utente.Utente;
import theknife.utente.Ruolo;
import theknife.ristorante.Ristorante;

public class UtenteService {
    private final DataContext data;

    public UtenteService(DataContext data) { this.data = data; }

    // Alias usato da MenuHandler
    public Utente trovaUtente(String username) { return data.findUtente(username); }
    public Utente find(String username)        { return data.findUtente(username); }

    public boolean registrazione(Utente nuovo) {
        if (nuovo == null || nuovo.getPassword() == null) return false;
        // Hash UNA sola volta prima di salvare
        nuovo.setPassword(Util.hashPassword(nuovo.getPassword()));
        return data.addUtente(nuovo);
    }

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

    private boolean isSha256Hex(String s) {
        return s != null && s.matches("^[0-9a-f]{64}$");
    }

    // ===== API unificate =====
    public void visualizzaPreferiti(String username) {
        Utente u = data.findUtente(username);
        if (u == null) return;
        if (u.getRuolo() != Ruolo.CLIENTE) { System.out.println("Utente non nella sezione clienti."); return; }
        u.visualizzaAssoc(); // stampa i preferiti
    }

    public void visualizzaRistorantiGestiti(String username) {
        Utente u = data.findUtente(username);
        if (u == null) return;
        if (u.getRuolo() != Ruolo.RISTORATORE) { System.out.println("Utente non nella sezione ristoratori."); return; }
        u.visualizzaAssoc(); // stampa i gestiti
    }

    public boolean aggiungiRistoranteGestito(String username, Ristorante r) {
        Utente u = data.findUtente(username);
        return u != null && u.getRuolo() == Ruolo.RISTORATORE && u.aggiungiAssoc(r);
    }

    public boolean rimuoviRistoranteGestito(String username, Ristorante r) {
        Utente u = data.findUtente(username);
        return u != null && u.getRuolo() == Ruolo.RISTORATORE && u.rimuoviAssoc(r);
    }
}