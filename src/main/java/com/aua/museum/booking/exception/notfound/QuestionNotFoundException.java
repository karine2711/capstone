package com.aua.museum.booking.exception.notfound;

public class QuestionNotFoundException extends NotFoundException {
    public QuestionNotFoundException() {
        super("Question not found!");
    }

    public QuestionNotFoundException(int questionId) {
        super(String.format("No question with id %s", questionId));
    }

    public QuestionNotFoundException(String message) {
        super(message);
    }

}
