/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.util.ArrayList;
import java.util.Collections;
import org.json.JSONArray;

/**
 *
 * @author Hugo Freixo
 */
public class Player {

    private ArrayList<Card> hand = new ArrayList<Card>();
    private String name;

    public Player(ArrayList<Card> h, String n) {
        hand = h;
        name = n;
    }

    public Player(String n) {
        hand = new ArrayList<Card>(10);
        name = n;
    }

    public void organize() {
        Collections.sort(hand);
    }

    public void setHand(ArrayList<Card> h) {
        hand = h;
    }

    public void setHand(JSONArray json) {
        hand.clear();
        for (int i = 0; i < json.length(); ++i) {
            hand.add(new Card(json.optJSONObject(i)));
        }
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public String getName() {
        return name;
    }

}