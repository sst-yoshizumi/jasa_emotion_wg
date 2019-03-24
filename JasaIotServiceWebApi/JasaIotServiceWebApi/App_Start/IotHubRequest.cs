using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace JasaIotServiceWebApi.App_Start
{
    [JsonObject("command")]
    public class RequestCommand
    {
        [JsonProperty("command_type")]
        public string CommandType;
        [JsonProperty("command_code")]
        public string CommandCode;
        [JsonProperty("command_value")]
        public string CommandValue;

        public RequestCommand(string t, string c, string v)
        {
            CommandType = t;
            CommandCode = c;
            CommandValue = v;
        }
        public RequestCommand()
        {
            CommandType = "selection";
            CommandCode = "power";
            CommandValue = "off";
        }
    }

    [JsonObject("requestObject")]
    public class IotHubRequest
    {
        [JsonProperty("thing_uuid")]
        public string ThingUuid { get; set; }
        [JsonProperty("r_edge_id")]
        public string RedgeId { get; set; }
        [JsonProperty("driver_id")]
        public string DriverId { get; set; }
        [JsonProperty("sekisyo")]
        public string Sekisyo { get; set; }

        [JsonProperty("command")]
        public List<RequestCommand> commandList { get; set; }

        public IotHubRequest()
        {

        }
        public IotHubRequest(IotHubRequest a)
        {
            ThingUuid = a.ThingUuid;
            RedgeId = a.RedgeId;
            DriverId = a.DriverId;
            Sekisyo = a.Sekisyo;
            commandList = new List<RequestCommand>();
            commandList.Add(new RequestCommand(a.commandList[0].CommandType, a.commandList[0].CommandCode, a.commandList[0].CommandValue));
        }
    }

    public class IotHubRequestList
    {
        [JsonProperty("requests")]
        public List<IotHubRequest> requestList { get; set; }
    }
}