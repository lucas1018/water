package cn.zerone.water.model;

import com.alibaba.fastjson.JSON;

public class EngineeringStation {
    int engineeringStationId;
    int taskId;
    int stepId;
    String workName;
    String taskName;
    String stepName;

    public EngineeringStation() {
    }

    @Override
    public String toString() {
        return  JSON.toJSONString(this);
    }

    public EngineeringStation(int engineeringStationId, int taskId, int stepId, String workName, String taskName, String stepName) {
        this.engineeringStationId = engineeringStationId;
        this.taskId = taskId;
        this.stepId = stepId;
        this.workName = workName;
        this.taskName = taskName;
        this.stepName = stepName;
    }

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

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }
}