package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * EventStreamClient
 * - wraps Socket + Object streams
 * - provides login/text/logout methods used by ClientGUI
 */
public class EventStreamClient {

    private final String host;
    private final int port;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public EventStreamClient(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        connect();
    }

    private void connect() throws IOException {
        System.out.println("Connecting to server at " + host + ":" + port + "...");
        socket = new Socket(host, port);
        System.out.println("Connected to server!");

        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    private synchronized Message sendAndReceive(Message message)
            throws IOException, ClassNotFoundException {

        System.out.println("\n[Client->Server] " + message);
        out.writeObject(message);
        out.flush();
        out.reset();

        Message response = (Message) in.readObject();
        System.out.println("[Server->Client] " + response);
        return response;
    }

    public boolean login(String email, String password)
            throws IOException, ClassNotFoundException {

        System.out.println("\n=== Performing login ===");
        Message loginMsg = new Message(Message.TYPE_LOGIN);

        // simple: send email as text for now (server ignores it)
        loginMsg.setText(email);

        Message response = sendAndReceive(loginMsg);
        return "success".equals(response.getStatus());
    }

    public String sendText(String text) throws IOException, ClassNotFoundException {
        Message textMsg = new Message(Message.TYPE_TEXT, text);
        Message response = sendAndReceive(textMsg);

        if ("success".equals(response.getStatus())) {
            return response.getText();
        } else {
            throw new IOException("Text failed: " + response.getText());
        }
    }

    public boolean logout() throws IOException, ClassNotFoundException {
        System.out.println("\n=== Logging out ===");
        Message logoutMsg = new Message(Message.TYPE_LOGOUT);
        Message response = sendAndReceive(logoutMsg);
        return "success".equals(response.getStatus());
    }

    public void close() {
        try {
            if (in != null) in.close();
        } catch (IOException ignored) {
        }
        try {
            if (out != null) out.close();
        } catch (IOException ignored) {
        }
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {
        }
        System.out.println("Connection closed.");
    }
}