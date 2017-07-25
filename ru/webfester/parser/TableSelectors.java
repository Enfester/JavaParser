/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.webfester.parser;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author antiv
 */
public class TableSelectors {

    public String name, nameSelector, paramKey, paramValue, array;
    private final DefaultTableModel model;

    public TableSelectors() {
        final Object[] data = {this.toString()};
        this.model = new DefaultTableModel(data, 1) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                System.out.println(data[0]);
                return super.getColumnClass(columnIndex);
            }
        };
        model.addRow(data);
    }
}
