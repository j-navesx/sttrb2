/**
 * Sistemas de Telecomunicacoes 
 *          2019/2020
 *
 * ServHttpd.java
 *
 * Main class with graphical interface and server control logic
 *
 * @author Luis Bernardo
 */


package server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Properties;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

public class ServHttpd extends javax.swing.JFrame {
    /**
     * Server name - EXERCISE: Replace the 00000 by your numbers
     */
    public final static String server_name = "HTTP Serv 2019/2020 by 00000/00000/00000";
    /**
     * Default file name when browser sends "/"
     */
    public final static String HOMEFILENAME = "index.htm";
    /**
     * Accepts up to 10 pending TCP connections
     */
    final static int MAXACCEPTLOG = 10;
    /**
     * Server socket where new connections are accepted
     */
    public ServerSocket ssock;
    /**
     * Main thread that accepts new connections
     */
    Daemon_tcp main_thread = null;
    /**
     * Datagram socket used to send REGIST messages to the proxy
     */
    private DatagramSocket ds;
/**
     * List of files exported to the proxy; it is equivalent to a 
     * HashMap<String,String>, but has methods to read from/write to a file
     */
    private final Properties files;
    /**
     * Timer object that sends REGIST messages
     */
//    private javax.swing.Timer timer;

    /**
     * Constructor
     */
    public ServHttpd() {
        initComponents();
        setTitle(server_name);
        ssock = null;
        files = new Properties();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jLabel5 = new javax.swing.JLabel();
        jTextLocalIP = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextLocalPort = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jTextRaizHtml = new javax.swing.JTextField();
        jButtonAdd = new javax.swing.JButton();
        jButtonDel = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextFilelist = new javax.swing.JTextField();
        jButtonSave = new javax.swing.JButton();
        jButtonLoad = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jButtonClear = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextProxyIP = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextProxyPort = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setTitle("Defined in constructor");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setMaximumSize(new java.awt.Dimension(450, 35));
        jPanel1.setMinimumSize(new java.awt.Dimension(450, 35));
        jPanel1.setName("Estado"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(450, 35));

        jToggleButton1.setText("Active");
        jToggleButton1.setMaximumSize(new java.awt.Dimension(85, 29));
        jToggleButton1.setPreferredSize(new java.awt.Dimension(85, 29));
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jToggleButton1);

        jLabel5.setText("IP");
        jPanel1.add(jLabel5);

        jTextLocalIP.setPreferredSize(new java.awt.Dimension(200, 20));
        jPanel1.add(jTextLocalIP);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setText("Port");
        jLabel4.setMaximumSize(new java.awt.Dimension(35, 17));
        jLabel4.setPreferredSize(new java.awt.Dimension(35, 17));
        jPanel1.add(jLabel4);

        jTextLocalPort.setText("20001");
        jPanel1.add(jTextLocalPort);

        getContentPane().add(jPanel1);

        jPanel3.setMaximumSize(new java.awt.Dimension(390, 37));
        jPanel3.setMinimumSize(new java.awt.Dimension(60, 33));
        jPanel3.setPreferredSize(new java.awt.Dimension(480, 33));

        jLabel8.setText("Html:");
        jPanel3.add(jLabel8);

        jTextRaizHtml.setText("/mnt/hgfs/Partilhada/ST_shared/lab2/html");
        jTextRaizHtml.setPreferredSize(new java.awt.Dimension(180, 24));
        jTextRaizHtml.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextRaizHtmlFocusLost(evt);
            }
        });
        jPanel3.add(jTextRaizHtml);

        jButtonAdd.setText("Add");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonAdd);

        jButtonDel.setText("Del");
        jButtonDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDelActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonDel);

        getContentPane().add(jPanel3);

        jPanel4.setMaximumSize(new java.awt.Dimension(32767, 39));

        jLabel2.setText("Filelist: ");
        jPanel4.add(jLabel2);

        jTextFilelist.setText("/home/user/Desktop/filelist.txt");
        jTextFilelist.setPreferredSize(new java.awt.Dimension(184, 28));
        jPanel4.add(jTextFilelist);

        jButtonSave.setText("Save list");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonSave);

        jButtonLoad.setText("Load list");
        jButtonLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLoadActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonLoad);

        getContentPane().add(jPanel4);

        jPanel2.setToolTipText("configuração");
        jPanel2.setMaximumSize(new java.awt.Dimension(460, 35));
        jPanel2.setMinimumSize(new java.awt.Dimension(420, 35));
        jPanel2.setName("configuracao"); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(460, 35));

        jButtonClear.setText("Clear");
        jButtonClear.setMaximumSize(new java.awt.Dimension(75, 29));
        jButtonClear.setOpaque(true);
        jButtonClear.setPreferredSize(new java.awt.Dimension(75, 29));
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonClear);

        jLabel1.setText("  Proxy:");
        jPanel2.add(jLabel1);

        jLabel3.setText("  IP ");
        jPanel2.add(jLabel3);

        jTextProxyIP.setText("127.0.0.1");
        jTextProxyIP.setMinimumSize(new java.awt.Dimension(14, 27));
        jTextProxyIP.setPreferredSize(new java.awt.Dimension(150, 24));
        jPanel2.add(jTextProxyIP);

        jLabel6.setText("Port ");
        jPanel2.add(jLabel6);

        jTextProxyPort.setText("20000");
        jTextProxyPort.setPreferredSize(new java.awt.Dimension(60, 24));
        jPanel2.add(jTextProxyPort);

        getContentPane().add(jPanel2);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(354, 104));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Filename", "Pathname"
            }
        ));
        jScrollPane2.setViewportView(jTable1);

        getContentPane().add(jScrollPane2);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(360, 180));

        jTextArea1.setLineWrap(true);
        jTextArea1.setPreferredSize(new java.awt.Dimension(200, 2000));
        jTextArea1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextArea1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTextArea1);

        getContentPane().add(jScrollPane1);

        getAccessibleContext().setAccessibleName("HTTP GUI - RIT2 2011/2012 by ?????/?????/?????");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Validates the html root directory name where the files are read
     * @param f
     */
    public void validate_name(javax.swing.JTextField f) {
        String str = f.getText();
        if (str.length() == 0) {
            return;
        }

        if (str.charAt(str.length() - 1) != File.separatorChar) {
            str = str + File.separatorChar;
        } else {
            while ((str.length() > 1) && (str.charAt(str.length() - 2) == File.separatorChar)) {
                str = str.substring(0, str.length() - 1);
            }
        }
        f.setText(str);
    }

    /**
     * Automatically validates the html directory name
     */
    private void jTextRaizHtmlFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextRaizHtmlFocusLost
        validate_name(jTextRaizHtml);
    }//GEN-LAST:event_jTextRaizHtmlFocusLost

    /**
     * Clear the text area
     */
    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        jTextArea1.setText("");
    }//GEN-LAST:event_jButtonClearActionPerformed

    /**
     * Clear the text area
     */
    private void jTextArea1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextArea1KeyPressed
        if (evt.getKeyChar() == ' ') {
            jTextArea1.setText("");
        }
    }//GEN-LAST:event_jTextArea1KeyPressed

    /**
     * Write the content of the file list in the GUI's table
     */
    private void display_files() {
        int line = 0;
        for (String str : files.stringPropertyNames()) {
            jTable1.setValueAt(str, line, 0);
            jTable1.setValueAt(files.getProperty(str), line, 1);
            line++;
        }
        for (; line < jTable1.getRowCount(); line++) {
            jTable1.setValueAt("", line, 0);
            jTable1.setValueAt("", line, 1);
        }
    }

    /**
     * Define the timer's callback function and creates timer object
     */
    private void set_timer_function(int period /*ms*/) {
        java.awt.event.ActionListener act;

        act = new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                /*
                    EXERCISE: Put here the timer code 
                    You need to regist all the filenames in the set given by 
                        files.stringPropertyNames()
                    You can use the method send_Registration to do it.
                */
            }
        };
        // Create the timer's object
        // timer = new javax.swing.Timer(period, act);
    }

    /**
     * Handles the button that starts and stops the Server 
     */
    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        if (jToggleButton1.isSelected()) {
            // Parse UI parameters            
            int tcp_port;
            try {
                tcp_port = Integer.parseInt(jTextLocalPort.getText());
            } catch (NumberFormatException e) {
                Log("Invalid port number\n");
                jToggleButton1.setSelected(false);
                return;
            }
            // Starts the server socketStarts web ssock
            int cnt = 0;
            do {
                try {
                    ssock = new ServerSocket(tcp_port+cnt, MAXACCEPTLOG);
                } catch (java.io.IOException e) {
                    // If the tcp_port is already used, try the next one until reaching a free one
                    if (cnt>100) {
                        Log("Server start failure: " + e + "\n");
                        jToggleButton1.setSelected(false);
                        return;
                    }
                    cnt++;
                }
            } while (ssock == null);
            jTextLocalPort.setText(Long.toString(tcp_port+cnt)); // Writes the port used.
            
            // Gets the local IP
            try {
                jTextLocalIP.setText(InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                Log("Failed to get local IP: " + e + "\n");
            }
            
             // Start the UDP socket
            try {
                ds = new DatagramSocket(0); // 0 means, use a random free port
            } catch (SocketException ex) {
                Log("Failed creation of UDP socket: " + ex + "\n");
                try {
                    ssock.close();
                } catch (IOException ex1) {
                    Log("Error closing TCP socket: " + ex1 + "\n");
                    ssock = null;
                }
                jToggleButton1.setSelected(false);
                return;
            }

            // starts the main thread (that receives connections)
            main_thread = new Daemon_tcp(this, ssock);
            main_thread.start();
            
            setEditable_jText(false);
            Log("Web server active\n");
        } else {
            Log("Server did not stop timer and Datagram socket\n");
            /*
                EXERCISE: Place here the code to stop the timer
            */
            // Stop the server socket thread and the sockets
            try {
                /*
                    EXERCISE: Place here the code to close the Datagram socket
                */
                if (main_thread != null) {
                    main_thread.stop_thread();
                    main_thread = null;
                }
                if (ssock != null) {
                    ssock.close();
                    ssock = null;
                }
            } catch (IOException e) {
                Log("Exception closing server: " + e + "\n");
            }
            setEditable_jText(true);
            Log("Web server stopped\n");
        }
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    /**
     * Exit the Application
     */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm

    /**
     * Adds a new file to the filelist exported to the proxy
     * @param evt graphical object
     */
    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        try {
            // Choose file to add
            if (jFileChooser1.showDialog(this, "Select file") == JFileChooser.APPROVE_OPTION) {
                File f = jFileChooser1.getSelectedFile();
                files.setProperty(f.getName(), f.getAbsolutePath());
                display_files();
            }
        } catch (Exception e) {
            System.err.println("Error selecting output file: " + e);
        }
    }//GEN-LAST:event_jButtonAddActionPerformed

    /**
     * Deletes a file from the filelist exported to the proxy
     * @param evt 
     */
    private void jButtonDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDelActionPerformed
        int row = jTable1.getSelectedRow();
        if (row == -1) {
            Log("Please select a file\n");
            return;
        }
        String name = (String) jTable1.getValueAt(row, 0);
        files.remove(name);
        display_files();
    }//GEN-LAST:event_jButtonDelActionPerformed

    /**
     * Save the filelist exported to the proxy in a file
     * @param evt 
     */
    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        try {
            // Choose file to write the list
            if (jFileChooser1.showDialog(this, "Select file to write filelist") == JFileChooser.APPROVE_OPTION) {
                File f = jFileChooser1.getSelectedFile();
                OutputStream fOut = new FileOutputStream(f);
                jTextFilelist.setText(f.getAbsolutePath());
                files.store(fOut, "Files to be shared");
                fOut.close();
            }
        } catch (Exception e) {
            System.err.println("Error writing filelist to file:" + e);
        }
    }//GEN-LAST:event_jButtonSaveActionPerformed

    /**
     * Load the filelist to be exported to a proxy from a file
     * @param evt 
     */
    private void jButtonLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLoadActionPerformed
        try {
            // Choose file to add
            if (jFileChooser1.showDialog(this, "Select file to load filelist") == JFileChooser.APPROVE_OPTION) {
                File f = jFileChooser1.getSelectedFile();
                FileInputStream fIn = new FileInputStream(f);
                jTextFilelist.setText(f.getAbsolutePath());
                files.load(fIn);
                fIn.close();
                display_files();
            }
        } catch (Exception e) {
            System.err.println("Error writing filelist to file:" + e);
        }
    }//GEN-LAST:event_jButtonLoadActionPerformed

    /**
     * Controls editability of jTexts
     *
     * @param editable if the user can change the boxes' contents
     */
    public void setEditable_jText(boolean editable) {
        jTextLocalPort.setEditable(editable);
        jTextRaizHtml.setEditable(editable);
        jTextProxyPort.setEditable(editable);
    }

    /**
     * Logs a message on the command line and on the text area
     *
     * @param s string to be written
     */
    public void Log(String s) {
        jTextArea1.append(s);
        System.out.print(s);
    }

    /**
     * Returns the port number in a JTextField
     */
    private int getPort(JTextField jt) {
        try {
            int port = Integer.parseInt(jt.getText());
            if ((port <= 0) || (port > 65535)) {
                return 0;
            }
            return port;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Returns the local TCP port number
     *
     * @return local port number
     */
    public int getLocalPort() {
        return getPort(jTextLocalPort);
    }

    /**
     * Returns the Proxy's UDP port number
     *
     * @return proxy port number
     */
    public int getProxyPort() {
        return getPort(jTextProxyPort);
    }

    /**
     * Returns the root html directory
     *
     * @return string with the root pathname
     */
    public String getRaizHtml() {
        return jTextRaizHtml.getText();
    }

    /**
     * Returns the full pathname associated to a filename
     * @param name  filename
     * @return the pathname where the file is
     */
    public String getFilename(String name) {
        return files.getProperty(name);
    }

    /**
     * Sends a REGIST message to the proxy
     * @param name the name of the file to be sent
     * @return true if successful
     */
    public boolean send_Registration(String name) {
        try {
            // Create and send packet to trader ssock
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            // Read the IP and port of the proxy server
            InetAddress ip = InetAddress.getByName(jTextProxyIP.getText());
            int port = getProxyPort();
            if (port <= 0) {
                return false;
            }

            // Get local IP
            String ip_srv= jTextLocalIP.getText();

//            System.out.println("Registering to " + ip.getHostAddress() + ":" + port);
            Log("Registering to " + ip.getHostAddress() + ":" + port + "\n");
            
            /* EXERCISE XX:
                Put here the code to prepare the REGIST message and to send it 
                    to the Proxy server!
            */
            Log("Message REGIST not sent - complete the missing code\n");
            
            return true;
        } catch (Exception ex) {
            Log("Error sending Registration packet:" + ex + "\n");
            return false;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new ServHttpd().setVisible(true);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonDel;
    private javax.swing.JButton jButtonLoad;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextFilelist;
    private javax.swing.JTextField jTextLocalIP;
    private javax.swing.JTextField jTextLocalPort;
    private javax.swing.JTextField jTextProxyIP;
    private javax.swing.JTextField jTextProxyPort;
    private javax.swing.JTextField jTextRaizHtml;
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration//GEN-END:variables
}
