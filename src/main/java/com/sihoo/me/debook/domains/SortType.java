package com.sihoo.me.debook.domains;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.sihoo.me.debook.errors.CustomException;
import org.springframework.http.HttpStatus;

public enum SortType {
    date("date"), sim("sim");

    private final String type;

    SortType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @JsonCreator
    public static SortType from(String type) {
        for (SortType s : SortType.values()) {
            if (s.getType().equals(type)) {
                return s;
            }
        }
        throw new CustomException("[ERROR] There is no type lie request sort type(Type: " + type + ")", HttpStatus.NOT_FOUND);

    }
}
