package Služby;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static Služby.Main.chosen_year;
import static Služby.Main.month_index;

public class Worker implements Serializable {

    private static final long serialVersionUID = 1L;


    double hours_per_month;
    position pozice;
    private String name;

    private List<Month> months;
    private List<Request> requests;

    Worker(position pozice, double uvazek, String name)
    {
        months = new ArrayList<>();
        requests = new ArrayList<>();
        this.pozice = pozice;
        this.name = name;
        Main.workers.add(this);

        double hours_per_year;
        if(pozice == position.VY)
        {
            hours_per_year = 2026 * uvazek;
            hours_per_month = hours_per_year /12d;
        }

        else
        {
            hours_per_year = 1960 * uvazek;
            hours_per_month = hours_per_year /12d;
        }

    }

    enum position {
        ZS,
        PS,
        OS,
        VY,
        OSVY
    }

    List<Request> getRequests()
    {
        return this.requests;
    }

    void deleteRequests(Request request)
    {
        this.requests.remove(request);
    }

    boolean equals(Worker worker)
    {
        return this.name.equals(worker.name);
    }

    public String getName()
    {
        return name;
    }

    double bil_sum()
    {
        int i = month_index;
        double sum_worked = 0;

        Month month;
        while(i >= 0)
        {
            month = find_month(i);
            if(month == null)
            {
                i--;
                continue;
            }

            sum_worked = sum_worked + month.hours_worked;
            i--;
        }

        double sum_should_work = this.hours_per_month * (month_index +1);

        return sum_worked - sum_should_work;
    }

    Month get_month_add_if_doesnt_exist()
    {
        Month month = getMonth();
        if(month != null)
            return month;
        else
        {
            Month last_month;
            month = new Month(chosen_year, month_index);
            if(month_index == 0)
            {
                month.setCurrent_work_hours_bilantion(-this.hours_per_month);
            }
            else
            {
                last_month = find_month_add_if_doesnt_exists(month_index - 1);
                month.setCurrent_work_hours_bilantion(last_month.getCurrent_work_hours_bilantion() - this.hours_per_month);
            }
            this.months.add(month);
            return month;
        }
    }

    Month getMonth()
    {
        for(Month month : this.months)
        {
            if(month.year == chosen_year && month.month_index == month_index)
                return month;
        }
        return null;
    }

    Month find_month(int index)
    {
        for(Month month : this.months)
        {
            if(month.year == chosen_year && month.month_index == index)
                return month;
        }
        return null;
    }

    Month find_month_add_if_doesnt_exists(int index)
    {
        for(Month month : this.months)
        {
            if(month.year == chosen_year && month.month_index == index)
                return month;
        }

        Month month = new Month(chosen_year, index);
        Month last_month;
        if(index == 0)
            month.setCurrent_work_hours_bilantion(-this.hours_per_month);
        else
        {
            last_month = find_month_add_if_doesnt_exists(index-1);
            month.setCurrent_work_hours_bilantion(last_month.getCurrent_work_hours_bilantion() - this.hours_per_month);
        }
        this.months.add(month);
        return month;
    }

    boolean is_night_and_days_in_row_ok(int today, int shift_today)
    {
        Month month;
        int yesterday;
        int day_in_row = 1;

        if (today > 0) //pokud není 1. den v měsíci vezmeme jednoduše včerejšek
        {
            month = this.getMonth();
            yesterday = today - 1;
            day_in_row = check_past(yesterday, month);
        }
        else //jinak musíme vytáhnout poslední den minulého měsíce a měsíc přepnout na minulý
        {
            month = this.find_month(month_index - 1);
            if(month != null) //pokud minulý měsíc neexistuje je leden a nemusíme řešit
            {
                yesterday = month.number_of_days - 1;
                day_in_row = check_past(yesterday, month);
            }
        }

        if(day_in_row == 3)
            return false;
        else
        {
            if(today <= 0)
                month = getMonth();
            return check_future(today, month, day_in_row, shift_today);
        }
    }

    private int check_past(int yesterday, Month month)
    {
        if (month.actual_work_plan[yesterday] != null) //pokud včera byl v práci
        {
            if (month.actual_work_plan[yesterday].time_of_day == 1) //a měl noční
                return 3; //tak nemůže do práce
            else //jinak kontrolujeme předevčerejšek
            {
                yesterday--;
                if(yesterday < 0) //musíme přepnout na minulý měsíc
                {
                    month = this.find_month(month_index - 1);
                    if(month == null) //pokud minulý měsíc neexistuje je leden a nemusíme řešit
                        return 2; //byl včera v práci, ale ne předevčírem

                    yesterday = month.number_of_days - 1;
                }

                if(month.actual_work_plan[yesterday] != null)
                    return 3; //nemůže do práce tři dny v kuse
                else
                    return 2; //byl včera v práci, ale ne předevčírem
            }
        }
        else
            return 1; //nebyl včera v práci
    }

    private boolean check_future(int today, Month month, int days_in_row, int shift_today)
    {
        if (today != month.number_of_days - 1) //pokud dnes není poslední den v měsíci
        {
            if (month.actual_work_plan[today + 1] != null) //tak zkontrolujeme zda zítra již nepracuje
            {
                if (shift_today == 1 || days_in_row == 2) //a my mu současně nezapisujeme noční nebo již nepracuje příliš mnoho dní
                    return false;
                else
                    return today + 1 == month.number_of_days - 1 || month.actual_work_plan[today + 2] == null; //pokud zítra je poslední den v měsíci nebo pozítří nepracuje je to v pořádku
            }
        }
        //jinak zítra jiste nepracuje, protože příští měsíc ještě nenastal a bude se řešit až při výpočtu příštích služeb
        return true;
    }

    void add_request(Request request)
    {
        requests.add(request);
    }

    int get_request_count()
    {
        return requests.size();
    }

    Request get_request(int i)
    {
        return requests.get(i);
    }

    boolean no_requests(int day_seq, int time_of_day)
    {
        for(Request request : requests)
        {
            if(day_seq == request.getDay_of_week())
            {
                switch(request.getTime())
                {
                    case DAY_AND_NIGHT:
                        return false;
                    case NIGHT:
                        if(time_of_day == 1)
                            return false;
                        break;
                    case DAY:
                    case EVENING:
                    case AFTERNOON:
                        if(time_of_day == 0 || time_of_day == 2)
                            return false;
                        break;
                    case MORNING:
                        if(time_of_day == 0)
                            return false;
                        break;
                }
            }
        }
        return true;
    }
}
