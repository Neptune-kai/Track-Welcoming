import java.util.ArrayList;


public class Quiz {
    private ArrayList<String> questions;
    
    public Quiz() {
        questions = new ArrayList<>();
        createquestion();
    }
    public String createquestion(){
        return questions.get((int) (Math.random() * questions.size()));
    }
        questions.add("What type of endurance would you say you have?");
        questions.add("How long can you run continuously?");
        questions.add("Do you like to feel close to dying?");
        questions.add("How coordinated are you?");
        questions.add("What previous sports have you played?");
        questions.add("How committed are you to your training?");
        questions.add("Do you like to build up speed?");
        questions.add("When you're really tired, can you still push yourself?");


    }
}
