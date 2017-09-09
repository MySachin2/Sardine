package com.fish.sardine.sardine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
        View rootview = inflater.inflate(R.layout.fragment_main_list_card_view, container, false);
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
                .into(image);
        rootview.setOnClickListener(new View.OnClickListener() {
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
        return rootview;
    }
}
