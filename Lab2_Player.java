//package com.company;

import java.util.*;

public class Player {
    /**
     * Performs a move
     *
     * @param pState
     *            the current state of the board
     * @param pDue
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */
    int real_player;

    public GameState play(final GameState pState, final Deadline pDue) {

        real_player = pState.getNextPlayer();
        Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);


        if (lNextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(pState, new Move());
        }

        /**
         * Here you should write your algorithms to get the best next move, i.e.
         * the best next state. This skeleton returns a random move instead.

        Random random = new Random();
        return lNextStates.elementAt(random.nextInt(lNextStates.size()));
         */

        //Determine a search dp depending on the amount of next states.

        //Sort next states depending on their evaluation
        lNextStates=sortDependingOnHeur(lNextStates);

        int dp = 8;


        //Search along states
        int winner_next_idx = 0;
        int v_heur = Integer.MIN_VALUE; //the values must be very low at the beggining so that the best next states are considered
        int winner_value = Integer.MIN_VALUE;
        int v_heur_temp;

        // alpha-beta pruning variables
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        //Select the best next state
        for (int i = 0; i < lNextStates.size(); i++) {
            //Search with alpha_beta_pruning the best next state value
            v_heur_temp = alpha_beta_prunning(lNextStates.elementAt(i), dp,alpha, beta, 0);

            //Keep the best evaluation value or the best prunned value
            v_heur = Math.max(v_heur, v_heur_temp);
            alpha = Math.max(alpha,v_heur);

            //We just want to keep the best next state
            if (alpha > winner_value) {
                winner_value = alpha;
                winner_next_idx = i;
            }
        }


        return lNextStates.elementAt(winner_next_idx);
    }


    
    public int alpha_beta_prunning(GameState game_state, int dp, int alpha, int beta, int player) {

        if ((dp <= 0) || game_state.isEOG()) {
            return heuristic_function(game_state, real_player);
        }

        Vector<GameState> next_states = new Vector<GameState>();
        game_state.findPossibleMoves(next_states);

        int v_heur;

        // Max player turn
        if (player == 1) {
            v_heur = Integer.MIN_VALUE;

            for (GameState state : next_states) {

                v_heur = Math.max(v_heur,
                        alpha_beta_prunning(state, dp - 1, alpha, beta, 0));
                alpha = Math.max(alpha, v_heur);
                if (beta <= alpha) {
                    break;
                }
            }
            return v_heur;

        }
        // Min player turn
        else {
            v_heur = Integer.MAX_VALUE;
            for (GameState state : next_states) {
                v_heur = Math.min(v_heur,
                        (alpha_beta_prunning(state, dp - 1, alpha, beta, 1)));
                beta = Math.min(beta, v_heur);
                if (beta <= alpha) {
                    break;
                }
            }
            return v_heur;

        }

    }



    public int heuristic_function(GameState gameState, int player)
    {
        //init for each player score
        int A_K_points = 0;
        int B_K_points = 0;
        int A_P_points = 0;
        int B_P_points = 0;
        int final_score = 0;

        //If the end of game is reached then we check who is winning and returning this who is winning
        if(gameState.isEOG())
        {
            if (player == 1 && gameState.isRedWin())
            {final_score = 1000;
                return  final_score;
            }
            else if (player == 1 && gameState.isWhiteWin())
            {final_score = -1000;
                return  final_score;
            }
            else if (player == 2 && gameState.isWhiteWin())
            {final_score = 1000;
                return  final_score;
            }
            if (player == 2 && gameState.isRedWin())
            {final_score = -1000;
                return  final_score;
            }
            else
            {
                final_score = -1000;
                return  final_score;
            }
        }
        else{

            //We start checking all the squares in the board
            for (int i = 0; i < gameState.NUMBER_OF_SQUARES; i++) {
                //if we are facing a valid cell occupied by a red checker
                if (0 != (gameState.get(i) & Constants.CELL_RED)) {
                    // if it is red turn and we are facing a red cell we add +2 to the final score if the red cell is on the last most row or column (max)
                    if (player == 1) {
                        if (gameState.cellToCol(i) == 0 || gameState.cellToRow(i) == 7) {
                            final_score += 2;
                        } else if (gameState.cellToCol(i) == 1 || gameState.cellToCol(i) == 6) {
                            final_score += 1;
                        } else if (gameState.cellToRow(i) == 6 || gameState.cellToRow(i) == 5) {
                            final_score += 1;
                        }
                    A_P_points += 5;

                        // if its white turn and facing a checker from the red team the final score will be deduced in the same amount (min)
                    } else {
                        if (gameState.cellToCol(i) == 0 || gameState.cellToRow(i) == 7) {
                            final_score -= 2;
                        } else if (gameState.cellToCol(i) == 1 || gameState.cellToCol(i) == 6) {
                            final_score -= 1;
                        } else if (gameState.cellToRow(i) == 6 || gameState.cellToRow(i) == 5) {
                            final_score -= 1;
                        }
                    }


                    // if we find a king ckecker
                    if (0 != (gameState.get(i) & Constants.CELL_KING)) {

                            A_K_points += 7;
                    }


                    //Repeat the process but for white cells
                }
                else if (0 != (gameState.get(i) & Constants.CELL_WHITE)) {
                        // if it is red turn and we are facing a red cell we add +2 to the final score if the red cell is on the last most row or column (max)
                        if (player == 2) {
                            if (gameState.cellToCol(i) == 7|| gameState.cellToRow(i) == 0 ) {
                                final_score += 2;
                            } else if (gameState.cellToCol(i) == 1 || gameState.cellToCol(i) == 6) {
                                final_score += 1;
                            }  else if (gameState.cellToRow(i) == 1 || gameState.cellToRow(i) == 2) {
                            final_score += 1;
                        }

                            // if its white turn and facing a checker from the red team the final score will be deduced in the same amount (min)
                        } else {
                            if (gameState.cellToCol(i) == 7|| gameState.cellToRow(i) == 0 ) {
                                final_score -= 2;
                            } else if (gameState.cellToCol(i) == 1 || gameState.cellToCol(i) == 6) {
                                final_score -= 1;
                            } else if (gameState.cellToRow(i) == 1 || gameState.cellToRow(i) == 2) {
                                final_score -= 1;
                            }
                        }

                        B_P_points += 5;

                        // if we find a king ckecker
                        if (0 != (gameState.get(i) & Constants.CELL_KING)) {
                             B_K_points += 7;

                        }
                    }

            }
            if (player == 1) {
                final_score += A_K_points;
                final_score += A_P_points;
                final_score -= B_K_points;
                final_score -= B_P_points;
                return final_score;
            } else {
                final_score += B_K_points;
                final_score += B_P_points;
                final_score -= A_K_points;
                final_score -= A_P_points;

                return final_score;
            }
        }
    }

    //Order states according to their heuristic value
    public class compareHeuristic implements Comparator<GameState> {


        public int compare(GameState sA, GameState sB) {
            return heuristic_function(sB, real_player) - heuristic_function(sA, real_player);
        }


        public boolean equals(GameState sA, GameState sB) {
            return heuristic_function(sB, real_player) == heuristic_function(sA, real_player);
        }
    }

    Vector<GameState> sortDependingOnHeur(Vector<GameState> stateVector) {
        Collections.sort(stateVector, new compareHeuristic());
        return stateVector;

    }


}
