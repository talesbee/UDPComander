#include <StringSplitter.h>
#include <ArduinoJson.h>
#include <FS.h>
#include <ESP8266WiFi.h>
#include <WiFiUdp.h>
#include<stdio.h>
#include<stdlib.h>


WiFiUDP udp;
String req;

unsigned int localPort = 8888;
char packetBuffer[UDP_TX_PACKET_MAX_SIZE + 1];
char cstr[50];

struct Config {
  String espName;
  String wifiName;
  String wifiPass;
  String wifiMode;
};

char _buffer[64];

const char *filename = "/config.txt";  // <- SD library uses 8.3 filenames
Config config;                         // <- global configuration object

// Loads the configuration from a file
void loadConfiguration(const char *filename, Config &config) {
  File file = SPIFFS.open(filename, "r");
  if (!file) {
    Serial.println("Erro ao abrir o arquivo!");
  } else {
    Serial.println("Arquivo aberto!");
    StaticJsonDocument<512> doc;

    DeserializationError error = deserializeJson(doc, file);
    if (error)
      Serial.println(F("Failed to read file, using default configuration"));

    String espId = "ESP8266-";
    espId += String(random(0xffff), HEX);

    String teste = doc["espName"];
    if (teste == "") {
      config.espName = espId;
      config.wifiName = espId;
      config.wifiPass = "12345678";
      config.wifiMode = "0";
    } else {
      config.espName = doc["espName"] | "nome";
      config.wifiName = doc["wifiName"] | "wifi";
      config.wifiPass = doc["wifiPass"] | "pass";
      config.wifiMode = doc["wifiMode"] | "modo";
    }
  }

  file.close();
}

void saveConfiguration(const char *filename, const Config &config) {
  SPIFFS.remove(filename);

  File f = SPIFFS.open(filename, "w+");
  if (!f) {
    Serial.println("Erro ao abrir o arquivo!");
  } else {
    Serial.println("Arquivo Criado!");
    StaticJsonDocument<256> doc;

    doc["espName"] = config.espName;
    doc["wifiName"] = config.wifiName;
    doc["wifiPass"] = config.wifiPass;
    doc["wifiMode"] = config.wifiMode;

    if (serializeJson(doc, f) == 0) {
      Serial.println(F("Failed to write to file"));
    }
  }

  f.close();
}

void printFile(const char *filename) {
  File file = SPIFFS.open(filename, "r");
  if (!file) {
    Serial.println("Erro ao abrir o arquivo!");
    return;
  } else {
    while (file.available()) {
      int l = file.readBytesUntil('\n', _buffer, sizeof(_buffer));
      _buffer[l] = 0;
      String msg1 = String(_buffer);
      Serial.println(msg1);
    }
  }
  Serial.println();

  file.close();
}

void setup() {
  Serial.begin(115200);
  while (!Serial) continue;

  while (!SPIFFS.begin()) {
    Serial.println(F("Falha em abrir o sistema de arquivos!"));
    hold(1000);
  }

  pinMode(D1, OUTPUT);
  pinMode(D4, OUTPUT);
  digitalWrite(D1, LOW);
  digitalWrite(D4, HIGH);

  Serial.println();
  Serial.println(F("Carregar as configurações salvas!"));
  loadConfiguration(filename, config);

  Serial.println(F("Recriando/Criando arquivo de configurações!"));
  saveConfiguration(filename, config);

  Serial.println(F("Printando os arquivos!"));
  printFile(filename);

  int erro = 0;

  if (config.wifiMode == "0") {
    Serial.println("Criando rede wifi!");
    WiFi.softAP(config.wifiName, config.wifiPass);
    Serial.println("Wifi name: " + config.wifiName + " Wifi pass: " + config.wifiPass + " Modo '0'");
  } else {
    Serial.println("Conectando ao Wifi!");
    WiFi.mode(WIFI_STA);
    WiFi.begin(config.wifiName, config.wifiPass);
    while (WiFi.status() != WL_CONNECTED) {
      Serial.print('.');
      erro++;
      if (erro == 10) {
        Serial.println("Erro ao conectar ao WiFi, resetando parametros!");
        SPIFFS.remove(filename);
        hold(500);
        ESP.reset();
      }
      hold(500);
    }
  }
  udp.begin(localPort);
  digitalWrite(D4, LOW);


}

void loop() {
  listen();
}

void listen() {
  if (udp.parsePacket() > 0) {
    req = "";
    while (udp.available() > 0) {
      char z = udp.read();
      req += z;
    }
    String comando[5];

    StringSplitter *splitter = new StringSplitter(req, '/', 5);  // new StringSplitter(string_to_split, delimiter, limit)
    int itemCount = splitter->getItemCount();

    for (int i = 0; i < itemCount; i++) {
      comando[i] = splitter->getItemAtIndex(i);
    }

    Serial.println("Comando recebido: " + req);
    req = "";


    if (comando[0] == config.espName) {
      if (comando[1] == "botao") {
        digitalWrite(D1, !digitalRead(D1));
        if (digitalRead(D1)) {
          enviar("Ligado");
        } else {
          enviar("Desligado");
        }
      } else if (comando[1] == "wifi") {
        enviar("Reconfigurando o wifi");
        config.wifiName = comando[2];
        config.wifiPass = comando[3];
        config.wifiMode = comando[4];
        saveConfiguration(filename, config);
        ESP.reset();
      }
      else if (comando[1] == "nome") {
        enviar("Mudando o nome do ESP");
        config.espName = comando[2];
        saveConfiguration(filename, config);
        ESP.reset();
      } else if (comando[1] == "reset") {
        enviar("Resetando Config de Fabrica!");
        SPIFFS.remove(filename);
        ESP.reset();
      }
    } else if (comando[0] == "REDE") {
      enviar("Conectado");
    }
    hold(100);
  }
}

void hold(const unsigned int &ms) {
  // Non blocking hold
  unsigned long m = millis();
  while (millis() - m < ms) {
    yield();
  }
}

void enviar(String msg) {
  String sMsgTemp = "PLACA/" + config.espName + "/";
  sMsgTemp += msg;
  sMsgTemp.toCharArray(cstr, 50);
  udp.beginPacket(udp.remoteIP(), 8888);
  udp.println(cstr);
  udp.endPacket();
  Serial.print("Enviando: "); Serial.println(sMsgTemp);
}
