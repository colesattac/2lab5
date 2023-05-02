package kz.talipovsn.widget;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static kz.talipovsn.widget.Widget.updateAllWidgets;

public class MainActivity extends AppCompatActivity {

    TextView textView_sum; // Переменная для доступа к компоненту со значением результата
    Button buttonSum; // Переменная для доступа к компоненту кнопки

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Доступ к компонентам окна
        textView_sum = findViewById(R.id.textView_sum);
        buttonSum = findViewById(R.id.buttonSum);

        updateAllWidgets(this,  new Intent(this, Widget.class)) ;
    }

    // МЕТОД КНОПКИ
    public void onClick(View v) {
        updateAllWidgets(this,  new Intent(this, Widget.class)) ;
    }

}
