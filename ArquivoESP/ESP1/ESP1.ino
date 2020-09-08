#include <ESP8266WiFi.h>
#include <WiFiUdp.h>
#include<stdio.h>
#include<stdlib.h>
#include <Separador.h>


WiFiUDP udp;
String req;

String espName = "ESP1";
Separador s;

#ifndef STASSID
#define STASSID "Tales07"
#define STAPSK  "S3m_S3nh@"
#endif

unsigned int localPort = 8888;      // local port to listen on

// buffers for receiving and sending data
char packetBuffer[UDP_TX_PACKET_MAX_SIZE + 1]; //buffer to hold incoming packet,
// a string to send back
char  led[] = "led";
char cstr[50];
int k = 0;



void setup() {
  Serial.begin(115200);

  pinMode(D2, OUTPUT);
  pinMode(D4, OUTPUT);
  pinMode(D5, INPUT);
  digitalWrite(D2, LOW);
  digitalWrite(D4, HIGH);

  WiFi.mode(WIFI_STA);
  WiFi.begin(STASSID, STAPSK);
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print('.');
    hold(500);
  }
  Serial.print("Connected! IP address: ");
  Serial.println(WiFi.localIP());
  Serial.printf("UDP server on port %d\n", localPort);
  udp.begin(localPort);
  digitalWrite(D4, LOW);

}
void enviar(char mensagem[]) {
  udp.beginPacket("255.255.255.255", 8888);
  udp.println(mensagem);
  udp.endPacket();
  Serial.print("Enviando: "); Serial.println(mensagem);
}


void loop() {
  listen();//Sub-rotina para verificar a existencia de pacotes UDP.
}

void listen()//Sub-rotina que verifica se há pacotes UDP's para serem lidos.
{
  if (udp.parsePacket() > 0)//Se houver pacotes para serem lidos
  {
    req = "";//Reseta a string para receber uma nova informaçao
    while (udp.available() > 0)//Enquanto houver dados para serem lidos
    {
      char z = udp.read();//Adiciona o byte lido em uma char
      req += z;//Adiciona o char à string
    }
    String comando[3];
    comando[0] = s.separa(req, '/', 0);
    comando[1] = s.separa(req, '/', 1);
    comando[2] = s.separa(req, '/', 2);
    req = "";
    if (comando[0] == espName) {
      if (comando[1] == "led") {
        Serial.println("Comando: " + comando[1]);
        String led = espName + " - led/";
        digitalWrite(D2, !digitalRead(D2));
        if (digitalRead(D2)) {
          led += "Ligado";
        } else {
          led += "Desligado";
        }
        led.toCharArray(cstr, 50);
        enviar(cstr);
      } else if (comando[1] == "sensor") {
        Serial.println("Comando: " + comando[1]);
        int x = random(0, 1000);
        String led = espName + " - sensor/" + x;

        led.toCharArray(cstr, 50);
        enviar(cstr);
      }
    } else {
      if (comando[1] == "rede") {
        Serial.println("Esp localizado!");
        String rede = espName + " - " + espName;
        rede.toCharArray(cstr, 50);
        enviar(cstr);
      } else {
        Serial.println("O comando nao he para este esp! ");
      }


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
