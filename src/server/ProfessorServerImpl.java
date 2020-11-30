package server;

import common.ProfessorServer;
import common.StudentClient;
import exam.Exam;
import exam.ExamBuilderCSV;
import exam.Question;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ProfessorServerImpl extends UnicastRemoteObject implements ProfessorServer {

    private Exam exam;
    private HashMap<String, StudentClient> students;
    private HashMap<String, Exam> studentExam;
    public boolean canRegistry;
    private Integer studentsNumber;

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

    public void stopRegister() {
        this.canRegistry = false;
    }

    @Override
    public void registerStudent(StudentClient client, String studentId) throws RemoteException {
        if (this.canRegistry) {
            this.students.put(studentId, client);
            this.studentsNumber += 1;
            System.out.println("Student " + studentId +
                    " joined, there are " +
                    this.studentsNumber +
                    " students in the room");
        } else {
            client.registerExpired("The registration time has expired");
            System.out.println("Student " + studentId + " tried to join");
        }
    }

    @Override
    public void sendAnswer(String studentId, Question question) {
        throw new UnsupportedOperationException();
    }
}
