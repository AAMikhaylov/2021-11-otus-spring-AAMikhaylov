package ru.otus.hw03.domain;

public class Question {
    private final int id;
    private final String content;
    private final String answer;

    public Question(int id, String content, String answer) {
        this.id = id;
        this.content = content;
        this.answer = answer;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getAnswer() {
        return answer;
    }

    @Override
    public String toString() {
        return "Question {" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
