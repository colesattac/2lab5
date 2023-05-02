package kz.talipovsn.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Widget extends AppWidgetProvider {

    public static String ACTION_WIDGET_ACTIVATE = "ActionReceiverActivate";

    StringBuffer rates = new StringBuffer();

    @Override
    // Обнвление виджетов
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        new GetData(context).execute();
    }

    // Поток получения данных для виджета
    class GetData extends AsyncTask<Void, Void, Void> {
        Context context;

        GetData(Context context) {
            this.context = context;
        }

        @Override
        // Выполняем фоновую задачу потока
        protected Void doInBackground(Void... params) {
            rates.setLength(0);
            rates.append(RatesReader.getRatesData());
            return null;
        }

        @Override
        // Окончание фоновой задачи потока
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Подключаемся к виджету
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);

            // Устанавливаем данные в виджете
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String s = "Данные от " + dateFormat.format(date)+"\n\n";
            remoteViews.setTextViewText(R.id.rates, s + rates.toString());

            // Делаем обработчик нажатия на виджет для вызова окна программы
            Intent intentMainActivity = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_MUTABLE);
            remoteViews.setOnClickPendingIntent(R.id.rates, pendingIntent);

            // Делаем обработчик нажатия на виджет для обновления
            Intent activeUpdate = new Intent(context, Widget.class);
            activeUpdate.setAction(ACTION_WIDGET_ACTIVATE);
            PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, activeUpdate, PendingIntent.FLAG_MUTABLE);
            remoteViews.setOnClickPendingIntent(R.id.widget_window, actionPendingIntent);

            // Обновляем все виджеты разом
            ComponentName widgetComponents = new ComponentName(context.getPackageName(), Widget.class.getName());
            appWidgetManager.updateAppWidget(widgetComponents, remoteViews);
        }
    }

    // Прослушивание нажатий на виджет
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), ACTION_WIDGET_ACTIVATE)) {
            updateAllWidgets(context, intent);
        } else {
            super.onReceive(context, intent);
        }
    }

    // Посылка команды обновления всех виджетов
    public static void updateAllWidgets(Context context, Intent intentWidget) {
        Toast.makeText(context, "Обновление виджетов", Toast.LENGTH_SHORT).show();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        intentWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context.getPackageName(),
            Widget.class.getName()));
        intentWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
        context.sendBroadcast(intentWidget);
    }
}
