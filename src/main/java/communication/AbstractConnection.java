package communication;

import com.google.gson.JsonObject;
import communication.security.AES;
import communication.security.AES_OLD;
import communication.security.RSA;
import controllers.MainController;
import core.ServiceLocator;
import data.DataLoader;
import data.Request;
import data.RequestType;
import exceptions.MalformedRequestException;
import misc.RichText;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.LinkedBlockingDeque;

public abstract class AbstractConnection {

    // User data
    private final String IP, USERNAME, PASSWORD;
    private final int PORT;
    protected DataLoader data = ServiceLocator.getService(DataLoader.class);

    // Communication objects
    protected volatile boolean isConnected = false;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // Encryption objects
    //Using RSA to tunnel the AES secret key securely
    private AES aes = new AES();

    // Communication data structures
    BlockingQueue<Request> outgoingRequests = new LinkedBlockingDeque<>();
    BlockingQueue<Request> incomingRequests = new LinkedBlockingDeque<>();
    // can be used for transmission control and tracking of sent and received requests
    // if the connection is dropped this acts as a backup storage for recently sent requests
    ConcurrentSkipListMap<Long, Request> sentRequests =
            new ConcurrentSkipListMap<>(Comparator.comparingLong(v -> v.longValue())); // sorting the sent requests by time

    AbstractConnection(String ip, int port, String username, String password) {
        this.IP = ip;
        this.PORT = port;
        this.USERNAME = username;
        this.PASSWORD = password;
    }

    public void connect() {
        new Thread(() -> {
            try {
                socket = new Socket(this.IP, this.PORT);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                isConnected = true;

                // send the AES key in a RSA tunnel with signature
                RSA tunnel = getRSAHandshake();
                sendAESKey(tunnel);

            } catch (IOException e) {
                System.err.println("Exception in connect(): " + e.getMessage());
                if(ServiceLocator.hasSerivce(MainController.class)) {
                    String msg = String.format("%s (%s): %s",
                            data.getMessage("trying"), getIP(), data.getMessage("failed-connection"));
                    RichText status = new RichText(msg);
                    ServiceLocator.getService(MainController.class).setStatusBar(status);
                }
                close();
                return;
                //wrapper.showWarning("The remote host refused the connection! Check your internet connection or the website for any details.");
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            inputStreamListener();
            handleIncomingRequests();
            handleOutgoingRequests();
            transmissionControl();
        }).start();
    }

    private RSA getRSAHandshake() throws IOException, NoSuchAlgorithmException {
        RSA rsa = new RSA();
        // send the public key to the server
        out.println(rsa.getPublicKeyBase64());
        out.flush();

        // wait for the public key of the server
        String serverKey = in.readLine();
        if (serverKey == null) { // TO-DO: do something about this ! reads null on disconnect
            System.out.println("[Handler]Host disconnected!");
        }

        // register the server public key in the RSA object
        rsa.setRemotePublicKey(serverKey);
        System.out.println("RSA tunnel initialised!");
        return rsa;
    }

    // transmits a random string which is used for the generation of the AES key on both sides
    private void sendAESKey(RSA tunnel) throws Exception {
        // encrypt the AES key
        String key = tunnel.encrypt(aes.getRandomString());
        // generate the signature of the message
        String signature = tunnel.sign(aes.getRandomString());
        out.println(key);
        out.flush();
        out.println(signature);
        out.flush();
    }

    private void inputStreamListener() {
        new Thread(() -> {
            System.out.println("Incoming requests listener running . . .");
            while(isConnected) {
                Request r = readRequest();
                incomingRequests.add(r);
            }
            System.out.println("Incoming requests listener stopped . . .");
        }).start();
    }

    private Request readRequest() throws MalformedRequestException {
        String encrypted = null;
        try {
            encrypted = in.readLine();
        } catch (IOException e) { // if the server drops the connection unexpectedly
            close();
        }
        if (encrypted == null) { // the reader reads null because the end of the stream is reached
            return new Request(RequestType.POISON_PILL, new JsonObject()); // dummy request
        }
        String decrypted = aes.decrypt(encrypted);
        System.out.println("INCOMING:"+decrypted);
        return new Request(decrypted);
    }

    // this is not used anymore but i will leave it for now
    protected String receive() throws IOException{
        String encrypted = in.readLine();   // halts until something appears in the socket stream
        if(encrypted == null) return null;  // will be "caught" on upper stage
        return aes.decrypt(encrypted);
    }

    protected void send(String msg) {
        System.out.println("OUTGOING:"+msg);
        out.println(aes.encrypt(msg));
        out.flush();
    }

    protected ConcurrentNavigableMap<Long, Request> getRequestsOlderThan(int seconds) {
        return sentRequests.headMap(System.currentTimeMillis() - seconds*1000);
    }

    public void sendRequest(Request r) {
        if(r.isValid())
            outgoingRequests.add(r);
        else
            System.out.println("[WARNING]Trying to send an invalid request!");
    }

    public void close(){
        isConnected = false;
        sendRequest(new Request(RequestType.POISON_PILL, new JsonObject()));
        incomingRequests.add(new Request(RequestType.POISON_PILL, new JsonObject()));
        sentRequests.put(-1L, new Request(RequestType.POISON_PILL, new JsonObject()));
        ServiceLocator.removeService(ServerConnection.class);
        try {
            if(socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // getters
    public String getIP()       {return this.IP;}
    public String getUSERNAME() {return this.USERNAME;}
    public String getPASSWORD() {return this.PASSWORD;}
    public int    getPORT()     {return  this.PORT;}

    protected abstract void transmissionControl();
    protected abstract void handleOutgoingRequests();
    protected abstract void handleIncomingRequests();
}
