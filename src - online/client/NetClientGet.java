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
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
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
    String name = "Hugo" + new Random().nextInt(10000);

    private GridPane grid = new GridPane();
    private Text score = new Text("Score\nTeam1: 0\nTeam2: 0");
    private Text points = new Text("Team1: 0\nTeam2: 0");
    private Text playerNum = new Text("You're Player");
    private Text isTurn = new Text("It's your turn!");
    private ImageView trumpImage;

    private Player player = new Player(name);
    private ArrayList<Card> table = new ArrayList<Card>(4);
    private boolean notEventCreated = true;
    private int myTurn;
    private AutoUpdate au = new AutoUpdate(this);
    private JSONObject state = new JSONObject();

    public void Begin() {

        System.out.println(name);
        String output = POST("/sueca/start", player.getName());

        JSONObject json = new JSONObject(output);

        trumpImage = GetImage(new Card(json.getJSONObject("trumpCard")));
        myTurn = json.getInt("turn");

        playerNum = new Text("You're Player" + (myTurn + 1));

        playerNum.setId("text");
        score.setId("text");
        points.setId("text");
        isTurn.setId("text");

        try {
            player.setHand(json.getJSONArray("hand"));
            setTable(json.getJSONArray("table"));
            System.out.println(output);
        } catch (Exception e) {

            String hand = "error";
            do {
                hand = POST("/sueca/getHand", player.getName());
                System.out.println(hand);
            } while (hand.startsWith("error"));

            String table = "error";
            do {
                table = POST("/sueca/getTable", player.getName());
                System.out.println(hand);
            } while (table.startsWith("error"));

            setHand(new JSONArray(hand));
            setTable(new JSONArray(table));

        }
    }

    public void ShowHand() {
        int turn = getTurn();

        System.out.println(turn);
        setHand(state.getJSONArray("hand"));
        int handSize = player.getHand().size();

        for (int i = 1; i <= handSize; ++i) {
            int wight = 13 - handSize / 2 + (i - 1);
            ImageView img = GetImage(player.getHand().get(i - 1));
            grid.add(img, wight, 20);
            final int index = i;
            System.out.println("Printing card " + (i - 1));
            if (turn == myTurn) {
                au.cancel();
                img.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (Play(index - 1)) {
                            System.out.println("Card Selected " + (index - 1));
                            au.restart();
                        }
                        event.consume();
                    }
                });
            }
        }
        if (fullTable()) {
            au.cancel();
            System.out.println("full Table");
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

        } else if (turn != myTurn) {
            System.out.println("Not my turn");
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
        grid.add(score, 0, 0);
        grid.add(playerNum, 13, 1);
        grid.add(trumpImage, 13, 0);

        if (myTurn == getTurn()) {
            grid.add(isTurn, 13, 2);
        }
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
        JSONObject json = new JSONObject(POST("/sueca/getState", player.getName()));
        if (!json.equals(state)) {
            grid.getChildren().clear();

            state = json;
            showScore();
            ShowTable();
            ShowHand();
        }
    }

    private void End() {
        String output = POST("/sueca/end", player.getName());
        System.out.println("END: " + output);

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

    private void register(String name, String password) {
        String output = POST("/sueca/register", name + " " + password);
        System.out.println(output);
    }

    private void login(String name, String password) {
        String output = POST("/sueca/login", name + " " + password);
        System.out.println(output);
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
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
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

        Begin();
        //login("Hugo", "12345");
        au.start();

        Scene scene = new Scene(grid, 1700, 700);
        primaryStage.setScene(scene);
        //primaryStage.setFullScreen(true);
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
