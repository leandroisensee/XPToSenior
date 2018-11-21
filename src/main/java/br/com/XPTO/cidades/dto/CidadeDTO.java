package br.com.XPTO.cidades.dto;

public class CidadeDTO {
	
	private Long id;
	
	private int codMunicipio;
	private String nomeCidade;
	private String ufSigla;
	private String ufNome;
	private int classificacao;
	public Double latitude;
	public Double longitude;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getCodMunicipio() {
		return codMunicipio;
	}
	public void setCodMunicipio(int codMunicipio) {
		this.codMunicipio = codMunicipio;
	}
	public String getNomeCidade() {
		return nomeCidade;
	}
	public void setNomeCidade(String nomeCidade) {
		this.nomeCidade = nomeCidade;
	}
	public String getUfSigla() {
		return ufSigla;
	}
	public void setUfSigla(String ufSigla) {
		this.ufSigla = ufSigla;
	}
	public String getUfNome() {
		return ufNome;
	}
	public void setUfNome(String ufNome) {
		this.ufNome = ufNome;
	}
	public int getClassificacao() {
		return classificacao;
	}
	public void setClassificacao(int classificacao) {
		this.classificacao = classificacao;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	
	


}
