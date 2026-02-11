package generator.domain.seckill;

import lombok.Getter;

/**
 * 秒杀活动状态枚举（维护状态码与状态文本的映射）
 */
@Getter // 提供 getter 方法，方便获取描述字段
public enum SeckillActivityStatusEnum {
    // 枚举项：(状态码, 状态文本)
    NOT_START(0, "未开始"),
    ON_GOING(1, "进行中"),
    ENDED(2, "已结束");

    // 状态码
    private final Integer code;
    // 状态文本描述
    private final String desc;

    // 私有构造方法（枚举类构造方法必须为私有）
    SeckillActivityStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据状态码获取对应的枚举项（方便快速查询）
     *
     * @param code 状态码
     * @return 对应的枚举项，无匹配返回 null
     */
    public static SeckillActivityStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        // 遍历枚举项，匹配状态码
        for (SeckillActivityStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 简化方法：直接根据状态码获取状态文本
     *
     * @param code 状态码
     * @return 状态文本，无匹配返回「未知状态」
     */
    public static String getDescByCode(Integer code) {
        SeckillActivityStatusEnum statusEnum = getByCode(code);
        return statusEnum == null ? "未知状态" : statusEnum.getDesc();
    }
}