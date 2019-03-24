using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace JasaIotServiceWebApi.App_Start
{
    // IoT Service に対する命令文字列
    //    const string REQ_BEDIN = "BEDIN";         // 入床（まだ起きている）
    //    const string REQ_SLEEP1 = "SLEEP1";       // 浅い眠りに入った（寝た）
    //    const string REQ_SLEEP2 = "SLEEP2";       // 深い眠りについた
    //    const string REQ_WAKEUP = "WAKEUP";       // 目覚めた
    //    const string REQ_GETUP = "GETUP";         // 離床

    public class ThingsCommand
    {
        public string thingsCommand;    // Things に対する命令

        // モノ（操作対象）によって変わる
        public string thingsUuid;       // Things UUID
        public string driverId;         // Driver ID
        public string sekisyo;          // sekisyo

        // コマンドによって変わる
        public string commandType;      // command_type
        public string commandCode;      // command_code
        public string commandValue;     // command_value

        // コンストラクタ
        public ThingsCommand(string tc, string t, string d, string s, string ct, string cc, string cv)
        {
            thingsCommand = tc;
            thingsUuid = t;
            driverId = d;
            sekisyo = s;
            commandType = ct;
            commandCode = cc;
            commandValue = cv;
        }
    }

    public class ThingsCommandList
    {
        // Things に対するコマンド
        public const string THING_RAPIRO_BEDIN = "RAPIRO_BEDIN";         // RAPIRO : ベッドに入ったことを通知
        public const string THING_RAPIRO_SLEEP1 = "RAPIRO_SLEEP1";       // RAPIRO : 眠りに就いたことを通知（眠い目）
        public const string THING_RAPIRO_SLEEP2 = "RAPIRO_SLEEP2";       // RAPIRO : 深い眠りに就いたことを通知   // 未使用
        public const string THING_RAPIRO_WAKEUP = "RAPIRO_WAKEUP";       // RAPIRO : 起こす
        public const string THING_RAPIRO_NORMAL = "RAPIRO_NORMAL";       // RAPIRO : 普通の目に戻す

        public const string THING_BLIND1_ON = "BLIND1_ON";               // ブラインド1（入口に近い）を ON する
        public const string THING_BLIND1_50 = "BLIND1_50";               // ブラインド1（入口に近い）を半開
        public const string THING_BLIND1_OPEN = "BLIND1_OPEN";           // ブラインド1（入口に近い）を全開
        public const string THING_BLIND1_CLOSE = "BLIND1_CLOSE";         // ブラインド1（入口に近い）を閉じる

        public const string THING_BLIND2_ON = "BLIND2_ON";               // ブラインド2（中央）を ON する
        public const string THING_BLIND2_50 = "BLIND2_50";               // ブラインド2（中央）を半開
        public const string THING_BLIND2_OPEN = "BLIND2_OPEN";           // ブラインド2（中央）を全開
        public const string THING_BLIND2_CLOSE = "BLIND2_CLOSE";         // ブラインド2（中央）を閉じる

        public const string THING_BLIND3_OPEN = "BLIND3_OPEN";           // ブラインド3（奥）を全開（ON コマンド）
        public const string THING_BLIND3_CLOSE = "BLIND3_CLOSE";         // ブラインド3（奥）を閉じる（OFF コマンド）

        public const string THING_CEILING1_ON = "CEILING1_ON";           // 天井照明1（ロフト）ON
        public const string THING_CEILING1_100 = "CEILING1_100";         // 天井照明1（ロフト）を光らせる（照度100%）
        public const string THING_CEILING1_50 = "CEILING1_50";           // 天井照明1（ロフト）を光らせる（照度 50%）
        public const string THING_CEILING1_20 = "CEILING1_20";           // 天井照明1（ロフト）を光らせる（照度 20%）
        public const string THING_CEILING1_OFF = "CEILING1_OFF";         // 天井照明1（ロフト）OFF

        public const string THING_CEILING2_ON = "CEILING2_ON";           // 天井照明2（ロフト）ON
        public const string THING_CEILING2_100 = "CEILING2_100";         // 天井照明2（入口）を光らせる（照度100%）
        public const string THING_CEILING2_50 = "CEILING2_50";           // 天井照明2（入口）を光らせる（照度 50%）
        public const string THING_CEILING2_20 = "CEILING2_20";           // 天井照明2（入口）を光らせる（照度 20%）
        public const string THING_CEILING2_OFF = "CEILING2_OFF";         // 天井照明2（入口）OFF

        public const string THING_CEILING3_ON = "CEILING3_ON";           // 天井照明3（ロフト）ON
        public const string THING_CEILING3_100 = "CEILING3_100";         // 天井照明3（奥）を光らせる（照度100%）
        public const string THING_CEILING3_50 = "CEILING3_50";           // 天井照明3（奥）を光らせる（照度 50%）
        public const string THING_CEILING3_20 = "CEILING3_20";           // 天井照明3（奥）を光らせる（照度 20%）
        public const string THING_CEILING3_OFF = "CEILING3_OFF";         // 天井照明3（奥）OFF

        public const string THING_OEL_ON = "OEL_ON";                     // 有機EL ON
        public const string THING_OEL_100 = "OEL_100";                   // 有機ELを光らせる（照度100%）
        public const string THING_OEL_50 = "OEL_50";                     // 有機ELを光らせる（照度 50%）
        public const string THING_OEL_20 = "OEL_20";                     // 有機ELを光らせる（照度 20%）
        public const string THING_OEL_10 = "OEL_10";                     // 有機ELを光らせる（照度 10%）
        public const string THING_OEL_OFF = "OEL_OFF";                   // 有機ELを消す
        public const string THING_OEL_COLOR = "OEL_COLOR";               // 有機EL カラー灯モード
        public const string THING_OEL_JOYATO = "OEL_JOYATO";             // 有機EL 常夜灯
        public const string THING_OEL_NORMAL = "OEL_NORMAL";             // 有機EL 通常灯
        public const string THING_OEL_DARKYELLOW = "OEL_DARK_YELLOW";    // 有機EL カラー灯（暗い黄色）
        public const string THING_OEL_YELLOW2 = "OEL_YELLOW_2";          // 有機EL カラー灯（黄色30%）
        public const string THING_OEL_BLUE = "OEL_BLUE";                 // 有機EL カラー灯 青

        public const string THING_NULL = "---";                          // 配列の終端

        public static ThingsCommand[] dictionary = new ThingsCommand[]
        {   //                Thingsにコマンド      Things UUID                                  Driver ID                       sekisyo     type            code        value

            // ■ RAPIRO
            new ThingsCommand(THING_RAPIRO_BEDIN,   "<RAPIROのThingsID>",     "<RAPIROのドライバID>",         "", "selection",    "power",    "M1"),  // 入床
            new ThingsCommand(THING_RAPIRO_SLEEP1,  "<RAPIROのThingsID>",     "<RAPIROのドライバID>",         "", "selection",    "power",    "M2"),  // 眠りに就いた
            new ThingsCommand(THING_RAPIRO_WAKEUP,  "<RAPIROのThingsID>",     "<RAPIROのドライバID>",         "", "selection",    "power",    "M3"),  // 起こす
            new ThingsCommand(THING_RAPIRO_NORMAL,  "<RAPIROのThingsID>",     "<RAPIROのドライバID>",         "", "selection",    "power",    "M4"),  // 起きている目（普通の目）

            // ■ ブラインド1（入口）
            new ThingsCommand(THING_BLIND1_ON,      "<ブラインド1のThingsID>", "<COMMAハウスのドライバID>",  "", "selection",    "80",       "30"),  // ブラインド1 ON
            new ThingsCommand(THING_BLIND1_50,      "<ブラインド1のThingsID>", "<COMMAハウスのドライバID>",  "", "character",    "E1",       "32"),  // ブラインド1 半開    0x32 = 50(%)
            new ThingsCommand(THING_BLIND1_OPEN,    "<ブラインド1のThingsID>", "<COMMAハウスのドライバID>",  "", "selection",    "E0",       "41"),  // ブラインド1 全開
            new ThingsCommand(THING_BLIND1_CLOSE,   "<ブラインド1のThingsID>", "<COMMAハウスのドライバID>",  "", "selection",    "E0",       "42"),  // ブラインド1 閉じる

            // ■ ブラインド2（中央）
            new ThingsCommand(THING_BLIND2_ON,      "<ブラインド2のThingsID>", "<COMMAハウスのドライバID>",  "", "selection",    "80",       "30"),  // ブラインド2 ON
            new ThingsCommand(THING_BLIND2_50,      "<ブラインド2のThingsID>", "<COMMAハウスのドライバID>",  "", "character",    "E1",       "32"),  // ブラインド2 半開    0x32 = 50(%)
            new ThingsCommand(THING_BLIND2_OPEN,    "<ブラインド2のThingsID>", "<COMMAハウスのドライバID>",  "", "selection",    "E0",       "41"),  // ブラインド2 全開
            new ThingsCommand(THING_BLIND2_CLOSE,   "<ブラインド2のThingsID>", "<COMMAハウスのドライバID>",  "", "selection",    "E0",       "42"),  // ブラインド2 閉じる

            // ■ ブラインド3（奥）
            new ThingsCommand(THING_BLIND3_OPEN,    "<ブラインド3のThingsID>", "<COMMAハウスのドライバID>",  "", "selection",    "80",       "30"),  // ブラインド3 全開
            new ThingsCommand(THING_BLIND3_CLOSE,   "<ブラインド3のThingsID>", "<COMMAハウスのドライバID>",  "", "selection",    "80",       "31"),  // ブラインド3 閉じる

            // ■ 天井照明1（ロフト）
            new ThingsCommand(THING_CEILING1_ON,    "<天井照明1のThingsID>", "<COMMAハウスのドライバID>",  "", "selection",    "80",       "30"),  // 天井照明1 ON
            new ThingsCommand(THING_CEILING1_100,   "<天井照明1のThingsID>", "<COMMAハウスのドライバID>",  "", "character",    "B0",       "64"),  // 天井照明1 100%
            new ThingsCommand(THING_CEILING1_50,    "<天井照明1のThingsID>", "<COMMAハウスのドライバID>",  "", "character",    "B0",       "32"),  // 天井照明1 50%
            new ThingsCommand(THING_CEILING1_20,    "<天井照明1のThingsID>", "<COMMAハウスのドライバID>",  "", "character",    "B0",       "14"),  // 天井照明1 20%
            new ThingsCommand(THING_CEILING1_OFF,   "<天井照明1のThingsID>", "<COMMAハウスのドライバID>",  "", "selection",    "80",       "31"),  // 天井照明1 OFF

            // ■ 天井照明2（入口）
            new ThingsCommand(THING_CEILING2_ON,    "<天井照明2のThingsID>", "<COMMAハウスのドライバID>",  "", "selection",    "80",       "30"),  // 天井照明2 ON
            new ThingsCommand(THING_CEILING2_100,   "<天井照明2のThingsID>", "<COMMAハウスのドライバID>",  "", "character",    "B0",       "64"),  // 天井照明2 100%
            new ThingsCommand(THING_CEILING2_50,    "<天井照明2のThingsID>", "<COMMAハウスのドライバID>",  "", "character",    "B0",       "32"),  // 天井照明2 50%
			new ThingsCommand(THING_CEILING2_20,    "<天井照明2のThingsID>", "<COMMAハウスのドライバID>",  "", "character",    "B0",       "14"),  // 天井照明2 20%
            new ThingsCommand(THING_CEILING2_OFF,   "<天井照明2のThingsID>", "<COMMAハウスのドライバID>",  "", "selection",    "80",       "31"),  // 天井照明2 OFF

            // ■ 天井照明3（奥）
            new ThingsCommand(THING_CEILING3_ON,    "<天井照明3のThingsID>", "<COMMAハウスのドライバID>",  "", "selection",    "80",       "30"),  // 天井照明3 ON
            new ThingsCommand(THING_CEILING3_100,   "<天井照明3のThingsID>", "<COMMAハウスのドライバID>",  "", "character",    "B0",       "64"),  // 天井照明3 100%
            new ThingsCommand(THING_CEILING3_50,    "<天井照明3のThingsID>", "<COMMAハウスのドライバID>",  "", "character",    "B0",       "32"),  // 天井照明3 50%
            new ThingsCommand(THING_CEILING3_20,    "<天井照明3のThingsID>", "<COMMAハウスのドライバID>",  "", "character",    "B0",       "14"),  // 天井照明3 20%
            new ThingsCommand(THING_CEILING3_OFF,   "<天井照明3のThingsID>", "<COMMAハウスのドライバID>",  "", "selection",    "80",       "31"),  // 天井照明3 OFF

            // ■ 有機EL
            new ThingsCommand(THING_OEL_ON,         "<有機EL照明のThingsID>", "<COMMAハウスのドライバID>",  "", "selection",    "80",       "30"),  // 有機EL ON
            new ThingsCommand(THING_OEL_100,        "<有機EL照明のThingsID>", "<COMMAハウスのドライバID>",  "", "character",    "B0",       "64"),  // 有機EL 100%
            new ThingsCommand(THING_OEL_50,         "<有機EL照明のThingsID>", "<COMMAハウスのドライバID>",  "", "character",    "B0",       "32"),  // 有機EL 50%
            new ThingsCommand(THING_OEL_20,         "<有機EL照明のThingsID>", "<COMMAハウスのドライバID>",  "", "character",    "B0",       "14"),  // 有機EL 20%
            new ThingsCommand(THING_OEL_10,         "<有機EL照明のThingsID>", "<COMMAハウスのドライバID>",  "", "character",    "B0",       "0A"),  // 有機EL 10%
            new ThingsCommand(THING_OEL_OFF,        "<有機EL照明のThingsID>", "<COMMAハウスのドライバID>",  "", "selection",    "80",       "31"),  // 有機EL OFF
            new ThingsCommand(THING_OEL_NORMAL,     "<有機EL照明のThingsID>", "<COMMAハウスのドライバID>",  "", "selection",    "B6",       "42"),  // 有機EL 通常
            new ThingsCommand(THING_OEL_JOYATO,     "<有機EL照明のThingsID>", "<COMMAハウスのドライバID>",  "", "selection",    "B6",       "43"),  // 有機EL 常夜灯
            new ThingsCommand(THING_OEL_COLOR,      "<有機EL照明のThingsID>", "<COMMAハウスのドライバID>",  "", "selection",    "B6",       "44"),  // 有機EL カラー
            new ThingsCommand(THING_OEL_YELLOW2,    "<有機EL照明のThingsID>", "<COMMAハウスのドライバID>",  "", "character",    "INPUT",    "100201/3/B60144C003F5E0A0B0011E"),  // 有機EL カラー暗い黄色 30%
            new ThingsCommand(THING_OEL_DARKYELLOW, "<有機EL照明のThingsID>", "<COMMAハウスのドライバID>",  "", "character",    "INPUT",    "100201/3/B60144C003F5E0A0B0010A"),  // 有機EL カラー暗い黄色10%
            new ThingsCommand(THING_OEL_BLUE,       "<有機EL照明のThingsID>", "<COMMAハウスのドライバID>",  "", "character",    "INPUT",    "100201/2/B60144C0030000FF"),  // 有機EL カラー青


            // ■ 配列の終端
            new ThingsCommand(THING_NULL,           "",                                         "",                                     "", "",             "",         ""),    // 配列の終端
        };

        /**
         *  コマンド配列から指定のコマンドの要素番号を返す
         */
        public int Find(string command)
        {
            if (command.Equals(THING_NULL) == true)
            {
                return -1;
            }

            int ite = 0;
            for (ite = 0; dictionary[ite].thingsCommand.Equals(THING_NULL) == false; ++ite)
            {
                if (dictionary[ite].thingsCommand.Equals(command) == true)
                {
                    return ite;
                }
            }

            return -1;
        }
    }

    public class CommandConst
    {
        //        const string SERVICE_ID = "iot-guest-002";
        public const string API_KEY_HEADER = "X-IOT-API-KEY";
        public const string API_KEY_VALUE = "<APIキー>";

        // センサシートから IoT Service に送られてくる request の value
        public const string REQ_BEDIN = "bedin";       // 入床
        public const string REQ_SLEEP1 = "sleep1";     // 入眠
        public const string REQ_SLEEP2 = "sleep2";     // 深い眠り
        public const string REQ_WAKEUP = "wakeup";     // 朝の浅い眠り（→起こす）
        public const string REQ_GETUP = "getup";       // 覚醒（本当は離床か？）
        public const string REQ_DEBUG = "debug";       // デバッグ用

        public const int REQID_BEDIN = 1;
        public const int REQID_SLEEP1 = 2;
        public const int REQID_SLEEP2 = 3;
        public const int REQID_WAKEUP = 4;
        public const int REQID_GETUP = 5;
        public const int REQID_DEBUG = 99;
    }
}
