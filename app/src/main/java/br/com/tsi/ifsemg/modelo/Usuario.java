package br.com.tsi.ifsemg.modelo;

import br.com.tsi.ifsemg.util.Recursos;

public class Usuario{

    private String nome, email;
    private long pontos;
    private long tempo;

    public Usuario(String nome, String email){
        this.nome = nome;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPontos() {
        return pontos;
    }

    public void setPontos(long pontos) {
        this.pontos = pontos;
    }

    public long getTempo() {
        return tempo;
    }

    public void setTempo(long tempo) {
        this.tempo = tempo;
    }

    @Override
    public String toString() {
        return String.format("%s \n%s - %d", nome, Recursos.obterMinSegString(tempo), pontos);
    }

}
