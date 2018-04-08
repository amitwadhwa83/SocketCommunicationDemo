package com.sample.players;

/**
 * This class is entry point for launching/starting the process of message
 * communication between Player(s)
 *
 */
public class LaunchPlayer {
    public static void main(String args[]) {

	// Starting initiator in separate thread for running communication between
	// players in the same java process
	new Thread() {
	    public void run() {
		new Player().startInitiator();
	    }
	}.start();

	// Starting receiver
	new Player().startReceiver();
	System.out.println("Ending the communication");
    }
}