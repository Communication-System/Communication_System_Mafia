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

public class GameCMConnector {
	public List<String> currentUsers;
	private boolean[] markers; // �� ��Ŀ�� ǥ�õ� �������Դ� Controll All�Լ��� ���޵��� �ʴ´�.
	
	
	public GameCMConnector() {
		currentUsers = new ArrayList<String>();
	}
	
	// ���� �¶��� ������ ������ ���� ��ȯ�Ѵ�.
	public int checkUsersOnline() {
		return currentUsers.size();
	}
	
	// �� �濡 ������ �߰��Ѵ�
	public boolean tryAddUser(String user) {
		if(currentUsers.size() < 5) {
			currentUsers.add(user);
			castGameEnter(user);
			return true;
		}
		return false;
	}
	

	public void broadcastGameEnd(String user) {
		
	}
	
	public void broadcastGameStart(String userList) {
		
		
	}
	
	public void broadcastGameStage(int stage, String userList) {
		
	}
	
	public void broadcastChatData(String data) {
		
	}
	
	public void broadcastUserDie(String user, String how) {
		
	}
	
	public void broadcastNightStart() {
		
	}
	
	public void broadcastNightEnd() {
		
	}
	
	public void castJobInfo(String user, int job) {
		
	}
	
	
	public void castGameEnter(String who) {
		
	}
	
	public void controllChatFunction(String user, boolean enable) {
		
	}
	
	public void controllChatFunctionAll(boolean enable) {
		
	}
	
	public void controllUserSelectFunction(String user, boolean enable) {
		
	}
	
	public void controllUserSelectFunctionAll(boolean enable) {
		
	}
	
	public void controllVoteFunctionAll(boolean enable) {
		
	}
	
	public void markUser(int id, boolean enable) {
		markers[id] = enable;
	}



	
}
