package br.com.tsi.ifsemg.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.tsi.ifsemg.R;
import br.com.tsi.ifsemg.bd.Database;
import br.com.tsi.ifsemg.util.RankingListAdapter;
import br.com.tsi.ifsemg.modelo.Usuario;
import br.com.tsi.ifsemg.util.Recursos;

import java.util.List;
import java.util.UUID;

public class WinScreen extends Activity {
    private ListView pontuacao_list;
    private EditText titulo_win_screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_screen);

        this.pontuacao_list = (ListView) findViewById(R.id.pontuacao_list);
        long pontuacao = getIntent().getLongExtra("pontuacao", -1);
        long duracao = getIntent().getLongExtra("duracao", -1);

        if(duracao > -1)
            obterDadosJogador(pontuacao, duracao);
        exibirPontuacoes();
    }

    public boolean obterDadosJogador(final long pontuacao, final long duracao){
        // Cria a caixa de diálogo para obter o nome do usuário
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.forneca_nome_jogador_title);
        String mensagem = getResources().getString(R.string.forneca_nome_jogador);
        builder.setMessage(String.format(mensagem, pontuacao, Recursos.obterMinSegString(duracao)));

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String nome = input.getText().toString();
                Usuario usuario = registrarNovaPontuacao(nome, pontuacao, duracao);
                verificarEAtualizarBestScore(usuario);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    private Usuario registrarNovaPontuacao(String nome, long pontos, long duracao) {
        final Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID().toString());
        usuario.setNome(((nome.isEmpty())? "Anônimo" : nome).toUpperCase());
        usuario.setPontos(pontos);
        usuario.setDuracao(duracao);
        Database.insert(String.format("score/%s", usuario.getId()), usuario);
        return  usuario;
    }

    public void verificarEAtualizarBestScore(final Usuario userNewScore){
        Database.getValue("bestscore", String.class, new Database.GetObjectListener<String>() {
            @Override
            public void getObject(final String bestScoreId, DatabaseReference reference, ValueEventListener listener) {
                reference.removeEventListener(listener);

                if(bestScoreId == null) {
                    Database.insert("bestscore", userNewScore.getId());
                    return;
                }

                Database.getValue(String.format("score/%s", bestScoreId), Usuario.class, new Database.GetObjectListener<Usuario>() {
                    @Override
                    public void getObject(Usuario userBestScore, DatabaseReference reference, ValueEventListener listener) {
                        reference.removeEventListener(listener);

                        if(userNewScore.getPontos() > userBestScore.getPontos()){
                            Database.insert("bestscore", userNewScore.getId());
                            return;
                        }

                        if(userNewScore.getPontos() == userBestScore.getPontos()
                                && userNewScore.getDuracao() < userBestScore.getDuracao()){
                            Database.insert("bestscore", userNewScore.getId());
                            return;
                        }
                    }
                });
            }
        });
    }

    public void exibirPontuacoes(){
        Database.all("score", Usuario.class, new Database.ListObjectsListener<Usuario>() {
            @Override
            public void listObjects(List<Usuario> usuarios) {
                if(usuarios == null) return;
                RankingListAdapter rankingAdapter = new RankingListAdapter(
                        WinScreen.this, 0 , usuarios
                );
                rankingAdapter.sort(new RankingListAdapter.RankingComparator());
                pontuacao_list.setAdapter(rankingAdapter);
            }
        });
    }

    private class ClickList implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
            Toast.makeText(
                    getApplicationContext(),
                    "Item: " + parent.getAdapter().getItem(i),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

}