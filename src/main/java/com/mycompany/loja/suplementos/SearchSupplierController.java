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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
import supportClasses.Supplier;
import supportClasses.databaseType;

/**
 *
 * @author vinicius
 */
public class SearchSupplierController extends ControllerModel {

    @FXML
    public TextField supplierSearchTextField;

    @FXML
    public TableColumn<Supplier, String> socialColumn;

    @FXML
    public TableColumn<Supplier, String> emailColumn;

    @FXML
    public TableColumn<Supplier, String> cnpjColumn;

    @FXML
    public TableColumn<Supplier, String> telephoneColumn;

    @FXML
    public TableColumn<Supplier, String> fantasyNameColumn;

    @FXML
    public TableColumn<Supplier, Integer> purchasesColumn;

    @FXML
    public javafx.scene.control.TableView<Supplier> supplierTable;

    public ObservableList<Supplier> data;

    public Stage dialog;

    public SearchSupplierController(Connection db) {
        super(db);
    }

    SearchSupplierController(Connection connection, databaseType dbType) {
        super(connection, dbType);
    }

    SearchSupplierController(MongoDatabase mongoDatabase, databaseType dbType) {
        super(mongoDatabase, dbType);
    }

    public void init(Stage modal) {

        dialog = modal;

        data = FXCollections.observableArrayList();

        // the property name between "" has to be the same name of the attribute on the class
        socialColumn.setCellValueFactory(new PropertyValueFactory<Supplier, String>("socialReason"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<Supplier, String>("email"));
        cnpjColumn.setCellValueFactory(new PropertyValueFactory<Supplier, String>("cnpj"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<Supplier, String>("telephone"));
        fantasyNameColumn.setCellValueFactory(new PropertyValueFactory<Supplier, String>("fantasyName"));
        purchasesColumn.setCellValueFactory(new PropertyValueFactory<Supplier, Integer>("purchases"));

        supplierTable.setItems(data);

        supplierSearchTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    Search(new ActionEvent());
                }
            }
        });

    }

    public void addToSupplierList(String socialreason, String email,
            String cnpj, String telephone, String fantasyname, Integer purchases) {
        data.add(
                new Supplier(socialreason, email, cnpj, telephone, fantasyname, purchases)
        );
    }

    @FXML
    public void Search(ActionEvent event) {

        data.clear();
        String supplierSearchString = null;
        try {
            supplierSearchString = supplierSearchTextField.getText();
        } catch (Exception e) {

        }

        if (supplierSearchString == null) {
            sendAlert("Error finding Supplier",
                    "No Supplier Fantasy name to search.",
                    "Pick a Supplier Fantasy name to search.",
                    Alert.AlertType.ERROR);
        }

        // if no brand name find all
        try {
            switch (dbType) {
                case mongodb:
                    MongoCollection<Document> suppliers = mongoDatabase.getCollection("suppliers");
                    if (supplierSearchString.equals("")) {
                        try {

                            List<Document> documents = suppliers.find().into(new ArrayList<Document>());                            

                            System.out.println("vai começar a pegar os documentos");
                            for (Document document : documents) {
                                addToSupplierList(
                                        document.getString("socialreason"),
                                        document.getString("email"),
                                        document.getString("cnpj"),
                                        document.getString("telephone"),
                                        document.getString("fantasyname"),
                                        document.getInteger("numberpurchases")
                                );
                            }
                        } catch (Exception e) {
                            System.out.println(dbType + " error " + ": " + e.getMessage() + " ");
                            e.printStackTrace();
                        }
                    } else {
                        try {

                            BasicDBObject andquery = new BasicDBObject();
                            List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
                            obj.add(new BasicDBObject("fantasyname", supplierSearchString));
                            andquery.put("$and", obj);
                            List<Document> documents = suppliers.find().filter(andquery).into(new ArrayList<Document>());

                            if (documents.size() != 0) {
                                for (Document document : documents) {
                                    addToSupplierList(
                                        document.getString("socialreason"),
                                        document.getString("email"),
                                        document.getString("cnpj"),
                                        document.getString("telephone"),
                                        document.getString("fantasyname"),
                                        document.getInteger("numberpurchases")
                                );
                                }
                            } else {
                                sendAlert("Information",
                                        "Results",
                                        "No results found with " + supplierSearchString,
                                        Alert.AlertType.INFORMATION);
                            }

                        } catch (Exception e) {
                            System.out.println(dbType + " error " + ": " + e.getMessage());
                        }

                    }
                    break;
                case firebird:
                    if (supplierSearchString.equals("")) {
                        try {
                            Statement st = this.connection.createStatement();
                            ResultSet rs = st.executeQuery(
                                    "select first 50 * "
                                    + " FROM suppliers "
                            );
                            while (rs.next()) {
                                addToSupplierList(
                                        rs.getString("socialreason"),
                                        rs.getString("email"),
                                        rs.getString("cnpj"),
                                        rs.getString("telephone"),
                                        rs.getString("fantasyname"),
                                        rs.getInt("numberpurchases"));
                            }
                            rs.close();
                            st.close();

                        } catch (Exception e) {

                        }

                    } // find a specific suplier name
                    else {
                        try {
                            Statement st = this.connection.createStatement();
                            ResultSet rs = st.executeQuery(
                                    "select * "
                                    + " FROM suppliers"
                                    + " WHERE fantasyname = '" + supplierSearchString + "'"
                            );
                            if (rs.next()) {
                                addToSupplierList(
                                        rs.getString("socialreason"),
                                        rs.getString("email"),
                                        rs.getString("cnpj"),
                                        rs.getString("telephone"),
                                        rs.getString("fantasyname"),
                                        rs.getInt("numberpurchases"));
                            } else {
                                sendAlert(
                                        "No results!",
                                        "Search lead to 0 results. Try a different Supplier Name",
                                        "No Supplier with this name",
                                        Alert.AlertType.INFORMATION);
                                return;
                            }
                            while (rs.next()) {
                                addToSupplierList(
                                        rs.getString("socialreason"),
                                        rs.getString("email"),
                                        rs.getString("cnpj"),
                                        rs.getString("telephone"),
                                        rs.getString("fantasyname"),
                                        rs.getInt("numberpurchases"));
                            }
                            rs.close();
                            st.close();
                        } catch (Exception e) {

                        }
                    }
                    break;
                case postgres:
                    if (supplierSearchString.equals("")) {
                        try {
                            Statement st = this.connection.createStatement();
                            ResultSet rs = st.executeQuery(
                                    "select * "
                                    + " FROM suppliers "
                                    + " limit 50"
                            );
                            while (rs.next()) {
                                addToSupplierList(
                                        rs.getString("socialreason"),
                                        rs.getString("email"),
                                        rs.getString("cnpj"),
                                        rs.getString("telephone"),
                                        rs.getString("fantasyname"),
                                        rs.getInt("numberpurchases"));
                            }
                            rs.close();
                            st.close();

                        } catch (Exception e) {

                        }

                    } // find a specific suplier name
                    else {
                        try {
                            Statement st = this.connection.createStatement();
                            ResultSet rs = st.executeQuery(
                                    "select * "
                                    + " FROM suppliers"
                                    + " WHERE fantasyname = '" + supplierSearchString + "'"
                            );
                            if (rs.next()) {
                                addToSupplierList(
                                        rs.getString("socialreason"),
                                        rs.getString("email"),
                                        rs.getString("cnpj"),
                                        rs.getString("telephone"),
                                        rs.getString("fantasyname"),
                                        rs.getInt("numberpurchases"));
                            } else {
                                sendAlert(
                                        "No results!",
                                        "Search lead to 0 results. Try a different Supplier Name",
                                        "No Supplier with this name",
                                        Alert.AlertType.INFORMATION);
                                return;
                            }
                            while (rs.next()) {
                                addToSupplierList(
                                        rs.getString("socialreason"),
                                        rs.getString("email"),
                                        rs.getString("cnpj"),
                                        rs.getString("telephone"),
                                        rs.getString("fantasyname"),
                                        rs.getInt("numberpurchases"));
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
        supplierTable.setItems(data);

    }

}
