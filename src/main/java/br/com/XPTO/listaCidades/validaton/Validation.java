package br.com.XPTO.listaCidades.validaton;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.XPTO.listaCidades.util.LerDoCSV;

@Component
public class Validation {
	
	@Autowired
	private LerDoCSV csv;
	
	public void validaInteiro(String stringParaVerirficar) throws IllegalArgumentException{
		try { 
	        Integer.parseInt(stringParaVerirficar); 
	    } catch(NumberFormatException e) { 
			throw new IllegalArgumentException("Erro, use apenas numeros inteiros");
		}
	}
	
	public void validaColuna(Integer colunaParaVerirficar) throws ArrayIndexOutOfBoundsException, FileNotFoundException{
		if (csv.lerPorColuna(colunaParaVerirficar).isEmpty()) {
			throw new ArrayIndexOutOfBoundsException("Erro na especificacao da coluna, use valores entre 0 e 7");
		}
	}
    
}
