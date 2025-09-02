/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.logica;

import theknife.csv.GestoreRecensioni;
import theknife.csv.GestoreRistoranti;
import theknife.csv.GestoreUtenti;

import theknife.recensione.Recensione;
import theknife.ristorante.Ristorante;
import theknife.utente.Utente;
import theknife.utente.Ruolo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * La classe DataContext gestisce i dati in memoria dell’applicazione:utenti, ristoranti e recensioni, 
 * più gli indici di ricerca rapida.
 * Offre operazioni di caricamento/salvataggio su CSV e di collegamento tra le entità 
 * (recensioni per ristorante, associazioni utente e ristoranti).
 * 
 * I dati sono mantenuti (durante l'esecuzione) sia come liste che in mappe indicizzate
 * per velocizzare ricerche per chiave.
 * 
 * @author Gasparini Lorenzo
 * @author Ciani Flavio Angelo
 * @author Scolaro Gabriele
 */
public class DataContext
{
    // CAMPI
    private final GestoreUtenti gestoreUtenti = new GestoreUtenti();
    private final GestoreRistoranti gestoreRistoranti = new GestoreRistoranti();
    private final GestoreRecensioni gestoreRecensioni = new GestoreRecensioni();

    private List<Utente> utenti;
    private List<Ristorante> ristoranti;
    private List<Recensione> recensioni;

    private Map<String, Utente> utentiPerUsername;              // chiave: username in minuscolo
    private Map<String, Ristorante> ristorantePerId;            // chiave: IDRistorante
    private Map<String, List<Recensione>> recensioniPerRistoId; // chiave: IDRistorante
    
    // GETTER
    public List<Utente> getUtenti() {return utenti;}
    public List<Ristorante> getRistoranti() {return ristoranti;}
    public List<Recensione> getRecensioni() {return recensioni;}

    // METODI

    /**
     * Carica i dati di tutte le entità (Utente, Ristorantee e Recensione) dai file CSV, attraverso i metodi
     * di caricamento dei GestoriCSV e ricostruisce gli indici (HashMap) in memoria.
     * Collega inoltre le recensioni ai rispettivi ristoranti e associa i ristoranti agli utenti (lista preferiti o lista gestiti).
     * 
     * @param utentiCsv Percorso CSV utenti
     * @param ristorantiCsv Percorso CSV ristoranti
     * @param recensioniCsv Percorso CSV recensioni
     */
    public void loadAll(String utentiCsv, String ristorantiCsv, String recensioniCsv) {
        gestoreUtenti.caricaDaCSV(utentiCsv);
        gestoreRistoranti.caricaDaCSV(ristorantiCsv);
        gestoreRecensioni.caricaDaCSV(recensioniCsv);

        utenti = gestoreUtenti.getElementi();
        if (utenti == null) utenti = new ArrayList<>();

        ristoranti = gestoreRistoranti.getElementi();
        if (ristoranti == null) ristoranti = new ArrayList<>();

        recensioni = gestoreRecensioni.getElementi();
        if (recensioni == null) recensioni = new ArrayList<>();

        buildIndici();
        collegaRecensioniAiRistoranti();
        linkAssocUtenti();
    }

    /**
     * Ricostruisce le mappe di indicizzazione (utenti per username, ristoranti per IDRistorante,
     * recensioni raggruppate per IDRistorante).
     */
    private void buildIndici() {
        utentiPerUsername = new HashMap<>();
        ristorantePerId = new HashMap<>();
        recensioniPerRistoId = new HashMap<>();

        // utenti per username (minuscolo)
        for (Utente u : utenti) {
            if (u != null && u.getUsername() != null) {
                utentiPerUsername.put(u.getUsername().toLowerCase(), u);
            }
        }

        // ristoranti per IDRistorante (controllo duplicati e id mancanti)
        for (Ristorante r : ristoranti) {
            if (r == null) 
            	continue;
            String id = r.getId();
            if (id == null || id.isEmpty()) {
                System.err.println("Ristorante senza ID: " + r.getNome());
                continue;
            }
            if (ristorantePerId.containsKey(id)) {
                System.err.println("ID ristorante duplicato: " + id);
                continue;
            }
            ristorantePerId.put(id, r);
        }

        // Recensioni raggruppate per IDRistorante
        for (Recensione rec : recensioni) {
            if (rec == null) 
            	continue;
            String id = rec.getIdRistorante();
            if (id == null) 
            	continue;
            List<Recensione> lista = recensioniPerRistoId.get(id);
            if (lista == null) {
                lista = new ArrayList<>();
                recensioniPerRistoId.put(id, lista);
            }
            lista.add(rec);
        }
    }

    /**
      * Svuota le liste di recensioni di tutti i ristoranti e le ricostruisce
      * associando a ciascun ristorante le recensioni che lo riguardano (attraverso IDRistorante).
     */
    private void collegaRecensioniAiRistoranti() {
        for (Ristorante r : ristoranti) {
            if (r != null && r.getRecensioni() != null) {
                r.getRecensioni().clear();
            }
        }
        for (Recensione rec : recensioni) {
            if (rec == null) continue;
            Ristorante r = ristorantePerId.get(rec.getIdRistorante());
            if (r != null) {
                r.aggiungiRecensione(rec);
            }
        }
    }

    /**
     * Trova un utente per username (in minuscolo).
     * 
     * @param username Username da cercare
     * @return Utente trovato, oppure null se non esiste
     */
    public Utente findUtente(String username) {
        if (username == null) return null;
        return utentiPerUsername.get(username.toLowerCase());
    }

    /**
     * Trova un ristorante a partire dall'IDRistorante.
     * 
     * @param id Identificativo del ristorante
     * @return Ristorante corrispondente, oppure null se non esiste
     */
    public Ristorante findRistoranteById(String id) {
        return ristorantePerId.get(id);
    }

    /**
     * Aggiunge un nuovo utente al sistema e aggiorna l'indice interno.
     *
     * @param u Utente da aggiungere.
     * @return true se l'utente è stato aggiunto correttamente, false se è null o se lo username è già presente.
     */
    public boolean addUtente(Utente u) {
        if (u == null || u.getUsername() == null) 
        	return false;
        String key = u.getUsername().toLowerCase();
        if (utentiPerUsername.containsKey(key)) 
        	return false;
        
        utenti.add(u);
        utentiPerUsername.put(key, u);
        
        return true;
    }

    /**
     * Aggiunge un nuovo ristorante e aggiorna l’indice interno.
     *
     * @param r Ristorante da aggiungere.
     * @return true se aggiunto correttamente, false se null o con ID già presente.
     */
    public boolean addRistorante(Ristorante r) {
        if (r == null || r.getId() == null || r.getId().isEmpty()) 
        	return false;
        if (ristorantePerId.containsKey(r.getId())) {
            System.err.println("ID già presente!");
            return false;
        }
        
        ristoranti.add(r);
        ristorantePerId.put(r.getId(), r);
        
        return true;
    }

    /**
     * Aggiunge una nuova recensione al sistema.
     * Aggiorna la lista globale, la lista del ristorante e l'indice per ID ristorante.
     *
     * @param rec Recensione da aggiungere.
     * @return true se aggiunta con successo, false se la recensione è nulla o il ristorante non esiste.
     */
    public boolean addRecensione(Recensione rec) {
        if (rec == null || rec.getIdRistorante() == null) 
        	return false;
        Ristorante r = findRistoranteById(rec.getIdRistorante());
        if (r == null) 
        	return false;

        recensioni.add(rec);
        r.aggiungiRecensione(rec);

        List<Recensione> lista = recensioniPerRistoId.get(rec.getIdRistorante());
        if (lista == null) {
            lista = new ArrayList<>();
            recensioniPerRistoId.put(rec.getIdRistorante(), lista);
        }
        lista.add(rec);
        
        return true;
    }

    /**
     * Rimuove una recensione dal sistema.
     * Toglie la recensione da tutte le strutture: lista globale, lista del ristorante
     * e mappa delle recensioni per IDRistorante.
     * 
     * @param rec Recensione da rimuovere
     * @return true se la recensione è stata rimossa correttamente, false se i dati sono null o incoerenti
     */
    public boolean removeRecensione(Recensione rec) {
        if (rec == null || rec.getIdRistorante() == null) 
        	return false;

        recensioni.remove(rec);

        Ristorante r = ristorantePerId.get(rec.getIdRistorante());
        if (r != null && r.getRecensioni() != null) {
            r.getRecensioni().remove(rec);
        }

        List<Recensione> lista = recensioniPerRistoId.get(rec.getIdRistorante());
        if (lista != null) {
            lista.remove(rec);
            if (lista.isEmpty()) {
                recensioniPerRistoId.remove(rec.getIdRistorante());
            }
        }
        
        return true;
    }

    /**
     * Collega utenti e ristoranti partendo dalla stringa “raw” (IDRistorante) salvata su CSV.
     * Dopo il collegamento, azzera la stringa raw per evitare uso fuori contesto (inutile in esecuzione).
     */
    private void linkAssocUtenti() {
        for (Utente u : utenti) {
            if (u == null) 
            	continue;

            String raw = u.getAssocKeysRaw();
            if (raw == null) 
            	raw = "";
            raw = raw.trim();
            if (raw.isEmpty()) 
            	continue;

            String[] tokens = raw.split(";");
            for (String id : tokens) {
                if (id == null) 
                	continue;
                id = id.trim();
                if (id.isEmpty()) 
                	continue;

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

    /**
     * Crea una stringa con gli ID dei ristoranti associati all’utente, 
     * separati da punto e virgola, da usare per il salvataggio su CSV.
     * 
     * Se l’utente è CLIENTE, la stringa rappresenta i suoi ristoranti preferiti.  
     * Se è RISTORATORE, rappresenta i ristoranti che gestisce.
     *
     * @param u Utente di riferimento
     * @return Stringa con IDRistoranti separati da ';', nel formato {@code "id1;id2;id3;..."}
     */
    private String buildAssocKeysRaw(Utente u) {
        List<Ristorante> src;
        if (u.getRuolo() == Ruolo.CLIENTE) {
            src = u.getRistorantiPreferiti();
        } else {
            src = u.getRistorantiGestiti();
        }

        List<String> ids = new ArrayList<>();
        for (Ristorante r : src) {
            if (r != null && r.getId() != null && !r.getId().isEmpty()) {
                ids.add(r.getId());
            }
        }

        if (ids.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) sb.append(';');
            sb.append(ids.get(i));
        }
        return sb.toString();
    }

    /**
     * Salva tutte le entità (Utente, Ristorantee e Recensione) dai file CSV, attraverso i metodi
     * di caricamento dei GestoriCSV.
     * 
     * Per ogni utente costruisce la lista degli ID dei ristoranti preferiti o gestiti (a seconda del ruolo)
     * e la salva nel campo {@code assocKeysRaw}, che verrà poi scritto nel file CSV.
     * Questo passaggio è necessario perché {@code assocKeysRaw} non viene aggiornato automaticamente.
     * 
     * @param utentiCsv Percorso CSV utenti
     * @param ristorantiCsv Percorso CSV ristoranti
     * @param recensioniCsv Percorso CSV recensioni
     */
    public void saveAll(String utentiCsv, String ristorantiCsv, String recensioniCsv) {
        for (Utente u : utenti) {
            if (u != null) {
                u.setAssocKeysRaw(buildAssocKeysRaw(u));
            }
        }
        gestoreUtenti.salvaSuCSV(utentiCsv);
        gestoreRistoranti.salvaSuCSV(ristorantiCsv);
        gestoreRecensioni.salvaSuCSV(recensioniCsv);
    }
}