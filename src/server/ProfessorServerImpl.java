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
import java.util.Map;
import java.util.Set;

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

    public void startExam() throws RemoteException {
        for (Map.Entry<String, StudentClient> studentSet  : this.students.entrySet()) {
            String studentId = studentSet.getKey();
            StudentClient student = studentSet.getValue();
            student.startExam("The exam starts now");
            student.sendQuestion(this.studentExam.get(studentId).nextQuestion());
        }
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
            this.studentExam.put(studentId, this.exam.copy());
        } else {
            client.registerExpired("The registration time has expired");
            System.out.println("Student " + studentId + " tried to join");
        }
    }

    @Override
    public void sendAnswer(String studentId, Question question) throws RemoteException {
        this.studentExam.get(studentId).answer(question);
        if (this.studentExam.get(studentId).hasNext()) {
            this.students.get(studentId).sendQuestion(this.studentExam.get(studentId).nextQuestion());
        } else {
            this.students.get(studentId).examFinished(this.studentExam.get(studentId).getGrade(),
                                                      "You finished the exam");
        }
    }
}
