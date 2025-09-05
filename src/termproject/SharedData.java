package termproject;

import java.io.Serializable;
import java.util.Arrays;


public class SharedData implements Serializable 
{
	// index 0: for player1
	// index 1: for player2
	public static final int P1 = 0;
	public static final int P2 = 1;
	
	public int whoami;
	String[] current_target_word;
	public int[][] correct_character_index;
	public int[][] sum; 
	
	SharedData(){
		correct_character_index = new int[2][];
		current_target_word = new String[2];
		sum = new int[2][2]; // [P1_correct, P1_wrong]
		sum[P1] = new int[] {0,0};
		sum[P2] = new int[] {0,0};
	}
	
	public String toString() {
		String introduce = String.format("this is Player %d... answer : %s\n", whoami,current_target_word[whoami]);
		String correct_state1 = String.format("%19s %s\n", "current correct(1):", Arrays.toString(correct_character_index[P1]));
		String correct_state2 = String.format("%19s %s\n", "current correct(2):", Arrays.toString(correct_character_index[P2]));
		String sum1 = String.format("%19s correct = %d, wrong = %d\n", "sum(1):", sum[P1][0], sum[P1][1]);
                String sum2 = String.format("%19s correct = %d, wrong = %d\n", "sum(2):", sum[P2][0], sum[P2][1]);
		
		return "\n" + introduce + correct_state1 + sum1 + correct_state2 + sum2;
	}
}