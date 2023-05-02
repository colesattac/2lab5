package kz.talipovsn.weather2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// ЧТЕНИЕ HTML-ДАННЫХ С WEB
public class HttpClient {

    // Чтение html-данных с web-адреса, при ошибке чтения результат null
    public static String getHTMLData(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            int response = connection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                StringBuilder data = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        data.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return data.toString();
            } else {
                return null;
            }
        } catch (Exception ignored) {
        } finally {
            connection.disconnect();
        }
        return null;
    }


    // Чтение bitmap-картинки с web-адреса, при ошибке чтения результат null
    public static Bitmap getHTMLImage(String url) {
        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            try (InputStream inputStream = connection.getInputStream()) {
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception ignored) {
            }
        } catch (IOException ignored) {
        }
        return bitmap;
    }

}
