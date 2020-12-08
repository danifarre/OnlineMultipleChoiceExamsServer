package server;

public class ServerMessages {

    public void serverStart() {
        System.out.println("Please, specify the file name of the exam:");
    }

    public void studentsRegister() {
        System.out.println("The students are registering.");
        System.out.println("If you want to start the exam, press (s).");
    }

    public void examStart() {
        System.out.println("The exam start now.");
        System.out.println("If you want to stop the exam, press (c).");
    }

    public void examStop() {
        System.out.println("The exam have been stopped.");
        System.out.println("The grades have been saved.");
    }

    public void studentJoined(String studentId, Integer studentsNumber){
        System.out.println("Student " + studentId + " joined, there are " + studentsNumber + " students in the room.");
    }

    public void studentTriedToJoin(String studentId) {
        System.out.println("Student " + studentId + " tried to join the room.");
    }

    public void allStudentsFinished() {
        System.out.println("All the students finished the exam.");
        System.out.println("Press (c) to close the exam and save the grades.");
    }

    public void examFinished() {
        System.out.println("Exam finished.");
    }

}
