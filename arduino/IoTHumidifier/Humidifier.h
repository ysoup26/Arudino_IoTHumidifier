//가습기 센서를 다루기 위한 헤더파일
#include <Arduino.h>

#define HUMIDIFIER_OFF 0
#define HUMIDIFIER_ON 1

class Humidifier {
  private:
    int pin;
    byte state;

  public:
    Humidifier(int pin);
    void init();
    void on();
    void off();
    byte getState();
};
