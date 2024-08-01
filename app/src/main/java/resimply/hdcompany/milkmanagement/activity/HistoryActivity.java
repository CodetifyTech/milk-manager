package resimply.hdcompany.milkmanagement.activity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import resimply.hdcompany.milkmanagement.MilkApplication;
import resimply.hdcompany.milkmanagement.R;
import resimply.hdcompany.milkmanagement.adapter.HistoryAdapter;
import resimply.hdcompany.milkmanagement.adapter.SelectMilkAdapter;
import resimply.hdcompany.milkmanagement.constant.Constants;
import resimply.hdcompany.milkmanagement.constant.GlobalFunction;
import resimply.hdcompany.milkmanagement.listener.IOnSingleClickListener;
import resimply.hdcompany.milkmanagement.models.History;

import resimply.hdcompany.milkmanagement.models.Milk;
import resimply.hdcompany.milkmanagement.utils.DateTimeUtils;
import resimply.hdcompany.milkmanagement.utils.StringUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistoryActivity extends BaseActivity {

    private TextView mTvDateSelected;
    private TextView tvTotalPrice;

    private List<Milk> mListMilk;

    private List<History> mListHistory;
    private HistoryAdapter mHistoryAdapter;

    private Milk mMilkSelected;
    private boolean isMilkUsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getDataIntent();
        initToolbar();
        initUi();
        getListMilks();
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        isMilkUsed = bundle.getBoolean(Constants.KEY_INTENT_MILK_USED);
    }

    private void initToolbar() {
        if (getSupportActionBar() == null) {
            return;
        }
        if (isMilkUsed) {
            getSupportActionBar().setTitle(getString(R.string.feature_milk_used));
        } else {
            getSupportActionBar().setTitle(getString(R.string.feature_add_milk));
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        TextView tvListTitle = findViewById(R.id.tv_list_title);
        if (isMilkUsed) {
            tvListTitle.setText(getString(R.string.list_milk_used));
        } else {
            tvListTitle.setText(getString(R.string.list_milk_buy));
        }

        tvTotalPrice = findViewById(R.id.tv_total_price);

        mTvDateSelected = findViewById(R.id.tv_date_selected);
        String currentDate = new SimpleDateFormat(DateTimeUtils.DEFAULT_FORMAT_DATE, Locale.ENGLISH).format(new Date());
        mTvDateSelected.setText(currentDate);
        getListHistoryMilkOfDate(currentDate);

        RelativeLayout layoutSelectDate = findViewById(R.id.layout_select_date);
        layoutSelectDate.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                GlobalFunction.showDatePicker(HistoryActivity.this, mTvDateSelected.getText().toString(), date -> {
                    mTvDateSelected.setText(date);
                    getListHistoryMilkOfDate(date);
                });
            }
        });

        FloatingActionButton fabAddData = findViewById(R.id.fab_add_data);
        fabAddData.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onClickAddOrEditHistory(null);
            }
        });

        RecyclerView rcvHistory = findViewById(R.id.rcv_history);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvHistory.setLayoutManager(linearLayoutManager);

        mListMilk = new ArrayList<>();
        mListHistory = new ArrayList<>();

        mHistoryAdapter = new HistoryAdapter(mListHistory, false,
                new HistoryAdapter.IManagerHistoryListener() {
                    @Override
                    public void editHistory(History history) {
                        onClickAddOrEditHistory(history);
                    }

                    @Override
                    public void deleteHistory(History history) {
                        onClickDeleteHistory(history);
                    }

                    @Override
                    public void onClickItemHistory(History history) {
                        Milk Milk = new Milk(history.getMilkId(), history.getMilkName(),
                                history.getUnitId(), history.getUnitName());
                        GlobalFunction.goToMilkDetailActivity(HistoryActivity.this, Milk);
                    }
                });
        rcvHistory.setAdapter(mHistoryAdapter);
        rcvHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fabAddData.hide();
                } else {
                    fabAddData.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void getListMilks() {
        MilkApplication.get(this).getMilkDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mListMilk != null) mListMilk.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Milk Milk = dataSnapshot.getValue(Milk.class);
                    mListMilk.add(0, Milk);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast(getString(R.string.msg_get_data_error));
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getListHistoryMilkOfDate(@NonNull String date) {
        long longDate = Long.parseLong(DateTimeUtils.convertDateToTimeStamp(date));
        MilkApplication.get(this).getHistoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (mListHistory != null) mListHistory.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            History history = dataSnapshot.getValue(History.class);
                            if (history != null) {
                                if (longDate == history.getDate()) {
                                    addHistoryToList(history);
                                }
                            }
                        }
                        mHistoryAdapter.notifyDataSetChanged();

                        // Calculator price
                        String strTotalPrice = getTotalPrice() + Constants.CURRENCY;
                        tvTotalPrice.setText(strTotalPrice);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showToast(getString(R.string.msg_get_data_error));
                    }
                });
    }

    private void addHistoryToList(History history) {
        if (history == null) {
            return;
        }
        if (isMilkUsed) {
            if (!history.isAdd()) {
                mListHistory.add(0, history);
            }
        } else {
            if (history.isAdd()) {
                mListHistory.add(0, history);
            }
        }
    }

    private void onClickAddOrEditHistory(History history) {
        if (mListMilk == null || mListMilk.isEmpty()) {
            showToast(getString(R.string.msg_list_milk_require));
            return;
        }

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_history);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Get view
        final TextView tvTitleDialog = dialog.findViewById(R.id.tv_title_dialog);
        final Spinner spnMilk = dialog.findViewById(R.id.spinner_milk);
        final EditText edtQuantity = dialog.findViewById(R.id.edt_quantity);
        final TextView tvUnitName = dialog.findViewById(R.id.tv_unit_name);
        final EditText edtPrice = dialog.findViewById(R.id.edt_price);
        final TextView tvDialogCancel = dialog.findViewById(R.id.tv_dialog_cancel);
        final TextView tvDialogAdd = dialog.findViewById(R.id.tv_dialog_add);

        // Set data
        if (isMilkUsed) {
            tvTitleDialog.setText(getString(R.string.feature_milk_used));
        } else {
            tvTitleDialog.setText(getString(R.string.feature_add_milk));
        }

        SelectMilkAdapter selectMilkAdapter = new SelectMilkAdapter(this, R.layout.item_choose_option, mListMilk);
        spnMilk.setAdapter(selectMilkAdapter);
        spnMilk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMilkSelected = selectMilkAdapter.getItem(position);
                tvUnitName.setText(mMilkSelected.getUnitName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (history != null) {
            if (isMilkUsed) {
                tvTitleDialog.setText(getString(R.string.edit_history_used));
            } else {
                tvTitleDialog.setText(getString(R.string.edit_history_add));
            }
            spnMilk.setSelection(getPositionMilkUpdate(history));
            edtQuantity.setText(String.valueOf(history.getQuantity()));
            edtPrice.setText(String.valueOf(history.getPrice()));
        }

        // Listener
        tvDialogCancel.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dialog.dismiss();
            }
        });

        tvDialogAdd.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String strQuantity = edtQuantity.getText().toString().trim();
                String strPrice = edtPrice.getText().toString().trim();
                if (StringUtil.isEmpty(strQuantity) || StringUtil.isEmpty(strPrice)) {
                    showToast(getString(R.string.msg_enter_full_infor));
                    return;
                }

                if (history == null) {
                    History history = new History();
                    history.setId(System.currentTimeMillis());
                    history.setMilkId(mMilkSelected.getId());
                    history.setMilkName(mMilkSelected.getName());
                    history.setUnitId(mMilkSelected.getUnitId());
                    history.setUnitName(mMilkSelected.getUnitName());
                    history.setQuantity(Integer.parseInt(strQuantity));
                    history.setPrice(Integer.parseInt(strPrice));
                    history.setTotalPrice(history.getQuantity() * history.getPrice());
                    history.setAdd(!isMilkUsed);
                    String strDate = DateTimeUtils.convertDateToTimeStamp(mTvDateSelected.getText().toString());
                    history.setDate(Long.parseLong(strDate));

                    MilkApplication.get(HistoryActivity.this).getHistoryDatabaseReference()
                            .child(String.valueOf(history.getId()))
                            .setValue(history, (error, ref) -> {
                                if (isMilkUsed) {
                                    showToast(getString(R.string.msg_used_milk_success));
                                } else {
                                    showToast(getString(R.string.msg_add_milk_success));
                                }
                                changeQuantity(history.getMilkId(), history.getQuantity(), !isMilkUsed);
                                GlobalFunction.hideSoftKeyboard(HistoryActivity.this);
                                dialog.dismiss();
                            });
                    return;
                }

                // Edit history
                Map<String, Object> map = new HashMap<>();
                map.put("MilkId", mMilkSelected.getId());
                map.put("MilkName", mMilkSelected.getName());
                map.put("unitId", mMilkSelected.getUnitId());
                map.put("unitName", mMilkSelected.getUnitName());
                map.put("quantity", Integer.parseInt(strQuantity));
                map.put("price", Integer.parseInt(strPrice));
                map.put("totalPrice", Integer.parseInt(strQuantity) * Integer.parseInt(strPrice));

                MilkApplication.get(HistoryActivity.this).getHistoryDatabaseReference()
                        .child(String.valueOf(history.getId()))
                        .updateChildren(map, (error, ref) -> {
                            GlobalFunction.hideSoftKeyboard(HistoryActivity.this);
                            if (isMilkUsed) {
                                showToast(getString(R.string.msg_edit_used_history_success));
                            } else {
                                showToast(getString(R.string.msg_edit_add_history_success));
                            }
                            changeQuantity(history.getMilkId(), Integer.parseInt(strQuantity) - history.getQuantity(), !isMilkUsed);

                            dialog.dismiss();
                        });
            }
        });

        dialog.show();
    }

    private void changeQuantity(long MilkId, int quantity, boolean isAdd) {
        MilkApplication.get(HistoryActivity.this).getQuantityDatabaseReference(MilkId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Integer currentQuantity = snapshot.getValue(Integer.class);
                        if (currentQuantity != null) {
                            int totalQuantity;
                            if (isAdd) {
                                totalQuantity = currentQuantity + quantity;
                            } else {
                                totalQuantity = currentQuantity - quantity;
                            }
                            MilkApplication.get(HistoryActivity.this).getQuantityDatabaseReference(MilkId).removeEventListener(this);
                            updateQuantityToFirebase(MilkId, totalQuantity);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void updateQuantityToFirebase(long MilkId, int quantity) {
        MilkApplication.get(HistoryActivity.this).getQuantityDatabaseReference(MilkId)
                .setValue(quantity);
    }

    private int getPositionMilkUpdate(History history) {
        if (mListMilk == null || mListMilk.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < mListMilk.size(); i++) {
            if (history.getMilkId() == mListMilk.get(i).getId()) {
                return i;
            }
        }
        return 0;
    }

    private int getTotalPrice() {
        if (mListHistory == null || mListHistory.isEmpty()) {
            return 0;
        }

        int totalPrice = 0;
        for (History history : mListHistory) {
            totalPrice += history.getTotalPrice();
        }
        return totalPrice;
    }

    private void onClickDeleteHistory(History history) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_delete), (dialogInterface, i)
                        -> MilkApplication.get(HistoryActivity.this).getHistoryDatabaseReference()
                        .child(String.valueOf(history.getId()))
                        .removeValue((error, ref) -> {
                            if (isMilkUsed) {
                                showToast(getString(R.string.msg_delete_used_history_success));
                            } else {
                                showToast(getString(R.string.msg_delete_add_history_success));

                            }
                            changeQuantity(history.getMilkId(), history.getQuantity(), isMilkUsed);
                            GlobalFunction.hideSoftKeyboard(HistoryActivity.this);
                        }))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }
}