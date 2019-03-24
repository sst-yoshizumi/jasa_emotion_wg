using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using JasaIotServiceWebApi.App_Start;

namespace JasaIotServiceWebApi.Controllers
{
    public class Blind1Controller : ApiController
    {
        const string IOT_HUB_URL = "https://jasa.hub.r-edge.org/api/request/";      // IoT Hub の URL
        const string SEND_CONTENT_TYPE = "application/json";                        // Content-Type ヘッダ
        const string API_KEY_HEADER = "X-IOT-API-KEY";                              // API_KEY の BASIC 認証
        const string API_KEY_VALUE = "48538017ed3e9bd692f5780371eccea0dc9371f7";

        private IotHubRequest request = new IotHubRequest();
        private IotHubRequestList requests = new IotHubRequestList();
        private int requestListCount = 0;

        public Blind1Controller()
        {
            request.commandList = new List<RequestCommand>();

//            request.ThingUuid = "9374d35c1937b8a0855e7faedbf3960cf56ef481";     // ブラインド1
//            request.ThingUuid = "9fb57cf270e9c70da3ffda96cfb2da17da00fe99";     // ブラインド2
//            request.ThingUuid = "02c9796afddfe87cf06f3c6dc3576455204777a6";     // ブラインド3 ←これだけ開閉コマンドは 80-30/31（他は E0-41/42）
//            request.ThingUuid = "1a6b135e-d92e-4142-8c6f-5cd644947e0f";     // 蛍光灯（Hue）
//            request.ThingUuid = "dafc5dd887c329bc3c54b264e74ff50d5a719b9c";     // 寝室エアコン
//            request.ThingUuid = "5fb0b7f5df59862321b30d4976727f87b7d71a24";     // 寝室照明1
//            request.ThingUuid = "fc0ec6fdf0a3bb3a9836174b724415bcc202a5b2";     // 寝室照明2（入口側）
//            request.ThingUuid = "263d366c9ae68944dd1868530816f3940c2e5a58";     // 寝室照明3（ロフト側）
            request.ThingUuid = "825b6c22315361c92f4bca4b9f555d9c4a6f583e";     // 有機EL

            request.DriverId = "jp.co.planis.samplecommahousedriver";
            //request.RedgeId = "78f92ebe-d7de-4388-80eb-946d828a29af";   // JASAスマホ
            request.RedgeId = "1bf66a08-e704-4a67-b1c6-b5e7903cd571";   // 吉栖スマホ
            request.Sekisyo = "";

            //            request.commandList.Add(new RequestCommand("", "", ""));

            //            requests.requestList = new List<IotHubRequest>();
            //            requests.requestList.Add(new IotHubRequest(request));
        }

        // GET: api/Blind1
        public IEnumerable<string> Get()
        {
            return new string[] { "value1", "value2" };
        }

        // GET: api/Blind1/5
        public string Get(int id)
        {
            return "value";
        }

        // POST: api/Blind1
        public void Post([FromBody]string value)
        {
            request.commandList.Add(new RequestCommand("selection", "80", value));   // ON/OFF
//            request.commandList.Add(new RequestCommand("selection", "E6", value));   // 有機EL 常夜灯 43
//            request.commandList.Add(new RequestCommand("character", "E9", value));   // 有機EL色調整
//            request.commandList.Add(new RequestCommand("selection", "B0", value));   // 照明照度レベル
//            request.commandList.Add(new RequestCommand("selection", "E0", value));   // value：全開 41、全閉 42 開動作中 43 閉動作中 44 途中停止 45
//get            request.commandList.Add(new RequestCommand("selection", "EA", value));   // value：全開 41、全閉 42 開動作中 43 閉動作中 44 途中停止 45
            requests.requestList = new List<IotHubRequest>();
            requests.requestList.Add(new IotHubRequest(request));

            Send("POST", requests);
        }

        // PUT: api/Blind1/5
        public void Put(int id, [FromBody]string value)
        {
        }

        // DELETE: api/Blind1/5
        public void Delete(int id)
        {
        }

        private void Send(string method, IotHubRequestList aRequests)
        {
            // シリアライズ
            string json = JsonConvert.SerializeObject(aRequests, Formatting.None);

            // 処理したリストのデータは削除しておく。
            aRequests.requestList[0].commandList.RemoveAt(requestListCount);
            aRequests.requestList.RemoveAt(0);

            // 送信する文字列をバイト型配列に変換。エンコードは UTF-8 指定。
            byte[] putDataBytes = System.Text.Encoding.UTF8.GetBytes(json);

            // WebRequest の作成。
            System.Net.WebRequest webReq = System.Net.WebRequest.Create(IOT_HUB_URL);

            // メソッドを指定。
            webReq.Method = method;

            // ContentType を設定。
            webReq.ContentType = SEND_CONTENT_TYPE;

            // API_KEY の BASIC 認証を追加。
            webReq.Headers.Add(API_KEY_HEADER, API_KEY_VALUE);

            // 送信するデータの長さを指定。
            webReq.ContentLength = putDataBytes.Length;

            // データを送信するための Stream を取得。
            System.IO.Stream reqStream = webReq.GetRequestStream();

            // 送信するデータを書き込むんで送信。 Stream を閉じる。
            reqStream.Write(putDataBytes, 0, putDataBytes.Length);
            reqStream.Close();

            return;
        }
    }
}
