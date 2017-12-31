
/* To change this license header, choose License Headers in Project Properties.
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.bson.Document;
import supportClasses.databaseType;

/**
 *
 * @author vinicius
 */
public class AddClientController extends ControllerModel {

    public Stage modal;

    @FXML
    public TextField NameTextField;

    @FXML
    public TextField emailTextField;

    @FXML
    public TextField telephoneTextField;

    @FXML
    public TextField govidTextField;

    @FXML
    public DatePicker birthdatePicker;

    public AddClientController(Connection db) {
        super(db);
    }

    AddClientController(Connection connection, databaseType dbType) {
        super(connection, dbType);
    }

    AddClientController(MongoDatabase mongoDatabase, databaseType dbType) {
        super(mongoDatabase, dbType);
    }

    public void init(Stage dialog) {
        modal = dialog;
        birthdatePicker.setConverter(new StringConverter<LocalDate>() {
            private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null) {
                    return "";
                }
                return dateTimeFormatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty()) {
                    return null;
                }
                return LocalDate.parse(dateString, dateTimeFormatter);
            }
        });
    }

    @FXML
    public void cancel() {
        modal.close();
    }

    public void addClient(String name, String email,
            String telephone, String govid, LocalDate date) {

        switch (this.dbType) {
            case mongodb:
                try {
                    MongoCollection<Document> clients = mongoDatabase.getCollection("clients");
                    
                    Document newclient = new Document();
                    newclient.put("name", name);
                    newclient.put("email", email);
                    newclient.put("telephone", telephone);
                    newclient.put("govid", govid);
                    newclient.put("numsales", 0);
                    newclient.put("birthdate", Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));                                                           
                    clients.insertOne(newclient);                    
                    sendAlert("Product type added with success!", 
                            "Type Added", 
                            "A new type have been added!", 
                            Alert.AlertType.CONFIRMATION);
                } catch (Exception e) {
                    System.out.println("Erro no banco " + dbType + ": " + e.getMessage());
                }
                break;

            default:
                try {
                    Statement st = this.connection.createStatement();
                    st.executeUpdate(""
                            + "insert into clients(name,numsales,email,telephone,govid,birthdate, joindate) \n"
                            + "values('" + name + "',0,'" + email + "','" + telephone + "','" + govid + "','" + date + "', current_timestamp)");
                    st.close();
                } catch (Exception e) {
                    System.out.println("Error " + e.getMessage());
                }
                sendAlert("Success", "Client Added!", "Client Added with Success.", Alert.AlertType.CONFIRMATION);
        }

    }

    public boolean checkClientAlreadyExists(String name, String email,
            String telephone, String govid, LocalDate date) {

        switch (this.dbType) {
            case mongodb:
                MongoCollection<Document> clients = mongoDatabase.getCollection("clients");

                BasicDBObject andquery = new BasicDBObject();
                List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
                obj.add(new BasicDBObject("username", name));
                obj.add(new BasicDBObject("email", email));
                obj.add(new BasicDBObject("govid", govid));
                andquery.put("$and", obj);

                List<Document> documents = clients.find().filter(andquery).into(new ArrayList<Document>());                

                if (documents.size() != 0) {
                    sendAlert("Error",
                            "Client already exists!",
                            "User already exists with those values",
                            Alert.AlertType.ERROR);
                    return true;
                }
                break;
            case firebird:
                try {
                    Statement st = this.connection.createStatement();
                    ResultSet rs = st.executeQuery(
                            "select first 1 *  "
                            + " FROM clients "
                            + " where name = '" + name + "'"
                            + " AND email = '" + email + "'"
                            + " AND govid = '" + govid + "'"
                    );
                    if (rs.next()) {
                        sendAlert("Error",
                                "Client already exists!",
                                "User already exists with those values",
                                Alert.AlertType.ERROR);
                        return true;
                    }
                    rs.close();
                    st.close();
                } catch (Exception e) {
                    System.out.println("Error = " + e.getMessage());

                }
                break;
            case postgres:
                try {
                    Statement st = this.connection.createStatement();
                    ResultSet rs = st.executeQuery(
                            "select *  "
                            + " FROM clients "
                            + " where name = '" + name + "'"
                            + " AND email = '" + email + "'"
                            + " AND govid = '" + govid + "'"
                            + " limit 1"
                    );
                    if (rs.next()) {
                        sendAlert("Error",
                                "Client already exists!",
                                "User already exists with those values",
                                Alert.AlertType.ERROR);
                        return true;
                    }
                    rs.close();
                    st.close();
                } catch (Exception e) {
                    System.out.println("Error = " + e.getMessage());
                }

        }
        return false;
    }

    @FXML
    public void checkForm() {
        // check if the user has filled the fields, trying to get all 
        String name = null;
        String email = null;
        String telephone = null;
        String govid = null;
        LocalDate date = null;

        try {
            name = NameTextField.getText();
            email = emailTextField.getText();
            telephone = telephoneTextField.getText();
            govid = govidTextField.getText();
            date = birthdatePicker.getValue();

        } catch (Exception e) {
            sendAlert("Form Error", "Error adding a client", "Error on getting values", Alert.AlertType.ERROR);
        }

        if (name.equals("") || email.equals("") || telephone.equals("")
                || govid.equals("") || date.equals(null) || date.equals("")) {
            sendAlert("Error", "Form Error", "Fill all the fields!", Alert.AlertType.ERROR);
        } else {
            System.out.println("Vai verificar se tem dados iguais");
            if (!checkClientAlreadyExists(name, email, telephone, govid, date)) {
                addClient(name, email, telephone, govid, date);
            }
        }

    }

}
