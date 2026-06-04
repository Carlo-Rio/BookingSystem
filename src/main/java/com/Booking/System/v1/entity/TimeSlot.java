package com.booking.system.v1.entity;

import jakarta.persistence.Embeddable;

import lombok.Data;

import java.time.DayOfWeek;

import java.time.LocalTime;
import java.util.Comparator;

@Embeddable
@Data
public class TimeSlot implements Comparable<TimeSlot> {


    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    private static final Comparator<TimeSlot> COMPARATOR =
            Comparator
                    .comparing(TimeSlot::getDayOfWeek)
                    .thenComparing(TimeSlot::getStartTime)
                    .thenComparing(TimeSlot::getEndTime);

    @Override
    public int compareTo(final TimeSlot that) {
        return TimeSlot.COMPARATOR.compare(this, that);
    }


}
