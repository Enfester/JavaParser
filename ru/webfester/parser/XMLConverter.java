//package ru.webfester.parser;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.io.StringWriter;
//import java.io.UnsupportedEncodingException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerConfigurationException;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.TransformerFactoryConfigurationError;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.xml.sax.SAXException;
//
///**
// *
// * @author antiv
// */
//public class XMLConverter {
//
//    static String start(String file, ArrayList<YandexMarketOffer> AllOffers, HashMap<String, String> hashCategories) {
//
//        try {
//            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//            Document document = documentBuilder.newDocument();
//
//            Element yml_catalog = document.createElement("yml_catalog"); // задаем рут элемнт 
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//            yml_catalog.setAttribute("date", dateFormat.format(new Date()) + "");
//
//            Element shop = document.createElement("shop"); // Создаем <shop>
//            Element categories = document.createElement("categories"); // Создаем <categories>            
//            Element offers = document.createElement("offers"); // Создаем <offers>
//
//            // Добавляем категории
//            for (Map.Entry<String, String> entry : hashCategories.entrySet()) {
//                Element category = document.createElement("category"); // Создаем <category>
//                category.setAttribute("id", entry.getKey());
//                category.setTextContent(entry.getValue());
//                categories.appendChild(category);
//            }
//
//            // Разбиваем массив товаров на товры
//            for (YandexMarketOffer entry : AllOffers) {
//
//                Element offer = document.createElement("offer"); // Создаем <offer>
//                // Задаем атрибуты
//                offer.setAttribute("id", entry.id + "");
//                offer.setAttribute("group_id", "0000" + entry.id + "");
//                offer.setAttribute("available", "true");
//
//                Element name = document.createElement("name"); // Создаем <name>
//                name.setTextContent(entry.name);
//                offer.appendChild(name);
//
//                Element url = document.createElement("url"); // Создаем <url>
//                url.setTextContent(entry.url);
//                offer.appendChild(url);
//
//                for (Map.Entry<String, String> price : entry.price.entrySet()) {
//                    Element el = document.createElement("price"); // Создаем <price>
//                    el.setTextContent(price.getValue());
//                    offer.appendChild(el);
//                }
//
//                for (String img : entry.picture) {
//                    if (!img.contains("http")) {
//                        img = "http://" + new URL(entry.url).getHost() + "/" + img;
//                    }
//                    Element picture = document.createElement("picture"); // Создаем <price>
//                    picture.setTextContent(img);
//                    offer.appendChild(picture);
//                }
//
//                Element currencyId = document.createElement("currencyId"); // Создаем <currencyId>
//                currencyId.setTextContent(entry.currencyId);
//                offer.appendChild(currencyId);
//
//                Element delivery = document.createElement("delivery"); // Создаем <delivery>
//                delivery.setTextContent(entry.delivery);
//                offer.appendChild(delivery);
//
//                Element categoryId = document.createElement("categoryId"); // Создаем <categoryId>
//                categoryId.setTextContent(Utils.getKeyByValue(hashCategories, entry.categoryId).toString());
//                offer.appendChild(categoryId);
//
//                Element vendor = document.createElement("vendor"); // Создаем <vendor>
//                vendor.setTextContent(entry.vendor);
//                offer.appendChild(vendor);
//
//                Element description = document.createElement("description"); // Создаем <description>
//                description.setTextContent(entry.description);
//                offer.appendChild(description);
//
//                for (Map.Entry<String, String> param : entry.param.entrySet()) {
//                    Element element = document.createElement("param");
//                    element.setAttribute("name", param.getKey());
//                    element.setTextContent(param.getValue());
//                    offer.appendChild(element);
//                }
//
//                offers.appendChild(offer);
//
//            }
//
//            shop.appendChild(categories);
//            shop.appendChild(offers);
//
//            yml_catalog.appendChild(shop);
//
//            document.appendChild(yml_catalog); // добавляем
//
//            if (file != null) {
//                writeDocument(document, new File(file));
//            }
//
//            return printDocument(document);
//        } catch (ParserConfigurationException ex) {
//            return "";
//        } catch (MalformedURLException ex) {
//            Logger.getLogger(XMLConverter.class.getName()).log(Level.SEVERE, null, ex);
//            return "";
//        }
//    }
//
//    public XMLConverter(File file, int tovarID, int groupID, HashMap<String, String> map, HashMap<String, String> param) {
//        System.out.println("Получаем: id:" + tovarID + " > " + map);
//        try {
//            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//            Document document;
//
//            if (!file.exists()) { // Если нету файла
//                document = documentBuilder.newDocument();   // создаем новый документ
//
//                Element yml_catalog = document.createElement("yml_catalog"); // задаем рут элемнт 
//
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                yml_catalog.setAttribute("date", dateFormat.format(new Date()) + "");
//                Element offers = document.createElement("offers"); // добавляем товар
//                Element shop = document.createElement("shop"); // добавляем товар
//                Element cat = document.createElement("categories"); // добавляем товар
//                shop.appendChild(cat);
//
//                yml_catalog.appendChild(shop);
//                shop.appendChild(offers);
//                document.appendChild(yml_catalog); // добавляем
//                writeDocument(document, file); // и собираем в файл
//
//            } else { // если файл есть
//
//                document = documentBuilder.parse(file); // парсим его
//
//                // Разбираемся с категориями
//                Node categories = document.getElementsByTagName("categories").item(0); // берем элемент категории
//
//                if (!isElements(categories, map.get("categoryId"))) {
//                    Element category = document.createElement("category");
//                    category.setAttribute("id", categories.getChildNodes().getLength() + 1 + "");
//                    category.setTextContent(map.get("categoryId"));
//                    categories.appendChild(category);
//                }
//
//                Node offers = document.getElementsByTagName("offers").item(0); // берем рут элемент
//                Element offer = document.createElement("offer"); // добавляем товар
//
//                // атрибуты товара
//                offer.setAttribute("id", groupID + "");
//                offer.setAttribute("group_id", "0000" + groupID + "");
//                offer.setAttribute("available", "true");
//
//                Element ct = document.createElement("categoryId");
//                ct.setTextContent(getNodeIDElements(categories, map.get("categoryId")) + "");
//                offer.appendChild(ct);
//                map.remove("categoryId");
//
//                // добовляем элементы товара
//                for (Map.Entry<String, String> entry : map.entrySet()) {
//
//                    String key = entry.getKey();
//                    if (key.contains("picture")) {
//                        key = "picture";
//                    }
//
//                    Element el = document.createElement(key);
//                    el.setTextContent(entry.getValue());
//                    offer.appendChild(el);
//                }
//
//                // параментры товара
//                for (Map.Entry<String, String> entry : param.entrySet()) {
//                    Element el = document.createElement("param");
//                    el.setAttribute("name", entry.getKey());
//                    el.setTextContent(entry.getValue());
//                    offer.appendChild(el);
//                }
//
//                offers.appendChild(offer);   // добавляем все это к рут элементу
//              //  writeDocument(document, file); // собираем в файл
//            }
//
//        } catch (ParserConfigurationException ex) {
//            ex.printStackTrace(System.out);
//        } catch (SAXException | IOException ex) {
//            Logger.getLogger(XMLConverter.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//  
//
//    private static String getNodeIDElements(Node node, String str) {
//        String ret = "";
//        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
//            if (node.getChildNodes().item(i).getTextContent().equals(str)) {
//                Element el = (org.w3c.dom.Element) node.getChildNodes().item(i);
//                ret = el.getAttribute("id");
//                break;
//            }
//        }
//        return ret;
//    }
//
//    /**
//     * Проверка Node на элемент
//     *
//     * @param node
//     * @param str
//     * @return
//     */
//    private static boolean isElements(Node node, String str) {
//        boolean ret = false;
//        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
//            if (node.getChildNodes().item(i).getTextContent().equals(str)) {
//                ret = true;
//                break;
//            }
//        }
//        return ret;
//    }
//
//    /**
//     * Возращаем индекс элемента в массиве по ключу
//     *
//     * @param map
//     * @param key
//     * @return
//     */
//    private static int getIndexHashMap(HashMap<String, String> map, String key) {
//        int i = 0;
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//
//            if (entry.getKey().equals(key)) {
//                break;
//            }
//            i++;
//        }
//        return i;
//    }
//}
