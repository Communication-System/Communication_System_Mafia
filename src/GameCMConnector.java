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

	public void broadcastGameEnd(int status) {
		UserGameEventInfo event = new UserGameEventInfo();
		event.args = "null";
		event.opcode = 11 + status;
		event.roomID = -1;
		event.userName = "Server";
		sendToAll(event);
	}
	
	public void broadcastGameStart(String userList) {
		UserGameEventInfo event = new UserGameEventInfo();
		event.args = userList;
		event.opcode = 0;
		event.roomID = -1;
		event.userName = "Server";
		sendToAll(event);
	}
	
	public void broadcastGameStage(int stage, String userList) {
		UserGameEventInfo event = new UserGameEventInfo();
		event.args = userList;
		event.opcode = 22 + stage;
		event.roomID = -1;
		event.userName = "Server";
		sendToAll(event);
	}
	
	public void broadcastChatData(String data) {
		UserGameEventInfo event = new UserGameEventInfo();
		event.args = data;
		event.opcode = 3;
		event.roomID = -1;
		event.userName = "Server";
		sendToAll(event);
	}
	
	public void broadcastUserDie(String user, String how) {
		UserGameEventInfo event = new UserGameEventInfo();
		event.args = user;
		event.opcode = 8;
		event.roomID = -1;
		event.userName = "Server";
		sendToAll(event);
	}
	
	
	public void castJobInfo(String user, int job) {
		UserGameEventInfo event = new UserGameEventInfo();
		event.args = job + "";
		event.opcode = 1;
		event.roomID = -1;
		event.userName = "Server";
		sendEvent(event, user);
	}
	
	
	public void castGameEnter(String who) {
		UserGameEventInfo event = new UserGameEventInfo();
		event.args = who;
		event.opcode = 18;
		event.roomID = -1;
		event.userName = "Server";
		sendEvent(event, who);
	}
	
	public void controllChatFunction(String user, boolean enable) {
		UserGameEventInfo event = new UserGameEventInfo();
		event.args = user;
		if(enable) {
			event.opcode = 4;
		}else {
			event.opcode = 5;
		}
		event.roomID = -1;
		event.userName = "Server";
		sendEvent(event, user);
	}
	
	public void controllChatFunctionAll(boolean enable) {
		UserGameEventInfo event = new UserGameEventInfo();
		event.args = "null";
		if(enable) {
			event.opcode = 4;
		}else {
			event.opcode = 5;
		}
		event.roomID = -1;
		event.userName = "Server";
		sendToAll(event);
	}
	
	public void controllUserSelectFunction(String user, boolean enable) {
		UserGameEventInfo event = new UserGameEventInfo();
		event.args = user;
		if(enable) {
			event.opcode = 9;
		}else {
			event.opcode = 10;
		}
		event.roomID = -1;
		event.userName = "Server";
		sendEvent(event, user);
	}
	
	public void controllUserSelectFunctionAll(boolean enable) {
		UserGameEventInfo event = new UserGameEventInfo();
		event.args = "null";
		if(enable) {
			event.opcode = 9;
		}else {
			event.opcode = 10;
		}
		event.roomID = -1;
		event.userName = "Server";
		sendToAll(event);
	}
	
	public void controllVoteFunctionAll(boolean enable, String who) {
		UserGameEventInfo event = new UserGameEventInfo();
		event.args = who;
		if(enable) {
			event.opcode = 14;
		}else {
			event.opcode = 15;
		}
		event.roomID = -1;
		event.userName = "Server";
		sendToAll(event);
	}
	
	public void markUser(int id, boolean enable) {
		markers[id] = enable;
	}

	
	private void sendToAll(UserGameEventInfo event) {
		for(int i = 0 ; i < currentUsers.size(); i ++) {
			sendEvent(event, currentUsers.get(i));
		}
	}
	
	private void sendEvent(UserGameEventInfo event, String to) {
		CMGameGateway.getInstance().sendGameEvent(event, to);
	}

	
}
