package ru.webfester.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

/**
 *
 * @author Володин Антон
 */
public class HTMLParser {

    private TreeMap<String, String> CategoriesNew;
    private TreeMap<String, String> Categories;
    private ArrayList<YandexOffer> Offers;
    private SelectorElements selectorElements;
    private int id = 0;
    private ArrayList<String> tmpDoubleOffer;

    public HTMLParser(SelectorElements selectorElements) {
        this.Categories = new TreeMap<>();
        this.selectorElements = selectorElements;
        this.Offers = new ArrayList<>();
        tmpDoubleOffer = new ArrayList<>();
    }

    void addParse(String url, String projectDirectory) throws IOException {
        if (url.contains("khit-prodazh") || url.contains("namatrasniki")
                || url.contains("osnovaniya") || url.contains("chekhly")
                || url.contains("podushki")) {

            id++;
            System.out.println("Парсинг: " + url);

            String r = url.replace("http://", "").replace("https://", "").replace("/", File.separator);

            File e = new File(projectDirectory + File.separator + r);
            if (!e.exists()) {
                e.mkdirs();
            }

            Document sel = null;
            File pageFile = new File(e + File.separator + "page.html");
            if (!pageFile.exists()) {
                sel = Jsoup.connect(url).get();
                Utils.WriteFile(pageFile.getAbsolutePath(), sel.html());
                System.err.println("Загружено из интернета");
            } else {
                System.err.println("Загружено с памяти");
                sel = Jsoup.parse(Utils.ReadFile(pageFile));
            }

            String name = sel.select(selectorElements.nameText).text().split(" :: ")[0];

            if (!tmpDoubleOffer.contains(name)) {
                tmpDoubleOffer.add(name);

                String currencyId = selectorElements.currencyIdText;

                HashSet<String> arrPicture = new HashSet<>();
                for (int i = 0; i < sel.select(selectorElements.pictureText).size(); i++) {

                    String pi = sel.select(selectorElements.pictureText).eq(i).attr("href");

                    if (!pi.contains("http")) {
                        pi = "http://" + new URL(url).getHost() + pi;
                    }

                    if (!pi.contains("задать_вопрос")) {
                        arrPicture.add(pi);
                    }
//
//                    if (pi.contains("&image=")) {
//                        Document w = Jsoup.connect(pi).get();
//
//                        for (Element dad : w.select("a")) {
//                            arrPicture.add(dad.attr("href"));
//                        }
//                    } else {
//                        arrPicture.add(pi);
//                    }
//                    if (arrPicture.isEmpty()) {
//                        arrPicture.add(sel.select(selectorElements.pictureText).select("img").attr("src"));
//                    }

                    arrPicture.remove("");
                }

                String[] picture = new String[arrPicture.size()];

                picture = arrPicture.toArray(picture);

                String delivery = selectorElements.deliveryText;

                String vendor = "Luntek";//sel.select(selectorElements.vendorText).text();

                String categoryId = "";

                //  CategoriesNew.put(, vendor); // Добавляем главную категорию
                for (int ct = sel.select(selectorElements.categoryIdText).size(); ct > 0; ct--) {
                    //    categoryId = sel.select(selectorElements.categoryIdText).eq(ct).text() + ">" + categoryId;

                    //   CategoriesNew.put(ct.text(), "");
                }

                for (int ct = 0; ct < sel.select(selectorElements.categoryIdText).size(); ct++) {
                    categoryId = sel.select(selectorElements.categoryIdText).eq(ct).text() + ">" + categoryId;
                }
                //     System.err.println(CategoriesNew);
                if (!Categories.containsValue(categoryId)) {
                    Categories.put(Categories.size() + 1 + "", categoryId);
                }

                String description = sel.select(selectorElements.descriptionText).text();

                TreeMap<String, String> params = new TreeMap<>();
                for (int i = 0; i < sel.select(selectorElements.paramText).size(); i++) {
                    String key = sel.select(selectorElements.paramKeyText).eq(i).text();
                    String value = sel.select(selectorElements.paramValueText).eq(i).text();

                    params.put(key.replace(":", ""), value);
                }

                TreeMap<String, String> price = new TreeMap<>();

                boolean onePrice = true;

                for (Element pw : sel.select(selectorElements.priceText).eq(0).select("option")) {
                    price.put("Ширина;" + pw.text(), sel.select(selectorElements.priceDataPriceText).text().replaceAll("\\D|\\s", ""));
                }
                for (Element pw : sel.select(selectorElements.priceText).eq(1).select("option")) {
                    price.put("Длина;" + pw.text(), sel.select(selectorElements.priceDataPriceText).text().replaceAll("\\D|\\s", ""));
                }
                //  System.err.println(sel.select(selectorElements.priceText).eq(2).select("option"));

                for (Element pw : sel.select(selectorElements.priceText).eq(2).select("option")) {
                    price.put("Высота;" + pw.text(), sel.select(selectorElements.priceDataPriceText).text().replaceAll("\\D|\\s", ""));
                }

//            for (Element pw : sel.select(selectorElements.priceText)) {
//
//                for (int j = 0; j < pw.select("select").size(); j++) {
//
//                  price.put(pw.text() +  ";" + pw.select("option").eq(j).text(),
//                                sel.select(selectorElements.priceDataPriceText).text().replaceAll("\\D|\\s", "") + "");
//
//                    onePrice = false;
//                }
//            }
//                if (onePrice) {
//                    String trt = sel.select(selectorElements.priceDataPriceText).eq(0).text().replaceAll("\\D|\\s", "");
//                    if (!"".equals(trt)) {
//                        int priceInt = Integer.parseInt(trt);
//                        price.put("Цена за шт.", priceInt + "");
//                    }
//                }
                YandexOffer addOf = new YandexOffer(id, url, name, price, currencyId, categoryId, picture, delivery, vendor, description, params);
                // System.out.println(addOf.getString());
                Offers.add(addOf);
            }
        }
    }

    String getJSON() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.err.println(Offers.size());
        return gson.toJson(Offers);
    }

    public static String getJSON(HTMLParser h) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //System.err.println(Offers.size());
        return gson.toJson(h.Offers);
    }

    String getXML() {
        YandexXML xml = new YandexXML();
        return xml.toXml(Offers, Categories);
    }

    public static String getXML(HTMLParser h) {
        YandexXML xml = new YandexXML();
        return xml.toXml(h.Offers, h.Categories);
    }

}
