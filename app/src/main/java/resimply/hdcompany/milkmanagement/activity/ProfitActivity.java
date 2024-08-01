package resimply.hdcompany.milkmanagement.activity;


import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import resimply.hdcompany.milkmanagement.MilkApplication;
import resimply.hdcompany.milkmanagement.R;
import resimply.hdcompany.milkmanagement.adapter.ProfitAdapter;
import resimply.hdcompany.milkmanagement.listener.IOnSingleClickListener;
import resimply.hdcompany.milkmanagement.models.History;
import resimply.hdcompany.milkmanagement.constant.GlobalFunction;
import resimply.hdcompany.milkmanagement.models.Milk;
import resimply.hdcompany.milkmanagement.models.Profit;
import resimply.hdcompany.milkmanagement.utils.DateTimeUtils;
import resimply.hdcompany.milkmanagement.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfitActivity extends BaseActivity {

    private TextView tvDateFrom, tvDateTo;
    private RecyclerView rcvData;

    private List<Profit> mListProfit;
    private ProfitAdapter mProfitAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit);

        initToolbar();
        initUi();
        getListProfit();
    }

    private void initToolbar() {
        if (getSupportActionBar() == null) {
            return;
        }
        getSupportActionBar().setTitle(getString(R.string.feature_profit));
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
        tvDateFrom = findViewById(R.id.tv_date_from);
        tvDateTo = findViewById(R.id.tv_date_to);
        rcvData = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvData.setLayoutManager(linearLayoutManager);

        tvDateFrom.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                GlobalFunction.showDatePicker(ProfitActivity.this, tvDateFrom.getText().toString(), date -> {
                    tvDateFrom.setText(date);
                    getListProfit();
                });
            }
        });

        tvDateTo.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                GlobalFunction.showDatePicker(ProfitActivity.this, tvDateTo.getText().toString(), date -> {
                    tvDateTo.setText(date);
                    getListProfit();
                });
            }
        });
    }

    private void getListProfit() {
        MilkApplication.get(this).getHistoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<History> list = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            History history = dataSnapshot.getValue(History.class);
                            if (canAddHistory(history)) {
                                list.add(history);
                            }
                        }
                        handleDataHistories(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showToast(getString(R.string.msg_get_data_error));
                    }
                });
    }

    private boolean canAddHistory(@Nullable History history) {
        if (history == null) {
            return false;
        }
        String strDateFrom = tvDateFrom.getText().toString();
        String strDateTo = tvDateTo.getText().toString();
        if (StringUtil.isEmpty(strDateFrom) && StringUtil.isEmpty(strDateTo)) {
            return true;
        }
        if (StringUtil.isEmpty(strDateFrom) && !StringUtil.isEmpty(strDateTo)) {
            long longDateTo = Long.parseLong(DateTimeUtils.convertDateToTimeStamp(strDateTo));
            return history.getDate() <= longDateTo;
        }
        if (!StringUtil.isEmpty(strDateFrom) && StringUtil.isEmpty(strDateTo)) {
            long longDateFrom = Long.parseLong(DateTimeUtils.convertDateToTimeStamp(strDateFrom));
            return history.getDate() >= longDateFrom;
        }
        long longDateTo = Long.parseLong(DateTimeUtils.convertDateToTimeStamp(strDateTo));
        long longDateFrom = Long.parseLong(DateTimeUtils.convertDateToTimeStamp(strDateFrom));
        return history.getDate() >= longDateFrom && history.getDate() <= longDateTo;
    }

    private void handleDataHistories(List<History> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        if (mListProfit != null) {
            mListProfit.clear();
        } else {
            mListProfit = new ArrayList<>();
        }
        for (History history : list) {
            long MilkId = history.getMilkId();
            if (checkProfitExist(MilkId)) {
                getProfitFromMilkId(MilkId).getHistories().add(history);
            } else {
                Profit profit = new Profit();
                profit.setMilkId(history.getMilkId());
                profit.setMilkName(history.getMilkName());
                profit.setMilkUnitId(history.getUnitId());
                profit.setMilkUnitName(history.getUnitName());
                profit.getHistories().add(history);
                mListProfit.add(profit);
            }
        }
        mProfitAdapter = new ProfitAdapter(this, mListProfit, profit -> {
            Milk Milk = new Milk(profit.getMilkId(), profit.getMilkName(),
                    profit.getMilkUnitId(), profit.getMilkUnitName());
            GlobalFunction.goToMilkDetailActivity(this, Milk);
        });
        rcvData.setAdapter(mProfitAdapter);


    }

    private boolean checkProfitExist(long MilkId) {
        if (mListProfit == null || mListProfit.isEmpty()) {
            return false;
        }
        boolean result = false;
        for (Profit profit : mListProfit) {
            if (MilkId == profit.getMilkId()) {
                result = true;
                break;
            }
        }
        return result;
    }

    private Profit getProfitFromMilkId(long MilkId) {
        Profit result = null;
        for (Profit profit : mListProfit) {
            if (MilkId == profit.getMilkId()) {
                result = profit;
                break;
            }
        }
        return result;
    }

    private int getTotalProfit() {
        if (mListProfit == null || mListProfit.isEmpty()) {
            return 0;
        }

        int total = 0;
        for (Profit profit : mListProfit) {
            total += profit.getProfit();
        }
        return total;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProfitAdapter != null) {
            mProfitAdapter.release();
        }
    }
}