package io.jenkins.plugins.sprintsdata;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;
import io.jenkins.plugins.configuration.SprintsConfig;
import io.jenkins.plugins.configuration.SprintsConnectionConfig;
import io.jenkins.plugins.sprints.RequestClient;
import jenkins.model.Jenkins;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.jenkins.plugins.util.Util.getSprintsGlobalConfig;
/**
 *@author selvavignesh.m
 * @version 1.0
 */
@Extension
public class ItemChangeListener extends ItemListener {
    private static final Logger LOGGER = Logger.getLogger(ItemChangeListener.class.getName());

    /**
     *
     * @param item Renamed item
     * @param oldName Old full name of item
     * @param newName New full name of item
     */
    @Override
    public void onRenamed(Item item, String oldName, String newName) {
       // After this method execution , OnLocationChanged will be called so moved the content to there
    }

    /**
     *
     * @param src Source Item
     * @param item Item created by the use of source
     */
    @Override
    public void onCopied(Item src, Item item) {
        doCreateJob(item);
    }

    /**
     *
     * @param item Deleted item Object
     */
    @Override
    public void onDeleted(Item item) {
       LOGGER.info(item.getFullName() + " deleted");
        SprintsConfig api = getSprintsGlobalConfig();
        if (api != null) {
            new Thread(() -> {
                Map<String, Object> param = new HashMap<>();
               // param.put("zapikey", api.getApiToken());
               // param.put("mailid", api.getMailid());
                param.put("name", item.getFullName());
                RequestClient client = new RequestClient(api.getJobDeleteUrl(), RequestClient.METHOD_DELETE, param);
                try {
                    client.setOAuthHeader();
                    client.execute();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING,"", e);
                }
            }).start();
        }
    }

    /**
     *
     * @param item New item has been created now
     */
    @Override
    public void onCreated(Item item) {
        doCreateJob(item);
    }

    /**
     *
     * @param item Relocated item
     * @param oldFullName Old full name of item
     * @param newFullName New full name of item
     */
    @Override
    public void onLocationChanged(Item item, String oldFullName, String newFullName) {
        SprintsConfig api = getSprintsGlobalConfig();
        if (api != null) {
            new Thread(() -> {
                Map<String, Object> param = new HashMap<>();
               // param.put("zapikey", api.getApiToken());
               // param.put("mailid", api.getMailid());
                param.put("action", "update");
                param.put("name", oldFullName);
                param.put("newname", newFullName);
                RequestClient client = new RequestClient(api.getJobDeleteUrl(), RequestClient.METHOD_POST, param);
                try {
                    client.setOAuthHeader();
                    client.execute();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "", e);
                }
            }).start();
        }
    }

    /**
     *
     * @param item Item need to create in sprints portal
     */
    public void doCreateJob(Item item) {
        SprintsConfig api = getSprintsGlobalConfig();
        if (api != null) {
            new Thread(() -> {
                Map<String, Object> param = new HashMap<>();
                //param.put("zapikey", api.getApiToken());
               // param.put("mailid", api.getMailid());
                param.put("name", item.getFullName());
                param.put("action", "create");
                RequestClient client = new RequestClient(api.getCreateJob(), RequestClient.METHOD_POST, param);
                try {
                    client.setOAuthHeader();
                    client.execute();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "", e);
                }
            }).start();
        }

    }

    /*private String checkFolderJob(Item item){
        ItemGroup<? extends Item> itemParent = item.getParent();
        if ( itemParent != null ) {
            Item parentItem = Jenkins.getInstanceOrNull().getItem(itemParent.getFullName());
            if (parentItem instanceof Folder) {
                return itemParent.getFullName(); // It will return the entire path of thr Parent Item like -> folder/inside
            }
        }
        return null;
    }*/



    @Restricted(NoExternalUse.class)
    private Map<String, String> getHeader() {
        Map<String, String> headerMap = new HashMap<>();
        List<SprintsConnectionConfig> extnList =  new ArrayList<>(Jenkins.getInstance().getExtensionList(SprintsConnectionConfig.class));
        SprintsConnectionConfig conf = extnList.get(0);
        headerMap.put("X-ZS-JENKINS-ID", conf.getZsheader());
        return headerMap;
    }
}
