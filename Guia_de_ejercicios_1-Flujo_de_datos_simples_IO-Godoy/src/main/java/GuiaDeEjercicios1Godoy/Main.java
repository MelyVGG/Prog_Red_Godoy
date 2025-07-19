package GuiaDeEjercicios1Godoy;

import java.io.*;
import java.util.*;

public class Main {
	public static void main(String[] args) throws IOException {
		ClaseEjerciciosIO ejercicios = new ClaseEjerciciosIO();

		while (true) {
			System.out.println("\n--- MENÃº PRINCIPAL ---");
			System.out.println("1 - Ejercicio 1");
			System.out.println("2 - Ejercicio 2");
			System.out.println("5 - Salir");
			System.out.print("Seleccione una opciÃ³n: ");

			int opcion = leerEntero();

			switch (opcion) {
			case 1:
				ejercicios.ejercicios1();
				break;
			case 2:
				ejercicios.ejercicios2();
				break;
			case 5:
				System.out.println("Saliendo...");
				return;
			default:
				System.out.println("OpciÃ³n invÃ¡lida");
			}
		}
	}

	public static int leerEntero() throws IOException {
		byte[] buffer = new byte[100];
		System.in.read(buffer);
		try {
			return Integer.parseInt(new String(buffer).trim());
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public static float leerFloat() throws IOException {
		byte[] buffer = new byte[100];
		System.in.read(buffer);
		try {
			return Float.parseFloat(new String(buffer).trim());
		} catch (NumberFormatException e) {
			return -1f;
		}
	}

	public static String leerLinea() throws IOException {
		byte[] buffer = new byte[100];
		System.in.read(buffer);
		return new String(buffer).trim();
	}
}

class ClaseEjerciciosIO {

	PrintStream ps = System.out;

	public void ejercicios1() throws IOException {
		while (true) {
			System.out.println("\n--- Ejercicios 1 ---");
			System.out.println("1 - Sueldo bruto");
			System.out.println("2 - Ã�ngulo restante");
			System.out.println("3 - PerÃ­metro del cuadrado");
			System.out.println("4 - Fahrenheit a Celsius");
			System.out.println("5 - Segundos a d/h/m/s");
			System.out.println("6 - Planes de pago");
			System.out.println("7 - Signo zodiacal");
			System.out.println("0 - Volver");
			System.out.print("Opcion: ");

			int op = Main.leerEntero();
			switch (op) {
			case 1:
				ejercicio1a();
				break;
			case 2:
				ejercicio1b();
				break;
			case 3:
				ejercicio1c();
				break;
			case 4:
				ejercicio1d();
				break;
			case 5:
				ejercicio1e();
				break;
			case 6:
				ejercicio1f();
				break;
			case 7:
				ejercicio1g();
				break;
			case 0:
				return;
			default:
				ps.println("Opcion invalida");
			}
		}
	}

	public void ejercicios2() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			ps.println("\n--- Ejercicios 2 ---");
			ps.println("1 - Ordenar 3 apellidos alfabÃ©ticamente");
			ps.println("2 - Menor de 4 nÃºmeros reales");
			ps.println("3 - Par o impar");
			ps.println("4 - Mayor divisible por menor");
			ps.println("5 - Signo zodiacal segÃºn fecha");
			ps.println("6 - Apellido mÃ¡s largo");
			ps.println("7 - Tabla de multiplicar");
			ps.println("8 - Verificar si es primo");
			ps.println("0 - Volver");
			ps.print("Opcion: ");
			String op = reader.readLine();
			switch (op) {
			case "1":
				ps.print("Apellido 1: ");
				String a1 = reader.readLine();
				ps.print("Apellido 2: ");
				String a2 = reader.readLine();
				ps.print("Apellido 3: ");
				String a3 = reader.readLine();
				List<String> apellidos = Arrays.asList(a1, a2, a3);
				Collections.sort(apellidos);
				ps.println("Ordenados: " + apellidos);
				break;
			case "2":
				double[] nums = new double[4];
				for (int i = 0; i < 4; i++) {
					ps.print("NÃºmero " + (i + 1) + ": ");
					nums[i] = Double.parseDouble(reader.readLine());
				}
				double menor = Arrays.stream(nums).min().getAsDouble();
				ps.println("Menor: " + menor);
				break;
			case "3":
				ps.print("Ingrese un nÃºmero: ");
				int n = Integer.parseInt(reader.readLine());
				ps.println((n % 2 == 0) ? "Es par" : "Es impar");
				break;
			case "4":
				ps.print("Primer nÃºmero: ");
				double num1 = Double.parseDouble(reader.readLine());
				ps.print("Segundo nÃºmero: ");
				double num2 = Double.parseDouble(reader.readLine());
				double mayor = Math.max(num1, num2);
				double menor2 = Math.min(num1, num2);
				if (menor2 != 0 && mayor % menor2 == 0)
					ps.println("El mayor es divisible por el menor");
				else
					ps.println("No es divisible");
				break;
			case "5":
				ps.print("DÃ­a de nacimiento: ");
				int dia = Integer.parseInt(reader.readLine());
				ps.print("Mes de nacimiento (1-12): ");
				int mes = Integer.parseInt(reader.readLine());
				String signo = "";
				if ((mes == 3 && dia >= 21) || (mes == 4 && dia <= 19))
					signo = "Aries";
				else if ((mes == 4 && dia >= 20) || (mes == 5 && dia <= 20))
					signo = "Tauro";
				else if ((mes == 5 && dia >= 21) || (mes == 6 && dia <= 20))
					signo = "GÃ©minis";
				else if ((mes == 6 && dia >= 21) || (mes == 7 && dia <= 22))
					signo = "CÃ¡ncer";
				else if ((mes == 7 && dia >= 23) || (mes == 8 && dia <= 22))
					signo = "Leo";
				else if ((mes == 8 && dia >= 23) || (mes == 9 && dia <= 22))
					signo = "Virgo";
				else if ((mes == 9 && dia >= 23) || (mes == 10 && dia <= 22))
					signo = "Libra";
				else if ((mes == 10 && dia >= 23) || (mes == 11 && dia <= 21))
					signo = "Escorpio";
				else if ((mes == 11 && dia >= 22) || (mes == 12 && dia <= 21))
					signo = "Sagitario";
				else if ((mes == 12 && dia >= 22) || (mes == 1 && dia <= 19))
					signo = "Capricornio";
				else if ((mes == 1 && dia >= 20) || (mes == 2 && dia <= 18))
					signo = "Acuario";
				else if ((mes == 2 && dia >= 19) || (mes == 3 && dia <= 20))
					signo = "Piscis";
				ps.println("Tu signo es: " + signo);
				break;
			case "6":
				ps.print("Nombre completo 1: ");
				String p1 = reader.readLine();
				ps.print("Nombre completo 2: ");
				String p2 = reader.readLine();
				String apellido1 = p1.trim().split(" ")[1];
				String apellido2 = p2.trim().split(" ")[1];
				ps.println((apellido1.length() > apellido2.length()) ? p1 : p2);
				break;
			case "7":
				ps.print("Ingrese un nÃºmero: ");
				int tabla = Integer.parseInt(reader.readLine());
				for (int i = 1; i <= 10; i++) {
					ps.printf("%d x %d = %d\n", tabla, i, tabla * i);
				}
				break;
			case "8":
				ps.print("Ingrese un nÃºmero: ");
				int num = Integer.parseInt(reader.readLine());
				boolean primo = num > 1;
				for (int i = 2; i <= Math.sqrt(num); i++) {
					if (num % i == 0) {
						primo = false;
						break;
					}
				}
				ps.println(primo ? "Es primo" : "No es primo");
				break;
			case "0":
				return;
			default:
				ps.println("Opcion invalida");
			}
		}
	}

	public void ejercicio1a() throws IOException {
		ps.print("Valor hora de trabajo: ");
		float valor = Main.leerFloat();
		ps.print("Cantidad de horas trabajadas: ");
		float horas = Main.leerFloat();
		ps.println("Sueldo bruto: $" + (valor * horas));
	}

	public void ejercicio1b() throws IOException {
		ps.print("Ã�ngulo 1: ");
		float a1 = Main.leerFloat();
		ps.print("Ã�ngulo 2: ");
		float a2 = Main.leerFloat();
		float restante = 180 - a1 - a2;
		if (restante > 0)
			ps.println("El Ã¡ngulo restante es: " + restante + "Â°");
		else
			ps.println("Los Ã¡ngulos no forman un triÃ¡ngulo vÃ¡lido.");
	}

	public void ejercicio1c() throws IOException {
		ps.print("Superficie del cuadrado en m2: ");
		float superficie = Main.leerFloat();
		double lado = Math.sqrt(superficie);
		ps.println("PerÃ­metro: " + (lado * 4));
	}

	public void ejercicio1d() throws IOException {
		ps.print("Temperatura en Fahrenheit: ");
		float f = Main.leerFloat();
		float c = (f - 32) * 5 / 9;
		ps.println("Temperatura en Celsius: " + c);
	}

	public void ejercicio1e() throws IOException {
		ps.print("Tiempo en segundos: ");
		int total = Main.leerEntero();
		int dias = total / 86400;
		int horas = (total % 86400) / 3600;
		int min = (total % 3600) / 60;
		int seg = total % 60;
		ps.printf("%d dÃ­as, %d horas, %d minutos, %d segundos\n", dias, horas, min, seg);
	}

	public void ejercicio1f() throws IOException {
		ps.print("Precio del artÃ­culo: ");
		float precio = Main.leerFloat();
		ps.printf("Plan 1: $%.2f (contado -10%%)\n", precio * 0.9);
		float total2 = precio * 1.1f;
		ps.printf("Plan 2: $%.2f contado y 2 cuotas de $%.2f\n", total2 * 0.5, (total2 * 0.5) / 2);
		float total3 = precio * 1.15f;
		ps.printf("Plan 3: $%.2f contado y 5 cuotas de $%.2f\n", total3 * 0.25, (total3 * 0.75) / 5);
		float total4 = precio * 1.25f;
		ps.printf("Plan 4: 4 cuotas de $%.2f y 4 cuotas de $%.2f\n", (total4 * 0.6f) / 4, (total4 * 0.4f) / 4);
	}

	public void ejercicio1g() throws IOException {
		ps.print("Ingrese su signo zodiacal: ");
		String signo = Main.leerLinea().toLowerCase();
		switch (signo) {
		case "aries":
			ps.println("Marzo - Abril");
			break;
		case "tauro":
			ps.println("Abril - Mayo");
			break;
		case "geminis":
		case "gÃ©minis":
			ps.println("Mayo - Junio");
			break;
		case "cancer":
		case "cÃ¡ncer":
			ps.println("Junio - Julio");
			break;
		case "leo":
			ps.println("Julio - Agosto");
			break;
		case "virgo":
			ps.println("Agosto - Septiembre");
			break;
		case "libra":
			ps.println("Septiembre - Octubre");
			break;
		case "escorpio":
			ps.println("Octubre - Noviembre");
			break;
		case "sagitario":
			ps.println("Noviembre - Diciembre");
			break;
		case "capricornio":
			ps.println("Diciembre - Enero");
			break;
		case "acuario":
			ps.println("Enero - Febrero");
			break;
		case "piscis":
			ps.println("Febrero - Marzo");
			break;
		default:
			ps.println("Signo no reconocido");
		}
	}
}
