import kr.ac.konkuk.ccslab.cm.entity.CMMember;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;

import kr.ac.konkuk.ccslab.cm.stub.*;

public class RoomController {
	
	//private ClientController u_Event = new ClientController();
//	private CMClientStub clientStub;

	public RoomController() {
//		clientStub = new CMClientStub();
	}
	
	public String makeMsg(String args) {
		return UserController.getInstance().getRoomId() + "|" + UserController.getInstance().getId() + "|" + args;
	}
	
	public void tryEnterRoom(){
		String msg = makeMsg("try");
		sendDummyEvent("17", msg);
	}
	public void enterRoom(String userId, String arg) {
//		UserController.getInstance().enterRoom(arg);
		FrameController.getInstance().init_room_frame(arg);
	}
	public void exitRoom() {
		UserController.getInstance().exitRoom();
	}
	public void showUserList() {
		getSessionMember();//session member 揶쏉옙占쎌죬占쏙옙占쎄퐣 癰귣똻肉т틠�눊由�
	}
	
	public void sendDummyEvent(String opcode, String msg) {
		System.out.println("====== DummyEvent send to default server");
		CMDummyEvent due = new CMDummyEvent();
		due.setSender(CMClientStub.getInstance().getCMInfo().getInteractionInfo().getMyself().getName());
		due.setDummyInfo(opcode+"|"+msg);
		CMClientStub.getInstance().send(due, "SERVER");
		System.out.println(due.getDummyInfo());
	}
	
	public void getSessionMember() {
		System.out.print("====== print group members\n");
		CMMember groupMembers = CMClientStub.getInstance().getGroupMembers();
		if(groupMembers == null || groupMembers.isEmpty())
		{
			System.err.println("No group member yet!");
			return;
		}
		System.out.print(groupMembers.toString()+"\n");
	}
	
}
