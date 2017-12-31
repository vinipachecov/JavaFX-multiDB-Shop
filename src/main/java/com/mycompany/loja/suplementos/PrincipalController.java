/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.loja.suplementos;

import com.mongodb.client.MongoDatabase;
import supportClasses.BestClient;
import java.sql.Connection;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import supportClasses.MostRequiredSupplier;
import supportClasses.NewClient;
import supportClasses.TopProduct;
import supportClasses.User;
import supportClasses.databaseType;
import supportClasses.userType;

/**
 * FXML Controller class
 *
 * @author vinicius
 */
public class PrincipalController extends ControllerModel {

    /**
     * Initializes the controller class.
     */
    @FXML
    public MenuBar menuBar;
    @FXML
    public MenuItem addUserMenuItem;

//    TOP SOLD PRODUCTS
    @FXML
    public TextField yearTopProductsTextField;

    @FXML
    public TableView<TopProduct> topProductsTable;

    @FXML
    public TableColumn<TopProduct, String> productNameColumn;

    @FXML
    public TableColumn<TopProduct, Integer> numberSalesColumn;

    @FXML
    public TableColumn<TopProduct, Float> cashGeneratedColumn;

    public TopSaleProductsController topSaleProductsController;

    // WORST SOLD PRODUCTS
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

    public WorstSaleProductsController worstSaleProductsController;

// TOP CLIENTS
    @FXML
    public TextField bestClientTextField;

    @FXML
    public TableView<BestClient> bestClientsTable;

    @FXML
    public TableColumn<BestClient, String> clientNameBCColumn;

    @FXML
    public TableColumn<BestClient, Integer> boughtTimesBCColumn;

    @FXML
    public TableColumn<BestClient, Float> cashGeneratedBCColumn;

    public BestClientsController bestClientsController;

//  NEW CLIENTS
    @FXML
    public TextField yearNewClientsTextField;

    @FXML
    public TableView<NewClient> newClientsTable;

    @FXML
    public TableColumn<NewClient, String> clientNameNCColumn;

    @FXML
    public TableColumn<NewClient, String> contactColumn;

    @FXML
    public TableColumn<NewClient, String> JoinDateNCColumn;

    public NewClientsController newClientsController;

    // MOST REQUIRED SUPPLIERS
    @FXML
    public TextField requiredSupplierTextField;

    @FXML
    public TableView<MostRequiredSupplier> mostRequiredSuppliersTable;

    @FXML
    public TableColumn<MostRequiredSupplier, String> nameMRSColumn;

    @FXML
    public TableColumn<MostRequiredSupplier, Integer> totalItemsMRSColumn;

    @FXML
    public TableColumn<MostRequiredSupplier, Float> totalPercentMRSColumn;

    public MostRequiredSuppliersController mostRequiredSuppliersController;

    //
    public Stage dialog;

    public Stage currentStage;

    public PrincipalController(Connection db, User current) {
        super(db, current);
    }

    public PrincipalController(Connection db, User current, databaseType databType) {
        super(db, current, databType);

    }

    PrincipalController(MongoDatabase mongoDatabase, User current, databaseType databaseType) {
        super(mongoDatabase, current, databaseType);
    }

    public void init(Stage stage) {
        this.currentStage = stage;

        topSaleProductsController = new TopSaleProductsController(yearTopProductsTextField, topProductsTable, productNameColumn, numberSalesColumn, cashGeneratedColumn, connection, dbType);
        bestClientsController = new BestClientsController(bestClientTextField, bestClientsTable, clientNameBCColumn, boughtTimesBCColumn, cashGeneratedBCColumn, connection, dbType);
        worstSaleProductsController = new WorstSaleProductsController(yearWorstProductsTextField, worstProductsTable, productNameWSColumn, numberSalesWSColumn, cashGeneratedWSColumn, connection, dbType);
        newClientsController = new NewClientsController(yearNewClientsTextField, newClientsTable, clientNameNCColumn, contactColumn, JoinDateNCColumn, connection, dbType);
        mostRequiredSuppliersController = new MostRequiredSuppliersController(requiredSupplierTextField, mostRequiredSuppliersTable, nameMRSColumn, totalItemsMRSColumn, totalPercentMRSColumn, connection, dbType);

    }

    @FXML
    public void searchRequiredSuppliers(ActionEvent event) {
        mostRequiredSuppliersController.searchMostRequiredSuppliers(event);
    }

    @FXML
    public void searchNewClients(ActionEvent event) {
        newClientsController.searchNewClients(event);
    }

    @FXML
    public void searchWorstSaleProducts(ActionEvent event) {
        worstSaleProductsController.searchWorstProducts(event);
    }

    @FXML
    public void searchTopProducts(ActionEvent event) {
        topSaleProductsController.searchTopProducts(event);
    }

    @FXML
    public void searchBestClients() {
        bestClientsController.searchBestClients();
    }

    @FXML
    public void checkHistory(ActionEvent event) {
        if (getUserType() == userType.admin) {
            CheckHistoryController historyController = new CheckHistoryController(this.connection, this.dbType);
            dialog = CreateModal(event, menuBar,
                    "/fxml/CheckHistory.fxml",
                    historyController,
                    "Check History");
            historyController.init(dialog);
        } else {
            sendAlert("Access error",
                    "Attempt to access admin feature.",
                    "You have no access to check History. Sign in as an administrator.",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void addSale(ActionEvent event) {
        AddSaleController saleController = null;
        switch (dbType) {
            case mongodb:
                saleController = new AddSaleController(this.mongoDatabase, this.dbType);
                ChangeScreen(menuBar,
                        "/fxml/AddSale.fxml",
                        saleController,
                        "Add a Sale");
                saleController.init(currentStage, this);
                break;
            default:
                saleController = new AddSaleController(this.connection, this.dbType);
                ChangeScreen(menuBar,
                        "/fxml/AddSale.fxml",
                        saleController,
                        "Add a Sale");
                saleController.init(currentStage, this);
                break;
        }

    }

    @FXML
    public void addPurchase(ActionEvent event) {

        AddPurchaseController purchaseController = null;
        switch (dbType) {
            case mongodb:
                purchaseController = new AddPurchaseController(this.mongoDatabase, this.dbType);
                ChangeScreen(menuBar,
                        "/fxml/AddPurchase.fxml",
                        purchaseController,
                        "Add a Purchase");
                purchaseController.init(currentStage, this);
                break;
            default:
                purchaseController = new AddPurchaseController(this.connection, this.dbType);
                ChangeScreen(menuBar,
                        "/fxml/AddPurchase.fxml",
                        purchaseController,
                        "Add a Purchase");
                purchaseController.init(currentStage, this);
                break;
        }

    }

    @FXML
    public void searchProduct(ActionEvent event) {
        SearchProductController searchController = null;
        switch (dbType) {
            case mongodb:
                searchController = new SearchProductController(this.mongoDatabase, this.dbType);
                dialog = CreateModal(event, menuBar, "/fxml/SearchProduct.fxml", searchController, "Searching Products");
                searchController.init(dialog);
                break;
            default:
                searchController = new SearchProductController(connection, this.dbType);
                dialog = CreateModal(event, menuBar, "/fxml/SearchProduct.fxml", searchController, "Searching Products");
                searchController.init(dialog);
                break;
        }

    }

    @FXML
    public void addSupplier(ActionEvent event) {
        if (getUserType() == userType.admin) {
            AddSupplierController addSupplier = null;
            switch (dbType) {
                case mongodb:
                    addSupplier = new AddSupplierController(this.mongoDatabase, this.dbType);
                    dialog = CreateModal(event, menuBar,
                            "/fxml/AddSupplier.fxml",
                            addSupplier,
                            "Add a Supplier");
                    addSupplier.init(dialog);
                    break;
                default:
                    addSupplier = new AddSupplierController(this.connection, this.dbType);
                    dialog = CreateModal(event, menuBar,
                            "/fxml/AddSupplier.fxml",
                            addSupplier,
                            "Add a Supplier");
                    addSupplier.init(dialog);
                    break;

            }
        } else {
            sendAlert("Access error",
                    "Attempt to access admin feature.",
                    "You have no access to add Suppliers. Sign in as an administrator.",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void searchSupplier(ActionEvent event) {

        if (getUserType() == userType.admin) {
            SearchSupplierController searchController = null;
            switch (dbType) {
                case mongodb:
                    searchController = new SearchSupplierController(this.mongoDatabase, this.dbType);
                    dialog = CreateModal(event, menuBar, "/fxml/SearchSupplier.fxml", searchController, "Searching Suppliers");
                    searchController.init(dialog);
                    break;
                default:
                    searchController = new SearchSupplierController(connection, this.dbType);
                    dialog = CreateModal(event, menuBar, "/fxml/SearchSupplier.fxml", searchController, "Searching Suppliers");
                    searchController.init(dialog);
                    break;

            }

        } else {
            sendAlert("Access error",
                    "Attempt to access admin feature.",
                    "You have no access to search Suppliers data. Sign in as an administrator.",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void searchPurchase(ActionEvent event) {

        SearchPurchaseController searchController = null;
        if (getUserType() == userType.admin) {

            switch (dbType) {
                case mongodb:
                    searchController = new SearchPurchaseController(mongoDatabase, this.dbType);
                    dialog = CreateModal(event, menuBar, "/fxml/SearchPurchase.fxml", searchController, "Searching Purchases");
                    searchController.init(dialog);
                    break;
                default:
                    searchController = new SearchPurchaseController(connection, this.dbType);
                    dialog = CreateModal(event, menuBar, "/fxml/SearchPurchase.fxml", searchController, "Searching Purchases");
                    searchController.init(dialog);
                    break;
            }
        } else {
            sendAlert("Access error",
                    "Attempt to access admin feature.",
                    "You have no access to search Suppliers data. Sign in as an administrator.",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void searchSale(ActionEvent event) {

        if (getUserType() == userType.admin) {
            SearchSaleController searchController = null;
            switch (dbType) {
                case mongodb:
                    searchController = new SearchSaleController(this.mongoDatabase, this.dbType);
                    dialog = CreateModal(event, menuBar, "/fxml/SearchSale.fxml", searchController, "Searching Sales");
                    searchController.init(dialog);
                    break;
                default:
                    searchController = new SearchSaleController(connection, this.dbType);
                    dialog = CreateModal(event, menuBar, "/fxml/SearchSale.fxml", searchController, "Searching Sales");
                    searchController.init(dialog);
                    break;
            }

        } else {
            sendAlert("Access error",
                    "Attempt to access admin feature.",
                    "You have no access to search Suppliers data. Sign in as an administrator.",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void searchBrands(ActionEvent event) {
        SearchBrandController searchController = null;
        switch (dbType) {
            case mongodb:
                searchController = new SearchBrandController(this.mongoDatabase, this.dbType);
                dialog = CreateModal(event, menuBar, "/fxml/SearchBrand.fxml", searchController, "Searching Brands");
                searchController.init(dialog);
                break;
            default:
                searchController = new SearchBrandController(connection, this.dbType);
                dialog = CreateModal(event, menuBar, "/fxml/SearchBrand.fxml", searchController, "Searching Brands");
                searchController.init(dialog);
                break;
        }

    }

    @FXML
    public void addProduct(ActionEvent event) {
        if (getUserType() == userType.admin) {
            AddProductController productController = null;
            switch (dbType) {
                case mongodb:
                    productController = new AddProductController(this.mongoDatabase, this.dbType);
                    dialog = CreateModal(event, menuBar,
                            "/fxml/AddProduct.fxml",
                            productController,
                            "Add a Product");
                    productController.init(dialog);
                    break;
                default:
                    productController = new AddProductController(connection, this.dbType);
                    dialog = CreateModal(event, menuBar,
                            "/fxml/AddProduct.fxml",
                            productController,
                            "Add a Product");
                    productController.init(dialog);
                    break;
            }

        } else {
            sendAlert("Access error",
                    "Attempt to access admin feature.",
                    "You have no access to add Products. Sign in as an administrator.",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void addTypes(ActionEvent event) {
        if (getUserType() == userType.admin) {
            AddTypeController addTypeController = null;
            switch (dbType) {
                case mongodb:
                    addTypeController = new AddTypeController(this.mongoDatabase, this.dbType);
                    dialog = CreateModal(event, menuBar,
                            "/fxml/AddType.fxml",
                            addTypeController,
                            "Add a Product Type");
                    addTypeController.init(dialog);
                    break;
                default:
                    addTypeController = new AddTypeController(this.connection, this.dbType);
                    dialog = CreateModal(event, menuBar,
                            "/fxml/AddType.fxml",
                            addTypeController,
                            "Add a Product Type");
                    addTypeController.init(dialog);
                    break;
            }
        } else {
            sendAlert("Access error",
                    "Attempt to access admin feature.",
                    "You have no access to add Types. Sign in as an administrator.",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void searchClients(ActionEvent event) {

        if (getUserType() == userType.admin) {
            SearchClientController searchController = null;
            switch (dbType) {
                case mongodb:
                    searchController = new SearchClientController(this.mongoDatabase, this.dbType);
                    dialog = CreateModal(event, menuBar, "/fxml/SearchClient.fxml", searchController, "Searching Clients");
                    searchController.init(dialog);
                    break;
                default:
                    searchController = new SearchClientController(connection, this.dbType);
                    dialog = CreateModal(event, menuBar, "/fxml/SearchClient.fxml", searchController, "Searching Clients");
                    searchController.init(dialog);
                    break;
            }
        } else {
            sendAlert("Access error",
                    "Attempt to access admin feature.",
                    "You have no access to search Client data. Sign in as an administrator.",
                    Alert.AlertType.ERROR);
        }

    }

    @FXML
    public void addClient(ActionEvent event) {
        AddClientController clientController = null;
        switch (dbType) {
            case mongodb:
                clientController = new AddClientController(this.mongoDatabase, this.dbType);
                dialog = CreateModal(event, menuBar,
                        "/fxml/AddClient.fxml",
                        clientController,
                        "Add new Client");
                clientController.init(dialog);
                break;
            default:
                clientController = new AddClientController(this.connection, this.dbType);
                dialog = CreateModal(event, menuBar,
                        "/fxml/AddClient.fxml",
                        clientController,
                        "Add new Client");
                clientController.init(dialog);
                break;
        }

    }

    @FXML
    public void searchTypes(ActionEvent event) {
        SearchTypeController controller = null;
        switch (dbType) {
            case mongodb:
                controller = new SearchTypeController(this.mongoDatabase, this.dbType);
                dialog = CreateModal(event, menuBar, "/fxml/SearchType.fxml", controller, "Searching Types");
                controller.init(dialog);
                break;
            default:
                controller = new SearchTypeController(connection, this.dbType);
                dialog = CreateModal(event, menuBar, "/fxml/SearchType.fxml", controller, "Searching Types");
                controller.init(dialog);
                break;
        }

    }

    @FXML
    public void addBrands(ActionEvent event) {

        if (getUserType() == userType.admin) {
            AddBrandController brandController = null;
            switch (dbType) {
                case mongodb:
                    brandController = new AddBrandController(this.mongoDatabase, this.dbType);
                    dialog = CreateModal(event, menuBar,
                            "/fxml/AddBrand.fxml",
                            brandController,
                            "Add a Brand");
                    brandController.init(dialog);
                    break;
                default:
                    brandController = new AddBrandController(this.connection, this.dbType);
                    dialog = CreateModal(event, menuBar,
                            "/fxml/AddBrand.fxml",
                            brandController,
                            "Add a Brand");
                    brandController.init(dialog);
                    break;
            }
        } else {
            sendAlert("Access error",
                    "Attempt to access admin feature.",
                    "You have no access to add Brands. Sign in as an administrator.",
                    Alert.AlertType.ERROR);
        }

    }

    @FXML
    public void Logout(ActionEvent event) {
        LoginController lc = new LoginController(null);
        ChangeScreen(menuBar, "/fxml/LoginScreen.fxml", lc, "Loja de Suplementos");
        lc.init();
    }

    @FXML
    public void addUserMenu(ActionEvent event) {
        if (getUserType() == userType.admin) {
            AddUserController controller = new AddUserController(connection, this.currentUser);
            dialog = CreateModal(event, menuBar, "/fxml/AddUser.fxml", controller, "Add a User");
            controller.init(dialog);
        } else {
            sendAlert("Access Error",
                    "Attempt to access admin feature.",
                    "YOu are not allowed to add Users. Sign in as an administrator.",
                    Alert.AlertType.ERROR);
        }
    }
}
