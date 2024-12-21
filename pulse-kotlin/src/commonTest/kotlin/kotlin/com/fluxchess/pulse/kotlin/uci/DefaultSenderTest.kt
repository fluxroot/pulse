/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package kotlin.com.fluxchess.pulse.kotlin.uci

import com.fluxchess.pulse.kotlin.uci.DefaultSender
import com.fluxchess.pulse.kotlin.uci.Writer
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultSenderTest {
	@Test
	fun `When 'id' is called with name and author it should print a valid string`() {
		val testWriter = TestWriter()
		val sender = DefaultSender(testWriter)
		sender.id("some-name", "some-author")
		assertEquals(
			"""
				|id name some-name
				|id author some-author
				|""".trimMargin(),
			testWriter.result,
		)
	}

	@Test
	fun `When 'ok' is called it should print 'uciok'`() {
		val testWriter = TestWriter()
		val sender = DefaultSender(testWriter)
		sender.ok()
		assertEquals(
			"""
				|uciok
				|""".trimMargin(),
			testWriter.result,
		)
	}

	@Test
	fun `When 'readyOk' is called it should print 'readyok'`() {
		val testWriter = TestWriter()
		val sender = DefaultSender(testWriter)
		sender.readyOk()
		assertEquals(
			"""
				|readyok
				|""".trimMargin(),
			testWriter.result,
		)
	}

	@Test
	fun `When 'debug' is called and debugMode is false it should print nothing`() {
		val testWriter = TestWriter()
		val sender = DefaultSender(testWriter)
		sender.debugMode = false
		sender.debug("some message")
		assertEquals(
			"",
			testWriter.result,
		)
	}

	@Test
	fun `When 'debug' is called and debugMode is true it should print the message`() {
		val testWriter = TestWriter()
		val sender = DefaultSender(testWriter)
		sender.debugMode = true
		sender.debug("some message")
		assertEquals(
			"""
				|info string some message
				|""".trimMargin(),
			testWriter.result,
		)
	}
}

class TestWriter : Writer {
	var result: String = ""
	override fun writeln(s: String) {
		this.result += "$s\n"
	}
}
