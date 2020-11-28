package exam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable {

    private final Integer questionNumber;
    private final String statement;
    private List<String> choices;
    private Integer answer;

    public Question(Integer questionNumber, String statement, List<String> choices) {
        this.questionNumber = questionNumber;
        this.statement = statement;
        this.choices = choices;

        this.choices = new ArrayList<>();
        this.answer = 0;
    }

    public void answer(Integer answer) {
        this.answer = answer;
    }

    public Integer getAnswer() {
        return this.answer;
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionNumber=" + questionNumber +
                ", statement='" + statement + '\'' +
                ", choices=" + choices +
                ", answer=" + answer +
                '}';
    }
}
