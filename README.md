# UDPComander

Sistema de controle, gerenciamento e acionamento, para dispositivos conectados a rede local. 

## Funcionamento

Usando o protocolo UDP, esse projeto apresenta uma proposta para gerenciar placas da rede como se fossem um ChromeCast. 

## Funcionalidade

Ao se compilar pela primeira vez, o equipamento gera uma rede WiFi com o nome dele, onde a senha é 12345678.
Se conectando a rede gerada, é possível já acionar um "botão" (pré configurado o pino D1), ou mudar o nome do dispositivo,
ou até mesmo reconfigurar o WiFi como desejar (mudar o nome, a senha, ou se conectar em algum WiFi existente).

## Código 

### Placa

Esse projeto foi feito para ser rodado num ESP8266, usando a plataforma Arduino IDE. Todas as bibliotecas usadas no projeto estão disponíveis
no "gerenciador de bibliotecas" da IDE:

* **ArduinoJson.h**: As configurações na placa, são gerenciadas e salvas usando um arquivo .json;
* **StringSplitter**: Para conseguir dar um tratamento diferenciado aos comandos recebidos, é utilizado essa biblioteca que possui a função de split();

As outras bibliotecas utilizadas ja são nativas na IDE.

### PC

Esse projeto foi desenvolvido com JavaFX. O código aqui disponível é o fonte original, para usa-lo é necessário um compilador que gere o executável. 
Foi usado o NetBeans originalmente, dessa forma é só importar como "projeto" e executar, que tudo já funciona normalmente.
