package com.user.snail;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class FragmentTest extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_test, container, false);
        CardView cardView1=view.findViewById(R.id.cardView1);
        CardView cardView2=view.findViewById(R.id.cardView2);
//        final ViewPager viewPager=((ViewPager)((Test2)getActivity()).viewPager);
//        cardView1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(viewPager.getCurrentItem()==4){
//                    getActivity().startActivity(new Intent(getActivity(),Test3.class));
//                    Toast.makeText(getActivity(),"Поздравляем вы прошли весь тест!",Toast.LENGTH_SHORT).show();
//                }else {
//                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
//                }
//            }
//        });
//
//
//        cardView2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(viewPager.getCurrentItem()==4){
//                    getActivity().startActivity(new Intent(getActivity(),Test3.class));
//                    Toast.makeText(getActivity(),"Поздравляем вы прошли весь тест!",Toast.LENGTH_SHORT).show();
//                }else {
//                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
//                }
//            }
//        });

        return view;



    }
}