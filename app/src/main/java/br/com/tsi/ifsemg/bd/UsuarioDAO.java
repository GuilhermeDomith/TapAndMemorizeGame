package br.com.tsi.ifsemg.bd;

import android.content.Context;
import android.database.Cursor;

import br.com.tsi.ifsemg.modelo.Usuario;

import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO extends Database<Usuario>{
    private static final String SQL_STRUCT =
            "CREATE TABLE IF NOT EXISTS usuario (" +
            "id_ INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nome TEXT NOT NULL," +
            "email TEXT NOT NULL," +
            "pontos LONG," +
            "tempo LONG )";

    private static final String SQL_INSERT= "INSERT INTO usuario (nome, email, pontos, tempo)  VALUES ('%s', '%s', '%d', '%d')";
    private static final String SQL_SELECT_ALL= "SELECT * FROM usuario ORDER BY nome";

    public UsuarioDAO(Context context) {
        super(context, "usuario", SQL_STRUCT);
    }

    public boolean insert(Usuario usuario){
        String query = String.format(SQL_INSERT,
                usuario.getNome(), usuario.getNome(),
                usuario.getPontos(), usuario.getTempo());
        database.execSQL(query);
        return true;
    }

    public List<Usuario> all(){
        List<Usuario> usuarios = new ArrayList<>();
        Usuario usuario;

        Cursor cursor = database.rawQuery(SQL_SELECT_ALL, null);
        int indexID, indexNome, indexEmail, indexDuracao, indexPontos;

        if(cursor.moveToFirst()){
            indexID = cursor.getColumnIndex("id_");
            indexNome = cursor.getColumnIndex("nome");
            indexEmail = cursor.getColumnIndex("email");
            indexDuracao =cursor.getColumnIndex("tempo");;
            indexPontos = cursor.getColumnIndex("pontos");;

            do{
                usuario = new Usuario(cursor.getString(indexNome), cursor.getString(indexEmail));
                usuario.setTempo(cursor.getLong(indexDuracao));
                usuario.setPontos(cursor.getLong(indexPontos));
                usuarios.add(usuario);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return usuarios;
    }
}
