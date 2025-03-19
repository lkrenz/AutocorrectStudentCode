public class TST {
    Node root;

    public TST(char letter) {
        this.root = new Node(letter);
    }
    public TST() {
        this.root = new Node('m');
    }

    // Adds words to the TST
    public void addWord(String word) {
        Node node = this.root;
        int i = 0;

        // Iterates through the TST, adding nodes if necessary to add words
        while (i < word.length()) {

            // I represents the current letter in the word
            char letter = word.charAt(i);

            // Handles cases of accepting the current node or moving to the left or right
            if (letter < node.getLetter()) {
                node = node.findChild(letter);
            }
            else if (letter > node.getLetter()) {
                node = node.findChild(letter);
            }
            else {
                // If we accept the node, we move on to the next letter
                i++;
                if (i == word.length()) {
                    node.setWord(true);
                    break;
                }
                // getMiddle can create a new node if necessary
                node = node.getMiddle(word.charAt(i));
            }
        }
    }


    public boolean checkWord(String word) {
        Node node = this.root;
        int i = 0;

        // Iterates until reaching a leaf or a null node
        while (node != null && i < word.length()) {
            char letter = word.charAt(i);
            char nodeLetter = node.getLetter();

            // Handles left right and middle cases on the TST
            if (letter < nodeLetter) {
                node = node.getLeft();
            }
            else if (letter > nodeLetter) {
                node = node.getRight();
            }
            else {
                // If we accept the node, we move onto the next character
                if (i == word.length() - 1) {
                    return !node.isWord();
                }
                i++;
                node = node.getMiddle();
            }
        }
        return true;
    }
}
