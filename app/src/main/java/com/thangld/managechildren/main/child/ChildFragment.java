package com.thangld.managechildren.main.child;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.thangld.managechildren.R;
import com.thangld.managechildren.entry.ChildEntry;
import com.thangld.managechildren.main.child.accessibility.EnableAccessibilityFragment;
import com.thangld.managechildren.main.child.deviceadmin.EnableUninstallFragment;
import com.thangld.managechildren.utils.Accessibility;

import java.util.ArrayList;
import java.util.List;

public class ChildFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TYPE = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ListView lvChild;
    private LinearLayout layoutIntroduce;

    private FloatingActionButton btnAdd;
    private View view;

    private FragmentManager fragmentManager;
    private ChildFragment.ListAdapter listAdapter;

    private ChildEntry mChildSelected;
    private Context mContext;

    public ChildFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChildFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChildFragment newInstance(String param1, String param2) {
        ChildFragment fragment = new ChildFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("mc_log", "ChildFragment-" + "onCreateView");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_child, container, false);
        mContext = getActivity();
        initViews();
        setListeners();
        ArrayList<ChildEntry> childs = ((ListChildActivity) getActivity()).getListChild();
        listAdapter = new ChildFragment.ListAdapter(getActivity(), R.layout.child_item, childs);
        lvChild.setAdapter(listAdapter);

        if (childs.size() > 0) {
            // Hien thi list view
            lvChild.setVisibility(View.VISIBLE);
            layoutIntroduce.setVisibility(View.INVISIBLE);
            listAdapter.notifyDataSetChanged();
        } else {
            // An list view
            layoutIntroduce.setVisibility(View.VISIBLE);
            lvChild.setVisibility(View.INVISIBLE);
        }
        fragmentManager = getActivity().getSupportFragmentManager();
        return view;
    }

    public void initViews() {
        lvChild = (ListView) view.findViewById(R.id.lv_child);
        btnAdd = (FloatingActionButton) view.findViewById(R.id.btn_add);
        layoutIntroduce = (LinearLayout) view.findViewById(R.id.introduce_add);
    }

    void setListeners() {
        btnAdd.setOnClickListener(this);
        lvChild.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mChildSelected = listAdapter.getItem(i);

                if (!Accessibility.isAccessibilitySettingsOn(mContext)) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.frameContainer, EnableAccessibilityFragment.newInstance(mChildSelected.getFullName(), mChildSelected.getBirth(),mChildSelected.getIdServer()))
                            .addToBackStack(EnableAccessibilityFragment.FRAGMENT_TAG)
                            .setCustomAnimations(R.anim.left_out, R.anim.right_enter)
                            .commit();
                } else if (!EnableUninstallFragment.isAdminActive(mContext)) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.frameContainer, EnableUninstallFragment.newInstance(mChildSelected.getFullName(), mChildSelected.getBirth(),mChildSelected.getIdServer()))
                            .addToBackStack(EnableAccessibilityFragment.FRAGMENT_TAG)
                            .setCustomAnimations(R.anim.left_out, R.anim.right_enter)
                            .commit();
                } else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.frameContainer, ConfirmChildFragment.newInstance(mChildSelected.getFullName(), mChildSelected.getBirth(),mChildSelected.getIdServer()))
                            .addToBackStack(ConfirmChildFragment.FRAGMENT_TAG)
                            .setCustomAnimations(R.anim.left_out, R.anim.right_enter)
                            .commit();//
                    // Khi da hoan tat thi hien thi den giao dien

                    // Thực hiện đồng bộ


                }





            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                fragmentManager.beginTransaction()
                        .replace(R.id.frameContainer, new AddChildFragment())
                        .addToBackStack(AddChildFragment.FRAGMENT_TAG)
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out).commit();
        }
    }

    public ChildEntry getChildSelected() {
        return mChildSelected;
    }


    public class ListAdapter extends ArrayAdapter<ChildEntry> {

        public ListAdapter(Context context, int resource, List<ChildEntry> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.child_item, null);
            }
            ChildEntry child = getItem(position);
            if (child != null) {
                TextView fullName = (TextView) v.findViewById(R.id.full_name);
                TextView birth = (TextView) v.findViewById(R.id.birth);

                if (fullName != null) {
                    fullName.setText(child.getFullName());
                }
                if (birth != null) {
                    birth.setText(String.valueOf(child.getBirth()));
                }
            }

            return v;
        }

    }


}
