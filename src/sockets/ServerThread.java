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
    private List<ClientThread> clients = new ArrayList<>();

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
                clients.add(ct);
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

    public List<ClientThread> getClients()
    {
        return clients;
    }
}
