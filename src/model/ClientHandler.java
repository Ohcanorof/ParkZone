package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

//the ClientHandler
//will handle connection with a single connected client, runs on its own thread, reads incomming 
//messages, and can send messages back to the client or ask the server to broadcast
//may change later, this and the ParkingSystemServer are barebones rn
public class ClientHandler implements Runnable {
	//attributes
	private final ParkingSystemServer server;//server= parkingSystem on the uml
	private final Socket socket;
	private final String clientId;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private final AtomicBoolean open = new AtomicBoolean(true);
	private boolean loggedIn = false;
	
	private final ParkingSystem parkingSystem = ParkingSystem.getInstance();
	
	//constructor
	public ClientHandler(ParkingSystemServer server, Socket socket, String clientId) {
        this.server = server;
        this.socket = socket;
        this.clientId = clientId;
    }
	
	//getters
	public String getClientId() {
		return clientId;
	}
	
	//implementing runnable
	@Override
    public void run() {
        System.out.println("[Client " + clientId + "] Connected from: " + socket.getInetAddress());

        try {
            // order matters: out first, then in
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            readLoop();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Client " + clientId + "] Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void readLoop() throws IOException, ClassNotFoundException {
        boolean running = true;

        while (running && open.get()) {
            try {
                Object obj = in.readObject();
                if (!(obj instanceof Message)) {
                    System.err.println("[Client " + clientId + "] Unknown object: " + obj);
                    continue;
                }

                Message message = (Message) obj;
                System.out.println("[Client " + clientId + "] Received: " + message);

                if (!loggedIn) {
                    // must login first
                    if (Message.TYPE_LOGIN.equals(message.getType())) {
                        handleLogin(message);
                    } else {
                        Message errorMsg = new Message(message.getType());
                        errorMsg.setStatus("error");
                        errorMsg.setText("Must login first");
                        send(errorMsg);
                    }
                } else {
                    switch (message.getType()) {
                        case Message.TYPE_TEXT -> handleText(message);
                        case Message.TYPE_LOGOUT -> {
                            handleLogout(message);
                            running = false;
                        }
                        default -> System.out.println(
                                "[Client " + clientId + "] Unknown message type: " + message.getType()
                        );
                    }
                }

            } catch (EOFException e) {
                System.out.println("[Client " + clientId + "] Disconnected unexpectedly");
                break;
            }
        }
    }

    private void handleLogin(Message message) throws IOException {
        System.out.println("[Client " + clientId + "] Processing login...");

        // TODO: integrate with ParkingSystem.login(email, password) later.
        // For now, just accept everyone.
        loggedIn = true;

        Message response = new Message(Message.TYPE_LOGIN);
        response.setStatus("success");
        response.setText("Login successful");
        send(response);

        System.out.println("[Client " + clientId + "] Login successful");
    }

    private void handleText(Message message) throws IOException {
        System.out.println("[Client " + clientId + "] Processing text: " + message.getText());

        String capitalizedText = message.getText() == null
                ? ""
                : message.getText().toUpperCase();

        Message response = new Message(Message.TYPE_TEXT);
        response.setStatus("success");
        response.setText(capitalizedText);

        send(response);
        System.out.println("[Client " + clientId + "] Sent capitalized text: " + capitalizedText);
    }

    private void handleLogout(Message message) throws IOException {
        System.out.println("[Client " + clientId + "] Processing logout...");

        loggedIn = false;

        Message response = new Message(Message.TYPE_LOGOUT);
        response.setStatus("success");
        response.setText("Logout successful");
        send(response);

        System.out.println("[Client " + clientId + "] Logout successful");
    }

    /** Send a message to this client */
    public synchronized void send(Message message) throws IOException {
        if (out == null) return;
        out.writeObject(message);
        out.flush();
        out.reset();
    }

    /** Used by server to push events (broadcast, sendTo) */
    public void onServerPush(Message message) {
        try {
            send(message);
        } catch (IOException e) {
            System.err.println("[Client " + clientId + "] Error during push: " + e.getMessage());
            close();
        }
    }

    public void close() {
        open.set(false);
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    private void cleanup() {
        server.removeClient(clientId);
        try {
            if (in != null) in.close();
        } catch (IOException ignored) {
        }
        try {
            if (out != null) out.close();
        } catch (IOException ignored) {
        }
        try {
            if (!socket.isClosed()) socket.close();
        } catch (IOException ignored) {
        }

        System.out.println("[Client " + clientId + "] Connection closed");
    }

	
}