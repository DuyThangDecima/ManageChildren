package com.thangld.managechildren.main.parent;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.thangld.managechildren.R;

public abstract class ListNavContentFragment extends Fragment implements View.OnClickListener {


    protected GridView listViewContainer;
    protected View view;
    protected Context mContext;
    protected Cursor mCursor;
    protected CursorAdapter cursorAdapter;

    protected static final String TITLE = "title";
    protected static final String INFO = "infor";


    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public ListNavContentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = getActivity();
        view = inflater.inflate(R.layout.fragment_list_nav_content, container, false);
        listViewContainer = (GridView) view.findViewById(R.id.lv_nav_container);
        setClickListeners();
        setAdapterContent();
        checkOnline();
        return view;
    }

    protected abstract void setAdapterContent();

    protected abstract void checkOnline();

    protected abstract void setClickListeners();

    @Override
    public void onClick(View view) {

    }

}
