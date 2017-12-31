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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
public class AddSupplierController extends ControllerModel {

    public Stage dialog;

    @FXML
    public TextField socialReasonTextField;

    @FXML
    public TextField emailTextField;

    @FXML
    public TextField cnpjTextField;

    @FXML
    public TextField telephoneTextField;

    @FXML
    public TextField fantasynameTextField;

    public AddSupplierController(Connection db) {
        super(db);
    }

    public AddSupplierController(Connection db, databaseType dbt) {
        super(db, dbt);
    }

    AddSupplierController(MongoDatabase mongoDatabase, databaseType dbType) {
        super(mongoDatabase, dbType);
    }

    public void init(Stage modal) {
        dialog = modal;
    }

    @FXML
    public void cancel() {
        dialog.close();
    }

    public void addSupplier(String socialrtf, String emailString,
            String cnpj, String telephone, String fantasyname) {

        switch (dbType) {
            case mongodb:
                try {
                    MongoCollection<Document> suppliers = mongoDatabase.getCollection("suppliers");

                    Document newsupplier = new Document();
                    newsupplier.put("socialreason", socialrtf);
                    newsupplier.put("email", emailString);
                    newsupplier.put("cnpj", cnpj);
                    newsupplier.put("fantasyname", fantasyname);
                    newsupplier.put("numpurchases", 0);
                    suppliers.insertOne(newsupplier);
                } catch (Exception e) {
                    System.out.println("Erro no banco " + dbType + ": " + e.getMessage());
                    return;
                }
                break;
            default:
                try {
                    Statement st = this.connection.createStatement();
                    st.executeUpdate(""
                            + "insert into suppliers(socialreason, email, cnpj, telephone, fantasyname, numberpurchases)\n"
                            + "values('" + socialrtf + "', '" + emailString + "' ,"
                            + "'" + cnpj + "','" + telephone + " ', '" + fantasyname + "', 0)");
                    st.close();
                } catch (Exception e) {
                    System.out.println("Erro no banco " + dbType + ": " + e.getMessage());
                    return;
                }
                break;
        }

        sendAlert("Success", "Supplier Added!", "Supplier Added with Success.", Alert.AlertType.CONFIRMATION);
    }

    public boolean checkSupplierAlreadyExists(String socialrtf, String emailString,
            String cnpj, String telephone, String fantasyname) {

        switch (this.dbType) {
            case mongodb:
                MongoCollection<Document> suppliers = mongoDatabase.getCollection("suppliers");

                BasicDBObject andquery = new BasicDBObject();
                List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
                obj.add(new BasicDBObject("fantasyname", fantasyname));
                obj.add(new BasicDBObject("email", emailString));
                obj.add(new BasicDBObject("cnpj", cnpj));
                andquery.put("$and", obj);

                List<Document> documents = suppliers.find().filter(andquery).into(new ArrayList<Document>());

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
                            + " FROM suppliers "
                            + " where socialreason = '" + socialrtf + "'"
                            + " AND email = '" + emailString + "'"
                            + " AND cnpj= '" + cnpj + "'"
                            + " AND telephone= '" + telephone + "'"
                            + " AND fantasyname= '" + fantasyname + "'"
                    );
                    if (rs.next()) {
                        sendAlert("Error",
                                "Supplier already exists!",
                                "Supplier already exists with those values",
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
                            + " FROM suppliers "
                            + " where socialreason = '" + socialrtf + "'"
                            + " AND email = '" + emailString + "'"
                            + " AND cnpj= '" + cnpj + "'"
                            + " AND telephone= '" + telephone + "'"
                            + " AND fantasyname= '" + fantasyname + "'"
                            + " limit 1"
                    );
                    if (rs.next()) {
                        sendAlert("Error",
                                "Supplier already exists!",
                                "Supplier already exists with those values",
                                Alert.AlertType.ERROR);
                        return true;
                    }
                    rs.close();
                    st.close();
                } catch (Exception e) {
                    System.out.println("Error = " + e.getMessage());

                }
                break;
        }

        return false;
    }

    @FXML
    public void checkForm() {
        String socialrtf = null;
        String emailString = null;
        String cnpj = null;
        String telephone = null;
        String fantasyname = null;

        try {
            socialrtf = socialReasonTextField.getText();
            emailString = emailTextField.getText();
            cnpj = cnpjTextField.getText();
            telephone = telephoneTextField.getText();
            fantasyname = fantasynameTextField.getText();

        } catch (Exception e) {
        }

        if (socialrtf.equals("") || emailString.equals("") || cnpj.equals("")
                || telephone.equals("") || fantasyname.equals("")) {
            sendAlert("Error Adding Supplier",
                    "FOrm Error",
                    "Fill all the fields!",
                    Alert.AlertType.ERROR);
            return;
        }
        if (!checkSupplierAlreadyExists(socialrtf, emailString, cnpj, telephone, fantasyname)) {
            addSupplier(socialrtf, emailString, cnpj, telephone, fantasyname);
        }

    }

}
