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
import supportClasses.TopProduct;
import supportClasses.databaseType;
import static supportClasses.databaseType.firebird;

/**
 *
 * @author vinicius
 */
public class WorstSaleProductsController extends ControllerModel{
    
    @FXML
    public TextField yearWorstProductsTextField;

    @FXML
    public TableView<TopProduct> worstProductsTable;

    
    public ObservableList<TopProduct> worstProductWSData;

    @FXML
    public TableColumn<TopProduct, String> productNameWSColumn;

    @FXML
    public TableColumn<TopProduct, Integer> numberSalesWSColumn;

    @FXML
    public TableColumn<TopProduct, Float> cashGeneratedWSColumn;

    public WorstSaleProductsController(Connection db, databaseType dbt) {
        super(db, dbt);
        
    
    }

    public WorstSaleProductsController(
            TextField yearWorstProductsTextField, 
            TableView<TopProduct> worstProductsTable,   TableColumn<TopProduct, 
            String> productNameWSColumn, TableColumn<TopProduct, 
            Integer> numberSalesWSColumn, TableColumn<TopProduct,
            Float> cashGeneratedWSColumn, Connection db, databaseType dbt
    ) {
        super(db, dbt);
        this.yearWorstProductsTextField = yearWorstProductsTextField;
        this.worstProductsTable = worstProductsTable;
        this.productNameWSColumn = productNameWSColumn;
        this.numberSalesWSColumn = numberSalesWSColumn;
        this.cashGeneratedWSColumn = cashGeneratedWSColumn;
        
        
        initWorstProducts();
    }

     public void initWorstProducts() {

        yearWorstProductsTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    yearWorstProductsTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        worstProductWSData = FXCollections.observableArrayList();
        // the property name between "" has to be the same name of the attribute on the class
        productNameWSColumn.setCellValueFactory(new PropertyValueFactory<TopProduct, String>("name"));
        numberSalesWSColumn.setCellValueFactory(new PropertyValueFactory<TopProduct, Integer>("numsales"));
        cashGeneratedWSColumn.setCellValueFactory(new PropertyValueFactory<TopProduct, Float>("cashgenerated"));

        worstProductsTable.setItems(worstProductWSData);

        yearWorstProductsTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    searchWorstProducts(new ActionEvent());
                }
            }
        });
    }

    public void addWorstProductsList(String prodname, Integer numsales,
            Float cashgenerated) {

        worstProductWSData.add(
                new TopProduct(prodname, numsales, cashgenerated)
        );
    }

    @FXML
    public void searchWorstProducts(ActionEvent event) {

        worstProductWSData.clear();
        String YearSearchString = null;
        try {
            YearSearchString = yearWorstProductsTextField.getText();
        } catch (Exception e) {

        }

        if (YearSearchString == null) {
            sendAlert("Error finding TopProducts",
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
                                    "SELECT * FROM  listWorstProductSales('2017');"
                            );
                            while (rs.next()) {
                                addWorstProductsList(
                                        rs.getString("prodname"),
                                        rs.getInt("prodsales"),
                                        rs.getFloat("prodsalecash")
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
                                    "SELECT * FROM  listWorstProductSales('" + YearSearchString + "');"
                            );
                            if (rs.next()) {
                                addWorstProductsList(
                                        rs.getString("prodname"),
                                        rs.getInt("prodsales"),
                                        rs.getFloat("prodsalecash")
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
                                addWorstProductsList(
                                        rs.getString("prodname"),
                                        rs.getInt("prodsales"),
                                        rs.getFloat("prodsalecash")
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
        worstProductsTable.setItems(worstProductWSData);

    }    
}
