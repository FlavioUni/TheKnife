/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.utente;

/**
 * L'enum {@code Ruolo} definisce i possibili ruoli che un {@link Utente} può avere all'interno dell'applicazione.
 * Questo determina le operazioni che l'utente è autorizzato a compiere.
 * <p>
 * I valori possibili sono:
 * <ul>
 * 	<li>{@code CLIENTE}: Può visualizzare ristoranti, aggiungerli ai preferiti e lasciare recensioni.</li>
 *  <li>{@code RISTORATORE}: Può gestire uno o più ristoranti e rispondere alle recensioni.</li>
 * </ul>
 * </p>
 * 
 * @author Lorenzo Gasparini
 */

public enum Ruolo {
	CLIENTE, RISTORATORE;
}
