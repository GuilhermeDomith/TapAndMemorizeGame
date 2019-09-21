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

import br.com.tsi.ifsemg.R;
import br.com.tsi.ifsemg.util.RankingListAdapter;
import br.com.tsi.ifsemg.bd.UsuarioDAO;
import br.com.tsi.ifsemg.modelo.Usuario;
import br.com.tsi.ifsemg.util.Recursos;

import java.util.List;

public class WinScreen extends Activity {
    private ListView pontuacao_list;
    private EditText titulo_win_screen;
    private UsuarioDAO usuarioDao;

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
                registrarNovaPontuacao(nome, pontuacao, duracao);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    private void registrarNovaPontuacao(String nome, long pontos, long duracao) {
        this.usuarioDao = new UsuarioDAO(this);
        Usuario usuario = new Usuario();
        usuario.setNome((nome.isEmpty())? "Anônimo" : nome);
        usuario.setPontos(pontos);
        usuario.setTempo(duracao);

        usuarioDao.insert(usuario);
        usuarioDao.close();
        exibirPontuacoes();
    }

    public void exibirPontuacoes(){
        this.usuarioDao = new UsuarioDAO(this);
        List<Usuario> usuarios = usuarioDao.all();

        RankingListAdapter rankingAdapter = new RankingListAdapter(this, 0 , usuarios);
        rankingAdapter.sort(new RankingListAdapter.RankingComparator());
        this.pontuacao_list.setAdapter(rankingAdapter);
        usuarioDao.close();
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