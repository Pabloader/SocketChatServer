package sockets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс клиентского потока. Этот класс запускается для каждого подключившегося
 * клиента.
 *
 * @author P@bloid
 */
class ClientThread extends Thread
{

    /**
     * Клиентский сокет
     */
    private final Socket s;

    /**
     * Создает новый поток для клиента, подключенного через s
     *
     * @param s Клиентский сокет
     */
    public ClientThread(Socket s)
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
