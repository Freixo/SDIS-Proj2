/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Card;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Hugo Freixo
 */
public class Dealer {

    private ArrayList<Card> deck = new ArrayList<Card>(40);

    public Dealer() {
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

    public ArrayList<Card> draw(int num) {
        ArrayList<Card> cards = new ArrayList<Card>(num);
        for (int i = 0; i < num; ++i) {
            cards.add(deck.get(0));
            deck.remove(0);
        }

        return cards;
    }

}
