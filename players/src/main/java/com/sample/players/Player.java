package com.sample.players;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;

/**
 * This class act as a base for creating the Player(s) and have methods to
 * support continuous message communication between them
 *
 */
public class Player {
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 5000;
    private static final AtomicInteger messageCount = new AtomicInteger(0);

    public int increment() {
	return messageCount.incrementAndGet();
    }

    public int value() {
	return messageCount.get();
    }

    /**
     * This method is used to initiate the communication between Player(s). It takes
     * input from console and pass it on to receiver(s) for processing.
     */
    public void startInitiator() {
	BufferedReader dis = null;
	DataInputStream disResponse = null;
	DataOutputStream dos = null;
	Socket socket = null;
	try {
	    // Establish a connection
	    socket = new Socket(HOST, PORT);
	    System.out.println("Initiator:Connected to ");

	    // Create I/O streams for communication
	    dis = new BufferedReader(new InputStreamReader(System.in));
	    disResponse = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
	    dos = new DataOutputStream(socket.getOutputStream());

	    // Reads message until 10 messages are received and sent back
	    while (messageCount.get() != 10) {
		String line = dis.readLine();
		System.out.println("Initiator:Sending message:" + line);
		dos.writeUTF(line);
		System.out.println("Initiator:Got back message:" + disResponse.readUTF());
	    }
	} catch (UnknownHostException unknownHostException) {
	    System.out.println("Initiator:UnknownHost Exception:" + unknownHostException);
	    unknownHostException.printStackTrace();
	} catch (IOException ioException) {
	    System.out.println("Initiator:IO Error line terminated abruptly:" + ioException);
	    ioException.printStackTrace();
	} finally {
	    System.out.println("Initiator:Closing resources");
	    closeAllResource(dis, disResponse, dos, socket);
	}
    }

    /**
     * This method is used to receive the message and send back response over
     * communication channel after processing
     */
    public void startReceiver() {
	DataInputStream dis = null;
	DataOutputStream dos = null;
	Socket socket = null;
	try {
	    // Open the Server Socket
	    ServerSocket serverSocket = new ServerSocket(PORT);
	    System.out.println("Receiver:Started.");

	    // Wait for the messages(blocks until a connection is made)
	    socket = serverSocket.accept();
	    System.out.println("Receiver:Initiator accepted");

	    // Create I/O streams for communication
	    dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
	    dos = new DataOutputStream(socket.getOutputStream());

	    // Reads message and process until 10 messages are received and sent back
	    while (messageCount.get() != 10) {
		String line = dis.readUTF();
		System.out.println("Receiver:Got message:" + line);
		String response = line + increment();
		System.out.println("Receiver:Sending back message:" + response);
		dos.writeUTF(response);
	    }
	} catch (IOException ioException) {
	    ioException.printStackTrace();
	} finally {
	    System.out.println("Receiver:Closing resources");
	    closeAllResource(dis, dos, socket);
	}
    }

    /**
     * This method is a utility to close all #Closeable resources used during
     * operation
     * 
     * @param dis
     * @param dos
     * @param socket
     */
    private void closeAllResource(Closeable... closeables) {
	for (Closeable closeable : closeables) {
	    IOUtils.closeQuietly(closeable);
	}
    }
}