/*
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.loja.suplementos;

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
public class AddPurchaseItemController extends ControllerModel {

    @FXML
    public TextField quantityTextField;

    @FXML
    public TextField unitValueTextField;

    @FXML
    public ComboBox<String> findProductComboBox;

    public ObservableList<ProductItem> auxdata;

    @FXML
    public Label totalLabel;

    public Integer purchaseID;

    public Stage dialog;

    public TableView purchaseTable;

    public Label subtotalAddPurchaseScreen;

    public Label totalAddPurchaseScreen;

    public AddPurchaseController apc;

    public Document currentPurchase;

    public AddPurchaseItemController(Connection connection) {
        super(connection);

    }

    AddPurchaseItemController(Connection connection, databaseType dbType) {
        super(connection, dbType);
    }

    AddPurchaseItemController(MongoDatabase mongoDatabase, databaseType dbType, Document currentPurchase) {
        super(mongoDatabase, dbType);
        this.currentPurchase = currentPurchase;
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    public void cancel(ActionEvent event) {
        dialog.close();
    }

    public void init(Stage modal, Integer id, TableView saletable, ObservableList<ProductItem> listdata,
            AddPurchaseController apc) {
        this.apc = apc;
        this.subtotalAddPurchaseScreen = subtotalAddPurchaseScreen;
        this.totalAddPurchaseScreen = totalAddPurchaseScreen;
        this.auxdata = listdata;
        purchaseTable = saletable;
        dialog = modal;
        this.purchaseID = id;
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
                        typename = rs.getString("typename");
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
                        typename = rs.getString("typename");
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
                        brandname = rs.getString("brandname");
                    }
                    break;
                case postgres:
                    st = this.connection.createStatement();
                    rs = st.executeQuery(
                            "select * from get_a_brandby_product('" + findProductComboBox.getValue() + "')"
                    );
                    if (rs.next()) {
                        brandname = rs.getString("brandname");
                    }
                    break;
            }

        } catch (Exception e) {
            sendAlert(
                    "Error ",
                    "Error retrieving values.",
                    "Error getting type or brand names", Alert.AlertType.ERROR);
        }

        auxdata.add(new ProductItem(itemname, unitvalue, brandname, typename, Quantity, total));
        purchaseTable.setItems(auxdata);
        apc.calculateSubtotalAndTotal();
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
                            "select * from get_products_additem;");
                    break;
                case postgres:
                    this.connection.createStatement();
                    rs = st.executeQuery(
                            "select * from get_products();");
                    break;
            }
            while (rs.next()) {
                findProductComboBox.getItems().add(rs.getString("productname"));
            }
        } catch (Exception e) {
        }

    }

    @FXML
    private void getUnitValue(ActionEvent event) {
        Float valperUnit = null;
        try {
            Statement st = null;
            ResultSet rs;
            switch (this.dbType) {
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
                        valperUnit = rs.getFloat("UNITVALUE");
                    }
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

                    ArrayList<BasicDBObject> purchaseitem = (ArrayList<BasicDBObject>) currentPurchase.get("purchaseitems");
                    BasicDBObject properties = new BasicDBObject();

                    properties.append("productname", itemname);
                    properties.append("unitvalue", unitValue);
                    properties.append("quantity", Quantity);
                    properties.append("total", totalLabel.getText());

                    System.out.println("adicionando ->" + properties.toJson());
                    if (purchaseitem.size() != 0) {

                        purchaseitem.add(properties);
                        currentPurchase.append("purchaseitems", purchaseitem);

                    } else {

                        List<BasicDBObject> saleitem = new ArrayList<BasicDBObject>();

                        saleitem.add(properties);

                        currentPurchase.append("purchaseitems", saleitem);

                    }

                    System.out.println("adicionou");

                    break;

                case firebird:
                    st = this.connection.createStatement();
                    st.executeUpdate(
                            "EXECUTE PROCEDURE add_purchaseitem("
                            + "" + purchaseID + ","
                            + "'" + itemname + "',"
                            + "" + unitValue + "  ,"
                            + "" + Quantity + ", "
                            + "" + totalLabel.getText() + ");"
                    );
                    break;
                case postgres:
                    st = this.connection.createStatement();
                    st.executeUpdate("DO $$ BEGIN\n"
                            + "    PERFORM add_purchaseitem("
                            + "" + purchaseID + ","
                            + "'" + itemname + "',"
                            + "" + unitValue + "  ,"
                            + "" + Quantity + ", "
                            + "" + totalLabel.getText() + ");\n"
                            + "END $$;"
                    );
                    break;
            }
            sendAlert("PurchaseItem added with success!", "PurchaseItem Added", "A PurchaseItem has been added!", Alert.AlertType.CONFIRMATION);
        } catch (Exception e) {
            System.out.println("Error adding product item" + e.getMessage() + e.getLocalizedMessage());
            return;
        }

        addToTable(itemname, Quantity, Float.parseFloat(totalLabel.getText()), unitValue);
    }

    public boolean checkItemAlreadyExists(String itemname) {

        try {
            Statement st = null;
            ResultSet rs = null;
            switch (dbType) {
                case mongodb:
                    try {
                        MongoCollection<Document> purchases = mongoDatabase.getCollection("purchases");

                        ArrayList<BasicDBObject> purchaseitems = (ArrayList<BasicDBObject>) currentPurchase.get("purchaseitems");

                        System.out.println("Todos valores");
                        for (Object saleitem : purchaseitems) {
                            BasicDBObject aux = (BasicDBObject) saleitem;
                            System.out.println("saleitem = " + aux.toJson());
                        }

                        if (purchaseitems.size() != 0) {
                            System.out.println("não está vazio");
                            for (Object saleitem : purchaseitems) {
                                BasicDBObject aux = (BasicDBObject) saleitem;
                                System.out.println("vai comparar " + aux.getString("productname") + "com " + itemname);
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
                    st = this.connection.createStatement();
                    st.executeUpdate("EXECUTE PROCEDURE purchaseItem_exists(" + purchaseID + ","
                            + "'" + itemname + "');");
                    break;
                case postgres:
                    st = this.connection.createStatement();
                    rs = st.executeQuery("select * from purchaseItem_exists(" + purchaseID + ","
                            + "'" + itemname + "');");
                    rs.next();
                    if (rs.getString("hasitem").equals("t")) {
                        sendAlert("Duplication error",
                                "Item already added.",
                                "Item already added! Choose a different product!",
                                Alert.AlertType.ERROR);
                        return true;
                    }
                    break;
            }

        } catch (Exception e) {
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
            if (!checkItemAlreadyExists(itemname)) {
                addItem(itemname, quantity, unitvalue);
            }
        }
    }
}
