package Služby;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static Služby.Main.workers;

public class PopUpWindowsController {

    @FXML private TextField arrival_date;
    @FXML private CheckBox RHB_week_one;
    @FXML private CheckBox RHB_week_two;
    @FXML private CheckBox RHB_week_three;
    @FXML private CheckBox add_shift;
    @FXML private Text add_shift_text;
    @FXML private TextField add_shift_field;

    private int month_index;
    private boolean turnus_change;
    private int turnus_start;
    private Stage stage;
    private ArrayList<Integer> chosen_weeks;
    private List<List<Shift>> all_chosen_shifts;
    private int chosen_year;
    private ObservableList<Request> observable_list_requests;
    private boolean new_request;

    private ObservableList<Worker> observable_list_workers;
    private boolean bil_changed;

    public void initialize(){
        month_index = -1;
        turnus_change = false;
        turnus_start = -1;
        chosen_weeks = null;
        all_chosen_shifts = null;
        bil_changed = false;

    }
    int show_choose_month(Parent root)
    {
        show_stage(root);
        return month_index;
    }

    boolean show_change_turnus_start_shifts(Parent root)
    {
        show_stage(root);
        return turnus_change;
    }

    int show_new_arrival_date(Parent root)
    {
        show_stage(root);
        return turnus_start;
    }

    List<Integer> show_RHB_weeks(Parent root)
    {
        show_stage(root);
        return chosen_weeks;
    }

    List<List<Shift>> show_set_shifts(Parent root)
    {
        show_stage(root);
        return all_chosen_shifts;
    }

    int show_set_year(Parent root)
    {
        show_stage(root);
        return chosen_year;
    }

    @FXML private TableView<Request> special_requests_table;
    @FXML private TableColumn<Request, String> requests_name;
    @FXML private TableColumn<Request, String> requests_day;
    @FXML private TableColumn<Request, String> requests_time;
    @FXML private TableColumn<Request, String> requests_validity;


    boolean show_special_requests(Parent root)
    {
        new_request = false;
        observable_list_requests = FXCollections.observableArrayList(new ArrayList<>());
        for (Worker worker : workers)
        {
            for (int i = 0; i < worker.get_request_count(); i++)
                observable_list_requests.add(worker.get_request(i));
        }
        special_requests_table.setItems(observable_list_requests);

        requests_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        requests_day.setCellValueFactory(new PropertyValueFactory<>("day_string"));
        requests_time.setCellValueFactory(new PropertyValueFactory<>("time"));
        requests_validity.setCellValueFactory(new PropertyValueFactory<>("validity"));
        System.out.println(observable_list_requests);
        show_stage(root);
        return new_request;
    }

    @FXML private TableView<Worker> workers_bilantion_table;
    @FXML private TableColumn<Worker, String> worker_name;
    @FXML private TableColumn<Worker, String> worker_bil_january;
    @FXML private TableColumn<Worker, String> worker_bil_february;
    @FXML private TableColumn<Worker, String> worker_bil_march;
    @FXML private TableColumn<Worker, String> worker_bil_april;

    @FXML private void change_bil_january(TableColumn.CellEditEvent edited_cel)
    {
        bil_changed = true;
        Worker selected = workers_bilantion_table.getSelectionModel().getSelectedItem();
        selected.find_month(0).setCurrent_work_hours_bilantion(Double.parseDouble(edited_cel.getNewValue().toString()));
    }

    @FXML private void change_bil_april(TableColumn.CellEditEvent edited_cel)
    {
        bil_changed = true;
        Worker selected = workers_bilantion_table.getSelectionModel().getSelectedItem();
        selected.find_month(3).setCurrent_work_hours_bilantion(Double.parseDouble(edited_cel.getNewValue().toString()));
    }



    boolean show_workers_bil(Parent root)
    {
        observable_list_workers = FXCollections.observableArrayList(new ArrayList<>());
        observable_list_workers.addAll(workers);
        workers_bilantion_table.setItems(observable_list_workers);
        worker_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        worker_bil_january.setCellValueFactory(bil_jan -> new SimpleStringProperty(String.format("%.1f",bil_jan.getValue().find_month_add_if_doesnt_exists(0).getCurrent_work_hours_bilantion())));
        worker_bil_february.setCellValueFactory(bil_jan -> new SimpleStringProperty(String.format("%.1f",bil_jan.getValue().find_month_add_if_doesnt_exists(1).getCurrent_work_hours_bilantion())));
        worker_bil_march.setCellValueFactory(bil_jan -> new SimpleStringProperty(String.format("%.1f",bil_jan.getValue().find_month_add_if_doesnt_exists(2).getCurrent_work_hours_bilantion())));
        worker_bil_april.setCellValueFactory(bil_jan -> new SimpleStringProperty(String.format("%.1f",bil_jan.getValue().find_month_add_if_doesnt_exists(3).getCurrent_work_hours_bilantion())));

        worker_bil_january.setCellFactory(TextFieldTableCell.forTableColumn());
        worker_bil_april.setCellFactory(TextFieldTableCell.forTableColumn());

        show_stage(root);
        if(bil_changed)
            Main.save_needed = true;

        return bil_changed;
    }

    private void show_stage(Parent root)
    {
        stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
    }



    @FXML
    private void add_request() throws IOException {
        System.out.println(observable_list_requests);
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("fxml_files/AddSpecialRequestWindow.fxml").openStream());
        AddSpecialRequestController popUp_controller = fxmlLoader.getController();
        Request request = popUp_controller.show_add_request(root);
        if(request != null)
        {
            new_request = true;
            observable_list_requests.add(request);
        }

    }

    @FXML
    private void delete_request()
    {
        Request to_delete = special_requests_table.getSelectionModel().getSelectedItem();
        if(to_delete == null)
            return;
        for(Worker worker : workers)
        {
            if(worker.getName().equals(to_delete.getName()))
            {
                for(Request request_w : worker.getRequests())
                {
                    if(to_delete.equals(request_w, false))
                    {
                        worker.deleteRequests(to_delete);
                        observable_list_requests.remove(to_delete);
                        Main.save_needed = true;
                        break;
                    }
                }
                break;
            }
        }
    }



    @FXML private TextField set_year_field;

    @FXML
    private void choose_year_confirm()
    {
        String chosen_year_str = set_year_field.getText();
        if(chosen_year_str.matches("^20[0-9][0-9]$"))
        {
            chosen_year = Integer.parseInt(chosen_year_str);
            stage.close();
        }
        else
        {
            set_year_field.setStyle("-fx-background-color: lightcoral");
        }
    }

    @FXML
    private void choose_year_storno()
    {
        stage.close();
    }

    @FXML Rectangle choose_january;
    @FXML Rectangle choose_february;
    @FXML Rectangle choose_march;
    @FXML Rectangle choose_april;
    @FXML Rectangle choose_may;
    @FXML Rectangle choose_june;
    @FXML Rectangle choose_july;
    @FXML Rectangle choose_august;
    @FXML Rectangle choose_september;
    @FXML Rectangle choose_october;
    @FXML Rectangle choose_november;
    @FXML Rectangle choose_december;



    @FXML
    private void choose_month(Event e)
    {
        Rectangle rec = (Rectangle) e.getSource();

        if(rec == choose_january)
            month_index = 0;
        else if(rec == choose_february)
            month_index = 1;
        else if(rec == choose_march)
            month_index = 2;
        else if(rec == choose_april)
            month_index = 3;
        else if(rec == choose_may)
            month_index = 4;
        else if(rec == choose_june)
            month_index = 5;
        else if(rec == choose_july)
            month_index = 6;
        else if(rec == choose_august)
            month_index = 7;
        else if(rec == choose_september)
            month_index = 8;
        else if(rec == choose_october)
            month_index = 9;
        else if(rec == choose_november)
            month_index = 10;
        else if(rec == choose_december)
            month_index = 11;

        stage.close();
    }



    @FXML
    private void changeTurnusYes()
    {
        turnus_change = true;
        stage.close();
    }

    @FXML
    private void changeTurnusNo()
    {
        turnus_change = false;
        stage.close();
    }

    @FXML
    private void arrival_date_confirm()
    {
        turnus_start = Integer.parseInt(arrival_date.getText());
        stage.close();
    }

    @FXML
    private void RHB_weeks_confirm()
    {
        chosen_weeks = new ArrayList<>();

        if(RHB_week_one.isSelected())
            chosen_weeks.add(1);
        if(RHB_week_two.isSelected())
            chosen_weeks.add(2);
        if(RHB_week_three.isSelected())
            chosen_weeks.add(3);
        stage.close();
    }

    @FXML
    private void allow_add_shift()
    {

        if(add_shift.isSelected())
        {
            add_shift_field.setDisable(false);
            add_shift_text.setOpacity(1.0);
        } else
        {
            add_shift_field.setDisable(true);
            add_shift_text.setOpacity(0.25);
        }
    }

    @FXML CheckBox weekday_D1;
    @FXML CheckBox weekday_D2;
    @FXML CheckBox weekday_O12;
    @FXML CheckBox weekday_O13;
    @FXML CheckBox weekday_O19;
    @FXML CheckBox weekday_O21;

    @FXML CheckBox weekday_D1b;
    @FXML CheckBox weekday_D2b;
    @FXML CheckBox weekday_O12b;
    @FXML CheckBox weekday_O13b;
    @FXML CheckBox weekday_O19b;
    @FXML CheckBox weekday_O21b;

    @FXML CheckBox weekend_D1;
    @FXML CheckBox weekend_D2;
    @FXML CheckBox weekend_O12;
    @FXML CheckBox weekend_O13;
    @FXML CheckBox weekend_O19;
    @FXML CheckBox weekend_O21;

    @FXML CheckBox weekend_D1b;
    @FXML CheckBox weekend_D2b;
    @FXML CheckBox weekend_O12b;
    @FXML CheckBox weekend_O13b;
    @FXML CheckBox weekend_O19b;
    @FXML CheckBox weekend_O21b;


    @FXML
    private void SetShifts_confirm()
    {
        all_chosen_shifts = new ArrayList<>(2);
        List<Shift> chosen_shifts = new ArrayList<>();
        List<Shift> chosen_shifts_weekend = new ArrayList<>();

        add_shifts(chosen_shifts, weekday_D1, weekday_D2, weekday_O12, weekday_O13, weekday_O19, weekday_O21);
        add_shifts(chosen_shifts, weekday_D1b, weekday_D2b, weekday_O12b, weekday_O13b, weekday_O19b, weekday_O21b);
        add_shifts(chosen_shifts_weekend, weekend_D1, weekend_D2, weekend_O12, weekend_O13, weekend_O19, weekend_O21);
        add_shifts(chosen_shifts_weekend, weekend_D1b, weekend_D2b, weekend_O12b, weekend_O13b, weekend_O19b, weekend_O21b);

        all_chosen_shifts.add(chosen_shifts);
        all_chosen_shifts.add(chosen_shifts_weekend);
        stage.close();
    }

    private void add_shifts(List<Shift> chosen_shifts, CheckBox weekday_d1, CheckBox weekday_d2, CheckBox weekday_o12, CheckBox weekday_o13, CheckBox weekday_o19, CheckBox weekday_o21) {
        if (weekday_d1.isSelected())
            chosen_shifts.add(new Shift(Shift.type.D1, 0, false));
        if (weekday_d2.isSelected())
            chosen_shifts.add(new Shift(Shift.type.D2, 0, false));
        if (weekday_o12.isSelected())
            chosen_shifts.add(new Shift(Shift.type.O12, 2, false));
        if (weekday_o13.isSelected())
            chosen_shifts.add(new Shift(Shift.type.O13, 2, false));
        if (weekday_o19.isSelected())
            chosen_shifts.add(new Shift(Shift.type.O19, 2, false));
        if (weekday_o21.isSelected())
            chosen_shifts.add(new Shift(Shift.type.O21, 2, false));
    }
}
