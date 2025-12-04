package ejercicio4;

import javax.swing.*;


public class MatricesMultiples {
	public static int[][] randomMatrix(int n) {
		int[][] m = new int[n][n];
		for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) m[i][j] = (int)(Math.random()*10);
		return m;
	}

	public static int[][] multiplySequential(int[][] a, int[][] b) {
		int n = a.length;
		int[][] c = new int[n][n];
		for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) {
			int sum = 0;
			for (int k = 0; k < n; k++) sum += a[i][k]*b[k][j];
			c[i][j] = sum;
		}
	return c;
	}

	public static int[][] multiplyThreads(int[][] a, int[][] b) throws InterruptedException {
		int n = a.length;
		int[][] c = new int[n][n];
		Thread[] threads = new Thread[4];
		for (int t = 0; t < 4; t++) {
			int startRow = t * (n/4);
			int endRow = (t == 3) ? n : startRow + (n/4);
			threads[t] = new Thread(new Worker(a,b,c,startRow,endRow));
			threads[t].start();
		}
		for (Thread th : threads) th.join();
		return c;
	}

	static class Worker implements Runnable {
		private final int[][] a,b,c; private final int rs,re;
		Worker(int[][] a,int[][] b,int[][] c,int rs,int re){this.a=a;this.b=b;this.c=c;this.rs=rs;this.re=re;}
		public void run(){
			int n=a.length;
			for(int i=rs;i<re;i++) for(int j=0;j<n;j++){int s=0; for(int k=0;k<n;k++) s+=a[i][k]*b[k][j]; c[i][j]=s;}
		}
	}

	public static void showMatrix(int[][] m, String title){
		StringBuilder sb = new StringBuilder(title + "\n");
		for (int[] row : m) { for (int v : row) sb.append(String.format("%4d", v)); sb.append('\n'); }
		JOptionPane.showMessageDialog(null, new JTextArea(sb.toString()));
	}
}