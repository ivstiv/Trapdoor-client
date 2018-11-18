package data;

public enum RequestType {
    CONNECT, ACTION, RESPONSE, MSG, POISON_PILL
}

 /*
    CONNECT - Send to connect to a server
    ACTION - Change of channel or other request
    MSG - Transmission of messages
    POISON_PILL - Thread management
    RESPONSE - Will transport codes to maintain state between the server and the client
        100 - Transmission confirmation
        200 - Wrong server password
        201 - Username already taken
        202 - Forbidden username
 */