package Služby;

import java.io.Serializable;

class Month implements Serializable {

    private static final long serialVersionUID = 5L;

    int year;
    int month_index;
    int number_of_days;

    int[] holiday;
    Shift.type[] year_plan;
    Shift[] actual_work_plan;

    double hours_worked;

    private double current_work_hours_bilantion;

    int nights_worked;
    private int nights_in_YP;

    private int saturdays_worked;
    private int sundays_worked;
    private int weekends_free;
    private int ord_worked;


    Month(int year, int month_index)
    {
        current_work_hours_bilantion = 0;
        number_of_days = Main.days_in_month(month_index, year);
        this.year = year;
        this.month_index = month_index;

        this.holiday = new int[number_of_days]; // 0 => can_work   1 => cant_work //   2 => RD
        this.year_plan = new Shift.type[number_of_days]; // d1 019 n1l
        this.actual_work_plan = new Shift[number_of_days];
        this.hours_worked = 0.0;

        nights_worked = 0;
        nights_in_YP = 0;

        saturdays_worked = 0;
        sundays_worked = 0;
        weekends_free = 0;

        ord_worked = 0;

    }

    double getCurrent_work_hours_bilantion()
    {
        return current_work_hours_bilantion;
    }

    void addtoCurrent_work_hours_bilantion(double amout_to_add)
    {
        current_work_hours_bilantion += amout_to_add;
    }

    void setCurrent_work_hours_bilantion(double amout)
    {
        current_work_hours_bilantion = amout;
    }

    boolean is_available_today(int today)
    {
        return this.holiday[today] == 0 && this.actual_work_plan[today] == null;
    }

    double year_plan_month_sum()
    {
        double sum = 0;
        for(Shift.type type : this.year_plan)
        {
            if(type == Shift.type.D1 || type == Shift.type.N1L)
                sum += 11.5;
            else if(type == Shift.type.O19)
                sum += 7;
        }
        return sum;
    }

    double proper_holiday_month_sum()
    {
        double sum = 0;
        for(int i = 0; i < number_of_days; i++)
        {
          if(year_plan[i] != null && holiday[i] == 2)
          {
              if(year_plan[i] == Shift.type.D1 || year_plan[i] == Shift.type.N1L)
                  sum += 11.5;
              else if(year_plan[i] == Shift.type.O19)
                  sum += 7;
          }
        }
        return sum;
    }

    int get_nights_in_YP()
    {
        int sum = 0;
        for(int i = 0; i < number_of_days; i++)
        {
            if(year_plan[i] == Shift.type.N1L)
                sum++;
        }
        return sum;
    }

    int day_off_month_count()
    {
        int sum = 0;
        for(int i = 0; i < number_of_days; i++)
        {
            if(year_plan[i] != null && holiday[i] == 1)
            {
                sum++;
            }
        }
        return sum;
    }

    void add_hours(Shift shift)
    {
        this.hours_worked += shift.duration;
        this.current_work_hours_bilantion += shift.duration;
    }

    void put_to_work(int day_index, Shift shift, int week_seq_number)
    {
        this.actual_work_plan[day_index] = shift;
        if(week_seq_number == 7)
            saturdays_worked++;
        else if(week_seq_number == 1)
            sundays_worked++;

        if(shift.time_of_day == 1)
            nights_worked++;
        else if(shift.ord)
            ord_worked++;
    }

    private void put_out_off_work(int shift_time_of_day, boolean shift_ord, int week_seq_number)
    {
        if(week_seq_number == 7)
            saturdays_worked--;
        else if(week_seq_number == 1)
            sundays_worked--;

        if(shift_time_of_day == 1)
            nights_worked--;
        else if(shift_ord)
            ord_worked--;
    }



    String change_solved_shifts(int day, int seq_number)
    {

        Shift old_shift = actual_work_plan[day];
        if(actual_work_plan[day] == null)
        {
            put_to_work(day, new Shift(Shift.type.D1, 0, false), seq_number);
            actual_work_plan[day].setUnchangeable(true);
            return "D1";
        }
        else if(actual_work_plan[day].typ == Shift.type.RHB_l)
        {
            put_out_off_work(old_shift.time_of_day, old_shift.ord, seq_number);
            this.actual_work_plan[day] = null;
            return "";
        }
        else
        {
            put_out_off_work(old_shift.time_of_day, old_shift.ord, seq_number);
            String text = change_next(actual_work_plan[day]);
            actual_work_plan[day].setUnchangeable(true);
            put_to_work(day, actual_work_plan[day], seq_number);
            return text;
        }
    }

    String change_day_year_plan(int day)
    {
        if(year_plan[day] == null)
        {
            year_plan[day] = Shift.type.D1;
            return "d1";
        }
        else
        {
            switch (year_plan[day])
            {
                case D1:
                    year_plan[day] = Shift.type.O19;
                    return "o19";
                case O19:
                    year_plan[day] = Shift.type.N1L;
                    return "n";
                case N1L:
                    year_plan[day] = null;
                    return "";
                default:
                    year_plan[day] = null;
                    return "ERR";
            }
        }
    }

    String change_day_back_year_plan(int day)
    {
        if(year_plan[day] == null)
        {
            year_plan[day] = Shift.type.N1L;
            return "n";
        }
        else
        {
            switch (year_plan[day])
            {
                case D1:
                    year_plan[day] = null;
                    return "";
                case O19:
                    year_plan[day] = Shift.type.D1;
                    return "d1";
                case N1L:
                    year_plan[day] = Shift.type.O19;
                    return "o19";
                default:
                    year_plan[day] = null;
                    return "ERR";
            }
        }
    }

    String change_day_reset_year_plan(int day)
    {
        year_plan[day] = null;
        return "";
    }

    String change_holiday(int day)
    {
        switch (holiday[day])
        {
            case 0:
                holiday[day] = 1;
                return "/";
            case 1:
                holiday[day] = 2;
                return "ŘD";
            case 2:
                holiday[day] = 0;
                return "";
            default:
                return "ERR";
        }
    }

    String change_back_holiday(int day)
    {
        switch (holiday[day])
        {
            case 0:
                holiday[day] = 2;
                return "ŘD";
            case 1:
                holiday[day] = 0;
                return "";
            case 2:
                holiday[day] = 1;
                return "/";
            default:
                return "ERR";
        }
    }

    String change_reset_holiday(int day)
    {
        holiday[day] = 0;
        return "";
    }

    void remove_changeable_shifts()
    {
        for (int i = 0; i < this.actual_work_plan.length; i++)
        {
            if(this.actual_work_plan[i] != null)
            {
                if(this.actual_work_plan[i].isUnchangeable())
                    this.actual_work_plan[i].worker_assigned = false;
                else
                    this.actual_work_plan[i] = null;
            }
        }
    }

    String change_next(Shift shift)
    {
        switch (shift.typ)
        {
            case D1:
                if(shift.ord)
                {
                    shift.change_values(Shift.type.D2, 2, false);
                    return "D2";
                }
                else
                {
                    shift.change_values(Shift.type.D1, 0, true);
                    return "D1L";
                }
            case D2:
                shift.change_values(Shift.type.D7, 0, false);
                return "D7";
            case D7:
                shift.change_values(Shift.type.O12, 2, false);
                return "O12";
            case O12:
                if(shift.ord)
                {
                    shift.change_values(Shift.type.O13, 2, false);
                    return "O13";
                }
                else
                {
                    shift.change_values(Shift.type.O12, 2, true);
                    return "O12L";
                }
            case O13:
                shift.change_values(Shift.type.O19, 2, false);
                return "O19";
            case O19:
                shift.change_values(Shift.type.O20, 2, false);
                return "O20";
            case O20:
                shift.change_values(Shift.type.O21, 2, false);
                return "O21";
            case O21:
                shift.change_values(Shift.type.N1S, 1, false);
                return "N";
            case N1S:
                shift.change_values(Shift.type.N1L, 1, false);
                return "N1";
            case N1L:
                shift.change_values(Shift.type.N2, 1, false);
                return "N2";
            case N2:
                shift.change_values(Shift.type.RHB_s, 3, false);
                return "RHB";
            case RHB_s:
                shift.change_values(Shift.type.RHB_l, 3, false);
                return "RHB";
            default:
                return "ERR";
        }
    }


}
