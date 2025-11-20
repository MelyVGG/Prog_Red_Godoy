package tpFinalDeA2;

public class celda {
	private tipoCelda tipo;
    private boolean disparada;
    private String nombreBarco; // null si no hay barco

    public celda() {
        this.tipo = tipoCelda.agua;
        this.disparada = false;
        this.nombreBarco = null;
    }

    public tipoCelda getTipo() { return tipo; }
    public void setTipo(tipoCelda tipo) { this.tipo = tipo; }

    public boolean isDisparada() { return disparada; }
    public void setDisparada(boolean disparada) { this.disparada = disparada; }

    public String getNombreBarco() { return nombreBarco; }
    public void setNombreBarco(String nombreBarco) { this.nombreBarco = nombreBarco; }
}
