package Služby;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Day implements Serializable {

    private static final long serialVersionUID = 3L;

    boolean is_weekend;
    int day_number;
    int week_seq_number;
    boolean has_rhb;
    List<Shift> shifts_for_day;

    boolean night_has_ZS;

    boolean day_has_VY;


    public Day(boolean is_weekend, int day_number, int week_seq_number)
    {
        this.night_has_ZS = false;
        this.day_has_VY = false;
        this.is_weekend = is_weekend;
        this.day_number = day_number;
        this.week_seq_number = week_seq_number;
        this.shifts_for_day = new ArrayList<>();
    }

    void init_shifts(List<Shift> shifts)
    {
        for(Shift shift : shifts)
        {
            shifts_for_day.add(new Shift(shift.typ, shift.time_of_day, shift.ord));
        }
    }

    void init_night()
    {
        if(this.is_weekend)
            shifts_for_day.add(new Shift(Shift.type.N1L, 1, false));
        else
            shifts_for_day.add(new Shift(Shift.type.N1S, 1, false));

        shifts_for_day.add(new Shift(Shift.type.N2, 1, false));
    }

    void init_night_and_ord()
    {
        if(this.is_weekend)
        {
            shifts_for_day.add(new Shift(Shift.type.N1L, 1, false));
            shifts_for_day.add(new Shift(Shift.type.D1, 0, true));
        }
        else
        {
            shifts_for_day.add(new Shift(Shift.type.N1S, 1, false));
            shifts_for_day.add(new Shift(Shift.type.O12, 0, true));
        }
        shifts_for_day.add(new Shift(Shift.type.N2, 1, false));
    }
}