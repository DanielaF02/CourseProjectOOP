import org.w3c.dom.*;
import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static boolean prevClosed = true;
    private static Data data = new Data();
    private static XMLParser parser = new XMLParser();
    private static String openFile = "";

    public static void main(String args[]) throws Exception {
        try {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Choose your command: ");
                String line = scanner.nextLine();
                String[] commands = line.split(" ");
                String mainCommand = commands[0];

                if (mainCommand.equals("exit")) {
                    System.out.println("Exiting the program...");
                    break;
                }

                switch (mainCommand) {
                    case "open":
                        onOpenCommand(commands);
                        break;
                    case "close":
                        onCloseCommand();
                        break;
                    case "save":
                        onSaveCommand();
                        break;
                    case "saveas":
                        onSaveAsCommand(commands);
                        break;
                    case "help":
                        onHelpCommand();
                        break;
                    case "addevent":
                        onAddEventCommand(commands);
                        break;
                    case "book":
                        onBookEventCommand(commands);
                        break;
                    case "unbook":
                        onUnBookEventCommand(commands);
                        break;
                    case "buy":
                        onBuyCommand(commands);
                        break;
                    case "check":
                        onCheckCommand(commands);
                        break;
                    case "freeseats":
                        onFreeSeatsCommand(commands);
                        break;
                    default:
                        System.out.println("Invalid command");
                        break;
                }
            }
        }
        catch (Exception e) {
            System.out.println("There was an error: " + e.getMessage());
        }
    }

    private static void setBookedAndBoughtSeats(Document doc) throws Exception {
        NodeList bookedTickets = doc.getElementsByTagName("booked-ticket");
        if (bookedTickets != null) {
            for (int i = 0; i < bookedTickets.getLength(); i++) {
                Node bookedTicket = bookedTickets.item(i);
                NamedNodeMap attrs = bookedTicket.getAttributes();
                int hallId = Integer.parseInt(attrs.getNamedItem("hall").getTextContent());
                Hall hall = data.getHalls().get(hallId);
                int row = Integer.parseInt(attrs.getNamedItem("row").getTextContent());
                int seat = Integer.parseInt(attrs.getNamedItem("seat").getTextContent());
                String date = attrs.getNamedItem("date").getTextContent();
                String name = attrs.getNamedItem("name").getTextContent();
                hall.bookSeat(row, seat, date, name);
            }
        }

        NodeList boughtTickets = doc.getElementsByTagName("bought-ticket");
        if (boughtTickets != null) {
            for (int i = 0; i < boughtTickets.getLength(); i++) {
                Node boughtTicket = boughtTickets.item(i);
                NamedNodeMap attrs = boughtTicket.getAttributes();
                int hallId = Integer.parseInt(attrs.getNamedItem("hall").getTextContent());
                Hall hall = data.getHalls().get(hallId);
                int row = Integer.parseInt(attrs.getNamedItem("row").getTextContent());
                int seat = Integer.parseInt(attrs.getNamedItem("seat").getTextContent());
                String date = attrs.getNamedItem("date").getTextContent();
                String name = attrs.getNamedItem("name").getTextContent();
                BoughtTicket ticket = new BoughtTicket(row, seat, date, name);
                ticket.setCode(attrs.getNamedItem("code").getTextContent());
                hall.buyTicket(ticket);
            }
        }
    }

    private static void onOpenCommand(String[] commands) throws Exception {
        prevClosed = false;
        String fileName = commands[1];
        File file = new File(fileName);
        openFile = fileName;

        if (file.createNewFile()) {
            Document doc = parser.getBuilder().newDocument();
            doc.appendChild(doc.createElement("data"));
            data.setTempData(doc);
        } else {
            Document doc = parser.getBuilder().parse(file);
            data.setTempData(doc);
            setBookedAndBoughtSeats(doc);
        }
        System.out.println("Successfully opened " + fileName);
    }

    private static void onCloseCommand() {
        prevClosed = true;
        Document doc = parser.getBuilder().newDocument();
        data.setTempData(doc);
        System.out.println("Successfully closed " + openFile);
    }

    private static void onSaveCommand() {
        parser.saveToXML(data.getTempData(), openFile);
        System.out.println("Successfully saved " + openFile);
    }

    private static void onSaveAsCommand(String[] commands) {
        String fileName = commands[1];
        parser.saveToXML(data.getTempData(), fileName);
        data.setTempData(parser.getBuilder().newDocument());
        System.out.println("Successfully saved another " + fileName);
    }

    private static void onFreeSeatsCommand(String[] commands) {
        String paramDate = commands[1];
        String paramName = commands[2];

        Document document = data.getTempData();
        NodeList events = document.getElementsByTagName("event");
        boolean eventExists = false;
        int hallOfEvent = 0;
        for (int i = 0; i < events.getLength(); i++) {
            Node event = events.item(i);
            NamedNodeMap attrs = event.getAttributes();
            if (paramName.equals(attrs.getNamedItem("name").getTextContent()) && paramDate.equals(attrs.getNamedItem("date").getTextContent())) {
                eventExists = true;
                hallOfEvent = Integer.parseInt(attrs.getNamedItem("hall").getTextContent());
                break;
            }
        }

        if (eventExists) {
            int boughtTicketsCount = 0;
            NodeList boughtTickets = document.getElementsByTagName("bought-ticket");
            for (int i = 0; i < boughtTickets.getLength(); i++) {
                Node ticket = boughtTickets.item(i);
                NamedNodeMap attrs = ticket.getAttributes();
                String name = attrs.getNamedItem("name").getTextContent();
                String date = attrs.getNamedItem("date").getTextContent();
                if (name.equals(paramName) && date.equals(paramDate)) {
                    boughtTicketsCount++;
                }
            }

            int bookedTicketsCount = 0;
            NodeList bookedTickets = document.getElementsByTagName("bought-ticket");
            for (int i = 0; i < bookedTickets.getLength(); i++) {
                Node ticket = bookedTickets.item(i);
                NamedNodeMap attrs = ticket.getAttributes();
                String name = attrs.getNamedItem("name").getTextContent();
                String date = attrs.getNamedItem("date").getTextContent();
                if (name.equals(paramName) && date.equals(paramDate)) {
                    bookedTicketsCount++;
                }
            }

            Hall hall = data.getHalls().get(hallOfEvent);
            int availableSeats = hall.getAllSeats();
            availableSeats -= bookedTicketsCount + bookedTicketsCount;

            if (availableSeats > 0) {
                System.out.println("There are " + Integer.toString(availableSeats) + " available seats");
            } else {
                System.out.println("There are no available seats");
            }
        } else {
            System.out.println("This event either does not exist or it is not on the given date");
        }
    }

    private static void onCheckCommand(String[] commands) throws Exception {
        String paramCode = commands[1];

        Document document = data.getTempData();
        NodeList boughtTickets = document.getElementsByTagName("bought-ticket");
        for (int i = 0; i < boughtTickets.getLength(); i++) {
            Node ticket = boughtTickets.item(i);
            NamedNodeMap attrs = ticket.getAttributes();
            String code = attrs.getNamedItem("code").getTextContent();
            if (code.equals(paramCode)) {
                String seat = attrs.getNamedItem("seat").getTextContent();
                System.out.println("You seat is " + seat);
            } else {
                throw new Exception("This code is invalid");
            }
        }
    }

    private static void onBuyCommand(String[] commands) throws Exception {
        int paramRow = Integer.parseInt(commands[1]);
        int paramSeat = Integer.parseInt(commands[2]);
        String paramDate = commands[3];
        String paramName = commands[4];

        Document document = data.getTempData();
        NodeList events = document.getElementsByTagName("event");
        boolean eventExists = false;
        int hallOfEvent = 0;
        for (int i = 0; i < events.getLength(); i++) {
            Node event = events.item(i);
            NamedNodeMap attrs = event.getAttributes();
            if (paramName.equals(attrs.getNamedItem("name").getTextContent()) && paramDate.equals(attrs.getNamedItem("date").getTextContent())) {
                eventExists = true;
                hallOfEvent = Integer.parseInt(attrs.getNamedItem("hall").getTextContent());
                break;
            }
        }

        if (eventExists) {
            Hall hallOfEventObj = data.getHalls().get(hallOfEvent);
            BoughtTicket ticket = new BoughtTicket(paramRow, paramSeat, paramDate, paramName);
            if (hallOfEventObj.buyTicket(ticket)) {
                Element boughtTicket = document.createElement("bought-ticket");
                boughtTicket.setAttribute("row", Integer.toString(paramRow));
                boughtTicket.setAttribute("seat", Integer.toString(paramSeat));
                boughtTicket.setAttribute("date", paramDate);
                boughtTicket.setAttribute("name", paramName);
                boughtTicket.setAttribute("hall", Integer.toString(hallOfEvent));
                boughtTicket.setAttribute("code", ticket.getCode());
                document.getDocumentElement().appendChild(boughtTicket);
                System.out.println("Successfully bought seat " + paramSeat + " on row " + paramRow);
            } else {
                System.out.println("This seat is already bought or booked");
            }
        } else {
            System.out.println("This event either does not exist or it is not on the given date");
        }
    }

    private static void onUnBookEventCommand(String[] commands) {
        int paramRow = Integer.parseInt(commands[1]);
        int paramSeat = Integer.parseInt(commands[2]);
        String paramDate = commands[3];
        String paramName = commands[4];

        Document doc = data.getTempData();
        NodeList bookedTickets = doc.getElementsByTagName("booked-ticket");
        if (bookedTickets != null) {
            for (int i = 0; i < bookedTickets.getLength(); i++) {
                Node bookedTicket = bookedTickets.item(i);
                NamedNodeMap attrs = bookedTicket.getAttributes();
                int row = Integer.parseInt(attrs.getNamedItem("row").getTextContent());
                int seat = Integer.parseInt(attrs.getNamedItem("seat").getTextContent());
                String date = attrs.getNamedItem("date").getTextContent();
                String name = attrs.getNamedItem("name").getTextContent();

                if (row == paramRow && seat == paramSeat && date.equals(paramDate) && name.equals(paramName)) {
                    int hallId = Integer.parseInt(attrs.getNamedItem("hall").getTextContent());
                    Hall hall = data.getHalls().get(hallId);
                    if (hall.unBookSeat(row, seat)) {
                        doc.getDocumentElement().removeChild(bookedTicket);
                        System.out.println("Successfully unbooked ticket");
                        break;
                    } else {
                        System.out.println("There was a problem unbooking your ticket");
                    }
                } else {
                    System.out.println("There are no events with this name or date");
                }
            }
        } else {
            System.out.println("You have not booked any tickets yet");
        }
    }

    private static void onBookEventCommand(String[] commands) throws Exception {
        int row = Integer.parseInt(commands[1]);
        int seat = Integer.parseInt(commands[2]);
        String eventDate = commands[3];
        String eventName = commands[4];
        String note = commands[5];

        Document document = data.getTempData();
        NodeList events = document.getElementsByTagName("event");
        boolean eventExists = false;
        int hallOfEvent = 0;
        for (int i = 0; i < events.getLength(); i++) {
            Node event = events.item(i);
            NamedNodeMap attrs = event.getAttributes();
            if (eventName.equals(attrs.getNamedItem("name").getTextContent()) && eventDate.equals(attrs.getNamedItem("date").getTextContent())) {
                eventExists = true;
                hallOfEvent = Integer.parseInt(attrs.getNamedItem("hall").getTextContent());
                break;
            }
        }

        if (eventExists) {
            Hall hallOfEventObj = data.getHalls().get(hallOfEvent);
            if (hallOfEventObj.bookSeat(row, seat, eventDate, eventName)) {
                Element bookedTicket = document.createElement("booked-ticket");
                bookedTicket.setAttribute("row", Integer.toString(row));
                bookedTicket.setAttribute("seat", Integer.toString(seat));
                bookedTicket.setAttribute("date", eventDate);
                bookedTicket.setAttribute("name", eventName);
                bookedTicket.setAttribute("note", note);
                bookedTicket.setAttribute("hall", Integer.toString(hallOfEvent));
                document.getDocumentElement().appendChild(bookedTicket);
                System.out.println("Successfully booked seat " + seat + " on row " + row);
            } else {
                System.out.println("This seat is already booked or bought");
            }
        } else {
            System.out.println("This event either does not exist or it is not on the given date");
        }
    }

    private static void onAddEventCommand(String[] commands) throws Exception {
        String date = commands[1];
        int hallNumber = Integer.parseInt(commands[2]);
        String eventName = commands[3];

        List<Hall> halls = data.getHalls();
        boolean hallDoesntExist = true;
        for (int i = 0; i < halls.size(); i++) {
            Hall hall = halls.get(i);
            if (hall.getId() == hallNumber) {
                hallDoesntExist = false;
                break;
            }
        }
        if (hallDoesntExist) {
            throw new Exception("This hall doesn't exist");
        }

        Document document = data.getTempData();
        NodeList events = document.getElementsByTagName("event");
        for (int i = 0; i < events.getLength(); i++) {
            Node event = events.item(i);
            NamedNodeMap dateAttr = event.getAttributes();
            String eventDate = dateAttr.getNamedItem("date").getTextContent();
            int eventHall = Integer.parseInt(dateAttr.getNamedItem("hall").getTextContent());
            if (eventDate.equals(date) && eventHall == hallNumber) {
                throw new Exception("An event is already planned for this hall");
            }
        }

        Element newEvent = document.createElement("event");
        newEvent.setAttribute("date", date);
        newEvent.setAttribute("hall", Integer.toString(hallNumber));
        newEvent.setAttribute("name", eventName);
        Element root = document.getDocumentElement();
        root.appendChild(newEvent);
        System.out.println("Successfully added new event");
    }

    private static void onHelpCommand() {
        System.out.println("The following commands are supported:\n");
        if (prevClosed) {
            System.out.println("open <file>: opens <file>");
            System.out.println("exit: exits the program\n");
        } else {
            System.out.println("open <file>: opens <file>");
            System.out.println("close: closes currently opened file");
            System.out.println("save saves the currently open file ");
            System.out.println("saveas <file>: saves the currently open file in <file>");
            System.out.println("help: prints this information");
            System.out.println("addevent <date> <hall> <name>: Добавя ново представление на дата <date> с име <name> в зала <hall>");
            System.out.println("freeseats <date> <name>: Извежда справка за свободните места за представление с име <name> на дата <date>");
            System.out.println("book <row> <seat> <date> <name> <note>: Запазва билет за представление с име <name> на <date> на ред <row> и място <seat>, като добавя бележка <note>");
            System.out.println("unbook <row> <seat> <date> <name>: Отменя резервация за представление с име <name> на <date> на ред <row> и място <seat>.");
            System.out.println("buy <row> <seat> <date> <name>: Закупува билет за представление с име <name> на <date> на ред <row> и място <seat>. За всеки билет се издава уникален сложен код, който съдържа информация за съответното място.");
            System.out.println("bookings [<date>] [<name>]: Извежда справка за запазените, но неплатени (незакупени) билети за представление с име <name> на <date>. Ако <name> е пропуснато, извежда информация за всички представления на дадената дата. Ако <date> е пропуснато, извежда информация за всички дати.");
            System.out.println("check <code>: Прави проверка за валидност на билет, като по дадения код <code> се извлича номера на мястото или се връща грешка, ако кодът е невалиден).");
            System.out.println("report <from> <to> [<hall>]: Извежда справка за закупени билети от дата <from> до дата <to> в зала <hall>, като извежда всички изнесени представления в залата и за всяко отделно представление се извежда и количеството продадени билети. Ако <hall> е пропуснато, извежда информация за всички зали.");
            System.out.println("exit: exits the program\n");
        }
    }
}