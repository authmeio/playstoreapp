package io.authme.home.history;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shardullavekar on 13/04/17.
 */

public class PendingData {
    private String status;
    private String timestamp;
    private String displayname;
    private String referenceId;
    private String icon;
    private String comment;

    public PendingData(JSONObject data) {
        try {
            this.status = data.getString("Status");
            this.timestamp = data.getString("CreatedAt");
            this.displayname = data.getString("DisplayName");
            this.referenceId = data.getString("ReferenceId");
            this.icon = data.getString("Icon");
            this.comment = data.getString("Comment");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getDisplayname() {
        return displayname;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public String getIcon() {
        return icon;
    }

    public String getComment() {
        return comment;
    }

    public String getStatus() {
        return status;
    }
}
