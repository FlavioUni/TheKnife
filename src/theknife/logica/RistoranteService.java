/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.logica;

import theknife.recensione.Recensione;
import theknife.utente.Utente;
import theknife.ristorante.Ristorante;

import java.util.ArrayList;
import java.util.List;

public class RistoranteService {
    
    private DataContext dataContext;
    
    public RistoranteService(DataContext dataContext) {
        this.dataContext = dataContext;
    }
    
    // ===== METODI PER UTENTI NON REGISTRATI =====
    
    public List<Ristorante> cercaRistorante(String cucina, String location, String fasciaPrezzo, 
                                          Boolean delivery, Boolean prenotazioneOnline, Double minStelle) {
        List<Ristorante> risultati = new ArrayList<>();
        List<Ristorante> tuttiRistoranti = dataContext.getRistoranti();
        
        for (Ristorante ristorante : tuttiRistoranti) {
            boolean matches = true;
            
            // Filtro per location (obbligatorio)
            if (location != null && !ristorante.getLocation().toLowerCase().contains(location.toLowerCase())) {
                matches = false;
            }
            
            // Filtro per cucina
            if (cucina != null && !ristorante.getCucina().toLowerCase().contains(cucina.toLowerCase())) {
                matches = false;
            }
            
            // Filtro per fascia prezzo
            if (fasciaPrezzo != null && !matchesFasciaPrezzo(ristorante.getPrezzo(), fasciaPrezzo)) {
                matches = false;
            }
            
            // Filtro per delivery
            if (delivery != null && ristorante.isDelivery() != delivery) {
                matches = false;
            }
            
            // Filtro per prenotazione online
            if (prenotazioneOnline != null && ristorante.isPrenotazioneOnline() != prenotazioneOnline) {
                matches = false;
            }
            
            // Filtro per stelle minime
            if (minStelle != null && ristorante.MediaStelle() < minStelle) {
                matches = false;
            }
            
            if (matches) {
                risultati.add(ristorante);
            }
        }
        return risultati;
    }
    
    private boolean matchesFasciaPrezzo(String prezzoRistorante, String fascia) {
        try {
            double prezzo = Double.parseDouble(prezzoRistorante);
            
            if (fascia.equals("minore di 30€")) return prezzo < 30;
            if (fascia.equals("tra 20€ e 50€")) return prezzo >= 20 && prezzo <= 50;
            if (fascia.equals("maggiore di 50€")) return prezzo > 50;
            
            // Gestione di altre fasce se necessario
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
        System.out.println("Delivery: " + (ristorante.isDelivery() ? "Sì" : "No"));
        System.out.println("Prenotazione online: " + (ristorante.isPrenotazioneOnline() ? "Sì" : "No"));
        System.out.println("Valutazione media: " + String.format("%.1f", ristorante.MediaStelle()) + " stelle");
        System.out.println("Numero recensioni: " + ristorante.getRecensioni().size());
    }
    
    public void visualizzaRecensioni(Ristorante ristorante) {
        System.out.println("=== RECENSIONI - " + ristorante.getNome() + " ===");
        if (ristorante.getRecensioni().isEmpty()) {
            System.out.println("Nessuna recensione disponibile.");
        } else {
            for (Recensione recensione : ristorante.getRecensioni()) {
                System.out.println(recensione.visualizzaRecensione());
                if (recensione.getRisposta() != null && !recensione.getRisposta().isEmpty()) {
                    System.out.println("Risposta del ristorante: " + recensione.getRisposta());
                }
                System.out.println("---");
            }
        }
    }
    
    // ===== METODI PER CLIENTI REGISTRATI =====
    
    public boolean aggiungiPreferito(Utente cliente, Ristorante ristorante) {
        try {
            return cliente.aggiungiRistorantePreferito(ristorante);
        } catch (Exception e) {
            System.err.println("Errore nell'aggiunta ai preferiti: " + e.getMessage());
            return false;
        }
    }
    
    public boolean rimuoviPreferito(Utente cliente, Ristorante ristorante) {
        try {
            return cliente.rimuoviRistorantePreferito(ristorante);
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
                System.out.println((i + 1) + ". " + preferiti.get(i).getNome() + 
                                 " - " + preferiti.get(i).getLocation() +
                                 " (" + preferiti.get(i).MediaStelle() + "★)");
            }
        }
    }
    
    public boolean aggiungiRecensione(Utente cliente, Ristorante ristorante, int stelle, String descrizione) {
        try {
            Recensione nuovaRecensione = new Recensione(cliente.getUsername(), stelle, descrizione);
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
            for (Recensione recensione : ristorante.getRecensioni()) {
                if (recensione.getAutore().equals(cliente.getUsername())) {
                    recensione.setStelle(stelle);
                    recensione.setDescrizione(nuovaDescrizione);
                    dataContext.aggiornaRistorante(ristorante);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Errore nella modifica della recensione: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminaRecensione(Utente cliente, Ristorante ristorante) {
        try {
            List<Recensione> recensioni = ristorante.getRecensioni();
            for (int i = 0; i < recensioni.size(); i++) {
                if (recensioni.get(i).getAutore().equals(cliente.getUsername())) {
                    recensioni.remove(i);
                    dataContext.aggiornaRistorante(ristorante);
                    return true;
                }
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
            
            nuovoRistorante.setProprietario(ristoratore.getUsername());
            dataContext.aggiungiRistorante(nuovoRistorante);
            ristoratore.aggiungiRistoranteGestito(nuovoRistorante);
            return true;
        } catch (Exception e) {
            System.err.println("Errore nell'aggiunta del ristorante: " + e.getMessage());
            return false;
        }
    }
    
    public void visualizzaRiepilogo(Utente ristoratore) {
        List<Ristorante> ristorantiGestiti = ristoratore.getRistorantiGestiti();
        System.out.println("=== RIEPILOGO DEI TUOI RISTORANTI ===");
        
        for (Ristorante ristorante : ristorantiGestiti) {
            System.out.println("Ristorante: " + ristorante.getNome());
            System.out.println("Recensioni: " + ristorante.getRecensioni().size());
            System.out.println("Valutazione media: " + String.format("%.1f", ristorante.MediaStelle()) + "★");
            System.out.println("---");
        }
    }
    
    public void visualizzaRecensioniRistoratore(Utente ristoratore) {
        List<Ristorante> ristorantiGestiti = ristoratore.getRistorantiGestiti();
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
            
            for (Recensione recensione : ristorante.getRecensioni()) {
                if (recensione.getAutore().equals(autoreRecensione)) {
                    if (recensione.getRisposta() != null && !recensione.getRisposta().isEmpty()) {
                        System.err.println("Hai già risposto a questa recensione");
                        return false;
                    }
                    recensione.setRisposta(risposta);
                    dataContext.aggiornaRistorante(ristorante);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Errore nell'aggiunta della risposta: " + e.getMessage());
            return false;
        }
    }
    

}