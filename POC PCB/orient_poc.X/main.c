/*
 * File:   main.c
 * Author: Dylan
 *
 * Created on December 4, 2019, 7:48 PM
 */


#include <avr/io.h>

void I2C_Init(void){
    TWI0.CTRLA = TWI_SDASETUP_4CYC_gc | TWI_SDAHOLD_50NS_gc;
    
}

int main(void) {
    PORTA_DIRSET |= 0xF0;
    PORTA_DIRSET &= ~0x0E;
    PORTB_DIRSET |= 0x38;
    PORTA_OUT |= 0xF0;
    /* Replace with your application code */
    while (1) {
    }
}
