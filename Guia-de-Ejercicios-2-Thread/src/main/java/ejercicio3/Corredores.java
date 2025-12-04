package ejercicio3;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Corredores implements Runnable {
	protected final String name;
	protected final AtomicInteger pos;
	protected final TablaCarreras board;
	protected final Random r = new Random();
	
	public Corredores(String name, AtomicInteger pos, TablaCarreras board) {
		this.name = name;
		this.pos = pos;
		this.board = board;
	}

	protected void printLine(int position, char c) {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < position; i++) sb.append(' ');
		sb.append(c);
		board.print(sb.toString());
	}
}
