package com.baijiahulian.live.ui.chat;

import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.models.LPDataModel;
import com.baijiahulian.livecore.models.imodels.IMessageModel;
import com.baijiahulian.livecore.models.imodels.IUserModel;

import java.util.Date;

/**
 * Created by Shubo on 2017/5/6.
 */

class UploadingImageModel extends LPDataModel implements IMessageModel {

    static final int STATUS_UPLOADING = 0;
    static final int STATUS_UPLOAD_FAILED = 1;

    private String filePath;

    private IUserModel self;

    private int status = STATUS_UPLOADING;

    UploadingImageModel(String filePath, IUserModel self) {
        this.filePath = filePath;
        this.self = self;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String getContent() {
        return null;
    }

    @Override
    public IUserModel getFrom() {
        return self;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getChannel() {
        return null;
    }

    @Override
    public LPConstants.MessageType getMessageType() {
        return LPConstants.MessageType.Image;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getUrl() {
        return filePath;
    }

    @Override
    public int getImageWidth() {
        return 0;
    }

    @Override
    public int getImageHeight() {
        return 0;
    }

    @Override
    public Date getTimestamp() {
        return null;
    }
}
