import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CloudController implements Initializable {

    private String clientDir = "client/clientDir";

    public ListView<String> clientView;
    public ListView<String> serverView;

    public void upload(ActionEvent actionEvent) {

    }

    public void download(ActionEvent actionEvent) {

    }

    private void fillServerData() {
        try {
            serverView.getItems().addAll(getServerFiles());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Can not fill client files");
        }
    }

    private void fillClientData() {
        try {
            clientView.getItems().addAll(getClientFiles());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Can not fill client files");
        }
    }

    private List<String> getServerFiles() throws IOException {
        Network.get().getOut().writeUTF("/list");
        int size = Network.get().getIn().readInt();
        ArrayList<String> serverFiles = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            serverFiles.add(Network.get().getIn().readUTF());
        }
        return serverFiles;
    }

    private List<String> getClientFiles() throws IOException {
        // ctrl + alt + v, cmd + opt + v
        Path clientDirPath = Paths.get(clientDir);
        // Files
        return Files.list(clientDirPath)
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fillClientData();
        fillServerData();
    }
}
