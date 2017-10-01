package com.fish.sardine.sardine;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailActivity extends AppCompatActivity {
    FishClass fishClass;
    TextView eng,mal,price,total;
    ImageView image,bg_image;
    EditText quantity;
    Button confirm;
    FloatingActionButton add,remove;
    DatabaseReference mRef;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fishClass = (FishClass) getIntent().getSerializableExtra("fish");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d("RGB",fishClass.rgb+"");
            getWindow().setStatusBarColor(fishClass.rgb);
        }
        setContentView(R.layout.activity_order);
        sharedPreferences = getSharedPreferences(Utils.pref,MODE_PRIVATE);
        editor = sharedPreferences.edit();
        eng = (TextView) findViewById(R.id.english_text);
        mal = (TextView) findViewById(R.id.malayalam_text);
        price = (TextView) findViewById(R.id.price);
        total = (TextView) findViewById(R.id.total);
        confirm = (Button) findViewById(R.id.confirm);
        image = (ImageView) findViewById(R.id.image);
        bg_image = (ImageView) findViewById(R.id.background_image);
        quantity = (EditText) findViewById(R.id.quantity);
        add = (FloatingActionButton) findViewById(R.id.add_button);
        remove = (FloatingActionButton) findViewById(R.id.remove_button);
        mRef = FirebaseDatabase.getInstance().getReference();

        eng.setText(fishClass.eng);
        mal.setText(fishClass.mal);
        price.setText(fishClass.price);
        quantity.setText("1.0");
        double d = Double.parseDouble(quantity.getText().toString());
        double n = d;
        String stripped = price.getText().toString().split("/")[0];
        double p = Integer.parseInt(stripped);
        int t = (int)(n*p);
        total.setText(String.valueOf(t));
        add.setBackgroundTintList(ColorStateList.valueOf(fishClass.rgb));
        remove.setBackgroundTintList(ColorStateList.valueOf(fishClass.rgb));
        quantity.getBackground().mutate().setColorFilter(fishClass.rgb, PorterDuff.Mode.SRC_ATOP);

        Picasso.with(OrderDetailActivity.this)
                .load(fishClass.img)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.ic_bookmark_24dp)
                .into(image);
        Picasso.with(OrderDetailActivity.this)
                .load(fishClass.img)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.ic_bookmark_24dp)
                .into(bg_image);
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
                    confirm.setClickable(true);
                }
                catch (Exception e)
                {
                    Log.e("Exception",e.toString());
                    confirm.setClickable(false);
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
                    if(t>=0) {
                        quantity.setText(String.valueOf(n));
                        total.setText(String.valueOf(t));
                    }
                    confirm.setClickable(true);
                }
                catch (Exception e)
                {
                    Log.e("Exception",e.toString());
                    confirm.setClickable(false);

                }
            }
        });

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                try {
                    Log.d("TAG",source + " " + dest);
                    if (dest.charAt(dest.length() - 1) == '.') {
                        if (!source.equals("2") && !source.equals("5") && !source.equals("7") && !source.equals("0")) {
                            return "";
                        }
                        else
                        {
                            if(source.equals("2") || source.equals("7"))
                            {
                                String input =  source + "5";
                                return input;
                            }
                            else
                            {
                                String input = source + "0";
                                return input;
                            }

                        }
                    }
                    else if(dest.charAt(dest.length() - 3) == '.')
                    {
                        if(source.equals(""))
                        {
                            if(source.equals("2") || source.equals("7"))
                            {
                                String input =  Character.toString(dest.charAt(dest.length()-1));
                                return input;
                            }
                            else
                            {
                                String input =  Character.toString(dest.charAt(dest.length()-1));
                                return input;
                            }
                        }
                        return "";
                    }
                }
                catch (IndexOutOfBoundsException e)
                {
                    confirm.setClickable(false);
                }
                return null;
            }
        };
        quantity.setFilters(new InputFilter[] { filter });
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
                    confirm.setClickable(true);
                }
                catch (Exception e)
                {
                    //Log.e("Exception",e.toString());
                    total.setText("0");
                    confirm.setClickable(false);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        confirm.setClickable(true);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderDetailActivity.this);
                alertDialogBuilder.setMessage("Buy " + fishClass.mal + " for ₹" + total.getText().toString());
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                try
                                {
                                    Float q = Float.parseFloat(quantity.getText().toString());
                                    Integer i = (int)(q*100)%25;
                                    Log.d("INT",String.valueOf(i));
                                    Log.d("FLOAT",String.valueOf(q));
                                    arg0.dismiss();
                                    if(i==0)
                                    {
                                        Date dNow = new Date();
                                        SimpleDateFormat ft =
                                                new SimpleDateFormat("dd-MM-yyyy");
                                        final Map<String,String> map = new HashMap<String, String>();
                                        map.put("Quantity",quantity.getText().toString());
                                        map.put("Total",total.getText().toString());
                                        map.put("English Name",fishClass.eng);
                                        map.put("Malayalam Name",fishClass.mal);
                                        map.put("Price",fishClass.price);
                                        map.put("Status","Not Paid");
                                        map.put("Order Date",ft.format(dNow));
                                        final ProgressDialog progressDialog = new ProgressDialog(OrderDetailActivity.this);
                                        progressDialog.setTitle("Uploading");
                                        progressDialog.show();
                                       final  String phone = sharedPreferences.getString("phone","");
                                        mRef.child("Order").child("By User").child(phone).push().setValue(map, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                progressDialog.dismiss();
                                                finish();
                                            }
                                        });
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "Invalid Quantity", Toast.LENGTH_LONG).show();
                                        quantity.setText("1.0");
                                    }
                                }
                                catch (Exception e)
                                {
                                    arg0.dismiss();
                                    confirm.setClickable(false);
                                }
                            }
                        });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }


}
