import java.util.ArrayList;
import java.util.HashMap;

public class Hall {
    private int id;
    private int rowsCount;
    private int seatsPerRow;

    // key - ред, value - Масив от резервирани места
    private final HashMap<Integer, ArrayList<Integer>> bookedSeats = new HashMap<Integer, ArrayList<Integer>>();

    // key - ред, value - Масив от билети за купени места
    private final HashMap<Integer, ArrayList<BoughtTicket>> boughtSeats = new HashMap<Integer, ArrayList<BoughtTicket>>();

    public int getId() {
        return id;
    }

    public int getAllSeats() {
        return this.rowsCount * this.seatsPerRow;
    }

    public boolean bookSeat(int row, int seat, String date, String name) throws Exception {
        BoughtTicket newTicket = new BoughtTicket(row, seat, date, name);
        if (!this.hasFreeSeats(row)) {
            throw new Exception("There are no seats left on this row");
        }

        if (this.boughtSeats.get(row) != null && this.boughtSeats.get(row).contains(newTicket)) {
            return false;
        }

        if (this.bookedSeats.get(row) != null && this.bookedSeats.get(row).contains(seat)) {
            return false;
        }

        if (this.bookedSeats.containsKey(row)) {
            this.bookedSeats.get(row).add(seat);
        } else {
            ArrayList<Integer> seatList = new ArrayList<Integer>();
            seatList.add(seat);
            this.bookedSeats.put(row, seatList);
        }
        return true;
    }

    public boolean unBookSeat(int row, int seat) {
        if (this.bookedSeats.get(row) != null) {
            ArrayList<Integer> seats = this.bookedSeats.get(row);
            seats.remove((Integer) seat);
            return true;
        }
        return false;
    }

    public boolean buyTicket(BoughtTicket ticket) throws Exception {
        if (!this.hasFreeSeats(ticket.getRow())) {
            throw new Exception("There are no seats left on this row");
        }

        if (this.boughtSeats.get(ticket.getRow()) != null && this.boughtSeats.get(ticket.getRow()).contains(ticket)) {
            return false;
        }

        if (this.bookedSeats.get(ticket.getRow()) != null && this.bookedSeats.get(ticket.getRow()).contains(ticket.getSeat())) {
            return false;
        }

        if (this.boughtSeats.containsKey(ticket.getRow())) {
            this.boughtSeats.get(ticket.getRow()).add(ticket);
        } else {
            ArrayList<BoughtTicket> seatList = new ArrayList<BoughtTicket>();
            seatList.add(ticket);
            this.boughtSeats.put(ticket.getRow(), seatList);
        }
        return true;
    }

    private boolean hasFreeSeats(int row) {
        int bookedSeats = 0;
        if (this.bookedSeats.get(row) != null) {
            bookedSeats = this.bookedSeats.get(row).size();
        }

        int boughtSeats = 0;
        if (this.boughtSeats.get(row) != null) {
            boughtSeats = this.boughtSeats.get(row).size();
        }
        return (bookedSeats + boughtSeats) < this.seatsPerRow;
    }

    public Hall(int newId, int newRowsCount, int newSeatsPerRow) {
        this.id = newId;
        this.rowsCount = newRowsCount;
        this.seatsPerRow = newSeatsPerRow;
    }
}