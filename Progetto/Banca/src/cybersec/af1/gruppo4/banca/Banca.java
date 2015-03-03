package cybersec.af1.gruppo4.banca;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

public class Banca {

//	private static int MIN_TEMPO_OPERAZIONE = 1500;
//	private static int MAX_TEMPO_OPERAZIONE = 3500;
	
	private boolean agenteInAttesa;
//	private int maxNumSportelli;
	private Sportello[] sportelli;
	private boolean[] sportelliOccupati;
	private int sportLiberi;
	private boolean esito;
	
	Random random = new Random();
	
	private static Logger logger = Logger.getLogger(Banca.class);
	
	Lock lock;
	Condition codaClienti;
	Condition agente;
	
	private LinkedList<Cliente> coda;
	
	public Banca(int numSportelli){
		agenteInAttesa = false;
		sportelliOccupati = new boolean[numSportelli];
		sportelli = new Sportello[numSportelli];
		sportLiberi = numSportelli;
		for(int i=0; i<numSportelli; i++){
			sportelli[i] = new Sportello(i);
			sportelliOccupati[i] = false;
		}
		lock = new ReentrantLock();
		codaClienti = lock.newCondition();
		agente = lock.newCondition();
		coda = new LinkedList<Cliente>();
	}
	
	public void richiediSportelloRicarica(int numSportello){
		lock.lock();
		try {
			logger.info("L'AGENTE ARRIVA ALLO SPORTELLO $BR:"+ numSportello +" PER RICARICARLO!");
			agenteInAttesa = true;
			while(sportelliOccupati[numSportello]){
				agente.await();
			}
			logger.debug("L'agente sta iniziando l'operazione di ricarica.");
			sportelli[numSportello].setDisponibilita(5000);
		} catch(InterruptedException e){
			logger.error("Interrupted Exception Banca.", e);
		}
		finally{
			lock.unlock();
		}
	}
	
	public void liberaSportelloRicarica(int numSportello){
		lock.lock();
		try {
			logger.info("L'AGENTE HA COMPLETATO L'OPERAZIONE DI RICARICA.");
			agenteInAttesa = false;
			codaClienti.signalAll();
		} catch (Exception e) {
			logger.error("Interrupted Exception Banca.", e);
		} finally {
			lock.unlock();
		}
	}
	
	public boolean richiediSportelloPrelievo(Cliente c){
		lock.lock();
		esito = false;
		try {
			coda.add(c);
			while(coda.getFirst().getNumeroCliente() != c.getNumeroCliente() || !sportelloLibero() || agenteInAttesa ){
				codaClienti.await();
			}
			coda.remove(c);
			int sport = getSportello();
			c.setIdSportello(sport);
			sportLiberi--;
			sportelliOccupati[sport] = true;
			
			if(c.getQuantita()<sportelli[sport].getDisponibilita()){				
				logger.debug("Il cliente sta iniziando l'operazione di prelievo.");
				sportelli[sport].setDisponibilita(sportelli[sport].getDisponibilita()-c.getQuantita());
				logger.info("IL CLIENTE $BC:" + c.getNumeroCliente() +" HA ESEGUITO L'OPERAZIONE ALLO SPORTELLO $BS:" + sport +" , HA PRELEVATO $BQ:"+ c.getQuantita() +" €.");
				esito = true;
			}else{
				logger.info("IL CLIENTE $CB:" + c.getNumeroCliente() +" NON E' RIUSCITO A PRELEVARE DALLO SPORTELLO $BN:" + sport +" .");
			}						
		} catch (InterruptedException e) {
			logger.error("Interrupted Exception Banca", e);
		}finally{
			lock.unlock();
		}
		return esito;
	}
	
	public void rilasciaSportelloPrelievo(Cliente c) {
		lock.lock();
		try {
			logger.info("Il cliente sta lasciando lo sportelllo.");
			int sport = c.getIdSportello();
			sportelliOccupati[sport] = false;
			sportLiberi++;
			
			if(agenteInAttesa){
				agente.signalAll();
			}
			codaClienti.signalAll();
			
		} catch (Exception e) {
			logger.error("Interrupted Exception Banca.", e);
		} finally {
			lock.unlock();
		}
	}

	private boolean sportelloLibero() {
		if(sportLiberi == 0)
			return false;
		return true;
	}

	public int getSportelloMinDisponibilita() {
		lock.lock();
		Sportello s = null;
		int nSport = 0;
		try {
			s = sportelli[0];
			for(int i=1; i<sportelli.length;i++){
				if(sportelli[i].getDisponibilita()<s.getDisponibilita()){
					s = sportelli[i];
					nSport = i;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return nSport;
	}
	
	public int getSportello() {
		for(int i=0; i<sportelliOccupati.length; i++)
			if(!sportelliOccupati[i])
				return i;
		
		return -1;
	}

	public boolean isAgenteInAttesa() {
		return agenteInAttesa;
	}

}
