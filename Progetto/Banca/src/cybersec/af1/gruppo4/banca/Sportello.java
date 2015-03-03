package cybersec.af1.gruppo4.banca;

public class Sportello {

	private int idSportello;
	private int disponibilita;

	public Sportello(int id){
		idSportello = id;
		disponibilita = 5000;
	}
	
	public int getIdSportello() {
		return idSportello;
	}

	public int getDisponibilita() {
		return disponibilita;
	}

	public void setDisponibilita(int disponibilita) {
		this.disponibilita = disponibilita;
	}
	
}
