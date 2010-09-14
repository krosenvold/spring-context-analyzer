package com.rosenvold.spring;

/**
 * @author <a href="mailto:kristoffer@zenior.no">Kristoffer Moum</a>
 */
public interface Filter<T> {
    boolean accept(T candidate);
}
