public class Node {

    private Node left;
    private Node middle;
    private Node right;
    private final char letter;
    private boolean word;

    // Creates a new node given only the letter
    public Node(char letter) {
        this.letter = letter;
        this.middle = null;
        this.right = null;
        this.left = null;
    }

    // Creates a new node given a letter and the boolean isWord
    public Node(char letter, boolean isWord) {
        this.letter = letter;
        this.middle = null;
        this.right = null;
        this.left = null;
        this.word = isWord;
    }

    public Node getLeft() {
        return left;
    }

    public Node getMiddle() {
        return middle;
    }

    public Node getRight() {
        return right;
    }

    public char getLetter() {
        return letter;
    }

    public boolean isWord() {
        return word;
    }

    public void setWord(boolean word) {
        this.word = word;
    }

    // Returns the correct child, initializing the node if necessary
    public Node findChild(char letter) {
        if (letter == this.letter) {
            if (this.middle == null) {
                this.middle = new Node(letter);
            }
            return this.middle;
        } else if (letter < this.letter) {
            if (this.left == null) {
                this.left = new Node(letter);
            }
            return this.left;
        } else {
            if (this.right == null) {
                this.right = new Node(letter);
            }
            return this.right;
        }
    }

    // Returns the middle node, initializing it if necessary
    public Node getMiddle(char letter) {
        if (this.middle == null) {
            this.middle = new Node(letter);
        }
        return middle;
    }
}
