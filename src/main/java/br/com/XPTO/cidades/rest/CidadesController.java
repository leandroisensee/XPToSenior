package br.com.XPTO.cidades.rest;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.XPTO.cidades.dto.CidadeDTO;
import br.com.XPTO.cidades.dto.DistanciaCidadeDTO;
import br.com.XPTO.cidades.dto.NomeCidadeDTO;
import br.com.XPTO.cidades.service.CidadeService;
import br.com.XPTO.cidades.util.LerDoCSV;

@RestController
@RequestMapping(value = "/api")
public class CidadesController {

	@Autowired
	private CidadeService service;
	
	@Autowired
	private LerDoCSV lerCSV;

	@GetMapping("/task1") // Ler o arquivo CSV das cidades para a base de dados
	public ResponseEntity<String> persistindoDados() throws IOException {
		lerCSV.gravarCsvToRepository();
		return new ResponseEntity<String>("Importação do CSV concluida", HttpStatus.OK);
	}

	@GetMapping("/task2") // Retornar somente as cidades que são capitais ordenadas por nome
	public ResponseEntity<List<NomeCidadeDTO>> buscarCidadePorClassificacao() {
		List<NomeCidadeDTO> list = service.buscarCidadePorClassificacao();
		return new ResponseEntity<List<NomeCidadeDTO>>(list, HttpStatus.OK);
	}

	@GetMapping("/task3") // Retornar o nome do estado com a maior e menor quantidade de cidades e a
							// quantidade de cidades
	public ResponseEntity<String> listaEstadosPorQuantidadeDeCidadesMaiorEMenor() {
		return new ResponseEntity<String>(service.gerarMapDeEstadoComFrequenciaTextoLimites(), HttpStatus.OK);
	}

	@GetMapping("/task4") // Retornar a quantidade de cidades por estado
	public ResponseEntity<TreeMap<Long, String>> listaEstadosPorQuantidadeDeCidades() {
		return new ResponseEntity<TreeMap<Long, String>>(service.gerarMapDeEstadoComFrequencia(), HttpStatus.OK);
	}

	@GetMapping("/task5/{codMunicipio}") // Obter os dados da cidade informando o id do IBGE
											// Porém como o id do IBGE está diferente em cada tabela que encontrei,
											// optei
											// por usar o codigo do municipio
	public ResponseEntity<CidadeDTO> listaCidadePorCodMunicipio(@PathVariable String codMunicipio) {
		return new ResponseEntity<CidadeDTO>(service.buscarCidadePorcodMunicipio(codMunicipio), HttpStatus.OK);
	}

	@GetMapping("/task6/{ufSigla}") // Retornar o nome das cidades baseado em um estado selecionado;
	public ResponseEntity<List<NomeCidadeDTO>> ListaCidadeUnidadeFederativa(@PathVariable String ufSigla) {
		return new ResponseEntity<List<NomeCidadeDTO>>(service.buscarCidadePorUf(ufSigla), HttpStatus.OK);
	}

	@PostMapping("/task7/criar") // Permitir adicionar uma nova Cidade;
	public ResponseEntity<CidadeDTO> cadastrarCidade(@RequestBody CidadeDTO dto) {
		CidadeDTO cidadeCadastrada = service.criarCidade(dto);
		return new ResponseEntity<CidadeDTO>(cidadeCadastrada, HttpStatus.OK);
	}

	@DeleteMapping("/task8/deletar/{id}")
	public ResponseEntity<String> apagarCidade(@PathVariable String id) {
		return new ResponseEntity<String>("Cidade " + service.deletarCidade(id).getNomeCidade() + " excluida",
				HttpStatus.OK);
	}

	@RequestMapping(value = "/task9/{coluna}/{pesquisa}") // Permitir selecionar uma coluna (do CSV) e através dela
															// entrar com uma string para filtrar.
	public ResponseEntity<List<String>> listarColunaPorPesquisa(@PathVariable String coluna,
			@PathVariable String pesquisa) throws IOException {
		return new ResponseEntity<List<String>>(lerCSV.lerColunaProcurarDado(coluna, pesquisa), HttpStatus.OK);
	}

	@RequestMapping(value = "/task10/{coluna}") // Retornar a quantidade de registro baseado em uma coluna. Não deve
												// contar itens iguais
	public ResponseEntity<String> listarColuna(@PathVariable String coluna) throws IOException {
		return new ResponseEntity<String>(lerCSV.contarTotalRegistrosNaoRepetidos(coluna), HttpStatus.OK);
	}

	@RequestMapping(value = "/task11") // Retornar a quantidade de registros total;
	public ResponseEntity<String> contarTotalDados() throws IOException {
		return new ResponseEntity<String>("Total de: " + lerCSV.contarTotalRegistros() + " registros", HttpStatus.OK);
	}


	@RequestMapping(value = "/task12") // Dentre todas as cidades, obter as duas cidades mais distantes uma da outra
										// com base na localização (distância em KM em linha reta);
	public ResponseEntity<DistanciaCidadeDTO> teste6() throws IOException {
		return new ResponseEntity<DistanciaCidadeDTO>(lerCSV.distanciaMaxima(), HttpStatus.OK);
	}
}
