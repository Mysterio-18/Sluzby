package Služby;


import java.io.Serializable;

public class Shift implements Serializable {

    private static final long serialVersionUID = 4L;

    type typ;
    int time_of_day; //0 = D, 1 = N, 2 = O
    boolean ord;
    boolean worker_assigned;
    double duration;

    private boolean unchangeable;

    public Shift(type typ, int time_of_day, boolean ord)
    {
        this.typ = typ;
        this.time_of_day = time_of_day;
        this.ord = ord;
        this.worker_assigned = false;
        this.duration = count_duration(typ);
        this.unchangeable = false;
    }

    void setUnchangeable(boolean change)
    {
        this.unchangeable = change;
    }

    boolean isUnchangeable()
    {
        return this.unchangeable;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;

        if (!(o instanceof Shift)) {
            return false;
        }

        Shift s = (Shift) o;

        return s.typ == this.typ && s.ord == this.ord;
    }

    boolean equals_unchangeable(Shift shift)
    {
        if(shift == null)
            return false;

        if(this.time_of_day == 0)
        {
            return shift.time_of_day == 0;
        }
        else if(this.time_of_day == 1)
        {
            return shift.time_of_day == 1;
        }
        else
            return shift.time_of_day == 2;
    }

    enum type {
        D1,     //0
        D2,     //1
        O12,    //2
        O19,    //3
        O13,    //4
        O21,    //5
        O20,    //6
        D7,     //7
        N1S,    //8
        N1L,    //9
        N2,     //10
        RHB_s,
        RHB_l
    }

    private double count_duration(Shift.type type)
    {
        switch (type)
        {
            case D1:
            case D2:
            case N1L:
            case N2:
                return 11.5;
            case O19:
                return 7;
            case O12:
                return 7.5;
            case O13:
            case RHB_l:
                return 6.5;
            case D7:
            case RHB_s:
                return 6;
            case O21:
                return 8;
            case N1S:
                return 11;
            default:
                Pop_Up.warning("Shift not found, from count_duration");
                return 0;
        }
    }

    boolean is_shift_applicable(Shift.type available_shift, Worker.position position, boolean first_go)
    {
        if(first_go)
        {
            if(available_shift == Shift.type.D1 && this.time_of_day == 0)
                return (!this.ord || position == Worker.position.ZS);
            else if(available_shift == type.O19 && this.time_of_day == 2)
                return (!this.ord || position == Worker.position.ZS);
        }
        else
        {
            if(available_shift == Shift.type.D1 || available_shift == type.O19)
            {
                if(this.time_of_day == 0 || this.time_of_day == 2)
                    return (!this.ord || position == Worker.position.ZS);
            }
        }
        return false;
    }

    boolean is_ord_or_VY_applicable(Shift.type available_shift)
    {
        if(available_shift == type.D1 && this.time_of_day == 0)
            return true;
        else
            return available_shift == type.O19 && this.time_of_day == 2;
    }

    void change_values(Shift.type type, int time, boolean ord)
    {
        this.typ = type;
        this.time_of_day = time;
        this.ord = ord;
    }


}
