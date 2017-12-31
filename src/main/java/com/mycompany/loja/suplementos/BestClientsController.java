/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.loja.suplementos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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
import javafx.stage.Stage;
import supportClasses.BestClient;
import supportClasses.databaseType;
import static supportClasses.databaseType.firebird;

/**
 *
 * @author vinicius
 */
public class BestClientsController extends ControllerModel {

    @FXML
    public TextField bestClientTextField;

    @FXML
    public TableView<BestClient> bestClientsTable;

    @FXML
    public ObservableList<BestClient> bestClientsData;

    @FXML
    public TableColumn<BestClient, String> clientNameBCColumn;

    @FXML
    public TableColumn<BestClient, Integer> numberSalesColumn;

    @FXML
    public TableColumn<BestClient, Float> cashGeneratedColumn;

    public BestClientsController(Connection db, databaseType dbt) {
        super(db, dbt);

    }

    public BestClientsController(
            TextField bestClientTextField, TableView<BestClient> bestClientsTable,
            TableColumn<BestClient, String> clientNameBCColumn,
            TableColumn<BestClient, Integer> numberSalesColumn,
            TableColumn<BestClient, Float> cashGeneratedColumn,
            Connection db, databaseType dbt
    ) {
        super(db, dbt);
        this.bestClientTextField = bestClientTextField;
        this.bestClientsTable = bestClientsTable;
        this.clientNameBCColumn = clientNameBCColumn;
        this.numberSalesColumn = numberSalesColumn;
        this.cashGeneratedColumn = cashGeneratedColumn;

        initBestClients();
    }

    public void initBestClients() {

        bestClientsData = FXCollections.observableArrayList();
        // the property name between "" has to be the same name of the attribute on the class
        clientNameBCColumn.setCellValueFactory(new PropertyValueFactory<BestClient, String>("name"));
        numberSalesColumn.setCellValueFactory(new PropertyValueFactory<BestClient, Integer>("numBought"));
        cashGeneratedColumn.setCellValueFactory(new PropertyValueFactory<BestClient, Float>("numcashgenerated"));

        bestClientsTable.setItems(bestClientsData);

        bestClientTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    searchBestClients();
                }
            }
        });
    }

    public void addBestClientsList(String prodname, Integer numsales,
            Float cashgenerated) {

        bestClientsData.add(
                new BestClient(prodname, numsales, cashgenerated)
        );
    }

    public void searchBestClients() {

        bestClientsData.clear();
        String ClientString = null;
        
        try {
            ClientString = bestClientTextField.getText();
        } catch (Exception e) {

        }


        if (ClientString == null) {
            sendAlert("Error finding BestClients",
                    "",
                    "FEATURE ERROR.",
                    Alert.AlertType.ERROR);
        }
        // if no brand name find all
        try {
            Statement st = this.connection.createStatement();
            ResultSet rs = null;
            switch (dbType) {
                case firebird:

                    if (ClientString.equals("")) {
                        try {
                            rs = st.executeQuery(
                                    "SELECT * FROM bestbuyers;"
                            );
                            while (rs.next()) {
                                addBestClientsList(
                                        rs.getString("clientname"),
                                        rs.getInt("numbuys"),
                                        rs.getFloat("totalcash")
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
                                    "SELECT * FROM client_spendings('" + ClientString + "');"
                            );
                            if (rs.next()) {
                                addBestClientsList(
                                        rs.getString("clientname"),
                                        rs.getInt("numbuys"),
                                        rs.getFloat("totalcash")
                                );
                            } else {
                                sendAlert(
                                        "No results!",
                                        "Search lead to 0 results.",
                                        "No records for " + ClientString,
                                        Alert.AlertType.INFORMATION);
                                return;
                            }
                            while (rs.next()) {
                                addBestClientsList(
                                        rs.getString("clientname"),
                                        rs.getInt("numbuys"),
                                        rs.getFloat("totalcash")
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
        bestClientsTable.setItems(bestClientsData);

    }
}
