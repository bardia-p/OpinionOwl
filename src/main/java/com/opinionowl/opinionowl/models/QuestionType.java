package com.opinionowl.opinionowl.models;

import lombok.Getter;

/**
 * An Enum to keep track of the different question types.
 */

@Getter
public enum QuestionType {
    LONG_ANSWER("Long Answer"),
    RADIO_CHOICE("Radio Choice"),
    RANGE("Range");

    private final String type;
    QuestionType(String type) {
        this.type = type;
    }
}
