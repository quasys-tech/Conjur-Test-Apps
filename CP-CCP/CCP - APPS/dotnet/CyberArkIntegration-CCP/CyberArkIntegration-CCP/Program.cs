using System;
using System.IO;
using System.Net;
using System.Text;

namespace CyberArkIntegration_CCP
{
    class Program
    {
        static void Main(string[] args)
        {
            // CyberArk API bilgileri
            string url = "http://172.27.76.24/AIMWebService/api/Accounts";
            string appId = "CCP_DEMO_APP"; // Uygulama ID
            string safe = "CCP_DEMO"; // Safe adı
            string objectName = "ccp_demo_account"; // Nesne adı

            // Tam URL'yi oluştur
            string fullUrl = $"{url}?AppID={appId}&Safe={safe}&Object={objectName}";

            try
            {
                // WebRequest oluştur
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(fullUrl);
                request.Method = "GET";
                request.ContentType = "application/json";

                // Yanıt al
                using (HttpWebResponse response = (HttpWebResponse)request.GetResponse())
                {
                    if (response.StatusCode == HttpStatusCode.OK)
                    {
                        // Yanıtı oku
                        using (StreamReader reader = new StreamReader(response.GetResponseStream()))
                        {
                            string responseContent = reader.ReadToEnd();
                            Console.WriteLine("Şifre başarıyla alındı:");
                            Console.WriteLine(responseContent);
                        }
                    }
                    else
                    {
                        Console.WriteLine($"Hata: HTTP {response.StatusCode}");
                    }
                }
            }
            catch (WebException ex)
            {
                // Hata durumunu ele al
                Console.WriteLine("İstek sırasında bir hata oluştu:");
                Console.WriteLine(ex.Message);

                if (ex.Response != null)
                {
                    using (StreamReader reader = new StreamReader(ex.Response.GetResponseStream()))
                    {
                        string errorResponse = reader.ReadToEnd();
                        Console.WriteLine("Hata Detayı:");
                        Console.WriteLine(errorResponse);
                    }
                }
            }
        }
    }
}
