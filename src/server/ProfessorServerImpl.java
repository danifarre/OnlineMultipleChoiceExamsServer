package server;

import common.ProfessorServer;
import common.StudentClient;
import exam.Exam;
import exam.Question;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ProfessorServerImpl extends UnicastRemoteObject implements ProfessorServer {

    public HashMap<String, StudentClient> students;
    private HashMap<String, Exam> studentExam;
    private boolean canRegistry;

    public ProfessorServerImpl() throws RemoteException {
        super();
        this.students = new HashMap<>();
        this.studentExam = new HashMap<>();
        this.canRegistry = true;
    }

    @Override
    public void registerStudent(StudentClient client, String studentId) {
        this.students.put(studentId, client);
    }

    @Override
    public void sendAnswer(String studentId, Question question) {
        throw new UnsupportedOperationException();
    }
}
