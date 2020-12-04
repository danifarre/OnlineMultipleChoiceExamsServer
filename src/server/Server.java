package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Server {
    private Scanner scanner;
    private ProfessorServerImpl server;

    public static void main(String[] args) {
        new Server().run();
        System.exit(0);
    }

    private void run() {
        this.scanner = new Scanner(System.in);
        try {
            Registry registry = startRegistry(null);
            this.server = new ProfessorServerImpl();
            registry.bind("exam", server);

            System.out.println("Please, specify the file name of the exam");
            server.uploadExam("exam.csv");
            //server.uploadExam(this.scanner.nextLine());

            startRegister();
            startExam();
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString()); e.printStackTrace();
        }
        this.scanner.close();
    }

    private void startExam() throws RemoteException {
        this.server.startExam();
        String in;
        do {
            in = this.scanner.nextLine();
        } while (!in.equals("c"));
    }

    private void startRegister() {
        System.out.println("The students are registering...");
        System.out.println("If you want to start the exam, press (s)");
        String in;
        do {
            in = scanner.nextLine();
        } while (!in.equals("s"));
        this.server.stopRegister();
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
