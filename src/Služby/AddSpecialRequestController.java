package Služby;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import static Služby.Main.workers;

public class AddSpecialRequestController {

    private Stage stage;
    private Request new_request;

    @FXML
    private ComboBox<Worker> request_for;
    @FXML private ComboBox<String> request_when;
    @FXML private ComboBox<Request.time_type> request_time;
    @FXML private ComboBox<String> request_until;

    Request show_add_request(Parent root)
    {

        for(Worker worker : workers)
            request_for.getItems().add(worker);


        request_for.setConverter(new StringConverter<Worker>() {
            @Override
            public String toString(Worker object) {
                return object.getName();
            }

            @Override
            public Worker fromString(String string) {
                return null;
            }
        });

        request_time.getItems().addAll(Request.time_type.MORNING, Request.time_type.AFTERNOON, Request.time_type.EVENING, Request.time_type.DAY, Request.time_type.NIGHT, Request.time_type.DAY_AND_NIGHT);

        request_time.setConverter(new StringConverter<Request.time_type>() {
            @Override
            public String toString(Request.time_type object) {
                switch (object)
                {
                    case MORNING:
                        return "Dopoledne";
                    case AFTERNOON:
                        return "Odpoledne";
                    case EVENING:
                        return "Večer";
                    case DAY:
                        return "Celý den";
                    case NIGHT:
                        return "Celou noc";
                    case DAY_AND_NIGHT:
                        return "Den i noc";
                }
                return null;
            }

            @Override
            public Request.time_type fromString(String string) {
                return null;
            }
        });
        stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
        return new_request;
    }

    @FXML
    private void add_request_confirm()
    {
        Worker sel = request_for.getSelectionModel().getSelectedItem();
        Request request = new Request(request_when.getSelectionModel().getSelectedIndex(), request_time.getSelectionModel().getSelectedItem(), request_until.getSelectionModel().getSelectedIndex(), sel.getName());
        request.setDay_string(request_when.getSelectionModel().getSelectedItem());
        request.setValidity(request_until.getSelectionModel().getSelectedItem());
        for(Worker worker : workers)
        {
            if(worker.equals(sel))
                worker.add_request(request);
        }
        new_request = request;
        Main.save_needed = true;
        stage.close();
    }
}
