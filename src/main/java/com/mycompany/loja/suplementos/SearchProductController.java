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
import supportClasses.Product;
import supportClasses.Supplier;
import supportClasses.Type;
import supportClasses.databaseType;

/**
 *
 * @author vinicius
 */
public class SearchProductController extends ControllerModel {

    @FXML
    public TextField productSearchTextField;

    @FXML
    public TableColumn<Product, String> nameColumn;

    @FXML
    public TableColumn<Product, String> brandColumn;

    @FXML
    public TableColumn<Product, String> typeColumn;

    @FXML
    public TableColumn<Product, Integer> minimumQuantityColumn;

    @FXML
    public TableColumn<Product, Integer> currentQuantityColumn;

    @FXML
    public TableColumn<Product, Float> unitValueColumn;

    @FXML
    public TableColumn<Product, String> unitTypeColumn;

    @FXML
    public javafx.scene.control.TableView<Product> productTable;

    public ObservableList<Product> data;

    public Stage dialog;

    public SearchProductController(Connection db) {
        super(db);
    }

    SearchProductController(Connection connection, databaseType dbType) {
        super(connection, dbType);
    }

    SearchProductController(MongoDatabase mongoDatabase, databaseType dbType) {
        super(mongoDatabase, dbType);
    }

    public void init(Stage modal) {

        dialog = modal;

        data = FXCollections.observableArrayList();

        // the property name between "" has to be the same name of the attribute on the class
        nameColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("brandname"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("typename"));
        minimumQuantityColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("minimumQuantity"));
        currentQuantityColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("currentQuantity"));
        unitValueColumn.setCellValueFactory(new PropertyValueFactory<Product, Float>("unitValue"));
        unitTypeColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("unit"));

        productTable.setItems(data);

        productSearchTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    Search(new ActionEvent());
                }
            }
        });

    }

    public void addToProductList(String productname, String brandname,
            String typename, Integer minimumquantity, Integer currentquantity, Float unitvalue, String unitType) {
        data.add(
                new Product(productname, minimumquantity, currentquantity, unitvalue, unitType, brandname, typename)
        );
    }

    @FXML
    public void Search(ActionEvent event) {

        data.clear();
        String productSearchString = null;
        try {
            productSearchString = productSearchTextField.getText();
        } catch (Exception e) {

        }

        if (productSearchString == null) {
            sendAlert("Error finding Product",
                    "No Product Fantasy name to search.",
                    "Pick a Product Fantasy name to search.",
                    Alert.AlertType.ERROR);
        }

        switch (this.dbType) {
            case mongodb:
                MongoCollection<Document> products = mongoDatabase.getCollection("products");
                if (productSearchString.equals("")) {
                    try {

                        List<Document> documents = products.find().into(new ArrayList<Document>());

                        for (Document document : documents) {
                            addToProductList(
                                    document.getString("name"),
                                    document.getString("brandname"),
                                    document.getString("typename"),
                                    document.getInteger("minimumquantity"),
                                    document.getInteger("currentquantity"),
                                    document.getDouble("unitvalue").floatValue(),
                                    document.getString("unittype")                                    
                            );
                        }
                    } catch (Exception e) {
                        System.out.println(dbType + " error " + ": " + e.getMessage());
                    }
                } else {
                    try {

                        BasicDBObject andquery = new BasicDBObject();
                        List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
                        obj.add(new BasicDBObject("name", productSearchString));
                        andquery.put("$and", obj);
                        List<Document> documents = products.find().filter(andquery).into(new ArrayList<Document>());

                        if (documents.size() != 0) {
                            for (Document document : documents) {
                                addToProductList(
                                        document.getString("name"),
                                        document.getString("brandname"),
                                        document.getString("typename"),
                                        document.getInteger("minimumquantity"),
                                        document.getInteger("currentquantity"),
                                        document.getDouble("unitvalue").floatValue(),
                                        document.getString("unittype")
                                );
                            }
                        } else {
                            sendAlert("Information",
                                    "Results",
                                    "No results found with " + productSearchString,
                                    Alert.AlertType.INFORMATION);
                        }

                    } catch (Exception e) {
                        System.out.println(dbType + " error " + ": " + e.getMessage());
                    }
                }
                break;
            case firebird:
                if (productSearchString.equals("")) {
                    try {
                        Statement st = this.connection.createStatement();
                        ResultSet rs = st.executeQuery(
                                "select * from search_products;"
                        );
                        while (rs.next()) {
                            addToProductList(
                                    rs.getString("pname"),
                                    rs.getString("brandname"),
                                    rs.getString("typename"),
                                    rs.getInt("minimumquantity"),
                                    rs.getInt("currentquantity"),
                                    rs.getFloat("unitvalue"),
                                    rs.getString("unittype"));
                        }
                        rs.close();
                        st.close();

                    } catch (Exception e) {
                        System.out.println("ERROR " + e.getMessage());

                    }

                } // find a specific suplier name
                else {
                    try {
                        Statement st = this.connection.createStatement();
                        ResultSet rs = st.executeQuery(
                                "EXECUTE PROCEDURE get_a_product('" + productSearchString + "');"
                        );
                        if (rs.next()) {
                            addToProductList(
                                    rs.getString("pname"),
                                    rs.getString("brandname"),
                                    rs.getString("typename"),
                                    rs.getInt("minimumquantity"),
                                    rs.getInt("currentquantity"),
                                    rs.getFloat("unitvalue"),
                                    rs.getString("unittype"));
                        } else {
                            sendAlert(
                                    "No results!",
                                    "Search lead to 0 results. Try a different Product Name",
                                    "No Product with this name",
                                    Alert.AlertType.INFORMATION);
                            return;
                        }
                        while (rs.next()) {
                            addToProductList(
                                    rs.getString("pname"),
                                    rs.getString("brandname"),
                                    rs.getString("typename"),
                                    rs.getInt("minimumquantity"),
                                    rs.getInt("currentquantity"),
                                    rs.getFloat("unitvalue"),
                                    rs.getString("unittype"));
                        }
                        rs.close();
                        st.close();
                    } catch (Exception e) {
                        System.out.println("ERRORS " + e.getMessage());
                    }
                }
                break;
            case postgres:
                // if no brand name find all
                if (productSearchString.equals("")) {
                    try {
                        Statement st = this.connection.createStatement();
                        ResultSet rs = st.executeQuery(
                                "select * from get_products();"
                        );
                        while (rs.next()) {
                            addToProductList(
                                    rs.getString("productname"),
                                    rs.getString("brandname"),
                                    rs.getString("typename"),
                                    rs.getInt("minimumquantity"),
                                    rs.getInt("currentquantity"),
                                    rs.getFloat("unitvalue"),
                                    rs.getString("unittype"));
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
                                "select * FROM get_a_product('" + productSearchString + "')"
                        );
                        if (rs.next()) {
                            addToProductList(
                                    rs.getString("productname"),
                                    rs.getString("brandname"),
                                    rs.getString("typename"),
                                    rs.getInt("minimumquantity"),
                                    rs.getInt("currentquantity"),
                                    rs.getFloat("unitvalue"),
                                    rs.getString("unittype"));
                        } else {
                            sendAlert(
                                    "No results!",
                                    "Search lead to 0 results. Try a different Product Name",
                                    "No Product with this name",
                                    Alert.AlertType.INFORMATION);
                            return;
                        }
                        while (rs.next()) {
                            addToProductList(
                                    rs.getString("name"),
                                    rs.getString("brandname"),
                                    rs.getString("typename"),
                                    rs.getInt("minimumquantity"),
                                    rs.getInt("currentquantity"),
                                    rs.getFloat("unitvalue"),
                                    rs.getString("unittype"));
                        }
                        rs.close();
                        st.close();
                    } catch (Exception e) {
                        System.out.println("ERRORS " + e.getMessage());
                    }
                }
                break;

        }

        // set items        
        productTable.setItems(data);

    }

}
