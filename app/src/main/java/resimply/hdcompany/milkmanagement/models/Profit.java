package resimply.hdcompany.milkmanagement.models;


import java.util.ArrayList;
import java.util.List;

public class Profit {

    private long MilkId;
    private String MilkName;
    private long MilkUnitId;
    private String MilkUnitName;
    private List<History> histories;

    public Profit(){}

    public Profit(long MilkId, String MilkName, long MilkUnitId, String MilkUnitName, List<History> histories) {
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

    public int getCurrentQuantity() {
        if (histories == null || histories.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (History history : histories) {
            if (history.isAdd()) {
                result += history.getQuantity();
            } else {
                result -= history.getQuantity();
            }
        }
        return result;
    }

    public int getProfit() {
        if (histories == null || histories.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (History history : histories) {
            if (history.isAdd()) {
                result -= history.getTotalPrice();
            } else {
                result += history.getTotalPrice();
            }
        }
        return result;
    }

}
