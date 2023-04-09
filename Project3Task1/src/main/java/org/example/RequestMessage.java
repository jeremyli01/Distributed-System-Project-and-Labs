/**
 * RequestMessage class for Project3Task1
 * @author Jeremy(Zihan) Li
 * @andrewEmail zihanli2@andrew.cmu.edu
 * */
package org.example;

public class RequestMessage {
    // choice for the request
    private int choice;
    // client request of number, can be difficulty or index in the blockList
    private int numberRequest;
    // client request of String, can be data input
    private String stringRequest;

    public int getChoice() {
        return choice;
    }

    public void setChoice(int choice) {
        this.choice = choice;
    }

    public int getNumberRequest() {
        return numberRequest;
    }

    public void setNumberRequest(int numberRequest) {
        this.numberRequest = numberRequest;
    }

    public String getStringRequest() {
        return stringRequest;
    }

    public void setStringRequest(String stringRequest) {
        this.stringRequest = stringRequest;
    }
}
