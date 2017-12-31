/*
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.loja.suplementos;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;
import supportClasses.ProductItem;
import supportClasses.databaseType;

/**
 * FXML Controller class
 *
 * @author vinicius
 */
public class AddSaleItemController extends ControllerModel {

    @FXML
    public Button addUserButton;

    @FXML
    public Button cancelAddUserButton;

    @FXML
    public TextField quantityTextField;

    @FXML
    public TextField unitValueTextField;

    @FXML
    public ComboBox<String> findProductComboBox;

    public ObservableList<ProductItem> auxdata;

    @FXML
    public Label totalLabel;

    public Integer saleID;

    public Stage dialog;

    public TableView saleTable;

    public Label subtotalAddSaleScreen;

    public Label totalAddSaleScreen;

    public AddSaleController asic;

    public Document currentSale;

    public AddSaleItemController(Connection connection) {
        super(connection);

    }

    AddSaleItemController(Connection connection, databaseType dbType) {
        super(connection, dbType);
    }

    AddSaleItemController(MongoDatabase mongoDatabase, databaseType dbType) {
        super(mongoDatabase, dbType);
    }

    AddSaleItemController(MongoDatabase mongoDatabase, Document currentSale, databaseType dbType) {
        super(mongoDatabase, dbType);
        this.currentSale = currentSale;
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    public void cancel(ActionEvent event) {
        dialog.close();
    }

    public void init(Stage modal, Integer id, TableView saletable, ObservableList<ProductItem> listdata,
            AddSaleController asic) {
        this.asic = asic;
        this.subtotalAddSaleScreen = subtotalAddSaleScreen;
        this.totalAddSaleScreen = totalAddSaleScreen;
        this.auxdata = listdata;
        saleTable = saletable;
        dialog = modal;
        this.saleID = id;
        getProducts();
    }

    public void init(Stage modal, TableView saletable, ObservableList<ProductItem> listdata,
            AddSaleController asic) {
        this.asic = asic;
        this.subtotalAddSaleScreen = subtotalAddSaleScreen;
        this.totalAddSaleScreen = totalAddSaleScreen;
        this.auxdata = listdata;
        saleTable = saletable;
        dialog = modal;
        getProducts();
    }

    @FXML
    public void recalculateTotal(ActionEvent event) {
        Float quantity = Float.parseFloat(quantityTextField.getText());
        Float unitvalue = Float.parseFloat(unitValueTextField.getText());
        Float total = quantity * unitvalue;
        totalLabel.setText(total.toString());
    }

    public void addToTable(String itemname, Integer Quantity, Float total, Float unitvalue) {

        String brandname = "";
        String typename = "";
        try {
            Statement st = null;
            ResultSet rs = null;
            switch (dbType) {
                case mongodb:

                    MongoCollection<Document> products = mongoDatabase.getCollection("products");

                    FindIterable<Document> documents = products.find(eq("name", findProductComboBox.getValue()));
                    if (documents == null) {

                        return;
                    } else {
                        typename = documents.first().getString("typename");
                        brandname = documents.first().getString("brandname");
                    }
                    break;
                case firebird:
                    st = this.connection.createStatement();
                    rs = st.executeQuery(
                            "EXECUTE PROCEDURE get_a_typeby_product('" + findProductComboBox.getValue() + "');"
                    );
                    if (rs.next()) {
                        typename = rs.getString("TYPENAME");
                    } else {
                        System.out.println("ERROR");
                    }
                    break;
                case postgres:
                    st = this.connection.createStatement();
                    rs = st.executeQuery(
                            "select * from get_a_typeby_product('" + findProductComboBox.getValue() + "')"
                    );
                    if (rs.next()) {
                        typename = rs.getString("TYPENAME");
                    } else {
                        System.out.println("ERROR");
                    }
                    break;
            }

            switch (dbType) {
                case firebird:
                    st = this.connection.createStatement();
                    rs = st.executeQuery(
                            "EXECUTE PROCEDURE get_a_brandby_product('" + findProductComboBox.getValue() + "');"
                    );
                    if (rs.next()) {
                        brandname = rs.getString("BRANDNAME");
                    }
                    break;
                case postgres:
                    st = this.connection.createStatement();
                    rs = st.executeQuery(
                            "select * from get_a_brandby_product('" + findProductComboBox.getValue() + "')"
                    );
                    if (rs.next()) {
                        brandname = rs.getString("BRANDNAME");
                    }
                    break;
            }
        } catch (Exception e) {
            System.out.println("ERROR GETTING TYPES AND BRANDS " + e.getMessage() + " " + e.getCause());
            sendAlert(
                    "Error ",
                    "Error retrieving values.",
                    "Error getting type or brand names", Alert.AlertType.ERROR);
        }

        auxdata.add(new ProductItem(itemname, unitvalue, brandname, typename, Quantity, total));
        saleTable.setItems(auxdata);
        asic.calculateSubtotalAndTotal();
    }

    public void getProducts() {
        try {
            Statement st = null;
            ResultSet rs = null;
            switch (dbType) {
                case mongodb:
                    MongoCollection<Document> products = mongoDatabase.getCollection("products");

                    try {
                        List<Document> documents = products.find().into(new ArrayList<Document>());
                        for (Document document : documents) {
                            findProductComboBox.getItems().add(document.getString("name"));
                        }
                    } catch (Exception e) {
                        System.out.println(dbType + " error " + ": " + e.getMessage());
                    }
                    break;
                case firebird:
                    st = this.connection.createStatement();
                    rs = st.executeQuery(
                            "SELECT productname FROM get_products_additem;");
                    while (rs.next()) {
                        findProductComboBox.getItems().add(rs.getString("productname"));
                    }
                    break;
                case postgres:
                    st = this.connection.createStatement();
                    rs = st.executeQuery(
                            "select * from get_products();");
                    while (rs.next()) {
                        findProductComboBox.getItems().add(rs.getString("productname"));
                    }
                    break;
            }
        } catch (Exception e) {
            sendAlert("Error",
                    "Error Retreiving Products",
                    "Error on function to get products",
                    Alert.AlertType.ERROR);
        }

    }

    @FXML
    private void getUnitValue() {
        Float valperUnit = null;
        try {
            Statement st = null;
            ResultSet rs = null;
            switch (dbType) {
                case mongodb:
                    MongoCollection<Document> products = mongoDatabase.getCollection("products");

                    try {
                        List<Document> documents = products.find(eq("name", findProductComboBox.getValue().toString())).into(new ArrayList<Document>());
                        double aux;
                        for (Document document : documents) {
                            aux = (double) document.get("unitvalue");
                            valperUnit = (float) aux;
                        }
                    } catch (Exception e) {
                        System.out.println(dbType + " error " + ": " + e.getMessage());
                    }
                    break;
                case firebird:
                    st = this.connection.createStatement();
                    rs = st.executeQuery(
                            "EXECUTE PROCEDURE get_itemunitvalue('" + findProductComboBox.getValue() + "');"
                    );
                    if (rs.next()) {
                        valperUnit = (Float) rs.getFloat("UNITVALUE");
                    }
                    st.close();
                    break;
                case postgres:
                    st = this.connection.createStatement();
                    rs = st.executeQuery(
                            "select * from get_itemunitvalue('" + findProductComboBox.getValue() + "')"
                    );
                    if (rs.next()) {
                        valperUnit = rs.getFloat("get_itemunitvalue");
                    }
                    st.close();
                    break;

            }
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
            return;
        }
        quantityTextField.setText("1");
        unitValueTextField.setText(valperUnit.toString());
        calculateTotal();
    }

    public void calculateTotal() {
        Float valperunit = Float.parseFloat(unitValueTextField.getText());
        Float quantity = Float.parseFloat(quantityTextField.getText());
        Float total = valperunit * quantity;
        totalLabel.setText(total.toString());
    }

    public void addItem(String itemname, Integer Quantity, Float unitValue) {

        try {
            Statement st = null;
            switch (dbType) {
                case mongodb:

                    ArrayList<BasicDBObject> saleitems = (ArrayList<BasicDBObject>) currentSale.get("saleitems");
                    BasicDBObject properties = new BasicDBObject();

                    properties.append("productname", itemname);
                    properties.append("unitvalue", unitValue);
                    properties.append("quantity", Quantity);
                    properties.append("total", totalLabel.getText());

                    if (saleitems.size() != 0) {

                        saleitems.add(properties);
                        currentSale.append("saleitems", saleitems);

                    } else {

                        List<BasicDBObject> saleitem = new ArrayList<BasicDBObject>();

                        saleitem.add(properties);

                        currentSale.append("saleitems", saleitem);

                    }

                    break;
                case firebird:
                    st = this.connection.createStatement();
                    st.executeUpdate(
                            "EXECUTE PROCEDURE add_saleitem("
                            + "" + saleID + ","
                            + "'" + itemname + "',"
                            + "" + unitValue + "  ,"
                            + "" + Quantity + ", "
                            + "" + totalLabel.getText() + ");"
                    );
                    break;
                case postgres:
                    st = this.connection.createStatement();
                    st.executeUpdate(
                            "DO $$ BEGIN\n"
                            + "    PERFORM add_saleitem("
                            + "" + saleID + ","
                            + "'" + itemname + "',"
                            + "" + unitValue + "  ,"
                            + "" + Quantity + ", "
                            + "" + totalLabel.getText() + ");\n"
                            + "END $$;"
                    );
                    break;
            }

            sendAlert("SaleItem added with success!", "SaleItem Added", "A saleItem has been added!", Alert.AlertType.CONFIRMATION);
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage() + e.getLocalizedMessage());
            return;
        }

        addToTable(itemname, Quantity, Float.parseFloat(totalLabel.getText()), unitValue);
    }

    public boolean checkItemAlreadyExists(String itemname) {

        try {

            switch (dbType) {
                case mongodb:
                    try {
                        MongoCollection<Document> sales = mongoDatabase.getCollection("sales");

                        ArrayList<BasicDBObject> saleitems = (ArrayList<BasicDBObject>) currentSale.get("saleitems");

                        for (Object saleitem : saleitems) {
                            BasicDBObject aux = (BasicDBObject) saleitem;
                        }

                        if (saleitems.size() != 0) {

                            for (Object saleitem : saleitems) {
                                BasicDBObject aux = (BasicDBObject) saleitem;

                                if (aux.getString("productname").equals(itemname)) {
                                    sendAlert("Duplication error",
                                            "Item already added.",
                                            "Item already added! Choose a different product!",
                                            Alert.AlertType.ERROR);
                                    return true;
                                }
                            }
                        } else {
                            return false;
                        }
                        // KEEP GOING

                    } catch (Exception e) {
                        System.out.println(dbType + " error " + ": " + e.getMessage());
                    }
                    break;
                case firebird:
                    Statement st1 = this.connection.createStatement();
                    ResultSet rs = null;
                    st1.executeUpdate("EXECUTE PROCEDURE saleItem_exists(" + saleID + ","
                            + "'" + itemname + "');");
                    st1.close();
                    break;
                case postgres:
                    Statement st2 = this.connection.createStatement();
                    rs = st2.executeQuery(
                            "select * from item_exists(" + saleID + ","
                            + "'" + itemname + "');");
                    rs.next();
                    if (rs.getString("hasitem").equals("t")) {
                        System.out.println("Error to add a item");
                        sendAlert("Duplication error",
                                "Item already added.",
                                "Item already added! Choose a different product!",
                                Alert.AlertType.ERROR);
                        rs.close();
                        return true;
                    }
                    st2.close();
                    break;
            }

        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());
            sendAlert("Duplication error",
                    "Item already added.",
                    "Item already added! Choose a different product!",
                    Alert.AlertType.ERROR);
            return true;
        }

        return false;
    }

    @FXML
    public void checkForm() {

        String itemname = findProductComboBox.getValue();
        Integer quantity = Integer.parseInt(quantityTextField.getText());
        Float unitvalue = Float.parseFloat(unitValueTextField.getText());

        if (itemname.equals("") || quantity.equals("")
                || unitvalue.equals("")) {
            sendAlert("Error to add an Item",
                    "Form Error",
                    "Fill all the fields", Alert.AlertType.ERROR);
        } else {
            if (!checkItemAlreadyExists(findProductComboBox.getValue())) {
                addItem(itemname, quantity, unitvalue);
            }
        }
    }
}
