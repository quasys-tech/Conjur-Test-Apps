using System.Security.Cryptography.X509Certificates;
using System.Text;

namespace cnjr_netcore_jwt
{
    public class SecretService : IHostedService
    {
        public Task StartAsync(CancellationToken cancellationToken)
        {
            String token = File.ReadAllText("/var/run/secrets/kubernetes.io/serviceaccount/token");
            String baseURL = Environment.GetEnvironmentVariable("CONJUR_APPLIANCE_URL");
            String serviceID = Environment.GetEnvironmentVariable("CONJUR_AUTHN_JWT_SERVICE_ID");
            String conjurAccount = Environment.GetEnvironmentVariable("CONJUR_ACCOUNT");
            String cert = Environment.GetEnvironmentVariable("CONJUR_SSL_CERTIFICATE");
            String secretPath = Environment.GetEnvironmentVariable("CONJUR_SECRET_ID");


            X509Certificate2 certificate = new X509Certificate2(Encoding.ASCII.GetBytes(cert));

            var httpClientHandler = new HttpClientHandler();

            httpClientHandler.ServerCertificateCustomValidationCallback = (message, cert, chain, _) =>
            {
                chain.ChainPolicy.TrustMode = X509ChainTrustMode.CustomRootTrust;
                chain.ChainPolicy.VerificationFlags = X509VerificationFlags.AllowUnknownCertificateAuthority;
                chain.ChainPolicy.CustomTrustStore.Add(certificate);
                chain.ChainPolicy.CustomTrustStore.AddRange(new X509Store(StoreName.Root, StoreLocation.LocalMachine, OpenFlags.ReadOnly).Certificates);

                return chain.Build(cert);
            };

            using var httpClient = new HttpClient(httpClientHandler);
            httpClient.DefaultRequestHeaders.Add("Accept-Encoding", "base64");
            httpClient.BaseAddress = new Uri(baseURL);
            StringContent stringContent = new StringContent("jwt=" + token, Encoding.UTF8, "application/x-www-form-urlencoded");
            var result = httpClient.PostAsync("/authn-jwt/"+ serviceID +"/"+ conjurAccount +"/authenticate", stringContent).Result.Content.ReadAsStringAsync().Result;
            
            

            httpClient.DefaultRequestHeaders.Clear();
            httpClient.DefaultRequestHeaders.Add("Authorization", "Token token=\"" + result + "\"");

            var secret = httpClient.GetAsync("/api/secrets/" + conjurAccount + "/variable/" + secretPath).Result.Content.ReadAsStringAsync().Result;

            Console.WriteLine("Password: " + secret.ToString());

            return Task.CompletedTask;
        }

        public Task StopAsync(CancellationToken cancellationToken)
        {
            Console.WriteLine("test02");

            return Task.CompletedTask;
        }
    }
}
