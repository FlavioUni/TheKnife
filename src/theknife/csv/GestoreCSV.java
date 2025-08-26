/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.csv;

import java.util.List;

public abstract class GestoreCSV<T> {
	protected List<T> elementi;

    public List<T> getElementi() {
        return elementi;
    }

    public abstract void caricaDaCSV(String filePath);

    public abstract void salvaSuCSV(String filePath);
}
