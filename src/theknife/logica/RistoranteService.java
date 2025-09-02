/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.logica;

import theknife.ristorante.Ristorante;
import theknife.utente.Utente;
import theknife.utente.Ruolo;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe RistoranteService fornisce i servizi principali per la gestione dei ristoranti.
 * Gestisce le operazioni disponibili per ospiti, clienti e ristoratori.
 * 
 * @author Gasparini Lorenzo
 * @author Ciani Flavio Angelo
 * @author Scolaro Gabriele
 */
public class RistoranteService {

    // CAMPI
    private final DataContext dataContext;
    private final GeoService geoService;

    /**
     * COSTRUTTORE della classe RistoranteService.
     * 
     * @param dataContext Oggetto che contiene tutti i dati dell'applicazione (utenti, ristoranti, recensioni)
     * @param geoService Servizio per funzioni di geolocalizzazione
     */
    public RistoranteService(DataContext dataContext, GeoService geoService) {
        this.dataContext = dataContext;
        this.geoService = geoService;
    }
    
    // METODI

    // ===== OSPITE/CLIENTE =====

    /**
     * Cerca ristoranti attraverso filtri di ricerca.
     * 
     * @param nome Nome (o parte del nome) del ristorante, oppure null
     * @param location Città o area geografica, oppure null
     * @param fasciaPrezzo Prezzo medio indicativo (es. "minore di 20€", "tra 20€ e 50€", "maggiore di 50€"), oppure null
     * @param cucina Tipologia di cucina, oppure null
     * @param prenotazioneOnline true se si vuole solo chi permette prenotazioni online, false per escluderli, null per ignorare
     * @param delivery true se si vuole solo chi offre consegna a domicilio, false per escluderli, null per ignorare
     * @param minStelle Voto minimo medio richiesto, oppure null
     * @return Lista filtrata dei ristoranti
     */
    public List<Ristorante> cercaRistorantePerFiltri(String nome, String cucina, String location, String fasciaPrezzo,
                                                     Boolean delivery, Boolean prenotazioneOnline, Double minStelle) {
        List<Ristorante> risultati = new ArrayList<>();
        for (Ristorante r : dataContext.getRistoranti()) {
            if (nome != null && !r.getNome().toLowerCase().contains(nome.toLowerCase())) 
                continue;
            if (!matchesCriteri(r, cucina, location, fasciaPrezzo, delivery, prenotazioneOnline, minStelle)) 
                continue;
            risultati.add(r);
        }
        return risultati;
    }

    /**
     * Verifica se un ristorante rispetta i criteri richiesti dalla ricerca con filtro dell'utente.
     * 
     * @param r Ristorante da verificare
     * @param location Città o area geografica, oppure null
     * @param fasciaPrezzo Prezzo medio indicativo (es. "minore di 20€", "tra 20€ e 50€", "maggiore di 50€"), oppure null
     * @param cucina Tipologia di cucina, oppure null
     * @param prenotazioneOnline true se si vuole solo chi permette prenotazioni online, false per escluderli, null per ignorare
     * @param delivery true se si vuole solo chi offre consegna a domicilio, false per escluderli, null per ignorare
     * @param minStelle Voto minimo medio richiesto, oppure null
     * @return true se il ristorante rispetta i criteri, false altrimenti
     */
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

    /**
     * Confronta un prezzo medio con la fascia richiesta.
     * 
     * @param prezzoRistorante Prezzo medio del ristorante (stringa)
     * @param fascia Fascia richiesta (minore di 20€, tra 20€ e 50€, maggiore di 50€)
     * @return true se il prezzo rientra nella fascia, false altrimenti
     */
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

    // ===== CLIENTE =====

    /**
     * Aggiunge un ristorante ai preferiti del cliente.
     * 
     * @param cliente Utente con ruolo CLIENTE
     * @param r Ristorante da aggiungere
     * @return true se aggiunto, false altrimenti
     */
    public boolean aggiungiPreferito(Utente cliente, Ristorante r) {
        return cliente != null
                && cliente.getRuolo() == Ruolo.CLIENTE
                && cliente.aggiungiAssoc(r);
    }

    /**
     * Rimuove un ristorante dai preferiti del cliente.
     * 
     * @param cliente Utente con ruolo CLIENTE
     * @param r Ristorante da rimuovere
     * @return true se rimosso, false altrimenti
     */
    public boolean rimuoviPreferito(Utente cliente, Ristorante r) {
        return cliente != null
                && cliente.getRuolo() == Ruolo.CLIENTE
                && cliente.rimuoviAssoc(r);
    }

    // ===== RISTORATORE =====

    /**
     * Aggiunge un nuovo ristorante (solo per ristoratori).
     * In caso di successo, lo aggiunge anche alla lista gestita dal ristoratore.
     * 
     * @param ristoratore Utente con ruolo RISTORATORE
     * @param nuovo Nuovo ristorante da inserire
     * @return true se inserito, false altrimenti
     */
    public boolean aggiungiRistorante(Utente ristoratore, Ristorante nuovo) {
        if (ristoratore.getRuolo() != Ruolo.RISTORATORE) {
            System.err.println("Solo i ristoratori possono aggiungere ristoranti");
            return false;
        }
        boolean ok = dataContext.addRistorante(nuovo);
        if (ok) {
            ristoratore.aggiungiAssoc(nuovo);
        }
        return ok;
    }

    // ===== GEO / VICINO A ME =====

    /**
     * Restituisce i ristoranti entro una certa distanza da un indirizzo specificato.
     * La distanza viene calcolata in chilometri usando le coordinate geografiche.
     * 
     * @param indirizzo Indirizzo di partenza
     * @param distanzaKm Distanza massima in chilometri
     * @return Lista dei ristoranti entro il raggio specificato
     */
    public List<Ristorante> cercaVicinoA(String indirizzo, double distanzaKm) {
        return geoService.filtraPerVicinoA(indirizzo, distanzaKm, dataContext.getRistoranti());
    }

}