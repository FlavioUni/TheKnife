package theknife.logica;

import theknife.recensione.Recensione;
import theknife.ristorante.Ristorante;

import java.util.ArrayList;
import java.util.List;

public class RistoranteService {
    
    private DataContext dataContext;
    
    // Costruttore
    public RistoranteService(DataContext dataContext) {
        this.dataContext = dataContext;
    }
    
    // Metodo per ottenere tutti i ristoranti
    public List<Ristorante> getAllRistoranti() {
        return dataContext.getRistoranti();
    }
    
    // Metodo per cercare ristoranti per nome
    public List<Ristorante> cercaRistorantiPerNome(String nome) {
        List<Ristorante> risultati = new ArrayList<>();
        List<Ristorante> tuttiRistoranti = dataContext.getRistoranti();
        
        for (Ristorante ristorante : tuttiRistoranti) {
            if (ristorante.getNome().toLowerCase().contains(nome.toLowerCase())) {
                risultati.add(ristorante);
            }
        }
        return risultati;
    }
    
    // Metodo per cercare ristoranti per tipo di cucina
    public List<Ristorante> cercaRistorantiPerCucina(String cucina) {
        List<Ristorante> risultati = new ArrayList<>();
        List<Ristorante> tuttiRistoranti = dataContext.getRistoranti();
        
        for (Ristorante ristorante : tuttiRistoranti) {
            if (ristorante.getCucina().toLowerCase().contains(cucina.toLowerCase())) {
                risultati.add(ristorante);
            }
        }
        return risultati;
    }
    
    // Metodo per cercare ristoranti per location
    public List<Ristorante> cercaRistorantiPerLocation(String location) {
        List<Ristorante> risultati = new ArrayList<>();
        List<Ristorante> tuttiRistoranti = dataContext.getRistoranti();
        
        for (Ristorante ristorante : tuttiRistoranti) {
            if (ristorante.getLocation().toLowerCase().contains(location.toLowerCase())) {
                risultati.add(ristorante);
            }
        }
        return risultati;
    }
    
    // Metodo per cercare ristoranti che offrono delivery
    public List<Ristorante> cercaRistorantiConDelivery() {
        List<Ristorante> risultati = new ArrayList<>();
        List<Ristorante> tuttiRistoranti = dataContext.getRistoranti();
        
        for (Ristorante ristorante : tuttiRistoranti) {
            if (ristorante.isDelivery()) {
                risultati.add(ristorante);
            }
        }
        return risultati;
    }
    
    // Metodo per cercare ristoranti con prenotazione online
    public List<Ristorante> cercaRistorantiConPrenotazioneOnline() {
        List<Ristorante> risultati = new ArrayList<>();
        List<Ristorante> tuttiRistoranti = dataContext.getRistoranti();
        
        for (Ristorante ristorante : tuttiRistoranti) {
            if (ristorante.isPrenotazioneOnline()) {
                risultati.add(ristorante);
            }
        }
        return risultati;
    }
    
    // Metodo per ottenere un ristorante specifico per nome esatto
    public Ristorante getRistoranteByNomeEsatto(String nome) {
        List<Ristorante> tuttiRistoranti = dataContext.getRistoranti();
        
        for (Ristorante ristorante : tuttiRistoranti) {
            if (ristorante.getNome().equalsIgnoreCase(nome)) {
                return ristorante;
            }
        }
        return null;
    }
    
    // Metodo per aggiungere un nuovo ristorante
    public boolean aggiungiRistorante(Ristorante ristorante) {
        try {
            dataContext.aggiungiRistorante(ristorante);
            return true;
        } catch (Exception e) {
            System.err.println("Errore durante l'aggiunta del ristorante: " + e.getMessage());
            return false;
        }
    }
    
    // Metodo per rimuovere un ristorante
    public boolean rimuoviRistorante(String nome) {
        try {
            Ristorante ristorante = getRistoranteByNomeEsatto(nome);
            if (ristorante != null) {
                dataContext.rimuoviRistorante(ristorante);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Errore durante la rimozione del ristorante: " + e.getMessage());
            return false;
        }
    }
    
    // Metodo per aggiornare un ristorante esistente
    public boolean aggiornaRistorante(Ristorante ristoranteAggiornato) {
        try {
            Ristorante ristoranteEsistente = getRistoranteByNomeEsatto(ristoranteAggiornato.getNome());
            if (ristoranteEsistente != null) {
                dataContext.aggiornaRistorante(ristoranteAggiornato);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Errore durante l'aggiornamento del ristorante: " + e.getMessage());
            return false;
        }
    }
    
    // Metodo per aggiungere una recensione a un ristorante
    public boolean aggiungiRecensione(String nomeRistorante, Recensione recensione) {
        try {
            Ristorante ristorante = getRistoranteByNomeEsatto(nomeRistorante);
            if (ristorante != null) {
                ristorante.aggiungiRecensione(recensione);
                dataContext.aggiornaRistorante(ristorante); // Aggiorna nel database
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Errore durante l'aggiunta della recensione: " + e.getMessage());
            return false;
        }
    }
    
    // Metodo per rimuovere una recensione da un ristorante
    public boolean rimuoviRecensione(String nomeRistorante, String username, String descrizione) {
        try {
            Ristorante ristorante = getRistoranteByNomeEsatto(nomeRistorante);
            if (ristorante != null) {
                ristorante.RimuoviRecensione(username, descrizione);
                dataContext.aggiornaRistorante(ristorante); // Aggiorna nel database
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Errore durante la rimozione della recensione: " + e.getMessage());
            return false;
        }
    }
    
    // Metodo per ottenere la media delle stelle di un ristorante
    public Double getMediaStelleRistorante(String nomeRistorante) {
        Ristorante ristorante = getRistoranteByNomeEsatto(nomeRistorante);
        if (ristorante != null && !ristorante.getRecensioni().isEmpty()) {
            return ristorante.MediaStelle();
        }
        return 0.0;
    }
    
    // Metodo per ottenere tutte le recensioni di un ristorante
    public List<Recensione> getRecensioniRistorante(String nomeRistorante) {
        Ristorante ristorante = getRistoranteByNomeEsatto(nomeRistorante);
        if (ristorante != null) {
            return ristorante.getRecensioni();
        }
        return new ArrayList<>();
    }
    
    // Metodo per visualizzare tutte le informazioni di un ristorante
    public void visualizzaDettagliRistorante(String nomeRistorante) {
        Ristorante ristorante = getRistoranteByNomeEsatto(nomeRistorante);
        if (ristorante != null) {
            System.out.println("=== DETTAGLI RISTORANTE ===");
            System.out.println(ristorante.toString());
            System.out.println("Media stelle: " + getMediaStelleRistorante(nomeRistorante));
            System.out.println("Numero recensioni: " + ristorante.getRecensioni().size());
            System.out.println("===========================");
        } else {
            System.out.println("Ristorante non trovato: " + nomeRistorante);
        }
    }
    
    // Metodo per filtrare ristoranti per range di prezzo
    public List<Ristorante> filtraRistorantiPerPrezzo(String prezzoMin, String prezzoMax) {
        List<Ristorante> risultati = new ArrayList<>();
        List<Ristorante> tuttiRistoranti = dataContext.getRistoranti();
        
        // Implementazione semplificata - assumendo che prezzo sia una stringa rappresentante un numero
        try {
            double min = Double.parseDouble(prezzoMin);
            double max = Double.parseDouble(prezzoMax);
            
            for (Ristorante ristorante : tuttiRistoranti) {
                try {
                    double prezzoRistorante = Double.parseDouble(ristorante.getPrezzo());
                    if (prezzoRistorante >= min && prezzoRistorante <= max) {
                        risultati.add(ristorante);
                    }
                } catch (NumberFormatException e) {
                    // Se il prezzo non è un numero valido, salta questo ristorante
                    continue;
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Formato prezzo non valido");
        }
        
        return risultati;
    }
}