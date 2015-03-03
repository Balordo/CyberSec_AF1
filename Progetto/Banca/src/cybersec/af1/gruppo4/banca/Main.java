package cybersec.af1.gruppo4.banca;

public class Main {

	public static void main(String[] args) {
		
		Banca b = new Banca(5);
		
		Agente agent = new Agente(b);
		new Thread(agent).start();
		
		Cliente c[] = new Cliente[30];
		for(int i = 0; i<30; i++){
			c[i] = new Cliente(i, b);
			new Thread(c[i]).start();
		}
		
	}

}
