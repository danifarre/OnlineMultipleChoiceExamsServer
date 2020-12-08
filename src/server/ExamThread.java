package server;

import exam.Exam;

import java.util.HashMap;

public class ExamThread extends Thread {

    private ProfessorServerImpl server;

    public ExamThread(ProfessorServerImpl server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            while (!this.server.studentsFinished()) {
                synchronized (this.server) {
                    this.server.wait();
                    String studentRequest = server.getStudentId();
                    if (this.server.studentHasFinished(studentRequest)) {
                        this.server.nextQuestion(studentRequest);
                    } else {
                        this.server.examFinished(studentRequest);
                    }
                }
            }
            System.out.println("All the students finished the exam");
            System.out.println("Press (c) to close the exam and save the grades");
        } catch (InterruptedException e) {
            System.out.println("Exam finished");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
        }
    }

    public HashMap<String, Exam> finishExam() {
        return this.server.studentExam;
    }
}
