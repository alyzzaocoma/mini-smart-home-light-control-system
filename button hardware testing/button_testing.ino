//BUTTON TESTING ONLY
//EXPECTATION: ARDUINO LANG AT LAPTOP ANG NAGCOCOMMUNICATE, JUST TO
//TEST IF NAGANA BA YUNG BUTTONS/SWITCHES
//(ONLY TO TEST IF THE BUTTONS WORK)

//CHANGE BUTTON PINS ACCORDINGLY
const int BTN_LIVING   = 4;
const int BTN_BATH     = 5;
const int BTN_BED      = 6;
const int BTN_LAUNDRY  = 7;

//SET TO HIGH MEANS NOT BEING PRESSED
int lastLiving  = HIGH;
int lastBath    = HIGH;
int lastBed     = HIGH;
int lastLaundry = HIGH;

void setup() {
  Serial.begin(9600);
  Serial.println("Button Debugger Started...");
  Serial.println("Press any button to test.");

  pinMode(BTN_LIVING, INPUT_PULLUP);
  pinMode(BTN_BATH, INPUT_PULLUP);
  pinMode(BTN_BED, INPUT_PULLUP);
  pinMode(BTN_LAUNDRY, INPUT_PULLUP);
}

void loop() {

  int bLiving = digitalRead(BTN_LIVING);
  int bBath = digitalRead(BTN_BATH);
  int bBed = digitalRead(BTN_BED);
  int bLaundry = digitalRead(BTN_LAUNDRY);

  //Living Room button AKA BUTTON 1
  if (bLiving == LOW && lastLiving == HIGH) {
    Serial.println("Living Room BUTTON PRESSED");
  }
  lastLiving = bLiving;

  //Bathroom button AKA BUTTON 2
  if (bBath == LOW && lastBath == HIGH) {
    Serial.println("Bathroom BUTTON PRESSED");
  }
  lastBath = bBath;

  //Bedroom button AKA BUTTON 3
  if (bBed == LOW && lastBed == HIGH) {
    Serial.println("Bedroom BUTTON PRESSED");
  }
  lastBed = bBed;

  //Laundry button AKA BUTTON 4
  if (bLaundry == LOW && lastLaundry == HIGH) {
    Serial.println("Laundry BUTTON PRESSED");
  }
  lastLaundry = bLaundry;

  delay(50);
}
