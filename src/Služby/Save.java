package Služby;

import java.io.*;

import static Služby.Main.work_plans;
import static Služby.Main.workers;

class Save {

    private File save_file_workers;
    private File save_file_work_plans;
    //private static File save_file_requests;


    int create_and_check_save_file_workers() throws IOException {
        save_file_workers = new File("./magic_shifts_maker_save_workers");
        if(save_file_workers.exists())
            return -1;
        else
        {
            if(save_file_workers.createNewFile())
                return 0;
            else return 1;
        }
    }

    int create_and_check_save_file_work_plans() throws IOException {
        save_file_work_plans = new File("./magic_shifts_maker_save_work_plans");
        if(save_file_work_plans.exists())
            return -1;
        else
        {
            if(save_file_work_plans.createNewFile())
                return 0;
            else return 1;
        }
    }

    void save_workers() throws IOException
    {
        FileOutputStream workers_file_stream = new FileOutputStream(save_file_workers, false);
        ObjectOutputStream workers_object_stream = new ObjectOutputStream(workers_file_stream);

        workers_object_stream.writeInt(workers.size());

        for(Worker worker : workers)
        {
            workers_object_stream.writeObject(worker);
        }

        workers_object_stream.close();

    }

    void read_workers() throws IOException, ClassNotFoundException {
        FileInputStream workers_read_file = new FileInputStream(save_file_workers);
        ObjectInputStream workers_read_object = new ObjectInputStream(workers_read_file);

        int number_of_workers = workers_read_object.readInt();

        for (int i = 0; i < number_of_workers; i++)
        {
            Worker worker = (Worker) workers_read_object.readObject();
            workers.add(worker);
        }
    }

    void save_work_plans() throws IOException
    {
        FileOutputStream work_plans_file_stream = new FileOutputStream(save_file_work_plans, false);
        ObjectOutputStream work_plans_object_stream = new ObjectOutputStream(work_plans_file_stream);

        for(Work_plan work_plan : work_plans)
        {
            work_plans_object_stream.writeObject(work_plan);
        }

        work_plans_object_stream.close();
    }

    void read_work_plans() throws IOException, ClassNotFoundException {
        FileInputStream work_plans_read_file = new FileInputStream(save_file_work_plans);
        ObjectInputStream work_plans_read_object = new ObjectInputStream(work_plans_read_file);


        for (int i = 0; i < 12; i++)
        {
            System.out.println("I: " + i);
            Work_plan work_plan = (Work_plan) work_plans_read_object.readObject();
            work_plans.add(work_plan);
        }
    }

    void load_year_plan_holiday_from_backup(String file_name) throws IOException, ClassNotFoundException {
        ObjectInputStream backup_object = new ObjectInputStream(new FileInputStream(file_name));

        int number_of_workers = backup_object.readInt();

        for(int i = 0; i< number_of_workers;i++)
        {
            Worker worker_bac = (Worker) backup_object.readObject();

            for(Worker worker : workers)
            {
                if(worker_bac.getName().equals(worker.getName()))
                {
                    Month month_bac = worker_bac.find_month(1);
                    worker.find_month(1).year_plan = month_bac.year_plan;
                    worker.find_month(1).holiday = month_bac.holiday;
                }
            }
        }
    }
/*
    static int save_request(Request request) throws IOException {
        save_file_requests = new File("./magic_shifts_maker_save_requests");
        if(!save_file_requests.exists())
        {
            if(!save_file_requests.createNewFile())
                return 1;
        }

        ObjectOutputStream requests_object_stream = new ObjectOutputStream(new FileOutputStream(save_file_requests, true));
        requests_object_stream.writeObject(request);
        requests_object_stream.close();
        return 0;
    }*/
/*
    static void load_request() throws IOException, ClassNotFoundException {
        ObjectInputStream requests_stream = new ObjectInputStream(new FileInputStream(save_file_requests));

        boolean exists = true;
        Request request;
        while(exists)
        {
            request  = (Request)requests_stream.readObject();
            if(request != null)
            {
                for(Worker worker : workers)
                {
                    if(worker.name.equals(request.getName()))

                }
            }
            else
                exists = false;
        }

    }
*/
}
