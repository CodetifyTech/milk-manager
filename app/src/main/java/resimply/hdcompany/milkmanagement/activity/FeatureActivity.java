package resimply.hdcompany.milkmanagement.activity;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import resimply.hdcompany.milkmanagement.R;
import resimply.hdcompany.milkmanagement.constant.Constants;
import resimply.hdcompany.milkmanagement.constant.GlobalFunction;
import resimply.hdcompany.milkmanagement.models.Feature;
import resimply.hdcompany.milkmanagement.adapter.FeatureAdapter;

import java.util.ArrayList;
import java.util.List;

public class FeatureActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature);
        initUi();
    }

    private void initUi() {
        RecyclerView rcvFeature = findViewById(R.id.rcv_feature);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rcvFeature.setLayoutManager(gridLayoutManager);

        FeatureAdapter featureAdapter = new FeatureAdapter(getListFeatures(), this::onClickItemFeature);
        rcvFeature.setAdapter(featureAdapter);
    }

    private List<Feature> getListFeatures() {
        List<Feature> list = new ArrayList<>();
        list.add(new Feature(Feature.FEATURE_MANAGE_UNIT, R.drawable.ic_manage_unit, getString(R.string.feature_manage_unit)));
        list.add(new Feature(Feature.FEATURE_LIST_MENU, R.drawable.ic_list_milk, getString(R.string.feature_list_menu)));
        list.add(new Feature(Feature.FEATURE_ADD_MILK, R.drawable.ic_add_milk, getString(R.string.feature_add_milk)));
        list.add(new Feature(Feature.FEATURE_MANAGE_MILK, R.drawable.ic_mange_milk_quan_ly, getString(R.string.feature_manage_milk)));
        list.add(new Feature(Feature.FEATURE_MILK_OUT_OF_STOCK, R.drawable.ic_milk_kho_trong, getString(R.string.feature_milk_out_of_stock)));
        list.add(new Feature(Feature.FEATURE_COST, R.drawable.ic_cost, getString(R.string.feature_cost)));
        list.add(new Feature(Feature.FEATURE_PROFIT, R.drawable.ic_profit, getString(R.string.feature_profit)));

        return list;
    }

    @Override
    public void onBackPressed() {
        showDialogExitApp();
    }

    private void showDialogExitApp() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.msg_confirm_exit_app))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> finishAffinity())
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    public void onClickItemFeature(Feature feature) {
        switch (feature.getId()) {
            case Feature.FEATURE_LIST_MENU:
                GlobalFunction.startActivity(this, ListMilkActivity.class);
                break;

            case Feature.FEATURE_MANAGE_UNIT:
                GlobalFunction.startActivity(this, UnitActivity.class);
                break;

            case Feature.FEATURE_ADD_MILK:
                GlobalFunction.startActivity(this, HistoryActivity.class);
                break;

            case Feature.FEATURE_MILK_USED:
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.KEY_INTENT_MILK_USED, true);
                GlobalFunction.startActivity(this, HistoryActivity.class, bundle);
                break;

            case Feature.FEATURE_MANAGE_MILK:
                GlobalFunction.startActivity(this, ManageMilkActivity.class);
                break;

            case Feature.FEATURE_MILK_OUT_OF_STOCK:
                GlobalFunction.startActivity(this, MilkOutOfStockActivity.class);
                break;

            case Feature.FEATURE_REVENUE:
                goToStatisticalActivity(Constants.TYPE_REVENUE);
                break;

            case Feature.FEATURE_COST:
                goToStatisticalActivity(Constants.TYPE_COST);
                break;

            case Feature.FEATURE_PROFIT:
                GlobalFunction.startActivity(this, ProfitActivity.class);
                break;

            case Feature.FEATURE_MILK_POPULAR:
                goToListDevicePopular();
                break;

            default:
                break;
        }
    }

    private void goToStatisticalActivity(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_TYPE_STATISTICAL, type);
        GlobalFunction.startActivity(this, StatisticalActivity.class, bundle);
    }

    private void goToListDevicePopular() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_TYPE_STATISTICAL, Constants.TYPE_REVENUE);
        bundle.putBoolean(Constants.KEY_MILK_POPULAR, true);
        GlobalFunction.startActivity(this, StatisticalActivity.class, bundle);
    }
}