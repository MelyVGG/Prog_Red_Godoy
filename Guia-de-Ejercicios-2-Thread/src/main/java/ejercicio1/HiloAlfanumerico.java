package ejercicio1;

public class HiloAlfanumerico implements Runnable {
	private final int tipo;
	private volatile boolean running = true;
	
	public HiloAlfanumerico(int tipo) {
		this.tipo = tipo;
	}
	
	public void stop() { running = false; }
	
	@Override
	public void run() {
		try {
			if (tipo == 1) {
				for (int i = 1; i <= 30 && running; i++) {
					System.out.println(i);
					Thread.sleep(100);
				}
			} else if (tipo == 2) {
				for (char c = 'a'; c <= 'z' && running; c++) {
					System.out.println(c);
					Thread.sleep(100);
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
