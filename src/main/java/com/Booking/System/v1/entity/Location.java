package com.booking.system.v1.entity;

import lombok.Getter;

@Getter
public enum Location {
    GROUND_FLOOR("A"),
    FLOOR_1("B"),
    FLOOR_2("C"),
    FLOOR_3("D"),
    FLOOR_4("E"),
    FLOOR_5("F"),
    FLOOR_6("G"),
    FLOOR_7("H"),
    FLOOR_8("I"),
    FLOOR_9("J"),
    FLOOR_10("K");


    private final String letter;


    Location(String letter) {
        this.letter = letter;
    }


    public String buildRoomCode(int roomNumber) {
        return letter + String.valueOf(roomNumber);
    }

}
