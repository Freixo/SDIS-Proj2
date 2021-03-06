/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Card;

import java.util.Arrays;

/**
 *
 * @author Hugo Freixo
 */
public class Card implements Comparable<Card> {

    private int value;
    private int points;
    private String suit;
    private static final String[] priority = {"Spades","Hearts","Clubs","Diamonds"};

    public Card() {
        value = 0;
        suit = "";
        points = 0;
    }

    public Card(int v, String s, int p) {
        value = v;
        suit = s;
        points = p;
    }

    public int getValue() {
        return value;
    }

    public String getSuit() {
        return suit;
    }

    public int getPoints() {
        return points;
    }

    public void print() {
        switch (value) {
            case 1:
                System.out.print("Ace");
                break;
            case 8:
                System.out.print("Queen");
                break;
            case 9:
                System.out.print("Jack");
                break;
            case 10:
                System.out.print("King");
                break;
            default:
                System.out.print(value);
                break;
        }
        System.out.println(" of " + suit);

    }

    @Override
    public int compareTo(Card t) {
        if (t.getSuit().compareTo(this.suit) == 0) {
            if (t.getPoints() == points) {
                return this.value - t.getValue();
            } else {
                return this.points - t.getPoints();
            }
        }
        return Arrays.asList(priority).indexOf(t.getSuit()) - Arrays.asList(priority).indexOf(this.suit);
    }

    public boolean beats(Card c, String trump) {
        if (suit.equals(c.getSuit())) {
            if (points == c.getPoints()) {
                return value > c.getValue();
            }
            return points > c.getPoints();
        } else if (suit.equals(trump)) {
            return true;
        } else if (c.getSuit().equals(trump)) {
            return false;
        } else {
            return true;
        }
    }
}
