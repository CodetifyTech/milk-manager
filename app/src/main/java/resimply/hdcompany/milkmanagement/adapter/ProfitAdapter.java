package resimply.hdcompany.milkmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import resimply.hdcompany.milkmanagement.R;
import resimply.hdcompany.milkmanagement.listener.IOnSingleClickListener;
import resimply.hdcompany.milkmanagement.models.Profit;

import java.util.List;

public class ProfitAdapter extends RecyclerView.Adapter<ProfitAdapter.ProfitViewHolder> {

    private Context mContext;
    private final List<Profit> mListProfit;
    private final IManagerProfitListener iManagerProfitListener;

    public void release() {
        mContext = null;
    }

    public interface IManagerProfitListener {
        void onClickItem(Profit model);
    }

    public ProfitAdapter(Context mContext, List<Profit> mListProfit, IManagerProfitListener iManagerProfitListener) {
        this.mContext = mContext;
        this.mListProfit = mListProfit;
        this.iManagerProfitListener = iManagerProfitListener;
    }

    public static class ProfitViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvStt;
        private final TextView tvMilkName;
        private final TextView tvCurrentQuantity;
        private final TextView tvProfit;
        private final LinearLayout layoutItem;

        public ProfitViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStt = itemView.findViewById(R.id.tv_stt);
            tvMilkName = itemView.findViewById(R.id.tv_milk_name);
            tvCurrentQuantity = itemView.findViewById(R.id.tv_current_quantity);
            tvProfit = itemView.findViewById(R.id.tv_profit);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }

    @NonNull
    @Override
    public ProfitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profit, parent, false);
        return new ProfitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfitViewHolder holder, int position) {
        Profit profit = mListProfit.get(position);
        if (profit == null) {
            return;
        }
        holder.tvStt.setText(String.valueOf(position + 1));
        holder.tvMilkName.setText(profit.getMilkName());
        String strQuantity = profit.getCurrentQuantity() + "";
        holder.tvCurrentQuantity.setText(strQuantity);
        String strProfit = profit.getMilkUnitName();
        holder.tvProfit.setText(strProfit);

        holder.layoutItem.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                iManagerProfitListener.onClickItem(profit);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListProfit != null)
            return mListProfit.size();
        return 0;
    }
}
