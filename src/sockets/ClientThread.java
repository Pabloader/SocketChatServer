package sockets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.SequenceInputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 * Класс клиентского потока. Этот класс запускается для каждого подключившегося
 * клиента.
 *
 * @author P@bloid
 */
class ClientThread extends Thread implements MTPEventListener
{

    /**
     * Тип пакета: соединение с сервером
     */
    public static final int TYPE_IAMNEW = 0;
    /**
     * Тип пакета: список клиентов
     */
    public static final int TYPE_LIST = 1;
    /**
     * Тип пакета: отправка сообщения
     */
    public static final int TYPE_MESSAGETO = 2;
    /**
     * Тип пакета: получение сообщения
     */
    public static final int TYPE_MESSAGEFROM = 3;
    /**
     * Клиентский сокет
     */
    private final Socket s;
    /**
     * Поток ввода
     */
    private DataInputStream input;
    /**
     * Поток вывода
     */
    private DataOutputStream output;
    /**
     * Парсер XML
     */
    private SAXParser parser;
    /**
     * Сервер, который обрабатывает сообщения этого клиента
     */
    private ServerThread server;
    /**
     * Имя клиента, к которому подключен поток
     */
    private String clientName;
    /**
     * Id клиента, к которому подключен поток
     */
    private int clientId;

    /**
     * Создает новый поток для клиента, подключенного через s
     *
     * @param s Клиентский сокет
     */
    public ClientThread(Socket s, ServerThread server)
    {
        this.s = s;
        this.server = server;
        try
        {
            input = new DataInputStream(s.getInputStream());
            output = new DataOutputStream(s.getOutputStream());
        }
        catch (IOException ex)
        {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try
        {
            parser = factory.newSAXParser();
        }
        catch (ParserConfigurationException | SAXException ex)
        {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run()
    {
        try
        {
            while (!s.isClosed())
                try (SequenceInputStream sis = new SequenceInputStream(new ByteArrayInputStream("<root>".getBytes()), input))
                {
                    parser.parse(sis, new MTPParser(this));
                }
                catch (SAXException sax)
                {
                }
            input.close();
            output.close();
            System.out.println("Closed " + clientName + "...");
        }
        catch (IOException ex)
        {
        }
    }

    /**
     * @see MTPEventListener#messageReceived(int, java.lang.String)
     */
    @Override
    public void messageReceived(int id, String message)
    {
        System.out.println("Message received from " + clientId + " to " + id + " message='" + message + "'");
        byte[] data = String.format("<MessageFrom Id=\"%d\">%s</MessageFrom>", clientId, message).getBytes();
        String header = String.format("<Header Type=\"%d\" Size=\"%d\" />", TYPE_MESSAGEFROM, data.length);
        ClientThread client = server.getClients().get(id);
        if (client != null && !client.isClosed())
            try
            {
                client.output.writeBytes(header);
                client.output.write(data);
            }
            catch (IOException ex)
            {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    /**
     * @see MTPEventListener#clientConnected(java.lang.String)
     */
    @Override
    public void clientConnected(String clientName)
    {
        System.out.println("Client connected name=" + clientName);
        this.clientName = clientName;
        StringBuilder sb = new StringBuilder("<List>");
        Map<Integer, ClientThread> clients = server.getClients();
        for (Entry<Integer, ClientThread> entry : clients.entrySet())
        {
            int i = entry.getKey();
            ClientThread client = entry.getValue();
            if (client != null && !client.isClosed() && client.clientName != null)
                sb.append("<Client Id=\"").append(i).append("\" Name=\"").append(client.clientName).append("\" IsI=\"").append(client == this).append("\" />");
        }
        sb.append("</List>");
        byte[] data = sb.toString().getBytes();
        String header = String.format("<Header Type=\"%d\" Size=\"%d\" />", TYPE_LIST, data.length);
        try
        {
            output.writeBytes(header);
            output.write(data);
        }
        catch (IOException ex)
        {
            try
            {
                s.close();
            }
            catch (IOException ex1)
            {
            }
        }
    }

    /**
     * Возвращает true, если поток закрыт
     *
     * @return true, если поток закрыт
     */
    private boolean isClosed()
    {
        return s.isClosed();
    }

    /**
     * Получить id клиента
     *
     * @return id клиента
     */
    public int getClientId()
    {
        return clientId;
    }

    /**
     * Установить ID клиента
     *
     * @param clientId Новый ID
     */
    public void setClientId(int clientId)
    {
        this.clientId = clientId;
    }
}
