package com.mdff.app.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdff.app.R;
import com.mdff.app.activity.Home;
import com.mdff.app.model.QnA;

/**
 * Created by Deepika.Mishra on 4/26/2018.
 */

public class QnADetail extends Fragment {
    private View rootView;private LinearLayout backLayout;
    private TextView tv_answer,tv_question,tv_name;
    private QnA qnA;private Home home;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.qna_detail, container, false);
        qnA = (QnA) getArguments().getSerializable("qa");
        home = ((Home) getActivity());
       initialzefindViewIds();
        rootView.setBackgroundColor(Color.WHITE);
        return rootView;
    }

    private void initialzefindViewIds() {
        tv_answer=(TextView)rootView.findViewById(R.id.tv_answer);
        tv_question=(TextView)rootView.findViewById(R.id.tv_question);
        tv_name=(TextView)rootView.findViewById(R.id.tv_name);
        backLayout= (LinearLayout) rootView.findViewById(R.id.backLayout);
//        home.appUtil.setFontTypeFaceLight(tv_answer);
        Typeface faceMedium = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/helvetica-neue-medium.ttf");
        tv_answer.setTypeface(faceMedium);
        tv_question.setTypeface(faceMedium,Typeface.BOLD);
        tv_name.setTypeface(faceMedium,Typeface.BOLD);

        if (qnA!=null) {
            tv_name.setText(qnA.getName());
            tv_question.setText("Q: "+qnA.getQuestion());
//            tv_answer.setText(Html.fromHtml("<font color='#b60e0e'><b>A: </b></font>"+ qnA.getAnswer()));
            tv_answer.setText(qnA.getAnswer());
        }
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }
}
