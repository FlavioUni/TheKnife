package theknife.ristorante;

public class Ristorante {

	private String nome;
	private String indirizzo;
	private String citta;
	private String nazione;
	private double latitudine;
	private double longitudine;
	private double fasciaPrezzo;
	private boolean delivery;
	private boolean prenotazioneOnline;
	private String tipoCucina;
	private String numeroTelefono;
	private String websiteUrl;
	private int premi;
	private int stelle;
	private String descrizione;
	
	public Ristorante(String nome, String indirizzo, String citta, String nazione, double latitudine,
					  double longitudine, double fasciaPrezzo, boolean delivery, boolean prenotazioneOnline,
					  String tipoCucina, String numeroTelefono, String websiteUrl, int premi, int stelle,
					  String descrizione){
		
		this.nome = nome;
        this.indirizzo = indirizzo;
        this.citta = citta;
        this.nazione = nazione;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.fasciaPrezzo = fasciaPrezzo;
        this.delivery = delivery;
        this.prenotazioneOnline = prenotazioneOnline;
        this.tipoCucina = tipoCucina;
        this.numeroTelefono = numeroTelefono;
        this.websiteUrl = websiteUrl;
        this.premi = premi;
        this.stelle = stelle;
        this.descrizione = descrizione;
		
	}
	
	public String getNome() { return nome; }
    public String getIndirizzo() { return indirizzo; }
    public String getCitta() { return citta; }
    public String getNazione() { return nazione; }
    public double getLatitudine() { return latitudine; }
    public double getLongitudine() { return longitudine; }
    public double getFasciaPrezzo() { return fasciaPrezzo; }
    public boolean isDelivery() { return delivery; }
    public boolean isPrenotazioneOnline() { return prenotazioneOnline; }
    public String getTipoCucina() { return tipoCucina; }
    public String getNumeroTelefono() { return numeroTelefono; }
    public String getWebsiteUrl() { return websiteUrl; }
    public int getPremi() { return premi; }
    public int getStelle() { return stelle; }
    public String getDescrizione() { return descrizione; }
		
    public void setStelle(int stelle) {
    	if(stelle>=1 && stelle<=5)
    		this.stelle = stelle;
    }
    public void setDescrizione(String descrizione) {
    	this.descrizione = descrizione;
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s, %s (%s)\nTipo cucina: %s\nFascia prezzo: %.2f€\nStelle: %d ⭐",
                nome, indirizzo, citta, nazione, tipoCucina, fasciaPrezzo, stelle);
    }
    
    
    public String toCSV() {
        return String.join(";",
                nome, indirizzo, citta, nazione,
                String.valueOf(latitudine), String.valueOf(longitudine),
                String.valueOf(fasciaPrezzo), String.valueOf(delivery),
                String.valueOf(prenotazioneOnline), tipoCucina, numeroTelefono,
                websiteUrl, String.valueOf(premi), String.valueOf(stelle), descrizione);
    }
    
    public boolean match(String tipoCucinaFiltro, String cittaFiltro, double prezzoMax) {
        return (tipoCucinaFiltro == null || tipoCucina.equalsIgnoreCase(tipoCucinaFiltro)) &&
               (cittaFiltro == null || citta.equalsIgnoreCase(cittaFiltro)) &&
               fasciaPrezzo <= prezzoMax;
    }
    
    
    
    
    
}
