/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.webfester.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jsoup.Jsoup;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Володин Антон
 */
public class YandexXML {

    private Document _document;

    private TreeMap<String, String> _categories;
    private ArrayList<YandexOffer> _offers;

    public YandexXML() {

    }

    private void start() {
        ArrayList parentId = new ArrayList<>();
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = documentBuilder.newDocument();

            Element yml_catalog = doc.createElement("yml_catalog"); // задаем рут элемнт 
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            yml_catalog.setAttribute("date", dateFormat.format(new Date()) + "");

            Element shop = doc.createElement("shop"); // Создаем <shop>
            Element categories = doc.createElement("categories"); // Создаем <categories>            
            Element offers = doc.createElement("offers"); // Создаем <offers>

          //  System.out.println("Добавляем категории");
            // Добавляем категории
            for (Map.Entry<String, String> entry : _categories.entrySet()) {

                Element category = doc.createElement("category"); // Создаем <category>

               
                for (String ct : entry.getValue().split(">")) { // каждый последующий это родитель предыдущего
               

                    //System.out.println("\t" + ct);
                }

                String par = entry.getValue().split(">")[0].trim();
                if (!parentId.contains(par)) {
                    parentId.add(par);
                    Element parent = doc.createElement("category");
                    parent.setAttribute("id", parentId.indexOf(par) + "");
                    parent.setTextContent(par);
                    categories.appendChild(parent);
                }

                category.setAttribute("id", entry.getKey() + 1);
                //  System.err.println(entry.getValue() + "|" + entry.getValue().split(">").length);
                if (entry.getValue().split(">").length > 1) {
                    category.setTextContent(entry.getValue().split(">")[1].trim());

                } else {
                    category.setTextContent(entry.getValue());
                }
                category.setAttribute("parentId", parentId.indexOf(entry.getValue().split(">")[0].trim()) + "");

                categories.appendChild(category);
            }

            ArrayList<String> tmpDoubleOffer = new ArrayList<String>();
            // Разбиваем массив товаров на товры

            for (YandexOffer entry : _offers) {

                if (!tmpDoubleOffer.contains(entry.name)) {
                    tmpDoubleOffer.add(entry.name);
                    for (Map.Entry<String, String> price : entry.price.entrySet()) {
                        Element offer = doc.createElement("offer"); // Создаем <offer>
                        // Задаем атрибуты
                        offer.setAttribute("id", Utils.md5Prefix(entry.vendor) + "00" + entry.id);
                        offer.setAttribute("group_id", Utils.md5Prefix(entry.vendor) + entry.id);
                        offer.setAttribute("available", "true");

                        Element name = doc.createElement("name"); // Создаем <name>
                        name.setTextContent(entry.name);
                        offer.appendChild(name);

                        Element url = doc.createElement("url"); // Создаем <url>
                        url.setTextContent(entry.url);
                        offer.appendChild(url);

                        Element el = doc.createElement("price"); // Создаем <price>
                        el.setTextContent(price.getValue());
                        offer.appendChild(el);

                        ArrayList<String> tmpImg = new ArrayList<String>();

                        if (entry.picture.length == 1) {
                            if ("".equals(entry.picture[0])) {
                                try {
                                    org.jsoup.nodes.Document sel = Jsoup.connect(entry.url).get();
                                    entry.picture[0] = sel.select(".photo a img").attr("src");
                                    System.err.println(entry.name + ":" + entry.picture[0]);
                                } catch (IOException ex) {
                                    System.err.println("404!");
                                }
                            }
                        }

                        for (String img : entry.picture) {
                            if (!tmpImg.contains(img)) {
                                tmpImg.add(img);
                                Element picture = doc.createElement("picture"); // Создаем <price>
                                picture.setTextContent(img);
                                offer.appendChild(picture);
                            }

                            if (tmpImg.size() > 9) {
                                break;
                            }
                        }

                        Element currencyId = doc.createElement("currencyId"); // Создаем <currencyId>
                        currencyId.setTextContent(entry.currencyId);
                        offer.appendChild(currencyId);

                        Element delivery = doc.createElement("delivery"); // Создаем <delivery>
                        delivery.setTextContent(entry.delivery);
                        offer.appendChild(delivery);

                        Element categoryId = doc.createElement("categoryId"); // Создаем <categoryId>
                        categoryId.setTextContent(Utils.getKeyByValue(_categories, entry.categoryId).toString() + 1);
                        offer.appendChild(categoryId);

                        Element vendor = doc.createElement("vendor"); // Создаем <vendor>
                        vendor.setTextContent(entry.vendor);
                        offer.appendChild(vendor);

                        Element description = doc.createElement("description"); // Создаем <description>
                        description.setTextContent(entry.description);
                        offer.appendChild(description);

                        for (Map.Entry<String, String> param : entry.param.entrySet()) {
                            Element element = doc.createElement("param");
                            element.setAttribute("name", param.getKey());
                            element.setTextContent(param.getValue());
                            offer.appendChild(element);
                        }

                        //     System.out.println(price.getKey().split(";")[1]);
                        Element size = doc.createElement("param");

                        //  System.err.println(price.getKey()+"|"+price.getKey().split(";").length);
                        if (price.getKey().split(";").length == 2) {
                            size.setAttribute("name", price.getKey().split(";")[0]);
                            size.setTextContent(price.getKey().split(";")[1]);
                            // System.err.println(price.getKey().split(";")[1]);
                        } else {
                            size.setAttribute("name", "Размер");
                            size.setTextContent(price.getKey());
                        }
                        offer.appendChild(size);

                        if (!"".equals(price.getValue())) {
                            offers.appendChild(offer);
                        }
                    }
                }
            }

            shop.appendChild(categories);
            shop.appendChild(offers);

            yml_catalog.appendChild(shop);

            doc.appendChild(yml_catalog); // добавляем

            _document = doc;
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(YandexXML.class.getName()).log(Level.SEVERE, null, ex);
            _document = null;
        }
    }

    String toXml(ArrayList<YandexOffer> Offers, TreeMap<String, String> Categories) {
        this._offers = Offers;
        this._categories = Categories;

        start();
        return printDocument(_document);
    }

    void saveToFile(String file) {
        writeDocument(_document, new File(file));
    }

    void saveToFile(File file) {
        writeDocument(_document, file);
    }

    /**
     * Возвращает отформатированый XML документ
     *
     * @param doc
     * @return
     */
    private String printDocument(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (TransformerConfigurationException ex) {
            return ex.getMessage();
        } catch (TransformerException ex) {
            return ex.getMessage();
        }
    }

    // Функция для сохранения DOM в файл
    private void writeDocument(Document document, File fileName) throws TransformerFactoryConfigurationError {
        System.out.println("Компоновка файла");
        long lBegin = System.currentTimeMillis();

        try {
            if (!fileName.exists()) {
                fileName.createNewFile();
            }
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(document);

            FileOutputStream fos = new FileOutputStream(fileName);
            StreamResult result = new StreamResult(fos);
            tr.transform(source, result);

            long lEnd = System.currentTimeMillis();
            System.out.println("Компоновка завершена " + (lEnd - lBegin));

        } catch (TransformerException | IOException e) {
            System.out.println("Ошибка записи файла");

        }

    }

}
