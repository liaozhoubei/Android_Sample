package com.example.changeskin;

/**
 * 布局的枚举类
 */
public enum LayoutEnum {
    layout_none("none"), Layout_one("vertical"), Layout_two("horizontal");
    private String orientation;

    LayoutEnum(String orientation) {
        this.orientation = orientation;
    }

    public LayoutEnum getLayoutEnum(String orientation) {
        LayoutEnum layoutEnum;
        switch (orientation) {
            case "vertical":
                layoutEnum = Layout_one;
                break;
            case "horizontal":
                layoutEnum = Layout_two;
                break;
            default:
                layoutEnum = layout_none;
        }
        return layoutEnum;
    }

    public String getOrientation() {
        return orientation;
    }
}
