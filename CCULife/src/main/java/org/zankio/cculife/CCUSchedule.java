package org.zankio.cculife;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CCUSchedule {
    //

    public final Context context;

    public CCUSchedule(Context context) {
        this.context = context;
    }

    public static final String[] SCHEDULE_TITLE = {"108學年度"};
    public static final String[] SCHEDULE_FILE = {"schedule/108schedule"};

    private String getScheduleRawDate(String fileName) {
        InputStream is;
        try {
            is = context.getAssets().open(fileName);
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            return new String(buffer,"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public Schedule[] getScheduleList(){
        Pattern pattern;
        Matcher matcher;
        Calendar date = null;
        List<Item> list;
        Schedule[] result = new Schedule[SCHEDULE_TITLE.length];

        pattern = Pattern.compile("^(\\d{0,4})[ \t]+(\\d{0,2})[ \t]+(\\d{0,2})[ \t]+([一二三四五六日]?)[ \t]*([^\n" +
                "]+)", Pattern.MULTILINE);

        for (int i = 0; i < SCHEDULE_TITLE.length; i++) {
            result[i] = new Schedule();
            result[i].Name = SCHEDULE_TITLE[i];

            matcher = pattern.matcher(getScheduleRawDate(SCHEDULE_FILE[i]));

            list = new ArrayList<>();
            while (matcher.find()) {
                Item item = new Item();
                if (!"".equals(matcher.group(1))) {
                    int year = Integer.parseInt(matcher.group(1));
                    int month = Integer.parseInt(matcher.group(2));
                    int day = Integer.parseInt(matcher.group(3));
                    date = new GregorianCalendar(year, month - 1, day);
                }

                item.Title = matcher.group(5);
                item.Date = date;
                list.add(item);
            }

            result[i].list = list.toArray(new Item[list.size()]);
        }


        return result;
    }

    public class Schedule {
        public String Name;
        public Item[] list;
    }

    public class Item {
        private static final String weekName = "日一二三四五六";
        public Calendar Date;
        public String Title;

        public boolean isToday(int today_year, int today_day_of_year) {
            return this.Date.get(Calendar.YEAR) == today_year
                    && this.Date.get(Calendar.DAY_OF_YEAR) == today_day_of_year;
        }

        public String toDateString() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM / dd", Locale.US);
            return String.format("%s (%s)",
                    simpleDateFormat.format(this.Date.getTime()),
                    weekName.charAt(this.Date.get(Calendar.DAY_OF_WEEK) - 1));
        }
    }
}
