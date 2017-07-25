package ru.webfester.parser;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Володин Антон
 */
public class Utils {

    public static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    //recursive procedure for finding the contents of a directory
    public static void getContent(File Directory, int Indents) {
        for (int i = 0; i < Indents; i++) {
            System.out.print("\t");
        }

        if (Directory.isFile()) {
            System.out.println(Directory.getName());
        } else {
            System.out.println(Directory.getName());
            File[] SubDirectory = Directory.listFiles();
            for (File SubWay : SubDirectory) {
                getContent(SubWay, Indents + 1);
            }
        }
    }

    public static ArrayList<String> urlre = new ArrayList<String>();

    public static void recurceURLS(String url, int count) {
        urlre.add(url);

        try {
            // Заходим на страницу
            Document selector = Jsoup.connect(url).get();
            // Берем все ссылки
            Elements a = selector.select("a");

            // Перебор ссылок
            for (Element element : a) {
                String href = element.attr("href");
                if (!"".equals(selector.select("#main > div.wrapper.basket_fly.head_type_2.banner_narrow > div.wrapper_inner > section > div > h1").text())) {
                    if (!urlre.contains(href)) {
                        System.err.println(count + ") " + href);
                        recurceURLS(href, count + 1);
                    }
                }
            }
        } catch (IOException iOException) {
        }

    }

    /**
     * Парсит URL и возвращает имя сайта без точек
     *
     * @param url
     * @return
     * @throws URISyntaxException
     */
    public static String getDomainName(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            String ret = domain.startsWith("www.") ? domain.substring(4) : domain;
            return ret.replace(".", "_");
        } catch (URISyntaxException ex) {
            return "Error_domatin";
        }
    }

    /**
     * Копирование файла
     *
     * @param source исходный файл
     * @param dest новый путь
     */
    public static void copyFileUsingStream(String source, String dest) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(new File(source));
            os = new FileOutputStream(new File(dest));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HTMLParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HTMLParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
                os.close();
            } catch (IOException ex) {
                Logger.getLogger(HTMLParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Получить рабочие ссылки по id родукта
     *
     * @param url
     */
    public static void getLinkIdProduct(String url) {
        Runnable runnable = () -> {
            String urls = "";
            for (int i = 0; i < 10000; i++) {
                try {
                    URL obj = new URL(url + i);
                    URLConnection conn = obj.openConnection();
                    System.out.println(i + " из 10000 / " + (float) (i * 100F / 10000F) + "%");
                    String server = conn.getHeaderField(0);
                    if ("HTTP/1.1 200 OK".equals(server)) {
                        urls = urls + ",\n" + url + i;
                        System.err.println(url + i);
                        WriteFile("D:\\home\\Downloads\\Super_Parser\\Super Parser\\дримлайн.рф.txt", urls);
                    }
                } catch (MalformedURLException ex) {
                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        runnable.run();
    }

    /**
     * Перезапись файла
     *
     * @param fileName Имя и путь к файлу
     * @param text содержание
     */
    public static void WriteFile(String fileName, String text) {
        //Определяем файл
        File file = new File(fileName);

        try {
            //проверяем, что если файл не существует то создаем его
            if (!file.exists()) {
                file.createNewFile();
            }

            //PrintWriter обеспечит возможности записи в файл
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            try {
                //Записываем текст у файл
                out.print(text);
            } finally {
                //После чего мы должны закрыть файл
                //Иначе файл не запишется
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //JOptionPane.showMessageDialog(null, "Файл записан " +file.getAbsoluteFile(), fileName, JOptionPane.YES_OPTION);
    }

    public static void toWriteFile(String fileName, String text) {
        //Определяем файл
        File file = new File(fileName);

        try {
            String getfile = "";
            //проверяем, что если файл не существует то создаем его
            if (!file.exists()) {
                file.createNewFile();
            }

            //PrintWriter обеспечит возможности записи в файл
            PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile(), true), "UTF-8"));

            try {
                //Записываем текст у файл
                out.print(getfile + "\n" + text);
            } finally {
                //После чего мы должны закрыть файл
                //Иначе файл не запишется
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String ReadFileKeyToValue(File file, String key) {

        String ret = "";
        String[] lines = ReadFileToArray(file, "\n");

        for (int i = 0; i < lines.length; i++) {
            //  System.out.println(Arrays.toString(lines[i].split(":")));
            if (lines[i].split(":").length > 1 && lines[i].split(":")[1] != null) {
                if (lines[i].split(":")[0].contains(key)) {
                    ret = lines[i].split(":", 2)[1];
                }
            }

            //    ret.put(lines[i].split(":")[0], lines[i].split(":")[1]);
        }

        return ret;

    }

    /**
     * Чтение файла ссылок через запятую
     *
     * @param file Путь к файлу и имя
     * @param subr Символ для разделения на массив
     * @return Возвращаем массив строк
     */
    public static String[] ReadFileToArray(File file, String subr) {

        return ReadFile(file).split(subr);
    }

    public static String[] ReadFileToArray(String file, String subr) {

        return ReadFileToArray(new File(file), subr);
    }

    public static String ReadFile(File file) {
        //Этот спец. объект для построения строки
        StringBuilder sb = new StringBuilder();

        if (!file.exists()) {
            try {
                throw new FileNotFoundException(file.getName());
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            //Объект для чтения файла в буфер
            BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            try {
                //В цикле построчно считываем файл
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } finally {
                //Также не забываем закрыть файл
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public static boolean toBoolean(String str) {
        return "true".equals(str);
    }

    public static String md5Prefix(String str) {
        return md5(str).replaceAll("\\D", "").substring(0, 3);
    }

    public static String md5(String str) {
        try {
            //String s="f78spx";
            //String s="muffin break";
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            // передаем в MessageDigest байт-код строки
            m.update(str.getBytes("utf-8"));
            // получаем MD5-хеш строки без лидирующих нулей
            String s2 = new BigInteger(1, m.digest()).toString(16);
            StringBuilder sb = new StringBuilder(32);
            // дополняем нулями до 32 символов, в случае необходимости
            //System.out.println(32 - s2.length());
            for (int i = 0, count = 32 - s2.length(); i < count; i++) {
                sb.append("0");
            }
            // возвращаем MD5-хеш
            return sb.append(s2).toString();
        } catch (NoSuchAlgorithmException ex) {
            return "";
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }

    public static String md5(int str) {
        return md5(str + "");
    }

    /**
     * Поиск в массиве по значениею
     *
     * @param map
     * @param value
     * @return
     */
    public static Object getKeyByValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    /**
     * Конвертирует строковые записи null в псевдозначение
     *
     * @param str
     * @return
     */
    static Object inNull(String str) {
        if (str == null) {
            return null;
        }

        if ("null".equals(str)) {
            return null;
        }

        return str;
    }

    public static YandexOffer[] XMLRead(File file) {
        try {

            Gson gson = new Gson();

            JsonReader reader = new JsonReader(new FileReader(file));
            YandexOffer[] data = gson.fromJson(reader, YandexOffer[].class);

            return data;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    static void JsonToXml(String xml) {

    }

    static YandexOffer[] JsonToXmlFile(File file) {

        return XMLRead(file);
    }

    static File FileChooser() {
        return FileChooser(".");
    }

    static File FileChooser(String file) {
        JFileChooser fileopen = new JFileChooser();
        fileopen.setCurrentDirectory(new File(file));
        int ret = fileopen.showDialog(null, "Открыть JSON для конвертации в XML Yndex.Market");
        if (ret == JFileChooser.APPROVE_OPTION) {
            return fileopen.getSelectedFile();
        } else {
            return null;
        }
    }

    static String JSONtoCSV(ArrayList<YandexOffer> Offers, HashMap<String, String> Categories) {

        String ret = "";
        for (YandexOffer offer : Offers) {

            ret = ret + "\"" + offer.id + "\",";
            ret = ret + "\"" + offer.name + "\",";
            ret = ret + "\"" + offer.categoryId + "\",";
            ret = ret + "\"" + offer.currencyId + "\",";
            ret = ret + "\"" + offer.delivery + "\",";
            ret = ret + "\"" + offer.description + "\",";
            ret = ret + "\"" + offer.url + "\",";
            ret = ret + "\"" + offer.vendor + "\",";

            for (String pric : offer.picture) {
                ret = ret + "\"" + pric + "\",";
            }
            for (Map.Entry<String, String> pric : offer.param.entrySet()) {
                ret = ret + "\"" + pric + "\",";
            }

            String p = "";
            ret = ret + "\"";
            for (Map.Entry<String, String> price : offer.price.entrySet()) {
                ret = ret + price.getKey().split(";")[0] + "---" + price.getKey().split(";")[1] + "|";

                p = price.getValue();
            }
            ret = ret + "\",";
            ret = ret + "\"" + p + "\",";

            ret = ret + "\n";
        }
        return ret;

    }

    public int getIndexByname(ArrayList<String> list, String pName) {
        for (String _item : list) {
            if (_item.equals(pName)) {
                return list.indexOf(_item);
            }
        }
        return -1;
    }
    public static String[] sizes = {"80x190",
        "80x195",
        "80x200",
        "90x190",
        "90x195",
        "90x200",
        "120x190",
        "120x195",
        "120x200",
        "140x190",
        "140x195",
        "140x200",
        "160x190",
        "160x195",
        "160x200",
        "180x190",
        "180x195",
        "180x200",
        "200x190",
        "200x195",
        "200x200",
        "60x120",
        "65x125",
        "70x140",
        "D200",
        "D210",
        "D220"};
}
