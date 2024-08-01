package resimply.hdcompany.milkmanagement.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import resimply.hdcompany.milkmanagement.R;
import resimply.hdcompany.milkmanagement.models.Milk;

import java.util.List;

public class SelectMilkAdapter extends ArrayAdapter<Milk> {
    private final Context context;

    public SelectMilkAdapter(@NonNull Context context, int resource, @NonNull List<Milk> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_choose_option, null);
            TextView tvSelected = convertView.findViewById(R.id.tv_selected);
            tvSelected.setText(this.getItem(position).getName());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = View.inflate(context, R.layout.item_drop_down_option, null);
        TextView tvName = view.findViewById(R.id.textview_name);
        tvName.setText(this.getItem(position).getName());
        return view;
    }
}
