package sockets;

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
    }
}
