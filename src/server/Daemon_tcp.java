/**
 * Sistemas de Telecomunicacoes 2019/2020
 *
 * Daemon_tcp.java
 *
 * Thread class that handles the acceptation of new connection on the server
 * socket
 *
 * @author Luis Bernardo
 */
package server;

import java.net.ServerSocket;
import java.net.Socket;

class Daemon_tcp extends Thread {

    /**
     * GUI object
     */
    ServHttpd root;
    /**
     * Server socket object
     */
    ServerSocket ss;
    /**
     * Active flag
     */
    volatile boolean active;

    /**
     * Constructor
     * @param root  GUI object
     * @param ss    server socket
     */
    Daemon_tcp(ServHttpd root, ServerSocket ss) {
        this.root = root;
        this.ss = ss;
    }

    /**
     * Interrupt the thread
     */
    public void wake_up() {
        this.interrupt();
    }

    /**
     * Stop the thread
     */
    public void stop_thread() {
        active = false;
        this.interrupt();
    }

    @Override
    public void run() {
        System.out.println(
                "\n******************** " + ServHttpd.server_name + " started ********************\n");
        active = true;
        while (active) {
            try {
                Socket s= ss.accept();  // Blocks waiting for new connection
                SHttpThread conn= new SHttpThread(root, ss, s);
                conn.start();
            } catch (java.io.IOException e) {
                if (active)
                    root.Log("IO exception: " + e + "\n");
                active = false;
            }
        }
    }
} // end of class Daemon_tcp
