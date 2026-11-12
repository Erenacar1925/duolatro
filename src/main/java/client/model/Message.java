package client.model;

public class Message {
  public String type;
  public Object content;

  // Default constructor for Gson
  public Message() {}

  // Constructor for type and content
  public Message(String type, Object content) {
    this.type = type;
    this.content = content;
  }

  public Object getPayload() {
    return content;
  }
  public String getType() {
    return type;
  }
}
