/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.loja.suplementos;

import com.mongodb.client.MongoDatabase;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
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
import javafx.util.StringConverter;
import supportClasses.Purchase;
import supportClasses.databaseType;

/**
 *
 * @author vinicius
 */
public class SearchPurchaseController extends ControllerModel {

    @FXML
    public TextField purchaseSearchTextField;

    @FXML
    public TableColumn<Purchase, String> nameColumn;

    @FXML
    public TableColumn<Purchase, String> invoiceColumn;

    @FXML
    public TableColumn<Purchase, Float> subtotalColumn;

    @FXML
    public TableColumn<Purchase, Float> totalColumn;

    @FXML
    public TableColumn<Purchase, String> purchaseDateColumn;

    @FXML
    TableColumn<Purchase, Float> discountColumn;

    @FXML
    public javafx.scene.control.TableView<Purchase> purchaseTable;

    public ObservableList<Purchase> data;

    public Stage dialog;

    public SearchPurchaseController(Connection db) {
        super(db);
    }

    SearchPurchaseController(Connection connection, databaseType dbType) {
        super(connection, dbType);
    }

    SearchPurchaseController(MongoDatabase mongoDatabase, databaseType dbType) {
        super(mongoDatabase, dbType);
    }

    public void init(Stage modal) {

        dialog = modal;

        data = FXCollections.observableArrayList();

        // the property name between "" has to be the same name of the attribute on the class
        nameColumn.setCellValueFactory(new PropertyValueFactory<Purchase, String>("supplier"));
        invoiceColumn.setCellValueFactory(new PropertyValueFactory<Purchase, String>("invoice"));
        subtotalColumn.setCellValueFactory(new PropertyValueFactory<Purchase, Float>("subtotal"));
        discountColumn.setCellValueFactory(new PropertyValueFactory<Purchase, Float>("discount"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<Purchase, Float>("total"));
        purchaseDateColumn.setCellValueFactory(new PropertyValueFactory<Purchase, String>("purchasedate"));

        purchaseTable.setItems(data);
        
         purchaseSearchTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    Search(new ActionEvent());
                }
            }
        });

    }

    public void addToPurchaseList(String fantasyname, String invoice, 
            Float subtotal, Float discount, Float total, String purchaseDate) {        
        data.add(
                new Purchase(fantasyname, invoice, subtotal, discount, total, purchaseDate)
        );
    }

    @FXML
    public void Search(ActionEvent event) {

        data.clear();
        String purchaseSearchString = null;
        try {
            purchaseSearchString = purchaseSearchTextField.getText();
        } catch (Exception e) {

        }

        if (purchaseSearchString == null) {
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
                    
                    if (purchaseSearchString.equals("")) {
                        try {
                            rs = st.executeQuery(
                                    "select first 50 * "
                                    + " FROM searchPurchase "
                            );
                            while (rs.next()) {                                
                                addToPurchaseList(
                                        rs.getString("fantasyname"),
                                        rs.getString("invoice"),
                                        rs.getFloat("subtotal"),                                        
                                        rs.getFloat("discount"),
                                        rs.getFloat("total"),                                        
                                        rs.getTimestamp("purchasedate")
                                                .toLocalDateTime()
                                                .format(DateTimeFormatter
                                                        .ofPattern("dd/MM/YYYY HH:mm:ss"))
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
                                    "SELECT * FROM search_a_purchase( '" + purchaseSearchString + "');"
                            );
                            if (rs.next()) {
                                addToPurchaseList(
                                        rs.getString("fantasyname"),
                                        rs.getString("invoice"),
                                        rs.getFloat("subtotal"),
                                        rs.getFloat("discount"),
                                        rs.getFloat("total"),
                                        rs.getTimestamp("purchasedate")
                                                .toLocalDateTime()
                                                .format(DateTimeFormatter
                                                        .ofPattern("dd/MM/YYYY HH:mm:ss"))
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
                                addToPurchaseList(
                                        rs.getString("fantasyname"),
                                        rs.getString("invoice"),
                                        rs.getFloat("subtotal"),
                                        rs.getFloat("discount"),
                                        rs.getFloat("total"),
                                        rs.getTimestamp("purchasedate")
                                                .toLocalDateTime()
                                                .format(DateTimeFormatter
                                                        .ofPattern("dd/MM/YYYY HH:mm:ss"))
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
                    if (purchaseSearchString.equals("")) {
                        try {
                            st = this.connection.createStatement();
                            rs = st.executeQuery(
                                    "select * "
                                    + " FROM searchpurchases;"
                            );
                            while (rs.next()) {
                                addToPurchaseList(
                                        rs.getString("fantasyname"),
                                        rs.getString("invoice"),
                                        rs.getFloat("subtotal"),
                                        rs.getFloat("discout"),
                                        rs.getFloat("total"),
                                        rs.getTimestamp("purchasedate")
                                                .toLocalDateTime()
                                                .format(DateTimeFormatter
                                                       .ofPattern("dd/MM/YYYY HH:mm:ss"))
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
                                    + " search_a_purchase('" + purchaseSearchString + "')"
                            );
                            if (rs.next()) {
                                addToPurchaseList(
                                        rs.getString("fantasyname"),
                                        rs.getString("invoice"),
                                        rs.getFloat("subtotal"),
                                        rs.getFloat("discout"),
                                        rs.getFloat("total"),
                                        rs.getTimestamp("purchasedate")
                                                .toLocalDateTime()
                                                .format(DateTimeFormatter
                                                       .ofPattern("dd/MM/YYYY HH:mm:ss"))
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
                                addToPurchaseList(
                                        rs.getString("fantasyname"),
                                        rs.getString("invoice"),
                                        rs.getFloat("subtotal"),
                                        rs.getFloat("discout"),
                                        rs.getFloat("total"),
                                        rs.getTimestamp("purchasedate")
                                                .toLocalDateTime()
                                                .format(DateTimeFormatter
                                                        .ofPattern("dd/MM/YYYY HH:mm:ss"))
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
        purchaseTable.setItems(data);

    }

}
