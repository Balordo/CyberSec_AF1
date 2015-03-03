package cybersec.af1.gruppo4.banca;

import java.util.Random;
import org.apache.log4j.Logger;

public class Agente implements Runnable {
	
	private Banca banca;
	private int sportello;
	
	Random random = new Random();
	
	private static int MIN_VIAGGIO = 2000;
	private static int MAX_VIAGGIO = 5000;
	private static int MIN_TEMPO_RICARICA = 1000;
	private static int MAX_TEMPO_RICARICA = 3000;
	
	private static Logger logger = Logger.getLogger(Agente.class);
	
	public Agente(Banca b) {
		
		this.banca = b;
	}
	
	@Override
	public void run() {
		try{
		Thread.sleep((random.nextInt(MAX_VIAGGIO - MIN_VIAGGIO) + MIN_VIAGGIO));
		while(true){
				logger.debug("Agente richiede alla banca lo sportello da rifornire!");
				sportello = banca.getSportelloMinDisponibilita();
				logger.debug("L'agente si reca allo sportello per rifornirlo!" );
				Thread.sleep((random.nextInt(MAX_VIAGGIO - MIN_VIAGGIO) + MIN_VIAGGIO));
				logger.debug("Agente arrivato allo sportello e si appresta a rifornirlo.");
				banca.richiediSportelloRicarica(sportello);
				Thread.sleep((random.nextInt(MAX_TEMPO_RICARICA - MIN_TEMPO_RICARICA) + MIN_TEMPO_RICARICA));
				banca.liberaSportelloRicarica(sportello);
				logger.debug("Sportello $ARS:" + sportello + " rifornito!"); //ARS : Agente Ricarica Sportello 
			}
		}catch(InterruptedException e){
				logger.error("Interrupted Exception Agente", e);
			}
		}
}