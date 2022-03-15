package org.cloud.sonic.agent.tests.handlers;

import com.alibaba.fastjson.JSONObject;
import org.cloud.sonic.agent.automation.HandleDes;
import org.cloud.sonic.agent.enums.ConditionEnum;
import org.cloud.sonic.agent.tests.common.RunStepThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * if 条件步骤
 *
 * @author JayWenStar
 * @date 2022/3/13 2:20 下午
 */
@Component
public class IfHandler implements StepHandler {

    @Autowired
    private NoneConditionHandler noneConditionHandler;
    @Autowired
    private StepHandlers stepHandlers;

    @Override
    public HandleDes runStep(JSONObject stepJSON, HandleDes handleDes, RunStepThread thread) throws Throwable {
        if (thread.isStopped()) {
            return null;
        }

        // 取出if下的步骤集合
        JSONObject conditionStep = stepJSON.getJSONObject("step");
        List<JSONObject> steps = conditionStep.getJSONArray("childSteps").toJavaList(JSONObject.class);
        // 执行条件步骤
        noneConditionHandler.runStep(stepJSON, handleDes, thread);
        // 上述步骤无异常则取出if下的步骤，再次丢给 stepHandlers 处理
        if (handleDes.getE() == null) {
            for (JSONObject step : steps) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("step", step);
                stepHandlers.runStep(jsonObject, handleDes, thread);
            }
        }
        return handleDes;
    }

    @Override
    public ConditionEnum getCondition() {
        return ConditionEnum.IF;
    }
}
