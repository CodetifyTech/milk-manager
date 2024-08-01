package resimply.hdcompany.milkmanagement.activity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import resimply.hdcompany.milkmanagement.MilkApplication;
import resimply.hdcompany.milkmanagement.R;
import resimply.hdcompany.milkmanagement.adapter.MilkAdapter;
import resimply.hdcompany.milkmanagement.adapter.SelectUnitAdapter;
import resimply.hdcompany.milkmanagement.constant.GlobalFunction;
import resimply.hdcompany.milkmanagement.listener.IOnSingleClickListener;
import resimply.hdcompany.milkmanagement.models.Milk;
import resimply.hdcompany.milkmanagement.models.History;
import resimply.hdcompany.milkmanagement.models.UnitObject;
import resimply.hdcompany.milkmanagement.utils.StringUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListMilkActivity extends BaseActivity {

    private List<Milk> mListMilk;
    private MilkAdapter mMilkAdapter;

    private List<UnitObject> mListUnit;
    private UnitObject mUnitSelected;

    private EditText edtSearchName;
    private String mKeySearch;
    private final ChildEventListener mChildEventListener = new ChildEventListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
            Milk Milk = dataSnapshot.getValue(Milk.class);
            if (Milk == null || mListMilk == null || mMilkAdapter == null) {
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
            mMilkAdapter.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            Milk Milk = dataSnapshot.getValue(Milk.class);
            if (Milk == null || mListMilk == null || mListMilk.isEmpty() || mMilkAdapter == null) {
                return;
            }
            for (int i = 0; i < mListMilk.size(); i++) {
                if (Milk.getId() == mListMilk.get(i).getId()) {
                    mListMilk.set(i, Milk);
                    break;
                }
            }
            mMilkAdapter.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            Milk Milk = dataSnapshot.getValue(Milk.class);
            if (Milk == null || mListMilk == null || mListMilk.isEmpty() || mMilkAdapter == null) {
                return;
            }
            for (Milk MilkObject : mListMilk) {
                if (Milk.getId() == MilkObject.getId()) {
                    mListMilk.remove(MilkObject);
                    break;
                }
            }
            mMilkAdapter.notifyDataSetChanged();
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
        setContentView(R.layout.activity_list_milk);

        initToolbar();
        initUi();
        getListUnit();
        getListMilk();
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.feature_list_menu));
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
                    GlobalFunction.hideSoftKeyboard(ListMilkActivity.this);
                }
            }
        });

        FloatingActionButton fabAdd = findViewById(R.id.fab_add_data);
        fabAdd.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onClickAddOrEditMilk(null);
            }
        });

        LinearLayout layoutDeleteAll = findViewById(R.id.layout_delete_all);
        layoutDeleteAll.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mListMilk == null || mListMilk.isEmpty()) {
                    return;
                }
                onClickDeleteAllMilk();
            }
        });

        RecyclerView rcvMilk = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvMilk.setLayoutManager(linearLayoutManager);

        mListUnit = new ArrayList<>();
        mListMilk = new ArrayList<>();

        mMilkAdapter = new MilkAdapter(mListMilk, new MilkAdapter.IManagerMilkListener() {
            @Override
            public void editMilk(Milk Milk) {
                onClickAddOrEditMilk(Milk);
            }

            @Override
            public void deleteMilk(Milk Milk) {
                onClickDeleteMilk(Milk);
            }

            @Override
            public void onClickItemMilk(Milk Milk) {
                GlobalFunction.goToMilkDetailActivity(ListMilkActivity.this, Milk);
            }
        });
        rcvMilk.setAdapter(mMilkAdapter);
        rcvMilk.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fabAdd.hide();
                } else {
                    fabAdd.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void getListUnit() {
        MilkApplication.get(this).getUnitDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mListUnit != null) mListUnit.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UnitObject unitObject = dataSnapshot.getValue(UnitObject.class);
                    mListUnit.add(0, unitObject);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast(getString(R.string.msg_get_data_error));
            }
        });
    }

    public void getListMilk() {
        if (mListMilk != null) {
            mListMilk.clear();
            MilkApplication.get(this).getMilkDatabaseReference().removeEventListener(mChildEventListener);
        }
        MilkApplication.get(this).getMilkDatabaseReference().addChildEventListener(mChildEventListener);
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

    private void onClickAddOrEditMilk(Milk Milk) {
        if (mListUnit == null || mListUnit.isEmpty()) {
            showToast(getString(R.string.msg_list_unit_require));
            return;
        }

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_add_and_edit_milk);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Get view
        final TextView tvTitleDialog = dialog.findViewById(R.id.tv_title_dialog);
        final EditText edtMilkName = dialog.findViewById(R.id.edt_milk_name);
        final TextView tvDialogCancel = dialog.findViewById(R.id.tv_dialog_cancel);
        final TextView tvDialogAction = dialog.findViewById(R.id.tv_dialog_action);
        final Spinner spnUnit = dialog.findViewById(R.id.spinner_unit);

        SelectUnitAdapter selectUnitAdapter = new SelectUnitAdapter(this, R.layout.item_choose_option, mListUnit);
        spnUnit.setAdapter(selectUnitAdapter);
        spnUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mUnitSelected = selectUnitAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set data
        if (Milk == null) {
            tvTitleDialog.setText(getString(R.string.add_milk_name));
            tvDialogAction.setText(getString(R.string.action_add));
        } else {
            tvTitleDialog.setText(getString(R.string.edit_milk_name));
            tvDialogAction.setText(getString(R.string.action_edit));
            edtMilkName.setText(Milk.getName());
            spnUnit.setSelection(getPositionUnitUpdate(Milk));
        }

        // Set listener
        tvDialogCancel.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dialog.dismiss();
            }
        });

        tvDialogAction.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String strMilkName = edtMilkName.getText().toString().trim();
                if (StringUtil.isEmpty(strMilkName)) {
                    showToast(getString(R.string.msg_milk_name_require));
                    return;
                }

                if (isMilkExist(strMilkName)) {
                    showToast(getString(R.string.msg_milk_exist));
                    return;
                }

                if (Milk == null) {
                    long id = System.currentTimeMillis();
                    Milk MilkObject = new Milk();
                    MilkObject.setId(id);
                    MilkObject.setName(strMilkName);
                    MilkObject.setUnitId(mUnitSelected.getId());
                    MilkObject.setUnitName(mUnitSelected.getName());

                    MilkApplication.get(ListMilkActivity.this).getMilkDatabaseReference()
                            .child(String.valueOf(id)).setValue(MilkObject, (error, ref) -> {
                                GlobalFunction.hideSoftKeyboard(ListMilkActivity.this, edtMilkName);
                                showToast(getString(R.string.msg_add_milk_success));
                                dialog.dismiss();
                                GlobalFunction.hideSoftKeyboard(ListMilkActivity.this);
                            });
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", strMilkName);
                    map.put("unitId", mUnitSelected.getId());
                    map.put("unitName", mUnitSelected.getName());

                    MilkApplication.get(ListMilkActivity.this).getMilkDatabaseReference()
                            .child(String.valueOf(Milk.getId())).updateChildren(map, (error, ref) -> {
                                GlobalFunction.hideSoftKeyboard(ListMilkActivity.this, edtMilkName);
                                showToast(getString(R.string.msg_edit_milk_success));
                                dialog.dismiss();
                                GlobalFunction.hideSoftKeyboard(ListMilkActivity.this);
                                updateMilkInHistory(new Milk(Milk.getId(), strMilkName,
                                        mUnitSelected.getId(), mUnitSelected.getName()));
                            });
                }
            }
        });

        dialog.show();
    }

    private int getPositionUnitUpdate(Milk Milk) {
        if (mListUnit == null || mListUnit.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < mListUnit.size(); i++) {
            if (Milk.getUnitId() == mListUnit.get(i).getId()) {
                return i;
            }
        }
        return 0;
    }

    private boolean isMilkExist(String MilkName) {
        if (mListMilk == null || mListMilk.isEmpty()) {
            return false;
        }

        for (Milk Milk : mListMilk) {
            if (MilkName.equals(Milk.getName())) {
                return true;
            }
        }

        return false;
    }

    private void onClickDeleteMilk(Milk Milk) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_delete), (dialogInterface, i)
                        -> MilkApplication.get(ListMilkActivity.this).getMilkDatabaseReference()
                        .child(String.valueOf(Milk.getId())).removeValue((error, ref) -> {
                            showToast(getString(R.string.msg_delete_milk_success));
                            GlobalFunction.hideSoftKeyboard(ListMilkActivity.this);
                        }))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void onClickDeleteAllMilk() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.msg_confirm_delete_all))
                .setPositiveButton(getString(R.string.delete_all), (dialogInterface, i)
                        -> MilkApplication.get(ListMilkActivity.this).getMilkDatabaseReference()
                        .removeValue((error, ref) -> {
                            showToast(getString(R.string.msg_delete_all_milk_success));
                            GlobalFunction.hideSoftKeyboard(ListMilkActivity.this);
                        }))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void updateMilkInHistory(Milk Milk) {
        MilkApplication.get(this).getHistoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<History> list = new ArrayList<>();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            History history = dataSnapshot.getValue(History.class);
                            if (history != null && history.getMilkId() == Milk.getId()) {
                                list.add(history);
                            }
                        }
                        MilkApplication.get(ListMilkActivity.this).getHistoryDatabaseReference()
                                .removeEventListener(this);
                        if (list.isEmpty()) {
                            return;
                        }
                        for (History history : list) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("MilkName", Milk.getName());
                            map.put("unitId", Milk.getUnitId());
                            map.put("unitName", Milk.getUnitName());

                            MilkApplication.get(ListMilkActivity.this).getHistoryDatabaseReference()
                                    .child(String.valueOf(history.getId()))
                                    .updateChildren(map);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}