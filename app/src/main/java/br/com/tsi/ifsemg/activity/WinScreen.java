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
        this.usuarioDao = new UsuarioDAO(this);
        long pontuacao = getIntent().getLongExtra("pontuacao", 0);
        long duracao = getIntent().getLongExtra("duracao", 0);

        if(duracao > 0)
            registrarNovaPontuacao(pontuacao, duracao);
        exibirPontuacoes();
    }

    public boolean registrarNovaPontuacao(final long pontuacao, final long duracao){
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
                        String nome_usuario = input.getText().toString();
                        if(nome_usuario.isEmpty())
                            nome_usuario = "Anônimo";
                        // Obtem nome do usuário
                        Usuario user = new Usuario(
                                nome_usuario,
                                "joao@email.com"
                        );
                        user.setPontos(pontuacao);
                        user.setTempo(duracao);
                        usuarioDao.insert(user);

                        exibirPontuacoes();
                        usuarioDao.close();
                        //grava no banco de dados
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    public void exibirPontuacoes(){
        // Obtem as pontuacoes do banco de dados

        //for(Usuario u : usuarios){
        //    Log.d("Usuario", u.getNome());
        //}

        //List<String> opcoes = new ArrayList<>();
        List<Usuario> usuarios = usuarioDao.all();
        //ArrayAdapter<Usuario> adaptador = new ArrayAdapter(this, android.R.layout.simple_list_item_1, usuarios);
        RankingListAdapter rankingAdapter = new RankingListAdapter(this, 0 , usuarios);
        rankingAdapter.sort(new RankingListAdapter.RankingComparator());
        this.pontuacao_list.setAdapter(rankingAdapter);

        //this.pontuacao_list.setOnItemClickListener(new ClickList());
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
