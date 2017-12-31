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
public class AddPurchaseController extends ControllerModel {

    @FXML
    public Button addItemButton;

    @FXML
    public Button backButton;

    @FXML
    public TextField discountTextField;

    @FXML
    public Button addFinishPurchaseButton;

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
    public ComboBox<String> suppliersComboBox;

    @FXML
    public Label subtotalLabel;

    @FXML
    public Label totalLabel;

    @FXML
    public javafx.scene.control.TableView<ProductItem> purchaseTable;

    public ObservableList<ProductItem> data;

    public Stage dialog;
    public Stage dialogAddItem;

    public boolean purchaseCreated = false;

    public ImageView imageView;

    private PrincipalController pc;

    public Document currentPurchase;

    private Integer purchaseId;

    public AddPurchaseController(Connection db) {
        super(db);
    }

    AddPurchaseController(Connection connection, databaseType dbType) {
        super(connection, dbType);
    }

    AddPurchaseController(MongoDatabase mongoDatabase, databaseType dbType) {
        super(mongoDatabase, dbType);
    }

    public void init(Stage modal, PrincipalController princpController) {

        pc = princpController;

        dialog = modal;

        data = FXCollections.observableArrayList();

        getComboBoxSuppliers();

        productNameColumn.setCellValueFactory(new PropertyValueFactory<ProductItem, String>("name"));
        productBrandColumn.setCellValueFactory(new PropertyValueFactory<ProductItem, String>("brandname"));
        productTypeColumn.setCellValueFactory(new PropertyValueFactory<ProductItem, String>("typename"));
        totalProductPriceColumn.setCellValueFactory(new PropertyValueFactory<ProductItem, Float>("total"));
        productQuantityColumn.setCellValueFactory(new PropertyValueFactory<ProductItem, Integer>("quantity"));

        ImageView im = new ImageView("file:leftarrow.png");
        im.setFitHeight(20);
        im.setFitWidth(20);
        backButton.setGraphic(im);

        purchaseTable.setItems(data);

        addItemButton.setDisable(true);

    }

    @FXML
    public void backToMainScreen() {
        ChangeScreen(dialog, "/fxml/MainScreen.fxml", pc);
    }

    @FXML
    public void cancel() {
        deletePurchase();
        ChangeScreen(dialog, "/fxml/MainScreen.fxml", pc);
    }

    public void calculateSubtotalAndTotal() {
        Float subtotal = (float) 0;
        for (ProductItem item : purchaseTable.getItems()) {
            subtotal += (totalProductPriceColumn.getCellObservableValue(item).getValue());
        }

        subtotalLabel.setText(subtotal.toString());

        Float discount = Float.parseFloat(discountTextField.getText());
        Float total = subtotal - (subtotal * discount);
        totalLabel.setText(total.toString());
    }

    public void deletePurchase() {
        try {
            Statement st = this.connection.createStatement();
            switch (dbType) {
                case firebird:
                    st.executeUpdate(
                            "EXECUTE PROCEDURE removePurchase(" + purchaseId + ")"
                    );
                    break;
                case postgres:
                    st.executeUpdate(
                            "DO $$ BEGIN\n"
                            + "    PERFORM removePurchase(" + purchaseId + ");\n"
                            + "END $$;"
                    );
                    break;
            }

        } catch (Exception e) {
            System.out.println("ERRO AO DELETAR PURCHASE " + e.getMessage());
            sendAlert("Error Deleting Item",
                    "Error Deleting Purchase",
                    "Error deleting current purchase. " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    public void getComboBoxSuppliers() {

        try {
            Statement st = null;
            ResultSet rs = null;
            switch (this.dbType) {
                case mongodb:
                    System.out.println("entrou");
                    MongoCollection<Document> clients = mongoDatabase.getCollection("suppliers");

                    try {
                        List<Document> documents = clients.find().into(new ArrayList<Document>());
                        for (Document document : documents) {
                            suppliersComboBox.getItems().add(document.getString("fantasyname"));
                        }
                    } catch (Exception e) {
                        System.out.println(dbType + " error " + ": " + e.getMessage());
                    }
                    break;
                case firebird:
                    st  = this.connection.createStatement();
                    try {
                        rs = st.executeQuery(
                                "select cname from get_suppliers;");
                        while (rs.next()) {
                            suppliersComboBox.getItems().add(rs.getString("cname"));
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR GETTING CLIENTS: " + e.getMessage());
                    }
                    break;
                case postgres:
                    st = this.connection.createStatement();
                    try {
                        rs = st.executeQuery(
                                "select * from get_suppliers();");
                        while (rs.next()) {
                            suppliersComboBox.getItems().add(rs.getString("name"));
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
    public void addItemPurchase() {

        AddPurchaseItemController itemController = null;
        switch (dbType) {
            case mongodb:
                itemController = new AddPurchaseItemController(mongoDatabase, dbType, currentPurchase);
                dialogAddItem = CreateModal(backButton, "/fxml/AddPurchaseItem.fxml", itemController, "Add Product Item");
                itemController.init(dialogAddItem, purchaseId, purchaseTable, data, this);
                break;
            default:
                itemController = new AddPurchaseItemController(connection, dbType);
                dialogAddItem = CreateModal(backButton, "/fxml/AddPurchaseItem.fxml", itemController, "Add Product Item");
                itemController.init(dialogAddItem, purchaseId, purchaseTable, data, this);
                break;
        }

    }

    @FXML
    public void createPurchase(String discount) {
        //Create a sale but it is not finished 
        //so the salesman can add products to this sale and then 
        // generate its invoice
        try {
            Statement st = null;
            ResultSet rs = null;
            switch (this.dbType) {
                case mongodb:
                    try {

                        BasicDBList saleitems = new BasicDBList();
                        MongoCollection<Document> purchases = mongoDatabase.getCollection("purchases");

                        Date now = new Date();
                        currentPurchase = new Document();

                        currentPurchase.put("purchasedate", now);
                        currentPurchase.put("discount", discount);
                        currentPurchase.put("supplier", suppliersComboBox.getValue());
                        currentPurchase.put("purchaseitems", saleitems);
                    } catch (Exception e) {
                        System.out.println("Erro no banco " + dbType + ": " + e.getMessage());
                    }
                    break;
                case firebird:
                    try {
                        st = this.connection.createStatement();
                        st.executeUpdate(
                                "EXECUTE PROCEDURE addPurchase(0.0,0.0,"
                                + "'" + suppliersComboBox.getValue() + "',"
                                + "" + discount + ", 'F');"
                        );                        
                        Statement st2 = this.connection.createStatement();
                        rs = st2.executeQuery(
                                "select max(id) from purchases"
                        );
                        if (rs.next()) {
                            purchaseId = rs.getInt("max");
                            System.out.println("PURCHASEID RECEBEU = " + purchaseId);
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR " + e.getMessage());
                    }
                    break;
                case postgres:
                    try {
                        st = this.connection.createStatement();
                        st.executeUpdate(
                                "DO $$ BEGIN\n"
                                + "PERFORM add_purchase(0.0,0.0,"
                                + "'" + suppliersComboBox.getValue() + "',"
                                + "" + discount + ", 'F');\n"
                                + "END $$;"
                        );
                        Statement st2 = this.connection.createStatement();
                        rs = st2.executeQuery(""
                                + "select max(id) from sales");
                        if (rs.next()) {
                            purchaseId = rs.getInt("max");
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR " + e.getMessage());
                    }
                    break;

            }
        } catch (Exception e) {
        }
        sendAlert("Success",
                "Purchase Created",
                "Purchase Created with sucess!", Alert.AlertType.CONFIRMATION);
        addItemButton.setDisable(false);
        purchaseCreated = true;
        addFinishPurchaseButton.setText("Finish Sale");        
    }

    // fake fiscal notes
    public String generateInvoice() {
        boolean newnumber = false;

        while (!newnumber) {
            String invoice = Integer.toString(ThreadLocalRandom.current().nextInt(100000000, 999999999 + 1));

            //verify if there is already a sale with the generated fiscal note        
            try {
                Statement st = null;
                ResultSet rs = null;
                switch (dbType) {
                    case mongodb:
                        MongoCollection<Document> sales = mongoDatabase.getCollection("purchases");

                        List<Document> documents = null;
                        documents = sales.find(eq("invoice", invoice)).into(new ArrayList<Document>());
                        if (documents == null || documents.size() == 0) {
                            return invoice;
                        }
                        break;
                    case firebird:
                        st = this.connection.createStatement();
                        System.out.println("executou check invoice");
                        st.executeUpdate("EXECUTE PROCEDURE checkPurchaseInvoiceExists('" + invoice + "');");                                                                        
                        return invoice;
                    case postgres:
                        st = this.connection.createStatement();
                        rs = st.executeQuery("select * from checkInvoicePurchaseAlreadyExists('" + invoice + "');");
                        if (rs.next()) {
                            sendAlert(
                                    "Error invoice generation",
                                    "Error creating invoice.",
                                    "Critical error on invoice", Alert.AlertType.ERROR);
                        } else {
                            return invoice;
                        }
                        break;
                }
                
            } catch (Exception e) {
                System.out.println("Erro no banco " + dbType + " " + e.getMessage() + e.getCause());
            }
        }
        sendAlert(
                "Error invoice generation",
                "Error creating invoice.",
                "Critical error on invoice", Alert.AlertType.ERROR);
        return "";
    }

    public void finishPurchase() {
        try {
            Float subtotal = Float.parseFloat(subtotalLabel.getText());
            Float total = Float.parseFloat(totalLabel.getText());
            System.out.println("começou  terminar");
                    
            Statement st = null;
            switch (dbType) {
                case mongodb:
                    MongoCollection<Document> purchases = mongoDatabase.getCollection("purchases");
                    try {

                        String invoice = generateInvoice();

                        currentPurchase.put("invoice", invoice);
                        currentPurchase.put("subtotal", subtotalLabel.getText());
                        currentPurchase.put("total", totalLabel.getText());

                        purchases.insertOne(currentPurchase);

                    } catch (Exception e) {
                        System.out.println(dbType + " error " + ": " + e.getMessage() + e.getCause());
                    }
                    break;
                case firebird:
                    
                    st = this.connection.createStatement();
                    st.executeUpdate(
                            "EXECUTE PROCEDURE finishPurchase("
                            + "" + purchaseId + ","
                            + "" + subtotal + ","
                            + "" + total + ");"
                    );
                    System.out.println("executou a finalização");
                    break;
                case postgres:
                    st = this.connection.createStatement();
                    st.executeUpdate(
                            "DO $$ BEGIN\n"
                            + "PERFORM finishPurchase("
                            + "" + purchaseId + ","
                            + "" + subtotal + ","
                            + "" + total + ");\n"
                            + "END $$;"
                    );
                    break;
            }

        } catch (Exception e) {
            System.out.println("ERROR FINISHING PURCHASE " + e.getMessage());
            return;
        }

        try {
            System.out.println("começou a gerar invoice");
            String invoice = generateInvoice();
            System.out.println("gerou invoice " + invoice);
            Statement st = null;
            switch (dbType) {
                case firebird:
                    st = this.connection.createStatement();
                    st.executeUpdate(
                            "EXECUTE PROCEDURE "
                            + "setInvoicePurchase(" + purchaseId + ","
                            + "'" + invoice + "')"
                    );
                    break;
                case postgres:
                    st = this.connection.createStatement();
                    st.executeUpdate(
                            "DO $$ BEGIN\n"
                            + "    PERFORM setPurchaseInvoice(" + purchaseId + ",'" + invoice + "');\n"
                            + "END $$;"
                    );
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error generating invoice! " + e.getMessage());
        }
        sendAlert("Success",
                "Purchase Added",
                "Purchase added with success!", Alert.AlertType.CONFIRMATION);
        addFinishPurchaseButton.setDisable(true);
    }

    @FXML
    public void checkForm() {
        String discount = null;
        try {
            discount = discountTextField.getText();
        } catch (Exception e) {
            sendAlert("Error Adding new Purchase",
                    "Form Error", "Error getting discount value.", Alert.AlertType.ERROR);
            return;
        }       
        

        if (discount.equals("")) {
            sendAlert("Error Adding new Purchase",
                    "Discount value is empty",
                    "Fill all the fields! Set a discountvalue.",
                    Alert.AlertType.ERROR);
        } else {
            if (!purchaseCreated) {
                createPurchase(discount);
            } else {
                finishPurchase();
            }
        }
    }

}
