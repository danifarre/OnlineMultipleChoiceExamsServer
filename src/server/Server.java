package server;

import exam.Exam;
import exam.StoreExam;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Scanner;

public class Server {
    private ServerMessages messages = new ServerMessages();

    public static void main(String[] args) {
        new Server().run();
        System.exit(0);
    }

    private void run() {
        Scanner scanner;
        ProfessorServerImpl server;
        String in;
        scanner = new Scanner(System.in);
        try {
            Registry registry = startRegistry(null);
            server = new ProfessorServerImpl();
            registry.bind("exam", server);

            messages.serverStart();
            //System.out.println("Please, specify the file name of the exam");
            server.uploadExam("exam.csv");
            //server.uploadExam(this.scanner.nextLine());

            messages.studentsRegister();
            //System.out.println("The students are registering...");
            //System.out.println("If you want to start the exam, press (s)");
            do {
                in = scanner.nextLine();
            } while (!in.equals("s"));

            server.stopRegister();
            server.startExam();

            ExamThread examThread = new ExamThread(server);
            examThread.start();

            messages.examStart();
            //System.out.println("The exam start now");
            //System.out.println("If you want to close the exam, press (c)");
            do {
                in = scanner.nextLine();
            } while (!in.equals("c"));

            server.examFinished();
            HashMap<String, Exam> exams = examThread.finishExam();

            messages.examStop();
            //System.out.println("The grades have been saved");
            examThread.interrupt();

            StoreExam.storeExam("grades.csv", exams);

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString()); e.printStackTrace();
        }
    }

    private Registry startRegistry(Integer port) throws RemoteException {
        if (port == null) port = 1099;
        try {
            Registry registry = LocateRegistry.getRegistry(port);
            registry.list();
            return registry;
        } catch (RemoteException ex) {
            //System.out.println("RMI registry cannot be located ");
            Registry registry = LocateRegistry.createRegistry(port);
            //System.out.println("RMI registry created at port ");
            return registry;
        }
    }
}
