package office;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.*;

import static java.lang.Integer.parseInt;

/**
 * User: sameer Date: 15/05/2013 Time: 15:12
 */
public class MeetingScheduler {

    private DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    private DateTimeFormatter separatedTimeFormatter = DateTimeFormat.forPattern("HH:mm");
    private DateTimeFormatter requestTimeFormatter = DateTimeFormat.forPattern("HH:mm:ss");

    /**
     *
     * @param meetingRequest
     * @return
     */
    public MeetingsSchedule schedule(String meetingRequest) {
        String[] requestLines = meetingRequest.split("\n");

        String[] officeHoursTokens = requestLines[0].split(" ");
        LocalTime officeStartTime = new LocalTime(parseInt(officeHoursTokens[0].substring(0, 2)),
                parseInt(officeHoursTokens[0].substring(2, 4)));
        LocalTime officeFinishTime = new LocalTime(parseInt(officeHoursTokens[1].substring(0, 2)),
                parseInt(officeHoursTokens[1].substring(2, 4)));

        Map<LocalDate, Set<Meeting>> meetings = new TreeMap<LocalDate, Set<Meeting>>();

        for (int i = 1; i < requestLines.length; i = i + 2) {

            String[] meetingSlotRequest = requestLines[i].split(" ");
            String[] employeeRequest = requestLines[i + 1].split(" ");
            LocalDate meetingDate = dateFormatter.parseLocalDate(employeeRequest[0]);

            Meeting meeting = extractMeeting(requestLines[i + 1], officeStartTime, officeFinishTime,
                    meetingSlotRequest);

            if (meeting == null)
                continue;

            LocalDate requestDate = dateFormatter.parseLocalDate(meetingSlotRequest[0]);
            LocalTime requestTime = requestTimeFormatter.parseLocalTime(meetingSlotRequest[1]);
            meeting.setRequestDateTime(requestDate, requestTime);

            if (meetings.containsKey(meetingDate)) {
                boolean canBeAdded = true;
                Set<Meeting> conflicts = new HashSet<Meeting>();
                for (Meeting dayMeeting : meetings.get(meetingDate)) {
                    if (meeting.overlapsWith(dayMeeting)) {
                        if (meeting.compareTo(dayMeeting) != -1) {
                            canBeAdded = false;
                        } else {
                            conflicts.add(dayMeeting);
                        }
                    }
                }
                if (canBeAdded) {
                    meetings.get(meetingDate).add(meeting);
                    for (Meeting conflict : conflicts) {
                        meetings.get(meetingDate).remove(conflict);
                    }
                }
            } else {
                Set<Meeting> meetingsForDay = new TreeSet<Meeting>();
                meetingsForDay.add(meeting);
                meetings.put(meetingDate, meetingsForDay);
            }
        }

        return new MeetingsSchedule(officeStartTime, officeFinishTime, meetings);
    }

    private Meeting extractMeeting(String requestLine, LocalTime officeStartTime, LocalTime officeFinishTime,
            String[] meetingSlotRequest) {
        String[] employeeRequest = requestLine.split(" ");
        String employeeId = meetingSlotRequest[2];

        LocalTime meetingStartTime = separatedTimeFormatter.parseLocalTime(employeeRequest[1]);
        LocalTime meetingFinishTime = new LocalTime(meetingStartTime.getHourOfDay(), meetingStartTime.getMinuteOfHour())
                .plusHours(parseInt(employeeRequest[2]));

        if (meetingTimeOutsideOfficeHours(officeStartTime, officeFinishTime, meetingStartTime, meetingFinishTime)) {
            System.out.println("EmployeeId:" + employeeId + " has requested booking which is outside office hour.");
            return null;
        } else {
            return new Meeting(employeeId, meetingStartTime, meetingFinishTime);

        }
    }

    private boolean meetingTimeOutsideOfficeHours(LocalTime officeStartTime, LocalTime officeFinishTime,
            LocalTime meetingStartTime, LocalTime meetingFinishTime) {
        return meetingStartTime.isBefore(officeStartTime) || meetingStartTime.isAfter(officeFinishTime)
                || meetingFinishTime.isAfter(officeFinishTime) || meetingFinishTime.isBefore(officeStartTime);
    }

}
