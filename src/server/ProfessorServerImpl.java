package server;

import common.ProfessorServer;
import common.StudentClient;
import exam.Exam;
import exam.ExamBuilderCSV;
import exam.Question;
import exam.StoreExam;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProfessorServerImpl extends UnicastRemoteObject implements ProfessorServer {

    private Exam exam;
    public HashMap<String, StudentClient> students;
    public HashMap<String, Exam> studentExam;
    public boolean canRegistry;
    public boolean examInProgress;
    private Integer studentsNumber;
    private String studentRequest;
    private Integer examsInProgress;

    public ProfessorServerImpl() throws RemoteException {
        super();
        this.students = new HashMap<>();
        this.studentExam = new HashMap<>();
        this.canRegistry = true;
        this.studentsNumber = 0;
        this.examsInProgress = 0;
        this.examInProgress = true;
    }

    public void uploadExam(String path) throws IOException {
        this.exam = ExamBuilderCSV.build(path);
    }

    public void stopRegister() {
        this.canRegistry = false;
    }

    public void nextQuestion(String studentRequest) throws RemoteException {
        this.students.get(studentRequest).examFinished(this.studentExam.get(studentRequest).getGrade(), "You finished the exam");
    }

    public void examFinished(String studentId) throws RemoteException {
        this.students.get(studentRequest).sendQuestion(this.studentExam.get(studentRequest).nextQuestion());
    }

    public void startExam() throws RemoteException {
        for (HashMap.Entry<String, StudentClient> studentSet  : this.students.entrySet()) {
            String studentId = studentSet.getKey();
            StudentClient student = studentSet.getValue();
            try {
                student.startExam("The exam starts now");
                student.sendQuestion(this.studentExam.get(studentId).nextQuestion());
                this.examsInProgress++;
            } catch (ConnectException ignored) {}
        }
    }

    @Override
    public synchronized void registerStudent(StudentClient client, String studentId) throws RemoteException {
        if (this.canRegistry) {
            this.students.put(studentId, client);
            this.studentsNumber += 1;
            System.out.println("Student " + studentId +
                    " joined, there are " +
                    this.studentsNumber +
                    " students in the room");
            this.studentExam.put(studentId, this.exam.copy());
        } else {
            client.registerExpired("The registration time has expired");
            System.out.println("Student " + studentId + " tried to join");
        }
    }

    @Override
    public synchronized void sendAnswer(String studentId, Question question) throws RemoteException {
        if (this.examInProgress) {
            this.studentExam.get(studentId).answer(question);
            this.studentRequest = studentId;
        }
        notify();
    }

    public String getStudentId() {
        return this.studentRequest;
    }

    public synchronized boolean studentHasFinished(String studentId) {
        if (!this.studentExam.get(studentId).hasNext()) {
            this.examsInProgress--;
            return true;
        }
        return false;
    }

    public void examFinished() throws RemoteException {
        for (HashMap.Entry<String, StudentClient> studentSet  : this.students.entrySet()) {
            String studentId = studentSet.getKey();
            StudentClient student = studentSet.getValue();
            try {
                student.examFinished(this.studentExam.get(studentId).getGrade(),"The exam was finish");
            } catch (ConnectException ignored) {}
        }
    }

    public boolean studentsFinished() {
        return this.examsInProgress == 0;
    }
}
