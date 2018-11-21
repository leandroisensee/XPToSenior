package br.com.XPTO.cidades.util;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.XPTO.cidades.dto.DistanciaCidadeDTO;
import br.com.XPTO.cidades.entity.CidadeEntity;
import br.com.XPTO.cidades.repository.CidadeRepository;
import br.com.XPTO.cidades.validaton.Validation;

@Component
public class LerDoCSV {
	
	@Autowired
	private CidadeRepository repository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private CalculadorDistancia calculador;
	
	@Autowired
	private Validation validation;

	private static final String fileCSV = "municipios_brasileiros.csv";
	
	public static String getFilecsv() {
		return fileCSV;
	}

	public void gravarCsvToRepository() throws IOException {
		long ini = System.currentTimeMillis();
		try (
			Reader reader = Files.newBufferedReader(Paths.get(fileCSV), StandardCharsets.ISO_8859_1);
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
		) {
			for (CSVRecord csvRecord : csvParser) {
				CidadeEntity entity = new CidadeEntity();
				entity.setCodMunicipio(Integer.parseInt(csvRecord.get(0)));
				entity.setNomeCidade(csvRecord.get(1));
				entity.setUfSigla(csvRecord.get(3));
				entity.setUfNome(csvRecord.get(4));
				entity.setClassificacao(Integer.parseInt(csvRecord.get(5)));
				entity.setLatitude(Double.parseDouble(csvRecord.get(6)));
				entity.setLongitude(Double.parseDouble(csvRecord.get(7)));
				repository.save(entity);
			}
			long fim = System.currentTimeMillis();
			System.out.println("Importação feita, levou " + (fim - ini) + " milissegundos");
		}
	}

	public Collection<String> lerPorColuna(String coluna) throws IOException {
		validation.validaInteiro(coluna);
		validation.validaColuna(coluna);
		Collection<String> listaColuna = new ArrayList<String>();
		try (
			Reader reader = Files.newBufferedReader(Paths.get(getFilecsv()), StandardCharsets.ISO_8859_1);
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
		) {
			for (CSVRecord csvRecord : csvParser) {
				listaColuna.add(csvRecord.get(Integer.parseInt(coluna)));
			}
		}
		return listaColuna;
	}
				
	public List<String> lerColunaProcurarDado(String coluna, String pesquisa) throws IOException {
		if ((lerPorColuna(coluna).stream().filter(i -> i.contains(pesquisa)).collect(Collectors.toList())).isEmpty()) {
			throw new EntityNotFoundException(
					"Não há resultados para a pesquisa do valor [" + pesquisa + "] na coluna " + coluna);
		}
		return lerPorColuna(coluna).stream().filter(i -> i.contains(pesquisa)).collect(Collectors.toList());
	}

	public String contarTotalRegistrosNaoRepetidos(String coluna) throws IOException {
		HashSet<String> toSet = new HashSet<String>(lerPorColuna(coluna));
		return "Total de: " + toSet.size() + " registros não repetidos na coluna selecionada";
	}

	public Integer contarTotalRegistros() throws IOException {
		Integer posicao = 0;
		Integer contador = 0;
		while (posicao <= 7) {
			contador += lerPorColuna(String.valueOf(posicao)).size();
			posicao++;
		}
		return contador;
	}
	
	public DistanciaCidadeDTO distanciaMaxima() throws IOException {
		long ini = System.currentTimeMillis();
		ArrayList<String> listaColuna = new ArrayList<String>(lerPorColuna(String.valueOf(1)));
		ArrayList<String> listaColunaLong = new ArrayList<String>(lerPorColuna(String.valueOf(7)));
		ArrayList<String> listaColunaLatitude = new ArrayList<String>(lerPorColuna(String.valueOf(6)));
		
		ArrayList<Double> listaDistancias = new ArrayList<Double>();
		int sizeOriginal = listaColuna.size();
		for (int contador = 0; contador < sizeOriginal - 1; contador ++) {
			for (int subContador = 0; subContador < listaColunaLong.size() - 1; subContador ++) {
				listaDistancias.add(calculador.calculadorDistancia(Double.parseDouble(listaColunaLatitude.get(0)), Double.parseDouble(listaColunaLatitude.get(subContador + 1)), 
						Double.parseDouble(listaColunaLong.get(0)), Double.parseDouble(listaColunaLong.get(subContador + 1))));
			}
			listaColunaLatitude.remove(0);
			listaColunaLong.remove(0);
		}	
		CidadeEntity entity = new CidadeEntity();
		entity.setDistancia(Math.floor(Collections.max(listaDistancias)*100)/100d);
		
		long fim = System.currentTimeMillis();
		System.out.println("Passaram " + (fim - ini) + " milissegundos");
	
		return modelMapper.map(entity, DistanciaCidadeDTO.class);	
	}


}
