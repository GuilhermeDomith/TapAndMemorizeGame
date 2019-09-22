package br.com.tsi.ifsemg.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.tsi.ifsemg.activity.MainActivity;
import br.com.tsi.ifsemg.bd.Database;
import br.com.tsi.ifsemg.modelo.Usuario;
import br.com.tsi.ifsemg.util.Recursos;

public class CheckScore extends Service {

    private DatabaseReference reference;
    private ValueEventListener listener;
    private String bestScoreId;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Serviço inicializado", Toast.LENGTH_SHORT).show();

        Database.getValue("bestscore", String.class, new Database.GetObjectListener<String>() {
            @Override
            public void getObject(String bestScoreId, DatabaseReference reference, ValueEventListener listener) {
                // Evita a notificação quando o serviço é criado
                if (bestScoreId != null && CheckScore.this.bestScoreId != null)
                    if (!CheckScore.this.bestScoreId.equals(bestScoreId))

                    Database.getValue(String.format("score/%s", bestScoreId), Usuario.class, new Database.GetObjectListener<Usuario>() {
                        @Override
                        public void getObject(Usuario userBestScore, DatabaseReference reference, ValueEventListener listener) {
                            reference.removeEventListener(listener);
                            Recursos.notificacao(
                                    getApplicationContext(), 23,
                                    String.format("O jogador %S bateu o recorde: %d Pts!", userBestScore.getNome(), userBestScore.getPontos())
                            );
                        }
                });

                CheckScore.this.reference = reference;
                CheckScore.this.listener = listener;
                CheckScore.this.bestScoreId = (bestScoreId == null)? "" : bestScoreId;
            }
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Serviço finalizado", Toast.LENGTH_SHORT).show();
        reference.removeEventListener(listener);
        super.onDestroy();
    }
}
