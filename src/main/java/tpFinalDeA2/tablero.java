package tpFinalDeA2;

import java.util.ArrayList;
import java.util.List;

public class tablero {
    public static final int TAMANIO = 10;
    private final ArrayList<ArrayList<celda>> tablero;
    private final ArrayList<barco> barcos;

    public tablero() {
        tablero = new ArrayList<>();
        barcos = new ArrayList<>();
        inicializarTablero();
    }

    public void inicializarTablero() {
        tablero.clear();
        for (int i = 0; i < TAMANIO; i++) {
            ArrayList<celda> fila = new ArrayList<>();
            for (int j = 0; j < TAMANIO; j++) fila.add(new celda());
            tablero.add(fila);
        }
    }

    public boolean colocarBarco(String nombre, int fila, int col, int longitud, boolean horizontal) {
        
        if (horizontal) {
            if (col + longitud > TAMANIO) return false;
        } else {
            if (fila + longitud > TAMANIO) return false;
        }
        
        for (int k = 0; k < longitud; k++) {
            int r = fila + (horizontal ? 0 : k);
            int c = col + (horizontal ? k : 0);
            if (tablero.get(r).get(c).getTipo() == tipoCelda.barco) return false;
        }
        
        barco barco = new barco(nombre, longitud);
        barcos.add(barco);
        for (int k = 0; k < longitud; k++) {
            int r = fila + (horizontal ? 0 : k);
            int c = col + (horizontal ? k : 0);
            celda cel = tablero.get(r).get(c);
            cel.setTipo(tipoCelda.barco);
            cel.setNombreBarco(nombre);
        }
        return true;
    }

    
    public String disparar(int fila, int col) {
        if (fila < 0 || fila >= TAMANIO || col < 0 || col >= TAMANIO) return "INVALIDO";
        celda cel = tablero.get(fila).get(col);
        if (cel.isDisparada()) return "REPETIDO";
        cel.setDisparada(true);
        if (cel.getTipo() == tipoCelda.barco) {
            cel.setTipo(tipoCelda.impacto);
            
            for (barco b : barcos) {
                if (b.getNombre().equals(cel.getNombreBarco())) {
                    b.recibirImpacto();
                    if (b.estaHundido()) return "HUNDIDO:" + b.getNombre();
                    return "IMPACTO";
                }
            }
            return "IMPACTO";
        } else {
            cel.setTipo(tipoCelda.fallo);
            return "AGUA";
        }
    }

    public boolean todosLosBarcosCaidos() {
        for (barco b : barcos) {
            if (!b.estaHundido()) return false;
        }
        return true;
    }

    public void mostrarTablero(boolean ocultarBarcos) {
        System.out.print("  ");
        for (int c = 0; c < TAMANIO; c++) System.out.print(c + " ");
        System.out.println();
        for (int r = 0; r < TAMANIO; r++) {
            System.out.print(r + " ");
            for (int c = 0; c < TAMANIO; c++) {
                celda cel = tablero.get(r).get(c);
                char simbolo = '~';
                if (cel.getTipo() == tipoCelda.barco) simbolo = ocultarBarcos ? '~' : 'B';
                if (cel.getTipo() == tipoCelda.impacto) simbolo = 'X';
                if (cel.getTipo() == tipoCelda.fallo) simbolo = 'O';
                System.out.print(simbolo + " ");
            }
            System.out.println();
        }
    }

    public void mostrarBarcos() {
        for (barco b : barcos) {
            System.out.printf("%s (len %d) - impactos %d - %s%n",
                    b.getNombre(), b.getLongitud(), b.getImpactos(),
                    b.estaHundido() ? "HUNDIDO" : "ACTIVO");
        }
    }

    public ArrayList<ArrayList<celda>> getTablero() {
        return tablero;
    }

    public List<barco> getBarcos() {
        return barcos;
    }
}