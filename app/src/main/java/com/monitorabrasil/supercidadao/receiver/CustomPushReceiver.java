package com.monitorabrasil.supercidadao.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.monitorabrasil.supercidadao.views.MainActivity;
import com.monitorabrasil.supercidadao.util.NotificationUtils;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by geral_000 on 06/02/2016.
 */
public class CustomPushReceiver extends ParsePushBroadcastReceiver {
    private final String TAG = CustomPushReceiver.class.getSimpleName();


    private NotificationUtils notificationUtils;


    private Intent parseIntent;


    public CustomPushReceiver() {
        super();
    }


    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);


        if (intent == null)
            return;


        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));


            Log.e(TAG, "Push received: " + json);


            parseIntent = intent;


            parsePushJson(context, json);


        } catch (JSONException e) {
            Log.e(TAG, "Push message json exception: " + e.getMessage());
        }
    }


    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }


    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }


    /**
     * Parses the push notification json
     *
     * @param context
     * @param json
     */
    private void parsePushJson(Context context, JSONObject json) {
        String idPergunta = null;
        String idTema = null;
        int idProjeto = 0;
        String casa = null;
        try {
            boolean isBackground = json.getBoolean("is_background");
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("titulo");
            String message = data.getString("alerta");
            String pushTipo = data.getString("tipo");
            switch (pushTipo){
                case "dialoga":
                    idPergunta = data.getString("pergunta");
                    idTema = data.getString("idTema");
                    break;
                case "projeto":
                    idProjeto = data.getInt("idProjeto");
                    casa = data.getString("casa");
                    break;
            }
            if (!isBackground) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("idPergunta", idPergunta);
                intent.putExtra("idTema", idTema);
                intent.putExtra("tipo", pushTipo);
//                intent.putExtra(ProjetoDetailFragment.ARG_ITEM_ID, idProjeto);
//                intent.putExtra(ProjetoDetailFragment.ARG_CASA, casa);
                showNotificationMessage(context, title, message, intent);
            }


        } catch (JSONException e) {
            Log.e(TAG, "Push message json exception: " + e.getMessage());
        }
    }




    /**
     * Shows the notification message in the notification bar
     * If the app is in background, launches the app
     *  @param context
     * @param title
     * @param message
     * @param intent
     */
    private void showNotificationMessage(Context context,String title, String message, Intent intent) {


        notificationUtils = new NotificationUtils(context);


        intent.putExtras(parseIntent.getExtras());


        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


        notificationUtils.showNotificationMessage(title, message, intent);
    }
}