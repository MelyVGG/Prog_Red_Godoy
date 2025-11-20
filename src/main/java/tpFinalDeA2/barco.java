package tpFinalDeA2;

public class barco {
	private final String nombre;
    private final int longitud;
    private int impactos;

    public barco(String nombre, int longitud) {
        this.nombre = nombre;
        this.longitud = longitud;
        this.impactos = 0;
    }

    public void recibirImpacto() {
        impactos++;
    }

    public boolean estaHundido() {
        return impactos >= longitud;
    }

    public String getNombre() { return nombre; }
    public int getLongitud() { return longitud; }
    public int getImpactos() { return impactos; }
}
