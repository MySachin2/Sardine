package com.fish.sardine.sardine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by GD on 9/9/2017.
 */

public class MainListCardFragment extends Fragment {

    FishClass fishClass;
    TextView eng,mal,price;
    ImageView image;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View rootview = inflater.inflate(R.layout.fragment_main_list_card_view, container, false);
        fishClass = (FishClass) getArguments().getSerializable("fish");
        eng = (TextView) rootview.findViewById(R.id.english_text);
        mal = (TextView) rootview.findViewById(R.id.malayalam_text);
        price = (TextView) rootview.findViewById(R.id.price);
        image = (ImageView) rootview.findViewById(R.id.image);

        eng.setText(fishClass.eng);
        mal.setText(fishClass.mal);
        price.setText(fishClass.price);
        Picasso.with(getActivity())
                .load(fishClass.img)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.ic_bookmark_24dp)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        image.setImageBitmap(bitmap);
                        Palette.from(bitmap)
                                .generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(Palette palette) {
                                        fishClass.rgb = palette.getMutedColor(0xFF0000);
                                    }
                                });
                        rootview.findViewById(R.id.btn_buy).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                                intent.putExtra("fish",fishClass);
                                Pair<View, String> p1 = Pair.create((View)image, "image_trans");
                                Pair<View, String> p2 = Pair.create((View)mal, "malayalam_trans");
                                Pair<View, String> p3 = Pair.create((View)eng, "eng_trans");
                                Pair<View, String> p4 = Pair.create((View)price, "price_trans");

                                ActivityOptionsCompat options = ActivityOptionsCompat.
                                        makeSceneTransitionAnimation(getActivity(), p1,p2,p3,p4);
                                startActivity(intent, options.toBundle());
                            }
                        });
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

        return rootview;
    }


    String getHexCode(int iColor)
    {
        return String.format("#%06X", iColor);
    }
}
