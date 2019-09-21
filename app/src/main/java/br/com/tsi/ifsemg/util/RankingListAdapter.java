package br.com.tsi.ifsemg.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.tsi.ifsemg.R;
import br.com.tsi.ifsemg.modelo.Usuario;

import java.util.Comparator;
import java.util.List;

public class RankingListAdapter extends ArrayAdapter<Usuario> {

    private Context context;
    private List<Usuario> usuarios;

    public RankingListAdapter(Context context, int resource, List<Usuario> usuarios) {
        super(context, resource, usuarios);
        this.context = context;
        this.usuarios = usuarios;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Usuario u = this.usuarios.get(position);
        convertView = LayoutInflater.from(this.context).inflate(R.layout.item_ranking, null);

        ImageView image = (ImageView) convertView.findViewById(R.id.usuario_icon);
        image.setImageResource(R.mipmap.ic_trophy);

        TextView nomeUsuarioText = (TextView) convertView.findViewById(R.id.nome_usuario_text);
        nomeUsuarioText.setText(u.getNome());

        TextView pontuacaoText = (TextView) convertView.findViewById(R.id.pontos_text);
        pontuacaoText.setText(String.format(
                "%d Pts\n%s",
                u.getPontos(),
                Recursos.obterMinSegString(u.getTempo())
        ));

        return convertView;
    }

    public static class RankingComparator implements Comparator<Usuario>{
        @Override
        public int compare(Usuario u1, Usuario u2) {
            if(u1.getPontos() == u2.getPontos())
                return u1.getTempo() < u2.getTempo() ? -1 : 1 ;
            return  u1.getPontos() < u2.getPontos() ? 1 : -1 ;
        }
    }
}
