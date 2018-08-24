import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.*;

/**
 * Class: CinemaBookingSystem
 * @author Victor Wang
 * Responsibility: maintains records of Cinemas and Bookings
 *                 import information
 *                 implement operations on cinema and booking objects
 * Collaborator: Cinema, Booking
 * @inv: There will be no duplicates of Cinema as well as booking
 *            All the bookings in the bookingList are valid
 */
public class CinemaBookingSystem
{

    private final LinkedHashMap<String, Cinema> cinemaList;
    private final LinkedHashMap<String, Booking> bookingList;


    /**
     * Description:
     * Class constructor
     */
    public CinemaBookingSystem()
    {
        this.cinemaList = new LinkedHashMap<>();
        this.bookingList = new LinkedHashMap<>();
    }


    /**
     * Description:
     * Class getter
     *
     * @return a cinemaList LinkedHashMap object
     */
    public LinkedHashMap<String, Cinema> getCinemaList()
    {
        return cinemaList;
    }


    /**
     * Description:
     * This function initialises or changes a Cinema object
     * If the said object does not exist, create a new one
     * If the object exists, add new seats to it.
     * @Pre Integer(rowNum) > 0
     * @param cinemaID: key of a Cinema object in cinemaList
     * @param rowID: key of a row LinkedHashMap object
     * @param rowNum: value of a row LinkedHashMap object
     * @pos a Cinema object is created or edited
     */
    public void addCinema(String cinemaID, String rowID, String rowNum)
    {
        Cinema cBuffer = getCinemaList().get(cinemaID);
        if (cBuffer == null)
        {
            cBuffer = new Cinema();
            getCinemaList().put(cinemaID, cBuffer);
        }
        cBuffer.addSeat(rowID, rowNum);
    }


    /**
     * Description:
     * This function pass on the information needed to a Cinema object to
     * initialise a Session object within the said Cinema.
     *
     * @pre info.length >= 3
     * @param info: the split line of entry which contains the information needed
     *              to create a Session
     * @pos a Session object is created
     */
    public void importSession(String[] info)
    {
        String cinemaID = info[1];
        LocalTime sessionTime = LocalTime.parse(info[2]);
        Cinema cBuffer = getCinemaList().get(cinemaID);
        String movie = "";
        String mBuffer = info[3];
        // The Stringbuilder is used to increase efficiency within the loop
        StringBuilder str = new StringBuilder();
        int i = 3;
        if (cBuffer == null) return;
        // This loop is made specifically to handle a movie name
        // which is longer than 1 word.
        while (i < info.length && mBuffer.charAt(0) != '#')
        {
            str.append(movie).append(mBuffer);
            i++;
            if (i >= info.length) break;
            mBuffer = info[i];
            if (mBuffer.charAt(0) != '#') str.append(" ");
        }
        movie = str.toString();
        cBuffer.addSession(sessionTime, movie);
    }


    /**
     * Description:
     * This function takes the booking info and try to reach the corresponding Cinema
     * to place the booking order.
     * If the cinemaID is wrong or the Cinema operation on session returns
     * failure, the request is be rejected.
     * Else the request is done, and a Booking object is initialised
     * and put into the bookingList.
     *
     * @param bookingID: key of a Booking object in bookingList
     * @param cinemaID: key of a Cinema object in cinemaList
     * @param sessionTime: time of a session, this will be passed to a Cinema object
     * @param seat: the required amount of seats
     */
    public void request (String bookingID, String cinemaID, LocalTime sessionTime, int seat)
    {
        Booking newBooking;
        Cinema cBuffer = cinemaList.get(cinemaID);
        // Scenario 1
        if (cBuffer == null)
        {
            System.out.println("Booking " + "rejected");
            return;
        }
        String status = cBuffer.requestSession(bookingID, sessionTime, seat);
        // Scenario 2
        // Upon failure
        if (status.equals("rejected"))
        {
            System.out.println("Booking " + "rejected");
        }
        // Upon success
        else
        {
            System.out.println("Booking " + bookingID + " " + status);
            newBooking = new Booking(cinemaID, sessionTime);
            bookingList.put(bookingID, newBooking);
        }
    }


    /**
     * Description:
     * This function takes the new info and try to reach the target Cinema
     * to place a new booking and remove the old one on success.
     * If the bookingID, seat and/or the cinemaID is wrong, or the change operation
     * returns failure, the change is rejected.
     * Else If the new cinemaID equals to the cinemaID of the booking which this
     * operation seeks to change, this function will call a special change function
     * of the Cinema object.
     * Else If the new cinemaID is different from the old cinemaID, this function invoke
     * booking function on the new Cinema. Upon success, it invokes the cancel function
     * on the old Cinema.
     * Upon success of the above two scenarios, the change is made and the old booking is
     * removed from the bookingList. If they failed, the old booking is retained.
     *
     * @param bookingID: key of a Booking object in bookingList
     * @param cinemaID: key of a Cinema object in cinemaList, null if type = "Cancel"
     * @param sessionTime: time of a session, this will be passed to a Cinema object, null if type = "Cancel"
     * @param seat: the required amount of seats
     */
    public void change (String bookingID, String cinemaID, LocalTime sessionTime, int seat)
    {
        Booking newBooking;
        Booking bBuffer = bookingList.get(bookingID);
        Cinema cBuffer = cinemaList.get(cinemaID);
        String status;
        // Scenario 1
        if (bBuffer == null || seat < 1 || cBuffer == null)
        {
            System.out.println("Change " + "rejected");
            return;
        }
        // Scenario 2
        else if (!bBuffer.getCinemaID().equals(cinemaID))
        {
            status = cBuffer.requestSession(bookingID, sessionTime, seat);
        }
        // Scenario 3
        else
        {
            status = cBuffer.changeSession(bookingID, sessionTime, seat);
        }
        // Upon failure
        if (status.equals("rejected"))
        {
            System.out.println("Change " + "rejected");
        }
        // Upon success
        else
        {
            System.out.println("Change " + bookingID + " " + status);
            newBooking = new Booking(cinemaID, sessionTime);
            bookingList.remove(bookingID);
            bookingList.put(bookingID, newBooking);
            if (!bBuffer.getCinemaID().equals(cinemaID)) cinemaList.get(bBuffer.getCinemaID())
                    .cancelSession(bookingID, bBuffer.getSessionTime());

        }
    }


    /**
     * Description:
     * This function takes the target bookingID and try to reach the target Cinema
     * to remove the booking.
     * If the bookingID is not found, the cancel is rejected
     * Else the cancellation is carried out, and the old Booking object
     * is removed from bookingList
     *
     * @param bookingID: key of a Booking object in bookingList
     */
    public void cancel (String bookingID)
    {
        Booking bBuffer = bookingList.get(bookingID);
        // Scenario 1
        if (bBuffer == null)
        {
            System.out.println("Cancel " + "rejected");
            return;
        }
        // Scenario 2
        System.out.println("Cancel " + bookingID);
        Cinema cBuffer = cinemaList.get(bBuffer.getCinemaID());
        cBuffer.cancelSession(bookingID, bBuffer.getSessionTime());
        bookingList.remove(bookingID);
    }


    /**
     * Description:
     * This function does what the print() function is required to do
     * @param cinemaID: key of the Cinema object
     * @param sessionTime: key of the Session object
     */
    public void print (String cinemaID, LocalTime sessionTime)
    {
        Cinema cBuffer = cinemaList.get(cinemaID);
        if (cBuffer == null) throw new IllegalArgumentException("Invalid cinema ID: " + cinemaID);
        cBuffer.printSession(sessionTime);
        System.out.println();
    }


    /**
     * Description:
     * The main function read the file and invoke various import function
     * @pre args[0] != null
     * @param args: the file where main will read from
     */
    public static void main(String args[])
    {
        CinemaBookingSystem cbs = new CinemaBookingSystem();
        Scanner sc = null;
        try
        {
            sc = new Scanner(new File(args[0]));
            while (sc.hasNext())
            {
                // This is the new line split to remove all space
                String[] buffer = sc.nextLine().split("\\s+");
                switch (buffer[0])
                {
                    case "Cinema":
                    {
                        cbs.addCinema(buffer[1], buffer[2], buffer[3]);
                        break;
                    }
                    case "Session":
                    {
                        cbs.importSession(buffer);
                        break;
                    }
                    case "Request":
                    {
                        cbs.request(buffer[1], buffer[2], LocalTime.parse(buffer[3]),
                                Integer.parseInt(buffer[4]));
                        break;
                    }
                    case "Change":
                    {
                        cbs.change(buffer[1], buffer[2], LocalTime.parse(buffer[3]),
                                Integer.parseInt(buffer[4]));
                        break;
                    }
                    case "Cancel":
                    {
                        cbs.cancel(buffer[1]);
                        break;
                    }
                    case "Print":
                    {
                        cbs.print(buffer[1], LocalTime.parse(buffer[2]));
                        break;
                    }
                    default:
                }
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
        finally
        {
            if (sc != null) sc.close();
        }
    }
}
