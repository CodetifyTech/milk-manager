package resimply.hdcompany.milkmanagement.activity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import resimply.hdcompany.milkmanagement.MilkApplication;
import resimply.hdcompany.milkmanagement.R;
import resimply.hdcompany.milkmanagement.adapter.ManageMilkAdapter;
import resimply.hdcompany.milkmanagement.constant.GlobalFunction;
import resimply.hdcompany.milkmanagement.listener.IOnSingleClickListener;
import resimply.hdcompany.milkmanagement.models.Milk;

import resimply.hdcompany.milkmanagement.utils.StringUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class ManageMilkActivity extends BaseActivity {

    private List<Milk> mListMilk;
    private ManageMilkAdapter mManageMilkAdapter;

    private EditText edtSearchName;
    private String mKeySearch;
    private final ChildEventListener mChildEventListener = new ChildEventListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
            Milk Milk = dataSnapshot.getValue(Milk.class);
            if (Milk == null || mListMilk == null || mManageMilkAdapter == null) {
                return;
            }
            if (StringUtil.isEmpty(mKeySearch)) {
                mListMilk.add(0, Milk);
            } else {
                if (GlobalFunction.getTextSearch(Milk.getName().toLowerCase())
                        .contains(GlobalFunction.getTextSearch(mKeySearch).toLowerCase())) {
                    mListMilk.add(0, Milk);
                }
            }
            mManageMilkAdapter.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            Milk Milk = dataSnapshot.getValue(Milk.class);
            if (Milk == null || mListMilk == null || mListMilk.isEmpty() || mManageMilkAdapter == null) {
                return;
            }
            for (int i = 0; i < mListMilk.size(); i++) {
                if (Milk.getId() == mListMilk.get(i).getId()) {
                    mListMilk.set(i, Milk);
                    break;
                }
            }
            mManageMilkAdapter.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            Milk Milk = dataSnapshot.getValue(Milk.class);
            if (Milk == null || mListMilk == null || mListMilk.isEmpty() || mManageMilkAdapter == null) {
                return;
            }
            for (Milk MilkObject : mListMilk) {
                if (Milk.getId() == MilkObject.getId()) {
                    mListMilk.remove(MilkObject);
                    break;
                }
            }
            mManageMilkAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            showToast(getString(R.string.msg_get_data_error));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_milk);

        initToolbar();
        initUi();
        getListMilk();
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.feature_manage_milk));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initUi() {
        edtSearchName = findViewById(R.id.edt_search_name);
        ImageView imgSearch = findViewById(R.id.img_search);
        imgSearch.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                searchMilk();
            }
        });

        edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchMilk();
                return true;
            }
            return false;
        });

        edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    mKeySearch = "";
                    getListMilk();
                    GlobalFunction.hideSoftKeyboard(ManageMilkActivity.this);
                }
            }
        });

        RecyclerView rcvMilk = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvMilk.setLayoutManager(linearLayoutManager);

        mListMilk = new ArrayList<>();
        mManageMilkAdapter = new ManageMilkAdapter(mListMilk, Milk
                -> GlobalFunction.goToMilkDetailActivity(this, Milk));
        rcvMilk.setAdapter(mManageMilkAdapter);
    }

    public void getListMilk() {
        if (mListMilk != null) {
            mListMilk.clear();
            MilkApplication.get(this.getApplicationContext()).getMilkDatabaseReference().removeEventListener(mChildEventListener);
        }
        MilkApplication.get(this.getApplicationContext()).getMilkDatabaseReference().addChildEventListener(mChildEventListener);
    }

    private void searchMilk() {
        if (mListMilk == null || mListMilk.isEmpty()) {
            GlobalFunction.hideSoftKeyboard(this);
            return;
        }
        mKeySearch = edtSearchName.getText().toString().trim();
        getListMilk();
        GlobalFunction.hideSoftKeyboard(this);
    }
}