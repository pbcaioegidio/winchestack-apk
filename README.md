<p align="center">
  <img src="logo.svg" width="96" height="96" alt="Winchestack APK" />
</p>

<h1 align="center">Winchestack APK</h1>

<p align="center">
  App Android (WebView) que abre o painel <strong>Winchestack</strong> num
  aplicativo de celular, com o login lembrado entre aberturas.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white" alt="Android 8.0+" />
  <img src="https://img.shields.io/badge/minSdk-26-3DDC84" alt="minSdk 26" />
  <img src="https://img.shields.io/badge/targetSdk-34-3DDC84" alt="targetSdk 34" />
  <img src="https://img.shields.io/badge/Java-WebView-orange?logo=openjdk&logoColor=white" alt="Java WebView" />
</p>

---

## Sobre

Um wrapper leve em **WebView** que carrega o painel Winchestack num app de
celular comum. O painel monitora quem está assistindo as câmeras e quem está na
rede local — este app só o coloca num ícone na tela inicial, com o login lembrado.

## Recursos

- 🌐 **WebView** apontando pra URL do painel (configurável)
- 🔑 **Login persistente** — logue uma vez com "Manter conectado" e o cookie é lembrado entre aberturas (até depois de reiniciar o aparelho)
- 🔁 **Reconecta sozinho** se a internet oscilar
- ↩️ Botão **Voltar** navega dentro do painel
- 📱 App normal (barra de status, rotação) — **não** é kiosk de TV
- 🔒 Só **HTTPS** (`usesCleartextTraffic="false"`)

## Por que WebView nativo (Java) e não Flutter / React Native?

Pra um app que **só embrulha um site**, o WebView nativo é a melhor escolha:

| Abordagem | Tamanho | Observação |
| --- | --- | --- |
| **WebView nativo (Java/Kotlin)** ✅ | ~11 KB | Usa o WebView do sistema direto. Zero runtime extra, controle total (cookie/login, JS, reconexão). |
| Flutter / Dart | vários MB | Carrega o engine do Flutter só pra usar um plugin (`webview_flutter`) que chama o **mesmo** WebView nativo. Overhead à toa. |
| React Native | vários MB | Igual: runtime do RN + `react-native-webview` embrulhando o mesmo WebView. |
| TWA (Trusted Web Activity) | pequeno | Usa o Chrome instalado direto. Bom, mas exige PWA + Digital Asset Links no domínio. |

> Flutter/RN só compensam se for adicionar **muita tela/recurso nativo** além do
> conteúdo web. Pra "abrir o painel num app", nativo é menor, mais simples e mais rápido.

## Build

> Não precisa instalar Gradle — o wrapper (`./gradlew`) baixa o Gradle 8.7 sozinho.
> Requer **Android SDK** (em `local.properties`) e **JDK 17**.

```bash
# 1) Configure o SDK e a URL do painel (arquivo fora do git)
cat > local.properties <<'EOF'
sdk.dir=/caminho/para/Android/Sdk
panel.url=https://SEU-PAINEL.exemplo
EOF

# 2) Gere o APK debug (instalável direto)
./gradlew assembleDebug
# saída: app/build/outputs/apk/debug/app-debug.apk
```

Ou abra a pasta no **Android Studio** e use *Build > Build APK(s)*.

### Release assinado (opcional)

```bash
keytool -genkey -v -keystore winchestack.jks -keyalg RSA -keysize 2048 \
  -validity 10000 -alias winchestack
# configure o signingConfig no app/build.gradle, depois:
./gradlew assembleRelease
```

## Instalar no aparelho

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Ou copie o APK pro celular e abra (permita "instalar de fontes desconhecidas").

## Personalizar

| O quê | Onde |
| --- | --- |
| URL do painel | `local.properties` → `panel.url` (fora do git) |
| Nome do app | `app/src/main/AndroidManifest.xml` → `android:label` |
| Ícone | `app/src/main/res/drawable/ic_launcher_foreground.xml` + cor em `res/values/colors.xml` |
| Pacote | `applicationId` / `namespace` em `app/build.gradle` |

## Detalhes

| Item | Valor |
| --- | --- |
| Pacote | `com.winchestack.app` |
| Versão | 1.0 (versionCode 1) |
| Base URL | definida em `local.properties` (`panel.url`) |
| minSdk / targetSdk | 26 (Android 8.0+) / 34 |
| Login | Laravel/Fortify do próprio painel (o app só guarda o cookie) |

## Deploy / produção

**Não precisa de servidor nem CI.** É um app cliente: gere o APK e instale nos
aparelhos manualmente. O backend é o próprio Winchestack.

## Licença

Projeto privado. Todos os direitos reservados.
