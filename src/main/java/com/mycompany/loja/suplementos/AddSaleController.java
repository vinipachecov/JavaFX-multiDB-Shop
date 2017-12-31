/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.loja.suplementos;

import com.mongodb.BasicDBList;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.bson.Document;
import supportClasses.ProductItem;
import supportClasses.databaseType;

/**
 *
 * @author vinicius
 */
public class AddSaleController extends ControllerModel {

    @FXML
    public Button addItemButton;

    @FXML
    public Button backButton;

    @FXML
    public TextField discountTextField;

    @FXML
    public Button addFinishSaleButton;

    @FXML
    public TableColumn<ProductItem, String> productNameColumn;

    @FXML
    public TableColumn<ProductItem, String> productBrandColumn;

    @FXML
    public TableColumn<ProductItem, String> productTypeColumn;

    @FXML
    public TableColumn<ProductItem, Float> totalProductPriceColumn;
    @FXML
    public TableColumn<ProductItem, Integer> productQuantityColumn;

    @FXML
    public ComboBox<String> clientComboBox;

    @FXML
    public Label subtotalLabel;

    @FXML
    public Label totalLabel;

    @FXML
    public javafx.scene.control.TableView<ProductItem> saleTable;

    public ObservableList<ProductItem> data;

    public Stage dialog;
    public Stage dialogAddItem;

    public boolean saleCreated = false;

    public Document currentSale;
    public ImageView imageView;

    private PrincipalController pc;

    private Integer saleId;

    public AddSaleController(Connection db) {
        super(db);
    }

    AddSaleController(Connection connection, databaseType dbType) {
        super(connection, dbType);
    }

    AddSaleController(MongoDatabase mongoDatabase, databaseType dbType) {
        super(mongoDatabase, dbType);
    }

    public void init(Stage modal, PrincipalController princpController) {

        pc = princpController;

        dialog = modal;

        data = FXCollections.observableArrayList();

        getComboBoxClients();

        productNameColumn.setCellValueFactory(new PropertyValueFactory<ProductItem, String>("name"));
        productBrandColumn.setCellValueFactory(new PropertyValueFactory<ProductItem, String>("brandname"));
        productTypeColumn.setCellValueFactory(new PropertyValueFactory<ProductItem, String>("typename"));
        totalProductPriceColumn.setCellValueFactory(new PropertyValueFactory<ProductItem, Float>("total"));
        productQuantityColumn.setCellValueFactory(new PropertyValueFactory<ProductItem, Integer>("quantity"));

        ImageView im = new ImageView("file:leftarrow.png");
        im.setFitHeight(20);
        im.setFitWidth(20);
        backButton.setGraphic(im);

        saleTable.setItems(data);

        addItemButton.setDisable(true);
        
        currentSale = null;

    }

    @FXML
    public void backToMainScreen() {
        ChangeScreen(dialog, "/fxml/MainScreen.fxml", pc);
    }

    @FXML
    public void cancel() {
        if (dbType != databaseType.mongodb) {
            deleteSale();
            ChangeScreen(dialog, "/fxml/MainScreen.fxml", pc);            
        }else{
            ChangeScreen(dialog, "/fxml/MainScreen.fxml", pc);            
        }        
    }

    public void calculateSubtotalAndTotal() {
        Float subtotal = (float) 0;
        for (ProductItem item : saleTable.getItems()) {
            subtotal += (totalProductPriceColumn.getCellObservableValue(item).getValue());
        }

        subtotalLabel.setText(subtotal.toString());

        Float discount = Float.parseFloat(discountTextField.getText());
        Float total = subtotal - (subtotal * discount);
        totalLabel.setText(total.toString());
    }

    public void deleteSale() {
        try {
            Statement st = null;
            switch (this.dbType) {
                case firebird:
                    st = this.connection.createStatement();
                    st.executeUpdate(
                            "EXECUTE PROCEDURE removeSale(" + saleId + ");"
                    );
                    break;
                case postgres:
                    st = this.connection.createStatement();
                    st.executeUpdate(
                            "DO $$ BEGIN\n"
                            + "    PERFORM removeSale(" + saleId + ");\n"
                            + "END $$;"
                    );
                    break;
            }

        } catch (Exception e) {
            System.out.println("ERRO AO DELETAR SALE " + e.getMessage());
            sendAlert("ERROR",
                    "Error deleting Sale",
                    "Error Deleting current Sale!", Alert.AlertType.ERROR);
        }
    }

    public void getComboBoxClients() {

        try {
            Statement st = null;
            ResultSet rs = null;
            switch (this.dbType) {
                case mongodb:
                    MongoCollection<Document> clients = mongoDatabase.getCollection("clients");

                    try {
                        List<Document> documents = clients.find().into(new ArrayList<Document>());
                        for (Document document : documents) {
                            clientComboBox.getItems().add(document.getString("name"));
                        }
                    } catch (Exception e) {
                        System.out.println(dbType + " error " + ": " + e.getMessage());
                    }
                    break;
                case firebird:
                    try {
                        st = this.connection.createStatement();
                        rs = st.executeQuery(
                                "select CNAME from get_clients;");
                        while (rs.next()) {
                            clientComboBox.getItems().add(rs.getString("CNAME"));
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR GETTING CLIENTS: " + e.getMessage());
                    }
                    break;
                case postgres:
                    try {
                        st = this.connection.createStatement();
                        rs = st.executeQuery(
                                "select * from get_clients();");
                        while (rs.next()) {
                            clientComboBox.getItems().add(rs.getString("name"));
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR GETTING CLIENTS: " + e.getMessage());
                    }
                    break;
            }

        } catch (Exception e) {

        }

    }

    //Add item to this sale
    @FXML
    public void addItemSale() {
        AddSaleItemController itemController = null;
        switch (dbType) {
            case mongodb:
                itemController = new AddSaleItemController(this.mongoDatabase, this.currentSale, this.dbType);
                dialogAddItem = CreateModal(backButton, "/fxml/AddSaleItem.fxml", itemController, "Add Product Item");
                itemController.init(dialogAddItem, saleTable, data, this);
                break;
            default:
                itemController = new AddSaleItemController(connection, this.dbType);
                dialogAddItem = CreateModal(backButton, "/fxml/AddSaleItem.fxml", itemController, "Add Product Item");
                itemController.init(dialogAddItem, saleId, saleTable, data, this);
                break;
        }

    }

    @FXML
    public void createSale(String discount) {
        //Create a sale but it is not finished 
        //so the salesman can add products to this sale and then 
        // generate its invoice

        switch (this.dbType) {
            case mongodb:
                try {

                    BasicDBList saleitems = new BasicDBList();
                    MongoCollection<Document> sales = mongoDatabase.getCollection("sales");                    
                    
                    Date now = new Date();                    
                    currentSale = new Document();
                    
                    currentSale.put("saledate", now);
                    currentSale.put("discount", discount);
                    currentSale.put("client", clientComboBox.getValue());
                    currentSale.put("saleitems", saleitems);
                } catch (Exception e) {
                    System.out.println("Erro no banco " + dbType + ": " + e.getMessage());
                }
                break;
            case firebird:
                try {
                    Statement st = this.connection.createStatement();
                    st.executeUpdate(
                            "EXECUTE PROCEDURE addSale(0.0,0.0,"
                            + "'" + clientComboBox.getValue() + "',"
                            + "" + discount + ", 'F');"
                    );
                    Statement st2 = this.connection.createStatement();
                    ResultSet rs = st2.executeQuery(""
                            + "select max(id) from sales");
                    if (rs.next()) {
                        saleId = rs.getInt("max");
                    }
                } catch (Exception e) {
                    System.out.println("ERROR " + e.getMessage());
                }
                break;
            case postgres:
                try {
                    Statement st = this.connection.createStatement();
                    st.executeUpdate(
                            "DO $$ BEGIN\n"
                            + "PERFORM add_sale(0.0,0.0,"
                            + "'" + clientComboBox.getValue() + "',"
                            + "" + discount + ", 'F');\n"
                            + "END $$;"
                    );
                    Statement st2 = this.connection.createStatement();
                    ResultSet rs = st2.executeQuery(""
                            + "select max(id) from sales");
                    if (rs.next()) {
                        saleId = rs.getInt("max");
                    }
                } catch (Exception e) {
                    System.out.println("ERROR " + e.getMessage());
                }
                break;

        }
        sendAlert(
                "Information",
                "Sale Created!",
                "Now you can Add Items!", Alert.AlertType.CONFIRMATION);
        addItemButton.setDisable(false);
        saleCreated = true;
        addFinishSaleButton.setText("Finish Sale");
    }

    // fake fiscal notes
    public String generateInvoice() {
        boolean newnumber = false;

        switch (this.dbType) {
            case mongodb:
                while (!newnumber) {
                    String invoice = Integer.toString(ThreadLocalRandom.current().nextInt(100000000, 999999999 + 1));
                    MongoCollection<Document> sales = mongoDatabase.getCollection("sales");

                    List<Document> documents = null;
                    documents = sales.find(eq("invoice", invoice)).into(new ArrayList<Document>());
                    if (documents == null || documents.size() == 0) {
                        return invoice;
                    }
                }
                break;
            case firebird:
                while (!newnumber) {
                    String invoice = Integer.toString(ThreadLocalRandom.current().nextInt(100000000, 999999999 + 1));

                    //verify if there is already a sale with the generated fiscal note        
                    System.out.println("invoice = " + invoice);
                    try {
                        Statement st = this.connection.createStatement();
                        st.executeUpdate("EXECUTE PROCEDURE checkInvoiceExists('" + invoice + "');");
                        return invoice;
                    } catch (Exception e) {
                        sendAlert("Invoice Error",
                                "Invoice Exists",
                                "Critical: error on invoice generation", Alert.AlertType.ERROR);
                    }

                }
                sendAlert(
                        "Error invoice generation",
                        "Error creating invoice.",
                        "Critical error on invoice", Alert.AlertType.ERROR);
                break;
            case postgres:
                while (!newnumber) {
                    String invoice = Integer.toString(ThreadLocalRandom.current().nextInt(100000000, 999999999 + 1));

                    //verify if there is already a sale with the generated fiscal note        
                    try {
                        Statement st = this.connection.createStatement();
                        ResultSet rs = st.executeQuery("select * from checkInvoiceIDAlreadyExists('" + invoice + "');");

                        if (rs.next()) {

                        } else {
                            return invoice;
                        }
                    } catch (Exception e) {
                    }

                }
                sendAlert(
                        "Error invoice generation",
                        "Error creating invoice.",
                        "Critical error on invoice", Alert.AlertType.ERROR);
                break;
        }

        return "";
    }

    public void finishSale() {

        switch (this.dbType) {
            case mongodb:
                MongoCollection<Document> sales = mongoDatabase.getCollection("sales");
                try {
                    
                    String invoice = generateInvoice();

                                        
                    
                    currentSale.put("invoice", invoice);
                    currentSale.put("subtotal", subtotalLabel.getText());
                    currentSale.put("total", totalLabel.getText());                    
                    
                    
                    
                    sales.insertOne(currentSale);


                } catch (Exception e) {
                    System.out.println(dbType + " error " + ": " + e.getMessage() + e.getCause());
                }
                break;
            case firebird:
                try {

                    String invoice = generateInvoice();

                    Statement st = this.connection.createStatement();
                    st.executeUpdate(
                            "EXECUTE PROCEDURE setInvoice(" + saleId + ",'" + invoice + "');"
                    );

                    st = this.connection.createStatement();
                    st.executeUpdate(
                            "EXECUTE PROCEDURE finishSale(" + saleId + ", "
                            + subtotalLabel.getText() + " , "
                            + "" + totalLabel.getText() + ");"
                    );

                } catch (Exception e) {
                    System.out.println("Error generating invoice! " + e.getMessage());
                }
                break;
            case postgres:
                try {

                    String invoice = generateInvoice();

                    Statement st = this.connection.createStatement();
                    st.executeUpdate(
                            "DO $$ BEGIN\n"
                            + "    PERFORM setInvoice(" + saleId + ",'" + invoice + "');\n"
                            + "END $$;"
                    );

                    st = this.connection.createStatement();
                    st.executeUpdate(
                            "DO $$ BEGIN\n"
                            + "    PERFORM finishSale(" + saleId + ", "
                            + subtotalLabel.getText() + " , "
                            + "" + totalLabel.getText() + ");\n"
                            + "END $$;"
                    );

                } catch (Exception e) {
                    System.out.println("Error generating invoice! " + e.getMessage());
                }
                break;
        }

        sendAlert("Success",
                "Sale Added",
                "Sale added with success!", Alert.AlertType.CONFIRMATION);        
    }

    @FXML
    public void checkForm() {
        System.out.println("começou a verificar o form");
        String discount = null;
        
        try {
            discount = discountTextField.getText();
        } catch (Exception e) {
            sendAlert("Error Adding new Type",
                    "No Type name", "Choose a product type name.", Alert.AlertType.ERROR);
            return;
        }

        if (discount.equals("")) {
            sendAlert("Error Adding new Type",
                    "No Type name", "Fill all the fields! Choose a product type name.", Alert.AlertType.ERROR);
        } else {
            if (!saleCreated) {
                createSale(discount);
            } else {
                finishSale();
            }
        }
    }

}
