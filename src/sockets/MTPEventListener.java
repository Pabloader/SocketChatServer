/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets;

/**
 *
 * @author P@bloid
 */
public interface MTPEventListener
{

    /**
     * Вызывается при получении сообщения от клиента
     *
     * @param id id пользователя, для которого предназначено сообщение
     * @param message сообщение
     */
    public void messageReceived(int id, String message);

    /**
     * Вызывается при подключении нового клиента
     *
     * @param clientName имя клиента
     */
    public void clientConnected(String clientName);
}
