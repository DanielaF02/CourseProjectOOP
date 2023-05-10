import java.util.UUID;

public class BoughtTicket {

    private int seat;
    private String date;
    private String name;

    private String code;

    private int row;

    public int getRow() {
        return row;
    }

    public int getSeat() {
        return seat;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    BoughtTicket(int row, int seat, String date, String name) {
        this.code = UUID.randomUUID().toString();
        this.row = row;
        this.seat = seat;
        this.date = date;
        this.name = name;
    }
}
