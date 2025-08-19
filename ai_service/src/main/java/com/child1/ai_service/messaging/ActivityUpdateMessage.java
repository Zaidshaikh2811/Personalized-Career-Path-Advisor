package com.child1.ai_service.messaging;

import com.child1.ai_service.model.Activity;

public class ActivityUpdateMessage {
    private String activityId;
    private Activity activity;
    private String action;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}

