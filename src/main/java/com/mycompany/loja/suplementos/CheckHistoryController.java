/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.loja.suplementos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import supportClasses.HistoryItem;
import supportClasses.databaseType;

/**
 *
 * @author vinicius
 */
public class CheckHistoryController extends ControllerModel {

    @FXML
    public TextField dateStartSearchTextField;
    
    @FXML
    public TextField dateToSearchTextField;

    @FXML
    public TableColumn<HistoryItem, String> typeColumn;

    @FXML
    public TableColumn<HistoryItem, String> invoiceColumn;

    @FXML
    public TableColumn<HistoryItem, String> productColumn;

    @FXML
    public TableColumn<HistoryItem, Integer> quantityColumn;
    
    @FXML
    public TableColumn<HistoryItem, Float> unitValueColumn;
    
    @FXML
    public TableColumn<HistoryItem, Float> totalColumn;

    @FXML
    public TableColumn<HistoryItem, String> DateColumn;

    

    @FXML
    public javafx.scene.control.TableView<HistoryItem> historyTable;

    public ObservableList<HistoryItem> data;

    public Stage dialog;

    public CheckHistoryController(Connection db) {
        super(db);
    }

    CheckHistoryController(Connection connection, databaseType dbType) {
        super(connection, dbType);
    }

    public void init(Stage modal) {

        dialog = modal;

        data = FXCollections.observableArrayList();

        // the property name between "" has to be the same name of the attribute on the class
        typeColumn.setCellValueFactory(new PropertyValueFactory<HistoryItem, String>("type"));
        invoiceColumn.setCellValueFactory(new PropertyValueFactory<HistoryItem, String>("invoice"));
        productColumn.setCellValueFactory(new PropertyValueFactory<HistoryItem, String>("product"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<HistoryItem, Integer>("quantity"));
        unitValueColumn.setCellValueFactory(new PropertyValueFactory<HistoryItem, Float>("unitvalue"));        
        totalColumn.setCellValueFactory(new PropertyValueFactory<HistoryItem, Float>("total"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<HistoryItem, String>("date"));

        historyTable.setItems(data);
        
         dateToSearchTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    Search(new ActionEvent());
                }
            }
        });

    }

    public void addToHistoryItemList(String type, String productname, 
            String invoice, Integer quantity, Float unitvalue, Float total, 
            String actiondate) {                
        data.add(
                new HistoryItem(type, invoice, productname, quantity, total, unitvalue, actiondate)
        );
    }

    @FXML
    public void Search(ActionEvent event) {

        data.clear();
        String dateFrom = null;
        String dateTo = null;
        try {
            dateFrom = dateStartSearchTextField.getText();
            dateTo = dateToSearchTextField.getText();
        } catch (Exception e) {

        }

        if (dateFrom == null) {
            sendAlert("Error finding Brand",
                    "No Brand name to search.",
                    "Pick a brand name to search.",
                    Alert.AlertType.ERROR);
        }
        // if no brand name find all
        try {
            Statement st = this.connection.createStatement();
            ResultSet rs = null;
            switch (dbType) {
                case firebird:
                    
                    if (dateFrom.equals("")) {
                        try {
                            rs = st.executeQuery(
                                    "SELECT * FROM checkAllHistory;"
                            );
                            while (rs.next()) {                                
                                addToHistoryItemList(
                                        rs.getString("tipo"),
                                        rs.getString("productname"),
                                        rs.getString("invoice"),                                        
                                        rs.getInt("quantity"),
                                        rs.getFloat("unitvalue"),                                        
                                        rs.getFloat("total"),                                                                                
                                        rs.getTimestamp("actiondate")
                                                .toLocalDateTime()
                                                .format(DateTimeFormatter
                                                        .ofPattern("YYYY-MM-dd HH:mm:ss"))
                                );
                            }
                            rs.close();
                            st.close();

                        } catch (Exception e) {
                            System.out.println("EERROR " + e.getMessage());
                        }

                    } // find a specific client name
                    else {
                        try {
                            st = this.connection.createStatement();
                            rs = st.executeQuery(
                                    "SELECT * from checkHistoryRange('" + dateFrom + "','" + dateTo + "');"
                            );
                            if (rs.next()) {
                                 addToHistoryItemList(
                                        rs.getString("tipo"),
                                        rs.getString("productname"),
                                        rs.getString("invoice"),                                        
                                        rs.getInt("quantity"),
                                        rs.getFloat("unitvalue"),                                        
                                        rs.getFloat("total"),                                                                                
                                        rs.getTimestamp("actiondate")
                                                .toLocalDateTime()
                                                .format(DateTimeFormatter
                                                        .ofPattern("YYYY-MM-dd HH:mm:ss"))
                                );
                            } else {
                                sendAlert(
                                        "No results!",
                                        "Search lead to 0 results. Try a different Purchase Name",
                                        "No Purchase with this name",
                                        Alert.AlertType.INFORMATION);
                                return;
                            }
                            while (rs.next()) {
                                addToHistoryItemList(
                                        rs.getString("tipo"),
                                        rs.getString("productname"),
                                        rs.getString("invoice"),                                        
                                        rs.getInt("quantity"),
                                        rs.getFloat("unitvalue"),                                        
                                        rs.getFloat("total"),                                                                                
                                        rs.getTimestamp("actiondate")
                                                .toLocalDateTime()
                                                .format(DateTimeFormatter
                                                        .ofPattern("YYYY-MM-dd HH:mm:ss"))
                                );
                            }
                            rs.close();
                            st.close();
                        } catch (Exception e) {
                            System.out.println("EERROR " + e.getMessage());
                            return;

                        }
                    }
                    break;
                case postgres:
                    if (dateFrom.equals("")) {
                        try {
                            st = this.connection.createStatement();
                            rs = st.executeQuery(
                                    "select * "
                                    + " FROM searchpurchases;"
                            );
                            while (rs.next()) {
                                 addToHistoryItemList(
                                        rs.getString("tipo"),
                                        rs.getString("productname"),
                                        rs.getString("invoice"),                                        
                                        rs.getInt("quantity"),
                                        rs.getFloat("unitvalue"),                                        
                                        rs.getFloat("total"),                                                                                
                                        rs.getTimestamp("actiondate")
                                                .toLocalDateTime()
                                                .format(DateTimeFormatter
                                                        .ofPattern("YYYY-MM-dd HH:mm:ss"))
                                );
                            }
                            rs.close();
                            st.close();

                        } catch (Exception e) {
                            System.out.println("Error getting all purchases " + e.getMessage());
                        }

                    } // find a specific client name
                    else {
                        try {
                            st = this.connection.createStatement();
                            rs = st.executeQuery(
                                    "select * "
                                    + " search_a_purchase('" + dateFrom + "')"
                            );
                            if (rs.next()) {
                                 addToHistoryItemList(
                                        rs.getString("tipo"),
                                        rs.getString("productname"),
                                        rs.getString("invoice"),                                        
                                        rs.getInt("quantity"),
                                        rs.getFloat("unitvalue"),                                        
                                        rs.getFloat("total"),                                                                                
                                        rs.getTimestamp("actiondate")
                                                .toLocalDateTime()
                                                .format(DateTimeFormatter
                                                        .ofPattern("YYYY-MM-dd HH:mm:ss"))
                                );
                            } else {
                                sendAlert(
                                        "No results!",
                                        "Search lead to 0 results. Try a different Purchase Name",
                                        "No Purchase with this name",
                                        Alert.AlertType.INFORMATION);
                                return;
                            }
                            while (rs.next()) {
                                 addToHistoryItemList(
                                        rs.getString("tipo"),
                                        rs.getString("productname"),
                                        rs.getString("invoice"),                                        
                                        rs.getInt("quantity"),
                                        rs.getFloat("unitvalue"),                                        
                                        rs.getFloat("total"),                                                                                
                                        rs.getTimestamp("actiondate")
                                                .toLocalDateTime()
                                                .format(DateTimeFormatter
                                                        .ofPattern("YYYY-MM-dd HH:mm:ss"))
                                );
                            }
                            rs.close();
                            st.close();
                        } catch (Exception e) {

                        }
                    }
                    break;
            }

        } catch (Exception e) {
        }

        // set items        
        historyTable.setItems(data);

    }

}
