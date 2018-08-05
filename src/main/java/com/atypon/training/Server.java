package com.atypon.training;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

/**
 * Created by Mohammad Abu Mayyaleh on 8/3/18.
 */
public class Server extends Thread {

    private ServerSocket server;
    private Socket clientSocket;

    public Server(int port) {
        try {
            //The clients will connect to this server on this port
            server = new ServerSocket(port);
            System.out.println("Server start working.");
        } catch (IOException e) {
            System.out.println("The server is already working ..");
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("Waiting for client connection .. ");
                clientSocket = server.accept();
                System.out.println("Connection established with " + clientSocket.getInetAddress());
                receiveFiles();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //This method take the file from the socket and write the file on predefined folder on the server
    public void receiveFiles() {
        System.out.println("Start receiving files from client .. please wait ..");
        try (DataInputStream in = new DataInputStream(clientSocket.getInputStream());
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(".//ServerFiles//" + in.readUTF()))) {
            //readUTF method reads in a string that has been encoded using a modified UTF-8 format
            //the string is the file name that we write it on uploadFile method

            long fileSize = in.readLong();//input file size from the socket using readlong method

            //Start writing the file in the predefined folder
            byte[] buffer = new byte[4096];
            int bytesRead;
            while (fileSize > 0 && (bytesRead = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                out.write(buffer, 0, bytesRead);
                fileSize -= bytesRead;
            }
            System.out.println("File received successfully form " + clientSocket.getInetAddress());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server(5000);
        server.start();
    }
}
