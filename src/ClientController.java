
import java.io.IOException;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import kr.ac.konkuk.ccslab.cm.entity.CMMember;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

public class ClientController {
	
//	private CMClientStub clientStub;
	private ClientControllerEventHandler clientEventHandler;
	
	private boolean m_bRun;
	// GameController gc;
	// RoomController rc;
	// UserController uc;
			
	public ClientController() {
//		clientStub = new CMClientStub();
		clientEventHandler = new ClientControllerEventHandler(CMClientStub.getInstance(),this);
	
		CMClientStub.getInstance().setAppEventHandler(clientEventHandler);
		//CM 珥덇린�솕 諛� �떆�옉  
		m_bRun = CMClientStub.getInstance().startCM(); 
		if(!m_bRun) {

			System.err.println("CM initialization error!");
			return;
		}else {
			System.out.println("CM initialization success!");	
		}
		
	}
	
	public CMClientStub getClientStub() {
		return CMClientStub.getInstance();
	}
	
	public ClientControllerEventHandler getClientEventHandler() {
		return clientEventHandler;
	}
	
		

	 /* Methods */

  /*  Functionality :
      CM 醫낅즺 �븿�닔  
      
  *   Parameters : void
  *   Return values : void
  * */
	public void terminateCM() {
		CMClientStub.getInstance().terminateCM();
		m_bRun = false;
	}
	 /* Methods */

    /*  Functionality : 
        濡쒓렇�씤 �븿�닔
          
    *   Parameters : void
    *   Return values : void
    * */	
	public boolean loginCM(String strUserID,  String strPassword) throws IOException {
		
		//String strUserID = "null";
		//String strPassword = "null";
	
		boolean bRequestResult = false;
		
		//Object[] message = {"User Name: ", id, "Password", pw};
		
		 		
		if(strUserID.equals("")) {
			System.out.println("Please enter your ID");

 		}else if(strPassword.equals("")){
 			System.out.println("Please enter your PassWord ");
		}
		
		
		
		bRequestResult = CMClientStub.getInstance().loginCM(strUserID, strPassword);
			
		if(bRequestResult) {
			System.out.println("successfully sent the login request.");
			UserController.getInstance().setId(strUserID);
			UserController.getInstance().setPassword(strPassword);
		}
		else {
			System.err.println("failed the login request!");
		}
			
		return bRequestResult;
			
}
	
		
	

	 /* Methods */

   /*  Functionality :
         �쉶�썝 媛��엯 �븿�닔
         ID, PWD , �솗�씤�슜 PWD �엯�젰 諛쏆쓬 
         
         
   *   Parameters : void
   *   Return values : void
   * */		
	public void singUpCM(String strUserID,String strPassword, String strRePassword) throws IOException {

		//String strUserID = "null";
		//String strPassword = "null";
		
		//Object[] message = {"User Name: ", id, "Password", pw, "RePassWord" , repw};
		
		if(strUserID.equals("")) {
			System.out.println("Please enter your ID");

 		}else if(strPassword.equals("")){
 			System.out.println("Please enter your PassWord ");
		}else if(strRePassword.equals("")) {
 			System.out.println("Please enter your RePassWord ");
		}
		
		if(!strPassword.equals(strRePassword)) {
			System.err.println("Password input error");
			return;
		}
		
		CMClientStub.getInstance().registerUser(strUserID, strPassword);
		
}


	 /* Methods */

  /*  Functionality :
      濡쒓렇�븘�썐 �븿�닔
  *   Parameters : void
  *   Return values : void
  * */
	public void logoutCM() {
		
		boolean bRequestResult = false;
		System.out.println("====== logout from default server");
		bRequestResult = CMClientStub.getInstance().logoutCM();
		if(bRequestResult)
			System.out.println("successfully sent the logout request.");
		else
			System.err.println("failed the logout request!");
		System.out.println("======");
	
	}
	

	 /* Methods */

   /*  Functionality :
       �쁽�옱 �꽭�뀡�뿉 議댁옱�븯�뒗 �궗�엺 由ъ뒪�듃 議고쉶 �븿�닔 

   *   Parameters : void
   *   Return values : void
   * */
	public String getSessionMember() {
		System.out.print("====== print group members\n");
		CMMember groupMembers = CMClientStub.getInstance().getGroupMembers();
		if(groupMembers == null || groupMembers.isEmpty())
		{
			System.err.println("No group member yet!");
			return " ";
		}
		System.out.print(groupMembers+"\n");
		
//		System.out.print(groupMembers.toString()+"\n");
		String member = groupMembers.toString();
		
		return member;
		
//		String[] mem = member.split(" ");
//		System.out.println("mem 0 : " + mem[0]);
//		System.out.println("aaa" + CMClientStub.getInstance().getGroupMembers().getMemberNum());
		//System.out.println("mem 1 : " + mem[1]);
    	//String[] memberList = mem[1].split(" ");
//		
//		Friends_List = new JList(new DefaultListModel());
//    	friend_model = (DefaultListModel)Friends_List.getModel();
		
//		FrameController.getInstance().main_frame.Friends_List = new JList(new DefaultListModel());
//		FrameController.getInstance().main_frame.friend_model = (DefaultListModel)FrameController.getInstance().main_frame.Friends_List.getModel();

//    	for(int i = 0; i < CMClientStub.getInstance().getGroupMembers().getMemberNum(); i++) {
//    		FrameController.getInstance().main_frame.friend_model.addElement(mem[i]);
//    	}

	}
	

	
	
	
	 /* Methods */

  /*  Functionality :
      DummyEvent �쟾�넚 �븿�닔
      
      InfoType :  "opcode|roomID|userName|args" 
      
      
      
  *   Parameters : void
  *   Return values : void
  * */
	
//	public void sendDummyEvent(String opcode, String msg) {
//		System.out.println("====== DummyEvent send to default server");
//		CMDummyEvent due = new CMDummyEvent();
//		due.setSender(CMClientStub.getInstance().getCMInfo().getInteractionInfo().getMyself().getName());
//		due.setDummyInfo(opcode+"|"+msg);
//		CMClientStub.getInstance().send(due, "SERVER");
//		System.out.println(due.getDummy5Info());
//	}

	

	
	 /* Methods */

 /*  Functionality :
     �꽭�뀡 �젙蹂� 異쒕젰 �븿�닔
     
 *   Parameters : void
 *   Return values : void
 * */
	
	public void SessionInfo()
	{
		boolean bRequestResult = false;
		System.out.println("====== request session info from default server");
		bRequestResult = CMClientStub.getInstance().requestSessionInfo();
		if(bRequestResult)
			System.out.println("successfully sent the session-info request.");
		else
			System.err.println("failed the session-info request!");
		System.out.println("======");
	}
	
	
	
	
	public static void main(String[] args) {	
		ClientController client = new ClientController();
//		client.clientStub.setAppEventHandler(client.clientEventHandler);
//		
		Login_Frame login_frame = new Login_Frame(client);
	
				
	}
	
}