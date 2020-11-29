package common;

import exam.Question;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProfessorServer extends Remote {
    void registerStudent(StudentClient client, String studentId) throws RemoteException;
    void sendAnswer(String studentId, Question question) throws RemoteException;
}
