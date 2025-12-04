package ejercicio4;

public class MainEj4 {
	public static void main(String[] args) throws InterruptedException {
		int n = 4;
		int[][] a = MatricesMultiples.randomMatrix(n);
		int[][] b = MatricesMultiples.randomMatrix(n);

		long t0 = System.currentTimeMillis();
		int[][] s = MatricesMultiples.multiplySequential(a,b);
		long seq = System.currentTimeMillis() - t0;

		long t1 = System.currentTimeMillis();
		int[][] m = MatricesMultiples.multiplyThreads(a,b);
		long par = System.currentTimeMillis() - t1;

		MatricesMultiples.showMatrix(a, "Matriz A");
		MatricesMultiples.showMatrix(b, "Matriz B");
		MatricesMultiples.showMatrix(s, "Resultado Secuencial (t="+seq+" ms)");
		MatricesMultiples.showMatrix(m, "Resultado Multithread (t="+par+" ms)");
	}
}