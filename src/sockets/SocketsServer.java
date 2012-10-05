/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author s160820
 */
public class SocketsServer
{

    private static BufferedReader br;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException
    {
        ServerSocket ss = new ServerSocket(5001);
        br = new BufferedReader(new InputStreamReader(System.in));
        while (true)
        {
            Socket s = ss.accept();
            System.out.println("Connected...");
            new SocketThread(s).start();
            System.out.println("Started...");
        }
    }

    private static class SocketThread extends Thread
    {

        private final Socket s;

        public SocketThread(Socket s)
        {
            this.s = s;
        }

        @Override
        public void run()
        {
            try
            {
                String command = null;
                BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                while (!"exit".equals(command))
                {
                    command = reader.readLine().trim().toLowerCase();
                    System.out.println("Received '" + command + "' from client");

                    bw.write(command + "\n");
                    bw.flush();
                }
                reader.close();
                bw.close();
                System.out.println("Closed...");
            }
            catch (IOException ex)
            {
                Logger.getLogger(SocketsServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
