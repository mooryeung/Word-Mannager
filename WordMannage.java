import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;


public class WordMannage {
    private final List<Word> words = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        while (true) {
            printMainMenu();
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해 주세요.");
                continue;
            }
            System.out.print("\n");
                   if (choice == 1) {
                showList();
            } else if (choice == 2) {
                add();
            } else if (choice == 3) {
                delete();

            } else if (choice == 4) {
                vocaQuiz();

            } else if (choice == 5) {
                saveToFile();
                if (onOff())return;
                else continue;
            } else if (choice == 10) {
                developScreen();
            } else System.out.println("1~5 사이의 수를 입력 하세요");
        }
    }



        public void printMainMenu () {

            System.out.println("\n=======VocaHelper=======\n");
            System.out.println("    저장된 단어 수 : " + words.size() + "\n");
            System.out.println("    (1) 단어 목록 보기");
            System.out.println("    (2) 단어 추가");
            System.out.println("    (3) 단어 삭제");
            System.out.println("    (4) 단어 퀴즈");
            System.out.println("    (5) 저장 후 종료");
            System.out.println("========================");
            System.out.print(" <메뉴 선택> :");

        }
        public void developScreen () {


            if (words.isEmpty()) {
                System.out.println("저장된 단어가 없습니다");
            } else {
                if (words.size() < 10) {
                    for (int i = 0; i < words.size(); i++) {
                        Word w = words.get(i);
                        System.out.printf("[%-1d] %-7s : %6.2f%%  : %3.1f : %2d : %2d   ",
                                i + 1,
                                w.getEnglish(),
                                w.getAccuracy(),
                                w.getWeight(),
                                w.getQuizCount(),
                                w.getCorrectCount()

                        );
                        if ((i + 1) % 4 == 0) System.out.println();
                    }
                    System.out.println("\n");
                } else if (10 <= words.size() && words.size() < 100) {
                    for (int i = 0; i < words.size(); i++) {
                        Word w = words.get(i);
                        System.out.printf("[%-2d] %-7s : %6.2f%%  : %3.1f : %2d : %2d   ",
                                i + 1,
                                w.getEnglish(),
                                w.getAccuracy(),
                                w.getWeight(),
                                w.getQuizCount(),
                                w.getCorrectCount()

                        );
                        if ((i + 1) % 3 == 0) System.out.println();
                    }
                } else if (100 <= words.size() && words.size() < 1000) {
                    for (int i = 0; i < words.size(); i++) {
                        Word w = words.get(i);
                        System.out.printf("[%-3d] %-7s : %6.2f%%  : %3.1f : %2d : %2d   ",
                                i + 1,
                                w.getEnglish(),
                                w.getAccuracy(),
                                w.getWeight(),
                                w.getQuizCount(),
                                w.getCorrectCount()

                        );
                    }

                }
                System.out.print("\n\n");
                System.out.print("엔터키를 누르면 메인으로 넘어갑니다 ...");
                scanner.nextLine();
                System.out.print("\n\n");
            }

        }



    public boolean onOff() {
        while (true) {
            System.out.print("프로그램을 종료하겠습니까? yes:[Y] no:[N] :");

            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("아무 것도 입력하지 않았습니다. 다시 입력해주세요.");
                continue;
            }

            char YN = input.charAt(0);
            if (YN == 'Y' || YN == 'y') {
                System.out.println("프로그램을 종료합니다!");
                return true;

            } else if (YN == 'N' || YN == 'n') {
                return false;

            } else {
                System.out.println("다시 입력해주세요 :");
                continue;
            }
        }
    }


    public void showList() {
        if (words.isEmpty()) {
            System.out.println("저장된 단어가 없습니다");
        } else {
            if (words.size() < 10) {
                for (int i = 0; i < words.size(); i++) {
                    Word w = words.get(i);
                    System.out.printf("[%d] %-10s : %-7s %6.2f%%    ",
                            i + 1,
                            w.getEnglish(),
                            w.getMeaning(),
                            w.getAccuracy()

                    );
                    if ((i + 1) % 4 == 0) System.out.println();
                }
                System.out.println("\n");
            } else if (10 <= words.size() && words.size() < 100) {
                for (int i = 0; i < words.size(); i++) {
                    Word w = words.get(i);
                    System.out.printf("[%2d]%-10s : %-7s%6.2f%%    ",
                            i + 1,
                            w.getEnglish(),
                            w.getMeaning(),
                            w.getAccuracy()

                    );
                    if ((i + 1) % 4 == 0) System.out.println();
                }
            } else if (100 <= words.size() && words.size() < 1000) {
                for (int i = 0; i < words.size(); i++) {
                    Word w = words.get(i);
                    System.out.printf("[%3d] %-10s : %-7s %6.2f%%    ",
                            i + 1,
                            w.getEnglish(),
                            w.getMeaning(),
                            w.getAccuracy()

                    );
                    if ((i + 1) % 4 == 0) System.out.println();
                }
            }

        }
        System.out.print("\n\n");
        System.out.print("엔터키를 누르면 메인으로 넘어갑니다 ...");
        scanner.nextLine() ;
        System.out.print("\n\n");
    }


    public void delete() {

        while (true) {
            showList();
            System.out.print("(메뉴:0) 삭제할 번호 입력 :");

            int number;
            try {
                number = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해 주세요.");
                continue;
            }
            if (number == 0) break;
            else if (0 < number && number <= words.size()) {
                System.out.print("\n");
                System.out.println("[" + number + "] " + words.get(number - 1).getEnglish() + " 을(를) 단어장에서 삭제했습니다.\n");
                words.remove(number - 1);

            } else {
                System.out.print("잘못 입력했습니다. 다시 입력해주세요.\n");
                continue;
            }
        }

    }

    public void add() {
        String english;
        String meaning;
        while (true) {
            while (true) {
                boolean duplicate = false;
                System.out.print("(메뉴:0) 영단어 입력 :");
                english = scanner.nextLine().trim();

                if (english.equals("0")) return;

                if (words.size() != 0) {
                    for (int i = 0; i < words.size(); i++) {
                        if (english.equalsIgnoreCase(words.get(i).getEnglish())) {
                            System.out.println("이미 존재하는 단어 입니다.\n");
                            duplicate = true;
                        }
                    }
                    if (duplicate) continue;
                }

                if (english.isEmpty()) {
                    System.out.println("다시 입력해주세요"); // 아무것도 입력하지 않은경우
                    continue;
                }
                if (!english.matches("[a-zA-Z]+")) {
                    System.out.println("영어만 입력할 수 있습니다 다시 입력해주세요");
                    continue;
                } else break;
            }

            while (true) {
                System.out.print("한글 뜻 입력 :");
                meaning = scanner.nextLine().trim();
                if (meaning.isEmpty()) {
                    System.out.println("다시 입력해주세요"); // 아무것도 입력하지 않은경우
                    continue;
                }
                if (!meaning.matches("[가-힣]+")) {
                    System.out.println("한글만 입력할 수 있습니다 다시 입력해주세요");
                    continue;
                } else break;
            }

            Word word = new Word(english, meaning);
            words.add(word);
            System.out.println("'" + word.getEnglish() + "'" + "를 추가했습니다");
            System.out.print("\n");
        }

    }

    public Word decideWord() {
        double totalWeight = 0.0;  //가중치 합
        double pickWord;  //  뽑는 랜덤 가중치 (0< 뽑는 랜덤 가중치 <총가중치)
        double temp = 0.0;

        for (Word w : words) {
            totalWeight += w.getWeight();
        }  //총 가중치 계산
        pickWord = Math.random() * totalWeight;
        for (Word w : words) {
            temp += w.getWeight();
            if (temp >= pickWord) {
                return w;
            }
        }
        return null;
    }

    public void vocaQuiz() {
        String answer;
        Word question;

        if (words.size() < 10) {
            System.out.println("단어가 10개 이상일 경우에만 퀴즈를 진행할 수 있습니다\n");
            return;
        }

        for (int i = 0; i < 10; i++) {

            int flag = (int)(Math.random() * 2); // 0 또는 1 . flag = 0 단어출력 , flag = 1 뜻 출력

            if (flag == 0) {

                question = decideWord();
                while(question.getExamFlag()) {
                    question = decideWord();
                }


                System.out.print("문제" + "[" + (i+1) + "] " + question.getEnglish() + " = ");
                answer = scanner.nextLine();
                if (answer.trim().equals(question.getMeaning())) {
                    System.out.println("정답");
                    question.increaseQuizCount();
                    question.increaseCorrectCount();
                } else {
                    System.out.println("땡");
                    question.increaseQuizCount();
                }
                question.setExamFlag(true);

            }
            else if (flag == 1) {

                question = decideWord();
                while(question.getExamFlag()) {
                    question = decideWord();
                }

                System.out.print("문제" + "[" + (i+1) + "] " + question.getMeaning() + " = ");
                answer = scanner.nextLine();
                if (answer.trim().equalsIgnoreCase(question.getEnglish().trim())) {
                    System.out.println("정답");
                    question.increaseQuizCount();
                    question.increaseCorrectCount();
                } else {
                    System.out.println("땡");
                    question.increaseQuizCount();
                }
                question.setExamFlag(true);

            }

        }
        for (Word w : words) {
            w.setExamFlag(false);
        }  // flag 초기화

    }

    public void saveToFile() {
        String filename = "voca.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {

            for (Word w : words) {
                // apple=사과,3,2 형태로 저장
                writer.write(w.getEnglish() + "," + w.getMeaning()+","+w.getQuizCount()+","+w.getCorrectCount());
                writer.newLine(); // 줄바꿈
            }

            System.out.println("\n단어가 '" + filename + "' 파일에 저장되었습니다.");

        } catch (IOException e) {
            System.out.println("파일 저장 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }

    public void loadFromFile() {

        String filename = "voca.txt";
        File file = new File(filename);

        if (!file.exists()) {
            System.out.println("\n\n저장된 단어장이 없어 새로 생성합니다.");
            return;  // 불러올 게 없으니 종료
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = reader.readLine()) != null) {

                // 라인 형식: english=meaning,quizcount,correctcount
                String[] parts = line.split(",");


                // parts[0] = 영어, parts[1] = 뜻
                if (parts.length == 4) {
                    String eng = parts[0].trim();
                    String kor = parts[1].trim();
                    String quizCount = parts[2].trim();
                    String correctCount = parts[3].trim();

                    Word word = new Word(eng, kor ,Integer.parseInt(quizCount),Integer.parseInt(correctCount));
                    words.add(word);


                }
            }

            System.out.println("파일에서 단어를 불러왔습니다.");

        } catch (IOException e) {
            System.out.println("파일을 불러오는 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }
    public List<Word> getWords() {
        return words;
    }
}



