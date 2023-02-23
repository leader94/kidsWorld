package com.ps.kidsworld.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ps.kidsworld.R;
import com.ps.kidsworld.utils.CommonService;

public class MainListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_list, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        ImageView imgView = (ImageView) view.findViewById(R.id.imageView1);
//        imgView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CommonService.curSceneName = "animals";
//                startSceneFragment();
//
//            }
//        });

        ImageView imgView2 = (ImageView) view.findViewById(R.id.imageView2);
        imgView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonService.curSceneName = "capitalAlphabets";
                startSceneFragment();

            }
        });

        ImageView imageView3 = (ImageView) view.findViewById(R.id.imageView3);
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonService.curSceneName = "numbers";
                startSceneFragment();

            }
        });


    }

    private void startSceneFragment() {
        CommonService.replaceFragment((AppCompatActivity) getActivity(), R.id.frame_layout, new SceneFragment());
    }
}
