/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets;


/**
 *
 * @author s160820
 */
public class SocketsServer
{

    public static final int DEFAULT_PORT = 5001;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        int port = DEFAULT_PORT;
        if (args.length >= 1)
            try
            {
                port = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException numberFormatException)
            {
            }
        ServerThread serverThread = new ServerThread(port);
        serverThread.start();

    }
}
