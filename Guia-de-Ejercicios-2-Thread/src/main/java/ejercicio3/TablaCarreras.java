package ejercicio3;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class TablaCarreras extends JFrame {
	private final JTextArea area;
	
	public TablaCarreras() {
		super("Carrera: Tortuga vs Liebre");
		area = new JTextArea(20, 80);
		area.setFont(new Font("Monospaced", Font.PLAIN, 12));
		add(new JScrollPane(area));
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}
	
	public void print(String s) {
		SwingUtilities.invokeLater(() -> area.append(s + "\n"));
	}
}