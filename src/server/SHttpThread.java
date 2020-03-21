/**
 * Sistemas de Telecomunicacoes 
 *          2019/2020
 *
 * SHttpThread.java
 *
 * Thread class that handles the (over)simplified HTTP protocol message exchange
 *
 * @author Luis Bernardo
 */
package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class SHttpThread extends Thread {

    /**
     * Reference to the GUI object
     */
    ServHttpd root;
    /**
     * Server socket where connects are accepted
     */
    ServerSocket ss;
    /**
     * Connection socket
     */
    Socket sock;

    /**
     * Creates a new instance of HttpThread
     *
     * @param root GUI object
     * @param ss Server socket
     * @param sock Connections socket
     */
    public SHttpThread(ServHttpd root, ServerSocket ss, Socket sock) {
        this.root = root;
        this.ss = ss;
        this.sock = sock;
    }

    /**
     * Sends a "404 Not Found" error message to the brower
     *
     * @param pout Stream object associated to the sock's socket
     * @param servername Server name
     */
    public void return_error(PrintStream pout, String servername) {
        if (pout == null) {
            return;
        }
        pout.print("HTTP/1.0 404 Not Found\r\nServer: " + servername + "\r\n\r\n");
        // Prepares a web page with an error description
        pout.print("<HTML>\r\n");
        pout.print("<HEAD><TITLE>Not Found</TITLE></HEAD>\r\n");
        pout.print("<H1> Page not found </H1>\r\n");
        pout.print("</HTML>\r\n");
    }

    /**
     * Threads code that waits for a request and send the answer
     */
    @Override
    public void run() {
        PrintStream pout = null;
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream(), "8859_1"));
            OutputStream out = sock.getOutputStream();
            pout = new PrintStream(out, false, "8859_1");
            String request = in.readLine(); // Read the first line 
            // The first request line has the format “GET /file.html HTTP/1.1”
            // This code uses the “StringTokenizer” class to separate the 3 components
            root.Log("Request= '" + request + '\n');
            StringTokenizer st = new StringTokenizer(request);
            if (st.countTokens() != 3) // If it does not have the 3 components isn't valid
            {
                return; // Invalid request
            }
            st.nextToken(); // Jumps the first token
            String file = st.nextToken(); // Gets the second token and ignores the third one
            String filename = null;
            // Prepares the corresponding pathname of the file to be transfered
            if (file.length() > 1) // Checks if it is a registered name
            {
                filename = root.getFilename(file.substring(1)); // Get the full pathname registered
            }
            if (filename == null) { // It isn't unregistered
                // The pathname is equal to the “root” + path; If it only has “/” adds index.htm
                filename = root.getRaizHtml() + file + (file.equals("/") ? "index.htm" : "");
            }
            System.out.println("Filename= " + filename);
            FileInputStream fis = new FileInputStream(filename);
            byte[] data = new byte[fis.available()]; // Alocate an array with the size of the file 
            fis.read(data); // Read the entire file to memory
            // Write the HTTP "200 OK" header
            pout.print("HTTP/1.0 200 OK\r\nServer: " + ServHttpd.server_name + "\r\n");
            // Write an empty line
            pout.print("\r\n");
            out.write(data); // Writes the file contents to the socket     ￼￼
            out.flush(); // Forces the sending
            fis.close(); // Closes the file 

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            // Writes a "file not found" error to the socket
            return_error(pout, ServHttpd.server_name);
        } catch (IOException e) {
            System.out.println("I/O error " + e);
        } catch (Exception e) {
            System.out.println("Error " + e);
        } finally {
            // This code is always run, even when there are exceptions
            try {
                sock.close(); // Closes the socket and all associated streams
            } catch (Exception e) { /* Ignore everything */ }
        }
    }
}
