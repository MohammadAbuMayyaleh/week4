package com.atypon.training;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Mohammad Abu Mayyaleh on 8/3/18.
 */
public class Client {

    private Socket clientSocket;

    /**
     * @param port port number for client
     */
    public Client(int port) {
        try {
            //We can access the server using socket by port number and ip address or domain name for the server
            clientSocket = new Socket("localhost", port);
        } catch (IOException e) {
            System.out.println("The server is unavailable or the port is used try again later.");
        }
    }

    /**
     * @param file we use file object to get the name
     *             and the size of the selected file
     *             the file object is any type of files
     *             such as text image video audio and etc ..
     */
    public void uploadFiles(File file) {
        try (DataInputStream in = new DataInputStream(new FileInputStream(file));
             DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {
            System.out.println("Start uploading the file to the server .. please wait ..");
            /*
            DataOutputStream enables you to write primitive data to a stream.
            in this method we need to write the file name and size in the header of the file
            and read these values from receiveFiles method.
             */
            long fileSize = file.length(); // the size of selected file

            //Write file name and size in the top of file to know the name and the size in receiveFiles method
            out.writeUTF(file.getName());
            out.writeLong(fileSize);

            //Start writing to the socket
            int bytesRead; // variable to hold the maximum number of bytes read in each iteration
            byte[] buffer = new byte[4096];
            /*
            we use []buffer to read and write from/to the streams []byte by []byte instead of byte by byte.
            read method reads length bytes from the DataInputStream and allocate them in the buffer array
            starting at buffer[0].
            we use Math.min method to take the fileSize length if the fileSize less then []buffer.
            and we write the []buffer to the socket using write method.
            */
            while (fileSize > 0 && (bytesRead = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                out.write(buffer, 0, bytesRead);
                fileSize -= bytesRead;
            }
            System.out.println("File uploaded successfully ..");
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
        //long startTime = System.nanoTime();
        Scanner input = new Scanner(System.in);
        System.out.print("Enter file path to upload it into the server : ");
        String filePath = input.nextLine();
        File file = new File(filePath);
        if (file.exists()) {
            Client client = new Client(5000);
            client.uploadFiles(file);
        } else {
            System.out.println("File not found ..");
        }
        //long endTime = System.nanoTime();
        //System.out.println(((endTime - startTime) / 1000000000.0) + "msec");
    }
}
