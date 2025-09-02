/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.logica;

import theknife.ristorante.Ristorante;
import theknife.recensione.Recensione;
import theknife.utente.Utente;
import theknife.utente.Ruolo;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe RistoranteService fornisce i servizi principali per la gestione dei ristoranti,
 * ovvero si occupa di tutte le operazioni che li riguardano. Definisce cosa può fare un utente 
 * non registrato, un cliente, o un ristoratore con i ristoranti
 */

public class RistoranteService {

    private final DataContext dataContext;
    private final GeoService geoService; 

    /**
     * Costruttore parametrico della classe RistoranteService
     * @param dataContext L'oggetto dataContext che prende in ingresso per accedere a tutti i dati di quella classe
     * @param geoServiceL'oggetto geoService per gestire tutte le operazioni legate alla geocalizzazione dei ristoranti
     */
    
    public RistoranteService(DataContext dataContext, GeoService geoService) {
        this.dataContext = dataContext;
        this.geoService = geoService;
    }

    // ===== UTENTE NON REGISTRATO =====
    
    /**
     * Avviene la ricerca del ristorante in base a una serie di criteri di filtro che l’utente può specificare (cucina, location, fascia di prezzo, ecc.)
     * @param cucina
     * @param location
     * @param fasciaPrezzo
     * @param delivery
     * @param prenotazioneOnline
     * @param minStelle
     * @return
     */

    public List<Ristorante> cercaRistorante(String cucina, String location, String fasciaPrezzo,
                                            Boolean delivery, Boolean prenotazioneOnline, Double minStelle) {
        List<Ristorante> risultati = new ArrayList<>();
        for (Ristorante r : dataContext.getRistoranti()) {
            if (matchesCriteri(r, cucina, location, fasciaPrezzo, delivery, prenotazioneOnline, minStelle)) {
                risultati.add(r);
            }
        }
        return risultati;
    }

    private boolean matchesCriteri(Ristorante r, String cucina, String location, String fasciaPrezzo,
                                   Boolean delivery, Boolean prenotazioneOnline, Double minStelle) {

        if (location != null && !r.getLocation().toLowerCase().contains(location.toLowerCase())) return false;
        if (cucina != null && !r.getCucina().toLowerCase().contains(cucina.toLowerCase())) return false;
        if (fasciaPrezzo != null && !matchesFasciaPrezzo(r.getPrezzoMedio(), fasciaPrezzo)) return false;
        if (delivery != null && r.isDelivery() != delivery) return false;
        if (prenotazioneOnline != null && r.isPrenotazioneOnline() != prenotazioneOnline) return false;

        if (minStelle != null) {
            Double media = r.mediaStelle();
            if (media.isNaN() || media < minStelle) return false;
        }
        return true;
    }

    private boolean matchesFasciaPrezzo(String prezzoRistorante, String fascia) {
        try {
            double prezzo = Double.parseDouble(prezzoRistorante.replace("€", "").trim());
            if ("minore di 20€".equals(fascia)) return prezzo < 20;
            if ("tra 20€ e 50€".equals(fascia)) return prezzo >= 20 && prezzo <= 50;
            if ("maggiore di 50€".equals(fascia)) return prezzo > 50;
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void visualizzaRistorante(Ristorante r) {
        System.out.println("=== " + r.getNome() + " ===");
        System.out.println("Indirizzo: " + r.getIndirizzo());
        System.out.println("Location: " + r.getLocation());
        System.out.println("Prezzo medio: " + r.getPrezzoMedio() + "€");
        System.out.println("Cucina: " + r.getCucina());
        System.out.println("Telefono: " + r.getNumeroTelefono());
        System.out.println("Sito web: " + r.getWebsiteUrl());
        System.out.println("Delivery: " + (r.isDelivery() ? "Sì" : "No"));
        System.out.println("Prenotazione online: " + (r.isPrenotazioneOnline() ? "Sì" : "No"));
        Double mediaStelle = r.mediaStelle();
        System.out.println("Valutazione media: " + (mediaStelle.isNaN() ? "Nessuna valutazione" : String.format("%.1f", mediaStelle) + " stelle"));
        System.out.println("Numero recensioni: " + r.getRecensioni().size());
    }

    public void visualizzaRecensioni(Ristorante r) {
        System.out.println("=== RECENSIONI - " + r.getNome() + " ===");
        if (r.getRecensioni().isEmpty()) {
            System.out.println("Nessuna recensione disponibile.");
        } else {
            for (Recensione rec : r.getRecensioni()) {
                System.out.println(rec.visualizzaRecensione());
                System.out.println("---");
            }
        }
    }

    // ===== CLIENTE REGISTRATO =====

    public boolean aggiungiPreferito(Utente cliente, Ristorante r) {
        return cliente != null
            && cliente.getRuolo() == Ruolo.CLIENTE
            && cliente.aggiungiAssoc(r);
    }

    public boolean rimuoviPreferito(Utente cliente, Ristorante r) {
        return cliente != null
            && cliente.getRuolo() == Ruolo.CLIENTE
            && cliente.rimuoviAssoc(r);
    }

    public void visualizzaPreferiti(Utente cliente) {
        if (cliente != null && cliente.getRuolo() == Ruolo.CLIENTE) {
            cliente.visualizzaAssoc();
        } else {
            System.out.println("Nessun ristorante nei preferiti.");
        }
    }

    public boolean aggiungiRecensione(Utente cliente, Ristorante ristorante, int stelle, String descrizione) {
        try {
            if (ristorante.esisteRecensioneDiUtente(cliente.getUsername())) {
                System.err.println("Hai già recensito questo ristorante.");
                return false;
            }
            Recensione rec = new Recensione(
                    cliente.getUsername(),
                    ristorante.getNome(),
                    ristorante.getLocation(),
                    stelle,
                    descrizione
            );
            return dataContext.addRecensione(rec);
        } catch (Exception e) {
            System.err.println("Errore nell'aggiunta della recensione: " + e.getMessage());
            return false;
        }
    }

    public boolean modificaRecensione(Utente cliente, Ristorante ristorante, int stelle, String nuovaDescrizione) {
        try {
            Recensione rec = ristorante.trovaRecensioneDiUtente(cliente.getUsername());
            if (rec == null) return false;
            rec.modificaRecensione(stelle, nuovaDescrizione);
            return true;
        } catch (Exception e) {
            System.err.println("Errore nella modifica della recensione: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminaRecensione(Utente cliente, Ristorante ristorante) {
        try {
            Recensione rec = ristorante.trovaRecensioneDiUtente(cliente.getUsername());
            if (rec == null) return false;
            return dataContext.removeRecensione(rec);
        } catch (Exception e) {
            System.err.println("Errore nell'eliminazione della recensione: " + e.getMessage());
            return false;
        }
    }

    // ===== RISTORATORE =====

    public boolean aggiungiRistorante(Utente ristoratore, Ristorante nuovo) {
        if (ristoratore.getRuolo() != Ruolo.RISTORATORE) {
            System.err.println("Solo i ristoratori possono aggiungere ristoranti");
            return false;
        }
        if (dataContext.findRistorante(nuovo.getNome(), nuovo.getLocation()) != null) {
            System.err.println("Esiste già un ristorante con questo nome e location");
            return false;
        }
        nuovo.setProprietario(ristoratore.getUsername());
        boolean ok = dataContext.addRistorante(nuovo);
        if (ok) ristoratore.aggiungiAssoc(nuovo);  // usa l’API unificata
        return ok;
    }

    public void visualizzaRiepilogo(Utente ristoratore) {
        List<Ristorante> gestiti = getRistorantiByProprietario(ristoratore.getUsername());
        System.out.println("=== RIEPILOGO DEI TUOI RISTORANTI ===");
        for (Ristorante r : gestiti) {
            Double media = r.mediaStelle();
            System.out.println("Ristorante: " + r.getNome());
            System.out.println("Recensioni: " + r.getRecensioni().size());
            System.out.println("Valutazione media: " + (media.isNaN() ? "Nessuna valutazione" : String.format("%.1f", media) + "★"));
            System.out.println("---");
        }
    }

    public void visualizzaRecensioniRistoratore(Utente ristoratore) {
        List<Ristorante> gestiti = getRistorantiByProprietario(ristoratore.getUsername());
        System.out.println("=== RECENSIONI DEI TUOI RISTORANTI ===");
        for (Ristorante r : gestiti) {
            System.out.println("\n--- " + r.getNome() + " ---");
            visualizzaRecensioni(r);
        }
    }

    public boolean rispostaRecensione(Utente ristoratore, Ristorante ristorante,
                                      String autoreRecensione, String risposta) {
        try {
            if (ristorante.getProprietario() == null ||
                !ristorante.getProprietario().equals(ristoratore.getUsername())) {
                System.err.println("Non sei il proprietario di questo ristorante");
                return false;
            }
            Recensione rec = ristorante.trovaRecensioneDiUtente(autoreRecensione);
            if (rec == null) return false;
            if (rec.getRisposta() != null && !rec.getRisposta().isEmpty()) {
                System.err.println("Hai già risposto a questa recensione");
                return false;
            }
            rec.setRisposta(risposta);
            return true;
        } catch (Exception e) {
            System.err.println("Errore nell'aggiunta della risposta: " + e.getMessage());
            return false;
        }
    }

    // ===== GEO / VICINO A ME =====

    /** Ricerca ristoranti entro un raggio (km) da un indirizzo usando GeoService. */
    public List<Ristorante> cercaVicinoA(String indirizzo, double distanzaKm) {
        return geoService.filtraPerVicinoA(indirizzo, distanzaKm, dataContext.getRistoranti());
    }

    // ===== AUSILIARI =====

    public List<Ristorante> getRistorantiByProprietario(String usernameProprietario) {
        List<Ristorante> risultati = new ArrayList<>();
        for (Ristorante r : dataContext.getRistoranti()) {
            if (r.getProprietario() != null && r.getProprietario().equals(usernameProprietario)) {
                risultati.add(r);
            }
        }
        return risultati;
    }

    public boolean existsRistorante(String nome, String location) {
        return dataContext.findRistorante(nome, location) != null;
    }
}