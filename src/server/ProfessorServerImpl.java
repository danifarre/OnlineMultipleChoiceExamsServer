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
import java.rmi.UnmarshalException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProfessorServerImpl extends UnicastRemoteObject implements ProfessorServer {

    private Exam exam;
    private HashMap<String, StudentClient> students;
    private HashMap<String, Exam> studentExam;
    private boolean canRegistry;
    private boolean examInProgress;
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

    public synchronized void nextQuestion(String studentRequest) throws RemoteException {
        try {
            this.students.get(studentRequest).examFinished(this.studentExam.get(studentRequest).getGrade(), "You finished the exam.");
        } catch (UnmarshalException ignored) {}
    }

    public synchronized void examFinished(String studentId) throws RemoteException {
        this.students.get(studentRequest).sendQuestion(this.studentExam.get(studentRequest).nextQuestion());
    }

    public void startExam() throws RemoteException {
        for (HashMap.Entry<String, StudentClient> studentSet  : this.students.entrySet()) {
            String studentId = studentSet.getKey();
            StudentClient student = studentSet.getValue();
            try {
                student.startExam("The exam starts now.");
                student.sendQuestion(this.studentExam.get(studentId).nextQuestion());
                this.examsInProgress++;
            } catch (ConnectException ignored) {}
        }
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
                student.examFinished(this.studentExam.get(studentId).getGrade(),"The exam was finished.");
            } catch (ConnectException | UnmarshalException ignored) {}
        }
    }

    public boolean studentsFinished() {
        return this.examsInProgress == 0;
    }

    public HashMap<String, Exam> getStudentsExams() {
        return this.studentExam;
    }

    @Override
    public synchronized void registerStudent(StudentClient client, String studentId) throws RemoteException {
        if (this.canRegistry) {
            this.students.put(studentId, client);
            this.studentsNumber += 1;
            ServerMessages.studentJoined(studentId, this.studentsNumber);
            this.studentExam.put(studentId, this.exam.copy());
        } else {
            client.registerExpired("The registration time has expired.");
            ServerMessages.studentTriedToJoin(studentId);
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
}
