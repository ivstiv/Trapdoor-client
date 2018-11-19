package communication;

import controllers.MainController;
import core.ServiceLocator;
import data.Request;
import data.RequestType;
import misc.RichText;

import java.util.concurrent.ConcurrentNavigableMap;

public class ServerConnection extends AbstractConnection {

    public ServerConnection(String ip, int port, String username, String password) {
        super(ip, port, username, password);
    }

    @Override
    protected void handleIncomingRequests() {
        new Thread(() -> {
            System.out.println("Incoming requests handler running . . .");
            MainController controller = ServiceLocator.getService(MainController.class);
            Request r = null;

            while(isConnected) {
                try {
                    r = incomingRequests.take();
                    if(r.getType() == RequestType.POISON_PILL) break;

                    switch(r.getType()) {

                        case RESPONSE:
                            int code = r.getContent().get("code").getAsInt();
                            if(code == 100) {
                                long timeStamp = r.getContent().get("timestamp").getAsLong();
                                sentRequests.remove(timeStamp);
                                break;
                            }else if(code == 200) {
                                RichText status = new RichText("&1&bTrying to connect ("+getIP()+"): &fWrong server password!");
                                controller.setStatusBar(status);
                                close();
                            }else if(code == 201) {
                                RichText status = new RichText("&1&bTrying to connect ("+getIP()+"): &fUsername is already in use!");
                                controller.setStatusBar(status);
                                close();
                            }else if(code == 202) {
                                RichText status = new RichText("&1&bTrying to connect ("+getIP()+"): &fForbidden username!");
                                controller.setStatusBar(status);
                                close();
                            }
                            break;
                        case MSG:
                            String username = r.getContent().get("sender").getAsString();
                            String message = r.getContent().get("message").getAsString();
                            controller.addMsg(username, message);
                            break;
                        case ACTION:
                            String action = r.getContent().get("action").getAsString();
                            if(action.equals("print")) {
                                controller.print(r.getContent().get("message").getAsString());
                                break;
                            }else if(action.equals("update_statusbar")) {
                                String channel = r.getContent().get("channel").getAsString();
                                controller.setStatusBar(new RichText("&1&g"+getUSERNAME()+"&l@&d"+getIP()+"&l:&c~/"+channel+" &l$"));
                                break;
                            }
                            break;
                        default:

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Incoming requests handler stopped . . .");
        }).start();
    }

    @Override
    protected void transmissionControl() {
        new Thread(() -> {
            System.out.println("Transmission control handler running . . .");
            while(isConnected) {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(sentRequests.size() > 0)
                    System.out.println("\nNot received requests: "+sentRequests.size());
                ConcurrentNavigableMap<Long, Request> reqs = getRequestsOlderThan(8);
                // sometimes the isConnected gets cached so this will act as a poison pill
                if(reqs.containsKey(-1L)) return; // -1 comes from close()

                if(!reqs.isEmpty()) {
                    System.out.println("do stuff there are requests that are not received by the server ");
                    // TODO: 29-Oct-18 do something with these requests (send them back? or notify the user)
                }
            }
            System.out.println("Transmission control handler stopped . . .");
        }).start();
    }

    @Override
    protected void handleOutgoingRequests() {
        new Thread(()-> {
            System.out.println("Outgoing requests handler running . . .");
            Request r;
            while(isConnected) {
                try {
                    r = outgoingRequests.take();
                    if(r.getType() == RequestType.POISON_PILL) break;

                    // move the request to the map until you receive a confirmation from the server
                    sentRequests.put(r.getTimestamp(), r);
                    send(r.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Outgoing requests handler stopped . . .");
        }).start();
    }
}
