import java.time.LocalTime;

/**
 * Class: Booking
 * @author Victor Wang
 * Responsibility: maintains information of individual booking
 *                 return information when called
 * Collaborator: CinemaBookingSystem
 * @inv: cinemaID and sessionTime are always valid
 */
public class Booking
{

    private String cinemaID;
    private LocalTime sessionTime;


    /**
     * Description:
     * Class constructor
     *
     * @param cinemaID: key of a Cinema object
     * @param sessionTime: key of a Session object
     */
    public Booking (String cinemaID, LocalTime sessionTime)
    {
        this.cinemaID = cinemaID;
        this.sessionTime = sessionTime;
    }


    /**
     * Description:
     * Class getter
     *
     * @return cinemaID
     */
    protected String getCinemaID()
    {
        return cinemaID;
    }


    /**
     * Description:
     * Class getter
     *
     * @return sessionTime
     */
    protected LocalTime getSessionTime()
    {
        return sessionTime;
    }
}
