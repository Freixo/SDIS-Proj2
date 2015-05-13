/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Misc;

import Card.Card;
import java.util.ArrayList;

/**
 *
 * @author Hugo Freixo
 */
public class Util {

    public static void print(ArrayList<Card> cards) {
        for (int i = 0; i < cards.size(); ++i) {
            System.out.print(i + ": ");
            cards.get(i).print();
        }
    }
}
