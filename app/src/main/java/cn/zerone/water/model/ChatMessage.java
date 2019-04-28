package cn.zerone.water.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.ArrayList;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
@Entity
public class ChatMessage
{
	@Id(autoincrement = true)
	private  Long id;
    private transient String headImg;
	private transient String userName;
	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	private String content;
	private long createDate;
	private boolean isComMeg;
	private int type;
	int engineeringStationId;
	int taskId;

	public int getEngineeringStationId() {
		return engineeringStationId;
	}

	public void setEngineeringStationId(int engineeringStationId) {
		this.engineeringStationId = engineeringStationId;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public int getStepId() {
		return stepId;
	}

	public void setStepId(int stepId) {
		this.stepId = stepId;
	}

	int stepId;
	public boolean isComMeg() {
		return isComMeg;
	}

	public void setComMeg(boolean comMeg) {
		isComMeg = comMeg;
	}
	public final static int RECIEVE_MSG = 0;
	public final static int SEND_MSG = 1;
	public final static int TYPE_TEXT = 1;
	public final static int TYPE_IMAGE =2;
	public final  static int TYPE_VIDEO = 3;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	public ChatMessage(String headImg, String userName,String userId, String content, long createDate, boolean isComMeg, int type ) {
		this.headImg = headImg;
		this.userName = userName;
		this.userId = userId;
		this.content = content;
		this.createDate = createDate;
		this.isComMeg = isComMeg;
		this.type = type;
	}

	public ChatMessage() {
	}

	public ChatMessage(Long id, String headImg, String userName,String userId, String content, long createDate, boolean isComMeg,
			int type) {
		this.id = id;
		this.headImg = headImg;
		this.userName = userName;
		this.userId = userId;
		this.content = content;
		this.createDate = createDate;
		this.isComMeg = isComMeg;
		this.type = type;
	}
	@Generated(hash = 1677770737)
	public ChatMessage(Long id, String userId, String content, long createDate, boolean isComMeg, int type, int engineeringStationId,
			int taskId, int stepId) {
		this.id = id;
		this.userId = userId;
		this.content = content;
		this.createDate = createDate;
		this.isComMeg = isComMeg;
		this.type = type;
		this.engineeringStationId = engineeringStationId;
		this.taskId = taskId;
		this.stepId = stepId;
	}

	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getContent() {
		return this.content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getCreateDate() {
		return this.createDate;
	}
	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}
	public boolean getIsComMeg() {
		return this.isComMeg;
	}
	public void setIsComMeg(boolean isComMeg) {
		this.isComMeg = isComMeg;
	}

	public String getHeadImg() {
		return this.headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}


}