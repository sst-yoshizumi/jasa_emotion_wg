using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http;

namespace JasaIotServiceWebApi
{
    public static class WebApiConfig
    {
        public static void Register(HttpConfiguration config)
        {
            // Web API の設定およびサービス

            // Web API ルート
            config.MapHttpAttributeRoutes();

            config.Routes.MapHttpRoute(
                name: "DefaultApi",
                routeTemplate: "COMMA/{controller}/{id}",
                defaults: new { id = RouteParameter.Optional }
            );
        }
    }
}
