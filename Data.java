import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class Data {
    private Document docData;
    private Document temDate;

    public Document getDocData() {
        return docData;
    }

    public void setDocData(Document docData) {
        this.docData = docData;
    }

    public Document getTempData() {
        return temDate;
    }

    public void setTempData(Document docData) {
        this.temDate = docData;
    }

    private List<Hall> halls = new ArrayList<Hall>();

    public List<Hall> getHalls() {
        return this.halls;
    }
    Data() {
        this.halls.clear();
        this.halls.add(new Hall(1, 10, 10));
        this.halls.add(new Hall(2, 10, 10));
        this.halls.add(new Hall(3, 10, 10));
        this.halls.add(new Hall(4, 10, 10));
        this.halls.add(new Hall(5, 10, 10));
    }
}