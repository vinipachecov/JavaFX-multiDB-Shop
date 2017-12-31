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
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;
import supportClasses.databaseType;

/**
 *
 * @author vinicius
 */
public class AddBrandController extends ControllerModel {

    @FXML
    TextField brandTextField;

    public Stage modal;

    public AddBrandController(Connection db) {
        super(db);
    }

    AddBrandController(Connection connection, databaseType dbType) {
        super(connection, dbType);
    }

    AddBrandController(MongoDatabase mongoDatabase, databaseType dbType) {
        super(mongoDatabase, dbType);
    }

    public void init(Stage dialog) {
        modal = dialog;
    }

    @FXML
    public void cancel() {
        modal.close();
    }

    public void addBrand(String brandString) {

        switch (this.dbType) {
            case firebird:
                try {
                    Statement st = this.connection.createStatement();
                    st.executeUpdate(
                            "insert into brands (name)"
                            + " VALUES('" + brandString + "' )"
                    );
                    st.close();
                    sendAlert(
                            "Brand added with success!", 
                            "Brand Added",
                            "A brand have been added!", 
                            Alert.AlertType.CONFIRMATION);
                } catch (Exception e) {
                    System.out.println("Error " + e.getMessage());
                    return;
                }
                break;

            case postgres:
                try {
                    Statement st = this.connection.createStatement();
                    st.executeUpdate(
                            "insert into brands (name)"
                            + " VALUES('" + brandString + "' )"
                    );
                    st.close();
                    sendAlert(
                            "Brand added with success!", 
                            "Brand Added",
                            "A brand have been added!", 
                            Alert.AlertType.CONFIRMATION);
                } catch (Exception e) {
                    System.out.println("Error " + e.getMessage());
                    return;
                }
                break;
            case mongodb:
                try {
                    MongoCollection<Document> types = mongoDatabase.getCollection("brands");
                    
                    Document newbrand = new Document();
                    newbrand.put("name", brandString);
                    types.insertOne(newbrand);                    
                    sendAlert(
                            "Brand added with success!", 
                            "Brand Added",
                            "A brand have been added!", 
                            Alert.AlertType.CONFIRMATION);                    
                } catch (Exception e) {
                    System.out.println("Erro no banco " + dbType + ": " + e.getMessage());
                }
                
                break;

        }

    }

    public boolean checkBrandExists(String brandString) {

        switch (this.dbType) {
            case firebird:
                try {
                    Statement st = this.connection.createStatement();
                    ResultSet rs = st.executeQuery(
                            "select first 1 id "
                            + " FROM brands "
                            + " WHERE name = '" + brandString + "'"
                            );
                    if (rs.next()) {
                        System.out.println("There is already a brand with this name");
                        sendAlert("Error to add a Brand",
                                "Brand exists.",
                                "Brand already Exists! Choose a different brand name!",
                                Alert.AlertType.ERROR);
                        return true;
                    }

                    rs.close();
                    st.close();

                } catch (Exception e) {
                    System.out.println("Error checking brands in " + dbType + " " + e.getMessage());
                }
                break;

            case postgres:
                try {
                    Statement st = this.connection.createStatement();
                    ResultSet rs = st.executeQuery(
                            "select id "
                            + " FROM brands "
                            + " WHERE name = '" + brandString + "'"
                            + " limit 1 ");
                    if (rs.next()) {
                        System.out.println("There is already a brand with this name");
                        sendAlert("Error to add a Brand",
                                "Brand exists.",
                                "Brand already Exists! Choose a different brand name!",
                                Alert.AlertType.ERROR);
                        return true;
                    }

                    rs.close();
                    st.close();

                } catch (Exception e) {

                }
                break;

            case mongodb:
                MongoCollection<Document> types = mongoDatabase.getCollection("brands");

                BasicDBObject andquery = new BasicDBObject();
                List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
                obj.add(new BasicDBObject("name", brandString));
                andquery.put("$and", obj);
                List<Document> documents = types.find().filter(andquery).into(new ArrayList<Document>());

                if (documents.size() != 0) {
                    sendAlert("Error to add a Brand",
                            "Brand exists.",
                            "Brand already Exists! Choose a different brand name!",
                            Alert.AlertType.ERROR);
                    return true;
                }
                break;
        }

        return false;
    }

    @FXML
    public void checkForm() {
        String brandString = null;
        try {
            brandString = brandTextField.getText();
        } catch (Exception e) {
            sendAlert("Error Adding Brand",
                    "No Brand name", "Choose a Brand name.", Alert.AlertType.ERROR);
            return;
        }

        if (brandTextField.getText().equals("")) {
            sendAlert("Error Adding Brand",
                    "No Brand name", "Fill all the fields! Choose a Brand name.", Alert.AlertType.ERROR);
        } else {
            if (!checkBrandExists(brandString)) {
                addBrand(brandString);
            }
        }
    }

}
