import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.swing.JOptionPane;

import kr.ac.konkuk.ccslab.cm.entity.CMGroup;
import kr.ac.konkuk.ccslab.cm.entity.CMGroupInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMList;
import kr.ac.konkuk.ccslab.cm.entity.CMMember;
import kr.ac.konkuk.ccslab.cm.entity.CMMessage;
import kr.ac.konkuk.ccslab.cm.entity.CMPosition;
import kr.ac.konkuk.ccslab.cm.entity.CMRecvFileInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMSendFileInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMServer;
import kr.ac.konkuk.ccslab.cm.entity.CMSession;
import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMBlockingEventQueue;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMFileEvent;
import kr.ac.konkuk.ccslab.cm.event.CMInterestEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.info.CMCommInfo;
import kr.ac.konkuk.ccslab.cm.info.CMConfigurationInfo;
import kr.ac.konkuk.ccslab.cm.info.CMFileTransferInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMConfigurator;
import kr.ac.konkuk.ccslab.cm.manager.CMFileTransferManager;
import kr.ac.konkuk.ccslab.cm.manager.CMMqttManager;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import kr.ac.konkuk.ccslab.cm.util.CMUtil;

// ���� ���� ��, �Ⱓ���� ������, ���� �κ��� �����Ϸ����� ������� �ʰ�, �ϵ��ڵ� �Ǿ���.
// �ϵ��ڵ��� �κ��� �ּ����� ǥ���߱� ������, ��쿡 ����, �����Ϸ����� �������� �����ָ� �ȴ�.


/*
 * ����ȭ ����
 * 
 * ����ȭ ������ ���� �ذ��ϱ� ���ؼ�, ���ͷ�Ʈ��� ������ ���� �����Ͽ���.
 * 
 * �� Ŭ���� ���忡��, ������� �� 2������� ���� �� �ִ�. 
 * �ϳ��� ���� �������̰�
 * ������ �ϳ���, main�Լ� �����̴�(CM �̺�Ʈ �ڵ鷯)
 * 
 * �̶�, ���ͷ�Ʈ��, main�Լ���, CM�ڵ鸵 ��û�� ������ �ǹ��Ѵ�.
 * �ܺο��� �� Ŭ������ ���� �ٲٴ� ����� setter���ε�, �� getter, setter�� ���ͷ�Ʈ��� �����Ѵ�.
 * ���ͷ�Ʈ�� disable�ϸ�, �� setter�� ����Ǵ���, �� Ŭ������ ������ �� �� ����.
 * 
 * ����, ���ͷ�Ʈ�� disable�� ���¿�����, �ڽ� �����常 ����ִ� �� ó�� �����ϰ� ���α׷��� �ص� ����� ����.
 * setter�� �׻�, ���ͷ�Ʈ Ȱ��/��Ȱ�� �Լ����� ����ȭ�� ����ؾ� �Ѵ�.
 * 
 * */

// �������� ������ ��� ������ ó���ϴ� Ŭ����
public class GameLogicController implements Runnable{
	// Field
	private GameUser[] users = new GameUser[5]; // �� �κ��� �ϵ��ڵ� �Ǿ��ֽ��ϴ�. �̿� ����, ���� ���� �����Ϸ����� �������� �ƴմϴ�.
	private GameCMConnector connector;
	private int alive = 5; // ���� ���� ������ ������ ��
	private volatile int voteCount; // �� ������ ĳ���� ����Ǹ� �ȵȴ�. ĳ�� ����ȭ �̽��� ���ϱ� �����̴�.
	private volatile boolean disabledInturrupt = false; // ����ȭ�� ����, �޸𸮿� �����Ѵ�. ����ȭ�� ���� state-variable�̴�.
	private int targetUser = -1;
	private int mafia, police, doctor;
	private UserSelectedBehaviour currentAction; // ������ ���� �������� request �ڵ鷯
	private boolean exitCondition = false; // ���� ���� ������ �޼��Ǿ��°�?
	private GameSessionCallback collectResource; //
	private int gameExitStatus = -1; // ���� ���� ����
	private int roomid = 0;
	
	private static final int CHAT_VOTE_TIME = 60; // �ʱ� ��ǥ �ð�
	private static final int PROS_CONS_TIME = 20; // ���� ��ǥ �ð�
	private static final int NIGHT_JOB_TIME = 20; // �� �ð��� �� ������ �����ϴ� �ð�
	
	
	private static final int CIVIL_WIN = 0; // �ù� ��
	private static final int MAFIA_WIN = 1; // ���Ǿ� ��
	private static final int SOMEONE_EXIT = 2; // ������ ������, ���� ������ �Ұ���
	
	
	// Constructor
	public GameLogicController(GameCMConnector cm, GameSessionCallback callback, int roomID) {
		connector = cm;
		collectResource = callback;
		roomid = roomID;
		for(int i = 0; i < 5; i ++) {
			users[i] = new GameUser(i);
			users[i].userName = connector.currentUsers.get(i);
		}
		connector.broadcastGameStart(getUserList(0));
	}
	
	
	
	//Methods
	
	
	// �濡 ������ �����鿡�� ������ �Ҵ��Ѵ�.
	private void assignJob() {
		int[] jobs;
		Randomizer randomizer = new Randomizer();
		// ���� ���� ó�� �κ��� �ϵ� �ڵ����� ��ü�Ѵ�.(�ù� : 2, ���Ǿ� : 1, ���� : 1, �ǻ� : 1)
		randomizer.addNumber(0);
		randomizer.addNumber(0);
		randomizer.addNumber(1);
		randomizer.addNumber(2);
		randomizer.addNumber(3);
		jobs = randomizer.getNums();
		
		// �������� �Ҵ���� ������ ����
		for(int i = 0; i < users.length; i ++) {
			users[i].job = jobs[i];
			connector.castJobInfo(users[i].userName, users[i].job); // �� �������� �ش� ������ ������ ���
			switch(i) {
			case 1 :
				mafia = i;
				break;
			case 2 : 
				police = i;
				break;
			case 3:
				doctor = i;
				break;
			default:
			}
		}
		
	}
	
	// �濡 ������ �������� ���¸� Ȯ���Ѵ�.
	private void initForNextTurn() {
		for(int i = 0; i < users.length; i ++) {
			users[i].cleanState();
		}
		targetUser = -1;
	}
	
	// ����ȭ �Լ�, CM���ͷ�Ʈ�� �����Ѵ�.
	private synchronized void disableInturrupt() {
		disabledInturrupt = true;
	}
	
	// ����ȭ �Լ�, CM���ͷ�Ʈ�� �޾Ƶ��δ�.
	private synchronized void enableInturrupt() {
		disabledInturrupt = false;
	}
	
	
	// �ù� ��ǥ�� �����ϴ� �Լ�
	private void civilVotePrepare() {
		connector.controllChatFunctionAll(true);
		connector.controllUserSelectFunctionAll(true);
	}
	
	
	// �ù� ��ǥ�� �����ϴ� �Լ�
	private void civilVoteClose() {
		connector.controllUserSelectFunctionAll(false);
	}
	
	// �ù� ��ǥ ����� �����ϴ� �Լ�
	private void civilVoteResult() {
		int who, score;
		score = 0;
		who = -1; // �ƹ��� ��ǥ�� ���� �ʾҴٸ�, ���� ��ǥ�� ����
		// �� ������ ��ȸ�ϸ�, �ִ� ��ǥ�� �� ������ Ž��
		for(int i = 0 ; i < users.length; i ++) {
			if(users[i].voteCount > score) {
				score = users[i].voteCount;
				who = i;
			}
		}
		this.targetUser = who;
	}
	
	
	// �ù� ��ǥ�� ó���ϴ� �Լ�
	private void civilVoteProcess() throws InterruptedException {
		connector.broadcastGameStage(0, getUserList(1));
		civilVotePrepare();
		enableInturrupt();
		Thread.sleep(1000 * CHAT_VOTE_TIME);
		disableInturrupt();
		civilVoteClose();
		civilVoteResult();
	}
	
	
	// ���� ��ǥ�� �����ϴ� �Լ�
	private void civilProsConsPrepare() {
		connector.controllVoteFunctionAll(true, users[targetUser].userName);
	}
	
	
	// ���� ��ǥ�� �����ϴ� �Լ�
	private void civilProsConsClose() {
		connector.controllVoteFunctionAll(false, "END");
		connector.controllChatFunctionAll(false);
	}
	
	// ���� ��ǥ ����� �����ϴ� �Լ�
	private void civilProsConsResult() {
		if(voteCount > alive / 2) {
			killPlayer(targetUser);
		}
	}
	
	// ���� ��ǥ�� �����ϴ� �Լ�
	private void civilProsConsProcess() throws InterruptedException {
		if(targetUser == -1){
			return;
		}
		civilProsConsPrepare();
		enableInturrupt();
		Thread.sleep(1000 * PROS_CONS_TIME);
		disableInturrupt();
		civilProsConsClose();
		civilProsConsResult();
	}
	
	
	private int checkAlivePlayer() {
		int ret = 0;
		for(int i = 0; i < users.length; i ++) {
			if(users[i].alive && users[i].job != 1) {
				ret ++;
			}
		}
		return ret;
	}
	
	// �ش� id�� �÷��̾ ��� ó���ϴ� �Լ�
	private void killPlayer(int id) {
		if(!users[id].isProtected) {
			users[id].alive = false;
			connector.markUser(id, false);
			connector.controllChatFunction(users[id].userName, false);
			connector.broadcastUserDie(users[id].userName, null);
		}
		if(id == mafia) {
			gameExitStatus = CIVIL_WIN;
			exitCondition = true;
			connector.broadcastGameEnd(0);
		}
		if(checkAlivePlayer() < 2) {
			gameExitStatus = MAFIA_WIN;
			exitCondition = true;
			connector.broadcastGameEnd(1);
		}
	}
	
	private void nightAction() throws InterruptedException{
		connector.broadcastGameStage(1, getUserList(1));
		doMafiaJob();
		doPoliceJob();
		doDoctorJob();
		civilVoteResult();
		if(targetUser != -1) {
			if(!users[targetUser].isProtected) {
				killPlayer(targetUser);
			}
		}
	}
	
	private void doMafiaJob() throws InterruptedException{
		connector.broadcastGameStage(2, getUserList(1));
		connector.controllUserSelectFunction(users[mafia].userName, true);
		currentAction = new MafiaSelectBehaviour();
		enableInturrupt();
		Thread.sleep(1000 * NIGHT_JOB_TIME);
		disableInturrupt();
		connector.controllUserSelectFunction(users[mafia].userName, false);
	}
	
	private void doDoctorJob() throws InterruptedException{
		connector.broadcastGameStage(3, getUserList(1));
		connector.controllUserSelectFunction(users[doctor].userName, true);
		currentAction = new DoctorSelectBehaviour();
		enableInturrupt();
		Thread.sleep(1000 * NIGHT_JOB_TIME);
		disableInturrupt();
		connector.controllUserSelectFunction(users[doctor].userName, false);
	}
	
	private void doPoliceJob() throws InterruptedException{
		connector.broadcastGameStage(4, getUserList(1));
		connector.controllUserSelectFunction(users[police].userName, true);
		currentAction = new PoliceSelectBehaviour();
		enableInturrupt();
		Thread.sleep(1000 * NIGHT_JOB_TIME);
		disableInturrupt();
		connector.controllUserSelectFunction(users[police].userName, false);
	}
	
	
	private int tryFindUser(String user) {
		for(int i = 0; i < users.length; i ++) {
			if(users[i].userName.equals(user)) {
				return i;
			}
		}
		return -1;
	}
	
	public synchronized void adjustVoteCount(String user, boolean pros) {
		if(!disabledInturrupt) {
			int subject = tryFindUser(user);
			if(subject == -1) {
				return;
			}
			if(!users[subject].isVoted) {
				users[subject].isVoted = true;
				if(pros) {
					voteCount ++;
				}
			}
		}
	}
	
	public synchronized void processUserSelected(String who, String target) {
		if(!disabledInturrupt) {
			currentAction.whenUserSelected(tryFindUser(target), tryFindUser(who), users, connector);
		}
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		assignJob();
		try {
			while (!exitCondition) {
				civilVoteProcess();
				if(exitCondition) {continue;}
				
				civilProsConsProcess();
				if(exitCondition) {continue;}
				
				nightAction();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		collectResource.requestCollectGameThread(-1, gameExitStatus);
		
	}
	
	private String getUserList(int type) {
		String ret = "";
		switch(type) {
		case 0:
			for(int i = 0; i < 5; i ++) {
				ret += users[i].userName;
				ret += "/";
			}
			return ret;
		case 1:
			for(int i = 0; i < 5; i ++) {
				if(users[i].alive) {
					ret += users[i].userName;
					ret += "/";
				}

			}
			return ret;
		}
		return ret;
	}
}
