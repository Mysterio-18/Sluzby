package Služby;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static Služby.Main.*;


public class DynamicGUIController {

    private int cell_width = 35;
    private int cell_height = 30;
    private int number_of_days;
    private int number_of_workers;
    private Text[][] worker_lines;
    private Label[] worker_names;
    private FXMLLoader fxmlLoader;
    private PopUpWindowsController popUp_controller;
    private Rectangle[][] grid_fill;
    private boolean work_plan_changed;
    private GridPane main_grid;

    @FXML
    private Button year_button;

    @FXML
    private ScrollPane main_scroll;

    @FXML
    private Button save_button;

    public void initialize(){
        int current_year = LocalDate.now().getYear();
        year_button.setText(Integer.toString(current_year));
        chosen_year = current_year;
        work_plan_changed = false;
    }

    private void init_date()
    {
        main_grid = new GridPane();
        main_scroll.setContent(main_grid);

        int chosen_year =  Integer.parseInt(year_button.getText());
        number_of_days = Main.days_in_month(month_index, chosen_year);

        Rectangle head = new Rectangle(150,30, Color.WHITE);
        head.setStroke(Color.BLACK);
        head.setStrokeType(StrokeType.INSIDE);
        main_grid.add(head, 0, 0);

        Text text = new Text("Jméno zaměstnance");
        GridPane.setHalignment(text, HPos.CENTER);
        main_grid.add(text, 0, 0);

        for (int i = 0; i < number_of_days; i++)
        {
            Rectangle date_fill = new Rectangle(cell_width, cell_height);

            Text date_number = new Text(Integer.toString(i + 1));
            GridPane.setHalignment(date_number, HPos.CENTER);
            date_fill.setStroke(Color.BLACK);
            date_fill.setStrokeType(StrokeType.INSIDE);

            date_fill.setFill(Color.WHITE);
            main_grid.add(date_fill, i + 1, 0);
            main_grid.add(date_number, i + 1, 0);
        }
    }

    private void init_worker_lines(boolean shifts, boolean plan, boolean holiday)
    {
        worker_names = create_labels_from_names();
        Rectangle[] worker_names_fill = new Rectangle[number_of_workers];

        grid_fill = new Rectangle[number_of_workers][number_of_days+5];
        worker_lines = new Text[number_of_workers][number_of_days+5];

        int day_of_week = work_plans.get(month_index).first_day_of_month;

        boolean weekend;

        for(int i = 0; i< number_of_workers; i++)
        {
            int temp = day_of_week;

            worker_names_fill[i] = new Rectangle(150, 30);
            worker_names_fill[i].setStroke(Color.BLACK);

            if (i % 2 == 0)
                worker_names_fill[i].setFill(Color.LAVENDERBLUSH);
            else
                worker_names_fill[i].setFill(Color.WHITE);

            main_grid.add(worker_names_fill[i], 0, i + 1);
            main_grid.add(worker_names[i], 0, i + 1);

            int number_of_cells;
            if(shifts)
                number_of_cells = number_of_days + 5;
            else if(plan)
                number_of_cells = number_of_days + 1;
            else
                number_of_cells = number_of_days + 2;

            for(int j = 0; j < number_of_cells; j++)
            {
                weekend = Work_plan.is_weekend(temp);


                worker_lines[i][j] = new Text();
                worker_lines[i][j].setMouseTransparent(true);

                if(j >= number_of_days)
                {
                    weekend = false;
                    grid_fill[i][j] = new Rectangle(cell_width*2, cell_height);
                }
                else
                    grid_fill[i][j] = new Rectangle(cell_width, cell_height);

                grid_fill[i][j].setStroke(Color.BLACK);
                grid_fill[i][j].setStrokeType(StrokeType.INSIDE);

                if (i % 2 == 0)
                    grid_fill[i][j].setFill(Color.LAVENDERBLUSH);
                else
                    grid_fill[i][j].setFill(Color.WHITE);


                if(weekend)
                {
                    Rectangle dark_cell = new Rectangle(cell_width, cell_height, Color.BLACK);
                    dark_cell.setStroke(Color.BLACK);
                    dark_cell.setStrokeType(StrokeType.INSIDE);
                    dark_cell.setMouseTransparent(true);

                    grid_fill[i][j].setOpacity(0.9);
                    main_grid.add(dark_cell, j + 1, i + 1);
                }

                main_grid.add(grid_fill[i][j], j + 1, i + 1);
                GridPane.setHalignment(worker_lines[i][j], HPos.CENTER);
                main_grid.add(worker_lines[i][j], j+1,i+1);

                temp++;
                if(temp > 7)
                    temp = 1;
            }
        }
    }

    private void load_written_shifts()
    {
        Rectangle hours_worked_fill = new Rectangle(2*cell_width, cell_height);
        Rectangle hours_bil_fill = new Rectangle(2*cell_width, cell_height);
        Rectangle hours_bil_sum_fill = new Rectangle(2*cell_width, cell_height);
        Rectangle nights_YP_fill = new Rectangle(2*cell_width, cell_height);
        Rectangle nights_worked_fill = new Rectangle(2*cell_width, cell_height);

        Label hours_worked_text = new Label("Odpracováno\ntento měsíc");
        Label hours_bil_text = new Label("Bilance\ntento měsíc");
        Label hours_bil_sum_text = new Label("Bilance\ncelkově");
        Label nights_YP_text = new Label("Počet nočních\nv RP");
        Label nights_worked_text = new Label("Počet nočních\nskutečně");

        init_hours_head(hours_worked_fill, hours_worked_text, number_of_days+1);
        init_hours_head(hours_bil_fill, hours_bil_text, number_of_days+2);
        init_hours_head(hours_bil_sum_fill, hours_bil_sum_text, number_of_days+3);
        init_hours_head(nights_YP_fill, nights_YP_text, number_of_days+4);
        init_hours_head(nights_worked_fill, nights_worked_text, number_of_days+5);

        init_worker_lines(true, false, false);

        for(int i = 0; i<number_of_workers; i++)
        {
            String current_worker = worker_names[i].getText();
            for(Worker worker : workers)
            {
                Month month = worker.get_month_add_if_doesnt_exist();
                if(worker.getName().equals(current_worker))
                {
                    for(int j = 0; j < number_of_days; j++)
                    {
                        if(month.actual_work_plan[j] == null)
                            worker_lines[i][j].setText("");
                        else
                        {
                            switch (month.actual_work_plan[j].typ)
                            {
                                case D1:
                                    make_rectangle_day(month.actual_work_plan[j].ord, month.year_plan[j], i, j);
                                    worker_lines[i][j].setText("d1");
                                    break;
                                case D2:
                                    make_rectangle_day(month.actual_work_plan[j].ord, month.year_plan[j], i, j);
                                    worker_lines[i][j].setText("d2");
                                    break;
                                case O12:
                                    make_rectangle_day(month.actual_work_plan[j].ord, month.year_plan[j], i, j);
                                    worker_lines[i][j].setText("o12");
                                    break;
                                case O13:
                                    worker_lines[i][j].setText("o13");
                                    break;
                                case O19:
                                    worker_lines[i][j].setText("o19");
                                    break;
                                case O20:
                                    worker_lines[i][j].setText("o20");
                                    break;
                                case O21:
                                    worker_lines[i][j].setText("o21");
                                    break;
                                case N1L:
                                    make_rectangle_night(month.year_plan[j], i, j);
                                    worker_lines[i][j].setText("n1l");
                                    break;
                                case N1S:
                                    make_rectangle_night(month.year_plan[j], i, j);
                                    worker_lines[i][j].setText("n1s");
                                    break;
                                case N2:
                                    make_rectangle_night(month.year_plan[j], i, j);
                                    worker_lines[i][j].setText("n2");
                                    break;
                                case RHB_l:
                                    worker_lines[i][j].setText("RHBl");
                                    break;
                                case RHB_s:
                                    worker_lines[i][j].setText("RHBs");
                                    break;
                                default:
                                    worker_lines[i][j].setText("XX");
                                    break;
                            }
                        }
                    }

                    double hours_worked = month.hours_worked;
                    double month_bil = hours_worked - worker.hours_per_month;

                    worker_lines[i][number_of_days].setText(String.valueOf(hours_worked));
                    worker_lines[i][number_of_days+1].setText(String.format("%.1f", month_bil));
                    worker_lines[i][number_of_days+2].setText(String.format("%.1f", month.getCurrent_work_hours_bilantion()));
                    worker_lines[i][number_of_days+3].setText(Integer.toString(month.get_nights_in_YP()));
                    worker_lines[i][number_of_days+4].setText(Integer.toString(month.nights_worked));
                }
            }
        }
    }

    private void load_year_plan()
    {
        Rectangle hours_year_plan_fill = new Rectangle(2*cell_width, cell_height);

        Label hours_year_plan_text = new Label("~Roční plán\ntento měsíc");

        init_hours_head(hours_year_plan_fill, hours_year_plan_text, number_of_days+1);

        init_worker_lines(false, true, false);
        for(int j = 0; j<number_of_workers;j++)
        {
            String current_worker = worker_names[j].getText();
            for (Worker worker : workers)
            {
                Month month = worker.get_month_add_if_doesnt_exist();
                if (worker.getName().equals(current_worker))
                {
                    for (int i = 0; i < number_of_days; i++)
                    {

                        if (month.year_plan[i] == Shift.type.D1)
                            worker_lines[j][i].setText("d1");
                        else if (month.year_plan[i] == Shift.type.O19)
                            worker_lines[j][i].setText("o19");
                        else if (month.year_plan[i] == Shift.type.N1L)
                            worker_lines[j][i].setText("n");
                        else
                            worker_lines[j][i].setText("");
                    }
                    worker_lines[j][number_of_days].setText(String.valueOf(month.year_plan_month_sum()));
                }
            }
        }
    }

    private void load_holidays()
    {
        Rectangle proper_holiday_fill = new Rectangle(2*cell_width, cell_height);
        Rectangle day_off_fill = new Rectangle(2*cell_width, cell_height);

        Label proper_holiday_text = new Label("~ŘD\ntento měsíc");
        Label day_off_text = new Label("Počet /\nv kolizi s RP");

        init_hours_head(proper_holiday_fill, proper_holiday_text, number_of_days+1);
        init_hours_head(day_off_fill, day_off_text, number_of_days+2);

        init_worker_lines(false, false, true);
        for(int j = 0; j<number_of_workers;j++)
        {
            String current_worker = worker_names[j].getText();
            for (Worker worker : workers)
            {
                Month month = worker.get_month_add_if_doesnt_exist();
                if (worker.getName().equals(current_worker))
                {
                    for (int i = 0; i < number_of_days; i++)
                    {
                        if (month.holiday[i] == 0)
                            worker_lines[j][i].setText("");
                        else if (month.holiday[i] == 1)
                            worker_lines[j][i].setText("/");
                        else
                            worker_lines[j][i].setText("ŘD");
                    }
                    worker_lines[j][number_of_days].setText(String.valueOf(month.proper_holiday_month_sum()));
                    worker_lines[j][number_of_days+1].setText(Integer.toString(month.day_off_month_count()));
                }
            }
        }
    }

    private Label[] create_labels_from_names()
    {
        number_of_workers = workers.size();

        Label[] worker_names = new Label[number_of_workers];

        String[] worker_array = new String[number_of_workers];

        for(int i = 0;i<number_of_workers;i++)
        {
            worker_array[i] = workers.get(i).getName();
        }
        Arrays.sort(worker_array);
        for(int i = 0;i< number_of_workers;i++)
        {
            worker_names[i] = new Label(worker_array[i]);
            worker_names[i].setPadding(new Insets(0,0,0,5));
            GridPane.setHalignment(worker_names[i], HPos.LEFT);
        }
        return worker_names;
    }

    private void init_hours_head(Rectangle fill, Label text, int i)
    {
        text.setMaxSize(2*cell_width, cell_height);
        text.setFont(new Font(10));
        text.setTextAlignment(TextAlignment.CENTER);
        text.setAlignment(Pos.CENTER);

        fill.setFill(Color.WHITE);
        fill.setStroke(Color.BLACK);
        GridPane.setHalignment(text, HPos.CENTER);
        GridPane.setValignment(text, VPos.CENTER);

        main_grid.add(fill, i, 0);
        main_grid.add(text, i,0);
    }

    private void make_rectangle_day(boolean ord, Shift.type type,  int i, int j)
    {
        Rectangle rec = new Rectangle(cell_width, cell_height);
        rec.setOpacity(0.5);
        rec.setMouseTransparent(true);
        if(type != null)
            return;
        if(ord)
            rec.setFill(Color.GREEN);
        else
            rec.setFill(Color.PURPLE);

        main_grid.add(rec, j+1,i+1);
    }

    private void make_rectangle_night(Shift.type type, int i, int j)
    {
        Rectangle rec = new Rectangle(cell_width, cell_height);
        rec.setOpacity(0.5);
        rec.setMouseTransparent(true);
        if(type == null)
            rec.setFill(Color.RED);
        else
            return;
        main_grid.add(rec, j+1,i+1);
    }

    @FXML
    private void write_shifts() throws IOException {

        //uživatel vybere měsíc, pro který chce plán nastavit
        month_index = choose_month();
        if(month_index == -1)
            return;

        Work_plan cur_work_plan = get_work_plan(month_index); //nalezneme pracovní plán (work_plan) na zadaný měsíc
        if(cur_work_plan == null)
        {
            Pop_Up.warning("NENÍ Work_plan pro daný měsíc");
            return;
        }

        boolean change = true;
        if(cur_work_plan.plan_done)
            change = want_to_change_turnus_start_and_shifts();
        //pokud již byl plán jednou inicializován, ptáme se uživatele zda chce plán nastavit znovu

        if(change)
        {
            if(init_work_plan(cur_work_plan)) //inicializace na tento měsíc byla v pořádku dokončena, je tedy třeba povolit ukládání
            {
                System.out.println("INIT THIS MONTH PLAN IS OK");
                cur_work_plan.plan_done = true;
                save_button.setDisable(false);
                save_needed = true;
            }
            else
            {
                Pop_Up.warning("PLÁN NA TENTO MĚSÍC NEBYL INICIALIZOVÁN");
                return;
            }
        }

        if(month_index == 0) //pokud jsme v lednu, plán na předchozí měsíc roku nemůže existovat, posíláme prázdný plán na minulý měsíc
            cur_work_plan.calculate_work_plan(cur_work_plan.turnus_start, null, null, null);
        else
        {
            Work_plan last_month_work_plan = work_plans.get(month_index - 1);
            if(!cur_work_plan.was_last_month_plan_init(last_month_work_plan)) //pokud plán na minulý měsíc nebyl inicializován, musíme ho inicializovat dodatečně
            {
                Pop_Up.warning("Je třeba donastavit informace z předchozího turnusu, pro kalkulaci služeb na první polovinu měsíce");
                if(init_work_plan(last_month_work_plan)) //pokud byla inicializace plánu minulý měsíc přerušena předčasně, je třeba ukončit kalkulaci plánu na tento měsíc
                    last_month_work_plan.plan_done = true;
                else
                    return;
            }
            cur_work_plan.calculate_work_plan(cur_work_plan.turnus_start, last_month_work_plan.chosen_shifts.get(0), last_month_work_plan.chosen_shifts.get(1), last_month_work_plan.rhb_weeks);
        }

        work_plan_changed = true;
        init_date();
        load_written_shifts();
    }

    private boolean init_work_plan(Work_plan cur_work_plan) throws IOException
    {
        int turnus_start = new_arrival_date(); //nabídne uživateli pole na vyplnění čísla, které určí začátek turnusu
        if(turnus_start == -1)
            return false;

        List<Integer> rhb_weeks = choose_rhb_weeks(); //uživatel zaškrtne okénka, která určí týdny rehabilitace
        if(rhb_weeks == null)
            return false;

        List<List<Shift>> chosen_shifts = set_shifts_window(); //uživatel zaškrtne okénka, která určí potřebné směny
        if(chosen_shifts == null)
            return false;

        cur_work_plan.turnus_start = turnus_start;
        cur_work_plan.rhb_weeks = rhb_weeks;
        cur_work_plan.chosen_shifts = chosen_shifts;
        //vše výše uvedené se uloží do pracovního plánu(work_plan), ukládáme až na konci společně, abychom zamezili
        //zbytečnému zapisování do pracovního plánu, pokud je proces přerušen před dokončením

        return true; //pokud vše proběhlo v pořádku vracíme true
    }

    //zobrazí zadané a vykalkulované směny na daný měsíc a dovolí je ručně měnit
    @FXML
    void display_shifts() throws IOException {
        month_index = choose_month();
        if(month_index == -1)
            return;
        init_date();
        load_written_shifts(); //načte veškeré již zadané směny a vyplní je do tabulky

        //handler pro změnu směn
        EventHandler<MouseEvent> event_handler = event -> {
            Object source = event.getSource();

            //najdu kam uživatel kliknul
            for (int i = 0; i < number_of_workers; i++)
            {
                for (int j = 0; j < number_of_days; j++)
                {
                    if(grid_fill[i][j] == source)
                    {
                        //pokud není povoleno ukládat, tak povolím uložení, protože nastala změna
                        if(save_button.isDisabled())
                        {
                            save_button.setDisable(false);
                            save_needed = true;
                        }
                        //najdu, kterému pracovníkovi měníme směnu
                        for (Worker worker : workers)
                        {
                            if(worker.getName().equals(worker_names[i].getText()))
                            {
                                String next_shift;
                                //zapíše směnu do mezi zadané směny
                                Work_plan plan = get_work_plan(worker.getMonth().month_index);
                                if(plan == null)
                                    Pop_Up.warning("JE TU PROBLEM RUČNĚ VYPLNĚNÉ SMĚNY");
                                else
                                {
                                    next_shift = worker.getMonth().change_solved_shifts(j, plan.get_day_seq_number(j));
                                    //změní text v buňce, aby uživatel viděl, co nastavil
                                    cell_text_change(next_shift, i, j);
                                }
                            }
                        }
                    }
                }
            }
        };
        for(int i = 0; i<workers.size();i++)
        {
            for(int j = 0; j<number_of_days; j++)
            {
                grid_fill[i][j].setOnMouseClicked(event_handler);
            }
        }
    }

    @FXML
    private void set_year_plan() throws IOException {

        month_index = choose_month();
        if(month_index == -1)
            return;
        init_date();
        load_year_plan();
        EventHandler<MouseEvent> event_handler = event -> {

            Object source = event.getSource();
            for(int i = 0; i< number_of_workers; i++)
            {
                for(int j = 0; j< number_of_days; j++)
                {
                    if(grid_fill[i][j] == source)
                    {
                        save_button.setDisable(false);
                        save_needed = true;
                        for(Worker worker : workers)
                        {
                            if(worker.getName().equals(worker_names[i].getText()))
                            {
                                String next_shift;
                                Object button_clicked = event.getButton();
                                if(button_clicked == MouseButton.SECONDARY)
                                    next_shift = worker.getMonth().change_day_back_year_plan(j);
                                else if(button_clicked == MouseButton.MIDDLE)
                                    next_shift = worker.getMonth().change_day_reset_year_plan(j);
                                else
                                    next_shift = worker.getMonth().change_day_year_plan(j);
                                cell_text_change(next_shift, i, j);
                            }
                        }
                    }
                }
            }
        };

        for(int i = 0; i<workers.size();i++)
        {
            for(int j = 0; j<number_of_days; j++)
            {
                grid_fill[i][j].setOnMouseClicked(event_handler);
            }
        }
    }

    @FXML
    private void set_free_days() throws IOException
    {
        month_index = choose_month();
        if(month_index == -1)
            return;
        init_date();
        load_holidays();

        EventHandler<MouseEvent> event_handler = event -> {

            Object source = event.getSource();

            for (int i = 0; i < number_of_workers; i++)
            {
                for (int j = 0; j < number_of_days; j++)
                {
                    if (grid_fill[i][j] == source)
                    {
                        save_button.setDisable(false);
                        save_needed = true;
                        for (Worker worker : workers)
                        {
                            if (worker.getName().equals(worker_names[i].getText()))
                            {
                                String next_holiday;
                                Object button_clicked = event.getButton();
                                if(button_clicked == MouseButton.SECONDARY)
                                    next_holiday = worker.getMonth().change_back_holiday(j);
                                else if(button_clicked == MouseButton.MIDDLE)
                                    next_holiday = worker.getMonth().change_reset_holiday(j);
                                else
                                    next_holiday = worker.getMonth().change_holiday(j);
                                cell_text_change(next_holiday, i, j);
                            }
                        }
                    }
                }
            }
        };

        for(int i = 0; i<workers.size();i++)
        {
            for(int j = 0; j<number_of_days; j++)
            {
                grid_fill[i][j].setOnMouseClicked(event_handler);
            }
        }

    }

    private void cell_text_change(String text, int row, int column)
    {
        worker_lines[row][column].setText(text);
    }

    @FXML
    private void set_year() throws IOException {
        fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("fxml_files/ChooseYearWindow.fxml").openStream());
        popUp_controller = fxmlLoader.getController();
        chosen_year = popUp_controller.show_set_year(root);
        year_button.setText(Integer.toString(chosen_year));
    }

    @FXML
    private void manage_requests() throws IOException {
        fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("fxml_files/ManageSpecialRequestsWindow.fxml").openStream());
        popUp_controller = fxmlLoader.getController();

        boolean new_request = popUp_controller.show_special_requests(root);
        if(new_request)
            save_button.setDisable(false);
    }

    @FXML
    private void set_bil() throws IOException {
        fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("fxml_files/ManageWorkersBilWindow.fxml").openStream());
        popUp_controller = fxmlLoader.getController();
        boolean bil_changed = popUp_controller.show_workers_bil(root);
        if(bil_changed)
            save_button.setDisable(false);
    }

    @FXML
    void save_changes()
    {
        try
        {
            Main.save_file.save_workers();
            if(work_plan_changed)
            {
                Main.save_file.save_work_plans();
                work_plan_changed = false;
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        save_button.setDisable(true);
    }

    private Work_plan get_work_plan(int month_index)
    {
        for(Work_plan plan : work_plans)
        {
            if(plan.month_index == month_index)
                return plan;
        }
        return null;
    }

    private int choose_month() throws IOException
    {
        fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("fxml_files/ChooseMonthWindow.fxml").openStream());
        popUp_controller = fxmlLoader.getController();
        return popUp_controller.show_choose_month(root);
    }

    private boolean want_to_change_turnus_start_and_shifts() throws IOException {
        fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("fxml_files/ChangeTurnusStartShiftsWindow.fxml").openStream());
        popUp_controller = fxmlLoader.getController();
        return popUp_controller.show_change_turnus_start_shifts(root);
    }

    private int new_arrival_date() throws IOException {
        fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("fxml_files/newArrivalDateWindow.fxml").openStream());
        popUp_controller = fxmlLoader.getController();
        return popUp_controller.show_new_arrival_date(root);
    }

    private List<Integer> choose_rhb_weeks() throws IOException {
        fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("fxml_files/ChooseRHBWeeksWindow.fxml").openStream());
        popUp_controller = fxmlLoader.getController();
        return popUp_controller.show_RHB_weeks(root);
    }

    private List<List<Shift>> set_shifts_window() throws IOException {
        fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("fxml_files/SetShiftsWindow.fxml").openStream());
        popUp_controller = fxmlLoader.getController();
        return popUp_controller.show_set_shifts(root);
    }
}
