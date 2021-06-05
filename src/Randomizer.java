import java.util.ArrayList;
import java.util.Random;

// ���õ��� ����� �����ϴ� Ŭ����
public class Randomizer {
	private ArrayList<Integer> nums;
	
	
	public Randomizer() {
		nums = new ArrayList<Integer>();
	}
	
	
	private int getRand() {
		Random rand = new Random();
		int uid = rand.nextInt();
		if(uid < 0) {
			uid = -uid;
		}
		return uid  % nums.size();
	}
	
	
	// ����Ʈ ���� ��ҵ��� �����ִ� �Լ�
	private void shuffle() {
		Random random = new Random();
		for(int i = 0; i < nums.size() * 2; i ++) {
			int target = (getRand());
			int curr = nums.get(target);
			nums.remove(target);
			int insertTo = (getRand());
			nums.add(insertTo, curr);
		}
	}
	
	
	public int[] getNums(){
		shuffle();
		int[] ret = new int[nums.size()];
		System.out.println("NumSize : " + nums.size());
		for(int i = 0; i < nums.size(); i ++) {
			ret[i] = nums.get(i);
		}
		return ret;
	}
	
	
	public int addNumber(int number) {
		nums.add(number);
		return nums.size();
	}
	
	public void init() {
		nums = new ArrayList<Integer>();
	}
}
