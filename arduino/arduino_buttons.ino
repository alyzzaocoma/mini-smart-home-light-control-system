#include <Arduino_FreeRTOS.h>


//PIN FOR SWITCHES/BUTTONS
const int BTN_LIVING   = 4;
const int BTN_BATH     = 5;
const int BTN_BED      = 6;
const int BTN_LAUNDRY  = 7;

//OPTIONAL ONLY, INDICATOR LANG IF YUNG BUTTON
//AY NAPRESSED AT NAKAPAGSEND KAY WEMOS
//(INDICATOR ONLY IF THE BUTTONS IS PRESSED AND SENT TO WEMOS)
const int LED_LIVING   = 8;
const int LED_BATH     = 9;
const int LED_BED      = 10;
const int LED_LAUNDRY  = 11;

//SET TO DEFAULT = O OR OFF
volatile int livingState   = 0;
volatile int bathState     = 0;
volatile int bedState      = 0;
volatile int laundryState  = 0;

volatile bool livingChanged   = false;
volatile bool bathChanged     = false;
volatile bool bedChanged      = false;
volatile bool laundryChanged  = false;


void setup() {
  Serial.begin(9600);

  pinMode(BTN_LIVING,   INPUT_PULLUP);
  pinMode(BTN_BATH,     INPUT_PULLUP);
  pinMode(BTN_BED,      INPUT_PULLUP);
  pinMode(BTN_LAUNDRY,  INPUT_PULLUP);

  pinMode(LED_LIVING,   OUTPUT);
  pinMode(LED_BATH,     OUTPUT);
  pinMode(LED_BED,      OUTPUT);
  pinMode(LED_LAUNDRY,  OUTPUT);

  //MAIN TASKS
  xTaskCreate(Task_ScanButtons, "ScanButtons", 128, NULL, 1, NULL);
  xTaskCreate(Task_UpdateLEDs,  "UpdateLEDs",  128, NULL, 1, NULL);
  xTaskCreate(Task_SendSerial,  "SendSerial",  128, NULL, 1, NULL);
}

void loop() {
}

//READ SWITCHES/BUTTONS WHICH ONE IS PRESSED
void Task_ScanButtons(void *pvParameters) {
  int prevLiving = HIGH, prevBath = HIGH, prevBed = HIGH, prevLaundry = HIGH;

  while (1) {
    int bLiving  = digitalRead(BTN_LIVING);
    int bBath    = digitalRead(BTN_BATH);
    int bBed     = digitalRead(BTN_BED);
    int bLaundry = digitalRead(BTN_LAUNDRY);
	
	//NOTE: BUTTON PRESSED = LOW

    if (bLiving == LOW && prevLiving == HIGH) {
      livingState = !livingState; //SINCE PRESSED IS, NEED TO SET ITS STATUS TO 1
      livingChanged = true; //TO INDICATE THAT THIS ROOM HAD CHANGES WITH ITS STATUS
    }

    if (bBath == LOW && prevBath == HIGH) {
      bathState = !bathState;
      bathChanged = true;
    }
    if (bBed == LOW && prevBed == HIGH) {
      bedState = !bedState;
      bedChanged = true;
    }
    if (bLaundry == LOW && prevLaundry == HIGH) {
      laundryState = !laundryState;
      laundryChanged = true;
    }

    prevLiving = bLiving;
    prevBath   = bBath;
    prevBed    = bBed;
    prevLaundry = bLaundry;

    vTaskDelay(50 / portTICK_PERIOD_MS);
  }
}

//TASK FOR LED INDICATORS
void Task_UpdateLEDs(void *pvParameters) {
  while (1) {
    digitalWrite(LED_LIVING,  livingState);
    digitalWrite(LED_BATH,    bathState);
    digitalWrite(LED_BED,     bedState);
    digitalWrite(LED_LAUNDRY, laundryState);

    vTaskDelay(50 / portTICK_PERIOD_MS);
  }
}

//SEND TO WEMOS TO BE ABLE TO SAVE STATUS TO XAMPP
void Task_SendSerial(void *pvParameters) {
  while (1) {
    if (livingChanged) {
      Serial.println('1');
      livingChanged = false;
    }
    if (bathChanged) {
      Serial.println('2');
      bathChanged = false;
    }
    if (bedChanged) {
      Serial.println('3');
      bedChanged = false;
    }
    if (laundryChanged) {
      Serial.println('4');
      laundryChanged = false;
    }

    vTaskDelay(100 / portTICK_PERIOD_MS);
  }
}
