package theknife.test;

import theknife.csv.GestoreRecensioni;
import theknife.recensione.Recensione;

import java.nio.file.Paths;
import java.util.Scanner;

public class TestGestoriCSV {
    public static void main(String[] args) {
        GestoreRecensioni gestore = new GestoreRecensioni();

        // Percorso relativo al file CSV (cross-platform)
        String filePath = Paths.get("data", "Recensioni.csv").toString();

        // Caricamento da file
        gestore.caricaDaCSV(filePath);
        System.out.println("Recensioni caricate dal file:");
        for (Recensione r : gestore.getElementi()) {
            System.out.println(r.visualizzaRecensione());
            System.out.println("------------------------");
        }

        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Vuoi aggiungere una nuova recensione? (s/n): ");
            String risposta = scanner.nextLine();

            if (risposta.equalsIgnoreCase("s")) {
                System.out.print("Username: ");
                String username = scanner.nextLine();

                System.out.print("Nome ristorante: ");
                String nomeRistorante = scanner.nextLine();

                int stelle = 0;
                while (true) {
                    System.out.print("Stelle (1-5): ");
                    try {
                        stelle = Integer.parseInt(scanner.nextLine());
                        if (stelle < 1 || stelle > 5) {
                            System.out.println("Inserisci un numero tra 1 e 5.");
                            continue;
                        }
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Inserisci un numero valido.");
                    }
                }

                System.out.print("Commento: ");
                String commento = scanner.nextLine();

                Recensione nuova = new Recensione(username, nomeRistorante, stelle, commento);
                gestore.getElementi().add(nuova);
                System.out.println("Recensione aggiunta.");
            }

            // Salvataggio su file
            gestore.salvaSuCSV(filePath);
            System.out.println("File aggiornato con successo.");

        } finally {
            scanner.close(); // Chiude lo scanner per evitare warning
        }
    }
}