package br.com.XPTO.listaCidades.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import br.com.XPTO.listaCidades.entity.CidadeEntity;

public interface CidadeRepository extends CrudRepository<CidadeEntity, Long>{
	
	Collection<CidadeEntity> findByClassificacao(int classificacao);
	
	@Query(value = "SELECT id FROM Cidades")
	Collection<String> findAnyId();

	@Query(value = "SELECT max(codMunicipio) FROM Cidades")
	Integer getMaxCodMunicipio();
	
	@Query(value = "SELECT min(codMunicipio) FROM Cidades")
	Integer getMinCodMunicipio();
	
	Optional<CidadeEntity> findByCodMunicipio(Integer codMunicipio);
	
	@Query(value = "SELECT ufNome FROM Cidades")
	Collection<String> findAllUfNome();
	
	Collection<CidadeEntity> findByufSiglaIgnoreCase(String ufSigla);
	
	

}
