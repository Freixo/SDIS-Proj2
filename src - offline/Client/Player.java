/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Card.Card;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Hugo Freixo
 */
public class Player {

    private ArrayList<Card> hand = new ArrayList<Card>();
    private String name;
    private String type;

    public Player(ArrayList<Card> h, String n, String t) {
        hand = h;
        name = n;
        type = t;
    }

    public Player(String n, String t) {
        hand = new ArrayList<Card>(10);
        name = n;
        type = t;
    }

    public void organize() {
        Collections.sort(hand);
    }

    public void setHand(ArrayList<Card> h) {
        hand = h;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }

}
