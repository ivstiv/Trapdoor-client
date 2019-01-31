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
                                String msg = String.format("%s (%s): %s",
                                        data.getMessage("response"), getIP(), data.getMessage("wrong-password"));
                                RichText status = new RichText(msg);
                                controller.setStatusBar(status);
                                close(false);
                                break;

                            }else if(code == 201) {
                                String msg = String.format("%s (%s): %s",
                                        data.getMessage("response"), getIP(), data.getMessage("username-in-use"));
                                RichText status = new RichText(msg);
                                controller.setStatusBar(status);
                                close(false);
                                break;

                            }else if(code == 202) {
                                String msg = String.format("%s (%s): %s",
                                        data.getMessage("response"), getIP(), data.getMessage("forbidden-username"));
                                RichText status = new RichText(msg);
                                controller.setStatusBar(status);
                                close(false);
                                break;

                            }else if(code == 203) {
                                String msg = String.format("%s (%s): %s",
                                        data.getMessage("response"), getIP(), data.getMessage("full-server"));
                                RichText status = new RichText(msg);
                                controller.setStatusBar(status);
                                close(false);
                                break;
                            }
                        case MSG:
                            String username = r.getContent().get("sender").getAsString();
                            String message = r.getContent().get("message").getAsString();
                            controller.addPublicMsg(username, message);
                            break;

                        case PRIVATE_MSG:
                            String sender = r.getContent().get("sender").getAsString();
                            String receiver = r.getContent().get("receiver").getAsString();
                            String privateMessage = r.getContent().get("message").getAsString();
                            controller.addPrivateMsg(sender, receiver, privateMessage);
                            break;

                        case ACTION:
                            String action = r.getContent().get("action").getAsString();
                            if(action.equals("print")) {
                                controller.print(r.getContent().get("message").getAsString());
                                break;
                            }else if(action.equals("update_statusbar")) {
                                String channel = r.getContent().get("channel").getAsString();
                                controller.setStatusBar(new RichText("~1~g"+getUSERNAME()+"~l@~d"+getIP()+"~l:~c~/"+channel+" ~l$"));
                                break;
                            }else if(action.equals("reset_statusbar")) {
                                controller.setStatusBar(new RichText("~1~gUsername~l@~d192.168.0.1~l:~c~/example ~l$"));
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
                if(reqs.containsKey(-1L)) break; // -1 comes from close()

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
