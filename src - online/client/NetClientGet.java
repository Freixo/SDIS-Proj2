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
import javafx.application.Application;
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
import org.json.*;

/**
 *
 * @author Hugo Freixo
 */
public class NetClientGet extends Application {

    private static final String path = "https://hidden-river-8597.herokuapp.com";

    private GridPane grid = new GridPane();
    private Text score = new Text("Score\nTeam1: 0\nTeam2: 0");
    private Text points = new Text("Team1: 0\nTeam2: 0");
    private ImageView trumpImage;

    private Player player = new Player("Player1", "Human");
    private ArrayList<Card> table = new ArrayList<Card>(4);
    private boolean notEventCreated = true;

    public void Begin() {
        String output = POST("/sueca/start", player.getName());

        //System.out.println(output);
        JSONObject json = new JSONObject(output);

        trumpImage = GetImage(new Card(json.getJSONObject("trumpCard")));
        player.setHand(json.getJSONArray("hand"));
        setTable(json.getJSONArray("table"));
    }

    public void ShowHand() {
        getHand();
        int handSize = player.getHand().size();
        for (int i = 1; i <= handSize; ++i) {
            int wight = 13 - handSize / 2 + (i - 1);
            ImageView img = GetImage(player.getHand().get(i - 1));
            grid.add(img, wight, 20);
            final int index = i;

            if (getTurn() == 0) {
                img.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (Play(index - 1)) {
                            Update();
                        }
                        event.consume();
                    }
                });
            }
        }
        if (fullTable()) {
            Points();
            if (notEventCreated) {
                grid.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (!end()) {
                            Update();
                        } else {
                            End();
                        }
                        event.consume();
                    }

                });
                notEventCreated = false;
            }
        } else if (getTurn() != 0) {
            Play();
            Update();
        }

    }

    private boolean end() {
        String output = GET("/sueca/endGame");

        return output.equals("1");
    }

    private void showScore() {

        setScore(getScore());
        setPoints(getPoints());

        grid.add(score, 0, 0);
        grid.add(trumpImage, 13, 0);
        grid.add(points, 26, 0);
    }

    private JSONObject getScore() {
        String output = GET("/sueca/getScore");

        return new JSONObject(output);
    }

    private JSONObject getPoints() {
        String output = GET("/sueca/getPoints");

        return new JSONObject(output);

    }

    public void ShowTable() {
        getTable();

        for (int i = 0; i < table.size(); ++i) {
            int heigth = 0, wight = 0, angle = 0;
            switch (i) {
                case 0:
                    wight = 13;
                    heigth = 11;
                    angle = 0;
                    break;
                case 1:
                    wight = 14;
                    heigth = 10;
                    angle = 90;
                    break;
                case 2:
                    wight = 13;
                    heigth = 9;
                    angle = 180;
                    break;
                case 3:
                    wight = 12;
                    heigth = 10;
                    angle = 270;
                    break;
                default:
                    break;
            }
            ImageView img = GetImage(table.get(i));
            img.setRotate(angle);
            grid.add(img, wight, heigth);
        }
    }

    private void Update() {
        grid.getChildren().clear();

        showScore();
        ShowTable();
        ShowHand();
    }

    private void End() {
        String output = POST("/sueca/end", player.getName());

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

    private void getTable() {
        String output = GET("/sueca/getTable");

        //System.out.println(output);
        setTable(new JSONArray(output));
    }

    private void getHand() {
        String output = POST("/sueca/getHand", player.getName());

        //System.out.println(output);
        setHand(new JSONArray(output));

    }

    private void setHand(JSONArray json) {
        player.getHand().clear();
        for (int i = 0; i < json.length(); ++i) {
            player.getHand().add(new Card(json.optJSONObject(i)));
        }
    }

    private int getTurn() {
        String output = GET("/sueca/getTurn");

        //System.out.println(output);
        int turn = Integer.parseInt(output);

        return turn;

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
        String output = GET("/sueca/points");

        JSONObject json = new JSONObject(output);

        setTable(json.getJSONArray("table"));
        setPoints(json);
    }

    private void setPoints(JSONObject json) {
        points = new Text("Team1: " + json.getInt("team1Points") + "\nTeam2: " + json.getInt("team2Points"));
    }

    private void setScore(JSONObject json) {
        score = new Text("Score\nTeam1: " + json.getInt("team1Games") + "\nTeam2: " + json.getInt("team2Games"));
    }

    private void Play() {
        GET("/sueca/play");
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

        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Begin();

        Update();

        Scene scene = new Scene(grid, 1700, 700);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        scene.getStylesheets().add(NetClientGet.class.getResource("Sueca.css").toExternalForm());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
