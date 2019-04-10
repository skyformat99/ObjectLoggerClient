package com.github.yeecode.objectLogger.client.service;

import com.github.yeecode.objectLogger.client.bean.ObjectLoggerConfigBean;
import com.github.yeecode.objectLogger.client.bean.HttpBean;
import com.github.yeecode.objectLogger.client.bean.LocalTypeHandler;
import com.github.yeecode.objectLogger.client.model.ActionItemModel;
import com.github.yeecode.objectLogger.client.task.SendLogForObjectTask;
import com.github.yeecode.objectLogger.client.task.SendLogForItemsTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class LogClient {
    @Autowired
    private ObjectLoggerConfigBean objectLoggerConfigBean;
    @Autowired
    private HttpBean httpBean;
    @Autowired
    private LocalTypeHandler localTypeHandler;

    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);


    /**
     * 根据新旧Model的变化，自动创建一条操作log记录
     * 注：自动创建操作需要机遇Model上的@LogDescription注解，无@LogDescription注解的属性不会被记录
     *
     * @param objectId    所操作的对象的具体编号
     * @param actor      操作人
     * @param action     操作方法英文名
     * @param actionName 操作方法中文名（用于展示）
     * @param extraWords 操作方法的附加说明
     * @param comment    操作备注
     * @param oldModel   操作前的旧对象
     * @param newModel   操作后的新对象
     */
    public void sendLogForObject(Integer objectId, String actor, String action, String actionName,
                                 String extraWords, String comment,
                                 Object oldModel, Object newModel) {
        try {
            SendLogForObjectTask sendLogForObjectTask = new SendLogForObjectTask(objectId, actor, action, actionName,
                    extraWords, comment, oldModel, newModel, objectLoggerConfigBean, httpBean, localTypeHandler);
            fixedThreadPool.execute(sendLogForObjectTask);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    /**
     * 传入发生变化的多条属性，根据该属性生成一条操作log日志
     *
     * @param objectName              对象名
     * @param objectId                操作对象的具体Id
     * @param actor                  操作人
     * @param action                 操作行为
     * @param actionName             操作方法中文名（用于展示）
     * @param extraWords             操作方法的附加说明
     * @param comment                操作备注
     * @param actionItemModelList 具体被操作的属性，其中
     *                               attributeType，attribute，attributeName必填；
     *                               oldValue，newValue,diffValue 按需选填，不能全为空；
     *                               id, actionId 不填
     */
    public void sendLogForItems(String objectName, Integer objectId,
                                String actor, String action, String actionName,
                                String extraWords, String comment,
                                List<ActionItemModel> actionItemModelList) {
        try {
            SendLogForItemsTask sendLogForItemsTask = new SendLogForItemsTask(objectName, objectId, actor,
                    action, actionName, extraWords, comment, actionItemModelList, objectLoggerConfigBean, httpBean);
            fixedThreadPool.execute(sendLogForItemsTask);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
