import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class VocaHelper {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        WordMannage W = new WordMannage() ;
        W.loadFromFile();
        W.run();

    }
}
