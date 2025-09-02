package theknife.logica;

import theknife.csv.GestoreRecensioni;
import theknife.csv.GestoreRistoranti;
import theknife.csv.GestoreUtenti;

import theknife.recensione.Recensione;
import theknife.ristorante.Ristorante;
import theknife.utente.Utente;
import theknife.utente.Ruolo;

import java.util.*;

public class DataContext {

    private final GestoreUtenti gestoreUtenti = new GestoreUtenti();
    private final GestoreRistoranti gestoreRistoranti = new GestoreRistoranti();
    private final GestoreRecensioni gestoreRecensioni = new GestoreRecensioni();

    private List<Utente> utenti;
    private List<Ristorante> ristoranti;
    private List<Recensione> recensioni;

    private Map<String, Utente> utentiPerUsername;
    private Map<String, Ristorante> ristorantePerId;
    private Map<String, List<Recensione>> recensioniPerRistoId;

    public void loadAll(String utentiCsv, String ristorantiCsv, String recensioniCsv) {
        gestoreUtenti.caricaDaCSV(utentiCsv);
        gestoreRistoranti.caricaDaCSV(ristorantiCsv);
        gestoreRecensioni.caricaDaCSV(recensioniCsv);

        utenti = Optional.ofNullable(gestoreUtenti.getElementi()).orElse(new ArrayList<>());
        ristoranti = Optional.ofNullable(gestoreRistoranti.getElementi()).orElse(new ArrayList<>());
        recensioni = Optional.ofNullable(gestoreRecensioni.getElementi()).orElse(new ArrayList<>());

        buildIndici();
        recensioniRistorante();
        linkAssocUtenti();
    }

    private void buildIndici() {
        utentiPerUsername = new HashMap<>();
        ristorantePerId = new HashMap<>();
        recensioniPerRistoId = new HashMap<>();

        for (Utente u : utenti) {
            if (u.getUsername() != null) {
                utentiPerUsername.put(u.getUsername().toLowerCase(), u);
            }
        }

        for (Ristorante r : ristoranti) {
            if (r.getId() == null || r.getId().isEmpty()) {
                System.err.println("Ristorante senza ID: " + r.getNome());
                continue;
            }
            if (ristorantePerId.containsKey(r.getId())) {
                System.err.println("ID ristorante duplicato: " + r.getId());
                continue;
            }
            ristorantePerId.put(r.getId(), r);
        }

        for (Recensione rec : recensioni) {
            String id = rec.getIdRistorante();
            if (id == null) continue;
            recensioniPerRistoId.computeIfAbsent(id, k -> new ArrayList<>()).add(rec);
        }
    }

    private void recensioniRistorante() {
        for (Ristorante r : ristoranti) r.getRecensioni().clear();

        for (Recensione rec : recensioni) {
            String id = rec.getIdRistorante();
            Ristorante r = ristorantePerId.get(id);
            if (r != null) {
                r.aggiungiRecensione(rec);
            }
        }
    }

    public List<Utente> getUtenti() { return utenti; }
    public List<Ristorante> getRistoranti() { return ristoranti; }
    public List<Recensione> getRecensioni() { return recensioni; }

    public Utente findUtente(String username) {
        return username == null ? null : utentiPerUsername.get(username.toLowerCase());
    }

    public Ristorante findRistoranteById(String id) {
        return ristorantePerId.get(id);
    }

    public boolean addUtente(Utente u) {
        if (u == null || u.getUsername() == null) return false;
        String user = u.getUsername().toLowerCase();
        if (utentiPerUsername.containsKey(user)) return false;
        utenti.add(u);
        utentiPerUsername.put(user, u);
        return true;
    }

    public boolean addRistorante(Ristorante r) {
        if (ristorantePerId.containsKey(r.getId())) {
            System.err.println("ID già presente!");
            return false;
        }
        ristorantePerId.put(r.getId(), r);
        ristoranti.add(r);
        return true;
    }

    public boolean addRecensione(Recensione rec) {
        if (rec == null || rec.getIdRistorante() == null) return false;
        Ristorante r = findRistoranteById(rec.getIdRistorante());
        if (r == null) return false;
        recensioni.add(rec);
        r.aggiungiRecensione(rec);
        recensioniPerRistoId.computeIfAbsent(rec.getIdRistorante(), k -> new ArrayList<>()).add(rec);
        return true;
    }

    public boolean removeRecensione(Recensione rec) {
        if (rec == null || rec.getIdRistorante() == null) return false;
        recensioni.remove(rec);
        Ristorante r = ristorantePerId.get(rec.getIdRistorante());
        if (r != null) r.getRecensioni().remove(rec);
        List<Recensione> lista = recensioniPerRistoId.get(rec.getIdRistorante());
        if (lista != null) {
            lista.remove(rec);
            if (lista.isEmpty()) recensioniPerRistoId.remove(rec.getIdRistorante());
        }
        return true;
    }

    private void linkAssocUtenti() {
        for (Utente u : utenti) {
            String raw = u.getAssocKeysRaw();
            if (raw == null || raw.trim().isEmpty()) continue;

            String[] tokens = raw.split(";");
            for (String id : tokens) {
                id = id.trim();
                if (id.isEmpty()) continue;
                Ristorante r = findRistoranteById(id);
                if (r != null) {
                    u.aggiungiAssoc(r);
                } else {
                    System.err.println("ID ristorante non trovato: " + id);
                }
            }

            u.setAssocKeysRaw("");
        }
    }

    private String buildAssocKeysRaw(Utente u) {
        List<Ristorante> src = (u.getRuolo() == Ruolo.CLIENTE)
                ? u.getRistorantiPreferiti()
                : u.getRistorantiGestiti();

        List<String> ids = new ArrayList<>();
        for (Ristorante r : src) {
            if (r != null && r.getId() != null && !r.getId().isEmpty()) {
                ids.add(r.getId());
            }
        }
        return String.join(";", ids);
    }

    public void saveAll(String utentiCsv, String ristorantiCsv, String recensioniCsv) {
        for (Utente u : utenti) {
            u.setAssocKeysRaw(buildAssocKeysRaw(u));
        }
        gestoreUtenti.salvaSuCSV(utentiCsv);
        gestoreRistoranti.salvaSuCSV(ristorantiCsv);
        gestoreRecensioni.salvaSuCSV(recensioniCsv);
    }
}