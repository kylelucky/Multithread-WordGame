package termproject;

import java.net.*;
import java.util.Arrays;
import java.io.*;

public class Server extends Thread
{
	public boolean state_changed;
	public SharedData shared_data;
	public ObjectOutputStream[] os;
	
	Server(){
		state_changed = false;
		shared_data = new SharedData();
		os = new ObjectOutputStream[2];
	}
	
	public synchronized void run() {
		while(true) {
			while(!state_changed) {
				try {
					System.out.printf("%-10s Server is going to sleep, waiting for state chage...\n", "[Server]:");
					wait();
					System.out.printf("%-10s Server has been awaken and goingto send!\n", "[Server]:");
				} catch (InterruptedException e) {e.printStackTrace(System.out);};
			}
			
			try {
				for(int i=0; i<2; i++) {
					if(os[i] != null) {
						shared_data.whoami = i;
						os[i].writeObject(shared_data);
						os[i].reset();
						System.out.printf("%-10s Server has sent to Client %d\n", "[Server]:", i);
					}
				}
				state_changed = false;
			} catch(IOException e) {e.printStackTrace(System.out);}
			
		}
	}
	
	public synchronized void notifyStateChanged() {
	    state_changed = true;
	    notify();
	}
	
	public static void main(String[] args) {
		String fileName = "words.txt"; // 파일 이름을 적절히 변경하세요
        String[][] lists = new String[2][5];
        
        try {
        	BufferedReader br = new BufferedReader(new FileReader(fileName));
            
            // 첫 번째 리스트 읽기
        	for(int P1P2 = 0; P1P2<2; P1P2++) {
        		br.readLine();
                for (int i = 0; i < 5; i++) {
                    lists[P1P2][i] = br.readLine();
                }
        	}
            
            br.close();
        } catch (FileNotFoundException e) {e.printStackTrace();
        } catch (IOException e) {e.printStackTrace();}
		
		Server server = new Server();
		server.start();
		System.out.printf("%-10s Server starts...\n", "[main]:");
		
		try {
			ServerSocket s = new ServerSocket(8189);
			System.out.printf("%-10s Server Socket Generated...\n", "[main]:");
			
			int P1P2 = 0;
			while(P1P2 < 2) { // 2명의 플레이어가 만들어지는 것을 계속 감지
				Socket incoming = s.accept();
				System.out.printf("%-10s client%d accepted\n", "[main]:", P1P2);
				new ThreadedClient(incoming, P1P2, server, lists[P1P2]).start();
				System.out.printf("%-10s Thread%d starts\n", "[main]:", P1P2);
				
				server.shared_data.whoami = P1P2;
				server.os[P1P2] = new ObjectOutputStream(incoming.getOutputStream());
				server.os[P1P2].writeObject(server.shared_data);
				P1P2++;
			}
		} catch (Exception e) {e.printStackTrace(System.out);}
	}
}

class ThreadedClient extends Thread
{
	private Server server;
	private Socket incoming;
	
	private int id;
	private String[] target_strings;
	private int string_idx;
	private SharedData shared_data;
	int[] correct_character_index;
	String old_string;
	
	ThreadedClient(Socket i, int P1P2, Server s, String[] a){
		incoming = i;
		server = s;
		id = P1P2;
		shared_data = s.shared_data;
		target_strings = a; // 맞춰야하는 문자열들
		string_idx = 0; // 정답 문자열을 선택하기 위해
	}
	
	public synchronized void run() {
		try {
			String answer = target_strings[string_idx];
			shared_data.current_target_word[id] = answer;
			old_string = "";
			
			server.notifyStateChanged();
			
			char guess;
			BufferedReader in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
			
			while(true) { // 클라이언트로부터 데이터가 들어오는 것을 계속감지
	        	
	        	answer = target_strings[string_idx];
	        	shared_data.current_target_word[id] = answer;
	        	System.out.printf("%-10s Player %d should guess : %s\n", "[Thread]: ", id, answer);
	        	
	        	// first try check
	        	if(old_string != answer) {
	        		shared_data.correct_character_index[id] = new int[answer.length()];
	        	}
	        	old_string = answer;
	        	
	        	while ((guess = (char)in.read()) != '\n' ) {
		        	System.out.printf("%-10s Socket %d received : \'%c\'\n", "[Thread]: ", id, guess);
		        	
	        		boolean is_correct = false;
	        		
	        		int[] old_correct_index = new int[answer.length()];
	        		for(int idx=0; idx < answer.length(); idx++) {
		        		old_correct_index[idx] = shared_data.correct_character_index[id][idx];
	        		}
	        		
	        		for(int idx=0; idx < answer.length(); idx++) {
		        		if(guess == answer.charAt(idx)) {
		        			is_correct = true;
		        			shared_data.correct_character_index[id][idx] = 1;
		        		}
		        	}
	        		
	        		if(!is_correct) {
	        			System.out.printf("%-10s Client %d Wrong\n", "[Tread]:", id);
	        			shared_data.sum[id][1]++;
	        		}
	        		else {
	        			System.out.printf("%-10s Client %d correct\n", "[Tread]:", id);
	        			System.out.printf("%-10s\n\told:\n\t%s\n\tnew:\n\t%s\n", "[Thread]:", Arrays.toString(old_correct_index), Arrays.toString(correct_character_index));
	        			int current_increment = 0;
	             		for(int idx=0; idx < answer.length(); idx++) {
			        		if(shared_data.correct_character_index[id][idx] == 1) {
			        			if(shared_data.correct_character_index[id][idx] != old_correct_index[idx]) { 
			        				current_increment++;
			        			}
			        		}
			        	}
	             		shared_data.sum[id][0] += current_increment;
	        		}
	        		
	        		boolean is_allright = true;
	        		for(int idx=0; idx<answer.length();idx++) {
	        			if(shared_data.correct_character_index[id][idx] == 0) is_allright = false; 
	        		}
	        		if(is_allright) {
	        			string_idx++;
	        			string_idx = (string_idx >= target_strings.length) ? 0 : string_idx;
	        		}
	        		server.notifyStateChanged();
	            }
	        }
		} catch (Exception e) {e.printStackTrace(System.out);}
	}
}