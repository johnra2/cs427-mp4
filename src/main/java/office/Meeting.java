package office;

import org.joda.time.LocalDate;

import org.joda.time.Interval;
import org.joda.time.LocalTime;

/**
 * User: sameer
 * Date: 16/05/2013
 * Time: 10:03
 */
public class Meeting implements Comparable<Meeting>{

    private String employeeId;

    private LocalTime startTime;

    private LocalTime finishTime;

    private LocalDate requestDate;

    private LocalTime requestTime;

    public Meeting(String employeeId, LocalTime startTime, LocalTime finishTime) {
        this.employeeId = employeeId;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }


    public String getEmployeeId() {
        return employeeId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getFinishTime() {
        return finishTime;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public LocalTime getRequestTime() {
        return requestTime;
    }

    public void setRequestDateTime(LocalDate requestDate, LocalTime requestTime) {
        this.requestDate = requestDate;
        this.requestTime = requestTime;
    }

    public int compareTo(Meeting that) {
        Interval meetingInterval = new Interval(startTime.toDateTimeToday(), finishTime.toDateTimeToday());
        Interval toCompareMeetingInterval = new Interval(that.getStartTime().toDateTimeToday(), that.getFinishTime().toDateTimeToday());

        if(meetingInterval.overlaps(toCompareMeetingInterval)){
            return 0;
        }else{
            return this.getStartTime().compareTo(that.getStartTime());
        }
    }

    public int compareToRequest(Meeting that) {
        if (this.getRequestDate().compareTo(that.getRequestDate()) == 0){
            return this.getRequestTime().compareTo(that.getRequestTime());
        }
        return this.getRequestDate().compareTo(that.getRequestDate());
    }
}
