package cybersec.af1.gruppo4.analisilog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class AnalisiLog
{
	
	private File log;
	private FileReader fr;
	private BufferedReader br;
	private StringTokenizer st;
	private String line;
	private Map<Integer, int[]> infoClienti;
	private Map<Integer, int[]> infoBanca;
	
	private static Logger logger = Logger.getLogger(AnalisiLog.class);
	
	public AnalisiLog(){
		// <NumCliente, (List[0]:#opRiuscite, list[1]:#opFallite, list[2]:denaroRichiesto, list[3]:denaroRicevuto, list[4]: tempoMedioAttesa)>
		infoClienti = new HashMap<Integer, int[] >();
		// <NumeroSportello, (List[0]:#opEffettuate, list[1]:#ricariche, list[2]: denaroErogato)>
		infoBanca = new HashMap<Integer, int[]>(); 
		
	}
	
	public void analizzaFile(String s){
		try {
			log = new File(s);
			fr = new FileReader(log);
			br = new BufferedReader(fr);
			line = br.readLine();
			//while che cicla sulle linee del file di log
			while(line != null){
				st = new StringTokenizer(line);
				int idS = -1, idC = -1, qta = -1;
				// while che cicla su ogni token della stringa 'line'
				flag: while(st.hasMoreTokens()){
					String s1 = st.nextToken();
					// controllo che identifica se la linea appartiene al log della classe Banca
					if(s1.contains(".Banca")){ 
						//while che cicla sui restanti token della linea relativa al log di Banca
						while(st.hasMoreTokens()){
							String s2 = st.nextToken();
							if(s2.startsWith("$")){
								if(s2.contains("BS")){
									idS = Integer.parseInt(s2.substring(4, s2.length()));
									if(!infoBanca.containsKey(idS)){
										infoBanca.put(idS, new int[3]);
									}
									//incremento il #operazioni effettuate dal sportello idS
									infoBanca.get(idS)[0]++; 
								} else if (s2.contains("BQ")){
									qta = Integer.parseInt(s2.substring(4, s2.length()));
									if(!infoBanca.containsKey(idS)){ //inserito controllo per sicurezza
										infoBanca.put(idS, new int[3]);
									}
									//incremento la quantita erogata dalla sportello ids di qta
									infoBanca.get(idS)[2] += qta;
								}else if(s2.contains("BR")){
									idS = Integer.parseInt(s2.substring(4, s2.length()));
									if(!infoBanca.containsKey(idS)){ //inserito controllo per sicurezza
										infoBanca.put(idS, new int[3]);
									}
									//incremento le operazioni di ricarica effettuate allo sportello idS
									infoBanca.get(idS)[1]++;
								}
							}
						}
						break flag;
						// controllo che identifica se la linea appartiene al log della classe Cliente
					}else if(s1.contains(".Cliente")){
						//while che cicla sui restanti token della linea relativa al log di Cliente
						while(st.hasMoreTokens()){
							String s2 = st.nextToken();
							if(s2.startsWith("$")){
								if(s2.contains("NC")){
									idC = Integer.parseInt(s2.substring(4, s2.length()));
									if(!infoClienti.containsKey(idC)){
										infoClienti.put(idC, new int[5]);
									}
									//incremento le operazioni riuscite per il cliente idC
									infoClienti.get(idC)[0]++;
								} else if(s2.contains("CQ")){
									qta = Integer.parseInt(s2.substring(4, s2.length()));
									if(!infoClienti.containsKey(idC)){
										infoClienti.put(idC, new int[5]);
									}
									//incremento le quantita di denaro richieste e ricevute dal cliente idC di qta
									infoClienti.get(idC)[2] += qta ;
									infoClienti.get(idC)[3] += qta ;
								} else if(s2.contains("CN")){
									idC = Integer.parseInt(s2.substring(4, s2.length()));
									if(!infoClienti.containsKey(idC)){
										infoClienti.put(idC, new int[5]);
									}
									//incremento le operazioni non riuscite dal cliente  idC
									infoClienti.get(idC)[1]++;
								} else if(s2.contains("QR")){
									qta = Integer.parseInt(s2.substring(4, s2.length()));
									if(!infoClienti.containsKey(idC)){
										infoClienti.put(idC, new int[5]);
									}
									//incremento solo la quantita di denaro richiesto dal cliente idC se l'operazione è fallita
									infoClienti.get(idC)[2] += qta ;
								} else if(s2.contains("CA")){
									qta = Integer.parseInt(s2.substring(4, s2.length()));
									if(!infoClienti.containsKey(idC)){
										infoClienti.put(idC, new int[5]);
									}
									//sommo tutti i tempi di attesa del cliente idC in coda
									infoClienti.get(idC)[4] += qta ;
								}
							}
						}
						break flag;
					}
				}
				line = br.readLine();
			}
			int dim = infoClienti.size();
			for (int i = 0; i < dim; i++) {
				int op = infoClienti.get(i)[0] + infoClienti.get(i)[1];
				infoClienti.get(i)[4] = (int) infoClienti.get(i)[4]/op;
			}
			
		}catch (FileNotFoundException f) {
			logger.error("FileNotFoundException AnalisiLog", f);
		}
		catch (IOException e) {
			logger.error("IOException AnalisiLog", e);
		}
	}

	public Map<Integer, int[]> getInfoClienti() {
		return infoClienti;
	}
	
	public Map<Integer, int[]> getInfoBanca() {
		return infoBanca;
	}
	
	public static void main(String[] args) {
		AnalisiLog l = new AnalisiLog();
		l.analizzaFile("D:/Progetto Cyber Sec/workspaceCS/banca_af1-1.log");
		l.analizzaFile("D:/Progetto Cyber Sec/workspaceCS/banca_af1-2.log");
		l.analizzaFile("D:/Progetto Cyber Sec/workspaceCS/banca_af1-3.log");
		Map<Integer, int[]> m1 = l.getInfoBanca();
		Map<Integer, int[]> m2 = l.getInfoClienti();
		
		Gson json = new Gson();
		String bancaJson = json.toJson(m1);
		String clientiJson = json.toJson(m2);
//		logger.info(bancaJson);
//		logger.info(clientiJson);
		try {
			//write converted json data to a file named "file.json"
			FileWriter fw1 = new FileWriter("D:/Progetto Cyber Sec/workspaceCS/Statbanca.json");
			fw1.write(bancaJson);
			fw1.close();
//			FileWriter 
			fw1 = new FileWriter("D:/Progetto Cyber Sec/workspaceCS/Statcliente.json");
			fw1.write(clientiJson);
			fw1.close();
	 
		} catch (IOException e) {
			logger.error("IOException AnalisiLog", e);
		}
	}
}