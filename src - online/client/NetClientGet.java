/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.json.*;

/**
 *
 * @author Hugo Freixo
 */
public class NetClientGet extends Application {

    private static final String path = "https://hidden-river-8597.herokuapp.com";
    String name = "";

    private GridPane grid = new GridPane();
    private Text score = new Text("Score\nTeam1: 0\nTeam2: 0");
    private Text points = new Text("Team1: 0\nTeam2: 0");
    private Text playerNum = new Text("You're Player");
    private Text message = new Text("Waiting for other Players...");
    private ImageView trumpImage;

    private Player player = new Player(name);
    private ArrayList<Card> table = new ArrayList<Card>(4);
    private boolean notEventCreated = true;
    private int myTurn;
    private AutoUpdate au = new AutoUpdate(this);
    private JSONObject state = new JSONObject();

    public void Start() {
        final Text textLoginConfirm = new Text();
        grid.add(textLoginConfirm, 1, 6);

        Text scenetitleLogin = new Text("Login");
        scenetitleLogin.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitleLogin, 0, 0, 2, 1);

        Label userNameLogin = new Label("User Name:");
        grid.add(userNameLogin, 0, 1);

        TextField userTextFieldLogin = new TextField();
        grid.add(userTextFieldLogin, 1, 1);

        Label pwLogin = new Label("Password:");
        grid.add(pwLogin, 0, 2);

        PasswordField pwBoxLogin = new PasswordField();
        grid.add(pwBoxLogin, 1, 2);

        Button btnLogin = new Button("Login");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btnLogin);
        grid.add(hbBtn, 1, 4);
        btnLogin.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                if (!userTextFieldLogin.getText().isEmpty() && !pwBoxLogin.getText().isEmpty()) {
                    if (login(userTextFieldLogin.getText(), pwBoxLogin.getText())) {
                        textLoginConfirm.setFill(Color.FIREBRICK);
                        textLoginConfirm.setText("Login Successful");
                        name = userTextFieldLogin.getText();
                        player = new Player(name);
                        Begin();
                    } else {
                        textLoginConfirm.setFill(Color.FIREBRICK);
                        textLoginConfirm.setText("Login Failed");
                    }
                }
            }
        });

        Separator sep = new Separator();
        sep.setOrientation(Orientation.VERTICAL);
        GridPane.setConstraints(sep, 4, 0);
        GridPane.setRowSpan(sep, 6);
        grid.getChildren().add(sep);

        final Text textRegisterConfirm = new Text();
        grid.add(textRegisterConfirm, 7, 6);

        Text scenetitleRegister = new Text("Register");
        scenetitleRegister.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitleRegister, 6, 0, 2, 1);

        Label userNameRegister = new Label("User Name:");
        grid.add(userNameRegister, 6, 1);

        TextField userTextFieldRegister = new TextField();
        grid.add(userTextFieldRegister, 7, 1);

        Label pwRegister = new Label("Password:");
        grid.add(pwRegister, 6, 2);

        PasswordField pwBoxRegister = new PasswordField();
        grid.add(pwBoxRegister, 7, 2);

        Button btnRegister = new Button("Register");
        HBox hbBtnregister = new HBox(10);
        hbBtnregister.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtnregister.getChildren().add(btnRegister);
        grid.add(hbBtnregister, 7, 4);
        btnRegister.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                if (!userTextFieldRegister.getText().isEmpty() && !pwBoxRegister.getText().isEmpty()) {
                    if (register(userTextFieldRegister.getText(), pwBoxRegister.getText())) {
                        textRegisterConfirm.setFill(Color.FIREBRICK);
                        textRegisterConfirm.setText("Register Successful\n"
                                + "Please Login");
                    } else {
                        textRegisterConfirm.setFill(Color.FIREBRICK);
                        textRegisterConfirm.setText("Register Failed! Try another Username");
                    }
                }

            }
        });
    }

    public void Begin() {
        //System.out.println(name);
        String output = POST("/sueca/start", player.getName());

        JSONObject json = new JSONObject(output);

        trumpImage = GetImage(new Card(json.getJSONObject("trumpCard")));
        myTurn = json.getInt("turn");

        playerNum = new Text("You're " + name + "\nTeam " + (myTurn % 2 + 1));

        playerNum.setId("text");
        score.setId("text");
        points.setId("text");
        message.setId("text");

        try {
            player.setHand(json.getJSONArray("hand"));
            setTable(json.getJSONArray("table"));
            //System.out.println(output);
        } catch (Exception e) {

        }

        au.start();
    }

    public void ShowHand() {
        int turn = getTurn();

        //System.out.println(turn);
        setHand(state.getJSONArray("hand"));
        int handSize = player.getHand().size();

        for (int i = 1; i <= handSize; ++i) {
            int wight = 13 - handSize / 2 + (i - 1);
            ImageView img = GetImage(player.getHand().get(i - 1));
            grid.add(img, wight, 20);
            final int index = i;
            // System.out.println("Printing card " + (i - 1));
            if (turn == myTurn) {
                au.cancel();
                img.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (Play(index - 1)) {
                            // System.out.println("Card Selected " + (index - 1));
                            au.restart();
                        }
                        event.consume();
                    }
                });
            }
        }
        if (fullTable()) {
            au.cancel();

            if (grid.getChildren().remove(message)) {
                message = new Text("Click to continue!");
                message.setId("text");
                grid.add(message, 26, 1);
            }
            // System.out.println("full Table");
            Points();
            if (end()) {
                End();
            }
            if (notEventCreated) {
                grid.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        au.restart();
                        event.consume();
                    }
                });
                notEventCreated = false;
            }

        }
    }

    private boolean end() {
        String output = POST("/sueca/endGame", player.getName());

        return output.equals("1");
    }

    private void showScore() {

        setScore();
        setPoints();
        setImage();
        setMessage();

        grid.add(score, 0, 0);
        grid.add(playerNum, 0, 2);
        grid.add(trumpImage, 13, 0);

        if (myTurn == getTurn()) {
            message = new Text("It's your turn");
            message.setId("text");
        }

        grid.add(message, 26, 1);
        grid.add(points, 26, 0);

    }

    public void ShowTable() {
        setTable(state.getJSONArray("table"));

        for (int i = 0; i < table.size(); ++i) {
            int heigth = 0, wight = 0, angle = 0;

            int diff = myTurn - i;
            switch (diff) {
                case -3:
                    wight = 12;
                    heigth = 10;
                    angle = 270;
                    break;
                case -2:
                    wight = 13;
                    heigth = 9;
                    angle = 180;
                    break;
                case -1:
                    wight = 14;
                    heigth = 10;
                    angle = 90;
                    break;
                case 0:
                    wight = 13;
                    heigth = 11;
                    angle = 0;
                    break;
                case 1:
                    wight = 12;
                    heigth = 10;
                    angle = 270;
                    break;
                case 2:
                    wight = 13;
                    heigth = 9;
                    angle = 180;
                    break;
                case 3:
                    wight = 14;
                    heigth = 10;
                    angle = 90;
                    break;
                default:
                    break;
            }
            ImageView img = GetImage(table.get(i));
            img.setRotate(angle);
            grid.add(img, wight, heigth);
        }
    }

    public void Update() {
        state = new JSONObject(POST("/sueca/getState", player.getName()));

        //System.out.println(state);
        grid.getChildren().clear();
        if (state.has("hand")) {
            showScore();
            ShowTable();
            ShowHand();
        } else {
            setMessage();
            grid.add(message, 13, 2);
        }
    }

    private void End() {
        String output = POST("/sueca/end", player.getName());
        // System.out.println("END: " + output);

        JSONObject json = new JSONObject(output);

        trumpImage = GetImage(new Card(json.getJSONObject("trumpCard")));
        player.setHand(json.getJSONArray("hand"));
        setTable(json.getJSONArray("table"));

    }

    private ImageView GetImage(Card card) {
        //System.out.println("img/card" + card.getSuit() + card.getValue() + ".png");
        return new ImageView(new Image("img/card" + card.getSuit() + card.getValue() + ".png", 0, 100, true, false));
    }

    private void setTable(JSONArray json) {
        table.clear();
        for (int i = 0; i < json.length(); ++i) {
            table.add(new Card(json.optJSONObject(i)));
        }
    }

    private void setHand(JSONArray json) {
        player.getHand().clear();
        for (int i = 0; i < json.length(); ++i) {
            player.getHand().add(new Card(json.optJSONObject(i)));
        }
    }

    private int getTurn() {
        if (fullTable()) {
            return -1;
        }
        return state.getInt("turn");
    }

    private boolean Play(int index) {
        String output = POST("/sueca/humanPlay", player.getName() + " " + index);

        //System.out.println(output);
        return output.equals("1");
    }

    private boolean fullTable() {
        for (int i = 0; i < table.size(); ++i) {
            if (table.get(i).getValue() == 0) {
                return false;
            }
        }
        return true;
    }

    private void Points() {
        String output = POST("/sueca/points", player.getName());

        JSONObject json = new JSONObject(output);

        setTable(json.getJSONArray("table"));
        setPoints();
    }

    private void setPoints() {
        points = new Text("Team1: " + state.getInt("team1Points") + "\nTeam2: " + state.getInt("team2Points"));
        points.setId("text");
    }

    private void setScore() {
        score = new Text("Score\nTeam1: " + state.getInt("team1Games") + "\nTeam2: " + state.getInt("team2Games"));
        score.setId("text");
    }

    private void setMessage() {
        //System.out.println(state.getString("message"));
        message = new Text(state.getString("message"));
        message.setId("text");
    }

    private boolean register(String name, String password) {
        String output = POST("/sueca/register", name + " " + password);
        //System.out.println(output);
        return !output.startsWith("ERROR");
    }

    private boolean login(String name, String password) {
        String output = POST("/sueca/login", name + " " + password);
        return output.equals("1");
    }

    private String POST(String route, String input) {
        try {
            URL url = new URL(path + route);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output = "";
            String aux;
            while ((aux = br.readLine()) != null) {
                output += aux;
            }

            conn.disconnect();

            return output;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String GET(String route) {
        try {
            URL url = new URL(path + route);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output = "";
            String aux;
            while ((aux = br.readLine()) != null) {
                output += aux;
            }

            conn.disconnect();

            return output;

        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();

        }
        return "";
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Sueca by Hugo Freixo");

        primaryStage.setOnCloseRequest(
                new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        au.destroy();
                        POST("/sueca/close", name);
                        try {
                            stop();

                        } catch (Exception ex) {
                            Logger.getLogger(NetClientGet.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
        );

        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Start();

        Scene scene = new Scene(grid, 1700, 700);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        scene.getStylesheets().add(NetClientGet.class.getResource("Sueca.css").toExternalForm());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);

    }

    private void setImage() {
        trumpImage = GetImage(new Card(state.getJSONObject("trump")));
    }
}

class AutoUpdate extends Thread {

    NetClientGet NGC;

    boolean running;
    boolean updating;

    long frameRate = 300;

    AutoUpdate(NetClientGet ncg) {
        NGC = ncg;
        running = true;
        updating = true;
    }

    public void run() {
        while (running) {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    if (updating) {
                        NGC.Update();
                    }
                }

            });
            try {
                Thread.sleep(frameRate);
            } catch (Exception e) {

            }
        }
    }

    public void destroy() {
        running = false;
    }

    public void cancel() {
        updating = false;
    }

    public void restart() {
        updating = true;
    }
}
