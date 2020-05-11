package Služby;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


class Pop_Up {

    private static Stage stage;

    static void warning(String problem)
    {
        VBox warning_frame = new VBox(10);
        Text warning_text = new Text(problem);
        stage = new Stage();
        Button confirm = new Button();
        warning_frame.getChildren().addAll(warning_text, confirm);
        confirm.setText("OK");
        confirm.setOnAction(event -> stage.close());
        display_pop_up(new Scene(warning_frame));
    }

    /*static String backup_pop_up()
    {
        VBox backup = new VBox(10);

        Text back_head = new Text("Název souboru");
        TextArea back_area = new TextArea();
        stage = new Stage();
        confirm_set();
        backup.getChildren().addAll(back_head, back_area, confirm);
        display_pop_up(new Scene(backup));

        return back_area.getText();
    }
*/
    private static void display_pop_up(Scene scene)
    {
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setTitle("Služby Křetín");
        stage.showAndWait();
    }

    /*static void add_worker_window()
    {
        VBox add_worker_background = new VBox(10);
        add_worker_background.setPrefWidth(600);
        HBox name = new HBox(10);
        HBox position = new HBox(10);
        HBox options = new HBox(10);
        HBox uvazek = new HBox(10);
        HBox buttons = new HBox(10);
        Button confirm = new Button("Potvrdit");
        Button storno = new Button("Zrušit");
        add_worker_background.getChildren().addAll(name,position,options,uvazek,buttons);

        Text first_name_label = new Text("Jméno");
        TextField first_name_area = new TextField();

        Text last_name_label = new Text("Příjmení");
        TextField last_name_area = new TextField();

        name.getChildren().addAll(first_name_label, first_name_area, last_name_label, last_name_area);
        name.setAlignment(Pos.CENTER);


        Text position_label = new Text("Pozice");

        position.getChildren().addAll(position_label);
        position.setAlignment(Pos.CENTER);

        CheckBox position_zs = new CheckBox("Zdravotní sestra");
        CheckBox position_ps = new CheckBox("Praktická sestra");
        CheckBox position_v = new CheckBox("Vychovatelka");
        CheckBox position_o = new CheckBox("Ošetřovatelka");

        options.getChildren().addAll(position_zs, position_ps, position_v, position_o);
        options.setAlignment(Pos.CENTER);

        Text uvazek_label = new Text("Úvazek");
        TextField uvazek_area = new TextField();
        uvazek_area.setPromptText("Zadejte číslo");

        uvazek.getChildren().addAll(uvazek_label, uvazek_area);
        uvazek.setAlignment(Pos.CENTER);

        buttons.getChildren().addAll(confirm, storno);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Scene scene = new Scene(add_worker_background);
        Stage stage = new Stage();
        stage.setTitle("Nový zaměstnanec");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.showAndWait();
    }*/

    static void save_unsaved_changes(Save save)
    {
        VBox save_changes = new VBox(10);
        HBox buttons = new HBox(10);

        Label save_text = new Label("Chcete uložit provedené změny?");

        Button yes = new Button("Ano");
        Button no = new Button("Ne");

        buttons.getChildren().addAll(yes, no);
        save_changes.getChildren().addAll(save_text, buttons);
        stage = new Stage();

        yes.setOnAction(event -> {
            try
            {
                save.save_workers(); //TODO ULOZIT JEN ZMENENE
                save.save_work_plans();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            stage.close();
        });
        no.setOnAction(event -> stage.close());
    }



}