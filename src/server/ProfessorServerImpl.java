package server;

import common.ProfessorServer;
import common.StudentClient;
import exam.Exam;
import exam.ExamBuilderCSV;
import exam.Question;
import exam.StoreExam;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProfessorServerImpl extends UnicastRemoteObject implements ProfessorServer {

    private Exam exam;
    public HashMap<String, StudentClient> students;
    public HashMap<String, Exam> studentExam;
    public boolean canRegistry;
    private Integer studentsNumber;
    private String studentRequest;

    public ProfessorServerImpl() throws RemoteException {
        super();
        this.students = new HashMap<>();
        this.studentExam = new HashMap<>();
        this.canRegistry = true;
        this.studentsNumber = 0;
    }

    public void uploadExam(String path) throws IOException {
        this.exam = ExamBuilderCSV.build(path);
    }

    public void storeExam(String path) throws IOException{
        StoreExam.storeExam(path, this.studentExam);
    }

    public void stopRegister() {
        this.canRegistry = false;
    }

    public void startExam() throws RemoteException {
        for (HashMap.Entry<String, StudentClient> studentSet  : this.students.entrySet()) {
            String studentId = studentSet.getKey();
            StudentClient student = studentSet.getValue();
            student.startExam("The exam starts now");
            student.sendQuestion(this.studentExam.get(studentId).nextQuestion());
        }
    }

    @Override
    public void registerStudent(StudentClient client, String studentId) throws RemoteException {
        synchronized (this) {
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
    }

    @Override
    public void sendAnswer(String studentId, Question question) throws RemoteException {
        synchronized (this) {
            this.studentExam.get(studentId).answer(question);
            this.studentRequest = studentId;
            notify();
        }
    }

    public String getStudentId() {
        synchronized (this) {
            return this.studentRequest;
        }
    }

    public boolean studentHasFinished(String studentId) {
        synchronized (this) {
            return !this.studentExam.get(studentId).hasNext();
        }
    }
}
