public class Hall {
    private int id;
    private int rowsCount;
    private int seatsPerRow;

    public int getId() {
        return id;
    }

    public int rowsCount() {
        return rowsCount;
    }

    public int seatsPerRow() {
        return seatsPerRow;
    }

    public void setId(int newId) {
        this.id = newId;
    }

    public void setRowsCount(int newRowsCount) {
        this.id = newRowsCount;
    }

    public void setSeatsPerRow(int newSeatsPerRow) {
        this.id = newSeatsPerRow;
    }

    public Hall(int newId, int newRowsCount, int newSeatsPerRow) {
        this.id = newId;
        this.rowsCount = newRowsCount;
        this.seatsPerRow = newSeatsPerRow;
    }
}
