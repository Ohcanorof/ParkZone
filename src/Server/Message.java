package Server;
import java.io.Serializable;

/**
 * Message class for client-server communication
 * Supports three message types: login, text, logout
 * Immutable type field prevents client modification after creation
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Message type constants
    public static final String TYPE_LOGIN = "login";
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_LOGOUT = "logout";
    
    // Message fields
    private final String type;      // Immutable - cannot be changed after construction
    private String status;          // Mutable - can be changed (e.g., "success", "error")
    private String text;            // Mutable - can be changed (e.g., capitalized by server)
    
    /**
     * Constructor for creating a message with specified type
     * @param type The message type (login, text, or logout)
     */
    public Message(String type) {
        this.type = type;
        this.status = "";
        this.text = "";
    }
    
    /**
     * Constructor for creating a message with type and text
     * @param type The message type
     * @param text The message text content
     */
    public Message(String type, String text) {
        this.type = type;
        this.text = text;
        this.status = "";
    }
    
    /**
     * Constructor for creating a message with all fields
     * @param type The message type
     * @param text The message text content
     * @param status The message status
     */
    public Message(String type, String text, String status) {
        this.type = type;
        this.text = text;
        this.status = status;
    }
    
    // Getters
    public String getType() {
        return type;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getText() {
        return text;
    }
    
    // Setters for mutable fields
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    @Override
    public String toString() {
        return "Message{type='" + type + "', status='" + status + "', text='" + text + "'}";
    }
}