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
import java.time.format.DateTimeFormatter;
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
import supportClasses.Client;
import supportClasses.databaseType;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.eq;

/**
 *
 * @author vinicius
 */
public class SearchClientController extends ControllerModel {

    @FXML
    public TextField clientSearchTextField;

    @FXML
    public TableColumn<Client, Integer> idColumn;

    @FXML
    public TableColumn<Client, String> nameColumn;

    @FXML
    public TableColumn<Client, Integer> numsalesColumn;

    @FXML
    public TableColumn<Client, String> emailColumn;

    @FXML
    public TableColumn<Client, String> telephoneColumn;

    @FXML
    public TableColumn<Client, String> govidColumn;

    @FXML
    public TableColumn<Client, LocalDate> birthdateColumn;

    @FXML
    public javafx.scene.control.TableView<Client> clientTable;

    public ObservableList<Client> data;

    public Stage dialog;

    public SearchClientController(Connection db) {
        super(db);
    }

    SearchClientController(Connection connection, databaseType dbType) {
        super(connection, dbType);
    }

    SearchClientController(MongoDatabase mongoDatabase, databaseType dbType) {
        super(mongoDatabase, dbType);
    }

    public void init(Stage modal) {

        dialog = modal;

        data = FXCollections.observableArrayList();

        // the property name between "" has to be the same name of the attribute on the class
        nameColumn.setCellValueFactory(new PropertyValueFactory<Client, String>("name"));
        numsalesColumn.setCellValueFactory(new PropertyValueFactory<Client, Integer>("numsales"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<Client, String>("email"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<Client, String>("telephone"));
        govidColumn.setCellValueFactory(new PropertyValueFactory<Client, String>("govid"));
        birthdateColumn.setCellValueFactory(new PropertyValueFactory<Client, LocalDate>("birthdate"));

        clientTable.setItems(data);

        clientSearchTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    Search(new ActionEvent());
                }
            }
        });

    }

    public void addToCLientList(String name, Integer numsales,
            String email, String telephone, String govid, LocalDate birthdate) {        
        data.add(
                new Client(name, numsales, email, telephone, govid, birthdate)
        );
    }

    @FXML
    public void Search(ActionEvent event) {

        data.clear();
        String clientSearchString = null;
        try {
            clientSearchString = clientSearchTextField.getText();
        } catch (Exception e) {

        }

        if (clientSearchString == null) {
            sendAlert("Error finding Brand",
                    "No Brand name to search.",
                    "Pick a brand name to search.",
                    Alert.AlertType.ERROR);
        }

        // if no brand name find all
        try {
            Statement st = null;
            ResultSet rs = null;
            switch (dbType) {
                case mongodb:
                    MongoCollection<Document> clients = mongoDatabase.getCollection("clients");
                    if (clientSearchString.equals("")) {
                        try {
                            
                            List<Document> documents = clients.find().into(new ArrayList<Document>());
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                            System.out.println("vai começar a pegar os documentos");
                            for (Document document : documents) {
                                  addToCLientList(
                                        document.getString("name"),
                                        document.getInteger("numsales"),
                                        document.getString("email"),
                                        document.getString("telephone"),
                                        document.getString("govid"),
                                        document.getDate("birthdate").toInstant().atZone(ZoneId.systemDefault()).toLocalDate()                                        
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
                            obj.add(new BasicDBObject("name", clientSearchString));
                            andquery.put("$and", obj);
                            List<Document> documents = clients.find().filter(andquery).into(new ArrayList<Document>());

                            if (documents.size() != 0) {
                                for (Document document : documents) {
                                     addToCLientList(
                                        document.getString("name"),
                                        document.getInteger("numsales"),
                                        document.getString("email"),
                                        document.getString("telephone"),
                                        document.getString("govid"),
                                        document.getDate("birthdate").toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                     );
                                }
                            } else {
                                sendAlert("Information",
                                        "Results",
                                        "No results found with " + clientSearchString,
                                        Alert.AlertType.INFORMATION);
                            }

                        } catch (Exception e) {
                            System.out.println(dbType + " error " + ": " + e.getMessage());
                        }

                    }
                    break;
                case firebird:
                    st = this.connection.createStatement();
                    if (clientSearchString.equals("")) {
                        try {
                            st = this.connection.createStatement();
                            rs = st.executeQuery(
                                    "select first 50 * "
                                    + " FROM clients "
                            );
                            while (rs.next()) {
                                addToCLientList(
                                        rs.getString("name"),
                                        rs.getInt("numsales"),
                                        rs.getString("email"),
                                        rs.getString("telephone"),
                                        rs.getString("govid"),
                                        (rs.getDate("birthdate").toLocalDate()));
                            }
                            rs.close();
                            st.close();

                        } catch (Exception e) {

                        }

                    } // find a specific client name
                    else {
                        try {
                            st = this.connection.createStatement();
                            rs = st.executeQuery(
                                    "select * "
                                    + " FROM clients"
                                    + " WHERE name = '" + clientSearchString + "'"
                            );
                            if (rs.next()) {
                                addToCLientList(
                                        rs.getString("name"),
                                        rs.getInt("numsales"),
                                        rs.getString("email"),
                                        rs.getString("telephone"),
                                        rs.getString("govid"),
                                        (rs.getDate("birthdate").toLocalDate()));
                            } else {
                                sendAlert(
                                        "No results!",
                                        "Search lead to 0 results. Try a different Client Name",
                                        "No Client with this name",
                                        Alert.AlertType.INFORMATION);
                                return;
                            }
                            while (rs.next()) {
                                addToCLientList(
                                        rs.getString("name"),
                                        rs.getInt("numsales"),
                                        rs.getString("email"),
                                        rs.getString("telephone"),
                                        rs.getString("govid"),
                                        (rs.getDate("birthdate").toLocalDate()));
                            }
                            rs.close();
                            st.close();
                        } catch (Exception e) {

                        }
                    }
                    break;
                case postgres:
                    st = this.connection.createStatement();
                    if (clientSearchString.equals("")) {
                        try {
                            st = this.connection.createStatement();
                            rs = st.executeQuery(
                                    "select * "
                                    + " FROM clients "
                                    + " limit 50"
                            );
                            while (rs.next()) {
                                addToCLientList(
                                        rs.getString("name"),
                                        rs.getInt("numsales"),
                                        rs.getString("email"),
                                        rs.getString("telephone"),
                                        rs.getString("govid"),
                                        (rs.getDate("birthdate").toLocalDate()));
                            }
                            rs.close();
                            st.close();

                        } catch (Exception e) {

                        }

                    } // find a specific client name
                    else {
                        try {
                            st = this.connection.createStatement();
                            rs = st.executeQuery(
                                    "select * "
                                    + " FROM clients"
                                    + " WHERE name = '" + clientSearchString + "'"
                            );
                            if (rs.next()) {
                                addToCLientList(
                                        rs.getString("name"),
                                        rs.getInt("numsales"),
                                        rs.getString("email"),
                                        rs.getString("telephone"),
                                        rs.getString("govid"),
                                        (rs.getDate("birthdate").toLocalDate()));
                            } else {
                                sendAlert(
                                        "No results!",
                                        "Search lead to 0 results. Try a different Client Name",
                                        "No Client with this name",
                                        Alert.AlertType.INFORMATION);
                                return;
                            }
                            while (rs.next()) {
                                addToCLientList(
                                        rs.getString("name"),
                                        rs.getInt("numsales"),
                                        rs.getString("email"),
                                        rs.getString("telephone"),
                                        rs.getString("govid"),
                                        (rs.getDate("birthdate").toLocalDate()));
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
        clientTable.setItems(data);

    }

}
