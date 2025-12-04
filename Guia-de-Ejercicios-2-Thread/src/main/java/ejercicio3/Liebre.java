package ejercicio3;

import java.util.concurrent.atomic.AtomicInteger;

public class Liebre extends Corredores {
	public Liebre(AtomicInteger pos, TablaCarreras board) { super("Liebre", pos, board); }

	@Override
	public void run() {
		try {
			while (pos.get() < 70) {
				Thread.sleep(1000);
				int rnd = r.nextInt(100) + 1;
				if (rnd <= 20) { /* duerme */ }
				else if (rnd <= 40) pos.addAndGet(9);
				else if (rnd <= 50) pos.addAndGet(-12);
				else if (rnd <= 80) pos.addAndGet(1);
				else pos.addAndGet(-2);
				if (pos.get() < 1) pos.set(1);
				printLine(pos.get(), 'L');
			}
		} catch (InterruptedException e) { Thread.currentThread().interrupt(); }
	}
}
