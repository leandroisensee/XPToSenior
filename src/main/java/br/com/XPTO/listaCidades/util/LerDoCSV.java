package br.com.XPTO.listaCidades.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.XPTO.listaCidades.entity.CidadeEntity;
import br.com.XPTO.listaCidades.repository.CidadeRepository;

@Component
public class LerDoCSV {
	
	@Autowired
	private CidadeRepository repository;
	
	String fileCSV = "municipios_brasileiros.csv"; 
	
	public void gravarCsvToRepository() throws FileNotFoundException {
		long ini = System.currentTimeMillis();
		Scanner scanner = new Scanner(new File(fileCSV), "windows-1252");
		while (scanner.hasNextLine()) {
			String linha = scanner.nextLine();
			Scanner linhaScanner = new Scanner(linha);
			linhaScanner.useLocale(Locale.US);
			linhaScanner.useDelimiter(",");

			CidadeEntity entity = new CidadeEntity();
			
			entity.setCodMunicipio(linhaScanner.nextInt());
			entity.setNomeCidade(linhaScanner.next());
			linhaScanner.next();
			entity.setUfSigla(linhaScanner.next());
			entity.setUfNome(linhaScanner.next());
			entity.setClassificacao(linhaScanner.nextInt());
			entity.setLatitude(linhaScanner.nextDouble());
			entity.setLongitude(linhaScanner.nextDouble());

			repository.save(entity);
			linhaScanner.close();
		}
		scanner.close();
		long fim = System.currentTimeMillis();
		System.out.println("Importação feita, levou " + (fim - ini) + " milissegundos");
	}
	
	public Collection<String> lerPorColuna(Integer coluna) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(fileCSV), "windows-1252");
		scanner.useLocale(Locale.US);
		Collection<String> listaColuna = new ArrayList<String>();
		String[] inArray = null;
		while (scanner.hasNextLine()) {
			String linha = scanner.nextLine();
			inArray = linha.split(",");
			try { 
				listaColuna.add(inArray[coluna]); 
		    } catch (ArrayIndexOutOfBoundsException e) { 
				break;
			}
		}
		scanner.close();
		return listaColuna;
	}
	
	public List<String> lerColunaProcurarDado(Integer coluna, String pesquisa) throws FileNotFoundException {
		if ((lerPorColuna(coluna).stream().filter(i -> i.contains(pesquisa)).collect(Collectors.toList())).isEmpty()) {
			throw new EntityNotFoundException("Não há resultados para a pesquisa do valor [" + pesquisa + "] na coluna " + coluna);
		}
			return lerPorColuna(coluna).stream().filter(i -> i.contains(pesquisa)).collect(Collectors.toList());
	}
	
	public String contarTotalRegistrosNaoRepetidos(Integer coluna) throws FileNotFoundException  {
		HashSet<String> toSet = new HashSet<String>(lerPorColuna(coluna));
		return "Total de: " + toSet.size() + " registros não repetidos na coluna selecionada";
	}
	
	public Integer contarTotalRegistros() throws FileNotFoundException  {
		Integer posicao = 0;
		Integer contador = 0;
		while (posicao <= 7) {
			contador += lerPorColuna(posicao).size();;
			posicao ++;
		}
		return contador;
	}
	
	
}
