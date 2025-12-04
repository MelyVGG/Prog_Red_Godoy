package ejercicio6;

public class Alumno {
	public String nombre, apellido;
	public int[] asistencia = new int[9];
	public double[] notas = new double[3];
	public boolean esAlumnoRegular;
	
	public Alumno(String n, String a) { this.nombre = n; this.apellido = a; }
	
	public double promedioNotas() {
		double s=0; for(double x: notas) s+=x; return s/ notas.length;
	}
	
	public double porcentajeAsistencia() {
		int present = 0; for(int x: asistencia) if (x==1) present++; return (present/9.0)*100.0;
	}
}
