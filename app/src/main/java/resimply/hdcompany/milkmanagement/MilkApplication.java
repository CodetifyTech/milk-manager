package resimply.hdcompany.milkmanagement;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/*
NOT DONE YET!!! NOT DONE YET!!! NOT DONE YET!!! NOT DONE YET!!! NOT DONE YET!!! NOT DONE YET!!! NOT
 */
public class MilkApplication extends Application {

    public static MilkApplication get(Context context) {
        return (MilkApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }

    public DatabaseReference getUnitDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference("/my_unit");
    }

    public DatabaseReference getMilkDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference("/milk");
    }

    public DatabaseReference getHistoryDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference("/history");
    }

    public DatabaseReference getQuantityDatabaseReference(long milkId) {
        return FirebaseDatabase.getInstance().getReference("/milk/" + milkId + "/quantity");
    }
}
