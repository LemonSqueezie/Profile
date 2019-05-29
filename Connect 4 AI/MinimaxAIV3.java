import java.util.*;
// import java.sql.Timestamp;


public class MinimaxAIV3 extends AIModule
{
    private static final int pMAX = 1;
    private static final int pMIN = 2;
    // private static final int moveOrder = {3, 2, 4, 1, 5, 0, 6};

    GameStateModule game;
    int width = -1;
    int height = -1;
    Tree gameTree = null;
    int activePlayer = 0;

    // DEBUGGING:
    // int num_moves_made = 0;
    // int num_moves_unmade = 0;

    public void getNextMove(final GameStateModule game)
    {
        initializeState(game);

        boolean isMax = true;
        if (activePlayer == pMIN)
            isMax = false;

        // Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // System.out.println("GROWING-------");
        // System.out.println(timestamp);
        growGameTree(gameTree.root, isMax);
        // timestamp = new Timestamp(System.currentTimeMillis());
        // System.out.println("STOPPED GROWING------");
        // System.out.println(timestamp);
        // System.out.println("After grow tree, num_moves_made = " + num_moves_made + " num_moves_unmade = " + num_moves_unmade);

        // System.out.println("ROOT size = " + gameTree.root.nextMoves.size());
        // printLevelTree(gameTree.root);


        // System.out.println("player index = " + isMax);

        int val = minimax(gameTree.root, 0, isMax);
        //System.out.println("val = " + val);

        // System.out.println("Printing Tree ");
        // printLevelTree(gameTree.root);

        ArrayList<Node> Move = gameTree.root.nextMoves;
        // System.out.println("Move size = " + Move.size());
        for (int i = 0; i < Move.size(); i++) {
            //System.out.println("move's eval is  " + Move.get(i).eval);
            if (Move.get(i).eval == val) {
                //System.out.println("chosenMove = " + i);
                chosenMove = Move.get(i).move;
                return;
            }
        }
    }

    int alphaBetaSearch(Node node, int depth, int alpha, int beta, boolean isMax)
    {
        if (depth >= gameTree.maxDepth) {
            return node.eval;
        }

        int childSize = node.nextMoves.size();
        if (childSize == 0) {
            return node.eval;
        }

        int val = node.eval;
        // int a = Integer


        if (isMax) {
            for (int i = 0; i < childSize; i++) {
                if (alphaBetaSearch(node.nextMoves.get(i), depth+1, alpha, beta, !isMax) > val) {

                }
            }
        } else {

        }
        return 0;
    }

    // return a utility value
    int minimax(Node node, int depth, boolean isMax)
    {
        if (depth >= gameTree.maxDepth) {
            // System.out.println("leaf eval = " + evaluation(game));
            // node.addEval(evaluation(game));
            // return evaluation(game);
            return node.eval;
        }

        int childSize = node.nextMoves.size();
        if (childSize == 0) {
            // System.out.println("CHILD_SIZE == 0 at node at depth = " + depth + ", isMax = " + isMax + " eval = " + node.eval);
            return node.eval;
        }
        ArrayList<Integer> vals = new ArrayList<Integer>(childSize);
        if (isMax) {
            for (int i = 0; i < childSize; i++) {
                vals.add(minimax(node.nextMoves.get(i), depth+1, false));
            }
            node.addEval(Collections.max(vals));
            return Collections.max(vals);
        } else {
            for (int i = 0; i < childSize; i++) {
                vals.add(minimax(node.nextMoves.get(i), depth+1, true));
            }
            node.addEval(Collections.min(vals));
            return Collections.min(vals);
        }
    }



    // For each token, calculate whether it is possible to make a 4-in-row
    // and if it is, calculate the number of tokens that makes up the row.
    // Do this for all 4 directions per token, and sum up all tokens.
    // Then subtract the number of total tokens between p1 and p2.
    int evaluation(final GameStateModule state)
    {
        //Value positions that lead to more win states.
        //Higher "utility" slots get values more
        int evalTable[][] = {{1, 2 , 3 , 5 , 3, 2, 1},
                            { 2, 4 , 6 , 8 , 6, 4, 2},
                            { 3, 6 , 9 , 12, 9, 6, 3},
                            { 3, 6 , 9 , 12, 9, 6, 3},
                            { 2, 4 , 6 , 8 , 6, 4, 2},
                            { 1, 2 , 3 , 5 , 3, 2, 1}};

        // Estimated utility to be calculated
        int estUtil;
        // Check for game over
        if (state.getActivePlayer() == pMAX) {
            if (state.isGameOver()) {
                // System.out.println("winner winner chicken dinner");
                if (state.getWinner() == pMAX) {
                    return Integer.MAX_VALUE;
                } else if (state.getWinner() == pMIN){
                    return Integer.MIN_VALUE;
                } else {
                    return 0;
                }
            }
        }
        else {
            if (state.isGameOver()) {
                if (state.getWinner() == pMIN) {
                    return Integer.MIN_VALUE;
                } else if (state.getWinner() == pMAX){
                    return Integer.MAX_VALUE;
                } else {
                    return 0;
                }
            }
        }

        // Iterate through all tokens
        int p1total = 0;
        int p2total = 0;
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < state.getHeight(); h++) {
                int p1token = 0;
                int p2token = 0;
                if (state.getAt(w, h) == pMAX) { // player 1

                    //grab eval table to add to potential win state eval.
                    //Must scale p1token to eval table. (Multiply by??)
                    p1token += checkLines(state, w, h, true);
                    p1token += evalTable[h][w];
                    //p1token = evalTable[w][h] + (6 * p1token);

                } else if (state.getAt(w, h) == pMIN){ // player 2

                  //grab eval table to add to potential win state eval.
                  //Must scale p1token to eval table. (Multiply by??)
                    p2token += checkLines(state, w, h, false);
                    p2token += evalTable[h][w];
                    //p2token = evalTable[h][w] + (6 * p2token);

                } else {
                    continue;
                }

                //p1token = evalTable[w][h] + (6 * p1token);
                //p2token = evalTable[h][w] + (6 * p2token);
                p1total += p1token;
                p2total += p2token;
            }
        }
        estUtil = p1total - p2total;
        return estUtil;
    }

    int checkLines(GameStateModule state, int x, int y, boolean isMax)
    {
        int totCoins = 0;
        int opponent;
        int player;
        int nextToken;

        if (isMax) {
            player = pMAX;
            opponent = pMIN;
        }
        else {
            player = pMIN;
            opponent = pMAX;
        }


        // Check horizontal
        int left = x;
        int right = x;
        int tempCoins = 1;
        int counter = 1;
        while (true)
        {
            left--;
            nextToken = state.getAt(left, y);
            if (left < 0 || nextToken == opponent)
                break;

            if (nextToken != opponent) {
                counter++;
                if(nextToken == player) {
                    tempCoins++;
                }
            }

        }
        while (true)
        {
            right++;
            nextToken = state.getAt(right, y);
            if (right >= width || nextToken == opponent)
                break;

            if (nextToken != opponent) {
                counter++;
                if(nextToken == player) {
                    tempCoins++;
                }
            }
        }
        if (counter >= 4) {
            totCoins += tempCoins;
        }

        tempCoins = 1;
        counter = 1;
        // Check vertical
        int up = y;
        int down = y;
        while (true)
        {
            down--;
            nextToken = state.getAt(x, down);
            if (down < 0 || nextToken == opponent)
                break;

            if (nextToken != opponent) {
                counter++;
                if (nextToken == player) {
                    tempCoins++;
                }
            }

        }
        while (true)
        {
            up++;
            nextToken = state.getAt(x, up);
            if (up >= state.getHeight() || nextToken == opponent)
                break;
            if (nextToken != opponent) {
                counter++;
                if (nextToken == player) {
                    tempCoins++;
                }
            }
        }
        if (counter >= 4) {
            totCoins += tempCoins;
        }
        tempCoins = 1;
        counter = 1;

        // Check left slash diagonal
        int x_iter = x;
        int y_iter = y;
        while (true)
        {
            x_iter--;
            y_iter++;
            nextToken = state.getAt(x_iter, y_iter);
            if (x_iter < 0 || y_iter >= state.getHeight() || nextToken == opponent)
                break;

            if (nextToken != opponent) {
                counter++;
                if (nextToken == player) {
                    tempCoins++;
                }
            }

        }
        x_iter = x;
        y_iter = y;
        while (true)
        {
            x_iter++;
            y_iter--;
            nextToken = state.getAt(x_iter, y_iter);
            if (y_iter < 0 || x_iter >= width || nextToken == opponent)
                break;

            if (nextToken != opponent) {
                counter++;
                if (nextToken == player) {
                    tempCoins++;
                }
            }

        }
        if (counter >= 4) {
            totCoins += tempCoins;
        }

        tempCoins = 1;
        counter = 1;
        // Check right slash diagonal
        x_iter = x;
        y_iter = y;
        while (true)
        {
            x_iter++;
            y_iter++;
            nextToken = state.getAt(x_iter, y_iter);

            if (x_iter >= width || y_iter>= state.getHeight() || nextToken == opponent)
                break;

            if (nextToken != opponent) {
                counter++;
                if (nextToken == player) {
                    tempCoins++;
                }
            }

        }
        x_iter = x;
        y_iter = y;
        while (true)
        {
            x_iter--;
            y_iter--;
            nextToken = state.getAt(x_iter, y_iter);

            if (y_iter < 0 || x_iter < 0 || nextToken == opponent)
                break;

            if (nextToken != opponent) {
                counter++;
                if (nextToken == player) {
                    tempCoins++;
                }
            }

        }
        if (counter >= 4) {
            totCoins += tempCoins;
        }
        // tempCoins = 1;
        // counter = 1;
        return totCoins;
    }


    void initializeState(final GameStateModule game)
    {
        this.game = game.copy();
        width = game.getWidth();
        height = game.getHeight();
        gameTree = new Tree();
        activePlayer = game.getActivePlayer();

        double numEmpty = 0;
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                if (game.getAt(col, row) == 0) {
                    numEmpty += 1;
                }
            }
        }
        double emptyRatio = numEmpty / (double)(width * height);
        int depth = 6;
        if (emptyRatio < 0.5)
            depth++;
        if (emptyRatio < 0.4)
            depth++;
        if (emptyRatio < 0.3)
            depth+=2;
        if (emptyRatio < 0.2)
            depth+=2;
        if (emptyRatio < 0.1)
            depth+=2;

        //System.out.println("SETTING MAX DEPTH AT + " + depth);
        gameTree.setMaxDepth(depth);
    }

    // generate the children of the node
    void growGameTree(Node node, boolean isMax)
    {
        if (node.eval == -1) {
            System.out.println("-------------Node is root");
        }
        // if (node.move != -1){
        //     game.makeMove(node.move);
        //     // System.out.println("make move");
        // }
        // if (isMax) {
        //     node.eval = Integer.MIN_VALUE;
        // } else {
        //     node.eval = Integer.MAX_VALUE;
        // }

        if (node.depth >= gameTree.maxDepth || game.isGameOver()) {
            // System.out.println("node.depth is: " + node.depth);
            // System.out.println("max.depth is: " + gameTree.maxDepth);
            node.addEval(evaluation(game));
            // game.unMakeMove();
            // num_moves_unmade++;
            // System.out.println("UNmake move");
            return;
        }

        // grow one layer of children of the node
        for (int move = 0; move < width; move++) {
            // System.out.println("make move");
            if (game.canMakeMove(move)) {
                int depth = node.depth;
                Node temp = new Node(depth + 1);
                temp.addMove(move);
                // System.out.println("make move : " + move);
                node.nextMoves.add(temp);
                game.makeMove(move);
                // temp.player = game.getActivePlayer();
                // num_moves_made++;
                growGameTree(temp, !isMax);
                game.unMakeMove();
                // num_moves_unmade++;
            } else {
                // DEBUGGING:
                // Print game state:
                // System.out.println("WARNING: cannot make move at move = " + move + " depth = " + node.depth + " height = " + game.getHeightAt(move));
                // throw new ArithmeticException();
            }
        }
    }

    class Tree
    {
        Node root;
        int maxDepth = -1;

        public Tree()
        {
            root = new Node();
            maxDepth = 0;
        }

        public Tree(Node root)
        {
            this.root = root;
            maxDepth = 0;
        }

        public void setMaxDepth(int maxDepth)
        {
            this.maxDepth = maxDepth;
        }
    }

    // public class comparator implements Comparator<Node>
    // {
    //     @Override
    //     public int compare(final Node n1, final Node n2)
    //     {
    //         return n1.eval - n2.eval;
    //     }
    // }

    void printLevelTree(Node root)
    {
        if (root == null)
            return;
        Queue<Node> q = new LinkedList<Node>();
        q.add(root);
        while (!q.isEmpty()) {
            int n = q.size();

            while(n > 0) {
                Node p = q.remove();
                System.out.print(p.eval + " (" + p.nextMoves.size() + ") ");

                for (int i = 0; i < p.nextMoves.size(); i++)
                    q.add(p.nextMoves.get(i));

                n--;
            }
            System.out.println();
            System.out.println("____________________");
        }
    }

    class Node
    {
        int depth = 0;
        int eval = 0;
        int move = -1;
        ArrayList<Node> nextMoves;

        // DEBUGGING:
        // int player = 0;

        public Node()
        {
            eval = 0;
            move = -1;
            depth = 0;
            nextMoves = new ArrayList<Node>();
        }

        public Node(int depth)
        {
            eval = 0;
            move = -1;
            this.depth = depth;
            nextMoves = new ArrayList<Node>();
        }
        public int value()
        {
            return this.eval;
        }

        public void addMove(int move)
        {
            this.move = move;
        }

        public void addEval(int eval)
        {
            this.eval = eval;
        }
    }

    // public class NodeComp implements Comparator<Node>
    // {
    //     @Override
    //     public int compare(Node n1, Node n2)
    //     {
    //         return n1.eval - n2.eval;
    //     }
    // }


}
