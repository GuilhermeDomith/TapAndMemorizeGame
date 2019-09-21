package br.com.tsi.ifsemg.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import br.com.tsi.ifsemg.R;
import br.com.tsi.ifsemg.activity.MainActivity;

import java.util.Calendar;
import java.util.Locale;

public abstract class Recursos {

    public static void executarSom(Context context, int soundId) {
        MediaPlayer mp = MediaPlayer.create(context, soundId);
        mp.start();
    }

    public static boolean vibrar(Context context, int milliseconds){
        Vibrator rr = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        try{
            rr.vibrate(milliseconds);
            return true;
        }catch (NullPointerException e){
            Log.d("debug", "Erro ao acionar sensor de vibração");
            return false;
        }
    }

    public static void sleep(int min){
        SystemClock.sleep(min * 1000);
    }

    public static String obterMinSegString(long timeInMillis){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);

        return  String.format(Locale.ENGLISH, "%02d:%02d",c.get(Calendar.MINUTE),c.get(Calendar.SECOND));
    }

    public static void notificacao(Context context, int noficationId, String texto){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("Jogo Memorização");
        builder.setContentText(texto);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.mipmap.ic_trophy);
        builder.setVibrate(new long[]{0, 1000, 1000, 1000});
        builder.setLights(Color.RED, 3000, 2000);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setNumber(3);

        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resulPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT
        );

        builder.setContentIntent(resulPendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                context.NOTIFICATION_SERVICE
        );

        notificationManager.notify(noficationId, builder.build());
    }

}
