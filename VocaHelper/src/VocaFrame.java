import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;


public class VocaFrame extends JFrame {

    private WordMannage manager;
    private JLabel centerLabel;
    private QuizResult lastResult = null;



    public VocaFrame() {

        setTitle("VocaHelper - 단어 암기 프로그램");
        setSize(600, 400);                // 프레임 크기
        setLocationRelativeTo(null);      // 화면 가운데 배치
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 상단 제목 라벨
        JLabel titleLabel = new JLabel("VocaHelper", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        //중앙 패널: 사진 + 저장된 단어
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);  // 배경 투명 (필요 시)

// 전체 가운데 정렬 설정
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

// 이미지 불러오기
        ImageIcon icon = new ImageIcon("src/img/main.png");
        Image scaled = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(scaled);
        JLabel imageLabel = new JLabel(resizedIcon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

// 저장된 단어 라벨
        centerLabel = new JLabel("저장된 단어: 0개");
        centerLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        centerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

// 중앙 정렬을 위해 Vertical Glue 추가
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(imageLabel);
        centerPanel.add(Box.createVerticalStrut(15)); // 사진과 텍스트 사이 간격
        centerPanel.add(centerLabel);
        centerPanel.add(Box.createVerticalGlue());

        add(centerPanel, BorderLayout.CENTER);

        // 하단 메뉴 버튼들
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());

        JButton btnShowList = new JButton("단어 목록 보기");
        JButton btnAdd = new JButton("단어 추가");
        JButton btnQuiz = new JButton("단어 퀴즈");
        JButton btnExit = new JButton("저장 후 종료");
        JButton btnQuizResult = new JButton("최근 퀴즈 결과");

        // 각 버튼에 동작을 연결합니다.
        btnQuizResult.addActionListener(e -> showQuizResult());
        btnShowList.addActionListener(e -> showWordList());
        btnAdd.addActionListener(e -> showAddWord());
        btnQuiz.addActionListener(e -> startQuiz());
        btnExit.addActionListener(e -> exitProgram());

        // 버튼들을 패널에 추가
        bottomPanel.add(btnShowList);
        bottomPanel.add(btnAdd);
        bottomPanel.add(btnQuiz);
        bottomPanel.add(btnQuizResult);
        bottomPanel.add(btnExit);

        add(bottomPanel, BorderLayout.SOUTH);

        // WordMannage 초기화 및 파일에서 단어 불러오기
        manager = new WordMannage();
        manager.loadFromFile();

        centerLabel.setText("저장된 단어: " + manager.getWords().size() + "개");
    }


    private void showWordList() {
        JDialog dialog = new JDialog(this, "단어 목록 / 삭제", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        String[] columns = {"영어", "뜻", "정답률(%)"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 셀 수정 방지
            }
        };

        // 테이블 데이터 채우기
        for (Word w : manager.getWords()) {
            double rate = (w.getQuizCount() == 0)
                    ? 0.0
                    : (w.getCorrectCount() * 100.0 / w.getQuizCount());

            Object[] row = {
                    w.getEnglish(),
                    w.getMeaning(),
                    String.format("%.1f", rate)
            };

            model.addRow(row);
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton btnDelete = new JButton("선택한 단어 삭제");
        JButton btnClose = new JButton("닫기");

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(dialog, "삭제할 단어를 선택하세요.");
                return;
            }

            String english = (String) model.getValueAt(row, 0);

            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "정말 삭제하시겠습니까?\n(" + english + ")",
                    "삭제 확인",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // 실제 데이터 삭제
                manager.getWords().removeIf(w -> w.getEnglish().equals(english));

                // 테이블에서 삭제
                model.removeRow(row);

                // 단어 개수 UI 갱신
                centerLabel.setText("저장된 단어: " + manager.getWords().size() + "개");

                JOptionPane.showMessageDialog(dialog, "삭제되었습니다.");
            }
        });

        btnClose.addActionListener(e -> dialog.dispose());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnDelete);
        bottomPanel.add(btnClose);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }


    private void showAddWord() {
        // 1) 다이얼로그(작은 창) 생성
        JDialog dialog = new JDialog(this, "단어 추가", true);
        dialog.setSize(400, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // 2) 입력 필드 패널 (라벨 + 텍스트필드)
        JPanel fieldPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        fieldPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblEng = new JLabel("영단어:");
        JTextField txtEng = new JTextField();

        JLabel lblKor = new JLabel("뜻(한글):");
        JTextField txtKor = new JTextField();

        fieldPanel.add(lblEng);
        fieldPanel.add(txtEng);
        fieldPanel.add(lblKor);
        fieldPanel.add(txtKor);

        // 3) 버튼 패널 (추가 / 취소)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOk = new JButton("추가");
        JButton btnCancel = new JButton("취소");
        buttonPanel.add(btnOk);
        buttonPanel.add(btnCancel);

        dialog.add(fieldPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // 4) "추가" 버튼 눌렀을 때 동작
        btnOk.addActionListener(e -> {
            String eng = txtEng.getText().trim();
            String kor = txtKor.getText().trim();

            // ── (1) 빈값 체크 ──
            if (eng.isEmpty() || kor.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "영단어와 뜻을 모두 입력해 주세요.");
                return;
            }

            // ── (2) 영어/한글 형식 체크 ──
            if (!eng.matches("[a-zA-Z]+")) {
                JOptionPane.showMessageDialog(dialog, "영단어는 영어 알파벳만 입력할 수 있습니다.");
                return;
            }
            if (!kor.matches("[가-힣]+")) {
                JOptionPane.showMessageDialog(dialog, "뜻은 한글만 입력할 수 있습니다.");
                return;
            }

            // ── (3) 중복 단어 체크 ──
            for (Word w : manager.getWords()) {
                if (eng.equalsIgnoreCase(w.getEnglish())) {
                    JOptionPane.showMessageDialog(dialog, "이미 존재하는 단어입니다.");
                    return;
                }
            }

            // ── (4) 실제 리스트에 단어 추가 ──
            Word newWord = new Word(eng, kor);
            manager.getWords().add(newWord);

            JOptionPane.showMessageDialog(dialog, "'" + eng + "' 단어가 추가되었습니다.");
            txtEng.setText("");
            txtKor.setText("");
            txtEng.requestFocus();
        });

        // 5) "취소" 버튼은 그냥 창 닫기
        btnCancel.addActionListener(e -> dialog.dispose());

        // 6) 실제로 창 띄우기
        dialog.setVisible(true);
    }

    private void startQuiz() {

        String[] options = {"쉬움(10초)", "중간(6초)", "어려움(5초)", "취소"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "난이도를 선택하세요.",
                "난이도 선택",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 3 || choice == JOptionPane.CLOSED_OPTION) return;

        int timeLimit = (choice == 0) ? 10 :
                (choice == 1) ? 6  :
                        5;

        // 퀴즈 결과 객체 생성
        QuizResult result = new QuizResult(options[choice]);

        // 후보 pool 생성
        List<Word> pool = new ArrayList<>();

        for (Word w : manager.getWords()) {

            double rate = (w.getQuizCount() == 0) ?
                    0.0 :
                    (double) w.getCorrectCount() / w.getQuizCount() * 100.0;

            if (choice == 2) { // Hard
                if (rate < 50.0) pool.add(w);
            } else {
                pool.add(w);
            }
        }

        // Hard 모드 보정
        if (choice == 2 && pool.size() < 10) {

            List<Word> sortedAll = new ArrayList<>(manager.getWords());

            sortedAll.sort((a, b) -> {

                double rateA = (a.getQuizCount() == 0) ? 0.0 :
                        (double) a.getCorrectCount() / a.getQuizCount();

                double rateB = (b.getQuizCount() == 0) ? 0.0 :
                        (double) b.getCorrectCount() / b.getQuizCount();

                return Double.compare(rateA, rateB);
            });

            for (Word w : sortedAll) {
                if (!pool.contains(w)) {
                    pool.add(w);
                    if (pool.size() >= 10) break;
                }
            }
        }

        if (pool.size() < 10) {
            JOptionPane.showMessageDialog(this,
                    "퀴즈를 진행할 단어가 부족합니다.");
            return;
        }

        // 중복 방지
        Set<Word> usedWords = new HashSet<>();

        int correct = 0;

        // 10문제 출제
        for (int i = 0; i < 10; i++) {

            Word q;

            if (choice == 0) { // Easy
                while (true) {
                    Collections.shuffle(pool);
                    q = pool.get(0);
                    if (!usedWords.contains(q)) break;
                }
            } else { // Normal·Hard → 가중치 기반
                while (true) {
                    q = weightedRandomPick(pool);
                    if (!usedWords.contains(q)) break;
                }
            }

            usedWords.add(q);

            int flag = (int) (Math.random() * 2);

            String question, answer;
            if (flag == 0) {
                question = "뜻을 입력하세요:\n" + q.getEnglish();
                answer = q.getMeaning();
            } else {
                question = "영어를 입력하세요:\n" + q.getMeaning();
                answer = q.getEnglish();
            }

            String userInput = showTimedInputDialog(question, timeLimit);

            q.increaseQuizCount();

            boolean isCorrect = userInput != null &&
                    userInput.equalsIgnoreCase(answer);

            if (isCorrect) {
                q.increaseCorrectCount();
                correct++;
                JOptionPane.showMessageDialog(this, "정답입니다!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "오답입니다.\n정답: " + answer);
            }

            // 문제별 기록 저장
            result.items.add(new QuizItem(
                    question,
                    answer,
                    userInput,
                    isCorrect
            ));
        }

        // 최종 기록 저장
        result.correct = correct;
        result.wrong   = 10 - correct;
        lastResult = result;

        JOptionPane.showMessageDialog(this,
                "퀴즈 종료\n정답: " + correct + "/10");
    }
    private void showQuizResult() {

        if (lastResult == null) {
            JOptionPane.showMessageDialog(this, "최근 퀴즈 기록이 없습니다.");
            return;
        }

        JDialog dialog = new JDialog(this, "최근 퀴즈 결과", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JLabel info = new JLabel(
                "<html>" +
                        "난이도: " + lastResult.difficulty + "<br>" +
                        "맞은 개수: " + lastResult.correct + "<br>" +
                        "틀린 개수: " + lastResult.wrong +
                        "</html>"
        );
        info.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        info.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(info, BorderLayout.NORTH);

        // 문제별 리스트
        DefaultListModel<String> model = new DefaultListModel<>();
        for (QuizItem item : lastResult.items) {
            String status = item.isCorrect ? "O" : "X";
            model.addElement(status + " : " + item.questionText);
        }

        JList<String> list = new JList<>(model);
        JScrollPane scroll = new JScrollPane(list);
        dialog.add(scroll, BorderLayout.CENTER);

        // 리스트 클릭하면 세부 오답 보기
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = list.getSelectedIndex();
                if (idx >= 0) {
                    QuizItem q = lastResult.items.get(idx);

                    JOptionPane.showMessageDialog(dialog,
                            "<html>" +
                                    "<b>문제:</b> " + q.questionText + "<br>" +
                                    "<b>정답:</b> " + q.correctAnswer + "<br>" +
                                    "<b>내 답:</b> " + q.userAnswer + "<br>" +
                                    "<b>결과:</b> " + (q.isCorrect ? "정답" : "오답") +
                                    "</html>"
                    );
                }
            }
        });

        JButton btnClose = new JButton("닫기");
        btnClose.addActionListener(e -> dialog.dispose());

        JPanel bottom = new JPanel();
        bottom.add(btnClose);

        dialog.add(bottom, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }


    private void exitProgram() {
        int result = JOptionPane.showConfirmDialog(this,
                "단어장을 저장하고 프로그램을 종료하시겠습니까?",
                "저장 후 종료",
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            manager.saveToFile();
            // 프레임을 닫고 응용 프로그램을 종료합니다.
            dispose();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VocaFrame frame = new VocaFrame();
            frame.setVisible(true);
        });
    }

    // 퀴즈 전용 커스텀 입력 다이얼로그
    private String showQuizDialog(String questionText) {
        // \n을 HTML <br>로 변환해서 JLabel에 표시
        String htmlText = "<html>" + questionText.replace("\n", "<br>") + "</html>";

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(htmlText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setHorizontalAlignment(SwingConstants.LEFT);

        JTextField inputField = new JTextField(15);
        inputField.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(10));
        panel.add(inputField);

        int result = JOptionPane.showConfirmDialog(
                this,                  // 부모 컴포넌트 (VocaFrame)
                panel,                 // 메시지(커스텀 패널)
                "단어 퀴즈",            // 타이틀
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE // 아이콘 제거
        );

        if (result == JOptionPane.OK_OPTION) {
            return inputField.getText().trim();
        } else {
            // 취소 또는 X 버튼
            return null;
        }
    }
    private String showTimedInputDialog(String questionText, int timeLimit) {

        JDialog dialog = new JDialog(this, "단어 퀴즈", true);
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // ▼ 원형 타이머 패널 생성
        CircularTimerPanel circlePanel = new CircularTimerPanel(timeLimit);

        JLabel timerLabel = new JLabel("남은 시간: " + timeLimit + "초");
        timerLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        topPanel.add(timerLabel);
        topPanel.add(circlePanel);

        dialog.add(topPanel, BorderLayout.NORTH);

        // ▼ 이하 기존 문제 출력 + 입력창 구성은 그대로
        JLabel label = new JLabel("<html>" + questionText.replace("\n", "<br>") + "</html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT); // ★ 반드시 추가!


        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(250, 28));
        textField.setMaximumSize(new Dimension(250, 28));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(label);
        centerPanel.add(Box.createVerticalStrut(10));

        JPanel tfPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tfPanel.add(textField);
        centerPanel.add(tfPanel);

        dialog.add(centerPanel, BorderLayout.CENTER);

        JButton okButton = new JButton("확인");
        dialog.add(okButton, BorderLayout.SOUTH);

        final String[] result = {null};
        final int[] time = {timeLimit};

        // ▼ 타이머 변경
        Timer timer = new Timer(1000, e -> {
            time[0]--;
            timerLabel.setText("남은 시간: " + time[0] + "초");

            circlePanel.setRemaining(time[0]);     // ← 부채꼴 갱신 포인트

            if (time[0] <= 0) {
                ((Timer)e.getSource()).stop();
                result[0] = "";  // 시간초과 → 자동 오답
                dialog.dispose();
            }
        });

        timer.start();

        // 엔터로 제출
        textField.addActionListener(e -> {
            result[0] = textField.getText().trim();
            timer.stop();
            dialog.dispose();
        });

        okButton.addActionListener(e -> {
            result[0] = textField.getText().trim();
            timer.stop();
            dialog.dispose();
        });

        dialog.setVisible(true);
        timer.stop();

        return result[0];
    }


    class CircularTimerPanel extends JPanel {

        private int remaining;
        private int total;

        public CircularTimerPanel(int totalTime) {
            this.remaining = totalTime;
            this.total = totalTime;
            setPreferredSize(new Dimension(60, 60));
            setOpaque(false);
        }

        public void setRemaining(int value) {
            this.remaining = value;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 5;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            // 바탕 원 (연한 회색)
            g2.setColor(new Color(230, 230, 230));
            g2.fillOval(x, y, size, size);

            // 부채꼴(남은 시간 비율)
            double ratio = (double) remaining / total;
            int angle = (int) (360 * ratio);

            g2.setColor(new Color(90, 70, 140)); // 보라색 계열
            g2.fillArc(x, y, size, size, 90, -angle);

            // 가운데 구멍 (도넛 모양)
            g2.setColor(getBackground());
            g2.fillOval(x + size/4, y + size/4, size/2, size/2);
        }
    }
    // 가중치 기반 단어 선택 메서드
    private Word weightedRandomPick(List<Word> pool) {

        // 전체 가중치 합산
        double totalWeight = 0;
        for (Word w : pool) {
            double rate = (w.getQuizCount() == 0) ? 0.0
                    : (double) w.getCorrectCount() / w.getQuizCount();
            double weight = 1.0 - rate;   // 가중치 = 1 - 정답률
            totalWeight += weight;
        }

        // 랜덤 값 선택
        double r = Math.random() * totalWeight;

        // 누적 가중치 기반 선택
        double cumulative = 0;
        for (Word w : pool) {
            double rate = (w.getQuizCount() == 0) ? 0.0
                    : (double) w.getCorrectCount() / w.getQuizCount();
            double weight = 1.0 - rate;

            cumulative += weight;

            if (r <= cumulative)
                return w;
        }

        return pool.get(pool.size() - 1); //예외처리
    }

    class QuizResult {
        String difficulty;
        int correct;
        int wrong;

        List<QuizItem> items = new ArrayList<>();

        public QuizResult(String difficulty) {
            this.difficulty = difficulty;
        }
    }

    class QuizItem {
        String questionText;
        String correctAnswer;
        String userAnswer;
        boolean isCorrect;

        public QuizItem(String q, String a, String u, boolean ok) {
            this.questionText = q;
            this.correctAnswer = a;
            this.userAnswer = u;
            this.isCorrect = ok;
        }
    }

}
