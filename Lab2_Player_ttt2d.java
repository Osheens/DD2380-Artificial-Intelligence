package com.company;

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

    //To store which is the actual player
    int actual_player;
    int other_player;

    public GameState play(final GameState gameState, final Deadline deadline) {
        actual_player = gameState.getNextPlayer();
        other_player = actual_player == 1 ? 2 : 1;
        Vector<GameState> NextStates = new Vector<GameState>();
        gameState.findPossibleMoves(NextStates);


        if (NextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(gameState, new Move());
        }

        int depth = 5;

        //Search along states
        int best_ever_state_idx = 0;
        double eval = Double.NEGATIVE_INFINITY; //the values must be very low at the beggining so that the best next states are considered
        double best_eval = Double.NEGATIVE_INFINITY;
        double actual_state_value;

        // alpha-beta pruning variables
        double alpha0 = Double.NEGATIVE_INFINITY;
        double beta0 = Double.POSITIVE_INFINITY;

        //Select the best next state
        for (int i = 0; i < NextStates.size(); i++) {
            //Search with alpha_beta_pruning the best next state value
            actual_state_value = alphaBeta(NextStates.elementAt(i), depth, alpha0, beta0, other_player);

            //Keep the best evaluation value or the best prunned value
            eval = Math.max(eval, actual_state_value);
            alpha0 = Math.max(alpha0, eval);

            //We just want to keep the best next state
            if (alpha0 > best_eval) {
                best_eval = alpha0;
                best_ever_state_idx = i;
            }
        }
        return NextStates.elementAt(best_ever_state_idx);
    }


    public double alphaBeta(GameState game_state, int depth, double alpha, double beta, int player) {
        // System.err.println("player color is " + game_state.getNextPlayer());
        if ((depth <= 0) || game_state.isEOG()) {
            return game_heuristic(game_state, actual_player);
        }

        Vector<GameState> next_states = new Vector<GameState>();
        game_state.findPossibleMoves(next_states);

        double eval;

        // Maximizing player
        if (player == 1) {
            eval = Double.NEGATIVE_INFINITY;
            for (GameState state : next_states) {
                eval = Math.max(eval,
                        alphaBeta(state, depth - 1, alpha, beta, other_player));
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return eval;

        }
        // Minimizing player
        else {
            eval = Double.POSITIVE_INFINITY;
            for (GameState state : next_states) {
                eval = Math.min(eval,
                        (alphaBeta(state, depth - 1, alpha, beta, actual_player)));
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return eval;
        }
    }


    //function that evaluates if there is a full row, column or diagonal or is end of game and who won
    double game_heuristic(GameState state, int first_idx) {
        int board_size = state.BOARD_SIZE;
        int other_player_idx = first_idx == 1 ? 2 : 1;
        int player_points = 0;
        int other_player_points = 0;



        if (state.isEOG()) {
            //Check if end of game is reached --> need to determine who won or if it is a draw.
            if (state.isXWin()) {
                return Double.POSITIVE_INFINITY;
            } else if (state.isOWin()) {
                return Double.NEGATIVE_INFINITY;
            } else {
                return 0;
            }
        }

        // evaluate heuristic for rows
        int[] row_points = new int[2];
        row_points = row_heuristics(board_size, state, first_idx, other_player_idx);
        player_points += row_points[0];
        other_player_points += row_points[1];


        // evaluate heuristic for columns
        int[] columns_points = new int[2];
        columns_points = columns_heuristics(board_size, state, first_idx, other_player_idx);
        player_points += columns_points[0];
        other_player_points += columns_points[1];

        // take the heuristics for diagonals
        int[] diagonals_points = new int[2];
        diagonals_points = diagonals_heuristics(board_size, state, first_idx, other_player_idx);
        player_points += diagonals_points[0];
        other_player_points += diagonals_points[1];


        return ((int)Math.pow(100, player_points) - (int)Math.pow(100, other_player_points));
    }

    //Calculate row heuristics
    public static int[] row_heuristics(int board_size, GameState state, int first_idx, int other_player_idx) {
        int player_points = 0;
        int other_player_points = 0;
        for (int i = 0; i < board_size; i++) {
            for (int j = 0; j < board_size; j++) {
                if (state.at(j, i) == first_idx) {
                    player_points++;
                } else if (state.at(j, i) == other_player_idx) {
                    other_player_points++;
                }
            }
        }
        return new int[]{player_points, other_player_points};
    }

    //Calculate columns heuristics
    public static int[] columns_heuristics(int board_size, GameState state, int first_idx, int other_player_idx)
    {
        int player_points = 0;
        int other_player_points = 0;
        for (int i = 0; i < board_size; i++)
        {
            for(int j = 0; j< board_size; j++)
            {
                if(state.at(i,j) == first_idx) {
                    player_points++;
                }
                else if (state.at(i,j)== other_player_idx) {
                    other_player_points++;
                }
            }
        }
        return new int[] {player_points, other_player_points};
    }

    //Calculate diagonal heurisics
    public static int[] diagonals_heuristics(int board_size, GameState state, int first_idx, int other_player_idx)
    {
        int player_points = 0;
        int other_player_points = 0;


        // cell 3,0 to cell 0,3
        int j = 0;
        for(int i = board_size-1; i>=0;i--)
        {
            if(state.at(i,j) == first_idx) {
                player_points++;
            }
            else if (state.at(i,j) == other_player_idx) {
                other_player_points++;
            }

            j++;
        }


        //cell 0,0 to cell 3,3

        for(int i = 0; i<board_size; i++)
        {
            if(state.at(i,i) == first_idx) {
                player_points++;
            }
            else if (state.at(i,i) == other_player_idx){other_player_points++;};
        }
        return new int[] {player_points, other_player_points};
    }





}

