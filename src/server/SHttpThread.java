/**
 * Sistemas de Telecomunicacoes 
 *          2018/2019
 *
 * SHttpThread.java
 *
 * Thread class that handles the (over)simplified HTTP protocol message exchange
 *
 * Created on February 20, 2018, 10:00
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
     * Connection socket, to communicate with the browser
     */
    Socket client;
    /**
     * Sequence number of a sent AUTH_REQ waiting for an answer, or -1
     */
    int seq;

    
    
    /**
     * Prepares the rit2CGI demo web page
     * @param ip        IP of the browser
     * @param port      port of the browser
     * @param user      user ID
     * @param p1        position 1
     * @param c1        char 1
     * @param p2        position 2
     * @param c2        char 2
     * @param p3        position 3
     * @param c3        char 3
     * @param message   Message to echo in the browser
     * @return String with the content of the web page
     */
    public String make_testPage(String ip, int port, String user, 
            int p1, String c1, int p2, String c2, int p3, String c3, String message) {
        
        // Prepare string html with web page
        String html= "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<html>\r\n<head>\r\n";
        html += "<meta content=\"text/html; charset=ISO-8859-1\" http-equiv=\"content-type\">\r\n";
        html += "<title>STlogin.htm</title>\r\n</head>\r\n<body>\r\n";
        html += "<h1 align=\"center\">Authentication Page for ST web server</h1>\r\n";
        html += "<h1 align=\"center\"><font color=\"#800000\">Sistemas de Telecomunica&ccedil;&otilde;es</font> <font color=\"#c0c0c0\">2018/2019</font></h1>\r\n";
        html += "<h3 align=\"center\">5th Lab work</h3>\r\n";
        html += "<p align=\"left\">Connection received from <font color=\"#ff0000\">" + ip + "</font>:";
        html += "<font color=\"#ff0000\">" + port + "</font>.</p>\r\n";
        
        if (message!=null && message.length()>0) {
            html += "<font color=\"#0000ff\">" + message + "</font>\r\n";
        }
        html += "<form method=\"post\" action=\"STlogin.cgi\">\r\n<h3>\r\nUser login</h3>";
        html += "<p>Username <input name=\"user\" size=\"10\" type=\"text\""+
                (user.length()>0 ? " value=\""+user+"\"": "")+"></p>\r\n";
        html += "<p>Position <input name=\"P1\" size=\"2\" type=\"text\""+
                (p1>0 ? " value="+p1 : "")+
                ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Key <input name=\"K1\" size=\"1\" type=\"text\""+
                (c1.length()>0 ? " value="+c1 : "")+
                "></p>\r\n";
        html += "<p>Position <input name=\"P2\" size=\"2\" type=\"text\""+
                (p2>0 ? " value="+p2 : "")+
                ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Key <input name=\"K2\" size=\"1\" type=\"text\""+
                (c2.length()>0 ? " value="+c2 : "")+
                "></p>\r\n";
        html += "<p>Position <input name=\"P3\" size=\"2\" type=\"text\""+
                (p3>0 ? " value="+p3 : "")+
                ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Key <input name=\"K3\" size=\"1\" type=\"text\""+
                (c3.length()>0 ? " value="+c3 : "")+
                "></p>\r\n";
        html += "<p><input value=\"Login\" name=\"Login\" type=\"submit\"><input value=\"Clear\" type=\"reset\" value=\"Reset\" name=\"Reset\"></p>\r\n</form>\r\n";
        html += "</body>\r\n</html>\r\n";
        
        return html; // HTML page code
    }

    
    /**
     * Internal class which stores the HTTP request information
     */
    class HTTPhdr {
        /**
         * Cookie contents received from the browser
         */
        public String cookie;
        /**
         * Content of a Set-Cookie field, to send in the reply to the browser
         */
        public String return_cookie;
        /**
         * Data content of a POST request 
         */
        public String paramstr;
        /**
         * Hashmap with the fields received in the POST request
         */
        public HashMap<String,String> param;    // Parameter list
        
        /**
         * Constructor of an empty request
         */
        public HTTPhdr() {
            cookie= null;
            return_cookie= null;
            paramstr= null;
            param= new HashMap<>();
        }
        
        /**
         * Store the cookie received
         * @param cookie 
         */
        public void set_cookie(String cookie) {
            this.cookie= cookie;
        }
        
        public void set_rcookie(String return_cookie) {
            this.return_cookie= return_cookie;
        }
        
        /**
         * Stores the POST parameter and parses its content, generating the list
         * @param paramstr - POST data string
         */
        public void set_param(String paramstr) {
            this.paramstr= paramstr;
            if (paramstr.length()>0) {
                int ix;
                StringTokenizer stp= new StringTokenizer(paramstr,"&");
                while (stp.hasMoreTokens()) {
                    String aux= stp.nextToken();
                    System.out.println("Token: "+aux);
                    if ((ix = aux.indexOf('=')) != -1) {  // Valid structure
                        if (aux.substring(ix + 1).trim().length() > 0)
                            param.put(aux.substring(0,ix).trim(), aux.substring(ix + 1).trim());
                    }
                }
                System.out.println("\nHashMap dump: "+param.toString());
            }
        }
    } // End of internal class HTTPhdr
            
    /**
     * Creates a new instance of HttpThread
     *
     * @param root GUI object
     * @param ss Server socket
     * @param client Connections socket
     */
    public SHttpThread(ServHttpd root, ServerSocket ss, Socket client) {
        this.root = root;
        this.ss = ss;
        this.client = client;
        this.seq = -1;      // No pending query
    }

    /**
     * Parses the lines with properties following the main HTTP header and
     * returns the Authorization line value
     *
     * @param in Input stream from browser
     * @param isPost
     * @return Properties with properties defined
     * @throws java.io.IOException
     */
    private HTTPhdr parse_CGIparam(BufferedReader in, boolean isPost) throws IOException {
        HTTPhdr reply= new HTTPhdr();
        String str= new String ();
        String req;
        int len= -1, ix, n, cnt= 0;

        // Skip all parameters until the first empty line
        while (((req = in.readLine()) != null) && (req.length() != 0)) {
            System.out.println("hdr(" + req + ")");
            if (req.startsWith("Content-Length: ")) {
                req = req.substring(16).trim();
                try {
                    len= Integer.parseInt(req);
                }
                catch (NumberFormatException e) {
                    // Ignore
                }
            } else if (req.contains("Cookie")) {
                // Only writes the Authorization header line in the GUI
                if ((ix = req.indexOf(':')) != -1) {  // Valid structure
                    reply.set_cookie(req.substring(ix + 1).trim());
                }
            }
        }
        
        // Get all the parameters that follow the header
        if (isPost && in.ready() && (len>0)) {
            char [] cbuf= new char [len];

            System.out.println("Get POST parameters (len="+len+")");
            while ((cnt<len) && (n= in.read (cbuf)) > 0) {
                str= str + new String (cbuf);
                cnt += n;
            }
            reply.set_param(str);
        }
        
        return reply;
    }

    
    /**
     * Sends a "404 Not Found" error message to the brower, with a sample HTML
     * web page
     *
     * @param pout Stream object associated to the client's socket
     * @param servername Server name
     */
    public void return_error(PrintStream pout, String servername) {
        if (pout == null) {
            return;
        }
        pout.print("HTTP/1.0 404 Not Found\r\nServer: " + servername + "\r\n");
        pout.print("\r\n"); // Header separator
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
        DatagramSocket ds; // Datagram socket, to communicate with the authentication server

        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream(), "8859_1"));
            OutputStream out = client.getOutputStream();
            pout = new PrintStream(out, false, "8859_1");
            ds = new DatagramSocket();

            String request = in.readLine(); // Read the first line 
            if (request == null) // End of connection
            {
                return;
            }

            // The first request line has the format “GET /file.html HTTP/1.1”
            // This code uses the “StringTokenizer” class to separate the 3 components
            root.Log("\nRequest= '" + request + '\n');
            StringTokenizer st = new StringTokenizer(request);
            if (st.countTokens() != 3) // If it does not have the 3 components isn't is_valid
            {
                return; // Invalid request
            }
            String method= st.nextToken(); // Jumps the first token
            String file = st.nextToken(); // Gets the second token and ignores the third one
            //HTTPhdr http_req= parse_CGIparam(in, method.equals("POST"));
            
            //String html= null;  // Contains an alternative page with a form, 
                                //     to ask for the reponse to a challenge

            
            /********************************
             * Lab work 5 
             *      Modify this function to check if the browser is sending a 
             *   valid authentication string in a cookie or in a POST request
             *   with the format:  "user=user&P1=1&K1=a&P2=2&K2=b&P3=3&K3=d&Login=Login"
             *   where P1,P2,P3 are positions, K1,K2,K3 are the password characters 
             *   in the positions,  user is the user name.
             * 
             *      To help you with this task, you will need to use:
             *          - http_req of the class HTTPhdr has the fields retrieved from the HTTP request
             *              including:
             *                 http_req.cookie (the cookie received); 
             *                 http_req.paramstr (the form fields received in a POST)
             *                 http_req.param (HashMap with the form fields received in a POST,
             *                      allowing searching for individual fields (e.g. http_req.param.get("K1");)
             *                 http_req.r_cookie, to store a cookie to return to the browser in a Set-Cookie field.
             *          - function get_challengeresponse of class ServHttpd
             * parse_Authorization of this class
             *          - function validate_cached_passwd of class ServHttpd
             *          - function return_unauthorized of this class
             *          - function make_testPage of this class, to prepare a form page
             */
            // ...
            
            
            
            // if it is a GET method and has a valid cookie
            //      then send the web page requested!
            //      - use the valid_KEY method in class ChallengeResponse to test the cookie contents 
            // if it is a GET and the cookie is invalid, then IGNORE THE COOKIE RECEIVED
            
            // if it is a POST and param was received
            //      test if user is defined -  use the HashMap!
            //      test if the paramstr is a valid response
            //      - use the valid_KEY method in class ChallengeResponse to test the paramstr contents
            //      - use the has_challenge method in class ChallengeResponse to test if a challenge-response is valid
            //      Use the function make_testPage to prepare the form with the parameters previously received, 
            //          and store it in html string
            
            // For instance, a first access with an empty form is obtained using:
            //        html= make_testPage(client.getInetAddress().getHostAddress(), client.getPort(), "", 
            //                0, "", 0, "", 0, "", "Introduce the username");

            // if (send file) {                
                // The user is validated - send the file
                String filename = root.getRaizHtml() + file + (file.equals("/") ? ServHttpd.HOMEFILENAME : "");
                System.out.println("Filename= '"+filename+"'");
                File f= new File (filename);
                if (!f.exists() || !f.isFile() || !f.canRead()) {
                    // File does not exist or cannot read
                    System.out.println("Filename= " + filename + " does not exist or cannot read");
                    return_error(pout, ServHttpd.SERVER_NAME);
                    return;
                }
                FileInputStream fis = new FileInputStream(f);
                byte[] data = new byte[fis.available()]; // Alocate an array with the size of the file 
                fis.read(data); // Reads the entire file to memory
                
                // Writes the HTTP "200 OK" header
                pout.print("HTTP/1.0 200 OK\r\nServer: " + ServHttpd.SERVER_NAME + "\r\n");
                
                /********************************
                 * Lab work 5 
                 * Send here a Set-Cookie: header, with the authentication string received
                 */ 
                // ...
                
                pout.print("\r\n");
                //pout.print(str);
                out.write(data); // Writes the file contents to the socket
                out.flush(); // Forces the sending
                fis.close(); // Closes the file 
            //} else {
                // Writes the HTTP "200 OK" header and a form page
                //   to receive a POST with the response
                
                /********************************
                 * Lab work 5 
                 *      Place here the code to write the "200 OK" header; 
                 *      add a Set-Cookie header in the answer to the browser, when required.
                 *      and send the content of html string.
                 */
                // ...
                
            //}
            
 
        } catch (FileNotFoundException e) {
            // Handle unpredicted file errors
            System.out.println("File not found");
            // Writes a "file not found" error to the socket
            return_error(pout, ServHttpd.SERVER_NAME);
        } catch (SocketException e) {
            System.out.println("Error creating datagram socket: " + e + "\n");
        } catch (IOException e) {
            System.out.println("I/O error " + e);
        } catch (Exception e) {
            System.out.println("Error " + e);
        } finally {
            // This code is always run, even when there are exceptions
            try {
                client.close(); // Closes the socket and all associated streams
            } catch (Exception e) {
                /* Ignore everything */ 
            }
        }
    }
}
