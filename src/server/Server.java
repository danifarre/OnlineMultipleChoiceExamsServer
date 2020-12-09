package server;

import exam.Exam;
import exam.StoreExam;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Scanner;

public class Server {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String in;

        try {
            Registry registry = startRegistry(null);
            ProfessorServerImpl server = new ProfessorServerImpl();

            ServerMessages.serverStart();
            server.uploadExam(scanner.nextLine());

            registry.bind("exam", server);

            ServerMessages.studentsRegister();

            do {
                in = scanner.nextLine();
            } while (!in.equals("s"));

            server.stopRegister();
            server.startExam();

            ExamThread examThread = new ExamThread(server);
            examThread.start();

            ServerMessages.examStart();

            do {
                in = scanner.nextLine();
            } while (!in.equals("c"));
            examThread.interrupt();

            server.examFinished();
            HashMap<String, Exam> exams = examThread.finishExam();
            ServerMessages.examStop();

            StoreExam.store("grades.csv", exams);

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString()); e.printStackTrace();
        }
        System.exit(0);
    }


    private static Registry startRegistry(Integer port) throws RemoteException {
        if (port == null) port = 1099;
        try {
            Registry registry = LocateRegistry.getRegistry(port);
            registry.list();
            return registry;
        } catch (RemoteException ex) {
            Registry registry = LocateRegistry.createRegistry(port);
            return registry;
        }
    }
}
