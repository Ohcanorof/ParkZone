package model;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

/**
 * Interface for encoding/decoding messages over streams
 * Used by ClientHandler for message serialization
 * 
 * @author Group 7
 */
public interface MessageCodec {
	/**
	 * Read a message from an input stream
	 * @param in The input stream
	 * @return The deserialized message object
	 * @throws IOException if I/O error occurs
	 * @throws ClassNotFoundException if class not found during deserialization
	 */
	Object read(InputStream in) throws IOException, ClassNotFoundException;
	
	/**
	 * Write a message to an output stream
	 * @param out The output stream
	 * @param msg The message object to serialize
	 * @throws IOException if I/O error occurs
	 */
	void write(OutputStream out, Object msg) throws IOException;
}

/**
 * Default implementation using Java serialization
 */
class DefaultMessageCodec implements MessageCodec {
	
	@Override
	public Object read(InputStream in) throws IOException, ClassNotFoundException {
		if (in == null) {
			throw new IllegalArgumentException("InputStream cannot be null");
		}
		ObjectInputStream ois = new ObjectInputStream(in);
		return ois.readObject();
	}
	
	@Override
	public void write(OutputStream out, Object msg) throws IOException {
		if (out == null) {
			throw new IllegalArgumentException("OutputStream cannot be null");
		}
		if (msg == null) {
			throw new IllegalArgumentException("Message cannot be null");
		}
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(msg);
		oos.flush();
	}
}