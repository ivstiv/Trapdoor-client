package data;

public enum RequestType {
    CONNECT, ACTION, MSG, POISON_PILL
}

 /*
    CONNECT - Send to connect to a server
    ACTION - Change of channel or other request
    MSG - Transmission of messages
    POISON_PILL - Thread management
 */