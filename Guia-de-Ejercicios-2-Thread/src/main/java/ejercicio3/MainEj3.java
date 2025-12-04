package ejercicio3;

import java.util.concurrent.atomic.AtomicInteger;

public class MainEj3 {
	public static void main(String[] args) throws InterruptedException {
		TablaCarreras board = new TablaCarreras();
		board.setVisible(true);
		board.print("--- Comienza la carrera ---");

		AtomicInteger posT = new AtomicInteger(1);
		AtomicInteger posL = new AtomicInteger(1);

		Thread tortuga = new Thread(new Tortuga(posT, board));
		Thread liebre = new Thread(new Liebre(posL, board));

		tortuga.start();
		liebre.start();

		while (true) {
			Thread.sleep(200);
			if (posT.get() >= 70 && posL.get() >= 70) { board.print("Empate!"); break; }
			if (posT.get() >= 70) { board.print("Gana la Tortuga!"); break; }
			if (posL.get() >= 70) { board.print("Gana la Liebre!"); break; }
		}

		tortuga.interrupt();
		liebre.interrupt();
	}
}