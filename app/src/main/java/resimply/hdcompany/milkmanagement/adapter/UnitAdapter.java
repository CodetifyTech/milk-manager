package resimply.hdcompany.milkmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import resimply.hdcompany.milkmanagement.R;
import resimply.hdcompany.milkmanagement.listener.IOnSingleClickListener;
import resimply.hdcompany.milkmanagement.models.UnitObject;

import java.util.List;

public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.UnitViewHolder> {
    private final List<UnitObject> mListUnit;
    private final IManagerUnitListener iManagerUnitListener;

    public interface IManagerUnitListener {
        void editUnit(UnitObject model);

        void deleteUnit(UnitObject model);
    }

    public UnitAdapter(List<UnitObject> mListUnit, IManagerUnitListener iManagerUnitListener) {
        this.mListUnit = mListUnit;
        this.iManagerUnitListener = iManagerUnitListener;
    }

    public static class UnitViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvName;
        private final ImageView imgEdit;
        private final ImageView imgDelete;

        public UnitViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }

    @NonNull
    @Override
    public UnitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_unit, parent, false);
        return new UnitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UnitViewHolder holder, int position) {
        UnitObject unitObject = mListUnit.get(position);
        if (unitObject == null) {
            return;
        }

        holder.tvName.setText(unitObject.getName());

        // Listener
        holder.imgEdit.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                iManagerUnitListener.editUnit(unitObject);
            }
        });

        holder.imgDelete.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                iManagerUnitListener.deleteUnit(unitObject);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListUnit != null)
            return mListUnit.size();
        return 0;
    }
}
