package model;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Message type constants
    public static final String TYPE_LOGIN = "login";
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_LOGOUT = "logout";
    public static final String TYPE_REGISTER = "register";
    public static final String TYPE_ADD_SLOTS = "add_slots";
    public static final String TYPE_GET_SLOTS = "get_slots";
    public static final String TYPE_SLOTS_DATA = "slots_data";
    public static final String TYPE_REMOVE_SLOT = "remove_slot";
    public static final String TYPE_RESERVE_SLOT = "reserve_slot";
    public static final String TYPE_SLOTS_UPDATE  = "slots_update";
    public static final String TYPE_GET_TICKETS = "get_tickets";
    
    // Message fields
    private final String type; // Immutable
    private String status; // Mutable
    private String text; // Mutable 
    private int slotId;
    private java.util.List<model.ParkingSlot> slots;
    
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
     *  type: The message type
     *  text: The message text content
     */
    public Message(String type, String text) {
        this.type = type;
        this.text = text;
        this.status = "";
    }
    
    /**
     * Constructor for creating a message with all fields
     * type: The message type
     * text: The message text content
     * status: The message status
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
    
    public int getSlotId() {
        return slotId;
    }
    
    public java.util.List<model.ParkingSlot> getSlots() {
        return slots;
    }
    
    // Setters for mutable fields
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }
    
    public void setSlots(java.util.List<model.ParkingSlot> slots) {
        this.slots = slots;
    }
    
    @Override
    public String toString() {
        return "Message{type='" + type + "', status='" + status + "', text='" + text + "'}";
    }
    //helper for the slot updates on the server
    public static Message makeSlotsUpdate(java.util.List<model.ParkingSlot> slots) {
        Message m = new Message(TYPE_SLOTS_UPDATE);
        m.setStatus("ok");
        m.setSlots(slots);
        return m;
    }
}
