/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.webfester.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

/**
 *
 * @author Володин Антон
 */
public class YandexOffer {

    int id;
    String url;
    String name;

    String currencyId;
    String[] picture;
    String delivery;
    String categoryId;
    String vendor;
    String description;
    TreeMap<String, String> param;
    TreeMap<String, String> price;

    /**
     *
     * @param url
     * @param name
     * @param price
     * @param currencyId
     * @param picture
     * @param delivery
     * @param vendor
     * @param description
     * @param param
     */
    public YandexOffer(int id, String url, String name, TreeMap price, String currencyId, String categoryId, String[] picture, String delivery, String vendor, String description, TreeMap param) {
        this.id = id;
        this.url = url;
        this.name = name;

        this.price = price;
        this.categoryId = categoryId;
        this.currencyId = currencyId;
        this.picture = picture;

        this.delivery = delivery;
        // this.categoryId = categoryId.replaceAll("[^0-9]", "");
        this.vendor = vendor;
        this.description = description;
        this.param = param;

    }

    public void setName(String str) {
        this.name = str;
    }

    public void setUrl(String str) {
        this.url = str;
    }

    public String toXML() {

        return id + "\n"+
                url + "\n"
                + name + "\n"
                + price + "\n"
                + currencyId + "\n"
                + Arrays.toString(picture) + "\n"
                + delivery + "\n"
                + categoryId + "\n"
                + vendor + "\n"
                + description + "\n"
                + param;
    }

    String getString() {
      return toXML().replaceAll("\n", "|");
    }
}
