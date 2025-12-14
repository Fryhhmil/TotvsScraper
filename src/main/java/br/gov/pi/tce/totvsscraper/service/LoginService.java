package br.gov.pi.tce.totvsscraper.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;

@Service
public class LoginService {

    private final String BASE_URL = "https://grupoeducacional127611.rm.cloudtotvs.com.br";
    private final OkHttpClient client;
    private final CookieManager cookieManager;
    private final ObjectMapper mapper = new ObjectMapper();

    public LoginService() {
        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        this.client = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .followRedirects(false) // ESSENCIAL para capturar a key
                .build();
    }

    public String acessarPortal(String usuario, String senha) {
        try {
            String key = this.loginAndGetKey(usuario, senha);
            this.consumeKey(key);

            JsonNode contextos = this.getContextos();
            JsonNode ctx = contextos.get(contextos.size() - 1);
            JsonNode rawNode = ctx.get("IDCONTEXTOALUNO");
            String idContextoAlunoRaw = rawNode.toString().replace("\"", "");

            List<String> cookies = this.selecionarContexto(idContextoAlunoRaw);

            return getCookiesAsHeader();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /* =========================
       1. LOGIN + CAPTURA DA KEY
       ========================= */
    private String loginAndGetKey(String usuario, String senha) throws Exception {

        RequestBody body = new FormBody.Builder()
                .add("User", usuario)
                .add("Pass", senha)
                .add("Alias", "CorporeRM")
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/Corpore.Net//Source/EDU-EDUCACIONAL/Public/EduPortalAlunoLogin.aspx?AutoLoginType=ExternalLogin")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {

            System.out.println("[LOGIN] HTTP " + response.code());

            if (response.code() != 302) {
                throw new IllegalStateException("Login não retornou 302");
            }

            String location = response.header("Location");

            if (location == null || !location.contains("key=")) {
                throw new IllegalStateException("Key não encontrada no Location");
            }

            String key = extractKey(location);
            System.out.println("KEY = " + key);

            return key;
        }
    }

    /* =========================
       2. CONSUMIR A KEY
       ========================= */
    private void consumeKey(String key) throws Exception {

        Request request = new Request.Builder()
                .url(BASE_URL + "/FrameHTML/RM/API/user/AutoLoginPortal?key=" + key)
                .get()
                .header("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {

            System.out.println("[AUTOLOGIN] HTTP " + response.code());

            if (!response.isSuccessful()) {
                throw new IllegalStateException("Erro ao consumir key");
            }
        }
    }

    /* =========================
   4.1 BUSCAR CONTEXTO (GET)
   ========================= */
    private JsonNode getContextos() throws Exception {

        Request request = new Request.Builder()
                .url(BASE_URL + "/FrameHTML/RM/API/TOTVSEducacional/Contexto")
                .get()
                .header("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {

            System.out.println("[GET CONTEXTO] HTTP " + response.code());

            if (!response.isSuccessful()) {
                throw new IllegalStateException("Erro ao buscar Contexto");
            }

            String body = response.body().string();
            return mapper.readTree(body).path("data");
        }
    }

    /* =========================
   4.2 SELECIONAR CONTEXTO
   ========================= */
    private List<String> selecionarContexto(String idContextoAluno) throws Exception {

        String json = """
    {
      "CodColigada": 4,
      "CodFilial": 1,
      "CodTipoCurso": 1,
      "IdContextoAluno": "%s",
      "IdHabilitacaoFilial": 76,
      "IdPerlet": 37,
      "RA": "2092311",
      "AcessoDadosAcademicos": true,
      "AcessoDadosFinanceiros": true
    }
    """.formatted(idContextoAluno);

        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json;charset=UTF-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "/FrameHTML/RM/API/TOTVSEducacional/Contexto/Selecao")
                .post(body)
                .header("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {

            System.out.println("[SELECIONAR CONTEXTO] HTTP " + response.code());

            if (!response.isSuccessful()) {
                throw new IllegalStateException("Erro ao selecionar contexto");
            }
            return response.headers("Set-Cookie");
        }
    }

    /* =========================
   UTIL
   ========================= */
    private String extractKey(String location) {
        return location.substring(location.indexOf("key=") + 4);
    }

    public String getCookiesAsHeader() {
        var cookies = cookieManager.getCookieStore().getCookies();

        return cookieManager.getCookieStore()
                .getCookies()
                .stream()
                .map(c -> c.getName() + "=" + c.getValue())
                .reduce((a, b) -> a + "; " + b)
                .orElse("");
    }





}