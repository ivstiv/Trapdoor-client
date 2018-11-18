package communication;

import controllers.MainController;
import core.ServiceLocator;
import data.Request;
import data.RequestType;
import misc.RichText;

import java.io.IOException;
import java.util.concurrent.ConcurrentNavigableMap;

public class ServerConnection extends AbstractConnection {

    public ServerConnection(String ip, int port, String username, String password) {
        super(ip, port, username, password);
    }

    @Override
    protected void handleIncomingRequests() {
        new Thread(() -> {

            MainController controller = ServiceLocator.getService(MainController.class);
            String line;
            while(isConnected) {
                try {
                    line = receive(); // halts until it receives something
                } catch (IOException e) {
                    System.out.println("[Error]Incoming stream was closed.");
                    break;
                }

                System.out.println("DATA:"+line);

                Request r;
                try {
                    r = new Request(line);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue; // skip the iteration if the request is invalid
                }

                System.out.println("Request received:\n"+line+"\n");
                switch(r.getType()) {

                    case RESPONSE:
                        int code = r.getContent().get("code").getAsInt();
                        if(code == 100) {
                            long timeStamp = r.getContent().get("timestamp").getAsLong();
                            sentRequests.remove(timeStamp);
                            break;
                        }else if(code == 200) {
                            RichText status = new RichText("&1&fWrong server password!");
                            status.setCustomSize(20);
                            status.setCustomFont("Consolas");
                          controller.setStatusBar(status);
                        }
                        break;
                    case MSG:
                        break;
                    case ACTION:
                        break;
                    default:

                }
            }
        }).start();
    }

    @Override
    protected void transmissionControl() {
        new Thread(() -> {
            while(isConnected) {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("\nNot received requests: "+sentRequests.size());
                ConcurrentNavigableMap<Long, Request> reqs = getRequestsOlderThan(8);
                if(!reqs.isEmpty()) {
                    // do stuff there are requests that are not received by the server
                    System.out.println("do stuff there are requests that are not received by the server ");
                    // TODO: 29-Oct-18 do something with these requests (send them back? or notify the user)
                }
            }
        }).start();
    }

    @Override
    protected void handleOutgoingRequests() {
        new Thread(()-> {
            Request r;
            while(isConnected) {
                try {
                    r = outgoingRequests.take();
                    if(r.getType() == RequestType.POISON_PILL) return;

                    // move the request to the map until you receive a confirmation from the server
                    sentRequests.put(r.getTimestamp(), r);
                    send(r.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
