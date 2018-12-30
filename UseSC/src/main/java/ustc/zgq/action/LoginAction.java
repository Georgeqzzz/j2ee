package ustc.zgq.action;

public class LoginAction {
	String username ,  passwd;
	public String login(String req) {
		//解析请求中username,passwd
		//do login
		if(identify()) {
			return "success";
		}
		else return "failure";	
	}
	public String logout(String req) {
		//解析请求
		//do logout
		return "success";
	}
	private boolean identify() {
		//check the username and psaawd
		return true;
	}
}
