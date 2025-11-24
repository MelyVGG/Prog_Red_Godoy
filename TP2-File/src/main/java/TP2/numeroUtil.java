package TP2;

public class numeroUtil {

    public static int tipoNumero(String texto) {
        if (texto.matches("^-?\\d+$")) {
            return 1;
        } else if (texto.matches("^-?\\d+([\\.,]\\d+)?$")) {
            return 2;
        } else {
            return 0;
        }
    }

    public static int convertirInt(String texto) {
        return Integer.parseInt(texto.replace(",", "."));
    }

    public static float convertirFloat(String texto) {
        return Float.parseFloat(texto.replace(",", "."));
    }
}