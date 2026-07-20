package com.image.hosting;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
@Test
void givenTwoNumbers_returnsTheirSum(){
    Calculator calculator = new Calculator();

    int result = calculator.add(1, 2);

    Assertions.assertEquals(3, result);
}
}