package io.choerodon.agile.infra.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/10/15
 */
public enum InitTransform {
    INIT("初始化", null, "start", "create", TransformType.INIT, TransformConditionStrategy.ALL),
    INIT_PROGRAM("初始化", null, "start", "prepare", TransformType.INIT, TransformConditionStrategy.ALL),
    TRANSTFORMALL1("全部转换到准备", null, null, "prepare", TransformType.ALL, TransformConditionStrategy.ALL),
    TRANSTFORMALL2("全部转换到待处理", null, null, "create", TransformType.ALL, TransformConditionStrategy.ALL),
    TRANSTFORMALL3("全部转换到处理中", null, null, "processing", TransformType.ALL, TransformConditionStrategy.ALL),
    TRANSTFORMALL4("全部转换到已完成", null, null, "complete", TransformType.ALL, TransformConditionStrategy.ALL),
    ;
    private String name;
    private String description;
    private String startNodeCode;
    private String endNodeCode;
    private String type;
    private String conditionStrategy;

    InitTransform(String name, String description, String startNodeCode, String endNodeCode, String type, String conditionStrategy) {
        this.name = name;
        this.description = description;
        this.startNodeCode = startNodeCode;
        this.endNodeCode = endNodeCode;
        this.type = type;
        this.conditionStrategy = conditionStrategy;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStartNodeCode() {
        return startNodeCode;
    }

    public String getEndNodeCode() {
        return endNodeCode;
    }

    public String getType() {
        return type;
    }

    public String getConditionStrategy() {
        return conditionStrategy;
    }

    public static List<InitTransform> list(String schemeApplyType) {
        List<InitTransform> result = new ArrayList<>();
        switch (schemeApplyType) {
            case "default":
            case SchemeApplyType.AGILE:
            case SchemeApplyType.TEST:
                result.add(InitTransform.INIT);
                result.add(InitTransform.TRANSTFORMALL2);
                result.add(InitTransform.TRANSTFORMALL3);
                result.add(InitTransform.TRANSTFORMALL4);
                break;
            case SchemeApplyType.PROGRAM:
                result.add(InitTransform.INIT_PROGRAM);
                result.add(InitTransform.TRANSTFORMALL1);
                result.add(InitTransform.TRANSTFORMALL2);
                result.add(InitTransform.TRANSTFORMALL3);
                result.add(InitTransform.TRANSTFORMALL4);
                break;
            default:
                break;
        }
        return result;
    }
}
