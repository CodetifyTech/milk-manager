package resimply.hdcompany.milkmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import resimply.hdcompany.milkmanagement.R;
import resimply.hdcompany.milkmanagement.listener.IOnSingleClickListener;
import resimply.hdcompany.milkmanagement.models.Milk;

import java.util.List;

public class ManageMilkAdapter extends RecyclerView.Adapter<ManageMilkAdapter.ManageMilkViewHolder> {


    private final List<Milk> mListMilk;
    private final IManageMilkListener iManageMilkListener;

    public interface IManageMilkListener {
        void clickItem(Milk model);
    }

    public ManageMilkAdapter(List<Milk> mListMilk, IManageMilkListener iManageMilkListener) {
        this.mListMilk = mListMilk;
        this.iManageMilkListener = iManageMilkListener;
    }

    public static class ManageMilkViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvName;
        private final TextView tvCurrentQuantity;
        private final RelativeLayout layoutItem;

        public ManageMilkViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCurrentQuantity = itemView.findViewById(R.id.tv_current_quantity);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }

    @NonNull
    @Override
    public ManageMilkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_milk, parent, false);
        return new ManageMilkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageMilkViewHolder holder, int position) {
        Milk Milk = mListMilk.get(position);
        if (Milk == null) {
            return;
        }
        holder.tvName.setText(Milk.getName());
        String strCurrentQuantity = Milk.getQuantity() + " " + Milk.getUnitName();
        holder.tvCurrentQuantity.setText(strCurrentQuantity);

        // Listener
        holder.layoutItem.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                iManageMilkListener.clickItem(Milk);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListMilk != null)
            return mListMilk.size();
        return 0;
    }
}
