package com.sihoo.me.debook.domains;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.sihoo.me.debook.errors.SortTypeNotFoundException;

public enum SortType {
    DATE("date"), SIM("sim");

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
        throw new SortTypeNotFoundException(type);

    }
}
