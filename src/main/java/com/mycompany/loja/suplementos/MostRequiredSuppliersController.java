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
import supportClasses.MostRequiredSupplier;
import supportClasses.databaseType;
import static supportClasses.databaseType.firebird;

/**
 *
 * @author vinicius
 */
public class MostRequiredSuppliersController extends ControllerModel {

    @FXML
    public TextField requiredSupplierTextField;

    @FXML
    public TableView<MostRequiredSupplier> mostRequiredSuppliersTable;

    public ObservableList<MostRequiredSupplier> mrsData;

    @FXML
    public TableColumn<MostRequiredSupplier, String> nameMRSColumn;

    @FXML
    public TableColumn<MostRequiredSupplier, Integer> totalItemsMRSColumn;

    @FXML
    public TableColumn<MostRequiredSupplier, Float> totalPercentMRSColumn;

    public MostRequiredSuppliersController(Connection db, databaseType dbt) {
        super(db, dbt);

    }

    public MostRequiredSuppliersController(
            TextField requiredSupplierTextField,
            TableView<MostRequiredSupplier> mostRequiredSuppliersTable, TableColumn<MostRequiredSupplier, String> nameMRSColumn, TableColumn<MostRequiredSupplier, Integer> totalItemsMRSColumn, TableColumn<MostRequiredSupplier, Float> totalPercentMRSColumn, Connection db, databaseType dbt
    ) {
        super(db, dbt);
        this.requiredSupplierTextField = requiredSupplierTextField;
        this.mostRequiredSuppliersTable = mostRequiredSuppliersTable;
        this.nameMRSColumn = nameMRSColumn;
        this.totalItemsMRSColumn = totalItemsMRSColumn;
        this.totalPercentMRSColumn = totalPercentMRSColumn;

        initMostRequiredSuppliers();
    }



    public void initMostRequiredSuppliers() {        

        mrsData = FXCollections.observableArrayList();
        // the property name between "" has to be the same name of the attribute on the class
        nameMRSColumn.setCellValueFactory(new PropertyValueFactory<MostRequiredSupplier, String>("name"));
        totalItemsMRSColumn.setCellValueFactory(new PropertyValueFactory<MostRequiredSupplier, Integer>("totalitems"));
        totalPercentMRSColumn.setCellValueFactory(new PropertyValueFactory<MostRequiredSupplier, Float>("totalpercent"));

        mostRequiredSuppliersTable.setItems(mrsData);

        requiredSupplierTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    searchMostRequiredSuppliers(new ActionEvent());
                }
            }
        });
    }

    public void addMostRequiredSuppliersList(String name, Integer totalitems,
            Float totalpercent) {        
        mrsData.add(
                new MostRequiredSupplier(name, totalitems, totalpercent)
        );
    }

    @FXML
    public void searchMostRequiredSuppliers(ActionEvent event) {

        mrsData.clear();
        String YearSearchString = null;
        try {
            YearSearchString = requiredSupplierTextField.getText();
        } catch (Exception e) {

        }

        if (YearSearchString == null) {
            sendAlert("Error finding MostRequiredSuppliers",
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
                                    "SELECT * FROM mostRequiredSupp;"
                            );
                            while (rs.next()) {
                                addMostRequiredSuppliersList(
                                        rs.getString("suppliername"),
                                        rs.getInt("suppliertotalpurchase"),
                                        rs.getFloat("supplierpercentProductsTotal")
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
                                    "SELECT * FROM  totalRequiredSupplier('" + YearSearchString + "');"
                            );
                            if (rs.next()) {
                               addMostRequiredSuppliersList(
                                        rs.getString("suppliername"),
                                        rs.getInt("suppliertotalpurchase"),
                                        rs.getFloat("supplierpercentProductsTotal")
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
                                addMostRequiredSuppliersList(
                                        rs.getString("suppliername"),
                                        rs.getInt("suppliertotalpurchase"),
                                        rs.getFloat("supplierpercentProductsTotal")
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
        mostRequiredSuppliersTable.setItems(mrsData);

    }
}
