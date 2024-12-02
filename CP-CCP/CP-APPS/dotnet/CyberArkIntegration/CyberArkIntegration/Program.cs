using System;
using System.Security;
using CyberArk.AIM.NetPasswordSDK;
using CyberArk.AIM.NetPasswordSDK.Exceptions;

namespace CyberArkIntegration
{
    class Program
    {
        static void Main(string[] args)
        {
            string appId = "CCP_DEMO_APP_AUTH2";      // CyberArk içinde tanımlı uygulama ID'si
            string safe = "CCP_DEMO2";               // Kullanmak istediğiniz Safe adı
            string folder = "Root";                 // Varsayılan "Root" klasörü
            string objectName = "ccp_demo2_object";  // Çekmek istediğiniz nesne adı (ör. kullanıcı adı)

            try
            {
                // Şifre talebi nesnesi oluştur
                PSDKPasswordRequest passwordRequest = new PSDKPasswordRequest
                {
                    AppID = appId,
                    Safe = safe,
                    Folder = folder,
                    Object = objectName
                };

                // Şifre talebi işlemi
                PSDKPassword password = PasswordSDK.GetPassword(passwordRequest);

                // Şifreyi düz metin olarak dönüştür ve yazdır
                Console.WriteLine("Şifre başarıyla alındı:");
                Console.WriteLine($"Safe: {safe}");
                Console.WriteLine($"Klasör: {folder}");
                Console.WriteLine($"Nesne: {objectName}");
                Console.WriteLine($"Şifre: {ConvertToUnsecureString(password.SecureContent)}");
            }
            catch (PSDKException ex)
            {
                Console.WriteLine("CyberArk SDK hatası oluştu:");
                Console.WriteLine($"Hata Mesajı: {ex.Message}");
            }
            catch (Exception ex)
            {
                Console.WriteLine("Bir hata oluştu:");
                Console.WriteLine(ex.Message);
            }
        }


        private static string ConvertToUnsecureString(SecureString secureString)
        {
            if (secureString == null)
                throw new ArgumentNullException(nameof(secureString));

            IntPtr unmanagedString = IntPtr.Zero;
            try
            {
                // SecureString'i unmanaged bellekten çöz
                unmanagedString = SecureStringMarshal.SecureStringToGlobalAllocUnicode(secureString);
                return System.Runtime.InteropServices.Marshal.PtrToStringUni(unmanagedString);
            }
            finally
            {
                // Belleği serbest bırak
                System.Runtime.InteropServices.Marshal.ZeroFreeGlobalAllocUnicode(unmanagedString);
            }
        }
    }
}
