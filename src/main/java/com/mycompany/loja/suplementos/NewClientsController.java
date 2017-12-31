/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.loja.suplementos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import supportClasses.BestClient;
import supportClasses.NewClient;
import supportClasses.databaseType;
import static supportClasses.databaseType.firebird;

/**
 *
 * @author vinicius
 */
public class NewClientsController extends ControllerModel {

    @FXML
    public TextField yearNewClientsTextField;

    @FXML
    public TableView<NewClient> newClientsTable;

    public ObservableList<NewClient> newClientNCData;

    @FXML
    public TableColumn<NewClient, String> clientNameNCColumn;

    @FXML
    public TableColumn<NewClient, String> contactColumn;

    @FXML
    public TableColumn<NewClient, String> JoinDateNCColumn;

    public NewClientsController(Connection db, databaseType dbt) {
        super(db, dbt);

    }

    public NewClientsController(
            TextField yearNewClientsTextField,
            TableView<NewClient> newClientsTable, TableColumn<NewClient, String> clientNameNCColumn, TableColumn<NewClient, String> contactColumn, TableColumn<NewClient, String> JoinDateNCColumn, Connection db, databaseType dbt
    ) {
        super(db, dbt);
        this.yearNewClientsTextField = yearNewClientsTextField;
        this.newClientsTable = newClientsTable;
        this.clientNameNCColumn = clientNameNCColumn;
        this.contactColumn = contactColumn;
        this.JoinDateNCColumn = JoinDateNCColumn;

        initNewClients();
    }



    public void initNewClients() {

        yearNewClientsTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d[4]")) {
                    yearNewClientsTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        newClientNCData = FXCollections.observableArrayList();
        // the property name between "" has to be the same name of the attribute on the class
        clientNameNCColumn.setCellValueFactory(new PropertyValueFactory<NewClient, String>("name"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<NewClient, String>("contact"));
        JoinDateNCColumn.setCellValueFactory(new PropertyValueFactory<NewClient, String>("joinDate"));

        newClientsTable.setItems(newClientNCData);

        yearNewClientsTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    searchNewClients(new ActionEvent());
                }
            }
        });
    }

    public void addNewClientsList(String name, String contact,
            String joindate) {
        System.out.println("String date joined = " + joindate);
        newClientNCData.add(
                new NewClient(name, contact, joindate)
        );
    }

    @FXML
    public void searchNewClients(ActionEvent event) {

        newClientNCData.clear();
        String YearSearchString = null;
        try {
            YearSearchString = yearNewClientsTextField.getText();
        } catch (Exception e) {

        }

        if (YearSearchString == null) {
            sendAlert("Error finding NewClients",
                    "No year to find top Products.",
                    "Type a year to find top products feature.",
                    Alert.AlertType.ERROR);
        }
        // if no brand name find all
        try {
            Statement st = this.connection.createStatement();
            ResultSet rs = null;
            switch (dbType) {
                case firebird:

                    if (YearSearchString.equals("")) {
                        try {
                            rs = st.executeQuery(
                                    "SELECT * FROM newclientsyear('2017');"
                            );
                            while (rs.next()) {
                                addNewClientsList(
                                        rs.getString("clientname"),
                                        rs.getString("telephone"),
                                        rs.getTimestamp("cjoindate")
                                                .toLocalDateTime()                                                
                                                .format(
                                                        DateTimeFormatter.
                                                                ofPattern("dd/MM/YYYY HH:mm:ss")
                                                )
                                );
                            }
                            rs.close();
                            st.close();

                        } catch (Exception e) {
                            System.out.println("EERROR " + e.getMessage());
                        }

                    } // find a specific client name
                    else {
                        try {
                            st = this.connection.createStatement();
                            rs = st.executeQuery(
                                    "SELECT * FROM  newclientsyear('" + YearSearchString + "');"
                            );
                            if (rs.next()) {
                                addNewClientsList(
                                        rs.getString("clientname"),
                                        rs.getString("telephone"),
                                        rs.getTimestamp("cjoindate")
                                                .toLocalDateTime()                                                
                                                .format(
                                                        DateTimeFormatter.
                                                                ofPattern("dd/MM/YYYY HH:mm:ss")
                                                )
                                );
                            } else {
                                sendAlert(
                                        "No results!",
                                        "Search lead to 0 results. Try a different Year",
                                        "No Top Products with the year " + YearSearchString,
                                        Alert.AlertType.INFORMATION);
                                return;
                            }
                            while (rs.next()) {
                                addNewClientsList(
                                        rs.getString("clientname"),
                                        rs.getString("telephone"),
                                        rs.getTimestamp("cjoindate")
                                                .toLocalDateTime()                                                
                                                .format(
                                                        DateTimeFormatter.
                                                                ofPattern("ddD/MM/YYYY HH:mm:ss")
                                                )
                                );
                            }
                            rs.close();
                            st.close();
                        } catch (Exception e) {
                            System.out.println("EERROR " + e.getMessage());
                            return;

                        }
                    }
                    break;
                default:
                    sendAlert("Warning",
                            "Feature not available",
                            "Feature not available in this DataBase",
                            Alert.AlertType.WARNING);
                    break;
            }

        } catch (Exception e) {
        }
        // set items        
        newClientsTable.setItems(newClientNCData);

    }
}
