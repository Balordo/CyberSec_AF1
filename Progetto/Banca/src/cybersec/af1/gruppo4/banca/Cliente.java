package cybersec.af1.gruppo4.banca;

import java.util.Random;
import org.apache.log4j.Logger;

public class Cliente implements Runnable {
	
	private int numCliente;
	private int quantita;
	private Banca banca;
	private int idSportello;
	private long inCoda;
	private long attesa;
	private boolean operazioneRiuscita;
	
	private static int MIN_VIAGGIO = 2000;
	private static int MAX_VIAGGIO = 4000;
	private static int MIN_DENARO = 150;
	private static int MAX_DENARO = 1500;
	private static int MIN_TEMPO_OPERAZIONE = 1000;
	private static int MAX_TEMPO_OPERAZIONE = 3000;
	
	static Random random = new Random();
	
	private static Logger logger = Logger.getLogger(Cliente.class);
	
	public Cliente(int id, Banca b){
		
		this.numCliente = id;
		this.banca = b;
	}
	
	@Override
	public void run() {
		while(true){
			try {
				logger.debug("Il cliente si reca allo sportello." );
				Thread.sleep((random.nextInt(MAX_VIAGGIO-MIN_VIAGGIO)+MIN_VIAGGIO));
				logger.debug("Il cliente è giunto allo sportello, e si appresta al prelievo!");
				quantita = generaQuantitaCasuale();
				inCoda = System.currentTimeMillis();
				operazioneRiuscita = banca.richiediSportelloPrelievo(this);
				attesa = System.currentTimeMillis() - inCoda;
				Thread.sleep((random.nextInt(MAX_TEMPO_OPERAZIONE-MIN_TEMPO_OPERAZIONE)+MIN_TEMPO_OPERAZIONE));
				banca.rilasciaSportelloPrelievo(this);
				if(operazioneRiuscita){			
					logger.info("Il cliente $NC:" + numCliente + " ha prelevato $CQ:"+ quantita + " € e ha atteso $CA:"+ attesa +" .");
				}else{
					logger.info("Il cliente $CN:" + numCliente + " non è riuscito a prelevare la quantita $QR:" + quantita + " € e ha atteso $CA:"+ attesa +" .");
				}
			} catch (InterruptedException e) {
				logger.error("Interrupted Exception Cliente " + numCliente, e);
			}
		}
	}

	private int generaQuantitaCasuale() {
		return (random.nextInt(MAX_DENARO-MIN_DENARO)+MIN_DENARO);
	}

	public int getNumeroCliente() {
		return numCliente;
	}

	public int getQuantita() {
		return quantita;
	}

	public long getInCoda() {
		return inCoda;
	}

	public boolean isOperazioneRiuscita() {
		return operazioneRiuscita;
	}

	public int getIdSportello() {
		return idSportello;
	}

	public void setIdSportello(int idSportello) {
		this.idSportello = idSportello;
	}
}