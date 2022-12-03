//가습기 제어에 대한 부분을 별도의 파일에서
//가습기 센서는 output이라 센서 상태를 알 수 없기 때문에
#include "Humidifier.h"

Humidifier::Humidifier(int pin) {
  this->pin = pin;
  init();
}
void Humidifier::init() {
  pinMode(pin, OUTPUT);
  off();
  state = HUMIDIFIER_OFF;
}
void Humidifier::on() {
  digitalWrite(pin, HIGH);
  state = HUMIDIFIER_ON;
}
void Humidifier::off() {
  digitalWrite(pin, LOW);
  state = HUMIDIFIER_OFF;
}

byte Humidifier::getState() {
  return state;
}
