import java.util.*;

public class Player {
    /**
     * Performs a move
     *
     * @param gameState
     * the current state of the board
     * @param deadline
     * time before which we must have returned
     * @return the next state the board is in after our move
     */


    //Create a hash table of the states
    Hashtable<String, Integer> heuristic_table = new Hashtable<String, Integer>(64 * 64 * 64);

    //which is our player
    int player_inround;

    final long roundTime=6000000000L/100;

    public GameState play(final GameState gameState, final Deadline deadline) {

        player_inround = gameState.getNextPlayer();
        heuristic_table.clear();
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        if (nextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(gameState, new Move());
        }

        /**
         * Here you should write your algorithms to get the best next move, i.e.
         * the best next state. This skeleton returns a random move instead.
         */

        /** Get a next random state
         *Random random = new Random();
         *return nextStates.elementAt(random.nextInt(nextStates.size()));*/


        //Sort next possible states depending on their heuristic value
        nextStates=sortStates(nextStates);

        //Define the depth of the alpha beta pruning
        int dp = 1;

        if (nextStates.size() < 50) dp = 2;
        if (nextStates.size() < 25) dp = 3;
        if (nextStates.size() < 10) dp = 4;
        if (nextStates.size() < 5) dp = 5;

        //Search along states
        int greater_v0_index = 0;
        double v0 = Double.NEGATIVE_INFINITY; //init the heuristic value with a very low number
        double best_v0 = Double.NEGATIVE_INFINITY; // init the best value variable with a very low number
        double state_v;

        // alpha-beta pruning variables
        double alp_0 = Double.NEGATIVE_INFINITY; // init of alpha
        double b_0 = Double.POSITIVE_INFINITY; // init of delta

        //Select the best next move according to the lowest heuristic score
        for (int i = 0; i < nextStates.size(); i++) {
            //Search with alpha_beta the best next state value
            state_v = alpha_beta(nextStates.elementAt(i), dp, alp_0, b_0, player_inround % 2 + 1);

            //Keep the best evaluation value or the best pruned value
            v0 = Math.max(v0, state_v);
            alp_0 = Math.max(alp_0,v0);

            //Just the best v0 value is kept
            if (state_v > best_v0) {
                best_v0 = state_v;
                greater_v0_index = i;
            }
        }
        return nextStates.elementAt(greater_v0_index);
    }

    double alpha_beta(GameState game_state, int dp, double alpha, double beta, int player_turn) {
        double v;

        if (dp == 0)
        {
            v = heuristic_function(game_state, player_inround);
        }
        else {
            Vector<GameState> next_states = new Vector<GameState>();
            //Find the moves from the actual next states
            game_state.findPossibleMoves(next_states);

            if (next_states.size() == 0)
            {v = heuristic_function(game_state, player_inround);}

            else {
                // we have yet not reached the desired depth

                //Min turn
                if (player_turn == player_inround) {
                    v = Double.NEGATIVE_INFINITY;
                    for (GameState state : next_states) {
                        v = Math.max(v, alpha_beta(state, dp - 1, alpha, beta, 2));
                        alpha = Math.max(alpha,v);
                        if (beta <= alpha) break;
                    }
                }
                //Max turn
                else {
                    v = Double.POSITIVE_INFINITY;
                    for (GameState state : next_states) {
                        v = Math.min(v, alpha_beta(state, dp - 1, alpha, beta, 1));
                        beta = Math.min(beta, v);
                        if (beta <= alpha) break;
                    }
                }
            }

        }
        return v;
    }

    int[][] score_return(GameState state, int player_turn, int idx_row, int idx_col, int idx_lay, int idx2_row, int idx2_col, int idx2_lay, int[][] game_score) {
        int board_size = state.BOARD_SIZE;
        int player_idx = player_turn;

        //check the step between each next position in the board
        int dif_row = (idx2_row - idx_row) / (board_size - 1);
        int dif_col = (idx2_col - idx_col) / (board_size - 1);
        int dif_layer = (idx2_lay - idx_lay) / (board_size - 1);

        //init scores and players
        int other_player_index = (player_turn % 2) + 1;
        int player_score = 0;
        int other_player_score = 0;


        //sum up the score if we find a movement of each player
        for (int i = 0; i < board_size; i++) {
            if (state.at(idx_row + dif_row * i, idx_col + dif_col * i, idx_lay + dif_layer * i) == player_idx) {
                player_score++;
            } else if (state.at(idx_row + dif_row * i, idx_col + dif_col * i, idx_lay + dif_layer * i) == other_player_index) {
                other_player_score++;
            }
        }

        //update game_score table with the new heuristic scores
        if (other_player_score > 0) {
            if (player_score > 0) return game_score;

            game_score[other_player_score - 1][other_player_index - 1]++;
            return game_score;
        }
        if (player_score > 0) game_score[player_score - 1][player_idx - 1]++;

        return game_score;
    }

    int heuristic_function(GameState state, int player_turn) {
        //keep the score ofthe actual game
        int[][] game_score = new int[4][2];
        int score = 0;
        //convert the state to string to relate it in the hash table
        String key = to_string(state);

        //If the cell has already been analyzed pass
        if (heuristic_table.containsKey(key)) {
            return heuristic_table.get(key);
        //If the cell has not been analyzed yet then evaluate it
        } else {
            //give the heuristic value for all possible combinations in 3d
           game_score = states_checker(state, player_turn, game_score);
        }

        //compute the value of the cell by subtracting it from the score of the opponent.
        for (int i = 0; i < 4; i++) {
            if ((0 < game_score[i][0]) || (0 < game_score[i][1])) {
                score = score + (game_score[i][player_turn-1] - game_score[i][player_turn%2]) * (int) Math.pow(100, (i + 1));
            }
        }
        heuristic_table.put(key, score);
        return score;
    }

    // function to perform the evaluation for all possible cells
    int[][] states_checker(GameState state, int player_turn, int[][] game_score)
    {
        //evaluate for rows and columns
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                game_score = score_return(state, player_turn, 0, i, j, 3, i, j, game_score);
                game_score = score_return(state, player_turn, i, 0, j, i, 3, j, game_score);
                game_score = score_return(state, player_turn, i, j, 0, i, j, 3, game_score);
            }
        }
        //evaluate diagonals
        for (int i = 0; i < definition_diags.length; i++) {
            game_score = score_return(state, player_turn, definition_diags[i][0][0], definition_diags[i][0][1], definition_diags[i][0][2], definition_diags[i][1][0], definition_diags[i][1][1], definition_diags[i][1][2], game_score);
        }
        return game_score;
    }

    //evaluates the number of crosses or knots of the players to get the score


    final int[][][] definition_diags = {

            //crossed diagonals
            {{0, 0, 0}, {3, 3, 3}},
            {{3, 0, 0}, {0, 3, 3}},
            {{0, 3, 0}, {3, 0, 3}},
            {{0, 0, 3}, {3, 3, 0}},

            //horizontal diagonals
            {{0, 0, 0}, {0, 3, 3}},
            {{0, 0, 3}, {0, 3, 0}},

            {{1, 0, 0}, {1, 3, 3}},
            {{1, 0, 3}, {1, 3, 0}},

            {{2, 0, 0}, {2, 3, 3}},
            {{2, 0, 3}, {2, 3, 0}},

            {{3, 0, 0}, {3, 3, 3}},
            {{3, 0, 3}, {3, 3, 0}},

            //vertical diagonals
            {{0, 0, 0}, {3, 0, 3}},
            {{0, 0, 3}, {3, 0, 0}},

            {{0, 1, 0}, {3, 1, 3}},
            {{0, 1, 3}, {3, 1, 0}},

            {{0, 2, 0}, {3, 2, 3}},
            {{0, 2, 3}, {3, 2, 0}},

            {{0, 3, 0}, {3, 3, 3}},
            {{0, 3, 3}, {3, 3, 0}},

            //z axis diag
            {{0, 0, 0}, {3, 3, 0}},
            {{0, 3, 0}, {3, 0, 0}},

            {{0, 0, 1}, {3, 3, 1}},
            {{0, 3, 1}, {3, 0, 1}},

            {{0, 0, 2}, {3, 3, 2}},
            {{0, 3, 2}, {3, 0, 2}},

            {{0, 0, 3}, {3, 3, 3}},
            {{0, 3, 3}, {3, 0, 3}}



    };

    // string converter to sort and find the different states where we have been in
    String to_string(GameState state) {
        String output = "";
        for (int i = 0; i < 64; i++) {
            output += Integer.toString(state.at(i));
        }
        return output;
    }

    //Sorter of states so that alpha beta pruning is more efficient
    Vector<GameState> sortStates(Vector<GameState> stateVector) {
        Collections.sort(stateVector, new stateComparator());
        return stateVector;

    }

    //Declare a comparator of the states based on their heuristic value
    public class stateComparator implements Comparator<GameState> {

        //@Override
        public int compare(GameState sA, GameState sB) {
            return heuristic_function(sB, 1) - heuristic_function(sA, 1);
        }

        //    @Override
        public boolean equals(GameState sA, GameState sB) {
            return heuristic_function(sB, 1) == heuristic_function(sA, 1);
        }
    }


}

