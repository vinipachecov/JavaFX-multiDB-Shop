/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.loja.suplementos;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import org.bson.Document;
import supportClasses.Sale;
import supportClasses.databaseType;

/**
 *
 * @author vinicius
 */
public class SearchSaleController extends ControllerModel {

    @FXML
    public TextField saleSearchTextField;

    @FXML
    public TableColumn<Sale, String> nameColumn;

    @FXML
    public TableColumn<Sale, String> invoiceColumn;

    @FXML
    public TableColumn<Sale, Float> subtotalColumn;

    @FXML
    public TableColumn<Sale, Float> totalColumn;

    @FXML
    public TableColumn<Sale, String> saleDateColumn;

    @FXML
    TableColumn<Sale, Float> discountColumn;

    @FXML
    public javafx.scene.control.TableView<Sale> saleTable;

    public ObservableList<Sale> data;

    public Stage dialog;

    public SearchSaleController(Connection db) {
        super(db);
    }

    SearchSaleController(Connection connection, databaseType dbType) {
        super(connection, dbType);
    }

    SearchSaleController(MongoDatabase mongoDatabase, databaseType dbType) {
        super(mongoDatabase, dbType);
    }

    public void init(Stage modal) {

        dialog = modal;

        data = FXCollections.observableArrayList();

//        // the property name between "" has to be the same name of the attribute on the class
        nameColumn.setCellValueFactory(new PropertyValueFactory<Sale, String>("client"));
        invoiceColumn.setCellValueFactory(new PropertyValueFactory<Sale, String>("invoice"));
        subtotalColumn.setCellValueFactory(new PropertyValueFactory<Sale, Float>("subtotal"));
        discountColumn.setCellValueFactory(new PropertyValueFactory<Sale, Float>("discount"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<Sale, Float>("total"));
        saleDateColumn.setCellValueFactory(new PropertyValueFactory<Sale, String>("saleDate"));

        saleTable.setItems(data);

        saleSearchTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    Search(new ActionEvent());
                }
            }
        });

    }

    public void addToSaleList(String clientname, String invoice,
            Float subtotal, Float discount, Float total, String purchaseDate) {
        data.add(
                new Sale(clientname, invoice, subtotal, discount, total, purchaseDate)
        );
    }

    @FXML
    public void Search(ActionEvent event) {

        data.clear();
        String saleSearchString = null;
        try {
            saleSearchString = saleSearchTextField.getText();
        } catch (Exception e) {
            System.out.println("Error getting sale" + e.getMessage());
        }

        if (saleSearchString == null) {
            sendAlert("Error finding client",
                    "No clientname to search.",
                    "Pick a clientname to search.",
                    Alert.AlertType.ERROR);
        }
        // if no brand name find all
        try {
            Statement st = null;
            ResultSet rs = null;
            switch (dbType) {
                case mongodb:
                    MongoCollection<Document> sales = mongoDatabase.getCollection("sales");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    if (saleSearchString.equals("")) {
                        try {

                            List<Document> documents = sales.find().into(new ArrayList<Document>());                            

                            for (Document document : documents) {                                                                
                                addToSaleList(
                                        document.getString("client"),
                                        document.getString("invoice"),
                                        Float.parseFloat(document.getString("subtotal")),
                                        Float.parseFloat(document.getString("discount")),
                                        Float.parseFloat(document.getString("total")),
                                        sdf.format(document.get("saledate"))
                                );
                            }
                        } catch (Exception e) {
                            System.out.println(dbType + " error " + ": " + e.getMessage());
                        }
                    } else {
                        try {

                            BasicDBObject andquery = new BasicDBObject();
                            List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
                            obj.add(new BasicDBObject("name", saleSearchString));
                            andquery.put("$and", obj);
                            List<Document> documents = sales.find().filter(andquery).into(new ArrayList<Document>());

                            if (documents.size() != 0) {
                                for (Document document : documents) {
                                    addToSaleList(
                                            document.getString("client"),
                                            document.getString("invoice"),
                                            Float.parseFloat(document.getString("subtotal")),
                                            Float.parseFloat(document.getString("discount")),
                                            Float.parseFloat(document.getString("total")),
                                            sdf.format(document.get("saledate"))
                                    );
                                }
                            } else {
                                sendAlert("Information",
                                        "Results",
                                        "No results found with " + saleSearchString,
                                        Alert.AlertType.INFORMATION);
                            }

                        } catch (Exception e) {
                            System.out.println(dbType + " error " + ": " + e.getMessage());
                        }

                    }
                    break;
                case firebird:
                    st = this.connection.createStatement();
                    if (saleSearchString.equals("")) {
                        try {
                            rs = st.executeQuery(
                                    "select first 50 * "
                                    + " FROM searchSale "
                            );
                            while (rs.next()) {
                                addToSaleList(
                                        rs.getString("clientname"),
                                        rs.getString("invoice"),
                                        rs.getFloat("subtotal"),
                                        rs.getFloat("discount"),
                                        rs.getFloat("total"),
                                        rs.getTimestamp("saledate")
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
                                    "EXECUTE PROCEDURE search_a_sale( '" + saleSearchString + "');"
                            );
                            if (rs.next()) {
                                addToSaleList(
                                        rs.getString("clientname"),
                                        rs.getString("invoice"),
                                        rs.getFloat("subtotal"),
                                        rs.getFloat("discount"),
                                        rs.getFloat("total"),
                                        rs.getTimestamp("saledate")
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
                                addToSaleList(
                                        rs.getString("clientname"),
                                        rs.getString("invoice"),
                                        rs.getFloat("subtotal"),
                                        rs.getFloat("discount"),
                                        rs.getFloat("total"),
                                        rs.getTimestamp("saledate")
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
                    st = this.connection.createStatement();
                    if (saleSearchString.equals("")) {
                        try {
                            st = this.connection.createStatement();
                            rs = st.executeQuery(
                                    "select * "
                                    + " FROM searchpurchases;"
                            );
                            while (rs.next()) {
                                addToSaleList(
                                        rs.getString("clientname"),
                                        rs.getString("invoice"),
                                        rs.getFloat("subtotal"),
                                        rs.getFloat("discout"),
                                        rs.getFloat("total"),
                                        rs.getTimestamp("saledate")
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
                                    + " search_a_purchase('" + saleSearchString + "')"
                            );
                            if (rs.next()) {
                                addToSaleList(
                                        rs.getString("clientname"),
                                        rs.getString("invoice"),
                                        rs.getFloat("subtotal"),
                                        rs.getFloat("discout"),
                                        rs.getFloat("total"),
                                        rs.getTimestamp("saledate")
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
                                addToSaleList(
                                        rs.getString("clientname"),
                                        rs.getString("invoice"),
                                        rs.getFloat("subtotal"),
                                        rs.getFloat("discout"),
                                        rs.getFloat("total"),
                                        rs.getTimestamp("saledate")
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
        saleTable.setItems(data);

    }

}
