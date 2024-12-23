/*
 * Copyright 2013-2024 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package com.fluxchess.pulse.kotlin.uci

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail

class DefaultReceiverTest {
	@Test
	fun `When EOF is received it should quit the engine`() {
		var quitCalled = 0
		val input = ""
		val sender = DefaultSender(TestWriter())
		val testEngine = object : TestEngine() {
			override fun quit() {
				quitCalled++
			}
		}
		DefaultReceiver(TestReader(input), sender, testEngine).run()
		assertEquals(1, quitCalled)
	}

	@Test
	fun `When whitespaces are received it should ignore them`() {
		val input = """
			|   debug	on
		""".trimMargin()
		val sender = DefaultSender(TestWriter())
		sender.debugMode = false
		val testEngine = object : TestEngine() {}
		DefaultReceiver(TestReader(input), sender, testEngine).run()
		assertTrue(sender.debugMode)
	}

	@Test
	fun `When 'uci' is received it should initialize the engine`() {
		var initializeCalled = 0
		val input = """
			|uci
		""".trimMargin()
		val sender = DefaultSender(TestWriter())
		val testEngine = object : TestEngine() {
			override fun initialize() {
				initializeCalled++
			}
		}
		DefaultReceiver(TestReader(input), sender, testEngine).run()
		assertEquals(1, initializeCalled)
	}

	@Test
	fun `When 'debug' is received it should toggle debug mode`() {
		val input = """
			|debug
		""".trimMargin()
		val sender = DefaultSender(TestWriter())
		sender.debugMode = false
		val testEngine = object : TestEngine() {}
		DefaultReceiver(TestReader(input), sender, testEngine).run()
		assertTrue(sender.debugMode)
	}

	@Test
	fun `When 'debug on' is received it should turn on debug mode`() {
		val input = """
			|debug on
		""".trimMargin()
		val sender = DefaultSender(TestWriter())
		sender.debugMode = false
		val testEngine = object : TestEngine() {}
		DefaultReceiver(TestReader(input), sender, testEngine).run()
		assertTrue(sender.debugMode)
	}

	@Test
	fun `When 'debug off' is received it should turn off debug mode`() {
		val input = """
			|debug off
		""".trimMargin()
		val sender = DefaultSender(TestWriter())
		sender.debugMode = true
		val testEngine = object : TestEngine() {}
		DefaultReceiver(TestReader(input), sender, testEngine).run()
		assertFalse(sender.debugMode)
	}

	@Test
	fun `When 'isready' is received it should check the engine for readiness`() {
		var readyCalled = 0
		val input = """
			|isready
		""".trimMargin()
		val sender = DefaultSender(TestWriter())
		val testEngine = object : TestEngine() {
			override fun ready() {
				readyCalled++
			}
		}
		DefaultReceiver(TestReader(input), sender, testEngine).run()
		assertEquals(1, readyCalled)
	}

	@Test
	fun `When 'setoption' with name only is received it should set the option on the engine`() {
		var setOptionCalled = 0
		val input = """
			|setoption name some option
		""".trimMargin()
		val sender = DefaultSender(TestWriter())
		val testEngine = object : TestEngine() {
			override fun setOption(name: String) {
				assertEquals("some option", name)
				setOptionCalled++
			}
		}
		DefaultReceiver(TestReader(input), sender, testEngine).run()
		assertEquals(1, setOptionCalled)
	}

	@Test
	fun `When 'setoption' with name and value is received it should set the option on the engine`() {
		var setOptionCalled = 0
		val input = """
			|setoption name some option value some value
		""".trimMargin()
		val sender = DefaultSender(TestWriter())
		val testEngine = object : TestEngine() {
			override fun setOption(name: String, value: String) {
				assertEquals("some option", name)
				assertEquals("some value", value)
				setOptionCalled++
			}
		}
		DefaultReceiver(TestReader(input), sender, testEngine).run()
		assertEquals(1, setOptionCalled)
	}

	@Test
	fun `When 'register' is received and debug mode is true it should send an error back`() {
		val input = """
			|register
		""".trimMargin()
		val testWriter = TestWriter()
		val sender = DefaultSender(testWriter)
		sender.debugMode = true
		val testEngine = object : TestEngine() {}
		DefaultReceiver(TestReader(input), sender, testEngine).run()
		assertEquals(
			"""
				|info string Unsupported command: register
				|""".trimMargin(),
			testWriter.result,
		)
	}

	@Test
	fun `When 'ucinewgame' is received it should start a new game on the engine`() {
		var newGameCalled = 0
		val input = """
			|ucinewgame
		""".trimMargin()
		val sender = DefaultSender(TestWriter())
		val testEngine = object : TestEngine() {
			override fun newGame() {
				newGameCalled++
			}
		}
		DefaultReceiver(TestReader(input), sender, testEngine).run()
		assertEquals(1, newGameCalled)
	}

	@Test
	fun `When 'stop' is received it should stop the engine`() {
		var stopCalled = 0
		val input = """
			|stop
		""".trimMargin()
		val sender = DefaultSender(TestWriter())
		val testEngine = object : TestEngine() {
			override fun stop() {
				stopCalled++
			}
		}
		DefaultReceiver(TestReader(input), sender, testEngine).run()
		assertEquals(1, stopCalled)
	}

	@Test
	fun `When 'ponderhit' is received it should call ponderhit on the engine`() {
		var ponderHitCalled = 0
		val input = """
			|ponderhit
		""".trimMargin()
		val sender = DefaultSender(TestWriter())
		val testEngine = object : TestEngine() {
			override fun ponderHit() {
				ponderHitCalled++
			}
		}
		DefaultReceiver(TestReader(input), sender, testEngine).run()
		assertEquals(1, ponderHitCalled)
	}

	@Test
	fun `When 'quit' is received it should quit the engine`() {
		var quitCalled = 0
		val input = """
			|quit
		""".trimMargin()
		val testEngine = object : TestEngine() {
			override fun quit() {
				quitCalled++
			}
		}
		DefaultReceiver(TestReader(input), DefaultSender(TestWriter()), testEngine).run()
		assertEquals(1, quitCalled)
	}
}

private class TestReader(input: String) : Reader {
	private val iterator: Iterator<String> = input.lineSequence().iterator()
	override fun readln(): String? {
		return if (iterator.hasNext()) iterator.next() else null
	}
}

private abstract class TestEngine : Engine {
	override fun initialize() {
		fail()
	}

	override fun ready() {
		fail()
	}

	override fun setOption(name: String) {
		fail()
	}

	override fun setOption(name: String, value: String) {
		fail()
	}

	override fun newGame() {
		fail()
	}

	override fun position() {
		fail()
	}

	override fun start() {
		fail()
	}

	override fun stop() {
		fail()
	}

	override fun ponderHit() {
		fail()
	}

	override fun quit() {
		// Do nothing
	}
}
