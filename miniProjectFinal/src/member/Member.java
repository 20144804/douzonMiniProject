package member;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Member implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8047473608469177098L;
	private String uid;
	private String pwd;
	private String name;
	private String sex;
	private String address;
	private String phone;
	private LocalDateTime loginDateTime;
	
	public Member(String uid, String pwd, String name, String sex, String address, String phone) {
		super();
		this.uid = uid;
		this.pwd = pwd;
		this.name = name;
		this.sex = sex;
		this.address = address;
		this.phone = phone;
		this.loginDateTime = null;
	}
	
	public Member(JSONObject jsonObject) {
		uid = jsonObject.getString("uid");
		pwd = jsonObject.getString("pwd");
		name = jsonObject.getString("name");
		sex = jsonObject.getString("sex");
		address = jsonObject.getString("address");
		phone = jsonObject.getString("phone");
        loginDateTime = null;
	}
	
	public JSONObject getJsonObject() {
        JSONObject jsonMember = new JSONObject();
        jsonMember.put("uid", uid);
        jsonMember.put("pwd", pwd);
        jsonMember.put("name", name);
        jsonMember.put("sex", sex);
        jsonMember.put("address", address);
        jsonMember.put("phone", phone);
        return jsonMember;
    }
}