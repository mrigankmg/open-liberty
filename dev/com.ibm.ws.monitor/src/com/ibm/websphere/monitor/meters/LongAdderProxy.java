/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.websphere.monitor.meters;

/**
 *
 */
public class LongAdderProxy {

    private interface Provider {
        LongAdderAdapter get();
    }

    /**
     * Backed by the internal LongAdder
     */
    private static class InternalLongAdderProvider implements Provider {

        @Override
        public LongAdderAdapter get() {
            return new LongAdderAdapter() {
                private final LongAdder longAdder = new LongAdder();

                @Override
                public void add(long x) {
                    longAdder.add(x);
                }

                @Override
                public long sum() {
                    return longAdder.sum();
                }

                @Override
                public void increment() {
                    longAdder.increment();
                }

                @Override
                public void decrement() {
                    longAdder.decrement();
                }

                @Override
                public long sumThenReset() {
                    return longAdder.sumThenReset();
                }
            };
        }

    }

    private static final Provider INSTANCE = getLongAdderProvider();

    private static Provider getLongAdderProvider() {
        return new InternalLongAdderProvider();
    }

    public static LongAdderAdapter create() {
        return INSTANCE.get();
    }
}