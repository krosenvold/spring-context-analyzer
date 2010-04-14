package com.rosenvold.spring;

import java.util.List;

/**
 * @author <a href="mailto:kristoffer79@gmail.com">Kristoffer Moum</a>
 */
public interface Filter <T> {
    boolean accept(T candidate);
}
