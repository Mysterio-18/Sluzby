package Služby;

import java.io.Serializable;

public class Request implements Serializable{

    private static final long serialVersionUID = 1L;

    private int day_of_week;
    private int valid_to_index;
    private time_type time;
    private String name;

    private String day_string;
    private String validity;

    Request(int day_of_week, time_type time, int valid_to, String name)
    {
        this.name = name;
        day_of_week = day_of_week +2;
        if(day_of_week == 8)
            day_of_week = 1;
        this.day_of_week = day_of_week;
        this.time = time;
        if(valid_to == 12)
            valid_to = -1;
        this.valid_to_index = valid_to;
    }

    public void setDay_string(String day)
    {
        day_string = day;
    }

    public void setValidity(String validity)
    {
        this.validity = validity;
    }

    enum time_type
    {
        MORNING,
        AFTERNOON,
        EVENING,
        DAY,
        NIGHT,
        DAY_AND_NIGHT
    }

    public boolean equals(Request request, boolean check_worker)
    {
        if(this.day_of_week == request.day_of_week)
        {
            if(this.valid_to_index == request.valid_to_index)
            {
                if(this.time == request.time)
                {
                    if(check_worker)
                        return this.name.equals(request.name);
                    else
                        return true;
                }
            }
        }
        return false;
    }

    public String getDay_string()
    {
        return day_string;
    }

    public String getValidity()
    {
        return validity;
    }

    public String getName() {
        return name;
    }

    public int getDay_of_week()
    {
        return day_of_week;
    }

    public int getValid_to_index()
    {
        return valid_to_index;
    }

    public time_type getTime()
    {
        return time;
    }


    int day_of_week_to_int(String day)
    {
        switch (day)
        {
            case "Pondělí":
                return 2;
            case "Úterý":
                return 3;
            case "Středa":
                return 4;
            case "Čtvrtek":
                return 5;
            case "Pátek":
                return 6;
            case "Sobota":
                return 7;
            default:
                return 1;
        }
    }

    int valid_to_int(String month)
    {
        switch (month)
        {
            case "Leden":
                return 0;
            case "Únor":
                return 1;
            case "Březen":
                return 2;
            case "Duben":
                return 3;
            case "Květen":
                return 4;
            case "Červen":
                return 5;
            case "Červenec":
                return 6;
            case "Srpen":
                return 7;
            case "Září":
                return 8;
            case "Říjen":
                return 9;
            case "Listopad":
                return 10;
            default:
                return 11;
        }
    }
}
