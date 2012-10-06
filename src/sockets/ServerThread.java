package sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс серверного потока. Прослушка и принятие соединений происходит именно в
 * нем.
 *
 * @author P@bloid
 */
class ServerThread extends Thread
{

    /**
     * Номер прослушиваемого порта
     */
    private int port;
    /**
     * Запущен ли сервер в данный момент
     */
    private boolean running;
    /**
     * Список подключенных клиентов
     */
    private Map<Integer, ClientThread> clients = new HashMap<>();
    /**
     * номер первого свободного клиента
     */
    private int clientId = 0;

    /**
     * Содать новый серверный поток
     *
     * @param port номер порта для прослушивания
     */
    public ServerThread(int port)
    {
        this.port = port;
    }

    /**
     * Главный метод потока
     */
    @Override
    public void run()
    {
        running = true;
        try
        {
            ServerSocket ss = new ServerSocket(port);
            while (running)
            {
                Socket s = ss.accept();
                ClientThread ct = new ClientThread(s, this);
                ct.setClientId(clientId);
                clients.put(clientId++, ct);
                ct.start();
            }
            ss.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public synchronized void stopServer()
    {
        this.running = false;
    }
    
    public Map<Integer, ClientThread> getClients()
    {
        return clients;
    }
}
