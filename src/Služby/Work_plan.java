package Služby;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import static Služby.Main.workers;
import static Služby.Pop_Up.warning;

class Work_plan implements Serializable {

    private static final long serialVersionUID = 2L;

    int first_day_of_month;
    List<List<Shift>> chosen_shifts;
    int month_index;
    private Day[] days;
    List<Integer> rhb_weeks;

    int turnus_start;

    boolean plan_done;

    Work_plan(int year, int month_index, int number_of_days)
    {
        this.month_index = month_index;
        chosen_shifts = null;

        first_day_of_month = day_of_week(year, month_index);

        days = new Day[number_of_days];

        init_days();
        Main.work_plans.add(this);

        this.plan_done = false;
    }

    int get_day_seq_number(int day_number)
    {
        return this.days[day_number].week_seq_number;
    }

    //zjistí den v týdnu 1. den v měsíci
    private int day_of_week(int year, int month_index)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month_index); // měsíce jsou číslovány od 0
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //System.out.println(calendar.getTime());
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    boolean was_last_month_plan_init(Work_plan last_plan)
    {
        return last_plan.plan_done;
    }

    private void init_days()
    {
        boolean weekend;
        int cur_day_of_week = first_day_of_month;
        for(int i = 0; i<days.length; i++)
        {
            weekend = is_weekend(cur_day_of_week);
            days[i] = new Day(weekend, i + 1, cur_day_of_week);
            cur_day_of_week++;
            if (cur_day_of_week > 7)
                cur_day_of_week = 1;
        }
    }

    static boolean is_weekend(int cur_day)
    {
        switch (cur_day)
        {
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return false;
            default:
                return true;
        }
    }

    //funkce nás provede kalkulací pracovní plánu (work_plan), nad kterým byla zavolaná, plán má již vše potřebné informace
    //nyní je seskládá a provede výpočet
    //turnus start -> začátek turnusu
    //first_shifts -> směny na první polovinu měsíce (starý turnus)
    //first_shifts_weekend -> VÍKENDOVÉ směny na první polovinu měsíce (starý turnus)
    //first_rhb_weeks -> rehabilitace na první polovinu měsíce (starý turnus)
    void calculate_work_plan(int turnus_start, List<Shift> first_shifts, List<Shift> first_shifts_weekend, List<Integer> first_rhb_weeks)
    {
        clear_previous_calculation(); //pokud již byla kalkulace provedena je třeba vyčistit staré výpočty, aby nám nepokřivily nové výpočty

        if(first_shifts != null) //pokud nejsme v lednu, bude existovat starý turnus
        {
            schedule_shifts(0, turnus_start-2, first_shifts, first_shifts_weekend); //rozdělí směny do dnů, dle zadaného rozsahu, zde pro starý turnus
            //add_rhb_old(turnus_start, first_rhb_weeks); //přidělí dle potřeby RHB
        }

        schedule_shifts(turnus_start, days.length, this.chosen_shifts.get(0), this.chosen_shifts.get(1)); //rozdělí směny do dnů, dle zadaného rozsahu, zde pro nový turnus
       //add_rhb_new(turnus_start); //přidělí dle potřeby RHB

        fill_shifts_with_hand_written(); //směny na den jsou označeny jako obsazené, pokud souhlasí s ručně vepsanými směnami

        //zde zapíšeme pracovníky pro rehabilitace
        for(int i = 0; i< days.length; i++)
        {
            if(days[i].has_rhb)
            {
                if(days[i].week_seq_number == 5)
                    solve_rhb(i, "Dočekal Roman", "Měcháčková Jana", find_rhb(i), false);
                else
                    solve_rhb(i, "Měcháčková Jana", "Dočekal Roman", find_rhb(i), false);
            }
        }

        //rozhodíme služby nejdříve dle RP a pak doplníme zbytek
        //doplnění zbytku probíhá v několika vlnách, postupně zvyšujeme toleranci tzn. dovolíme více pracovníkům obsadit směnu
        //toto provádíme dokud zbývají volné směny, pokud ani největší tolerance není dostatečná, je třeba vyřešit ručně

        //Noční
        solve_year_plan_nigts();
        if(solve_remaining_nights())
            Pop_Up.warning("Chybí noční");

        //ordinace
        if(solve_ord())
            Pop_Up.warning("Chybí ordinace");

        //vychovatelé
        if(solve_VY())
            Pop_Up.warning("Chybí vychovatel");

        //zaplní se vše, co zbylo
        if(solve_remaining_shifts())
            Pop_Up.warning("Nejsou zapsány všechny směny");

        //cervene ctverecky, pro debug
        for(int i = 0; i<days.length;i++)
        {
            for(Shift shift : days[i].shifts_for_day)
            {
                if(!shift.worker_assigned)
                {
                    System.out.println("NENI PRIRAZEN");
                }
            }
        }
    }

    //funkce přidělí každému dnu v rozsahu start-end směny, které je potřeba zaplnit pracovníky
    //start -> datum začátku
    //end -> datum konce
    //shifts -> směny na všední dny
    //shifts_weekend -> směny na víkendy
    private void schedule_shifts(int start, int end, List<Shift> shifts, List<Shift> shifts_weekend)
    {
        //pokud start není první den v měsíci tzn. jsme na přelomu turnusů, je třeba doplnit noční na den nájezdu
        //denní služby na den nájezdu jsou doplněny ručně dle potřeby
        //TODO co když turnus začne zrovna první den v měsíci
        if(start > 0)
        {
            int i = start -1; //vrátíme se do dne nájezdu a zapíšeme noční
            days[i].init_night();
        }

        //přidělíme dnům směny
        for(int i = start; i<end; i++)
        {
            if(days[i].is_weekend)
                days[i].init_shifts(shifts_weekend);
            else
                days[i].init_shifts(shifts);

            days[i].init_night_and_ord();
        }
    }

    private void add_rhb_old(int turnus_start, List<Integer> rhb_weeks)
    {
        int week = 3;
        for(int i = turnus_start - 4; i >= 0; i--) //-4 -> neděle
        {
            check_weeks_rhb(i,week, rhb_weeks);
            if(days[i].week_seq_number == 2)
                week--;
        }
    }

    private void add_rhb_new(int turnus_start)
    {
        int week = 0;
        for (int i = turnus_start; i < this.days.length; i++)
        {
            check_weeks_rhb(i, week, this.rhb_weeks);
            if(days[i].week_seq_number == 1)
                week++;
        }

    }

    private void check_weeks_rhb(int i, int week, List<Integer> rhb_weeks)
    {
        if(days[i].week_seq_number == 5) //čtvrtek
        {
            if(week != 0)
            {
                days[i].shifts_for_day.add(new Shift(Shift.type.RHB_l,3,false));
                days[i].has_rhb = true;
            }
        }
        else if(days[i].week_seq_number == 4) //středa
        {
            for(Integer rhb_week : rhb_weeks)
            {
                if(week == rhb_week)
                {
                    days[i].shifts_for_day.add(new Shift(Shift.type.RHB_s,3,false));
                    days[i].has_rhb = true;
                }
            }
        }
    }

    private Shift find_rhb(int i)
    {
        for(Shift shift : days[i].shifts_for_day)
        {
            if(shift.typ == Shift.type.RHB_l || shift.typ == Shift.type.RHB_s)
                return shift;
        }
        return null;
    }

    private boolean solve_rhb(int i, String first, String second, Shift rhb, boolean tried_change)
    {
        if(rhb == null)
        {
            Pop_Up.warning("SHOULD BE RHB BUT ISNT FOUND");
            return false;
        }

        boolean done = false;
        for (Worker worker : workers)
        {
            Month month = worker.get_month_add_if_doesnt_exist();
            if (worker.getName().equals(first))
            {
                if (month.holiday[i] == 0)
                {
                    if (month.actual_work_plan[i] == null)
                    {
                        month.actual_work_plan[i] = rhb;
                        done = true;

                        for(Shift shift : days[i].shifts_for_day)
                        {
                            if(shift.typ == rhb.typ)
                            {
                                shift.worker_assigned = true;
                                month.hours_worked += shift.duration;
                                month.addtoCurrent_work_hours_bilantion(shift.duration);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
        if(!done)
        {
            if(!tried_change)
            {
                done = solve_rhb(i, second, first, rhb, true);
            }
            else
            {
                warning("Není nikdo na RHB na den " + (i+1));
            }
        }
        return done;
    }

    private void solve_year_plan_nigts()
    {
        for(int i = 0; i<days.length; i++) //projdeme všechny dny
        {
            if(i < turnus_start)
                continue;
            if(i > 24)
                continue;
            for(Shift shift : days[i].shifts_for_day) //pro každý den všechny směny
            {
                if(shift.time_of_day == 1 && !shift.worker_assigned) //pokud je směna noční a není obsazena
                {
                    for(Worker worker : workers) //projdeme všechny pracovníky
                    {
                        Month month = worker.get_month_add_if_doesnt_exist(); //najdeme pracovníkův měsíc, kde má své služby
                        if(month.holiday[i] == 0 && month.actual_work_plan[i] == null) //pracovník nemá proškrtnuto a má něco v ročním plánu
                        {
                            if(month.year_plan[i] == Shift.type.N1L) //v ročním plánu má noc
                            {
                                put_to_work(shift, worker, i); //obsadíme službu pracovníkem a pokračujeme další službou
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean solve_remaining_nights()
    {
        Worker temp;
        boolean nights_remain = false;

        for(int i = 0; i<days.length; i++) //projdeme postupně všechny dny v měsíci
        {
            fill_hours(i, false);
            if(i < turnus_start)
                continue;
            if(i > 24)
                continue;
            for(Shift shift : days[i].shifts_for_day) //u každého dne všechny dostupné směny
            {
                if(shift.time_of_day == 1 && !shift.worker_assigned) //pokud je směna noční a není obsazena
                {
                    temp = null;

                    for (Worker worker : workers) //a pro každou dostupnu směnu se pokusíme najít nejvhodnějšího pracovníka
                    {
                        if(is_night_ok(worker.pozice, i)) //pustí jen ZS pokud není, jinak ZS/OS/PS
                        {
                            Month month = worker.getMonth();
                            if(month.is_available_today(i)) //nemá ten den dovolenou a již ten den nepracuje
                            {
                                if(worker.is_night_and_days_in_row_ok(i, shift.time_of_day)) //nekoliduje s noční nebo příliš dnům po sobě
                                {
                                    if(temp == null || is_new_worker_better(temp.getMonth(), month))
                                        temp = worker;
                                }
                            }
                        }
                    }
                    if(temp != null) //pokud jsme našli alespoň jednoho vhodného pracovníka, zapíšeme ho do služeb
                    {
                        put_to_work(shift, temp, i);
                        if(temp.pozice != Worker.position.OSVY)
                            temp.getMonth().add_hours(shift);
                    }

                    else
                        nights_remain = true;
                }
            }
        }
        return nights_remain;
    }

    private boolean solve_ord()
    {
        Worker temp;
        boolean ord_remain = false;

        for(int i = 0; i<days.length; i++) //projdeme postupně všechny dny v měsíci
        {
            if(i < turnus_start)
                continue;
            if(i > 24)
                continue;
            for (Shift shift : days[i].shifts_for_day) //u každého dne všechny dostupné směny
            {
                if (shift.ord && !shift.worker_assigned) //pokud je směna ordinace a nebyla ještě obsazena
                {
                    temp = null;
                    for(Worker worker : workers) //projdeme všechny pracovníky
                    {
                        Month month = worker.getMonth();
                        if (worker.pozice == Worker.position.ZS) //pracovník musí být ZS
                        {
                            if (month.is_available_today(i)) //nemá dovolenou ani již nepracuje
                            {
                                if(worker.is_night_and_days_in_row_ok(i, shift.time_of_day)) //nemá po noční ani není příliš dní po sobě v práci
                                {
                                    if(temp == null || month.getCurrent_work_hours_bilantion() < temp.getMonth().getCurrent_work_hours_bilantion()) //pokud jsme již nenašli někoho lepšího, uložíme si dočasně tohoto pracovníka
                                        temp = worker;
                                }
                            }
                        }
                    }
                    if(temp != null) //pokud jsme našli alespoň jednoho vhodného pracovníka, zapíšeme ho do služeb
                    {
                        put_to_work(shift, temp, i);
                        temp.getMonth().add_hours(shift);

                    }

                    else
                        ord_remain = true;
                }
            }
        }
        return ord_remain;
    }

    private boolean solve_VY()
    {
        Worker temp;
        boolean needed_VY = false;

        for(int i = 0; i<days.length; i++) //projdeme postupně všechny dny v měsíci
        {
            fill_hours(i, true);
            if(i < turnus_start)
                continue;
            if(i > 24)
                continue;
            if(!days[i].day_has_VY) //den ještě nemá vychovatele
            {
                for (Shift shift : days[i].shifts_for_day) //u každého dne všechny dostupné směny
                {
                    if (!shift.worker_assigned) //služba ještě nebyla obsazena
                    {
                        temp = null;
                        for(Worker worker : workers)
                        {
                            Month month = worker.getMonth();
                            if(worker.pozice == Worker.position.VY || worker.pozice == Worker.position.OSVY)
                            {
                                if (month.is_available_today(i) && worker.no_requests(days[i].week_seq_number, shift.time_of_day))
                                {
                                    if(worker.is_night_and_days_in_row_ok(i, shift.time_of_day))
                                    {
                                        if(temp == null || month.getCurrent_work_hours_bilantion() < temp.getMonth().getCurrent_work_hours_bilantion()) //pokud jsme již nenašli někoho lepšího, uložíme si dočasně tohoto pracovníka
                                            temp = worker;
                                    }
                                }
                            }
                        }
                        if(temp != null) //pokud jsme našli alespoň jednoho vhodného pracovníka, zapíšeme ho do služeb
                        {
                            put_to_work(shift, temp, i);
                            temp.getMonth().add_hours(shift);
                        }


                    }
                    if(days[i].day_has_VY)
                        break;
                }
                if(!days[i].day_has_VY && i > turnus_start)
                {
                    System.out.println(i);
                    needed_VY = true;
                }

            }
        }
        return needed_VY;
    }

    private boolean solve_remaining_shifts()
    {
        Worker temp;
        boolean shift_empty = false;

        for(int i = 0; i<days.length; i++) //projdeme postupně všechny dny v měsíci
        {
            if(i < turnus_start)
                continue;
            if(i > 24)
                continue;
            for(Shift shift : days[i].shifts_for_day) //u každého dne všechny dostupné směny
            {
                temp = null;
                if(!shift.worker_assigned)
                {
                    for (Worker worker : workers) //a pro každou dostupnu směnu se pokusíme najít nejvhodnějšího pracovníka
                    {
                        Month month = worker.get_month_add_if_doesnt_exist();

                        if(month.is_available_today(i) && worker.no_requests(days[i].week_seq_number, shift.time_of_day)) //nemá ten den dovolenou a již nepracuje
                        {
                            if(worker.is_night_and_days_in_row_ok(i, shift.time_of_day))
                            {
                                if (temp == null || month.getCurrent_work_hours_bilantion() < temp.getMonth().getCurrent_work_hours_bilantion()) //pokud jsme již nenašli někoho lepšího, uložíme si dočasně tohoto pracovníka
                                    temp = worker;
                            }
                        }
                    }
                    if(temp != null) //pokud jsme našli alespoň jednoho vhodného pracovníka, zapíšeme ho do služeb
                    {
                        put_to_work(shift, temp, i);
                        temp.getMonth().add_hours(shift);
                    }

                    else
                        shift_empty = true;

                }
            }
        }
        return shift_empty;
    }

    private void clear_previous_calculation()
    {
        for(Worker worker : workers)
        {
            Month month = worker.get_month_add_if_doesnt_exist();

            month.remove_changeable_shifts();
            month.hours_worked = 0;
            month.nights_worked = 0;


            if(month_index != 0)
            {
                Month last_month = worker.find_month(month_index-1);
                if(last_month != null)
                    month.setCurrent_work_hours_bilantion(last_month.getCurrent_work_hours_bilantion() - worker.hours_per_month);
            }
            else
                month.setCurrent_work_hours_bilantion(-worker.hours_per_month);
        }
        init_days();
    }

    private boolean is_night_ok(Worker.position pos, int i)
    {
        if(!this.days[i].night_has_ZS)
            return pos == Worker.position.ZS;
        else
            return pos == Worker.position.ZS || pos == Worker.position.OS || pos == Worker.position.OSVY;
    }

    private boolean is_ord_ok(Worker.position pos, Shift shift)
    {
        if(shift.ord)
            return pos == Worker.position.ZS;
        else
            return true;
    }

    private void put_to_work(Shift shift, Worker worker, int i)
    {
        shift.worker_assigned = true;
        worker.getMonth().put_to_work(i, shift, this.days[i].week_seq_number);
        if(shift.time_of_day == 2 && !this.days[i].night_has_ZS)
            this.days[i].night_has_ZS = worker.pozice == Worker.position.ZS;
        if((worker.pozice == Worker.position.OSVY && shift.time_of_day != 1)|| worker.pozice == Worker.position.VY)
            this.days[i].day_has_VY = true;
    }

    private boolean is_new_worker_better(Month old_worker, Month new_worker)
    {
        int old_worker_missing_nights = old_worker.get_nights_in_YP() - old_worker.nights_worked;
        int new_worker_missing_nights = new_worker.get_nights_in_YP() - new_worker.nights_worked;

        if (new_worker_missing_nights > old_worker_missing_nights)
            return true;
        else if (new_worker_missing_nights == old_worker_missing_nights)
            return new_worker.getCurrent_work_hours_bilantion() < old_worker.getCurrent_work_hours_bilantion();
        else
            return false;
    }

    private void fill_shifts_with_hand_written()
    {
        for (int i = 0; i < days.length; i++) //projdeme postupně všechny dny v měsíci
        {
            for(Shift shift : days[i].shifts_for_day) //u každého dne všechny dostupné směny
            {
                if(!shift.worker_assigned) //směna zatím není obsazená
                {
                    for (Worker worker : workers) //a pro každou dostupnu směnu se pokusíme najít pracovníka
                    {
                        Month month = worker.getMonth();
                        if(shift.equals_unchangeable(month.actual_work_plan[i]) && !month.actual_work_plan[i].worker_assigned) //našli jsme pracovníka se stejnou směnou
                        {
                            if(month.actual_work_plan[i].isUnchangeable()) //směna je předem vypsaná ručně
                            {
                                shift.worker_assigned = true;
                                month.actual_work_plan[i].worker_assigned = true;
                                if(shift.time_of_day == 2 && !this.days[i].night_has_ZS) //pokud je služba noční a zatím na danou noc není ZS
                                    this.days[i].night_has_ZS = worker.pozice == Worker.position.ZS; //pokud je pracovník ZS je zapsáno, že na noc je ZS
                                if((worker.pozice == Worker.position.OSVY && shift.time_of_day != 1) || worker.pozice == Worker.position.VY) //pokud je služba denní a pracovník je VY/OSVY
                                    this.days[i].day_has_VY = true; //je zapsáno, že na den je VY

                                break; //již jsme našli pracovníka, pokračujeme další směnou
                            }
                        }
                    }
                }
            }
        }
    }

    private void fill_hours(int i, boolean osvy)
    {
        for(Worker worker : workers)
        {
            if((worker.pozice == Worker.position.OSVY && osvy) || worker.pozice != Worker.position.OSVY && !osvy)
            {
                Month month = worker.get_month_add_if_doesnt_exist();
                if(month.actual_work_plan[i] != null)
                    month.add_hours(month.actual_work_plan[i]);
            }
        }
    }
/*
    private void solve_year_plan(boolean first_go)
    {
        Worker temp;

        for(int i = 0; i<days.length; i++) //projdeme postupně všechny dny v měsíci
        {
            for(Shift shift : days[i].shifts_for_day) //u každého dne všechny dostupné směny
            {
                if(!shift.worker_assigned)
                {
                    temp = null;
                    for (Worker worker : workers) //a pro každou dostupnu směnu se pokusíme najít nejvhodnějšího pracovníka
                    {
                        Month month = worker.get_month_add_if_doesnt_exist();
                        if(month.holiday[i] == 0 && month.actual_work_plan[i] == null && worker.no_requests(days[i].week_seq_number, shift.time_of_day)) //nemá ten den dovolenou a již nepracuje
                        {
                            if(worker.is_night_and_days_in_row_ok(i, shift.time_of_day)) //moc dni po sobě, práce po noci
                            {
                                if(shift.is_shift_applicable(month.year_plan[i], worker.pozice, first_go)) //je dostupná směna shodná se směnou v RP?
                                {
                                    if(temp == null || month.current_work_hours_bilantion < temp.get_month().current_work_hours_bilantion) //pokud jsme již nenašli někoho lepšího, uložíme si dočasně tohoto pracovníka
                                        temp = worker;
                                }
                            }
                        }
                    }
                    if(temp != null) //pokud jsme našli alespoň jednoho vhodného pracovníka, zapíšeme ho do služeb
                        put_to_work(shift, temp, i);
                }
            }
        }
    }

     private boolean solve_year_plan_ord()
    {
        Worker temp;
        boolean ord_remains = false;

        for(int i = 0; i<days.length; i++)
        {
            for (Shift shift : days[i].shifts_for_day)
            {
                if (shift.ord)
                {
                    temp = null;
                    for(Worker worker : workers)
                    {
                        Month month = worker.get_month();
                        if(worker.pozice == Worker.position.ZS)
                        {
                            if(month.holiday[i] == 0 && month.actual_work_plan[i] == null)
                            {
                                if(worker.is_night_and_days_in_row_ok(i, shift.time_of_day)) //moc dni po sobě, práce po noci
                                {
                                    if(shift.is_ord_or_VY_applicable(month.year_plan[i]))
                                    {
                                        if(temp == null || month.current_work_hours_bilantion < temp.get_month().current_work_hours_bilantion) //pokud jsme již nenašli někoho lepšího, uložíme si dočasně tohoto pracovníka
                                            temp = worker;
                                    }
                                }
                            }
                        }
                    }
                    if(temp != null) //pokud jsme našli alespoň jednoho vhodného pracovníka, zapíšeme ho do služeb
                        put_to_work(shift, temp, i);
                    else
                        ord_remains = true;
                }
            }
        }
        return ord_remains;
    }

    private boolean solve_year_plan_VY()
    {
        Worker temp;
        boolean needed_VY = false;

        for(int i = 0; i<days.length; i++)
        {
            for (Shift shift : days[i].shifts_for_day)
            {
                if(!shift.worker_assigned)
                {
                    temp = null;
                    for(Worker worker : workers)
                    {
                        Month month = worker.get_month();
                        if(worker.pozice == Worker.position.VY || worker.pozice == Worker.position.OSVY)
                        {
                            if(month.holiday[i] == 0 && month.actual_work_plan[i] == null && worker.no_requests(days[i].week_seq_number, shift.time_of_day))
                            {
                                if(worker.is_night_and_days_in_row_ok(i, shift.time_of_day)) //moc dni po sobě, práce po noci
                                {
                                    if(shift.is_ord_or_VY_applicable(month.year_plan[i]))
                                    {
                                        if(temp == null || month.current_work_hours_bilantion < temp.get_month().current_work_hours_bilantion) //pokud jsme již nenašli někoho lepšího, uložíme si dočasně tohoto pracovníka
                                            temp = worker;
                                    }
                                }
                            }
                        }
                    }
                    if(temp != null) //pokud jsme našli alespoň jednoho vhodného pracovníka, zapíšeme ho do služeb
                        put_to_work(shift, temp, i);
                }
                if(days[i].day_has_VY)
                    break;
            }
            if(!days[i].day_has_VY)
                needed_VY = true;
        }
        return needed_VY;
    }
*/
}
