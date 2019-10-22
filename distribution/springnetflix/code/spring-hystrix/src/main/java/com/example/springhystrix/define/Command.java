package com.example.springhystrix.define;

public interface Command<T> {
    T run();

    T fallback();
}
