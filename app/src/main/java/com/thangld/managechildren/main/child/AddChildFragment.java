package com.thangld.managechildren.main.child;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.thangld.managechildren.R;
import com.thangld.managechildren.cloud.UrlPattern;
import com.thangld.managechildren.cloud.resource.AccountResource;
import com.thangld.managechildren.cloud.resource.ChildResource;
import com.thangld.managechildren.entry.ChildEntry;
import com.thangld.managechildren.main.LoadingFragment;
import com.thangld.managechildren.main.account.AccountActivity;
import com.thangld.managechildren.main.account.CustomToast;
import com.thangld.managechildren.main.account.Utils;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.utils.DialogCustom;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddChildFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddChildFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddChildFragment extends Fragment implements View.OnClickListener {
    public static String FRAGMENT_TAG = "AddChildFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private Spinner spinnerBirth;
    private Button btnContinue;
    private TextView tvFullName;
    private Activity mActivity;

    private FragmentManager mFragmentManager;

    public AddChildFragment() {
        // Required empty public constructor
    }

    public static AddChildFragment newInstance(String param1, String param2) {
        AddChildFragment fragment = new AddChildFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mActivity = getActivity();
        view = inflater.inflate(R.layout.fragment_add_child, container, false);
        mFragmentManager = getActivity().getSupportFragmentManager();
        initViews();
        setOnListeners();


        return view;
    }

    public void initViews() {
        spinnerBirth = (Spinner) view.findViewById(R.id.spinner_birth);
        tvFullName = (TextView) view.findViewById(R.id.full_name);
        btnContinue = (Button) view.findViewById(R.id.btn_continue);


        Calendar calendar = Calendar.getInstance();
        int yearCurrent = calendar.get(Calendar.YEAR);
        int index = 17;
        String[] births = new String[index];
        for (int i = 1; i <= index; i++) {
            births[i - 1] = String.valueOf(yearCurrent - i);
        }
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, births);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinnerBirth.setAdapter(spinnerArrayAdapter);

    }

    public void setOnListeners() {
        btnContinue.setOnClickListener(this);
    }

    // Kiểm tra trước khi đăng nhập
    private boolean checkValidation() {
        String fullName = tvFullName.getText().toString();

        // patter for email id
        Pattern p = Pattern.compile(Utils.regEx);
        boolean result = false;
        // Check rỗng
        if (fullName.equals("") || fullName.length() == 0) {
            new CustomToast().showToast(getActivity(), R.drawable.error, getString(R.string.all_field_required));
            return false;
        }
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_continue:
                if (checkValidation()) {
                    try {
                        JSONObject data = new JSONObject();
                        String fullName = tvFullName.getText().toString();
                        int birth = Integer.valueOf(spinnerBirth.getSelectedItem().toString());

                        data.put(UrlPattern.FULL_NAME_KEY, fullName);
                        data.put(UrlPattern.BIRTH_KEY, birth);

                        AddChildTask addChildTask = new AddChildTask(mActivity);
                        addChildTask.execute(data);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class AddChildTask extends AsyncTask<JSONObject, Void, String> {
        private Context context;

        public AddChildTask(Context context) {
            super();
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Fragment loadingFragment = new LoadingFragment();
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.replace(R.id.frameContainer, loadingFragment);
            transaction.addToBackStack(LoadingFragment.FRAGMENT_TAG);
            transaction.commit();
        }

        @Override
        protected String doInBackground(JSONObject... jsonObjects) {

            JSONObject jsonObject = jsonObjects[0];
            String respond = new ChildResource(context).post(jsonObject);
            return respond;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mFragmentManager.popBackStack();
            if (s == null || s.length() == 0) {
                // check network and
                Toast.makeText(context, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            } else {
                try {
                    Log.d("mc_log","respond " + s);
                    JSONObject json = new JSONObject(s);

                    int status = json.getInt(UrlPattern.STATUS_KEY);
                    if (status == UrlPattern.STATUS_SUCCESS) {
                        // phải gửi về child_id,
                        String id_server = json.getString(UrlPattern.CHILD_ID_KEY);
                        String full_name = json.getString(UrlPattern.FULL_NAME_KEY);
                        int birth = json.getInt(UrlPattern.BIRTH_KEY);
                        ChildModel.QueryHelper.insertChild(mActivity, id_server, full_name, birth, ChildModel.Contents.ACTIVE_TRUE);
                        ((ListChildActivity) getActivity()).addListChild(new ChildEntry(full_name, birth));
                        mFragmentManager.popBackStack();
                    } else {
                        String error_id = json.getString(UrlPattern.MSG_KEY);
                        if (error_id.equals(UrlPattern.ERROR_AUTH)) {
                            new CustomToast().showToast(getActivity(), R.drawable.error, getString(R.string.session_expired_login_again));
                            AccountResource.setLogout(context);
                            Intent intent = new Intent(context, AccountActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);

                        } else if (error_id.equals(UrlPattern.ERROR_EXIST)) {
                            new CustomToast().showToast(getActivity(), R.drawable.error, getString(R.string.child_exist));
                        } else {
                            new CustomToast().showToast(getActivity(), R.drawable.error, getString(R.string.error_try_again));
                            mFragmentManager.popBackStack();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    notifyError(context.getString(R.string.error_try_again));
                    mFragmentManager.popBackStack();
                }
            }
        }

        /**
         * Quay về màn hình đăng nhập và hiển thị lỗi
         *
         * @param content
         */
        private void notifyError(String content) {
            DialogCustom.showDialog(getActivity(), 0, getString(R.string.re_try), content);
//            new CustomToast().showToast(context, view, content);
            getFragmentManager().popBackStack();
        }
    }
}
