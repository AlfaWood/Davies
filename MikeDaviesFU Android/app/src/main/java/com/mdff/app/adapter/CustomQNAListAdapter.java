package com.mdff.app.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdff.app.R;
import com.mdff.app.fragment.QnADetail;
import com.mdff.app.model.QnA;
import com.mdff.app.utility.AppUtil;

import java.util.ArrayList;

/**
 * Created by Deepika.Mishra on 4/25/2018.
 */

public class CustomQNAListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    String[] strQ={"Question","Question1"};
    String[]strA={"Answer","Answer1"};
    private Context context;
    private AppUtil appUtil;
    private ArrayList<QnA> qnaItemList;
    private FragmentManager fragmentManager;


    public CustomQNAListAdapter( String[] strQ, String[]strA, Context context) {
        this.strQ = strQ;
        this.context = context;
        this.strA = strA;
        appUtil=new AppUtil(this.context);

    }
    public CustomQNAListAdapter( ArrayList<QnA> qnaItemList, Context context,FragmentManager fragmentManager) {
        this.context = context;
        appUtil=new AppUtil(this.context);
        this.qnaItemList=qnaItemList;
        this.fragmentManager=fragmentManager;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.qna_list_items, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, final int position) {
        final ItemViewHolder hol;
        if (holder1 instanceof ItemViewHolder) {
            ItemViewHolder holder = (ItemViewHolder) holder1;
            hol=holder;
            holder.tv_question.setText("Q: "+qnaItemList.get(position).getQuestion());
            holder.tv_name.setText(qnaItemList.get(position).getName());
            holder.tv_answer.setText(qnaItemList.get(position).getAnswer());
            holder.tv_answer.post(new Runnable() {
                @Override
                public void run() {
                    int lineCnt = hol.tv_answer.getLineCount();
                    if (lineCnt > 0)
                    {
                        if (hol.tv_answer.getLayout().getEllipsisCount(lineCnt-1) > 0) {
                            hol.tv_readmore.setVisibility(View.VISIBLE);
                        }
                        else {
                            hol.tv_readmore.setVisibility(View.GONE);
                        }

                    }
                }
            });
            holder.qa_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new QnADetail();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("qa", qnaItemList.get(position));
                    fragment.setArguments(bundle);
                    if (fragment != null) {
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        fragmentTransaction.setCustomAnimations(R.animator.enter
                                ,
                                R.animator.exit,
                                R.animator.left_to_right,
                                R.animator.right_to_left

                        );
                        fragmentTransaction.add(R.id.fragment_container, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }

                }
            });
        }
        }



    @Override
    public int getItemCount() {
        return qnaItemList.size();
    }



     class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_question, tv_answer, tv_name,tv_readmore;private LinearLayout qa_layout;
        public ItemViewHolder(View view) {
            super(view);
            tv_answer = (TextView) view.findViewById(R.id.tv_answer);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_question = (TextView) view.findViewById(R.id.tv_question);
            qa_layout=(LinearLayout) view.findViewById(R.id.qa_layout);
            tv_readmore = (TextView) view.findViewById(R.id.tv_readmore);
            Typeface faceMedium = Typeface.createFromAsset(context.getAssets(),
                    "fonts/helvetica-neue-medium.ttf");
            Typeface faceLightItalic = Typeface.createFromAsset(context.getAssets(),
                    "fonts/HelveticaNeue-LightItalic.ttf");
            tv_answer.setTypeface(faceMedium);
            tv_question.setTypeface(faceMedium,Typeface.BOLD);
            tv_name.setTypeface(faceMedium,Typeface.BOLD);
            tv_readmore.setTypeface(faceLightItalic);
        }
    }

    public void clear() {
        qnaItemList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll() {
        notifyDataSetChanged();
    }
}
