package org.zankio.ccudata.train.source.remote;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zankio.ccudata.base.model.HttpResponse;
import org.zankio.ccudata.base.model.JSON;
import org.zankio.ccudata.base.model.Request;
import org.zankio.ccudata.base.source.SourceProperty;
import org.zankio.ccudata.base.source.annotation.DataType;
import org.zankio.ccudata.base.source.annotation.Important;
import org.zankio.ccudata.base.source.annotation.Order;
import org.zankio.ccudata.base.source.http.HTTPJSONSource;
import org.zankio.ccudata.base.source.http.annotation.Method;
import org.zankio.ccudata.train.model.HMAC_SHA1;
import org.zankio.ccudata.train.model.TrainRequest;
import org.zankio.ccudata.train.model.TrainTimetable;

import java.io.IOException;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@SuppressWarnings("DefaultAnnotationParam")

@Method("GET")

@Order(SourceProperty.Level.MIDDLE)
@Important(SourceProperty.Level.HIGH)
@DataType(PTXTrainStationTimetableSource.TYPE)
public class PTXTrainStationTimetableSource extends HTTPJSONSource<TrainRequest, TrainTimetable>{
    public final static String TYPE = "TRAIN_STATION_TIMETABLE";
    private static final String URL_TRAIN_TIMETABLE = "https://ptx.transportdata.tw/MOTC/v2/Rail/TRA/DailyTimetable/Station/%s/%s";
    public static Request<TrainTimetable, TrainRequest> request(String no, String date) {
        return new Request<>(TYPE, new TrainRequest(no, date), TrainTimetable.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void initHTTPRequest(Request<TrainTimetable, TrainRequest> request) {
        super.initHTTPRequest(request);
        TrainRequest trainRequest = request.args;
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        /* 為了ptx hmac check */
        String xdate = getServerTime();
        String SignDate = "x-date: " + xdate;
        String APPID = "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF";
        String APPKey = "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF";
        String Signature="";
        try {
            //取得加密簽章
            Signature = HMAC_SHA1.Signature(SignDate, APPKey);
        } catch (SignatureException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String sAuth = "hmac username=\"" + APPID + "\", algorithm=\"hmac-sha1\", headers=\"x-date\", signature=\"" + Signature + "\"";
        httpParameter(request)
                .url(String.format(URL_TRAIN_TIMETABLE, trainRequest.no, trainRequest.date))
                .headers("Authorization",sAuth)
                .headers("x-date",xdate)
                .queryStrings("$format", "JSON")
                .queryStrings("$filter",
                        String.format(
                                "DepartureTime ge '%s' and EndingStationID ne '%s'",
                                simpleDateFormat.format(calendar.getTime()),
                                trainRequest.no
                        )
                )
                .queryStrings("$orderby", "DepartureTime");
    }

    @Override
    protected TrainTimetable parse(Request<TrainTimetable, TrainRequest> request, HttpResponse response, JSON json) throws JSONException, IOException {
        TrainTimetable trainTimetable = new TrainTimetable();
        List<TrainTimetable.Item> up = new ArrayList<>();
        List<TrainTimetable.Item> down = new ArrayList<>();
        Log.d("JSON-LOG",request.toString());
        JSONArray traininfos = json.array();
        for (int i = 0; i < traininfos.length(); i++) {
            JSONObject traininfo = traininfos.getJSONObject(i);
            TrainTimetable.Item item = trainTimetable.new Item();
            item.trainNo = traininfo.getString("TrainNo");
            item.to = traininfo.getString("EndingStationName");
            item.departure = traininfo.getString("DepartureTime").substring(0, 5);
            item.trainType = parseTrainClassification(traininfo.getString("TrainTypeName"));

            if (traininfo.getInt("Direction") == 0) up.add(item);
            else down.add(item);
        }

        trainTimetable.up = up.toArray(new TrainTimetable.Item[0]);
        trainTimetable.down = down.toArray(new TrainTimetable.Item[0]);

        return trainTimetable;
    }

    private String parseTrainClassification(String trainClassificationName) {
        if (trainClassificationName == null) return "";
        if (trainClassificationName.contains("太魯閣")) return "太魯閣";
        if (trainClassificationName.contains("普悠瑪")) return "普悠瑪";
        if (trainClassificationName.contains("自強")) return "自強";
        if (trainClassificationName.contains("復興")) return "復興";
        if (trainClassificationName.contains("莒光")) return "莒光";
        if (trainClassificationName.contains("區間快")) return "區間快";
        if (trainClassificationName.contains("區間")) return "區間";
        return "";
    }

    private String parseLineType(int tripLine) {
        return tripLine == 0 ? "" :
                tripLine == 1 ? "山" : "海";
    }

    private String parseDelay(String delay) {
        if (delay == null || "".equals(delay)) return "";
        else if ("0".equals(delay)) return "準點";
        else return String.format("晚 %s 分", delay);
    }

    private static String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

}
