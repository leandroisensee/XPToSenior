package br.com.XPTO.listaCidades.service;

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

import br.com.XPTO.listaCidades.dto.CidadeDTO;
import br.com.XPTO.listaCidades.dto.NomeCidadeDTO;
import br.com.XPTO.listaCidades.entity.CidadeEntity;
import br.com.XPTO.listaCidades.repository.CidadeRepository;

@Service
public class CidadeService {

	@Autowired
	private CidadeRepository repository;

	@Autowired
	private ModelMapper modelMapper;
	
	public void validarBanco() throws EntityNotFoundException {
		Collection<String> teste = repository.findAnyId();
		if (teste.isEmpty()) {
			throw new EntityNotFoundException("Banco de dados está vazio, inicie pela task1");
		}
	}

	public Integer solicitarMaxCodMunicipal() {
		return repository.getMaxCodMunicipio();
	}
	
	public Integer solicitarMinCodMunicipal() {
		return repository.getMinCodMunicipio();
	}
	
	public List<NomeCidadeDTO> buscarCidadePorClassificacao(int classificacao) {
		return repository.findByClassificacao(classificacao).stream()
				.map(entity -> modelMapper.map(entity, NomeCidadeDTO.class)).collect(Collectors.toList());
	}

	public TreeMap<Long, String> gerarMapDeEstadoComFrequencia() {
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

	public CidadeDTO buscarCidadePorcodMunicipio(Integer codMunicipio) {
		Optional<CidadeEntity> optional = repository.findByCodMunicipio(codMunicipio);
		if (optional.isPresent()) {
			return modelMapper.map(optional.get(), CidadeDTO.class);
		}
		throw new EntityNotFoundException("Erro, tente valores entre "
				+ solicitarMinCodMunicipal() + " e " + solicitarMaxCodMunicipal());
	}
	
	public List<NomeCidadeDTO> buscarCidadePorUf(String ufSigla) {
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

	public CidadeDTO deletarCidade(Long cidadeId) {
		Optional<CidadeEntity> optional = repository.findById(cidadeId);
		if (!optional.isPresent()) {
			throw new EntityNotFoundException("ID " + cidadeId + " não está cadastrado");
		}
		repository.deleteById(cidadeId);
		return modelMapper.map(optional.get(), CidadeDTO.class);	
	}

}
