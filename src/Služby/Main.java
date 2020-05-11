package Služby;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;


public class Main extends Application {

    static ArrayList<Worker> workers;
    static ArrayList<Work_plan> work_plans;
    static int month_index;
    static int chosen_year;
    static boolean save_needed;
    static Save save_file;

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("fxml_files/MainWindow.fxml"));

        Scene scene = new Scene(root, 1366, 768);

        save_needed = false;

        init();

        save_file = new Save();
        int exists = save_file.create_and_check_save_file_workers();
        if(exists == 0)
        {
            initWorkers();
            save_file.save_workers();
        }
        else if(exists == -1)
            save_file.read_workers();
        else
        {
            System.out.println("Nelze vytvořit soubor");
            return;
        }
        exists = save_file.create_and_check_save_file_work_plans();
        if(exists == 0)
        {
            init_work_plans();
            save_file.save_work_plans();
        }
        else if(exists == -1)
            save_file.read_work_plans();
        else
        {
            System.out.println("Nelze vytvořit soubor");
            return;
        }

        primaryStage.setTitle("Služby Křetín");
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event ->
        {
            if(save_needed)
            {
                Pop_Up.save_unsaved_changes(save_file);
            }

        });
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public void init()
    {
        month_index = -1;
        workers = new ArrayList<>();
        work_plans = new ArrayList<>();
    }

    private static void initWorkers()
    {
        new Worker(Worker.position.OS, 1, "Petrová Jitka");
        new Worker(Worker.position.ZS, 1, "Němcová Milada");
        new Worker(Worker.position.ZS, 1, "Mistrová Lada");
        new Worker(Worker.position.ZS, 1, "Navrátilová Alena");
        new Worker(Worker.position.ZS, 1, "Tenorová Božena");
        new Worker(Worker.position.ZS, 1, "Šillerová Tereza");
        new Worker(Worker.position.ZS, 0.7, "Šichová Monika");
        new Worker(Worker.position.ZS, 1, "Poláková Lenka");
        new Worker(Worker.position.ZS, 1, "Folerová Jiřina");
        new Worker(Worker.position.ZS, 0.6, "Holasová Lucie");
        new Worker(Worker.position.VY, 1, "Marečková Dana");
        new Worker(Worker.position.VY, 1, "Dočekal Roman");
        new Worker(Worker.position.VY, 0.7, "Holasová Radka");
        new Worker(Worker.position.OSVY, 1, "Měcháčková Jana");
    }

    private static void init_work_plans()
    {
        for(int i = 0; i<12; i++)
        {
            System.out.println(chosen_year);
            new Work_plan(chosen_year, i, days_in_month(i, chosen_year));
        }
    }



    static int days_in_month(int month_index, int year)
    {
        switch (month_index+1)   //real_month = month_index + 1
        {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 2:
                if((year % 4) == 0)
                    return 29;
                else
                    return 28;
            default:
                return 30;
        }
    }



}
