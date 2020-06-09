package ru.task.converter;

import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.*;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    private  static  String adr = "https://api.exchangeratesapi.io/latest";
    private  static JsonObject rates;
    private static String[] val = new String[] {
            "USD", "RUB",
            "GBP", "PLN",
            "EUR"
    };
    String from = "USD";
    String to = "USD";



    private void update(){
        if(rates != null) {
            TextView textView = (TextView) findViewById(R.id.report);
            textView.setText(from + " " + to);

            Boolean check = false;
            for (int i = 0; i < val.length; i++) {
                if (val[i] == to) check = true;
            }
            if (!check) to = "USD";

            check = false;
            for (int i = 0; i < val.length; i++) {
                if (val[i] == from) check = true;
            }
            if (!check) from = "RUB";

            TextView num_out = (TextView) findViewById(R.id.out);
            EditText num_in = (EditText) findViewById(R.id.in);
            float in;
            if(num_in.getText().toString().equals(""))
                in = 0.f;
            else
                in = Float.parseFloat(num_in.getText().toString());
            float out = in / rates.get(from).getAsFloat() * rates.get(to).getAsFloat();
            num_out.setText(Float.toString(out));
        }
    }

    public void onButtonClick(View view){
        update();
    }
    AdapterView.OnItemSelectedListener spn_to = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            to = (String)adapterView.getItemAtPosition(i);

            update();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    AdapterView.OnItemSelectedListener spn_from = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            from = (String)adapterView.getItemAtPosition(i);

            update();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            update();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Spinner spinner_from = (Spinner) findViewById(R.id.from);
        Spinner spinner_to = (Spinner) findViewById(R.id.to);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, val);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_from.setAdapter(adapter);
        spinner_to.setAdapter(adapter);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    URLConnection connection = new URL(adr).openConnection();

                    InputStream is = connection.getInputStream();

                    InputStreamReader reader = new InputStreamReader(is);
                    char[] buffer = new char[256];
                    int rc;

                    StringBuilder sb = new StringBuilder();

                    while ((rc = reader.read(buffer)) != -1)
                        sb.append(buffer, 0, rc);

                    reader.close();

                    JsonParser parser = new JsonParser();
                    JsonElement jsonElement = parser.parse(sb.toString());

                    JsonObject rootObject = jsonElement.getAsJsonObject();

                    rates = rootObject.getAsJsonObject("rates");
                    rates.addProperty("EUR", 1.0f);
                } catch (Exception e){
                    TextView textView = (TextView)findViewById(R.id.report);
                    textView.setText("error" + e.toString());

                    e.printStackTrace();
                }
            }
        };

        Thread t = new Thread(r);
        t.start();

        spinner_from.setOnItemSelectedListener(spn_from);
        spinner_to.setOnItemSelectedListener(spn_to);

        TextView num_out = (TextView) findViewById(R.id.out);
        EditText num_in = (EditText) findViewById(R.id.in);

        num_in.addTextChangedListener(textWatcher);
        /*try {

            load();

        } catch (Exception e) {
            TextView textView = (TextView)findViewById(R.id.report);
            textView.setText("error" + e.toString());

            e.printStackTrace();
        }*/
    }
}
