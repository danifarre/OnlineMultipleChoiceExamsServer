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
        this.answer = 0;
    }

    public void answer(Integer answer) {
        this.answer = answer;
    }

    public Integer getAnswer() {
        return this.answer;
    }

    public Integer getQuestionNumber() {
        return this.questionNumber;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder(this.questionNumber +
                ". " + this.statement + "\n");
        for (int i = 0; i < this.choices.size(); i++) {
            output.append("    ").append(i + 1).append(") ").append(this.choices.get(i));
        }
        return output.toString();
    }
}
