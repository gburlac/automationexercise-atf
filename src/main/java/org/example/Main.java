package org.example;

import lombok.extern.slf4j.Slf4j;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@Slf4j
public class Main {
    public static void main(String[] args) {
        log.info("Application start");
        System.out.printf("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            log.debug("Loop iteration i={}", i);
            System.out.println("i = " + i);
        }

        log.info("Application end");
    }
}