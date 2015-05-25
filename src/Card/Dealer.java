/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Card;

import Client.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author Hugo Freixo
 */
public class Dealer {

    private ArrayList<Card> deck = new ArrayList<>(40);
    private ArrayList<Player> players = new ArrayList<>(4);
    private ArrayList<Card> table = new ArrayList<>(4);

    private int startingPlayer = 0;
    private int turn = 0;
    private String mainSuit = "";
    private String trump = "";

    private static int team1Points = 0;
    private static int team2Points = 0;

    private static int team1Games = 0;
    private static int team2Games = 0;

    private Random random = new Random();

    public Dealer() {
        createDeck();
        for (int i = 0; i < 4; ++i) {
            players.add(new Player("Player" + (i + 1), "CPU"));
        }

        for (int i = 0; i < 4; ++i) {
            table.add(new Card());
        }
    }

    public Dealer(Player p) {
        createDeck();
        players.add(p);
        for (int i = 1; i < 4; ++i) {
            players.add(new Player("Player" + (i + 1), "CPU"));
        }

        for (int i = 0; i < 4; ++i) {
            table.add(new Card());
        }
    }

    public void drawCards() {
        for (int i = 0; i < players.size(); ++i) {
            players.get((turn + i) % 4).setHand(draw(10));
            players.get((turn + i) % 4).organize();
        }
    }

    public Card drawTrump() {
        trump = deck.get(39).getSuit();
        return deck.get(39);
    }

    public void play() {
        if (mainSuit.equals("")) {
            table.set(turn, players.get(turn).getHand().remove(random.nextInt(players.get(turn).getHand().size())));
            mainSuit = table.get(turn).getSuit();
        } else {
            if (hasMainSuit()) {
                Card c;
                do {
                    c = players.get(turn).getHand().get(random.nextInt(players.get(turn).getHand().size()));
                } while (!c.getSuit().equals(mainSuit));
                table.set(turn, c);
                players.get(turn).getHand().remove(c);
            } else {
                table.set(turn, players.get(turn).getHand().remove(random.nextInt(players.get(turn).getHand().size())));
            }
        }
        turn = (turn + 1) % 4;
    }

    public boolean play(int index) {
        if (mainSuit.equals("")) {
            mainSuit = players.get(turn).getHand().get(index).getSuit();
            table.set(turn, players.get(turn).getHand().remove(index));
            turn = (turn + 1) % 4;
            return true;
        } else {
            if (hasMainSuit()) {
                if (players.get(turn).getHand().get(index).getSuit().equals(mainSuit)) {
                    table.set(turn, players.get(turn).getHand().remove(index));
                    turn = (turn + 1) % 4;
                    return true;
                } else {
                    return false;
                }
            } else {
                table.set(turn, players.get(turn).getHand().remove(index));
                turn = (turn + 1) % 4;
                return true;
            }
        }
    }

    public void points() {
        Card strong = table.get(turn);
        for (int i = 0; i < table.size(); ++i) {
            if (!strong.beats(table.get(i), trump)) {
                strong = table.get(i);
            }
        }

        strong.print();

        turn = table.indexOf(strong);

        int points = 0;
        for (int i = 0; i < table.size(); ++i) {
            points += table.get(i).getPoints();
        }

        if (players.get(turn).getName().endsWith("1")
                || players.get(turn).getName().endsWith("3")) {
            team1Points += points;
        } else if (players.get(turn).getName().endsWith("2")
                || players.get(turn).getName().endsWith("4")) {
            team2Points += points;
        }

        deck.addAll(table);

        table.clear();
        for (int i = 0; i < 4; ++i) {
            table.add(new Card());
        }
        mainSuit = "";
    }

    public void endTurn() {
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

        startingPlayer = (startingPlayer + 1) % 4;
        turn = startingPlayer;
    }

    private void createDeck() {
        for (int v = 1; v <= 10; ++v) {
            switch (v) {
                case 1:
                    deck.add(new Card(v, "Spades", 11));
                    deck.add(new Card(v, "Hearts", 11));
                    deck.add(new Card(v, "Clubs", 11));
                    deck.add(new Card(v, "Diamonds", 11));
                    break;
                case 7:
                    deck.add(new Card(v, "Spades", 10));
                    deck.add(new Card(v, "Hearts", 10));
                    deck.add(new Card(v, "Clubs", 10));
                    deck.add(new Card(v, "Diamonds", 10));
                    break;
                case 8:
                    deck.add(new Card(v, "Spades", 2));
                    deck.add(new Card(v, "Hearts", 2));
                    deck.add(new Card(v, "Clubs", 2));
                    deck.add(new Card(v, "Diamonds", 2));
                    break;
                case 9:
                    deck.add(new Card(v, "Spades", 3));
                    deck.add(new Card(v, "Hearts", 3));
                    deck.add(new Card(v, "Clubs", 3));
                    deck.add(new Card(v, "Diamonds", 3));
                    break;
                case 10:
                    deck.add(new Card(v, "Spades", 4));
                    deck.add(new Card(v, "Hearts", 4));
                    deck.add(new Card(v, "Clubs", 4));
                    deck.add(new Card(v, "Diamonds", 4));
                    break;
                default:
                    deck.add(new Card(v, "Spades", 0));
                    deck.add(new Card(v, "Hearts", 0));
                    deck.add(new Card(v, "Clubs", 0));
                    deck.add(new Card(v, "Diamonds", 0));
                    break;
            }

        }
    }

    public void shuffle() {
        Collections.shuffle(deck);
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public ArrayList<Card> getTable() {
        return table;
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public ArrayList<Card> draw(int num) {
        ArrayList<Card> cards = new ArrayList<>(num);
        for (int i = 0; i < num; ++i) {
            cards.add(deck.get(0));
            deck.remove(0);
        }

        return cards;
    }

    private boolean hasMainSuit() {
        boolean hasMainSuit = false;
        for (int c = 0; c < players.get(turn).getHand().size(); ++c) {
            if (players.get(turn).getHand().get(c).getSuit().equals(mainSuit)) {
                hasMainSuit = true;
                break;
            }
        }
        return hasMainSuit;
    }

    public ArrayList<Card> getHand() {
        if (players.get(0).getType().equals("Human")) {
            return players.get(0).getHand();
        } else {
            return new ArrayList<>();
        }
    }

    public boolean fullTable() {
        for (int i = 0; i < table.size(); ++i) {
            if (table.get(i).getValue() == 0) {
                return false;
            }
        }
        return true;
    }

    public int getTurn() {
        return turn;
    }

    public int getTeam1Games() {
        return team1Games;
    }

    public int getTeam2Games() {
        return team2Games;
    }

    public int getTeam1Points() {
        return team1Points;
    }

    public int getTeam2Points() {
        return team2Points;
    }

    public boolean end() {
        for (int i = 0; i < players.size(); ++i) {
            if (players.get(i).getHand().size() != 0) {
                return false;
            }
        }
        return true;
    }
}
