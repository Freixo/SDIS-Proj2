/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Card.*;
import Client.*;
import static Misc.Util.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Hugo Freixo
 */
public class Game {

    private static Dealer dealer;
    private static ArrayList<Player> players = new ArrayList<Player>(4);
    private static ArrayList<Card> table;
    private static String trump;
    private static String mainSuit;
    private static int startingPlayer = 0;
    private static int winningPlayer = 0;

    private static int team1Points = 0;
    private static int team2Points = 0;

    private static int team1Games = 0;
    private static int team2Games = 0;

    private static Scanner in = new Scanner(System.in);

    public static void Begin() {
        dealer = new Dealer();
        dealer.shuffle();
        trump = dealer.getDeck().get(39).getSuit();
        table = new ArrayList<Card>(4);

        System.out.println("Score: " + team1Games + " - " + team2Games);

        for (int i = 0; i < 4; ++i) {
            players.add(new Player(dealer.draw(10), "Player" + (i + 1)));
            players.get(i).organize();
        }
    }

    public static void Play() {
        System.out.println(players.get(winningPlayer).getName() + " play a card!");
        print(players.get(winningPlayer).getHand());
        System.out.println("Trump: " + trump);

        int input = in.nextInt();

        table.add(players.get(winningPlayer).getHand().get(input));
        players.get(winningPlayer).getHand().remove(input);

        mainSuit = table.get(0).getSuit();

        int index = winningPlayer + 1;

        System.out.println();

        for (int i = 1; i < players.size(); ++i) {

            boolean hasMainSuit = false;
            for (int c = 0; c < players.get(index % 4).getHand().size(); ++c) {
                if (players.get(index % 4).getHand().get(c).getSuit().equals(mainSuit)) {
                    hasMainSuit = true;
                    break;
                }
            }

            if (hasMainSuit) {
                do {
                    System.out.println(players.get(index % 4).getName() + " play a card!");
                    print(players.get(index % 4).getHand());
                    System.out.println();
                    System.out.println("Trump: " + trump);
                    System.out.println("Remember! You have to assist!");
                    System.out.println("Main Suit: " + mainSuit);
                    System.out.println();
                    System.out.println("Cards on the table: ");

                    for (int j = 0; j < table.size(); j++) {
                        System.out.print("      - ");
                        table.get(j).print();
                    }

                    input = in.nextInt();
                    
                } while (!players.get(index % 4).getHand().get(input).getSuit().equals(mainSuit));
            } else {
                System.out.println(players.get(index % 4).getName() + " play a card!");
                print(players.get(index % 4).getHand());
                System.out.println();
                System.out.println("Trump: " + trump);
                System.out.println("Main Suit: " + mainSuit);
                System.out.println();
                System.out.println("Cards on the table: ");

                for (int j = 0; j < table.size(); j++) {
                    System.out.print("     - ");
                    table.get(j).print();
                }

                input = in.nextInt();
            }

            table.add(players.get(index % 4).getHand().get(input));
            players.get(index % 4).getHand().remove(input);

            index++;
            System.out.println();
        }
    }

    public static void Points() {
        Card strong = table.get(0);
        for (int i = 0; i < table.size(); ++i) {
            if (!strong.beats(table.get(i), trump)) {
                strong = table.get(i);
            }
        }

        System.out.println("Cards on the table: ");

        for (int j = 0; j < table.size(); j++) {
            System.out.print("      - ");
            table.get(j).print();
        }

        winningPlayer = table.indexOf(strong);
        System.out.println("Winning Player: " + players.get(winningPlayer).getName());

        int points = 0;
        for (int i = 0; i < table.size(); ++i) {
            points += table.get(i).getPoints();
        }

        if (players.get(winningPlayer).getName().endsWith("1")
                || players.get(winningPlayer).getName().endsWith("3")) {
            team1Points += points;
        } else if (players.get(winningPlayer).getName().endsWith("2")
                || players.get(winningPlayer).getName().endsWith("4")) {
            team2Points += points;
        }

        System.out.println("Team 1: " + team1Points);
        System.out.println("Team 2: " + team2Points);
        System.out.println();

        dealer.getDeck().addAll(table);
        table = new ArrayList<Card>(4);
        mainSuit = "";
    }

    public static void EndTurn() {
        if (team1Points > team2Points) {
            switch (team1Points) {
                case 120:
                    team1Games += 4;
                    break;
                case 90:
                    team1Games += 3;
                    break;
                default:
                    if (team1Points > 90) {
                        team1Games += 2;
                    } else {
                        team1Games += 1;
                    }
                    break;
            }
        } else if (team1Points < team2Points) {
            switch (team2Points) {
                case 120:
                    team2Games += 4;
                    break;
                case 90:
                    team2Games += 3;
                    break;
                default:
                    if (team2Points > 90) {
                        team2Games += 2;
                    } else {
                        team2Games += 1;
                    }
                    break;
            }
        }
        team1Points = 0;
        team2Points = 0;
        dealer.shuffle();
        trump = dealer.getDeck().get(39).getSuit();

        System.out.println("Score: " + team1Games + " - " + team2Games);

        startingPlayer = (startingPlayer + 1) % 4;
        winningPlayer = startingPlayer;

        for (int i = 0; i < players.size(); ++i) {
            players.get((winningPlayer + i) % 4).setHand(dealer.draw(10));
            players.get((winningPlayer + i) % 4).organize();
        }

    }

    private static void End() {
        System.out.println("Score: " + team1Games + " - " + team2Games);
        if (team1Games > team2Games) {
            System.out.println("Team 1 Won");
        } else if (team2Games > team1Games) {
            System.out.println("Team 2 Won");
        } else {
            System.out.println("Draw!");
        }
    }

    public static void main(String[] args) {
        Begin();
        while (team1Games < 4 && team2Games < 4) {
            for (int i = 0; i < 10; ++i) {
                Play();
                Points();
            }
            EndTurn();
        }
        End();
    }

}
