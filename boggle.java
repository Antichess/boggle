import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import javax.swing.*;

// this is my entire approach to this project:
// use HashSet for the entire wordlist. checking is extremely efficient
// randomly generate a grid first. 1/3 vowels and 2/3 consonants
// lay them out and then get every possible column, vertical, and diagonal and append them as a string to an arraylist
// also reverse all the strings
// this will lay the foundation for the checking that will be done later on
// display the GUI
// whenever someone checks for a word, it activates the actionlistener

public class boggle {
    static private JFrame f = new JFrame("Boggle Game"); // initialize these two so they can be used in methods and everywhere
    static private JPanel panel = new JPanel();


    public static void randomize(String[][] grid) { //this will randomize the board
        int len = 6;
        Random r = new Random(); //random class
        for (int i = 0; i < len; i++) { // these two loops will run through the entire grid
            for (int j = 0; j < len; j++) {
                String c = "";
                String[] vowels = {"A","E","I","O","U"}; //list of vowels and consonants
                String[] consonants = {"B","C","D","F","G","J","K","L","M","N","P","Q","S","T","V","X","Z","H","R","W","Y"};
                if (r.nextInt(3) == 0) { // if the number generated between 0 and 2 is 0, then pick a vowel
                    c = vowels[r.nextInt(5)];
                } else { // if the number generated between 0 and 2 is either 1 or 2, pick a consonant
                    c = consonants[r.nextInt(21)];
                }
                grid[i][j] = c; // map the consonant or vowel to the grid
            }
        }
    }
    public static void getCombos(String[][] grid, ArrayList<String> combos) { //the combos arraylist stores all the combos
        // combos are a list of all rows, columns, and diagonals that exist
        // there may not be a word in every one of them
        // to check if there is a word, it will run through the user input with .contains and return true if the word is in one of the combos
        int len = 6;

        combos.clear(); // clears it to make sure that there is nothing in the array from previous uses
        StringBuilder sb = new StringBuilder(); // using stringbuilder because i want to get reverse diagonals
                                                // i could use mathematics but i don't want to get another headache trying to figure that out
        for (int k = 0; k < len * 2; k++) {
            String str1 = ""; // two strings at a time, because i realized they are just opposite of each other, hence [5-i]
            String str2 = "";
            for (int j = 0; j <= k; j++) {
                int i = k - j;
                if (i < len && j < len) {
                    str1 = str1 + grid[i][j]; // get diagonals from bottom left to top right
                    str2 = str2 + grid[5-i][j]; // get diagonals from top left to bottom right
                }
            }
            combos.add(str1); // add this to the arraylist
            combos.add(str2);
            // this is a stringbuilder, it reverses the diagonal i just made
            sb.append(str1);  // add the string to the stringbuilder
            combos.add(sb.reverse().toString()); // reverse the string and add it
            sb.setLength(0); // clear the stringbuilder
            sb.append(str2); // do the same for the other string
            combos.add(sb.reverse().toString());

        }

        for (int i = 0; i < len; i++) { // this gets all the vertical and horizontal combos
            String hor1 = ""; String ver1 = ""; String hor2 = ""; String ver2 = ""; //initialize 4 at a time
            for (int j = 0; j < len; j++) { // for loop to run for it
                hor1 = hor1 + grid[j][i];
                ver1 = ver1 + grid[i][j];
                hor2 = hor2 + grid[5-j][5-i]; // i figured that you could get all of them in one for loop, by just subtracting it by 5
                ver2 = ver2 + grid[5-i][5-j];
            }
            combos.add(hor1); // add them all to the arraylist
            combos.add(hor2);

            combos.add(ver1);
            combos.add(ver2);
        }

        while (combos.contains("")) { // remove any null values from the arraylist
            combos.remove("");
        }


    }

    public static void displayTable(String[][] grid) { // this just prints the grid out, mostly redundant but was useful for the first stages of this program
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println("");
        }
    }

    public static void displayLabels(JLabel[] labels, String[][] grid) { // JLabels are stored in a 1D array
        int len = 6;

        for (int i = 0; i < len; i++) { // this places them out into a grid style
            for (int j = 0; j < len; j++) {
                labels[i*6+j] = new JLabel(grid[j][i]);
                labels[i*6+j].setBounds((250 + i * 30), (100 + j * 30), 15, 15); // depending on what i and j are, it spaces them out accordingly
                panel.add(labels[i*6+j]); // add the label to the window

            }
        }
    }
    public static void removeLabels(JLabel[] labels) { // to reset the grid, we must remove the labels
        for (int i = 0; i < 36; i++) { // 36 labels in the grid, 6*6
            panel.remove(labels[i]); // removes labels from the array, the labels are stored as array
            panel.revalidate(); // revalidate since the old labels may mess something up
            panel.repaint();
        }
    }


        public static void main(String[] args) {
            BufferedReader reader; // to read the file
            StringBuilder sb = new StringBuilder();
            Set<String> list = new HashSet<>();

            try {
                reader = new BufferedReader(new FileReader("wordlist.txt")); // read file
                String line = reader.readLine();

                while (line != null) { // keep adding words to array until it reaches the end
                    list.add(line); // add to the hashset
                    line = reader.readLine(); // read the next line and go back to the top
                }
                int len = 6;

                String grid[][] = new String[6][6]; // declare the grid in the main method


                ArrayList<String> combos = new ArrayList<>(); // declare the combos arraylist in the main method

                //displayTable(grid);
                //f.getContentPane();

                JLabel[] labels = new JLabel[36]; // declare the array that stores the 6x6 grid of labels

                // i used non-editable textfields for this next part as it was much easier to work with than labels
                // i tried to use labels but i could not figure out how to do so in this context, so i settled with textfields
                // textfields work just as well

                final JTextField wordexists = new JTextField(); // this declares the textfield that tells if the word exists or not
                wordexists.setEditable(false);
                wordexists.setOpaque(true);
                wordexists.setBorder(null);
                wordexists.setBounds(250,275,300,20);
                wordexists.setText("");

                final JTextField isonboard = new JTextField(); // this declares the textfield that tells if the word is on the board or not
                isonboard.setEditable(false);
                isonboard.setOpaque(true);
                isonboard.setBorder(null);
                isonboard.setBounds(250,300,300,20);
                isonboard.setText("");

                // do these actions when the program first opens up
                randomize(grid); // randomize the grid with the method
                getCombos(grid, combos); // this will fill the arraylist with all the possible combos in the grid
                displayLabels(labels, grid); // display the grid onto the GUI

                panel.setLayout(null); // makes it so that my own layout is accepted and isn't automatically centered

                JButton b = new JButton("Randomize!"); // initialize the randomize button
                b.setBounds(70,275,120,30);
                b.addActionListener(new ActionListener(){ // this is for making the button's action listener
                                                          // without this we cannot detect whenever someone randomizes the board
                    public void actionPerformed(ActionEvent e){
                        // these actions are the same as when the program is first opened up
                        randomize(grid); // randomize
                        getCombos(grid, combos); // get the combos
                        removeLabels(labels); // remove the previous labels that were displayed on the GUI as they are no longer valid
                        displayLabels(labels,grid); // redisplay the labels that were re-randomized
                        wordexists.setText("Board randomized."); // tell the user that the board was successfully randomize
                        Color color = new Color(0,0,0); // set it to black text
                        wordexists.setForeground(color);
                        isonboard.setText("");
                        f.setVisible(true);
                        f.pack();
                        f.setSize(500, 500);


                    }
                });

                final JTextField tf = new JTextField(); // input textfield
                tf.setBounds(50,100, 150,20);

                // big boggle text at the top middle
                JLabel title = new JLabel("Boggle");
                title.setBounds(175,-50,200,200);
                title.setFont(new Font("Arial", Font.BOLD, 35));

                // label for instructions
                JLabel instructions = new JLabel("<html><div style='text-align: center;'>Input your word guesses into<br/>the text box, and then<br/>press Enter to check.</html>");
                instructions.setBounds(55,50,200,200);
                instructions.setFont(new Font("Arial", Font.PLAIN, 10));

                f.add(wordexists); // add everything to the window
                f.add(isonboard);
                f.add(title);
                f.add(instructions);
                f.add(b);
                f.add(tf);
                f.add(panel);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // end program when the window is closed
                f.setSize(500, 500);
                f.setVisible(true);

                String input = ""; // initialize input variable

                tf.addActionListener(new ActionListener() { // this entire actionlistener will check when the enter key is pressed to submit a word
                    public void actionPerformed(ActionEvent e) {
                        String input = tf.getText(); // this tells that the variable is changed whenever the textbox has text entered into it


                        // if the english dictionary has the inputted word, and the input is not null, then;
                        if (list.contains(input.toLowerCase()) && (!input.equals(""))) {
                            // display the text that the word exists
                            wordexists.setText("The word " + input + " exists");
                            Color color = new Color(0,128,0); // green colour
                            wordexists.setForeground(color);
                        } else {
                            // else, display that the text that the word does not exist
                            wordexists.setText("The word " + input + " does not exist");
                            Color color = new Color(255,0,0); // red colour
                            wordexists.setForeground(color);
                        }
                        // this will now loop through the combos array we made
                        boolean inside = false;
                        for (String n : combos) {
                            // if the inputted word is in at least one of the combos, it will return True and have an effect on the if statement after
                            if (n.contains(input.toUpperCase())) {
                                if (!input.equals("")) {
                                    inside = true;
                                    break;
                                }
                            }
                        }
                        // if the while loop beforehand has returned true, then change text accordingly
                        if (inside) {
                            // display the text that the word is on the board
                            isonboard.setText(input + " is on the board");
                            Color color = new Color(0,128,0); // green colour
                            isonboard.setForeground(color);
                        } else {
                            // display the text that the word is not on the board
                            isonboard.setText(input + " is not on the board");
                            Color color = new Color(255,0,0); // red colour
                            isonboard.setForeground(color);
                        }

                        tf.setText(""); // clear the textbox, as the textbox does not clear by default after pressing enter

                        f.setVisible(true); // set the entire window to be visible again incase of any errors
                        //f.pack();
                        f.setSize(500, 500);
                    }
                });

                // this is all the way back to the file reading, incase it did not work
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }