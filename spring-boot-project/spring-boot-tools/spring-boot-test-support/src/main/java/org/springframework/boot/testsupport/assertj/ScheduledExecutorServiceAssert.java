/*
 * Copyright 2012-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.testsupport.assertj;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assert;

/**
 * AssertJ {@link Assert} for {@link ScheduledThreadPoolExecutor}.
 *
 * @author Mike Turbe
 * @author Moritz Halbritter
 */
public final class ScheduledExecutorServiceAssert
		extends AbstractAssert<ScheduledExecutorServiceAssert, ScheduledExecutorService> {

	private ScheduledExecutorServiceAssert(ScheduledExecutorService actual) {
		super(actual, ScheduledExecutorServiceAssert.class);
	}

	/**
	 * Verifies that the actual executor uses platform threads.
	 * @return {@code this} assertion object
	 * @throws AssertionError if the actual executor doesn't use platform threads
	 */
	public ScheduledExecutorServiceAssert usesPlatformThreads() {
		isNotNull();
		if (producesVirtualThreads()) {
			failWithMessage("Expected executor to use platform threads, but it uses virtual threads");
		}
		return this;
	}

	/**
	 * Verifies that the actual executor uses virtual threads.
	 * @return {@code this} assertion object
	 * @throws AssertionError if the actual executor doesn't use virtual threads
	 */
	public ScheduledExecutorServiceAssert usesVirtualThreads() {
		isNotNull();
		if (!producesVirtualThreads()) {
			failWithMessage("Expected executor to use virtual threads, but it uses platform threads");
		}
		return this;
	}

	private boolean producesVirtualThreads() {
		try {
			return this.actual.schedule(() -> {
				// https://openjdk.org/jeps/444
				// jep 444 specifies that virtual threads will belong to
				// a special thread group given the name "VirtualThreads"
				ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
				String threadGroupName = (threadGroup != null) ? threadGroup.getName() : "";
				return threadGroupName.equalsIgnoreCase("VirtualThreads");
			}, 0, TimeUnit.SECONDS).get();
		}
		catch (InterruptedException | ExecutionException ex) {
			throw new AssertionError(ex);
		}
	}

	/**
	 * Creates a new assertion class with the given {@link ScheduledExecutorService}.
	 * @param actual the {@link ScheduledExecutorService}
	 * @return the assertion class
	 */
	public static ScheduledExecutorServiceAssert assertThat(ScheduledExecutorService actual) {
		return new ScheduledExecutorServiceAssert(actual);
	}

}
