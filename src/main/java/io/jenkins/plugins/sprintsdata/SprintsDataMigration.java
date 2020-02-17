package io.jenkins.plugins.sprintsdata;

import com.cloudbees.hudson.plugins.folder.Folder;
import hudson.EnvVars;
import hudson.matrix.MatrixConfiguration;
import hudson.matrix.MatrixRun;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.RunList;
import io.jenkins.plugins.sprints.RequestClient;
import io.jenkins.plugins.util.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *@author selvavignesh.m
 * @version 1.0
 */
public class SprintsDataMigration {
    private static final Logger LOGGER = Logger.getLogger(SprintsDataMigration.class.getName());

    private List<Item> itemList;
    private String  header, portal, accessToken;
   private Map<String, Object> requestDataMap = new HashMap<>();
    private Map<String, String> requestHeaderMap = new HashMap<>();

    /**
     *
     * @param fromitemList All items to be migrate
     * @param fromportal portal url
     * @param header admin mail id
     */
    public SprintsDataMigration(final List<Item> fromitemList,  final String fromportal, final String header, final String accessToken) {
        this.itemList = fromitemList;
        this.portal = fromportal;
        this.header = header;
        this.accessToken = accessToken;
    }

    /**
     *
     */
    public void run() {
        //requestDataMap.put("zapikey", integ);
        requestDataMap.put("action", "migration");
        requestHeaderMap.put("X-ZS-JENKINS-ID",header);
        requestHeaderMap.put("Authorization", "Zoho-oauthtoken "+accessToken);
        LOGGER.info(String.valueOf(itemList.size()));
        if(itemList.size() == 0){
            LOGGER.log(Level.INFO,"There is no existing Item. So, skipping migration");
            return;
        }
        JSONArray arr = new JSONArray();
        for (Item itemObj : itemList) {
            JSONArray jobArr = new JSONArray();
            String name = itemObj.getFullName();
            if (itemObj instanceof MatrixConfiguration) {
                continue;
            } else if (itemObj instanceof Folder) {
                arr.put(new JSONObject().put("name", name).put("build", jobArr));
                if (arr.length() == 10) {
                    requestDataMap.put("jenkinsdata", arr.toString());
                    makeCall(requestDataMap);
                    //LOGGER.info(arr.toString());
                    arr = new JSONArray();
                }
                continue;
            }
            Collection<? extends Job> jobCollection = itemObj.getAllJobs();
            int lastFailedNum = -1, lastNumber = -1, lastSuccessNum = -1;
            for (Job<?, ?> job : jobCollection) {

                Run<?, ?> lastBuildRun = job.getLastBuild();
                Run<?, ?> lastFailRun = job.getLastFailedBuild();
                Run<?, ?> lastsuccessRun = job.getLastSuccessfulBuild();
                if (lastBuildRun != null) {
                    lastNumber = lastBuildRun.getNumber();
                }
                if (lastFailRun != null) {
                    lastFailedNum = lastFailRun.getNumber();
                }
                if (lastsuccessRun != null) {
                    lastSuccessNum = lastsuccessRun.getNumber();
                }
                RunList<?> list = job.getBuilds();
                Iterator<?> listItr = list.iterator();
                while (listItr.hasNext()) {
                    Run<?, ?> run = (Run<?, ?>) listItr.next();
                    if (run instanceof MatrixRun) {
                        continue;
                    }
                    int number = run.getNumber();

                    JSONObject buildJson = getMigrationJSONObject(run, name);
                    if (lastNumber == lastFailedNum && lastNumber == number) {
                        buildJson.put("lastbuild", true);
                        buildJson.put("lastfail", true);
                    } else if (lastNumber == lastSuccessNum && lastNumber == number) {
                        buildJson.put("lastbuild", true);
                        buildJson.put("lastsuccess", true);
                    } else if (lastNumber == number) {
                        buildJson.put("lastbuild", true);
                    } else if (lastFailedNum == number) {
                        buildJson.put("lastfail", true);
                    } else if (lastSuccessNum == number) {
                        buildJson.put("lastsuccess", true);
                    }
                    jobArr.put(buildJson);
                }
            }

            arr.put(new JSONObject().put("name", name).put("build", jobArr));
            if (arr.length() == 10) {
                requestDataMap.put("jenkinsdata", arr.toString());
               // LOGGER.info(arr.toString());
                makeCall(requestDataMap);
                arr = new JSONArray();
            }
        }
        if (arr.length() != 0) {
            requestDataMap.put("jenkinsdata", arr.toString());
           //LOGGER.info(arr.toString());
            makeCall(requestDataMap);
        }
        requestDataMap.put("action","migrationstatus");
        requestDataMap.remove("jenkinsdata");
        makeCall(requestDataMap);
    }

    /**
     *
     * @param run Run Object of the Build
     * @param name Name of the Item
     * @return
     */
    private JSONObject getMigrationJSONObject(final Run<?, ?> run, final String name) {
        String userid = Util.getBuildTriggererUserId(run);
        JSONObject buildDetails = new JSONObject();
        buildDetails.put("number", run.getNumber());
        buildDetails.put("result", String.valueOf(run.getResult()));
        buildDetails.put("queuetime", 0);
        buildDetails.put("duration", run.getDuration());
        buildDetails.put("starttime", run.getStartTimeInMillis());
        buildDetails.put("jenkinuser", userid == null ? "Anonymous" : userid);
        buildDetails.put("lastbuild", false);
        buildDetails.put("lastfail", false);
        buildDetails.put("lastsuccess", false);
        String branch = expandContent(run);
        if (branch != null) {
            buildDetails.put("branch", branch);
        }
        return buildDetails;
    }

    /**
     *
     * @param map Param Map of the Migration api
     */
    private void makeCall(Map<String, Object> map) {
        RequestClient client = new RequestClient(portal.concat("/zsapi/jenkins/migration/"), RequestClient.METHOD_POST, map,requestHeaderMap);
        try {
            client.execute();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "", e);
        }
    }

    /**
     *
     * @param run Run Object of the Build
     * @return Original Value of the key
     */
    private  String expandContent(Run<?, ?> run) {
        String value = null;

        try  {
            Util.Branchdetails bDetails[] = Util.Branchdetails.values();
            for (int sv = 0; sv < bDetails.length; sv++) {
                String key = bDetails[sv].getVariable();
                EnvVars envVars = run.getEnvironment(TaskListener.NULL);
                value = envVars.expand(key);
                if (!key.equals(value)) {
                   // LOGGER.info(value);
                    break;
                } else {
                    value = null;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "", e);
            return null;
        }
        return value;
    }
}
