package sockets;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author P@bloid
 */
public class MTPParser extends DefaultHandler
{

    /**
     * Листенер событий
     */
    private final MTPEventListener listener;
    /**
     * Имя текущего тега
     */
    private String currentElement;
    /**
     * id клиента, которому предназначено сообщение
     */
    private int messageToId = -1;

    public MTPParser(MTPEventListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        currentElement = qName.toLowerCase();
        switch (qName.toLowerCase())
        {
            case "iamnew":
                String name = attributes.getValue("name");
                listener.clientConnected(name);
                break;
            case "messageto":
                String idStr = attributes.getValue("id");
                messageToId = Integer.parseInt(idStr);
                break;
        }
        System.out.println("Start element " + qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        String data = new String(ch, start, length).trim();
        switch (currentElement)
        {
            case "messageto":
                if (messageToId >= 0)
                    listener.messageReceived(messageToId, data);
                break;
        }
        System.out.println("Characters " + data);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        currentElement = "";
        messageToId = -1;
        System.out.println("End element " + qName);
    }
}
