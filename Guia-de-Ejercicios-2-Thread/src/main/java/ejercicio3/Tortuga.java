package ejercicio3;

import java.util.concurrent.atomic.AtomicInteger;

public class Tortuga extends Corredores {
	public Tortuga(AtomicInteger pos, TablaCarreras board) { super("Tortuga", pos, board); }

	@Override
	public void run() {
try {
while (pos.get() < 70) {
Thread.sleep(1000);
int rnd = r.nextInt(100) + 1;
if (rnd <= 50) pos.addAndGet(3);
else if (rnd <= 70) pos.addAndGet(-6);
else pos.addAndGet(1);
if (pos.get() < 1) pos.set(1);
printLine(pos.get(), 'T');
}
} catch (InterruptedException e) { Thread.currentThread().interrupt(); }
}
}
