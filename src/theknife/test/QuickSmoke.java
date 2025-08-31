package theknife.test;

import theknife.logica.DataContext;
import theknife.logica.UtenteService;
import theknife.logica.RistoranteService;
import theknife.logica.RecensioneService;
import theknife.ristorante.Ristorante;
import theknife.utente.Utente;

public class QuickSmoke {
    public static void main(String[] args) {
        DataContext data = new DataContext();
        data.loadAll("data/Utenti.csv", "data/Ristoranti.csv", "data/Recensioni.csv");

        System.out.println("Utenti: " + data.getUtenti().size());
        System.out.println("Ristoranti: " + data.getRistoranti().size());
        System.out.println("Recensioni: " + data.getRecensioni().size());

        // Prova findRistorante (metti un nome/location esistenti nel CSV)
        Ristorante r = data.findRistorante("La Cascata", "Faloppio, Italia");
        System.out.println("Trova 'La Cascata|Faloppio, Italia': " + (r != null));

        // Prova login veloce (metti credenziali coerenti con Utenti.csv)
        UtenteService us = new UtenteService(data);
        Utente u = us.login("usernameDiProva", "passwordDiProva");
        System.out.println("Login riuscito? " + (u != null));

        // Salva per sicurezza (non cambia nulla se non hai modificato dati)
        data.saveAll("data/Utenti.csv","data/Ristoranti.csv","data/Recensioni.csv");
        System.out.println("Smoke test OK.");
    }
}