package resimply.hdcompany.milkmanagement.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import resimply.hdcompany.milkmanagement.fragments.MilkDetailAddedFragment;
import resimply.hdcompany.milkmanagement.fragments.MilkDetailUsedFragment;
import resimply.hdcompany.milkmanagement.models.Milk;

public class MyPagerAdapter extends FragmentStateAdapter {

    private final Milk mMilk;

    public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity, Milk mMilk) {
        super(fragmentActivity);
        this.mMilk = mMilk;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new MilkDetailUsedFragment(mMilk);
        }
        return new MilkDetailAddedFragment(mMilk);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
