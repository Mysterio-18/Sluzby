package Služby;
/*
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.time.LocalDate;

import static Služby.Main.*;

class GUI {

    static HBox background;
    private static ScrollPane scroll_name_date;
    private static GridPane grid_name_date;
    private static VBox left_side;
    private static HBox top_line;
    private static HBox bottom_line;

    static Button write_shifts;
    static Button display_shifts;
    static Button set_year_plan;
    static Button set_free_days;
    static Button add_worker;
    static Button set_year;
    static Button save_changes;
    static Button backup;
    static Button manage_requests;

    private static Rectangle[] worker_names_fill;
    static Label[] worker_names;
    static Rectangle[][] date_fill;
    private static Text[][] worker_lines;
    static int number_of_days;
    static int number_of_workers;


    private static int grid_height;
    private static int grid_width;

    private static int cell_width;
    private static int cell_height;

    void init()
    {
        number_of_days = 0;

        background = new HBox(20);
        background.setPrefWidth(1366);
        background.setPrefHeight(728);

        String frame_border = "-fx-border-color: white;\n" + "-fx-border-width: 10;\n";
        background.setStyle(frame_border);
        left_side = new VBox(20);
        top_line = new HBox(20);
        bottom_line = new HBox(20);

        HBox.setHgrow(left_side,Priority.ALWAYS);
        background.getChildren().addAll(left_side);
    }

    void grid()
    {
        grid_height = 400;
        grid_width = 900;

        cell_width = 35;
        cell_height = 30;

        scroll_name_date = new ScrollPane();

        VBox.setVgrow(scroll_name_date, Priority.ALWAYS);
        scroll_name_date.setPrefSize(grid_width,grid_height);

        left_side.getChildren().addAll(top_line, scroll_name_date, bottom_line);
    }



    void settings()
    {
        write_shifts = new Button("Zapsat směny");
        top_line.getChildren().add(write_shifts);

        display_shifts = new Button("Zobrazit směny");
        top_line.getChildren().add(display_shifts);

        set_year_plan = new Button("Nastavit roční plán");
        top_line.getChildren().add(set_year_plan);

        set_free_days = new Button("Nastavit dovolenou");
        top_line.getChildren().add(set_free_days);

        add_worker = new Button("Přidat zaměstnance");
        top_line.getChildren().add(add_worker);

        manage_requests = new Button("Speciální požadavky");
        top_line.getChildren().add(manage_requests);

        LocalDate current_date = LocalDate.now();
        int current_year = current_date.getYear();
        set_year = new Button(Integer.toString(current_year));
        top_line.getChildren().add(set_year);

        chosen_year = current_year;

        backup = new Button("Záloha");
        top_line.getChildren().add(backup);

        save_changes = new Button("Uložit změny");
        bottom_line.getChildren().add(save_changes);
        bottom_line.setAlignment(Pos.CENTER_RIGHT);
        save_changes.setDisable(true);
    }



   /* static void init_year_table()
    {
        grid_name_date = new GridPane();
        scroll_name_date.setContent(grid_name_date);

        grid_name_date.setPrefSize(grid_width,grid_height);

        Rectangle headline = new Rectangle(150,30);
        headline.setStroke(Color.BLACK);
        headline.setFill(Color.WHITE);
        Text headline_name = new Text("Jméno zaměstnance");
        GridPane.setHalignment(headline_name, HPos.CENTER);


        grid_name_date.add(headline,0,0);
        grid_name_date.add(headline_name,0,0);


        worker_names = create_text_from_names();


        chosen_year =  Integer.parseInt(set_year.getText());
        number_of_days = days_in_month(month_index, chosen_year);
        worker_names_fill = new Rectangle[number_of_workers];

        date_fill = new Rectangle[number_of_workers][number_of_days+5];


        for(int i = 0; i < number_of_days; i++)
        {

            Rectangle date_fill = new Rectangle(cell_width, cell_height);
            int date = i+1;
            String date_s = Integer.toString(date);
            Text date_number = new Text(date_s);
            GridPane.setHalignment(date_number,HPos.CENTER);
            date_fill.setStroke(Color.BLACK);

            date_fill.setFill(Color.WHITE);
            grid_name_date.add(date_fill, i + 1, 0);
            grid_name_date.add(date_number, i + 1, 0);

        }
    }*/
/*
    private static void init_hours_head(Rectangle fill, Label text, int i)
    {
        text.setMaxSize(2*cell_width, cell_height);
        text.setFont(new Font(10));
        text.setTextAlignment(TextAlignment.CENTER);
        text.setAlignment(Pos.CENTER);

        fill.setFill(Color.WHITE);
        fill.setStroke(Color.BLACK);
        GridPane.setHalignment(text, HPos.CENTER);
        GridPane.setValignment(text, VPos.CENTER);

        grid_name_date.add(fill, i, 0);
        grid_name_date.add(text, i,0);
    }
*/
  /*
    // inicializuje jednotlivé řádky pro zaměstance, připraví na vyplnění textem
    private static void init_workers(boolean ready_hours, boolean ready_short, boolean ready_holiday)
    {
        int day_of_week = work_plans.get(month_index).first_day_of_month;
        int temp;
        boolean weekend;

        worker_lines = new Text[number_of_workers][number_of_days+5];

        for(int i = 0; i< number_of_workers; i++)
        {
            temp = day_of_week;
            worker_names_fill[i] = new Rectangle(150, 30);
            worker_names_fill[i].setStroke(Color.BLACK);

            if (i % 2 == 0)
                worker_names_fill[i].setFill(Color.LAVENDERBLUSH);
            else
                worker_names_fill[i].setFill(Color.WHITE);

            grid_name_date.add(worker_names_fill[i], 0, i + 1);
            grid_name_date.add(worker_names[i], 0, i + 1);

            int number_of_cells;
            if(ready_hours)
                number_of_cells = number_of_days+5;
            else if(ready_short)
                number_of_cells = number_of_days+1;
            else if(ready_holiday)
                number_of_cells = number_of_days+2;
            else
                number_of_cells = number_of_days;
            for(int j = 0; j < number_of_cells; j++)
            {
                weekend = Work_plan.is_weekend(temp);
                if(j >= number_of_days)
                    weekend = false;

                worker_lines[i][j] = new Text();
                worker_lines[i][j].setMouseTransparent(true);
                if(j<number_of_days)
                    date_fill[i][j] = new Rectangle(cell_width, cell_height);
                else
                    date_fill[i][j] = new Rectangle(cell_width*2, cell_height);
                date_fill[i][j].setStroke(Color.BLACK);

                if (i % 2 == 0)
                    date_fill[i][j].setFill(Color.LAVENDERBLUSH);
                else
                    date_fill[i][j].setFill(Color.WHITE);

                if(weekend)
                {
                    Rectangle dark_cell = new Rectangle(cell_width, cell_height, Color.BLACK);
                    dark_cell.setStroke(Color.BLACK);
                    dark_cell.setMouseTransparent(true);
                    //date_fill[i][j].setFill(Color.GRAY);
                    date_fill[i][j].setOpacity(0.9);
                    grid_name_date.add(dark_cell, j + 1, i + 1);
                }

                grid_name_date.add(date_fill[i][j], j + 1, i + 1);
                GridPane.setHalignment(worker_lines[i][j], HPos.CENTER);
                grid_name_date.add(worker_lines[i][j], j+1,i+1);

                temp++;
                if(temp > 7)
                    temp = 1;
            }

        }
    }
*/
    /*
    //pokud již bylo něco zapsáno vytáhne z paměti a vyplní
    static void init_letter_write_shifts()
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

        init_workers(true, false, false);

        for(int i = 0; i<number_of_workers; i++)
        {
            String current_worker = worker_names[i].getText();
            for(Worker worker : workers)
            {
                Month month = worker.get_month_add_if_doesnt_exist();
                if(worker.name.equals(current_worker))
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
                                    make_rectangle_day(month.actual_work_plan[j].ord, month.year_plan[j], worker.pozice, i, j);
                                    worker_lines[i][j].setText("d1");
                                    break;
                                case D2:
                                    make_rectangle_day(month.actual_work_plan[j].ord, month.year_plan[j], worker.pozice, i, j);
                                    worker_lines[i][j].setText("d2");
                                    break;
                                case O12:
                                    make_rectangle_day(month.actual_work_plan[j].ord, month.year_plan[j], worker.pozice, i, j);
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
                                case RHB_s:
                                    worker_lines[i][j].setText("RHB");
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
                    worker_lines[i][number_of_days+2].setText(String.format("%.1f", month.current_work_hours_bilantion));
                    worker_lines[i][number_of_days+3].setText(Integer.toString(month.get_nights_in_YP()));
                    worker_lines[i][number_of_days+4].setText(Integer.toString(month.nights_worked));
                }
            }
        }
    }*/
/*
    //pokud již bylo něco zapsáno vytáhne z paměti a vyplní
    static void init_letter_year_plan()
    {
        Rectangle hours_year_plan_fill = new Rectangle(2*cell_width, cell_height);

        Label hours_year_plan_text = new Label("~Roční plán\ntento měsíc");

        init_hours_head(hours_year_plan_fill, hours_year_plan_text, number_of_days+1);

        init_workers(false, true, false);
        for(int j = 0; j<number_of_workers;j++)
        {
            String current_worker = worker_names[j].getText();
            for (Worker worker : workers)
            {
                Month month = worker.get_month_add_if_doesnt_exist();
                if (worker.name.equals(current_worker))
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
*/
/*
    //pokud již bylo něco zapsáno vytáhne z paměti a vyplní
    static void init_letter_holiday()
    {
        Rectangle proper_holiday_fill = new Rectangle(2*cell_width, cell_height);
        Rectangle day_off_fill = new Rectangle(2*cell_width, cell_height);

        Label proper_holiday_text = new Label("~ŘD\ntento měsíc");
        Label day_off_text = new Label("Počet /\nv kolizi s RP");

        init_hours_head(proper_holiday_fill, proper_holiday_text, number_of_days+1);
        init_hours_head(day_off_fill, day_off_text, number_of_days+2);

        init_workers(false, false, true);
        for(int j = 0; j<number_of_workers;j++)
        {
            String current_worker = worker_names[j].getText();
            for (Worker worker : workers)
            {
                Month month = worker.get_month_add_if_doesnt_exist();
                if (worker.name.equals(current_worker))
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

    static void cell_text_change(String text, int row, int column)
    {
        worker_lines[row][column].setText(text);
    }

    static void mark_day(int day_index)
    {
        Rectangle marker = new Rectangle(cell_width, cell_height);
        marker.setFill(Color.RED);
        marker.setStroke(Color.BLACK);
        grid_name_date.add(marker, day_index+1, number_of_workers+1);
    }



}
*/