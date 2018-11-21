package br.com.XPTO.cidades.validaton;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.XPTO.cidades.repository.CidadeRepository;
import br.com.XPTO.cidades.util.LerDoCSV;

@Component
public class Validation {
	
	@Autowired
	private CidadeRepository repository;
	
	public void validarBanco() throws EntityNotFoundException {
		Collection<String> teste = repository.findAnyId();
		if (teste.isEmpty()) {
			throw new EntityNotFoundException("Banco de dados está vazio, inicie pela task1");
		}
	}
	
	public void validaInteiro(String stringParaVerirficar) throws IllegalArgumentException{
		if (!StringUtils.isNumeric(stringParaVerirficar))
			throw new IllegalArgumentException("Erro, use apenas numeros inteiros");
	}
	
	public void validaColuna(String colunaParaVerirficar) throws ArrayIndexOutOfBoundsException, IOException{
		try (
			Reader reader = Files.newBufferedReader(Paths.get(LerDoCSV.getFilecsv()), StandardCharsets.ISO_8859_1);
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
		) {
			CSVRecord firstRecord = csvParser.iterator().next();
			int numColumns = firstRecord.size();
			if (Integer.parseInt(colunaParaVerirficar) >= numColumns) {
				throw new ArrayIndexOutOfBoundsException("Erro na especificacao da coluna, use valores entre 0 e " + (numColumns - 1));
			}
		}
	}

}
