package br.com.XPTO.cidades.dto;


public class NomeCidadeDTO implements Comparable<NomeCidadeDTO> {
	
	private String nomeCidade;
	private String ufSigla;
	
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

	@Override
    public int compareTo(NomeCidadeDTO outraCidade) {
        return this.nomeCidade.compareTo(outraCidade.getNomeCidade());
    }

}
