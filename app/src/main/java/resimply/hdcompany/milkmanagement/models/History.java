package resimply.hdcompany.milkmanagement.models;


public class History {

    private long id;
    private long MilkId;
    private String MilkName;
    private long unitId;
    private String unitName;
    private int quantity;
    private int price;
    private int totalPrice;
    private long date;
    private boolean add;

    public History(){}

    public History(long id, long MilkId, String MilkName, long unitId, String unitName, int quantity, int price, int totalPrice, long date, boolean add) {
        this.id = id;
        this.MilkId = MilkId;
        this.MilkName = MilkName;
        this.unitId = unitId;
        this.unitName = unitName;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
        this.date = date;
        this.add = add;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getUnitId() {
        return unitId;
    }

    public void setUnitId(long unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }
}
