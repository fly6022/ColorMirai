package Color_yr.ColorMirai.Pack.ToPlugin;

import Color_yr.ColorMirai.Pack.PackBase;

/*
82 [机器人]机器人戳一戳（事件）
id: QQ号
name: QQ昵称
action: 戳一戳的动作名称
suffix: 戳一戳中设置的自定义后缀
 */
public class BotNudgedEventPack extends PackBase {
    public long id;
    public String name;
    public String action;
    public String suffix;

    public BotNudgedEventPack(long qq, long id, String name, String action, String suffix) {
        this.qq = qq;
        this.id = id;
        this.name = name;
        this.action = action;
        this.suffix = suffix;
    }
}
