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
import supportClasses.User;
import supportClasses.databaseType;
import supportClasses.userType;

/**
 *
 * @author vinicius
 */
public class AddTypeController extends ControllerModel {

    @FXML
    public TextField typeTextField;

    public Stage modal;

    public AddTypeController(Connection db) {
        super(db);
    }

    AddTypeController(Connection connection, databaseType dbType) {
        super(connection, dbType);
    }

    AddTypeController(MongoDatabase mongoDatabase, databaseType dbType) {
        super(mongoDatabase, dbType);
    }

    public void init(Stage dialog) {
        modal = dialog;
    }

    @FXML
    public void cancel() {
        modal.close();
    }

    public void addType(String typeString) {

        switch (dbType) {
            case firebird:
                try {
                    Statement st = this.connection.createStatement();
                    st.executeUpdate(
                            "insert into types (name)"
                            + " VALUES('" + typeString + "' )"
                    );
                    st.close();
                    sendAlert("Product type added with success!", 
                            "Type Added", 
                            "A new type have been added!", 
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
                            "insert into types (name)"
                            + " VALUES('" + typeString + "' )"
                    );
                    st.close();
                    sendAlert("Product type added with success!", 
                            "Type Added", 
                            "A new type have been added!", 
                            Alert.AlertType.CONFIRMATION);
                } catch (Exception e) {
                    System.out.println("Error " + e.getMessage());
                    return;
                }
                break;
            case mongodb:
                try {
                    MongoCollection<Document> types = mongoDatabase.getCollection("types");
                    
                    Document newtype = new Document();
                    newtype.put("name", typeString);
                    types.insertOne(newtype);                    
                    sendAlert("Product type added with success!", 
                            "Type Added", 
                            "A new type have been added!", 
                            Alert.AlertType.CONFIRMATION);
                } catch (Exception e) {
                    System.out.println("Erro no banco " + dbType + ": " + e.getMessage());
                }
                break;
        }
    }

    public boolean checkTypeExists(String typeString) {

        switch (dbType) {
            case postgres:
                try {
                    Statement st = this.connection.createStatement();
                    ResultSet rs = st.executeQuery(
                            "select id "
                            + " FROM types "
                            + " WHERE name = '" + typeString + "'"
                            + " limit 1 ");
                    if (rs.next()) {
                        System.out.println("There is already a type with this name");
                        sendAlert("Error to add a type",
                                "Type exists.",
                                "Type already Exists! Choose a different Type name!",
                                Alert.AlertType.ERROR);
                        return true;
                    }

                    rs.close();
                    st.close();

                } catch (Exception e) {

                }
                break;
            case firebird:
                try {
                    Statement st = this.connection.createStatement();
                    ResultSet rs = st.executeQuery(
                            "select first 1 * "
                            + " FROM types "
                            + " WHERE name = '" + typeString + "'"
                    );
                    if (rs.next()) {
                        System.out.println("There is already a type with this name");
                        sendAlert("Error to add a type",
                                "Type exists.",
                                "Type already Exists! Choose a different Type name!",
                                Alert.AlertType.ERROR);
                        return true;
                    }
                    rs.close();
                    st.close();

                } catch (Exception e) {

                }
                break;
            case mongodb:
                MongoCollection<Document> types = mongoDatabase.getCollection("types");

                BasicDBObject andquery = new BasicDBObject();
                List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
                obj.add(new BasicDBObject("name", typeString));
                andquery.put("$and", obj);
                List<Document> documents = types.find().filter(andquery).into(new ArrayList<Document>());

                if (documents.size() != 0) {
                    sendAlert("Error to add a type",
                            "Type exists.",
                            "Type already Exists! Choose a different Type name!",
                            Alert.AlertType.ERROR);
                    return true;
                }
                break;
        }
        return false;
    }

    @FXML
    public void checkForm() {
        System.out.println("começou a verificar o form");
        String typeString = null;
        try {
            typeString = typeTextField.getText();
        } catch (Exception e) {
            sendAlert("Error Adding new Type",
                    "No Type name", "Choose a product type name.", Alert.AlertType.ERROR);
            return;
        }

        if (typeString.equals("")) {
            sendAlert("Error Adding new Type",
                    "No Type name", "Fill all the fields! Choose a product type name.", Alert.AlertType.ERROR);
        } else {
            if (!checkTypeExists(typeString)) {
                addType(typeString);
            }
        }
    }
}
