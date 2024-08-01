package resimply.hdcompany.milkmanagement.models;


import java.util.ArrayList;
import java.util.List;

public class Statistical {
    // Statistical: Thống Kê
    private long MilkId;
    private String MilkName;
    private long MilkUnitId;
    private String MilkUnitName;
    private List<History> histories;

    public Statistical(){}

    public Statistical(long MilkId, String MilkName, long MilkUnitId, String MilkUnitName, List<History> histories) {
        this.MilkId = MilkId;
        this.MilkName = MilkName;
        this.MilkUnitId = MilkUnitId;
        this.MilkUnitName = MilkUnitName;
        this.histories = histories;
    }

    public long getMilkId() {
        return MilkId;
    }

    public void setMilkId(long MilkId) {
        this.MilkId = MilkId;
    }

    public String getMilkName() {
        return MilkName;
    }

    public void setMilkName(String MilkName) {
        this.MilkName = MilkName;
    }

    public long getMilkUnitId() {
        return MilkUnitId;
    }

    public void setMilkUnitId(long MilkUnitId) {
        this.MilkUnitId = MilkUnitId;
    }

    public String getMilkUnitName() {
        return MilkUnitName;
    }

    public void setMilkUnitName(String MilkUnitName) {
        this.MilkUnitName = MilkUnitName;
    }

    public List<History> getHistories() {
        if (histories == null) {
            histories = new ArrayList<>();
        }
        return histories;
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
    }

    public int getQuantity() {
        if (histories == null || histories.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (History history : histories) {
            result += history.getQuantity();
        }
        return result;
    }

    public int getTotalPrice() {
        if (histories == null || histories.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (History history : histories) {
            result += history.getTotalPrice();
        }
        return result;
    }
}
