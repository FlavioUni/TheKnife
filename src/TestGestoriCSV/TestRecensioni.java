package TestGestoriCSV;

import theknife.csv.GestoreRecensioni;
import theknife.recensione.Recensione;

import java.util.Scanner;

public class TestRecensioni {
    public static void main(String[] args) {
        GestoreRecensioni gestore = new GestoreRecensioni();

        // Percorso relativo al file CSV
        String filePath = "data/Recensioni.csv";

        // Carica da CSV
        gestore.caricaDaCSV(filePath);
        System.out.println("Recensioni caricate dal file:");
        for (Recensione r : gestore.getElementi()) {
            System.out.println(r.visualizzaRecensione());
            System.out.println("------------------------");
        }

        // Aggiunta manuale di una nuova recensione (esempio base)
        Scanner scanner = new Scanner(System.in);
        System.out.print("Vuoi aggiungere una nuova recensione? (s/n): ");
        String risposta = scanner.nextLine();

        if (risposta.equalsIgnoreCase("s")) {
            System.out.print("Username: ");
            String username = scanner.nextLine();

            System.out.print("Nome ristorante: ");
            String nomeRistorante = scanner.nextLine();

            System.out.print("Stelle (1-5): ");
            int stelle = Integer.parseInt(scanner.nextLine());

            System.out.print("Commento: ");
            String commento = scanner.nextLine();

            Recensione nuova = new Recensione(username, nomeRistorante, stelle, commento);
            gestore.getElementi().add(nuova);
            System.out.println("Recensione aggiunta.");
        }

        // Salvataggio finale
        gestore.salvaSuCSV(filePath);
        System.out.println("File aggiornato con successo.");
    }
}