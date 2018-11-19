package br.com.XPTO.listaCidades.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Scanner;

import org.springframework.stereotype.Component;


@Component
public class CalculadorDistancia {
	
	// by: https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
	public double distance(double lat1, double lat2, double lon1, double lon2) {

		final int R = 6371; // Radius of the earth

		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lon2 - lon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c;

		distance = Math.pow(distance, 2);
		return Math.sqrt(distance);
	}

	public String cidadesMaisDistantes() throws FileNotFoundException {

		long ini = System.currentTimeMillis();
		
		Scanner scanner = new Scanner(new File("municipios_brasileiros.csv"), "windows-1252");
		scanner.useLocale(Locale.US);
		ArrayList<Double> listaColunaLong = new ArrayList<Double>();
		ArrayList<Double> listaColunaLatitude = new ArrayList<Double>();
		ArrayList<String> listaColuna = new ArrayList<String>();
		while (scanner.hasNextLine()) {
			String linha = scanner.nextLine();
			String[] inArray = linha.split(",");
			listaColuna.add(inArray[1]);
			listaColunaLong.add(Double.parseDouble(inArray[7]));
			listaColunaLatitude.add(Double.parseDouble(inArray[6]));
		}
		scanner.close();
	
		ArrayList<Double> listaFinal = new ArrayList<Double>();
		int sizeOriginal = listaColuna.size();
		for (int contador = 0; contador < sizeOriginal - 1; contador ++) {
			for (int subContador = 0; subContador < listaColunaLong.size() - 1; subContador ++) {
				listaFinal.add(new CalculadorDistancia().distance(listaColunaLatitude.get(0), listaColunaLatitude.get(subContador + 1), listaColunaLong.get(0), listaColunaLong.get(subContador + 1)));
			}
			listaColunaLatitude.remove(0);
			listaColunaLong.remove(0);
		}
	
		ArrayList<String> listaFinalTextosPartida = new ArrayList<String>();
		ArrayList<String> listaFinalTextosDestino = new ArrayList<String>();
		int sizeOriginalTexto = listaColuna.size();
		for (int contador = 0; contador < sizeOriginalTexto - 1; contador ++) {
			for (int subContador = 0; subContador < listaColuna.size() - 1; subContador ++) {
				listaFinalTextosPartida.add(listaColuna.get(0));
				listaFinalTextosDestino.add(listaColuna.get(subContador + 1));
			}
			listaColuna.remove(0);
		}
		
		DecimalFormat df = new DecimalFormat("0.##");
		
		String cidadesMaisDistantes = listaFinalTextosPartida.get(listaFinal.indexOf(Collections.max(listaFinal))) + " - " 
				+ listaFinalTextosDestino.get(listaFinal.indexOf(Collections.max(listaFinal)));
		
		String maisDistante = df.format(Collections.max(listaFinal));
		
		listaFinalTextosPartida.remove(listaFinal.indexOf(Collections.max(listaFinal)));
		listaFinalTextosDestino.remove(listaFinal.indexOf(Collections.max(listaFinal)));
		listaFinal.remove(listaFinal.indexOf(Collections.max(listaFinal)));
		
		String segundaCidadesMaisDistantes = listaFinalTextosPartida.get(listaFinal.indexOf(Collections.max(listaFinal))) + " - " 
				+ listaFinalTextosDestino.get(listaFinal.indexOf(Collections.max(listaFinal)));
		
		String segundaMaisDistante = df.format(Collections.max(listaFinal));
		
		long fim = System.currentTimeMillis();
		System.out.println("Passaram " + (fim - ini) + " milissegundos");
				
		return ("Importando do arquivo CSV..." + System.lineSeparator()
				+ "Considerando a curvatura da terra, as duas cidades mais distantes são: " + System.lineSeparator()
				+ cidadesMaisDistantes + " totalizando: "
				+ maisDistante + " km" + System.lineSeparator()
				+ "e as segundas mais distantes são: " + System.lineSeparator()
				+ segundaCidadesMaisDistantes + " totalizando: "
				+ segundaMaisDistante + " km" + System.lineSeparator());
	}
}
