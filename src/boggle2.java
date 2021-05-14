import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;


public class boggle2 {
    static Random rand = new Random();
    static private int roundspassed = 0; // these global variables are set because we need them due to actionlisteners that cannot sync
    static private int numRounds = 0;
    static private int timersetting = 0;
    static private int turn = 1;
    static private int p1score = 0;
    static private int p2score = 0;
    static private int p1t = 0;
    static private int p2t = 0;
    static private boolean runp1t = true;
    static private boolean runp2t = true;
    static private int difficulty = -1;
    static private JFrame f = new JFrame("Boggle Game"); // initialize these two so they can be used in methods and everywhere
    static private JPanel panel = new JPanel();
    static private TimerTask timer1;
    static private TimerTask timer2;
    static private Timer timer = new Timer();

    static boolean existOnBoard(char[][] board, String word, boolean almostpossible) { // call this method to check if a word exists
        if (board == null || board.length == 0 || word == null || word.isEmpty() || (word.length() == 1)) { // makes sure that the word is valid
            return false; // if any one of these conditions are true, then return false, telling that the word is invalid
        }
        boolean[][] visited = new boolean[5][5]; // we will make a visited array because we want to make sure that the
                                                 // algorithm will not keep looping around the letters that it's been to
        for (int i = 0; i < 5; i++) {            // this for loop runs through each letter first, and it will check if
            for (int j = 0; j < 5; j++) {        // the first letter matches with the current letter it is looping through. see line 20
                resetVisited(visited);
                //System.out.print(board[i][j] + " ");
                if (board[i][j] == word.charAt(0)) { // if the first letter matches the current letter in the loop,
                    if (DFS(board, word, i, j, 1, visited, almostpossible)) { // recursively call the second letter (index 1), and return true
                        return true;                          // if the recursion follows through for the entire word
                    }
                }
            }
        }
        return false; // return false if it has iterated through the board and cannot find a singular match,
                      // or if it has iterated through and determined that words cannot be found
    }

    static void resetVisited(boolean[][] visited) { // set the entire visited array to false. this is needed because every single
        for (int l = 0; l < visited.length; l++) {  // DFS search should be independent of each other
            Arrays.fill(visited[l], false);
        }
    }

    static boolean DFS(char[][] board, String word, int row, int col, int index, boolean[][] visited, boolean almostpossible) { // main DFS method. boolean almostpossible will be explained in line 926
        int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};
        // these are all possible directions of a matrix. for example, if the direction is dx (x displacement vector) 0 1,
        // and dy (y displacement vector) of 0, that means it will move one to the right, and none down.  this accounts for all
        // 8 directions that it can search through. every letter it iterates through, whether it be the first or the last letter
        // will be searched in all 8 directions before coming to a conclusion if it is possible or not.

        visited[row][col] = true; // mark the current letter being searched as true because we will not want to go back to it in the future
        if ((almostpossible) && (index >= (word.length()-1))) {
            return true;
        } else if ((!almostpossible) && (index >= word.length())) { // if the index is equal or greater (which will not happen) to the word length, that means it has reached
            return true;              // the end of the word, and this means there is nothing else to search for.
        }

        for (int z = 0; z < 8; z++) { // this for loop is out of 8 because of the 8 directions we were talking about earlier.
            if (check(board, row + dx[z], col + dy[z], visited)) {
                // check function, checks if the current letter we are on is will exist one we add the direction vectors onto the
                // letter to search. we need this because arrays start with an index of 0, and it is possible to be on, for example
                // the letter with the position of [0,1] and adding direction vectors of [-1,-1]. this will result in the position
                // to be [-1,0], and the x position is invalid, hence the negative index. this function does not check if the letter
                // the same as the position, that is on the next line.
                if (word.charAt(index) == board[row + dx[z]][col + dy[z]]) {
                    // if the letter at the current index is the same as the added vectors, then;
                    // recursively call the function for the next index, and put the new position vectors
                    if (DFS(board, word, row + dx[z], col + dy[z], index + 1, visited, almostpossible)) {
                        return true;
                        // this true statement is for the recursion and for backtracking. if one is false, all others will return false
                        // and therefore affecting the main existOnBoard method.
                    }
                }
            }
        }

        return false;

    }

    static boolean check(char[][] board, int i, int j, boolean[][] visited) {
        return (i >= 0 && i < 5 && j >= 0 && j < 5 && !visited[i][j]);
        // check if the new vectors will be still valid
    }

    static void printBoard(char[][] board) { // print board
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    static void randomizeBoard(String[][] dice, char[][] board, JLabel[] letters) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                char gen = dice[i][j].charAt(rand.nextInt(6));
                board[i][j] = gen;
            }
        }
    }

    static void getPossible(ArrayList<String> possible, Set<String> list, char[][] board) {
        possible.clear();
        for (String word : list) {
            if(existOnBoard(board, word.toUpperCase(),false)) {
                possible.add(word);
            }
        }

        Collections.sort(possible, Comparator.comparing(String::length));
        Collections.reverse(possible);
    }

    static void getAlmostPossible(ArrayList<String> almostpossible, Set<String> list, char[][] board, ArrayList<String> possible) {
        almostpossible.clear();
        for (String word : list) {
            if(existOnBoard(board, word.toUpperCase(),true)) {
                almostpossible.add(word);
            }
        }
        for (String possibleword : possible) {

            if (almostpossible.contains(possibleword)) {
                almostpossible.remove(possibleword);
            }
        }
        Collections.sort(almostpossible, Comparator.comparing(String::length));
        Collections.reverse(almostpossible);
    }

    static void displayMainMenu(JLabel title, JButton singleplayer, JButton multiplayer, JButton computer) {
        title.setBounds(200,20,120,40);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        singleplayer.setBounds(150,150,200,30);
        multiplayer.setBounds(150,200,200,30);
        computer.setBounds(150,250,200,30);
        f.add(singleplayer);
        f.add(multiplayer);
        f.add(computer);
        f.add(title);
    }

    static void removeMainMenu(JLabel title, JButton singleplayer, JButton multiplayer, JButton computer) {
        f.remove(title);
        f.remove(singleplayer);
        f.remove(multiplayer);
        f.remove(computer);
        f.pack();
        f.setSize(500,500);
    }

    static void displayReturnMainMenu(JButton back) {
        back.setBounds(10,425,150,30);
        f.add(back);
        panel.revalidate(); // revalidate since the old labels may mess something up
        panel.repaint();

    }

    static void displayLabels(JLabel[] letters, char[][] board, int offset) {
        for (int i = 0; i < 5; i++) { // this places them out into a grid style
            for (int j = 0; j < 5; j++) {
                letters[i*5+j] = new JLabel(String.valueOf(board[j][i]));
                letters[i*5+j].setBounds((offset + i * 45), (100 + j * 45), 30, 30); // depending on what i and j are, it spaces them out accordingly
                letters[i*5+j].setFont(new Font("Arial", Font.BOLD, 30));
                f.add(letters[i*5+j]); // add the label to the window
            }
        }
    }

    static void removeLabels(JLabel[] letters) {
        for (int i = 0; i < 25; i++) {
            f.remove(letters[i]);
            f.revalidate(); // revalidate since the old labels may mess something up
            f.repaint();
        }
    }



    public static void main(String[] args) {
        BufferedReader reader;
        Set<String> list = new HashSet<>();

        try {
            reader = new BufferedReader(new FileReader("wordlist.txt"));
            BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
            String line = reader.readLine();
            while (line != null) { // keep adding words to array until it reaches the end
                list.add(line); // add to the hashset
                line = reader.readLine(); // read the next line and go back to the top
            }


            JLabel[] letters = new JLabel[25]; // array of labels that will be displalying the board. 25 for 5*5 letters

            String[][] dice = {{"AAAFRS", "AAEEEE", "AAFIRS", "ADENNN", "AEEEEM"}, // dice for entire
                    {"AEEGMU", "AEGMNN", "AFIRSY", "BJKQXZ", "CCNSTW"},
                    {"CEIILT", "CEILPT", "CEIPST", "DDLNOR", "DHHLOR"},
                    {"DHHNOT", "DHLNOR", "EIIITT", "EMOTTT", "ENSSSU"},
                    {"FIPRSY", "GORRVW", "HIPRRY", "NOOTUW", "OOOTTU"}};

            char[][] board = new char[5][5]; // board 2d array that will be used for all modes

            boolean visited[][] = new boolean[5][5];
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    visited[i][j] = false;
                }
            }


            ArrayList<String> possible = new ArrayList<String>();
            ArrayList<String> almostpossible = new ArrayList<String>();
            ArrayList<String> guessed = new ArrayList<String>();

            // every single jpanel object
            JLabel title = new JLabel("Boggle"); // main menu title
            JButton singleplayer = new JButton("Singleplayer"); // button for singleplayer main menu
            JButton multiplayer = new JButton("Multiplayer"); // button for multiplayer main menu
            JButton computer = new JButton("Play against Computer"); // button for vs ai main menu
            JButton back = new JButton("Back to Main Menu"); // universal back to main menu button

            //singleplayer
            JTextField tf = new JTextField(); // textfield for singleplayer only
            JLabel inputlabel = new JLabel("Input"); // label that says input
            JLabel recentlyguessedlabel = new JLabel("Recently guessed words"); // label that says recently guessed words
            JLabel recentlyguessed = new JLabel(); // list of recently guessed words in singleplayer only. not in multiplayer or ai to keep it competetive
            JLabel guessedpossible = new JLabel(); // the ratio of guessed words that have been guessed on the top center
            JLabel singleplayeroutput = new JLabel(); // tells if the word is accepted, invalid, already inputted
            //multiplayer settings
            JButton confirmsettings = new JButton("Start"); // start button after inputting the settings
            JTextField rounds = new JTextField(); // textfield that lets you input number of rounds you want
            JLabel roundslabel = new JLabel("Number of Rounds"); // label above the rounds textbox
            JLabel timerlabel = new JLabel("Seconds per round"); // label for seconds per round
            JLabel disabletimerlabel = new JLabel("Disable Timer"); // label for the checkbox to disable timer
            JCheckBox disabletimer = new JCheckBox(); // checkbox to disable timer
            JLabel eliminationlabel = new JLabel("Enable Elimination"); // label for the checkbox to enable elimination
            JCheckBox elimination = new JCheckBox(); // checkbox to enable elimination
            JTextField timerinput = new JTextField(); // textfield to add the number of seconds for each round
            JLabel multiplayererror = new JLabel("Your input is invalid, please try again!"); //
            // multiplayer
            JLabel currentrounds = new JLabel(); // number of rounds passed
            JTextField p1 = new JTextField(); // textfield for player 1 to enter words
            JTextField p2 = new JTextField(); // textfield for player 2 to enter words
            JLabel p1label = new JLabel("Player 1"); // label above textfield
            JLabel p2label = new JLabel("Player 2");
            JLabel multiplayermsg = new JLabel(); // messages that will say if a player has entered a word correctly or not
            JLabel multiplayerend = new JLabel(); // messages that will announce the ending of the game
            multiplayermsg.setBounds(100,320,300,30);
            multiplayermsg.setHorizontalAlignment(SwingConstants.CENTER);
            multiplayerend.setBounds(0,360,500,30);
            multiplayerend.setHorizontalAlignment(SwingConstants.CENTER);
            JLabel p1scorecounter = new JLabel("0"); // score counter, above the textfields
            JLabel p2scorecounter = new JLabel("0");
            JLabel p1scorelabel = new JLabel("Score"); // score label above the score counter
            JLabel p2scorelabel = new JLabel("Score");
            JLabel p1time = new JLabel(); // timer under the textfields
            JLabel p2time = new JLabel();
            // play against computer
            JLabel difficultylabel = new JLabel("Difficulty"); // big difficulty word label
            JLabel difficultyselected = new JLabel("Difficulty selected:"); // label above the difficulty to tell user what they have selected
            difficultyselected.setVisible(false);
            JButton confirmcomputer = new JButton("Start"); // start button to initiate vs computer game
            JButton easy = new JButton("Easy"); // easy button in difficulty selection
            JButton medium = new JButton("Medium"); // medium button in difficulty selection
            JButton hard = new JButton("Hard"); // hard button in difficulty selection
            JButton impossible = new JButton("Impossible"); // impossible button in difficulty selection
            JLabel difficultytitle = new JLabel(); // label with the difficulty in the different colour
            JLabel difficultydesc = new JLabel(); // label that describes the extent of the difficulty
            JLabel computersettingmsg = new JLabel(); // label that will remind user if no difficulty is selected
            JTextField computerinput = new JTextField(); // textfield that will let user input words
            JLabel playerscore = new JLabel(); // score of player
            JLabel playerscorelabel = new JLabel("Your score"); // label above score of player
            JLabel computerscore = new JLabel(); // computer's score
            JLabel computerscorelabel = new JLabel("Computer score"); // label above computer's score
            JLabel computeroutput = new JLabel(); // messages that relate to user's input of words
            JLabel computerplayeroutput = new JLabel(); // messages that relate to the computer's inputs

            f.setLayout(null);
            panel.setLayout(null);
            f.setResizable(false);

            displayMainMenu(title,singleplayer,multiplayer,computer); // initialize the main menu once the game is opened up

            back.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    try {
                        removeLabels(letters);
                    } catch (Exception eedsaf) {}
                    displayMainMenu(title,singleplayer,multiplayer,computer);

                    // removes every single jpanel object. the reason why i did this is because i wanted
                    // a universal method that could remove all, and not make different ones. this is
                    // a lot more simpler anyways.
                    guessed.clear();
                    f.remove(guessedpossible);
                    recentlyguessed.setText("");
                    f.remove(recentlyguessed);
                    f.remove(recentlyguessedlabel);
                    f.remove(singleplayeroutput);
                    f.remove(inputlabel);
                    f.remove(timerlabel);
                    f.remove(timerinput);
                    f.remove(tf);
                    f.remove(back);
                    f.remove(confirmsettings);
                    f.remove(rounds);
                    f.remove(roundslabel);
                    f.remove(disabletimer);
                    f.remove(disabletimerlabel);
                    f.remove(eliminationlabel);
                    f.remove(elimination);
                    f.remove(currentrounds);
                    f.remove(multiplayererror);
                    f.remove(p1);
                    f.remove(p2);
                    f.remove(p1label);
                    f.remove(p2label);
                    f.remove(multiplayermsg);
                    f.remove(multiplayerend);
                    f.remove(p1scorecounter);
                    f.remove(p2scorecounter);
                    f.remove(p1scorelabel);
                    f.remove(p2scorelabel);
                    f.remove(p1time);
                    f.remove(p2time);
                    f.remove(confirmcomputer);
                    f.remove(easy);
                    f.remove(medium);
                    f.remove(hard);
                    f.remove(impossible);
                    f.remove(difficultytitle);
                    f.remove(difficultydesc);
                    f.remove(computerinput);
                    f.remove(computersettingmsg);
                    f.remove(playerscore);
                    f.remove(playerscorelabel);
                    f.remove(computerscore);
                    f.remove(computerscorelabel);
                    f.remove(computerplayeroutput);
                    f.remove(computeroutput);
                    f.remove(difficultylabel);
                    f.remove(difficultyselected);

                    singleplayeroutput.setText("");
                    multiplayerend.setText("");
                    computeroutput.setText("");
                    computerplayeroutput.setText("");
                    f.pack();
                    f.revalidate();
                    f.repaint();
                    f.setSize(500, 500);

                    try { // disable the timer for multiplayer, if there is one.
                        if (!disabletimer.isSelected()) {
                            timer1.cancel();
                            timer2.cancel();
                        }
                    } catch (Exception ahusdf) {}

                }
            });
            singleplayer.addActionListener(new ActionListener(){ // the singleplayer actionlistener activates the singleplayer mode
                public void actionPerformed(ActionEvent e) { // all singleplayer functions are contained within this one
                    randomizeBoard(dice, board, letters); // randomize the board first,
                    getPossible(possible,list,board); // get the number of possible words,
                    removeMainMenu(title, singleplayer, multiplayer, computer); // remove the main menu to make way for the board and singleplayer GUI,
                    displayLabels(letters, board, 200); // display the board onto the screen,
                    displayReturnMainMenu(back); // display the back button so user can go back to the main screen

                    guessedpossible.setBounds(200,10,100,40); // this is the label that says how many words the user has guessed so far, and how many are still out there
                    guessedpossible.setText(guessed.size() + "/" + possible.size()); // this fraction will update after every word inputted. number of correct words /  number of possible words
                    guessedpossible.setFont(new Font("Arial", Font.BOLD, 20));

                    inputlabel.setBounds(60,90,100,20); // label that just says input, tells user where input box is
                    recentlyguessedlabel.setBounds(13,150,125,20);  // label for that says recently guessed words
                    recentlyguessed.setBounds(13,170,125,240); // this label is a dynamic label that shows the recently guessed words that the user has inputted and has been accepted.
                                                               // only in singleplayer because the other modes are competetive and should involve memory
                    recentlyguessedlabel.setFont(new Font("Arial", Font.BOLD, 10));
                    singleplayeroutput.setBounds(150,350,300,30); // this notifies the player on the outcome of the word they have entered
                    recentlyguessed.setHorizontalAlignment(SwingConstants.CENTER); // centering
                    recentlyguessed.setHorizontalAlignment(JLabel.LEFT);
                    recentlyguessed.setVerticalAlignment(JLabel.TOP);

                    tf.setBounds(10,110,125,20); // textbox
                    f.add(inputlabel);
                    f.add(recentlyguessedlabel);
                    f.add(recentlyguessed, BorderLayout.NORTH);
                    f.add(tf);
                    f.add(guessedpossible);
                    f.add(singleplayeroutput);

                    tf.addActionListener(new ActionListener() { // this entire actionlistener will check when the enter key is pressed to submit a word
                        public void actionPerformed(ActionEvent e) { // these commands will trigger when the user enters a word

                            String input = tf.getText(); // get the text and store it as String input
                            if (existOnBoard(board, input.toUpperCase(),false) && !guessed.contains(input.toUpperCase()) && possible.contains(input.toLowerCase())) { // if the word exists on the board,
                                guessed.add(input.toUpperCase()); // add this to the accepted words
                                String recentlyguessedstring = "<html>"; // i must use html formatting here for the line breaks. the list of recently guessed words is a collection of line breaks and words
                                guessedpossible.setText(guessed.size() + "/" + possible.size()); // update the fraction that was set out in the beginning
                                for (int i = guessed.size() - 1; i >= 0; i--) { // iterate through the list backwards. the first guessed word will always be the first word guessed, and the most recent will always be the last in the ArrayList
                                    recentlyguessedstring += guessed.get(i) + "<br/>"; // add html formatting to the words, and ship it
                                }
                                recentlyguessedstring += "</html>";
                                recentlyguessed.setText(recentlyguessedstring); // set the text
                                singleplayeroutput.setText(input.toLowerCase() + " is an valid word, and has been accepted."); //tell the player that the word is a valid word
                            } else if (existOnBoard(board, input.toUpperCase(),false)) { // if the word has already been entered,
                                singleplayeroutput.setText(input.toLowerCase() + " has already been entered."); // tell them the word has already been entered
                            } else { // otherwise,
                                singleplayeroutput.setText(input.toLowerCase() + " is an invalid word."); // word is invalid
                            }

                            tf.setText(""); // clear the textbox, as the textbox does not clear by default after pressing enter


                        }
                    });
                }
            });
            multiplayer.addActionListener(new ActionListener(){ // this is the Multiplayer button in the main menu
                public void actionPerformed(ActionEvent e){
                    removeMainMenu(title, singleplayer, multiplayer, computer); // remove the main menu to make way for the buttons
                    displayReturnMainMenu(back); // display the back button
                    roundslabel.setBounds(150,70,200,30); // label that says "number of rounds"
                    roundslabel.setHorizontalAlignment(SwingConstants.CENTER);
                    rounds.setBounds(225,100,50,30); // textfield for number of rounds
                    rounds.setHorizontalAlignment(SwingConstants.CENTER);
                    timerlabel.setBounds(150,130,200,30); // label that says "seconds per round"
                    timerlabel.setHorizontalAlignment(SwingConstants.CENTER);
                    timerinput.setBounds(225,160,50,30); // textfield for seconds per round
                    timerinput.setHorizontalAlignment(SwingConstants.CENTER);
                    disabletimerlabel.setBounds(150,190,200,30); // label for "disable timer"
                    disabletimerlabel.setHorizontalAlignment(SwingConstants.CENTER);
                    disabletimer.setBounds(150,220,200,30); // checkbox for disabling timer
                    disabletimer.setHorizontalAlignment(SwingConstants.CENTER);
                    eliminationlabel.setBounds(150,250,200,30); // label for "enable elimination"
                    eliminationlabel.setHorizontalAlignment(SwingConstants.CENTER);
                    elimination.setBounds(150,280,200,30); // checkbox for enable elimination
                    elimination.setHorizontalAlignment(SwingConstants.CENTER);
                    confirmsettings.setBounds(150,370,200,30); // button at bottom that says "start"
                    confirmsettings.setHorizontalAlignment(SwingConstants.CENTER);

                    f.add(timerlabel);
                    f.add(timerinput);
                    f.add(confirmsettings);
                    f.add(roundslabel);
                    f.add(rounds);
                    f.add(disabletimer);
                    f.add(disabletimerlabel);
                    f.add(eliminationlabel);
                    f.add(elimination);

                }
            });

            disabletimer.addItemListener(new ItemListener() { // disable timer checkbox eventlistener
                public void itemStateChanged(ItemEvent e) {
                    if (disabletimer.isSelected()) { // if the checkbox is ticked,
                        timerlabel.setForeground(new Color(169, 169, 169)); // set the text to gray
                        timerinput.setEditable(false); // do not let them to edit the textbox
                    } else { // if the checkbox is unticked,
                        timerlabel.setForeground(new Color(0, 0, 0)); // set the text to black
                        timerinput.setEditable(true); //  let the edit the textbox
                    }
                }
            });

            confirmsettings.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        numRounds = Integer.parseInt(rounds.getText()); // try to convert the input to an integer
                        if (!disabletimer.isSelected()) { // if disabletimer is not ticked,
                            timersetting = Integer.parseInt(timerinput.getText()); // try to convert the input to an integer
                        } else {
                            timersetting = 1; // else, set the timersetting to 1.
                        }

                        if (numRounds > 0 && timersetting > 0) {
                            randomizeBoard(dice, board, letters); // randomize the board
                            getPossible(possible, list, board); // get the possible words
                            displayLabels(letters, board, 140); // place the board with labels
                            f.remove(confirmsettings); // remove the menu that asks user for the inputted settings
                            f.remove(roundslabel);
                            f.remove(rounds);
                            f.remove(multiplayererror);
                            f.remove(timerinput);
                            f.remove(timerlabel);
                            f.remove(disabletimer);
                            f.remove(disabletimerlabel);
                            f.remove(eliminationlabel);
                            f.remove(elimination);
                            f.pack();
                            f.revalidate();
                            f.repaint();
                            f.setSize(500, 500);
                            currentrounds.setText("Round 1/" + numRounds); // set the current round to 1
                            currentrounds.setFont(new Font("Arial", Font.BOLD, 20));
                            currentrounds.setBounds(190, 30, 150, 30);
                            f.add(currentrounds);
                            p1label.setBounds(30, 110, 110, 20); // set the bounds of the label that says "Player 1"
                            p2label.setBounds(390, 110, 110, 20);// set the bounds of the label that says "Player 2"
                            p1.setBounds(10, 150, 110, 20); // set the bounds of the textbox of Player 1
                            p2.setBounds(360, 150, 110, 20); // set the bounds of the textbox of Player 2
                            p1scorecounter.setText("0"); // set the score of player 1 to 0
                            p1scorecounter.setBounds(40, 70, 100, 40);
                            p1scorecounter.setFont(new Font("Arial", Font.BOLD, 30));
                            p2scorecounter.setText("0"); // set the score of player 2 to 0
                            p2scorecounter.setBounds(400, 70, 100, 40);
                            p2scorecounter.setFont(new Font("Arial", Font.BOLD, 30));
                            p1scorelabel.setBounds(35, 45, 110, 20);
                            p2scorelabel.setBounds(395, 45, 110, 20);
                            p2label.setForeground(new Color(169, 169, 169));          // set these three labels to gray, because it is not player 2's turn
                            p2scorecounter.setForeground(new Color(169, 169, 169));   // makes it more obvious to the players
                            p2scorelabel.setForeground(new Color(169, 169, 169));
                            p1label.setForeground(new Color(0, 0, 0));                // set these three labels to black, because it is player 1's turn
                            p1scorecounter.setForeground(new Color(0, 0, 0));         // makes it more obvious to the players that it is player 1's turn
                            p1scorelabel.setForeground(new Color(0, 0, 0));
                            multiplayermsg.setText(""); // clear any mesages from the last games
                            p1time.setBounds(35, 250, 110, 30);
                            p1time.setFont(new Font("Arial", Font.BOLD, 30));
                            p2time.setBounds(395, 250, 110, 30);
                            p2time.setFont(new Font("Arial", Font.BOLD, 30));
                            p1time.setText(String.valueOf(timersetting)); // set the current timer to the number of seconds per round for both players
                            p2time.setText(String.valueOf(timersetting)); // however, only one of them will actually activate immediately, player 1 because it is their turn

                            f.add(p1scorecounter);
                            f.add(p2scorecounter);
                            f.add(p1);
                            f.add(p2);
                            f.add(p1scorelabel);
                            f.add(p2scorelabel);
                            p1.setEditable(true); // only let player 1 to input into the textbox
                            p2.setEditable(false);
                            roundspassed = 1; // set current round to 1. this variable is for label at the top middle to show which round it is
                            f.add(p1label);
                            f.add(p2label);
                            f.add(multiplayermsg);
                            f.add(multiplayerend);
                            f.add(p1time);
                            f.add(p2time);
                            p1score = 0; // reset both scores
                            p2score = 0;
                            turn = 1; // player 1's turn, i do this so i can stop the textboxes from entering
                            // if a textfield is set to as .setEditable(false), and the game has ended, the cursor can still be on it and the players can spam enter and break the game if they wanted to

                            // p1t and p2t are variables that denote the current time left on each player's clock
                            // they will be affected by the timer, which will subtract by 1 every second until it reaches 0
                            p1t = timersetting + 1;
                            p2t = timersetting + 1;
                            runp1t = true; // another failsafe for the timer. if this boolean is true, then the timer will run
                            runp2t = false;
                            p1time.setVisible(true); // only set the player 1's time to be visible
                            p2time.setVisible(false);

                            if (!disabletimer.isSelected()) { // if the disable timer option was not selected,
                                timer1 = new TimerTask() { // new timertask for player 1.
                                    public void run() {
                                        if (runp1t) { // failsafe. this will run whenever it is set to true
                                            p1t--; // subtract 1 from the current timer
                                            p1time.setText(String.valueOf(p1t)); // update the timer for the label
                                            if (p1t == 0) { // if the timer is zero
                                                runp1t = false; // stop both timers from running
                                                runp2t = false;
                                                if (!disabletimer.isSelected()) { // cancel the timers
                                                    timer1.cancel();
                                                    timer2.cancel();
                                                }
                                                p1label.setForeground(new Color(169, 169, 169)); // set player 1 (losing player) colour to gray
                                                p1scorecounter.setForeground(new Color(169, 169, 169));
                                                p1scorelabel.setForeground(new Color(169, 169, 169));
                                                p2scorelabel.setForeground(new Color(0, 0, 0)); // set player 2's to green and black
                                                p2scorecounter.setForeground(new Color(34,139,34));
                                                p2label.setForeground(new Color(0, 0, 0));
                                                p1.setEditable(false); // stop players from editing the textbox
                                                p2.setEditable(false);
                                                turn = 0; // turn = 0 makes it so that it's neither player 1's or player 2's turn
                                                multiplayerend.setText("Player 1 ran out of time, therefore Player 2 has won the game!");


                                            }
                                        }
                                    }
                                };
                                timer.schedule(timer1, 0, 1000); // schedule the timer to run every second (1000 ms)

                                // timer 2 is the same as timer 1, but instead timer 2 is for player 2. everything is the same except the 1 and the 2 in the variables are switched
                                timer2 = new TimerTask() {
                                    public void run() {
                                        if (runp2t) {
                                            p2t--;
                                            p2time.setText(String.valueOf(p2t));
                                            if (p2t == 0) {
                                                runp1t = false;
                                                runp2t = false;



                                                if (!disabletimer.isSelected()) {
                                                    timer1.cancel();
                                                    timer2.cancel();
                                                }
                                                p1label.setForeground(new Color(169, 169, 169));
                                                p1scorecounter.setForeground(new Color(169, 169, 169));
                                                p1scorelabel.setForeground(new Color(169, 169, 169));
                                                p1scorelabel.setForeground(new Color(0, 0, 0));
                                                p1scorecounter.setForeground(new Color(34,139,34));
                                                p1label.setForeground(new Color(0, 0, 0));
                                                p1.setEditable(false);
                                                p2.setEditable(false);
                                                turn = 0;
                                                multiplayerend.setText("Player 2 ran out of time, therefore Player 1 has won the game!");

                                            }
                                        }
                                    }
                                };
                                timer.schedule(timer2, 0, 1000);
                            } else { // if the timer is disabled,
                                f.remove(p1time); // remove the timer labels from the screen
                                f.remove(p2time);
                            }
                        } else { // this is back to the confirming settings, in line 490
                                 // if the user has entered a negative number or 0, deem the input as invalid
                            Color color = new Color(255,0,0); // red colour
                            multiplayererror.setForeground(color); // set "invalid input" text colour to red
                            multiplayererror.setBounds(140,340,300,30);
                            f.add(multiplayererror);
                            rounds.setText(""); // clear the textboxes of invalid input
                            timerinput.setText("");
                            f.pack();
                            f.setSize(500,500);
                        }

                    } catch (Exception aaa){ // if there is a casting error with the integers,
                        Color color = new Color(255,0,0); // red colour
                        multiplayererror.setForeground(color);
                        multiplayererror.setBounds(140,340,300,30);
                        f.add(multiplayererror);
                        rounds.setText("");
                        timerinput.setText("");
                        f.pack();
                        f.setSize(500,500);

                    }

                }
            });

            p1.addActionListener(new ActionListener(){ // p1 is the player 1 textbox input
                public void actionPerformed(ActionEvent e){
                    if (turn == 1) { // if it is player 1's turn,
                        p2time.setVisible(true); // set player 2's time visible, as their clock will start ticking
                        p1time.setVisible(false); // set player 1's time invisible since their time is stopped
                        runp2t = true; // set the failsafe to true
                        runp1t = false;
                        p1time.setText(String.valueOf(timersetting)); // update both timers with the correct starting time per round
                        p2time.setText(String.valueOf(timersetting));
                        p1t = timersetting + 1; // add 1 to the time because the first second is quite quick compared to others
                        p2t = timersetting + 1;
                        String input = p1.getText(); // get the text for the input
                        p1.setEditable(false); // make player 1's textbox uneditable, and make player 2's editable since it is their turn
                        p2.setEditable(true);
                        p1label.setForeground(new Color(169, 169, 169)); // set player 1's labels to gray, as it is not their turn
                        p1scorecounter.setForeground(new Color(169, 169, 169));
                        p1scorelabel.setForeground(new Color(169, 169, 169));
                        p2label.setForeground(new Color(0, 0, 0)); // set player 2's labels to black, so they can see that it is their turn
                        p2scorecounter.setForeground(new Color(0, 0, 0));
                        p2scorelabel.setForeground(new Color(0, 0, 0));
                        p1.setText(""); // clear the text in the textbox
                        turn = 2; // set the other failsafe to 2, the turn
                        p2.requestFocus(); // switch the cursor from the player 1 textfield to the player 2 textfield
                        if (existOnBoard(board, input.toUpperCase(),false) && !guessed.contains(input.toUpperCase()) && possible.contains(input.toLowerCase())) { // if the word exists,
                            guessed.add(input.toUpperCase()); // add the word to the guessed list
                            multiplayermsg.setText(input.toLowerCase() + " is a valid word, and Player 1 earned " + input.length() + " points."); // notify the player that they have earned points
                            p1score += input.length(); // add the length of the word to the score, as that is the scoring system
                            p1scorecounter.setText(String.valueOf(p1score)); // update the score counter
                        } else { // else if the word is incorrect,
                            if (existOnBoard(board, input.toUpperCase(),false) && guessed.contains(input.toUpperCase())) { // if the word has already been entered,
                                multiplayermsg.setText(input.toLowerCase() + " has already been entered and accepted."); // notify the player
                            } else { // else if the word has not been guessed and is invalid
                                multiplayermsg.setText(input.toLowerCase() + " is not a valid word."); // notify the player
                            }
                            if (elimination.isSelected()) { // if the word is incorrect and elimination is activated,
                                p1label.setForeground(new Color(169, 169, 169)); // set all labels to gray, since the game is over
                                p1scorecounter.setForeground(new Color(169, 169, 169));
                                p1scorelabel.setForeground(new Color(169, 169, 169));
                                p1.setEditable(false); // do not let the players edit the textboxes, since the game is over
                                p2.setEditable(false);
                                multiplayerend.setText("Player 1 has entered an invalid word, therefore Player 2 has won the game!"); // notify that player 1 has lost the game
                                p2scorelabel.setForeground(new Color(0, 0, 0)); // set the score label of player 2 to black
                                p2scorecounter.setForeground(new Color(34,139,34)); // set the score to player 2 to green, as they have won
                                p2label.setForeground(new Color(0, 0, 0));
                                turn = 0; // make the turn to 0, failsafe on
                                if (!disabletimer.isSelected()) { // if the is on,
                                    timer1.cancel(); // cancel the timers, and remove the labels for them
                                    timer2.cancel();
                                    f.remove(p1time);
                                    f.remove(p2time);
                                }
                            }
                        }



                    }

                }
            });
            // p2 is the listener for the player 2 textbox
            // it is the exact same as player 1 textbox
            // everything is the same for the variables except p1 and p2 are switched
            // new code on line 794
            p2.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    if (turn == 2) {
                        p1time.setVisible(true);
                        p2time.setVisible(false);
                        runp2t = false;
                        runp1t = true;
                        String input = p2.getText();
                        p1.setEditable(true);
                        p2.setEditable(false);
                        p2label.setForeground(new Color(169, 169, 169));
                        p2scorecounter.setForeground(new Color(169, 169, 169));
                        p2scorelabel.setForeground(new Color(169, 169, 169));
                        p1label.setForeground(new Color(0, 0, 0));
                        p1scorecounter.setForeground(new Color(0, 0, 0));
                        p1scorelabel.setForeground(new Color(0, 0, 0));
                        p2.setText("");
                        turn = 1;
                        p1.requestFocus();
                        if (existOnBoard(board, input.toUpperCase(),false) && !guessed.contains(input.toUpperCase()) && possible.contains(input.toLowerCase())) {
                            guessed.add(input.toUpperCase());
                            multiplayermsg.setText(input.toLowerCase() + " is a valid word, and Player 2 earned " + input.length() + " points.");
                            p2score += input.length();
                            p2scorecounter.setText(String.valueOf(p2score));
                        } else {
                            if (existOnBoard(board, input.toUpperCase(),false) && guessed.contains(input.toUpperCase())) {
                                multiplayermsg.setText(input.toLowerCase() + " has already been entered and accepted.");
                            } else {
                                multiplayermsg.setText(input.toLowerCase() + " is not a valid word.");
                            }
                            if (elimination.isSelected()) {
                                p2label.setForeground(new Color(169, 169, 169));
                                p2scorecounter.setForeground(new Color(169, 169, 169));
                                p2scorelabel.setForeground(new Color(169, 169, 169));
                                p1label.setForeground(new Color(169, 169, 169));
                                p1scorecounter.setForeground(new Color(169, 169, 169));
                                p1scorelabel.setForeground(new Color(169, 169, 169));
                                p1.setEditable(false);
                                p2.setEditable(false);
                                multiplayerend.setText("Player 2 has entered an invalid word, therefore Player 1 has won the game!");
                                p1scorelabel.setForeground(new Color(0, 0, 0));
                                p1scorecounter.setForeground(new Color(34,139,34));
                                p1label.setForeground(new Color(0, 0, 0));
                                turn = 0;
                                if (!disabletimer.isSelected()) {
                                    timer1.cancel();
                                    timer2.cancel();
                                    f.remove(p1time);
                                    f.remove(p2time);

                                }
                            }
                        }
                        p1time.setText(String.valueOf(timersetting));
                        p2time.setText(String.valueOf(timersetting));
                        p1t = timersetting + 1;
                        p2t = timersetting + 1;


                        roundspassed++; // we will add one round to the counter since player 1 always goes first
                        // after player 2 has entered, we will check if this is the last round
                        if (roundspassed == (numRounds+1)) { // if this is the last round,
                            p1.setEditable(false); // set both the textboxes to uneditable
                            p2.setEditable(false);
                            turn = 0; // failsafe
                            p1label.setForeground(new Color(169, 169, 169)); // set the labels to all gray
                            p1scorecounter.setForeground(new Color(169, 169, 169));
                            p1scorelabel.setForeground(new Color(169, 169, 169));
                            p2label.setForeground(new Color(169, 169, 169));
                            p2scorecounter.setForeground(new Color(169, 169, 169));
                            p2scorelabel.setForeground(new Color(169, 169, 169));
                            runp1t = false; // another failsafe
                            runp2t = false;
                            p1time.setVisible(false); // set the timer labels to invisible
                            p2time.setVisible(false);
                            if (p1score > p2score) { // winning conditions, if player 1 has higher score than player 2
                                multiplayerend.setText("The rounds are over, and Player 1 has won!"); // notify the players
                                p1scorelabel.setForeground(new Color(0, 0, 0));
                                p1scorecounter.setForeground(new Color(34,139,34));
                                p1label.setForeground(new Color(0, 0, 0)); // set colours respectively to winning
                            } else if (p1score < p2score) { // vice versa
                                multiplayerend.setText("The rounds are over, and Player 2 has won!");
                                p2scorelabel.setForeground(new Color(0, 0, 0));
                                p2scorecounter.setForeground(new Color(34,139,34));
                                p2label.setForeground(new Color(0, 0, 0));
                            } else { // if the game is tied, meaning both have the same scores
                                multiplayerend.setText("The rounds are over, and the game is tied!");
                            }
                            if (!disabletimer.isSelected()) { // kill the timers if there are any
                                timer1.cancel();
                                timer2.cancel();
                            }

                        } else { // if there are still rounds to go, update the round labels. everything has been done to switch the turn back to player 1 already
                            currentrounds.setText("Round " + roundspassed + "/" + numRounds);

                        }

                    }

                }
            });

            computer.addActionListener(new ActionListener(){ // this is the actionlistener for the button in the main menu for "Play against Computer"
                public void actionPerformed(ActionEvent e){
                    difficultylabel.setBounds(0,30,500,40); // add the label that says "Difficulty" in the top middle
                    difficultylabel.setFont(new Font("Arial", Font.BOLD, 20));
                    difficultylabel.setHorizontalAlignment(SwingConstants.CENTER);
                    difficultyselected.setBounds(0,130,500,40); // add the label (originally invisible) under the buttons
                    difficultyselected.setHorizontalAlignment(SwingConstants.CENTER);
                    confirmcomputer.setBounds(150,370,200,30);
                    confirmcomputer.setHorizontalAlignment(SwingConstants.CENTER); // add the start button at the bottom middle
                    easy.setBounds(20,100,100,30);
                    easy.setHorizontalAlignment(SwingConstants.CENTER); // easy button
                    medium.setBounds(140,100,100,30);
                    medium.setHorizontalAlignment(SwingConstants.CENTER); // medium button
                    hard.setBounds(260,100,100,30);
                    hard.setHorizontalAlignment(SwingConstants.CENTER); // hard button
                    impossible.setBounds(380,100,100,30);
                    impossible.setHorizontalAlignment(SwingConstants.CENTER); // impossible button
                    difficultytitle.setBounds(150,150,200,50);
                    difficultytitle.setHorizontalAlignment(SwingConstants.CENTER); // this is the label that tells the user which difficulty they have selected
                    difficultytitle.setFont(new Font("Arial", Font.BOLD, 20));
                    difficultydesc.setBounds(0,200,500,30); // this is the label that describes the difficulty to the user, under the difficulty they have chosen
                    difficultydesc.setHorizontalAlignment(SwingConstants.CENTER);
                    computersettingmsg.setBounds(0,340,500,30); // this label will notify the user if they try to press the start button without choosing a difficulty
                    computersettingmsg.setHorizontalAlignment(SwingConstants.CENTER);
                    displayReturnMainMenu(back); // display back button
                    removeMainMenu(title,singleplayer,multiplayer,computer);
                    f.add(difficultylabel);
                    f.add(difficultyselected);
                    f.add(confirmcomputer);
                    f.add(easy);
                    f.add(medium);
                    f.add(hard);
                    f.add(impossible);
                    f.add(difficultytitle);
                    f.add(difficultydesc);
                    f.add(computersettingmsg);

                }
            });

            easy.addActionListener(new ActionListener() { // if the easy difficulty is chosen,
                public void actionPerformed(ActionEvent e){
                    difficulty = 0; // set the difficulty to 0 (0 for easy, 1 for medium, 2 for hard, 3 for impossible)
                    difficultyselected.setVisible(true);
                    difficultytitle.setForeground(new Color(34,139,34)); // set the title of the difficulty to a different colour
                    difficultytitle.setText("Easy");
                    difficultydesc.setText("Computer has a small chance in guessing a word successfully."); // set the labels
                } // do the same for the next 3 difficulties (medium, hard, impossible)
            });

            medium.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e){
                    difficulty = 1;
                    difficultyselected.setVisible(true);
                    difficultytitle.setForeground(new Color(255,140,0));
                    difficultytitle.setText("Medium");
                    difficultydesc.setText("Computer correctly guesses a word half of the time.");
                }
            });

            hard.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e){
                    difficulty = 2;
                    difficultyselected.setVisible(true);
                    difficultytitle.setForeground(new Color(255,0,0));
                    difficultytitle.setText("Hard");
                    difficultydesc.setText("Computer correctly guesses a word most of the time.");
                }
            });

            impossible.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e){
                    difficulty = 3;
                    difficultyselected.setVisible(true);
                    difficultytitle.setForeground(new Color(128,0,0));
                    difficultytitle.setText("Impossible");
                    difficultydesc.setText("Computer will guess the highest scoring words in order, without error. Good luck!");
                }
            });

            confirmcomputer.addActionListener(new ActionListener() { // actionlistener that will listen for the start button
                public void actionPerformed(ActionEvent e){
                    if (difficulty != -1) { // if the difficulty is anything other than -1 (which is if the user picks anything)

                        computersettingmsg.setText(""); // clear the invalid message (if there is one)
                        randomizeBoard(dice, board, letters); // get board,
                        getPossible(possible, list, board); // get possible words,
                        getAlmostPossible(almostpossible, list, board, possible); // get almost possible words. explanation here
                        // my approach to the player vs computer mode would be that each difficulty would have the computer have a certain chance of getting a word correct or not
                        // for example, in easy mode, the computer has a 25% chance of guessing a word correctly (pulled from possible words list) and the rest
                        // of the 75%, the computer will get wrong. but for the rest of the 75%, how would i make it look like the computer was trying to guess, instead of just saying
                        // that the computer did not guess anything, or pull a random word from the dictionary? so i thought of a method of finding the "almost possible" words on the board.
                        // to do this, we iterate through every single word in the english dictionary, and then see if the words are "almost possible".
                        // i will provide an example here. if the word JAI (not an english word) is on the board, the word JAIL can be spawned from it. this means that from the word JAI that
                        // is on the board, it can almost spawn the word JAIL, adding it to the almost possible words list.
                        // in summary, if there is a word that has its last letter missing from the board, and all other words exist, then it will be added to the almost possible words list.
                        displayLabels(letters, board, 150); // display the board
                        displayReturnMainMenu(back); // display back button
                        f.remove(difficultylabel);
                        f.remove(difficultyselected);
                        f.remove(confirmcomputer);
                        f.remove(easy);
                        f.remove(medium);
                        f.remove(hard);
                        f.remove(impossible);
                        f.remove(difficultytitle);
                        f.remove(difficultydesc);
                        f.remove(computersettingmsg);
                        // remove everything from the previous menu (difficulty choosing)
                        f.pack();
                        f.revalidate();
                        f.repaint();
                        f.setSize(500, 500);

                        computerinput.setBounds(20, 150, 80, 30); // textfield for the player
                        playerscorelabel.setBounds(35, 45, 110, 20); // label that says "Your Score"
                        playerscore.setBounds(40, 70, 100, 40); // player's score
                        playerscore.setText("0"); // set the score of the label to 0 for player
                        playerscore.setFont(new Font("Arial", Font.BOLD, 30));
                        computerscorelabel.setBounds(380, 45, 110, 20);
                        computerscore.setBounds(400, 70, 100, 40);
                        computerscore.setFont(new Font("Arial", Font.BOLD, 30));
                        computerscore.setText("0"); // set the score of the label to 0 for computer
                        computerplayeroutput.setBounds(0,320,500,30);
                        computerplayeroutput.setHorizontalAlignment(SwingConstants.CENTER);
                        computeroutput.setBounds(0,350,500,30);
                        computeroutput.setHorizontalAlignment(SwingConstants.CENTER);

                        f.add(computerinput);
                        f.add(playerscorelabel);
                        f.add(computerscorelabel);
                        f.add(playerscore);
                        f.add(computerscore);
                        f.add(computerplayeroutput);
                        f.add(computeroutput);
                        computerinput.setEditable(true);
                        // add all labels

                    } else { // catching no difficulty from line 920
                        computersettingmsg.setText("You have not selected a difficulty!"); // notify the player needs to set a difficulty
                        computersettingmsg.setForeground(new Color(255,0,0));
                    }

                }
            });

            computerinput.addActionListener(new ActionListener() { // actionlistener that will listen in for player input
                public void actionPerformed(ActionEvent e){
                    if (guessed.size() != possible.size()) { // if the number of guessed words has not met the number of possible words, the game will go on
                        String guess = ""; // this guess string will set the computers guess, if there is any
                        String input = computerinput.getText(); // get input
                        if (existOnBoard(board, input.toUpperCase(), false) && !guessed.contains(input.toUpperCase()) && possible.contains(input.toLowerCase())) { // if the word is possible,
                            playerscore.setText(String.valueOf(Integer.parseInt(playerscore.getText()) + input.length())); // update the player's score
                            guessed.add(input.toUpperCase()); // add the word to the list of guessed and validated words
                            computerplayeroutput.setText(input.toLowerCase() + " is a valid word, and you have earned " + input.length() + " points."); // notify the player they have recived points
                        } else if (existOnBoard(board, input.toUpperCase(), false) && guessed.contains(input.toUpperCase())) { // if word has already been guessed
                            computerplayeroutput.setText(input.toLowerCase() + " has already been inputted and accepted."); // notify player
                        } else { // else if invalid
                            computerplayeroutput.setText(input.toLowerCase() + " is an invalid word.");
                        }
                        computerinput.setText("");
                        int chance = 0; // this is really a percentage for the chance of the computer
                        if (difficulty == 0) { // if the difficulty is easy,
                            chance = 25; // 25% success
                        } else if (difficulty == 1) { // if the difficulty is medium,
                            chance = 50; // 50% success
                        } else if (difficulty == 2) { // if the difficulty is hard,
                            chance = 75; // 75% success
                        } else if (difficulty == 3) { // if the difficulty is impossible,
                            chance = 100; // 100% success
                        }

                        if (chance == 100) { // if 100% success (impossible mode)
                            for (int i = 0; i < possible.size(); i++) { // instead, we won't use randomization to pick a word that the computer is going to guess.
                                // we will iterate through the possible words, but from highest value to lowest value. it's called impossible for a reason :)
                                if (!guessed.contains(possible.get(i).toUpperCase())) { // if the word being iterated through has not been entered,
                                    guess = possible.get(i); // set the computer's guess to that
                                    break; // break the loop
                                }
                            }
                            computeroutput.setText("The computer guessed " + guess + " and earned " + guess.length() + " points."); // notify the player of the computer's guess
                            computerscore.setText(String.valueOf(Integer.parseInt(computerscore.getText()) + guess.length())); // add points
                            guessed.add(guess.toUpperCase()); // add the word to the guessed list
                        } else {

                            if (chance > rand.nextInt(100)) { // if the difficulty isn't impossible, and the chance comes out to the computer's favour, pick a random number
                                guess = possible.get(rand.nextInt(possible.size())); // used the picked number to get the word from the list
                                if (guessed.contains(guess.toUpperCase())) { // if the guessed word has already been guessed,
                                    computeroutput.setText("The computer guessed " + guess + " but it has already been inputted and accpeted."); // tell the user
                                } else {
                                    computeroutput.setText("The computer guessed " + guess + " and earned " + guess.length() + " points."); // otherwise, tell them that the points have been awarded
                                    computerscore.setText(String.valueOf(Integer.parseInt(computerscore.getText()) + guess.length()));
                                    guessed.add(guess.toUpperCase()); // add this to the guessed list
                                }
                            } else { // if the computer will guess incorrectly, not in their favour
                                guess = almostpossible.get(rand.nextInt(almostpossible.size())); // get a word from the almost possible words list
                                computeroutput.setText("The computer guessed " + guess + ", an invalid word."); // let the player know they guessed incorrectly
                            }
                        }

                        if (guessed.size() == possible.size()) { // if the guessed list and the possible list are the same as each other,
                            computerinput.setEditable(false); // end the game
                            computerplayeroutput.setText("All words have been guessed, and the game has ended.");
                        }
                    }
                }
            });

            f.setSize(500, 500);
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.add(panel);
            f.setVisible(true);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

