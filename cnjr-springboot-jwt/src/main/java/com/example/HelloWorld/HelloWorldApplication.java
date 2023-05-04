package com.example.HelloWorld;


import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.tcp.SslProvider;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

@SpringBootApplication
@RestController
public class HelloWorldApplication {

	public static void main(String[] args) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, URISyntaxException, InterruptedException, KeyManagementException {
//		System.out.println("----------------------------WEB CLIENT----------------------------------");
//		getPasswordWC();
//		System.out.println("------------------------------------------------------------------------");

		System.out.println("----------------------------HTTP CLIENT---------------------------------");
		getPasswordHC();
		System.out.println("------------------------------------------------------------------------");


		SpringApplication.run(HelloWorldApplication.class, args);

	}

	public static  String getTokenWC() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
		String serviceID = System.getenv("CONJUR_AUTHN_JWT_SERVICE_ID");
		String conjurAccount = System.getenv("CONJUR_ACCOUNT");
		String authLogin = System.getenv("CONJUR_AUTHN_LOGIN");
		Path filePath = Path.of("/var/run/secrets/kubernetes.io/serviceaccount/token");
		String tokenContent = Files.readString(filePath);

		WebClient client = webClient();
		String token = client.post().uri("/authn-jwt/" + serviceID +"/"+ conjurAccount +"/" + authLogin + "/authenticate")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("Accept-Encoding", "base64")
				.bodyValue("jwt=" + tokenContent)
				.retrieve().bodyToMono(String.class).block();
		return  token;
	}
	public static void getPasswordWC() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
		String conjurAccount = System.getenv("CONJUR_ACCOUNT");
		String secretPath = System.getenv("CONJUR_SECRET_ID");
		WebClient client = webClient();
		String response = client.get().uri("/api/secrets/"+ conjurAccount +"/variable/" + secretPath)
				.header("Authorization", "Token token=\""+ getTokenWC() +"\"")
				.retrieve().bodyToMono(String.class).block();
		System.out.println("Password with WebClient: " + response);
	}
	public static WebClient webClient() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
		String baseURL = System.getenv("CONJUR_APPLIANCE_URL");
		String conjurCert = System.getenv("CONJUR_SSL_CERTIFICATE");
		final CertificateFactory cf = CertificateFactory.getInstance("X.509");
		final Certificate cert = cf.generateCertificate(new ByteArrayInputStream(conjurCert.getBytes()));
		final KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(null);
		ks.setCertificateEntry("conjurTlsCaPath", cert);

		final TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(ks);

		SslContext sslContext = SslContextBuilder
				.forClient()
				.trustManager(tmf)
				.build();
		SslProvider sslProvider = SslProvider.builder()
				.sslContext(sslContext).build();
		reactor.netty.http.client.HttpClient httpClient = reactor.netty.http.client.HttpClient.create()
				.secure(sslProvider);
		return WebClient.builder().baseUrl(baseURL)
				.clientConnector(new ReactorClientHttpConnector(httpClient)).build();
	}

	public static void getPasswordHC() throws URISyntaxException, CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, InterruptedException {
		String conjurCert = System.getenv("CONJUR_SSL_CERTIFICATE");
		String baseURL = System.getenv("CONJUR_APPLIANCE_URL");
		String conjurAccount = System.getenv("CONJUR_ACCOUNT");
		String secretPath = System.getenv("CONJUR_SECRET_ID");
		String authLogin = System.getenv("CONJUR_AUTHN_LOGIN");
		String serviceID = System.getenv("CONJUR_AUTHN_JWT_SERVICE_ID");
		Path filePath = Path.of("/var/run/secrets/kubernetes.io/serviceaccount/token");
		String tokenContent = Files.readString(filePath);

		final CertificateFactory cf = CertificateFactory.getInstance("X.509");
		final Certificate cert = cf.generateCertificate(new ByteArrayInputStream(conjurCert.getBytes()));
		final KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(null);
		ks.setCertificateEntry("conjurTlsCaPath", cert);

		final TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(ks);
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, tmf.getTrustManagers(), null);

		HttpClient client = HttpClient.newBuilder().sslContext(sslContext).build();

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(baseURL + "/authn-jwt/" + serviceID +"/"+ conjurAccount +"/" + authLogin + "/authenticate"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("Accept-Encoding", "base64")
				.POST(HttpRequest.BodyPublishers.ofString("jwt=" + tokenContent))
				.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		String accessToken = response.body();
		request = HttpRequest.newBuilder()
				.uri(URI.create(baseURL + "/api/secrets/"+ conjurAccount +"/variable/" + secretPath))
				.header("Authorization", "Token token=\"" + accessToken.toString() + "\"")
				.build();
		response = client.send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println("Password with HttpClient: " + response.body());
	}
	@RequestMapping("/")
	public String hello(){
		System.out.println("Hello World, Spring Boot!");
		return "Hello World, Spring Boot!";
	}
}
