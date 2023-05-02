package kz.talipovsn.widget;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// СОЗДАТЕЛЬ КОТИРОВОК ВАЛЮТ
public class RatesReader {

    private static final String BASE_URL = "https://github.com/proffix4?tab=repositories";

    // Парсинг котировок из формата html web-страницы банка, при ошибке доступа возвращаем null
    public static String getRatesData() {
        StringBuilder data = new StringBuilder();
        try {
            Document doc = Jsoup.connect(BASE_URL).timeout(5000).get();

            Elements layout = doc.select("div.Layout");
            Elements author = doc.select("span.p-name");
            Elements nickname = doc.select("span.p-nickname");
            Elements layoutMain = layout.select("div.Layout-main");

            Elements o = layoutMain.select("h3.wb-break-all");
            Elements mOne = layoutMain.select("div.f6.color-fg-muted.mt-2");
            Elements mTwo = mOne.select("span.ml-0.mr-3");
            Elements time = mOne.select("relative-time.no-wrap");

            Element repoName = o.get(0);
            Element repoLang = mTwo.get(0);
            Element repoUpdated = time.get(0);
            Element authorName = author.get(0);
            Element authorNick = nickname.get(0);
            int i = 0;

            for (Element rowAuthor : authorName.select("span")) {
                data.append(rowAuthor.text());
                data.append("\n");
            }
            for (Element rowNick : authorNick.select("span")) {
                data.append(rowNick.text());
                data.append("\n");
            }
            data.append("Repositories" + ":\n");
            data.append("\n");

            for (Element rowName : repoName.select("a")) {
                data.append(rowName.text());
                data.append("\n");
            }
            for (Element rowLang : repoLang.select("span[itemprop]")) {
                data.append("Язык: " + rowLang.text());
                data.append("\n");
            }
            for (Element rowUpd : repoUpdated.select("relative-time")) {
                data.append("Последнее обновление: " + rowUpd.text());
                data.append("\n");
            }


        } catch (Exception ignored) {
            return null; // При ошибке доступа возвращаем null
        }
        return data.toString().trim(); // Возвращаем результат
    }

}
