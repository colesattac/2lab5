package kz.talipovsn.weather2;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONObject;

import static kz.talipovsn.weather2.HttpClient.getHTMLData;
import static kz.talipovsn.weather2.HttpClient.getHTMLImage;

// СОЗДАТЕЛЬ ПОГОДЫ
public class WeatherBuilder {

    // Получение JSON html-данных погоды по городу и языку
    private static String getWeatherData(String location, String lang) {
        String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?units=metric";
        String API = "847ae35bc20ab748b0852e32c325cc6b";
        String url = BASE_URL + "&q=" + location + "&lang=" + lang + "&appid=" + API;
        return getHTMLData(url);
    }

    // Получение с web bitmap картинки погоды по ее коду
    private static Bitmap getWheaterImage(String code) {
        String IMG_URL = "http://openweathermap.org/img/w/";
        String url = IMG_URL + code + ".png";
        return getHTMLImage(url);
    }

    // Парсинг даты в формате JSON с созданием объекта погоды
    private static Weather dataParsing(String json) {
        Weather weather = new Weather();
        try {
            JSONObject _obj = new JSONObject(json);
            JSONObject _sys = _obj.getJSONObject("sys");
            JSONObject _wind = _obj.getJSONObject("wind");
            JSONArray _weather = _obj.getJSONArray("weather");
            JSONObject _weather0 = _weather.getJSONObject(0);
            JSONObject _main = _obj.getJSONObject("main");
            weather.setCountry(_sys.getString("country"));
            weather.setCity(_obj.getString("name"));
            weather.setDt(_obj.getLong("dt"));
            weather.setDescription(_weather0.getString("description"));
            weather.setCondition(_weather0.getString("main"));
            weather.setIconName(_weather0.getString("icon"));
            weather.setTemperature(_main.getDouble("temp"));
            weather.setWind_speed(_wind.getDouble("speed"));
            weather.setWind_deg(_wind.getDouble("deg"));
        } catch (Exception ignore) {
        }
        return weather;
    }

    // Получение готового объекта погоды по городу и языку
    public static Weather buildWeather (String location, String lang) {
        Weather weather = dataParsing(getWeatherData(location, lang));
        weather.setIconData(getWheaterImage(weather.getIconName()));
        return weather;
    }
}