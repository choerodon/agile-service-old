package io.choerodon.agile.infra.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/10/15
 */
public enum InitNode {
    START("start", 21L, 0L, 20L, 20L, NodeType.START),
    INIT("create", 0L, 50L, 62L, 26L, NodeType.INIT),
    DEFAULT2("processing", 0L, 100L, 62L, 26L, NodeType.CUSTOM),
    DEFAULT3("complete", 0L, 150L, 62L, 26L, NodeType.CUSTOM),

    PROGRAM1("prepare", 0L, 50L, 62L, 26L, NodeType.INIT),
    PROGRAM2("create", 0L, 100L, 62L, 26L, NodeType.CUSTOM),
    PROGRAM3("processing", 0L, 150L, 62L, 26L, NodeType.CUSTOM),
    PROGRAM4("complete", 0L, 200L, 62L, 26L, NodeType.CUSTOM);
    String code;
    Long positionX;
    Long positionY;
    Long width;
    Long height;
    String type;

    InitNode(String code, Long positionX, Long positionY, Long width, Long height, String type) {
        this.code = code;
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public Long getPositionX() {
        return positionX;
    }

    public Long getPositionY() {
        return positionY;
    }

    public Long getWidth() {
        return width;
    }

    public Long getHeight() {
        return height;
    }

    public String getType() {
        return type;
    }

    public static List<InitNode> list(String schemeApplyType) {
        List<InitNode> result = new ArrayList<>();
        switch (schemeApplyType) {
            case "default":
            case SchemeApplyType.AGILE:
            case SchemeApplyType.TEST:
                result.add(InitNode.START);
                result.add(InitNode.INIT);
                result.add(InitNode.DEFAULT2);
                result.add(InitNode.DEFAULT3);
                break;
            case SchemeApplyType.PROGRAM:
                result.add(InitNode.START);
                result.add(InitNode.PROGRAM1);
                result.add(InitNode.PROGRAM2);
                result.add(InitNode.PROGRAM3);
                result.add(InitNode.PROGRAM4);
                break;
            default:
                break;
        }
        return result;
    }
}
