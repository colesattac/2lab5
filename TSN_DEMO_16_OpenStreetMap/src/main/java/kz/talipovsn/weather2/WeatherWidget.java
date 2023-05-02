package kz.talipovsn.weather2;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Objects;


public class WeatherWidget extends AppWidgetProvider {

    public static String ACTION_WIDGET_ACTIVATE = "ActionReceiverActivate";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // Проверяем есть ли соединения с сетью интернет
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }

        // Обновляем все виджеты разом, для которых настало время обновления, если есть Интернет
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            new WidgetData(context).execute("Pavlodar, KZ", "ru");
        }

    }

    private class WidgetData extends AsyncTask<String, Void, Weather> {
        Context context;

        WidgetData(Context context) {
            this.context = context;
        }

        // Тут реализуем фоновую асинхронную загрузку данных, требующих много времени
        @Override
        protected Weather doInBackground(String... params) {
            return WeatherBuilder.buildWeather(params[0], params[1]);
        }

        // Тут реализуем что нужно сделать после окончания загрузки данных
        @Override
        protected void onPostExecute(final Weather weather) {
            super.onPostExecute(weather);

            String temperature = String.valueOf(weather.getTemperature());
            String time_temp_update = String.valueOf(weather.getDt());

            // Подключаемся к виджету
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

            // Устанавливаем данные в виджете
            remoteViews.setTextViewText(R.id.temperature, temperature + " \u2103" +
                    " \u2638" + Weather.getWindDirectionCode180(weather.getWind_deg()));
            remoteViews.setTextViewText(R.id.time_temp_update, time_temp_update);
            remoteViews.setImageViewBitmap(R.id.weather_icon, weather.getIconData());

            // Делаем обработчик нажатия на виджет для вызова окна программы
            Intent intentMainActivity = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE);
            remoteViews.setOnClickPendingIntent(R.id.widget_window, pendingIntent);

            // Делаем обработчик нажатия на картинку виджета для обновления
            Intent activeUpdate = new Intent(context, WeatherWidget.class);
            activeUpdate.setAction(ACTION_WIDGET_ACTIVATE);
            PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, activeUpdate, PendingIntent.FLAG_IMMUTABLE);
            remoteViews.setOnClickPendingIntent(R.id.weather_icon, actionPendingIntent);

            // Обновляем все виджеты разом
            ComponentName widgetComponents = new ComponentName(context.getPackageName(), WeatherWidget.class.getName());
            appWidgetManager.updateAppWidget(widgetComponents, remoteViews);
        }
    }

    // Прослушивание нажатий на виджет
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), ACTION_WIDGET_ACTIVATE)) {
            Toast.makeText(context, context.getText(R.string.обновление_виджетов), Toast.LENGTH_SHORT).show();
            updateAllWidgets(context, intent);
        } else {
            super.onReceive(context, intent);
        }
    }

    // Посылка команды обновления всех виджетов
    public static void updateAllWidgets(Context context, Intent intentWidget) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        intentWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context.getPackageName(), WeatherWidget.class.getName()));
        intentWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
        context.sendBroadcast(intentWidget);
    }

}

