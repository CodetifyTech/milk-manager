package resimply.hdcompany.milkmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import resimply.hdcompany.milkmanagement.R;
import resimply.hdcompany.milkmanagement.models.Milk;
import resimply.hdcompany.milkmanagement.listener.IOnSingleClickListener;

import java.util.List;

public class MilkAdapter extends RecyclerView.Adapter<MilkAdapter.MilkViewHolder> {

    private final List<Milk> mListMilk;
    private final IManagerMilkListener iManagerMilkListener;

    public interface IManagerMilkListener{
        void editMilk(Milk model);
        void deleteMilk(Milk model);
        void onClickItemMilk(Milk model);
    }

    public MilkAdapter(List<Milk> mListMilk, IManagerMilkListener iManagerMilkListener) {
        this.mListMilk = mListMilk;
        this.iManagerMilkListener = iManagerMilkListener;
    }

    public static class MilkViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvName;
        private final TextView tvUnitName;
        private final ImageView imgEdit;
        private final ImageView imgDelete;
        private final RelativeLayout layoutItem;

        public MilkViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvUnitName = itemView.findViewById(R.id.tv_unit_name);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }

    @NonNull
    @Override
    public MilkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_milk, parent, false);
        return new MilkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MilkViewHolder holder, int position) {
        Milk Milk = mListMilk.get(position);
        if (Milk == null) {
            return;
        }
        holder.tvName.setText(Milk.getName());
        holder.tvUnitName.setText(Milk.getUnitName());

        // Listener
        holder.imgEdit.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                iManagerMilkListener.editMilk(Milk);
            }
        });

        holder.imgDelete.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                iManagerMilkListener.deleteMilk(Milk);
            }
        });

        holder.layoutItem.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                iManagerMilkListener.onClickItemMilk(Milk);
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
