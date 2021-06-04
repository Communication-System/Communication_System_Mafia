import java.util.ArrayList;
import java.util.Random;

// ���õ��� ����� �����ϴ� Ŭ����
public class Randomizer {
	private ArrayList<Integer> nums;
	
	
	public Randomizer() {
		nums = new ArrayList<Integer>();
	}
	
	
	
	// ����Ʈ ���� ��ҵ��� �����ִ� �Լ�
	private void shuffle() {
		Random random = new Random();
		for(int i = 0; i < nums.size() * 2; i ++) {
			int target = (random.nextInt() % nums.size());
			int curr = nums.get(target);
			nums.remove(target);
			int insertTo = (random.nextInt() % (nums.size()));
			nums.add(insertTo, curr);
		}
	}
	
	
	public int[] getNums(){
		shuffle();
		int[] ret = new int[nums.size()];
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
