#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>

//SET CHANGES HERE GUYS

//WIFI SETTINGS
const char* ssid = "YOUR_WIFI";
const char* password = "YOUR_PASSWORD";

//XAMPP'S IP ADDRESS OR OF LAPTOP
const char* serverIP = "192.168.x.x";  

//LIGHT PINS
const int PIN_LIVING   = D1;
const int PIN_BATH     = D2;
const int PIN_BED      = D3;
const int PIN_LAUNDRY  = D4;

//SET TO DEFAULT 0 OR OFF
String livingStatus  = "0";
String bathStatus    = "0";
String bedStatus     = "0";
String laundryStatus = "0";



void setup() {
  Serial.begin(9600);

  pinMode(PIN_LIVING,  OUTPUT);
  pinMode(PIN_BATH,    OUTPUT);
  pinMode(PIN_BED,     OUTPUT);
  pinMode(PIN_LAUNDRY, OUTPUT);
  

  //ESTABLISHING WIFI CONNECTION
  WiFi.mode(WIFI_STA);      
  WiFi.begin(ssid, password);

  Serial.print("Connecting to WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  } //FOR DEBUGGING
  Serial.println();
  Serial.println("WiFi connected.");
  Serial.print("IP: ");
  Serial.println(WiFi.localIP());
}

void loop() {
  //RETRIEVES CHAR FROM ARDUINO
  if (Serial.available()) {
    char c = Serial.read();

    if (c == '1') toggleRoom("LivingRoom",  livingStatus,  PIN_LIVING);
    if (c == '2') toggleRoom("Bathroom",     bathStatus,    PIN_BATH);
    if (c == '3') toggleRoom("Bedroom",      bedStatus,     PIN_BED);
    if (c == '4') toggleRoom("LaundryRoom", laundryStatus, PIN_LAUNDRY);
  }

  //SYNC FROM XAMPP
  syncFromDB("LivingRoom",  livingStatus,  PIN_LIVING);
  syncFromDB("Bathroom",     bathStatus,    PIN_BATH);
  syncFromDB("Bedroom",      bedStatus,     PIN_BED);
  syncFromDB("LaundryRoom", laundryStatus, PIN_LAUNDRY);

  delay(1000);
}


//TOGGLE ROOM BASED ON WHAT ROOM ARDUINO SENT
void toggleRoom(String room, String &status, int pin) {
  status = (status == "0") ? "1" : "0";
  digitalWrite(pin, (status == "1"));

  updateDB(room, status);
}


//UPDATE XAMPP
void updateDB(String room, String status) {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    WiFiClient client;

    String url = "http://" + String(serverIP) +
                 "/IT155/setLightStatus.php?room=" + room +
                 "&status=" + status;

    http.begin(client, url);
    http.GET();
    http.end();
  }
}

//READ OR GET FROM XAMPP
void syncFromDB(String room, String &status, int pin) {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    WiFiClient client;

    String url = "http://" + String(serverIP) +
                 "/IT155/getLightStatus.php?room=" + room;

    http.begin(client, url);
    int code = http.GET();

    if (code == 200) {
      String s = http.getString();
      s.trim();
      if (s == "0" || s == "1") {
        status = s;
        digitalWrite(pin, (s == "1"));
      }
    }

    http.end();
  }
}
