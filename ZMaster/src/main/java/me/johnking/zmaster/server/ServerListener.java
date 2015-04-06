package me.johnking.zmaster.server;

import java.io.*;

/**
 * Created by Marco on 15.08.2014.
 */
public class ServerListener extends Thread {

    private Server server;

    public ServerListener(Server server) {
        this.server = server;
    }
    private boolean isRunning() {
        try {
            server.getServerProcess().exitValue();
        } catch (IllegalThreadStateException e) {
            return true;
        }
        return false;
    }
    @Override
    public void run() {
        StreamGobbler errorGobbler = new StreamGobbler(server.getServerProcess().getErrorStream(), server);
        StreamGobbler normalGobbler = new StreamGobbler(server.getServerProcess().getInputStream(), server);
        errorGobbler.start();
        normalGobbler.start();
        try {
            int exitVal = server.getServerProcess().waitFor();
            server.processStop(exitVal);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class StreamGobbler extends Thread {
        private InputStream is;

        private Server server;

        StreamGobbler(InputStream is, Server server) {
            this.is = is;
            this.server = server;
        }
        @SuppressWarnings("ResultOfMethodCallIgnored")
        public void run() {
            File outputFile = new File("logs/" + server.getType() + "/" + server.getServerId() + ".log");
            FileOutputStream fos = null;
            OutputStreamWriter osw;
            BufferedWriter bw = null;
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                outputFile.getParentFile().mkdirs();
                fos = new FileOutputStream(outputFile, true);
                osw = new OutputStreamWriter(fos, "UTF-8");
                bw = new BufferedWriter(osw);
                String line;
                while ((line = br.readLine()) != null) {
                    synchronized (server.getLog()) {
                        server.getLog().add(line);
                        if (server.getLog().size() > 100) {
                            server.getLog().removeFirst();
                        }
                    }
                    server.logLine(line);
                    bw.write(line + "\n");
                    bw.flush();
                }
            } catch (IOException ioe) {
                System.err.println("stdout/stderr of server " + server.getServerId() + " was closed while reading");
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (fos != null) {
                        if (bw != null) {
                            bw.flush();
                        }
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

