package javaapplication2;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    public static StringBuffer history = new StringBuffer();

    public static void main(String[] args) {
        ArrayList<ChatHandler> AllHandlers = new ArrayList<ChatHandler>();

        try {
            ServerSocket s = new ServerSocket(2865);
            history.append("\n");
            for (;;) {
                Socket incoming = s.accept();
                new ChatHandler(incoming, AllHandlers).start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}

class ChatHandler extends Thread {

    public ChatHandler(Socket i, ArrayList<ChatHandler> h) {
        incoming = i;
        handlers = h;
        handlers.add(this);
        try {
            in = new ObjectInputStream(incoming.getInputStream());
            out = new ObjectOutputStream(incoming.getOutputStream());
        } catch (Exception ioe) {
            System.out.println("Could not create streams.");
        }
    }

    public synchronized void broadcast() {

        ChatHandler left = null;

        if (myObject.getFlag().equalsIgnoreCase("userlist")) {

            StringBuffer sb = new StringBuffer();
            sb.append(myObject.getName());
            sb.append(" requested user list:\n");
            for (int i = 0; i < handlers.size(); ++i) {
                ChatHandler ct = handlers.get(i);
                sb.append((i + 1) + ") " + ct.userName + "\n");
            }

            WhiteBoard cm = new WhiteBoard();
            cm.setMessage(sb.toString());
            cm.setName("\n");
            cm.setFlag("\n");
            try {
                out.writeObject(cm);

            } catch (IOException ioe) {

            }
            return;
        }

        for (ChatHandler handler : handlers) {
            //System.out.print(history);
            WhiteBoard cm = new WhiteBoard();
            cm.setFlag(myObject.getFlag());
            cm.setMessage(myObject.getMessage());
            cm.x1 = myObject.getx1();
            cm.y1 = myObject.gety1();
            cm.x2 = myObject.getx2();
            cm.y2 = myObject.gety2();
            if (cm.getFlag().equalsIgnoreCase("connect")) {

                cm.setName(myObject.getName() + " ");

                userName = myObject.getName();
            } else if (cm.getFlag().equalsIgnoreCase("disconnect")) {

                cm.setName(myObject.getName() + " ");
                userName = myObject.getName();

            } else {
                cm.setName(myObject.getName() + ":");
            }

            try {
                handler.out.writeObject(cm);
            } catch (IOException ioe) {
                left = handler;
            }
        }
        handlers.remove(left);

        if (myObject.getFlag().equals("disconnect")) {
            done = true;
            handlers.remove(this);
        }
    }

    public void run() {
        try {
            while (!done) {
                myObject = (WhiteBoard) in.readObject();

                if (myObject.getFlag().equalsIgnoreCase("whiteboard")) {
                    broadcast();
                } else if (myObject.getFlag().equalsIgnoreCase("history")) {
                    WhiteBoard cm1 = new WhiteBoard();
                    String s = "";
                    cm1.setMessage(ChatServer.history.toString());
                    cm1.setName(s);
                    cm1.setFlag("\n");
                    try {
                        out.writeObject(cm1);

                    } catch (IOException ioe) {

                    }
                } else {
                    if (myObject.getFlag().equalsIgnoreCase("userlist")) {
                        ChatServer.history.append(myObject.getName());
                        ChatServer.history.append(" requested user list:\n");
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < handlers.size(); ++i) {
                            ChatHandler ct = handlers.get(i);
                            sb.append((i + 1) + ") " + ct.userName + "\n");
                            ChatServer.history.append((i + 1) + ") " + ct.userName + "\n");
                        }
                    } else if (myObject.getFlag().equalsIgnoreCase("connect")) {

                        ChatServer.history.append(myObject.getName());
                        ChatServer.history.append(" has entered the chat room...\n");

                    } else if (myObject.getFlag().equalsIgnoreCase("disconnect")) {

                        ChatServer.history.append(myObject.getName());
                        ChatServer.history.append(" has left the chat room...\n");
                    } else {
                        ChatServer.history.append(myObject.getName() + ":" + myObject.getMessage() + "\n");
                    }
                    System.out.print(ChatServer.history);
                    broadcast();
                }
            }
        } catch (IOException e) {
            if (e.getMessage().equals("Connection reset")) {
                System.out.println("A client terminated its connection.");
            } else {
                System.out.println("Problem receiving: " + e.getMessage());
            }
        } catch (ClassNotFoundException cnfe) {
            System.out.println(cnfe.getMessage());
        } finally {
            handlers.remove(this);
        }
    }

    WhiteBoard myObject = null;
    private Socket incoming;
    String userName;
    boolean done = false;
    ArrayList<ChatHandler> handlers;
    ObjectOutputStream out;
    ObjectInputStream in;
}
