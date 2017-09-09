package com.fish.sardine.sardine;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;

public class OrderDetailActivity extends AppCompatActivity {
    FishClass fishClass;
    TextView eng,mal,price,total;
    ImageView image;
    EditText quantity;
    FloatingActionButton add,remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        eng = (TextView) findViewById(R.id.english_text);
        mal = (TextView) findViewById(R.id.malayalam_text);
        price = (TextView) findViewById(R.id.price);
        total = (TextView) findViewById(R.id.total);

        image = (ImageView) findViewById(R.id.image);
        quantity = (EditText) findViewById(R.id.quantity);
        add = (FloatingActionButton) findViewById(R.id.add_button);
        remove = (FloatingActionButton) findViewById(R.id.remove_button);

        fishClass = (FishClass) getIntent().getSerializableExtra("fish");
        eng.setText(fishClass.eng);
        mal.setText(fishClass.mal);
        price.setText(fishClass.price);
        quantity.setText("1.0");
        total.setText(fishClass.price);
        Picasso.with(OrderDetailActivity.this)
                .load(fishClass.img)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.ic_bookmark_24dp)
                .into(image);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    String stripped = price.getText().toString().split("/")[0];
                    double p = Integer.parseInt(stripped);
                    double d = Double.parseDouble(quantity.getText().toString());
                    double n = d+0.25;
                    int t = (int)(n*p);

                    quantity.setText(String.valueOf(n));
                    total.setText(String.valueOf(t));
                }
                catch (Exception e)
                {
                    Log.e("Exception",e.toString());
                }
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    double d = Double.parseDouble(quantity.getText().toString());
                    double n = d-0.25;
                    String stripped = price.getText().toString().split("/")[0];
                    double p = Integer.parseInt(stripped);
                    int t = (int)(n*p);

                    quantity.setText(String.valueOf(n));
                    total.setText(String.valueOf(t));
                }
                catch (Exception e)
                {
                    Log.e("Exception",e.toString());

                }
            }
        });
        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int j, int i1, int i2) {
                try
                {
                    String stripped = price.getText().toString().split("/")[0];
                    double p = Integer.parseInt(stripped);
                    double d = Double.parseDouble(quantity.getText().toString());
                    int t = (int)(d*p);
                    total.setText(String.valueOf(t));
                }
                catch (Exception e)
                {
                    Log.e("Exception",e.toString());

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }
}
