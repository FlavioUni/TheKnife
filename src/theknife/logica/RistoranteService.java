/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.logica;

import theknife.database.DataContext;   //per dopo
import theknife.ristorante.Ristorante;
import theknife.recensione.Recensione;
import theknife.utente.Utente;

import java.util.ArrayList;
import java.util.List;

public class RistoranteService {
    
    private final DataContext dataContext;
    
    public RistoranteService(DataContext dataContext) {
        this.dataContext = dataContext;
    }
    
    // ===== METODI PER UTENTI NON REGISTRATI =====
    
    public List<Ristorante> cercaRistorante(String cucina, String location, String fasciaPrezzo, 
                                          Boolean delivery, Boolean prenotazioneOnline, Double minStelle) {
        List<Ristorante> risultati = new ArrayList<>();
        List<Ristorante> tuttiRistoranti = dataContext.getRistoranti();
        
        for (Ristorante ristorante : tuttiRistoranti) {
            if (matchesCriteri(ristorante, cucina, location, fasciaPrezzo, delivery, prenotazioneOnline, minStelle)) {
                risultati.add(ristorante);
            }
        }
        return risultati;
    }
    
    private boolean matchesCriteri(Ristorante ristorante, String cucina, String location, String fasciaPrezzo,
                                  Boolean delivery, Boolean prenotazioneOnline, Double minStelle) {
        
        // Filtro location (obbligatorio secondo specifiche)
        if (location != null && !ristorante.getLocation().toLowerCase().contains(location.toLowerCase())) {
            return false;
        }
        
        // Altri filtri
        if (cucina != null && !ristorante.getCucina().toLowerCase().contains(cucina.toLowerCase())) {
            return false;
        }
        
        if (fasciaPrezzo != null && !matchesFasciaPrezzo(ristorante.getPrezzo(), fasciaPrezzo)) {
            return false;
        }
        
        if (delivery != null && ristorante.isDelivery() != delivery) {
            return false;
        }
        
        if (prenotazioneOnline != null && ristorante.isPrenotazioneOnline() != prenotazioneOnline) {
            return false;
        }
        
        if (minStelle != null) {
            Double media = ristorante.mediaStelle();
            if (media.isNaN() || media < minStelle) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean matchesFasciaPrezzo(String prezzoRistorante, String fascia) {
        try {
            double prezzo = Double.parseDouble(prezzoRistorante.replace("€", "").trim());
            
            if (fascia.equals("minore di 30€")) return prezzo < 30;
            if (fascia.equals("tra 20€ e 50€")) return prezzo >= 20 && prezzo <= 50;
            if (fascia.equals("maggiore di 50€")) return prezzo > 50;
            
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public void visualizzaRistorante(Ristorante ristorante) {
        System.out.println("=== " + ristorante.getNome() + " ===");
        System.out.println("Indirizzo: " + ristorante.getIndirizzo());
        System.out.println("Location: " + ristorante.getLocation());
        System.out.println("Prezzo medio: " + ristorante.getPrezzo() + "€");
        System.out.println("Cucina: " + ristorante.getCucina());
        System.out.println("Telefono: " + ristorante.getNumeroTelefono());
        System.out.println("Sito web: " + ristorante.getWebsiteUrl());
        System.out.println("Delivery: " + (ristorante.isDelivery() ? "Sì" : "No"));
        System.out.println("Prenotazione online: " + (ristorante.isPrenotazioneOnline() ? "Sì" : "No"));
        
        Double mediaStelle = ristorante.mediaStelle();
        System.out.println("Valutazione media: " + (mediaStelle.isNaN() ? "Nessuna valutazione" : String.format("%.1f", mediaStelle) + " stelle"));
        System.out.println("Numero recensioni: " + ristorante.getRecensioni().size());
    }
    
    public void visualizzaRecensioni(Ristorante ristorante) {
        System.out.println("=== RECENSIONI - " + ristorante.getNome() + " ===");
        if (ristorante.getRecensioni().isEmpty()) {
            System.out.println("Nessuna recensione disponibile.");
        } else {
            for (Recensione recensione : ristorante.getRecensioni()) {
                System.out.println(recensione.visualizzaRecensione());
                System.out.println("---");
            }
        }
    }
    
    // ===== METODI PER CLIENTI REGISTRATI =====
    
    public boolean aggiungiPreferito(Utente cliente, Ristorante ristorante) {
        try {
            return cliente.aggiungiPreferito(ristorante); // ✅ METODO CORRETTO
        } catch (Exception e) {
            System.err.println("Errore nell'aggiunta ai preferiti: " + e.getMessage());
            return false;
        }
    }

    public boolean rimuoviPreferito(Utente cliente, Ristorante ristorante) {
        try {
            return cliente.rimuoviPreferito(ristorante); // ✅ METODO CORRETTO
        } catch (Exception e) {
            System.err.println("Errore nella rimozione dai preferiti: " + e.getMessage());
            return false;
        }
    }
    
    public void visualizzaPreferiti(Utente cliente) {
        List<Ristorante> preferiti = cliente.getRistorantiPreferiti();
        if (preferiti.isEmpty()) {
            System.out.println("Nessun ristorante nei preferiti.");
        } else {
            System.out.println("=== I TUOI RISTORANTI PREFERITI ===");
            for (int i = 0; i < preferiti.size(); i++) {
                Ristorante r = preferiti.get(i);
                Double media = r.mediaStelle();
                System.out.println((i + 1) + ". " + r.getNome() + 
                                 " - " + r.getLocation() +
                                 " (" + (media.isNaN() ? "Nessuna valutazione" : String.format("%.1f", media) + "★") + ")");
            }
        }
    }
    
    public boolean aggiungiRecensione(Utente cliente, Ristorante ristorante, int stelle, String descrizione) {
        try {
            // Verifica se l'utente ha già recensito questo ristorante
            if (ristorante.hasRecensioneFromUser(cliente.getUsername())) {
                System.err.println("Hai già recensito questo ristorante.");
                return false;
            }
            
            Recensione nuovaRecensione = new Recensione(cliente.getUsername(), ristorante.getNome(), 
                                                      ristorante.getLocation(), stelle, descrizione);
            ristorante.aggiungiRecensione(nuovaRecensione);
            dataContext.aggiornaRistorante(ristorante);
            return true;
        } catch (Exception e) {
            System.err.println("Errore nell'aggiunta della recensione: " + e.getMessage());
            return false;
        }
    }
    
    public boolean modificaRecensione(Utente cliente, Ristorante ristorante, int stelle, String nuovaDescrizione) {
        try {
            Recensione recensioneEsistente = ristorante.getRecensioneFromUser(cliente.getUsername());
            if (recensioneEsistente != null) {
                recensioneEsistente.modificaRecensione(stelle, nuovaDescrizione);
                dataContext.aggiornaRistorante(ristorante);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Errore nella modifica della recensione: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminaRecensione(Utente cliente, Ristorante ristorante) {
        try {
            Recensione recensione = ristorante.getRecensioneFromUser(cliente.getUsername());
            if (recensione != null) {
                ristorante.rimuoviRecensione(recensione);
                dataContext.aggiornaRistorante(ristorante);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Errore nell'eliminazione della recensione: " + e.getMessage());
            return false;
        }
    }
    
    // ===== METODI PER RISTORATORI REGISTRATI =====
    
    public boolean aggiungiRistorante(Utente ristoratore, Ristorante nuovoRistorante) {
        try {
            if (!ristoratore.getRuolo().equals("ristoratore")) {
                System.err.println("Solo i ristoratori possono aggiungere ristoranti");
                return false;
            }
            
            // Verifica che non esista già un ristorante con lo stesso nome
            if (getRistoranteByNome(nuovoRistorante.getNome()) != null) {
                System.err.println("Esiste già un ristorante con questo nome");
                return false;
            }
            
            nuovoRistorante.setProprietario(ristoratore.getUsername());
            dataContext.aggiungiRistorante(nuovoRistorante);
            return true;
        } catch (Exception e) {
            System.err.println("Errore nell'aggiunta del ristorante: " + e.getMessage());
            return false;
        }
    }
    
    public void visualizzaRiepilogo(Utente ristoratore) {
        List<Ristorante> ristorantiGestiti = getRistorantiByProprietario(ristoratore.getUsername());
        System.out.println("=== RIEPILOGO DEI TUOI RISTORANTI ===");
        
        for (Ristorante ristorante : ristorantiGestiti) {
            Double media = ristorante.MediaStelle();
            System.out.println("Ristorante: " + ristorante.getNome());
            System.out.println("Recensioni: " + ristorante.getRecensioni().size());
            System.out.println("Valutazione media: " + (media.isNaN() ? "Nessuna valutazione" : String.format("%.1f", media) + "★"));
            System.out.println("---");
        }
    }
    
    public void visualizzaRecensioniRistoratore(Utente ristoratore) {
        List<Ristorante> ristorantiGestiti = getRistorantiByProprietario(ristoratore.getUsername());
        System.out.println("=== RECENSIONI DEI TUOI RISTORANTI ===");
        
        for (Ristorante ristorante : ristorantiGestiti) {
            System.out.println("\n--- " + ristorante.getNome() + " ---");
            visualizzaRecensioni(ristorante);
        }
    }
    
    public boolean rispostaRecensione(Utente ristoratore, Ristorante ristorante, 
                                    String autoreRecensione, String risposta) {
        try {
            if (!ristorante.getProprietario().equals(ristoratore.getUsername())) {
                System.err.println("Non sei il proprietario di questo ristorante");
                return false;
            }
            
            Recensione recensione = ristorante.getRecensioneFromUser(autoreRecensione);
            if (recensione != null) {
                if (recensione.getRisposta() != null && !recensione.getRisposta().isEmpty()) {
                    System.err.println("Hai già risposto a questa recensione");
                    return false;
                }
                recensione.setRisposta(risposta);
                dataContext.aggiornaRistorante(ristorante);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Errore nell'aggiunta della risposta: " + e.getMessage());
            return false;
        }
    }
    
    // ===== METODI AUSILIARI =====
    
    public Ristorante getRistoranteByNome(String nome) {
        for (Ristorante ristorante : dataContext.getRistoranti()) {
            if (ristorante.getNome().equalsIgnoreCase(nome)) {
                return ristorante;
            }
        }
        return null;
    }
    
    public List<Ristorante> getRistorantiByProprietario(String usernameProprietario) {
        List<Ristorante> risultati = new ArrayList<>();
        for (Ristorante ristorante : dataContext.getRistoranti()) {
            if (ristorante.getProprietario() != null && 
                ristorante.getProprietario().equals(usernameProprietario)) {
                risultati.add(ristorante);
            }
        }
        return risultati;
    }
    
    public List<Ristorante> getRistorantiVicini(String location) {
        return cercaRistorante(null, location, null, null, null, null);
    }
    
    public boolean existsRistorante(String nomeRistorante) {
        return getRistoranteByNome(nomeRistorante) != null;
    }
}