import java.util.*;
import java.lang.Math;
// import java.sql.Timestamp;


public class AlphaBetaAIV2 extends AIModule
{
    private static final int pMAX = 1;
    private static final int pMIN = 2;

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
        boolean isMax = true;
        if (activePlayer == pMIN)
            isMax = false;

        initializeState(game);
        int val = alphaBetaSearch(gameTree.root, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, isMax);
        System.out.println("val = " + val);
        ArrayList<Node> Move = gameTree.root.nextMoves;
        for (int i = 0; i < Move.size(); i++) {
           System.out.println("move's eval is  " + Move.get(i).eval);
           if (Move.get(i).eval == val) {
               System.out.println("chosenMove = " + Move.get(i).move);
               chosenMove = Move.get(i).move;
               return;
           }
        }
        // for (int iterDeepDepth = 7; iterDeepDepth < iterDeepMaxDepth; iterDeepDepth++) {
        //     System.out.println("---------INIT STATE WITH ITER DEEPTH DEPTH OF " + iterDeepDepth);
        //     initializeState(game, iterDeepDepth);
        //     int val = alphaBetaSearch(gameTree.root, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, isMax);
        //     System.out.println("val = " + val);
        //     ArrayList<Node> Move = gameTree.root.nextMoves;
        //     for (int i = 0; i < Move.size(); i++) {
        //         System.out.println("move's eval is  " + Move.get(i).eval);
        //         if (Move.get(i).eval == val) {
        //             System.out.println("chosenMove = " + i);
        //             chosenMove = Move.get(i).move;
        //             // return;
        //         }
        //     }
        // }
    }

    int alphaBetaSearch(Node node, int depth, int alpha, int beta, boolean isMax)
    {
        if (depth >= gameTree.maxDepth || game.isGameOver()) {
            // return node.eval;
            node.addEval(evaluation(game));
            return node.eval;
        }

        int val;

        if (isMax) {
            node.eval = Integer.MIN_VALUE;
            val = node.eval;
        } else {
            node.eval = Integer.MAX_VALUE;
            val = node.eval;
        }

        for (int move = 0; move < width; move++) {
            if (game.canMakeMove(move)) {
                Node next = new Node(depth + 1);
                next.addMove(move);
                node.nextMoves.add(next);
                game.makeMove(move);

                int childVal = alphaBetaSearch(next, depth+1, alpha, beta, !isMax);

                game.unMakeMove();

                if (isMax) {
                    if (childVal > val) {
                        val = childVal;
                    }
                    if (val >= beta) break;
                    alpha = Math.max(alpha, val);
                } else {
                    if (childVal <= val) {
                        val = childVal;
                    }
                    if (val <= alpha) break; //return val;
                    beta = Math.min(beta, val);
                }
            }
        }

        int childSize = node.nextMoves.size();
        if (childSize == 0) {
            node.addEval(evaluation(game));
            return node.eval;
        }

        node.addEval(val);
        return node.eval;
    }

    int max(Node node, int depth, int alpha, int beta, boolean isMax)
    {
        return 0;
    }

    int min(Node node, int depth, int alpha, int beta, boolean isMax)
    {
        return 0;
    }



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
                      p1token += evalTable[h][w] *2;
                      //p1token = evalTable[w][h] + (6 * p1token);

                  } else if (state.getAt(w, h) == pMIN){ // player 2

                    //grab eval table to add to potential win state eval.
                    //Must scale p1token to eval table. (Multiply by??)
                      p2token += checkLines(state, w, h, false);
                      p2token += evalTable[h][w] *2;
                      //p2token = evalTable[h][w] + (6 * p2token);

                  } else {
                      continue;
                  }

                  //p1token = evalTable[w][h] + (6 * p1token);
                  //p2token = evalTable[h][w] + (6 * p2token);
                  p1total += p1token;
                  //p1token += evalTable[h][w]*2;

                  p2total += p2token;
                  //p2token += evalTable[h][w]*2;
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

        // Check for game over
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
        int depth = 7;
        if (emptyRatio < 0.8)
            depth++;
        if (emptyRatio < 0.6)
            depth++;
        if (emptyRatio < 0.5)
            depth+=1;
        if (emptyRatio < 0.4)
            depth+=1;
        if (emptyRatio < 0.3)
            depth+=1;

        System.out.println("SETTING MAX DEPTH AT + " + depth);
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
}
