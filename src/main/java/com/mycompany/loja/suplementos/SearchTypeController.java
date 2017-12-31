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
import supportClasses.Brand;
import supportClasses.Type;
import supportClasses.databaseType;

/**
 *
 * @author vinicius
 */
public class SearchTypeController extends ControllerModel {

    @FXML
    public TextField typeSearchTextField;

    @FXML
    public TableColumn<Type, Integer> idColumn;

    @FXML
    public TableColumn<Type, String> nameColumn;

    @FXML
    public javafx.scene.control.TableView<Type> typeTable;

    public ObservableList<Type> data;

    public Stage dialog;

    public SearchTypeController(Connection db) {
        super(db);
    }

    SearchTypeController(Connection connection, databaseType dbType) {
        super(connection, dbType);
    }

    SearchTypeController(MongoDatabase mongoDatabase, databaseType dbType) {
        super(mongoDatabase, dbType);
    }

    public void init(Stage modal) {

        dialog = modal;

        data = FXCollections.observableArrayList();

        idColumn.setCellValueFactory(new PropertyValueFactory<Type, Integer>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Type, String>("name"));

        typeTable.setItems(data);

        typeSearchTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    Search(new ActionEvent());
                }
            }
        });
    }

    @FXML
    public void Search(ActionEvent event) {

        data.clear();

        String typeSearchString = null;
        try {
            typeSearchString = typeSearchTextField.getText();
        } catch (Exception e) {

        }

        if (typeSearchString == null) {
            sendAlert("Error finding Type",
                    "No Type name to search.", "Pick a type name to search.", Alert.AlertType.ERROR);
        }

        switch (this.dbType) {
            case firebird:
                // if no type name, find all
                if (typeSearchString.equals("")) {
                    try {
                        Statement st = this.connection.createStatement();
                        ResultSet rs = st.executeQuery(
                                "select first 50 * "
                                + " FROM types "
                        );
                        while (rs.next()) {
                            data.add(new Type(rs.getInt("id"), rs.getString("name")));
                        }
                        rs.close();
                        st.close();

                    } catch (Exception e) {
                        System.out.println( dbType + " error " + ": " + e.getMessage());
                    }

                } // find a specific type name
                else {
                    try {
                        Statement st = this.connection.createStatement();
                        ResultSet rs = st.executeQuery(
                                "select * "
                                + " FROM types "
                                + " WHERE name = '" + typeSearchString + "'"
                        );
                        while (rs.next()) {
                            data.add(new Type(rs.getInt("id"), rs.getString("name")));
                        }
                        rs.close();
                        st.close();
                    } catch (Exception e) {
                        System.out.println( dbType + " error " + ": " + e.getMessage());
                    }
                }
                break;
            case postgres:
                // if no type name, find all
                if (typeSearchString.equals("")) {
                    try {
                        Statement st = this.connection.createStatement();
                        ResultSet rs = st.executeQuery(
                                "select * "
                                + " FROM types "
                                + " limit 50"
                        );
                        while (rs.next()) {
                            data.add(new Type(rs.getInt("id"), rs.getString("name")));
                        }
                        rs.close();
                        st.close();

                    } catch (Exception e) {
                        System.out.println( dbType + " error " + ": " + e.getMessage());
                    }

                } // find a specific type name
                else {
                    try {
                        Statement st = this.connection.createStatement();
                        ResultSet rs = st.executeQuery(
                                "select * "
                                + " FROM types "
                                + " WHERE name = '" + typeSearchString + "'"
                        );
                        while (rs.next()) {
                            data.add(new Type(rs.getInt("id"), rs.getString("name")));
                        }
                        rs.close();
                        st.close();
                    } catch (Exception e) {
                        System.out.println( dbType + " error " + ": " + e.getMessage());
                    }
                }
                break;

            case mongodb:
                MongoCollection<Document> types = mongoDatabase.getCollection("types");
                if (typeSearchString.equals("")) {
                    try {

                        List<Document> documents = types.find().into(new ArrayList<Document>());

                        for (Document document : documents) {
                            data.add(new Type(document.getString("name")));
                        }
                    } catch (Exception e) {
                        System.out.println( dbType + " error " + ": " + e.getMessage());
                    }
                } else {
                    try {

                        BasicDBObject andquery = new BasicDBObject();
                        List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
                        obj.add(new BasicDBObject("name", typeSearchString));
                        andquery.put("$and", obj);
                        List<Document> documents = types.find().filter(andquery).into(new ArrayList<Document>());

                        if (documents.size() != 0) {
                            for (Document document : documents) {
                                data.add(new Type(document.getString("name")));
                            }
                        } else {
                            sendAlert("Information",
                                    "Results",
                                    "No results found with " + typeSearchString,
                                    Alert.AlertType.INFORMATION);
                        }

                    } catch (Exception e) {
                        System.out.println( dbType + " error " + ": " + e.getMessage());
                    }

                }

                break;
        }

        // set items        
        typeTable.setItems(data);
    }

}
