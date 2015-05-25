/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sueca;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import Card.*;
import Client.*;
import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Hugo Freixo
 */
public class Sueca extends Application {

    private GridPane grid = new GridPane();
    private Text score = new Text("Score\nTeam1: 0\nTeam2: 0");
    private Text points = new Text("Team1: 0\nTeam2: 0");
    private ImageView trumpImage;

    private Dealer dealer;
    private Player player = new Player("Player1", "Human");
    private ArrayList<Card> table = new ArrayList<Card>(4);
    private boolean notEventCreated = true;

    public void Begin() {
        dealer = new Dealer(player);
        dealer.shuffle();

        Card trumpCard = dealer.drawTrump();
        trumpImage = GetImage(trumpCard);

        dealer.drawCards();
        player.setHand(dealer.getHand());

        table = dealer.getTable();
    }

    public void Play() {
        if (dealer.getTurn() != 0) {
            dealer.play();
        }
    }

    public void ShowHand() {
        player.setHand(dealer.getHand());
        int handSize = player.getHand().size();
        for (int i = 1; i <= handSize; ++i) {
            int wight = 13 - handSize / 2 + (i - 1);
            ImageView img = GetImage(player.getHand().get(i - 1));
            grid.add(img, wight, 20);
            final int index = i;

            if (dealer.getTurn() == 0) {
                img.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (dealer.play(index - 1)) {
                            Update();
                        }
                        event.consume();
                    }
                });
            }
        }
        if (dealer.fullTable()) {
            Points();
            if (notEventCreated) {
                grid.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (!dealer.end()) {
                            Update();
                        } else {
                            End();
                        }
                        event.consume();
                    }

                });
                notEventCreated = false;
            }
        } else if (dealer.getTurn() != 0) {
            Play();
            Update();
        }
    }

    private void Points() {
        dealer.points();
    }

    private void showScore() {
        score = new Text("Score\nTeam1: " + dealer.getTeam1Games() + "\nTeam2: " + dealer.getTeam2Games());
        points = new Text("Team1: " + dealer.getTeam1Points() + "\nTeam2: " + dealer.getTeam2Points());
        grid.add(score, 0, 0);
        grid.add(trumpImage, 13, 0);
        grid.add(points, 26, 0);
    }

    private void Update() {
        grid.getChildren().clear();

        showScore();
        ShowTable();
        ShowHand();
    }

    public void ShowTable() {
        table = dealer.getTable();

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

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX Welcome");

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
        scene
                .getStylesheets().add(Sueca.class
                        .getResource("Sueca.css").toExternalForm());
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private ImageView GetImage(Card card) {
        //System.out.println("img/card" + card.getSuit() + card.getValue() + ".png");
        return new ImageView(new Image("img/card" + card.getSuit() + card.getValue() + ".png", 0, 100, true, false));
    }

    private void End() {
        dealer.endTurn();

        dealer.shuffle();

        Card trumpCard = dealer.drawTrump();
        trumpImage = GetImage(trumpCard);

        dealer.drawCards();
        player.setHand(dealer.getHand());

        table = dealer.getTable();
    }

}
