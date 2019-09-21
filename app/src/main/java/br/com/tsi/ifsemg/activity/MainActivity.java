package br.com.tsi.ifsemg.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import br.com.tsi.ifsemg.R;
import br.com.tsi.ifsemg.util.Recursos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends Activity{
    private final Integer NUMEROS_POSSIVEIS[] = {1, 2, 3, 4, 5, 6};
    private final int PONTUACAO_MAXIMA = 100000;
    private final int PESO_SUBTRAIR_POR_CLICK = 655;
    private final int PESO_SUBTRAIR_POR_SEGUNDO = 655;

    private List<Integer> sequencia_da_partida;
    private LinkedList<Integer> sequencia_restante;
    private List<Button> botoes_clicados;

    private ProgressBar progress_bar;
    private TextView duracao_text;
    private ConstraintLayout back_layout;
    private Drawable back_color_default;

    private int contador_click;
    private HandlerDuracao handler_duracao;


    public void iniciarJogo(){
        this.back_layout.setBackground(this.back_color_default);
        this.duracao_text.setText(R.string.duracao_inicial);
        this.contador_click = 0;

        iniciarNovaSequencia();
        limparProgresso();
    }

    public void reiniciarJogo(){
        this.handler_duracao.deleteRunnable();
        iniciarJogo();
        Toast.makeText(this, "O jogo foi reiniciado!", Toast.LENGTH_LONG).show();
        Recursos.notificacao(this, 12, "O Jogo foi reiniciado");
    }

    public void jogarNovamente(){
        Recursos.vibrar(this, 30);
        this.back_layout.setBackground(this.back_color_default);
        limparProgresso();
    }

    public void jogoVencido() {
        long duracao = handler_duracao.getDuracao();
        long pontos = obterPontuacao(duracao);
        this.handler_duracao.deleteRunnable();

        Intent intent = new Intent(this, WinScreen.class);
        intent.putExtra("pontuacao", pontos);
        intent.putExtra("duracao", duracao);
        startActivity(intent);

        Recursos.executarSom(this, R.raw.sound_win);
        iniciarJogo();
    }

    private long obterPontuacao(long duracao) {
        long descontar = (this.contador_click - this.NUMEROS_POSSIVEIS.length) * this.PESO_SUBTRAIR_POR_CLICK;
        descontar += (duracao / 1000) * PESO_SUBTRAIR_POR_SEGUNDO;
        long pontos = PONTUACAO_MAXIMA - descontar;
        return (pontos < 0)? 0 : pontos;
    }

    private void numeroClicado(Button numero_btn) {
        if(this.contador_click == 0)
            // Inicia o cronômetro
            this.handler_duracao = new HandlerDuracao(this.duracao_text);

        this.contador_click++;
        Button btn = (Button) numero_btn;
        int numero = Integer.parseInt(btn.getText().toString());

        if(!isNumeroSeguinte(numero)) {
            jogarNovamente();
            return;
        }

        btn.setVisibility(View.INVISIBLE);
        this.botoes_clicados.add(btn);
        executarSomNumeroClicado(numero);
        this.back_layout.setBackground(btn.getBackground());

        float progresso = ((float) this.botoes_clicados.size()) /this.NUMEROS_POSSIVEIS.length * 100;
        this.progress_bar.setProgress((int) progresso);

        if(isJogoTerminado()) jogoVencido();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        this.botoes_clicados = new ArrayList<>();
        this.back_layout = (ConstraintLayout) findViewById(R.id.back_layout);
        this.duracao_text = (TextView) findViewById(R.id.duracao_text);
        this.back_color_default = this.back_layout.getBackground();
        this.contador_click = 0;

        iniciarJogo();
    }

    private void executarSomNumeroClicado(int numero_clicado) {
        switch (numero_clicado){
            case 1: Recursos.executarSom(this, R.raw.sound_btn_1); break;
            case 2: Recursos.executarSom(this, R.raw.sound_btn_2); break;
            case 3: Recursos.executarSom(this, R.raw.sound_btn_3); break;
            case 4: Recursos.executarSom(this, R.raw.sound_btn_4); break;
            case 5: Recursos.executarSom(this, R.raw.sound_btn_5); break;
            case 6: Recursos.executarSom(this, R.raw.sound_btn_7); break;
        }
    }

    private void iniciarNovaSequencia(){
        this.sequencia_da_partida = new ArrayList<>(Arrays.asList(this.NUMEROS_POSSIVEIS));
        Collections.shuffle(this.sequencia_da_partida);

        Log.d("sequencia", this.sequencia_da_partida.toString());
    }

    private void limparProgresso(){
        if(this.botoes_clicados != null)
            for(Button btn : this.botoes_clicados){
                btn.setVisibility(View.VISIBLE);
            }

        this.progress_bar.setProgress(0);
        this.sequencia_restante = new LinkedList<>(this.sequencia_da_partida);
        this.botoes_clicados = new ArrayList<>();
    }

    private boolean isJogoTerminado() {
        return this.sequencia_restante.isEmpty();
    }

    private boolean isNumeroSeguinte(int numero){
        int numero_seguinte = this.sequencia_restante.pollFirst();
        return numero == numero_seguinte;
    }

    public void btnReiniciarJogoClick(View reiniciar_btn){
        reiniciarJogo();
    }
    public void btnNumeroClick(View numero_btn){
        numeroClicado((Button) numero_btn);
    }
    public void btnRankingClick(View ranking_button){
        Intent intent = new Intent(this, WinScreen.class);
        startActivity(intent);
    }

    private static class HandlerDuracao extends Handler{

        private Calendar hora_inicio;
        private Runnable atualiza_hora_run;
        private TextView text_update;
        private long duracao;

        public HandlerDuracao(TextView text_update){
            super();
            this.hora_inicio = Calendar.getInstance();
            this.atualiza_hora_run = getRunnable();
            this.text_update = text_update;
            this.post(this.atualiza_hora_run);
        }

        public void deleteRunnable(){
            this.removeCallbacks(this.atualiza_hora_run);
        }

        public Runnable getRunnable(){
            return new Runnable() {
                @Override
                public void run() {
                    duracao = Calendar.getInstance().getTimeInMillis() - hora_inicio.getTimeInMillis();
                    String duracao_string = Recursos.obterMinSegString(duracao);

                    HandlerDuracao.this.text_update.setText(duracao_string);
                    HandlerDuracao.this.postDelayed(this, 100);
                }
            };
        }

        public Calendar getHoraInicio() {
            return hora_inicio;
        }

        public void setHoraInicio(Calendar hora_inicio) {
            this.hora_inicio = hora_inicio;
        }

        public long getDuracao() {
            return duracao;
        }

        public void setDuracao(long duracao) {
            this.duracao = duracao;
        }
    }

}