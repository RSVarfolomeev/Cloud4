import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Handler implements Runnable {

    private String userDir = "server/serverFiles";
    private Path userPath;

    private static int inc = 0;
    private String userName;
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean running;

    public Handler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        inc++;
        userName = "User" + inc;
        userDir += "/" + userName;
        userPath = Paths.get(userDir);
        if (Files.notExists(userPath)) {
            Files.createDirectory(userPath);
        }
        initStreams();
        running = true;
    }

    private void initStreams() throws IOException {
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    private String readMessage() throws IOException {
        return in.readUTF();
    }

    public void writeMessage(String message) throws IOException {
        out.writeUTF(message);
        out.flush();
    }

    private String wrapMessageWithName(String message) {
        return userName + ": " + message;
    }

    @Override
    public void run() {
        try {
            while (running) {
                String message = readMessage();
                System.out.println("Received: " + message);
                if (message.equals("/list")) {
                    List<String> userFiles = getFiles();
                    out.writeInt(userFiles.size());
                    for (String userFile : userFiles) {
                        out.writeUTF(userFile);
                    }
                    out.flush();
                }
                if (message.equals("/upload")) {
                    String fileName = in.readUTF();
                    long size = in.readLong();
                    byte[] buffer = new byte[256];
                    Path path = Paths.get(userDir, fileName);
                    if (Files.notExists(path)) {
                        Files.createFile(path);
                    }
                    FileOutputStream fos = new FileOutputStream(new File(userDir + "/" + fileName));
                    for (int i = 0; i < (size + 255) / 256; i++) {
                        if (i == (size + 255) / 256 - 1) {
                            for (int j = 0; j < size % 256; j++) {
                                fos.write(in.readByte());
                            }
                        } else {
                            int read = in.read(buffer);
                            fos.write(buffer, 0, read);
                        }
                    }
                    fos.close();
                }
                if (message.equals("/download")) {
                    String fileName = in.readUTF();
                    File file = new File(userDir + "/" + fileName);
                    long size = file.length();
                    FileInputStream fis = new FileInputStream(file);
                    out.writeLong(size);
                    byte[] buffer = new byte[256];
                    for (int i = 0; i < (size + 255) / 256; i++) {
                        int read = fis.read(buffer);
                        out.write(buffer, 0, read);
                    }
                    out.flush();
                }
            }
        } catch (Exception e) {
            System.err.println("Exception while read or write!");
            server.kick(this);
        } finally {
            close();
        }
    }

    private List<String> getFiles() throws IOException {
        return Files.list(userPath).map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
    }

    public void close() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
