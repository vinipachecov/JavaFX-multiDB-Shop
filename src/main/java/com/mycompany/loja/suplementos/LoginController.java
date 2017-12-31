package com.mycompany.loja.suplementos;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import supportClasses.User;
import supportClasses.databaseType;
import supportClasses.userType;

public class LoginController extends ControllerModel {

    private String[] databases = {"PostgreSQL", "Firebird", "MongoDB"};

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private ComboBox<String> dbComboBox;

    @FXML
    private ImageView logoImageView;

    public LoginController(Connection db) {
        super(db);
    }

    public void init() {
        dbComboBox.getItems().addAll(databases);
        Image img = new Image("file:bb-logo-clean.png");
        logoImageView.setImage(img);
    }

    @FXML
    public void connectDatabase() {
        String dbToConnect = null;

        try {

            dbToConnect = dbComboBox.getValue();

        } catch (Exception e) {
            sendAlert("Error",
                    "No Database Selected.",
                    "Select a database!", Alert.AlertType.ERROR);
        }

        switch (dbToConnect) {
            case "PostgreSQL":
                connectDBPostgre();
                break;
            case "Firebird":
                connectDBFirebird();
                break;
            case "MongoDB":
                connectMongoDB();
                break;
        }
    }

    public void connectMongoDB() {
        MongoClient mongoClient = null;

        System.out.println("Connecting to MongoDB!");
        try {            
            MongoClientURI uri = new MongoClientURI("mongodb://test:uTTtujcMU0OHIUL2@cluster0-shard-00-00-frcus.mongodb.net:27017,cluster0-shard-00-01-frcus.mongodb.net:27017,cluster0-shard-00-02-frcus.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin");
            // Creating a Mongo client 
            mongoClient = new MongoClient(uri);

        } catch (Exception e) {
            sendAlert(
                    "Error Login",
                    "Database Error",
                    "Fail to connect to Database", Alert.AlertType.ERROR);
        }
        System.out.println("Connected to Database");

        String username = usernameTextField.getText();
        String password = passwordField.getText();
        loginMongoDB(mongoClient, username, password);

    }

    public void loginMongoDB(MongoClient client, String username, String password) {
        mongoDatabase = client.getDatabase("store");

        MongoCollection<Document> users = mongoDatabase.getCollection("users");

        BasicDBObject andquery = new BasicDBObject();
        List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
        obj.add(new BasicDBObject("username", username));
        obj.add(new BasicDBObject("password", password));
        andquery.put("$and", obj);

        List<Document> documents = users.find().filter(andquery).into(new ArrayList<Document>());

        if (documents.size() != 0) {

            User current = new User(username,
                    userType.ParseUserType(documents.get(0).get("usertype").toString())
            );
            Stage stage;
            PrincipalController controller = new PrincipalController(mongoDatabase, current, databaseType.mongodb);
            stage = ChangeScreen(loginButton, "/fxml/MainScreen.fxml", controller);
            controller.init(stage);

        } else {
            sendAlert("Login Error",
                    "Error doing Login",
                    "Username and password are incorrect.",
                    Alert.AlertType.ERROR);
        }
    }

    public void connectDBPostgre() {
        System.out.println("-------- PostgreSQL "
                + "JDBC Connection Testing ------------");

        try {

            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {
            System.out.println("Where is your PostgreSQL JDBC Driver? "
                    + "Include in your library path!");
            e.printStackTrace();
            return;

        }

        connection = null;

        try {

            connection = DriverManager.getConnection(
                    "jdbc:postgresql://127.0.0.1:5432/loja-suplemento", "postgres",
                    "123456");

        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println("Connection to Postgres Succeded!");
        } else {
            System.out.println("Failed to make connection!");
        }
        checkLoginPostgres(usernameTextField.getText().toString(), passwordField.getText().toString());

    }

    public void connectDBFirebird() {
        System.out.println("-------- FireBird "
                + "JDBC Connection Testing ------------");

        try {

            Class.forName("org.firebirdsql.jdbc.FBDriver");

        } catch (ClassNotFoundException e) {
            System.out.println("Where is your PostgreSQL JDBC Driver? "
                    + "Include in your library path!");
            e.printStackTrace();
            return;

        }

        connection = null;

        try {
            java.util.Properties connectionProperties = new java.util.Properties();
            connectionProperties.put("user", "SYSDBA");
            connectionProperties.put("password", "masterkey");
            connection = DriverManager.getConnection(
                    "jdbc:firebirdsql://localhost:3050//home/vinicius/repos/JavaFX-multiDB-Shop/Loja-Suplementos/FireBirdDataBase.fdb", connectionProperties
            );

        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println("Connection to Firebird Succeded!");

        } else {
            System.out.println("Failed to make connection!");
        }
        System.out.println("username " + usernameTextField.getText());
        System.out.println("password = " + passwordField.getText());
        checkLoginFirebird(usernameTextField.getText().toString(), passwordField.getText().toString());

    }

    public boolean checkLoginPostgres(String username, String password) {
        System.out.println("Signing in...");
        try {
            // Find user
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("select * "
                    + " FROM users "
                    + " WHERE username = '" + username + "'"
                    + " AND password = '" + password + "'"
                    + " limit 1");
            // found a user                 
            if (rs.next()) {
                User current = new User(username,
                        userType.ParseUserType(rs.getString("usertype").toString())
                );
                Stage stage;
                PrincipalController controller = new PrincipalController(this.connection, current, databaseType.postgres);
                stage = ChangeScreen(loginButton, "/fxml/MainScreen.fxml", controller);
                controller.init(stage);
            } else {
                sendAlert("Login Error",
                        "Error doing Login",
                        "Username and password are incorrect.",
                        Alert.AlertType.ERROR);
            }
            rs.close();
            st.close();

        } catch (Exception e) {
            sendAlert("Erro de Login",
                    "Erro na função de login!",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean checkLoginFirebird(String username, String password) {
        System.out.println("Signing in...");
        try {
            // Find user
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT usertype first "
                    + " FROM  USERS "
                    + " WHERE USERNAME = '" + username + "'"
                    + " AND PASSWORD = '" + password + "'"
            );
            // found a user
            if (rs.next()) {

                String tipousuario = rs.getString("usertype").toString();
                tipousuario = tipousuario.replace(" ", "");
                User current = new User(username,
                        userType.ParseUserType(tipousuario)
                );
                Stage stage;
                PrincipalController controller = new PrincipalController(this.connection, current, databaseType.firebird);
                stage = ChangeScreen(loginButton, "/fxml/MainScreen.fxml", controller);
                controller.init(stage);
            } else {
                sendAlert("Login Error",
                        "Error doing Login",
                        "Username or password are incorrect.",
                        Alert.AlertType.ERROR);
            }
            rs.close();
            st.close();

        } catch (Exception e) {
            sendAlert("Erro de Login",
                    "Erro na função de login!",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
            System.out.println(e.getMessage());
        }
        return false;
    }

}
