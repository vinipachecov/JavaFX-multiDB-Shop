/*
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;
import supportClasses.Type;
import supportClasses.databaseType;

/**
 * FXML Controller class
 *
 * @author vinicius
 */
public class AddProductController extends ControllerModel {

    @FXML
    public Button addUserButton;

    @FXML
    public Button cancelAddUserButton;

    @FXML
    public TextField nameTextField;

    @FXML
    public TextField minimumQuantityTextField;

    @FXML
    public TextField currentQuantityTextField;

    @FXML
    public TextField unitValueTextField;

    @FXML
    public TextField unitTextField;

    @FXML
    public ComboBox<String> typeComboBox;

    @FXML
    public ComboBox<String> brandComboBox;

    public Stage dialog;

    public ArrayList<String> brandsList;
    public ArrayList<String> typesList;

    public AddProductController(Connection connection) {
        super(connection);

    }

    AddProductController(Connection connection, databaseType dbType) {
        super(connection, dbType);
    }

    AddProductController(MongoDatabase mongoDatabase, databaseType dbType) {
        super(mongoDatabase, dbType);
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    public void cancel(ActionEvent event) {
        dialog.close();
    }

    public void getBrands() {

        switch (this.dbType) {
            case mongodb:
                MongoCollection<Document> brands = mongoDatabase.getCollection("brands");

                try {
                    List<Document> documents = brands.find().into(new ArrayList<Document>());
                    for (Document document : documents) {
                        brandComboBox.getItems().add(document.getString("name"));
                    }
                } catch (Exception e) {
                    System.out.println(dbType + " error " + ": " + e.getMessage());
                }
                break;
            default:
                try {
                    Statement st = this.connection.createStatement();
                    ResultSet rs = st.executeQuery("Select name from getbrands");
                    while (rs.next()) {
                        brandComboBox.getItems().add(rs.getString("name"));
                    }
                } catch (Exception e) {
                }
                break;
        }
    }

    public void getTypes() {
        switch (this.dbType) {
            case mongodb:
                 MongoCollection<Document> types = mongoDatabase.getCollection("types");

                try {
                    List<Document> documents = types.find().into(new ArrayList<Document>());
                    for (Document document : documents) {
                        typeComboBox.getItems().add(document.getString("name"));
                    }
                } catch (Exception e) {
                    System.out.println(dbType + " error " + ": " + e.getMessage());
                }
                break;
            default:
                try {
                    Statement st = this.connection.createStatement();
                    ResultSet rs = st.executeQuery("Select name from gettypes");
                    while (rs.next()) {
                        typeComboBox.getItems().add(rs.getString("name"));
                    }
                } catch (Exception e) {
                }
                break;
        }
    }

    public void init(Stage modal) {
        brandComboBox.getItems().removeAll(brandComboBox.getItems());
        getBrands();
        getTypes();
        dialog = modal;
    }

    public void addProduct(String name, String brandname, String typename,
            Integer minimumQ, Integer currentQ, Float unitvalue, String unittype) {

        switch (this.dbType) {
            case mongodb:
                try {
                    MongoCollection<Document> products = mongoDatabase.getCollection("products");

                    Document newproduct = new Document();
                    newproduct.put("name", name);
                    newproduct.put("brandname", brandname);
                    newproduct.put("typename", typename);
                    newproduct.put("minimumquantity", minimumQ);
                    newproduct.put("currentquantity", currentQ);
                    newproduct.put("unitvalue", unitvalue);
                    newproduct.put("unittype", unittype);
                    products.insertOne(newproduct);
                    sendAlert("Product type added with success!",
                            "Type Added",
                            "A new type have been added!",
                            Alert.AlertType.CONFIRMATION);
                } catch (Exception e) {
                    System.out.println("Erro no banco " + dbType + ": " + e.getMessage());
                }
                break;
            case firebird:
                try {
                    System.out.println("COMEÇOU A ADICIONAR");
                    Statement st = this.connection.createStatement();
                    st.executeUpdate(
                            "EXECUTE PROCEDURE SP_ADDPRODUCT("
                            + "'" + name + "',"
                            + " '" + brandname + "',"
                            + " '" + typename + "',"
                            + " " + minimumQ + ","
                            + " " + currentQ + ","
                            + " " + unitvalue + ","
                            + " '" + unittype + "');"
                    );
                    st.close();
                } catch (Exception e) {
                    System.out.println("ERROR " + e.getMessage());
                }
                sendAlert("Product Added!",
                        "Success!",
                        "Success adding a product!",
                        Alert.AlertType.CONFIRMATION);
                break;
            case postgres:
                try {
                    Statement st = this.connection.createStatement();
                    ResultSet rs = st.executeQuery(
                            "DO $$ BEGIN\n"
                            + "    PERFORM add_product("
                            + "'" + name + "',"
                            + "'" + brandname + "',"
                            + "'" + typename + "', " + minimumQ + ","
                            + "" + currentQ + "," + unitvalue + ","
                            + "'" + unittype + "');\n"
                            + "END $$;"
                    );
                    st.close();
                } catch (Exception e) {
                }
                sendAlert("Product Added!",
                        "Success!",
                        "Success adding a product!",
                        Alert.AlertType.CONFIRMATION);
                break;

        }

    }

    public boolean productAlreadyExists(String productname, String brandname) {

        switch (this.dbType) {
            case mongodb:
                MongoCollection<Document> types = mongoDatabase.getCollection("types");

                BasicDBObject andquery = new BasicDBObject();
                List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
                obj.add(new BasicDBObject("name", productname));
                obj.add(new BasicDBObject("brandname", brandname));
                andquery.put("$and", obj);
                List<Document> documents = types.find().filter(andquery).into(new ArrayList<Document>());

                if (documents.size() != 0) {
                    sendAlert("Error to add a Product",
                            "Product exists.",
                            "Product already Exists! Choose a different product name!",
                            Alert.AlertType.ERROR);
                    return true;
                }
                break;
            case postgres:
                try {
                    Statement st = this.connection.createStatement();
                    ResultSet rs = st.executeQuery(
                            "select hasproduct from product_exists('" + productname + "' ,"
                            + " '" + brandname + "')"
                    );
                    rs.next();
                    if (rs.getString("hasproduct").equals("t")) {
                        sendAlert("Error to add a Product",
                                "Product exists.",
                                "Product already Exists! Choose a different product name!",
                                Alert.AlertType.ERROR);
                        return true;
                    }
                    rs.close();
                    st.close();

                } catch (Exception e) {
                    System.out.println("Error product already exists! :" + e.getMessage());
                }
                break;
            case firebird:
                try {
                    Statement st = this.connection.createStatement();
                    st.executeUpdate(
                            "EXECUTE PROCEDURE hasproduct('" + productname + "', "
                            + "'" + brandname + "')");
                    System.out.println("PRODUTO DIFERENTE YES!");
                    // no similar product in the database
                    return false;
                } catch (Exception e) {
                    sendAlert("Error to add a Product",
                            "Product exists in the database.",
                            "Product already Exists! Choose a different product name!",
                            Alert.AlertType.ERROR);
                    System.out.println("Error product already exists! :" + e.getMessage());
                    return true;
                }
        }

        return false;
    }

    @FXML
    public void checkForm() {
        String name = nameTextField.getText();
        String brandname = brandComboBox.getValue();
        String typename = typeComboBox.getValue();
        Integer minimumQ = Integer.parseInt(minimumQuantityTextField.getText());
        Integer currentQ = Integer.parseInt(currentQuantityTextField.getText());
        Float unitvalue = Float.parseFloat(unitValueTextField.getText());
        String unittype = unitTextField.getText();

        if (name.equals("") && brandname.equals("") || brandname == null 
                || typename.equals("") || typename == null
                || minimumQ.equals(null) || currentQ.equals(null)
                || unitvalue.equals(null) || unittype.equals("")) {
            sendAlert("Error to add a Product",
                    "Form Error",
                    "Fill all the fields", Alert.AlertType.ERROR);
        } else {
            if (!productAlreadyExists(name, brandname)) {
                addProduct(name, brandname, typename, minimumQ, currentQ, unitvalue, unittype);
            }
        }
    }
}
