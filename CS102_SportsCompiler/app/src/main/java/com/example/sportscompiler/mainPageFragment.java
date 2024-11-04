package com.example.sportscompiler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link mainPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mainPageFragment extends Fragment {

    private TextView day1Txt, day2Txt, day3Txt;
    private ImageView day1Img, day2Img, day3Img;
    private int day;
    private String cityName = "Ankara";
    private String APIKey = "9f5051ab914d08850742c634382c54ee";


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public mainPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment mainPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static mainPageFragment newInstance(String param1, String param2) {
        mainPageFragment fragment = new mainPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);
        day1Txt = view.findViewById(R.id.day1Txt);
        day2Txt = view.findViewById(R.id.day2Txt);
        day3Txt = view.findViewById(R.id.day3Txt);

        day1Img = view.findViewById(R.id.day1Img);
        day2Img = view.findViewById(R.id.day2Img);
        day3Img = view.findViewById(R.id.day3Img);

        updateWeatherConditions(1, day1Txt, day1Img); //for tomorrow
        updateWeatherConditions(2, day2Txt, day2Img); //tomorrow + 1
        updateWeatherConditions(3, day3Txt, day3Img); //tomorrow + 2

        return view;
    }

    private void updateWeatherConditions(int day, TextView txtView, ImageView imgView)
    {
        // Run the task on a background thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                String result = getWeatherData();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateWeatherUI(result, handler, day, txtView, imgView);
                    }
                });
            }
        });
    }

    private String getWeatherData() {
        StringBuilder result = new StringBuilder();
        try {
            String urlStr = "https://api.openweathermap.org/data/2.5/forecast?q=" + cityName + "&appid=" + APIKey;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            //To get the return of API
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }


    private void updateWeatherUI(String result, Handler handler, int day, TextView txtView, ImageView imgView) {
        try {
            // Parse JSON data
            JSONObject jsonObject = new JSONObject(result);
            JSONArray list = jsonObject.getJSONArray("list");

            for(int i = 0 ; i < list.length() ; i++)
            {
                JSONObject weatherData = list.getJSONObject(i);
                String dateStr = weatherData.getString("dt_txt");

                if(isWantedDate(day, dateStr, txtView) && dateStr.contains("12:00:00")) //to get on wanted day at 12
                {
                    JSONObject wantedDay = weatherData.getJSONObject("main");
                    double temperature = wantedDay.getDouble("temp") - 273.15;

                    String weatherDescription = weatherData
                            .getJSONArray("weather")
                            .getJSONObject(0)
                            .getString("description");

                    String iconCode = weatherData
                            .getJSONArray("weather")
                            .getJSONObject(0)
                            .getString("icon");

                    txtView.setText(String.format("%.1f°C, %s", temperature, weatherDescription));

                    //To download icon and set it to image view:
                    String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                URL url = new URL(iconUrl);
                                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        imgView.setImageBitmap(bitmap);
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    break;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isWantedDate(int day, String dayStr, TextView txt)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, day);
        String tomorrowDate = sdf.format(tomorrow.getTime());
        return dayStr.startsWith(tomorrowDate);
    }
}