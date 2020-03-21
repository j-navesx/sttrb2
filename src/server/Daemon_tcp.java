/**
 * Sistemas de Telecomunicacoes 
 *          2018/2019
 *
 * Daemon_tcp.java
 *
 * Thread class that handles the acceptation of new connection on the server socket
 *
 * Created on February 20, 2019, 10:00
 *
 * @author Luis Bernardo
 */
package server;

import java.net.ServerSocket;

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

    /**
     * Thread's code
     */
    @Override
    public void run() {
        System.out.println(
                "\n******************** " + ServHttpd.SERVER_NAME + " started ********************\n");
        active = true;
        while (active) {
            try {
                SHttpThread conn
                        = new SHttpThread(root, ss, ss.accept());
                conn.start();
            } catch (java.io.IOException e) {
                if (active)
                    root.Log("IO exception: " + e + "\n");
                active = false;
            }
        }
    }
} // end of class Daemon_tcp
