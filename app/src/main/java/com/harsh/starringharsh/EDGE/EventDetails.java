package com.harsh.starringharsh.EDGE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.Calendar;

public class EventDetails extends AppCompatActivity {

    TextView tvDet, tvName, tvCont1, tvCont2, tvUpcoming;
    Button bCall1, bCall2, bReminder;
    LinearLayout llUpcoming;
    String name, det, linkadd, details, cont1, cont2, up;
    int date, month, hr, min;
    long phn1, phn2;
    int p=0;
    Calendar cal, calR;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Master master;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);


        master = new Master();

        sharedPreferences = getSharedPreferences("EventsChoice", Context.MODE_PRIVATE);
        name = sharedPreferences.getString("Name", "not found");
        editor = sharedPreferences.edit();

        init();

        tvName.setText(name);

        linkadd = master.link.get(name);


        tvDet.setText(det);

        new BackFetch().execute();


    }

    void init()
    {
        progress = new ProgressDialog(this);
        tvName = (TextView) findViewById(R.id.tvDetailsName);
        tvDet = (TextView) findViewById(R.id.tvDetailsDet);
        tvCont1 = (TextView) findViewById(R.id.tvDetailsCont1);
        tvCont2 = (TextView) findViewById(R.id.tvDetailsCont2);
        tvUpcoming = (TextView) findViewById(R.id.tvDetailsUpcoming);
        bCall1 = (Button) findViewById(R.id.bDetailsCall1);
        bCall2 = (Button) findViewById(R.id.bDetailsCall2);
        llUpcoming = (LinearLayout) findViewById(R.id.llDetailsUpcoming);
        bReminder = (Button) findViewById(R.id.bDetailsUpcoming);
    }



    class BackFetch extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setIndeterminate(false);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setCancelable(true);
            progress.setMessage("Fetching Information...");
            progress.show();
            System.out.println("PRE");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(linkadd);
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                String str, newDet="";
                while ((str = br.readLine()) != null) {
                    newDet += str + "\n";
                }
                br.close();
                det = newDet;
                editor.putString(name, newDet);
                editor.commit();
            } catch (Exception e) {
                System.out.println("Failed");
                det = sharedPreferences.getString(name, master.eventDetails.get(name));
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            System.out.println("POST");
            super.onPostExecute(aVoid);
            progress.dismiss();
            BufferedReader br = new BufferedReader(new StringReader(det));

            try {
                details =  br.readLine();
                cont1 =  br.readLine();
                phn1 = Long.parseLong(br.readLine());
                cont2 =  br.readLine();;
                phn2 = Long.parseLong(br.readLine());
                up =  br.readLine();
                date = Integer.parseInt(br.readLine());
                month = Integer.parseInt(br.readLine());
                hr = Integer.parseInt(br.readLine());
                min = Integer.parseInt(br.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }


            tvDet.setText(details);
            tvCont1.setText(cont1);
            bCall1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phn1));
                    startActivity(intent);
                }
            });

            if(phn1!=phn2)
            {
                tvCont2.setVisibility(View.VISIBLE);
                bCall2.setVisibility(View.VISIBLE);

                tvCont2.setText(cont2);
                bCall2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phn2));
                        startActivity(intent);
                    }
                });
            }

            if(up.equalsIgnoreCase("Y"))
            {
            cal = Calendar.getInstance();
            int dateS = cal.get(Calendar.DATE);
            int monthS = cal.get(Calendar.MONTH);
            int hrS = cal.get(Calendar.HOUR);
            int minS = cal.get(Calendar.MINUTE);
            tvUpcoming.setText(dateS + "/" + monthS + "/2017 " + hrS + ":" + minS);
            tvUpcoming.setText("Yes");
            calR = Calendar.getInstance();
            calR.set(2016, month, date, hr, min);
            if(cal.getTimeInMillis() < calR.getTimeInMillis())
            {
                llUpcoming.setVisibility(View.VISIBLE);
                bReminder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra("beginTime", calR.getTimeInMillis());
                        intent.putExtra("allDay", false);
                        intent.putExtra("endTime", calR.getTimeInMillis()+120*60*1000);
                        intent.putExtra("title", "Reminder for event: " + name);
                        startActivity(intent);
                    }
                });
                }
            }

        }
    }

}
