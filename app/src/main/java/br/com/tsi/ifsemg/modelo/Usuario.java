package br.com.tsi.ifsemg.modelo;

import br.com.tsi.ifsemg.util.Recursos;

public class Usuario{

    private String id;
    private String nome;
    private long pontos;
    private long duracao;

    public Usuario(String nome, String email){
        this.nome = nome;
        //this.email = email;
    }

    public Usuario() {
        this.nome = this.id = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public long getPontos() {
        return pontos;
    }

    public void setPontos(long pontos) {
        this.pontos = pontos;
    }

    public long getDuracao() {
        return duracao;
    }

    public void setDuracao(long duracao) {
        this.duracao = duracao;
    }

    @Override
    public String toString() {
        return String.format("%s \n%s - %d", nome, Recursos.obterMinSegString(duracao), pontos);
    }

}
