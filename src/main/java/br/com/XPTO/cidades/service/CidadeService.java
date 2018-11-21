package br.com.XPTO.cidades.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.XPTO.cidades.dto.CidadeDTO;
import br.com.XPTO.cidades.dto.NomeCidadeDTO;
import br.com.XPTO.cidades.entity.CidadeEntity;
import br.com.XPTO.cidades.repository.CidadeRepository;
import br.com.XPTO.cidades.validaton.Validation;

@Service
public class CidadeService {

	@Autowired
	private CidadeRepository repository;

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private Validation validation;
	
	public Integer solicitarMaxCodMunicipal() {
		return repository.getMaxCodMunicipio();
	}
	
	public Integer solicitarMinCodMunicipal() {
		return repository.getMinCodMunicipio();
	}
	
	public List<NomeCidadeDTO> buscarCidadePorClassificacao() {
		validation.validarBanco();
		int classificacao = 1; // Parametro fixado para atender ao requisito
		return repository.findByClassificacao(classificacao).stream()
				.map(entity -> modelMapper.map(entity, NomeCidadeDTO.class)).collect(Collectors.toList());
	}

	public TreeMap<Long, String> gerarMapDeEstadoComFrequencia() {
		validation.validarBanco();
		Map<String, Long> mapUfComFrequencia = Stream.of(repository.findAllUfNome().toArray(new String[] {}))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		TreeMap<Long, String> mapUfComFrequenciaInvertido = new TreeMap<Long, String>();
		for (String i : mapUfComFrequencia.keySet())
			mapUfComFrequenciaInvertido.put(mapUfComFrequencia.get(i), i);
		return mapUfComFrequenciaInvertido;
	}
	
	public String gerarMapDeEstadoComFrequenciaTextoLimites() {
		return "Estado com Maior numero de cidades: "
				+ gerarMapDeEstadoComFrequencia().get(gerarMapDeEstadoComFrequencia().lastKey())
				+ " com " + gerarMapDeEstadoComFrequencia().lastKey() + " cidades" + System.lineSeparator()
				+ "Estado com Menor numero de cidades: "
				+ gerarMapDeEstadoComFrequencia().get(gerarMapDeEstadoComFrequencia().firstKey())
				+ " com " + gerarMapDeEstadoComFrequencia().firstKey() + " cidade";
	}

	public CidadeDTO buscarCidadePorcodMunicipio(String codMunicipio) {
		validation.validarBanco();
		validation.validaInteiro(codMunicipio);
		Optional<CidadeEntity> optional = repository.findByCodMunicipio(Integer.parseInt(codMunicipio));
		if (optional.isPresent()) {
			return modelMapper.map(optional.get(), CidadeDTO.class);
		}
		throw new EntityNotFoundException("Erro, tente valores entre "
				+ solicitarMinCodMunicipal() + " e " + solicitarMaxCodMunicipal());
	}
	
	public List<NomeCidadeDTO> buscarCidadePorUf(String ufSigla) {
		validation.validarBanco();
		Collection<CidadeEntity> validaBuscaCidadePorUf = repository.findByufSiglaIgnoreCase(ufSigla);
		if (validaBuscaCidadePorUf.isEmpty()) {
			throw new EntityNotFoundException("Valor inválido, utilize a sigla de duas sílabas relativas a unidade federativa desejada");
		}
		return validaBuscaCidadePorUf.stream().map(entity -> modelMapper.map(entity, NomeCidadeDTO.class))
				.collect(Collectors.toList());
	}

	public CidadeDTO criarCidade(CidadeDTO dto) {
		CidadeEntity cidadeEntity = modelMapper.map(dto, CidadeEntity.class);
		CidadeEntity cidadeCriada = repository.save(cidadeEntity);
		return modelMapper.map(cidadeCriada, CidadeDTO.class);
	}

	public CidadeDTO deletarCidade(String cidadeId) {
		validation.validaInteiro(cidadeId);
		Optional<CidadeEntity> optional = repository.findById(Long.parseLong(cidadeId));
		if (!optional.isPresent()) {
			throw new EntityNotFoundException("ID " + cidadeId + " não está cadastrado");
		}
		repository.deleteById(Long.parseLong(cidadeId));
		return modelMapper.map(optional.get(), CidadeDTO.class);	
	}
	
}
