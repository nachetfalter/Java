import java.util.*;

/**
 * Class: Session
 * @author Victor Wang
 * Responsibility: maintains records of booked seats and movie
 *                 operate on booked seats
 * Collaborator: Cinema
 * @inv: There will be no duplicates of seat
 */
public class Session
{

    private String movie;
    private LinkedHashMap<String, String> seatAlloc;


    /**
     * Description:
     * Class constructor
     *
     * @param movie: name of the movie
     * @param seatAlloc: the LinkedHashMap which maintains the booking condition
     *                   of all seats in a Session
     */
    public Session (String movie, LinkedHashMap<String, String> seatAlloc)
    {
        this.movie = movie;
        this.seatAlloc = seatAlloc;
    }


    /**
     * Description:
     * Class getter
     *
     * @return name of the movie
     */
    protected String getMovie() {
        return movie;
    }


    /**
     * Description:
     * This function tries to allocate required number of seats for a booking
     * If there isn't enough seats for the booking, return "rejected"
     * Else return the allocated seats
     *
     * @pre seat > 0
     * @param bookingID: key of a Booking object, it's used to mark seats here
     * @param seat: the required amount of seats
     * @return "rejected" or the allocated seats
     */
    protected String requestSeat (String bookingID, int seat)
    {
        Set set = seatAlloc.entrySet();
        Iterator i = set.iterator();
        String status = "";
        // How many consecutive empty seats are there?
        int emptySeat = 0;
        // The Stringbuilder is used to increase efficiency within the loop
        StringBuilder str = new StringBuilder();
        // What is the current row?
        String row = "";
        // Which seat is the first seat of in our booked seats?
        String firstSeat = "";
        while (i.hasNext())
        {
            Map.Entry e = (Map.Entry)i.next();
            // Get the first row ever
            if (row.equals("")) row = ((String) e.getKey()).split("\\d+")[0];
            // A fresh start with no seat in our status?
            // Add the current empty seat to it
            if (status.equals(""))
            {
                status = str.append((String) e.getKey()).append("-").toString();
                firstSeat = (String) e.getKey();
            }
            // We are on the same row and we past an empty seat
            // Count the seat for empty seat and mark it as ours
            if (e.getValue() == null && ((String)e.getKey()).split("\\d+")[0].equals(row))
            {
                emptySeat += 1;
                seatAlloc.put((String)e.getKey(), bookingID);
            }
            // We just get on a different row and the first seat
            // of it is empty?
            // We reset the counting first and change the row then add the seat
            else if (!((String)e.getKey()).split("\\d+")[0].equals(row)&& e.getValue() == null)
            {
                emptySeat = 1;
                str.setLength(0);
                status = str.append((String) e.getKey()).append("-").toString();
                firstSeat = (String) e.getKey();
                seatAlloc.put((String)e.getKey(), bookingID);
            }
            // We hit an no empty seat before we get
            // enough consecutive empty seats
            // Reset the count and status
            else
            {
                str.setLength(0);
                status = "";
                emptySeat = 0;
            }
            // We finally get enough seats
            // Put the seats together into status
            if (emptySeat == seat)
            {
                status = str.append(e.getKey()).toString();
                break;
            }
            // Updating the row so we know if we changed row
            row = ((String)e.getKey()).split("\\d+")[0];
        }
        // So we end up getting enough empty seats?
        if (emptySeat == seat)
        {
            i = set.iterator();
            // We reset all seats we previously marked to empty
            // until we hit the first seat in our booked seat
            while (i.hasNext())
            {
                Map.Entry e = (Map.Entry)i.next();
                if (e.getKey().toString().equals(firstSeat))
                {
                    break;
                }
                if (e.getValue().toString().equals(bookingID))
                {
                    seatAlloc.put((String)e.getKey(), null);
                }
            }
            // Check if there is only one seat in our booked seat
            // If so we only return one seat
            String[] checker = status.split("-");
            if(checker[0].equals(checker[1])) status = checker[0];
        }
        // We didn't end up getting the seats :(
        // set status to "rejected"
        // clear all our markings
        else
        {
            status = "rejected";
            cancelSeat(bookingID);
        }
        return status;
    }


    /**
     * Description:
     * This function tries to reallocate required number of seats for a booking
     * If there isn't enough seats for the booking, return "rejected"
     * Else return the allocated seats
     * This function is only used if the changed booking is within the same Cinema
     * as the previous booking because this situation calls for recovery shall the change fails
     *
     * @pre the bookingID must be valid, seat > 0
     * @param bookingID: key of a Booking object, it's used to mark and check seats here
     * @param seat: the required amount of seats
     * @return "rejected" or the allocated seats
     */
    protected String changeSeat (String bookingID, int seat){
        // This list will be used for our recovery shall the change fails
        List<String> seatRecovery = new ArrayList<>();
        Set set = seatAlloc.entrySet();
        // We temporarily reset the previous booked seats
        for (Object aSet : set)
        {
            Map.Entry e = (Map.Entry) aSet;
            if (e.getValue() != null && e.getValue().toString().equals(bookingID))
            {
                seatRecovery.add((String) e.getKey());
                seatAlloc.put((String)e.getKey(), null);
            }
        }
        // We invoke the requestSeat function as if we are placing a new booking
        String status = requestSeat(bookingID, seat);
        int j = 0;
        int size = seatRecovery.size();
        // If the change failed
        if (status.equals("rejected"))
        {
            // We recover the previous booked seats from the recovery list
            for (Object aSet : set)
            {
                Map.Entry e = (Map.Entry) aSet;
                if (e.getKey().toString().equals(seatRecovery.get(j))) {
                    seatAlloc.put((String)e.getKey(), bookingID);
                    j++;
                }
                if (j >= size) {
                    break;
                }
            }
        }
        return status;
    }


    /**
     * Description:
     * This function reset the previous booked seats of a booking
     * @pre The bookingID must be valid
     * @param bookingID: key of a Booking object, it's used to check seats here
     */
    protected void cancelSeat (String bookingID)
    {
        Set set = seatAlloc.entrySet();
        for (Object aSet : set)
        {
            Map.Entry e = (Map.Entry) aSet;
            if (e.getValue() != null && e.getValue().toString().equals(bookingID))
            {
                seatAlloc.put((String)e.getKey(), null);
            }
        }
    }


    /**
     * Description:
     * This function print booked seats from each row as well as their row names
     * The booked seats are separated by their bookings.
     */
    protected void printSeat ()
    {
        Set set = seatAlloc.entrySet();
        Iterator i = set.iterator();
        List<String> bookedSeat = new ArrayList<>();
        // Check for different bookingID
        String bookingID = "-1";
        String row = "";
        // We go through all seats first
        // Record every seat that has been booked
        while (i.hasNext())
        {
            Map.Entry e = (Map.Entry)i.next();
            if (e.getValue() != null)
            {
                if (row.equals("")) row = ((String)e.getKey()).split("\\d+")[0];
                if (bookingID.equals("-1")) bookingID = (String) e.getValue();
                // If the bookingID on the booked seat is different from the ones
                // we met before, add a ',' to the record
                if (!bookingID.equals(e.getValue()))
                {
                    bookedSeat.add(",");
                }
                // If we get into a new row and we ended on the seat on a new row
                // with different bookingID
                // We firstly set the last "," to " " then add the new row name
                if(!((String)e.getKey()).split("\\d+")[0].equals(row)
                        && bookedSeat.get(bookedSeat.size() - 1).equals(","))
                {
                    bookedSeat.set(bookedSeat.size() - 1, " ");
                    bookedSeat.add(((String) e.getKey()).split("\\d+")[0]);
                }
                // Add seats to the list, update row and bookingID for checking
                bookedSeat.add((String) e.getKey());
                bookingID = (String) e.getValue();
                row = ((String)e.getKey()).split("\\d+")[0];
            }
        }
        // This is here to prevent a cinema with no booked seat
        if (bookedSeat.size() <= 0) return;
        System.out.println();
        row = bookedSeat.get(0).split("\\d+")[0];
        System.out.print(row + ": ");
        // This is here so we know we reached the end of all booked seats
        // which is the equivalent of a new line
        bookedSeat.add(" ");
        // j is used to count the difference between the start and finish seat
        // We counts from each start seat until the finish seat or upon new row
        int j = 0;
        String startSeat = "";
        for (String s: bookedSeat)
        {
            // If we reached a new row, print the row name and reset our counter
            if (s.equals(s.split("\\d+")[0]) && !s.equals(" ") && !s.equals(","))
            {
                System.out.print("\n" + s + ": ");
                j = -1;
            }
            // If we just get a new range of seats, record the start seatNum
            else if (j == 0)
            {
                System.out.print(s.split("\\D+")[1]);
                startSeat = s.split("\\D+")[1];
            }
            // If we reached the end of a line or the end of a seat range,
            // we add the end seat string to the first seat and create the complete
            // seat range
            else if (s.equals(",") || s.equals(" "))
            {
                // end seat value = start seat value + their difference
                int buffer = Integer.parseInt(startSeat) + j - 1;
                if (!startSeat.equals(String.valueOf(buffer))) System.out.print("-" + buffer);
                if (s.equals(",")) System.out.print(",");
                j = -1;
            }
            j++;
        }
    }
}

