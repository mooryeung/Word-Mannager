public class Word {

    private String english;      // 영어 단어
    private String meaning;      // 뜻


    private int quizCount;       // 출제된 횟수
    private int correctCount;    // 맞춘 횟수
    private double weight;    // 가중치 ( 1-정답률)
    private Boolean examFlag ;




    public Word(String english, String meaning) {
        this.english = english;
        this.meaning = meaning;
        this.quizCount = 0;
        this.correctCount = 0;
        this.weight = 1.0; // 처음에는 가중치를 1로 넣고 나올 수 있게끔
        this.examFlag = false;
    }
    public Word(String english, String meaning ,int quizCount ,int correctCount) {
        this.english = english;
        this.meaning = meaning;
        this.quizCount = quizCount;
        this.correctCount = correctCount;
        updateWeight();      // ← 여기서 가중치 계산
        this.examFlag = false;
    }

    public String getEnglish() { return english; }
    public String getMeaning() { return meaning; }
    public double getWeight()  { return weight;}
    public int getQuizCount() { return quizCount; }
    public int getCorrectCount() { return correctCount; }
    public boolean getExamFlag() { return examFlag; }

    // 정답률(%) 계산
    public double getAccuracy() {
        if (quizCount == 0) return 0.0;
        return (double) correctCount / quizCount * 100.0;
    }
    public void increaseQuizCount() {
        quizCount++;
        updateWeight();
    }
    public void increaseCorrectCount() {
        correctCount++;
        updateWeight();
    }
    private void updateWeight() {
        if (quizCount == 0) weight = 1.0;
        else weight = Math.max( 1.0 - ((double) correctCount / quizCount) , 0.1); // 가중치 최소 0.1 설정
    }
    public void setExamFlag(boolean value) {
        this.examFlag = value;
    }
}
