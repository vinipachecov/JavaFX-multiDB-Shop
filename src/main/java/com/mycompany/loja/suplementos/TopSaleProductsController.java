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
import supportClasses.TopProduct;
import supportClasses.databaseType;
import static supportClasses.databaseType.firebird;

/**
 *
 * @author vinicius
 */
public class TopSaleProductsController extends ControllerModel{
    
    @FXML
    public TextField yearTopProductsTextField;

    @FXML
    public TableView<TopProduct> topProductsTable;

    @FXML
    public ObservableList<TopProduct> topProductsData;

    @FXML
    public TableColumn<TopProduct, String> productNameColumn;

    @FXML
    public TableColumn<TopProduct, Integer> numberSalesColumn;

    @FXML
    public TableColumn<TopProduct, Float> cashGeneratedColumn;

    public TopSaleProductsController(Connection db, databaseType dbt) {
        super(db, dbt);
        
    
    }

    public TopSaleProductsController(
            TextField yearTopProductsTextField, 
            TableView<TopProduct> topProductsTable,   TableColumn<TopProduct, 
            String> productNameColumn, TableColumn<TopProduct, 
            Integer> numberSalesColumn, TableColumn<TopProduct,
            Float> cashGeneratedColumn, Connection db, databaseType dbt
    ) {
        super(db, dbt);
        this.yearTopProductsTextField = yearTopProductsTextField;
        this.topProductsTable = topProductsTable;
        this.productNameColumn = productNameColumn;
        this.numberSalesColumn = numberSalesColumn;
        this.cashGeneratedColumn = cashGeneratedColumn;
        
        
        initTopProducts();
    }

     public void initTopProducts() {

        yearTopProductsTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    yearTopProductsTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        topProductsData = FXCollections.observableArrayList();
        // the property name between "" has to be the same name of the attribute on the class
        productNameColumn.setCellValueFactory(new PropertyValueFactory<TopProduct, String>("name"));
        numberSalesColumn.setCellValueFactory(new PropertyValueFactory<TopProduct, Integer>("numsales"));
        cashGeneratedColumn.setCellValueFactory(new PropertyValueFactory<TopProduct, Float>("cashgenerated"));

        topProductsTable.setItems(topProductsData);

        yearTopProductsTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    searchTopProducts(new ActionEvent());
                }
            }
        });
    }

    public void addTopProductsList(String prodname, Integer numsales,
            Float cashgenerated) {

        topProductsData.add(
                new TopProduct(prodname, numsales, cashgenerated)
        );
    }

    @FXML
    public void searchTopProducts(ActionEvent event) {

        topProductsData.clear();
        String YearSearchString = null;
        try {
            YearSearchString = yearTopProductsTextField.getText();
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
                                    "SELECT * FROM  listTopProductSales('2017');"
                            );
                            while (rs.next()) {
                                addTopProductsList(
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
                                    "SELECT * FROM  listTopProductSales('" + YearSearchString + "');"
                            );
                            if (rs.next()) {
                                addTopProductsList(
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
                                addTopProductsList(
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
        topProductsTable.setItems(topProductsData);

    }    
}
