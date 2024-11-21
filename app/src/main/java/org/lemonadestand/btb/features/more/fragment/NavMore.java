package org.lemonadestand.btb.features.more.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.lemonadestand.btb.R;
import org.lemonadestand.btb.activities.LoginActivity;
import org.lemonadestand.btb.utils.Utils;


public class NavMore extends Fragment {
    TextView btnLogOut;

    public NavMore() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nav_more, container, false);

        btnLogOut = view.findViewById(R.id.tvLogOut);

        btnLogOut.setOnClickListener(v -> {
            Utils.saveData(requireActivity(), Utils.TOKEN, "12");
            Intent i = new Intent(requireActivity(), LoginActivity.class);
            startActivity(i);
            requireActivity().finish();
        });

        return view;
    }
}