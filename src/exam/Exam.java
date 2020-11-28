package exam;

import java.util.*;

public class Exam {

    private final List<Question> questions;
    private final HashMap<Question, Integer> answers;
    private List<Question> studentAnswers;
    private Integer grade;

    private final ListIterator<Question> itQuestion;

    public Exam(List<Question> questions, HashMap<Question, Integer> answers) {
        this.questions = questions;
        this.answers = answers;
        this.grade = 0;

        this.studentAnswers = new ArrayList<>();
        this.itQuestion = questions.listIterator();
    }

    public Integer getGrade() {
        return this.grade;
    }

    public Question nextQuestion() {
        return this.itQuestion.next();
    }

    public boolean hasNext() {
        return this.itQuestion.hasNext();
    }

    public Question previousQuestion() {
        return this.itQuestion.previous();
    }

    public void answer(Question question) {
        this.studentAnswers.add(question);
        if (correctAnswer(question)) {
            increaseGrade();
        }
    }

    private void increaseGrade() {
        this.grade += 1;
    }

    private boolean correctAnswer(Question question) {
        return question.getAnswer().equals(this.answers.get(question));
    }

    @Override
    public String toString() {
        return "Exam{" +
                "questions=" + questions +
                ", answers=" + answers +
                ", studentAnswers=" + studentAnswers +
                ", grade=" + grade +
                ", itQuestion=" + itQuestion +
                '}';
    }
}
