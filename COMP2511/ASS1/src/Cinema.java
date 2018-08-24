import java.time.LocalTime;
import java.util.*;

/**
 * Class: Cinema
 * @author Victor Wang
 * Responsibility: maintains records of Sessions and seat
 *                 invoke operations on Session object
 * Collaborator: CinemaBookingSystem, Session
 * @inv: there will be no duplicates of sessionTime as well as seats
 */
public class Cinema
{

    private LinkedHashMap<LocalTime, Session> sessionList;
    private LinkedHashMap<String, String> seatList;


    /**
     * Description:
     * Class constructor
     */
    public Cinema ()
    {
        this.seatList = new LinkedHashMap<>();
        this.sessionList = new LinkedHashMap<>();
    }

    /**
     * Description:
     * This function creates a Session object
     *
     * @param sessionTime: the key of a Session object
     * @param movie: name of the movie
     * @pos a Session object is created
     */
    protected void addSession (LocalTime sessionTime, String movie)
    {
        LinkedHashMap<String, String> seatAlloc = new LinkedHashMap<>();
        Set set = seatList.entrySet();
        // Initialise the session seat condition
        for (Object aSet : set)
        {
            Map.Entry e = (Map.Entry) aSet;
            for (int j = 1; j <= Integer.parseInt((String) e.getValue()); ++j)
            {
                seatAlloc.put((e.getKey() + String.valueOf(j)), null);
            }
            sessionList.put(sessionTime, (new Session(movie, seatAlloc)));
        }
    }

    /**
     * Description:
     * This function add seat info into seatList of this Cinema
     *
     * @param rowID: name of a row
     * @param rowNum: the number of seats in the row
     */
    protected void addSeat (String rowID, String rowNum)
    {
        seatList.put(rowID, rowNum);
    }


    /**
     * Description:
     * This function try to ask the session to book seats.
     * If the session doesn't exist or the seat cannot be allocated
     * return status "rejected" to the caller.
     * Else return the allocated seats.
     *
     * @pre seat > 0
     * @param bookingID: key of a Booking object in bookingList, it is passed into
     *                   Session to mark the seats
     * @param sessionTime: key of a Session object
     * @param seat: the required amount of seats
     * @return "rejected" or the seats allocated to the booking
     */
    protected String requestSession(String bookingID, LocalTime sessionTime, int seat)
    {
        Session sBuffer = sessionList.get(sessionTime);
        // Scenario 1
        if (sBuffer == null)
        {
            System.out.println("Booking " + "rejected");
            return "rejected";
        }
        // Scenario 2
        return sBuffer.requestSeat(bookingID, seat);
    }


    /**
     * Description:
     * This function try to ask the target session for a new booking.
     * If the session doesn't exist or the seat cannot be allocated
     * return status "rejected" to the caller.
     * Else return the allocated seats.
     *
     * @pre seat > 0
     * @param bookingID: key of a Booking object in bookingList, it is passed into
     *                   Session to mark and check the seats
     * @param sessionTime: key of a Session object
     * @param seat: the required amount of seats
     * @return "rejected" or the seats allocated to the booking
     */
    protected String changeSession(String bookingID, LocalTime sessionTime, int seat)
    {
        Session sBuffer = sessionList.get(sessionTime);
        // Scenario 1
        if (sBuffer == null)
        {
            System.out.println("Change " + "rejected");
            return "rejected";
        }
        // Scenario 2
        return sBuffer.changeSeat(bookingID, seat);
    }


    /**
     * Description:
     * This function try to ask the target session to cancel a booking
     *
     * @pre bookingID must be valid
     * @param bookingID: key of a Booking object in bookingList, it is passed into
     *                   Session to check the seats
     * @param sessionTime: key of a Session object
     */
    protected void cancelSession (String bookingID, LocalTime sessionTime)
    {
        Session sBuffer = sessionList.get(sessionTime);
        sBuffer.cancelSeat(bookingID);
    }


    /**
     * Description:
     * This function print out the info regarding the session
     * @param sessionTime: key of a Session object
     * @throw IllegalArugumentException if sessionTime is not valid
     */
    protected void printSession (LocalTime sessionTime)
    {
        Session sBuffer = sessionList.get(sessionTime);
        if (sBuffer == null) throw new IllegalArgumentException("Invalid session time: " + sessionTime.toString());
        System.out.print(sBuffer.getMovie());
        sBuffer.printSeat();
    }
}
